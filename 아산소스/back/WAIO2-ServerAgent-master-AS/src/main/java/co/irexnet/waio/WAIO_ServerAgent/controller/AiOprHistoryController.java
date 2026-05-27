package co.irexnet.waio.WAIO_ServerAgent.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprRealTimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableSwagger2
@Slf4j
public class AiOprHistoryController {
    @Autowired
    DatabaseServiceImpl databaseService;
    
    /**
     * 대시보드 팝업에서 조회되는 현재까지의 누적운영시간 조회
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/aioprhistory/total", method = RequestMethod.GET)
    public ResponseEntity<String> getAiOprTotal() {
    	log.debug("Recv getDashboardAiOpr");
    	
    	// key 형태
    	// "B1_0" : 234, "B1_1" : 454, "B1_2" : 32
    	// "G1_PRE_0": 23, "G1_PRE_1" : 45, "G1_PRE_2" : 56
    	LinkedHashMap<String, Object> aiOprTotal = new LinkedHashMap<>();
    	
    	// 오늘 현재까지의 누적시간 조회
    	List<AiOprRealTimeDTO> aiOprRealTimeList = databaseService.getAllAiOprRealTime();
    	
    	// 이력테이블의 누적시간 조회
    	List<AiOprHistoryDTO> aiOprHistoryList = databaseService.getAllAiOprHistory();

    	if (aiOprRealTimeList != null && aiOprHistoryList != null) {
    		for (AiOprRealTimeDTO dto : aiOprRealTimeList) {
    			if (dto.getDisinfection_index().equals(CommonValue.NONE)) {
    				// 각 공정 모드별 운영시간
    				String key = dto.getProc_cd() + "_" + dto.getAi_opr();
    				aiOprTotal.put(key, dto.getOpr_minutes());
    				// 각 공정 모든 모드 합산한 총 운영시간
    				String sumKey = dto.getProc_cd() + "_SUM";
    				if (dto.getAi_opr() != 0) {
    					// 동일한 key 값이 있을 경우 기존의 opr_minutes와 합산
    					aiOprTotal.put(sumKey, (int)aiOprTotal.getOrDefault(sumKey, 0) + dto.getOpr_minutes());
    				}
    			} else {
    				// 소독 공정일 경우
    				String key = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_" + dto.getAi_opr();
    				aiOprTotal.put(key, dto.getOpr_minutes());
    				String sumKey = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_SUM";
    				if (dto.getAi_opr() != 0) {
    					aiOprTotal.put(sumKey, (int)aiOprTotal.getOrDefault(sumKey, 0) + dto.getOpr_minutes());
    				}
    			}
    		}
    		
    		for (AiOprHistoryDTO dto : aiOprHistoryList) {
    			if (dto.getDisinfection_index().equals(CommonValue.NONE)) {
    				String key = dto.getProc_cd() + "_" + dto.getAi_opr();
    				// 동일한 key 값이 있을 경우 기존의 opr_minutes와 합산
    				aiOprTotal.put(key, (int)aiOprTotal.getOrDefault(key, 0) + dto.getOpr_minutes());
    				String sumKey = dto.getProc_cd() + "_SUM";
    				if (dto.getAi_opr() != 0) {
    					aiOprTotal.put(sumKey, (int)aiOprTotal.getOrDefault(sumKey, 0) + dto.getOpr_minutes());
    				}
    			} else {
    				// 소독 공정일 경우
    				String key = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_" + dto.getAi_opr();
    				// 동일한 key 값이 있을 경우 기존의 opr_minutes와 합산
    				aiOprTotal.put(key, (int)aiOprTotal.getOrDefault(key, 0) + dto.getOpr_minutes());
    				String sumKey = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_SUM";
    				if (dto.getAi_opr() != 0) {
    					aiOprTotal.put(sumKey, (int)aiOprTotal.getOrDefault(sumKey, 0) + dto.getOpr_minutes());
    				}
    			}
    		}
    		
    		Map<String, Object> responseBody = new HashMap<>();
    		responseBody.put("aiOprTotal", aiOprTotal);
    		
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
            String strErrorBody = "{\"reason\":\"Empty aiOprRealTimeList or aiOprHistoryList\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 누적운영시간 상세페이지 조회
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/aioprhistory/search", method = RequestMethod.PUT)
    public ResponseEntity<String> getAiOprSearch(@RequestBody InterfaceDateSearchDTO dateSearchDTO) {
    	log.debug("getAiOprSearch, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());
    	
    	LinkedHashMap<String, Object> aiOprSearch = new LinkedHashMap<>();
    	
    	// 이력테이블의 누적시간 조회
    	List<AiOprHistoryDTO> aiOprHistoryList = databaseService.getAiOprHistoryBySearchDate(dateSearchDTO);
    	
    	if (aiOprHistoryList != null) {
    		for (AiOprHistoryDTO dto : aiOprHistoryList) {
    			if (dto.getDisinfection_index().equals(CommonValue.NONE)) {
    				String key = dto.getProc_cd() + "_" + dto.getAi_opr();
    				aiOprSearch.put(key, dto.getOpr_minutes());
    				String sumKey = dto.getProc_cd() + "_SUM";
    				aiOprSearch.put(sumKey, (int)aiOprSearch.getOrDefault(sumKey, 0) + dto.getOpr_minutes());
    			} else {
    				String key = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_" + dto.getAi_opr();
    				aiOprSearch.put(key, dto.getOpr_minutes());
    				String sumKey = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_SUM";
    				aiOprSearch.put(sumKey, (int)aiOprSearch.getOrDefault(sumKey, 0) + dto.getOpr_minutes());
    			}
    		}
    		
    		Map<String, Object> responseBody = new HashMap<>();
    		responseBody.put("aiOprHistorySearch", aiOprSearch);
    		
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
            String strErrorBody = "{\"reason\":\"Empty aiOprHistoryList\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

}
