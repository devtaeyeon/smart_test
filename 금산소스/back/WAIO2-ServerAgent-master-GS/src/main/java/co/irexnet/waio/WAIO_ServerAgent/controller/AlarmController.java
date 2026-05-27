package co.irexnet.waio.WAIO_ServerAgent.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessAlarmDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessControlDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiFactorCollectionDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiFactorDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmNotifyDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlmCtrHisDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAlarmControlDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAlarmControlHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;
import co.irexnet.waio.WAIO_ServerAgent.kafka.KafkaProducer;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.AlarmInfoList;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableSwagger2
@Slf4j
public class AlarmController
{
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
//        calendar.set(2023, 10, 15, 9, 44);// FIXME 삭제 23/10/17/16:17:46
        Date startTime = calendar.getTime();

        // 최근 1분 알람 조회
        List<AlarmNotifyDTO> alarmNotifyList = databaseService.getAlarmNotify(startTime);
        log.debug("getAlarmNotify, result:[{}]", alarmNotifyList.size());
        if(alarmNotifyList.size() > 0) {
        	// Make Response Body
            List<LinkedHashMap<String, Object>> resultArray = new ArrayList<>();
            for(AlarmNotifyDTO alarmNotify : alarmNotifyList) {
                // 최근 1분간 발보된 알람 중 타 시스템 발보 및 반자동 제어를 위한 알람 추출
                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(alarmNotify.getAlm_id());
                
                if(alarmInfo == null) {
                    continue;
                }

                if(!alarmInfo.getAlm_dp_yn().equals("Y")) {
                    continue;
                }
                
                // alarmNotify , alarmInfo 내부 변수 추출
                int processStep = alarmInfo.getProcessStep();//공정 단계
                Date almNtfTi = alarmNotify.getAlm_ntf_ti(); //알람 시간
                
                if(alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_OFF ||
                        alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_THRESHOLD) {
                    // update alarm_notify ack_state
//                    databaseService.modAlarmNotifyAckState(alarmNotify.getAlarm_notify_index(), true);
                	if (alarmInfo.getCd_nm() != null && alarmInfo.getCd_nm().endsWith(CommonValue.ALARM_CODE_CONNECTION_ERROR)) {
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
                } else if(alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_ANOTHER_SYSTEM) {
                    // update alarm_notify ack_state
//                    databaseService.modAlarmNotifyAckState(alarmNotify.getAlarm_notify_index(), true);
                    // add resultArray
                	
            		String almId = String.valueOf(alarmInfo.getAlm_id());
            		if(almId.charAt(0) == '1' && almId.charAt(1) == '3') { // ' 자율운영 - AI 알람 '의 경우
                    	// 팝업으로 노출할 알람 조회
                    	AiProcessAlarmDTO queryDto = new AiProcessAlarmDTO();
                        queryDto.setAlm_ti(almNtfTi);
                        queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                        queryDto.setProcessStep(processStep);
                    	
                    	List<AiProcessAlarmDTO> aiAlarmList = new ArrayList<>();
                    	aiAlarmList = getAiAlarmList(queryDto, alarmInfo);
                    	
                    	//조회된 aiAlarmList가 없다면 팝업을 보내지않음.
                        if(aiAlarmList.size() == 0) {
                            continue;
                        }
                        //조회된 aiAlarmList 중  almId, almTi이 해당하는 알람으로 필터링 후 재확인.
                    	AiProcessAlarmDTO targetAlarm;
                    	targetAlarm = (AiProcessAlarmDTO) aiAlarmList
                    												 .stream()
                    												 .filter(target -> alarmNotify.getAlm_ntf_ti().compareTo(target.getAlm_ti()) == 0 && alarmNotify.getAlm_id() == target.getAlm_id())
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
                    singleBody.put("alm_ntf_ti", almNtfTi);

                    resultArray.add(singleBody);
                } else if(alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_SEMI_AUTO || alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED) {
                	
                	int nOperationMode = 0; // 반자동 제어 모드임을 확인하는 변수
                	AiProcessInitDTO aiProcessInit = new AiProcessInitDTO();
                	
                	String processName = alarmInfo.getCd_nm().substring(0, alarmInfo.getCd_nm().indexOf("_"));//공정 명칭
                    //cdNm, processStep에 해당하는 운전모드 조회
                    aiProcessInit = getAiProcessInitDTO(alarmInfo);
                    log.debug("getAi"+processName+"Init, result:[{}]", aiProcessInit != null ? 1 : 0);
                    if(aiProcessInit == null){
                        continue;
                    }
                    nOperationMode = aiProcessInit.getInit_val().intValue();
                    // 제어 -- 현재 운전모드가 반자동 제어 모드가 아니라면 resultArray에 포함시키지 않음
                    if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO && alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_SEMI_AUTO){
                        continue;
                    }
                    // 임계치 초과 -- 현재 운전모드가 수동모드라면 resultArray에 포함시키지 않음
                    if(nOperationMode == CommonValue.OPERATION_MODE_MANUAL && alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED){
                        continue;
                    }
                    
                   // 팝업에 노출할 CTR 리스트 조회 -- 추후 알람 '제어'버튼 클릭시 조회하는 조건과 동일.
                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRnti(almNtfTi);
                    queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                    queryDto.setProcessStep(processStep);
                    
                    List<AiProcessControlDTO> aiControlList = new ArrayList<>();
                    aiControlList = getAiControlList(queryDto, alarmInfo);
                    
                    //조회된 aiControlList가 없다면 팝업을 보내지않음.
                    if(aiControlList.size() == 0) {
                        continue;
                    }
                    
                    List<AiProcessControlDTO> aiControlList2 = new ArrayList<AiProcessControlDTO>();
                    aiControlList2 = aiControlList.stream().filter(target -> alarmNotify.getAlm_ntf_ti().compareTo(target.getRnti()) == 0).collect(Collectors.toList());
                    
                    // aiControlList에 해당하는 DP데이터 추가
                    aiControlList2.forEach(aiControl -> aiControl.setTag_dp(databaseService.selectTagDp(aiControl.getTag_sn())));
                   
                    if(aiControlList2.size() > 0) {
	                    // add resultArray
	                    LinkedHashMap<String, Object> singleBody = new LinkedHashMap<>();
	                    singleBody.put("seq", alarmNotify.getSeq());
	                    singleBody.put("alm_id", alarmNotify.getAlm_id());
	                    singleBody.put("alm_ty", alarmInfo.getAlm_ty());
	                    singleBody.put("message", alarmInfo.getDp_nm());
	                    singleBody.put("url", alarmInfo.getUrl());
	                    singleBody.put("alm_ntf_ti", almNtfTi);
	                    singleBody.put("ctr_list", aiControlList2); //controlList
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
            } catch(JsonProcessingException e) {
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
     *     반자동 제어 팝업에서 제어버튼을 눌렀을 때 SCADA로 제어메시지를 전송
     * 
     * @param alarmControl Front-end 팝업을 통한 제어 명령을 저장하기 위한 DTO
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/alarm/control", method = RequestMethod.PUT)
    public ResponseEntity<String> putControlAlarm(@RequestBody InterfaceAlarmControlDTO alarmControl) {
        log.debug("putControlAlarm, alm_id:[{}]", alarmControl.getAlm_id());
        String strErrorBody = "";
        // 등록된 알람ID인지 검사
        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(alarmControl.getAlm_id());
        log.debug("getAlarmInfoFromAlarmId, result:[{}]", alarmInfo != null ? 1 : 0);
        if(alarmInfo == null){
            log.error("Does not exist Alarm:[{}]", alarmControl.getAlm_id());
            strErrorBody = "{\"reason\":\"Invalid alm_id\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
        
    	int processStep = alarmInfo.getProcessStep();//공정 단계
    	String processName = alarmInfo.getCd_nm().substring(0, alarmInfo.getCd_nm().indexOf("_"));//공정 명칭
        AiProcessInitDTO aiProcessInit = null;

        //cdNm, processStep에 해당하는 운전모드 조회
        aiProcessInit = getAiProcessInitDTO(alarmInfo);
        
        //조회된 Init이 없는경우
        if(aiProcessInit == null) {
        	strErrorBody = "{\"reason\":\"Empty ai_"+processName.toLowerCase()+"_init\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        int nOperationMode = aiProcessInit.getInit_val().intValue();
        //현재 운전모드가 반자동 제어가 아니라면 SCADA로 제어명령을 전송하지 않음 (제어)
        if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO && alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_SEMI_AUTO) {
            strErrorBody = "{\"reason\":\"Invalid ai operation state\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
        
        //현재 운전모드가 수동이라면 SCADA로 제어명령을 전송하지 않음 (임계치 초과)
        if(nOperationMode == CommonValue.OPERATION_MODE_MANUAL && alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED) {
            strErrorBody = "{\"reason\":\"Invalid ai operation state\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        //최근 공정에 등록된 제어 명령을 조회 및 전송
        AiProcessControlDTO queryDto = new AiProcessControlDTO();
        queryDto.setRnti(alarmControl.getAlm_ntf_ti());
        queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
        queryDto.setProcessStep(processStep);
        
        List<AiProcessControlDTO> aiControlList = new ArrayList<>();
        aiControlList = getAiControlList(queryDto, alarmInfo);
        
        List<AiProcessControlDTO> aiControlList2 = new ArrayList<AiProcessControlDTO>();
        aiControlList2 = aiControlList.stream().filter(target -> alarmControl.getAlm_ntf_ti().compareTo(target.getRnti()) == 0).collect(Collectors.toList());
        
        
        if(!aiControlList2.isEmpty()){
        	// 제어태그 kafka 전송 & kafka flag send 업데이트
            return sendAiProcessControl(aiControlList2, alarmInfo);
        } else {
            // 이미 제어가 완료되어 kfk_flg가 변경된 경우
            strErrorBody = "{\"reason\":\"already controlled\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.CONFLICT);
        }
    }

    /**
     * 제어 또는 공정 알람이 취소 되었을때, KAFKA FLAG CANCEL 업데이트
     * 
     * @param alarmControl Front-end 팝업을 통한 제어 명령을 저장하기 위한 DTO
     * @param noActionFlag 알람 무반응 플래그
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/alarm/cancel", method = RequestMethod.PUT)
    public ResponseEntity<String> cancelControlAlarm(@RequestBody InterfaceAlarmControlDTO alarmControl, @RequestParam(required = false) String noActionFlag) {
        log.debug("cancelControlAlarm, alm_id:[{}]", alarmControl.getAlm_id());
        String strErrorBody = "";
        
        // 등록된 알람ID인지 검사
        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(alarmControl.getAlm_id());
        log.debug("getAlarmInfoFromAlarmId, result:[{}]", alarmInfo != null ? 1 : 0);
        if(alarmInfo == null){
            log.error("Does not exist Alarm:[{}]", alarmControl.getAlm_id());
            strErrorBody = "{\"reason\":\"Invalid alm_id\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
        
    	int processStep = alarmInfo.getProcessStep();//공정 단계
    	String processName = alarmInfo.getCd_nm().substring(0, alarmInfo.getCd_nm().indexOf("_"));//공정 명칭

    	// 반자동 / 임계치 알람
    	if(alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_SEMI_AUTO || alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED) {
        //최근 공정에 등록된 제어 명령 조회
        AiProcessControlDTO queryDto = new AiProcessControlDTO();
        queryDto.setRnti(alarmControl.getAlm_ntf_ti());
        queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
        queryDto.setProcessStep(processStep);
        
        List<AiProcessControlDTO> aiControlList = new ArrayList<>();
        aiControlList = getAiControlList(queryDto, alarmInfo);
        
        List<AiProcessControlDTO> aiControlList2 = new ArrayList<AiProcessControlDTO>();
        aiControlList2 = aiControlList.stream().filter(target -> alarmControl.getAlm_ntf_ti().compareTo(target.getRnti()) == 0).collect(Collectors.toList());
        
        //Kafka Flag - cancel 업데이트
        if(!aiControlList2.isEmpty()){
        	for(AiProcessControlDTO dto : aiControlList2) {
        		AiProcessControlDTO updateDto = dto;
        		updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_CANCEL);
        		updateDto.setProcessStep(alarmInfo.getProcessStep());
        		updateControlKafkaFlag(updateDto, alarmInfo);
        	}
            return new ResponseEntity<>("", HttpStatus.OK);
        }else{
        	if(noActionFlag != null) {
        		//무반응 알람 - CONFLICT 처리 안하도록.
            	strErrorBody = "{\"reason\":\"already controlled\"}";
            	return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        	}else {
        		// 이미 제어가 완료되어 kfk_flg가 변경된 경우
        		strErrorBody = "{\"reason\":\"already controlled\"}";
        		return new ResponseEntity<>(strErrorBody, HttpStatus.CONFLICT);            		
        	}
        }
        
    	// 3번 알람
    	}else if(alarmInfo.getAlm_ty() == CommonValue.ALARM_TYPE_ANOTHER_SYSTEM) {
    		String almId = String.valueOf(alarmInfo.getAlm_id());
    		if(almId.charAt(0) != '1' || almId.charAt(1) != '3') { // ' 자율운영 - AI 알람 코드 '의 경우만 카프카 플래그 업데이트 진행
    	        strErrorBody = "{\"reason\":\"Not AI alarm_info\"}";
    	        return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
    		}
    		
            // 최근 공정에 등록된 알람 데이터 조회
        	AiProcessAlarmDTO queryDto = new AiProcessAlarmDTO();
            queryDto.setAlm_ti(alarmControl.getAlm_ntf_ti());
            queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
            queryDto.setProcessStep(processStep);
        	
            // 해당하는 알람 조회
        	List<AiProcessAlarmDTO> aiAlarmList = new ArrayList<>();
        	aiAlarmList = getAiAlarmList(queryDto, alarmInfo);

        	//조회된 aiAlarmList가 없다면 팝업을 보내지않음.
            if(aiAlarmList.size() == 0) {
                strErrorBody = "{\"reason\":\"Empty"+processName.toLowerCase()+"alarm\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
            
            //조회된 aiAlarmList 중  almId, almTi이 해당하는 알람으로 필터링.
        	AiProcessAlarmDTO targetAlarm;
        	targetAlarm = (AiProcessAlarmDTO) aiAlarmList
        												 .stream()
        												 .filter(target -> alarmControl.getAlm_ntf_ti().compareTo(target.getAlm_ti()) == 0 && alarmControl.getAlm_id() == target.getAlm_id())
        												 .findFirst()
        												 .orElse(null);
        	
        	// KFK_FLG를 KAFKA_FLAG_CANCEL로 UPDATE
        	if(targetAlarm != null){
        		AiProcessAlarmDTO updateDto = targetAlarm;
        		updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_CANCEL);
        		updateDto.setProcessStep(alarmInfo.getProcessStep());
        		updateAlarmKafkaFlag(updateDto, alarmInfo);
        		return new ResponseEntity<>("", HttpStatus.OK);
            } else{
                strErrorBody = "{\"reason\":\"Empty"+processName.toLowerCase()+"alarm\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }  

    	}
    	
        strErrorBody = "{\"reason\":\"Empty alarm_info\"}";
        return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 공정 알람 '이동'버튼을 눌렀을때, KAFKA FLAG CONFIRM 업데이트
     * 
     * @param alarmControl Front-end 팝업을 통한 제어 명령을 저장하기 위한 DTO
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/alarm/confirm", method = RequestMethod.PUT)
    public ResponseEntity<String> confirmAlarm(@RequestBody InterfaceAlarmControlDTO alarmControl){
        log.debug("confirmAlarm, alm_id:[{}]", alarmControl.getAlm_id());
        String strErrorBody = "";
        
        // 등록된 알람ID인지 검사
        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(alarmControl.getAlm_id());
        log.debug("getAlarmInfoFromAlarmId, result:[{}]", alarmInfo != null ? 1 : 0);
        if(alarmInfo == null){
            log.error("Does not exist Alarm:[{}]", alarmControl.getAlm_id());
            strErrorBody = "{\"reason\":\"Invalid alm_id\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
        
    	int processStep = alarmInfo.getProcessStep();//공정 단계
    	String processName = alarmInfo.getCd_nm().substring(0, alarmInfo.getCd_nm().indexOf("_"));//공정 명칭
    	
    	if(alarmInfo.getAlm_ty() != CommonValue.ALARM_TYPE_ANOTHER_SYSTEM) {
	        strErrorBody = "{\"reason\":\"Not alarm_info\"}";
	        return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
    	}
		String almId = String.valueOf(alarmInfo.getAlm_id());
		if(almId.charAt(0) != '1' || almId.charAt(1) != '3') { // ' 자율운영 - AI 알람 코드 '의 경우만 카프카 플래그 업데이트 진행
	        strErrorBody = "{\"reason\":\"Not AI alarm_info\"}";
	        return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
		}
		
        // 최근 공정에 등록된 알람 데이터 조회
    	AiProcessAlarmDTO queryDto = new AiProcessAlarmDTO();
        queryDto.setAlm_ti(alarmControl.getAlm_ntf_ti());
        queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
        queryDto.setProcessStep(processStep);
    	
        // 해당하는 알람 조회
    	List<AiProcessAlarmDTO> aiAlarmList = new ArrayList<>();
    	aiAlarmList = getAiAlarmList(queryDto, alarmInfo);

    	//조회된 aiAlarmList가 없다면 팝업을 보내지않음.
        if(aiAlarmList.size() == 0) {
            strErrorBody = "{\"reason\":\"Empty"+processName.toLowerCase()+"alarm\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
        
        //조회된 aiAlarmList 중  almId, almTi이 해당하는 알람으로 필터링.
    	AiProcessAlarmDTO targetAlarm;
    	targetAlarm = (AiProcessAlarmDTO) aiAlarmList
    												 .stream()
    												 .filter(target -> alarmControl.getAlm_ntf_ti().compareTo(target.getAlm_ti()) == 0 && alarmControl.getAlm_id() == target.getAlm_id())
    												 .findFirst()
    												 .orElse(null);
    	
    	// KFK_FLG를 KAFKA_FLAG_CONFIRM로 UPDATE
    	if(targetAlarm != null){
    		AiProcessAlarmDTO updateDto = targetAlarm;
    		updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_CONFIRM);
    		updateDto.setProcessStep(alarmInfo.getProcessStep());
    		updateAlarmKafkaFlag(updateDto, alarmInfo);
    		return new ResponseEntity<>("", HttpStatus.OK);
        } else{
            strErrorBody = "{\"reason\":\"Empty"+processName.toLowerCase()+"alarm\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }  
    }
    
    /**
     * 등록된 알람 정보 조회
     * 
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/alarm/info", method = RequestMethod.GET)
    public ResponseEntity<String> getAlarmInfo()
    {
        log.debug("getAlarmInfo");

        // 전체 알람 정보 조회
        List<AlarmInfoDTO> alarmInfoDTOList = databaseService.getAlarmInfo();
        log.debug("getAlarmInfo, result:[{}]", alarmInfoDTOList.size());
        if(alarmInfoDTOList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("alarm_info", alarmInfoDTOList);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = "";
            try{
                strBody = objectMapper.writeValueAsString(responseBody);
            } catch(JsonProcessingException e){
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        }
        else {
            String strErrorBody = "{\"reason\":\"Empty alarm_info\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 알람 이력 등록
     * 
     * @param almCtrHisList 알람 리스트
     * @param token 로그인 정보
     * @return 
     */
    @RequestMapping(value= "/alarm/almCtrHis/put", method = RequestMethod.PUT)
    public ResponseEntity<String> putAlmCtrHis(@RequestBody List<AlmCtrHisDTO> almCtrHisList, @RequestHeader("X-ACCESS-TOKEN") String token) { 

	    //이력이 추가되지 않은 알람만 이력 추가 진행 - almSeq 및 tag sn 동일조건.
	    List<AlmCtrHisDTO> filteredList = almCtrHisList.stream().filter((ctr) -> databaseService.getCtrHisBySeqAndTag(ctr).size() == 0).collect(Collectors.toList());
	    
	    if(filteredList.size() == 0) {
            String strErrorBody = "{\"reason\":\"No AlmCtrHis to insert\"}";
	        return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
	    }

	    Date currentDate = new Date();
    	//ctrTi
    	filteredList.forEach((ctr) ->  ctr.setCtr_ti(currentDate));
    	// 로그인세션이 있을경우
        if(!"".equals(token)) {
	        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
    		filteredList.forEach((ctr) -> {
	        	ctr.setUsr_id(accessTokenDTO.getUsr_id());
	        	ctr.setUsr_nm(accessTokenDTO.getUsr_nm());
	        });
	    }
    	databaseService.addAlmCtrHis(filteredList);	    	
    	
        String strBody = "";
        return new ResponseEntity<>(strBody, HttpStatus.OK);
	    	
    }
    
    //제어 알람 이력 리스트 조회
//    @RequestMapping(value="/alarm/ctrHisList", method = RequestMethod.GET)
//    public ResponseEntity<String> getCtrHisList(){
//    	
//        log.debug("Recv getAllAlarms");
//
//        // 제어 알람 이력 조회
//        List<AlmCtrHisDTO> almCtrHisList = databaseService.getAlmCtrHisList();
//        log.debug("getAllAlarmNotify, result:[{}]", almCtrHisList.size());
//        if(almCtrHisList.size() > 0)
//        {
//            // Make Response Body
//            Map<String, Object> responseBody = new HashMap<>();
//            responseBody.put("almCtrHisList", almCtrHisList);
//    	
//    	
//	        // ObjectMapper를 통해 JSON 값을 String으로 변환
//	        ObjectMapper objectMapper = new ObjectMapper();
//	        String strBody = "";
//	        try
//	        {
//	            strBody = objectMapper.writeValueAsString(responseBody);
//	        }
//	        catch(JsonProcessingException e)
//	        {
//	            String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
//	            return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
//	        }
//	        	return new ResponseEntity<>(strBody, HttpStatus.OK);
//	    }
//	    else
//	    {
//	        String strErrorBody = "{\"reason\":\"Empty alarm_notify\"}";
//	        return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//	    }
//	}
//    
    /**
     * 제어 알람 이력 리스트 조회 (기간선택)
     * @param dateSearchDTO 기간정보
     * @return
     */
    @RequestMapping(value="/alarm/ctrHisListSearch", method = RequestMethod.PUT)
    public ResponseEntity<String> getCtrHisListSearch(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.debug("getAlarmsSearch, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // Get Alarm_notify between alarm_time
        List<AlmCtrHisDTO> almCtrHisList = databaseService.getSearchAlmCtrHis(dateSearchDTO);
        log.debug("getSearchAlarmNotify, result:[{}]", almCtrHisList.size());
        
        // 조회된 이력 각각에 해당하는 태그 정보 리스트
        List<AlmCtrHisDTO> tagInfoList = new ArrayList<AlmCtrHisDTO>();
        
        for(AlmCtrHisDTO alarmControlHistory : almCtrHisList) {
            tagInfoList = databaseService.getTagInfoList(alarmControlHistory);
            // 태그가 1건인 경우에만 리스트에 노출
            if(tagInfoList.size() == 1) {
                alarmControlHistory.setDp(tagInfoList.get(0).getDp());
                alarmControlHistory.setTag_val(tagInfoList.get(0).getTag_val());
                alarmControlHistory.setTag_cmp_val(tagInfoList.get(0).getTag_cmp_val());
            }else {
                alarmControlHistory.setDp("-");
                alarmControlHistory.setTag_val("-");
                alarmControlHistory.setTag_cmp_val("-");
            }
        }        
        
        // Make Response Body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("almCtrHisList", almCtrHisList);

        // ObjectMapper를 통해 JSON 값을 String으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String strBody = "";
        try
        {
            strBody = objectMapper.writeValueAsString(responseBody);
        }
        catch(JsonProcessingException e)
        {
            String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(strBody, HttpStatus.OK);
    }
    
    /**
     * 제어 1건에 대한 CTR 리스트 조회
     * @param almCtrHisDTO 알람 정보
     * @return
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @RequestMapping(value="alarm/detail", method = RequestMethod.PUT)
    public ResponseEntity<String> putAlarmDetail(@RequestBody AlmCtrHisDTO almCtrHisDTO) throws JsonMappingException, JsonProcessingException
    {
        log.debug("putAlarmDetail");
        
        //Make Response Body
        LinkedHashMap<String, Object> alarmDetail = new LinkedHashMap<>();

        //put CtrHisLst
        List<AiProcessControlDTO> ctrListByAlm = databaseService.getCtrListByAlm(almCtrHisDTO.getAlm_id(), almCtrHisDTO.getAlm_seq(), almCtrHisDTO.getCtr_ti());       

        if(ctrListByAlm.size() == 0) {
            String strErrorBody = "{\"reason\":\"Empty alarm_ctr_list\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.OK);
        }
        alarmDetail.put("ctrListByAlm", ctrListByAlm);
        
        //get processInfo
        Map<String, String> processInfoMap = getProcessInfo(almCtrHisDTO);
        String procCd = processInfoMap.get("procCd");
        String disinfectionIndex = processInfoMap.get("disinfectionIndex");
        
        //Put Factor Map
    	ObjectMapper objectMapper = new ObjectMapper();
    	AiFactorDTO aiFactorDTO = new AiFactorDTO();
    	aiFactorDTO = databaseService.getAiFactorData(ctrListByAlm.get(0).getRnti(), procCd, disinfectionIndex);
    	
		AiFactorCollectionDTO aiFactorCollectionDTO = new AiFactorCollectionDTO();
	    List<AiFactorCollectionDTO> aiFactorMapList = objectMapper.readValue(aiFactorDTO.getFactor(), new TypeReference<List<AiFactorCollectionDTO>>() {});
	    aiFactorCollectionDTO  = aiFactorMapList.get(0);
	    
	    //Put DisinfectionIndex
	    if(disinfectionIndex.equals("PRE")) {
	    	aiFactorCollectionDTO.setDisinfection_index(1);
	    }else if (disinfectionIndex.equals("POST")){
	    	aiFactorCollectionDTO.setDisinfection_index(3);
	    }
    	alarmDetail.put("factorMap",  aiFactorCollectionDTO);
    	
    	//put ProcessInfo
        alarmDetail.put("procCd", procCd);
        
        // Make Response Body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("alarmDetail", alarmDetail);

        String strBody = "";
        try {
            strBody = objectMapper.writeValueAsString(responseBody);
        } catch (JsonProcessingException e) {
            String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(strBody, HttpStatus.OK);
    }

    
    /**
     * almId, processStep에 해당하는 운전모드 조회
     * @param alarmInfo
     * @return
     */
    public AiProcessInitDTO getAiProcessInitDTO(AlarmInfoDTO alarmInfo) {
    	AiProcessInitDTO aiProcessInit = new AiProcessInitDTO();
    	switch (alarmInfo.getProcess()) {
	        case CommonValue.PROCESS_COAGULANT:           // 약품
	            aiProcessInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE, alarmInfo.getProcessStep());
	            break;
	        case CommonValue.PROCESS_MIXING:              // 혼화응집
	            aiProcessInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE, alarmInfo.getProcessStep());
	            break;
	        case CommonValue.PROCESS_DISINFECTION:        // 소독
	            if(alarmInfo.getDisinfectionIndex() == CommonValue.DISINFECTION_PRE_STEP) {
	            	aiProcessInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE, alarmInfo.getProcessStep(), CommonValue.DISINFECTION_PRE_STEP);
	            } else if(alarmInfo.getDisinfectionIndex() == CommonValue.DISINFECTION_POST_STEP) {
	            	aiProcessInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE, alarmInfo.getProcessStep(), CommonValue.DISINFECTION_POST_STEP);
	            }
	            break;
    	}
    	return aiProcessInit;
    }

    /**
     * 최근 공정에 등록된 제어 명령을 조회
     * @param queryDto
     * @param alarmInfo
     * @return
     */
    public List<AiProcessControlDTO> getAiControlList(AiProcessControlDTO queryDto, AlarmInfoDTO alarmInfo) {
        List<AiProcessControlDTO> aiControlList = new ArrayList<>();
        switch (alarmInfo.getProcess()) {
	        case CommonValue.PROCESS_COAGULANT:           // 약품
	            aiControlList = databaseService.getListAiCoagulantControl(queryDto);
	            break;
	        case CommonValue.PROCESS_MIXING:              // 혼화응집
	            aiControlList = databaseService.getListAiMixingControl(queryDto);
	            break;
	        case CommonValue.PROCESS_DISINFECTION:        // 소독
	            if(alarmInfo.getDisinfectionIndex() == CommonValue.DISINFECTION_PRE_STEP) {
	                aiControlList = databaseService.getListAiPreDisinfectionControl(queryDto);
	            } else if(alarmInfo.getDisinfectionIndex() == CommonValue.DISINFECTION_POST_STEP) {
	                aiControlList = databaseService.getListAiPostDisinfectionControl(queryDto);
	            }
	            break;
        }
        return aiControlList;
    }

    
    /**
     * 최근 공정에 등록된 안내알람을 조회
     * @param queryDto
     * @param alarmInfo
     * @return
     */
    public List<AiProcessAlarmDTO> getAiAlarmList(AiProcessAlarmDTO queryDto, AlarmInfoDTO alarmInfo) {
        List<AiProcessAlarmDTO> aiAlarmList = new ArrayList<>();
        switch (alarmInfo.getProcess()) {
	        case CommonValue.PROCESS_COAGULANT:           // 약품
	        	aiAlarmList = databaseService.getAllAiCoagulantAlarm(queryDto.getAlm_ti(), queryDto.getKfk_flg(), queryDto.getProcessStep());
	            break;
	        case CommonValue.PROCESS_MIXING:              // 혼화응집
	        	aiAlarmList = databaseService.getAllAiMixingAlarm(queryDto.getAlm_ti(), queryDto.getKfk_flg(), queryDto.getProcessStep());
	            break;
	        case CommonValue.PROCESS_DISINFECTION:        // 소독
	            if(alarmInfo.getDisinfectionIndex() == CommonValue.DISINFECTION_PRE_STEP) {
	            	aiAlarmList = databaseService.getAllAiDisinfectionAlarm(queryDto.getAlm_ti(), queryDto.getKfk_flg(), queryDto.getProcessStep(), CommonValue.DISINFECTION_PRE_STEP);
	            } else if(alarmInfo.getDisinfectionIndex() == CommonValue.DISINFECTION_POST_STEP) {
	            	aiAlarmList = databaseService.getAllAiDisinfectionAlarm(queryDto.getAlm_ti(), queryDto.getKfk_flg(), queryDto.getProcessStep(), CommonValue.DISINFECTION_POST_STEP);
	            }
	            break;
        }
        return aiAlarmList;
    }

    /**
     * 제어태그 kafka 전송 & kafka flag send 업데이트
     * @param aiControlList
     * @param alarmInfo
     * @return
     */
    public ResponseEntity<String> sendAiProcessControl(List<AiProcessControlDTO> aiControlList, AlarmInfoDTO alarmInfo) {
        String strBody;
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strErrorBody = "";
        try {
            for(AiProcessControlDTO dto : aiControlList) {
                // send control value to kafka ai_control
                LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                controlMap.put("tag", dto.getTag_sn());
                // Pulse / Value 값 구분하여 설정
                controlMap.put("value", dto.getTag_val().equalsIgnoreCase(CommonValue.CONTROL_TRUE) ? true : Float.parseFloat(dto.getTag_val()));
                controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                strBody = objectMapper.writeValueAsString(controlMap);
                kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                // update kafka_flag=3 (kafka_send)
                AiProcessControlDTO updateDto = dto;
                updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_SEND);
                updateDto.setProcessStep(alarmInfo.getProcessStep());
                updateControlKafkaFlag(updateDto, alarmInfo);
            }
            return new ResponseEntity<>("", HttpStatus.OK);
        } catch(JsonProcessingException e) {
            log.error("JsonProcessingException Occurred in Alarm "+alarmInfo.getCd_nm().substring(0, alarmInfo.getCd_nm().indexOf("_"))+" Control Process");
            strErrorBody = "{\"reason\":\"JsonProcessing Exception\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        } catch(NumberFormatException e) {
            log.error("NumberException Occurred in Alarm "+alarmInfo.getCd_nm().substring(0, alarmInfo.getCd_nm().indexOf("_"))+" Control Process");
            strErrorBody = "{\"reason\":\"NumberFormatException Exception\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * update kafka flag - 공정별 ctr테이블
     * @param updateDto
     * @param alarmInfo
     */
    public void updateControlKafkaFlag(AiProcessControlDTO updateDto, AlarmInfoDTO alarmInfo) {
	    switch(alarmInfo.getProcess()){
		case CommonValue.PROCESS_COAGULANT:
	        databaseService.modAiCoagulantControlKafkaFlag(updateDto);
	        break;
		case CommonValue.PROCESS_MIXING:
	        databaseService.modAiMixingControlKafkaFlag(updateDto);
	        break;
	    case CommonValue.PROCESS_DISINFECTION:        // 소독
	        if(alarmInfo.getDisinfectionIndex() == CommonValue.DISINFECTION_PRE_STEP) {
	        	databaseService.modAiPreDisinfectionControlKafkaFlag(updateDto);
	        }else if(alarmInfo.getDisinfectionIndex() == CommonValue.DISINFECTION_POST_STEP) {
	        	databaseService.modAiPostDisinfectionControlKafkaFlag(updateDto);
	        }
	        break;
	  }
    }
    
    /**
     * update kafka flag - 공정별 alm테이블
     * @param updateDto
     * @param alarmInfo
     */
    public void updateAlarmKafkaFlag(AiProcessAlarmDTO updateDto, AlarmInfoDTO alarmInfo) {
    	switch(alarmInfo.getProcess()){
	    	case CommonValue.PROCESS_COAGULANT:
	            databaseService.modAiCoagulantAlarmKafkaFlag(updateDto, updateDto.getProcessStep());
	            break;
	    	case CommonValue.PROCESS_MIXING:
	            databaseService.modAiMixingAlarmKafkaFlag(updateDto, updateDto.getProcessStep());
	            break;
	        case CommonValue.PROCESS_DISINFECTION:        // 소독
	            if(alarmInfo.getDisinfectionIndex() == CommonValue.DISINFECTION_PRE_STEP) {
	            	databaseService.modAiDisinfectionAlarmKafkaFlag(updateDto, updateDto.getProcessStep(), CommonValue.DISINFECTION_PRE_STEP);
	            } else if(alarmInfo.getDisinfectionIndex() == CommonValue.DISINFECTION_POST_STEP) {
	            	databaseService.modAiDisinfectionAlarmKafkaFlag(updateDto, updateDto.getProcessStep(), CommonValue.DISINFECTION_POST_STEP);
	            }
	            break;
    	}
    	
    }
    
    /**
     * 공정 정보 조회
     * @param almCtrHisDTO
     * @return
     */
    private Map<String, String> getProcessInfo(AlmCtrHisDTO almCtrHisDTO) {
    	Map<String, String> processInfoMap = new HashMap<String, String>();
    	String procCd = "";
    	String disinfectionIndex = "NONE";
    	String cdNm = "";
    	
    	AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(almCtrHisDTO.getAlm_id());
    	if(alarmInfo!= null) {
    		cdNm = alarmInfo.getCd_nm();
    	}
    	

        if (CommonValue.ALARM_CODE_COAGULANT_AI_CONTROL.equals(cdNm) || CommonValue.ALARM_CODE_COAGULANT_AI_THRESHOLD.equals(cdNm)) {
        	procCd = "C";
        } else if (CommonValue.ALARM_CODE_MIXING_AI_CONTROL.equals(cdNm)|| CommonValue.ALARM_CODE_MIXING_AI_THRESHOLD.equals(cdNm)) {
        	procCd = "D";
        } else if (CommonValue.ALARM_CODE_DISINFECTION_AI_PRE_CONTROL.equals(cdNm)|| CommonValue.ALARM_CODE_DISINFECTION_AI_PRE_THRESHOLD.equals(cdNm)) {
        	procCd = "G";
        	disinfectionIndex = "PRE";
        } else if (CommonValue.ALARM_CODE_DISINFECTION_AI_POST_CONTROL.equals(cdNm)|| CommonValue.ALARM_CODE_DISINFECTION_AI_POST_THRESHOLD.equals(cdNm)) {
        	procCd = "G";
        	disinfectionIndex = "POST";
        }
        	
        processInfoMap.put("procCd", procCd);
        processInfoMap.put("disinfectionIndex", disinfectionIndex);
        
    	return processInfoMap;
    }

}
