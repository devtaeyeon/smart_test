package co.irexnet.waio.WAIO_ServerAgent.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiFactorCollectionDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiFactorDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmNotifyDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAlarmControlHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.AlarmInfoList;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesAuthentication;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableSwagger2
@Slf4j
public class AlarmsController {
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    AlarmInfoList alarmInfoList;

    /**
     * 전체 알람 발보 이력 조회
     * 
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/alarms/all", method = RequestMethod.GET)
    public ResponseEntity<String> getAllAlarms() {
        log.debug("Recv getAllAlarms");

        // 알람 발보 이력 조회
        List<AlarmNotifyDTO> alarmNotifyList = databaseService.getAllAlarmNotify();
        log.debug("getAllAlarmNotify, result:[{}]", alarmNotifyList.size());
        if (alarmNotifyList.size() > 0) {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("alarms", alarmNotifyList);

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
            String strErrorBody = "{\"reason\":\"Empty alarm_notify\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 기간 선택을 통한 알람 발보 이력 조회
     * 
     * @param dateSearchDTO
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/alarms/search", method = RequestMethod.PUT)
    public ResponseEntity<String> getAlarmsSearch(@RequestBody InterfaceAlarmControlHistoryDTO dateSearchDTO) {
        log.debug("getAlarmsSearch, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());
        // 기간에 따른 발보 이력 조회
        List<InterfaceAlarmControlHistoryDTO> alarmControlHistoryList = new ArrayList<InterfaceAlarmControlHistoryDTO>();
        // 조회된 이력 각각에 해당하는 태그 정보 리스트
        List<InterfaceAlarmControlHistoryDTO> tagInfoList = new ArrayList<InterfaceAlarmControlHistoryDTO>();

        alarmControlHistoryList = databaseService.getAlarmControlHistory(dateSearchDTO);
        
        for(InterfaceAlarmControlHistoryDTO alarmControlHistory : alarmControlHistoryList) {
        	tagInfoList = databaseService.getTagInfoList(alarmControlHistory);
        	// 태그가 1건인 경우에만 리스트에 노출
        	if(tagInfoList.size() == 1) {
	    		alarmControlHistory.setDp(tagInfoList.get(0).getDp());
	    		alarmControlHistory.setTagVal(tagInfoList.get(0).getTagVal());
	    		alarmControlHistory.setTagCmpVal(tagInfoList.get(0).getTagCmpVal());
        	}
        }

        log.debug("getAlarmsSearch, result:[{}]", alarmControlHistoryList.size());
        if (alarmControlHistoryList.size() > 0) {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("alarms", alarmControlHistoryList);

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
            String strErrorBody = "{\"reason\":\"Empty alarmControlHistoryList\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.OK);
        }
    }
    
    // 상세 보기 팝업 조회
    @RequestMapping(value = "/alarms/searchDetail", method = RequestMethod.PUT)
    public ResponseEntity<String> getAlarmsSearchDetail(@RequestBody InterfaceAlarmControlHistoryDTO detailSearchDTO)
    {
        log.debug("getAlarmsSearchDetail");
        
        // Make Response Body
        LinkedHashMap<String, Object> alarmsDetail = new LinkedHashMap<>();
        
        String procCd = getProcessCode(detailSearchDTO);
    	alarmsDetail.put("procCd", procCd);

        // Get Alarm Detail
        List<InterfaceAlarmControlHistoryDTO> alarmControlHistoryList = new ArrayList<InterfaceAlarmControlHistoryDTO>();
        alarmControlHistoryList = databaseService.getAlarmControlHistoryDetail(detailSearchDTO);
        
        log.debug("getAlarmsSearchDetail, result:[{}]", alarmControlHistoryList.size());
        if(alarmControlHistoryList.size() > 0)
        {
        	alarmsDetail.put("ctrHisList", alarmControlHistoryList);
        	
        	AiFactorDTO aiFactorDTO = new AiFactorDTO();
        	aiFactorDTO.setProc_cd(procCd);
        	aiFactorDTO.setDisinfection_index(String.valueOf(detailSearchDTO.getDisinfectionIndex()));
        	aiFactorDTO.setRnti(alarmControlHistoryList.get(0).getRnti());
        	
        	aiFactorDTO = databaseService.getAiFactorData(aiFactorDTO);
        	
        	// ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            
            try 
            {
            	AiFactorCollectionDTO aiFactorCollectionDTO = new AiFactorCollectionDTO();
            	List<AiFactorCollectionDTO> aiFactorMapList = objectMapper.readValue(aiFactorDTO.getFactor(), new TypeReference<List<AiFactorCollectionDTO>>() {});
            	aiFactorCollectionDTO  = aiFactorMapList.get(0);
            	alarmsDetail.put("factorMap",  aiFactorCollectionDTO);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"Factor JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("alarmsDetail", alarmsDetail);
            
            String strBody = "";
            try
            {
                strBody = objectMapper.writeValueAsString(responseBody);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"AlarmsDetail JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty alarmControlHistoryList\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.OK);
        }
    }

    /**
     * 알람 설정 정보 요청
     * 
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/alarms/setting", method = RequestMethod.GET)
    public ResponseEntity<String> getAlarmsSetting() {
        log.debug("getAlarmsSetting");

        // 알람 설정 정보 조회
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
     * 알람 설정 정보 수정
     * 
     * @param alarmInfoIndex 알람정보 INDEX
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/alarms/setting/{alarmInfoIndex}", method = RequestMethod.PUT)
    public ResponseEntity<String> putAlarmsSetting(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable int alarmInfoIndex,
            @RequestBody AlarmInfoDTO alarmInfoDTO) {
        log.debug("putAlarmsSetting, alarmInfoIndex:[{}]", alarmInfoIndex);
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if (accessTokenDTO != null) {
            // 관리자/운용자 권한인지 확인
            nAuthority = accessTokenDTO.getUsr_auth();
            if (nAuthority == CommonValue.GUEST) {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            } else if (nAuthority == CommonValue.NONE_AUTHORITY) {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            } else {
                bFindToken = true;
                // Stop auto login time update
                // long expiredTime = CommonValue.ONE_MINUTE *
                // propertiesAuthentication.getExpiration();
                // Date expirationDate = new Date();
                // expirationDate.setTime(expirationDate.getTime() + expiredTime);
                // databaseService.modToken(token, expirationDate);
            }
        }

        if (bFindToken == true) {
            // Update alarm_info
            alarmInfoDTO.setSeq(alarmInfoIndex);
            int nResult = databaseService.modAlarmInfo(alarmInfoDTO);
            log.debug("modAlarmInfo:[{}], result:[{}]", alarmInfoIndex, nResult);
            if (nResult > 0) {
                // Update AlarmInfoList
                alarmInfoList.setAlarmInfoList(databaseService.getAlarmInfo());

                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmInfoIndex(alarmInfoIndex);
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("alarm_info", alarmInfo);

                // ObjectMapper를 통해 JSON 값을 String으로 변환
                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    strBody = objectMapper.writeValueAsString(responseBody);
                } catch (JsonProcessingException e) {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            } else {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
            }
        } else {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }
    
    private String getProcessCode(InterfaceAlarmControlHistoryDTO detailSearchDTO) {
    	StringBuilder procCd = new StringBuilder();
    	procCd.append(detailSearchDTO.getProcess());
    	procCd.append(detailSearchDTO.getProcessStep());
    	return procCd.toString();
    }
}
