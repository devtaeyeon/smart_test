package co.irexnet.waio.WAIO_ServerAgent.controller;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.*;
import co.irexnet.waio.WAIO_ServerAgent.dto.*;
import co.irexnet.waio.WAIO_ServerAgent.kafka.KafkaProducer;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class FilterController
{
    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    KafkaProducer kafkaProducer;

    // 여과 공정 최근 데이터 조회
    @RequestMapping(value = "/filter/latest/{processStep}", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestFilter(@PathVariable int processStep)
    {
        log.debug("Recv getLatestFilter");

        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());
        strPartitionName = "20231101";	// FIXME 현재 날짜 수정

        // get ai_filter_init(f_operation_mode)
        AiProcessInitDTO aiFilterInit = databaseService.getAiFilterInit(CommonValue.F_OPERATION_MODE, processStep);
        log.debug("getAiFilterInit(operation_mode), result:[{}]", aiFilterInit != null ? 1 : 0);

        // get ai_filter_init(f_location_ti_set_max)
        AiProcessInitDTO aiFilterTi = databaseService.getAiFilterInit(CommonValue.F_LOCATION_TI_SET_MAX, processStep);
        log.debug("getAiFilterInit(f_location_ti_set_max), result:[{}]", aiFilterTi != null ? 1 : 0);

        // get ai_filter_realtime
        AiFilterRealtimeDTO aiFilterRealtime = databaseService.getLatestAiFilterRealtimeValue(processStep);
        log.debug("getLatestAiFilterRealtimeValue, result: [{}]", aiFilterRealtime != null ? 1 : 0);

        // get filter_realtime
        List<ProcessRealtimeDTO> filterRealtime = databaseService.getLatestFilterRealtimeValue(strPartitionName, processStep);
        log.debug("getLatestFilterRealtimeValue, result:[{}]", filterRealtime.size());

        // get tag_manage(filter)
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_FILTER, processStep);
        log.debug("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_FILTER, tagManageList.size());

        // get location number(지 번호)
//        TagManageRangeDTO filterRange = databaseService.getTagManageRange(CommonValue.PROCESS_FILTER, processStep);
//        log.debug("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_FILTER, filterRange != null ? 1 : 0);

//        int nLocationMin = 0, nLocationMax = 0;
//        if(filterRange != null)
//        {
//            nLocationMin = filterRange.getMin();
//            nLocationMax = filterRange.getMax();
//        }
        // TODO TB_TAG_MAG에 LOC 컬럼 값 채워지면 변경
        int nLocationMin = 1, nLocationMax = 8;

        if(aiFilterRealtime != null)
        {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiFilterInfo = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp, locationTemp;

            // update_time
            aiFilterInfo.put("upd_ti", aiFilterRealtime.getUpd_ti());

            // operation_mode
            if(aiFilterInit != null)
            {
                aiFilterInfo.put("ai_opr", aiFilterInit.getInit_val().intValue());
            }
            else
            {
                aiFilterInfo.put("ai_opr", aiFilterRealtime.getAi_opr());
            }

            // peak_mode
            aiFilterInfo.put("peak_mode", aiFilterRealtime.getF_peak_mode());

            // f_location_ti_set_max
            if(aiFilterTi != null)
            {
                aiFilterInfo.put("f_location_ti_set_max", aiFilterTi.getInit_val().intValue());
            }
            else
            {
                aiFilterInfo.put("f_location_ti_set_max", null);
            }

            // Realtime data from SCADA
            // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiFilterInfo에 등록
            for(TagManageDTO tagManage : tagManageList)
            {
                for(ProcessRealtimeDTO dto : filterRealtime)
                {
//                    if(tagManage.getItm().equalsIgnoreCase("d1_in_fr") == true &&
//                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
//                    {
//                        // 1계열 혼화지 유입 유량
//                        aiFilterInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
//                        break;
//                    }
//                    else if(tagManage.getItm().equalsIgnoreCase("d_ser_in_fr") == true &&
//                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
//                    {
//                        // 2계열 혼화지 유입 유량
//                        aiFilterInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
//                        break;
//                    }
//                    else if(tagManage.getItm().equalsIgnoreCase("e1_tb_b") == true &&
//                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
//                    {
//                        // 1계열 침전지 후탁도
//                        aiFilterInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
//                        break;
//                    }
//                    else if(tagManage.getItm().equalsIgnoreCase("e2_tb_b") == true &&
//                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
//                    {
//                        // 2계열 침전지 후탁도
//                        aiFilterInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
//                        break;
//                    }
//                    else if(tagManage.getItm().equalsIgnoreCase("f_sp") == true &&
//                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
//                    {
//                        // 여과 속도
//                        aiFilterInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
//                        break;
//                    }
                    if(tagManage.getItm().equalsIgnoreCase("f_out_fr") == true &&
                            tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                    {
                        // 여과지 유출 유량
                        aiFilterInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        break;
                    } else if(tagManage.getItm().equalsIgnoreCase("b_in_fr") == true &&
                    			tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
					{
						// 원수 유입 유량
						aiFilterInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
						break;
					}
                    else if(tagManage.getItm().equalsIgnoreCase("e1_tb_b") == true &&
                			tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
					{
						// 침전수 탁도
						aiFilterInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
						break;
					}	
                }
            }

            // 지별 수위, 지별 탁도, 지별 여과 지속 시간, 운영 지수, 지별 상태를 저장하기 위한 변수 선언
            LinkedHashMap<String, Object> locationLeMap = new LinkedHashMap<>();
            LinkedHashMap<String, Object> locationTbMap = new LinkedHashMap<>();
            LinkedHashMap<String, Object> locationTiMap = new LinkedHashMap<>();
            TreeMap<String, Object> locationStateMap = new TreeMap<>();	// 키 정렬
            int nOperationCount = 0;

            for(int i = nLocationMin; i <= nLocationMax; i++)
            {
                String strLeName = "f_loc_le" + i;
                String strTbName = "f_loc_tb" + i;
                String strTiHName = "f_location_ti_h" + i;
                String strTiMName = "f_location_ti_m" + i;
                String strStateName = "f_loc_stt" + i + "_";

                int nLocationTi = 0;            // 지별 여과 지속 시간을 계산하기 위한 변수
                int nLocationState = 0;         // 지별 현재 상태를 저장하기 위한 변수
                int nLocationStateCount = 0;    // 지별 현재 상태가 중복 임을 확인하기 위한 변수

                for(TagManageDTO tagManage : tagManageList)
                {
                    for(ProcessRealtimeDTO dto : filterRealtime)
                    {
                        if(tagManage.getItm().equalsIgnoreCase(strLeName) == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                        {
                            // 지별 수위 값 등록
                            locationLeMap.put("location" + i, Float.parseFloat(dto.getTag_val()));
                            break;
                        }
                        else if(tagManage.getItm().equalsIgnoreCase(strTbName) == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                        {
                            // 지별 탁도 값 등록
                            locationTbMap.put("location" + i, Float.parseFloat(dto.getTag_val()));
                            break;
                        }
                        else if(tagManage.getItm().equalsIgnoreCase(strTiHName) == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                        {
                            // 지별 여과 지속 시간 계산(시)
                            nLocationTi += (int)Float.parseFloat(dto.getTag_val()) * 60;
                            break;
                        }
                        else if(tagManage.getItm().equalsIgnoreCase(strTiMName) == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                        {
                            // 지별 여과 지속 시간 계산(분)
                            nLocationTi += (int)Float.parseFloat(dto.getTag_val());
                            break;
                        }
//                        else if(tagManage.getItm().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_FILTER)== true &&
//                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true &&
//                                Float.parseFloat(dto.getTag_val()) > 0)
//                        {
//                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
//                            nLocationState = CommonValue.FILTER_STATE_FILTER;
//                            nLocationStateCount ++;
//                            break;
//                        }
//                        else if(tagManage.getItm().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_RELAX) == true &&
//                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true &&
//                                Float.parseFloat(dto.getTag_val()) > 0)
//                        {
//                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
//                            nLocationState = CommonValue.FILTER_STATE_RELAX;
//                            nLocationStateCount ++;
//                            break;
//                        }
//                        else if(tagManage.getItm().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_BW) == true &&
//                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true &&
//                                Float.parseFloat(dto.getTag_val()) > 0)
//                        {
//                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
//                            nLocationState = CommonValue.FILTER_STATE_BW;
//                            nLocationStateCount ++;
//                            break;
//                        }
//                        else if(tagManage.getItm().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_BW_RELAX) == true &&
//                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true &&
//                                Float.parseFloat(dto.getTag_val()) > 0)
//                        {
//                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
//                            nLocationState = CommonValue.FILTER_STATE_BW_RELAX;
//                            nLocationStateCount ++;
//                            break;
//                        }
//                        else if(tagManage.getItm().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_CIRCULATION) == true &&
//                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true &&
//                                Float.parseFloat(dto.getTag_val()) > 0)
//                        {
//                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
//                            nLocationState = CommonValue.FILTER_STATE_CIRCULATION;
//                            nLocationStateCount ++;
//                            break;
//                        }
//                        else if(tagManage.getItm().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_CIRCULATION_RELAX) == true &&
//                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true &&
//                                Float.parseFloat(dto.getTag_val()) > 0)
//                        {
//                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
//                            nLocationState = CommonValue.FILTER_STATE_CIRCULATION_RELAX;
//                            nLocationStateCount ++;
//                            break;
//                        }
//                        else if(tagManage.getItm().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_CIRCULATION_STANDBY) == true &&
//                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true &&
//                                Float.parseFloat(dto.getTag_val()) > 0)
//                        {
//                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
//                            nLocationState = CommonValue.FILTER_STATE_CIRCULATION_STANDBY;
//                            nLocationStateCount ++;
//                            break;
//                        }
                    }
                }
                locationTiMap.put("location"+i, nLocationTi);

//                if(nLocationStateCount == 1)
//                {
//                    // 지별 상태가 중복되어 있지 않을 경우 현재 지별 상태를 locationStateMap에 등록
//                    locationStateMap.put("location"+i, nLocationState);
//                    if(nLocationState == CommonValue.FILTER_STATE_FILTER)
//                    {
//                        nOperationCount ++;
//                    }
//                }
//                else
//                {
//                    // 지별 상태가 중복되어 있다면 상태 없음 처리
//                    locationStateMap.put("location"+i, CommonValue.FILTER_STATE_NONE);
//                }
            }

            try
            {
                // AI 운영지 수 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
            	ArrayList<String> keyList;
            	Object objectTemp;
            	
            	//TODO 운영지수 운영반영시 데이터 포맷변경 필요.
//                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_num_fil(), LinkedHashMap.class);
//                keyList = new ArrayList<>(mapTemp.keySet());
//                objectTemp = mapTemp.get(keyList.get(0));
//                aiFilterInfo.put("ai_f_num_fil", objectTemp);
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_num_fil(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));

                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiFilterInfo.put("ai_f_num_fil", objectTemp);

                // 지별 현재 상태
                // TODO 상태 중복을 체크할 수 있는지 구조 재확인 필요
                int nLocationState = 0;         // 지별 현재 상태를 저장하기 위한 변수
                
                // 여과준비중
//                mapTemp = objectMapper.readValue(aiFilterRealtime.getF_fil_ready(), LinkedHashMap.class);
//                keyList = new ArrayList<>(mapTemp.keySet());
//                objectTemp = mapTemp.get(keyList.get(0));
//                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
//                keyList = new ArrayList<>(mapTemp.keySet());
//                
//                for(String key : keyList) {
//                	int status = objectMapper.convertValue(mapTemp.get(key), Integer.class);
//                	
//                	if(status == 1) {
//                		nLocationState = CommonValue.FILTER_STATE_READY;
//                		locationStateMap.put(key, nLocationState);
//                	}
//                }
//                
                // 여과중
                mapTemp = objectMapper.readValue(aiFilterRealtime.getF_fil_ing(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                
                for(String key : keyList) {
                	int status = objectMapper.convertValue(mapTemp.get(key), Integer.class);
                	
                	if(status == 1) {
                		nLocationState = CommonValue.FILTER_STATE_FIL_ING;                		
                		locationStateMap.put(key, nLocationState);
                		nOperationCount ++;
                	}
                }
                
                // 여과종료
//                mapTemp = objectMapper.readValue(aiFilterRealtime.getF_fil_end(), LinkedHashMap.class);
//                keyList = new ArrayList<>(mapTemp.keySet());
//                objectTemp = mapTemp.get(keyList.get(0));
//                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
//                keyList = new ArrayList<>(mapTemp.keySet());
//                
//                for(String key : keyList) {
//                	int status = objectMapper.convertValue(mapTemp.get(key), Integer.class);
//                	
//                	if(status == 1) {
//                		nLocationState = CommonValue.FILTER_STATE_END;                		
//                		locationStateMap.put(key, nLocationState);
//                	}
//                }
                
                // 대기중
//                mapTemp = objectMapper.readValue(aiFilterRealtime.getF_wait(), LinkedHashMap.class);
//                keyList = new ArrayList<>(mapTemp.keySet());
//                objectTemp = mapTemp.get(keyList.get(0));
//                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
//                keyList = new ArrayList<>(mapTemp.keySet());
//                
//                for(String key : keyList) {
//                	int status = objectMapper.convertValue(mapTemp.get(key), Integer.class);
//                	
//                	if(status == 1) {
//                		nLocationState = CommonValue.FILTER_STATE_WAIT;                		
//                		locationStateMap.put(key, nLocationState);
//                	}
//                }
                
                // 역세대기중
                mapTemp = objectMapper.readValue(aiFilterRealtime.getF_bw_wait(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                
                for(String key : keyList) {
                	int status = objectMapper.convertValue(mapTemp.get(key), Integer.class);
                	
                	if(status == 1) {
                		nLocationState = CommonValue.FILTER_STATE_BW_WAIT;                		
                		locationStateMap.put(key, nLocationState);
                	}
                }
                
                // 역세중
                mapTemp = objectMapper.readValue(aiFilterRealtime.getF_bw_ing(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                
                for(String key : keyList) {
                	int status = objectMapper.convertValue(mapTemp.get(key), Integer.class);
                	
                	if(status == 1) {
                		nLocationState = CommonValue.FILTER_STATE_BW_ING;                		
                		locationStateMap.put(key, nLocationState);
                	}
                }
                
                // 여과대기중 -- 23/11/09추가
                mapTemp = objectMapper.readValue(aiFilterRealtime.getF_fil_wait(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                
                for(String key : keyList) {
                	int status = objectMapper.convertValue(mapTemp.get(key), Integer.class);
                	
                	if(status == 1) {
                		nLocationState = CommonValue.FILTER_STATE_FIL_WAIT;                		
                		locationStateMap.put(key, nLocationState);
                	}
                }
                
                // 시동방수중
                mapTemp = objectMapper.readValue(aiFilterRealtime.getF_dr_ing(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                
                for(String key : keyList) {
                	int status = objectMapper.convertValue(mapTemp.get(key), Integer.class);
                	
                	if(status == 1) {
                		nLocationState = CommonValue.FILTER_STATE_DR_ING;                		
                		locationStateMap.put(key, nLocationState);
                	}
                }
                
                // 운휴중 -- 23/11/09추가
                mapTemp = objectMapper.readValue(aiFilterRealtime.getF_rest(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                
                for(String key : keyList) {
                	int status = objectMapper.convertValue(mapTemp.get(key), Integer.class);
                	
                	if(status == 1) {
                		nLocationState = CommonValue.FILTER_STATE_F_REST;                		
                		locationStateMap.put(key, nLocationState);
                	}
                }
                
                aiFilterInfo.put("f_loc_stt", locationStateMap);
                
                // 현재 운영지 수
                aiFilterInfo.put("f_opr_cnt", nOperationCount);

                // AI 지별 수위 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                //TODO 분석 개발 후 주석 처리해제
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_wl(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));

                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                locationTemp = new LinkedHashMap<>();
                for(String strKey : keyList)
                {
                    LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(strKey), LinkedHashMap.class);
                    ArrayList<String> locationKeyList = new ArrayList<>(locationMapTemp.keySet());
                    locationTemp.put(strKey, locationMapTemp.get(locationKeyList.get(0)));
                }
                aiFilterInfo.put("ai_f_loc_le", locationTemp);
//
                // 지별 수위
                aiFilterInfo.put("f_loc_le", locationLeMap);

                // 지별 탁도
                aiFilterInfo.put("f_loc_tb", locationTbMap);

                // 지별 여과 지속 시간
                aiFilterInfo.put("f_loc_ti", locationTiMap);
                
                // 여과 속도
                aiFilterInfo.put("f_sp", aiFilterRealtime.getF_speed());

                // AI 지별 여과 지속 시간 예측
                // TODO 분석개발 완료 후 주석 해제
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_time(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiFilterInfo.put("ai_f_loc_ti", objectTemp);

                // AI 지별 역세 시작 시점 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_bw_start_time(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiFilterInfo.put("ai_f_loc_bw_ti", objectTemp);

                // AI 지별 운영 스케쥴
                // TODO 분석개발 완료 후 주석 해제
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_operation(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiFilterInfo.put("ai_f_location_schedule", objectTemp);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            catch(NumberFormatException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error, Number Format Exception\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiFilterInfo);

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
            String strErrorBody = "{\"reason\":\"Empty ai_filter\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 실시간 여과 공정 지별 세부 항목 조회
    @RequestMapping(value = "/filter/location/{locationNumber}/{processStep}", method = RequestMethod.GET)
    public ResponseEntity<String> getLocationFilter(@PathVariable int locationNumber, @PathVariable int processStep)
    {
        log.debug("getLocationFilter, locationNumber:[{}]", locationNumber);

        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_FILTER, processStep);
        log.debug("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_FILTER, tagManageList.size());
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

            // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
            Calendar calendarToday = Calendar.getInstance();
            calendarToday.set(Calendar.MINUTE, 0);
            calendarToday.set(Calendar.SECOND, 0);
            calendarToday.set(Calendar.HOUR_OF_DAY, 0);
            SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
            String strPartitionName = partitionNameFormat.format(calendarToday.getTime());
            strPartitionName = "20231101";	// FIXME 현재 날짜 수정

            // get ai_filter_realtime
            AiFilterRealtimeDTO aiFilterRealtime = databaseService.getLatestAiFilterRealtimeValue(processStep);
            log.debug("getLatestAiFilterRealtimeValue, result:[{}]", aiFilterRealtime != null ? 1 : 0);

            // get filter_realtime
            List<ProcessRealtimeDTO> filterRealtime = databaseService.getLatestFilterRealtimeValue(strPartitionName, processStep);
            log.debug("getLatestFilterRealtimeValue, result:[{}]", filterRealtime.size());
            if(aiFilterRealtime != null)
            {
                // JSON 처리를 위한 ObjectMapper 선언
                ObjectMapper objectMapper = new ObjectMapper();

                // Make Response Body
                LinkedHashMap<String, Object> aiFilterLocationInfo = new LinkedHashMap<>();
                LinkedHashMap<String, Object> mapTemp;

                // update_time
                aiFilterLocationInfo.put("upd_ti", aiFilterRealtime.getUpd_ti());

                // 수위, 탁도, 여과 지속 시간, 역세 후 대기 시간/분을 저장하기 위한 변수 선언
                String strLeName = "f_loc_le" + locationNumber;
                String strTbName = "f_loc_tb" + locationNumber;
                String strTiHName = "f_location_ti_h" + locationNumber; 
                String strTiMName = "f_location_ti_m" + locationNumber;
                String strBwWaitTiHName = "f_location_bw_wait_ti_h" + locationNumber;
                String strBwWaitTiMName = "f_location_bw_wait_ti_m" + locationNumber;

                int nLocationTi = 0;        // 여과 지속 시간을 계산하기 위한 변수
                int nLocationBwWaitTi = 0;  // 역세 후 대기 시간을 계산하기 위한 변수

                // Realtime data from SCADA
                // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiFilterLocationInfo에 등록
                for(TagManageDTO tagManage : tagManageList)
                {
                    for (ProcessRealtimeDTO dto : filterRealtime)
                    {
//                        if (tagManage.getItm().equalsIgnoreCase("d1_in_fr") == true &&
//                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
//                        {
//                            // 1계열 혼화지 유입 유량
//                            aiFilterLocationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
//                            break;
//                        }
//                        else if (tagManage.getItm().equalsIgnoreCase("d_ser_in_fr") == true &&
//                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
//                        {
//                            // 2계열 혼화지 유입 유량
//                            aiFilterLocationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
//                            break;
//                        }
//                        else if(tagManage.getItm().equalsIgnoreCase("f_sp") == true &&
//                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
//                        {
//                            // 여과 속도
//                            aiFilterLocationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
//                            break;
//                        }
                        if(tagManage.getItm().equalsIgnoreCase("f_out_fr") == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                        {
                            // 여과지 유출 유량
                            aiFilterLocationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                            break;
                        }
                        else if(tagManage.getItm().equalsIgnoreCase(strLeName) == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                        {
                            // 여과지 수위
                            aiFilterLocationInfo.put("f_loc_le", Float.parseFloat(dto.getTag_val()));
                            break;
                        }
                        else if(tagManage.getItm().equalsIgnoreCase(strTbName) == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                        {
                            // 여과지 탁도
                            aiFilterLocationInfo.put("f_loc_tb", Float.parseFloat(dto.getTag_val()));
                            break;
                        }
                        else if(tagManage.getItm().equalsIgnoreCase(strTiHName) == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                        {
                            // 여과 지속 시간 계산(시)
                            nLocationTi += (int)Float.parseFloat(dto.getTag_val()) * 60;
                            break;
                        }
                        else if(tagManage.getItm().equalsIgnoreCase(strTiMName) == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                        {
                            // 여과 지속 시간 계산(분)
                            nLocationTi += (int)Float.parseFloat(dto.getTag_val());
                            break;
                        }
                        else if(tagManage.getItm().equalsIgnoreCase(strBwWaitTiHName) == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                        {
                            // 역세 후 대기 시간 계산(시)
                            nLocationBwWaitTi += (int)Float.parseFloat(dto.getTag_val()) * 60;
                            break;
                        }
                        else if(tagManage.getItm().equalsIgnoreCase(strBwWaitTiMName) == true &&
                                tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
                        {
                            // 역세 후 대기 시간 계산(분)
                            nLocationBwWaitTi += (int)Float.parseFloat(dto.getTag_val());
                            break;
                        }
                        else if(tagManage.getItm().equalsIgnoreCase("b_in_fr") == true &&
                    			tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true)
						{
							// 원수 유입 유량
                        	aiFilterLocationInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
							break;
						}
                    }
                }

                aiFilterLocationInfo.put("f_loc_ti", nLocationTi);
                aiFilterLocationInfo.put("f_loc_bw_wt_ti", nLocationBwWaitTi);

                try
                {
                    // AI 수위 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                	ArrayList<String> keyList;
                	Object objectTemp;
                	//TODO 분석개발 후 주석해제 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_wl(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    aiFilterLocationInfo.put("ai_f_loc_le", mapTemp.get("location" + locationNumber));

                    // AI 여과 지속 시간 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_time(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    Integer ai_f_loc_ti = Integer.parseInt(mapTemp.get("location" + locationNumber).toString());
                    aiFilterLocationInfo.put("ai_f_loc_ti", ai_f_loc_ti);

                    // AI 역세 시작 시간 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_bw_start_time(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    aiFilterLocationInfo.put("ai_f_loc_bw_ti", mapTemp.get("location" + locationNumber));

                    // AI 여과 종료 시간 예측(AI 여과 지속 시간 예측 - 현재 여과 지속 시간)
                    aiFilterLocationInfo.put("ai_f_location_end_ti", ai_f_loc_ti - nLocationTi);
                    
                    // 여과 속도
                    aiFilterLocationInfo.put("f_sp", aiFilterRealtime.getF_speed());

                    // AI 역세 후 대기 시간 예측
                    // TODO 분석개발 완료 후 주석 해제
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_bw_wait_time(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    aiFilterLocationInfo.put("ai_f_location_bw_wait_ti", mapTemp.get("location" + locationNumber));

                    // AI 운영 스케쥴 예측
                    // TODO 분석개발 완료 후 주석 해제
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_operation(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    aiFilterLocationInfo.put("ai_f_location_schedule", mapTemp.get("location" + locationNumber));
                }
                catch(JsonProcessingException e)
                {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("location", aiFilterLocationInfo);

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
                String strErrorBody = "{\"reason\":\"Empty ai_filter\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty tag_manage\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 여과 공정 제어모드 변경
    @RequestMapping(value = "/filter/control/operation/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlFilter(@RequestBody InterfaceOperationModeDTO operationMode, @PathVariable int processStep)
    {
        log.info("putOperationControlFilter, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_filter_init's operation_mode
//        log.debug("update aiFilterOperationMode:[{}], mode:[{}]",
//                databaseService.modAiFilterOperationMode(nOperationMode), nOperationMode);

        // send control value to kafka ai_control(f_operation_mode)
        AiProcessInitDTO aiFilterInit = databaseService.getAiFilterInit(CommonValue.F_OPERATION_MODE, processStep);
        log.debug("getAiFilterInit, result:[{}]", aiFilterInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            if(aiFilterInit != null) {
	            // Kafka에 전송할 값 정의
	            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
	            controlMap.put("tag", aiFilterInit.getTag_sn());
	            controlMap.put("value", nOperationMode);
	            controlMap.put("time", strDate);
	
	            // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
	            ObjectMapper objectMapper = new ObjectMapper();
	            String strBody = objectMapper.writeValueAsString(controlMap);
	            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
	            log.info("send to kafka:[{}]", strBody);
	
	            // Kafka에 여과 공정 제어모드 변경 알람 전송
	            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI, CommonValue.PROCESS_FILTER, processStep);            
	            for(TagManageDTO dto : tagManageList)
	            {
	                if(dto.getItm().equalsIgnoreCase("f_operation_mode_a") == true)
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
	            log.error("Does not exist aiFilterInit:[{}]", CommonValue.F_OPERATION_MODE);
	        }
        }
        catch(JsonProcessingException e)
        {
            log.error("JsonProcessingException Occurred in /filter/control/operation API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 여과 공정 최대 여과 지속 시간 설정
    @RequestMapping(value = "/filter/control/ti/{processStep}", method = RequestMethod.PUT)
    public ResponseEntity<String> putTiControlFilter(@RequestBody InterfaceFilterTiDTO filterTi, @PathVariable int processStep)
    {
        int nTi = filterTi.getF_location_ti_set_max();
        log.debug("putTiControlFilter, ti:[{}]", nTi);

        // 잘못된 시간 값 검사
        if(nTi < 0)
        {
            log.error("Invalid ti:[{}]", nTi);

            String strErrorBody = "{\"reason\":\"Invalid ti\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        int nResult = databaseService.modAiFilterInitTi(nTi, processStep);
        log.debug("modAiFilterInitTi, result:[{}]", nResult);

        if(nResult > 0)
        {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_filter_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // AI 여과 운영 스케쥴 예측값 조회
    @RequestMapping(value = "/filter/schedule/{processStep}", method = RequestMethod.GET)
    public ResponseEntity<String> getScheduleFilter(@PathVariable int processStep)
    {
        log.debug("Recv getScheduleFilter");

        // get ai_filter_realtime
        AiFilterRealtimeDTO aiFilterRealtime = databaseService.getLatestAiFilterRealtimeValue(processStep);
        log.debug("getLatestAiFilterRealtimeValue, result:[{}]", aiFilterRealtime != null ? 1 : 0);

        // get ai_gac_realtime
//        AiGacRealtimeDTO aiGacRealtime = databaseService.getLatestAiGacRealtimeValue();
//        log.debug("getLatestAiGacRealtimeValue, result:[{}]", aiGacRealtime != null ? 1 : 0);

        if(aiFilterRealtime != null)
        {
            try
            {
                // 전체 schedule을 저장할 scheduleMap, 여과/GAC 여과의 스케쥴을 저장할 filterMap, gacMap 선언
                LinkedHashMap<String, Object> scheduleMap = new LinkedHashMap<>();
                LinkedHashMap<String, Object> filterMap = new LinkedHashMap<>();
                // LinkedHashMap<String, Object> gacMap = new LinkedHashMap<>();
                ObjectMapper objectMapper = new ObjectMapper();

                // 여과 schedule
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                LinkedHashMap<String, Object> mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_schedule(), LinkedHashMap.class);
                List<String> keyList = new ArrayList<>(mapTemp.keySet());
                Object objectTemp = mapTemp.get(keyList.get(0));

                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                for(String key : keyList)
                {
                    if(key.indexOf("location") >= 0)
                    {
                        filterMap.put(key, mapTemp.get(key));
                    }
                }

                // GAC 여과 schedule
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
//                mapTemp = objectMapper.readValue(aiGacRealtime.getAi_f_loc_bw_stt(), LinkedHashMap.class);
//                keyList = new ArrayList<>(mapTemp.keySet());
//                objectTemp = mapTemp.get(keyList.get(0));
//
//                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
//                keyList = new ArrayList<>(mapTemp.keySet());
//                for(String key : keyList)
//                {
//                    if(key.indexOf("location") >= 0)
//                    {
//                        gacMap.put(key, mapTemp.get(key));
//                    }
//                }

                scheduleMap.put("filter", filterMap);
//                scheduleMap.put("gac", gacMap);

                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("schedule", scheduleMap);

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
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_filter or ai_gac\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
