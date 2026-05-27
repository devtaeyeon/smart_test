package co.irexnet.waio.WAIO_ServerAgent.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
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

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiDisinfectionRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDisinfectionPostDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDisinfectionPreDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceOperationModeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageDTO;
import co.irexnet.waio.WAIO_ServerAgent.kafka.KafkaProducer;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableSwagger2
@Slf4j
public class DisinfectionController {
    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    KafkaProducer kafkaProducer;

    /**
     * 소독 공정 최근 데이터 조회
     * 
     * @param disinfectionIndex 전차염: 1, 후차염: 3
     * @param processStep       공정단계
     * @return ResponseEntity<String> 메시지
     */

    @RequestMapping(value = "/disinfection/latest/{disinfectionIndex}/{processStep}", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestDisinfection(@PathVariable int disinfectionIndex,
            @PathVariable int processStep) {
        log.debug("Recv getLatestDisinfection");

        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());
        
        // get ai_disinfection_init(g_pre_operation_mode)
        AiProcessInitDTO aiPreDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE,
                processStep, disinfectionIndex);
        log.debug("getAiDisinfectionInit pre, result:[{}]", aiPreDisinfectionInit != null ? 1 : 0);

        // get ai_disinfection_init(g_peri_operation_mode)
        AiProcessInitDTO aiPeriDisinfectionInit = databaseService
                .getAiDisinfectionInit(CommonValue.G_PERI_OPERATION_MODE, processStep, disinfectionIndex);
        log.debug("getAiDisinfectionInit peri, result:[{}]", aiPeriDisinfectionInit != null ? 1 : 0);

