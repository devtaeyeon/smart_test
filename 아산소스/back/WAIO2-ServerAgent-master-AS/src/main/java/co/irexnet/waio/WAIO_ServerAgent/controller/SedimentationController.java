package co.irexnet.waio.WAIO_ServerAgent.controller;

import java.lang.reflect.Field;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiCoagulantRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiDisinfectionRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation10RealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation11RealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation12RealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation1RealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation2RealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation3RealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation4RealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation5RealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation6RealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation7RealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation8RealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationLocation9RealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.FrequencyDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAiOnOffDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceOperationModeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceSedimentationScDTO;
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
public class SedimentationController {
    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    KafkaProducer kafkaProducer;

    /**
     * 침전 공정 최근 데이터 조회
     * 
     * @param processStep 공정단계
     * @return ResponseEntity<String> 메시지
     */
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

        // get ai_sedimentation_realtime
        AiSedimentationRealtimeDTO aiSedimentationRealtime = databaseService
                .getLatestAiSedimentationRealtimeValue(processStep);
        log.debug("getLatestAiSedimentationRealtimeValue, result:[{}]", aiSedimentationRealtime != null ? 1 : 0);

        // get ai_sedimentation_init(e_operation_mode)
        AiProcessInitDTO aiSedimentationInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE,
                processStep);
        log.debug("getAiSedimentationInit, result:[{}]", aiSedimentationInit != null ? 1 : 0);

        // get ai_sedimentation_init
        List<AiProcessInitDTO> aiSedimentationInitList = databaseService.getAllAiSedimentationInit(processStep);
        log.debug("getAllAiSedimentationInit, result:[{}]", aiSedimentationInitList.size());

        // get sedimentation_realtime
        List<ProcessRealtimeDTO> sedimentationRealtime = databaseService
                .getLatestSedimentationRealtimeValue(strPartitionName, processStep);
        log.debug("getLatestSedimentationRealtimeValue, result:[{}]", sedimentationRealtime.size());

        // get tag_manage
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_SEDIMENTATION,
                processStep);
        log.debug("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_SEDIMENTATION, tagManageList.size());

        TagManageRangeDTO sedimentationRange = databaseService.getTagManageRange(CommonValue.PROCESS_SEDIMENTATION,
                processStep);
        log.debug("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_SEDIMENTATION,
                sedimentationRange != null ? 1 : 0);

        int nLocationMin = 0, nLocationMax = 0;

        if (aiSedimentationRealtime != null) {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiSedimentationInfo = new LinkedHashMap<>();

            if (sedimentationRange != null) {
                nLocationMin = sedimentationRange.getMin();
                nLocationMax = sedimentationRange.getMax();
            }
            aiSedimentationInfo.put("locationMin", processStep == 1 ? 11 : nLocationMin);
            aiSedimentationInfo.put("locationMax", processStep == 1 ? 12 : nLocationMax);
            // update_time
            aiSedimentationInfo.put("upd_ti", aiSedimentationRealtime.getUpd_ti());
            // operation mode
            aiSedimentationInfo.put("ai_opr", aiSedimentationInit != null ? aiSedimentationInit.getInit_val().intValue()
                    : aiSedimentationRealtime.getAi_opr());

            for (AiProcessInitDTO dto : aiSedimentationInitList) {
                if (dto.getItm().equalsIgnoreCase("e_sc_set_sludge_q") == true) {
                    // 슬러지 수집기 운행 기준 적산 슬러지 양
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set_max_wait") == true) {
                    // 슬러지 수집기 운행 대기 최대 일
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val());
                } else if (dto.getItm().equalsIgnoreCase("e_set_lt") == true) {
                    // 슬러지 수집기 편도 운전 거리
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val());
                } else if (dto.getItm().equalsIgnoreCase("e_low_hour") == true) {
                    // 대차 운전주기 최소기준시간
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set_ti") == true) {
                    // 슬러지 수집기 운전 시간
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set1") == true) {
                    // 1지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set2") == true) {
                    // 2지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set3") == true) {
                    // 3지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set4") == true) {
                    // 4지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set5") == true) {
                    // 5지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set6") == true) {
                    // 6지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set7") == true) {
                    // 7지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set8") == true) {
                    // 8지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set9") == true) {
                    // 9지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set10") == true) {
                    // 10지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set11") == true) {
                    // 11지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                } else if (dto.getItm().equalsIgnoreCase("e_sc_set12") == true) {
                    // 12지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItm(), dto.getInit_val().intValue());
                }
            }

            Float mm_fr = 0.0f;
            // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiSedimenationInfo에 등록
            for (TagManageDTO tagManage : tagManageList) {
                for (ProcessRealtimeDTO dto : sedimentationRealtime) {
                    if (tagManage.getItm().equalsIgnoreCase("b_in_fr") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 원수 유입 유량
                        aiSedimentationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                    } else if (tagManage.getItm().equalsIgnoreCase("b_tb") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 원수 탁도
                        aiSedimentationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                    } else if (tagManage.getItm().equalsIgnoreCase("mm_fr") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // PACS 주입량
                        mm_fr = Float.parseFloat(dto.getTag_val());
                    } else if (tagManage.getItm().equalsIgnoreCase("mm_fr1") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 3단계공업 주입량1
                        mm_fr += Float.parseFloat(dto.getTag_val());
                    } else if (tagManage.getItm().equalsIgnoreCase("mm_fr2") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 3단계공업 주입량2
                        mm_fr += Float.parseFloat(dto.getTag_val());
                    } else if (tagManage.getItm().equalsIgnoreCase("mm_fr3") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 3단계공업 주입량3
                        mm_fr += Float.parseFloat(dto.getTag_val());
                    } else if (tagManage.getItm().equalsIgnoreCase("c_mm_fr_etc") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 폴리아민 주입량
                        aiSedimentationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                    } else if (tagManage.getItm().equalsIgnoreCase("c_mm_fr_etc1") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 활성탄 주입량
                        aiSedimentationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                    } else if (tagManage.getItm().equalsIgnoreCase("c_mm_fr_etc2") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 생활가성소다 주입량
                        aiSedimentationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                    }
                }
            }
            // 약품 종류
            Map<String, Object> c_cf_coagulant = databaseService.selectUsrMng("c" + processStep + "_cf_coagulant");
            aiSedimentationInfo.put("c_cf_coagulant", c_cf_coagulant.get("init_val"));
            // 약품 사용량 유량
            aiSedimentationInfo.put("mm_fr", mm_fr);

            // AI 슬러지 발생량 예측
            aiSedimentationInfo.put("ai_e1_sludge", aiSedimentationRealtime.getAIE_5300());
            // 약품주입량
            aiSedimentationInfo.put("c_mm_fr",
                    processStep == 3
                            ? aiSedimentationRealtime.getC_mm_fr1() + aiSedimentationRealtime.getC_mm_fr2()
                                    + aiSedimentationRealtime.getC_mm_fr3()
                            : aiSedimentationRealtime.getC_mm_fr());
            // 보조약품 주입량
            // aiSedimentationInfo.put("c_mm_fr_etc",
            // aiSedimentationRealtime.getC_mm_fr_etc());
            // aiSedimentationInfo.put("c_mm_fr_etc1",
            // aiSedimentationRealtime.getC_mm_fr_etc1());
            // aiSedimentationInfo.put("c_mm_fr_etc2",
            // aiSedimentationRealtime.getC_mm_fr_etc2());

            try {
                // AI 총 슬러지 발생량 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                String strTemp = aiSedimentationRealtime.getAIE_5200();
                LinkedHashMap<String, Object> mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);

                ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
                Object objectTemp = mapTemp.get(keyList.get(0));
                LinkedHashMap<String, Float> aiSludgeMap = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                aiSedimentationInfo.put("ai_e_total_sludge", aiSludgeMap);

                // List<LinkedHashMap<String, Object>> locationMap = new
                // ArrayList<LinkedHashMap<String, Object>>();
                if (processStep != 1) {
                	// 1지 데이터를 추출하여 location1Dto에 저장
                	getParamMapping(aiSedimentationInfo, aiSedimentationRealtime.getAIE_9001(),
                			AiSedimentationLocation1RealtimeDTO.class);
                	// 2지 데이터를 추출하여 location2Dto에 저장
                	getParamMapping(aiSedimentationInfo, aiSedimentationRealtime.getAIE_9002(),
                			AiSedimentationLocation2RealtimeDTO.class);
                	// 3지 데이터를 추출하여 location3Dto에 저장
                	getParamMapping(aiSedimentationInfo, aiSedimentationRealtime.getAIE_9003(),
                			AiSedimentationLocation3RealtimeDTO.class);
                	// 4지 데이터를 추출하여 location4Dto에 저장
                	getParamMapping(aiSedimentationInfo, aiSedimentationRealtime.getAIE_9004(),
                			AiSedimentationLocation4RealtimeDTO.class);
                } else {
                	// 11지 데이터를 추출하여 location11Dto에 저장
                	getParamMapping(aiSedimentationInfo, aiSedimentationRealtime.getAIE_9011(),
                			AiSedimentationLocation11RealtimeDTO.class);
                	// 12지 데이터를 추출하여 location12Dto에 저장
                	getParamMapping(aiSedimentationInfo, aiSedimentationRealtime.getAIE_9012(),
                			AiSedimentationLocation12RealtimeDTO.class);
                }
            } catch (JsonProcessingException e) {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IllegalArgumentException e) {
                String strErrorBody = "{\"reason\":\"IllegalArgumentException Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IllegalAccessException e) {
                String strErrorBody = "{\"reason\":\"IllegalAccessException Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiSedimentationInfo);

            String strBody = "";
            try {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            } catch (JsonProcessingException e) {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IllegalArgumentException e) {
                String strErrorBody = "{\"reason\":\"IllegalArgumentException Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);

            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        } else {
             String strErrorBody = "{\"reason\":\"Empty ai_sedimentation\"}";
             return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 침전 지별 세부현황 조회
     * 
     * @param aiSedimentationInfo front-end로 보낼 침전 LinkedHashMap
     * @param strTemp             지별 세부현황 DTO
     * @param dto                 지별 세부현황 DTO Class
     */
    public void getParamMapping(LinkedHashMap<String, Object> aiSedimentationInfo, String strTemp, Class<?> dto)
            throws IllegalArgumentException, IllegalAccessException, JsonMappingException, JsonProcessingException {
        if (strTemp != null) {
            LinkedHashMap<String, Object> mapTemp;
            ArrayList<String> keyList;
            Object objectTemp;
            ObjectMapper objectMapper = new ObjectMapper();
            strTemp = strTemp.replaceAll("NaN", "\"\"");
            mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
            keyList = new ArrayList<>(mapTemp.keySet());
            objectTemp = mapTemp.get(keyList.get(0));

            List<LinkedHashMap<String, Object>> locationMap = objectMapper.convertValue(objectTemp,
                    new TypeReference<List<LinkedHashMap<String, Object>>>() {
                    });
            mapTemp.clear();

            for (Map<String, Object> map : locationMap) {
                mapTemp.putAll(map);
            }

            Object paramDto = objectMapper.convertValue(mapTemp, dto);

            Map<String, String> scScheduleMap = new HashMap<>();
            for (Field field : paramDto.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value;
                value = field.get(paramDto);
                // 인발밸브 상태
                if (field.getName().contains("e_drn_vv")) {
                    int idx = 1;
                    for (Field field2 : field.getType().getDeclaredFields()) {
                        field2.setAccessible(true);
                        aiSedimentationInfo.put("e_drawing_vv_" + String.valueOf(idx++), field2.get(value));
                        aiSedimentationInfo.put("e_drawing_vv_".concat(field2.getName().split("e_drn_vv")[1]),
                                field2.get(value));
                    }
                    // 수집기 대차 위치
                } else if (field.getName().contains("e_loc_sc")) {
                    for (Field field2 : field.getType().getDeclaredFields()) {
                        field2.setAccessible(true);
                        aiSedimentationInfo.put("e_sc".concat(field.getName().split("e_loc_sc_")[1]).concat("_")
                                .concat(field2.getName()), field2.get(value));
                        aiSedimentationInfo.put("e_sc".concat("_").concat(field2.getName()), field2.get(value));
                    }
                    // 수집기 운영 스케쥴
                } else if (field.getName().contains("AIE_60")) {
                    for (Field field2 : field.getType().getDeclaredFields()) {
                        field2.setAccessible(true);
                        if ("start".equalsIgnoreCase(field2.getName())) {
                            aiSedimentationInfo.put("e_sc_latest", field2.get(value));
                        } else if ("next_start".equalsIgnoreCase(field2.getName())) {
                            aiSedimentationInfo.put("ai_e_sc_next", field2.get(value));
                        }
                        scScheduleMap.put(field2.getName(), String.valueOf(field2.get(value)));

                    }
                    aiSedimentationInfo
                            .put("e_sc".concat(String.valueOf(Integer.parseInt(field.getName().split("AIE_60")[1])))
                                    .concat("_schedule"), scScheduleMap);
                    // 슬러지 발생량
                } else if (field.getName().contains("AIE_50")) {
                	String numberPart = field.getName().substring(4); // "AIE-" 이후의 숫자 부분 추출
                	if (numberPart.length() == 4) {
                		if ("11".equals(dto.getName().replaceAll("[^0-9]", "")) || "12".equals(dto.getName().replaceAll("[^0-9]", ""))) {
                			aiSedimentationInfo.put("e_location_sludge", value);
                			// 지별 슬러지 발생량
                			aiSedimentationInfo.put("e_location_sludge_" + dto.getName().replaceAll("[^0-9]", ""), value);
                		} else {
                			if (field.getName().contains("AIE_501")) {
                				// 지별 슬러지 경과시간
                				aiSedimentationInfo.put("e_location_sludge_elapsedTime_" + dto.getName().replaceAll("[^0-9]", ""), value);
                			} else {
                				aiSedimentationInfo.put("e_location_sludge", value);
                    			// 지별 슬러지 발생량
                    			aiSedimentationInfo.put("e_location_sludge_" + dto.getName().replaceAll("[^0-9]", ""), value);
                			}
                		}
                	} else if (numberPart.length() == 5) {
                		// 지별 슬러지 경과시간
                		aiSedimentationInfo.put("e_location_sludge_elapsedTime_" + dto.getName().replaceAll("[^0-9]", ""), value);
                	}
                    
                } else if (field.getName().contains("AIE_51")) {
                    aiSedimentationInfo.put("e_location_sludge_trend",
                            objectMapper.convertValue(value, LinkedHashMap.class));
                } else if (field.getName().contains("operation_mode")) {
                    aiSedimentationInfo.put("e_operation_mode_" + dto.getName().replaceAll("[^0-9]", ""), value);
                }
            }
        }
    }

    /**
     * 실시간 침전 공정 지별 세부 항목 조회
     * 
     * @param locationNumber 선택한 지
     * @param processStep    공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/sedimentation/location/{locationNumber}/{processStep}", method = RequestMethod.GET)
    public ResponseEntity<String> getLocationSedimentation(@PathVariable int locationNumber,
            @PathVariable int processStep) {
        log.debug("getLocationSedimentation, locationNumber:[{}]", locationNumber);

        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_SEDIMENTATION,
                processStep);
        if (tagManageList.size() > 0) {
            // Get series number
            // Front-end로부터 전달받은 지 번호가 tag_manage에 등록되었는지 검사
            int nLocNumber = 0;
            for (TagManageDTO dto : tagManageList) {
                if (dto.getLoc() == locationNumber) {
                    nLocNumber = dto.getLoc();
                    break;
                }
            }

            // 등록되지 않은 지 번호는 에러처리
            if (nLocNumber == 0) {
                String strErrorBody = "{\"reason\":\"Invalid location number.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            Calendar calendarToday = Calendar.getInstance();
            calendarToday.set(Calendar.MINUTE, 0);
            calendarToday.set(Calendar.SECOND, 0);
            calendarToday.set(Calendar.HOUR_OF_DAY, 0);
            SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
            String strPartitionName = partitionNameFormat.format(calendarToday.getTime());

            // get sedimentation_realtime
            List<ProcessRealtimeDTO> sedimentationRealtime = databaseService
                    .getLatestSedimentationRealtimeValue(strPartitionName, processStep);
            log.debug("getLatestSedimentationRealtimeValue, result:[{}]", sedimentationRealtime.size());

            // Get ai_sedimentation_realtime
            AiSedimentationRealtimeDTO aiSedimentationRealtime = databaseService
                    .getLatestAiSedimentationRealtimeValue(processStep);
            log.debug("getLatestAiSedimentationRealtimeValue, result:[{}]", aiSedimentationRealtime != null ? 1 : 0);
            if (aiSedimentationRealtime != null) {
                // JSON 처리를 위한 ObjectMapper 선언
                ObjectMapper objectMapper = new ObjectMapper();

                // Make ResponseBody
                LinkedHashMap<String, Object> aiSedimentationLocationInfo = new LinkedHashMap<>();

                // update_time
                aiSedimentationLocationInfo.put("upd_ti", aiSedimentationRealtime.getUpd_ti());
                // 원수 유입 유량
                // aiSedimentationLocationInfo.put("b_in_fr",
                // aiSedimentationRealtime.getB_in_fr());
                // 원수 탁도
                // aiSedimentationLocationInfo.put("b_tb", aiSedimentationRealtime.getB_tb());
                // 약품 사용량 (1호기 주입량 + 2호기 주입량)
                aiSedimentationLocationInfo.put("d_mm_fr",
                        processStep == 3
                                ? aiSedimentationRealtime.getC_mm_fr1() + aiSedimentationRealtime.getC_mm_fr2()
                                        + aiSedimentationRealtime.getC_mm_fr3()
                                : aiSedimentationRealtime.getC_mm_fr());

                Float mm_fr = 0.0f;
                // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiSedimenationInfo에 등록
                for (TagManageDTO tagManage : tagManageList) {
                    for (ProcessRealtimeDTO dto : sedimentationRealtime) {
                        if (tagManage.getItm().equalsIgnoreCase("b_in_fr") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 원수 유입 유량
                            aiSedimentationLocationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("b_tb") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 원수 탁도
                            aiSedimentationLocationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("mm_fr") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // PACS 주입량
                            mm_fr = Float.parseFloat(dto.getTag_val());
                        } else if (tagManage.getItm().equalsIgnoreCase("mm_fr1") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 3단계공업 주입량1
                            mm_fr += Float.parseFloat(dto.getTag_val());
                        } else if (tagManage.getItm().equalsIgnoreCase("mm_fr2") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 3단계공업 주입량2
                            mm_fr += Float.parseFloat(dto.getTag_val());
                        } else if (tagManage.getItm().equalsIgnoreCase("mm_fr3") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 3단계공업 주입량3
                            mm_fr += Float.parseFloat(dto.getTag_val());
                        } else if (tagManage.getItm().equalsIgnoreCase("c_mm_fr_etc") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 폴리아민 주입량
                            aiSedimentationLocationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("c_mm_fr_etc1") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 활성탄 주입량
                            aiSedimentationLocationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("c_mm_fr_etc2") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 생활가성소다 주입량
                            aiSedimentationLocationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        }
                    }
                }
                // 약품 사용량 유량
                aiSedimentationLocationInfo.put("mm_fr", mm_fr);

                // 지별 데이터
                String strTemp;
                LinkedHashMap<String, Object> mapTemp;
                ArrayList<String> keyList;
                Object objectTemp;
                try {
                    if (locationNumber == 1 && aiSedimentationRealtime.getAIE_9001() != null) {
                        // 1지 데이터를 추출하여 location2Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9001();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap = objectMapper.convertValue(objectTemp,
                                new TypeReference<List<LinkedHashMap<String, Object>>>() {
                                });
                        mapTemp.clear();
                        for (Map<String, Object> map : locationMap) {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation1RealtimeDTO location1Dto = objectMapper.convertValue(mapTemp,
                                AiSedimentationLocation1RealtimeDTO.class);

                        // 슬러지 발생량
                        aiSedimentationLocationInfo.put("e_location_sludge", location1Dto.getAIE_5001());

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

                        // 슬러지 발생량 트렌드
                        objectTemp = location1Dto.getAIE_5101();
                        LinkedHashMap<String, Float> location1SludgeMap = objectMapper.convertValue(objectTemp,
                                LinkedHashMap.class);
                        aiSedimentationLocationInfo.put("e_location_sludge_trend", location1SludgeMap);
                    } else if (locationNumber == 2 && aiSedimentationRealtime.getAIE_9002() != null) {
                        getParamMapping(aiSedimentationLocationInfo, aiSedimentationRealtime.getAIE_9002(),
                                AiSedimentationLocation2RealtimeDTO.class);
                    } else if (locationNumber == 3 && aiSedimentationRealtime.getAIE_9003() != null) {
                        getParamMapping(aiSedimentationLocationInfo, aiSedimentationRealtime.getAIE_9003(),
                                AiSedimentationLocation3RealtimeDTO.class);
                    } else if (locationNumber == 4 && aiSedimentationRealtime.getAIE_9004() != null) {
                        // 4지 데이터를 추출하여 location4Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9004();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap = objectMapper.convertValue(objectTemp,
                                new TypeReference<List<LinkedHashMap<String, Object>>>() {
                                });
                        mapTemp.clear();
                        for (Map<String, Object> map : locationMap) {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation4RealtimeDTO location4Dto = objectMapper.convertValue(mapTemp,
                                AiSedimentationLocation4RealtimeDTO.class);

                        // 슬러지 발생량
                        aiSedimentationLocationInfo.put("e_location_sludge", location4Dto.getAIE_5004());

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

                        // 슬러지 발생량 트렌드
                        objectTemp = location4Dto.getAIE_5104();
                        LinkedHashMap<String, Float> locationSludgeMap = objectMapper.convertValue(objectTemp,
                                LinkedHashMap.class);
                        aiSedimentationLocationInfo.put("e_location_sludge_trend", locationSludgeMap);
                    } else if (locationNumber == 5 && aiSedimentationRealtime.getAIE_9005() != null) {
                        getParamMapping(aiSedimentationLocationInfo, aiSedimentationRealtime.getAIE_9005(),
                                AiSedimentationLocation5RealtimeDTO.class);
                    } else if (locationNumber == 6 && aiSedimentationRealtime.getAIE_9006() != null) {
                        getParamMapping(aiSedimentationLocationInfo, aiSedimentationRealtime.getAIE_9006(),
                                AiSedimentationLocation6RealtimeDTO.class);
                    } else if (locationNumber == 7 && aiSedimentationRealtime.getAIE_9007() != null) {
                        getParamMapping(aiSedimentationLocationInfo, aiSedimentationRealtime.getAIE_9007(),
                                AiSedimentationLocation7RealtimeDTO.class);
                    } else if (locationNumber == 8 && aiSedimentationRealtime.getAIE_9008() != null) {
                        getParamMapping(aiSedimentationLocationInfo, aiSedimentationRealtime.getAIE_9008(),
                                AiSedimentationLocation8RealtimeDTO.class);
                    } else if (locationNumber == 9 && aiSedimentationRealtime.getAIE_9009() != null) {
                        getParamMapping(aiSedimentationLocationInfo, aiSedimentationRealtime.getAIE_9009(),
                                AiSedimentationLocation9RealtimeDTO.class);
                    } else if (locationNumber == 10 && aiSedimentationRealtime.getAIE_9010() != null) {
                        getParamMapping(aiSedimentationLocationInfo, aiSedimentationRealtime.getAIE_9010(),
                                AiSedimentationLocation10RealtimeDTO.class);
                    } else if (locationNumber == 11 && aiSedimentationRealtime.getAIE_9011() != null) {
                        getParamMapping(aiSedimentationLocationInfo, aiSedimentationRealtime.getAIE_9011(),
                                AiSedimentationLocation11RealtimeDTO.class);
                    } else if (locationNumber == 12 && aiSedimentationRealtime.getAIE_9012() != null) {
                        getParamMapping(aiSedimentationLocationInfo, aiSedimentationRealtime.getAIE_9012(),
                                AiSedimentationLocation12RealtimeDTO.class);
                    }
                } catch (JsonProcessingException e) {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (IllegalArgumentException e) {
                    String strErrorBody = "{\"reason\":\"IllegalArgumentException Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (IllegalAccessException e) {
                    String strErrorBody = "{\"reason\":\"IllegalAccessException Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("location", aiSedimentationLocationInfo);

                String strBody;
                try {
                    // ObjectMapper를 통해 JSON 값을 String으로 변환
                    strBody = objectMapper.writeValueAsString(responseBody);
                } catch (JsonProcessingException e) {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            } else {
                String strErrorBody = "{\"reason\":\"Empty ai_sedimentation\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        } else {
            String strErrorBody = "{\"reason\":\"Empty tag_manage\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 침전수 잔류염소 측정 이력 조회
     * 
     * @param disinfectionIndex 전차염: 1, 후차염: 3
     * @param processStep       공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/sedimentation/history/cl/{disinfectionIndex}/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> getClHistorySedimentation(@RequestBody InterfaceDateSearchDTO dateSearchDTO,
            @PathVariable int disinfectionIndex, @PathVariable int processStep) {
        log.debug("getClHistorySedimentation, start:[{}], end:[{}]", dateSearchDTO.getStart_time(),
                dateSearchDTO.getEnd_time());

        // 소독 공정 데이터 조회
        List<AiDisinfectionRealtimeDTO> aiDisinfectionRealtimeList = databaseService
                .getAiDisinfectionRealtimeValueFromUpdateTime(dateSearchDTO, processStep, disinfectionIndex);
        log.debug("getAiDisinfectionRealtimeValueFromUpdateTime, result:[{}]", aiDisinfectionRealtimeList.size());
        if (aiDisinfectionRealtimeList.size() > 0) {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> series1 = new LinkedHashMap<>();

            // aiDisinfectionRealtimeList에서 계열별 침전지 잔류염소 를 조회하여 계열별 map에 저장
            for (AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList) {
                String strDate = simpleDateFormat.format(dto.getUpd_ti());
                series1.put(strDate, dto.getG_e_residual_cl());
            }

            LinkedHashMap<String, Object> seriesClInfo = new LinkedHashMap<>();
            seriesClInfo.put("series1", series1);

            System.out.println("=====" + seriesClInfo);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("cl", seriesClInfo);

            ObjectMapper objectMapper = new ObjectMapper();
            String strBody;
            try {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            } catch (JsonProcessingException e) {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 침전수 탁도 측정 이력 조회
     * 
     * @param dateSearchDTO Front-end 시간 검색 값을 저장하기 위한 DTO
     * @param processStep   공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/sedimentation/history/tb/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> getTbHistorySedimentation(@RequestBody InterfaceDateSearchDTO dateSearchDTO,
            @PathVariable int processStep) {
        log.debug("getTbHistorySedimentation, start:[{}], end:[{}]", dateSearchDTO.getStart_time(),
                dateSearchDTO.getEnd_time());

        // 약품 공정 데이터 조회
        List<AiCoagulantRealtimeDTO> aiCoagulantRealtimeList = databaseService
                .getAiCoagulantRealtimeValueFromUpdateTime(dateSearchDTO, processStep);
        log.debug("getAiCoagulantRealtimeValueFromUpdateTime, result:[{}]", aiCoagulantRealtimeList.size());
        if (aiCoagulantRealtimeList.size() > 0) {
            // Make Response Body
            // 계열별 데이터를 분리 저장하기 위한 변수 선언
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> series1 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> series2 = new LinkedHashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                // aiCoagulantRealtimeList에서 침전지 탁도 정보를 조회하여 계열별 map에 등록
                for (AiCoagulantRealtimeDTO dto : aiCoagulantRealtimeList) {
                    String strDate = simpleDateFormat.format(dto.getUpd_ti());
                    // 생활만, 공업만
                    series1.put(strDate, dto.getCTbE());
                }

                LinkedHashMap<String, Object> seriesTbInfo = new LinkedHashMap<>();
                seriesTbInfo.put("series1", series1);
                seriesTbInfo.put("series2", series2);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("tb", seriesTbInfo);

                // ObjectMapper를 통해 JSON 값을 String으로 변환
                String strBody = objectMapper.writeValueAsString(responseBody);
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException Occurred in /sedimentation/history/tb API");
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            String strErrorBody = "{\"reason\":\"Empty ai_coagulant_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 침전지 탁도 정규분포 데이터 조회
     * 
     * @param processStep 공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/sedimentation/distribution/tb/{processStep}", method = RequestMethod.GET)
    public ResponseEntity<String> getTbDistributionSedimentation(@PathVariable int processStep) {
        // Use coagulant_realtime table
        log.debug("Recv getTbDistributionSedimentation");

        // get tag_manage
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_COAGULANT,
                processStep);
        log.debug("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_COAGULANT, tagManageList.size());
        // Get start_time(before seven days)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        // calendar.add(Calendar.MONTH, -8); // 2022년 12월 샘플 데이터 조회를 위한 설정 추가
        Date startTime = calendar.getTime();

        // Make Response Body
        int nTotalSize = 0;
        LinkedHashMap<String, Object> seriesTbInfo = new LinkedHashMap<>();
        for (TagManageDTO tagManage : tagManageList) {
            if (tagManage.getItm().equalsIgnoreCase("e1_tb_b") == true) {
                // 1계열 침전지 탁도 정규분포 데이터를 조회하여 series1에 저장
                List<FrequencyDTO> frequencyList = databaseService.getCoagulantDistribution(startTime,
                        tagManage.getTag_sn(), processStep);
                nTotalSize += frequencyList.size();

                Map<String, Integer> countBody = new LinkedHashMap<>();
                for (FrequencyDTO dto : frequencyList) {
                    if (dto.getValue() == null) {
                        continue;
                    }
                    countBody.put(dto.getValue(), dto.getCount());
                }
                seriesTbInfo.put("series1", countBody.isEmpty() ? null : countBody);
            } else if (tagManage.getItm().equalsIgnoreCase("e2_tb_b") == true) {
                // 2계열 침전지 탁도 정규분포 데이터를 조회하여 series2에 저장
                List<FrequencyDTO> frequencyList = databaseService.getCoagulantDistribution(startTime,
                        tagManage.getTag_sn(), processStep);
                nTotalSize += frequencyList.size();

                Map<String, Integer> countBody = new LinkedHashMap<>();
                for (FrequencyDTO dto : frequencyList) {
                    if (dto.getValue() == null) {
                        continue;
                    }
                    countBody.put(dto.getValue(), dto.getCount());
                }
                seriesTbInfo.put("series2", countBody.isEmpty() ? null : countBody);
            }
        }
        if (nTotalSize == 0) {
            String strErrorBody = "{\"reason\":\"Empty TB Trend\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("tb", seriesTbInfo);

        String strBody = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // ObjectMapper를 통해 JSON 값을 String으로 변환
            strBody = objectMapper.writeValueAsString(responseBody);
        } catch (JsonProcessingException e) {
            String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(strBody, HttpStatus.OK);
    }

    /**
     * 침전 공정 제어모드 변경
     * 
     * @param operationMode 제어모드
     * @param processStep   공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/sedimentation/control/operation/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlSedimentation(@RequestBody InterfaceOperationModeDTO operationMode,
            @PathVariable int processStep) {
        log.info("putOperationControlSedimentation, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if (nOperationMode < CommonValue.OPERATION_MODE_MANUAL
                || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO) {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // send control value to kafka ai_control(e_operation_mode)
        AiProcessInitDTO aiSedimentationInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE,
                processStep);
        log.info("getAiSedimentationInit, result:[{}]", aiSedimentationInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try {
        	if (aiSedimentationInit != null) {
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
        		List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
        		for (TagManageDTO dto : tagManageList) {
        			if (dto.getItm().equalsIgnoreCase("e_operation_mode_a") == true && CommonValue.PROCESS_SEDIMENTATION
        					.concat(String.valueOf(processStep)).equals(dto.getProc_cd())) {
        				// Kafka에 전송할 값 정의
        				controlMap = new LinkedHashMap<>();
        				controlMap.put("tag", dto.getTag_sn());
        				controlMap.put("value", nOperationMode == CommonValue.OPERATION_MODE_MANUAL ? false : true);
        				controlMap.put("time", strDate);
        				
        				// ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
        				objectMapper = new ObjectMapper();
        				strBody = objectMapper.writeValueAsString(controlMap);
        				kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
        				log.info("send to kafka:[{}]", strBody);
        				break;
        			}
        		}
        		// DB UPDATE
        		databaseService.modAiSedimentationOperationMode(nOperationMode, processStep);
        	} else {
                log.error("Does not exist aiSedimentationInit:[{]]", CommonValue.E_OPERATION_MODE);
            }
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException Occurred in /sedimentation/control/operation API");
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    /**
     * 침전 알고리즘 설정값 변경
     * 
     * @param sedimentationSc Front-end 침전 공정 알고리즘 설정값을 저장하기 위한 DTO
     * @param processStep     공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/sedimentation/control/sc/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putScControlSedimentation(@RequestBody InterfaceSedimentationScDTO sedimentationSc,
            @PathVariable int processStep) {
        log.debug("putScControlSedimentation, sc:[{}]", sedimentationSc);

        boolean result = true;

        // update 슬러지 수집기 운행 기준 적산 슬러지 양
        result = (databaseService.modAiSedimentationInit("e_sc_set_sludge_q", sedimentationSc.getE_sc_set_sludge_q(),
                processStep) == 1) && result;
        // update 슬러지 수집기 운행 대기 최대 일
        result = (databaseService.modAiSedimentationInit("e_sc_set_max_wait", sedimentationSc.getE_sc_set_max_wait(),
                processStep) == 1) && result;
        // update 슬러지 수집기 편도 운전 거리
        result = (databaseService.modAiSedimentationInit("e_set_lt", sedimentationSc.getE_set_lt(), processStep) == 1)
                && result;
        // update 슬러지 수집기 운전 시간
        result = (databaseService.modAiSedimentationInit("e_sc_set_ti", sedimentationSc.getE_sc_set_ti(),
                processStep) == 1) && result;

        result = (databaseService.modAiSedimentationInit("e_low_hour", sedimentationSc.getE_low_hour(),
                processStep) == 1) && result;

        if (result == true) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"ai_sedimentation_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 침전 공정 지별 AI 모드 변경
     * 
     * @param locationNumber 선택한 지
     * @param aiOnOff        Front-end 지별 AI ON/OFF 명령을 저장하기 위한 DTO
     * @param processStep    공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/sedimentation/control/location/{locationNumber}/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putLocationControlSedimentation(@PathVariable int locationNumber,
            @RequestBody InterfaceAiOnOffDTO aiOnOff, @PathVariable int processStep) {
        log.debug("putLocationControlSedimentation, location:[{}], AI:[{}]", locationNumber, aiOnOff.getAi());

        // get location number(지 번호)
        TagManageRangeDTO sedimentationRange = databaseService.getTagManageRange(CommonValue.PROCESS_SEDIMENTATION,
                processStep);
        log.debug("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_SEDIMENTATION,
                sedimentationRange != null ? 1 : 0);

        int nLocationMin = 0, nLocationMax = 0;
        if (sedimentationRange != null) {
            nLocationMin = sedimentationRange.getMin();
            nLocationMax = sedimentationRange.getMax();
        }

        // 지 번호 검사
        if (locationNumber < nLocationMin || locationNumber > nLocationMax) {
            log.debug("invalid location number:[{}]", locationNumber);
            String strErrorBody = "{\"reason\":\"invalid location number\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // ON/OFF 값 검사
        if (aiOnOff.getAi() < CommonValue.AI_OFF || aiOnOff.getAi() > CommonValue.AI_ON) {
            log.debug("invalid AI on/off:[{}]", aiOnOff.getAi());
            String strErrorBody = "{\"reason\":\"invalid on/off value\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // 지별 AI 모드 업데이트
        if (databaseService.modAiSedimentationInit("e_sc_set" + locationNumber, aiOnOff.getAi(), processStep) == 1) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"ai_sedimentation_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}