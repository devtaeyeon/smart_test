package co.irexnet.waio.WAIO_ServerAgent.controller;

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

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiCoagulantRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiCoagulantSimulationDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAiCoagulantSimulationDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceCoagulantAiDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceCoagulantDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;
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
public class CoagulantController {
    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    KafkaProducer kafkaProducer;

    /**
     * 약품 공정 최근 데이터 조회
     * 
     * @param processStep 공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/coagulant/latest/{processStep}", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestCoagulant(@PathVariable int processStep) {
        log.debug("Recv getLatestCoagulant");
        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());
        
        // get ai_coagulant_init(c_operation_mode)
        AiProcessInitDTO aiCoagulantInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE,
                processStep);
        log.debug("getAiCoagulantInit, result:[{}]", aiCoagulantInit != null ? 1 : 0);

        Map<String, Object> c_cf_coagulant = databaseService.selectUsrMng("c" + processStep + "_cf_coagulant");
        // get ai_coagulant_init
        List<AiProcessInitDTO> aiCoagulantInitList = databaseService.getAllAiCoagulantInit(processStep);
        log.debug("getAllAiCoagulantInit, result:[{}]", aiCoagulantInitList.size());

        // get ai_coagulant_realtime
        AiCoagulantRealtimeDTO aiCoagulantRealtime = databaseService.getLatestAiCoagulantRealtimeValue(processStep);

        log.debug("getLatestAiCoagulantRealtimeValue, result:[{}]", aiCoagulantRealtime != null ? 1 : 0);

        // // 1단계 공업이 메인일때는 2단계 생활이 부(aiCoagulantRealtime2), 2단계 생활이 메인일때는 1단계 공업이
        // 부(aiCoagulantRealtime2)
        // // -> 1단계 공업과 2단계 생활 동시에 데이터를 담아두기 위한 장치
        // if(processStep == 1 || processStep == 2) {
        // aiCoagulantRealtime2 =
        // databaseService.getLatestAiCoagulantRealtimeValue(processStep == 1 ? 2 : 1);
        // }

        // get coagulant_realtime
        List<ProcessRealtimeDTO> coagulantRealtime = databaseService.getLatestCoagulantRealtimeValue(strPartitionName,
                processStep);
        log.debug("getLatestCoagulantRealtimeValue, result:[{}]", coagulantRealtime.size());

        // get tag_manage(coagulant)
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_COAGULANT,
                processStep);
        log.debug("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_COAGULANT, tagManageList.size());

        if (aiCoagulantRealtime != null) {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiCoagulantInfo = new LinkedHashMap<>();
            // update_time
            aiCoagulantInfo.put("upd_ti", aiCoagulantRealtime.getUpd_ti());
            aiCoagulantInfo.put("ai_c_cf", aiCoagulantRealtime.getAi_c_cf());
            aiCoagulantInfo.put("c_injector1", aiCoagulantRealtime.getC_injector1());
            aiCoagulantInfo.put("c_injector2", aiCoagulantRealtime.getC_injector2());
            aiCoagulantInfo.put("c_injector3", aiCoagulantRealtime.getC_injector3());
            aiCoagulantInfo.put("c_injector4", aiCoagulantRealtime.getC_injector4());
            // operation_mode
            aiCoagulantInfo.put("ai_opr", aiCoagulantInit != null ? aiCoagulantInit.getInit_val().intValue()
                    : aiCoagulantRealtime.getAi_opr());
            // 약품종류
            aiCoagulantInfo.put("c_cf_coagulant", c_cf_coagulant.get("init_val"));
            
         // 20260520 주석처리된 블록 대신 아래로 교체
            for (AiProcessInitDTO dto : aiCoagulantInitList) {
                if (dto.getItm().equalsIgnoreCase("c_cf_max") == true) {
                    // PACS 주입률 상한
                    aiCoagulantInfo.put(dto.getItm(), dto.getInit_val());
                } else if (dto.getItm().equalsIgnoreCase("c_cf_min") == true) {
                    // PACS 주입률 하한
                    aiCoagulantInfo.put(dto.getItm(), dto.getInit_val());
                } else if (dto.getItm().equalsIgnoreCase("c_user_correct") == true) {
                    // 사용자 보정 값
                    aiCoagulantInfo.put(dto.getItm(), dto.getInit_val());
                }
                // c_user_tb_e는 제거 (화면에서 표시 안 함)
                // 트렌드 차트 점선은 TBC-2001 실시간값을 별도로 내려줌
            }

            // TBC-2001 실시간값을 c_user_tb_e 키로 내려줌 (차트 점선용)
         // TBC-2001 실시간값을 c_user_tb_e 키로 내려줌 (차트 점선용)
            if (processStep == 1) {
                for (ProcessRealtimeDTO rtDto : coagulantRealtime) {
                    if (rtDto.getTag_sn().equalsIgnoreCase("606-354-TBC-2001")) {
                        aiCoagulantInfo.put("c_user_tb_e", Float.parseFloat(rtDto.getTag_val()));
                        break;
                    }
                }
            } else {
                // 2단계, 3단계는 INIT 테이블 c_user_tb_e값으로 차트 점선 표시
                for (AiProcessInitDTO dto : aiCoagulantInitList) {
                    if (dto.getItm().equalsIgnoreCase("c_user_tb_e") == true) {
                        aiCoagulantInfo.put(dto.getItm(), dto.getInit_val());
                        break;
                    }
                }
            }

			/*
			 * for (AiProcessInitDTO dto : aiCoagulantInitList) { if
			 * (dto.getItm().equalsIgnoreCase("c_cf_max") == true) { // PACS 주입률 상한
			 * aiCoagulantInfo.put(dto.getItm(), dto.getInit_val()); } else if
			 * (dto.getItm().equalsIgnoreCase("c_cf_min") == true) { // PACS 주입률 하한
			 * aiCoagulantInfo.put(dto.getItm(), dto.getInit_val()); } else if
			 * (dto.getItm().equalsIgnoreCase("c_user_correct") == true) { // 사용자 보정 값
			 * aiCoagulantInfo.put(dto.getItm(), dto.getInit_val()); } else if
			 * (dto.getItm().equalsIgnoreCase("c_user_tb_e") == true) { // 목표 침전지 탁도
			 * aiCoagulantInfo.put(dto.getItm(), dto.getInit_val()); } }
			 */

