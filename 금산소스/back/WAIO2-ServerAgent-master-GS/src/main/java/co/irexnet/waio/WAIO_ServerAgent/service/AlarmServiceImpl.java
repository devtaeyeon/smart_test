package co.irexnet.waio.WAIO_ServerAgent.service;

import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmNotifyDTO;
import co.irexnet.waio.WAIO_ServerAgent.kafka.KafkaProducer;
import co.irexnet.waio.WAIO_ServerAgent.util.AlarmInfoList;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import co.irexnet.waio.WAIO_ServerAgent.util.GlobalSystemConfig;
import co.irexnet.waio.WAIO_ServerAgent.util.HttpSend;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AlarmServiceImpl implements IAlarmService
{
    @Autowired
    AlarmInfoList alarmInfoList;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    GlobalSystemConfig globalSystemConfig;

    @Autowired
    KafkaProducer kafkaProducer;

    // HTTP Connection 설정
    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectionRequestTimeout(3 * CommonValue.ONE_SECOND)
            .build();

    @Override
    public Integer alarmNotify(int alarmId, String message, String url, String time)
    {
        // Get AlarmInfo, if not exist, add alarm_info(EMS, PMS)
        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(alarmId);
        if(alarmInfo == null)
        {
            if(alarmId > CommonValue.MAX_WAIO_ALARM_COUNT)
            {
                alarmInfo = new AlarmInfoDTO();
                alarmInfo.setAlm_id(alarmId);
                alarmInfo.setCd_nm(message);
                alarmInfo.setDp_nm(message);
                alarmInfo.setUrl(url);
                alarmInfo.setAlm_ty(CommonValue.ALARM_TYPE_ANOTHER_SYSTEM);
                alarmInfo.setCmp(0);
                alarmInfo.setCmp_val("");
                alarmInfo.setScd_snd(false);
                alarmInfo.setTag_sn("");
                alarmInfo.setAlm_dp_yn("Y");
                int nResult = databaseService.addAlarmInfo(alarmInfo);
                log.info("Add AlarmInfo:[{}], result:[{}]", alarmId, nResult);
                if(nResult > 0)
                {
                    alarmInfoList.addAlarmInfo(alarmInfo);
                }
            }
            else
            {
                log.error("Does not exist alarm:[{}]", alarmId);
                return null;
            }
        }

        // Insert alarm_notify
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date alarmTime = simpleDateFormat.parse(time);

            AlarmNotifyDTO alarmNotify = new AlarmNotifyDTO();
            alarmNotify.setAlm_id(alarmInfo.getAlm_id());
            alarmNotify.setAlm_ntf_ti(alarmTime);
            // 알람 ID에 따라 호스트명 결정
            if(alarmId > 0 && alarmId <= CommonValue.MAX_WAIO_ALARM_COUNT)
            {
                alarmNotify.setHost("WAIO");
                alarmNotify.setVal(alarmInfo.getCmp_val());
            }
            else if(alarmId > CommonValue.MAX_WAIO_ALARM_COUNT && alarmId <= CommonValue.MAX_EMS_ALARM_COUNT)
            {
                alarmNotify.setHost("EMS");
                alarmNotify.setVal("EMS");
            }
            else if(alarmId > CommonValue.MAX_EMS_ALARM_COUNT && alarmId <= CommonValue.MAX_PMS_ALARM_COUNT)
            {
                alarmNotify.setHost("PMS");
                alarmNotify.setVal("PMS");
            }

            int nResult = databaseService.addAlarmNotify(alarmNotify);

            return nResult;
        }
        catch (ParseException e)
        {
            log.error("Invalid Date format, [{}]", time);

            return null;
        }
    }

    @Override
    public Integer alarmNotify(String alarmCode, String hostname, Object value, boolean onceADay)
    {
        // Get AlarmInfo
        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(alarmCode, 0);
        if(alarmInfo != null)
        {
            // Check once a day
            if(onceADay == true)
            {
                // Check latest alarm_time
                Date yesterdayDate = new Date();
                yesterdayDate.setTime(yesterdayDate.getTime() - CommonValue.ONE_DAY);
                AlarmNotifyDTO alarmNotify = databaseService.getLatestAlarmNotify(
                        alarmInfo.getAlm_id(), yesterdayDate, hostname);

                if(alarmNotify != null)
                {
                    // If exist a alarm, ignore this alarm.
                    return null;
                }
            }

            // Insert alarm_notify
            AlarmNotifyDTO alarmNotify = new AlarmNotifyDTO();
            alarmNotify.setAlm_id(alarmInfo.getAlm_id());
            alarmNotify.setAlm_ntf_ti(new Date());
            alarmNotify.setHost(hostname);
            alarmNotify.setVal(String.format("%s", value));

            int nResult = databaseService.addAlarmNotify(alarmNotify);

            // Send alarm information to SCADA Server
            // If alarm_info's "scd_snd" is true
            if(alarmInfo.isScd_snd() == true && alarmInfo.getTag_sn() != null)
            {
                // Make body
                String strBody;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = simpleDateFormat.format(alarmNotify.getAlm_ntf_ti());
                Map<String, Object> requestBody = new HashMap<>();
//                requestBody.put("alarm_id", alarmInfo.getAlarm_id());
//                requestBody.put("alarm_time", strDate);
//                requestBody.put("hostname", hostname);
//                requestBody.put("value", value);
                requestBody.put("tag_sn", alarmInfo.getTag_sn());
                requestBody.put("time", strDate);
                requestBody.put("value", false);

                ObjectMapper objectMapper = new ObjectMapper();
                try
                {
                    strBody = objectMapper.writeValueAsString(requestBody);
                }
                catch(JsonProcessingException e)
                {
                    strBody = "";
                    log.error("JsonProcessingException occurred...value:[{}]", requestBody.toString());
                }
                kafkaProducer.sendMessageToVip("alarm", strBody);
            }
            return nResult;
        }
        else
        {
            log.error("Does not exist alarm:[{}]", alarmCode);
            return null;
        }
    }
}
