package co.irexnet.waio.WAIO_ServerAgent.controller;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.*;
import co.irexnet.waio.WAIO_ServerAgent.dto.*;
import co.irexnet.waio.WAIO_ServerAgent.kafka.KafkaProducer;
import co.irexnet.waio.WAIO_ServerAgent.service.AlarmServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.*;
import co.irexnet.waio.WAIO_ServerAgent.vo.HadoopClusterInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.vo.HadoopClusterMetricsDTO;
import co.irexnet.waio.WAIO_ServerAgent.vo.HadoopJmxBeans;
import co.irexnet.waio.WAIO_ServerAgent.vo.SupervisorStateInfoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class InternalController
{
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    PropertiesControlCheck propertiesControlCheck;

    @Autowired
    PropertiesAlgorithmCheck propertiesAlgorithmCheck;

    @Autowired
    PropertiesStorage propertiesStorage;

    @Autowired
    PropertiesReceivingData propertiesReceivingData;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    AlarmServiceImpl alarmService;

    @Autowired
    AlarmInfoList alarmInfoList;

    @Autowired
    GlobalSystemConfig globalSystemConfig;

    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    HttpSend httpSend;

    private Date sensorDate = null;
    private Date analysisDate = null;
    private Date daqDate = null;
    private Date controlDate = null;
    private Date alarmDate = null;
    private Date algorithmDate = null;
    private Date databaseDate = null;
    private Date realTimeDate = null;
    private Date aiOprRtDate = null;
    private Date aiOprRtAndHisDate = null;

    private String lastCoagulants1 = "", lastCoagulants2 = "";

    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectionRequestTimeout(3 * CommonValue.ONE_SECOND)
            .build();

    /**
     * Kafka에 실시간 AI 예측값 전달
     * 
     * @param token
     */
    @RequestMapping(value = "/internal/sensors", method = RequestMethod.GET)
    public void getSensors(@RequestHeader("X-ACCESS-TOKEN") String token)
    {

        /*
        * // Coagulant Change Alarm
                    // Compare to before Coagulants
                    if(lastCoagulants1.equalsIgnoreCase("") == true ||
                            lastCoagulants2.equalsIgnoreCase("") == true)
                    {
                        lastCoagulants1 = coagulantsAnalysisDTO.getChemical1();
                        lastCoagulants2 = coagulantsAnalysisDTO.getChemical2();
                    }

                    String strHostname;
                    try
                    {
                        strHostname = InetAddress.getLocalHost().getHostName();
                    }
                    catch(UnknownHostException e)
                    {
                        strHostname = CommonValue.ANALYSIS1_HOSTNAME;
                    }

                    if(lastCoagulants1.equalsIgnoreCase(coagulantsAnalysisDTO.getChemical1()) == false)
                    {
                        lastCoagulants1 = coagulantsAnalysisDTO.getChemical1();

                        // Insert alarm_notify & SCADA send
                        alarmService.alarmNotify(
                                CommonValue.ALARM_CODE_COAGULANTS_CHANGE1,
                                strHostname,
                                CommonValue.ALARM_VALUE_CHANGE,
                                false);
                    }

                    if(lastCoagulants2.equalsIgnoreCase(coagulantsAnalysisDTO.getChemical2()) == false)
                    {
                        lastCoagulants2 = coagulantsAnalysisDTO.getChemical2();

                        // Insert alarm_notify & SCADA send
                        alarmService.alarmNotify(
                                CommonValue.ALARM_CODE_COAGULANTS_CHANGE2,
                                strHostname,
                                CommonValue.ALARM_VALUE_CHANGE,
                                false);
                    }
        * */

        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("getSensors, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getSensor() initialization sensorDate before one hour
        if(sensorDate == null)
        {
            sensorDate = new Date();
            sensorDate.setTime(sensorDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.debug("[internal] getSensors");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - sensorDate.getTime() > CommonValue.ONE_MINUTE)
        {
            sensorDate = new Date();

            // 실시간 데이터 처리를 Kafka로 하면서 삭제
//            // GET /sensors to SCADA1
//            String strSCADAUri = "http://" + globalSystemConfig.getScada1_information() + "/sensors";
//            HttpGet httpGet = new HttpGet(strSCADAUri);
//            httpGet.setConfig(requestConfig);
//
//            HttpResponse response = httpSend.send(httpGet);
//            if(response == null)
//            {
//                log.error("Send /sensors to SCADA1 response null");
//
//                // Insert alarm_notify & SCADA send
//                alarmService.alarmNotify(
//                        CommonValue.ALARM_CODE_COLLECTOR_OFF1,
//                        globalSystemConfig.getScada1_information(),
//                        CommonValue.ALARM_VALUE_OFF,
//                        false);
//            }
//
//            // GET /sensors to SCADA2
//            strSCADAUri = "http://" + globalSystemConfig.getScada2_information() + "/sensors";
//            httpGet = new HttpGet(strSCADAUri);
//            httpGet.setConfig(requestConfig);
//
//            response = httpSend.send(httpGet);
//            if(response == null)
//            {
//                log.error("Send /sensors to SCADA2 response null");
//
//                // Insert alarm_notify & SCADA send
//                alarmService.alarmNotify(
//                        CommonValue.ALARM_CODE_COLLECTOR_OFF2,
//                        globalSystemConfig.getScada2_information(),
//                        CommonValue.ALARM_VALUE_OFF,
//                        false);
//            }

            // get tag_manage(UI)
//            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);

            //Coagulant Process 
            int coagulantCnt = sendAiCoagulantData();
            //Mixing Process
            int mixingCnt = sendAiMixingData();
            //Disinfection Process
            int disinfectionCnt = sendAiDisinfectionData();

            log.info("Send count, coagulant:[{}], mixing:[{}], disinfection:[{}]",
            	coagulantCnt,
            	mixingCnt,
            	disinfectionCnt
            );
        }
    }

    /**
     * 분석 서버 Check
     * 
     * @param token 토큰
     */
    @RequestMapping(value = "/internal/analysis", method = RequestMethod.GET)
    public void getAnalysis(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("getAnalysis, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getAnalysis() initialization analysisDate before one hour
        if(analysisDate == null)
        {
            analysisDate = new Date();
            analysisDate.setTime(analysisDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.debug("[internal] getAnalysis");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - analysisDate.getTime() > CommonValue.ONE_MINUTE)
        {
            analysisDate = new Date();

            // Resource Manager1 : [GET] cluster/info
            int nActiveState = CommonValue.ACTIVE_STATE_NONE;
            String strHaState, strActiveNodes;
            String strUri = "http://" + globalSystemConfig.getAnalysis1_ResourceManager() + "/ws/v1/cluster/info";
            HttpGet httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);
            StringBuffer stringBuffer;
            ObjectMapper objectMapper = new ObjectMapper();

            HttpResponse response = httpSend.send(httpGet);
            if(response != null)
            {
                int nStatus = response.getStatusLine().getStatusCode();
                if(nStatus == HttpStatus.SC_OK)
                {
                    // 하둡 클러스터 정보 response에 대한 Parsing
                    stringBuffer = new StringBuffer();

                    BufferedReader bufferedReader = null;
                    InputStreamReader inputStreamReader = null;
                    try
                    {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while((strLine = bufferedReader.readLine()) != null)
                        {
                            stringBuffer.append(strLine);
                        }

                        // ObjectMapper를 통해 하둡 클러스터 정보 저장
                        HadoopClusterInfoDTO clusterInfo = objectMapper.readValue(stringBuffer.toString(), HadoopClusterInfoDTO.class);
                        strHaState = clusterInfo.getClusterInfo().getHaState();
                        log.debug("clusterInfo1, haState:[{}]", strHaState);

                        if(strHaState.equalsIgnoreCase(CommonValue.HASTATE_ACTIVE) == true)
                        {
                            nActiveState = CommonValue.ACTIVE_STATE_FIRST;
                        }
                    }
                    catch(IOException e)
                    {
                        log.error("Invalid Body or BufferedReader...");
                        strHaState = CommonValue.HASTATE_ERROR;
                    }
                    finally
                    {
                        if(inputStreamReader != null)
                        {
                            try
                            {
                                inputStreamReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("inputStreamReader Close Exception occurred");
                            }
                        }
                        if(bufferedReader != null)
                        {
                            try
                            {
                                bufferedReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("bufferedReader Close Exception occurred");
                            }
                        }
                    }
                }
                else
                {
                    strHaState = CommonValue.HASTATE_ERROR;
                }
            }
            else
            {
                strHaState = CommonValue.HASTATE_ERROR;
            }

            // Insert system_monitoring(Hadoop Resource Manager)
            SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHost(CommonValue.ANALYSIS1_HOSTNAME);
            systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
            systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_RM);
            systemMonitoringDTO.setMntr_val(strHaState.toUpperCase());
            systemMonitoringDTO.setMntr_upd_ti(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            // Resource Manager2 : cluster/info
            strUri = "http://" + globalSystemConfig.getAnalysis2_ResourceManager() + "/ws/v1/cluster/info";
            httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);

            response = httpSend.send(httpGet);
            if(response != null)
            {
                int nStatus = response.getStatusLine().getStatusCode();
                if(nStatus == HttpStatus.SC_OK)
                {
                    // 하둡 클러스터 정보 Response에 대한 Parsing
                    stringBuffer = new StringBuffer();

                    BufferedReader bufferedReader = null;
                    InputStreamReader inputStreamReader = null;
                    try
                    {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while((strLine = bufferedReader.readLine()) != null)
                        {
                            stringBuffer.append(strLine);
                        }

                        // ObjectMapper를 통해 하둡 클러스터 정보 저장
                        HadoopClusterInfoDTO clusterInfo = objectMapper.readValue(stringBuffer.toString(), HadoopClusterInfoDTO.class);
                        strHaState = clusterInfo.getClusterInfo().getHaState();
                        log.debug("clusterInfo2, haState:[{}]", strHaState);

                        if(strHaState.equalsIgnoreCase(CommonValue.HASTATE_ACTIVE) == true)
                        {
                            nActiveState = CommonValue.ACTIVE_STATE_SECOND;
                        }
                    }
                    catch(IOException e)
                    {
                        log.error("Invalid Body or BufferedReader...");
                        strHaState = CommonValue.HASTATE_ERROR;
                    }
                    finally
                    {
                        if(inputStreamReader != null)
                        {
                            try
                            {
                                inputStreamReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("inputStreamReader Close Exception occurred");
                            }
                        }
                        if(bufferedReader != null)
                        {
                            try
                            {
                                bufferedReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("bufferedReader Close Exception occurred");
                            }
                        }
                    }
                }
                else
                {
                    strHaState = CommonValue.HASTATE_ERROR;
                }
            }
            else
            {
                strHaState = CommonValue.HASTATE_ERROR;
            }

            // Insert system_monitoring(Hadoop Resource Manager)
            systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHost(CommonValue.ANALYSIS2_HOSTNAME);
            systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
            systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_RM);
            systemMonitoringDTO.setMntr_val(strHaState.toUpperCase());
            systemMonitoringDTO.setMntr_upd_ti(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            // Resource Manager1 : cluster/metrics
            if(nActiveState > CommonValue.ACTIVE_STATE_NONE)
            {
                // ACTIVE Server의 주소로 요청
                String strHostname;
                if(nActiveState == CommonValue.ACTIVE_STATE_FIRST)
                {
                    strUri = "http://" + globalSystemConfig.getAnalysis1_ResourceManager() + "/ws/v1/cluster/metrics";
                    strHostname = CommonValue.ANALYSIS1_HOSTNAME;
                }
                else
                {
                    strUri = "http://" + globalSystemConfig.getAnalysis2_ResourceManager() + "/ws/v1/cluster/metrics";
                    strHostname = CommonValue.ANALYSIS2_HOSTNAME;
                }

                httpGet = new HttpGet(strUri);
                httpGet.setConfig(requestConfig);

                response = httpSend.send(httpGet);
                if(response != null)
                {
                    int nStatus = response.getStatusLine().getStatusCode();
                    if(nStatus == HttpStatus.SC_OK)
                    {
                        // Metric 정보 Response에 대한 Parsing
                        stringBuffer = new StringBuffer();

                        BufferedReader bufferedReader = null;
                        InputStreamReader inputStreamReader = null;
                        try
                        {
                            inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                            bufferedReader = new BufferedReader(inputStreamReader);

                            String strLine;
                            while((strLine = bufferedReader.readLine()) != null)
                            {
                                stringBuffer.append(strLine);
                            }

                            // Cluster Metric 정보를 저장하여 active node 계산
                            HadoopClusterMetricsDTO clusterMetrics =
                                    objectMapper.readValue(stringBuffer.toString(), HadoopClusterMetricsDTO.class);

                            int nTemp = clusterMetrics.getClusterMetrics().getActiveNodes();
                            strActiveNodes = String.format("%d", nTemp);
                            log.debug("clusterMetrics, activeNodes:[{}]", clusterMetrics.getClusterMetrics().getActiveNodes());
                        }
                        catch(IOException e)
                        {
                            log.error("Invalid Body or BufferedReader...");
                            strActiveNodes = "-";
                        }
                        finally
                        {
                            if(inputStreamReader != null)
                            {
                                try
                                {
                                    inputStreamReader.close();
                                }
                                catch(IOException e)
                                {
                                    log.error("inputStreamReader Close Exception occurred");
                                }
                            }
                            if(bufferedReader != null)
                            {
                                try
                                {
                                    bufferedReader.close();
                                }
                                catch(IOException e)
                                {
                                    log.error("bufferedReader Close Exception occurred");
                                }
                            }
                        }
                    }
                    else
                    {
                        strActiveNodes = "-";
                    }
                }
                else
                {
                    strActiveNodes = "-";
                }

                // Insert system_monitoring(Node Manager)
                systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHost(strHostname);
                systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
                systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_NM);
                systemMonitoringDTO.setMntr_val(strActiveNodes);
                systemMonitoringDTO.setMntr_upd_ti(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);
            }
            else
            {
                // Insert alarm_notify & SCADA send
                alarmService.alarmNotify(
                        CommonValue.ALARM_CODE_ANALYSIS_OFF,
                        CommonValue.ANALYSIS1_HOSTNAME,
                        CommonValue.ALARM_VALUE_OFF,
                        true);
            }


            // NameNode, DataNode1 : jmx
            nActiveState = CommonValue.ACTIVE_STATE_NONE;
            strUri = "http://" + globalSystemConfig.getAnalysis1_NameNode() + "/jmx?qry=Hadoop:service=NameNode,name=FSNamesystem";
            httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);

            response = httpSend.send(httpGet);
            if(response != null)
            {
                int nStatus = response.getStatusLine().getStatusCode();
                if(nStatus == HttpStatus.SC_OK)
                {
                    // JMX Response에 대한 Parsing
                    stringBuffer = new StringBuffer();
                    BufferedReader bufferedReader = null;
                    InputStreamReader inputStreamReader = null;
                    try
                    {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while((strLine = bufferedReader.readLine()) != null)
                        {
                            stringBuffer.append(strLine);
                        }

                        // JMX 정보를 저장하고 data node 저장
                        HadoopJmxBeans jmxBeans = objectMapper.readValue(stringBuffer.toString(), HadoopJmxBeans.class);
                        strHaState = jmxBeans.getBeans().get(0).getTagHaSate();
                        int nTemp = jmxBeans.getBeans().get(0).getNumLiveDataNodes();
                        strActiveNodes = String.format("%d", nTemp);
                        log.debug("jmxBeans, HAState:[{}], NumLiveDataNodes:[{}]",
                                jmxBeans.getBeans().get(0).getTagHaSate(),
                                jmxBeans.getBeans().get(0).getNumLiveDataNodes());
                    }
                    catch(IOException e)
                    {
                        log.error("Invalid Body or BufferedReader...");
                        strHaState = CommonValue.HASTATE_ERROR;
                        strActiveNodes = "-";
                    }
                    finally
                    {
                        if(inputStreamReader != null)
                        {
                            try
                            {
                                inputStreamReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("inputStreamReader Close Exception occurred");
                            }
                        }
                        if(bufferedReader != null)
                        {
                            try
                            {
                                bufferedReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("bufferedReader Close Exception occurred");
                            }
                        }
                    }
                }
                else
                {
                    strHaState = CommonValue.HASTATE_ERROR;
                    strActiveNodes = "-";
                }
            }
            else
            {
                strHaState = CommonValue.HASTATE_ERROR;
                strActiveNodes = "-";
            }

            // Insert system_monitoring(Name Node)
            systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHost(CommonValue.ANALYSIS1_HOSTNAME);
            systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
            systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_NN);
            systemMonitoringDTO.setMntr_val(strHaState.toUpperCase());
            systemMonitoringDTO.setMntr_upd_ti(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            if(strHaState.equalsIgnoreCase(CommonValue.HASTATE_ACTIVE) == true)
            {
                nActiveState = CommonValue.ACTIVE_STATE_FIRST;

                // Insert system_monitoring
                systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHost(CommonValue.ANALYSIS1_HOSTNAME);
                systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
                systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_DN);
                systemMonitoringDTO.setMntr_val(strActiveNodes);
                systemMonitoringDTO.setMntr_upd_ti(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);
            }

            // NameNode, DataNode2 : jmx
            strUri = "http://" + globalSystemConfig.getAnalysis2_NameNode() + "/jmx?qry=Hadoop:service=NameNode,name=FSNamesystem";
            httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);

            response = httpSend.send(httpGet);
            if(response != null)
            {
                int nStatus = response.getStatusLine().getStatusCode();
                if(nStatus == HttpStatus.SC_OK)
                {
                    // JMX Response에 대한 Parsing
                    stringBuffer = new StringBuffer();
                    BufferedReader bufferedReader = null;
                    InputStreamReader inputStreamReader = null;
                    try
                    {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while((strLine = bufferedReader.readLine()) != null)
                        {
                            stringBuffer.append(strLine);
                        }

                        // JMX 정보를 저장하고 data node 저장
                        HadoopJmxBeans jmxBeans = objectMapper.readValue(stringBuffer.toString(), HadoopJmxBeans.class);
                        strHaState = jmxBeans.getBeans().get(0).getTagHaSate();
                        int nTemp = jmxBeans.getBeans().get(0).getNumLiveDataNodes();
                        strActiveNodes = String.format("%d", nTemp);
                        log.debug("jmxBeans, HAState:[{}], NumLiveDataNodes:[{}]",
                                jmxBeans.getBeans().get(0).getTagHaSate(),
                                jmxBeans.getBeans().get(0).getNumLiveDataNodes());
                    }
                    catch(IOException e)
                    {
                        log.error("Invalid Body or BufferedReader...");
                        strHaState = CommonValue.HASTATE_ERROR;
                        strActiveNodes = "-";
                    }
                    finally
                    {
                        if(inputStreamReader != null)
                        {
                            try
                            {
                                inputStreamReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("inputStreamReader Close Exception occurred");
                            }
                        }
                        if(bufferedReader != null)
                        {
                            try
                            {
                                bufferedReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("bufferedReader Close Exception occurred");
                            }
                        }
                    }
                }
                else
                {
                    strHaState = CommonValue.HASTATE_ERROR;
                    strActiveNodes = "-";
                }
            }
            else
            {
                strHaState = CommonValue.HASTATE_ERROR;
                strActiveNodes = "-";
            }

            // Insert system_monitoring(Name Node)
            systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHost(CommonValue.ANALYSIS2_HOSTNAME);
            systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
            systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_NN);
            systemMonitoringDTO.setMntr_val(strHaState.toUpperCase());
            systemMonitoringDTO.setMntr_upd_ti(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            if(strHaState.equalsIgnoreCase(CommonValue.HASTATE_ACTIVE) == true)
            {
                nActiveState = CommonValue.ACTIVE_STATE_SECOND;

                // Insert system_monitoring
                systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHost(CommonValue.ANALYSIS2_HOSTNAME);
                systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
                systemMonitoringDTO.setMntr_itm(CommonValue.HADOOP_DN);
                systemMonitoringDTO.setMntr_val(strActiveNodes);
                systemMonitoringDTO.setMntr_upd_ti(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);
            }

            // Name Node Active Check(Insert alarm_notify, same resource manager's alarm)
            if(nActiveState == CommonValue.ACTIVE_STATE_NONE)
            {
                // Insert alarm_notify & SCADA send
                alarmService.alarmNotify(
                        CommonValue.ALARM_CODE_ANALYSIS_OFF,
                        CommonValue.ANALYSIS1_HOSTNAME,
                        CommonValue.ALARM_VALUE_OFF,
                        true);
            }
        }
    }

    /**
     * 데이터 수집기 상태 확인
     * 
     * @param token 토큰
     */
    @RequestMapping(value = "/internal/daq", method = RequestMethod.GET)
    public void getDaq(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("getDaq, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getDaq() initialization daqDate before one hour
        if(daqDate == null)
        {
            daqDate = new Date();
            daqDate.setTime(daqDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.debug("[internal] getDaq");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - daqDate.getTime() > CommonValue.ONE_MINUTE)
        {
            daqDate = new Date();

            // 최근 5분 간 데이터 수집기 HealthCheck 이력을 조회하기 위한 Date 선언
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            Date startTime = calendar.getTime();

            List<SystemMonitoringDTO> systemMonitoringList = databaseService.getLatestSystemMonitoring(startTime);
            log.debug("getLatestSystemMonitoring, result:[{}]", systemMonitoringList.size());

            String strDaq1Value = CommonValue.ALARM_VALUE_OFF;
            String strDaq2Value = CommonValue.ALARM_VALUE_OFF;

            // 최근 5분 간 데이터 수집기 측정 이력이 있다면 해당 값 저장
            for(SystemMonitoringDTO dto : systemMonitoringList)
            {
                if(dto.getMntr_ty() == CommonValue.MONITORING_TYPE_COLLECTOR)
                {
                    if(dto.getHost().equalsIgnoreCase(CommonValue.COLLECTOR1_HOSTNAME) == true)
                    {
                        strDaq1Value = dto.getMntr_val();
                    }
                    else if(dto.getHost().equalsIgnoreCase(CommonValue.COLLECTOR2_HOSTNAME) == true)
                    {
                        strDaq2Value = dto.getMntr_val();
                    }
                }
            }

            // If alarm value is 'OFF' then, insert alarm_notify and insert system_monitoring
            if(strDaq1Value.equalsIgnoreCase(CommonValue.ALARM_VALUE_OFF) == true)
            {
                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHost(CommonValue.COLLECTOR1_HOSTNAME);
                systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_COLLECTOR);
                systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR1_HOSTNAME);
                systemMonitoringDTO.setMntr_val(CommonValue.ALARM_VALUE_OFF);
                systemMonitoringDTO.setMntr_upd_ti(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);

                // Insert alarm_notify & SCADA send
                alarmService.alarmNotify(
                        CommonValue.ALARM_CODE_COLLECTOR_OFF1,
                        CommonValue.COLLECTOR1_HOSTNAME,
                        CommonValue.ALARM_VALUE_OFF,
                        true);
            }

            // If alarm value is 'OFF' then, insert alarm_notify and insert system_monitoring
            if(strDaq2Value.equalsIgnoreCase(CommonValue.ALARM_VALUE_OFF) == true)
            {
                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHost(CommonValue.COLLECTOR2_HOSTNAME);
                systemMonitoringDTO.setMntr_ty(CommonValue.MONITORING_TYPE_COLLECTOR);
                systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR2_HOSTNAME);
                systemMonitoringDTO.setMntr_val(CommonValue.ALARM_VALUE_OFF);
                systemMonitoringDTO.setMntr_upd_ti(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);

                // Insert alarm_notify & SCADA send
                alarmService.alarmNotify(
                        CommonValue.ALARM_CODE_COLLECTOR_OFF2,
                        CommonValue.COLLECTOR2_HOSTNAME,
                        CommonValue.ALARM_VALUE_OFF,
                        true);
            }
//            boolean bResponseOK = false;
//
//            // DAQ1 API Check
//            String strUri = "http://" + globalSystemConfig.getScada1_daq() + "/api/plugins.json";
//            HttpGet httpGet = new HttpGet(strUri);
//            httpGet.setConfig(requestConfig);
//
//            HttpResponse response = httpSend.send(httpGet);
//            if(response != null)
//            {
//                int nStatus = response.getStatusLine().getStatusCode();
//                log.debug("DAQ1 HealthCheck...Response:[{}]", nStatus);
//                if(nStatus == HttpStatus.SC_OK)
//                {
//                    bResponseOK = true;
//                }
//                else
//                {
//                    bResponseOK = false;
//                }
//            }
//            else
//            {
//                bResponseOK = false;
//                log.debug("DAQ1 HealthCheck...Response:[ERROR]");
//            }
//
//            // According to bResponseOK value, Insert system_monitoring
//            if(bResponseOK == true)
//            {
//                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
//                systemMonitoringDTO.setHostname(CommonValue.COLLECTOR1_HOSTNAME);
//                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
//                systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR1_HOSTNAME);
//                systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_ON);
//                systemMonitoringDTO.setUpdate_time(new Date());
//                databaseService.addSystemMonitoring(systemMonitoringDTO);
//            }
//            else
//            {
//                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
//                systemMonitoringDTO.setHostname(CommonValue.COLLECTOR1_HOSTNAME);
//                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
//                systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR1_HOSTNAME);
//                systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_OFF);
//                systemMonitoringDTO.setUpdate_time(new Date());
//                databaseService.addSystemMonitoring(systemMonitoringDTO);
//
//                // Insert alarm_notify & SCADA send
//                alarmService.alarmNotify(
//                        CommonValue.ALARM_CODE_DAQ_OFF1,
//                        CommonValue.COLLECTOR1_HOSTNAME,
//                        CommonValue.ALARM_VALUE_OFF,
//                        true);
//            }
//
//            // DAQ2 API Check
//            bResponseOK = false;
//            strUri = "http://" + globalSystemConfig.getScada2_daq() + "/api/plugins.json";
//            httpGet = new HttpGet(strUri);
//            httpGet.setConfig(requestConfig);
//
//            response = httpSend.send(httpGet);
//            if(response != null)
//            {
//                int nStatus = response.getStatusLine().getStatusCode();
//                log.debug("DAQ2 HealthCheck...Response:[{}]", nStatus);
//                if(nStatus == HttpStatus.SC_OK)
//                {
//                    bResponseOK = true;
//                }
//                else
//                {
//                    bResponseOK = false;
//                }
//            }
//            else
//            {
//                bResponseOK = false;
//                log.debug("DAQ2 HealthCheck...Response:[ERROR]");
//            }
//
//            // According to bResponseOK value, Insert system_monitoring
//            if(bResponseOK == true)
//            {
//                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
//                systemMonitoringDTO.setHostname(CommonValue.COLLECTOR2_HOSTNAME);
//                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
//                systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR2_HOSTNAME);
//                systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_ON);
//                systemMonitoringDTO.setUpdate_time(new Date());
//                databaseService.addSystemMonitoring(systemMonitoringDTO);
//            }
//            else
//            {
//                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
//                systemMonitoringDTO.setHostname(CommonValue.COLLECTOR2_HOSTNAME);
//                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
//                systemMonitoringDTO.setMntr_itm(CommonValue.COLLECTOR2_HOSTNAME);
//                systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_OFF);
//                systemMonitoringDTO.setUpdate_time(new Date());
//                databaseService.addSystemMonitoring(systemMonitoringDTO);
//
//                // Insert alarm_notify & SCADA send
//                alarmService.alarmNotify(
//                        CommonValue.ALARM_CODE_DAQ_OFF2,
//                        CommonValue.COLLECTOR2_HOSTNAME,
//                        CommonValue.ALARM_VALUE_OFF,
//                        true);
//            }
        }
    }

    /**
     * 공정별 AI 제어값을 Kafka로 전송
     * 
     * @param token 토큰
     */
    @RequestMapping(value = "/internal/control", method = RequestMethod.GET)
    public void getControl(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("getControl, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getControl() initialization controlDate before one hour
        if(controlDate == null)
        {
            controlDate = new Date();
            controlDate.setTime(controlDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.debug("[internal] getControl");

        // Check ten seconds after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - controlDate.getTime() > propertiesControlCheck.getPeriod())
        {
            controlDate = new Date();
          // Coagulant Process
          getCoagulantControl();
          // Mixing Process
          getMixingControl();
          // Disinfection Process
          getDisinfectionControl();


        }
    }

    /**
     * 통합 운영 시스템 알람을 Kafka로 전송
     * 
     * @param token 토큰
     */
    @RequestMapping(value = "/internal/alarm", method = RequestMethod.GET)
    public void getAlarm(@RequestHeader("X-ACCESS-TOKEN") String token) {
        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getAlarm, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getAlarm() initialization alarmDate before one hour
        if(alarmDate == null) {
            alarmDate = new Date();
            alarmDate.setTime(alarmDate.getTime() - CommonValue.ONE_HOUR);
        }
        
        log.debug("[internal] getAlarm");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - alarmDate.getTime() > CommonValue.THIRTY_SECOND)
        {
            alarmDate = new Date();

            // 1. get latest(1minute) control value(kafka_flag = 0)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -1);
            Date alarmTime = calendar.getTime();
            
            for(int processStep : CommonValue.PROCESS_STEP_ARRAY) {
              getAllAlarm(CommonValue.PROCESS_COAGULANT, alarmTime, CommonValue.KAFKA_FLAG_INIT, processStep);
              getAllAlarm(CommonValue.PROCESS_MIXING, alarmTime, CommonValue.KAFKA_FLAG_INIT, processStep);
          	  getAllAlarm(CommonValue.PROCESS_DISINFECTION_PRE, alarmTime, CommonValue.KAFKA_FLAG_INIT, processStep);
              getAllAlarm(CommonValue.PROCESS_DISINFECTION_POST, alarmTime, CommonValue.KAFKA_FLAG_INIT, processStep);
            }
        }
    }
    /**
     * 데이터베이스 정리
     * 
     * @param token 토큰
     */
    @RequestMapping(value = "/internal/database", method = RequestMethod.GET)
    public void getDatabase(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("[Collector]getDatabase, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getDatabase() initialization databaseDate before one hour
        if(databaseDate == null)
        {
            databaseDate = new Date();
            databaseDate.setTime(databaseDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.info("[Collector] getDatabase");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - databaseDate.getTime() > CommonValue.ONE_MINUTE) {
            databaseDate = new Date();

            // Database check
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -propertiesStorage.getStorage());
            Date deleteTime = calendar.getTime();

            // log.debug([Collector]"Delete Login History:[{}]", databaseService.delLoginHistory(deleteTime));
            // log.debug([Collector]"Delete System Monitoring:[{}]", databaseService.delSystemMonitoring(deleteTime));
            // log.debug([Collector]"Delete Sensor:[{}]", databaseService.delSensor(deleteTime));

            for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
                log.info("[Collector]Delete TB_AI_C_RT:[{}]", databaseService.delAiCoagulantRealtimeValue(deleteTime, processStep));
                log.info("[Collector]Delete TB_AI_C_CTR:[{}]", databaseService.delAiCoagulantControl(deleteTime, processStep));
                log.info("[Collector]Delete TB_AI_D_RT:[{}]", databaseService.delAiMixingRealtimeValue(deleteTime, processStep));
                log.info("[Collector]Delete TB_AI_D_CTR:[{}]", databaseService.delAiMixingControl(deleteTime, processStep));
                log.info("[Collector]Delete TB_AI_PRE_G_RT:[{}]", databaseService.delAiDisinfectionRealtimeValue(deleteTime, processStep, CommonValue.DISINFECTION_PRE_STEP));
                log.info("[Collector]Delete TB_AI_PRE_G_CTR:[{}]", databaseService.delAiPreDisinfectionControl(deleteTime, processStep));
                log.info("[Collector]Delete TB_AI_POST_G_RT:[{}]", databaseService.delAiDisinfectionRealtimeValue(deleteTime, processStep, CommonValue.DISINFECTION_POST_STEP));
            	log.info("[Collector]Delete TB_AI_POST_G_CTR:[{}]", databaseService.delAiPostDisinfectionControl(deleteTime, processStep));
            }
        }
    }

    /**
     * 약품 실시간 AI 예측값 전달
     * 
     * @param tagManageList
     */
    public int sendAiCoagulantData() {
    	int sendCnt = 0;
        ///////////////////////////////////////////////
        // Coagulant Process
        ///////////////////////////////////////////////
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // get ai_coagulant_realtime
            AiCoagulantRealtimeDTO aiCoagulantRealtime = databaseService.getLatestAiCoagulantRealtimeValue(processStep);
            log.debug("getLatestAiCoagulantRealtimeValue, result:[{}]", aiCoagulantRealtime != null ? 1 : 0);
            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI, CommonValue.PROCESS_COAGULANT, processStep);
            UsrMngDTO coagulantUsrMngDTO = databaseService.getUsrMng(processStep);
            if(aiCoagulantRealtime != null)
            {
                try
                {
                    LinkedHashMap<String, Object> controlMap, mapTemp;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = simpleDateFormat.format(aiCoagulantRealtime.getUpd_ti());
                    String strBody;
                    
                    // c_operation_mode
                    AiProcessInitDTO aiCoagulantInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE, processStep);
                    
                    if(aiCoagulantInit != null)
                    {
                        // 운전모드 태그는 전달하지 않음
//                        controlMap = new LinkedHashMap<>();
//                        controlMap.put("tag", aiCoagulantInit.getTag_sn());
//                        controlMap.put("value", aiCoagulantInit.getValue().intValue());
//                        controlMap.put("time", strDate);
//                        strBody = objectMapper.writeValueAsString(controlMap);
//                        kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                        // 운전모드 알람 태그는 항상 전송
                        for(TagManageDTO dto : tagManageList)
                        {
                            if(dto.getItm().equalsIgnoreCase("c_operation_mode_a") == true) // TODO 추후에 수정
                            {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getTag_sn());
                                controlMap.put("value", aiCoagulantInit.getInit_val().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                                sendCnt++;
                                break;
                            }
                        }
                    }
                    
                    for(TagManageDTO dto : tagManageList)
                    {
                    	// 약품공정이 아니면 continue
                    	if(dto.getProc_cd().substring(0,1).equalsIgnoreCase(CommonValue.PROCESS_COAGULANT) != true)
                        {
                    		continue;
                        }
                    	
                    	if(dto.getItm().equalsIgnoreCase("ai_c_ti") == true)
                    	{
                    		// ai update_time
                    		controlMap = new LinkedHashMap<>();
                    		controlMap.put("tag", dto.getTag_sn());
                    		controlMap.put("value", strDate);
                    		controlMap.put("time", strDate);
                    		strBody = objectMapper.writeValueAsString(controlMap);
                    		kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                            sendCnt++;
                    	}
                    	else if(dto.getItm().equalsIgnoreCase("ai_c1_cf_coagulant") == true)
                    	{
                    		// AI 약품 종류 예측
                    		controlMap = new LinkedHashMap<>();
                    		controlMap.put("tag", dto.getTag_sn());
                    		controlMap.put("value",coagulantUsrMngDTO.getInit_val());
                    		controlMap.put("time", strDate);
                    		strBody = objectMapper.writeValueAsString(controlMap);
                    		kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
    						sendCnt++;
                    	}
                    	else if(dto.getItm().equalsIgnoreCase("ai_c1_cf") == true)
                    	{
                    		// AI 약품 주입률 예측 최종값
                    		controlMap = new LinkedHashMap<>();
                    		controlMap.put("tag", dto.getTag_sn());
                    		controlMap.put("value", aiCoagulantRealtime.getAi_c_cf());
                    		controlMap.put("time", strDate);
                    		strBody = objectMapper.writeValueAsString(controlMap);
                    		kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
    						sendCnt++;
                    	}
                    }
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Coagulant Process");
                }
            }
        }
        return sendCnt;
    }
    /**
     * 혼화응집 실시간 AI 예측값 전달
     * 
     * @param tagManageList
     */
    public int sendAiMixingData() {
    	int sendCnt = 0;
        ///////////////////////////////////////////////
        // Mixing Process
        ///////////////////////////////////////////////
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // get ai_mixing_realtime
            AiMixingRealtimeDTO aiMixingRealtime = databaseService.getLatestAiMixingRealtimeValue(processStep);
            log.debug("getLatestAiMixingRealtimeValue, result:[{}]", aiMixingRealtime != null ? 1 : 0);
            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI, CommonValue.PROCESS_MIXING, processStep);
            // get location number(지 번호)
            TagManageRangeDTO mixingRange = databaseService.getTagManageRange(CommonValue.PROCESS_MIXING, processStep);
            log.debug("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_MIXING, mixingRange != null ? 1 : 0);
            if(aiMixingRealtime != null) {
                int nLocationMin = 1, nLocationMax = 2;
//                 if(mixingRange != null)
//                 {
//                     nLocationMin = mixingRange.getMin();
//                     nLocationMax = mixingRange.getMax();
//                 }
                try
                {
                    LinkedHashMap<String, Object> controlMap, mapTemp;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = simpleDateFormat.format(aiMixingRealtime.getUpd_ti());
                    String strBody;

                    // d_operation_mode
                    AiProcessInitDTO aiMixingInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE, processStep);
                    if(aiMixingInit != null)
                    {
                        // 운전모드 태그는 전달하지 않음
//                    controlMap = new LinkedHashMap<>();
//                    controlMap.put("tag", aiMixingInit.getTag_sn());
//                    controlMap.put("value", aiMixingInit.getValue().intValue());
//                    controlMap.put("time", strDate);
//                    strBody = objectMapper.writeValueAsString(controlMap);
//                    kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                        // 운전모드 알람 태그는 항상 전송
                        for(TagManageDTO dto : tagManageList)
                        {
                            if(dto.getItm().equalsIgnoreCase("d_operation_mode_a") == true)
                            {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getTag_sn());
                                controlMap.put("value", aiMixingInit.getInit_val().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                                sendCnt++;
                                break;
                            }
                        }
                    }

                    for(TagManageDTO dto : tagManageList)
                    {
                    	// 혼화응집공정이 아니면 continue
                    	if (dto.getProc_cd().substring(0,1).equalsIgnoreCase(CommonValue.PROCESS_MIXING) != true)
                    	{
                            continue;
                        }

                        if (dto.getItm().equalsIgnoreCase("ai_d_ti") == true)
                        {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
    						sendCnt++;
//                            log.debug("Send AI_RESULT kafka ai_d_ti tag:[{}], value:[{}]", dto.getTag_sn(), strDate);
                        }
//                        else if(dto.getItm().equalsIgnoreCase("d_de") == true)
//                        {
//                            // 물의 밀도
//                            controlMap = new LinkedHashMap<>();
//                            controlMap.put("tag", dto.getTag_sn());
//                            controlMap.put("value", aiMixingRealtime.getD_de());
//                            controlMap.put("time", strDate);
//                            strBody = objectMapper.writeValueAsString(controlMap);
//                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
//                        }
                        else if(dto.getItm().equalsIgnoreCase("d_dv_loc1") == true)
                        {
                            // 물의 점성계수 (1지)
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiMixingRealtime.getD_dv_loc1());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
    						sendCnt++;
                        }
                        else if(dto.getItm().equalsIgnoreCase("d_dv_loc2") == true)
                        {
                            // 물의 점성계수 (2지)
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiMixingRealtime.getD_dv_loc1());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
    						sendCnt++;
                        }
                        else if(dto.getItm().equalsIgnoreCase("d_im_d") == true)
                        {
                            // 임펠러 직경
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiMixingRealtime.getD_im_d());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
    						sendCnt++;
                        }
                        else if(dto.getItm().equalsIgnoreCase("d_pw") == true)
                        {
                            // Power Number
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiMixingRealtime.getD_pw());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
    						sendCnt++;
                        }
                        else if(dto.getItm().equalsIgnoreCase("d_v") == true)
                        {
                            // 조체적
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getTag_sn());
                            controlMap.put("value", aiMixingRealtime.getD_v());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
    						sendCnt++;
                        }
