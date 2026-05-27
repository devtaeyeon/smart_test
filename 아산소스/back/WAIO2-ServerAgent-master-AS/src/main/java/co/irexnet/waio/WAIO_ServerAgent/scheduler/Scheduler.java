package co.irexnet.waio.WAIO_ServerAgent.scheduler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAlarmControlHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.SystemMonitoringDTO;
import co.irexnet.waio.WAIO_ServerAgent.kafka.KafkaProducer;
import co.irexnet.waio.WAIO_ServerAgent.service.AlarmServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.AlarmInfoList;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import co.irexnet.waio.WAIO_ServerAgent.util.Conversion;
import co.irexnet.waio.WAIO_ServerAgent.util.GlobalSystemConfig;
import co.irexnet.waio.WAIO_ServerAgent.util.HttpSend;
import co.irexnet.waio.WAIO_ServerAgent.util.ProcessCodeList;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesAuthentication;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesDelegate;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesSystemCheck;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesVisualizeCheck;
import co.irexnet.waio.WAIO_ServerAgent.util.TagDescriptionList;
import co.irexnet.waio.WAIO_ServerAgent.util.VisualizeHealthStatus;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Scheduler {
    @Autowired
    PropertiesDelegate propertiesDelegate;

    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    PropertiesVisualizeCheck propertiesVisualizeCheck;

    @Autowired
    PropertiesSystemCheck propertiesSystemCheck;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    AlarmServiceImpl alarmService;

    @Autowired
    AlarmInfoList alarmInfoList;

    @Autowired
    TagDescriptionList tagDescriptionList;

    @Autowired
    GlobalSystemConfig globalSystemConfig;

    @Autowired
    ProcessCodeList processCodeList;

    @Autowired
    HttpSend httpSend;

    @Value("${server.port}")
    int nServerPort;

    // private int nVisualizeHealthCheck = CommonValue.HEALTH_CHECK_NORMAL;

    @Autowired
    VisualizeHealthStatus visualizeHealthStatus;

    @Autowired
    KafkaProducer kafkaProducer;

    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectionRequestTimeout(3 * CommonValue.ONE_SECOND)
            .build();

    // Partitioning Table 관리를 위한 스케쥴러(매일 23시 50분 실행)
    @Scheduled(cron = "0 50 23 * * ?")
    public void checkDatabasePartition() {
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/manageRtTable";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        httpSend.send(httpGet);

        // Database Check(send to internal)
		strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/database";
		httpGet = new HttpGet(strUri);
		httpGet.setConfig(requestConfig);
		httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
		httpSend.send(httpGet);
    }

    /**
     * 내부 변수 관리를 위한 스케쥴러(default: 매 10분)
     */
    @Scheduled(fixedDelayString = "${properties.check-process.period}")
    public void checkProcess() {
        log.debug("Check Process Start...Thread[{}], version:[{}]",
                Thread.currentThread().getName(), propertiesDelegate.getVersion());

        // Get all alarm_info
        alarmInfoList.setAlarmInfoList(databaseService.getAlarmInfo());
        log.debug("Get exist[{}] alarm_info", alarmInfoList.getSize());

        // Get system_config
        // globalSystemConfig.setSystemConfig(databaseService.getSystemConfig());
        log.debug("Get system_config:[{}]", globalSystemConfig);

        // Get all tag_description
        tagDescriptionList.setTagDescriptionList(databaseService.getAllTagDescription());
        log.debug("Get exist[{}] tag_description", tagDescriptionList.getSize());

        // Check local is vip
        httpSend.isVip();

        // Get all process_code
        // processCodeList.setProcessCodeList(databaseService.getAllProcessCode());
        log.debug("get Process: [{}]", processCodeList.getSize());

        Calendar calendar = Calendar.getInstance();
        int nDeleteCount = databaseService.delToken(calendar.getTime());
        List<AccessTokenDTO> accessTokenList = databaseService.getAllTokens();
        log.debug("Delete Token:[{}], Remain:[{}]", nDeleteCount, accessTokenList.size());

    }

    /**
     * 5분 이상 RT 테이블 미적재시 시스템 통신 연결 알람(팝업창) 발생
     */
    @Scheduled(fixedDelayString = "${properties.scada-check.period}", initialDelay = 3000)
    public void realTimeCheck() {
        log.debug("realTimeCheck...Thread:[{}]", Thread.currentThread().getName());
        
        // sensors
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/realTime";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        httpSend.send(httpGet);
    }
    
    /**
     * 1분에 한번씩 현재 운전모드를 체크하여 update
     */
    @Scheduled(fixedDelayString = "${properties.opr_check.period}", initialDelay = 3000)
    public void aiOprRealTimeCheck() {
        log.debug("aiOprRealTimeCheck...Thread:[{}]", Thread.currentThread().getName());
        
        // sensors
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/aiOprRealTime";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        httpSend.send(httpGet);
    }
    
    /**
     * 오늘 누적된 운전모드 운영시간을 이력 테이블에 적재(매일 자정에 실행)
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void aiOprHistoryInsert() {
        log.debug("aiOprHistoryInsert...Thread:[{}]", Thread.currentThread().getName());
        
        // sensors
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/aiOprHistory";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        httpSend.send(httpGet);
    }

    /**
     * kafka에 실시간 AI 예측값 전달을 위해 /internal/sensors API 호출
     */
    @Scheduled(fixedDelayString = "${properties.scada-check.period}", initialDelay = 3000)
    public void scadaCheck() {
        log.debug("sendScada...Thread:[{}]", Thread.currentThread().getName());

        // sensors
        String strUri = "http://" + propertiesDelegate.getLocalAddress() + ":" + nServerPort + "/internal/sensors";
        // String strUri = "http://localhost" + ":" + nServerPort + "/internal/sensors";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        httpSend.sendToLocal(httpGet);
    }

    /**
     * 분석 서버 Check를 위해 /internal/analysis API 호출
     */
    // @Scheduled(fixedDelayString = "${properties.analysis-check.period}",
    // initialDelay = 3000)
    public void analysisCheck() {
        log.debug("analysis HealthCheck...Thread[{}]", Thread.currentThread().getName());

        // Analysis Health Check(send to internal)
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/analysis";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        httpSend.send(httpGet);
    }

    /**
     * 데이터 수집기 상태 확인을 위한 /internal/daq API 호출
     */
    // @Scheduled(fixedDelayString = "${properties.daq_check.period}", initialDelay
    // = 3000)
    public void daqCheck() {
        log.debug("daq HealthCheck...Thread[{}]", Thread.currentThread().getName());

        // DAQ Health Check(send to internal)
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/daq";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        httpSend.send(httpGet);
    }

    /**
     * 시각화 시스템 API 이중화 상태 확인
     */
    // @Scheduled(fixedDelayString = "${properties.visualize-check.period}",
    // initialDelay = 3000)
    public void visualizeCheck() {
        String strAddress = propertiesVisualizeCheck.getAddress();
        int nPort = propertiesVisualizeCheck.getPort();
        log.debug("visualize HealthCheck address:[{}]...Thread[{}]", strAddress, Thread.currentThread().getName());
        if (Conversion.isValidInet4Address(strAddress) == true) {
            // Send Health Check Message
            // 상대방 API Server에 /hostname API 호출
            String strUri = "http://" + strAddress + ":" + nServerPort + "/hostname";
            HttpGet httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);
            httpGet.setHeader("Content-Type", "application/json");
            HttpResponse response = httpSend.send(httpGet);
            if (response != null) {
                int nStatus = response.getStatusLine().getStatusCode();

                if (nStatus == HttpStatus.SC_OK) {
                    // 정상 상태인 경우 시스템 모니터링에 정상 상태 등록
                    visualizeHealthStatus.setStatus(CommonValue.HEALTH_CHECK_NORMAL);

                    List<InterfaceInfoDTO> interfaceInfoList = databaseService.getInterfaceInfoFromAddress(strAddress);
                    if (interfaceInfoList.size() == 0) {
                        interfaceInfoList = databaseService
                                .getInterfaceInfoFromAddress(propertiesDelegate.getAddress());
                    }

                    if (interfaceInfoList.size() > 0) {
                        InterfaceInfoDTO interfaceInfo = interfaceInfoList.get(0);

                        log.debug("Health Check Response:[{}][{}]", nStatus, interfaceInfo.getHost());

                        // Insert system_monitoring
                        SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                        systemMonitoringDTO.setHost(interfaceInfo.getHost());
                        systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_VISUALIZE_API);
                        systemMonitoringDTO.setMntr_itm(interfaceInfo.getHost());
                        systemMonitoringDTO.setMntr_val(CommonValue.ALARM_VALUE_ON);
                        systemMonitoringDTO.setMntr_upd_ti(new Date());
                        databaseService.addSystemMonitoring(systemMonitoringDTO);
                    }
                } else {
                    // 잘못된 응답을 수신한 경우 Fail 상태로 변경
                    log.debug("Health Check Response:[{}]", nStatus);
                    visualizeHealthStatus.setFailStatus();
                }
            } else {
                // 응답이 없으면 Fail 상태로 변경
                log.error("Health Check [{}:{}] Error...", strAddress, nServerPort);
                visualizeHealthStatus.setFailStatus();
            }

            // 상대방 시각화 시스템 API가 정상 상태가 아닐 경우
            if (visualizeHealthStatus.getStatus() > CommonValue.HEALTH_CHECK_NORMAL) {
                List<InterfaceInfoDTO> interfaceInfoList = databaseService.getInterfaceInfoFromAddress(strAddress);
                if (interfaceInfoList.size() > 0) {
                    InterfaceInfoDTO interfaceInfoDTO = interfaceInfoList.get(0);

                    // Insert System Monitoring(another visualize API)
                    SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                    systemMonitoringDTO.setHost(interfaceInfoDTO.getHost());
                    systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_VISUALIZE_API);
                    systemMonitoringDTO.setMntr_itm(interfaceInfoDTO.getHost());
                    systemMonitoringDTO.setMntr_val(CommonValue.ALARM_VALUE_OFF);
                    systemMonitoringDTO.setMntr_upd_ti(new Date());
                    databaseService.addSystemMonitoring(systemMonitoringDTO);

                    // 알람 상태인 경우 알람 발보
                    if (visualizeHealthStatus.getStatus() == CommonValue.HEALTH_CHECK_ALARM) {
                        String strHostname = interfaceInfoDTO.getHost();
                        String strAlarmCode;

                        if (strHostname.equalsIgnoreCase(CommonValue.ANALYSIS1_HOSTNAME) == true) {
                            strAlarmCode = CommonValue.ALARM_CODE_VISUALIZE_API_OFF1;
                        } else {
                            strAlarmCode = CommonValue.ALARM_CODE_VISUALIZE_API_OFF2;
                        }
                        // Insert alarm_notify & SCADA send
                        alarmService.alarmNotify(
                                strAlarmCode,
                                strHostname,
                                CommonValue.ALARM_VALUE_OFF,
                                true);

                        // Send System Shutdown Request
                        // strUri = "http://" + strAddress + ":" + nPort + "/shutdown";
                        // HttpPost httpPost = new HttpPost(strUri);
                        // httpPost.setConfig(requestConfig);
                        // httpPost.setHeader("Content-Type", "application/json");
                        // httpSend.send(httpPost);
                    }
                } else {
                    log.error("Unknown hostname:[{}]", strAddress);
                }

                // Insert system_monitoring(myself)
                // 상대방 시각화 시스템 API가 정상 상태가 아닌 경우
                // 상대방이 자신의 시각화 시스템 API 상태를 업데이트하지 못하기 때문에
                // 자기 자신의 상태를 업데이트
                try {
                    SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                    systemMonitoringDTO.setHost(InetAddress.getLocalHost().getHostName());
                    systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_VISUALIZE_API);
                    systemMonitoringDTO.setMntr_itm(InetAddress.getLocalHost().getHostName());
                    systemMonitoringDTO.setMntr_val(CommonValue.ALARM_VALUE_ON);
                    systemMonitoringDTO.setMntr_upd_ti(new Date());
                    databaseService.addSystemMonitoring(systemMonitoringDTO);
                } catch (UnknownHostException e) {
                    log.error("Unknown myself host");
                }
            }
        } else {
            log.error("Invalid HealthCheck Address:[{}]", strAddress);
        }
    }

    /**
     * 전체 시스템 상태 체크
     */
    // @Scheduled(fixedDelayString = "${properties.system-check.period}",
    // initialDelay= 3000)
    public void systemCheck() {
        log.debug("systemCheck start...Thread[{}]", Thread.currentThread().getName());
        // 시스템 정보 조회
        // List<SystemInfoDTO> systemInfoList = databaseService.getAllSystemInfo();

        // 상태 체크 기준 시간 조회(Default : 5분)
        long different = propertiesSystemCheck.getReference();
        Date currentDate = new Date();

        // if(systemInfoList.size() > 0)
        // {
        // for(SystemInfoDTO systemInfoDTO : systemInfoList)
        // {
        // // Check system_info update_time
        // String strHostname = systemInfoDTO.getHost();
        // String strAlarmCode;

        // if(strHostname.equalsIgnoreCase(CommonValue.ANALYSIS1_HOSTNAME) == true)
        // {
        // strAlarmCode = CommonValue.ALARM_CODE_SYSTEM_OFF1;
        // }
        // else if(strHostname.equalsIgnoreCase(CommonValue.ANALYSIS2_HOSTNAME) == true)
        // {
        // strAlarmCode = CommonValue.ALARM_CODE_SYSTEM_OFF2;
        // }
        // else if(strHostname.equalsIgnoreCase(CommonValue.ANALYSIS3_HOSTNAME) == true)
        // {
        // strAlarmCode = CommonValue.ALARM_CODE_SYSTEM_OFF3;
        // }
        // else if(strHostname.equalsIgnoreCase(CommonValue.ANALYSIS4_HOSTNAME) == true)
        // {
        // strAlarmCode = CommonValue.ALARM_CODE_SYSTEM_OFF4;
        // }
        // else if(strHostname.equalsIgnoreCase(CommonValue.ANALYSIS5_HOSTNAME) == true)
        // {
        // strAlarmCode = CommonValue.ALARM_CODE_SYSTEM_OFF5;
        // }
        // else
        // {
        // log.error("systemCheck(), unknown hostname:[{}]", strHostname);
        // continue;
        // }

        // // 시스템 정보의 업데이트 시간이 기준 시간을 초과했을 경우 알람 발보
        // if(currentDate.getTime() - systemInfoDTO.getSys_upd_ti().getTime() >
        // different)
        // {
        // // Insert alarm_notify & SCADA send
        // alarmService.alarmNotify(
        // strAlarmCode,
        // strHostname,
        // CommonValue.ALARM_VALUE_OFF,
        // true);
        // }
        // }
        // }
        // else
        // {
        // log.error("Has no system info");
        // }
    }

    /**
     * 공정별 AI 제어값을 Kafka로 전송하기 위한 /internal/control API 호출
     */
    @Scheduled(fixedDelayString = "${properties.control-check.period}", initialDelay = 3000)
    public void controlCheck() {
        log.debug("controlCheck start...Thread[{}]", Thread.currentThread().getName());

        // Control Check(send to internal)
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/control";
        // String strUri = "http://localhost:" + nServerPort + "/internal/control";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        httpSend.send(httpGet);
    }

    /**
     * 통합 운영 시스템 알람을 Kafka로 전송하기 위한 /internal/alarm API 호출
     */
    @Scheduled(fixedDelayString = "${properties.alarm-check.period}", initialDelay = 5000)
    public void alarmCheck() {
        log.debug("alarmCheck start...Thread[{}]", Thread.currentThread().getName());

        // Alarm Check(send to internal)
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/alarm";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        httpSend.send(httpGet);
    }
    /*
     * // 알고리즘 상태 확인을 위한 /internal/algorithm API 호출
     * 
     * @Scheduled(fixedDelayString = "${properties.algorithm-check.period}",
     * initialDelay = 7000)
     * public void algorithmCheck()
     * {
     * log.debug("algorithmCheck Start...Thread[{}]",
     * Thread.currentThread().getName());
     * 
     * // Algorithm Check(send to internal)
     * String strUri = "http://" + propertiesDelegate.getAddress() + ":" +
     * nServerPort + "/internal/algorithm";
     * HttpGet httpGet = new HttpGet(strUri);
     * httpGet.setConfig(requestConfig);
     * httpGet.setHeader("X-ACCESS-TOKEN",
     * propertiesAuthentication.getInternalToken());
     * httpSend.send(httpGet);
     * }
     */
}
