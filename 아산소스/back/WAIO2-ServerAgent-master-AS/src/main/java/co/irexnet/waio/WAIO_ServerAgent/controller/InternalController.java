package co.irexnet.waio.WAIO_ServerAgent.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiCoagulantRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiDisinfectionRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiFilterRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiMixingRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessAlarmDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessControlDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiReceivingRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.JsonBSeriesInt;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprRealTimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAlarmControlHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.SystemMonitoringDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageRangeDTO;
import co.irexnet.waio.WAIO_ServerAgent.kafka.KafkaProducer;
import co.irexnet.waio.WAIO_ServerAgent.service.AlarmServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.AlarmInfoList;
import co.irexnet.waio.WAIO_ServerAgent.util.AlgorithmHealthStatus;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import co.irexnet.waio.WAIO_ServerAgent.util.GlobalSystemConfig;
import co.irexnet.waio.WAIO_ServerAgent.util.HttpSend;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesAlgorithmCheck;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesAuthentication;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesControlCheck;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesReceivingData;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesStorage;
import co.irexnet.waio.WAIO_ServerAgent.vo.HadoopClusterInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.vo.HadoopClusterMetricsDTO;
import co.irexnet.waio.WAIO_ServerAgent.vo.HadoopJmxBeans;
import co.irexnet.waio.WAIO_ServerAgent.vo.SupervisorStateInfoDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class InternalController {
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    PropertiesControlCheck propertiesControlCheck;

    @Autowired
    PropertiesAlgorithmCheck propertiesAlgorithmCheck;

    @Autowired
    PropertiesStorage propertiesStorage;

    @Autowired
    PropertiesReceivingData propertiesReceivingData;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    AlarmServiceImpl alarmService;

    @Autowired
    AlarmInfoList alarmInfoList;

    @Autowired
    GlobalSystemConfig globalSystemConfig;

    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    HttpSend httpSend;

    private Date sensorDate = null;
    private Date analysisDate = null;
    private Date daqDate = null;
    private Date controlDate = null;
    private Date alarmDate = null;
    private Date algorithmDate = null;
    private Date databaseDate = null;
    private Date realTimeDate = null;
    private Date aiOprRealTimeDate = null;

    private String lastCoagulants1 = "", lastCoagulants2 = "";

    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectionRequestTimeout(3 * CommonValue.ONE_SECOND)
            .build();

    /**
     * Kafka에 실시간 AI 예측값 전달
     * 
     * @param token
     */
    @RequestMapping(value = "/internal/sensors", method = RequestMethod.GET)
    public void getSensors(@RequestHeader("X-ACCESS-TOKEN") String token) throws JsonProcessingException {

        // Token Check
        if (propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getSensors, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getSensor() initialization sensorDate before one hour
        if (sensorDate == null) {
            sensorDate = new Date();
            sensorDate.setTime(sensorDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.info("[internal] getSensors");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if (currentDate.getTime() - sensorDate.getTime() > CommonValue.ONE_MINUTE) {
            sensorDate = new Date();

            // get tag_manage(UI)
            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
            // Receiving Process
            getReceivingSensors(tagManageList);
            // Coagulant Process
            getCoagulantSensors(tagManageList);
            // Mixing Process
            getMixingSensors(tagManageList);
            // Sedimentation Process
            getSedimentationSensors(tagManageList);
            // Filter Process
            getFilterSensors(tagManageList);
            // Disinfection Process
            getDisinfectionSensors(tagManageList);
        }
    }
    
    /**
     * 5분 이상 RT 테이블 미적재시 시스템 통신 연결 알람(팝업창) 발생
     * @param token
     */
    @RequestMapping(value = "/internal/realTime", method = RequestMethod.GET)
    public void getRealTime(@RequestHeader("X-ACCESS-TOKEN") String token) {
    	// Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getRealTime, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }
        
        // If first call getRealTime() initialization realTimeDate before one hour
        if(realTimeDate == null) {
        	realTimeDate = new Date();
        	realTimeDate.setTime(realTimeDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.debug("[internal] getRealTime");
        
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - realTimeDate.getTime() > CommonValue.ONE_MINUTE) {
        	LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
            // 실시간 RT테이블 조회
            List<InterfaceAlarmControlHistoryDTO> allRealTimeList = databaseService.getAllRealTime(today);

            String strBody;
            ObjectMapper objectMapper = new ObjectMapper();
            
            if (allRealTimeList.size() > 0) {
    	        InterfaceAlarmControlHistoryDTO dto = new InterfaceAlarmControlHistoryDTO();
    	        dto.setStart_time(Date.from(today.atZone(ZoneId.systemDefault()).toInstant()));
    	        dto.setAlm_id(129901);
    	
    	        // 최근 5분 알람 조회
    	        List<InterfaceAlarmControlHistoryDTO> alarmNotifyList = databaseService.getBeforeAlarmNotify(dto);
    	
    	        if (alarmNotifyList.size() == 0) {
    	            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
    	            String almCdNm = "system_"+CommonValue.ALARM_CODE_CONNECTION_ERROR;
    	            AlarmInfoDTO alarmInfo = alarmInfoList
    	                    .getAlarmInfoFromAlarmCode(almCdNm, 0);
    	            if (alarmInfo != null) {
    	            	popupMap.put("alarm_id", alarmInfo.getAlm_id());
    	            	popupMap.put("message", alarmInfo.getDp_nm());
    	            	popupMap.put("url", alarmInfo.getUrl());
    	            	popupMap.put("time", today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    	            	try {
    	            		strBody = objectMapper.writeValueAsString(popupMap);
    	            		kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);
    	            	} catch (JsonProcessingException e) {
    	            		log.error("JsonProcessingException");
    	            	}
    	            } else {
                        log.error("Does not exist alarmInfo:[{]]", almCdNm);
                    }
    	        }
    	    }
        }
    }
    
    /**
     * 1분에 한번씩 현재 운전모드를 체크하여 update
     * @param token
     */
    @RequestMapping(value = "/internal/aiOprRealTime", method = RequestMethod.GET)
    public void updateAiOprRealTime(@RequestHeader("X-ACCESS-TOKEN") String token) {
    	// Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("updateAiOprRealTime, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }
        
        // If first call updateAiOprRealTime() initialization aiOprRealTimeDate before one hour
        if(aiOprRealTimeDate == null) {
        	aiOprRealTimeDate = new Date();
        	aiOprRealTimeDate.setTime(aiOprRealTimeDate.getTime() - CommonValue.ONE_HOUR);
        }
        
        log.debug("[internal] updateAiOprRealTime");
        
        Date currentDate = new Date();
        if(currentDate.getTime() - aiOprRealTimeDate.getTime() > CommonValue.ONE_MINUTE) {
        	aiOprRealTimeDate = new Date();
        	
        	AiProcessInitDTO aiInit = new AiProcessInitDTO();
        
        	// 각 공정의 init 테이블 데이터 조회
        	for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
        		// 착수
	            aiInit = databaseService.getAiReceivingInit(CommonValue.B_OPERATION_MODE, processStep);
	            databaseService.modAiOprRealTime(CommonValue.PROCESS_RECEIVING, CommonValue.NONE, aiInit.getInit_val().intValue(), processStep);
	            // 약품
	            aiInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE, processStep);
	            databaseService.modAiOprRealTime(CommonValue.PROCESS_COAGULANT, CommonValue.NONE, aiInit.getInit_val().intValue(), processStep);
	            // 혼화응집
	            aiInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE, processStep);
	            databaseService.modAiOprRealTime(CommonValue.PROCESS_MIXING, CommonValue.NONE, aiInit.getInit_val().intValue(), processStep);
	            // 침전
	            aiInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE, processStep);
	            databaseService.modAiOprRealTime(CommonValue.PROCESS_SEDIMENTATION, CommonValue.NONE, aiInit.getInit_val().intValue(), processStep);
	            // 전차염
	            aiInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE, processStep, CommonValue.DISINFECTION_PRE_STEP);
	            databaseService.modAiOprRealTime(CommonValue.PROCESS_DISINFECTION, CommonValue.DISINFECTION_PRE, aiInit.getInit_val().intValue(), processStep);
	            
	            if (processStep == 2) {
	            	// 여과
	            	aiInit = databaseService.getAiFilterInit(CommonValue.F_OPERATION_MODE, processStep);
	            	databaseService.modAiOprRealTime(CommonValue.PROCESS_FILTER, CommonValue.NONE, aiInit.getInit_val().intValue(), processStep);
	            	// 후차염
	            	aiInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE, processStep, CommonValue.DISINFECTION_POST_STEP);
	            	databaseService.modAiOprRealTime(CommonValue.PROCESS_DISINFECTION, CommonValue.DISINFECTION_POST, aiInit.getInit_val().intValue(), processStep);
	            }
        	}
        }
    }
    
    /**
     * 오늘 누적된 운전모드 운영시간을 이력 테이블에 적재(매일 자정에 실행)
     * @param token
     */
    @RequestMapping(value = "/internal/aiOprHistory", method = RequestMethod.GET)
    public void insertAiOprHistory(@RequestHeader("X-ACCESS-TOKEN") String token) {
        // Token Check
        if (propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("insertAiOprHistory, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        log.debug("[internal] insertAiOprHistory");
        
        List<AiOprRealTimeDTO> aiOprRealTimeList = databaseService.getAllAiOprRealTime();
        List<AiOprHistoryDTO> aiOprHistoryList = new ArrayList<AiOprHistoryDTO>();
        
        Date todayDate = new Date();
        AiOprHistoryDTO aiOprHistoryDto = null;
        
        if (aiOprRealTimeList != null) {
        	for (AiOprRealTimeDTO aiOprRealTime : aiOprRealTimeList) {
        		aiOprHistoryDto = new AiOprHistoryDTO();
        		aiOprHistoryDto.setProc_cd(aiOprRealTime.getProc_cd());
        		aiOprHistoryDto.setDisinfection_index(aiOprRealTime.getDisinfection_index());
        		aiOprHistoryDto.setAi_opr(aiOprRealTime.getAi_opr());
        		aiOprHistoryDto.setHis_date(todayDate);
        		aiOprHistoryDto.setOpr_minutes(aiOprRealTime.getOpr_minutes());
        		aiOprHistoryList.add(aiOprHistoryDto);
        	}
        	// 이력 테이블에 적재
        	databaseService.addAiOprHistoryList(aiOprHistoryList);
        	
        	// TB_AI_OPR_RT테이블 리셋
        	databaseService.modAllAiOprRealTime();
        } else {
        	log.error("Does not exist aiOprRealTimeList");
        }
    }
    
    /**
     * 공정별 AI 제어값을 Kafka로 전송
     * 
     * @param token 토큰
     */
    @RequestMapping(value = "/internal/control", method = RequestMethod.GET)
    public void getControl(@RequestHeader("X-ACCESS-TOKEN") String token) {
        // Token Check
        if (propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getControl, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getControl() initialization controlDate before one hour
        if (controlDate == null) {
            controlDate = new Date();
            controlDate.setTime(controlDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.info("[internal] getControl");

        // Check ten seconds after previous transfer
        Date currentDate = new Date();
        if (currentDate.getTime() - controlDate.getTime() > propertiesControlCheck.getPeriod()) {
            controlDate = new Date();
            // Receiving Process
            getReceivingControl();
            // Coagulant Process
            getCoagulantControl();
            // Mixing Process
            getMixingControl();
            // Sedimentation Process
            getSedimentationControl();
            // Filter Process
            getFilterControl();
            // Disinfection Process
            getDisinfectionControl();
        }
    }

    /**
     * 착수 실시간 AI 예측값 전달
     * 
     * @param tagManageList
     */
    public void getReceivingSensors(List<TagManageDTO> tagManageList) {
        ArrayList<String> keyList = new ArrayList<String>();
        Object objectTemp = new Object();
        int resultCnt = 0;

        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // get ai_receiving_realtime
            AiReceivingRealtimeDTO aiReceivingRealtime = databaseService.getLatestAiReceivingRealtimeValue(processStep);
            log.debug("getLatestAiReceivingRealtimeValue, result:[{}]", aiReceivingRealtime != null ? 1 : 0);
            if (aiReceivingRealtime != null) {
                try {
                    LinkedHashMap<String, Object> controlMap, mapTemp;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = simpleDateFormat.format(aiReceivingRealtime.getUpd_ti());
                    String strBody = "";

                    // b_operation_mode
                    AiProcessInitDTO aiReceivingInit = databaseService.getAiReceivingInit(CommonValue.B_OPERATION_MODE,
                            processStep);
                    if (aiReceivingInit != null) {
                        // 운전모드 알람 태그는 항상 전송
                        for (TagManageDTO dto : tagManageList) {
                            if (dto.getItm().equalsIgnoreCase("b_operation_mode_a") && CommonValue.PROCESS_RECEIVING
                                    .concat(String.valueOf(processStep)).equals(dto.getProc_cd())) {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getTag_sn());
                                controlMap.put("value",
                                        aiReceivingInit.getInit_val().intValue() == CommonValue.OPERATION_MODE_MANUAL
                                                ? false
                                                : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                                resultCnt++;
                                break;
                            }
                        }
                    }

                    for (TagManageDTO dto : tagManageList) {
                        if (dto.getProc_cd().equalsIgnoreCase(
                                CommonValue.PROCESS_RECEIVING.concat(String.valueOf(processStep))) != true) {
                            continue;
                        }
                        if (dto.getItm().equalsIgnoreCase("ai_b_ti") == true) {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("ai_b_in_fri") == true) {
                            // 1계열 원수 유입 유량 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiReceivingRealtime.getAi_b_in_fri());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("ai_b_vv_po") == true) {
                            // 1계열 원수 조절 밸브 개도 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiReceivingRealtime.getAi_b_vv_po());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("ai_b1_vv_po") == true) {
                            mapTemp = objectMapper.readValue(aiReceivingRealtime.getAi_b_vv_po(), LinkedHashMap.class);
                            keyList = new ArrayList<>(mapTemp.keySet());
                            objectTemp = mapTemp.get(keyList.get(0));
                            JsonBSeriesInt ai_b_vv_po = objectMapper.convertValue(objectTemp, JsonBSeriesInt.class);
                            // 1계열 원수 조절 밸브 개도 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", ai_b_vv_po.getSeries1());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("ai_b2_vv_po") == true) {
                            mapTemp = objectMapper.readValue(aiReceivingRealtime.getAi_b_vv_po(), LinkedHashMap.class);
                            keyList = new ArrayList<>(mapTemp.keySet());
                            objectTemp = mapTemp.get(keyList.get(0));
                            JsonBSeriesInt ai_b_vv_po = objectMapper.convertValue(objectTemp, JsonBSeriesInt.class);
                            // 2계열 원수 조절 밸브 개도 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", ai_b_vv_po.getSeries2());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("ai_b_pump_cnt") == true) {
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiReceivingRealtime.getAi_b_pump_cnt());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        }
                    }

                    // 펌프
                    if (processStep == 1) {
                        mapTemp = objectMapper.readValue(aiReceivingRealtime.getAi_b_pump_cb(), LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        for (String key : keyList) {
                            for (int i = 1; i <= 10; i++) {
                                String strItemName = "ai_b_pump_cb_p" + i;
                                if (key.equalsIgnoreCase("p" + i) == true) {
                                    // tagManageList에서 strItemName을 검색
                                    TagManageDTO dto = tagManageList.stream()
                                            .filter(tagManage -> strItemName.equalsIgnoreCase(tagManage.getItm())
                                                    && tagManage.getProc_cd()
                                                            .equalsIgnoreCase(CommonValue.PROCESS_RECEIVING
                                                                    .concat(String.valueOf(processStep))))
                                            .findAny()
                                            .orElse(null);

                                    if (dto == null) {
                                        continue;
                                    }

                                    // Kafka에 전송할 값 정의
                                    controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getTag_sn());
                                    controlMap.put("value", mapTemp.get(key));
                                    controlMap.put("time", strDate);

                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                                    resultCnt++;
                                    continue;
                                }
                            }
                        }
                    }

                } catch (JsonProcessingException e) {
                    log.error("JsonProcessingException Occurred in Receiving Process");
                }
            }
        }
        log.info("getReceivingSensors count : " + resultCnt);
    }

    public void getDisinfectionSensors(List<TagManageDTO> tagManageList) {
        int resultCnt = 0;
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
        	// 1. DISINFECTION_PRE_STEP
            // get ai_disinfection_realtime
            AiDisinfectionRealtimeDTO aiDisinfectionRealtime = databaseService
                    .getLatestAiDisinfectionRealtimeValue(processStep, CommonValue.DISINFECTION_PRE_STEP);
            log.debug("getLatestAiDisinfectionRealtimeValue pre, result:[{}]", aiDisinfectionRealtime != null ? 1 : 0);
            if (aiDisinfectionRealtime != null) {
                try {
                    LinkedHashMap<String, Object> controlMap;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = simpleDateFormat.format(aiDisinfectionRealtime.getUpd_ti());
                    String strBody;

                    // g_pre_operation_mode
                    AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(
                            CommonValue.G_PRE_OPERATION_MODE, processStep, CommonValue.DISINFECTION_PRE_STEP);
                    if (aiDisinfectionInit != null) {

                        // 운전모드 알람 태그는 항상 전송
                        for (TagManageDTO dto : tagManageList) {
                            if (dto.getItm().equalsIgnoreCase("g_pre_operation_mode_a") == true
                                    && CommonValue.PROCESS_DISINFECTION.concat(String.valueOf(processStep))
                                            .equals(dto.getProc_cd())) {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getTag_sn());
                                controlMap.put("value",
                                        aiDisinfectionInit.getInit_val().intValue() == CommonValue.OPERATION_MODE_MANUAL
                                                ? false
                                                : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                                resultCnt++;
                                break;
                            }
                        }
                    }

                    for (TagManageDTO dto : tagManageList) {
                        if (dto.getProc_cd().equalsIgnoreCase(
                                CommonValue.PROCESS_DISINFECTION.concat(String.valueOf(processStep))) != true
                                || dto.getDp().contains("전차염") != true) {
                            continue;
                        }

                        if (dto.getItm().equalsIgnoreCase("ai_g_ti") == true) {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                            // log.debug("Send AI_RESULT kafka ai_g_ti tag:[{}], value:[{}]",
                            // dto.getTag_sn(), strDate);
                        } else if (dto.getItm().equalsIgnoreCase("ai_g_pre_evaporation") == true) {
                            // 증발량 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiDisinfectionRealtime.getAi_g_pre_evaporation());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("ai_g_pre_chlorination") == true) {
                            // 다음 목표 주입률 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiDisinfectionRealtime.getAi_g_chol_rate());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        }
                    }
                } catch (JsonProcessingException e) {
                    log.error("JsonProcessingException Occurred in Disinfection Pre Process");
                }
            }
            
            // 2. DISINFECTION_POST_STEP
            if (processStep == 2) {
            	aiDisinfectionRealtime = databaseService.getLatestAiDisinfectionRealtimeValue(processStep,
            			CommonValue.DISINFECTION_POST_STEP);
            	log.debug("getLatestAiDisinfectionRealtimeValue post, result:[{}]", aiDisinfectionRealtime != null ? 1 : 0);
            	if (aiDisinfectionRealtime != null) {
            		try {
                        LinkedHashMap<String, Object> controlMap;
                        ObjectMapper objectMapper = new ObjectMapper();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String strDate = simpleDateFormat.format(aiDisinfectionRealtime.getUpd_ti());
                        String strBody;
                        
                        // g_post_operation_mode
                        AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(
                        		CommonValue.G_POST_OPERATION_MODE, processStep, CommonValue.DISINFECTION_POST_STEP);
                        if (aiDisinfectionInit != null) {
                        	for (TagManageDTO dto : tagManageList) {
                        		// 운전모드 알람 태그 전송
                        		if (dto.getItm().equalsIgnoreCase("g_post_operation_mode_a") == true
                        				&& CommonValue.PROCESS_DISINFECTION.concat(String.valueOf(processStep))
                        				.equals(dto.getProc_cd())) {
                        			controlMap = new LinkedHashMap<>();
                        			controlMap.put("tag", dto.getTag_sn());
                        			controlMap.put("value",
                        					aiDisinfectionInit.getInit_val()
                        					.intValue() == CommonValue.OPERATION_MODE_MANUAL ? false
                        							: true);
                        			controlMap.put("time", strDate);
                        			strBody = objectMapper.writeValueAsString(controlMap);
                        			kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        			resultCnt++;
                        			break;
                        		}
                        	}
                        }
                        for (TagManageDTO dto : tagManageList) {
                        	if (dto.getProc_cd().equalsIgnoreCase(
                        			CommonValue.PROCESS_DISINFECTION.concat(String.valueOf(processStep))) != true
                        			|| dto.getDp().contains("후차염") != true) {
                        		continue;
                        	}
                        	if (dto.getItm().equalsIgnoreCase("ai_g_ti") == true) {
                        		// ai update_time
                        		controlMap = new LinkedHashMap<>();
                        		controlMap.put("tag", dto.getTag_sn());
                        		controlMap.put("value", strDate);
                        		controlMap.put("time", strDate);
                        		strBody = objectMapper.writeValueAsString(controlMap);
                        		kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        		resultCnt++;
                        		// log.debug("Send AI_RESULT kafka ai_g_ti tag:[{}], value:[{}]",
                        		// dto.getTag_sn(), strDate);
                        	} else if (dto.getItm().equalsIgnoreCase("ai_g_post_corrected") == true) {
                        		// 이전 주입률 보정값 예측
                        		controlMap = new LinkedHashMap<>();
                        		controlMap.put("tag", dto.getTag_sn());
                        		controlMap.put("value", aiDisinfectionRealtime.getAi_g_correct_degree());
                        		controlMap.put("time", strDate);
                        		strBody = objectMapper.writeValueAsString(controlMap);
                        		kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        		resultCnt++;
                        	} else if (dto.getItm().equalsIgnoreCase("ai_g_post_chlorination") == true) {
                        		// 다음 목표 주입률 예측
                        		controlMap = new LinkedHashMap<>();
                        		controlMap.put("tag", dto.getTag_sn());
                        		controlMap.put("value", aiDisinfectionRealtime.getAi_g_chol_rate());
                        		controlMap.put("time", strDate);
                        		strBody = objectMapper.writeValueAsString(controlMap);
                        		kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        		resultCnt++;
                        	}
                        }
	                } catch (JsonProcessingException e) {
	                    log.error("JsonProcessingException Occurred in Disinfection Post Process");
	                }
            	}
            }
        }
        log.info("getDisinfectionSensors count : " + resultCnt);
    }

    public void getFilterSensors(List<TagManageDTO> tagManageList) {
        int resultCnt = 0;
        // get ai_filter_realtime
        AiFilterRealtimeDTO aiFilterRealtime = databaseService.getLatestAiFilterRealtimeValue();
        log.debug("getLatestAiFilterRealtimeValue, result:[{}]", aiFilterRealtime != null ? 1 : 0);

        // get location number(지 번호)
        TagManageRangeDTO filterRange = databaseService.getTagManageRange(CommonValue.PROCESS_FILTER,
                CommonValue.PROCESS_STEP_ARRAY[1]);
        log.debug("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_FILTER, filterRange != null ? 1 : 0);
        if (aiFilterRealtime != null) {
            int nLocationMin = 0, nLocationMax = 0;
            if (filterRange != null) {
                nLocationMin = filterRange.getMin();
                nLocationMax = filterRange.getMax();
            }

            try {
                LinkedHashMap<String, Object> controlMap, mapTemp;
                ObjectMapper objectMapper = new ObjectMapper();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat aiDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                SimpleDateFormat valueDateFormat = new SimpleDateFormat("MM-dd HH:mm");
                String strDate = simpleDateFormat.format(aiFilterRealtime.getUpd_ti());
                String strBody, strValueDate;

                Date valueDate;

                // f_operation_mode]
                AiProcessInitDTO aiFilterInit = databaseService.getAiFilterInit(CommonValue.F_OPERATION_MODE,
                        CommonValue.PROCESS_STEP_ARRAY[1]);
                if (aiFilterInit != null) {
                    // 운전모드 알람 태그는 항상 전송
                    for (TagManageDTO dto : tagManageList) {
                        if (dto.getItm().equalsIgnoreCase("f_operation_mode_a") == true
                                && CommonValue.PROCESS_FILTER.concat(String.valueOf(2)).equals(dto.getProc_cd())) {
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value",
                                    aiFilterInit.getInit_val().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false
                                            : true);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                            break;
                        }
                    }
                }

                for (TagManageDTO dto : tagManageList) {
                    if (dto.getProc_cd().equalsIgnoreCase(CommonValue.PROCESS_FILTER.concat("2")) != true) {
                        continue;
                    }

                    if (dto.getItm().equalsIgnoreCase("ai_f_ti") == true) {
                        // ai update_time
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", strDate);
                        controlMap.put("time", strDate);
                        strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        resultCnt++;
                        break;
                    }
                }

                // AI 지별 여과 지속 시간 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_loc_ti(), LinkedHashMap.class);
                ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
                Object objectTemp = mapTemp.get(keyList.get(0));

                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());

                for (String key : keyList) {
                    for (int i = nLocationMin; i <= nLocationMax; i++) {
                        String strItemName = "ai_f_ti" + i;
                        if (key.equalsIgnoreCase("location" + i) == true) {
                            // tagManageList에서 strItemName을 검색
                            TagManageDTO dto = tagManageList.stream()
                                    .filter(tagManage -> strItemName.equalsIgnoreCase(tagManage.getItm()))
                                    .findAny()
                                    .orElse(null);

                            if (dto == null) {
                                continue;
                            }

                            // Kafka에 전송할 값 정의
                            int value = (int) mapTemp.get(key);
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", Math.round((float) value / 60));
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                            continue;
                        }
                    }
                }

                // AI 지별 수위 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_loc_le(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));

                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                for (String key : keyList) {
                    LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(key),
                            LinkedHashMap.class);
                    ArrayList<String> locationKeyList = new ArrayList<>(locationMapTemp.keySet());

                    for (int i = nLocationMin; i <= nLocationMax; i++) {
                        String strItemName = "ai_f_le" + i;
                        if (key.equalsIgnoreCase("location" + i) == true) {
                            // tagManageList에서 strItemName을 검색
                            TagManageDTO dto = tagManageList.stream()
                                    .filter(tagManage -> strItemName.equalsIgnoreCase(tagManage.getItm()))
                                    .findAny()
                                    .orElse(null);

                            if (dto == null) {
                                continue;
                            }

                            // Kafka에 전송할 값 정의
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", locationMapTemp.get(locationKeyList.get(0)));
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                            continue;
                        }
                    }
                }

                // tagManageList에서 ai_f_operation_count 키 값 검색
                TagManageDTO aiFOperationCount = tagManageList.stream()
                        .filter(tagManage -> "ai_f_num_fil".equalsIgnoreCase(tagManage.getItm()))
                        .findAny()
                        .orElse(null);
                if (aiFOperationCount != null) {
                    // AI 운영지 수 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_num_fil(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", aiFOperationCount.getTag_sn());
                    controlMap.put("value", objectTemp);
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                    resultCnt++;
                }

                // AI 지별 운영 스케쥴 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_operation(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));

                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                for (String key : keyList) {
                    LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(key),
                            LinkedHashMap.class);
                    ArrayList<String> locationKeyList = new ArrayList<>(locationMapTemp.keySet());

                    for (String locationKey : locationKeyList) {
                        for (int i = nLocationMin; i <= nLocationMax; i++) {
                            String strStartTiName = "ai_f_start_ti" + i;
                            String strEndTiName = "ai_f_end_ti" + i;
                            String strBwStartTiName = "ai_f_bw_start_ti" + i;
                            if (key.equalsIgnoreCase("location" + i) == true
                                    && locationKey.equalsIgnoreCase("start") == true) {
                                // AI 여과 시작 시간 예측
                                // tagManageList에서 strItemName을 검색
                                TagManageDTO dto = tagManageList.stream()
                                        .filter(tagManage -> strStartTiName.equalsIgnoreCase(tagManage.getItm()))
                                        .findAny()
                                        .orElse(null);

                                if (dto == null) {
                                    continue;
                                }

                                String strValue = locationMapTemp.get(locationKey).toString();
                                if (strValue.equalsIgnoreCase("0") == true) {
                                    strValueDate = "--";
                                } else {
                                    valueDate = aiDateFormat.parse(strValue);
                                    strValueDate = valueDateFormat.format(valueDate);
                                }
                                // Kafka에 전송할 값 정의
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getTag_sn());
                                controlMap.put("value", strValueDate);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                                resultCnt++;

                                continue;
                            } else if (key.equalsIgnoreCase("location" + i) == true
                                    && locationKey.equalsIgnoreCase("end") == true) {
                                // AI 여과 종료 시간 예측
                                // tagManageList에서 strItemName을 검색
                                TagManageDTO dto = tagManageList.stream()
                                        .filter(tagManage -> strEndTiName.equalsIgnoreCase(tagManage.getItm()))
                                        .findAny()
                                        .orElse(null);

                                if (dto == null) {
                                    continue;
                                }

                                String strValue = locationMapTemp.get(locationKey).toString();
                                if (strValue.equalsIgnoreCase("0") == true) {
                                    strValueDate = "--";
                                } else {
                                    valueDate = aiDateFormat.parse(strValue);
                                    strValueDate = valueDateFormat.format(valueDate);
                                }
                                // Kafka에 전송할 값 정의
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getTag_sn());
                                controlMap.put("value", strValueDate);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                                resultCnt++;

                                continue;
                            } else if (key.equalsIgnoreCase("location" + i) == true
                                    && locationKey.equalsIgnoreCase("bw_start") == true) {
                                // AI 역세 시작 시간 예측
                                // tagManageList에서 strItemName을 검색
                                TagManageDTO dto = tagManageList.stream()
                                        .filter(tagManage -> strBwStartTiName.equalsIgnoreCase(tagManage.getItm()))
                                        .findAny()
                                        .orElse(null);

                                if (dto == null) {
                                    continue;
                                }

                                String strValue = locationMapTemp.get(locationKey).toString();
                                if (strValue.equalsIgnoreCase("0") == true) {
                                    strValueDate = "--";
                                } else {
                                    valueDate = aiDateFormat.parse(strValue);
                                    strValueDate = valueDateFormat.format(valueDate);
                                }
                                // Kafka에 전송할 값 정의
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getTag_sn());
                                controlMap.put("value", strValueDate);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                                resultCnt++;
                                continue;
                            }
                        }
                    }
                }
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException Occurred in Filter Process");
            } catch (ParseException e) {
                log.error("TextParse Exception Occurred in Filter Process");
            }
        }
        log.info("getFilterSensors count : " + resultCnt);
    }

    /**
     * 침전 실시간 AI 예측값 전달
     * 
     * @param tagManageList
     */
    public void getSedimentationSensors(List<TagManageDTO> tagManageList) {
        int resultCnt = 0;
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // get ai_sedimentation_realtime
            AiSedimentationRealtimeDTO aiSedimentationRealtime = databaseService
                    .getLatestAiSedimentationRealtimeValue(processStep);
            log.debug("getLatestAiSedimentationRealtimeValue, result:[{}]", aiSedimentationRealtime != null ? 1 : 0);
            if (aiSedimentationRealtime != null) {
                try {
                    LinkedHashMap<String, Object> controlMap, mapTemp;
                    List<LinkedHashMap<String, Object>> locationMap;
                    ObjectMapper objectMapper = new ObjectMapper();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = simpleDateFormat.format(aiSedimentationRealtime.getUpd_ti());
                    String strBody = null;

                    // e_operation_mode
                    AiProcessInitDTO aiSedimentationInit = databaseService
                            .getAiSedimentationInit(CommonValue.E_OPERATION_MODE, processStep);
                    if (aiSedimentationInit != null) {
                        // 운전모드 알람 태그는 항상 전송
                        for (TagManageDTO dto : tagManageList) {
                            if (dto.getItm().equalsIgnoreCase("e_operation_mode_a") == true
                                    && CommonValue.PROCESS_SEDIMENTATION.concat(String.valueOf(processStep))
                                            .equals(dto.getProc_cd())) {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getTag_sn());
                                controlMap
                                        .put("value",
                                                aiSedimentationInit.getInit_val()
                                                        .intValue() == CommonValue.OPERATION_MODE_MANUAL ? false
                                                                : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                                resultCnt++;
                                break;
                            }
                        }
                    }

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("ai_e_sc_start_ti", "start");
                    map.put("ai_e_sc_end_ti", "stop");
                    map.put("ai_e_sc_next_start_ti", "next_start");
                    map.put("ai_e_sc_inbal_ti", "inbal");
                    Field field;

                    for (TagManageDTO dto : tagManageList) {
                        if (dto.getProc_cd().split(String.valueOf(processStep))[0]
                                .equalsIgnoreCase(CommonValue.PROCESS_SEDIMENTATION) != true) {
                            continue;
                        }
                        if (dto.getItm().equalsIgnoreCase("ai_e_ti") == true) {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        }
                        if (dto.getItm().equalsIgnoreCase("ai_e_sludge") == true) {
                            // 침전지 슬러지 양 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiSedimentationRealtime.getAIE_5300());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        }

                        String location = null, targetDto = null;
                        for (Entry<String, String> elem : map.entrySet()) {
                            if (elem.getKey().equalsIgnoreCase(dto.getItm().replaceAll("\\d", ""))) {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getTag_sn());
                                location = "AIE_90".concat(
                                        String.format("%02d", Integer.parseInt(dto.getItm().replaceAll("[^0-9]", ""))));
                                targetDto = "co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation"
                                        .concat(dto.getItm().replaceAll("[^0-9]", "")).concat("RealtimeDTO");
                                field = aiSedimentationRealtime.getClass().getDeclaredField(location);
                                field.setAccessible(true);
                                getParamMapping(controlMap, strDate, elem.getValue(),
                                        String.valueOf(field.get(aiSedimentationRealtime)), Class.forName(targetDto));
                                resultCnt++;
                            }
                        }
                    }
                } catch (JsonProcessingException e) {
                    log.error("JsonProcessingException Occurred in Sedimentation Process");
                } catch (ParseException e) {
                    log.error("TimeParsingException Occurred in Sedimentation Process");
                } catch (IllegalArgumentException e) {
                    log.error("IllegalArgumentException Occurred in Sedimentation Process");
                } catch (IllegalAccessException e) {
                    log.error("IllegalAccessException Occurred in Sedimentation Process");
                } catch (NoSuchFieldException e) {
                    log.error("NoSuchFieldException Occurred in Sedimentation Process");
                } catch (SecurityException e) {
                    log.error("SecurityException Occurred in Sedimentation Process");
                } catch (ClassNotFoundException e) {
                    log.error("ClassNotFoundException Occurred in Sedimentation Process");
                }
            }
        }
        log.info("getSedimentationSensors count : " + resultCnt);
    }

    private void getParamMapping(LinkedHashMap<String, Object> controlMap, String strDate, String key, String strTemp,
            Class<?> dto)
            throws IllegalArgumentException, IllegalAccessException, ParseException, JsonProcessingException {
        if (strTemp != null) {
            LinkedHashMap<String, Object> mapTemp;
            String strBody = null;
            SimpleDateFormat resultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat valueDateFormat = new SimpleDateFormat("MM-dd HH:mm");
            ObjectMapper objectMapper = new ObjectMapper();
            Object paramDto = new Object();

            strTemp = strTemp.replaceAll("NaN", "\"\"");
            mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
            ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
            Object objectTemp = mapTemp.get(keyList.get(0));
            List<LinkedHashMap<String, Object>> locationMap = objectMapper.convertValue(objectTemp,
                    new TypeReference<List<LinkedHashMap<String, Object>>>() {
                    });

            mapTemp.clear();

            for (Map<String, Object> map : locationMap) {
                mapTemp.putAll(map);
            }
            paramDto = objectMapper.convertValue(mapTemp, dto);
            for (Field field : paramDto.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(paramDto);

                if (field.getName().contains("AIE_60")) {
                    for (Field field2 : field.getType().getDeclaredFields()) {
                        field2.setAccessible(true);
                        if (key.equals(field2.getName())) {
                            controlMap.put("value",
                                    "".equals(String.valueOf(field2.get(value))) ? "--"
                                            : valueDateFormat
                                                    .format(resultDateFormat.parse(String.valueOf(field2.get(value)))));
                        }
                    }
                }
            }
            controlMap.put("time", strDate);
            strBody = objectMapper.writeValueAsString(controlMap);
            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
        }
    }

    public void getMixingSensors(List<TagManageDTO> tagManageList) {
        int resultCnt = 0;
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // get ai_mixing_realtime
            AiMixingRealtimeDTO aiMixingRealtime = databaseService.getLatestAiMixingRealtimeValue(processStep);
            log.debug("getLatestAiMixingRealtimeValue, result:[{}]", aiMixingRealtime != null ? 1 : 0);

            // get location number(지 번호)
            TagManageRangeDTO mixingRange = databaseService.getTagManageRange(CommonValue.PROCESS_MIXING, processStep);
            log.debug("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_MIXING, mixingRange != null ? 1 : 0);
            if (aiMixingRealtime != null) {
                int nLocationMin = 0, nLocationMax = 0;
                if (mixingRange != null) {
                    nLocationMin = mixingRange.getMin();
                    nLocationMax = mixingRange.getMax();
                }
                try {
                    LinkedHashMap<String, Object> controlMap, mapTemp;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = simpleDateFormat.format(aiMixingRealtime.getUpd_ti());
                    String strBody;

                    // d_operation_mode
                    AiProcessInitDTO aiMixingInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE,
                            processStep);
                    if (aiMixingInit != null) {

                        // 운전모드 알람 태그는 항상 전송
                        for (TagManageDTO dto : tagManageList) {
                            if (dto.getItm().equalsIgnoreCase("d_operation_mode_a") == true
                                    && CommonValue.PROCESS_MIXING.concat(String.valueOf(processStep))
                                            .equals(dto.getProc_cd())) {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getTag_sn());
                                controlMap.put("value",
                                        aiMixingInit.getInit_val().intValue() == CommonValue.OPERATION_MODE_MANUAL
                                                ? false
                                                : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                                resultCnt++;
                                break;
                            }
                        }
                    }

                    for (TagManageDTO dto : tagManageList) {
                        if (dto.getProc_cd().equalsIgnoreCase(
                                CommonValue.PROCESS_MIXING.concat(String.valueOf(processStep))) != true) {
                            continue;
                        }
                        if (dto.getItm().equalsIgnoreCase("ai_d_ti") == true) {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("d_ki_dv") == true) {
                            // 동점성 계수
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", String.format("%.8f", aiMixingRealtime.getD_ki_dv()));
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("d_anr") == true) {
                            // 패들면적
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiMixingRealtime.getD_anr());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("d_v") == true) {
                            // 조 체적
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiMixingRealtime.getD_v());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        }
                    }

                    // AI 지별 응집기 속도 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    //260521 온더시스 이현수 아산정수장 하자보수 펜타시스템 
                    // 1~10지 추가 및 현재 운영상태가 아닌 예측값 내보내기
                    //mapTemp = objectMapper.readValue(aiMixingRealtime.getAi_d_loc_fc_stt(), LinkedHashMap.class);
                    mapTemp = objectMapper.readValue(aiMixingRealtime.getAi_d_loc_fc_sp(), LinkedHashMap.class);
                    List<String> keyList = new ArrayList<>(mapTemp.keySet());
                    Object objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet()); // location2...9

                    for (String key : keyList) {
                        LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(key),
                                LinkedHashMap.class);
                        List<String> locationKeyList = new ArrayList<>(locationMapTemp.keySet()); // step1...3

                        for (String locationKey : locationKeyList) {
                            LinkedHashMap<String, Object> stepMapTemp = objectMapper
                                    .convertValue(locationMapTemp.get(locationKey), LinkedHashMap.class);
                            List<String> stepKeyList = new ArrayList<>(stepMapTemp.keySet()); // 1...3

                            for (int i = nLocationMin; i <= nLocationMax; i++) {
                                for (int j = 1; j <= 3; j++) { // step count 1...3
                                    String strItemName = "ai_d_loc_fc" + i + "_" + j;
                                    if (key.equalsIgnoreCase("location" + i) == true
                                            && locationKey.equalsIgnoreCase("step" + j) == true) {

                                        // tagManageList에서 strItemName을 검색
                                        TagManageDTO dto = tagManageList.stream()
                                                .filter(tagManage -> strItemName.equalsIgnoreCase(tagManage.getItm())
                                                        && CommonValue.PROCESS_MIXING
                                                                .concat(String.valueOf(processStep))
                                                                .equals(tagManage.getProc_cd()))
                                                .findAny()
                                                .orElse(null);

                                        if (dto == null) {
                                            continue;
                                        }

                                        // Kafka에 전송할 값 정의
                                        // System.out.println(stepMapTemp.get(stepKeyList.get(0)));
                                        controlMap = new LinkedHashMap<>();
                                        controlMap.put("tag", dto.getTag_sn());
                                        controlMap.put("value", stepMapTemp.get(stepKeyList.get(0)));
                                        controlMap.put("time", strDate);
                                        strBody = objectMapper.writeValueAsString(controlMap);
                                        kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                                        resultCnt++;

                                        continue;
                                    }
                                }
                            }
                        }
                    }
                } catch (JsonProcessingException e) {
                    log.error("JsonProcessingException Occurred in Mixing Process");
                }
            }
        }
        log.info("getMixingSensors count : " + resultCnt);
    }

    /**
     * 약품 실시간 AI 예측값 전달
     * 
     * @param tagManageList
     */
    public void getCoagulantSensors(List<TagManageDTO> tagManageList) {
        int resultCnt = 0;
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // get ai_coagulant_realtime
            AiCoagulantRealtimeDTO aiCoagulantRealtime = databaseService.getLatestAiCoagulantRealtimeValue(processStep);
            log.debug("getLatestAiCoagulantRealtimeValue, result:[{}]", aiCoagulantRealtime != null ? 1 : 0);
            if (aiCoagulantRealtime != null) {
                try {
                    LinkedHashMap<String, Object> controlMap, mapTemp;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = simpleDateFormat.format(aiCoagulantRealtime.getUpd_ti());
                    String strBody;

                    // c_operation_mode
                    AiProcessInitDTO aiCoagulantInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE,
                            processStep);
                    if (aiCoagulantInit != null) {
                        // 운전모드 알람 태그는 항상 전송
                        for (TagManageDTO dto : tagManageList) {
                            if (dto.getItm().equalsIgnoreCase("c_operation_mode_a") == true &&
                                    CommonValue.PROCESS_COAGULANT.concat(String.valueOf(processStep))
                                            .equals(dto.getProc_cd())) {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getTag_sn());
                                controlMap.put("value",
                                        aiCoagulantInit.getInit_val().intValue() == CommonValue.OPERATION_MODE_MANUAL
                                                ? false
                                                : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                                resultCnt++;
                                break;
                            }
                        }
                    }

                    Map<String, Object> c_cf_coagulant = databaseService
                            .selectUsrMng("c" + processStep + "_cf_coagulant");

                    for (TagManageDTO dto : tagManageList) {
                        if (dto.getProc_cd().equalsIgnoreCase(
                                CommonValue.PROCESS_COAGULANT.concat(String.valueOf(processStep))) != true) {
                            continue;
                        }

                        if (dto.getItm().equalsIgnoreCase("ai_c_ti") == true) {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("c1_cf_coagulant") == true) { // 1단계공업 약품종류
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", c_cf_coagulant.get("init_val"));
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("c2_cf_coagulant") == true) { // 2단계생활 약품종류
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", c_cf_coagulant.get("init_val"));
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("c3_cf_coagulant") == true) { // 3단계공업 약품종류
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", c_cf_coagulant.get("init_val"));
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        } else if (dto.getItm().equalsIgnoreCase("ai_c_cf") == true) {
                            // AI 1계열 약품 주입률 예측 최종값
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiCoagulantRealtime.getAi_c_cf());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            resultCnt++;
                        }
                    }
                } catch (JsonProcessingException e) {
                    log.error("JsonProcessingException Occurred in Coagulant Process");
                }
            }
        }
        log.info("getCoagulantSensors count : " + resultCnt);
    }

    /**
     * 분석 서버 Check
     * 
     * @param token 토큰
     */
    @RequestMapping(value = "/internal/analysis", method = RequestMethod.GET)
    public void getAnalysis(@RequestHeader("X-ACCESS-TOKEN") String token) {
        // Token Check
        if (propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getAnalysis, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getAnalysis() initialization analysisDate before one hour
        if (analysisDate == null) {
            analysisDate = new Date();
            analysisDate.setTime(analysisDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.debug("[internal] getAnalysis");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if (currentDate.getTime() - analysisDate.getTime() > CommonValue.ONE_MINUTE) {
            analysisDate = new Date();

            // Resource Manager1 : [GET] cluster/info
            int nActiveState = CommonValue.ACTIVE_STATE_NONE;
            String strHaState, strActiveNodes;
            String strUri = "http://" + globalSystemConfig.getAnalysis1_ResourceManager() + "/ws/v1/cluster/info";
            HttpGet httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);
            StringBuffer stringBuffer;
            ObjectMapper objectMapper = new ObjectMapper();

            HttpResponse response = httpSend.send(httpGet);
            if (response != null) {
                int nStatus = response.getStatusLine().getStatusCode();
                if (nStatus == HttpStatus.SC_OK) {
                    // 하둡 클러스터 정보 response에 대한 Parsing
                    stringBuffer = new StringBuffer();

                    BufferedReader bufferedReader = null;
                    InputStreamReader inputStreamReader = null;
                    try {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while ((strLine = bufferedReader.readLine()) != null) {
                            stringBuffer.append(strLine);
                        }

                        // ObjectMapper를 통해 하둡 클러스터 정보 저장
                        HadoopClusterInfoDTO clusterInfo = objectMapper.readValue(stringBuffer.toString(),
                                HadoopClusterInfoDTO.class);
                        strHaState = clusterInfo.getClusterInfo().getHaState();
                        log.debug("clusterInfo1, haState:[{}]", strHaState);

                        if (strHaState.equalsIgnoreCase(CommonValue.HASTATE_ACTIVE) == true) {
                            nActiveState = CommonValue.ACTIVE_STATE_FIRST;
                        }
                    } catch (IOException e) {
                        log.error("Invalid Body or BufferedReader...");
                        strHaState = CommonValue.HASTATE_ERROR;
                    } finally {
                        if (inputStreamReader != null) {
                            try {
                                inputStreamReader.close();
                            } catch (IOException e) {
                                log.error("inputStreamReader Close Exception occurred");
                            }
                        }
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                log.error("bufferedReader Close Exception occurred");
                            }
                        }
                    }
                } else {
                    strHaState = CommonValue.HASTATE_ERROR;
                }
            } else {
                strHaState = CommonValue.HASTATE_ERROR;
            }

            // Insert system_monitoring(Hadoop Resource Manager)
            SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHost(CommonValue.ANALYSIS1_HOSTNAME);
            systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
            systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_RM);
            systemMonitoringDTO.setMntr_val(strHaState.toUpperCase());
            systemMonitoringDTO.setMntr_upd_ti(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            // Resource Manager2 : cluster/info
            strUri = "http://" + globalSystemConfig.getAnalysis2_ResourceManager() + "/ws/v1/cluster/info";
            httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);

            response = httpSend.send(httpGet);
            if (response != null) {
                int nStatus = response.getStatusLine().getStatusCode();
                if (nStatus == HttpStatus.SC_OK) {
                    // 하둡 클러스터 정보 Response에 대한 Parsing
                    stringBuffer = new StringBuffer();

                    BufferedReader bufferedReader = null;
                    InputStreamReader inputStreamReader = null;
                    try {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while ((strLine = bufferedReader.readLine()) != null) {
                            stringBuffer.append(strLine);
                        }

                        // ObjectMapper를 통해 하둡 클러스터 정보 저장
                        HadoopClusterInfoDTO clusterInfo = objectMapper.readValue(stringBuffer.toString(),
                                HadoopClusterInfoDTO.class);
                        strHaState = clusterInfo.getClusterInfo().getHaState();
                        log.debug("clusterInfo2, haState:[{}]", strHaState);

                        if (strHaState.equalsIgnoreCase(CommonValue.HASTATE_ACTIVE) == true) {
                            nActiveState = CommonValue.ACTIVE_STATE_SECOND;
                        }
                    } catch (IOException e) {
                        log.error("Invalid Body or BufferedReader...");
                        strHaState = CommonValue.HASTATE_ERROR;
                    } finally {
                        if (inputStreamReader != null) {
                            try {
                                inputStreamReader.close();
                            } catch (IOException e) {
                                log.error("inputStreamReader Close Exception occurred");
                            }
                        }
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                log.error("bufferedReader Close Exception occurred");
                            }
                        }
                    }
                } else {
                    strHaState = CommonValue.HASTATE_ERROR;
                }
            } else {
                strHaState = CommonValue.HASTATE_ERROR;
            }

            // Insert system_monitoring(Hadoop Resource Manager)
            systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHost(CommonValue.ANALYSIS2_HOSTNAME);
            systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
            systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_RM);
            systemMonitoringDTO.setMntr_val(strHaState.toUpperCase());
            systemMonitoringDTO.setMntr_upd_ti(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            // Resource Manager1 : cluster/metrics
            if (nActiveState > CommonValue.ACTIVE_STATE_NONE) {
                // ACTIVE Server의 주소로 요청
                String strHostname;
                if (nActiveState == CommonValue.ACTIVE_STATE_FIRST) {
                    strUri = "http://" + globalSystemConfig.getAnalysis1_ResourceManager() + "/ws/v1/cluster/metrics";
                    strHostname = CommonValue.ANALYSIS1_HOSTNAME;
                } else {
                    strUri = "http://" + globalSystemConfig.getAnalysis2_ResourceManager() + "/ws/v1/cluster/metrics";
                    strHostname = CommonValue.ANALYSIS2_HOSTNAME;
                }

                httpGet = new HttpGet(strUri);
                httpGet.setConfig(requestConfig);

                response = httpSend.send(httpGet);
                if (response != null) {
                    int nStatus = response.getStatusLine().getStatusCode();
                    if (nStatus == HttpStatus.SC_OK) {
                        // Metric 정보 Response에 대한 Parsing
                        stringBuffer = new StringBuffer();

                        BufferedReader bufferedReader = null;
                        InputStreamReader inputStreamReader = null;
                        try {
                            inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                            bufferedReader = new BufferedReader(inputStreamReader);

                            String strLine;
                            while ((strLine = bufferedReader.readLine()) != null) {
                                stringBuffer.append(strLine);
                            }

                            // Cluster Metric 정보를 저장하여 active node 계산
                            HadoopClusterMetricsDTO clusterMetrics = objectMapper.readValue(stringBuffer.toString(),
                                    HadoopClusterMetricsDTO.class);

                            int nTemp = clusterMetrics.getClusterMetrics().getActiveNodes();
                            strActiveNodes = String.format("%d", nTemp);
                            log.debug("clusterMetrics, activeNodes:[{}]",
                                    clusterMetrics.getClusterMetrics().getActiveNodes());
                        } catch (IOException e) {
                            log.error("Invalid Body or BufferedReader...");
                            strActiveNodes = "-";
                        } finally {
                            if (inputStreamReader != null) {
                                try {
                                    inputStreamReader.close();
                                } catch (IOException e) {
                                    log.error("inputStreamReader Close Exception occurred");
                                }
                            }
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e) {
                                    log.error("bufferedReader Close Exception occurred");
                                }
                            }
                        }
                    } else {
                        strActiveNodes = "-";
                    }
                } else {
                    strActiveNodes = "-";
                }

                // Insert system_monitoring(Node Manager)
                systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHost(strHostname);
                systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
                systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_NM);
                systemMonitoringDTO.setMntr_val(strActiveNodes);
                systemMonitoringDTO.setMntr_upd_ti(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);
            } else {
                // Insert alarm_notify & SCADA send
                alarmService.alarmNotify(
                        CommonValue.ALARM_CODE_ANALYSIS_OFF,
                        CommonValue.ANALYSIS1_HOSTNAME,
                        CommonValue.ALARM_VALUE_OFF,
                        true);
            }

            // NameNode, DataNode1 : jmx
            nActiveState = CommonValue.ACTIVE_STATE_NONE;
            strUri = "http://" + globalSystemConfig.getAnalysis1_NameNode()
                    + "/jmx?qry=Hadoop:service=NameNode,name=FSNamesystem";
            httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);

            response = httpSend.send(httpGet);
            if (response != null) {
                int nStatus = response.getStatusLine().getStatusCode();
                if (nStatus == HttpStatus.SC_OK) {
                    // JMX Response에 대한 Parsing
                    stringBuffer = new StringBuffer();
                    BufferedReader bufferedReader = null;
                    InputStreamReader inputStreamReader = null;
                    try {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while ((strLine = bufferedReader.readLine()) != null) {
                            stringBuffer.append(strLine);
                        }

                        // JMX 정보를 저장하고 data node 저장
                        HadoopJmxBeans jmxBeans = objectMapper.readValue(stringBuffer.toString(), HadoopJmxBeans.class);
                        strHaState = jmxBeans.getBeans().get(0).getTagHaSate();
                        int nTemp = jmxBeans.getBeans().get(0).getNumLiveDataNodes();
                        strActiveNodes = String.format("%d", nTemp);
                        log.debug("jmxBeans, HAState:[{}], NumLiveDataNodes:[{}]",
                                jmxBeans.getBeans().get(0).getTagHaSate(),
                                jmxBeans.getBeans().get(0).getNumLiveDataNodes());
                    } catch (IOException e) {
                        log.error("Invalid Body or BufferedReader...");
                        strHaState = CommonValue.HASTATE_ERROR;
                        strActiveNodes = "-";
                    } finally {
                        if (inputStreamReader != null) {
                            try {
                                inputStreamReader.close();
                            } catch (IOException e) {
                                log.error("inputStreamReader Close Exception occurred");
                            }
                        }
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                log.error("bufferedReader Close Exception occurred");
                            }
                        }
                    }
                } else {
                    strHaState = CommonValue.HASTATE_ERROR;
                    strActiveNodes = "-";
                }
            } else {
                strHaState = CommonValue.HASTATE_ERROR;
                strActiveNodes = "-";
            }

            // Insert system_monitoring(Name Node)
            systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHost(CommonValue.ANALYSIS1_HOSTNAME);
            systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
            systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_NN);
            systemMonitoringDTO.setMntr_val(strHaState.toUpperCase());
            systemMonitoringDTO.setMntr_upd_ti(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            if (strHaState.equalsIgnoreCase(CommonValue.HASTATE_ACTIVE) == true) {
                nActiveState = CommonValue.ACTIVE_STATE_FIRST;

                // Insert system_monitoring
                systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHost(CommonValue.ANALYSIS1_HOSTNAME);
                systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
                systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_DN);
                systemMonitoringDTO.setMntr_val(strActiveNodes);
                systemMonitoringDTO.setMntr_upd_ti(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);
            }

            // NameNode, DataNode2 : jmx
            strUri = "http://" + globalSystemConfig.getAnalysis2_NameNode()
                    + "/jmx?qry=Hadoop:service=NameNode,name=FSNamesystem";
            httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);

            response = httpSend.send(httpGet);
            if (response != null) {
                int nStatus = response.getStatusLine().getStatusCode();
                if (nStatus == HttpStatus.SC_OK) {
                    // JMX Response에 대한 Parsing
                    stringBuffer = new StringBuffer();
                    BufferedReader bufferedReader = null;
                    InputStreamReader inputStreamReader = null;
                    try {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while ((strLine = bufferedReader.readLine()) != null) {
                            stringBuffer.append(strLine);
                        }

                        // JMX 정보를 저장하고 data node 저장
                        HadoopJmxBeans jmxBeans = objectMapper.readValue(stringBuffer.toString(), HadoopJmxBeans.class);
                        strHaState = jmxBeans.getBeans().get(0).getTagHaSate();
                        int nTemp = jmxBeans.getBeans().get(0).getNumLiveDataNodes();
                        strActiveNodes = String.format("%d", nTemp);
                        log.debug("jmxBeans, HAState:[{}], NumLiveDataNodes:[{}]",
                                jmxBeans.getBeans().get(0).getTagHaSate(),
                                jmxBeans.getBeans().get(0).getNumLiveDataNodes());
                    } catch (IOException e) {
                        log.error("Invalid Body or BufferedReader...");
                        strHaState = CommonValue.HASTATE_ERROR;
                        strActiveNodes = "-";
                    } finally {
                        if (inputStreamReader != null) {
                            try {
                                inputStreamReader.close();
                            } catch (IOException e) {
                                log.error("inputStreamReader Close Exception occurred");
                            }
                        }
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                log.error("bufferedReader Close Exception occurred");
                            }
                        }
                    }
                } else {
                    strHaState = CommonValue.HASTATE_ERROR;
                    strActiveNodes = "-";
                }
            } else {
                strHaState = CommonValue.HASTATE_ERROR;
                strActiveNodes = "-";
            }

            // Insert system_monitoring(Name Node)
            systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHost(CommonValue.ANALYSIS2_HOSTNAME);
            systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
            systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_NN);
            systemMonitoringDTO.setMntr_val(strHaState.toUpperCase());
            systemMonitoringDTO.setMntr_upd_ti(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            if (strHaState.equalsIgnoreCase(CommonValue.HASTATE_ACTIVE) == true) {
                nActiveState = CommonValue.ACTIVE_STATE_SECOND;

                // Insert system_monitoring
                systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHost(CommonValue.ANALYSIS2_HOSTNAME);
                systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
                systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_DN);
                systemMonitoringDTO.setMntr_val(strActiveNodes);
                systemMonitoringDTO.setMntr_upd_ti(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);
            }

            // Name Node Active Check(Insert alarm_notify, same resource manager's alarm)
            if (nActiveState == CommonValue.ACTIVE_STATE_NONE) {
                // Insert alarm_notify & SCADA send
                alarmService.alarmNotify(
                        CommonValue.ALARM_CODE_ANALYSIS_OFF,
                        CommonValue.ANALYSIS1_HOSTNAME,
                        CommonValue.ALARM_VALUE_OFF,
                        true);
            }
        }
    }

    /**
     * 데이터 수집기 상태 확인
     * 
     * @param token 토큰
     */
    @RequestMapping(value = "/internal/daq", method = RequestMethod.GET)
    public void getDaq(@RequestHeader("X-ACCESS-TOKEN") String token) {
        // Token Check
        if (propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getDaq, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getDaq() initialization daqDate before one hour
        if (daqDate == null) {
            daqDate = new Date();
            daqDate.setTime(daqDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.debug("[internal] getDaq");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if (currentDate.getTime() - daqDate.getTime() > CommonValue.ONE_MINUTE) {
            daqDate = new Date();

            // 최근 5분 간 데이터 수집기 HealthCheck 이력을 조회하기 위한 Date 선언
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            Date startTime = calendar.getTime();

            List<SystemMonitoringDTO> systemMonitoringList = databaseService.getLatestSystemMonitoring(startTime);
            log.debug("getLatestSystemMonitoring, result:[{}]", systemMonitoringList.size());

            String strDaq1Value = CommonValue.ALARM_VALUE_OFF;
            String strDaq2Value = CommonValue.ALARM_VALUE_OFF;

            // 최근 5분 간 데이터 수집기 측정 이력이 있다면 해당 값 저장
            for (SystemMonitoringDTO dto : systemMonitoringList) {
                if (dto.getMntr_ty() == CommonValue.MONITORING_TYPE_COLLECTOR) {
                    if (dto.getHost().equalsIgnoreCase(CommonValue.COLLECTOR1_HOSTNAME) == true) {
                        strDaq1Value = dto.getMntr_val();
                    } else if (dto.getHost().equalsIgnoreCase(CommonValue.COLLECTOR2_HOSTNAME) == true) {
                        strDaq2Value = dto.getMntr_val();
                    }
                }
            }

            // If alarm value is 'OFF' then, insert alarm_notify and insert
            // system_monitoring
            if (strDaq1Value.equalsIgnoreCase(CommonValue.ALARM_VALUE_OFF) == true) {
                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHost(CommonValue.COLLECTOR1_HOSTNAME);
                systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_COLLECTOR);
                systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR1_HOSTNAME);
                systemMonitoringDTO.setMntr_val(CommonValue.ALARM_VALUE_OFF);
                systemMonitoringDTO.setMntr_upd_ti(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);

                // Insert alarm_notify & SCADA send
                alarmService.alarmNotify(
                        CommonValue.ALARM_CODE_COLLECTOR_OFF1,
                        CommonValue.COLLECTOR1_HOSTNAME,
                        CommonValue.ALARM_VALUE_OFF,
                        true);
            }

            // If alarm value is 'OFF' then, insert alarm_notify and insert
            // system_monitoring
            if (strDaq2Value.equalsIgnoreCase(CommonValue.ALARM_VALUE_OFF) == true) {
                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHost(CommonValue.COLLECTOR2_HOSTNAME);
                systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_COLLECTOR);
                systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR2_HOSTNAME);
                systemMonitoringDTO.setMntr_val(CommonValue.ALARM_VALUE_OFF);
                systemMonitoringDTO.setMntr_upd_ti(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);

                // Insert alarm_notify & SCADA send
                alarmService.alarmNotify(
                        CommonValue.ALARM_CODE_COLLECTOR_OFF2,
                        CommonValue.COLLECTOR2_HOSTNAME,
                        CommonValue.ALARM_VALUE_OFF,
                        true);
            }
            // boolean bResponseOK = false;
            //
            // // DAQ1 API Check
            // String strUri = "http://" + globalSystemConfig.getScada1_daq() +
            // "/api/plugins.json";
            // HttpGet httpGet = new HttpGet(strUri);
            // httpGet.setConfig(requestConfig);
            //
            // HttpResponse response = httpSend.send(httpGet);
            // if(response != null)
            // {
            // int nStatus = response.getStatusLine().getStatusCode();
            // log.debug("DAQ1 HealthCheck...Response:[{}]", nStatus);
            // if(nStatus == HttpStatus.SC_OK)
            // {
            // bResponseOK = true;
            // }
            // else
            // {
            // bResponseOK = false;
            // }
            // }
            // else
            // {
            // bResponseOK = false;
            // log.debug("DAQ1 HealthCheck...Response:[ERROR]");
            // }
            //
            // // According to bResponseOK value, Insert system_monitoring
            // if(bResponseOK == true)
            // {
            // SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
            // systemMonitoringDTO.setHostname(CommonValue.COLLECTOR1_HOSTNAME);
            // systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
            // systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR1_HOSTNAME);
            // systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_ON);
            // systemMonitoringDTO.setUpdate_time(new Date());
            // databaseService.addSystemMonitoring(systemMonitoringDTO);
            // }
            // else
            // {
            // SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
            // systemMonitoringDTO.setHostname(CommonValue.COLLECTOR1_HOSTNAME);
            // systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
            // systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR1_HOSTNAME);
            // systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_OFF);
            // systemMonitoringDTO.setUpdate_time(new Date());
            // databaseService.addSystemMonitoring(systemMonitoringDTO);
            //
            // // Insert alarm_notify & SCADA send
            // alarmService.alarmNotify(
            // CommonValue.ALARM_CODE_DAQ_OFF1,
            // CommonValue.COLLECTOR1_HOSTNAME,
            // CommonValue.ALARM_VALUE_OFF,
            // true);
            // }
            //
            // // DAQ2 API Check
            // bResponseOK = false;
            // strUri = "http://" + globalSystemConfig.getScada2_daq() +
            // "/api/plugins.json";
            // httpGet = new HttpGet(strUri);
            // httpGet.setConfig(requestConfig);
            //
            // response = httpSend.send(httpGet);
            // if(response != null)
            // {
            // int nStatus = response.getStatusLine().getStatusCode();
            // log.debug("DAQ2 HealthCheck...Response:[{}]", nStatus);
            // if(nStatus == HttpStatus.SC_OK)
            // {
            // bResponseOK = true;
            // }
            // else
            // {
            // bResponseOK = false;
            // }
            // }
            // else
            // {
            // bResponseOK = false;
            // log.debug("DAQ2 HealthCheck...Response:[ERROR]");
            // }
            //
            // // According to bResponseOK value, Insert system_monitoring
            // if(bResponseOK == true)
            // {
            // SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
            // systemMonitoringDTO.setHostname(CommonValue.COLLECTOR2_HOSTNAME);
            // systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
            // systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR2_HOSTNAME);
            // systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_ON);
            // systemMonitoringDTO.setUpdate_time(new Date());
            // databaseService.addSystemMonitoring(systemMonitoringDTO);
            // }
            // else
            // {
            // SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
            // systemMonitoringDTO.setHostname(CommonValue.COLLECTOR2_HOSTNAME);
            // systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
            // systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR2_HOSTNAME);
            // systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_OFF);
            // systemMonitoringDTO.setUpdate_time(new Date());
            // databaseService.addSystemMonitoring(systemMonitoringDTO);
            //
            // // Insert alarm_notify & SCADA send
            // alarmService.alarmNotify(
            // CommonValue.ALARM_CODE_DAQ_OFF2,
            // CommonValue.COLLECTOR2_HOSTNAME,
            // CommonValue.ALARM_VALUE_OFF,
            // true);
            // }
        }
    }

    /**
     * 소독 CTR 조회
     */
    public void getDisinfectionControl() {
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // 1. get operation mode - pre disinfection
            AiProcessInitDTO aiPreDisinfectionInit = databaseService.getAiDisinfectionInit(
                    CommonValue.G_PRE_OPERATION_MODE, processStep, CommonValue.DISINFECTION_PRE_STEP);
            log.debug("getAiDisinfectionInit, result:[{}]", aiPreDisinfectionInit != null ? 1 : 0);

            if (aiPreDisinfectionInit != null) {
                int nOperationMode = aiPreDisinfectionInit.getInit_val().intValue();

                // 수동 모드일 경우 전송하지 않음
                if (nOperationMode > CommonValue.OPERATION_MODE_MANUAL) {
                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRnti(runTime);
                    queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    queryDto.setProcessStep(processStep);

                    List<AiProcessControlDTO> aiDisinfectionControlList = databaseService
                            .getListAiPreDisinfectionControl(queryDto);
                    if (aiDisinfectionControlList.size() > 0) {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date rnti = new Date();
                        boolean alarmExceededFlag = false; //임계치 알람 해당 여부
                        try {
                            for (AiProcessControlDTO dto : aiDisinfectionControlList) {
                                InterfaceAlarmControlHistoryDTO paramAlarm = new InterfaceAlarmControlHistoryDTO();
                                paramAlarm.setAlm_ntf_ti(dto.getRnti());
                                paramAlarm.setProcess(CommonValue.PROCESS_DISINFECTION);
                                paramAlarm.setProcessStep(String.valueOf(processStep));
                                paramAlarm.setDisinfectionIndex(CommonValue.DISINFECTION_PRE_STEP);
                                paramAlarm.setAlmTy(CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED);
                                paramAlarm.setUpdTi(dto.getUpd_ti());
                                paramAlarm.setTagSn(dto.getTag_sn());

                                InterfaceAlarmControlHistoryDTO alarmExceededInfo = databaseService
                                        .getAlarmExceeded(paramAlarm);
                                if (alarmExceededInfo != null) { //임계치 제어에 해당
                                	alarmExceededFlag = true;
                                    if (bFirst == true) {
                                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                        rnti = dto.getRnti();
                                        popupMap.put("alarm_id", alarmExceededInfo.getAlm_id());
                                        popupMap.put("message", alarmExceededInfo.getDp_nm());
                                        popupMap.put("url", alarmExceededInfo.getUrl());
                                        popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(popupMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                        bFirst = false;
                                    }
                                    if (!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                        updateDto.setProcessStep(processStep);
                                        updateDto.setDisinfectionIndex(CommonValue.DISINFECTION_PRE_STEP);
                                        databaseService.modAiPreDisinfectionControlKafkaFlag(updateDto);
                                    }

                                } else {
                                    if (nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO) {
                                        // 3. if operation_mode==1 (semi_auto)
                                        // 3-1. send control value to kafka ai_popup
                                        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(
                                                CommonValue.ALARM_CODE_DISINFECTION_AI_PRE_CONTROL, processStep);
                                        if (alarmInfo != null) {
                                            // KAFKA topic is called only once.
                                            if (bFirst == true) {
                                                LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                                rnti = dto.getRnti();
                                                popupMap.put("alarm_id", alarmInfo.getAlm_id());
                                                popupMap.put("message", alarmInfo.getDp_nm());
                                                popupMap.put("url", alarmInfo.getUrl());
                                                popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                                strBody = objectMapper.writeValueAsString(popupMap);
                                                kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                                bFirst = false;
                                            }
                                            if (!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                                // 3-2. update kafka_flag=1 (kafka_popup)
                                                AiProcessControlDTO updateDto = dto;
                                                updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                                updateDto.setProcessStep(processStep);
                                                updateDto.setDisinfectionIndex(CommonValue.DISINFECTION_PRE_STEP);
                                                databaseService.modAiPreDisinfectionControlKafkaFlag(updateDto);
                                            }
                                        } else {
                                            log.error("Does not exist alarmInfo:[{]]",
                                                    CommonValue.ALARM_CODE_DISINFECTION_AI_PRE_CONTROL);
                                        }
                                    } else if (nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                                        // 4. if operation_mode==2 (full_auto)

                                        // 4-1. send control value to kafka ai_control
                                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                        controlMap.put("tag", dto.getTag_sn());
                                        controlMap.put("value", Float.parseFloat(dto.getTag_val()));
                                        controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(controlMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                        // 4-2. update kafka_flag=3 (kafka_send)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_SEND);
                                        updateDto.setProcessStep(processStep);
                                        updateDto.setDisinfectionIndex(CommonValue.DISINFECTION_PRE_STEP);
                                        databaseService.modAiPreDisinfectionControlKafkaFlag(updateDto);
                                    }
                                }
                            } // for문 끝
                            //자동모드이면서 && 임계치 알람이 아닌 경우( = AI제어 알람인 경우) -- 이력 업데이트.
                            if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO && !alarmExceededFlag) {
                                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_DISINFECTION_AI_PRE_CONTROL, processStep);
                                Date currentDate = new Date();
                                
                                // insert alarm_notify & get almSeq
                                if(alarmInfo != null) {
                                	int almSeq = alarmService.alarmNotify(
                                			alarmInfo.getAlm_id(),
                                			alarmInfo.getDp_nm(),
                                			alarmInfo.getUrl(),
                                			simpleDateFormat.format(currentDate)
                                			);
                                	if(almSeq !=0) {
                                		//ctrHisList insert 
                                		for(AiProcessControlDTO ctr : aiDisinfectionControlList) {
                                			InterfaceAlarmControlHistoryDTO his = new InterfaceAlarmControlHistoryDTO();
                                			his.setAlm_id(alarmInfo.getAlm_id());
                                			his.setSeq(almSeq);// alarmNotify에 넣을때 사용한 seq를 반환받아서 넣어야함.
                                			his.setAlmTy(alarmInfo.getAlm_ty());
                                			his.setCtrTi(currentDate); // 현재시간
                                			his.setCtrYn("A");
                                			his.setTagSn(ctr.getTag_sn());
                                			his.setUpdTi(ctr.getUpd_ti());
                                			databaseService.insertAlarmControlHistory(his);
                                		}
                                	}
                                } else {
                                    log.error("Does not exist alarmInfo:[{]]", CommonValue.ALARM_CODE_DISINFECTION_AI_PRE_CONTROL);
                                }
                            }
                        } catch (JsonProcessingException e) {
                            log.error("JsonProcessingException Occurred in Pre Disinfection Control Process");
                        } catch (NumberFormatException e) {
                            log.error("NumberException Occurred in Pre Disinfection Control Process");
                        }
                    }
                }
            }
        }

        // 1. get operation mode - post disinfection
        AiProcessInitDTO aiPostDisinfectionInit = databaseService
                .getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE, 2, CommonValue.DISINFECTION_POST_STEP);
        log.debug("getAiDisinfectionInit, result:[{}]", aiPostDisinfectionInit != null ? 1 : 0);

        if (aiPostDisinfectionInit != null) {
            int nOperationMode = aiPostDisinfectionInit.getInit_val().intValue();

            // 수동 모드일 경우 전송하지 않음
            if (nOperationMode > CommonValue.OPERATION_MODE_MANUAL) {
                // 2. get latest(10minutes) control value(kafka_flag = 0)
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, -10);
                Date runTime = calendar.getTime();

                AiProcessControlDTO queryDto = new AiProcessControlDTO();
                queryDto.setRnti(runTime);
                queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                queryDto.setProcessStep(2);

                List<AiProcessControlDTO> aiDisinfectionControlList = databaseService
                        .getListAiPostDisinfectionControl(queryDto);
                if (aiDisinfectionControlList.size() > 0) {
                    String strBody;
                    boolean bFirst = true;
                    ObjectMapper objectMapper = new ObjectMapper();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date rnti = new Date();
                    boolean alarmExceededFlag = false; //임계치 알람 해당 여부
                    try {
                        for (AiProcessControlDTO dto : aiDisinfectionControlList) {
                            InterfaceAlarmControlHistoryDTO paramAlarm = new InterfaceAlarmControlHistoryDTO();
                            paramAlarm.setAlm_ntf_ti(dto.getRnti());
                            paramAlarm.setProcess(CommonValue.PROCESS_DISINFECTION);
                            paramAlarm.setProcessStep("2");
                            paramAlarm.setDisinfectionIndex(CommonValue.DISINFECTION_POST_STEP);
                            paramAlarm.setAlmTy(CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED);
                            paramAlarm.setUpdTi(dto.getUpd_ti());
                            paramAlarm.setTagSn(dto.getTag_sn());

                            InterfaceAlarmControlHistoryDTO alarmExceededInfo = databaseService
                                    .getAlarmExceeded(paramAlarm);
                            if (alarmExceededInfo != null) { //임계치 제어에 해당
                            	alarmExceededFlag = true;
                                if (bFirst == true) {
                                    LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                    rnti = dto.getRnti();
                                    popupMap.put("alarm_id", alarmExceededInfo.getAlm_id());
                                    popupMap.put("message", alarmExceededInfo.getDp_nm());
                                    popupMap.put("url", alarmExceededInfo.getUrl());
                                    popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                    strBody = objectMapper.writeValueAsString(popupMap);
                                    kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                    bFirst = false;
                                }
                                if (!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                    // 3-2. update kafka_flag=1 (kafka_popup)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                    updateDto.setProcessStep(2);
                                    updateDto.setDisinfectionIndex(CommonValue.DISINFECTION_POST_STEP);
                                    databaseService.modAiPostDisinfectionControlKafkaFlag(updateDto);
                                }

                            } else {
                                if (nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO) {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(
                                            CommonValue.ALARM_CODE_DISINFECTION_AI_POST_CONTROL, 2);
                                    if (alarmInfo != null) {
                                        // KAFKA topic is called only once.
                                        if (bFirst == true) {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            rnti = dto.getRnti();
                                            popupMap.put("alarm_id", alarmInfo.getAlm_id());
                                            popupMap.put("message", alarmInfo.getDp_nm());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                            bFirst = false;
                                        }
                                        if (!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                            // 3-2. update kafka_flag=1 (kafka_popup)
                                            AiProcessControlDTO updateDto = dto;
                                            updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                            updateDto.setProcessStep(2);
                                            updateDto.setDisinfectionIndex(CommonValue.DISINFECTION_POST_STEP);
                                            databaseService.modAiPostDisinfectionControlKafkaFlag(updateDto);
                                        }
                                    } else {
                                        log.error("Does not exist alarmInfo:[{]]",
                                                CommonValue.ALARM_CODE_DISINFECTION_AI_POST_CONTROL);
                                    }
                                } else if (nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                                    // 4. if operation_mode==2 (full_auto)

                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getTag_sn());
                                    controlMap.put("value", Float.parseFloat(dto.getTag_val()));
                                    controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                    // 4-2. update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_SEND);
                                    updateDto.setProcessStep(2);
                                    updateDto.setDisinfectionIndex(CommonValue.DISINFECTION_POST_STEP);
                                    databaseService.modAiPostDisinfectionControlKafkaFlag(updateDto);
                                }
                            }
                        } // for문 끝
                        //자동모드이면서 && 임계치 알람이 아닌 경우( = AI제어 알람인 경우) -- 이력 업데이트.
                        if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO && !alarmExceededFlag) {
                            AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_DISINFECTION_AI_POST_CONTROL, 2);
                            Date currentDate = new Date();
                            
                            // insert alarm_notify & get almSeq
                            if(alarmInfo != null) {
                            	int almSeq = alarmService.alarmNotify(
                            			alarmInfo.getAlm_id(),
                            			alarmInfo.getDp_nm(),
                            			alarmInfo.getUrl(),
                            			simpleDateFormat.format(currentDate)
                            			);
                            	if(almSeq !=0) {
                            		//ctrHisList insert 
                            		for(AiProcessControlDTO ctr : aiDisinfectionControlList) {
                            			InterfaceAlarmControlHistoryDTO his = new InterfaceAlarmControlHistoryDTO();
                            			his.setAlm_id(alarmInfo.getAlm_id());
                            			his.setSeq(almSeq);// alarmNotify에 넣을때 사용한 seq를 반환받아서 넣어야함.
                            			his.setAlmTy(alarmInfo.getAlm_ty());
                            			his.setCtrTi(currentDate); // 현재시간
                            			his.setCtrYn("A");
                            			his.setTagSn(ctr.getTag_sn());
                            			his.setUpdTi(ctr.getUpd_ti());
                            			databaseService.insertAlarmControlHistory(his);
                            		}
                            	}
                            } else {
                                log.error("Does not exist alarmInfo:[{]]", CommonValue.ALARM_CODE_DISINFECTION_AI_POST_CONTROL);
                            }
                        }
                    } catch (JsonProcessingException e) {
                        log.error("JsonProcessingException Occurred in Post Disinfection Control Process");
                    } catch (NumberFormatException e) {
                        log.error("NumberException Occurred in Post Disinfection Control Process");
                    }
                }
            }
        }
    }

    /**
     * 여과 CTR 조회
     */
    public void getFilterControl() {
        // 1. get operation mode
        AiProcessInitDTO aiFilterInit = databaseService.getAiFilterInit(CommonValue.F_OPERATION_MODE, 2);
        log.debug("getAiFilterInit, result:[{}]", aiFilterInit != null ? 1 : 0);

        if (aiFilterInit != null) {
            int nOperationMode = aiFilterInit.getInit_val().intValue();

            // 수동 모드일 경우 전송하지 않음
            if (nOperationMode > CommonValue.OPERATION_MODE_MANUAL) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, -10);
                Date runTime = calendar.getTime();

                AiProcessControlDTO queryDto = new AiProcessControlDTO();
                queryDto.setRnti(runTime);
                queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                queryDto.setProcessStep(2);

                // 2. get latest(10 minutes) control value(kafka_flag = 0)
                List<AiProcessControlDTO> aiFilterControlList = databaseService.getListAiFilterControl(queryDto);
                if (aiFilterControlList.size() > 0) {
                    String strBody;
                    boolean bFirst = true;
                    ObjectMapper objectMapper = new ObjectMapper();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date rnti = new Date();
                    try {
                        for (AiProcessControlDTO dto : aiFilterControlList) {
                            if (nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO) {
                                // 3. if operation_mode==1 (semi_auto)
                                // 3-1. send control value to kafka ai_popup
                                AlarmInfoDTO alarmInfo = alarmInfoList
                                        .getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_FILTER_AI_CONTROL, 2);
                                if (alarmInfo != null) {
                                    // KAFKA topic is called only once.
                                    if (bFirst == true) {
                                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                        rnti = dto.getRnti();
                                        popupMap.put("alarm_id", alarmInfo.getAlm_id());
                                        popupMap.put("message", alarmInfo.getDp_nm());
                                        popupMap.put("url", alarmInfo.getUrl());
                                        popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(popupMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                        bFirst = false;
                                    }

                                    if (!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                        updateDto.setProcessStep(2);
                                        databaseService.modAiFilterControlKafkaFlag(updateDto);
                                    }
                                } else {
                                    log.error("Does not exist alarmInfo:[{}]",
                                            CommonValue.ALARM_CODE_FILTER_AI_CONTROL);
                                }
                            } else if (nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                                // 4. if operation_mode==2 (full_auto)
                                // 4-1. send control value to kafka ai_control
                                LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getTag_sn());
                                controlMap.put("value",
                                        dto.getTag_val().equalsIgnoreCase(CommonValue.CONTROL_TRUE) ? true
                                                : Float.parseFloat(dto.getTag_val()));
                                controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                strBody = objectMapper.writeValueAsString(controlMap);
                                kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                // 4-2. update kafka_flag=3 (kafka_send)
                                AiProcessControlDTO updateDto = dto;
                                updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_SEND);
                                updateDto.setProcessStep(2);
                                databaseService.modAiFilterControlKafkaFlag(updateDto);
                            }
                        } // for문 끝
                        //자동모드이면서 && 임계치 알람이 아닌 경우( = AI제어 알람인 경우) -- 이력 업데이트.
                        if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                            AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_FILTER_AI_CONTROL, 2);
                            Date currentDate = new Date();
                            
                            // insert alarm_notify & get almSeq
                            if(alarmInfo != null) {
                            	int almSeq = alarmService.alarmNotify(
                            			alarmInfo.getAlm_id(),
                            			alarmInfo.getDp_nm(),
                            			alarmInfo.getUrl(),
                            			simpleDateFormat.format(currentDate)
                            			);
                            	if(almSeq !=0) {
                            		//ctrHisList insert 
                            		for(AiProcessControlDTO ctr : aiFilterControlList) {
                            			InterfaceAlarmControlHistoryDTO his = new InterfaceAlarmControlHistoryDTO();
                            			his.setAlm_id(alarmInfo.getAlm_id());
                            			his.setSeq(almSeq);// alarmNotify에 넣을때 사용한 seq를 반환받아서 넣어야함.
                            			his.setAlmTy(alarmInfo.getAlm_ty());
                            			his.setCtrTi(currentDate); // 현재시간
                            			his.setCtrYn("A");
                            			his.setTagSn(ctr.getTag_sn());
                            			his.setUpdTi(ctr.getUpd_ti());
                            			databaseService.insertAlarmControlHistory(his);
                            		}
                            	}
                            } else {
                                log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_FILTER_AI_CONTROL);
                            }
                        }
                    } catch (JsonProcessingException e) {
                        log.error("JsonProcessingException Occurred in Filter Control Process");
                    } catch (NumberFormatException e) {
                        log.error("NumberException Occurred in Filter Control Process");
                    }
                }
            }
        }
    }

    /**
     * 침전 CTR 조회
     */
    public void getSedimentationControl() {
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // 1. get operation mode
            AiProcessInitDTO aiSedimentationInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE,
                    processStep);
            log.debug("getAiSedimentationInit, result:[{}]", aiSedimentationInit != null ? 1 : 0);

            if (aiSedimentationInit != null) {
                int nOperationMode = aiSedimentationInit.getInit_val().intValue();

                // 수동 모드일 경우 전송하지 않음
                if (nOperationMode > CommonValue.OPERATION_MODE_MANUAL) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRnti(runTime);
                    queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    queryDto.setProcessStep(processStep);

                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    List<AiProcessControlDTO> aiSedimentationControlList = databaseService
                            .getListAiSedimentationControl(queryDto);
                    if (aiSedimentationControlList.size() > 0) {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date rnti = new Date();
                        try {
                            for (AiProcessControlDTO dto : aiSedimentationControlList) {
                                if (nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO) {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(
                                            CommonValue.ALARM_CODE_SEDIMENTATION_AI_CONTROL, processStep);
                                    if (alarmInfo != null) {
                                        // KAFKA topic is called only once.
                                        if (bFirst == true) {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            rnti = dto.getRnti();
                                            popupMap.put("alarm_id", alarmInfo.getAlm_id());
                                            popupMap.put("message", alarmInfo.getDp_nm());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                            bFirst = false;
                                        }
                                        if (!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                            // 3-2. update kafka_flag=1 (kafka_popup)
                                            AiProcessControlDTO updateDto = dto;
                                            updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                            updateDto.setProcessStep(processStep);
                                            databaseService.modAiSedimentationControlKafkaFlag(updateDto);
                                        }
                                    } else {
                                        log.error("Does not exist alarmInfo:[{}]",
                                                CommonValue.ALARM_CODE_SEDIMENTATION_AI_CONTROL);
                                    }
                                } else if (nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                                    // 4. if operation_mode==2 (full_auto)

                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getTag_sn());
                                    controlMap.put("value",
                                            dto.getTag_val().equalsIgnoreCase(CommonValue.CONTROL_TRUE) ? true
                                                    : Float.parseFloat(dto.getTag_val()));
                                    controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                    // 4-2.update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_SEND);
                                    updateDto.setProcessStep(processStep);
                                    databaseService.modAiSedimentationControlKafkaFlag(updateDto);
                                }
                            } // for문 끝
                            //자동모드이면서 && 임계치 알람이 아닌 경우( = AI제어 알람인 경우) -- 이력 업데이트.
                            if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_SEDIMENTATION_AI_CONTROL, processStep);
                                Date currentDate = new Date();
                                
                                // insert alarm_notify & get almSeq
                                if(alarmInfo != null) {
                                	int almSeq = alarmService.alarmNotify(
                                			alarmInfo.getAlm_id(),
                                			alarmInfo.getDp_nm(),
                                			alarmInfo.getUrl(),
                                			simpleDateFormat.format(currentDate)
                                			);
                                	if(almSeq !=0) {
                                		//ctrHisList insert 
                                		for(AiProcessControlDTO ctr : aiSedimentationControlList) {
                                			InterfaceAlarmControlHistoryDTO his = new InterfaceAlarmControlHistoryDTO();
                                			his.setAlm_id(alarmInfo.getAlm_id());
                                			his.setSeq(almSeq);// alarmNotify에 넣을때 사용한 seq를 반환받아서 넣어야함.
                                			his.setAlmTy(alarmInfo.getAlm_ty());
                                			his.setCtrTi(currentDate); // 현재시간
                                			his.setCtrYn("A");
                                			his.setTagSn(ctr.getTag_sn());
                                			his.setUpdTi(ctr.getUpd_ti());
                                			databaseService.insertAlarmControlHistory(his);
                                		}
                                	}
                                } else {
                                    log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_SEDIMENTATION_AI_CONTROL);
                                }
                            }
                        } catch (JsonProcessingException e) {
                            log.error("JsonProcessingException Occurred in Sedimentation Control Process");
                        } catch (NumberFormatException e) {
                            log.error("NumberException Occurred in Sedimentation Control Process");
                        }
                    }
                }
            }
        }
    }

    /**
     * 혼화응집 CTR 조회
     */
    public void getMixingControl() {
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // 1. get operation mode
            AiProcessInitDTO aiMixingInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE, processStep);
            log.debug("getAiMixingInit, result:[{}]", aiMixingInit != null ? 1 : 0);

            if (aiMixingInit != null) {
                int nOperationMode = aiMixingInit.getInit_val().intValue();

                // 수동 모드일 경우 전송하지 않음
                if (nOperationMode > CommonValue.OPERATION_MODE_MANUAL) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRnti(runTime);
                    queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    queryDto.setProcessStep(processStep);

                    // 2. get latest(10 minutes) control value(kafka_flag = 0)
                    List<AiProcessControlDTO> aiMixingControlList = databaseService.getListAiMixingControl(queryDto);
                    if (aiMixingControlList.size() > 0) {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date rnti = new Date();
                        boolean alarmExceededFlag = false; //임계치 알람 해당 여부
                        try {
                            for (AiProcessControlDTO dto : aiMixingControlList) {
                                InterfaceAlarmControlHistoryDTO paramAlarm = new InterfaceAlarmControlHistoryDTO();
                                paramAlarm.setAlm_ntf_ti(dto.getRnti());
                                paramAlarm.setProcess(CommonValue.PROCESS_MIXING);
                                paramAlarm.setProcessStep(String.valueOf(processStep));
                                paramAlarm.setAlmTy(CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED);
                                paramAlarm.setUpdTi(dto.getUpd_ti());
                                paramAlarm.setTagSn(dto.getTag_sn());

                                InterfaceAlarmControlHistoryDTO alarmExceededInfo = databaseService
                                        .getAlarmExceeded(paramAlarm);
                                if (alarmExceededInfo != null) { //임계치 제어에 해당
                                	alarmExceededFlag = true;
                                    if (bFirst == true) {
                                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                        rnti = dto.getRnti();
                                        popupMap.put("alarm_id", alarmExceededInfo.getAlm_id());
                                        popupMap.put("message", alarmExceededInfo.getDp_nm());
                                        popupMap.put("url", alarmExceededInfo.getUrl());
                                        popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(popupMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                        bFirst = false;
                                    }
                                    if (!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                        updateDto.setProcessStep(processStep);
                                        databaseService.modAiMixingControlKafkaFlag(updateDto);
                                    }
                                } else {
                                    if (nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO) {
                                        // 3. if operation_mode==1 (semi_auto)
                                        // 3-1. send control value to kafka ai_popup
                                        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(
                                                CommonValue.ALARM_CODE_MIXING_AI_CONTROL, processStep);
                                        if (alarmInfo != null) {
                                            // KAFKA topic is called only once.
                                            if (bFirst == true) {
                                                LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                                rnti = dto.getRnti();
                                                popupMap.put("alarm_id", alarmInfo.getAlm_id());
                                                popupMap.put("message", alarmInfo.getDp_nm());
                                                popupMap.put("url", alarmInfo.getUrl());
                                                popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                                strBody = objectMapper.writeValueAsString(popupMap);
                                                kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                                bFirst = false;
                                            }
                                            if (!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                                // 3-2. update kafka_flag=1 (kafka_popup)
                                                AiProcessControlDTO updateDto = dto;
                                                updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                                updateDto.setProcessStep(processStep);
                                                databaseService.modAiMixingControlKafkaFlag(updateDto);
                                            }
                                        } else {
                                            log.error("Does not exist alarmInfo:[{]]",
                                                    CommonValue.ALARM_CODE_MIXING_AI_CONTROL);
                                        }
                                    } else if (nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                                        // 4. if operation_mode==2 (full_auto)

                                        // 4-1. send control value to kafka ai_control
                                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                        controlMap.put("tag", dto.getTag_sn());
                                        controlMap.put("value", Float.parseFloat(dto.getTag_val()));
                                        controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(controlMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                        // 4-2. update kafka_flag=3 (kafka_send)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_SEND);
                                        updateDto.setProcessStep(processStep);
                                        databaseService.modAiMixingControlKafkaFlag(updateDto);
                                    }
                                }
                            } // for문 끝
                            //자동모드이면서 && 임계치 알람이 아닌 경우( = AI제어 알람인 경우) -- 이력 업데이트.
                            if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO && !alarmExceededFlag) {
                                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_MIXING_AI_CONTROL, processStep);
                                Date currentDate = new Date();
                                
                                // insert alarm_notify & get almSeq
                                if(alarmInfo != null) {
                                	int almSeq = alarmService.alarmNotify(
                                			alarmInfo.getAlm_id(),
                                			alarmInfo.getDp_nm(),
                                			alarmInfo.getUrl(),
                                			simpleDateFormat.format(currentDate)
                                			);
                                	if(almSeq !=0) {
                                		//ctrHisList insert 
                                		for(AiProcessControlDTO ctr : aiMixingControlList) {
                                			InterfaceAlarmControlHistoryDTO his = new InterfaceAlarmControlHistoryDTO();
                                			his.setAlm_id(alarmInfo.getAlm_id());
                                			his.setSeq(almSeq);// alarmNotify에 넣을때 사용한 seq를 반환받아서 넣어야함.
                                			his.setAlmTy(alarmInfo.getAlm_ty());
                                			his.setCtrTi(currentDate); // 현재시간
                                			his.setCtrYn("A");
                                			his.setTagSn(ctr.getTag_sn());
                                			his.setUpdTi(ctr.getUpd_ti());
                                			databaseService.insertAlarmControlHistory(his);
                                		}
                                	}
                                } else {
                                    log.error("Does not exist alarmInfo:[{]]", CommonValue.ALARM_CODE_MIXING_AI_CONTROL);
                                }
                            }
                        } catch (JsonProcessingException e) {
                            log.error("JsonProcessingException Occurred in Mixing Control Process");
                        } catch (NumberFormatException e) {
                            log.error("NumberException Occurred in Mixing Control Process");
                        }
                    }
                }
            }
        }
    }

    /**
     * 약품 CTR 조회
     */
    public void getCoagulantControl() {
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // 1. get operation mode
            AiProcessInitDTO aiCoagulantInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE,
                    processStep);
            log.debug("getAiCoagulantInit, result:[{}]", aiCoagulantInit != null ? 1 : 0);

            if (aiCoagulantInit != null) {
                int nOperationMode = aiCoagulantInit.getInit_val().intValue();

                // 수동 모드일 경우 전송하지 않음
                if (nOperationMode > CommonValue.OPERATION_MODE_MANUAL) {
                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRnti(runTime);
                    queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    queryDto.setProcessStep(processStep);

                    List<AiProcessControlDTO> aiCoagulantControlList = databaseService
                            .getListAiCoagulantControl(queryDto);

                    if (aiCoagulantControlList.size() > 0) {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date rnti = new Date();
                        boolean alarmExceededFlag = false; //임계치 알람 해당 여부
                        try {
                            for (AiProcessControlDTO dto : aiCoagulantControlList) {
                                InterfaceAlarmControlHistoryDTO paramAlarm = new InterfaceAlarmControlHistoryDTO();
                                paramAlarm.setAlm_ntf_ti(dto.getRnti());
                                paramAlarm.setProcess(CommonValue.PROCESS_COAGULANT);
                                paramAlarm.setProcessStep(String.valueOf(processStep));
                                paramAlarm.setAlmTy(CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED);
                                paramAlarm.setUpdTi(dto.getUpd_ti());
                                paramAlarm.setTagSn(dto.getTag_sn());

                                InterfaceAlarmControlHistoryDTO alarmExceededInfo = databaseService
                                        .getAlarmExceeded(paramAlarm);
                                if (alarmExceededInfo != null) { //임계치 제어에 해당
                                	alarmExceededFlag = true;
                                    if (bFirst == true) {
                                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                        rnti = dto.getRnti();
                                        popupMap.put("alarm_id", alarmExceededInfo.getAlm_id());
                                        popupMap.put("message", alarmExceededInfo.getDp_nm());
                                        popupMap.put("url", alarmExceededInfo.getUrl());
                                        popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(popupMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                        bFirst = false;
                                    }
                                    if (!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                        updateDto.setProcessStep(processStep);
                                        databaseService.modAiCoagulantControlKafkaFlag(updateDto);
                                    }
                                } else {
                                    if (nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO) {

                                        // 3. if operation_mode==1 (semi_auto)
                                        // 3-1. send control value to kafka ai_popup
                                        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(
                                                CommonValue.ALARM_CODE_COAGULANT_AI_CONTROL, processStep);
                                        if (alarmInfo != null) {
                                            // KAFKA topic is called only once.
                                            if (bFirst == true) {
                                                LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                                rnti = dto.getRnti();
                                                popupMap.put("alarm_id", alarmInfo.getAlm_id());
                                                popupMap.put("message", alarmInfo.getDp_nm());
                                                popupMap.put("url", alarmInfo.getUrl());
                                                popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                                strBody = objectMapper.writeValueAsString(popupMap);
                                                kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                                bFirst = false;
                                            }

                                            if (!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                                // 3-2. update kafka_flag=1 (kafka_popup)
                                                AiProcessControlDTO updateDto = dto;
                                                updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                                updateDto.setProcessStep(processStep);
                                                databaseService.modAiCoagulantControlKafkaFlag(updateDto);
                                            }
                                        } else {
                                            log.error("Does not exist alarmInfo:[{}]",
                                                    CommonValue.ALARM_CODE_COAGULANT_AI_CONTROL);
                                        }
                                    } else if (nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                                        // 4. if operation_mode==2 (full_auto)

                                        // 4-1. send control value to kafka ai_control
                                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                        controlMap.put("tag", dto.getTag_sn());
                                        controlMap.put("value",
                                                dto.getTag_val().equalsIgnoreCase(CommonValue.CONTROL_TRUE) ? true
                                                        : Float.parseFloat(dto.getTag_val()));
                                        controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(controlMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                        // 4-2. update kafka_flag=3 (kafka_send)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_SEND);
                                        updateDto.setProcessStep(processStep);
                                        databaseService.modAiCoagulantControlKafkaFlag(updateDto);
                                    }
                                }
                            } // for문 끝
                            //자동모드이면서 && 임계치 알람이 아닌 경우( = AI제어 알람인 경우) -- 이력 업데이트.
                            if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO && !alarmExceededFlag) {
                                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_COAGULANT_AI_CONTROL, processStep);
                                Date currentDate = new Date();
                                
                                // insert alarm_notify & get almSeq
                                if(alarmInfo != null) {
                                	int almSeq = alarmService.alarmNotify(
                                			alarmInfo.getAlm_id(),
                                			alarmInfo.getDp_nm(),
                                			alarmInfo.getUrl(),
                                			simpleDateFormat.format(currentDate)
                                			);
                                	if(almSeq !=0) {
                                		//ctrHisList insert 
                                		for(AiProcessControlDTO ctr : aiCoagulantControlList) {
                                			InterfaceAlarmControlHistoryDTO his = new InterfaceAlarmControlHistoryDTO();
                                			his.setAlm_id(alarmInfo.getAlm_id());
                                			his.setSeq(almSeq);// alarmNotify에 넣을때 사용한 seq를 반환받아서 넣어야함.
                                			his.setAlmTy(alarmInfo.getAlm_ty());
                                			his.setCtrTi(currentDate); // 현재시간
                                			his.setCtrYn("A");
                                			his.setTagSn(ctr.getTag_sn());
                                			his.setUpdTi(ctr.getUpd_ti());
                                			databaseService.insertAlarmControlHistory(his);
                                		}
                                	}
                                } else {
                                    log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_COAGULANT_AI_CONTROL);
                                }
                            }
                        } catch (JsonProcessingException e) {
                            log.error("JsonProcessingException Occurred in Receiving Control Process");
                        } catch (NumberFormatException e) {
                            log.error("NumberException Occurred in Receiving Control Process");
                        }
                    }
                }
            }
        }
    }

    /**
     * 착수 CTR 조회
     */
    public void getReceivingControl() {
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // 1. get operation mode
            AiProcessInitDTO aiReceivingInit = databaseService.getAiReceivingInit(CommonValue.B_OPERATION_MODE,
                    processStep);
            log.debug("getAiReceivingInit, result:[{}]", aiReceivingInit != null ? 1 : 0);

            if (aiReceivingInit != null) {
                int nOperationMode = aiReceivingInit.getInit_val().intValue();
                // 수동 모드일 경우 전송하지 않음
                if (nOperationMode > CommonValue.OPERATION_MODE_MANUAL) {
                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRnti(runTime);
                    queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    queryDto.setProcessStep(processStep);

                    List<AiProcessControlDTO> aiReceivingControlList = databaseService
                            .getListAiReceivingControl(queryDto);

                    if (aiReceivingControlList.size() > 0) {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date rnti = new Date();
                        
                        // 자동모드 history insert시 TB_ALM_NTF 반환 변수
                        int almSeq = 0;
                        try {
                            for (AiProcessControlDTO dto : aiReceivingControlList) {
                                InterfaceAlarmControlHistoryDTO paramAlarm = new InterfaceAlarmControlHistoryDTO();
                                paramAlarm.setAlm_ntf_ti(dto.getRnti());
                                paramAlarm.setProcess(CommonValue.PROCESS_RECEIVING);
                                paramAlarm.setProcessStep(String.valueOf(processStep));
                                paramAlarm.setAlmTy(CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED);
                                paramAlarm.setUpdTi(dto.getUpd_ti());
                                paramAlarm.setTagSn(dto.getTag_sn());

                                InterfaceAlarmControlHistoryDTO alarmExceededInfo = databaseService
                                        .getAlarmExceeded(paramAlarm);
                                if (alarmExceededInfo != null) { // 임계치 제어에 해당
                                    if (bFirst == true) {
                                        rnti = dto.getRnti();
                                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                        popupMap.put("alarm_id", alarmExceededInfo.getAlm_id());
                                        popupMap.put("message", alarmExceededInfo.getDp_nm());
                                        popupMap.put("url", alarmExceededInfo.getUrl());
                                        popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(popupMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                        bFirst = false;
                                    }

                                    if (!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                        updateDto.setProcessStep(processStep);
                                        databaseService.modAiReceivingControlKafkaFlag(updateDto);
                                    }
                                } else {
                                    if (nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO) {

                                        // 3. if operation_mode==1 (semi_auto)
                                        // 3-1. send control value to kafka ai_popup
                                        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(
                                                CommonValue.ALARM_CODE_RECEIVING_AI_CONTROL, processStep);

                                        if (alarmInfo != null) {
                                            // KAFKA topic is called only once.
                                            if (bFirst == true) {
                                                LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                                rnti = dto.getRnti();
                                                popupMap.put("alarm_id", alarmInfo.getAlm_id());
                                                popupMap.put("message", alarmInfo.getDp_nm());
                                                popupMap.put("url", alarmInfo.getUrl());
                                                popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                                strBody = objectMapper.writeValueAsString(popupMap);
                                                kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                                bFirst = false;
                                            }
                                            if (!bFirst && dto.getRnti().compareTo(rnti) == 0) {

                                                // 3-2. update kafka_flag=1 (kafka_popup)
                                                AiProcessControlDTO updateDto = dto;
                                                updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                                updateDto.setProcessStep(processStep);
                                                databaseService.modAiReceivingControlKafkaFlag(updateDto);
                                            }
                                        } else {
                                            log.error("Does not exist alarmInfo:[{}]",
                                                    CommonValue.ALARM_CODE_RECEIVING_AI_CONTROL);
                                        }
                                    } else if (nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                                    	AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_RECEIVING_AI_CONTROL, processStep);
                                		Date currentDate = new Date();
                                		// rnti를 최초 1번만 세팅하고 이후 동일한 시간을 비교하기 위한 처리
                                		if(alarmInfo != null) {
                                			if(bFirst == true) {
                                				rnti = dto.getRnti();
                                				// rnti가 동일할 경우 TB_ALM_NTF 에서 반환되는 almSeq는 동일한 값으로 세팅 필요
                                				almSeq = alarmService.alarmNotify(
                                						alarmInfo.getAlm_id(),
                                						alarmInfo.getDp_nm(),
                                						alarmInfo.getUrl(),
                                						simpleDateFormat.format(currentDate)
                                						);
                                				bFirst = false;
                                			}
                                			
                                			// 동일한 시간일 경우에만 이번 스케줄에 수행. 시간이 다를 경우 다음 스케줄에 수행
                                			if(!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                				// 4-1. send control value to kafka ai_control
                                				LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                				controlMap.put("tag", dto.getTag_sn());
                                				if(dto.getTag_val().equalsIgnoreCase(CommonValue.CONTROL_TRUE) == true)
                                				{
                                					controlMap.put("value", true);
                                				}
                                				else
                                				{
                                					controlMap.put("value", Float.parseFloat(dto.getTag_val()));
                                				}
                                				controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                				strBody = objectMapper.writeValueAsString(controlMap);
                                				kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                                				
                                				// 4-2. update kafka_flag=3 (kafka_send)
                                				AiProcessControlDTO updateDto = dto;
                                				updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_SEND);
                                				updateDto.setProcessStep(processStep);
                                				databaseService.modAiReceivingControlKafkaFlag(updateDto);
                                				
                                				// 4-3. insert TB_ALM_CTR_HIS 
                                				if(almSeq !=0) {
                                					//ctrHisList insert 
                                					InterfaceAlarmControlHistoryDTO his = new InterfaceAlarmControlHistoryDTO();
                                					his.setAlm_id(alarmInfo.getAlm_id());
                                					his.setSeq(almSeq); // alarmNotify에 넣을때 사용한 seq를 반환받아서 넣어야함.
                                					his.setAlmTy(alarmInfo.getAlm_ty());
                                					his.setCtrTi(currentDate); // 현재시간
                                					his.setCtrYn("A");
                                					his.setTagSn(dto.getTag_sn());
                                					his.setUpdTi(dto.getUpd_ti());
                                					databaseService.insertAlarmControlHistory(his);
                                				}
                                			}
                                		} else {
                            				log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_RECEIVING_AI_CONTROL);
                            			}
                                    }
                                }
                            } // for문 끝
                            //자동모드이면서 && 임계치 알람이 아닌 경우( = AI제어 알람인 경우) -- 이력 업데이트.
//                            if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO && !alarmExceededFlag) {
//                                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_RECEIVING_AI_CONTROL, processStep);
//                                Date currentDate = new Date();
//                                
//                                // insert alarm_notify & get almSeq
//                                int almSeq = alarmService.alarmNotify(
//                                		alarmInfo.getAlm_id(),
//                                		alarmInfo.getDp_nm(),
//                                        alarmInfo.getUrl(),
//                                        simpleDateFormat.format(currentDate)
//                                );
//                                if(almSeq !=0) {
//                                	//ctrHisList insert 
//                                	for(AiProcessControlDTO ctr : aiReceivingControlList) {
//                                		InterfaceAlarmControlHistoryDTO his = new InterfaceAlarmControlHistoryDTO();
//                                		his.setAlm_id(alarmInfo.getAlm_id());
//                                		his.setSeq(almSeq);// alarmNotify에 넣을때 사용한 seq를 반환받아서 넣어야함.
//                                		his.setAlmTy(alarmInfo.getAlm_ty());
//                                		his.setCtrTi(currentDate); // 현재시간
//                                		his.setCtrYn("A");
//                                		his.setTagSn(ctr.getTag_sn());
//                                		his.setUpdTi(ctr.getUpd_ti());
//                                		databaseService.insertAlarmControlHistory(his);
//                                	}
//                                }
//                            }
                        } catch (JsonProcessingException e) {
                            log.error("JsonProcessingException Occurred in Receiving Control Process");
                        } catch (NumberFormatException e) {
                            log.error("NumberException Occurred in Receiving Control Process");
                        }
                    }
                }
            }
        }
    }

    /**
     * 통합 운영 시스템 알람을 Kafka로 전송
     * 
     * @param token 토큰
     */
    @RequestMapping(value = "/internal/alarm", method = RequestMethod.GET)
    public void getAlarm(@RequestHeader("X-ACCESS-TOKEN") String token) {
        // Token Check
        if (propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getAlarm, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getAlarm() initialization alarmDate before one hour
        if (alarmDate == null) {
            alarmDate = new Date();
            alarmDate.setTime(alarmDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.info("[internal] getAlarm");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if (currentDate.getTime() - alarmDate.getTime() > CommonValue.THIRTY_SECOND) {
            alarmDate = new Date();

            // 1. get latest(1minute) control value(kafka_flag = 0)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -1);
            // almTy = 3 or almTy = 4
            Date alarmTime = calendar.getTime();
            for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
                getAllAlarm(CommonValue.PROCESS_RECEIVING, alarmTime, CommonValue.KAFKA_FLAG_INIT, processStep);
                getAllAlarm(CommonValue.PROCESS_COAGULANT, alarmTime, CommonValue.KAFKA_FLAG_INIT, processStep);
                getAllAlarm(CommonValue.PROCESS_MIXING, alarmTime, CommonValue.KAFKA_FLAG_INIT, processStep);
                getAllAlarm(CommonValue.PROCESS_SEDIMENTATION, alarmTime, CommonValue.KAFKA_FLAG_INIT, processStep);
                getAllAlarm(CommonValue.PROCESS_DISINFECTION, alarmTime, CommonValue.KAFKA_FLAG_INIT, processStep);
                if (processStep == 2) {
                    getAllAlarm(CommonValue.PROCESS_FILTER, alarmTime, CommonValue.KAFKA_FLAG_INIT, processStep);
                    getAllAlarm(CommonValue.PROCESS_DISINFECTION_POST, alarmTime, CommonValue.KAFKA_FLAG_INIT,
                            processStep);
                }
            }
        }
    }

    /**
     * 공정별 알람정보 목록조회
     * 
     * @param processType 공정타입
     * @param alarmTime   현재시간
     * @param kafkaFlag   카프카플래그
     * @param processStep 공정단계
     * @return List<AiProcessAlarmDTO> 공정 알람정보 목록
     */
    private List<AiProcessAlarmDTO> getAllAlarm(String processType, Date alarmTime, int kafkaFlag, int processStep) {
        List<AiProcessAlarmDTO> aiAlarmList = new ArrayList<AiProcessAlarmDTO>();

        switch (processType) {
            case CommonValue.PROCESS_RECEIVING: // 착수
                // 2. Receiving Process get ai_receiving_alarm
                aiAlarmList = databaseService.getAllAiReceivingAlarm(alarmTime, kafkaFlag, processStep);
                modifyAlarmKafkaFlag(aiAlarmList, processType);
                break;
            case CommonValue.PROCESS_COAGULANT: // 약품
                // 2. Coagulant Process get ai_coagulant_alarm
                aiAlarmList = databaseService.getAllAiCoagulantAlarm(alarmTime, kafkaFlag, processStep);
                modifyAlarmKafkaFlag(aiAlarmList, processType);
                break;
            case CommonValue.PROCESS_MIXING: // 혼화응집
                // 2. Mixing Process get ai_mixing_alarm
                aiAlarmList = databaseService.getAllAiMixingAlarm(alarmTime, kafkaFlag, processStep);
                modifyAlarmKafkaFlag(aiAlarmList, processType);
                break;
            case CommonValue.PROCESS_SEDIMENTATION: // 침전
                // 2. Sedimentation Process get ai_sedimentation_alarm
                aiAlarmList = databaseService.getAllAiSedimentationAlarm(alarmTime, kafkaFlag, processStep);
                modifyAlarmKafkaFlag(aiAlarmList, processType);
                break;
            case CommonValue.PROCESS_FILTER: // 여과
                // 2. Filter Process get ai_filter_alarm
                aiAlarmList = databaseService.getAllAiFilterAlarm(alarmTime, kafkaFlag, processStep);
                modifyAlarmKafkaFlag(aiAlarmList, processType);
                break;
            case CommonValue.PROCESS_DISINFECTION: // 전차염
                // 2. Disinfection Process get ai_disinfection_alarm
                aiAlarmList = databaseService.getAllAiDisinfectionAlarm(alarmTime, kafkaFlag, processStep,
                        CommonValue.DISINFECTION_PRE_STEP);
                modifyAlarmKafkaFlag(aiAlarmList, processType);
                break;
            case CommonValue.PROCESS_DISINFECTION_POST: // 후차염
                aiAlarmList = databaseService.getAllAiDisinfectionAlarm(alarmTime, kafkaFlag, processStep,
                        CommonValue.DISINFECTION_POST_STEP);
                modifyAlarmKafkaFlag(aiAlarmList, processType);
                break;
        }
        return aiAlarmList;
    }

    /**
     * 카프카 플래그 수정
     * 
     * @param alarmList 알람정보 목록
     * @param process   공정
     */
    private void modifyAlarmKafkaFlag(List<AiProcessAlarmDTO> alarmList, String process) {
        if (alarmList.size() > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strBody;
            ObjectMapper objectMapper = new ObjectMapper();

            for (AiProcessAlarmDTO dto : alarmList) {
                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(dto.getAlm_id());
                if (alarmInfo != null) {
                    if (alarmInfo.getAlm_ty() == 3) {
                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                        popupMap.put("alarm_id", alarmInfo.getAlm_id());
                        popupMap.put("message", alarmInfo.getDp_nm());
                        popupMap.put("url", alarmInfo.getUrl());
                        popupMap.put("time", simpleDateFormat.format(dto.getAlm_ti()));

                        // 3. Send Kafka ai_popup
                        try {
                            strBody = objectMapper.writeValueAsString(popupMap);
                            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);
                        } catch (JsonProcessingException e) {
                            log.error(
                                    "JsonProcessingException Occurred in PROCESS_CODE :" + process + " Alarm Process");
                        }
                    }

                    // 4. Update kafka_flag=1
                    AiProcessAlarmDTO updateDto = dto;
                    updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                    if (CommonValue.PROCESS_RECEIVING.equals(process)) { // 착수
                        databaseService.modAiReceivingAlarmKafkaFlag(updateDto);
                    } else if (CommonValue.PROCESS_COAGULANT.equals(process)) { // 약품
                        databaseService.modAiCoagulantAlarmKafkaFlag(updateDto);
                    } else if (CommonValue.PROCESS_MIXING.equals(process)) { // 혼화응집
                        databaseService.modAiMixingAlarmKafkaFlag(updateDto);
                    } else if (CommonValue.PROCESS_SEDIMENTATION.equals(process)) { // 침전
                        databaseService.modAiSedimentationAlarmKafkaFlag(updateDto);
                    } else if (CommonValue.PROCESS_FILTER.equals(process)) { // 여과
                        databaseService.modAiFilterAlarmKafkaFlag(updateDto);
                    } else if (CommonValue.PROCESS_DISINFECTION.equals(process)
                            || updateDto.getProcessStep() == 2
                                    && CommonValue.PROCESS_DISINFECTION_POST.equals(process)) { // 소독
                        databaseService.modAiDisinfectionAlarmKafkaFlag(updateDto);
                    }
                }
            }
        }
    }

    /**
     * 데이터베이스 정리
     * 
     * @param token 토큰
     */
    @RequestMapping(value = "/internal/database", method = RequestMethod.GET)
    public void getDatabase(@RequestHeader("X-ACCESS-TOKEN") String token) {
        // Token Check
        if (propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("[Collector]getDatabase, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getDatabase() initialization databaseDate before one hour
        if (databaseDate == null) {
            databaseDate = new Date();
            databaseDate.setTime(databaseDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.debug("[internal] getDatabase");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if (currentDate.getTime() - databaseDate.getTime() > CommonValue.ONE_MINUTE) {
            databaseDate = new Date();

            // Database check
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -propertiesStorage.getStorage());
            Date deleteTime = calendar.getTime();

//            log.info("[Collector]Delete Login History:[{}]", databaseService.delLoginHistory(deleteTime));
            
            for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            	log.info("[Collector]Delete TB_AI_B_{}_RT:[{}]"
            			, processStep
            			, databaseService.delAiReceivingRealtimeValue(deleteTime, processStep));
            	log.info("[Collector]Delete TB_AI_B_{}_CTR:[{}]"
            			, processStep
            			, databaseService.delAiReceivingControl(deleteTime, processStep));
                log.info("[Collector]Delete TB_AI_C_{}_RT:[{}]"
                		, processStep
                		, databaseService.delAiCoagulantRealtimeValue(deleteTime, processStep));
                log.info("[Collector]Delete TB_AI_C_{}_CTR:[{}]"
                		, processStep
                        , databaseService.delAiCoagulantControl(deleteTime, processStep));
                log.info("[Collector]Delete TB_AI_D_{}_RT:[{}]"
                		, processStep
                		, databaseService.delAiMixingRealtimeValue(deleteTime, processStep));
                log.info("[Collector]Delete TB_AI_D_{}_CTR:[{}]"
                		, processStep
                		, databaseService.delAiMixingControl(deleteTime, processStep));
                log.info("[Collector]Delete TB_AI_E_{}_RT:[{}]"
                		, processStep
                		, databaseService.delAiSedimentationRealtimeValue(deleteTime, processStep));
                log.info("[Collector]Delete TB_AI_E_{}_CTR:[{}]"
                		, processStep
                		, databaseService.delAiSedimentationControl(deleteTime, processStep));
                log.info("[Collector]Delete TB_AI_PRE_G_{}_RT:[{}]"
                		, processStep
                		, databaseService.delAiDisinfectionRealtimeValue(deleteTime, processStep, CommonValue.DISINFECTION_PRE_STEP)); // 전차염
                log.info("[Collector]Delete TB_AI_PRE_G_{}_CTR:[{}]"
                		, processStep
                		, databaseService.delAiPreDisinfectionControl(deleteTime, processStep));
            }
            
            // 여과와 후차염은 2단계만 존재
            log.info("[Collector]Delete TB_AI_F_{}_RT:[{}]"
            		, CommonValue.PROCESS_STEP_ARRAY[1]
            		, databaseService.delAiFilterRealtimeValue(deleteTime));
            log.info("[Collector]Delete TB_AI_F_{}_CTR:[{}]"
            		, CommonValue.PROCESS_STEP_ARRAY[1]
            		, databaseService.delAiFilterControl(deleteTime, CommonValue.PROCESS_STEP_ARRAY[1]));
            log.info("[Collector]Delete TB_AI_F_{}_ALM:[{}]"
            		, CommonValue.PROCESS_STEP_ARRAY[1]
            		, databaseService.delAiFilterAlarm(deleteTime));
            log.info("[Collector]Delete TB_AI_POST_G_{}_RT:[{}]"
            		, CommonValue.PROCESS_STEP_ARRAY[1]
            		, databaseService.delAiDisinfectionRealtimeValue(deleteTime, CommonValue.PROCESS_STEP_ARRAY[1], CommonValue.DISINFECTION_POST_STEP));
            log.info("[Collector]Delete TB_AI_POST_G_{}_CTR:[{}]"
            		, CommonValue.PROCESS_STEP_ARRAY[1]
            		, databaseService.delAiPostDisinfectionControl(deleteTime, CommonValue.PROCESS_STEP_ARRAY[1]));
        }
    }

    /**
     * 알고리즘 상태 확인
     * 
     * @param token
     */
    @RequestMapping(value = "/internal/algorithm", method = RequestMethod.GET)
    public void getAlgorithm(@RequestHeader("X-ACCESS-TOKEN") String token) {
        // Token Check
        if (propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getDatabase, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getAlgorithm() initialization algorithmDate before one hour
        if (algorithmDate == null) {
            algorithmDate = new Date();
            algorithmDate.setTime(algorithmDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.debug("[internal] getAlgorithm");

        // Check one minute after previous transfer
        Date currentDate = new Date();
        if (currentDate.getTime() - algorithmDate.getTime() > CommonValue.ONE_MINUTE) {
            algorithmDate = new Date();

            // get algorithm health check URL
            List<String> strUri = propertiesAlgorithmCheck.getAlgorithmHealth();

            StringBuffer stringBuffer = new StringBuffer();
            ObjectMapper objectMapper = new ObjectMapper();
            BufferedReader bufferedReader = null;
            InputStreamReader inputStreamReader = null;
            AlgorithmHealthStatus algorithmHealthStatus = new AlgorithmHealthStatus();

            try {
                for (String uri : strUri) {
                    HttpGet httpGet = new HttpGet(uri);
                    httpGet.setConfig(requestConfig);

                    HttpResponse response = httpSend.send(httpGet);
                    if (response == null) {
                        continue;
                    }

                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while ((strLine = bufferedReader.readLine()) != null) {
                            stringBuffer.append(strLine);
                        }

                        // Algorithm Healthcheck Response에 대한 Parsing
                        ArrayList<SupervisorStateInfoDTO> algorithmList = objectMapper.readValue(
                                stringBuffer.toString(), new TypeReference<ArrayList<SupervisorStateInfoDTO>>() {
                                });
                        for (SupervisorStateInfoDTO processState : algorithmList) {
                            if (processState.getName().equalsIgnoreCase(CommonValue.PROCESS_RECEIVING_NAME) == true) {
                                // 착수 공정 알고리즘 프로세스 상태 저장
                                if (algorithmHealthStatus.getReceiving() == CommonValue.PROCESS_STATE_RUNNING) {
                                    continue;
                                }
                                algorithmHealthStatus.setReceiving(processState.getState());
                            } else if (processState.getName()
                                    .equalsIgnoreCase(CommonValue.PROCESS_COAGULANT_NAME) == true) {
                                // 약품 공장 알고리즘 프로세스 상태 저장
                                if (algorithmHealthStatus.getCoagulant() == CommonValue.PROCESS_STATE_RUNNING) {
                                    continue;
                                }
                                algorithmHealthStatus.setCoagulant(processState.getState());
                            } else if (processState.getName()
                                    .equalsIgnoreCase(CommonValue.PROCESS_MIXING_NAME) == true) {
                                // 혼화응집 공정 알고리즘 프로세스 상태 저장
                                if (algorithmHealthStatus.getMixing() == CommonValue.PROCESS_STATE_RUNNING) {
                                    continue;
                                }
                                algorithmHealthStatus.setMixing(processState.getState());
                            } else if (processState.getName()
                                    .equalsIgnoreCase(CommonValue.PROCESS_SEDIMENTATION_NAME) == true) {
                                // 침전 공정 알고리즘 프로세스 상태 저장
                                if (algorithmHealthStatus.getSedimentation() == CommonValue.PROCESS_STATE_RUNNING) {
                                    continue;
                                }
                                algorithmHealthStatus.setSedimentation(processState.getState());
                            } else if (processState.getName()
                                    .equalsIgnoreCase(CommonValue.PROCESS_FILTER_NAME) == true) {
                                // 여과 공정 알고리즘 프로세스 상태 저장
                                if (algorithmHealthStatus.getFilter() == CommonValue.PROCESS_STATE_RUNNING) {
                                    continue;
                                }
                                algorithmHealthStatus.setFilter(processState.getState());
                            } else if (processState.getName().equalsIgnoreCase(CommonValue.PROCESS_GAC_NAME) == true) {
                                // GAC 여과 공정 알고리즘 프로세스 상태 저장
                                if (algorithmHealthStatus.getGac() == CommonValue.PROCESS_STATE_RUNNING) {
                                    continue;
                                }
                                algorithmHealthStatus.setGac(processState.getState());
                            } else if (processState.getName()
                                    .equalsIgnoreCase(CommonValue.PROCESS_DISINFECTION_NAME) == true) {
                                // 소독 공정 알고리즘 프로세스 상태 저장
                                if (algorithmHealthStatus.getDisinfection() == CommonValue.PROCESS_STATE_RUNNING) {
                                    continue;
                                }
                                algorithmHealthStatus.setDisinfection(processState.getState());
                            } else if (processState.getName()
                                    .equalsIgnoreCase(CommonValue.PROCESS_OZONE_NAME) == true) {
                                // 오존 공정 알고리즘 프로세스 상태 저장
                                if (algorithmHealthStatus.getOzone() == CommonValue.PROCESS_STATE_RUNNING) {
                                    continue;
                                }
                                algorithmHealthStatus.setOzone(processState.getState());
                            }
                        }
                    }
                }

                // Receiving Algorithm Health Check & Send Alarm
                if (algorithmHealthStatus.getReceiving() != CommonValue.PROCESS_STATE_RUNNING) {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlm_ti(new Date());
                    aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlm_id(CommonValue.ALARM_RECEIVING_AI_MODULE_ERROR);
                    log.info("insert receiving algorithm module error alarm:[{}]",
                            databaseService.addAiReceivingAlarm(aiProcessAlarm));
                }

                // Coagulant Algorithm Health Check & Send Alarm
                if (algorithmHealthStatus.getCoagulant() != CommonValue.PROCESS_STATE_RUNNING) {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlm_ti(new Date());
                    aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlm_id(CommonValue.ALARM_COAGULANT_AI_MODULE_ERROR);
                    log.info("insert coagulant algorithm module error alarm:[{}]",
                            databaseService.addAiCoagulantAlarm(aiProcessAlarm));
                }

                // Mixing Algorithm Health Check & Send Alarm
                if (algorithmHealthStatus.getMixing() != CommonValue.PROCESS_STATE_RUNNING) {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlm_ti(new Date());
                    aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlm_id(CommonValue.ALARM_MIXING_AI_MODULE_ERROR);
                    log.info("insert mixing algorithm module error alarm:[{}]",
                            databaseService.addAiMixingAlarm(aiProcessAlarm));
                }

                // Sedimentation Algorithm Health Check & Send Alarm
                if (algorithmHealthStatus.getSedimentation() != CommonValue.PROCESS_STATE_RUNNING) {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlm_ti(new Date());
                    aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlm_id(CommonValue.ALARM_SEDIMENTATION_AI_MODULE_ERROR);
                    log.info("insert sedimentation algorithm module error alarm:[{}]",
                            databaseService.addAiSedimentationAlarm(aiProcessAlarm));
                }

                // Filter Algorithm Health Check & Send Alarm
                if (algorithmHealthStatus.getFilter() != CommonValue.PROCESS_STATE_RUNNING) {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlm_ti(new Date());
                    aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlm_id(CommonValue.ALARM_FILTER_AI_MODULE_ERROR);
                    log.info("insert filter algorithm module error alarm:[{}]",
                            databaseService.addAiFilterAlarm(aiProcessAlarm));
                }

                // Disinfection Algorithm Health Check & Send Alarm
                if (algorithmHealthStatus.getDisinfection() != CommonValue.PROCESS_STATE_RUNNING) {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlm_ti(new Date());
                    aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlm_id(CommonValue.ALARM_DISINFECTION_AI_MODULE_ERROR);
                    log.info("insert disinfection algorithm module error alarm:[{}]",
                            databaseService.addAiDisinfectionAlarm(aiProcessAlarm));
                }
            } catch (IOException e) {
                log.error("Invalid Body or BufferedReader...");
            } finally {
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (IOException e) {
                        log.error("inputStreamReader Close Exception occurred");
                    }
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        log.error("bufferedReader Close Exception occurred");
                    }
                }
            }
        }
    }

    /**
     * 데이터베이스 정리
     * 
     * @param token 토큰
     */
    @RequestMapping(value = "/internal/manageRtTable", method = RequestMethod.GET)
    public void manageRtTable(@RequestHeader("X-ACCESS-TOKEN") String token) {
        // Token Check
        if (propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("[Collector]getDatabase, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        log.info("[Collector]Check Database Partition...Thread[{}]", Thread.currentThread().getName());

        // Set default calendar(tomorrow)
        Calendar calendarAdd = Calendar.getInstance();
        calendarAdd.set(Calendar.MINUTE, 0);
        calendarAdd.set(Calendar.SECOND, 0);
        calendarAdd.set(Calendar.HOUR_OF_DAY, 0);
        calendarAdd.add(Calendar.HOUR_OF_DAY, 24);

        // Set partition name
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        List<String> strAddPartitionNameList = new ArrayList<>();
        strAddPartitionNameList.add("p_" + partitionNameFormat.format(calendarAdd.getTime()));

        List<String> procCdList = databaseService.selectProcCd();
        for (String procCd : procCdList) {
            try {
                databaseService.addProcessRealtimePartition(procCd, strAddPartitionNameList);
                log.info("[Collector]Success Add Table[TB_{}_RT]...Partition Name:{}", procCd,
                        strAddPartitionNameList.toString());
            } catch (DataAccessException e) {
                log.error("[Collector]Failed Add Table[TB_{}_RT]...Partition Name:{}", procCd,
                        strAddPartitionNameList.toString());
                log.error(e.toString());
            }
        }

        // Delete Realtime table partition(7 days)
        Calendar calendarDel = Calendar.getInstance();
        calendarDel.set(Calendar.MINUTE, 0);
        calendarDel.set(Calendar.SECOND, 0);
        calendarDel.set(Calendar.HOUR_OF_DAY, 0);
        calendarDel.add(Calendar.DAY_OF_MONTH, -7);
        String strDelStartPartitionName = "p_" + partitionNameFormat.format(calendarDel.getTime());

        for (String procCd : procCdList) {
            List<String> dropPartitionList = new ArrayList<>();
            try {
                // Get drop partition list
                dropPartitionList = databaseService.getDropPartitionList(procCd, strDelStartPartitionName);
                databaseService.delProcessRealtimePartition(procCd, dropPartitionList);
                log.info("[Collector]Success Del Table[TB_{}_RT]... Partition Name:[{}]", procCd,
                        dropPartitionList.toString());
            } catch (DataAccessException e) {
                log.info("[Collector]Failed Del Table[TB_{}_RT]... Partition Name:[{}]", procCd,
                        dropPartitionList.toString());
                log.error(e.toString());
            }
        }
    }

    /**
     * 각 서비스의 컨테이너 연결 이상 시 서비스 통신 연결 알람(팝업창) 발생
     * @param token
     */
    @RequestMapping(value = "/internal/serviceStatus", method = RequestMethod.GET)
    public void getServiceStatus(@RequestHeader("X-ACCESS-TOKEN") String token, @RequestHeader("SERVER") String server, @RequestHeader("SERVICE") String service) {
    	// Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getRealTime, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        String strBody;
        ObjectMapper objectMapper = new ObjectMapper();

        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
        String alarmCdNm = server + "_" + service + "_connection_error";
        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(alarmCdNm, 0);
        if (alarmInfo != null) {
        	popupMap.put("alarm_id", alarmInfo.getAlm_id());
        	popupMap.put("message", alarmInfo.getDp_nm());
        	popupMap.put("url", alarmInfo.getUrl());
        	popupMap.put("time", today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        	try {
        		strBody = objectMapper.writeValueAsString(popupMap);
        		kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);
        		log.error("[Collector] " + server + "-" + service + " is dead");
        	} catch (JsonProcessingException e) {
        		log.error("JsonProcessingException");
        	}
        } else {
			log.error("Does not exist alarmInfo:[{}]", alarmCdNm);
		}
    }
}