//                        else if(dto.getItm().equalsIgnoreCase("d_anr") == true)
//                        {
//                            // 패들면적
//                            controlMap = new LinkedHashMap<>();
//                            controlMap.put("tag", dto.getTag_sn());
//                            controlMap.put("value", aiMixingRealtime.getD_pw());
//                            controlMap.put("time", strDate);
//                            strBody = objectMapper.writeValueAsString(controlMap);
//                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
//                        }
//                        else if(dto.getItm().equalsIgnoreCase("d_ki_dv") == true)
//                        {
//                            // 동점성 계수
//                            controlMap = new LinkedHashMap<>();
//                            controlMap.put("tag", dto.getTag_sn());
//                            controlMap.put("value", aiMixingRealtime.getD_pw());
//                            controlMap.put("time", strDate);
//                            strBody = objectMapper.writeValueAsString(controlMap);
//                            kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
//                        }
                    }

                    // AI 지별 응집기 속도 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiMixingRealtime.getAi_d_loc_fc_sp(), LinkedHashMap.class);
                    List<String> keyList = new ArrayList<>(mapTemp.keySet());
                    Object objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());    // location1...2

                    for(String key : keyList)
                    {
                        LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(key), LinkedHashMap.class);
                        List<String> locationKeyList = new ArrayList<>(locationMapTemp.keySet());   // step1...2

                        for(String locationKey : locationKeyList)
                        {
//                            LinkedHashMap<String, Object> stepMapTemp =
//                                    objectMapper.convertValue(locationMapTemp.get(locationKey), LinkedHashMap.class);
//                            List<String> stepKeyList = new ArrayList<>(stepMapTemp.keySet());   // 1...3

                            for(int i = nLocationMin; i <= nLocationMax; i++)
                            {
                                for(int j = 1; j <= 2; j++) // step count 1...2
                                {
                                    String strItemName = "ai_d_loc_fc" + i + "_" + j;
                                    if(key.equalsIgnoreCase("location"+i) == true &&
                                            locationKey.equalsIgnoreCase("step"+j) == true)
                                    {

                                        // tagManageList에서 strItemName을 검색
                                        TagManageDTO dto = tagManageList.stream()
                                                .filter(tagManage -> strItemName.equalsIgnoreCase(tagManage.getItm()))
                                                .findAny()
                                                .orElse(null);

                                        if(dto == null)
                                        {
                                            continue;
                                        }

                                        // Kafka에 전송할 값 정의
                                        controlMap = new LinkedHashMap<>();
                                        controlMap.put("tag", dto.getTag_sn());
                                        controlMap.put("value", locationMapTemp.get("step"+j));
                                        controlMap.put("time", strDate);
                                        strBody = objectMapper.writeValueAsString(controlMap);
                                        kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                						sendCnt++;
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Mixing Process");
                }
            }
        }
        return sendCnt;
    }
    /**
     * 소독 실시간 AI 예측값 전달
     * 
     * @param tagManageList
     */
    public int sendAiDisinfectionData() {
    	int sendCnt = 0;
        ///////////////////////////////////////////////
        // Disinfection Process
        ///////////////////////////////////////////////
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
        	
        	List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI, CommonValue.PROCESS_DISINFECTION, processStep);
        	LinkedHashMap<String, Object> controlMap;
        	ObjectMapper objectMapper = new ObjectMapper();
        	String strBody;
        	AiDisinfectionRealtimeDTO aiDisinfectionRealtime;
        	
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String strDate;
			
			
        	// 전차염 
        	aiDisinfectionRealtime = databaseService.getLatestAiDisinfectionRealtimeValue(processStep, 1);
        	log.debug("getLatestAiDisinfectionRealtimeValue, result:[{}]", aiDisinfectionRealtime != null ? 1 : 0);
    		if(aiDisinfectionRealtime != null) {
    			try {
    				strDate = simpleDateFormat.format(aiDisinfectionRealtime.getUpd_ti());
    				
    				//g_pre_operation_mode
    				AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE, processStep, CommonValue.DISINFECTION_PRE_STEP);
    				
					// 운전모드 알람 태그는 항상 전송
                    if(aiDisinfectionInit != null)
                    {
    					for(TagManageDTO dto : tagManageList)
    					{
    						if(dto.getItm().equalsIgnoreCase("g_pre_operation_mode_a") == true)
    						{
    							controlMap = new LinkedHashMap<>();
    							controlMap.put("tag", dto.getTag_sn());
    							controlMap.put("value", aiDisinfectionInit.getInit_val().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
    							controlMap.put("time", strDate);
    							strBody = objectMapper.writeValueAsString(controlMap);
    							kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
        						sendCnt++;
    							break;
    						}
    					}
                    }
	                    
					//분석데이터
    				for(TagManageDTO dto : tagManageList)
    				{
    					// 소독공정이 아니면 continue
                    	if (dto.getProc_cd().substring(0,1).equalsIgnoreCase(CommonValue.PROCESS_DISINFECTION) != true)
    					{
    						continue;
    					}
                    	
                    	if(dto.getItm().equalsIgnoreCase("ai_g_ti_pre") == true)
                    	{
                    		// ai update_time
                    		controlMap = new LinkedHashMap<>();
                    		controlMap.put("tag", dto.getTag_sn());
                    		controlMap.put("value", strDate);
                    		controlMap.put("time", strDate);
                    		strBody = objectMapper.writeValueAsString(controlMap);
                    		kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
    						sendCnt++;
                    	}
                    	else if(dto.getItm().equalsIgnoreCase("ai_g_chol_consume_pre") == true)
    					{
    						// 전염소 염소소모량 예측값
    						controlMap = new LinkedHashMap<>();
    						controlMap.put("tag", dto.getTag_sn());
    						controlMap.put("value", aiDisinfectionRealtime.getAi_g_consumption());
    						controlMap.put("time", strDate);
    						strBody = objectMapper.writeValueAsString(controlMap);
    						kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
    						sendCnt++;
    					}
                    	else if(dto.getItm().equalsIgnoreCase("ai_g_chol_rate_pre") == true)
    					{
    						// 주입률 예측
    						controlMap = new LinkedHashMap<>();
    						controlMap.put("tag", dto.getTag_sn());
    						controlMap.put("value", aiDisinfectionRealtime.getAi_g_chol_rate());
    						controlMap.put("time", strDate);
    						strBody = objectMapper.writeValueAsString(controlMap);
    						kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
    						sendCnt++;
    					}
    				}
    			}catch(JsonProcessingException e)
    			{
    				log.error("JsonProcessingException Occurred in Disinfection Process");
    			}
    		}
    		
        	//후차염
    		if(processStep == 1) {
            	aiDisinfectionRealtime = databaseService.getLatestAiDisinfectionRealtimeValue(processStep, 3);
            	if(aiDisinfectionRealtime != null) {
        			try {
	                	AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE, processStep, CommonValue.DISINFECTION_POST_STEP);
	    				strDate = simpleDateFormat.format(aiDisinfectionRealtime.getUpd_ti());
	    				
    					// 운전모드 알람 태그는 항상 전송
	                    if(aiDisinfectionInit != null)
	                    {
		    				for(TagManageDTO dto : tagManageList)
							{
								if(dto.getItm().equalsIgnoreCase("g_post_operation_mode_a") == true)
								{
									controlMap = new LinkedHashMap<>();
									controlMap.put("tag", dto.getTag_sn());
									controlMap.put("value", aiDisinfectionInit.getInit_val().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
									controlMap.put("time", strDate);
									strBody = objectMapper.writeValueAsString(controlMap);
									kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
		    						sendCnt++;
									break;
								}
							}
	                    }
	                    
    					//분석데이터
        				for(TagManageDTO dto : tagManageList)
        				{
        					// 소독공정이 아니면 continue
                        	if (dto.getProc_cd().substring(0,1).equalsIgnoreCase(CommonValue.PROCESS_DISINFECTION) != true)
        					{
        						continue;
        					}
                        	
                        	if(dto.getItm().equalsIgnoreCase("ai_g_ti_post") == true)
                        	{
                        		// ai update_time
                        		controlMap = new LinkedHashMap<>();
                        		controlMap.put("tag", dto.getTag_sn());
                        		controlMap.put("value", strDate);
                        		controlMap.put("time", strDate);
                        		strBody = objectMapper.writeValueAsString(controlMap);
                        		kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
        						sendCnt++;
                        	}
                        	else if(dto.getItm().equalsIgnoreCase("ai_g_chol_consume_post") == true)
        					{
        						// 염소 소모량 예측값
        						controlMap = new LinkedHashMap<>();
        						controlMap.put("tag", dto.getTag_sn());
        						controlMap.put("value", aiDisinfectionRealtime.getAi_g_consumption());
        						controlMap.put("time", strDate);
        						strBody = objectMapper.writeValueAsString(controlMap);
        						kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
        						sendCnt++;
        					}
                        	else if(dto.getItm().equalsIgnoreCase("ai_g_chol_rate_post") == true)
        					{
        						// 주입률 예측
        						controlMap = new LinkedHashMap<>();
        						controlMap.put("tag", dto.getTag_sn());
        						controlMap.put("value", aiDisinfectionRealtime.getAi_g_chol_rate());
        						controlMap.put("time", strDate);
        						strBody = objectMapper.writeValueAsString(controlMap);
        						kafkaProducer.sendMessageToLocal(CommonValue.KAFKA_TOPIC_RESULT, strBody);
        						sendCnt++;
        					}
        				}
        				
        			}catch(JsonProcessingException e){
        				log.error("JsonProcessingException Occurred in Disinfection Process");
        			}
            	}
    		}
        }
        return sendCnt;
    }
    
    
    // 알고리즘 상태 확인
    // @RequestMapping(value = "/internal/algorithm", method = RequestMethod.GET)
    // public void getAlgorithm(@RequestHeader("X-ACCESS-TOKEN") String token)
    // {
    //     // Token Check
    //     if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
    //     {
    //         log.error("getDatabase, Invalid X-ACCESS-TOKEN:[{}]", token);
    //         return;
    //     }

    //     // If first call getAlgorithm() initialization algorithmDate before one hour
    //     if(algorithmDate == null)
    //     {
    //         algorithmDate = new Date();
    //         algorithmDate.setTime(algorithmDate.getTime() - CommonValue.ONE_HOUR);
    //     }

    //     log.debug("[internal] getAlgorithm");

    //     // Check one minute after previous transfer
    //     Date currentDate = new Date();
    //     if(currentDate.getTime() - algorithmDate.getTime() > CommonValue.ONE_MINUTE)
    //     {
    //         algorithmDate = new Date();

    //         // get algorithm health check URL
    //         List<String> strUri = propertiesAlgorithmCheck.getAlgorithmHealth();

    //         StringBuffer stringBuffer = new StringBuffer();
    //         ObjectMapper objectMapper = new ObjectMapper();
    //         BufferedReader bufferedReader = null;
    //         InputStreamReader inputStreamReader = null;
    //         AlgorithmHealthStatus algorithmHealthStatus = new AlgorithmHealthStatus();

    //         try
    //         {
    //             for(String uri : strUri)
    //             {
    //                 HttpGet httpGet = new HttpGet(uri);
    //                 httpGet.setConfig(requestConfig);

    //                 HttpResponse response = httpSend.send(httpGet);
    //                 if(response == null)
    //                 {
    //                     continue;
    //                 }

    //                 if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    //                 {
    //                     inputStreamReader = new InputStreamReader(response.getEntity().getContent());
    //                     bufferedReader = new BufferedReader(inputStreamReader);

    //                     String strLine;
    //                     while((strLine = bufferedReader.readLine()) != null)
    //                     {
    //                         stringBuffer.append(strLine);
    //                     }

    //                     // Algorithm Healthcheck Response에 대한 Parsing
    //                     ArrayList<SupervisorStateInfoDTO> algorithmList =
    //                             objectMapper.readValue(stringBuffer.toString(), new TypeReference<ArrayList<SupervisorStateInfoDTO>>(){});
    //                     for(SupervisorStateInfoDTO processState : algorithmList)
    //                     {
    //                         if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_RECEIVING_NAME) == true)
    //                         {
    //                             // 착수 공정 알고리즘 프로세스 상태 저장
    //                             if(algorithmHealthStatus.getReceiving() == CommonValue.PROCESS_STATE_RUNNING)
    //                             {
    //                                 continue;
    //                             }
    //                             algorithmHealthStatus.setReceiving(processState.getState());
    //                         }
    //                         else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_COAGULANT_NAME) == true)
    //                         {
    //                             // 약품 공장 알고리즘 프로세스 상태 저장
    //                             if(algorithmHealthStatus.getCoagulant() == CommonValue.PROCESS_STATE_RUNNING)
    //                             {
    //                                 continue;
    //                             }
    //                             algorithmHealthStatus.setCoagulant(processState.getState());
    //                         }
    //                         else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_MIXING_NAME) == true)
    //                         {
    //                             // 혼화응집 공정 알고리즘 프로세스 상태 저장
    //                             if(algorithmHealthStatus.getMixing() == CommonValue.PROCESS_STATE_RUNNING)
    //                             {
    //                                 continue;
    //                             }
    //                             algorithmHealthStatus.setMixing(processState.getState());
    //                         }
    //                         else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_SEDIMENTATION_NAME) == true)
    //                         {
    //                             // 침전 공정 알고리즘 프로세스 상태 저장
    //                             if(algorithmHealthStatus.getSedimentation() == CommonValue.PROCESS_STATE_RUNNING)
    //                             {
    //                                 continue;
    //                             }
    //                             algorithmHealthStatus.setSedimentation(processState.getState());
    //                         }
    //                         else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_FILTER_NAME) == true)
    //                         {
    //                             // 여과 공정 알고리즘 프로세스 상태 저장
    //                             if(algorithmHealthStatus.getFilter() == CommonValue.PROCESS_STATE_RUNNING)
    //                             {
    //                                 continue;
    //                             }
    //                             algorithmHealthStatus.setFilter(processState.getState());
    //                         }
    //                         else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_GAC_NAME) == true)
    //                         {
    //                             // GAC 여과 공정 알고리즘 프로세스 상태 저장
    //                             if(algorithmHealthStatus.getGac() == CommonValue.PROCESS_STATE_RUNNING)
    //                             {
    //                                 continue;
    //                             }
    //                             algorithmHealthStatus.setGac(processState.getState());
    //                         }
    //                         else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_DISINFECTION_NAME) == true)
    //                         {
    //                             // 소독 공정 알고리즘 프로세스 상태 저장
    //                             if(algorithmHealthStatus.getDisinfection() == CommonValue.PROCESS_STATE_RUNNING)
    //                             {
    //                                 continue;
    //                             }
    //                             algorithmHealthStatus.setDisinfection(processState.getState());
    //                         }
    //                         else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_OZONE_NAME) == true)
    //                         {
    //                             // 오존 공정 알고리즘 프로세스 상태 저장
    //                             if(algorithmHealthStatus.getOzone() == CommonValue.PROCESS_STATE_RUNNING)
    //                             {
    //                                 continue;
    //                             }
    //                             algorithmHealthStatus.setOzone(processState.getState());
    //                         }
    //                     }
    //                 }
    //             }

    //             // Receiving Algorithm Health Check & Send Alarm
    //             if(algorithmHealthStatus.getReceiving() != CommonValue.PROCESS_STATE_RUNNING)
    //             {
    //                 AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
    //                 aiProcessAlarm.setAlm_ti(new Date());
    //                 aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
    //                 aiProcessAlarm.setAlm_id(CommonValue.ALARM_RECEIVING_AI_MODULE_ERROR);
    //                 log.debug("insert receiving algorithm module error alarm:[{}]",
    //                         databaseService.addAiReceivingAlarm(aiProcessAlarm));
    //             }

    //             // Coagulant Algorithm Health Check & Send Alarm
    //             if(algorithmHealthStatus.getCoagulant() != CommonValue.PROCESS_STATE_RUNNING)
    //             {
    //                 AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
    //                 aiProcessAlarm.setAlm_ti(new Date());
    //                 aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
    //                 aiProcessAlarm.setAlm_id(CommonValue.ALARM_COAGULANT_AI_MODULE_ERROR);
    //                 log.debug("insert coagulant algorithm module error alarm:[{}]",
    //                         databaseService.addAiCoagulantAlarm(aiProcessAlarm));
    //             }

    //             // Mixing Algorithm Health Check & Send Alarm
    //             if(algorithmHealthStatus.getMixing() != CommonValue.PROCESS_STATE_RUNNING)
    //             {
    //                 AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
    //                 aiProcessAlarm.setAlm_ti(new Date());
    //                 aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
    //                 aiProcessAlarm.setAlm_id(CommonValue.ALARM_MIXING_AI_MODULE_ERROR);
    //                 log.debug("insert mixing algorithm module error alarm:[{}]",
    //                         databaseService.addAiMixingAlarm(aiProcessAlarm));
    //             }

    //             // Sedimentation Algorithm Health Check & Send Alarm
    //             if(algorithmHealthStatus.getSedimentation() != CommonValue.PROCESS_STATE_RUNNING)
    //             {
    //                 AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
    //                 aiProcessAlarm.setAlm_ti(new Date());
    //                 aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
    //                 aiProcessAlarm.setAlm_id(CommonValue.ALARM_SEDIMENTATION_AI_MODULE_ERROR);
    //                 log.debug("insert sedimentation algorithm module error alarm:[{}]",
    //                         databaseService.addAiSedimentationAlarm(aiProcessAlarm));
    //             }

    //             // Filter Algorithm Health Check & Send Alarm
    //             if(algorithmHealthStatus.getFilter() != CommonValue.PROCESS_STATE_RUNNING)
    //             {
    //                 AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
    //                 aiProcessAlarm.setAlm_ti(new Date());
    //                 aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
    //                 aiProcessAlarm.setAlm_id(CommonValue.ALARM_FILTER_AI_MODULE_ERROR);
    //                 log.debug("insert filter algorithm module error alarm:[{}]",
    //                         databaseService.addAiFilterAlarm(aiProcessAlarm));
    //             }

    //             // GAC Algorithm Health Check & Send Alarm
    //             if(algorithmHealthStatus.getGac() != CommonValue.PROCESS_STATE_RUNNING)
    //             {
    //                 AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
    //                 aiProcessAlarm.setAlm_ti(new Date());
    //                 aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
    //                 aiProcessAlarm.setAlm_id(CommonValue.ALARM_GAC_AI_MODULE_ERROR);
    //                 log.debug("insert gac algorithm module error alarm:[{]]",
    //                         databaseService.addAiGacAlarm(aiProcessAlarm));
    //             }

    //             // Disinfection Algorithm Health Check & Send Alarm
    //             if(algorithmHealthStatus.getDisinfection() != CommonValue.PROCESS_STATE_RUNNING)
    //             {
    //                 AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
    //                 aiProcessAlarm.setAlm_ti(new Date());
    //                 aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
    //                 aiProcessAlarm.setAlm_id(CommonValue.ALARM_DISINFECTION_AI_MODULE_ERROR);
    //                 log.debug("insert disinfection algorithm module error alarm:[{}]",
    //                         databaseService.addAiDisinfectionAlarm(aiProcessAlarm));
    //             }

    //             // Ozone Algorithm Health Check & Send Alarm
    //             if(algorithmHealthStatus.getOzone() != CommonValue.PROCESS_STATE_RUNNING)
    //             {
    //                 AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
    //                 aiProcessAlarm.setAlm_ti(new Date());
    //                 aiProcessAlarm.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
    //                 aiProcessAlarm.setAlm_id(CommonValue.ALARM_OZONE_AI_MODULE_ERROR);
    //                 log.debug("insert ozone algorithm module error alarm:[{}]",
    //                         databaseService.addAiOzoneAlarm(aiProcessAlarm));
    //             }
    //         }
    //         catch(IOException e)
    //         {
    //             log.error("Invalid Body or BufferedReader...");
    //         }
    //         finally
    //         {
    //             if(inputStreamReader != null)
    //             {
    //                 try
    //                 {
    //                     inputStreamReader.close();
    //                 }
    //                 catch(IOException e)
    //                 {
    //                     log.error("inputStreamReader Close Exception occurred");
    //                 }
    //             }
    //             if(bufferedReader != null)
    //             {
    //                 try
    //                 {
    //                     bufferedReader.close();
    //                 }
    //                 catch(IOException e)
    //                 {
    //                     log.error("bufferedReader Close Exception occurred");
    //                 }
    //             }
    //         }
    //     }
    // }

    /**
     * 데이터베이스 정리
     * 
     * @param token 토큰
     */
    @RequestMapping(value = "/internal/manageRtTable", method = RequestMethod.GET)
    public void manageRtTable(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
		// Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("[Collector]getDatabase, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }
		
		log.debug("[Collector]Check Database Partition...Thread[{}]", Thread.currentThread().getName());

        // Set default calendar(tomorrow)
        Calendar calendarAdd = Calendar.getInstance();
        calendarAdd.set(Calendar.MINUTE, 0);
        calendarAdd.set(Calendar.SECOND, 0);
        calendarAdd.set(Calendar.HOUR_OF_DAY, 0);
        calendarAdd.add(Calendar.HOUR_OF_DAY, 24);

        // Set partition name
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        List<String> strAddPartitionNameList = new ArrayList<>();
        strAddPartitionNameList.add("p_" + partitionNameFormat.format(calendarAdd.getTime()));

        List<String> procCdList = databaseService.selectProcCd();
        for(String procCd : procCdList)
        {
            try{
                databaseService.addProcessRealtimePartition(procCd, strAddPartitionNameList);
                log.debug("[Collector]Success Add Table[TB_{}_RT]...Partition Name:{}", procCd, strAddPartitionNameList.toString());
            } catch (DataAccessException e) {
                log.error("[Collector]Failed Add Table[TB_{}_RT]...Partition Name:{}", procCd, strAddPartitionNameList.toString());
                log.error(e.toString());
            }
        }

        // Delete Realtime table partition(7 days)
        Calendar calendarDel = Calendar.getInstance();
        calendarDel.set(Calendar.MINUTE, 0);
        calendarDel.set(Calendar.SECOND, 0);
        calendarDel.set(Calendar.HOUR_OF_DAY, 0);
        calendarDel.add(Calendar.DAY_OF_MONTH, -7);
        String strDelStartPartitionName = "p_" + partitionNameFormat.format(calendarDel.getTime());

        for(String procCd : procCdList)
        {
            List<String> dropPartitionList = new ArrayList<>();
            try{
				// Get drop partition list
				dropPartitionList = databaseService.getDropPartitionList(procCd, strDelStartPartitionName);
				databaseService.delProcessRealtimePartition(procCd, dropPartitionList);
                log.debug("[Collector]Success Del Table[TB_{}_RT]... Partition Name:[{}]", procCd, dropPartitionList.toString());
            } catch (DataAccessException e) {
                log.debug("[Collector]Failed Del Table[TB_{}_RT]... Partition Name:[{}]", procCd, dropPartitionList.toString());
                log.error(e.toString());
            }
        }
	}
    
    /**
     * 공정별 알람정보 목록조회
     * 
     * @param processType 공정타입
     * @param alarmTime   현재시간
     * @param kafkaFlag   카프카플래그
     * @param processStep 공정단계
     * @return List<AiProcessAlarmDTO> 공정 알람정보 목록
     */
    private List<AiProcessAlarmDTO> getAllAlarm(String processType, Date alarmTime, int kafkaFlag, int processStep) {    	
        List<AiProcessAlarmDTO> aiAlarmList = new ArrayList<AiProcessAlarmDTO>();
        switch (processType) {
            case CommonValue.PROCESS_COAGULANT:           // 약품
                // 2. Coagulant Process get ai_coagulant_alarm
                aiAlarmList = databaseService.getAllAiCoagulantAlarm(alarmTime, kafkaFlag, processStep);
                break;
            case CommonValue.PROCESS_MIXING:              // 혼화응집
                // 2. Mixing Process get ai_mixing_alarm
                aiAlarmList = databaseService.getAllAiMixingAlarm(alarmTime, kafkaFlag, processStep);
                break;
            case CommonValue.PROCESS_DISINFECTION_PRE:       // 전차염
                // 2. Disinfection Process get ai_disinfection_alarm
                aiAlarmList = databaseService.getAllAiDisinfectionAlarm(alarmTime, kafkaFlag, processStep, CommonValue.DISINFECTION_PRE_STEP);
                break;
            case CommonValue.PROCESS_DISINFECTION_POST:  // 후차염
                aiAlarmList = databaseService.getAllAiDisinfectionAlarm(alarmTime, kafkaFlag, processStep, CommonValue.DISINFECTION_POST_STEP);
                break;
        }
        modifyAlarmKafkaFlag(aiAlarmList, processType, processStep);
        return aiAlarmList;
    }
    
    /**
     * 카프카 플래그 수정
     * 
     * @param alarmList 알람정보 목록
     * @param process   공정
     */
    private void modifyAlarmKafkaFlag(List<AiProcessAlarmDTO> alarmList, String process, int processStep) {
    	if(alarmList.size() > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strBody;
            ObjectMapper objectMapper = new ObjectMapper();
            
            for(AiProcessAlarmDTO dto : alarmList) {
            	AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(dto.getAlm_id());
            	if(alarmInfo != null) {
                    if(alarmInfo.getAlm_ty() == 3) {
                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                        popupMap.put("alarm_id", alarmInfo.getAlm_id());
                        popupMap.put("message", alarmInfo.getDp_nm());
                        popupMap.put("url", alarmInfo.getUrl());
                        popupMap.put("time", simpleDateFormat.format(dto.getAlm_ti()));

                        // 3. Send Kafka ai_popup
                        try {
                            strBody = objectMapper.writeValueAsString(popupMap);
                            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);
                        } catch(JsonProcessingException e) {
                            log.error("JsonProcessingException Occurred in PROCESS_CODE :" + process + " Alarm Process");
                        }
                    }
                    
                    //4.update kafka_flag = 1
                    AiProcessAlarmDTO updateDto = dto;
                    updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                    
                    if(CommonValue.PROCESS_COAGULANT.equals(process)) {      // 약품
                        databaseService.modAiCoagulantAlarmKafkaFlag(updateDto, processStep);
                    } else if(CommonValue.PROCESS_MIXING.equals(process)) {         // 혼화응집
                        databaseService.modAiMixingAlarmKafkaFlag(updateDto, processStep);
                    } else if(CommonValue.PROCESS_DISINFECTION_PRE.equals(process)) {   // 소독 전차염
                        databaseService.modAiDisinfectionAlarmKafkaFlag(updateDto, processStep, CommonValue.DISINFECTION_PRE_STEP);
                    } else if(CommonValue.PROCESS_DISINFECTION_POST.equals(process)) {   // 소독 후차염
                        databaseService.modAiDisinfectionAlarmKafkaFlag(updateDto, processStep, CommonValue.DISINFECTION_POST_STEP);
                    }
                    
            	}
            }
            
    	}
    }
    
    
    /**
     * 소독 CTR조회
     */
    public void getDisinfectionControl() {
        for(int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // 1. get operation mode - pre disinfection
            AiProcessInitDTO aiPreDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE, processStep, CommonValue.DISINFECTION_PRE_STEP); 
            log.debug("getAiDisinfectionInit, result:[{}]", aiPreDisinfectionInit != null ? 1 : 0);

            if(aiPreDisinfectionInit != null) {
                int nOperationMode = aiPreDisinfectionInit.getInit_val().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL) {
                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRnti(runTime);
                    queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    queryDto.setProcessStep(processStep);

                    List<AiProcessControlDTO> aiDisinfectionControlList = databaseService.getListAiPreDisinfectionControl(queryDto);
                    if(aiDisinfectionControlList.size() > 0) {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date rnti = new Date();
                        boolean alarmExceededFlag = false; //임계치 알람 해당 여부
                        try {
                            for(AiProcessControlDTO dto : aiDisinfectionControlList) {
                                InterfaceAlarmControlHistoryDTO paramAlarm = new InterfaceAlarmControlHistoryDTO();
                                paramAlarm.setAlm_ntf_ti(dto.getRnti());
                                paramAlarm.setProcess(CommonValue.PROCESS_DISINFECTION);
                                paramAlarm.setProcessStep(String.valueOf(processStep));
                                paramAlarm.setDisinfectionIndex(CommonValue.DISINFECTION_PRE_STEP);
                                paramAlarm.setAlmTy(CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED);
                                paramAlarm.setUpdTi(dto.getUpd_ti());
                                paramAlarm.setTagSn(dto.getTag_sn());

                                InterfaceAlarmControlHistoryDTO alarmExceededInfo = databaseService.getAlarmExceeded(paramAlarm);
                                if(alarmExceededInfo != null) { //임계치 제어에 해당
                                	alarmExceededFlag = true;
                                    if(bFirst == true) {
                                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                        rnti = dto.getRnti();
                                        popupMap.put("alarm_id", alarmExceededInfo.getAlm_id());
                                        popupMap.put("message", alarmExceededInfo.getDp_nm());
                                        popupMap.put("url", alarmExceededInfo.getUrl());
                                        popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(popupMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                        bFirst = false;
                                    }
                                    if(!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                        updateDto.setProcessStep(processStep);
                                        updateDto.setDisinfectionIndex(CommonValue.DISINFECTION_PRE_STEP);
                                        databaseService.modAiPreDisinfectionControlKafkaFlag(updateDto);
                                    }

                                } else {
                                    if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO) {
                                        // 3. if operation_mode==1 (semi_auto)
                                        // 3-1. send control value to kafka ai_popup
                                        AlarmInfoDTO alarmInfo =
                                                alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_DISINFECTION_AI_PRE_CONTROL, processStep);
                                        if(alarmInfo != null) {
                                            // KAFKA topic is called only once.
                                            if(bFirst == true) {
                                                LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                                rnti = dto.getRnti();
                                                popupMap.put("alarm_id", alarmInfo.getAlm_id());
                                                popupMap.put("message", alarmInfo.getDp_nm());
                                                popupMap.put("url", alarmInfo.getUrl());
                                                popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                                strBody = objectMapper.writeValueAsString(popupMap);
                                                kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);
    
                                                bFirst = false;
                                            }
                                            if(!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                            // 3-2. update kafka_flag=1 (kafka_popup)
                                                AiProcessControlDTO updateDto = dto;
                                                updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                                updateDto.setProcessStep(processStep);
                                                updateDto.setDisinfectionIndex(CommonValue.DISINFECTION_PRE_STEP);
                                                databaseService.modAiPreDisinfectionControlKafkaFlag(updateDto);
                                            }
                                        } else {
                                            log.error("Does not exist alarmInfo:[{]]", CommonValue.ALARM_CODE_DISINFECTION_AI_PRE_CONTROL);
                                        }
                                    } else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                                        // 4. if operation_mode==2 (full_auto)
    
                                        // 4-1. send control value to kafka ai_control
                                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                        controlMap.put("tag", dto.getTag_sn());
                                        controlMap.put("value", dto.getTag_val().equalsIgnoreCase(CommonValue.CONTROL_TRUE) ? true : Float.parseFloat(dto.getTag_val()));
                                        controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(controlMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
    
                                        // 4-2. update kafka_flag=3 (kafka_send)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_SEND);
                                        updateDto.setProcessStep(processStep);
                                        updateDto.setDisinfectionIndex(CommonValue.DISINFECTION_PRE_STEP);
                                        databaseService.modAiPreDisinfectionControlKafkaFlag(updateDto);
                                    }
                                }
                            } //for문 끝
                            //자동모드이면서 && 임계치 알람이 아닌 경우( = AI제어 알람인 경우) -- 이력 업데이트.
                            if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO && !alarmExceededFlag) {
                                List<AlmCtrHisDTO> autoAlmCtrHisList = new ArrayList<>(); // 자동 제어시의 히스토리 리스트
                                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_DISINFECTION_AI_PRE_CONTROL, processStep);
                                Date currentDate = new Date();
                                if(alarmInfo != null) {
	                                // insert alarm_notify & get almSeq
	                                int almSeq = alarmService.alarmNotify(
	                                		alarmInfo.getAlm_id(),
	                                		alarmInfo.getDp_nm(),
	                                        alarmInfo.getUrl(),
	                                        simpleDateFormat.format(currentDate)
	                                );
	                                if(almSeq !=0) {
	                                	//ctrHisList insert 
	                                	for(AiProcessControlDTO ctr : aiDisinfectionControlList) {
	                                		AlmCtrHisDTO his = new AlmCtrHisDTO();
	                                		his.setAlm_id(alarmInfo.getAlm_id());
	                                		his.setAlm_seq(almSeq); // alarmNotify에 넣을때 사용한 seq를 반환받아서 넣어야함.
	                                		his.setAlm_ty(alarmInfo.getAlm_ty());
	                                		his.setCtr_ti(currentDate);
	                                		his.setCtr_yn("A");
	                                		his.setTag_sn(ctr.getTag_sn());
	                                		his.setUpd_ti(ctr.getUpd_ti());
	                                		autoAlmCtrHisList.add(his);
	                                	}
	                                	databaseService.addAlmCtrHis(autoAlmCtrHisList);                                    	
	                                }
                                }else {
                                	log.error("Does not exist alarmInfo:[{]]", CommonValue.ALARM_CODE_DISINFECTION_AI_PRE_CONTROL);
                                }
                            }
                        } catch(JsonProcessingException e) {
                            log.error("JsonProcessingException Occurred in Pre Disinfection Control Process");
                        } catch(NumberFormatException e) {
                            log.error("NumberException Occurred in Pre Disinfection Control Process");
                        }
                    }
                }
            }
        }

        // 1. get operation mode - post disinfection
        AiProcessInitDTO aiPostDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE, 1, CommonValue.DISINFECTION_POST_STEP);
        log.debug("getAiDisinfectionInit, result:[{}]", aiPostDisinfectionInit != null ? 1 : 0);

        if(aiPostDisinfectionInit != null) {
            int nOperationMode = aiPostDisinfectionInit.getInit_val().intValue();

            // 수동 모드일 경우 전송하지 않음
            if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL) {
                // 2. get latest(10minutes) control value(kafka_flag = 0)
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, -10);
                Date runTime = calendar.getTime();

                AiProcessControlDTO queryDto = new AiProcessControlDTO();
                queryDto.setRnti(runTime);
                queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                queryDto.setProcessStep(1);

                List<AiProcessControlDTO> aiDisinfectionControlList = databaseService.getListAiPostDisinfectionControl(queryDto);
                if(aiDisinfectionControlList.size() > 0) {
                    String strBody;
                    boolean bFirst = true;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date rnti = new Date();
                    boolean alarmExceededFlag = false; //임계치 알람 해당 여부
                    try {
                        for(AiProcessControlDTO dto : aiDisinfectionControlList) {
                            InterfaceAlarmControlHistoryDTO paramAlarm = new InterfaceAlarmControlHistoryDTO();
                            paramAlarm.setAlm_ntf_ti(dto.getRnti());
                            paramAlarm.setProcess(CommonValue.PROCESS_DISINFECTION);
                            paramAlarm.setProcessStep("1");
                            paramAlarm.setDisinfectionIndex(CommonValue.DISINFECTION_POST_STEP);
                            paramAlarm.setAlmTy(CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED);
                            paramAlarm.setUpdTi(dto.getUpd_ti());
                            paramAlarm.setTagSn(dto.getTag_sn());

                            InterfaceAlarmControlHistoryDTO alarmExceededInfo = databaseService.getAlarmExceeded(paramAlarm);
                            if(alarmExceededInfo != null) { //임계치 제어에 해당
                            	alarmExceededFlag = true;
                                if(bFirst == true) {
                                    LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                    rnti = dto.getRnti();
                                    popupMap.put("alarm_id", alarmExceededInfo.getAlm_id());
                                    popupMap.put("message", alarmExceededInfo.getDp_nm());
                                    popupMap.put("url", alarmExceededInfo.getUrl());
                                    popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                    strBody = objectMapper.writeValueAsString(popupMap);
                                    kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                    bFirst = false;
                                }
                                if(!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                    // 3-2. update kafka_flag=1 (kafka_popup)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                    updateDto.setProcessStep(1);
                                    updateDto.setDisinfectionIndex(CommonValue.DISINFECTION_POST_STEP);
                                    databaseService.modAiPostDisinfectionControlKafkaFlag(updateDto);
                                }

                            } else {
                                if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO) {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo =
                                            alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_DISINFECTION_AI_POST_CONTROL, 1);
                                    if(alarmInfo != null) {
                                        // KAFKA topic is called only once.
                                        if(bFirst == true) {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            rnti = dto.getRnti();
                                            popupMap.put("alarm_id", alarmInfo.getAlm_id());
                                            popupMap.put("message", alarmInfo.getDp_nm());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);
    
                                            bFirst = false;
                                        }
                                        if(!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                            // 3-2. update kafka_flag=1 (kafka_popup)
                                            AiProcessControlDTO updateDto = dto;
                                            updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                            updateDto.setProcessStep(1);
                                            updateDto.setDisinfectionIndex(CommonValue.DISINFECTION_POST_STEP);
                                            databaseService.modAiPostDisinfectionControlKafkaFlag(updateDto);
                                        }
                                    } else {
                                        log.error("Does not exist alarmInfo:[{]]", CommonValue.ALARM_CODE_DISINFECTION_AI_POST_CONTROL);
                                    }
                                } else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                                    // 4. if operation_mode==2 (full_auto)
    
                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getTag_sn());
                                    controlMap.put("value", dto.getTag_val().equalsIgnoreCase(CommonValue.CONTROL_TRUE) ? true : Float.parseFloat(dto.getTag_val()));
                                    controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
    
                                    // 4-2. update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_SEND);
                                    updateDto.setProcessStep(1);
                                    updateDto.setDisinfectionIndex(CommonValue.DISINFECTION_POST_STEP);
                                    databaseService.modAiPostDisinfectionControlKafkaFlag(updateDto);
                                }
                            }
                        } //for문 끝
                        //자동모드이면서 && 임계치 알람이 아닌 경우( = AI제어 알람인 경우) -- 이력 업데이트.
                        if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO && !alarmExceededFlag) {
                            List<AlmCtrHisDTO> autoAlmCtrHisList = new ArrayList<>(); // 자동 제어시의 히스토리 리스트
                            AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_DISINFECTION_AI_POST_CONTROL, 1);
                            Date currentDate = new Date();
                            if(alarmInfo != null) {
	                            // insert alarm_notify & get almSeq
	                            int almSeq = alarmService.alarmNotify(
	                            		alarmInfo.getAlm_id(),
	                            		alarmInfo.getDp_nm(),
	                                    alarmInfo.getUrl(),
	                                    simpleDateFormat.format(currentDate)
	                            );
	                            if(almSeq !=0) {
	                            	//ctrHisList insert 
	                            	for(AiProcessControlDTO ctr : aiDisinfectionControlList) {
	                            		AlmCtrHisDTO his = new AlmCtrHisDTO();
	                            		his.setAlm_id(alarmInfo.getAlm_id());
	                            		his.setAlm_seq(almSeq); // alarmNotify에 넣을때 사용한 seq를 반환받아서 넣어야함.
	                            		his.setAlm_ty(alarmInfo.getAlm_ty());
	                            		his.setCtr_ti(currentDate);
	                            		his.setCtr_yn("A");
	                            		his.setTag_sn(ctr.getTag_sn());
	                            		his.setUpd_ti(ctr.getUpd_ti());
	                            		autoAlmCtrHisList.add(his);
	                            	}
	                            	databaseService.addAlmCtrHis(autoAlmCtrHisList);                                    	
	                            }
                            }else {
                            	log.error("Does not exist alarmInfo:[{]]", CommonValue.ALARM_CODE_DISINFECTION_AI_POST_CONTROL);
                            }
                        }
                    } catch(JsonProcessingException e) {
                        log.error("JsonProcessingException Occurred in Post Disinfection Control Process");
                    } catch(NumberFormatException e) {
                        log.error("NumberException Occurred in Post Disinfection Control Process");
                    }
                }
            }
        }
    }


    
    /**
     * 혼화응집 CTR조회
     */
    public void getMixingControl() {
        for(int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // 1. get operation mode
            AiProcessInitDTO aiMixingInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE, processStep);
            log.debug("getAiMixingInit, result:[{}]", aiMixingInit != null ? 1 : 0);

            if(aiMixingInit != null) {
                int nOperationMode = aiMixingInit.getInit_val().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRnti(runTime);
                    queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    queryDto.setProcessStep(processStep);

                    // 2. get latest(10 minutes) control value(kafka_flag = 0)
                    List<AiProcessControlDTO> aiMixingControlList = databaseService.getListAiMixingControl(queryDto);
                    if(aiMixingControlList.size() > 0) {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date rnti = new Date();
                        boolean alarmExceededFlag = false; //임계치 알람 해당 여부
                        try {
                            for(AiProcessControlDTO dto : aiMixingControlList) {
                                InterfaceAlarmControlHistoryDTO paramAlarm = new InterfaceAlarmControlHistoryDTO();
                                paramAlarm.setAlm_ntf_ti(dto.getRnti());
                                paramAlarm.setProcess(CommonValue.PROCESS_MIXING);
                                paramAlarm.setProcessStep(String.valueOf(processStep));
                                paramAlarm.setAlmTy(CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED);
                                paramAlarm.setUpdTi(dto.getUpd_ti());
                                paramAlarm.setTagSn(dto.getTag_sn());
                                
                                InterfaceAlarmControlHistoryDTO alarmExceededInfo = databaseService.getAlarmExceeded(paramAlarm);
                                if(alarmExceededInfo != null) { //임계치 제어에 해당
                                	alarmExceededFlag = true;
                                    if(bFirst == true) {
                                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                        rnti = dto.getRnti();
                                        popupMap.put("alarm_id", alarmExceededInfo.getAlm_id());
                                        popupMap.put("message", alarmExceededInfo.getDp_nm());
                                        popupMap.put("url", alarmExceededInfo.getUrl());
                                        popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(popupMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                        bFirst = false;
                                    }
                                    if(!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                        updateDto.setProcessStep(processStep);
                                        databaseService.modAiMixingControlKafkaFlag(updateDto);
                                    }
                                } else {
                                    if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO) {
                                        // 3. if operation_mode==1 (semi_auto)
                                        // 3-1. send control value to kafka ai_popup
                                        AlarmInfoDTO alarmInfo =
                                                alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_MIXING_AI_CONTROL, processStep);
                                        if(alarmInfo != null) {
                                            // KAFKA topic is called only once.
                                            if(bFirst == true) {
                                                LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                                rnti = dto.getRnti();
                                                popupMap.put("alarm_id", alarmInfo.getAlm_id());
                                                popupMap.put("message", alarmInfo.getDp_nm());
                                                popupMap.put("url", alarmInfo.getUrl());
                                                popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                                strBody = objectMapper.writeValueAsString(popupMap);
                                                kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);
    
                                                bFirst = false;
                                            }
                                            if(!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                                // 3-2. update kafka_flag=1 (kafka_popup)
                                                AiProcessControlDTO updateDto = dto;
                                                updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                                updateDto.setProcessStep(processStep);
                                                databaseService.modAiMixingControlKafkaFlag(updateDto);
                                            }
                                        } else {
                                            log.error("Does not exist alarmInfo:[{]]", CommonValue.ALARM_CODE_MIXING_AI_CONTROL);
                                        }
                                    }  else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                                        // 4. if operation_mode==2 (full_auto)
    
                                        // 4-1. send control value to kafka ai_control
                                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                        controlMap.put("tag", dto.getTag_sn());
                                        controlMap.put("value", dto.getTag_val().equalsIgnoreCase(CommonValue.CONTROL_TRUE) ? true : Float.parseFloat(dto.getTag_val()));
                                        controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(controlMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
    
                                        // 4-2. update kafka_flag=3 (kafka_send)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_SEND);
                                        updateDto.setProcessStep(processStep);
                                        databaseService.modAiMixingControlKafkaFlag(updateDto);
                                    }
                                }
                            } // for문 끝
                            //자동모드이면서 && 임계치 알람이 아닌 경우( = AI제어 알람인 경우) -- 이력 업데이트.
                            if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO && !alarmExceededFlag) {
                                List<AlmCtrHisDTO> autoAlmCtrHisList = new ArrayList<>(); // 자동 제어시의 히스토리 리스트
                                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_MIXING_AI_CONTROL, processStep);
                                Date currentDate = new Date();
                                if(alarmInfo != null) {
	                                // insert alarm_notify & get almSeq
	                                int almSeq = alarmService.alarmNotify(
	                                		alarmInfo.getAlm_id(),
	                                		alarmInfo.getDp_nm(),
	                                        alarmInfo.getUrl(),
	                                        simpleDateFormat.format(currentDate)
	                                );
	                                if(almSeq !=0) {
	                                	//ctrHisList insert 
	                                	for(AiProcessControlDTO ctr : aiMixingControlList) {
	                                		AlmCtrHisDTO his = new AlmCtrHisDTO();
	                                		his.setAlm_id(alarmInfo.getAlm_id());
	                                		his.setAlm_seq(almSeq); // alarmNotify에 넣을때 사용한 seq를 반환받아서 넣어야함.
	                                		his.setAlm_ty(alarmInfo.getAlm_ty());
	                                		his.setCtr_ti(currentDate);
	                                		his.setCtr_yn("A");
	                                		his.setTag_sn(ctr.getTag_sn());
	                                		his.setUpd_ti(ctr.getUpd_ti());
	                                		autoAlmCtrHisList.add(his);
	                                	}
	                                	databaseService.addAlmCtrHis(autoAlmCtrHisList);                                    	
	                                }
                                }else {
                                	log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_MIXING_AI_CONTROL);	
                                }
                            }
                        } catch(JsonProcessingException e) {
                            log.error("JsonProcessingException Occurred in Mixing Control Process");
                        } catch(NumberFormatException e) {
                            log.error("NumberException Occurred in Mixing Control Process");
                        }
                    }
                }
            }
        }
    }


    
    public void getCoagulantControl() {
        for(int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            // 1. get operation mode
            AiProcessInitDTO aiCoagulantInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE, processStep);
            log.debug("getAiCoagulantInit, result:[{}]", aiCoagulantInit != null ? 1 : 0);

            if(aiCoagulantInit != null) {
                int nOperationMode = aiCoagulantInit.getInit_val().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL) {
                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRnti(runTime);
                    queryDto.setKfk_flg(CommonValue.KAFKA_FLAG_INIT);
                    queryDto.setProcessStep(processStep);

                    List<AiProcessControlDTO> aiCoagulantControlList = databaseService.getListAiCoagulantControl(queryDto);
                    
                    if(aiCoagulantControlList.size() > 0) {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date rnti = new Date();
                        boolean alarmExceededFlag = false; //임계치 알람 해당 여부
                        try {
                            for(AiProcessControlDTO dto : aiCoagulantControlList) {
                                InterfaceAlarmControlHistoryDTO paramAlarm = new InterfaceAlarmControlHistoryDTO();
                                paramAlarm.setAlm_ntf_ti(dto.getRnti());
                                paramAlarm.setProcess(CommonValue.PROCESS_COAGULANT);
                                paramAlarm.setProcessStep(String.valueOf(processStep));
                                paramAlarm.setAlmTy(CommonValue.ALARM_TYPE_THRESHOLD_EXCEEDED);
                                paramAlarm.setUpdTi(dto.getUpd_ti());
                                paramAlarm.setTagSn(dto.getTag_sn());

                                InterfaceAlarmControlHistoryDTO alarmExceededInfo = databaseService.getAlarmExceeded(paramAlarm);
                                if(alarmExceededInfo != null) { //임계치 제어에 해당
                                	alarmExceededFlag = true;
                                    if(bFirst == true) {
                                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                        rnti = dto.getRnti();
                                        popupMap.put("alarm_id", alarmExceededInfo.getAlm_id());
                                        popupMap.put("message", alarmExceededInfo.getDp_nm());
                                        popupMap.put("url", alarmExceededInfo.getUrl());
                                        popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(popupMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                        bFirst = false;
                                    }
                                    if(!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                        updateDto.setProcessStep(processStep);
                                        databaseService.modAiCoagulantControlKafkaFlag(updateDto);
                                    }
                                } else {
                                    if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO) {
                                    
                                        // 3. if operation_mode==1 (semi_auto)
                                        // 3-1. send control value to kafka ai_popup
                                        AlarmInfoDTO alarmInfo =
                                                alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_COAGULANT_AI_CONTROL, processStep);
                                        if(alarmInfo != null) {
                                            // KAFKA topic is called only once.
                                            if(bFirst == true) {
                                                LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                                rnti = dto.getRnti();
                                                popupMap.put("alarm_id", alarmInfo.getAlm_id());
                                                popupMap.put("message", alarmInfo.getDp_nm());
                                                popupMap.put("url", alarmInfo.getUrl());
                                                popupMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                                strBody = objectMapper.writeValueAsString(popupMap);
                                                kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);
    
                                                bFirst = false;
                                            }
    
                                            if(!bFirst && dto.getRnti().compareTo(rnti) == 0) {
                                                // 3-2. update kafka_flag=1 (kafka_popup)
                                                AiProcessControlDTO updateDto = dto;
                                                updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_POPUP);
                                                updateDto.setProcessStep(processStep);
                                                databaseService.modAiCoagulantControlKafkaFlag(updateDto);
                                            }
                                        } else {
                                            log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_COAGULANT_AI_CONTROL);
                                        }         
                                    } else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO) {
                                        // 4. if operation_mode==2 (full_auto)
    
                                        // 4-1. send control value to kafka ai_control
                                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                        controlMap.put("tag", dto.getTag_sn());
                                        controlMap.put("value", dto.getTag_val().equalsIgnoreCase(CommonValue.CONTROL_TRUE) ? true : Float.parseFloat(dto.getTag_val()));
                                        controlMap.put("time", simpleDateFormat.format(dto.getRnti()));
                                        strBody = objectMapper.writeValueAsString(controlMap);
                                        kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
    
                                        // 4-2. update kafka_flag=3 (kafka_send)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKfk_flg(CommonValue.KAFKA_FLAG_SEND);
                                        updateDto.setProcessStep(processStep);
                                        databaseService.modAiCoagulantControlKafkaFlag(updateDto);

                                    }     
                                }
                            } //for문 끝
                            //자동모드이면서 && 임계치 알람이 아닌 경우( = AI제어 알람인 경우) -- 이력 업데이트.
                            if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO && !alarmExceededFlag) {
                                List<AlmCtrHisDTO> autoAlmCtrHisList = new ArrayList<>(); // 자동 제어시의 히스토리 리스트
                                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_COAGULANT_AI_CONTROL, processStep);
                                Date currentDate = new Date();
                                if(alarmInfo != null) {
	                                // insert alarm_notify & get almSeq
	                                int almSeq = alarmService.alarmNotify(
	                                		alarmInfo.getAlm_id(),
	                                		alarmInfo.getDp_nm(),
	                                        alarmInfo.getUrl(),
	                                        simpleDateFormat.format(currentDate)
	                                );
	                                if(almSeq !=0) {
	                                	//ctrHisList insert 
	                                	for(AiProcessControlDTO ctr : aiCoagulantControlList) {
	                                		AlmCtrHisDTO his = new AlmCtrHisDTO();
	                                		his.setAlm_id(alarmInfo.getAlm_id());
	                                		his.setAlm_seq(almSeq); // alarmNotify에 넣을때 사용한 seq를 반환받아서 넣어야함.
	                                		his.setAlm_ty(alarmInfo.getAlm_ty());
	                                		his.setCtr_ti(currentDate);
	                                		his.setCtr_yn("A");
	                                		his.setTag_sn(ctr.getTag_sn());
	                                		his.setUpd_ti(ctr.getUpd_ti());
	                                		autoAlmCtrHisList.add(his);
	                                	}
	                                	databaseService.addAlmCtrHis(autoAlmCtrHisList);                                    	
	                                }
                                }else {
                                    log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_COAGULANT_AI_CONTROL);
                                }
                            }
                        } catch(JsonProcessingException e) {
                            log.error("JsonProcessingException Occurred in Coagulant Control Process");
                        } catch(NumberFormatException e) {
                            log.error("NumberException Occurred in Coagualnt Control Process");
                        }
                    }
                }
            }
        }
    }

    
    /**
     * 기존 시스템 통신 연결이 끊겼을때 근무자 인지 알람(팝업창) 설정
     * @param token
     */
    @RequestMapping(value = "/internal/realTimeCheck", method = RequestMethod.GET)
    public void realTimeCheck(@RequestHeader("X-ACCESS-TOKEN") String token) {
    	
        // Token Check
        if (propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getSensors, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }
        
        if(realTimeDate == null) {
        	realTimeDate = new Date();
            realTimeDate.setTime(realTimeDate.getTime() - CommonValue.ONE_HOUR);
        }
        
        // Check ten seconds after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - realTimeDate.getTime() > CommonValue.ONE_MINUTE)
        {
        	realTimeDate = new Date();
            log.info("get realTime table");
            LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
            // 실시간 RT테이블 조회
            List<InterfaceAlarmControlHistoryDTO> allRealTimeList = databaseService.getAllRealTime(today);

            String strBody;
            ObjectMapper objectMapper = new ObjectMapper();
            
            if (allRealTimeList.size() > 0) {
    	        InterfaceAlarmControlHistoryDTO dto = new InterfaceAlarmControlHistoryDTO();
    	        dto.setStart_time(Date.from(today.atZone(ZoneId.systemDefault()).toInstant()));
    	        dto.setAlm_id(129901);
    	
    	        // 최근 5분 알람 조회
    	        List<InterfaceAlarmControlHistoryDTO> alarmNotifyList = databaseService.getBeforeAlarmNotify(dto);
    	
    	        if (alarmNotifyList.size() == 0) {
    	            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                    String almCdNm = "system_"+CommonValue.ALARM_CODE_CONNECTION_ERROR;
    	            AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(almCdNm, 0);
    	            if(alarmInfo != null) {
	    	            popupMap.put("alarm_id", alarmInfo.getAlm_id());
	    	            popupMap.put("message", alarmInfo.getDp_nm());
	    	            popupMap.put("url", alarmInfo.getUrl());
	    	            popupMap.put("time", today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	    	            try {
	    	                strBody = objectMapper.writeValueAsString(popupMap);
	    	                kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);
	    	            } catch (JsonProcessingException e) {
	    	                log.error("JsonProcessingException");
	    	            }
    	            }else {
    	            	log.error("Does not exist alarmInfo:[{]]", almCdNm);
    	            }
    	        }
    	    }
        }

    }

    /**
     * 실시간 AI 운영모드 체크 및 운영시간 update 
     * @param token
     */
    @RequestMapping(value = "/internal/updateAiOprRt", method = RequestMethod.GET)
    public void updateAiOprRt(@RequestHeader("X-ACCESS-TOKEN") String token) {
    	
        // Token Check
        if (propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getSensors, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }
        
        if(aiOprRtDate == null) {
        	aiOprRtDate = new Date();
            aiOprRtDate.setTime(aiOprRtDate.getTime() - CommonValue.ONE_HOUR);
        }
        
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - aiOprRtDate.getTime() > CommonValue.ONE_MINUTE)
        {
        	aiOprRtDate = new Date();
            log.info("get AiOprRealtime table");
            
            // 전 공정 운전모드 조회 (소독 제외)
            String[] processArray = new String[]{"C", "D"}; //공정 리스트 (소독 제외)
//            String[] processStepArray;
            List<AiOprRealtimeDTO> aiOprRtList= new ArrayList<AiOprRealtimeDTO>();
            for(int i = 0; i < processArray.length; i++) {
            	AiOprRealtimeDTO oprRt = new AiOprRealtimeDTO();
            	oprRt.setProc_cd(processArray[i]);
            	oprRt.setDisinfection_index("NONE");
        		AiProcessInitDTO init = new AiProcessInitDTO();
            	switch (processArray[i]) {
            		case CommonValue.PROCESS_COAGULANT:
            			init = databaseService.getAiCoagulantInit(processArray[i].toLowerCase()+"_operation_mode", 1);
            		break;
            		case CommonValue.PROCESS_MIXING:
            			init = databaseService.getAiMixingInit(processArray[i].toLowerCase()+"_operation_mode", 1);
            		break;
            	}
            	
        		if(init != null) {
        			oprRt.setAi_opr(init.getInit_val().intValue());
        			aiOprRtList.add(oprRt);
        		}
            }
            
            //소독 공정 운전모드 조회
            int[] disinfectionStepArray = new int[] {1,3};
            String[] disinfectionIndexArray = new String[] {"PRE", "POST"};
            for(int i = 0; i < disinfectionStepArray.length; i++) {
            	AiOprRealtimeDTO oprRt = new AiOprRealtimeDTO();
            	oprRt.setProc_cd("G");
            	AiProcessInitDTO init = new AiProcessInitDTO();
            	init = databaseService.getAiDisinfectionInit("g_"+disinfectionIndexArray[i].toLowerCase()+"_operation_mode", 1, disinfectionStepArray[i]);
    			if(init != null) {
    				oprRt.setAi_opr(init.getInit_val().intValue());
    				oprRt.setDisinfection_index(disinfectionIndexArray[i]);
    				aiOprRtList.add(oprRt);
    			}
            }

            //운전모드 RT 테이블 데이터 업데이트
            aiOprRtList.forEach(oprRt -> databaseService.updateAiOprRealtime(oprRt));
            
        }

    }
    
    /**
     * AI 운영모드 이력 테이블 데이터 추가 && 실시간 AI 운영모드 테이블 운영시간 초기화
     * @param token
     */
    @RequestMapping(value = "/internal/updateAiOprHisAndRt", method = RequestMethod.GET)
    public void updateAiOprRtAndHis(@RequestHeader("X-ACCESS-TOKEN") String token) {
    	
        //Token Check
        if (propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getSensors, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        //History 테이블 추가
        List<AiOprHistoryDTO> aiOprHisList = new ArrayList<AiOprHistoryDTO>();
        List<AiOprRealtimeDTO> aiOprRtList = new ArrayList<AiOprRealtimeDTO>();
        
        aiOprRtList = databaseService.getAllAiOprRt();

        if(aiOprRtList != null) {
	        for(AiOprRealtimeDTO aiOprRt : aiOprRtList) {
	        	AiOprHistoryDTO aiHisDTO = new AiOprHistoryDTO();
	        	aiHisDTO.setHis_date(yesterday);
	        	aiHisDTO.setProc_cd(aiOprRt.getProc_cd());
	        	aiHisDTO.setAi_opr(aiOprRt.getAi_opr());
	        	aiHisDTO.setDisinfection_index(aiOprRt.getDisinfection_index());
	        	aiHisDTO.setOpr_minutes(aiOprRt.getOpr_minutes());
	        	aiOprHisList.add(aiHisDTO);
	        }
	        
	        //aiOprHis 추가
	        aiOprHisList.forEach(aiOprHis -> databaseService.addAiOprHistory(aiOprHis));
	        //aiOprRt 초기화
	        databaseService.initializeAiOprRealtimeValues();
        } else {
        	log.error("Does not exist aiOprRtList");
        }
    }

    /**
     * 각 서비스의 컨테이너 연결 이상 시 서비스 통신 연결 알람(팝업창) 발생
     * @param token
     */
    @RequestMapping(value = "/internal/serviceStatus", method = RequestMethod.GET)
    public void getServiceStatus(@RequestHeader("X-ACCESS-TOKEN") String token, @RequestHeader("SERVER") String server, @RequestHeader("SERVICE") String service) {
    	// Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false) {
            log.error("getRealTime, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        String strBody;
        ObjectMapper objectMapper = new ObjectMapper();

        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
        String alarmCdNm = server + "_" + service + "_connection_error";
        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(alarmCdNm, 0);
        if(alarmInfo != null) {
	        popupMap.put("alarm_id", alarmInfo.getAlm_id());
	        popupMap.put("message", alarmInfo.getDp_nm());
	        popupMap.put("url", alarmInfo.getUrl());
	        popupMap.put("time", today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	        try {
	            strBody = objectMapper.writeValueAsString(popupMap);
	            kafkaProducer.sendMessageToVip(CommonValue.KAFKA_TOPIC_POPUP, strBody);
	            log.error("[Collector] " + server + "-" + service + " is dead");
	        } catch (JsonProcessingException e) {
	            log.error("JsonProcessingException");
	        }
        }else {
			log.error("Does not exist alarmInfo:[{]]", alarmCdNm);
		}
    }

    

}
