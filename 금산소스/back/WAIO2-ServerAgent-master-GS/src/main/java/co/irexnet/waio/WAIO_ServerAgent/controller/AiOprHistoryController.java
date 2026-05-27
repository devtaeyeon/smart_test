package co.irexnet.waio.WAIO_ServerAgent.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import co.irexnet.waio.WAIO_ServerAgent.dao.AiOprRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableSwagger2
@Slf4j
public class AiOprHistoryController {

	@Autowired
    DatabaseServiceImpl databaseService;
	
    /**
     *     AI 운영시간 이력 검색 조회 (상세페이지)
     * @param dateSearchDTO 조회 날짜
     * @return ResponseEntity<String> 메시지
     */
	@RequestMapping(value="/aioprhistory/search", method = RequestMethod.PUT)
	public ResponseEntity<String> getAiOprHistorySearch(@RequestBody InterfaceDateSearchDTO dateSearchDTO){
		
        log.debug("getAiOprHisSearch, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());
       
        //aiOprHistorySearch
        LinkedHashMap<String, Integer> aiOprHistorySearch = new LinkedHashMap<>();

        //set aiOprHistorySearch keyList by using rtList ( List is fixed in Rt table ) 
        List<AiOprRealtimeDTO> aiOprRtList = databaseService.getAllAiOprRt();
        
        if(aiOprRtList == null) {
            String strErrorBody = "{\"reason\":\"Empty aiOprRtList\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);   
        }
        
        for (AiOprRealtimeDTO dto : aiOprRtList) {
        	String key = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_" + dto.getAi_opr();
        	aiOprHistorySearch.put(key, 0);

        	//Key for Sum Data by Process
        	String sumKey = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_" + "SUM";
        	aiOprHistorySearch.put(sumKey, 0);
        }
       
        //get aiOprHis between search time && set aiOprHistorySearch
        List<AiOprHistoryDTO> aiOprHisList = databaseService.getApiOprHistory(dateSearchDTO);
       
        if(aiOprHisList != null) {
	        for (AiOprHistoryDTO dto : aiOprHisList) {
	        	String key = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_" + dto.getAi_opr();
	        	aiOprHistorySearch.put(key, dto.getOpr_minutes());
	        	
	        	//Sum up Data by Process
	        	String sumKey = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_" + "SUM";
	        	int originValue = (int) aiOprHistorySearch.get(sumKey);
	        	aiOprHistorySearch.put(sumKey, originValue + dto.getOpr_minutes());
	        }
        }
        
        //make response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("aiOprHistorySearch", aiOprHistorySearch);
        
        ObjectMapper objectMapper = new ObjectMapper();
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
     * AI 운영시간 이력 실시간 포함 전체 조회 (대시보드 팝업)
     * @return ResponseEntity<String> 메시지
     */
	@RequestMapping(value="/aioprhistory/total", method = RequestMethod.GET)
	public ResponseEntity<String> getAiOprHistoryTotal(){
        
		log.debug("getgetAiOprHisAll");
        
		//aiOprTotal Map to put all operation minutes
        LinkedHashMap<String, Integer> aiOprTotal = new LinkedHashMap<>();
        
        //get RT
        List<AiOprRealtimeDTO> aiOprRtList = databaseService.getAllAiOprRt();
        
        if(aiOprRtList == null) {
            String strErrorBody = "{\"reason\":\"Empty aiOprRtList\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);   
        }
        
        //add RT to aiOprTotal
        for (AiOprRealtimeDTO dto : aiOprRtList) {
        	String key = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_" + dto.getAi_opr();
        	aiOprTotal.put(key, dto.getOpr_minutes());
        	//Sum up Data by Process (only ai)
        	if(dto.getAi_opr() !=  0) {
	        	String sumKey = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_" + "SUM";
	        	int originValue = (int) aiOprTotal.getOrDefault(sumKey, 0);
	        	aiOprTotal.put(sumKey, originValue + dto.getOpr_minutes());
        	}
        }
		
        //add totalList 
        
        //set start/end date to get all history data
		Calendar calendar = Calendar.getInstance();
		calendar.set(2024, Calendar.JANUARY, 1); //start day before service
		Date startDate = calendar.getTime();
		Date endDate = new Date();
		
		InterfaceDateSearchDTO dateSearchDTO = new InterfaceDateSearchDTO();
		dateSearchDTO.setStart_time(startDate);
		dateSearchDTO.setEnd_time(endDate);
		
		// get history
        List<AiOprHistoryDTO> aiOprHisList = databaseService.getApiOprHistory(dateSearchDTO);
        
        //add history to aiOprTotal
        if(aiOprHisList != null) {
	        for (AiOprHistoryDTO dto : aiOprHisList) {
	        	String key = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_" + dto.getAi_opr();
	        	int rtValue = (int) aiOprTotal.get(key);
	        	aiOprTotal.put(key, rtValue + dto.getOpr_minutes());
	        	//Sum up Data by Process (only ai)
	        	if(dto.getAi_opr() !=  0) {
		        	String sumKey = dto.getProc_cd() + "_" + dto.getDisinfection_index() + "_" + "SUM";
		        	int originValue = (int) aiOprTotal.get(sumKey);
		        	aiOprTotal.put(sumKey, originValue + dto.getOpr_minutes());
	        	}
	        }
        }
        
        
        //make response body
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
        
	}
}