            // Realtime data from SCADA
            // 신규 추가
            Double d1_mm_fr_pacs = 0.0;
            Double d2_mm_fr_pacs = 0.0;
            Double d3_mm_fr_pacs = 0.0;
            Double d4_mm_fr_pacs = 0.0;
            Double d_mm_fr_pacs = 0.0;
            Float b_in_fr = 0.0f;
            Float c1_cf = 0.0f;
            Float c2_cf = 0.0f;
            Float c3_cf = 0.0f;

            // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiCoagulantInfo에 등록
            for (TagManageDTO tagManage : tagManageList) {
                for (ProcessRealtimeDTO dto : coagulantRealtime) {
                    if (tagManage.getItm().equalsIgnoreCase("b_tb") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 원수 탁도
                        aiCoagulantInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    } else if (tagManage.getItm().equalsIgnoreCase("b_ph") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 원수 pH
                        aiCoagulantInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    } else if (tagManage.getItm().equalsIgnoreCase("b_te") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 원수 수온
                        aiCoagulantInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    } else if (tagManage.getItm().equalsIgnoreCase("b_cu") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 원수 전기전도도
                        aiCoagulantInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    } else if (tagManage.getItm().equalsIgnoreCase("b_in_fr") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 원수 유입 유량
                        aiCoagulantInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        b_in_fr = Float.parseFloat(dto.getTag_val());
                        break;
                    } else if (tagManage.getItm().equalsIgnoreCase("e1_tb_b") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 침전지 탁도
                        aiCoagulantInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    } 
                }
            }
            // 1차 또는 2차일 경우
            if (processStep == 1 || processStep == 2) {
                // 실시간 테이블의 응집제 주입량은 모두 1차 공업의 태그정보에 존재함
                // FIXED 2024-01-29 각 단계별 RT 테이블의 태그를 바라보도록 수정
                coagulantRealtime = databaseService.getLatestCoagulantRealtimeValue(strPartitionName, processStep);
                tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_COAGULANT, processStep);

