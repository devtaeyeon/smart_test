package co.irexnet.waio.WAIO_ServerAgent.kafka;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagDescriptionDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageDTO;
import co.irexnet.waio.WAIO_ServerAgent.service.AlarmServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import co.irexnet.waio.WAIO_ServerAgent.util.Conversion;
import co.irexnet.waio.WAIO_ServerAgent.util.TagDescriptionList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    AlarmServiceImpl alarmService;

    @Autowired
    TagDescriptionList tagDescriptionList;

    @Autowired
    KafkaProducer kafkaProducer;

    @Value("${kafka.topic.dpf_scada1}")
    private String dpf_scada1;

    @Value("${kafka.topic.dpf_scada2}")
    private String dpf_scada2;

    private Date daq1Date = null;
    private Date daq2Date = null;

    /**
     * SCADA 제어 전송 topic
     * 
     * @param message 메시지
     */
    @KafkaListener(topics = "ai_control", groupId = CommonValue.KAFKA_GROUP_ID)
    public void listenAiControl(String message) throws JsonProcessingException {
        log.debug("ai_control, received message:{}", message);
    }

    /**
     * 통합 운영 시스템 알람 팝업용 topic
     * 
     * @param message 메시지
     */
    @KafkaListener(topics = "ai_popup", groupId = CommonValue.KAFKA_GROUP_ID)
    public void listenAiPopup(List<String> message) {
        log.debug("ai_popup, received message:{}", message);
        // JSON 처리를 위한 ObjectMapper 선언
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            for (String strSingleMessage : message) {
                KafkaAiPopupDTO kafkaAiPopup = objectMapper.readValue(strSingleMessage, KafkaAiPopupDTO.class);

                // insert alarm_notify
                alarmService.alarmNotify(
                        kafkaAiPopup.getAlarmId(),
                        kafkaAiPopup.getMessage(),
                        kafkaAiPopup.getUrl(),
                        kafkaAiPopup.getTime());
            }
        } catch (JsonProcessingException e) {
            log.error("Kafka topic: ai_popup, message format error:[{}]", message);
        }
    }

    /**
     * SCADA AI 예측값 전송 topic
     * 
     * @param message 메시지
     */
    @KafkaListener(topics = "control", groupId = CommonValue.KAFKA_GROUP_ID)
    public void listenControl(String message) {
        log.debug("control, received message:{}", message);
    }

    // SCADA의 전체 태그 정보를 저장하기 위한 topic
    @KafkaListener(topics = {"${kafka.topic.dpf_taginfo1}", "${kafka.topic.dpf_taginfo2}"})
    public void listenTagInfo(List<ConsumerRecord<String, String>> records)
    {
        //int nSendToBDCount = 0; 주석처리 이현수 20251126		
        int nCreateCount = 0;
        int nUpdateCount = 0;
        int nIgnoreCount = 0;
        int nFailedCount = 0;

        // JSON 처리를 위한 ObjectMapper 선언
        ObjectMapper objectMapper = new ObjectMapper();
        for(ConsumerRecord<String, String> record: records) {
            String strSingleMessage = record.value();
            String topic = record.topic();
			/* 주석처리 이현수 20251126
			 * try { kafkaProducer.sendMessageToBd(topic, strSingleMessage);
			 * nSendToBDCount++; } catch (KafkaProducerException e) {
			 * log.error("[Collector]Failed to send TagInfo from AI to Bigdata Kafka"); }
			 */

            try {
                KafkaTagDescriptionDTO kafkaTagDescription = objectMapper.readValue(strSingleMessage,
                        KafkaTagDescriptionDTO.class);

                if (kafkaTagDescription.getCreated() == null || kafkaTagDescription.getDescription() == null
                        || kafkaTagDescription.getTagname() == null) {
                    nIgnoreCount++;
                } else {
                    // Check exist tag_description.
                    // If exist tag_description, compare description
                    // Else insert tag_description
                    String strTagName;
                    if (kafkaTagDescription.getTagname().indexOf(".") >= 0) {
                        strTagName = kafkaTagDescription.getTagname().substring(
                                kafkaTagDescription.getTagname().indexOf(".") + 1,
                                kafkaTagDescription.getTagname().lastIndexOf("."));
                    } else {
                        strTagName = kafkaTagDescription.getTagname();
                    }

                    TagDescriptionDTO tagDescription = tagDescriptionList.getTagDescriptionFromName(strTagName);
                    if (tagDescription != null) {
                        if (kafkaTagDescription.getDescription().equalsIgnoreCase(tagDescription.getDesc()) == false) {
                            // update description & update tagDescriptionList
                            tagDescription.setDesc(kafkaTagDescription.getDescription());
                            databaseService.modTagDescription(tagDescription);
                            tagDescriptionList.setTagDescriptionList(databaseService.getAllTagDescription());
                            nUpdateCount++;
                        }
                    } else {
                        tagDescription = new TagDescriptionDTO();

                        tagDescription.setTag_sn(strTagName);
                        tagDescription.setDesc(kafkaTagDescription.getDescription());
                        tagDescription.setCrtd(kafkaTagDescription.getCreated());

                        // Insert & update tagDescriptionList
                        if (databaseService.addTagDescription(tagDescription) > 0) {
                            tagDescriptionList.addTagDescription(tagDescription);
                            nCreateCount++;
                        }
                    }
                }
            } catch (JsonMappingException e) {
                log.error("[Collector]message format error:[{}]", strSingleMessage);
                log.error(e.toString());
            } catch (JsonProcessingException e) {
                log.error("[Collector]message format error:[{}]", strSingleMessage);
                log.error(e.toString());
            }
        }
        //nSendToBDCount 사용안하는 변수 삭제 이현수 20251126
        log.info("[Collector]Listen TagInfo, received message:{}, sendedToBD:{}, created:{}, updated:{}, ignored:{}, failed:{}", records.size(),  nCreateCount, nUpdateCount, nIgnoreCount, nFailedCount);
    }

    /**
     * SCADA 실시간 태그 데이터 공정별 적재 및 INIT 변경
     * 
     * @param records 태그 목록
     */
    @KafkaListener(topics = { "${kafka.topic.dpf_scada1}", "${kafka.topic.dpf_scada2}" })
    public void listenScada(List<ConsumerRecord<String, String>> records) {
        if (daq1Date == null) {
            daq1Date = new Date();
            daq1Date.setTime(daq1Date.getTime() - CommonValue.ONE_HOUR);
        }

        // Check one minute after previous transfer
        Date currentDate = new Date();
        if (currentDate.getTime() - daq1Date.getTime() > CommonValue.ONE_MINUTE) {
            daq1Date = currentDate;

            // Insert system_monitoring
            // SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
            // systemMonitoringDTO.setHost(CommonValue.COLLECTOR1_HOSTNAME);
            // systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_COLLECTOR);
            // systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR1_HOSTNAME);
            // systemMonitoringDTO.setMntr_val(CommonValue.ALARM_VALUE_ON);
            // systemMonitoringDTO.setMntr_upd_ti(new Date());
            // databaseService.addSystemMonitoring(systemMonitoringDTO);
        }

        // JSON 처리를 위한 ObjectMapper 선언
        ObjectMapper objectMapper = new ObjectMapper();
        int nIgnoreCount = 0;

        // 데이터 전처리 결과를 저장하기 위한 kafkaRealtimeValue 선언
        List<ProcessRealtimeDTO> kafkaRealtimeValue = new ArrayList<>();
        int scd1TotalCnt = 0;
        int scd2TotalCnt = 0;
        //int scd1ToBdSuccessCnt = 0;	주석처리 이현수 20251126
        //int scd2ToBdSuccessCnt = 0;
        int scd1SuccessCnt = 0;
        int scd2SuccessCnt = 0;
        
        for(ConsumerRecord<String, String> record: records) {
            String strSingleMessage = record.value();
            String topic = record.topic();

            if(topic.equals(dpf_scada1)) {
                scd1TotalCnt++;
            }
            if(topic.equals(dpf_scada2)) {
                scd2TotalCnt++;
            }

			/* 주석처리 이현수 20251126
			 * try { kafkaProducer.sendMessageToBd(topic, strSingleMessage);
			 * if(topic.equals(dpf_scada1)) { scd1ToBdSuccessCnt++; }
			 * if(topic.equals(dpf_scada2)) { scd2ToBdSuccessCnt++; } } catch
			 * (KafkaProducerException e) {
			 * log.error("[Collector]Failed to send TagData from AI to Bigdata Kafka"); }
			 */

            try {
                KafkaTagDataDTO realTimeTagData = objectMapper.readValue(strSingleMessage, KafkaTagDataDTO.class);

                if (realTimeTagData.getTagname() == null || realTimeTagData.getTimestamp() == null
                        || realTimeTagData.getValue() == null || realTimeTagData.getQuality() == null) {
                    nIgnoreCount++;
                } else {
                    // Ignore quality == 0 data
                    int nQuality = Conversion.isNumber(realTimeTagData.getQuality())
                            ? Integer.parseInt(realTimeTagData.getQuality())
                            : 0;
                    if (nQuality == 0) {
                        nIgnoreCount++;
                        continue;
                    }

                    String strDate = realTimeTagData.getTimestamp();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date updateTime = simpleDateFormat.parse(strDate);

                    ProcessRealtimeDTO processRealtime = new ProcessRealtimeDTO();

                    // Tagname preamble parsing : AVHSWGS1.745-617-XXX-XXXX to 745-617-XXX-XXXX
                    String strTagName;
                    if (realTimeTagData.getTagname().indexOf(".") >= 0) {
                        strTagName = realTimeTagData.getTagname().substring(
                                realTimeTagData.getTagname().indexOf(".") + 1,
                                realTimeTagData.getTagname().lastIndexOf("."));
                    } else {
                        strTagName = realTimeTagData.getTagname();
                    }
                    processRealtime.setTag_sn(strTagName);
                    processRealtime.setTag_val(realTimeTagData.getValue());
                    processRealtime.setUpd_ti(updateTime);
                    processRealtime.setQlt(nQuality);

                    kafkaRealtimeValue.add(processRealtime);

                    if(topic.equals(dpf_scada1)) {
                        scd1SuccessCnt++;
                    }
                    if(topic.equals(dpf_scada2)) {
                        scd2SuccessCnt++;
                    }
                }
            } catch (JsonProcessingException e) {
                log.error("[Collector]RealTimeTagData whole format error");
            } catch (ParseException e) {
                log.error("[Collector]RealTimeTagData date format error");
            }
        }
        //scd1ToBdSuccessCnt,scd2ToBdSuccessCnt 사용안하는 변수 제거 이현수 20251126
        log.info("[Collector]Send to Bigdata Kafka Success Cnt {}: {}/{}, {}: {}/{}", dpf_scada1,  scd1TotalCnt, dpf_scada2,  scd2TotalCnt);
        log.info("[Collector]Get TagData in AI Platform Success Cnt {}: {}/{}, {}: {}/{}", dpf_scada1, scd1SuccessCnt, scd1TotalCnt, dpf_scada2, scd2SuccessCnt, scd2TotalCnt);

        List<TagManageDTO> tagManageList = databaseService.getAllTagManage();

        if(kafkaRealtimeValue.size() > 0){
            // SCADA 실시간 태그 데이터 공정별 적재
            loadTagData(kafkaRealtimeValue, tagManageList);

            // 2024.10 유역본부 요청으로 운전모드 업데이트는 수신 후가 아니라 변경 즉시 DB UPDATE로 변경
            /* INIT 테이블 update */
//            HashMap<String, Object> resultMap = updateInit(kafkaRealtimeValue, tagManageList);
//
//            log.info(
//                    "Update count, records[{}], time:[{}], ignore:[{}], receiving:[{}], coagulant:[{}], mixing:[{}], sedimentation:[{}], filter:[{}], disinfection:[{}]",
//                    records.size(),
//                    kafkaRealtimeValue.get(kafkaRealtimeValue.size() - 1).getUpd_ti(),
//                    nIgnoreCount,
//                    resultMap.get("nReceivingUpdateCount"),
//                    resultMap.get("nCoagulantUpdateCount"),
//                    resultMap.get("nMixingUpdateCount"),
//                    resultMap.get("nSedimentationUpdateCount"),
//                    resultMap.get("nFilterUpdateCount"),
//                    resultMap.get("nDisinfectionUpdateCount"));
        }
    }

    /**
     * 공정별 INIT 변경
     * 
     * @param kafkaRealtimeValue 대상 INIT 목록
     * @param tagManageList      태그정보 목록
     * @return HashMap<String, Object> UPDATE COUNT HashMap
     */
    private HashMap<String, Object> updateInit(List<ProcessRealtimeDTO> kafkaRealtimeValue,
            List<TagManageDTO> tagManageList) {
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        int nReceivingUpdateCount = 0, nCoagulantUpdateCount = 0, nMixingUpdateCount = 0, nSedimentationUpdateCount = 0,
                nFilterUpdateCount = 0, nDisinfectionUpdateCount = 0;

        for (ProcessRealtimeDTO realtime : kafkaRealtimeValue) {
            for (TagManageDTO tagManage : tagManageList) {
                if (tagManage.getTag_ty() != CommonValue.TAG_MANAGE_TYPE_INIT) {
                    continue;
                }
                // 공정 별 init table에 정의된 컬럼 업데이트(운전모드, 설정값 등)
                if (tagManage.getTag_ty() == CommonValue.TAG_MANAGE_TYPE_INIT) {
                    if (tagManage.getAi_cd().equalsIgnoreCase(CommonValue.PROCESS_RECEIVING) == true
                            && CommonValue.B_OPERATION_MODE.equalsIgnoreCase(tagManage.getItm()) == true) {
                        if (tagManage.getTag_sn().equalsIgnoreCase(realtime.getTag_sn()) == true &&
                                Conversion.isNumber(realtime.getTag_val()) == true) {
                            nReceivingUpdateCount += databaseService.modAiReceivingInit(tagManage.getItm(),
                                    Float.parseFloat(realtime.getTag_val()), tagManage.getProcessStep());
                        }
                    } else if (tagManage.getAi_cd().equalsIgnoreCase(CommonValue.PROCESS_COAGULANT) == true
                            && CommonValue.C_OPERATION_MODE.equalsIgnoreCase(tagManage.getItm()) == true) {
                        if (tagManage.getTag_sn().equalsIgnoreCase(realtime.getTag_sn()) == true &&
                                Conversion.isNumber(realtime.getTag_val()) == true) {
                            nCoagulantUpdateCount += databaseService.modAiCoagulantInit(tagManage.getItm(),
                                    Float.parseFloat(realtime.getTag_val()), tagManage.getProcessStep());
                        }
                    } else if (tagManage.getAi_cd().equalsIgnoreCase(CommonValue.PROCESS_MIXING) == true
                            && CommonValue.D_OPERATION_MODE.equalsIgnoreCase(tagManage.getItm()) == true) {
                        if (tagManage.getTag_sn().equalsIgnoreCase(realtime.getTag_sn()) == true &&
                                Conversion.isNumber(realtime.getTag_val()) == true) {
                            nMixingUpdateCount += databaseService.modAiMixingInit(tagManage.getItm(),
                                    Float.parseFloat(realtime.getTag_val()), tagManage.getProcessStep());
                        }
                    } else if (tagManage.getAi_cd().equalsIgnoreCase(CommonValue.PROCESS_SEDIMENTATION) == true
                            && CommonValue.E_OPERATION_MODE.equalsIgnoreCase(tagManage.getItm()) == true) {
                        if (tagManage.getTag_sn().equalsIgnoreCase(realtime.getTag_sn()) == true &&
                                Conversion.isNumber(realtime.getTag_val()) == true) {
                            nSedimentationUpdateCount += databaseService.modAiSedimentationInit(tagManage.getItm(),
                                    Float.parseFloat(realtime.getTag_val()), tagManage.getProcessStep());
                        }
                    } else if (tagManage.getAi_cd().equalsIgnoreCase(CommonValue.PROCESS_FILTER) == true
                            && CommonValue.F_OPERATION_MODE.equalsIgnoreCase(tagManage.getItm()) == true) {
                        if (tagManage.getTag_sn().equalsIgnoreCase(realtime.getTag_sn()) == true &&
                                Conversion.isNumber(realtime.getTag_val()) == true) {
                            nFilterUpdateCount += databaseService.modAiFilterInit(tagManage.getItm(),
                                    Float.parseFloat(realtime.getTag_val()), tagManage.getProcessStep());
                        }
                    } else if (tagManage.getAi_cd().equalsIgnoreCase(CommonValue.PROCESS_DISINFECTION) == true &&
                            (CommonValue.G_PRE_OPERATION_MODE.equalsIgnoreCase(tagManage.getItm()) == true
                                    || CommonValue.G_POST_OPERATION_MODE
                                            .equalsIgnoreCase(tagManage.getItm()) == true)) {
                        if (tagManage.getTag_sn().equalsIgnoreCase(realtime.getTag_sn()) == true &&
                                Conversion.isNumber(realtime.getTag_val()) == true) {
                            nDisinfectionUpdateCount += databaseService.modAiDisinfectionInit(tagManage.getItm(),
                                    Float.parseFloat(realtime.getTag_val()), tagManage.getProcessStep(),
                                    CommonValue.DISINFECTION_PRE_STEP); // 전차염

                            if (tagManage.getProcessStep() == 2) {
                                nDisinfectionUpdateCount += databaseService.modAiDisinfectionInit(tagManage.getItm(),
                                        Float.parseFloat(realtime.getTag_val()), tagManage.getProcessStep(),
                                        CommonValue.DISINFECTION_POST_STEP); // 후차염
                            }
                        }
                    }
                    continue;
                }
            }
        }

        resultMap.put("nReceivingUpdateCount", nReceivingUpdateCount);
        resultMap.put("nCoagulantUpdateCount", nCoagulantUpdateCount);
        resultMap.put("nMixingUpdateCount", nMixingUpdateCount);
        resultMap.put("nSedimentationUpdateCount", nSedimentationUpdateCount);
        resultMap.put("nFilterUpdateCount", nFilterUpdateCount);
        resultMap.put("nDisinfectionUpdateCount", nDisinfectionUpdateCount);

        return resultMap;
    }

    /**
     * SCADA 실시간 태그 데이터 공정별 적재
     * 
     * @param kafkaRealtimeValue 데이터 전처리 결과를 저장 DTO
     * @param tagManageList      태그정보 목록
     */
    private void loadTagData(List<ProcessRealtimeDTO> kafkaRealtimeValue, List<TagManageDTO> tagManageList) {
        HashMap<String, List<ProcessRealtimeDTO>> classifiedDataByProcCd = new HashMap<>();

        // classify data by PROC_CD
        for (ProcessRealtimeDTO realtime : kafkaRealtimeValue) {
            for (TagManageDTO tagManage : tagManageList) {
                if (tagManage.getTag_ty() == CommonValue.TAG_MANAGE_TYPE_SCADA) {
                    if (realtime.getTag_sn().equals(tagManage.getTag_sn())) {
                        String key = tagManage.getProc_cd();
                        ProcessRealtimeDTO value = realtime;

                        classifiedDataByProcCd.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                    }
                }
            }
        }

        List<String> procCdList = databaseService.selectProcCd();

        // load data in process RT table
        for (String procCd : procCdList) {
            List<ProcessRealtimeDTO> processRealtime = classifiedDataByProcCd.get(procCd);
            if (processRealtime != null) {
                try {
                    databaseService.addProcessRealtimeValue(procCd, processRealtime);
                    // log.debug("[Collector]Success load to table {}: {}", procCd,
                    // databaseService.addProcessRealtimeValue(procCd, processRealtime));
                } catch (DataAccessException e) {
                    log.error("[Collector]Failed load to table {}", procCd);
                    log.error(e.toString());
                }
            }
        }
    }

}