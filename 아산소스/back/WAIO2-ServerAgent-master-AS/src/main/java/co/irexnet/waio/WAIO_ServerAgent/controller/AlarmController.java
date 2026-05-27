package co.irexnet.waio.WAIO_ServerAgent.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessAlarmDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessControlDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmNotifyDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAlarmControlDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAlarmControlHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.kafka.KafkaProducer;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.AlarmInfoList;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableSwagger2
@Slf4j
public class AlarmController {
    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    AlarmInfoList alarmInfoList;

    @Autowired
    KafkaProducer kafkaProducer;

    /**
     * 최근 알람 이력 조회
     * 
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/alarm/notify", method = RequestMethod.GET)
    public ResponseEntity<String> getNotifyAlarm() {
        log.debug("Recv getNotifyAlarm");

        // 최근 1분간의 알람을 조회하기 위한 Date 정의
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        Date startTime = calendar.getTime();
        // 최근 1분 알람 조회
        List<AlarmNotifyDTO> alarmNotifyList = databaseService.getAlarmNotify(startTime);
        log.debug("getAlarmNotify, result:[{}]", alarmNotifyList.size());
        if (alarmNotifyList.size() > 0) {
            // Make Response Body
            List<LinkedHashMap<String, Object>> resultArray = new ArrayList<>();
            for (AlarmNotifyDTO alarmNotify : alarmNotifyList) {
                // 최근 1분간 발보된 알람 중 타 시스템 발보 및 반자동 제어를 위한 알람 추출
                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(alarmNotify.getAlm_id());
                
                if (alarmInfo == null) {
                    continue;
                } else if(!alarmInfo.getAlm_dp_yn().equals("Y")) {
                	continue;
                } else if (alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_OFF
                        || alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_THRESHOLD) {
                    if (alarmInfo.getCd_nm().endsWith(CommonValue.ALARM_CODE_CONNECTION_ERROR)) {
                        // add resultArray
                        LinkedHashMap<String, Object> singleBody = new LinkedHashMap<>();
                        singleBody.put("seq", alarmNotify.getSeq());
                        singleBody.put("alm_id", alarmNotify.getAlm_id());
                        singleBody.put("alm_ty", alarmInfo.getAlm_ty());
                        singleBody.put("message", alarmInfo.getDp_nm());
                        singleBody.put("url", alarmInfo.getUrl());
                        singleBody.put("alm_ntf_ti", alarmNotify.getAlm_ntf_ti());

                        resultArray.add(singleBody);
                    }
                    // update alarm_notify ack_state
                    // databaseService.modAlarmNotifyAckState(alarmNotify.getAlarm_notify_index(),
                    // true);
                } else if (alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_ANOTHER_SYSTEM) {
                	
                	String almId = String.valueOf(alarmInfo.getAlm_id());
                    if(almId.charAt(0) == '1' && almId.charAt(1) == '3') { // ' 자율운영 - AI 알람 '의 경우
                    	String process = alarmInfo.getProcess();
                    	if (CommonValue.PROCESS_DISINFECTION.equals(process) && alarmInfo.getCd_nm().contains("post")) {
                    		process = CommonValue.PROCESS_DISINFECTION_POST;
                    	}
                    	
                    	List<AiProcessAlarmDTO> aiAlarmList = getProcessAlarmList(process, alarmNotify.getAlm_ntf_ti(),
                    			CommonValue.KAFKA_FLAG_POPUP, alarmInfo.getProcessStep());
                    	//조회된 aiAlarmList가 없다면 팝업을 보내지않음.
                        if(aiAlarmList.size() == 0) {
                            continue;
                        }
                        
                        //조회된 aiAlarmList 중  almId, almTi이 해당하는 알람으로 필터링 후 재확인.
                    	AiProcessAlarmDTO targetAlarm;
                    	targetAlarm = (AiProcessAlarmDTO) aiAlarmList
									 .stream()
									 .filter(target -> alarmNotify.getAlm_ntf_ti().compareTo(target.getAlm_ti()) == 0 
									 && alarmNotify.getAlm_id() == target.getAlm_id())
									 .findFirst()
									 .orElse(null);
                    	
                    	//조회된 알람이 없다면 팝업을 보내지않음.
                        if(targetAlarm == null) {
                            continue;
                        }
                    }
                    
                    LinkedHashMap<String, Object> singleBody = new LinkedHashMap<>();
                    singleBody.put("seq", alarmNotify.getSeq());
                    singleBody.put("alm_id", alarmNotify.getAlm_id());
                    singleBody.put("alm_ty", alarmInfo.getAlm_ty());
                    singleBody.put("message", alarmInfo.getDp_nm());
                    singleBody.put("url", alarmInfo.getUrl());
                    singleBody.put("alm_ntf_ti", alarmNotify.getAlm_ntf_ti());

                    resultArray.add(singleBody);
                    
                } else if (alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_SEMI_AUTO) {
                    int nOperationMode = 0; // 반자동 제어 모드임을 확인하는 변수
                    AiProcessInitDTO aiInit = new AiProcessInitDTO();

                    aiInit = getAiInit(alarmInfo);
                    if (aiInit == null) {
                        continue;
                    }
                    // get operation mode
                    nOperationMode = aiInit.getInit_val().intValue();

                    // 현재 운전모드가 반자동 제어 모드가 아니라면 resultArray에 포함시키지 않음
                    if (nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO) {
                        continue;
                    }
                    List<AiProcessControlDTO> aiControlList = getAlarmCtrList(alarmInfo, alarmNotify.getAlm_ntf_ti());
                    List<AiProcessControlDTO> aiControlList2 = new ArrayList<AiProcessControlDTO>();
                    aiControlList2 = aiControlList.stream()
                            .filter(target -> alarmNotify.getAlm_ntf_ti().compareTo(target.getRnti()) == 0)
                            .collect(Collectors.toList());

                    // aiControlList에 해당하는 DP데이터 추가
                    aiControlList2.forEach(
                            aiControl -> aiControl.setTag_dp(databaseService.selectTagDp(aiControl.getTag_sn())));

                    if (aiControlList2.size() > 0) {
                        // add resultArray
                        LinkedHashMap<String, Object> singleBody = new LinkedHashMap<>();
                        singleBody.put("seq", alarmNotify.getSeq());
                        singleBody.put("alm_id", alarmNotify.getAlm_id());
                        singleBody.put("alm_ty", alarmInfo.getAlm_ty());
                        singleBody.put("message", alarmInfo.getDp_nm());
                        singleBody.put("url", alarmInfo.getUrl());
                        singleBody.put("alm_ntf_ti", alarmNotify.getAlm_ntf_ti());
                        singleBody.put("ctrList", aiControlList2);
                        resultArray.add(singleBody);
                    }

                } else if (alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED) {
                    int nOperationMode = 0;
                    AiProcessInitDTO aiInit = new AiProcessInitDTO();

                    aiInit = getAiInit(alarmInfo);
                    if (aiInit == null) {
                        continue;
                    }
                    // get operation mode
                    nOperationMode = aiInit.getInit_val().intValue();

                    // 현재 운전모드가 수동 제어 모드라면 resultArray에 포함시키지 않음
                    if (nOperationMode == CommonValue.OPERATION_MODE_MANUAL) {
                        continue;
                    }
                    List<AiProcessControlDTO> aiControlList = getAlarmCtrList(alarmInfo, alarmNotify.getAlm_ntf_ti());
                    List<AiProcessControlDTO> aiControlList2 = new ArrayList<AiProcessControlDTO>();
                    aiControlList2 = aiControlList.stream()
                            .filter(target -> alarmNotify.getAlm_ntf_ti().compareTo(target.getRnti()) == 0)
                            .collect(Collectors.toList());

                    // aiControlList에 해당하는 DP데이터 추가
                    aiControlList2.forEach(
                            aiControl -> aiControl.setTag_dp(databaseService.selectTagDp(aiControl.getTag_sn())));

                    if (aiControlList2.size() > 0) {
                        // add resultArray
                        LinkedHashMap<String, Object> singleBody = new LinkedHashMap<>();
                        singleBody.put("seq", alarmNotify.getSeq());
                        singleBody.put("alm_id", alarmNotify.getAlm_id());
                        singleBody.put("alm_ty", alarmInfo.getAlm_ty());
                        singleBody.put("message", alarmInfo.getDp_nm());
                        singleBody.put("url", alarmInfo.getUrl());
                        singleBody.put("alm_ntf_ti", alarmNotify.getAlm_ntf_ti());
                        singleBody.put("ctrList", aiControlList2);

                        resultArray.add(singleBody);
                    }
                }
            }
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("alarm", resultArray);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody;
            try {
                strBody = objectMapper.writeValueAsString(responseBody);
            } catch (JsonProcessingException e) {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"Empty alarm_notify\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.OK);
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
    public List<AiProcessAlarmDTO> getProcessAlarmList(String processType, Date alarmTime, int kafkaFlag,
            int processStep) {
        List<AiProcessAlarmDTO> aiAlarmList = new ArrayList<AiProcessAlarmDTO>();

        switch (processType) {
            case CommonValue.PROCESS_RECEIVING: // 착수
                // 2. Receiving Process get ai_receiving_alarm
                aiAlarmList = databaseService.getAllAiReceivingAlarm(alarmTime, kafkaFlag, processStep);
                break;
            case CommonValue.PROCESS_COAGULANT: // 약품
                // 2. Coagulant Process get ai_coagulant_alarm
                aiAlarmList = databaseService.getAllAiCoagulantAlarm(alarmTime, kafkaFlag, processStep);
                break;
            case CommonValue.PROCESS_MIXING: // 혼화응집
                // 2. Mixing Process get ai_mixing_alarm
                aiAlarmList = databaseService.getAllAiMixingAlarm(alarmTime, kafkaFlag, processStep);
                break;
            case CommonValue.PROCESS_SEDIMENTATION: // 침전
                // 2. Sedimentation Process get ai_sedimentation_alarm
                aiAlarmList = databaseService.getAllAiSedimentationAlarm(alarmTime, kafkaFlag, processStep);
                break;
            case CommonValue.PROCESS_FILTER: // 여과
                // 2. Filter Process get ai_filter_alarm
                aiAlarmList = databaseService.getAllAiFilterAlarm(alarmTime, kafkaFlag, processStep);
                break;
            case CommonValue.PROCESS_DISINFECTION: // 전차염
                // 2. Disinfection Process get ai_disinfection_alarm
                aiAlarmList = databaseService.getAllAiDisinfectionAlarm(alarmTime, kafkaFlag, processStep,
                        CommonValue.DISINFECTION_PRE_STEP);
                break;
            case CommonValue.PROCESS_DISINFECTION_POST: // 후차염
                aiAlarmList = databaseService.getAllAiDisinfectionAlarm(alarmTime, kafkaFlag, processStep,
                        CommonValue.DISINFECTION_POST_STEP);
                break;
        }
        return aiAlarmList;
    }

    /**
     * 반자동 제어 팝업에서 제어버튼을 눌렀을 때 SCADA로 제어메시지를 전송
     * 
     * @param alarmControl Front-end 팝업을 통한 제어 명령을 저장하기 위한 DTO
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/alarm/control", method = RequestMethod.PUT)
    public ResponseEntity<String> putControlAlarm(@RequestBody InterfaceAlarmControlDTO alarmControl) {
        log.info("putControlAlarm, alm_id:[{}]", alarmControl.getAlm_id());
        String strErrorBody = "";

        // 등록된 알람ID인지 검사
        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(alarmControl.getAlm_id());
        log.info("getAlarmInfoFromAlarmId, result:[{}]", alarmInfo != null ? 1 : 0);
        if (alarmInfo == null) {
            log.error("Does not exist Alarm:[{}]", alarmControl.getAlm_id());
            strErrorBody = "{\"reason\":\"Invalid alm_id\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        if (alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_ANOTHER_SYSTEM) { //공정별 알람
        	String almId = String.valueOf(alarmInfo.getAlm_id());
    		if(almId.charAt(0) != '1' || almId.charAt(1) != '3') { // ' 자율운영 - AI 알람 코드 '의 경우만 카프카 플래그 업데이트 진행
    	        strErrorBody = "{\"reason\":\"Not AI alarm_info\"}";
    	        return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
    		}
            // 4. Update kafka_flag=1
            AiProcessAlarmDTO updateDto = new AiProcessAlarmDTO();
            updateDto.setKfk_flg(alarmControl.getKfk_flg());
            updateDto.setProcessStep(alarmInfo.getProcessStep());
            updateDto.setAlm_id(alarmControl.getAlm_id());
            updateDto.setAlm_ti(alarmControl.getAlm_ntf_ti());

            String process = alarmInfo.getProcess();
            if (CommonValue.PROCESS_DISINFECTION.equals(process) && alarmInfo.getCd_nm().contains("post")) {
                process = CommonValue.PROCESS_DISINFECTION_POST;
            }

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
                    || updateDto.getProcessStep() == 2 && CommonValue.PROCESS_DISINFECTION_POST.equals(process)) { // 소독
                updateDto.setDisinfectionIndex(
                        CommonValue.PROCESS_DISINFECTION_POST.equals(process) ? CommonValue.DISINFECTION_POST_STEP
                                : CommonValue.DISINFECTION_PRE_STEP);
                databaseService.modAiDisinfectionAlarmKafkaFlag(updateDto);
            }
        } else if(alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_SEMI_AUTO || alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED){//반자동,임계치 알람
            List<AiProcessControlDTO> aiControlList = new ArrayList<AiProcessControlDTO>();
            aiControlList = getAlarmCtrList(alarmInfo, alarmControl.getAlm_ntf_ti());
            List<AiProcessControlDTO> aiControlList2 = new ArrayList<AiProcessControlDTO>();
            aiControlList2 = aiControlList.stream()
                    .filter(target -> alarmControl.getAlm_ntf_ti().compareTo(target.getRnti()) == 0)
                    .collect(Collectors.toList());
            if (aiControlList2.size() > 0) {
                for (AiProcessControlDTO dto : aiControlList2) {
                    dto.setKfk_flg(alarmControl.getKfk_flg());
                    if (alarmControl.getKfk_flg() == CommonValue.KAFKA_FLAG_SEND) {
                        String strBody;
                        ObjectMapper objectMapper = new ObjectMapper();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if (dto.getTag_sn() != null) {
                            // send control value to kafka ai_control
                            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            // Pulse / Value 값 구분하여 설정
                            controlMap.put("value",
                                    dto.getTag_val().equalsIgnoreCase(CommonValue.CONTROL_TRUE) ? true
                                            : Float.parseFloat(dto.getTag_val()));
                            controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                            try {
								strBody = objectMapper.writeValueAsString(controlMap);
							} catch (JsonProcessingException e) {
								strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
							}
                            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                            log.info("alarm kfkf send code 3");
                            /* 수정 */
                            strErrorBody = getAiControl(strErrorBody, "U", alarmInfo, dto);