                for (TagManageDTO tagManage : tagManageList) {
                    for (ProcessRealtimeDTO dto : coagulantRealtime) {
                        if (tagManage.getItm().equalsIgnoreCase("d1_mm_fr_pacs") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 1번 투입량
                            d1_mm_fr_pacs = Double.parseDouble(dto.getTag_val());
                            break;
                        } else if (tagManage.getItm().equalsIgnoreCase("d2_mm_fr_pacs") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 2번 투입량
                            d2_mm_fr_pacs = Double.parseDouble(dto.getTag_val());
                            break;
                        } else if (tagManage.getItm().equalsIgnoreCase("d3_mm_fr_pacs") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 3번 투입량
                            d3_mm_fr_pacs = Double.parseDouble(dto.getTag_val());
                            break;
                        } else if (tagManage.getItm().equalsIgnoreCase("d4_mm_fr_pacs") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 4번 투입량
                            d4_mm_fr_pacs = Double.parseDouble(dto.getTag_val());
                            break;
                        } else if (tagManage.getItm().equalsIgnoreCase("d_mm_fr_pacs") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 2단계 생활 투입량
                            d_mm_fr_pacs = Double.parseDouble(dto.getTag_val());
                            break;
                        }
                    }
                }
                // PACS 1/2/3/4번 응집제 주입량
                aiCoagulantInfo.put("c1_mm_fr", d1_mm_fr_pacs);
                aiCoagulantInfo.put("c2_mm_fr", d2_mm_fr_pacs);
                aiCoagulantInfo.put("c3_mm_fr", d3_mm_fr_pacs);
                aiCoagulantInfo.put("c4_mm_fr", d4_mm_fr_pacs);
                aiCoagulantInfo.put("c_mm_fr", d_mm_fr_pacs); // 2단계생활