        // get ai_disinfection_init(g_post_operation_mode)
        AiProcessInitDTO aiPostDisinfectionInit = databaseService
                .getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE, processStep, disinfectionIndex);
        log.debug("getAiDisinfectionInit post, result:[{}]", aiPostDisinfectionInit != null ? 1 : 0);

        // get ai_disinfection_init
        List<AiProcessInitDTO> aiDisinfectionInitList = databaseService.getAllAiDisinfectionInit(processStep,
                disinfectionIndex);
        log.debug("getAllAiDisinfectionInit, result:[{}]", aiDisinfectionInitList.size());

        // get ai_disinfection_realtime
        AiDisinfectionRealtimeDTO aiDisinfectionRealtime = databaseService
                .getLatestAiDisinfectionRealtimeValue(processStep, disinfectionIndex);
        log.debug("getLatestAiDisinfectionRealtimeValue, result:[{}]", aiDisinfectionRealtime != null ? 1 : 0);

        // get disinfection_realtime
        List<ProcessRealtimeDTO> disinfectionRealtime = databaseService
                .getLatestDisinfectionRealtimeValue(strPartitionName, processStep);
        log.debug("getLatestDisinfectionRealtimeValue, result:[{}]", disinfectionRealtime.size());

        // get tag_manage(disinfection)
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_DISINFECTION,
                processStep);
        log.debug("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_DISINFECTION, tagManageList.size());

        if (aiDisinfectionRealtime != null) {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiDisinfectionInfo = new LinkedHashMap<>();

            // update_time
            aiDisinfectionInfo.put("upd_ti", aiDisinfectionRealtime.getUpd_ti());
            aiDisinfectionInfo.put("g_tei", aiDisinfectionRealtime.getG_tei()); // 기온

            // pre_operation_mode
            if (aiPreDisinfectionInit != null) {
                aiDisinfectionInfo.put("pre_ai_opr", aiPreDisinfectionInit.getInit_val().intValue());
            } else {
                aiDisinfectionInfo.put("pre_ai_opr", aiDisinfectionRealtime.getG_pre_operation_mode());
            }

            // peri_operataion_mode
            if (aiPeriDisinfectionInit != null) {
                aiDisinfectionInfo.put("peri_ai_opr", aiPeriDisinfectionInit.getInit_val().intValue());
            } else {
                aiDisinfectionInfo.put("peri_ai_opr", aiDisinfectionRealtime.getG_peri_operation_mode());
            }

            // post_operataion_mode
            if (aiPostDisinfectionInit != null) {
                aiDisinfectionInfo.put("post_ai_opr", aiPostDisinfectionInit.getInit_val().intValue());
            } else {
                aiDisinfectionInfo.put("post_ai_opr", aiDisinfectionRealtime.getG_post_operation_mode());
            }

            // Realtime data from SCADA
            // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiDisinfectionInfo에 등록
            for (TagManageDTO tagManage : tagManageList) {
                for (ProcessRealtimeDTO dto : disinfectionRealtime) {
                    if (tagManage.getItm().equalsIgnoreCase("b_te") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 원수 수온
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    } else if (tagManage.getItm().equalsIgnoreCase("air_te") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 기온
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    } else if (tagManage.getItm().equalsIgnoreCase("b_in_fr") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 원수 유입 유량
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    } else if (tagManage.getItm().equalsIgnoreCase("h_tb") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 정수 탁도
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    } else if (tagManage.getItm().equalsIgnoreCase("h_ph") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 정수 pH
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                }
            }
//            for (AiProcessInitDTO dto : aiDisinfectionInitList) {
//                if (dto.getItm().equalsIgnoreCase("d1_target_cl") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
//                {
//                    // 1계열 혼화지 목표 잔류염소
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                } else if (dto.getItm().equalsIgnoreCase("g_e_obj_residual_cl") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
//                {
//                    // 1계열 침전지 목표 잔류염소
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                } else if (dto.getItm().equalsIgnoreCase("g_h_obj_residual_cl") == true) {
//                    // 정수지 목표 잔류염소
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                } else if (dto.getItm().equalsIgnoreCase("g_pre_set_max") == true) {
//                    // 1계열 전염소 최대 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                } else if (dto.getItm().equalsIgnoreCase("g_pre_set_min") == true) {
//                    // 1계열 전염소 최소 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                } else if (dto.getItm().equalsIgnoreCase("g_pre_chg_limit_for_onetime") == true) {
//                    // 1계열 전염소 변경 한계치
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                } else if (dto.getItm().equalsIgnoreCase("g_pre_calib_cycle") == true) {
//                    // 전차염 보정주기
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                } else if (dto.getItm().equalsIgnoreCase("g_post_set_max") == true) {
//                    // 후염소 최대 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                } else if (dto.getItm().equalsIgnoreCase("g_post_set_min") == true) {
//                    // 후염소 최소 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                } else if (dto.getItm().equalsIgnoreCase("g_post_chg_limit_for_onetime") == true) {
//                    // 후염소 주입률 변경 한계치
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                } else if (dto.getItm().equalsIgnoreCase("g_post_calib_cycle") == true) {
//                    // 후염소 보정주기
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                } else if (dto.getItm().equalsIgnoreCase("g_post_calib_num") == true) {
//                    // 후염소 보정상수
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//            }
            
            if (aiDisinfectionInitList != null) {
                for (AiProcessInitDTO dto : aiDisinfectionInitList) {
                	aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
            }

            // 혼화지 잔류염소
            aiDisinfectionInfo.put("g_d_residual_cl", aiDisinfectionRealtime.getG_d_residual_cl());
            // 침전지 잔류염소
            aiDisinfectionInfo.put("g_e_residual_cl", aiDisinfectionRealtime.getG_e_residual_cl());
            // 정수지 유입 잔류염소
            aiDisinfectionInfo.put("g_h_in_residual_cl", aiDisinfectionRealtime.getG_h_in_residual_cl());
            // 정수지 유출 잔류염소
            aiDisinfectionInfo.put("g_h_out_residual_cl", aiDisinfectionRealtime.getG_h_out_residual_cl());
            // 계열별 전염소 주입률
            aiDisinfectionInfo.put("g_pre_chol_rate", aiDisinfectionRealtime.getG_pre_chol_rate()); // TODO : 데이터 JSON형식
                                                                                                    // 분석팀과 협의 필요
            // 후염소 주입률
            aiDisinfectionInfo.put("g_post_chol_rate", aiDisinfectionRealtime.getG_post_chol_rate());
            // 계열별 AI 전염소 증발량 예측
            aiDisinfectionInfo.put("ai_g_pre_evaporation", aiDisinfectionRealtime.getAi_g_pre_evaporation()); // TODO :
                                                                                                              // 데이터
                                                                                                              // JSON형식
                                                                                                              // 분석팀과 협의
                                                                                                              // 필요
            aiDisinfectionInfo.put("g_ser_cl_eva", aiDisinfectionRealtime.getG_ser_cl_eva());

            // 계열별 AI 전/후염소 주입률 예측
            aiDisinfectionInfo.put("ai_g_chol_rate", aiDisinfectionRealtime.getAi_g_chol_rate()); // TODO : 데이터 JSON형식
                                                                                                  // 분석팀과 협의 필요
            // 계열별 AI 후염소 주입률 예측
            aiDisinfectionInfo.put("ai_g_pre2_chlorination", aiDisinfectionRealtime.getAi_g_pre2_chlorination()); // TODO
                                                                                                                  // :
                                                                                                                  // 데이터
                                                                                                                  // JSON형식
                                                                                                                  // 분석팀과
                                                                                                                  // 협의
                                                                                                                  // 필요
            // 후차염 이전 주입률 보정예측
            aiDisinfectionInfo.put("ai_g_correct_degree", aiDisinfectionRealtime.getAi_g_correct_degree()); // TODO :
                                                                                                            // 데이터
                                                                                                            // JSON형식
                                                                                                            // 분석팀과 협의
                                                                                                            // 필요

            aiDisinfectionInfo.put("g_pump_1_run", aiDisinfectionRealtime.getG_pump_1_run());
            aiDisinfectionInfo.put("g_pump_2_run", aiDisinfectionRealtime.getG_pump_2_run());

            // 주입 후 경과시간
            aiDisinfectionInfo.put("g_elapsed_time", aiDisinfectionRealtime.getG_elapsed_time());
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiDisinfectionInfo);

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
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 전염소 주입률 측정 이력 조회
     * 
     * @param dateSearchDTO     Front-end 시간 검색 값을 저장하기 위한 DTO
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/history/chlorination/pre/{disinfectionIndex}/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> getPreHistoryDisinfection(@RequestBody InterfaceDateSearchDTO dateSearchDTO,
            @PathVariable int disinfectionIndex, @PathVariable int processStep) {
        log.debug("getPostCholRateHistoryDisinfection, start:[{}], end:[{}]", dateSearchDTO.getStart_time(),
                dateSearchDTO.getEnd_time());

        // 소독 공정 데이터 조회
        List<AiDisinfectionRealtimeDTO> aiDisinfectionRealtimeList = databaseService
                .getAiDisinfectionRealtimeValueFromUpdateTime(dateSearchDTO, processStep, disinfectionIndex);
        log.debug("getAiDisinfectionRealtimeValueFromUpdateTime, result:[{}]", aiDisinfectionRealtimeList.size());

        if (aiDisinfectionRealtimeList.size() > 0) {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> cholRate = new LinkedHashMap<>();

            // aiDisinfectionRealtimeList에서 전염소 주입률 예측을 조회하여 cholRate 등록
            for (AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList) {
                String strDate = simpleDateFormat.format(dto.getUpd_ti());
                cholRate.put(strDate, dto.getAi_g_chol_rate());
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("cholRate", cholRate);

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
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 전염소 증발량 예측 이력 조회
     * 
     * @param dateSearchDTO     Front-end 시간 검색 값을 저장하기 위한 DTO
     * @param disinfectionIndex 전차염: 1, 후차염: 3
     * @param processStep       공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/history/evaporation/{disinfectionIndex}/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> getEvaporationHistoryDisinfection(@RequestBody InterfaceDateSearchDTO dateSearchDTO,
            @PathVariable int disinfectionIndex, @PathVariable int processStep) {
        log.debug("getEvaporationHistoryDisinfection, start:[{}], end:[{}]", dateSearchDTO.getStart_time(),
                dateSearchDTO.getEnd_time());

        // 소독 공정 데이터 조회
        List<AiDisinfectionRealtimeDTO> aiDisinfectionRealtimeList = databaseService
                .getAiDisinfectionRealtimeValueFromUpdateTime(dateSearchDTO, processStep, disinfectionIndex);
        log.debug("getAiDisinfectionRealtimeValueFromUpdateTime, result:[{}]", aiDisinfectionRealtimeList.size());
        if (aiDisinfectionRealtimeList.size() > 0) {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> series1 = new LinkedHashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();

            // aiDisinfectionRealtimeList에서 계열별 전염소 증발량을 조회하여 series1, series2에 등록
            for (AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList) {
                String strDate = simpleDateFormat.format(dto.getUpd_ti());
                series1.put(strDate, dto.getAi_g_chol_rate());
            }

            // pre_evaporation에 series1, series2 등록
            LinkedHashMap<String, Object> pre_evaporation = new LinkedHashMap<>();
            pre_evaporation.put("series1", series1);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("pre_evaporation", pre_evaporation);

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
     * 
     * 후염소 주입률 예측 이력 조회
     * 
     * @param disinfectionIndex 전차염: 1, 후차염: 3
     * @param processStep       공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/history/chlorination/post/{disinfectionIndex}/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> getPostChlorinationHistoryDisinfection(
            @RequestBody InterfaceDateSearchDTO dateSearchDTO, @PathVariable int disinfectionIndex,
            @PathVariable int processStep) {
        log.debug("getPostCholRateHistoryDisinfection, start:[{}], end:[{}]", dateSearchDTO.getStart_time(),
                dateSearchDTO.getEnd_time());

        // 소독 공정 데이터 조회
        List<AiDisinfectionRealtimeDTO> aiDisinfectionRealtimeList = databaseService
                .getAiDisinfectionRealtimeValueFromUpdateTime(dateSearchDTO, processStep, disinfectionIndex);
        log.debug("getAiDisinfectionRealtimeValueFromUpdateTime, result:[{}]", aiDisinfectionRealtimeList.size());

        if (aiDisinfectionRealtimeList.size() > 0) {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> cholRate = new LinkedHashMap<>();

            // aiDisinfectionRealtimeList에서 후염소 주입률 예측을 조회하여 cholRate 등록
            for (AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList) {
                String strDate = simpleDateFormat.format(dto.getUpd_ti());
                // cholRate.put(strDate, dto.getAi_g_chol_rate()); // 후염소 주입률 예측
                cholRate.put(strDate, dto.getAi_g_correct_degree()); // 후차염 이전 주입률 보정예측
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("cholRate", cholRate);

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
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 소독(전) 공정 제어모드 변경
     * 
     * @param operationMode     제어모드
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/control/operation/pre/{disinfectionIndex}/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlPreDisinfection(
            @RequestBody InterfaceOperationModeDTO operationMode, @PathVariable int processStep,
            @PathVariable int disinfectionIndex) {
        log.info("putOperationControlPreDisinfection, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if (nOperationMode < CommonValue.OPERATION_MODE_MANUAL
                || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO) {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // send control value to kafka ai_control(g_operation_mode)
        AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE,
                processStep, disinfectionIndex);
        log.info("getAiDisinfectionInit pre, result:[{}]", aiDisinfectionInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try {
        	if (aiDisinfectionInit != null) {
        		// Kafka에 전송할 값 정의
        		LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
        		controlMap.put("tag", aiDisinfectionInit.getTag_sn());
        		controlMap.put("value", nOperationMode);
        		controlMap.put("time", strDate);
        		
        		// ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
        		ObjectMapper objectMapper = new ObjectMapper();
        		String strBody = objectMapper.writeValueAsString(controlMap);
        		kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
        		log.info("send to kafka:[{}]", strBody);
        		
        		// Kafka에 소독(전) 공정 제어모드 변경 알람 전송
        		List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
        		for (TagManageDTO dto : tagManageList) {
        			if (dto.getItm().equalsIgnoreCase("g_pre_operation_mode_a") == true && CommonValue.PROCESS_DISINFECTION
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
        		databaseService.modAiDisinfectionOperationMode(nOperationMode, processStep, CommonValue.DISINFECTION_PRE_STEP);
        	} else {
                log.error("Does not exist aiDisinfectionInit:[{]]", CommonValue.G_PRE_OPERATION_MODE);
            }
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException Occurred in /disinfection/control/operation/pre API");
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    /**
     * 소독(후) 공정 제어모드 변경
     * 
     * @param operationMode     공정모드
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/control/operation/post/{disinfectionIndex}/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlPostDisinfection(
            @RequestBody InterfaceOperationModeDTO operationMode, @PathVariable int processStep,
            @PathVariable int disinfectionIndex) {
        log.info("putOperationControlPostDisinfection, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if (nOperationMode < CommonValue.OPERATION_MODE_MANUAL
                || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO) {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // send control value to kafka ai_control(g_operation_mode)
        AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE,
                processStep, disinfectionIndex);
        log.info("getAiDisinfectionInit post, result:[{}]", aiDisinfectionInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try {
        	if (aiDisinfectionInit != null) {
        		// Kafka에 전송할 값 정의
        		LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
        		controlMap.put("tag", aiDisinfectionInit.getTag_sn());
        		controlMap.put("value", nOperationMode);
        		controlMap.put("time", strDate);
        		
        		// ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
        		ObjectMapper objectMapper = new ObjectMapper();
        		String strBody = objectMapper.writeValueAsString(controlMap);
        		kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
        		log.info("send to kafka:[{}]", strBody);
        		
        		// Kafka에 소독(후) 공정 제어모드 변경 알람 전송
        		List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
        		for (TagManageDTO dto : tagManageList) {
        			if (dto.getItm().equalsIgnoreCase("g_post_operation_mode_a") == true && CommonValue.PROCESS_DISINFECTION
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
        		databaseService.modAiDisinfectionOperationMode(nOperationMode, processStep, CommonValue.DISINFECTION_POST_STEP);
        	} else {
                log.error("Does not exist aiDisinfectionInit:[{]]", CommonValue.G_POST_OPERATION_MODE);
            }
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException Occurred in /disinfection/control/operation/post API");
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    /**
     * 소독(전) 알고리즘 설정값 변경
     * 
     * @param disinfectionPre   Front-end 소독 전염소 알고리즘 설정값을 저장하기 위한 DTO
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/control/pre/{disinfectionIndex}/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putPreControlDisinfection(@RequestBody InterfaceDisinfectionPreDTO disinfectionPre,
            @PathVariable int processStep, @PathVariable int disinfectionIndex) {
        log.debug("putPreControlDisinfection, pre:[{}]", disinfectionPre);

        boolean result = true;

        // update 1계열 전염소 최대 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_pre_set_max", disinfectionPre.getG_pre_set_max(),
                processStep, disinfectionIndex) == 1) && result;
        // update 1계열 전염소 최소 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_pre_set_min", disinfectionPre.getG_pre_set_min(),
                processStep, disinfectionIndex) == 1) && result;
        // update 1계열 전염소 변경 한계치
        result = (databaseService.modAiDisinfectionInit("g_pre_chg_limit_for_onetime",
                disinfectionPre.getG_pre_chg_limit_for_onetime(), processStep, disinfectionIndex) == 1) && result;
        // 전차염 보정주기
        result = (databaseService.modAiDisinfectionInit("g_pre_calib_cycle", disinfectionPre.getG_pre_calib_cycle(),
                processStep, disinfectionIndex) == 1) && result;
        // update 1계열 침전지 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("g_e_obj_residual_cl", disinfectionPre.getG_e_obj_residual_cl(),
                processStep, disinfectionIndex) == 1) && result;
        
        if(processStep == 2) {
        	// update 목표 침전지 잔류염소 대비 잔류염소 허용 편차
            result = (databaseService.modAiDisinfectionInit("g_e_residual_cl_holding", disinfectionPre.getG_e_residual_cl_holding(),
                    processStep, disinfectionIndex) == 1) && result;
        }

        // 업데이트가 성공하면 Kafka를 통해 설정값 전달
        if (result == true) {
            // send control value to kafka ai_control
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = simpleDateFormat.format(new Date().getTime());

            LinkedHashMap<String, Object> controlMap;
            ObjectMapper objectMapper = new ObjectMapper();

            List<AiProcessInitDTO> aiDisinfectionInitList = databaseService.getAllAiDisinfectionInit(processStep,
                    disinfectionIndex);
            log.debug("getAllAiDisinfectionInit, result:[{}]", aiDisinfectionInitList.size());

            try {
                for (AiProcessInitDTO dto : aiDisinfectionInitList) {
                    if (dto.getItm().equalsIgnoreCase("d1_target_cl") == true) {
                        // 1계열 혼화지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getD1_target_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("d2_target_cl") == true) {
                        // 2계열 혼화지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getD2_target_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_e_obj_residual_cl") == true) {
                        // 1계열 침전지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getG_e_obj_residual_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_pre_chg_limit_for_onetime") == true) {
                        // 1계열 전염소 변경 한계치 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getG_pre_chg_limit_for_onetime());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_pre_calib_cycle") == true) {
                        // 전차염 보정주기
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getG_pre_calib_cycle());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_pre_set_max") == true) {
                        // 1계열 전염소 최대 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getG_pre_set_max());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_pre_set_min") == true) {
                        // 1계열 전염소 최소 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getG_pre_set_min());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_e_residual_cl_holding") == true) {
                        // 목표 침전지 잔류염소 대비 잔류염소 허용 편차
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getG_e_residual_cl_holding());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                }
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException Occurred in /disinfection/control/pre API");
            }
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"ai_disinfection_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 소독(후) 알고리즘 설정값 변경
     * 
     * @param disinfectionPost  Front-end 소독 후염소 알고리즘 설정값을 저장하기 위한 DTO
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/control/post/{disinfectionIndex}/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putPostControlDisinfection(@RequestBody InterfaceDisinfectionPostDTO disinfectionPost,
            @PathVariable int processStep, @PathVariable int disinfectionIndex) {
        log.debug("putPostControlDisinfection, post:[{}]", disinfectionPost);

        boolean result = true;

        // update 후염소 최대 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_post_set_max", disinfectionPost.getG_post_set_max(),
                processStep, disinfectionIndex) == 1) && result;
        // update 후염소 최소 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_post_set_min", disinfectionPost.getG_post_set_min(),
                processStep, disinfectionIndex) == 1) && result;
        // update 후염소 변경 한계치
        result = (databaseService.modAiDisinfectionInit("g_post_chg_limit_for_onetime",
                disinfectionPost.getG_post_chg_limit_for_onetime(), processStep, disinfectionIndex) == 1) && result;
        // update 정수지 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("g_h_obj_residual_cl",
                disinfectionPost.getG_h_obj_residual_cl(), processStep, disinfectionIndex) == 1) && result;
        // update 후차염 보정주기
        result = (databaseService.modAiDisinfectionInit("g_post_calib_cycle", disinfectionPost.getG_post_calib_cycle(),
                processStep, disinfectionIndex) == 1) && result;
        // update 후차염 보정상수
        result = (databaseService.modAiDisinfectionInit("g_post_calib_num", disinfectionPost.getG_post_calib_num(),
                processStep, disinfectionIndex) == 1) && result;
        
        if(processStep == 2) {
        	// update 정수지 유입 잔류염소 홀딩 범위
            result = (databaseService.modAiDisinfectionInit("g_h_in_residual_cl_holding", disinfectionPost.getG_h_in_residual_cl_holding(),
                    processStep, disinfectionIndex) == 1) && result;
        }

        // 업데이트가 성공하면 Kafka를 통해 설정값 전달
        if (result == true) {
            // send control value to kafka ai_control
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = simpleDateFormat.format(new Date().getTime());

            LinkedHashMap<String, Object> controlMap;
            ObjectMapper objectMapper = new ObjectMapper();

            List<AiProcessInitDTO> aiDisinfectionInitList = databaseService.getAllAiDisinfectionInit(processStep,
                    disinfectionIndex);
            log.debug("getAllAiDisinfectionInit, result:[{}]", aiDisinfectionInitList.size());

            try {
                for (AiProcessInitDTO dto : aiDisinfectionInitList) {
                    if (dto.getItm().equalsIgnoreCase("g_ser_trg_cl") == true) {
                        // 정수지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_ser_trg_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_post_chg_limit_for_onetime") == true) {
                        // 후염소 변경 한계치 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_post_chg_limit_for_onetime());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_post_set_max") == true) {
                        // 후염소 최대 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_post_set_max());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_post_set_min") == true) {
                        // 후염소 최소 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_post_set_min());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_h_obj_residual_cl") == true) {
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_h_obj_residual_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_post_calib_cycle") == true) {
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_post_calib_cycle());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_h_in_residual_cl_holding") == true) {
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_h_in_residual_cl_holding());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    } else if (dto.getItm().equalsIgnoreCase("g_post_calib_num") == true) {
                        // 후차염 보정 상수
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_post_calib_num());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                }
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException Occurred in /disinfection/control/post API");
            }
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"ai_disinfection_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 2단계생활 전차염 시간대별 주입률 설정
     * @param disinfectionPre	Front-end 소독 전차염 알고리즘 설정값을 저장하기 위한 DTO
     * @return
     */
    @RequestMapping(value = "/disinfection/control/cholrate", method = RequestMethod.PUT)
    public ResponseEntity<String> putDisinfectionCholrateControl(@RequestBody InterfaceDisinfectionPreDTO disinfectionPre) {
        log.debug("putDisinfectionCholrateControl, pre:[{}]", disinfectionPre);

        boolean result = true;
        
        // g_pre_max_limit_0 ~ g_pre_max_limit_23
        // g_pre_min_limit_0 ~ g_pre_min_limit_23 반복 처리
        for (int i = 0; i < 24; i++) {
            try {
                // g_pre_max_limit
                float maxLimit = (float) InterfaceDisinfectionPreDTO.class.getDeclaredMethod("getG_pre_max_limit_" + i).invoke(disinfectionPre);
                result = (databaseService.modAiDisinfectionInit("g_pre_max_limit_" + i, maxLimit, 2, 1) == 1) && result;
                
                // g_pre_min_limit
                float minLimit = (float) InterfaceDisinfectionPreDTO.class.getDeclaredMethod("getG_pre_min_limit_" + i).invoke(disinfectionPre);
                result = (databaseService.modAiDisinfectionInit("g_pre_min_limit_" + i, minLimit, 2, 1) == 1) && result;
            } catch (IllegalAccessException e) {
                log.error("Error during reflection", e);
                return new ResponseEntity<>("{\"reason\":\"ai_update_fail\"}", HttpStatus.BAD_REQUEST);
			} catch (IllegalArgumentException e) {
                log.error("Error during reflection", e);
                return new ResponseEntity<>("{\"reason\":\"ai_update_fail\"}", HttpStatus.BAD_REQUEST);
			} catch (InvocationTargetException e) {
                log.error("Error during reflection", e);
                return new ResponseEntity<>("{\"reason\":\"ai_update_fail\"}", HttpStatus.BAD_REQUEST);
			} catch (NoSuchMethodException e) {
                log.error("Error during reflection", e);
                return new ResponseEntity<>("{\"reason\":\"ai_update_fail\"}", HttpStatus.BAD_REQUEST);
			} catch (SecurityException e) {
                log.error("Error during reflection", e);
                return new ResponseEntity<>("{\"reason\":\"ai_update_fail\"}", HttpStatus.BAD_REQUEST);
			}
        }
        
        if (result == true) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"ai_pre_g2_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
