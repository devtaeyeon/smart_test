package co.irexnet.waio.WAIO_ServerAgent.controller;

import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmNotifyDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.AlarmInfoList;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesAuthentication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@EnableSwagger2
@Slf4j
public class AlarmsController
{
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    AlarmInfoList alarmInfoList;

    /**
     * 전체 알람 발보 이력 조회
     * @return
     */
    @RequestMapping(value = "/alarms/all", method = RequestMethod.GET)
    public ResponseEntity<String> getAllAlarms()
    {
        log.debug("Recv getAllAlarms");

        // 알람 발보 이력 조회
        List<AlarmNotifyDTO> alarmNotifyList = databaseService.getAllAlarmNotify();
        log.debug("getAllAlarmNotify, result:[{}]", alarmNotifyList.size());
        if(alarmNotifyList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("alarms", alarmNotifyList);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = "";
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
            String strErrorBody = "{\"reason\":\"Empty alarm_notify\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
// 알람 네비게이터 삭제
//    @RequestMapping(value = "/alarms/unack", method = RequestMethod.GET)
//    public ResponseEntity<String> getUnackAlarms()
//    {
//        log.debug("Recv getUnackAlarms");
//
//        List<AlarmNotifyDTO> alarmNotifyList = databaseService.getAlarmNotifyFromAckState(false);
//        log.debug("getAlarmNotifyFromAckState, result:[{}]", alarmNotifyList.size());
//
//        // Make Response Body
//        Map<String, Object> responseBody = new HashMap<>();
//        responseBody.put("alarms", alarmNotifyList);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String strBody = "";
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
// 알람 네비게이터 삭제
//    @RequestMapping(value = "/alarms/ack", method = RequestMethod.PUT)
//    public ResponseEntity<String> putAckAlarms(
//            @RequestHeader("X-ACCESS-TOKEN") String token,
//            @RequestBody List<AlarmNotifyDTO> alarmNotifyList)
//    {
//        log.debug("Recv putAckAlarms");
//        // Check Access Token
//        boolean bFindToken = false;
//        int nAuthority;
//        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
//        if(accessTokenDTO != null)
//        {
//            nAuthority = accessTokenDTO.getAuthority();
//            if(nAuthority == CommonValue.USER)
//            {
//                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
//            }
//            else if(nAuthority == CommonValue.NONE_AUTHORITY)
//            {
//                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//            }
//            else
//            {
//                bFindToken = true;
//                // Stop auto login time update
////                long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
////                Date expirationDate = new Date();
////                expirationDate.setTime(expirationDate.getTime() + expiredTime);
////                databaseService.modToken(token, expirationDate);
//            }
//        }
//
//        if(bFindToken == true)
//        {
//            int nTotalResult = 0;
//            for(AlarmNotifyDTO alarmNotifyDTO : alarmNotifyList)
//            {
//                int nResult = databaseService.modAlarmNotifyAckState(alarmNotifyDTO.getAlarm_notify_index(), true);
//
//                if(nResult > 0)
//                {
//                    nTotalResult += nResult;
//                }
//                else
//                {
//                    log.error("modAlarmNotifyAckState fail, alarm_notify_index:[{}]", alarmNotifyDTO.getAlarm_notify_index());
//                }
//            }
//            log.debug("modAlarmNotifyAckState:[{}]", nTotalResult);
//            return new ResponseEntity<>("", HttpStatus.OK);
//        }
//        else
//        {
//            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
//        }
//    }

    /**
     * 기간 선택을 통한 알람 발보 이력 조회
     * @param dateSearchDTO
     * @return
     */
    @RequestMapping(value = "/alarms/search", method = RequestMethod.PUT)
    public ResponseEntity<String> getAlarmsSearch(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.debug("getAlarmsSearch, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // Get Alarm_notify between alarm_time
        List<AlarmNotifyDTO> alarmNotifyList = databaseService.getSearchAlarmNotify(dateSearchDTO);
        log.debug("getSearchAlarmNotify, result:[{}]", alarmNotifyList.size());
        if(alarmNotifyList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("alarms", alarmNotifyList);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = "";
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
            String strErrorBody = "{\"reason\":\"Empty alarm_notify\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 알람 설정 정보 요청
     * @return
     */
    @RequestMapping(value = "/alarms/setting", method = RequestMethod.GET)
    public ResponseEntity<String> getAlarmsSetting()
    {
        log.debug("getAlarmsSetting");

        // 알람 설정 정보 조회
        List<AlarmInfoDTO> alarmInfoDTOList = databaseService.getAlarmInfo();
        log.debug("getAlarmInfo, result:[{}]", alarmInfoDTOList.size());
        if(alarmInfoDTOList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("alarm_info", alarmInfoDTOList);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = "";
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
            String strErrorBody = "{\"reason\":\"Empty alarm_info\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 알람 설정 정보 수정
     * @param token
     * @param alarmInfoIndex
     * @param alarmInfoDTO
     * @return
     */
    @RequestMapping(value = "/alarms/setting/{alarmInfoIndex}", method = RequestMethod.PUT)
    public ResponseEntity<String> putAlarmsSetting(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable int alarmInfoIndex,
            @RequestBody AlarmInfoDTO alarmInfoDTO
    )
    {
        log.debug("putAlarmsSetting, alarmInfoIndex:[{}]", alarmInfoIndex);
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            // 관리자/운용자 권한인지 확인
            nAuthority = accessTokenDTO.getUsr_auth();
            if(nAuthority == CommonValue.GUEST)
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
                // Stop auto login time update
//                long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
//                Date expirationDate = new Date();
//                expirationDate.setTime(expirationDate.getTime() + expiredTime);
//                databaseService.modToken(token, expirationDate);
            }
        }

        if(bFindToken == true)
        {
            // Update alarm_info
            alarmInfoDTO.setSeq(alarmInfoIndex);
            int nResult = databaseService.modAlarmInfo(alarmInfoDTO);
            log.debug("modAlarmInfo:[{}], result:[{}]", alarmInfoIndex, nResult);
            if(nResult > 0)
            {
                // Update AlarmInfoList
                alarmInfoList.setAlarmInfoList(databaseService.getAlarmInfo());

                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmInfoIndex(alarmInfoIndex);
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("alarm_info", alarmInfo);

                // ObjectMapper를 통해 JSON 값을 String으로 변환
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
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }
}
