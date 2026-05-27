package co.irexnet.waio.WAIO_ServerAgent.controller;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.*;
import co.irexnet.waio.WAIO_ServerAgent.dto.*;
import co.irexnet.waio.WAIO_ServerAgent.kafka.KafkaProducer;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@EnableSwagger2
@Slf4j
public class SedimentationController
{
    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    KafkaProducer kafkaProducer;

    // 침전 공정 최근 데이터 조회
    @RequestMapping(value = "/sedimentation/latest/{processStep}", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestSedimentation(@PathVariable int processStep) {
        log.debug("Recv getLatestSedimentation");
        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());
        strPartitionName = "20231101"; // FIXME 현재 날짜 수정

        // get ai_sedimentation_realtime
        AiSedimentationRealtimeDTO aiSedimentationRealtime = databaseService.getLatestAiSedimentationRealtimeValue(processStep);
        log.debug("getLatestAiSedimentationRealtimeValue, result:[{}]", aiSedimentationRealtime != null ? 1 : 0);

        // get ai_sedimentation_init(e_operation_mode)
        AiProcessInitDTO aiSedimentationInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE, processStep);
        log.debug("getAiSedimentationInit, result:[{}]", aiSedimentationInit != null ? 1 : 0);

        // get ai_sedimentation_init
        List<AiProcessInitDTO> aiSedimentationInitList = databaseService.getAllAiSedimentationInit(processStep);
        log.debug("getAllAiSedimentationInit, result:[{}]", aiSedimentationInitList.size());

        // get sedimentation_realtime
        List<ProcessRealtimeDTO> sedimentationRealtime = databaseService.getLatestSedimentationRealtimeValue(strPartitionName, processStep);
        log.debug("getLatestSedimentationRealtimeValue, result:[{}]", sedimentationRealtime.size());