                            log.info("alarm kfkf send code 3 update");

                        }
                    } else if (alarmControl.getKfk_flg() == CommonValue.KAFKA_FLAG_CANCEL) {
                        log.info("alarmcontrol.cancel" + alarmControl.getKfk_flg());
                        /* 수정 */
                        strErrorBody = getAiControl(strErrorBody, "U", alarmInfo, dto);

                    } else {
                        strErrorBody = "{\"reason\":\"error control\"}";
                    }
                }
            } else {
            	if (alarmControl.getNo_action_flg() != null) {
            		//무반응 알람 - CONFLICT 처리 안하도록
                	strErrorBody = "{\"reason\":\"already controlled\"}";
        	        return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            	} else {
            		// 이미 제어가 완료되어 kfk_flg가 변경된 경우
            		strErrorBody = "{\"reason\":\"already controlled\"}";
            		return new ResponseEntity<>(strErrorBody, HttpStatus.CONFLICT);
            	}
            }
        }
        return new ResponseEntity<>(strErrorBody, "".equals(strErrorBody) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    /**
     * 공정별 알람 KAFKA_FLAG 수정
     * 
     * @param strErrorBody 메시지
     * @param targetType   U: 수정
     * @param aiControl    등록된 알람 정보 DTO
     * @param queryDto     공정 별 제어 DTO
     * @return String 메시지
     */
    private String getAiControl(String strErrorBody, String targetType, AlarmInfoDTO aiControl,
            AiProcessControlDTO queryDto) {
        if ("U".equals(targetType)) {
            AiProcessControlDTO updateDto = queryDto;
            updateDto.setProcessStep(aiControl.getProcessStep());
            switch (aiControl.getProcess()) {
                case CommonValue.PROCESS_RECEIVING: // 착수
                    databaseService.modAiReceivingControlKafkaFlag(updateDto);
                    break;
                case CommonValue.PROCESS_COAGULANT: // 약품
                    databaseService.modAiCoagulantControlKafkaFlag(updateDto);
                    break;
                case CommonValue.PROCESS_MIXING: // 혼화응집
                    databaseService.modAiMixingControlKafkaFlag(updateDto);
                    break;
                case CommonValue.PROCESS_SEDIMENTATION: // 침전
                    databaseService.modAiSedimentationControlKafkaFlag(updateDto);
                    break;
                case CommonValue.PROCESS_FILTER: // 여과
                    databaseService.modAiFilterControlKafkaFlag(updateDto);
                    break;
                case CommonValue.PROCESS_DISINFECTION: // 소독
                    if (aiControl.getDisinfectionIndex() == 1) {
                        databaseService.modAiPreDisinfectionControlKafkaFlag(updateDto);
                    } else if (aiControl.getDisinfectionIndex() == 3) {
                        databaseService.modAiPostDisinfectionControlKafkaFlag(updateDto);
                    }
                    break;
            }
        }
        return strErrorBody;
    }

    /**
     * 공정별 INIT 정보
     * 
     * @param aiControl 알람정보 DTO
     * @return AiProcessInitDTO 공정 INIT DTO
     */
    private AiProcessInitDTO getAiInit(AlarmInfoDTO aiControl) {
        AiProcessInitDTO aiInit = new AiProcessInitDTO();
        String logMsg = "";

        switch (aiControl.getProcess()) {
            case CommonValue.PROCESS_RECEIVING: // 착수
                aiInit = databaseService.getAiReceivingInit(CommonValue.B_OPERATION_MODE, aiControl.getProcessStep());
                logMsg = "getAiReceivingInit, result:[{}]";
                break;
            case CommonValue.PROCESS_COAGULANT: // 약품
                aiInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE, aiControl.getProcessStep());
                logMsg = "getAiCoagulantInit, result:[{}]";
                break;
            case CommonValue.PROCESS_MIXING: // 혼화응집
                aiInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE, aiControl.getProcessStep());
                logMsg = "getAiMixingInit, result:[{}]";
                break;
            case CommonValue.PROCESS_SEDIMENTATION: // 침전
                aiInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE,
                        aiControl.getProcessStep());
                logMsg = "getAiSedimentationInit, result:[{}]";
                break;
            case CommonValue.PROCESS_FILTER: // 여과
                aiInit = databaseService.getAiFilterInit(CommonValue.F_OPERATION_MODE, 2);
                logMsg = "getAiFilterInit, result:[{}]";
                break;
            case CommonValue.PROCESS_DISINFECTION: // 소독
                if (aiControl.getDisinfectionIndex() == CommonValue.DISINFECTION_PRE_STEP) {
                    aiInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE,
                            aiControl.getProcessStep(), CommonValue.DISINFECTION_PRE_STEP);
                } else if (aiControl.getDisinfectionIndex() == CommonValue.DISINFECTION_POST_STEP) {
                    aiInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE,
                            aiControl.getProcessStep(), CommonValue.DISINFECTION_POST_STEP);
                }
                logMsg = "getAiDisinfectionInit, result:[{}]";
                break;
        }
        log.debug(logMsg, aiInit != null ? 1 : 0);
        return aiInit;
    }

    /**
     * 등록된 알람 정보 조회
     * 
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/alarm/info", method = RequestMethod.GET)
    public ResponseEntity<String> getAlarmInfo() {
        log.debug("getAlarmInfo");

        // 전체 알람 정보 조회
        List<AlarmInfoDTO> alarmInfoDTOList = databaseService.getAlarmInfo();
        log.debug("getAlarmInfo, result:[{}]", alarmInfoDTOList.size());

        if (alarmInfoDTOList.size() > 0) {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("alarm_info", alarmInfoDTOList);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = "";
            try {
                strBody = objectMapper.writeValueAsString(responseBody);
            } catch (JsonProcessingException e) {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"Empty alarm_info\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 알람 이력 등록
     * 
     * @param alarmControl Front-end 팝업을 통한 제어 명령을 저장하기 위한 DTO
     */
    @RequestMapping(value = "/alarm/controlHistory", method = RequestMethod.PUT)
    public ResponseEntity<String> putAlarmControlHistory(@RequestBody InterfaceAlarmControlHistoryDTO alarmControl,
            @RequestHeader("X-ACCESS-TOKEN") String token) {

        // 등록된 알람ID인지 검사
        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(alarmControl.getAlm_id());
        log.debug("getAlarmInfoFromAlarmId, result:[{}]", alarmInfo != null ? 1 : 0);
        
        if (alarmInfo != null) {
        	//alarmControl에 해당하는 aiControlList2 조회
        	List<AiProcessControlDTO> aiControlList = new ArrayList<AiProcessControlDTO>();
        	aiControlList = getAlarmCtrList(alarmInfo, alarmControl.getAlm_ntf_ti());
        	
        	List<AiProcessControlDTO> aiControlList2 = new ArrayList<AiProcessControlDTO>();
        	aiControlList2 = aiControlList.stream()
        			.filter(target -> alarmControl.getAlm_ntf_ti().compareTo(target.getRnti()) == 0)
        			.collect(Collectors.toList());
        	
        	// 아산은 이 시점에 aiControlList2.size() == 0 이라면 이미 이력적재 및 제어 완료된 상태
        	if(aiControlList2.size() == 0) {
        		String strErrorBody = "{\"reason\":\"No Alarm Control History to insert\"}";
        		return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        	}
        	
        	//이력이 이미 추가된 알람은 히스토리를 적재하지 않음
//        List<AiProcessControlDTO> filteredList = aiControlList2.stream().filter(ctr -> databaseService.getCtrHisBySeqAndTag(alarmControl.getSeq(), ctr.getTag_sn()).size() == 0).collect(Collectors.toList());
//        if(filteredList.size() == 0) {
//            String strErrorBody = "{\"reason\":\"No Alarm Control History to insert\"}";
//	        return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
        	
        	Date currentDate = new Date();
        	
        	//Convert CTR into AlarmControlHistory && Insert AlarmControlHistory
        	for(AiProcessControlDTO ctr : aiControlList2) {
        		InterfaceAlarmControlHistoryDTO almCtrHis = new InterfaceAlarmControlHistoryDTO();
        		// 로그인세션이 있을경우
        		if (token != null && !"".equals(token) && !token.equals("undefined")) {
        			AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        			almCtrHis.setUsrId(accessTokenDTO.getUsr_id());
        			almCtrHis.setUsrNm(accessTokenDTO.getUsr_nm());
        		}
        		almCtrHis.setCtrTi(currentDate);
        		almCtrHis.setCtrYn(alarmControl.getCtrYn());
        		almCtrHis.setSeq(alarmControl.getSeq());
        		almCtrHis.setUpdTi(ctr.getUpd_ti());
        		almCtrHis.setTagSn(ctr.getTag_sn());
        		databaseService.insertAlarmControlHistory(almCtrHis);
        	}
        	
        	String strBody = "Successfully inserted into alarm control history";
        	return new ResponseEntity<>(strBody, HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"Empty alarmInfo\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 공정별 CTR 조회
     * 
     * @param alarmInfo  알람정보 DTO
     * @param alm_ntf_ti 알람시간
     * @return List<AiProcessControlDTO> 공정 CTR 조회
     */
    private List<AiProcessControlDTO> getAlarmCtrList(AlarmInfoDTO alarmInfo, Date alm_ntf_ti) {

        List<AiProcessControlDTO> aiControlList = new ArrayList<AiProcessControlDTO>();
        // 최근 공정에 등록된 제어 명령을 조회
        AiProcessControlDTO queryDto = new AiProcessControlDTO();
        queryDto.setRnti(alm_ntf_ti);
        queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP); // get latest(1minute) control value(kafka_flag = 1)
        queryDto.setProcessStep(alarmInfo.getProcessStep());

        switch (alarmInfo.getProcess()) {
            case CommonValue.PROCESS_RECEIVING: // 착수
                aiControlList = databaseService.getListAiReceivingControl(queryDto);
                break;
            case CommonValue.PROCESS_COAGULANT: // 약품
                aiControlList = databaseService.getListAiCoagulantControl(queryDto);
                break;
            case CommonValue.PROCESS_MIXING: // 혼화응집
                aiControlList = databaseService.getListAiMixingControl(queryDto);
                break;
            case CommonValue.PROCESS_SEDIMENTATION: // 침전
                aiControlList = databaseService.getListAiSedimentationControl(queryDto);
                break;
            case CommonValue.PROCESS_FILTER: // 여과
                aiControlList = databaseService.getListAiFilterControl(queryDto);
                break;
            case CommonValue.PROCESS_DISINFECTION: // 소독
                if (alarmInfo.getDisinfectionIndex() == CommonValue.DISINFECTION_PRE_STEP) {
                    aiControlList = databaseService.getListAiPreDisinfectionControl(queryDto);
                } else if (alarmInfo.getDisinfectionIndex() == CommonValue.DISINFECTION_POST_STEP) {
                    aiControlList = databaseService.getListAiPostDisinfectionControl(queryDto);
                }
                break;
        }
        return aiControlList;
    }
}
