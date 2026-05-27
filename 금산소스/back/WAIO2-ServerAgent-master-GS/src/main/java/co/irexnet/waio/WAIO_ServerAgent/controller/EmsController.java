package co.irexnet.waio.WAIO_ServerAgent.controller;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiEmsRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageDTO;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@EnableSwagger2
@Slf4j
public class EmsController
{
    @Autowired
    DatabaseServiceImpl databaseService;

    // 실시간 EMS 요약 항목 조회
    @RequestMapping(value = "/ems/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestEms()
    {
        log.debug("Recv getLatestEms");
        
        // tag_manage에서 EMS로 등록된 태그 조회
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_EMS, 0);
        log.debug("getTagManageFromCode[{}], result:[{}]", CommonValue.PROCESS_EMS, tagManageList.size());
        if(tagManageList == null || tagManageList.size() == 0)
        {
            String strErrorBody = "{\"reason\":\"Empty tag_manage\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // 실시간 EMS 데이터 테이블 최근 값 조회
        List<ProcessRealtimeDTO> emsRealtimeList = databaseService.getLatestEmsRealtimeValue();
        log.debug("getLatestEmsRealtimeValue, result:[{}]", emsRealtimeList.size());
        if(emsRealtimeList == null || emsRealtimeList.size() == 0)
        {
            String strErrorBody = "{\"reason\":\"Empty ems_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
        
        // 예측 EMS 데이터 테이블 최근 값 조회
        List<AiEmsRealtimeDTO> aiEmsRealtimeList = databaseService.getLatestAiEmsRealtimeValue();
        log.debug("getLatestAiEmsRealtimeValue, result:[{}]", aiEmsRealtimeList.size());
        if(aiEmsRealtimeList == null || aiEmsRealtimeList.size() == 0)
        {
            String strErrorBody = "{\"reason\":\"Empty ai_ems_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Make Response Body
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedHashMap<String, Object> latestMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> pumpMap = new LinkedHashMap<>();
        
        // EMS 데이터를 처리
        processEmsRealtime(tagManageList, emsRealtimeList, pumpMap);
        
        // AI EMS 데이터를 처리
        processAiEmsRealtime(tagManageList, aiEmsRealtimeList, pumpMap);

        // 펌프 상태를 latestMap에 등록
        latestMap.put("pump", pumpMap);

        try
        {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", latestMap);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            String strBody = objectMapper.writeValueAsString(responseBody);
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        }
        catch(JsonProcessingException e)
        {
            String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 태그와 값을 파싱하는 메서드
    private Object parseValue(String itm, String tagVal) {
    	if (itm != null && tagVal != null) {
    		try {
    			if (itm.startsWith("h1_pm") || itm.startsWith("h2_pm") || itm.startsWith("h3_pm") || itm.startsWith("h4_pm") 
    					|| itm.startsWith("ai_h1_pm") || itm.startsWith("ai_h2_pm") || itm.startsWith("ai_h3_pm") || itm.startsWith("ai_h4_pm")) {
    				return Float.parseFloat(tagVal);
    			} else if (itm.startsWith("h1_pm_sp") || itm.startsWith("h2_pm_sp") || itm.startsWith("h3_pm_sp") || itm.startsWith("h4_pm_sp")
    					|| itm.startsWith("ai_h1_pm_sp") || itm.startsWith("ai_h2_pm_sp") || itm.startsWith("ai_h3_pm_sp") || itm.startsWith("ai_h4_pm_sp")) {
    				return Float.parseFloat(tagVal);
    			} else if (itm.equalsIgnoreCase("h1_operation_mode") || itm.equalsIgnoreCase("h2_operation_mode")
    					|| itm.equalsIgnoreCase("h3_operation_mode") || itm.equalsIgnoreCase("h4_operation_mode")) {
    				return Float.parseFloat(tagVal);
    			}
    		} catch (NumberFormatException e) {
    			log.error("Failed to parse value. Item: {}, Value: {}, Error: {}", itm, tagVal, e.getMessage());
    		}
        } else {
            log.warn("Null input detected in parseValue. itm: {}, tagVal: {}", itm, tagVal);
        }
    	return null;
    }
    
    // EMS 데이터를 처리하는 메서드
    private void processEmsRealtime (List<TagManageDTO> tagManageList, List<ProcessRealtimeDTO> emsRealtimeList, Map<String, Object> pumpMap) {
    	if (tagManageList != null && emsRealtimeList != null && pumpMap != null) {
    		for (TagManageDTO tagManage : tagManageList) {
    			for (ProcessRealtimeDTO emsRealtime : emsRealtimeList) {
    				if (tagManage.getTag_sn() != null && emsRealtime.getTag_sn() != null 
    						&& tagManage.getTag_sn().equalsIgnoreCase(emsRealtime.getTag_sn())) {
    					Object value = parseValue(tagManage.getItm(), emsRealtime.getTag_val());
    					if (value != null) {
    						pumpMap.put(tagManage.getItm(), value);
    						break;
    					}
    				}
    			}
    		}
    	}
    }
    
    // AI EMS 데이터를 처리하는 메서드
    private void processAiEmsRealtime (List<TagManageDTO> tagManageList, List<AiEmsRealtimeDTO> aiEmsRealtimeList, Map<String, Object> pumpMap) {
    	if (tagManageList != null && aiEmsRealtimeList != null && pumpMap != null) {
    		for (TagManageDTO tagManage : tagManageList) {
    			for (AiEmsRealtimeDTO aiEmsRealtime : aiEmsRealtimeList) {
    				if (tagManage.getTag_sn() != null && aiEmsRealtime.getTag_sn() != null 
    						&& tagManage.getTag_sn().equalsIgnoreCase(aiEmsRealtime.getTag_sn())) {
    					Object value = parseValue(tagManage.getItm(), aiEmsRealtime.getTag_val());
    					if (value != null) {
    						pumpMap.put(tagManage.getItm(), value);
    						break;
    					}
    				}
    			}
    		}
    	}
    }
}