        // get tag_manage
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_SEDIMENTATION, processStep);
        log.debug("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_SEDIMENTATION, tagManageList.size());

        if(aiSedimentationRealtime != null)
        {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiSedimentationInfo = new LinkedHashMap<>();

            // update_time
            aiSedimentationInfo.put("upd_ti", aiSedimentationRealtime.getUpd_ti());

            // operation mode
            if(aiSedimentationInit != null)
            {
                aiSedimentationInfo.put("ai_opr", aiSedimentationInit.getInit_val().intValue());
            }
            else
            {
                aiSedimentationInfo.put("ai_opr", aiSedimentationRealtime.getAi_opr());
            }

            for(AiProcessInitDTO dto : aiSedimentationInitList)
            {
                if(dto.getItm().equalsIgnoreCase("e_sc_set_sludge_q") == true)
                {
                    // 슬러지 수집기 운행 기준 적산 슬러지 양
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val());
                }
                else if(dto.getItm().equalsIgnoreCase("e_sc_set_max_wait") == true)
                {
                    // 슬러지 수집기 운행 대기 최대 일
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val());
                }
                else if(dto.getItm().equalsIgnoreCase("e_set_lt") == true)
                {
                    // 슬러지 수집기 편도 운전 거리
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val());
                }
                else if(dto.getItm().equalsIgnoreCase("e_sc_set_ti") == true)
                {
                    // 슬러지 수집기 운전 시간
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val());
                }
                else if(dto.getItm().equalsIgnoreCase("e_low_hour") == true)
                {
                    // 대차 운전주기 최소기준시간
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val());
                }
                else if(dto.getItm().equalsIgnoreCase("e_sc_set1") == true)
                {
                    // 1지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                }
                else if(dto.getItm().equalsIgnoreCase("e_sc_set2") == true)
                {
                    // 2지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                }
                else if(dto.getItm().equalsIgnoreCase("e_sc_set3") == true)
                {
                    // 3지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                }
                else if(dto.getItm().equalsIgnoreCase("e_sc_set4") == true)
                {
                    // 4지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                }
            }

            Float c1_mm_fr = 0.0f;
            Float c2_mm_fr = 0.0f;
            // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiSedimenationInfo에 등록
            for(TagManageDTO tagManage : tagManageList)
            {
                for(ProcessRealtimeDTO dto : sedimentationRealtime)
                {
                    if(tagManage.getItm().equalsIgnoreCase("b_in_fr") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {
                        // 원수 유입 유량
                        aiSedimentationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("b_tb") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {
                        // 원수 탁도
                        aiSedimentationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("c1_mm_fr") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                      {
 					 	// 1호기 약품 사용량
                     	c1_mm_fr = Float.parseFloat(dto.getTag_val());
                      }
                      else if(tagManage.getItm().equalsIgnoreCase("c2_mm_fr") == true &&
                              tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
 					 {
 					 	// 2호기 약품 사용량
                     	c2_mm_fr = Float.parseFloat(dto.getTag_val());
 					 }
                }
            }

            // // 약품 사용량
            // aiSedimentationInfo.put("mm_fr", d1_mm_fr_pacs + d2_mm_fr_pacs);

            // // AI 슬러지 발생량 예측
            // aiSedimentationInfo.put("ai_e1_sludge", aiSedimentationRealtime.getAIE_5300());
//            aiSedimentationInfo.put("ai_e2_sludge", aiSedimentationRealtime.getAIE_5302());
            aiSedimentationInfo.put("mm_fr", c1_mm_fr+c2_mm_fr);//약품사용량 = 약품1사용량  + 약품2사용량

            if(processStep == 1 || processStep == 2) { //1단계만 해당, 2단계는 AIE5300 데이터 없음
            // AI 슬러지 발생량 예측
            	aiSedimentationInfo.put("ai_e_sludge", aiSedimentationRealtime.getAIE_5300());
            }
            try
            {
            	String strTemp;
            	LinkedHashMap<String, Object> mapTemp;
            	ArrayList<String> keyList;
            	Object objectTemp;
            	LinkedHashMap<String, Float> aiSludgeMap;
            	
            	if(processStep == 1 || processStep == 2) {// 1단계만 아래 해당, 2단계는 AIE5200 데이터 없음
	                // AI 총 슬러지 발생량 예측
	                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
	            	strTemp = aiSedimentationRealtime.getAIE_5200();
	                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
	
	                keyList = new ArrayList<>(mapTemp.keySet());
	                objectTemp = mapTemp.get(keyList.get(0));
	                aiSludgeMap = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
	                 aiSedimentationInfo.put("ai_e_total_sludge", aiSludgeMap);
	                
            	}
                 
                List<LinkedHashMap<String, Object>> locationMap;
                Map<String, String> scScheduleMap = new HashMap<>();
                 
            	// 1지 데이터를 추출하여 location1Dto에 저장
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                if (aiSedimentationRealtime.getAIE_9001() != null) {
                	
	                strTemp = aiSedimentationRealtime.getAIE_9001();
	                strTemp = strTemp.replaceAll("NaN", "\"\"");
	                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
	                keyList = new ArrayList<>(mapTemp.keySet());
	                objectTemp = mapTemp.get(keyList.get(0));
	
	                locationMap = objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
	                mapTemp.clear();
	
	                for(Map<String, Object> map : locationMap)
	                {
	                    mapTemp.putAll(map);
	                }
	                AiSedimentationLocation1RealtimeDTO location1Dto =
	                        objectMapper.convertValue(mapTemp, AiSedimentationLocation1RealtimeDTO.class);
	
	                //1지 운영모드
	                aiSedimentationInfo.put("e_operation_mode_1", location1Dto.getE_operation_mode());
	                
	                // 1지 인발밸브 상태
	                aiSedimentationInfo.put("e_drawing_vv1_1", location1Dto.getE_drn_vv1().getE_drn_vv1_1());
	                aiSedimentationInfo.put("e_drawing_vv1_2", location1Dto.getE_drn_vv1().getE_drn_vv1_2());
	                aiSedimentationInfo.put("e_drawing_vv1_3", location1Dto.getE_drn_vv1().getE_drn_vv1_3());
	                aiSedimentationInfo.put("e_drawing_vv1_4", location1Dto.getE_drn_vv1().getE_drn_vv1_4());
	                aiSedimentationInfo.put("e_drawing_vv1_5", location1Dto.getE_drn_vv1().getE_drn_vv1_5());
	                aiSedimentationInfo.put("e_drawing_vv1_6", location1Dto.getE_drn_vv1().getE_drn_vv1_6());
	
	                // 1지 수집기 대차 위치
	                aiSedimentationInfo.put("e_sc1_f", location1Dto.getE_loc_sc_1().getF());
	                aiSedimentationInfo.put("e_sc1_b", location1Dto.getE_loc_sc_1().getB());
	
	                // 1지 수집기 운영 스케쥴
	                scScheduleMap = new HashMap<>();
	                scScheduleMap.put("latest", location1Dto.getAIE_6001().getLatest());
	                scScheduleMap.put("start", location1Dto.getAIE_6001().getStart());
	                scScheduleMap.put("stop", location1Dto.getAIE_6001().getStop());
	                scScheduleMap.put("next_start", location1Dto.getAIE_6001().getNext_start());
	                scScheduleMap.put("next_end", location1Dto.getAIE_6001().getNext_end());
	                scScheduleMap.put("inbal", location1Dto.getAIE_6001().getInbal());
	                aiSedimentationInfo.put("e_sc1_schedule", scScheduleMap);
                }
                
                if (aiSedimentationRealtime.getAIE_9002() != null) {
	                // 2지 데이터를 추출하여 location2Dto에 저장
	                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
	                strTemp = aiSedimentationRealtime.getAIE_9002();
	                strTemp = strTemp.replaceAll("NaN", "\"\"");
	                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
	                keyList = new ArrayList<>(mapTemp.keySet());
	                objectTemp = mapTemp.get(keyList.get(0));
	
	                locationMap = objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
	                mapTemp.clear();
	
	                for(Map<String, Object> map : locationMap)
	                {
	                    mapTemp.putAll(map);
	                }
	                AiSedimentationLocation2RealtimeDTO location2Dto =
	                        objectMapper.convertValue(mapTemp, AiSedimentationLocation2RealtimeDTO.class);
	
	                //2지 운영모드
	                aiSedimentationInfo.put("e_operation_mode_2", location2Dto.getE_operation_mode());
	                
	                // 2지 인발밸브 상태
	                aiSedimentationInfo.put("e_drawing_vv2_1", location2Dto.getE_drn_vv2().getE_drn_vv2_1());
	                aiSedimentationInfo.put("e_drawing_vv2_2", location2Dto.getE_drn_vv2().getE_drn_vv2_2());
	                aiSedimentationInfo.put("e_drawing_vv2_3", location2Dto.getE_drn_vv2().getE_drn_vv2_3());
	                aiSedimentationInfo.put("e_drawing_vv2_4", location2Dto.getE_drn_vv2().getE_drn_vv2_4());
	                aiSedimentationInfo.put("e_drawing_vv2_5", location2Dto.getE_drn_vv2().getE_drn_vv2_5());
	                aiSedimentationInfo.put("e_drawing_vv2_6", location2Dto.getE_drn_vv2().getE_drn_vv2_6());
	
	                // 2지 수집기 대차 위치
	                aiSedimentationInfo.put("e_sc2_f", location2Dto.getE_loc_sc_2().getF());
	                aiSedimentationInfo.put("e_sc2_b", location2Dto.getE_loc_sc_2().getB());
	
	                // 2지 수집기 운영 스케쥴
	                scScheduleMap = new HashMap<>();
	                scScheduleMap.put("latest", location2Dto.getAIE_6002().getLatest());
	                scScheduleMap.put("start", location2Dto.getAIE_6002().getStart());
	                scScheduleMap.put("stop", location2Dto.getAIE_6002().getStop());
	                scScheduleMap.put("next_start", location2Dto.getAIE_6002().getNext_start());
	                scScheduleMap.put("next_end", location2Dto.getAIE_6002().getNext_end());
	                scScheduleMap.put("inbal", location2Dto.getAIE_6002().getInbal());
	                aiSedimentationInfo.put("e_sc2_schedule", scScheduleMap);
               }
                
                if (aiSedimentationRealtime.getAIE_9003() != null) {
	                // 3지 데이터를 추출하여 location3Dto에 저장
	                strTemp = aiSedimentationRealtime.getAIE_9003();
	                strTemp = strTemp.replaceAll("NaN", "\"\"");
	                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
	                keyList = new ArrayList<>(mapTemp.keySet());
	                objectTemp = mapTemp.get(keyList.get(0));
	
	                locationMap = objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
	                mapTemp.clear();
	                for(Map<String, Object> map : locationMap)
	                {
	                    mapTemp.putAll(map);
	                }
	                AiSedimentationLocation3RealtimeDTO location3Dto =
	                        objectMapper.convertValue(mapTemp, AiSedimentationLocation3RealtimeDTO.class);
	
	                //3지 운영모드
	                aiSedimentationInfo.put("e_operation_mode_3", location3Dto.getE_operation_mode());
	                
	                // 3지 인발밸브 상태
	                aiSedimentationInfo.put("e_drawing_vv3_1", location3Dto.getE_drn_vv3().getE_drn_vv3_1());
	                aiSedimentationInfo.put("e_drawing_vv3_2", location3Dto.getE_drn_vv3().getE_drn_vv3_2());
	                aiSedimentationInfo.put("e_drawing_vv3_3", location3Dto.getE_drn_vv3().getE_drn_vv3_3());
	                aiSedimentationInfo.put("e_drawing_vv3_4", location3Dto.getE_drn_vv3().getE_drn_vv3_4());
	                aiSedimentationInfo.put("e_drawing_vv3_5", location3Dto.getE_drn_vv3().getE_drn_vv3_5());
	                aiSedimentationInfo.put("e_drawing_vv3_6", location3Dto.getE_drn_vv3().getE_drn_vv3_6());
	
	                // 3지 수집기 대차 위치
	                aiSedimentationInfo.put("e_sc3_f", location3Dto.getE_loc_sc_3().getF());
	                aiSedimentationInfo.put("e_sc3_b", location3Dto.getE_loc_sc_3().getB());
	
	                // 3지 수집기 운영 스케쥴
	                scScheduleMap = new HashMap<>();
	                scScheduleMap.put("latest", location3Dto.getAIE_6003().getLatest());
	                scScheduleMap.put("start", location3Dto.getAIE_6003().getStart());
	                scScheduleMap.put("stop", location3Dto.getAIE_6003().getStop());
	                scScheduleMap.put("next_start", location3Dto.getAIE_6003().getNext_start());
	                scScheduleMap.put("next_end", location3Dto.getAIE_6003().getNext_end());
	                scScheduleMap.put("inbal", location3Dto.getAIE_6003().getInbal());
	                aiSedimentationInfo.put("e_sc3_schedule", scScheduleMap);
                }
                
                if (aiSedimentationRealtime.getAIE_9004() != null) {
	                // 4지 데이터를 추출하여 location4Dto에 저장
	                strTemp = aiSedimentationRealtime.getAIE_9004();
	                strTemp = strTemp.replaceAll("NaN", "\"\"");
	                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
	                keyList = new ArrayList<>(mapTemp.keySet());
	                objectTemp = mapTemp.get(keyList.get(0));
	
	                locationMap = objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
	                mapTemp.clear();
	                for(Map<String, Object> map : locationMap)
	                {
	                    mapTemp.putAll(map);
	                }
	                AiSedimentationLocation4RealtimeDTO location4Dto =
	                        objectMapper.convertValue(mapTemp, AiSedimentationLocation4RealtimeDTO.class);
	                
	                //4지 운영모드
	                aiSedimentationInfo.put("e_operation_mode_4", location4Dto.getE_operation_mode());
	                
	                // 4지 인발밸브 상태
	                aiSedimentationInfo.put("e_drawing_vv4_1", location4Dto.getE_drn_vv4().getE_drn_vv4_1());
	                aiSedimentationInfo.put("e_drawing_vv4_2", location4Dto.getE_drn_vv4().getE_drn_vv4_2());
	                aiSedimentationInfo.put("e_drawing_vv4_3", location4Dto.getE_drn_vv4().getE_drn_vv4_3());
	                aiSedimentationInfo.put("e_drawing_vv4_4", location4Dto.getE_drn_vv4().getE_drn_vv4_4());
	                aiSedimentationInfo.put("e_drawing_vv4_5", location4Dto.getE_drn_vv4().getE_drn_vv4_5());
	                aiSedimentationInfo.put("e_drawing_vv4_6", location4Dto.getE_drn_vv4().getE_drn_vv4_6());
	
	                // 4지 수집기 대차 위치
	                aiSedimentationInfo.put("e_sc4_f", location4Dto.getE_loc_sc_4().getF());
	                aiSedimentationInfo.put("e_sc4_b", location4Dto.getE_loc_sc_4().getB());
	
	                // 4지 수집기 운영 스케쥴
	                scScheduleMap = new HashMap<>();
	                scScheduleMap.put("latest", location4Dto.getAIE_6004().getLatest());
	                scScheduleMap.put("start", location4Dto.getAIE_6004().getStart());
	                scScheduleMap.put("stop", location4Dto.getAIE_6004().getStop());
	                scScheduleMap.put("next_start", location4Dto.getAIE_6004().getNext_start());
	                scScheduleMap.put("next_end", location4Dto.getAIE_6004().getNext_end());
	                scScheduleMap.put("inbal", location4Dto.getAIE_6004().getInbal());
	                aiSedimentationInfo.put("e_sc4_schedule", scScheduleMap);
                }

            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiSedimentationInfo);


            String strBody = "";
            try
            {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        }
        else
        {
            // FIXME 실시간 데이터만 노출하기 위한 분기 처리. 추후 삭제 필요
        	// JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiSedimentationInfo = new LinkedHashMap<>();
            Float mm_fr = 0.0f;
            
            // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiSedimenationInfo에 등록
            for(TagManageDTO tagManage : tagManageList)
            {
                for(ProcessRealtimeDTO dto : sedimentationRealtime)
                {
                    if(tagManage.getItm().equalsIgnoreCase("b_in_fr") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {
                        // 원수 유입 유량
                        aiSedimentationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("b_tb") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {
                        // 원수 탁도
                        aiSedimentationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("mm_fr") == true &&
                          tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
	                  {
	                      // 약품 사용량
	                      mm_fr = Float.parseFloat(dto.getTag_val());
	                  }
                }
            }

            // 약품 사용량
            aiSedimentationInfo.put("mm_fr", mm_fr);
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiSedimentationInfo);

            String strBody = "";
            try
            {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
            
            // TODO AI테이블 데이터 입력 시 주석 해제
//            String strErrorBody = "{\"reason\":\"Empty ai_sedimentation\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 실시간 침전 공정 지별 세부 항목 조회
    @RequestMapping(value = "/sedimentation/location/{locationNumber}/{processStep}", method = RequestMethod.GET)
    public ResponseEntity<String> getLocationSedimentation(@PathVariable int locationNumber, @PathVariable int processStep)
    {
        log.debug("getLocationSedimentation, locationNumber:[{}]", locationNumber);

        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_SEDIMENTATION, processStep);
        if(tagManageList.size() > 0)
        {
            // Get series number
            // Front-end로부터 전달받은 지 번호가 tag_manage에 등록되었는지 검사
            int nLocNumber = 0;
            for(TagManageDTO dto : tagManageList)
            {
                if(dto.getLoc() == locationNumber)
                {
                	nLocNumber = dto.getLoc();
                    break;
                }
            }

            // 등록되지 않은 지 번호는 에러처리
            if(nLocNumber == 0)
            {
                String strErrorBody = "{\"reason\":\"Invalid location number.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // Get ai_sedimentation_realtime
            AiSedimentationRealtimeDTO aiSedimentationRealtime = databaseService.getLatestAiSedimentationRealtimeValue(processStep);
            log.debug("getLatestAiSedimentationRealtimeValue, result:[{}]", aiSedimentationRealtime != null ? 1 : 0);
            if(aiSedimentationRealtime != null)
            {
                // JSON 처리를 위한 ObjectMapper 선언
                ObjectMapper objectMapper = new ObjectMapper();

                // Make ResponseBody
                LinkedHashMap<String, Object> aiSedimentationLocationInfo = new LinkedHashMap<>();

                // update_time
                aiSedimentationLocationInfo.put("upd_ti", aiSedimentationRealtime.getUpd_ti());

                // 원수 유입 유량
                aiSedimentationLocationInfo.put("b_in_fr", aiSedimentationRealtime.getB_in_fr());

                // 원수 탁도
                aiSedimentationLocationInfo.put("b_tb", aiSedimentationRealtime.getB_tb());
                
                // 약품 사용량 (1호기 주입량 + 2호기 주입량)
                aiSedimentationLocationInfo.put("d_mm_fr", aiSedimentationRealtime.getC_mm_fr1() + aiSedimentationRealtime.getC_mm_fr2());

                // 지별 데이터 추출
//                if(nSeriesNumber == 1)
//                {
//                    // 혼화기 약품 종류
//                    aiSedimentationLocationInfo.put("d_mm_coagulant",
//                            aiSedimentationRealtime.getFRI_2053() > aiSedimentationRealtime.getFRI_2055() ? "APAC" : "POLYMAX");
//
//                    // 혼화기 유량
//                    aiSedimentationLocationInfo.put("d_mm_fr",
//                            aiSedimentationRealtime.getFRI_2053() > aiSedimentationRealtime.getFRI_2055()
//                                    ? aiSedimentationRealtime.getFRI_2053() : aiSedimentationRealtime.getFRI_2055());
//
//                    // 계면계 수위
//                    aiSedimentationLocationInfo.put("e_interface_le", aiSedimentationRealtime.getAIE_9901());
//
//                    // 침전지 전탁도
//                    aiSedimentationLocationInfo.put("e_tb_f", aiSedimentationRealtime.getAIE_9903());
//
//                    // 침전지 후탁도
//                    aiSedimentationLocationInfo.put("e_ser_tb_b", aiSedimentationRealtime.getTBI_2001());
//                }
//                else
//                {
//                    // 혼화기 약품 종류
//                    aiSedimentationLocationInfo.put("d_mm_coagulant",
//                            aiSedimentationRealtime.getFRI_2054() > aiSedimentationRealtime.getFRI_2056() ? "APAC" : "POLYMAX");
//
//                    // 혼화기 유량
//                    aiSedimentationLocationInfo.put("d_mm_fr",
//                            aiSedimentationRealtime.getFRI_2054() > aiSedimentationRealtime.getFRI_2056()
//                                    ? aiSedimentationRealtime.getFRI_2054() : aiSedimentationRealtime.getFRI_2056());
//
//                    // 계면계 수위
//                    aiSedimentationLocationInfo.put("e_interface_le", aiSedimentationRealtime.getAIE_9902());
//
//                    // 침전지 전탁도
//                    aiSedimentationLocationInfo.put("e_tb_f", aiSedimentationRealtime.getAIE_9904());
//
//                    // 침전지 후탁도
//                    aiSedimentationLocationInfo.put("e_ser_tb_b", aiSedimentationRealtime.getTBI_2002());
//                }

                // 지별 데이터
                String strTemp;
                LinkedHashMap<String, Object> mapTemp;
                ArrayList<String> keyList;
                Object objectTemp;
                try
                {
                	if(locationNumber == 1)
                    {
                        // 1지 데이터를 추출하여 location2Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9001();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap =
                                objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                        mapTemp.clear();
                        for(Map<String, Object> map : locationMap)
                        {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation1RealtimeDTO location1Dto =
                                objectMapper.convertValue(mapTemp, AiSedimentationLocation1RealtimeDTO.class);

                        if(processStep ==1 || processStep == 2) { //500# 은 1단계일 경우만 데이터가 있음
                        	// 슬러지 발생량
                        	aiSedimentationLocationInfo.put("e_location_sludge", location1Dto.getAIE_5001());                        	
                        }

                        // 수집기 위치
                        aiSedimentationLocationInfo.put("e_sc_f", location1Dto.getE_loc_sc_1().getF());
                        aiSedimentationLocationInfo.put("e_sc_b", location1Dto.getE_loc_sc_1().getB());

                        // 이전 대차 시작 시간
                        aiSedimentationLocationInfo.put("e_sc_latest", location1Dto.getAIE_6001().getStart());

                        // AI 다음 대차 시작 시간 예측
                        aiSedimentationLocationInfo.put("ai_e_sc_next", location1Dto.getAIE_6001().getNext_start());

                        // 인발밸브 상태
                        aiSedimentationLocationInfo.put("e_drawing_vv_1", location1Dto.getE_drn_vv1().getE_drn_vv1_1());
                        aiSedimentationLocationInfo.put("e_drawing_vv_2", location1Dto.getE_drn_vv1().getE_drn_vv1_2());
                        aiSedimentationLocationInfo.put("e_drawing_vv_3", location1Dto.getE_drn_vv1().getE_drn_vv1_3());
                        aiSedimentationLocationInfo.put("e_drawing_vv_4", location1Dto.getE_drn_vv1().getE_drn_vv1_4());
                        aiSedimentationLocationInfo.put("e_drawing_vv_5", location1Dto.getE_drn_vv1().getE_drn_vv1_5());
                        aiSedimentationLocationInfo.put("e_drawing_vv_6", location1Dto.getE_drn_vv1().getE_drn_vv1_6());
                        
                        if(processStep == 1 || processStep == 2) { //510# 은 1단계일 경우만 데이터가 있음
                        	// 슬러지 발생량 트렌드
                        	objectTemp = location1Dto.getAIE_5101();
                        	LinkedHashMap<String, Float> location1SludgeMap =
                        			objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                        	aiSedimentationLocationInfo.put("e_location_sludge_trend", location1SludgeMap);                        	
                        }
                    }
                	else if(locationNumber == 2)
                    {
                        // 2지 데이터를 추출하여 location2Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9002();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap =
                                objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                        mapTemp.clear();
                        for(Map<String, Object> map : locationMap)
                        {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation2RealtimeDTO location2Dto =
                                objectMapper.convertValue(mapTemp, AiSedimentationLocation2RealtimeDTO.class);

                        if(processStep ==1 || processStep == 2) { //500# 은 1단계일 경우만 데이터가 있음
                        	// 슬러지 발생량
                        	aiSedimentationLocationInfo.put("e_location_sludge", location2Dto.getAIE_5002());
                        }
                        // 수집기 위치
                        aiSedimentationLocationInfo.put("e_sc_f", location2Dto.getE_loc_sc_2().getF());
                        aiSedimentationLocationInfo.put("e_sc_b", location2Dto.getE_loc_sc_2().getB());

                        // 이전 대차 시작 시간
                        aiSedimentationLocationInfo.put("e_sc_latest", location2Dto.getAIE_6002().getStart());

                        // AI 다음 대차 시작 시간 예측
                        aiSedimentationLocationInfo.put("ai_e_sc_next", location2Dto.getAIE_6002().getNext_start());

                        // 인발밸브 상태
                        aiSedimentationLocationInfo.put("e_drawing_vv_1", location2Dto.getE_drn_vv2().getE_drn_vv2_1());
                        aiSedimentationLocationInfo.put("e_drawing_vv_2", location2Dto.getE_drn_vv2().getE_drn_vv2_2());
                        aiSedimentationLocationInfo.put("e_drawing_vv_3", location2Dto.getE_drn_vv2().getE_drn_vv2_3());
                        aiSedimentationLocationInfo.put("e_drawing_vv_4", location2Dto.getE_drn_vv2().getE_drn_vv2_4());
                        aiSedimentationLocationInfo.put("e_drawing_vv_5", location2Dto.getE_drn_vv2().getE_drn_vv2_5());
                        aiSedimentationLocationInfo.put("e_drawing_vv_6", location2Dto.getE_drn_vv2().getE_drn_vv2_6());

                        if(processStep == 1 || processStep == 2) { //510# 은 1단계일 경우만 데이터가 있음
	                        // 슬러지 발생량 트렌드
	                        objectTemp = location2Dto.getAIE_5102();
	                        LinkedHashMap<String, Float> location2SludgeMap =
	                                objectMapper.convertValue(objectTemp, LinkedHashMap.class);
	                        aiSedimentationLocationInfo.put("e_location_sludge_trend", location2SludgeMap);
                        }
                    }
                    else if(locationNumber == 3)
                    {
                        // 3지 데이터를 추출하여 location3Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9003();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap =
                                objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                        mapTemp.clear();
                        for(Map<String, Object> map : locationMap)
                        {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation3RealtimeDTO location3Dto =
                                objectMapper.convertValue(mapTemp, AiSedimentationLocation3RealtimeDTO.class);

                        if(processStep ==1 || processStep == 2) { //500# 은 1단계일 경우만 데이터가 있음
                        	// 슬러지 발생량
                        	aiSedimentationLocationInfo.put("e_location_sludge", location3Dto.getAIE_5003());
                        }
                        // 수집기 위치
                        aiSedimentationLocationInfo.put("e_sc_f", location3Dto.getE_loc_sc_3().getF());
                        aiSedimentationLocationInfo.put("e_sc_b", location3Dto.getE_loc_sc_3().getB());

                        // 이전 대차 시작 시간
                        aiSedimentationLocationInfo.put("e_sc_latest", location3Dto.getAIE_6003().getStart());

                        // AI 다음 대차 시작 시간 예측
                        aiSedimentationLocationInfo.put("ai_e_sc_next", location3Dto.getAIE_6003().getNext_start());

                        // 인발밸브 상태
                        aiSedimentationLocationInfo.put("e_drawing_vv_1", location3Dto.getE_drn_vv3().getE_drn_vv3_1());
                        aiSedimentationLocationInfo.put("e_drawing_vv_2", location3Dto.getE_drn_vv3().getE_drn_vv3_2());
                        aiSedimentationLocationInfo.put("e_drawing_vv_3", location3Dto.getE_drn_vv3().getE_drn_vv3_3());
                        aiSedimentationLocationInfo.put("e_drawing_vv_4", location3Dto.getE_drn_vv3().getE_drn_vv3_4());
                        aiSedimentationLocationInfo.put("e_drawing_vv_5", location3Dto.getE_drn_vv3().getE_drn_vv3_5());
                        aiSedimentationLocationInfo.put("e_drawing_vv_6", location3Dto.getE_drn_vv3().getE_drn_vv3_6());

                        if(processStep == 1 || processStep == 2 ) { //510# 은 1단계일 경우만 데이터가 있음
	                        // 슬러지 발생량 트렌드
	                        objectTemp = location3Dto.getAIE_5103();
	                        LinkedHashMap<String, Float> locationSludgeMap =
	                                objectMapper.convertValue(objectTemp, LinkedHashMap.class);
	                        aiSedimentationLocationInfo.put("e_location_sludge_trend", locationSludgeMap);
                        }
                    }
                    else if(locationNumber == 4)
                    {
                        // 4지 데이터를 추출하여 location4Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9004();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap =
                                objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                        mapTemp.clear();
                        for(Map<String, Object> map : locationMap)
                        {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation4RealtimeDTO location4Dto =
                                objectMapper.convertValue(mapTemp, AiSedimentationLocation4RealtimeDTO.class);
                        
                        if(processStep ==1 || processStep == 2) { //500# 은 1단계일 경우만 데이터가 있음
                        	// 슬러지 발생량
                        	aiSedimentationLocationInfo.put("e_location_sludge", location4Dto.getAIE_5004());
                        }
                        // 수집기 위치
                        aiSedimentationLocationInfo.put("e_sc_f", location4Dto.getE_loc_sc_4().getF());
                        aiSedimentationLocationInfo.put("e_sc_b", location4Dto.getE_loc_sc_4().getB());

                        // 이전 대차 시작 시간
                        aiSedimentationLocationInfo.put("e_sc_latest", location4Dto.getAIE_6004().getStart());

                        // AI 다음 대차 시작 시간 예측
                        aiSedimentationLocationInfo.put("ai_e_sc_next", location4Dto.getAIE_6004().getNext_start());

                        // 인발밸브 상태
                        aiSedimentationLocationInfo.put("e_drawing_vv_1", location4Dto.getE_drn_vv4().getE_drn_vv4_1());
                        aiSedimentationLocationInfo.put("e_drawing_vv_2", location4Dto.getE_drn_vv4().getE_drn_vv4_2());
                        aiSedimentationLocationInfo.put("e_drawing_vv_3", location4Dto.getE_drn_vv4().getE_drn_vv4_3());
                        aiSedimentationLocationInfo.put("e_drawing_vv_4", location4Dto.getE_drn_vv4().getE_drn_vv4_4());
                        aiSedimentationLocationInfo.put("e_drawing_vv_5", location4Dto.getE_drn_vv4().getE_drn_vv4_5());
                        aiSedimentationLocationInfo.put("e_drawing_vv_6", location4Dto.getE_drn_vv4().getE_drn_vv4_6());

                        if(processStep == 1 || processStep == 2) { //510# 은 1단계일 경우만 데이터가 있음
	                        // 슬러지 발생량 트렌드
	                        objectTemp = location4Dto.getAIE_5104();
	                        LinkedHashMap<String, Float> locationSludgeMap =
	                                objectMapper.convertValue(objectTemp, LinkedHashMap.class);
	                        aiSedimentationLocationInfo.put("e_location_sludge_trend", locationSludgeMap);
                        }
                    }

                }
                catch(JsonProcessingException e)
                {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }


                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("location", aiSedimentationLocationInfo);

                String strBody;
                try
                {
                    // ObjectMapper를 통해 JSON 값을 String으로 변환
                    strBody = objectMapper.writeValueAsString(responseBody);
                }
                catch(JsonProcessingException e)
                {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Empty ai_sedimentation\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }


        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty tag_manage\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 침전 공정 계면계 측정 이력 조회
    // locationNumber가 0일 경우 전체 조회
//    @RequestMapping(value = "/sedimentation/history/interface/{locationNumber}/{processStep}", method = RequestMethod.PUT)
//    public ResponseEntity<String> getInterfaceHistorySedimentation(
//            @PathVariable int locationNumber, @RequestBody InterfaceDateSearchDTO dateSearchDTO, @PathVariable int processStep)
//    {
//        log.debug("getInterfaceHistorySedimentation, location:[{}], start:[{}], end:[{}]",
//                locationNumber, dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());
//
//        // 침전 공정 데이터 조회
//        List<AiSedimentationInterfaceRealtimeDTO> aiSedimentationInterfaceRealtimeList =
//                databaseService.getAiSedimentationInterfaceRealtimeValueFromUpdateTime(dateSearchDTO, processStep);
//        log.debug("getAiSedimentationInterfaceRealtimeValueFromUpdateTime, result:[{}]", aiSedimentationInterfaceRealtimeList.size());
//        if(aiSedimentationInterfaceRealtimeList.size() > 0)
//        {
//            // Make Response Body
//            LinkedHashMap<String, Object> seriesInterfaceInfo = new LinkedHashMap<>();
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String strDate;
//
//            // 계열별 계면계 측정 이력 정보를 분리 저장하기 위한 변수 선언
//            LinkedHashMap<String, Float> series1InterfaceMap = new LinkedHashMap<>();
//            LinkedHashMap<String, Float> series2InterfaceMap = new LinkedHashMap<>();
//            for(AiSedimentationInterfaceRealtimeDTO dto : aiSedimentationInterfaceRealtimeList)
//            {
//                strDate = simpleDateFormat.format(dto.getUpdate_time());
//                series1InterfaceMap.put(strDate, dto.getAIE_9901());
//                series2InterfaceMap.put(strDate, dto.getAIE_9902());
//            }
//
//            // Whole sedimentation interface history
//            if(locationNumber == 0)
//            {
//                seriesInterfaceInfo.put("series1", series1InterfaceMap);
//                seriesInterfaceInfo.put("series2", series2InterfaceMap);
//            }
//            else
//            {
//                List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_SEDIMENTATION, processStep);
//                if(tagManageList.size() > 0)
//                {
//                    // Get series number
//                    int nSeriesNumber = 0;
//                    for(TagManageDTO dto : tagManageList)
//                    {
//                        if(dto.getLoc() == locationNumber)
//                        {
//                            nSeriesNumber = dto.getSer();
//                            break;
//                        }
//                    }
//
//                    if(nSeriesNumber == 1)
//                    {
//                        seriesInterfaceInfo.put("series1", series1InterfaceMap);
//                    }
//                    else if(nSeriesNumber == 2)
//                    {
//                        seriesInterfaceInfo.put("series2", series2InterfaceMap);
//                    }
//                    else
//                    {
//                        String strErrorBody = "{\"reason\":\"Invalid location number.\"}";
//                        return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//                    }
//                }
//                else
//                {
//                    String strErrorBody = "{\"reason\":\"Empty tag_manage\"}";
//                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//                }
//            }
//
//            Map<String, Object> responseBody = new HashMap<>();
//            responseBody.put("interface", seriesInterfaceInfo);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            String strBody;
//            try
//            {
//                // ObjectMapper를 통해 JSON 값을 String으로 변환
//                strBody = objectMapper.writeValueAsString(responseBody);
//            }
//            catch(JsonProcessingException e)
//            {
//                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//            return new ResponseEntity<>(strBody, HttpStatus.OK);
//        }
//        else
//        {
//            String strErrorBody = "{\"reason\":\"Empty ai_sedimentation_realtime\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//    }

    // 잔류 염소 측정 이력 조회 
    @RequestMapping(value = "/sedimentation/history/cl/{processStep}/{disinfectionStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> getClHistorySedimentation(@RequestBody InterfaceDateSearchDTO dateSearchDTO, @PathVariable int processStep, @PathVariable int disinfectionStep)
    {
        log.debug("getClHistorySedimentation, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 소독 공정 데이터 조회
        List<AiDisinfectionRealtimeDTO> aiDisinfectionRealtimeList =
                databaseService.getAiDisinfectionRealtimeValueFromUpdateTime(dateSearchDTO, processStep, disinfectionStep);
        log.debug("getAiDisinfectionRealtimeValueFromUpdateTime, result:[{}]", aiDisinfectionRealtimeList.size());
        if(aiDisinfectionRealtimeList.size() > 0)
        {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> series1 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> series2 = new LinkedHashMap<>();
            // aiDisinfectionRealtimeList에서 계열별 침전지 잔류염소 를 조회하여 계열별 map에 저장
            for(AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList)
            {
                String strDate = simpleDateFormat.format(dto.getUpd_ti());
                //disinfectionStep에 따른 데이터 구분 (전차염: 침전지 잔류염소, 중차염: 여과지 잔류염소, 후차염: 정수지유입잔류염소)
                if(disinfectionStep == 1) {
                	series1.put(strDate, dto.getG_e_residual_cl());
                }else if (disinfectionStep == 2) {
                	series1.put(strDate, dto.getG_f_out_residual_cl());
                }else {
                	series1.put(strDate, dto.getG_h_in_residual_cl());
                }
//                series2.put(strDate, dto.getD_ser_cl());
            }

            LinkedHashMap<String, Object> seriesClInfo = new LinkedHashMap<>();
            seriesClInfo.put("series1", series1);
            seriesClInfo.put("series2", series2);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("cl", seriesClInfo);

            ObjectMapper objectMapper = new ObjectMapper();
            String strBody;
            try
            {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 침전수 탁도 측정 이력 조회
    @RequestMapping(value = "/sedimentation/history/tb/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> getTbHistorySedimentation(@RequestBody InterfaceDateSearchDTO dateSearchDTO, @PathVariable int processStep)
    {
        log.debug("getTbHistorySedimentation, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 약품 공정 데이터 조회
        List<AiCoagulantRealtimeDTO> aiCoagulantRealtimeList =
                databaseService.getAiCoagulantRealtimeValueFromUpdateTime(dateSearchDTO, processStep);
        log.debug("getAiCoagulantRealtimeValueFromUpdateTime, result:[{}]", aiCoagulantRealtimeList.size());
        if(aiCoagulantRealtimeList.size() > 0)
        {
            // Make Response Body
            // 계열별 데이터를 분리 저장하기 위한 변수 선언
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> series1 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> series2 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp;
            ObjectMapper objectMapper = new ObjectMapper();

            try
            {
                // aiCoagulantRealtimeList에서 침전지 탁도 정보를 조회하여 계열별 map에 등록
                for(AiCoagulantRealtimeDTO dto : aiCoagulantRealtimeList)
                {
                    String strDate = simpleDateFormat.format(dto.getUpd_ti());
                    
                    // 1호기 데이터만 put
                    series1.put(strDate, dto.getC_tb());

//                  mapTemp = objectMapper.readValue(dto.getC_tb(), LinkedHashMap.class);
//					ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet()); 
//					Object objectTemp = mapTemp.get(keyList.get(0));
//					  
//					JsonCSeriesFloat e_ser_tb_b = objectMapper.convertValue(objectTemp, JsonCSeriesFloat.class); 
//					series1.put(strDate, e_ser_tb_b.getSeries1());
//					series2.put(strDate, e_ser_tb_b.getSeries2());
                    
                }

                LinkedHashMap<String, Object> seriesTbInfo = new LinkedHashMap<>();
                seriesTbInfo.put("series1", series1);
                seriesTbInfo.put("series2", series2);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("tb", seriesTbInfo);

                // ObjectMapper를 통해 JSON 값을 String으로 변환
                String strBody = objectMapper.writeValueAsString(responseBody);
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            }
            catch(JsonProcessingException e)
            {
                log.error("JsonProcessingException Occurred in /sedimentation/history/tb API");
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_coagulant_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 테스트 코드
//    @RequestMapping(value = "/sedimentation/distribution/tb2", method = RequestMethod.GET)
//    public ResponseEntity<String> getTb2DistributionSedimentation()
//    {
//        log.debug("Recv getTbDistributionSedimentation");
//
//        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_SEDIMENTATION);
//        log.debug("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_SEDIMENTATION, tagManageList.size());
//
//        // Get start_time(before 24 hours)
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, -1);
//        Date startTime = calendar.getTime();
//
//        // Get distribution data
//        List<FrequencyDTO> series1 = new ArrayList<>();
//        List<FrequencyDTO> series2 = new ArrayList<>();
//
//        for(TagManageDTO dto : tagManageList)
//        {
//            if(dto.getItm().equalsIgnoreCase("e1_tb_b") == true)
//            {
//                series1 = databaseService.getDistribution(startTime, dto.getName());
//                log.debug("getDistribution[{}], result:[{}]", dto.getName(), series1.size());
//            }
//            else if(dto.getItm().equalsIgnoreCase("e2_tb_b") == true)
//            {
//                series2 = databaseService.getDistribution(startTime, dto.getName());
//                log.debug("getDistribution[{}], result:[{}]", dto.getName(), series2.size());
//            }
//        }
//
//        if(series1.size() + series2.size() == 0)
//        {
//            String strErrorBody = "{\"reason\":\"Empty TB Trend\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//
//        // Make series body
//        LinkedHashMap<String, Object> seriesInfo = new LinkedHashMap<>();
//        Map<String, Integer> countBody = new LinkedHashMap<>();
//        for(FrequencyDTO dto : series1)
//        {
//            if(dto.getValue() == null)
//            {
//                continue;
//            }
//            countBody.put(dto.getValue(), dto.getCount());
//        }
//        if(countBody.isEmpty() == true)
//        {
//            seriesInfo.put("series1", null);
//        }
//        else
//        {
//            seriesInfo.put("series1", countBody);
//        }
//
//        countBody = new LinkedHashMap<>();
//        for(FrequencyDTO dto : series2)
//        {
//            if(dto.getValue() == null)
//            {
//                continue;
//            }
//            countBody.put(dto.getValue(), dto.getCount());
//        }
//        if(countBody.isEmpty() == true)
//        {
//            seriesInfo.put("series2", null);
//        }
//        else
//        {
//            seriesInfo.put("series2", countBody);
//        }
//
//        Map<String, Object> responseBody = new HashMap<>();
//        responseBody.put("tb", seriesInfo);
//
//        String strBody = "";
//        ObjectMapper objectMapper = new ObjectMapper();
//        try
//        {
//            strBody = objectMapper.writeValueAsString(responseBody);
//        }
//        catch(JsonProcessingException e)
//        {
//            String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(strBody, HttpStatus.OK);
//    }

    // 침전지 탁도 정규분포 데이터 조회
    @RequestMapping(value = "/sedimentation/distribution/tb/{processStep}", method = RequestMethod.GET)
    public ResponseEntity<String> getTbDistributionSedimentation(@PathVariable int processStep)
    {
        // Use coagulant_realtime table
        log.debug("Recv getTbDistributionSedimentation");

        // get tag_manage
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_COAGULANT, processStep);
        log.debug("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_COAGULANT, tagManageList.size());

        // Get start_time(before seven days)
        Calendar calendar = Calendar.getInstance(); // TODO
        calendar.add(Calendar.DATE, -7); //TODO        
        Date startTime = calendar.getTime();

//        Calendar calendar = Calendar.getInstance();
//        calendar.set(2018, 7, 1, 0, 0);
//        Date startTime = calendar.getTime();
//        
        // Make Response Body
        int nTotalSize = 0;
        LinkedHashMap<String, Object> seriesTbInfo = new LinkedHashMap<>();
        for(TagManageDTO tagManage: tagManageList)
        {
            if(tagManage.getItm().equalsIgnoreCase("e1_tb_b") == true)
            {
                // 1계열 침전지 탁도 정규분포 데이터를 조회하여 series1에 저장
                List<FrequencyDTO> frequencyList = databaseService.getCoagulantDistribution(startTime, tagManage.getTag_sn(), processStep);
                nTotalSize += frequencyList.size();

                Map<String, Integer> countBody = new LinkedHashMap<>();
                for(FrequencyDTO dto : frequencyList)
                {
                    if(dto.getValue() == null)
                    {
                        continue;
                    }
                    countBody.put(dto.getValue(), dto.getCount());
                }
                if(countBody.isEmpty() == true)
                {
                    seriesTbInfo.put("series1", null);
                }
                else
                {
                    seriesTbInfo.put("series1", countBody);
                }
            }
            else if(tagManage.getItm().equalsIgnoreCase("e2_tb_b") == true)
            {
                // 2계열 침전지 탁도 정규분포 데이터를 조회하여 series2에 저장
                List<FrequencyDTO> frequencyList = databaseService.getCoagulantDistribution(startTime, tagManage.getTag_sn(), processStep);
                nTotalSize += frequencyList.size();

                Map<String, Integer> countBody = new LinkedHashMap<>();
                for(FrequencyDTO dto : frequencyList)
                {
                    if(dto.getValue() == null)
                    {
                        continue;
                    }
                    countBody.put(dto.getValue(), dto.getCount());
                }
                if(countBody.isEmpty() == true)
                {
                    seriesTbInfo.put("series2", null);
                }
                else
                {
                    seriesTbInfo.put("series2", countBody);
                }
            }
        }

        if(nTotalSize == 0)
        {
            String strErrorBody = "{\"reason\":\"Empty TB Trend\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("tb", seriesTbInfo);

        String strBody = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            // ObjectMapper를 통해 JSON 값을 String으로 변환
            strBody = objectMapper.writeValueAsString(responseBody);
        }
        catch(JsonProcessingException e)
        {
            String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(strBody, HttpStatus.OK);
    }

    // 테스트 코드
    @RequestMapping(value = "/sedimentation/distribution/tbtest", method = RequestMethod.GET)
    public ResponseEntity<String> getTbTestDistributionSedimentation()
    {
        // Use sedimentation_realtime Table
        log.debug("Recv getTbDistributionSedimentation");

        // Get start_time(before one month)
        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MONTH, -1);
        calendar.add(Calendar.DATE, -7);
        Date startTime = calendar.getTime();

        // Make Body
        // get TBI_2001 distinct count
        int nTotalSize = 0;
        List<FrequencyDTO> frequencyList = databaseService.getDistributionE1Tb(startTime, 2);
        log.debug("getDistributionE1Tb, result:[{}]", frequencyList.size());
        nTotalSize += frequencyList.size();

        LinkedHashMap<String, Object> seriesTbInfo = new LinkedHashMap<>();

        Map<String, Integer> countBody = new LinkedHashMap<>();
        for(FrequencyDTO dto : frequencyList)
        {
            if(dto.getValue() == null)
            {
                continue;
            }
            countBody.put(dto.getValue(), dto.getCount());
        }
        if(countBody.isEmpty() == true)
        {
            seriesTbInfo.put("series1", null);
        }
        else
        {
            seriesTbInfo.put("series1", countBody);
        }

        // get TBI_2002 distinct count
        frequencyList = databaseService.getDistributionE2Tb(startTime, 2);
        log.debug("getDistributionE2Tb, result:[{}]", frequencyList.size());
        nTotalSize += frequencyList.size();

        countBody = new LinkedHashMap<>();
        for(FrequencyDTO dto : frequencyList)
        {
            if(dto.getValue() == null)
            {
                continue;
            }
            countBody.put(dto.getValue(), dto.getCount());
        }
        if(countBody.isEmpty() == true)
        {
            seriesTbInfo.put("series2", null);
        }
        else
        {
            seriesTbInfo.put("series2", countBody);
        }

        if(nTotalSize == 0)
        {
            String strErrorBody = "{\"reason\":\"Empty TB Trend\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("tb", seriesTbInfo);

        String strBody = "";
        ObjectMapper objectMapper = new ObjectMapper();
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

    // 침전 공정 제어모드 변경
    @RequestMapping(value = "/sedimentation/control/operation/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlSedimentation(@RequestBody InterfaceOperationModeDTO operationMode, @PathVariable int processStep)
    {
        log.info("putOperationControlSedimentation, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_sedimentation_init's operation_mode
//        log.debug("update aiSedimentationOperationMode:[{}], mode:[{}]",
//                databaseService.modAiSedimentationOperationMode(nOperationMode), nOperationMode);

        // send control value to kafka ai_control(e_operation_mode)
        AiProcessInitDTO aiSedimentationInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE, processStep);
        log.debug("getAiSedimentationInit, result:[{}]", aiSedimentationInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            if(aiSedimentationInit != null) {
	            // Kafka에 전송할 값 정의
	            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
	            controlMap.put("tag", aiSedimentationInit.getTag_sn());
	            controlMap.put("value", nOperationMode);
	            controlMap.put("time", strDate);
	
	            // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
	            ObjectMapper objectMapper = new ObjectMapper();
	            String strBody = objectMapper.writeValueAsString(controlMap);
	            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
	            log.info("send to kafka:[{}]", strBody);
	
	            // Kafka에 침전 공정 제어모드 변경 알람 전송
	            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI, CommonValue.PROCESS_SEDIMENTATION, processStep);
	            for(TagManageDTO dto : tagManageList)
	            {
	                if(dto.getItm().equalsIgnoreCase("e_operation_mode_a") == true)
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
                log.error("Does not exist aiSedimentationInit:[{}]", CommonValue.E_OPERATION_MODE);
            }
        }
        catch(JsonProcessingException e)
        {
            log.error("JsonProcessingException Occurred in /sedimentation/control/operation API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 침전 알고리즘 설정값 변경
    @RequestMapping(value = "/sedimentation/control/sc/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putScControlSedimentation(@RequestBody InterfaceSedimentationScDTO sedimentationSc, @PathVariable int processStep)
    {
        log.debug("putScControlSedimentation, sc:[{}]", sedimentationSc);

        boolean result = true;

        // update 슬러지 수집기 운행 기준 적산 슬러지 양
        result = (databaseService.modAiSedimentationInit("e_sc_set_sludge_q", sedimentationSc.getE_sc_set_sludge_q(), processStep) == 1) && result;

        // update 슬러지 수집기 운행 대기 최대 일
        result = (databaseService.modAiSedimentationInit("e_sc_set_max_wait", sedimentationSc.getE_sc_set_max_wait(), processStep) ==1) && result;

        // update 슬러지 수집기 편도 운전 거리
        result = (databaseService.modAiSedimentationInit("e_set_lt", sedimentationSc.getE_set_lt(), processStep) == 1) && result;

        // update 슬러지 수집기 운전 시간
        result = (databaseService.modAiSedimentationInit("e_sc_set_ti", sedimentationSc.getE_sc_set_ti(), processStep) == 1) && result;
        
        // update 대차 운전 주기 최소기준시간
        result = (databaseService.modAiSedimentationInit("e_low_hour", sedimentationSc.getE_low_hour(), processStep) == 1) && result;

        if(result == true)
        {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_sedimentation_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 침전 공정 지별 AI 모드 변경
    @RequestMapping(value = "/sedimentation/control/location/{locationNumber}/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putLocationControlSedimentation(@PathVariable int locationNumber, @RequestBody InterfaceAiOnOffDTO aiOnOff, @PathVariable int processStep)
    {
        log.debug("putLocationControlSedimentation, location:[{}], AI:[{}]", locationNumber, aiOnOff.getAi());

        // get location number(지 번호)
        TagManageRangeDTO sedimentationRange = databaseService.getTagManageRange(CommonValue.PROCESS_SEDIMENTATION, processStep);
        log.debug("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_SEDIMENTATION, sedimentationRange != null ? 1 : 0);

        int nLocationMin = 0, nLocationMax = 0;
        if(sedimentationRange != null)
        {
            nLocationMin = sedimentationRange.getMin();
            nLocationMax = sedimentationRange.getMax();
       }

        // 지 번호 검사
        if(locationNumber < nLocationMin || locationNumber > nLocationMax)
        {
            log.debug("invalid location number:[{}]", locationNumber);
            String strErrorBody = "{\"reason\":\"invalid location number\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // ON/OFF 값 검사
        if(aiOnOff.getAi() < CommonValue.AI_OFF || aiOnOff.getAi() > CommonValue.AI_ON)
        {
        log.debug("invalid AI on/off:[{}]", aiOnOff.getAi());
            String strErrorBody = "{\"reason\":\"invalid on/off value\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // 지별 AI 모드 업데이트
        if(databaseService.modAiSedimentationInit("e_sc_set" + locationNumber, aiOnOff.getAi(), processStep) == 1)
        {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_sedimentation_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}