                // PACS 1/2/3/4번 응집제 주입률 (응집제 주입량 / 원수유입양 * 1000)
                aiCoagulantInfo.put("c1_cf", d1_mm_fr_pacs / b_in_fr * 1000);
                aiCoagulantInfo.put("c2_cf", d2_mm_fr_pacs / b_in_fr * 1000);
                aiCoagulantInfo.put("c3_cf", d3_mm_fr_pacs / b_in_fr * 1000);
                aiCoagulantInfo.put("c4_cf", d4_mm_fr_pacs / b_in_fr * 1000);
                aiCoagulantInfo.put("c_cf", d_mm_fr_pacs / b_in_fr * 1000); // 2단계생활
            } else {
                // 3차일 경우
                coagulantRealtime = databaseService.getLatestCoagulantRealtimeValue(strPartitionName, processStep);
                tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_COAGULANT, processStep);

                for (TagManageDTO tagManage : tagManageList) {
                    for (ProcessRealtimeDTO dto : coagulantRealtime) {
                        if (tagManage.getItm().equalsIgnoreCase("c1_cf") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 1번 주입률
                            c1_cf = Float.parseFloat(dto.getTag_val());
                            break;
                        } else if (tagManage.getItm().equalsIgnoreCase("c2_cf") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 2번 주입률
                            c2_cf = Float.parseFloat(dto.getTag_val());
                            break;
                        } else if (tagManage.getItm().equalsIgnoreCase("c3_cf") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 3번 주입률
                            c3_cf = Float.parseFloat(dto.getTag_val());
                            break;
                        } else if (tagManage.getItm().equalsIgnoreCase("c1_mm_fr") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 3단계 아산(정) 응집제약품펌프1 투입값
                            aiCoagulantInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                            break;
                        } else if (tagManage.getItm().equalsIgnoreCase("c2_mm_fr") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 3단계 아산(정) 응집제약품펌프2 투입값
                            aiCoagulantInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                            break;
                        } else if (tagManage.getItm().equalsIgnoreCase("c3_mm_fr") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 3단계 아산(정) 응집제약품펌프3 투입값
                            aiCoagulantInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                            break;
                        }
                    }
                }
                // 응집제 주입률
                aiCoagulantInfo.put("c1_cf", c1_cf);
                aiCoagulantInfo.put("c2_cf", c2_cf);
                aiCoagulantInfo.put("c3_cf", c3_cf);
            }

            // ai_cluster_id
            aiCoagulantInfo.put("ai_clst_id", aiCoagulantRealtime.getAi_clst_id());
            // AI 주입률 예측 최종값
            aiCoagulantInfo.put("ai_c_cf", aiCoagulantRealtime.getAi_c_cf());
            // 사용자보정 처리 이전 주입률
            aiCoagulantInfo.put("ai_c_cf_norm_co", aiCoagulantRealtime.getAi_c_cf_norm_co());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiCoagulantInfo);
            // ObjectMapper를 통해 JSON 값을 String으로 변환
            String strBody;
            try {
                strBody = objectMapper.writeValueAsString(responseBody);
            } catch (JsonProcessingException e) {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"Empty ai_coagulant\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 약품 공정 제어모드 변경
     * 
     * @param operationMode 제어모드
     * @param processStep   공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/coagulant/control/operation/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlCoagulant(@RequestBody InterfaceOperationModeDTO operationMode,
            @PathVariable int processStep) {
        log.info("putOperationControlCoagulant, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if (nOperationMode < CommonValue.OPERATION_MODE_MANUAL
                || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO) {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // send control value to kafka ai_control(c_operation_mode)
        AiProcessInitDTO aiCoagulantInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE,
                processStep);
        log.info("getAiCoagulantInit, result:[{}]", aiCoagulantInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try {
        	if(aiCoagulantInit != null) {
        		// Kafka에 전송할 값 정의
        		LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
        		controlMap.put("tag", aiCoagulantInit.getTag_sn());
        		controlMap.put("value", nOperationMode);
        		controlMap.put("time", strDate);
        		
        		// ObjectMapper를 통해 JSON 값을 String으로 변환하여 kafka 전송
        		ObjectMapper objectMapper = new ObjectMapper();
        		String strBody = objectMapper.writeValueAsString(controlMap);
        		kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
        		log.info("send to kafka:[{}]", strBody);
        		
        		// Kafka에 약품 공정 제어모드 변경 알람 전송
        		List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
        		for (TagManageDTO dto : tagManageList) {
        			if (dto.getItm().equalsIgnoreCase("c_operation_mode_a") == true
        					&& dto.getProc_cd().equalsIgnoreCase(CommonValue.PROCESS_COAGULANT + processStep)) {
        				// Kafka에 전송할 값 정의
        				controlMap = new LinkedHashMap<>();
        				controlMap.put("tag", dto.getTag_sn());
        				controlMap.put("value", nOperationMode == CommonValue.OPERATION_MODE_MANUAL ? false : true);
        				controlMap.put("time", strDate);
        				
        				// ObjectMapper를 통해 JSON 값을 String으로 변환하여 kafka 전송
        				objectMapper = new ObjectMapper();
        				strBody = objectMapper.writeValueAsString(controlMap);
        				kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
        				log.info("send to kafka:[{}]", strBody);
        				break;
        			}
        		}
        		// DB UPDATE
        		databaseService.modAiCoagulantOperationMode(nOperationMode, processStep);
        	} else {
                log.error("Does not exist aiCoagulantInit:[{]]", CommonValue.C_OPERATION_MODE);
            }
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException Occurred in /coagulant/control/operation API");
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    /**
     * 약품 주입률 상/하한 수정
     * 
     * @param coagulantAi Front-end 주입률 상/하한 값을 저장하기 위한 DTO
     * @param processStep 공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/coagulant/control/ai/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putAiControlCoagulant(@RequestBody InterfaceCoagulantAiDTO coagulantAi,
            @PathVariable int processStep) {
        log.debug("putAiControlCoagulant, ai:[{}]", coagulantAi);

        boolean result = true;

        // update APAC 주입률 상한
        result = (databaseService.modAiCoagulantInit("c_cf_max", coagulantAi.getC_cf_max(), processStep) == 1)
                && result;
        // update APAC 주입률 하한
        result = (databaseService.modAiCoagulantInit("c_cf_min", coagulantAi.getC_cf_min(), processStep) == 1)
                && result;
        // update 목표 침전지 탁도
        // 20260520 온더시스 이현수 아산정수장 백지엽 차장 요청사항
        // 약품공정 웹사이트상 목표 침전지 탁도값이 실제 ai입력변수에 들어가는 값 일치 되게 요청  단 tag_mng상태값으로 인해 일단은 지우는 방향으로 수정
        //result = (databaseService.modAiCoagulantInit("c_user_tb_e", coagulantAi.getC_user_tb_e(), processStep) == 1)
                //&& result;
        
        if (result == true) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"ai_coagulant_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @deprecated 미사용
     */
    @RequestMapping(value = "/coagulant/simulation", method = RequestMethod.POST)
    public ResponseEntity<String> postSimulationCoagulant(@RequestBody InterfaceAiCoagulantSimulationDTO simulation) {
        log.debug("Recv postSimulationCoagulant, [{}]", simulation);

        // Make simulation data
        AiCoagulantSimulationDTO aiCoagulantSimulation = new AiCoagulantSimulationDTO();
        aiCoagulantSimulation.setReg_time(new Date());
        aiCoagulantSimulation.setState(CommonValue.STATE_STANDBY);
        aiCoagulantSimulation.setB_tb(simulation.getB_tb());
        aiCoagulantSimulation.setB_ph(simulation.getB_ph());
        aiCoagulantSimulation.setB_te(simulation.getB_te());
        aiCoagulantSimulation.setB_cu(simulation.getB_cu());
        aiCoagulantSimulation.setB_in_fr(simulation.getB_in_fr());
        aiCoagulantSimulation.setE1_tb(simulation.getE1_tb());
        aiCoagulantSimulation.setE2_tb(simulation.getE2_tb());

        // Insert simulation data
        int nResult = databaseService.addAiCoagulantSimulation(aiCoagulantSimulation);
        log.debug("addCoagulantSimulation, result:[{}]", nResult);
        if (nResult > 0) {
            return new ResponseEntity<>("", HttpStatus.CREATED);
        } else {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.CONFLICT.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.CONFLICT);
        }
    }

    /**
     * @deprecated 미사용
     */
    @RequestMapping(value = "/coagulant/simulation", method = RequestMethod.PUT)
    public ResponseEntity<String> getSimulationCoagulant(@RequestBody InterfaceDateSearchDTO dateSearch) {
        log.debug("getSimulationCoagulant, start:[{}], end:[{}]", dateSearch.getStart_time(), dateSearch.getEnd_time());

        // Get simulation data
        List<AiCoagulantSimulationDTO> aiCoagulantSimulationList = databaseService.getAiCoagulantSimulation(dateSearch);
        log.debug("getAiCoagulantSimulation, result:[{}]", aiCoagulantSimulationList.size());

        if (aiCoagulantSimulationList.size() > 0) {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("simulation", aiCoagulantSimulationList);

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
            String strErrorBody = "{\"reason\":\"Empty ai_coagulant_simulation\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 응집제 종류 수정
     * 
     * @param coagulantAi 약품 INIT값을 저장하기 위한 DTO
     * @param processStep 공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/coagulants/control/coagulant/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putControlCoagulant(@RequestBody InterfaceCoagulantAiDTO coagulantAi,
            @PathVariable int processStep) {
        log.debug("putAiControlCoagulant, ai:[{}]", coagulantAi);

        boolean result = true;

        result = (databaseService.modUsrMng("c" + processStep + "_cf_coagulant", coagulantAi.getC_cf_coagulant()) == 1)
                && result;

        if (result == true) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"ai_coagulant_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 사용자 보정 값 설정
     * 
     * @param coagulantAi 약품 INIT값을 저장하기 위한 DTO
     * @param processStep 공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/coagulants/control/userCorrect/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putAiControlUserCorrect(@RequestBody InterfaceCoagulantAiDTO coagulantAi,
            @PathVariable int processStep) {
        log.debug("putAiControlUserCorrect, user_correct:[{}]", coagulantAi);

        boolean result = true;

        // update 사용자 보정 값
        result = (databaseService.modAiCoagulantInit("c_user_correct", coagulantAi.getC_user_correct(), processStep) == 1)
                && result;

        if (result == true) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"ai_coagulant_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
