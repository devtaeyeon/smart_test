package co.irexnet.waio.WAIO_ServerAgent.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiMixingRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceMixingAiDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceOperationModeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageRangeDTO;
import co.irexnet.waio.WAIO_ServerAgent.kafka.KafkaProducer;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableSwagger2
@Slf4j
public class MixingController
{
    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    KafkaProducer kafkaProducer;

    /** 
     * 혼화응집 공정 최근 데이터 조회
     * @param processStep 공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/mixing/latest/{processStep}", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestMixing(@PathVariable int processStep) {
        log.debug("Recv getLatestMixing");

        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());
//        strPartitionName = "20180801"; // FIXME 현재 날짜 수정

        ResponseEntity<String> result = null;

        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("d_operation_mode", CommonValue.D_OPERATION_MODE);
        paramMap.put("processStep", processStep);
        paramMap.put("strPartitionName", strPartitionName);
        result = databaseService.getMixingLatest(paramMap);

        return result;
    }

    /** 
     * 응집기 설정 속도 예측 이력 조회
     * @param dateSearchDTO Front-end 시간 검색 값을 저장하기 위한 DTO
     * @param processStep 공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/mixing/history/fc/sp/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> getFcAgHistoryMixing(@RequestBody InterfaceDateSearchDTO dateSearchDTO, @PathVariable int processStep)
    {
        log.debug("getSpFcHistoryMixing, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 혼화응집 공정 데이터 조회
        List<AiMixingRealtimeDTO> aiMixingRealtimeList =
                databaseService.getAiMixingRealtimeValueFromUpdateTime(dateSearchDTO, processStep);
        log.debug("getAiMixingRealtimeValueFromUpdateTime, result:[{}]", aiMixingRealtimeList.size());

        if(aiMixingRealtimeList.size() > 0)
        {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> d_fc_sp = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp, locationMapTemp;
            LinkedHashMap<String, Object> locationMap, stepMap;
            ObjectMapper objectMapper = new ObjectMapper();

            ArrayList<String> keyList;
            Object objectTemp;
            String strDate;

            int nLocationCount = 0, nStepCount = 0;

            try
            {
                // aiMixinfRealtimeList에서 응집기 속도 예측 이력을 조회하여 JSON Map 처리
                mapTemp = objectMapper.readValue(aiMixingRealtimeList.get(0).getAi_d_loc_fc_sp(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));

                // 전송할 운영지 수 저장
                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                ArrayList<String> locationKeyList = new ArrayList<>(mapTemp.keySet());
                nLocationCount = nLocationCount > locationKeyList.size() ? nLocationCount : locationKeyList.size();

                // 지별 단 수 저장
                objectTemp = mapTemp.get(locationKeyList.get(0));
                locationMapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                ArrayList<String> stepKeyList = new ArrayList<>(locationMapTemp.keySet());
                nStepCount = nStepCount > stepKeyList.size() ? nStepCount : stepKeyList.size();
                for(int i = 0; i < nLocationCount; i++)
                {
                    locationMap = new LinkedHashMap<>();
                    for(int j = 0; j < nStepCount; j++)
                    {
                        stepMap = new LinkedHashMap<>();
                        for(AiMixingRealtimeDTO dto : aiMixingRealtimeList)
                        {
                            strDate = simpleDateFormat.format(dto.getUpd_ti());

                            /*
                            * JSON 구조
                            * {                                         // d_fc_location_sp
                                "ai_d_fc_location_sp2": {               // root
                                    "location2": {                      // location
                                        "step1": {                      // step
                                            "1": 24.565403479393588,    // first
                                            "2": 24.565403479393588,
                                            "3": 24.565403479393588
                                        },
                                        "step2": {
                                            "1": 18.746911491287346,
                                            "2": 18.746911491287346,
                                            "3": 18.746911491287346
                                        },
                                        "step3": {
                                            "1": 11.809814204194506,
                                            "2": 11.809814204194506,
                                            "3": 11.809814204194506
                                        }
                                    }...
                                }
                            * */
                            // d_fc_location_sp
                            mapTemp = objectMapper.readValue(dto.getAi_d_loc_fc_sp(), LinkedHashMap.class);
                            keyList = new ArrayList<>(mapTemp.keySet());

