//package co.irexnet.waio.WAIO_ServerAgent.controller;
//
//import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiOzoneRealtimeDTO;
//import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
//import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;
//import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceOperationModeDTO;
//import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceOzoneAiDTO;
//import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageDTO;
//import co.irexnet.waio.WAIO_ServerAgent.kafka.KafkaProducer;
//import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
//import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
//
/**
 * @deprecated 24-11-17 기준 사용하지 않는 컨트롤러
 */
//@RestController
//@EnableSwagger2
//@Slf4j
//public class OzoneController
//{
//    @Autowired
//    DatabaseServiceImpl databaseService;
//
//    @Autowired
//    KafkaProducer kafkaProducer;
//
//    // 오존 공정 최근 데이터 조회
//    @RequestMapping(value = "/ozone/latest", method = RequestMethod.GET)
//    public ResponseEntity<String> getLatestOzone()
//    {
//        log.debug("Recv getLatestOzone");
//
//        // get ai_ozone_init(io_operation_mode)
//        AiProcessInitDTO aiOzoneInit = databaseService.getAiOzoneInit(CommonValue.IO_OPERATION_MODE, 2);
//        log.debug("getAiOzoneInit, result:[{}]", aiOzoneInit != null ? 1 : 0);
//
//        // get ai_ozone_init
//        List<AiProcessInitDTO> aiOzoneInitList = databaseService.getAllAiOzoneInit(2);
//        log.debug("getAllAiOzoneInit, result:[{}]", aiOzoneInitList.size());
//
//        // get ai_ozone_realtime
//        AiOzoneRealtimeDTO aiOzoneRealtime = databaseService.getLatestAiOzoneRealtimeValue();
//        log.debug("getLatestAiOzoneRealtimeValue, result:[{}]", aiOzoneRealtime != null ? 1 : 0);
//
//        if(aiOzoneRealtime != null)
//        {
//            // JSON 처리를 위한 ObjectMapper 선언
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            // Make Response Body
//            LinkedHashMap<String, Object> aiOzoneInfo = new LinkedHashMap<>();
//
//            // update_time
//            aiOzoneInfo.put("upd_ti", aiOzoneRealtime.getUpd_ti());
//
//            // operation_mode
//            if(aiOzoneInit != null)
//            {
//                aiOzoneInfo.put("ai_opr", aiOzoneInit.getInit_val().intValue());
//            }
//            else
//            {
//                aiOzoneInfo.put("ai_opr", aiOzoneRealtime.getAi_opr());
//            }
//
//            for(AiProcessInitDTO dto : aiOzoneInitList)
//            {
//                if(dto.getItm().equalsIgnoreCase("io_set") == true)
//                {
//                    // 주입률 기준 설정
//                    aiOzoneInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("io_injection_max") == true)
//                {
//                    // 최고 주입률 설정
//                    aiOzoneInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("io_injection_min") == true)
//                {
//                    // 최소 주입률 설정
//                    aiOzoneInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("io_og_1_min") == true)
//                {
//                    // 오존발생기 1구간 최소값
//                    aiOzoneInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("io_og_1_max") == true)
//                {
//                    // 오존발생기 1구간 최대값
//                    aiOzoneInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("io_og_2_min") == true)
//                {
//                    // 오존발생기 2구간 최소값
//                    aiOzoneInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("io_og_2_max") == true)
//                {
//                    // 오존발생기 2구간 최대값
//                    aiOzoneInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("io_target_de") == true)
//                {
//                    // 목표 잔류오존 농도 설정
//                    aiOzoneInfo.put(dto.getItm(), dto.getInit_val());
//                }
//            }
//
//            // 현재 오존 주입률
//            aiOzoneInfo.put("io_inr", aiOzoneRealtime.getIo_inr());
//
//            // AI 오존 주입률 예측
//            aiOzoneInfo.put("ai_io_inr", aiOzoneRealtime.getAi_io_inr());
//
//            // 배출 오존 농도
//            aiOzoneInfo.put("io_de", aiOzoneRealtime.getIo_de());
//
//            // 1계열 전단 용존오존농도
//            aiOzoneInfo.put("io_pre1_de", aiOzoneRealtime.getIo_pre1_de()); // TODO : 데이터 JSON형식 분석팀과 협의 필요
//
//            // 1계열 중간 용존오존농도
//            aiOzoneInfo.put("io_peri1_de", aiOzoneRealtime.getIo_peri1_de()); // TODO : 데이터 JSON형식 분석팀과 협의 필요
//
//            // 1계열 후단 용존오존농도
//            aiOzoneInfo.put("io_post1_de", aiOzoneRealtime.getIo_post1_de()); // TODO : 데이터 JSON형식 분석팀과 협의 필요
//
//            // 2계열 후단 용존오존농도
//            aiOzoneInfo.put("io_pre2_de", aiOzoneRealtime.getIo_pre2_de()); // TODO : 데이터 JSON형식 분석팀과 협의 필요
//
//            // 2계열 중간 용존오존농도
//            aiOzoneInfo.put("io_peri2_de", aiOzoneRealtime.getIo_peri2_de()); // TODO : 데이터 JSON형식 분석팀과 협의 필요
//
//            // 2계열 후단 용존오존농도
//            aiOzoneInfo.put("io_ser_de", aiOzoneRealtime.getIo_ser_de());
//
//            // 오존 총 생산량
//            aiOzoneInfo.put("io_og_qu", aiOzoneRealtime.getIo_og_qu());
//
//            // HRT
//            aiOzoneInfo.put("io_hrt1", aiOzoneRealtime.getIo_hrt1());
//            aiOzoneInfo.put("io_hrt2", aiOzoneRealtime.getIo_hrt2());
//            aiOzoneInfo.put("io_hrt3", aiOzoneRealtime.getIo_hrt3());
//
//            // 구간별 k계산 오존 측정값
//            aiOzoneInfo.put("ai_io_de1", aiOzoneRealtime.getAi_io_de1());
//            aiOzoneInfo.put("ai_io_de2", aiOzoneRealtime.getAi_io_de2());
//            aiOzoneInfo.put("ai_io_de3", aiOzoneRealtime.getAi_io_de3());
//
//            // (Delta)HRT
//            aiOzoneInfo.put("io_hrt_q1", aiOzoneRealtime.getIo_hrt_q1());
//            aiOzoneInfo.put("io_hrt_q2", aiOzoneRealtime.getIo_hrt_q2());
//            aiOzoneInfo.put("io_hrt_q3", aiOzoneRealtime.getIo_hrt_q3());
//
//            // Ln 값
//            aiOzoneInfo.put("ai_io_ln2", aiOzoneRealtime.getAi_io_ln2());
//            aiOzoneInfo.put("ai_io_ln3", aiOzoneRealtime.getAi_io_ln3());
//
//            // 속도상수 k
//            aiOzoneInfo.put("ai_io_k2", aiOzoneRealtime.getAi_io_k2());
//            aiOzoneInfo.put("ai_io_k3", aiOzoneRealtime.getAi_io_k3());
//
//            // C0
//            aiOzoneInfo.put("ai_io_c0", aiOzoneRealtime.getAi_io_c0());
//
//            // ID
//            aiOzoneInfo.put("ai_io_id", aiOzoneRealtime.getAi_io_id());
//
//            // AI 결정 운전 상태
//            aiOzoneInfo.put("ai_io_state", aiOzoneRealtime.getAi_io_state());
//
//            // AI 상태별 결정 주입률
//            aiOzoneInfo.put("ai_io_normal", aiOzoneRealtime.getAi_io_normal());
//            aiOzoneInfo.put("ai_io_abnormal1", aiOzoneRealtime.getAi_io_abnormal1());
//            aiOzoneInfo.put("ai_io_abnormal2", aiOzoneRealtime.getAi_io_abnormal2());
//
//            Map<String, Object> responseBody = new HashMap<>();
//            responseBody.put("latest", aiOzoneInfo);
//
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
//            String strErrorBody = "{\"reason\":\"Empty ai_ozone\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    // 오존 공정 제어모드 변경
//    @RequestMapping(value = "/ozone/control/operation", method = RequestMethod.PUT)
//    public ResponseEntity<String> putOperationControlOzone(@RequestBody InterfaceOperationModeDTO operationMode)
//    {
//        log.debug("putOperationControlOzone, mode:[{}]", operationMode.getOperation());
//
//        // 잘못된 제어모드 값 검사
//        int nOperationMode = operationMode.getOperation();
//        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
//        {
//            log.error("Invalid operation mode:[{}]", nOperationMode);
//
//            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//
//        // Update ai_ozone_init's operation_mode
////        log.debug("update aiOzoneOperationMode:[{}], mode:[{}]",
////                databaseService.modAiOzoneOperationMode(nOperationMode), nOperationMode);
//
//        // send control value to kafka ai_ozone(io_operation_mode)
//        AiProcessInitDTO aiOzoneInit = databaseService.getAiOzoneInit(CommonValue.IO_OPERATION_MODE, 2);
//        log.debug("getAiOzoneInit, result:[{}]", aiOzoneInit != null ? 1 : 0);
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String strDate = simpleDateFormat.format(new Date().getTime());
//
//        try
//        {
//            // Kafka에 전송할 값 정의
//            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
//            controlMap.put("tag", aiOzoneInit.getTag_sn());
//            controlMap.put("value", nOperationMode);
//            controlMap.put("time", strDate);
//
//            // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
//            ObjectMapper objectMapper = new ObjectMapper();
//            String strBody = objectMapper.writeValueAsString(controlMap);
//            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
//
//            // Kafka에 오존 공정 제어모드 변경 알람 전송
//            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
//            for(TagManageDTO dto : tagManageList)
//            {
//                if(dto.getItm().equalsIgnoreCase("io_operation_mode_a") == true)
//                {
//                    // Kafka에 전송할 값 정의
//                    controlMap = new LinkedHashMap<>();
//                    controlMap.put("tag", dto.getTag_sn());
//                    controlMap.put("value", nOperationMode == CommonValue.OPERATION_MODE_MANUAL ? false : true);
//                    controlMap.put("time", strDate);
//
//                    // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
//                    objectMapper = new ObjectMapper();
//                    strBody = objectMapper.writeValueAsString(controlMap);
//                    kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
//
//                    break;
//                }
//            }
//        }
//        catch(JsonProcessingException e)
//        {
//            log.error("JsonProcessingException Occurred in /ozone/control/operation API");
//        }
//
//        return new ResponseEntity<>("", HttpStatus.OK);
//    }
//
//    // 오존 주입률 예측 이력 조회
//    @RequestMapping(value = "/ozone/history/injection", method = RequestMethod.PUT)
//    public ResponseEntity<String> putInjectionHistoryOzone(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
//    {
//        log.debug("putInjectionHistoryOzone, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());
//
//        // 오존 공정 데이터 조회
//        List<AiOzoneRealtimeDTO> aiOzoneRealtimeList =
//                databaseService.getAiOzoneRealtimeValueFromUpdateTime(dateSearchDTO);
//        log.debug("getAiOzoneRealtimeValueFromUpdateTime, result:[{}]", aiOzoneRealtimeList.size());
//        if(aiOzoneRealtimeList.size() > 0)
//        {
//            // Make Response Body
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            LinkedHashMap<String, Object> injection = new LinkedHashMap<>();
//
//            // aiOzoneRealtimeList에서 AI 오존 주입률 예측 값을 조회하여 injection에 등록
//            for(AiOzoneRealtimeDTO dto : aiOzoneRealtimeList)
//            {
//                String strDate = simpleDateFormat.format(dto.getUpd_ti());
//                injection.put(strDate, dto.getAi_io_inr());
//            }
//
//            Map<String, Object> responseBody = new HashMap<>();
//            responseBody.put("injection", injection);
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
//            String strErrorBody = "{\"reason\":\"Empty ai_ozone_realtime\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    // 계열별 용존오존농도 측정 이력 조회
//    @RequestMapping(value = "/ozone/history/de", method = RequestMethod.PUT)
//    public ResponseEntity<String> putDeHistoryOzone(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
//    {
//        log.debug("putDeHistoryOzone, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());
//
//        // 오존 공정 데이터 조회
//        List<AiOzoneRealtimeDTO> aiOzoneRealtimeList =
//                databaseService.getAiOzoneRealtimeValueFromUpdateTime(dateSearchDTO);
//        log.debug("getAiOzoneRealtimeValueFromUpdateTime, result:[{}]", aiOzoneRealtimeList.size());
//        if(aiOzoneRealtimeList.size() > 0)
//        {
//            // Make ResponseBody
//            // 계열별 정보를 저장하기 위해 pre1, pre2, peri1, peri2, post1, post2 선언
//            LinkedHashMap<String, Object> pre1Info = new LinkedHashMap<>();
//            LinkedHashMap<String, Object> peri1Info = new LinkedHashMap<>();
//            LinkedHashMap<String, Object> post1Info = new LinkedHashMap<>();
//            LinkedHashMap<String, Object> pre2Info = new LinkedHashMap<>();
//            LinkedHashMap<String, Object> peri2Info = new LinkedHashMap<>();
//            LinkedHashMap<String, Object> post2Info = new LinkedHashMap<>();
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String strDate;
//
//            // aiOzoneRealtimeList에서 계열별 용존오존농도 값을 각 Info map에 저장
//            for(AiOzoneRealtimeDTO dto : aiOzoneRealtimeList)
//            {
//                strDate = simpleDateFormat.format(dto.getUpd_ti());
//                pre1Info.put(strDate, dto.getIo_pre1_de());
//                peri1Info.put(strDate, dto.getIo_peri1_de());
//                post1Info.put(strDate, dto.getIo_post1_de());
//                pre2Info.put(strDate, dto.getIo_pre2_de());
//                peri2Info.put(strDate, dto.getIo_peri2_de());
//                post2Info.put(strDate, dto.getIo_ser_de());
//            }
//            LinkedHashMap<String, Object> series1Info = new LinkedHashMap<>();
//            LinkedHashMap<String, Object> series2Info = new LinkedHashMap<>();
//
//            // 1계열 pre/peri/post 정보 저장
//            series1Info.put("pre", pre1Info);
//            series1Info.put("peri", peri1Info);
//            series1Info.put("post", post1Info);
//
//            // 2계열 pre/peri/post 정보 저장
//            series2Info.put("pre", pre2Info);
//            series2Info.put("peri", peri2Info);
//            series2Info.put("post", post2Info);
//
//            // deInfo에 계열별 map 저장
//            LinkedHashMap<String, Object> deInfo = new LinkedHashMap<>();
//            deInfo.put("series1", series1Info);
//            deInfo.put("series2", series2Info);
//
//            Map<String, Object> responseBody = new HashMap<>();
//            responseBody.put("de", deInfo);
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
//            String strErrorBody = "{\"reason\":\"Empty ai_ozone_realtime\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    // 계열별 후단 용존오존농도 이력 조회
//    @RequestMapping(value = "/ozone/history/de/post", method = RequestMethod.PUT)
//    public ResponseEntity<String> putPostDeHistoryOzone(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
//    {
//        log.debug("putPostDeHistoryOzone, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());
//
//        // 오존 공정 데이터 조회
//        List<AiOzoneRealtimeDTO> aiOzoneRealtimeList =
//                databaseService.getAiOzoneRealtimeValueFromUpdateTime(dateSearchDTO);
//        log.debug("getAiOzoneRealtimeValueFromUpdateTime, result:[{}]", aiOzoneRealtimeList.size());
//        if(aiOzoneRealtimeList.size() > 0)
//        {
//            // Make ResponseBody
//            LinkedHashMap<String, Object> seriesInfo = new LinkedHashMap<>();
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String strDate;
//
//            // 계열별로 분리 저장을 위해 series1, series2 선언
//            LinkedHashMap<String, Float> series1PostMap = new LinkedHashMap<>();
//            LinkedHashMap<String, Float> series2PostMap = new LinkedHashMap<>();
//
//            // aiOzoneRealtimeList에서 계열별 후단 용존오존농도 값을 조회하여 series1, series2에 등록
//            for(AiOzoneRealtimeDTO dto : aiOzoneRealtimeList)
//            {
//                strDate = simpleDateFormat.format(dto.getUpd_ti());
//                series1PostMap.put(strDate, dto.getIo_post1_de());
//                series2PostMap.put(strDate, dto.getIo_ser_de());
//            }
//
//            seriesInfo.put("series1", series1PostMap);
//            seriesInfo.put("series2", series2PostMap);
//
//            Map<String, Object> responseBody = new HashMap<>();
//            responseBody.put("post_de", seriesInfo);
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
//            String strErrorBody = "{\"reason\":\"Empty ai_ozone_realtime\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    // 오존 알고리즘 설정값 변경
//    @RequestMapping(value = "/ozone/control/ai", method = RequestMethod.PUT)
//    public ResponseEntity<String> putAiControlOzone(@RequestBody InterfaceOzoneAiDTO ozoneAi)
//    {
//        log.debug("putAiControlOzone, ai:[{}]", ozoneAi);
//
//        boolean result = true;
//
//        // update 주입률 기준값
//        result = (databaseService.modAiOzoneInit("io_set", ozoneAi.getIo_set(), 2) == 1) && result;
//
//        // update 목표 잔류오존 농도
//        result = (databaseService.modAiOzoneInit("io_target_de", ozoneAi.getIo_target_de(), 2) == 1) && result;
//
//        // update 최고 주입률
//        result = (databaseService.modAiOzoneInit("io_injection_max", ozoneAi.getIo_injection_max(), 2) == 1) && result;
//
//        // update 최저 주입률
//        result = (databaseService.modAiOzoneInit("io_injection_min", ozoneAi.getIo_injection_min(), 2) == 1) && result;
//
//        // update 오존발생기 1구간 최소값
//        result = (databaseService.modAiOzoneInit("io_og_1_min", ozoneAi.getIo_og_1_min(), 2) == 1) && result;
//
//        // update 오존발생기 1구간 최대값
//        result = (databaseService.modAiOzoneInit("io_og_1_max", ozoneAi.getIo_og_1_max(), 2) == 1) && result;
//
//        // update 오존발생기 2구간 최소값
//        result = (databaseService.modAiOzoneInit("io_og_2_min", ozoneAi.getIo_og_2_min(), 2) == 1) && result;
//
//        // update 오존발생기 2구간 최대값
//        result = (databaseService.modAiOzoneInit("io_og_2_max", ozoneAi.getIo_og_2_max(), 2) == 1) && result;
//
//        if(result == true)
//        {
//            return new ResponseEntity<>("", HttpStatus.OK);
//        }
//        else
//        {
//            String strErrorBody = "{\"reason\":\"ai_ozone_init update_fail\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//    }
//}
