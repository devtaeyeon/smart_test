package co.irexnet.waio.WAIO_ServerAgent.controller;

import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.DashboardDataDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.DashboardIdDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.DashboardInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceOperationModeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageDTO;
import co.irexnet.waio.WAIO_ServerAgent.kafka.KafkaProducer;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import co.irexnet.waio.WAIO_ServerAgent.util.Conversion;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesAuthentication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@EnableSwagger2
@Slf4j
public class DashboardController
{
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;
    
    @Autowired
    KafkaProducer kafkaProducer;

    /**
     * 대시보드 조회
     * 
     * @param token         토큰
     * @param dashboardInfo 대시보드 정보 DTO
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/dashboard", method = RequestMethod.POST)
    public ResponseEntity<String> postDashboard(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @RequestBody DashboardInfoDTO dashboardInfo)
    {
        log.debug("Recv postDashboard");
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            nAuthority = accessTokenDTO.getUsr_auth();
            if(nAuthority == CommonValue.USER)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            else if(nAuthority == CommonValue.NONE_AUTHORITY)
            {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
            else
            {
                bFindToken = true;
            }
        }

        if(bFindToken == true)
        {
            // Convert Class value to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> dashboardData = objectMapper.convertValue(dashboardInfo, Map.class);
            String strData = "";
            try
            {
                strData = objectMapper.writeValueAsString(dashboardData);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            int nResult = databaseService.addDashboardInfo(strData);
            log.debug("addDashboardInfo, result:[{}]", nResult);
            return new ResponseEntity<>("", HttpStatus.CREATED);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 대시보드 최신 조회
     * 
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/dashboard/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestDashboard()
    {
        log.debug("Recv getLatestDashboard");

        DashboardIdDTO dashboardId = databaseService.getLatestDashboardInfo();
        log.debug("getLatestDashboardInfo, result:[{}]", dashboardId != null ? dashboardId.getDashboard_id() : null);
        if(dashboardId != null)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("dashboard_id", dashboardId.getDashboard_id());

            String strBody;
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
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * @deprecated 미사용
     * @param dashboard_id
     * @return ResponseEntity<String>
     */
    @RequestMapping(value = "/dashboard/{dashboard_id}", method = RequestMethod.GET)
    public ResponseEntity<String> getDashboard(@PathVariable String dashboard_id)
    {
        log.debug("Recv getDashboard, dashboard_id:[{}]", dashboard_id);
        // Check dashboard_id type
        if(Conversion.isNumber(dashboard_id) == false)
        {
            String strErrorBody = "{\"reason\":\"dashboard_id must number.\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        int nDashboardId = Integer.parseInt(dashboard_id);
        DashboardDataDTO dashboardData = databaseService.getDashboardInfo(nDashboardId);
        log.debug("getDashboardInfo, result:[{}]", dashboardData != null ? 1 : 0);

        if(dashboardData != null)
        {
            // Make Response Body
            String strBody;
            ObjectMapper objectMapper = new ObjectMapper();
            try
            {
                DashboardInfoDTO dashboardInfo = objectMapper.readValue(dashboardData.getData(), DashboardInfoDTO.class);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("dashboard", dashboardInfo);

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
            String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * @deprecated 미사용
     * @param dashboard_id
     * @return ResponseEntity<String>
     */
    @RequestMapping(value = "/dashboard/{dashboard_id}", method = RequestMethod.PUT)
    public ResponseEntity<String> putDashboard(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable String dashboard_id,
            @RequestBody DashboardInfoDTO dashboardInfo)
    {
        log.debug("Recv putDashboard, dashboard_id:[{}]", dashboard_id);
        // Check dashboard_id type
        if(Conversion.isNumber(dashboard_id) == false)
        {
            String strErrorBody = "{\"reason\":\"dashboard_id must number.\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Check dashboardInfo
        if(dashboardInfo.getCreate_time() == null || dashboardInfo.getUpdate_time() == null || dashboardInfo.getTitle() == null ||
        dashboardInfo.getThumb() == null || dashboardInfo.getProcess_list() == null || dashboardInfo.getPath_list() == null)
        {
            String strErrorBody = "{\"reason\":\"dashboard_data must not null.\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            nAuthority = accessTokenDTO.getUsr_auth();
            if(nAuthority == CommonValue.USER)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            else if(nAuthority == CommonValue.NONE_AUTHORITY)
            {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
            else
            {
                bFindToken = true;
            }
        }

        if(bFindToken == true)
        {
            int nDashboardId = Integer.parseInt(dashboard_id);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> dashboardData = objectMapper.convertValue(dashboardInfo, Map.class);
            String strData;
            try
            {
                strData = objectMapper.writeValueAsString(dashboardData);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            int nResult = databaseService.modDashboardInfo(nDashboardId, strData);
            log.debug("modDashbardInfo, result:[{}]", nResult);

            // If update is successful, retrieve new updated record
            if(nResult > 0)
            {
                DashboardDataDTO dashboardDataDTO = databaseService.getDashboardInfo(nDashboardId);
                log.debug("getDashboardInfo, result:[{}]", dashboardData != null ? 1 : 0);

                if(dashboardData != null)
                {
                    // Make Response Body
                    String strBody;
                    try
                    {
                        DashboardInfoDTO dashboardInfoDTO = objectMapper.readValue(dashboardDataDTO.getData(), DashboardInfoDTO.class);

                        Map<String, Object> responseBody = new HashMap<>();
                        responseBody.put("dashboard", dashboardInfoDTO);

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
                    String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
                }
            }
            else
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * @deprecated 미사용
     * @param dashboard_id
     * @return ResponseEntity<String>
     */
    @RequestMapping(value = "/dashboard/{dashboard_id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteDashboard(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable String dashboard_id)
    {
        log.debug("Recv deleteDashboard, dashboard_id:[{}]", dashboard_id);
        // Check dashboard_id type
        if(Conversion.isNumber(dashboard_id) == false)
        {
            String strErrorBody = "{\"reason\":\"dashboard_id must number.\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            nAuthority = accessTokenDTO.getUsr_auth();
            if(nAuthority == CommonValue.USER)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            else if(nAuthority == CommonValue.NONE_AUTHORITY)
            {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
            else
            {
                bFindToken = true;
            }
        }

        if(bFindToken == true)
        {
            int nDashboardId = Integer.parseInt(dashboard_id);
            int nResult = databaseService.delDashboardInfo(nDashboardId);
            log.debug("delDashboardInfo, result:[{}]", nResult);

            if(nResult > 0)
            {
                return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
            }
            else
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * 메인대시보드 전체공정 운영모드 조회
     * 
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/dashboard/control/operation", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationAllProcess(@RequestBody InterfaceOperationModeDTO operationMode){
		
        log.info("putOperationControlAllProcess, mode:[{}]", operationMode.getOperation());
        
        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
        
        // update operation mode
        for(int processStep : CommonValue.PROCESS_STEP_ARRAY) {            
            databaseService.modAiCoagulantOperationMode(nOperationMode, processStep);
            databaseService.modAiMixingOperationMode(nOperationMode, processStep);
            databaseService.modAiDisinfectionOperationMode(nOperationMode, processStep, 1);
            databaseService.modAiDisinfectionOperationMode(nOperationMode, processStep, 3);                
        }

        //각 공정운영모드 TAG_SN List
    	List<TagManageDTO> operationTagList = databaseService.getAllTagManage().stream()
				.filter(dto -> dto.getTag_ty().equals(CommonValue.TAG_MANAGE_TYPE_INIT) && dto.getItm().endsWith("operation_mode"))
				.collect(Collectors.toList());
        
        try {
        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	String strDate = simpleDateFormat.format(new Date().getTime());
        	String strBody = "";
        	
        	LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
        	ObjectMapper objectMapper = new ObjectMapper();
        	// 공정 제어모드 값 KAFKA 전송
        	for(TagManageDTO operationTag : operationTagList) {
                controlMap.clear();
        		controlMap.put("tag", operationTag.getTag_sn());
        		controlMap.put("value", nOperationMode);
        		controlMap.put("time", strDate);
        		
        		strBody = objectMapper.writeValueAsString(controlMap);
        		kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                log.info("send to kafka:[{}]", strBody);
        	}
        	
        	// 공정 제어모드 변경 알람 KAFKA 전송
        	List<TagManageDTO> operationAlarmTagList = databaseService.getAllTagManage().stream()
        											.filter(dto -> dto.getAi_cd().equals("UI") && dto.getItm().endsWith("operation_mode_a"))
        											.collect(Collectors.toList());
        	
        	for(TagManageDTO operationAlarmTag : operationAlarmTagList) {
                controlMap.clear();
                controlMap.put("tag", operationAlarmTag.getTag_sn());
                controlMap.put("value", nOperationMode == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                controlMap.put("time", strDate);

                strBody = objectMapper.writeValueAsString(controlMap);
                kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
        	}
        	
        }
        catch(JsonProcessingException e)
        {
            log.error("JsonProcessingException Occurred in /dashboard/control/operation API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