                            // root
                            objectTemp = mapTemp.get(keyList.get(0));
                            mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);

                            // location
                            objectTemp = mapTemp.get(locationKeyList.get(i));
                            locationMapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);

                            // step -- 금산의 경우 first데이터가 없고, step의 value값이 상수
                            objectTemp = locationMapTemp.get(stepKeyList.get(j));
                            stepMap.put(strDate, objectTemp);

                        }
                        locationMap.put(stepKeyList.get(j), stepMap);

                    }
                    d_fc_sp.put(locationKeyList.get(i), locationMap);
                }
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("fc_sp", d_fc_sp);

                // ObjectMapper를 통해 JSON 값을 String으로 변환
                String strBody = "";
                strBody = objectMapper.writeValueAsString(responseBody);
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_mixing_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /** 
     * 혼화응집 공정 제어모드 변경
     * @param operationMode 제어모드
     * @param processStep 공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/mixing/control/operation/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlMixing(@RequestBody InterfaceOperationModeDTO operationMode, @PathVariable int processStep)
    {
        log.info("putOperationControlMixing, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_mixing_init's operation_mode
//        log.debug("update aiMixingOperationMode:[{}], mode:[{}]",
//                databaseService.modAiMixingOperationMode(nOperationMode), nOperationMode);

        // update operation mode
        databaseService.modAiMixingOperationMode(nOperationMode, processStep);
        
        // send control value to kafka ai_control(d_operation_mode)
        AiProcessInitDTO aiMixingInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE, processStep);
        log.debug("getAiMixingInit, result:[{}]", aiMixingInit != null ? 1 : 0);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            if(aiMixingInit != null) {
	            // Kafka에 전송할 값 정의
	            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
	            controlMap.put("tag", aiMixingInit.getTag_sn());
	            controlMap.put("value", nOperationMode);
	            controlMap.put("time", strDate);
	
	            // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
	            ObjectMapper objectMapper = new ObjectMapper();
	            String strBody = objectMapper.writeValueAsString(controlMap);
	            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
	            log.info("send to kafka:[{}]", strBody);
	
	            // Kafka에 혼화응집 공정 제어모드 변경 알람 전송
	            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI, CommonValue.PROCESS_MIXING, processStep);
	            for(TagManageDTO dto : tagManageList)
	            {
	                if(dto.getItm().equalsIgnoreCase("d_operation_mode_a") == true)
	                {
	                    // Kafka에 전송할 값 정의
	                    controlMap = new LinkedHashMap<>();
	                    controlMap.put("tag", dto.getTag_sn());
	                    controlMap.put("value", nOperationMode == CommonValue.OPERATION_MODE_MANUAL ? false : true);
	                    controlMap.put("time", strDate);
	
	                    // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
	                    objectMapper = new ObjectMapper();
	                    strBody = objectMapper.writeValueAsString(controlMap);
	                    kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
	
	                    break;
	                }
	            }
            }else {
                log.error("Does not exist aiMixingInit:[{}]", CommonValue.D_OPERATION_MODE);
            }
        }
        catch(JsonProcessingException e)
        {
            log.error("JsonProcessingException Occurred in /mixing/control/operation API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    /** 
     * 혼화응집 알고리즘 설정값 변경
     * @param mixingAi Front-end 혼화응집 알고리즘 설정값을 저장하기 위한 DTO
     * @param processStep 공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/mixing/control/ai/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putAiControlMixing(@RequestBody InterfaceMixingAiDTO mixingAi, @PathVariable int processStep)
    {
        log.debug("putAiControlMixing, ai:[{}]", mixingAi);

        boolean result = true;

        float d_g_value_ctr_flag = mixingAi.getD_g_value_ctr_flag(); // G값 제어 여부
        

        if(d_g_value_ctr_flag == 0)  {// 수동인 경우 G값 변경        	
        	
        	// update 응집기 1단 교반강도 G 값
        	result = (databaseService.modAiMixingInit("d_g_value_loc1", mixingAi.getD_g_value_loc1(), processStep) == 1) && result;
        	
        	// update 응집기 2단 교반강도 G 값
        	result = (databaseService.modAiMixingInit("d_g_value_loc2", mixingAi.getD_g_value_loc2(), processStep) == 1) && result;
        	
        }	

        
	    // G값 제어여부 업데이트
	    result = (databaseService.modAiMixingInit("d_g_value_ctr_flag", d_g_value_ctr_flag, processStep) == 1) && result;
        
        if(result == true)
        {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        {
            String strErrorBody = "{\"reason\":\"ai_mixing_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 혼화응집 교반강도 한계값 설정값 변경
     * @param mixingAi
     * @param processStep
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @RequestMapping(value = "/mixing/glimit/control/ai/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putAiGLimitMixing(@RequestBody InterfaceMixingAiDTO mixingAi, @PathVariable int processStep) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        log.debug("putAiGLimitControlMixing, ai:[{}]", mixingAi);

        boolean result = true;

        for (int n = 1; n <= 2; n++) { //1~2열 G값 상,하한값 업데이트
            // update 교반강도 n열 상한값
        	result = (databaseService.modAiMixingInit("d_g_step"+n+"_max", (float)mixingAi.getClass().getMethod("getD_g_step"+n+"_max").invoke(mixingAi), processStep) == 1) && result;
            // update 교반강도 n열 하한값
        	result = (databaseService.modAiMixingInit("d_g_step"+n+"_min", (float)mixingAi.getClass().getMethod("getD_g_step"+n+"_min").invoke(mixingAi), processStep) == 1) && result;
        }

        
        if(result == true)
        {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        {
            String strErrorBody = "{\"reason\":\"ai_mixing_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 혼화응집 교반강도 보정계수 설정값 변경
     * @param mixingAi
     * @param processStep
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @RequestMapping(value = "/mixing/gcrt/control/ai/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putAiGCrtMixing(@RequestBody InterfaceMixingAiDTO mixingAi, @PathVariable int processStep) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        log.debug("putAiGCrtControlMixing, ai:[{}]", mixingAi);

        boolean result = true;

        for (int n = 1; n <= 2; n++) { //1~2열 보정계수 설정값 업데이트
            // update 교반강도 n열 보정계수
        	result = (databaseService.modAiMixingInit("d_g_step"+n+"_crt", (float)mixingAi.getClass().getMethod("getD_g_step"+n+"_crt").invoke(mixingAi), processStep) == 1) && result;
        }

        
        if(result == true)
        {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        {
            String strErrorBody = "{\"reason\":\"ai_mixing_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
