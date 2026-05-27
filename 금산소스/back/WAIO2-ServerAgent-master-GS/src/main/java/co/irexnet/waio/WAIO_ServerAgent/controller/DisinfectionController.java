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

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiDisinfectionRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDisinfectionPeriDTO;
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
public class DisinfectionController
{
    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    KafkaProducer kafkaProducer;

    /**
     * 소독 공정 최근 데이터 조회
     * 
     * @param disinfectionIndex 전차염: 1, 중차염: 2, 후차염: 3
     * @param processStep       공정단계
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/latest/{processStep}/{disinfectionStep}", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestDisinfection(@PathVariable int processStep, @PathVariable int disinfectionStep)
    {
        log.debug("Recv getLatestDisinfection");

        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());
//        strPartitionName = "20180801";// FIXME 현재 날짜 수정
        // get ai_disinfection_init(g_pre_operation_mode)
        AiProcessInitDTO aiPreDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE, processStep, 1);
        log.debug("getAiDisinfectionInit pre, result:[{}]", aiPreDisinfectionInit != null ? 1 : 0);
        
        AiProcessInitDTO aiPostDisinfectionInit = null;
        
        // get ai_disinfection_init(g_post_operation_mode)
        aiPostDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE, processStep, 3);
        log.debug("getAiDisinfectionInit post, result:[{}]", aiPostDisinfectionInit != null ? 1 : 0);

        

        // get ai_disinfection_init
        List<AiProcessInitDTO> aiDisinfectionInitList = databaseService.getAllAiDisinfectionInit(processStep, disinfectionStep);
        log.debug("getAllAiDisinfectionInit, result:[{}]", aiDisinfectionInitList.size());

        // get ai_disinfection_realtime
        AiDisinfectionRealtimeDTO aiDisinfectionRealtime = databaseService.getLatestAiDisinfectionRealtimeValue(processStep, disinfectionStep);
        log.debug("getLatestAiDisinfectionRealtimeValue, result:[{}]", aiDisinfectionRealtime != null ? 1 : 0);

        // get disinfection_realtime
        List<ProcessRealtimeDTO> disinfectionRealtime = databaseService.getLatestDisinfectionRealtimeValue(strPartitionName, processStep);
        log.debug("getLatestDisinfectionRealtimeValue, result:[{}]", disinfectionRealtime.size());

        // get tag_manage(disinfection)
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_DISINFECTION, processStep);
        log.debug("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_DISINFECTION, tagManageList.size());

        if(aiDisinfectionRealtime != null)
        {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiDisinfectionInfo = new LinkedHashMap<>();

            // update_time
            aiDisinfectionInfo.put("upd_ti", aiDisinfectionRealtime.getUpd_ti());

            // pre_operation_mode
            if(aiPreDisinfectionInit != null)
            {
                aiDisinfectionInfo.put("pre_ai_opr", aiPreDisinfectionInit.getInit_val().intValue());
            }
            else
            {
                aiDisinfectionInfo.put("pre_ai_opr", aiDisinfectionRealtime.getG_pre_operation_mode());
            }
            
            // post_operataion_mode
            if(aiPostDisinfectionInit != null)
            {
                aiDisinfectionInfo.put("post_ai_opr", aiPostDisinfectionInit.getInit_val().intValue());
            }
            else
            {
                aiDisinfectionInfo.put("post_ai_opr", aiDisinfectionRealtime.getG_post_operation_mode());
            }


            // Realtime data from SCADA
            // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiDisinfectionInfo에 등록
            for(TagManageDTO tagManage : tagManageList)
            {
                for(ProcessRealtimeDTO dto : disinfectionRealtime)
                {
                    if(tagManage.getItm().equalsIgnoreCase("b_te") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {
                        // 원수 수온
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
//                    else if(tagManage.getItm().equalsIgnoreCase("air_te") == true &&
//                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
//                    {
//                        // 기온
//                       aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
//                        break;
//                    }
                    else if(tagManage.getItm().equalsIgnoreCase("b_in_fr") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {
                        // 적용 유량
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("h_in_fr") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {
                        // 정수지 유입 유량
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("h_tb") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {	
                    	//정수 탁도
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("h_ph") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {	
                    	//정수 ph
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("b_tb") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {	
                    	//원수 탁도
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("b_ph") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {	
                    	//원수 pH
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("b_cu") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {	
                    	//원수 전기전도도
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("b_al") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {	
                    	//원수 알카리도
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("mnr") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {	
                    	//취수 망간
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("f_tb") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {	
                    	//여과수 통합 탁도
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("g_b_residual_cl") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {	
                    	//원수 잔류염소
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                    else if(tagManage.getItm().equalsIgnoreCase("g_f_out_residual_cl") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {	
                    	//여과수 통합 잔류염소
                        aiDisinfectionInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    }
                }
            }
            for(AiProcessInitDTO dto : aiDisinfectionInitList)
            {
            	//전차염
                if(dto.getItm().equalsIgnoreCase("g_pre_set_max") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 전차염 주입률 상한값
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
                if(dto.getItm().equalsIgnoreCase("g_pre_set_min") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 전차염 주입률 하한값
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
                if(dto.getItm().equalsIgnoreCase("g_pre_chg_limit_for_onetime") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 1회 변경 제한 주입률 값
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
                if(dto.getItm().equalsIgnoreCase("g_pre_calib_cycle") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 전차염 보정 주기
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
                if(dto.getItm().equalsIgnoreCase("g_d_obj_residual_cl") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 혼화지 목표 잔류 염소
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
                if(dto.getItm().equalsIgnoreCase("g_d_residual_cl_holding") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 혼화지 잔류염소 홀딩 범위
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
                
                
            	//후차염
                if(dto.getItm().equalsIgnoreCase("g_post_set_max") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 후차염 주입률 상한값
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
                if(dto.getItm().equalsIgnoreCase("g_post_set_min") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 후차염 주입률 하한값
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
                if(dto.getItm().equalsIgnoreCase("g_post_chg_limit_for_onetime") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 1회 변경 제한 주입률 값
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
                if(dto.getItm().equalsIgnoreCase("g_post_calib_cycle") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 후차염 보정 주기
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
                if(dto.getItm().equalsIgnoreCase("g_h_in_obj_residual_cl") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 여과지 유출 목표 잔류 염소
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
                if(dto.getItm().equalsIgnoreCase("g_post_chol_rate_holding_time") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 주입률 변경 후 잔류염소 미변동 시 대기시간
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
                if(dto.getItm().equalsIgnoreCase("g_h_in_residual_cl_holding") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
                {
                    // 정수지 유입 잔류염소 홀딩 범위
                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
                }
//                if(dto.getItm().equalsIgnoreCase("d1_target_cl") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
//                {
//                    // 1계열 혼화지 목표 잔류염소
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("d2_target_cl") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
//                {
//                    // 2계열 혼화지 목표 잔류염소
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("e1_target_cl") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
//                {
//                    // 1계열 침전지 목표 잔류염소
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("e2_target_cl") == true) // TODO : 데이터 JSON형식 분석팀과 협의 필요
//                {
//                    // 2계열 침전지 목표 잔류염소
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_ser_trg_cl") == true)
//                {
//                    // 정수지 목표 잔류염소
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_pre1_set_max") == true)
//                {
//                    // 1계열 전염소 최대 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_pre1_set_min") == true)
//                {
//                    // 1계열 전염소 최소 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_pre1_chg_limit_for_onetime") == true)
//                {
//                    // 1계열 전염소 변경 한계치
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_pre2_set_max") == true)
//                {
//                    // 2계열 전염소 최대 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_pre2_set_min") == true)
//                {
//                    // 2계열 전염소 최소 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_pre2_chg_limit_for_onetime") == true)
//                {
//                    // 2계열 전염소 주입률 변경 한계치
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_peri1_set_max") == true)
//                {
//                    // 1계열 중염소 최대 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_peri1_set_min") == true)
//                {
//                    // 1계열 중염소 최소 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_peri1_chg_limit_for_onetime") == true)
//                {
//                    // 1계열 중염소 주입률 변경 한계치
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_peri2_set_max") == true)
//                {
//                    // 2계열 중염소 최대 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_peri2_set_min") == true)
//                {
//                    // 2계열 중염소 최소 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_peri2_chg_limit_for_onetime") == true)
//                {
//                    // 2계열 중염소 주입률 변경 한계치
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_post_set_max") == true)
//                {
//                    // 후염소 최대 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_post_set_min") == true)
//                {
//                    // 후염소 최소 주입률 설정
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
//                else if(dto.getItm().equalsIgnoreCase("g_post_chg_limit_for_onetime") == true)
//                {
//                    // 후염소 주입률 변경 한계치
//                    aiDisinfectionInfo.put(dto.getItm(), dto.getInit_val());
//                }
            }

            // 주요인자
            // 혼화지 잔류염소
            aiDisinfectionInfo.put("g_d_residual_cl", aiDisinfectionRealtime.getG_d_residual_cl());

            // 침전지 잔류염소
//            aiDisinfectionInfo.put("g_e_residual_cl", aiDisinfectionRealtime.getG_e_residual_cl());

            // 여과수 통합 잔류염소
            aiDisinfectionInfo.put("g_f_out_residual_cl", aiDisinfectionRealtime.getG_f_out_residual_cl());
            
            // 계열별 전염소 주입률
            aiDisinfectionInfo.put("g_pre_chol_rate", aiDisinfectionRealtime.getG_pre_chol_rate());
            
            // 계열별 중염소 주입률
            aiDisinfectionInfo.put("g_peri_chol_rate", aiDisinfectionRealtime.getG_peri_chol_rate());
            
            // 후염소 주입률
            aiDisinfectionInfo.put("g_post_chol_rate", aiDisinfectionRealtime.getG_post_chol_rate());

            // 정수지 유입 잔류염소
            aiDisinfectionInfo.put("g_h_in_residual_cl", aiDisinfectionRealtime.getG_h_in_residual_cl());
            
            // 정수지 유출 잔류염소
            aiDisinfectionInfo.put("g_h_out_residual_cl", aiDisinfectionRealtime.getG_h_out_residual_cl());
            
            // 정수지 유입 잔류염소 (평활)
            aiDisinfectionInfo.put("g_h_in_residual_cl_prep", aiDisinfectionRealtime.getG_h_in_residual_cl_prep());
            
//            // 정수지 잔류염소 (2단계 공업)
//            aiDisinfectionInfo.put("g_h_residual_cl", aiDisinfectionRealtime.getG_h_residual_cl());

            // 계열별 AI 전염소 증발량 예측
            aiDisinfectionInfo.put("ai_g_evap", aiDisinfectionRealtime.getAi_g_evap());
            
            // AI 주입률 예측
            aiDisinfectionInfo.put("ai_g_chol_rate", aiDisinfectionRealtime.getAi_g_chol_rate());

            // AI 염소소모량예측값
            aiDisinfectionInfo.put("ai_g_consumption", aiDisinfectionRealtime.getAi_g_consumption());
            
            //대시보드용 주입률 데이터 ( 추후 변경 예정 FIXME 
            AiDisinfectionRealtimeDTO livPreAiDisinfectionRealtime = databaseService.getLatestAiDisinfectionRealtimeValue(1, 1);
            AiDisinfectionRealtimeDTO livPostAiDisinfectionRealtime = databaseService.getLatestAiDisinfectionRealtimeValue(1, 3);
            
            // 전염소 주입률 (대시보드)
            aiDisinfectionInfo.put("g_pre_chol_rate_liv_dashboard", livPreAiDisinfectionRealtime.getG_pre_chol_rate());
            // 후염소 주입률 (대시보드)
            aiDisinfectionInfo.put("g_post_chol_rate_dashboard", livPostAiDisinfectionRealtime.getG_post_chol_rate());
            
            //펌프 run 여부
            aiDisinfectionInfo.put("g_pump_1_run", aiDisinfectionRealtime.getG_pump_1_run());
            aiDisinfectionInfo.put("g_pump_2_run", aiDisinfectionRealtime.getG_pump_2_run());            
            
            //주입 후 경과시간
            aiDisinfectionInfo.put("g_elapsed_time", aiDisinfectionRealtime.getG_elapsed_time());                        
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiDisinfectionInfo);

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
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 주입률 측정 이력 조회
     * 
     * @param dateSearchDTO     Front-end 시간 검색 값을 저장하기 위한 DTO
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 중차염: 2, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/history/cholrate/{processStep}/{disinfectionStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> getPostCorrectedHistoryDisinfection(@RequestBody InterfaceDateSearchDTO dateSearchDTO, @PathVariable int processStep, @PathVariable int disinfectionStep)
    {
        log.debug("getPostCorrectedHistoryDisinfection, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 소독 공정 데이터 조회
        List<AiDisinfectionRealtimeDTO> aiDisinfectionRealtimeList =
                databaseService.getAiDisinfectionRealtimeValueFromUpdateTime(dateSearchDTO, processStep, disinfectionStep);
        log.debug("getAiDisinfectionRealtimeValueFromUpdateTime, result:[{}]", aiDisinfectionRealtimeList.size());

        if(aiDisinfectionRealtimeList.size() > 0)
        {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> cholrate = new LinkedHashMap<>();

            // aiDisinfectionRealtimeList에서 주입률 예측을 조회하여 corrected에 등록
            for(AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList)
            {
            	String strDate = simpleDateFormat.format(dto.getUpd_ti());
                cholrate.put(strDate, dto.getAi_g_chol_rate());
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("cholrate", cholrate);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody;
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
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 염소 소모량 예측
     * 
     * @param dateSearchDTO     Front-end 시간 검색 값을 저장하기 위한 DTO
     * @param disinfectionIndex 전차염: 1, 중차염: 2, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/consumption/{disinfectionStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> getPostDisinfectionConsumption(@RequestBody InterfaceDateSearchDTO dateSearchDTO, @PathVariable int disinfectionStep)
    {
        log.debug("getPostCorrectedHistoryDisinfection, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 후차염 공정 데이터 조회
        List<AiDisinfectionRealtimeDTO> aiDisinfectionRealtimeList =
                databaseService.getAiDisinfectionRealtimeValueFromUpdateTime(dateSearchDTO, 1, disinfectionStep);
        log.debug("getAiDisinfectionRealtimeValueFromUpdateTime, result:[{}]", aiDisinfectionRealtimeList.size());

        if(aiDisinfectionRealtimeList.size() > 0)
        {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> aiGConsumption = new LinkedHashMap<>();

            // aiDisinfectionRealtimeList에서 후차염 주입률 보정예측을 조회하여 corrected에 등록
            for(AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList)
            {
            	String strDate = simpleDateFormat.format(dto.getUpd_ti());
            	aiGConsumption.put(strDate, dto.getAi_g_consumption());
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("ai_g_consumption", aiGConsumption);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody;
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
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
    
    
    
    /**
     * 증발량 예측 이력 조회
     * 
     * @param dateSearchDTO     Front-end 시간 검색 값을 저장하기 위한 DTO
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 중차염: 2, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/history/evaporation/{processStep}/{disinfectionStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> getEvaporationHistoryDisinfection(@RequestBody InterfaceDateSearchDTO dateSearchDTO, @PathVariable int processStep, @PathVariable int disinfectionStep)
    {
        log.debug("getPostCorrectedHistoryDisinfection, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 소독 공정 데이터 조회
        List<AiDisinfectionRealtimeDTO> aiDisinfectionRealtimeList =
                databaseService.getAiDisinfectionRealtimeValueFromUpdateTime(dateSearchDTO, processStep, disinfectionStep);
        log.debug("getAiDisinfectionRealtimeValueFromUpdateTime, result:[{}]", aiDisinfectionRealtimeList.size());

        if(aiDisinfectionRealtimeList.size() > 0)
        {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> evaporation = new LinkedHashMap<>();

            // aiDisinfectionRealtimeList에서 증발량 예측을 조회하여 corrected에 등록
            for(AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList)
            {
                String strDate = simpleDateFormat.format(dto.getUpd_ti());
                evaporation.put(strDate, dto.getAi_g_evap());
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("pre_evaporation", evaporation);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody;
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
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
    
    
    /**
     * 소독(전) 공정 제어모드 변경
     * 
     * @param operationMode     제어모드
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 중차염: 2, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/control/operation/pre/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlPreDisinfection(@RequestBody InterfaceOperationModeDTO operationMode, @PathVariable int processStep)
    {
        log.info("putOperationControlPreDisinfection, mode:[{}]", operationMode.getOperation());

        //소독(전) disinfectionStep = 1
        int disinfectionStep = 1;
        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_disinfection_init's operation_mode
//        log.debug("update aiDisinfectionOperationMode:[{}], mode:[{}]",
//                databaseService.modAiDisinfectionOperationMode(nOperationMode), nOperationMode);

        // update operation mode
        databaseService.modAiDisinfectionOperationMode(nOperationMode, processStep, disinfectionStep);
        
        // send control value to kafka ai_control(g_operation_mode)
        AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE, processStep, disinfectionStep);
        log.debug("getAiDisinfectionInit pre, result:[{}]", aiDisinfectionInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            if(aiDisinfectionInit != null) {
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
	            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI, CommonValue.PROCESS_DISINFECTION, processStep);
	            for(TagManageDTO dto : tagManageList)
	            {
	                if(dto.getItm().equalsIgnoreCase("g_pre_operation_mode_a") == true)
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
                log.error("Does not exist aiDisinfectionInit:[{}]", CommonValue.G_OPERATION_MODE);
            }
        }
        catch(JsonProcessingException e)
        {
            log.error("JsonProcessingException Occurred in /disinfection/control/operation/pre API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    /**
     * 소독(중) 공정 제어모드 변경
     * 
     * @param operationMode     제어모드
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 중차염: 2, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/control/operation/peri/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlPeriDisinfection(@RequestBody InterfaceOperationModeDTO operationMode, @PathVariable int processStep)
    {
        //소독(중) disinfectionStep = 1
        int disinfectionStep = 2;
        log.info("putOperationControlPeriDisinfection, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_disinfection_init's operation_mode
//        log.debug("update aiDisinfectionOperationMode:[{}], mode:[{}]",
//                databaseService.modAiDisinfectionOperationMode(nOperationMode), nOperationMode);

        // send control value to kafka ai_control(g_operation_mode)
        AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PERI_OPERATION_MODE, processStep, disinfectionStep);
        log.debug("getAiDisinfectionInit peri, result:[{}]", aiDisinfectionInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            if(aiDisinfectionInit != null) {
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
	
	            // Kafka에 소독(중) 공정 제어모드 변경 알람 전송
	            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI, CommonValue.PROCESS_DISINFECTION, processStep);
	            for(TagManageDTO dto : tagManageList)
	            {
	                if(dto.getItm().equalsIgnoreCase("g_peri_operation_mode_a") == true)
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
                log.error("Does not exist aiDisinfectionInit:[{}]", CommonValue.G_OPERATION_MODE);
            }
        }
        catch(JsonProcessingException e)
        {
            log.error("JsonProcessingException Occurred in /disinfection/control/operation/peri API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    /**
     * 소독(후) 공정 제어모드 변경
     * 
     * @param operationMode     제어모드
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 중차염: 2, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/control/operation/post/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlPostDisinfection(@RequestBody InterfaceOperationModeDTO operationMode, @PathVariable int processStep)
    {
        //소독(후) disinfectionStep = 3
        int disinfectionStep = 3;
        
        log.info("putOperationControlPostDisinfection, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_disinfection_init's operation_mode
//        log.debug("update aiDisinfectionOperationMode:[{}], mode:[{}]",
//                databaseService.modAiDisinfectionOperationMode(nOperationMode), nOperationMode);

        // update operation mode
        databaseService.modAiDisinfectionOperationMode(nOperationMode, processStep, disinfectionStep);
        
        // send control value to kafka ai_control(g_operation_mode)
        AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE, processStep, disinfectionStep);
        log.debug("getAiDisinfectionInit post, result:[{}]", aiDisinfectionInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            if(aiDisinfectionInit != null) {
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
	            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI, CommonValue.PROCESS_DISINFECTION, processStep);
	            for(TagManageDTO dto : tagManageList)
	            {
	                if(dto.getItm().equalsIgnoreCase("g_post_operation_mode_a") == true)
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
                log.error("Does not exist aiDisinfectionInit:[{}]", CommonValue.G_OPERATION_MODE);
            }
        }
        catch(JsonProcessingException e)
        {
            log.error("JsonProcessingException Occurred in /disinfection/control/operation/post API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    /**
     * 소독(전) 알고리즘 설정값 변경
     * 
     * @param disinfectionPre   Front-end 소독 전염소 알고리즘 설정값을 저장하기 위한 DTO
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 중차염: 2, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/control/pre/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putPreControlDisinfection(@RequestBody InterfaceDisinfectionPreDTO disinfectionPre, @PathVariable int processStep)
    {
        log.debug("putPreControlDisinfection, pre:[{}]", disinfectionPre);
        //소독(전) disinfectionStep = 1;
        int disinfectionStep = 1;
        
        boolean result = true;

        // update 전염소 최대 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_pre_set_max", disinfectionPre.getG_pre_set_max(), processStep, disinfectionStep) == 1) && result;

        // update 전염소 최소 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_pre_set_min", disinfectionPre.getG_pre_set_min(), processStep, disinfectionStep) == 1) && result;

        // update 전염소 변경 한계치
        result = (databaseService.modAiDisinfectionInit("g_pre_chg_limit_for_onetime", disinfectionPre.getG_pre_chg_limit_for_onetime(), processStep, disinfectionStep) == 1) && result;

        // update 보정주기
        result = (databaseService.modAiDisinfectionInit("g_pre_calib_cycle", disinfectionPre.getG_pre_calib_cycle(), processStep, disinfectionStep) == 1) && result;

        // update 혼화지 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("g_d_obj_residual_cl", disinfectionPre.getG_d_obj_residual_cl(), processStep, disinfectionStep) == 1) && result;

        // update 혼화지 잔류염소 홀딩 범위
        result = (databaseService.modAiDisinfectionInit("g_d_residual_cl_holding", disinfectionPre.getG_d_residual_cl_holding(), processStep, disinfectionStep) == 1) && result;
       
        // 업데이트가 성공하면 Kafka를 통해 설정값 전달
        if(result == true)
        {
            // send control value to kafka ai_control
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = simpleDateFormat.format(new Date().getTime());

            LinkedHashMap<String, Object> controlMap;
            ObjectMapper objectMapper = new ObjectMapper();

            List<AiProcessInitDTO> aiDisinfectionInitList = databaseService.getAllAiDisinfectionInit(processStep, disinfectionStep);
            log.debug("getAllAiDisinfectionInit, result:[{}]", aiDisinfectionInitList.size());

            try
            {
                for(AiProcessInitDTO dto : aiDisinfectionInitList)
                {
                    if(dto.getItm().equalsIgnoreCase("g_pre_set_max") == true)
                    {
                        // 전염소 최대 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getG_pre_set_max());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItm().equalsIgnoreCase("g_pre_set_min") == true)
                    {
                        // 전염소 최소 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getG_pre_set_min());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItm().equalsIgnoreCase("g_pre_chg_limit_for_onetime") == true)
                    {
                        // 전염소 변경 한계치 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getG_pre_chg_limit_for_onetime());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                	else if(dto.getItm().equalsIgnoreCase("g_pre_calib_cycle") == true)
                    {
                        // 혼화지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getG_pre_calib_cycle());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItm().equalsIgnoreCase("g_d_obj_residual_cl") == true)
                    {
                        // 혼화지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getG_d_obj_residual_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItm().equalsIgnoreCase("g_d_residual_cl_holding") == true)
                    {
                        // 혼화지 잔류염소 홀딩 범위
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPre.getG_d_residual_cl_holding());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                }
            }
            catch(JsonProcessingException e)
            {
                log.error("JsonProcessingException Occurred in /disinfection/control/pre API");
            }

            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_disinfection_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 소독(중) 알고리즘 설정값 변경
     * 
     * @param disinfectionPeri  Front-end 소독 중염소 알고리즘 설정값을 저장하기 위한
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 중차염: 2, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/control/peri/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putPeriControlDisinfection(@RequestBody InterfaceDisinfectionPeriDTO disinfectionPeri, @PathVariable int processStep)
    {
        log.debug("putPeriControlDisinfection, peri:[{}]", disinfectionPeri);
        //중차염 disinfectionStep = 2
        int disinfectionStep = 2;
        
        boolean result = true;

        // update 중염소 최대 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_peri_set_max", disinfectionPeri.getG_peri_set_max(), processStep, disinfectionStep) == 1) && result;

        // update 중염소 최소 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_peri_set_min", disinfectionPeri.getG_peri_set_min(), processStep, disinfectionStep) == 1) && result;

        // update 중염소 변경 한계치
        result = (databaseService.modAiDisinfectionInit("g_peri_chg_limit_for_onetime", disinfectionPeri.getG_peri_chg_limit_for_onetime(), processStep, disinfectionStep) == 1) && result;

        // update 보정주기
        result = (databaseService.modAiDisinfectionInit("g_peri_calib_cycle", disinfectionPeri.getG_peri_calib_cycle(), processStep, disinfectionStep) == 1) && result;

        // update 여과지 유출 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("g_f_out_obj_residual_cl", disinfectionPeri.getG_f_out_obj_residual_cl(), processStep, disinfectionStep) == 1) && result;

        // 업데이트가 성공하면 Kafka를 통해 설정값 전달
        if(result == true)
        {
            // send control value to kafka ai_control
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = simpleDateFormat.format(new Date().getTime());

            LinkedHashMap<String, Object> controlMap;
            ObjectMapper objectMapper = new ObjectMapper();

            List<AiProcessInitDTO> aiDisinfectionInitList = databaseService.getAllAiDisinfectionInit(processStep, disinfectionStep);
            log.debug("getAllAiDisinfectionInit, result:[{}]", aiDisinfectionInitList.size());

            try
            {
                    for(AiProcessInitDTO dto : aiDisinfectionInitList)
                    {
                        if(dto.getItm().equalsIgnoreCase("g_peri_set_max") == true)
                        {
                            // 전염소 최대 주입률 설정 값을 설정하여 Kafka 전송
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", disinfectionPeri.getG_peri_set_max());
                            controlMap.put("time", strDate);

                            String strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                        }
                        else if(dto.getItm().equalsIgnoreCase("g_peri_set_min") == true)
                        {
                            // 전염소 최소 주입률 설정 값을 설정하여 Kafka 전송
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", disinfectionPeri.getG_peri_set_min());
                            controlMap.put("time", strDate);

                            String strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                        }
                        else if(dto.getItm().equalsIgnoreCase("g_peri_chg_limit_for_onetime") == true)
                        {
                            // 전염소 변경 한계치 값을 설정하여 Kafka 전송
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", disinfectionPeri.getG_peri_chg_limit_for_onetime());
                            controlMap.put("time", strDate);

                            String strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                        }
                    	else if(dto.getItm().equalsIgnoreCase("g_peri_calib_cycle") == true)
                        {
                            // 1계열 혼화지 목표 잔류염소 값을 설정하여 Kafka 전송
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", disinfectionPeri.getG_peri_calib_cycle());
                            controlMap.put("time", strDate);

                            String strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                        }
                        else if(dto.getItm().equalsIgnoreCase("g_f_out_obj_residual_cl") == true)
                        {
                            // 1계열 침전지 목표 잔류염소 값을 설정하여 Kafka 전송
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", disinfectionPeri.getG_f_out_obj_residual_cl());
                            controlMap.put("time", strDate);

                            String strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                        }
                }
            }
            catch(JsonProcessingException e)
            {
                log.error("JsonProcessingException Occurred in /disinfection/control/peri API");
            }

            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_disinfection_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 소독(후) 알고리즘 설정값 변경
     * 
     * @param disinfectionPost  Front-end 소독 후염소 알고리즘 설정값을 저장하기 위한 DTO
     * @param processStep       공정단계
     * @param disinfectionIndex 전차염: 1, 중차염: 2, 후차염: 3
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/disinfection/control/post/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putPostControlDisinfection(@RequestBody InterfaceDisinfectionPostDTO disinfectionPost, @PathVariable int processStep)
    {
        log.debug("putPostControlDisinfection, post:[{}]", disinfectionPost);

        boolean result = true;
        
        //후차염 disinfectionStep = 3;
        int disinfectionStep =3;

        // update 후염소 최대 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_post_set_max", disinfectionPost.getG_post_set_max(), processStep, disinfectionStep) == 1) && result;

        // update 후염소 최소 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_post_set_min", disinfectionPost.getG_post_set_min(), processStep, disinfectionStep) == 1) && result;

        // update 후염소 변경 한계치
        result = (databaseService.modAiDisinfectionInit("g_post_chg_limit_for_onetime", disinfectionPost.getG_post_chg_limit_for_onetime(), processStep, disinfectionStep) == 1) && result;

        // update 보정주기
        result = (databaseService.modAiDisinfectionInit("g_post_calib_cycle", disinfectionPost.getG_post_calib_cycle(), processStep, disinfectionStep) == 1) && result;

        // update 정수지 유입목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("g_h_in_obj_residual_cl", disinfectionPost.getG_h_in_obj_residual_cl(), processStep, disinfectionStep) == 1) && result;

        // update 주입률 변경 후 잔류염소 미변동 시 대기시간
        result = (databaseService.modAiDisinfectionInit("g_post_chol_rate_holding_time", disinfectionPost.getG_post_chol_rate_holding_time(), processStep, disinfectionStep) == 1) && result;

        // update 정수지 유입 잔류염소 홀딩 범위
        result = (databaseService.modAiDisinfectionInit("g_h_in_residual_cl_holding", disinfectionPost.getG_h_in_residual_cl_holding(), processStep, disinfectionStep) == 1) && result;

        
        // 업데이트가 성공하면 Kafka를 통해 설정값 전달
        if(result == true)
        {
            // send control value to kafka ai_control
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = simpleDateFormat.format(new Date().getTime());

            LinkedHashMap<String, Object> controlMap;
            ObjectMapper objectMapper = new ObjectMapper();

            List<AiProcessInitDTO> aiDisinfectionInitList = databaseService.getAllAiDisinfectionInit(processStep, disinfectionStep);
            log.debug("getAllAiDisinfectionInit, result:[{}]", aiDisinfectionInitList.size());

            try
            {
                for(AiProcessInitDTO dto : aiDisinfectionInitList)
                {
                    if(dto.getItm().equalsIgnoreCase("g_post_set_max") == true)
                    {
                        // 후염소 최대 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_post_set_max());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItm().equalsIgnoreCase("g_post_set_min") == true)
                    {
                        // 후염소 최소 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_post_set_min());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItm().equalsIgnoreCase("g_post_chg_limit_for_onetime") == true)
                    {
                        // 후차염 1회 변경 제한 주입률 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_post_chg_limit_for_onetime());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                	else if(dto.getItm().equalsIgnoreCase("g_post_calib_cycle") == true)
                    {
                        // 후차염 보정주기 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_post_calib_cycle());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItm().equalsIgnoreCase("g_h_in_obj_residual_cl") == true)
                    {
                        // 침전지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_h_in_obj_residual_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItm().equalsIgnoreCase("g_post_chol_rate_holding_time") == true)
                    {
                        // 주입률 변경 후 잔류염소 미변동 시 대기시간 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_post_chol_rate_holding_time());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItm().equalsIgnoreCase("g_h_in_residual_cl_holding") == true)
                    {
                        // 정수지 유입 잔류염소 홀딩 범위 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getTag_sn());
                        controlMap.put("value", disinfectionPost.getG_h_in_residual_cl_holding());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                }
            }
            catch(JsonProcessingException e)
            {
                log.error("JsonProcessingException Occurred in /disinfection/control/post API");
            }

            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_disinfection_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
