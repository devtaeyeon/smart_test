package co.irexnet.waio.WAIO_ServerAgent.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiClearOperationBandDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiCoagulantRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiCoagulantSimulationDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiDisinfectionRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiEmsRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiFilterRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiGacRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiMixingRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiOzoneRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessAlarmDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessControlDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiReceivingRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationInterfaceRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.FrequencyDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.JsonBSeriesInt;
import co.irexnet.waio.WAIO_ServerAgent.dao.AccessTokenDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiClearEmsOperationBandDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiClearOperationBandDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiClearWideOperationBandDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiCoagulantAlarmDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiCoagulantControlDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiCoagulantInitDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiCoagulantRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiCoagulantSimulationDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiDisinfectionAlarmDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiDisinfectionInitDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiDisinfectionRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiEmsRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiFactorDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiFilterAlarmDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiFilterControlDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiFilterInitDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiFilterRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiGacControlDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiGacInitDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiGacRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiMixingAlarmDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiMixingControlDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiMixingInitDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiMixingRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiOprHistoryDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiOprRealTimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiOzoneControlDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiOzoneInitDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiOzoneRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiPeriDisinfectionControlDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiPostDisinfectionControlDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiPreDisinfectionControlDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiReceivingAlarmDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiReceivingControlDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiReceivingDataDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiReceivingInitDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiReceivingRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiSedimentationAlarmDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiSedimentationControlDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiSedimentationInitDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AiSedimentationRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AlarmInfoDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.AlarmNotifyDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.ChemicalInfoDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.ClassInfoDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.ClusterInfoDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.CoagulantRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.CoagulantsAnalysisDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.CoagulantsSimulationDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.DashboardInfoDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.DiatomDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.DisinfectionRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.DiskInfoDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.EmsRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.FilterRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.GacRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.InterfaceInfoDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.LoadProcessRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.LoginHistoryDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.MixingRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.OzoneRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.PartitionInfoDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.PmsRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.ProcessCodeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.ReceivingRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.SedimentationRealtimeDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.SensorDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.SystemConfigDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.SystemInfoDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.SystemMonitoringDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.TagDescriptionDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.TagManageDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.UserDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dao.WaterPurificationInfoDAOImpl;
import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiFactorDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprRealTimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmNotifyDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.ChemicalInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.ClassInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.ClusterInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.CoagulantsAnalysisDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.CoagulantsSimulationDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.DashboardDataDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.DashboardIdDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.DiatomDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.DiskInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAlarmControlHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.LoginHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.PartitionInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.PmsAiDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.PmsScadaDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessCodeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.SensorDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.SystemConfigDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.SystemInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.SystemMonitoringDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagDescriptionDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageRangeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TrendCodeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TrendTbDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.UserDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.WaterPurificationInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.resource_dto.CoagulantsDTO;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DatabaseServiceImpl implements IDatabaseService {
    @Autowired
    AccessTokenDAOImpl accessToken;

    @Autowired
    UserDAOImpl user;

    @Autowired
    LoginHistoryDAOImpl loginHistory;

    @Autowired
    DiskInfoDAOImpl diskInfo;

    @Autowired
    PartitionInfoDAOImpl partitionInfo;

    @Autowired
    InterfaceInfoDAOImpl interfaceInfo;

    @Autowired
    SystemConfigDAOImpl systemConfig;

    @Autowired
    SystemInfoDAOImpl systemInfo;

    @Autowired
    SystemMonitoringDAOImpl systemMonitoring;

    @Autowired
    SensorDAOImpl sensor;

    @Autowired
    DiatomDAOImpl diatom;

    @Autowired
    AlarmInfoDAOImpl alarmInfo;

    @Autowired
    AlarmNotifyDAOImpl alarmNotify;

    @Autowired
    ChemicalInfoDAOImpl chemicalInfo;

    @Autowired
    WaterPurificationInfoDAOImpl waterPurificationInfo;

    @Autowired
    CoagulantsAnalysisDAOImpl coagulantsAnalysis;

    @Autowired
    CoagulantsSimulationDAOImpl coagulantsSimulation;

    @Autowired
    ClassInfoDAOImpl classInfo;

    @Autowired
    ClusterInfoDAOImpl clusterInfo;

    @Autowired
    DashboardInfoDAOImpl dashboardInfo;

    @Autowired
    TagDescriptionDAOImpl tagDescription;

    @Autowired
    TagManageDAOImpl tagManage;

    @Autowired
    ProcessCodeDAOImpl processCode;

    @Autowired
    ReceivingRealtimeDAOImpl receivingRealtime;

    @Autowired
    AiReceivingRealtimeDAOImpl aiReceivingRealtime;

    @Autowired
    AiReceivingControlDAOImpl aiReceivingControl;

    @Autowired
    AiReceivingInitDAOImpl aiReceivingInit;

    @Autowired
    AiReceivingDataDAOImpl aiReceivingData;

    @Autowired
    AiReceivingAlarmDAOImpl aiReceivingAlarm;

    @Autowired
    AiClearOperationBandDAOImpl aiClearOperationBand;

    @Autowired
    AiClearEmsOperationBandDAOImpl aiClearEmsOperationBand;

    @Autowired
    AiClearWideOperationBandDAOImpl aiClearWideOperationBand;

    @Autowired
    CoagulantRealtimeDAOImpl coagulantRealtime;

    @Autowired
    AiCoagulantRealtimeDAOImpl aiCoagulantRealtime;

    @Autowired
    AiCoagulantControlDAOImpl aiCoagulantControl;

    @Autowired
    AiCoagulantInitDAOImpl aiCoagulantInit;

    @Autowired
    AiCoagulantAlarmDAOImpl aiCoagulantAlarm;

    @Autowired
    AiCoagulantSimulationDAOImpl aiCoagulantSimulation;

    @Autowired
    MixingRealtimeDAOImpl mixingRealtime;

    @Autowired
    AiMixingRealtimeDAOImpl aiMixingRealtime;

    @Autowired
    AiMixingControlDAOImpl aiMixingControl;

    @Autowired
    AiMixingInitDAOImpl aiMixingInit;

    @Autowired
    AiMixingAlarmDAOImpl aiMixingAlarm;

    @Autowired
    SedimentationRealtimeDAOImpl sedimentationRealtime;

    @Autowired
    AiSedimentationRealtimeDAOImpl aiSedimentationRealtime;

    @Autowired
    AiSedimentationControlDAOImpl aiSedimentationControl;

    @Autowired
    AiSedimentationInitDAOImpl aiSedimentationInit;

    @Autowired
    AiSedimentationAlarmDAOImpl aiSedimentationAlarm;

    @Autowired
    FilterRealtimeDAOImpl filterRealtime;

    @Autowired
    AiFilterRealtimeDAOImpl aiFilterRealtime;

    @Autowired
    AiFilterControlDAOImpl aiFilterControl;

    @Autowired
    AiFilterInitDAOImpl aiFilterInit;

    @Autowired
    AiFilterAlarmDAOImpl aiFilterAlarm;

    @Autowired
    GacRealtimeDAOImpl gacRealtime;

    @Autowired
    AiGacRealtimeDAOImpl aiGacRealtime;

    @Autowired
    AiGacControlDAOImpl aiGacControl;

    @Autowired
    AiGacInitDAOImpl aiGacInit;

    @Autowired
    DisinfectionRealtimeDAOImpl disinfectionRealtime;

    @Autowired
    AiDisinfectionRealtimeDAOImpl aiDisinfectionRealtime;

    @Autowired
    AiPreDisinfectionControlDAOImpl aiPreDisinfectionControl;

    @Autowired
    AiPeriDisinfectionControlDAOImpl aiPeriDisinfectionControl;

    @Autowired
    AiPostDisinfectionControlDAOImpl aiPostDisinfectionControl;

    @Autowired
    AiDisinfectionInitDAOImpl aiDisinfectionInit;

    @Autowired
    AiDisinfectionAlarmDAOImpl aiDisinfectionAlarm;

    @Autowired
    OzoneRealtimeDAOImpl ozoneRealtime;

    @Autowired
    AiOzoneRealtimeDAOImpl aiOzoneRealtime;

    @Autowired
    AiOzoneControlDAOImpl aiOzoneControl;

    @Autowired
    AiOzoneInitDAOImpl aiOzoneInit;

    @Autowired
    EmsRealtimeDAOImpl emsRealtime;
    
    @Autowired
    AiEmsRealtimeDAOImpl aiEmsRealtime;

    @Autowired
    PmsRealtimeDAOImpl pmsRealtime;

    @Autowired
    LoadProcessRealtimeDAOImpl loadProcessRealtime;
    
    @Autowired
    AiFactorDAOImpl aiFactor;
    
    @Autowired
    AiOprRealTimeDAOImpl aiOprRealTime;
    
    @Autowired
    AiOprHistoryDAOImpl aiOprHistory;

    ////////////////////////////////////////////
    // access_token
    ////////////////////////////////////////////
    @Override
    public int addToken(AccessTokenDTO dto) {
        return accessToken.insert(dto);
    }

    @Override
    public List<AccessTokenDTO> getAllTokens() {
        return accessToken.select();
    }

    @Override
    public AccessTokenDTO getToken(String token) {
        return accessToken.select(token);
    }

    @Override
    public int modToken(String token, Date expiration) {
        return accessToken.update(token, expiration);
    }

    @Override
    public int delToken(String token) {
        return accessToken.delete(token);
    }

    @Override
    public int delToken(Date expiration) {
        return accessToken.delete(expiration);
    }

    ////////////////////////////////////////////
    // user
    ////////////////////////////////////////////
    @Override
    public int addUser(UserDTO dto) {
        return user.insert(dto);
    }

    @Override
    public UserDTO getUser(String usr_id, String usr_pw) {
        return user.selectUser(usr_id, usr_pw);
    }

    @Override
    public UserDTO getUserFromUserid(String usr_id) {
        return user.selectUserFromUserid(usr_id);
    }

    @Override
    public List<UserDTO> getAllUser() {
        return user.selectAll();
    }

    @Override
    public int modUser(int usr_auth, UserDTO dto) {
        return user.update(usr_auth, dto);
    }
    
    @Override
    public int modUserMyInfo(int usr_auth, UserDTO dto)
    {
        return user.updateMyInfo(usr_auth, dto);
    }

    @Override
    public int modUserPw(String usr_id, String usr_pw) {
        return user.updatePw(usr_id, usr_pw);
    }

    @Override
    public int delUser(String usr_id) {
        return user.delete(usr_id);
    }

    ////////////////////////////////////////////
    // login_history
    ////////////////////////////////////////////
    @Override
    public int addLoginHistory(LoginHistoryDTO dto) {
        return loginHistory.insert(dto);
    }

    @Override
    public List<LoginHistoryDTO> getAllLoginHistory() {
        return loginHistory.select();
    }

    @Override
    public int delLoginHistory(Date date) {
        return loginHistory.delete(date);
    }

    ////////////////////////////////////////////
    // disk_info
    ////////////////////////////////////////////
    @Override
    public int addDiskInfo(DiskInfoDTO dto) {
        return diskInfo.insert(dto);
    }

    @Override
    public List<DiskInfoDTO> getDiskInfoFromHostname(String hostname) {
        return diskInfo.select(hostname);
    }

    @Override
    public int modDiskInfo(DiskInfoDTO dto) {
        return diskInfo.update(dto);
    }

    ////////////////////////////////////////////
    // partition_info
    ////////////////////////////////////////////
    @Override
    public int addPartitionInfo(PartitionInfoDTO dto) {
        return partitionInfo.insert(dto);
    }

    @Override
    public List<PartitionInfoDTO> getPartitionInfoFromHostname(String hostname) {
        return partitionInfo.select(hostname);
    }

    @Override
    public int modPartitionInfo(PartitionInfoDTO dto) {
        return partitionInfo.update(dto);
    }

    ////////////////////////////////////////////
    // interface_info
    ////////////////////////////////////////////
    @Override
    public int addInterfaceInfo(InterfaceInfoDTO dto) {
        return interfaceInfo.insert(dto);
    }

    @Override
    public List<InterfaceInfoDTO> getInterfaceInfoFromHostname(String hostname) {
        return interfaceInfo.select(hostname);
    }

    @Override
    public List<InterfaceInfoDTO> getInterfaceInfoFromAddress(String address) {
        return interfaceInfo.selectWhereAddress(address);
    }

    @Override
    public int modInterfaceInfo(InterfaceInfoDTO dto) {
        return interfaceInfo.update(dto);
    }

    ////////////////////////////////////////////
    // system_config
    ////////////////////////////////////////////
    @Override
    public SystemConfigDTO getSystemConfig() {
        return systemConfig.select();
    }

    @Override
    public int modSystemConfig(SystemConfigDTO dto) {
        return systemConfig.update(dto);
    }

    ////////////////////////////////////////////
    // system_info
    ////////////////////////////////////////////
    @Override
    public int addSystemInfo(SystemInfoDTO dto) {
        return systemInfo.insert(dto);
    }

    @Override
    public SystemInfoDTO getSystemInfoFromHostname(String hostname) {
        return systemInfo.select(hostname);
    }

    @Override
    public List<SystemInfoDTO> getAllSystemInfo() {
        return systemInfo.select();
    }

    @Override
    public int modSystemInfo(SystemInfoDTO dto) {
        return systemInfo.update(dto);
    }

    @Override
    public int modSystemInfoName(String hostname, String name) {
        return systemInfo.updateName(hostname, name);
    }

    ////////////////////////////////////////////
    // system_monitoring
    ////////////////////////////////////////////
    @Override
    public int addSystemMonitoring(SystemMonitoringDTO dto) {
        return systemMonitoring.insert(dto);
    }

    @Override
    public List<SystemMonitoringDTO> getAllSystemMonitoring() {
        return systemMonitoring.select();
    }

    @Override
    public List<SystemMonitoringDTO> getSystemMonitoringFromHostname(String hostname) {
        return systemMonitoring.select(hostname);
    }

    @Override
    public List<SystemMonitoringDTO> getLatestSystemMonitoring(Date startDate) {
        return systemMonitoring.selectLatest(startDate);
    }

    @Override
    public int delSystemMonitoring(Date date) {
        return systemMonitoring.delete(date);
    }

    ////////////////////////////////////////////
    // sensor
    ////////////////////////////////////////////
    @Override
    public int addSensor(SensorDTO dto) {
        return sensor.insert(dto);
    }

    @Override
    public List<SensorDTO> getSensor(Date startDate) {
        return sensor.select(startDate);
    }

    @Override
    public SensorDTO getLatestSensor() {
        return sensor.selectLatest();
    }

    @Override
    public List<CoagulantsDTO> getHistorySensor() {
        return sensor.selectHistory();
    }

    @Override
    public CoagulantsDTO getLatestCoagulants() {
        return sensor.selectCoagulants();
    }

    @Override
    public List<TrendTbDTO> getTrendTb(InterfaceDateSearchDTO dto) {
        return sensor.selectTb(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public List<TrendCodeDTO> getTrendCode(String code, InterfaceDateSearchDTO dto) {
        return sensor.selectCode(code, dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public List<FrequencyDTO> getDistinctCountHsE1Tb(Date startDate) {
        return sensor.selectDistinctCountHsE1Tb(startDate);
    }

    @Override
    public List<FrequencyDTO> getDistinctCountHsE2Tb(Date startDate) {
        return sensor.selectDistinctCountHsE2Tb(startDate);
    }

    @Override
    public List<FrequencyDTO> getDistinctCountHsFTb(Date startDate) {
        return sensor.selectDistinctCountHsFTb(startDate);
    }

    @Override
    public int delSensor(Date updateTime) {
        return sensor.delete(updateTime);
    }

    ////////////////////////////////////////////
    // diatom
    ////////////////////////////////////////////
    @Override
    public int addDiatom(DiatomDTO dto) {
        return diatom.insert(dto);
    }

    @Override
    public List<DiatomDTO> getDiatom() {
        return diatom.select();
    }

    @Override
    public DiatomDTO getLatestDiatom() {
        return diatom.selectLatest();
    }

    @Override
    public int modDiatom(DiatomDTO dto) {
        return diatom.update(dto);
    }

    @Override
    public int delDiatom(int diatomIndex) {
        return diatom.delete(diatomIndex);
    }

    ////////////////////////////////////////////
    // alarm_info
    ////////////////////////////////////////////
    @Override
    public int addAlarmInfo(AlarmInfoDTO dto) {
        return alarmInfo.insert(dto);
    }

    @Override
    public List<AlarmInfoDTO> getAlarmInfo() {
        return alarmInfo.select();
    }

    @Override
    public int modAlarmInfo(AlarmInfoDTO dto) {
        return alarmInfo.update(dto);
    }

    @Override
    public int delAlarmInfo(int alarmId) {
        return alarmInfo.delete(alarmId);
    }

    ////////////////////////////////////////////
    // alarm_notify
    ////////////////////////////////////////////
    @Override
    public int addAlarmNotify(AlarmNotifyDTO dto) {
        return alarmNotify.insert(dto);
    }

    @Override
    public List<AlarmNotifyDTO> getAllAlarmNotify() {
        return alarmNotify.select();
    }
    // 알람 네비게이터 삭제
    // @Override
    // public List<AlarmNotifyDTO> getAlarmNotifyFromAckState(boolean ackState)
    // {
    // return alarmNotify.select(ackState);
    // }

    @Override
    public List<AlarmNotifyDTO> getAlarmNotify(Date startTime) {
        return alarmNotify.select(startTime);
    }

    @Override
    public List<AlarmNotifyDTO> getSearchAlarmNotify(InterfaceDateSearchDTO dto) {
        return alarmNotify.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AlarmNotifyDTO getLatestAlarmNotify(int alarmId, Date alarmDate, String hostname) {
        return alarmNotify.select(alarmId, alarmDate, hostname);
    }

    @Override
    public int modAlarmNotifyAckState(int alarmNotifyIndex, boolean ackState) {
        return alarmNotify.updateAckState(alarmNotifyIndex, ackState);
    }

    ////////////////////////////////////////////
    // chemical_info
    ////////////////////////////////////////////
    @Override
    public int addChemicalInfo(ChemicalInfoDTO dto) {
        return chemicalInfo.insert(dto);
    }

    @Override
    public List<ChemicalInfoDTO> getAllChemicalInfo() {
        return chemicalInfo.select();
    }

    @Override
    public ChemicalInfoDTO getChemicalInfoFromCode(String code) {
        return chemicalInfo.select(code);
    }

    @Override
    public int modChemicalInfo(ChemicalInfoDTO dto) {
        return chemicalInfo.update(dto);
    }

    @Override
    public int delChemicalInfo(String code) {
        return chemicalInfo.delete(code);
    }

    ////////////////////////////////////////////
    // water_purification_info
    ////////////////////////////////////////////
    @Override
    public int addWaterPurificationInfo(WaterPurificationInfoDTO dto) {
        return waterPurificationInfo.insert(dto);
    }

    @Override
    public List<WaterPurificationInfoDTO> getAllWaterPurificationInfo() {
        return waterPurificationInfo.select();
    }

    @Override
    public WaterPurificationInfoDTO getWaterPurificationInfoFromCode(String code) {
        return waterPurificationInfo.select(code);
    }

    @Override
    public int modWaterPurificationInfo(WaterPurificationInfoDTO dto) {
        return waterPurificationInfo.update(dto);
    }

    @Override
    public int delWaterPurificationInfo(String code) {
        return waterPurificationInfo.delete(code);
    }

    ////////////////////////////////////////////
    // coagulants_analysis
    ////////////////////////////////////////////
    @Override
    public List<CoagulantsAnalysisDTO> getAllCoagulantsAnalysis() {
        return coagulantsAnalysis.select();
    }

    @Override
    public CoagulantsAnalysisDTO getCoagulantsAnalysisFromLogTime(Date logTime) {
        return coagulantsAnalysis.select(logTime);
    }

    @Override
    public CoagulantsAnalysisDTO getLatestCoagulantsAnalysis() {
        return coagulantsAnalysis.selectLatest();
    }

    @Override
    public List<CoagulantsAnalysisDTO> get2LatestCoagulantsAnalysis() {
        return coagulantsAnalysis.select2Latest();
    }

    @Override
    public List<CoagulantsAnalysisDTO> getMinuteCoagulantsAnalysis(Date startTime) {
        return coagulantsAnalysis.selectMinute(startTime);
    }

    @Override
    public int addRawWaterClassInfo(ClassInfoDTO dto) {
        return classInfo.insert(dto);
    }

    @Override
    public List<ClassInfoDTO> getAllRawWaterClassInfo() {
        return classInfo.select();
    }

    @Override
    public int modRawWaterClassInfo(ClassInfoDTO dto) {
        return classInfo.update(dto);
    }

    @Override
    public int delRawWaterClassInfo(int classIndex) {
        return classInfo.delete(classIndex);
    }

    @Override
    public List<ClusterInfoDTO> getAllCoagulantsClusterInfo() {
        return clusterInfo.select();
    }

    ////////////////////////////////////////////
    // coagulants_simulation
    ////////////////////////////////////////////

    @Override
    public int addCoagulantsSimulation(CoagulantsSimulationDTO dto) {
        return coagulantsSimulation.insert(dto);
    }

    @Override
    public List<CoagulantsSimulationDTO> getAllCoagulantsSimulation() {
        return coagulantsSimulation.select();
    }

    @Override
    public List<CoagulantsSimulationDTO> getCoagulantsSimulationUpperState(int state) {
        return coagulantsSimulation.select(true, state);
    }

    @Override
    public List<CoagulantsSimulationDTO> getCoagulantsSimulationLowerState(int state) {
        return coagulantsSimulation.select(false, state);
    }

    ////////////////////////////////////////////
    // dashboard_info
    ////////////////////////////////////////////

    @Override
    public int addDashboardInfo(String data) {
        return dashboardInfo.insert(data);
    }

    @Override
    public DashboardIdDTO getLatestDashboardInfo() {
        return dashboardInfo.selectLatest();
    }

    @Override
    public DashboardDataDTO getDashboardInfo(int dashboard_index) {
        return dashboardInfo.select(dashboard_index);
    }

    @Override
    public int modDashboardInfo(int dashboard_id, String data) {
        return dashboardInfo.update(dashboard_id, data);
    }

    @Override
    public int delDashboardInfo(int dashboard_id) {
        return dashboardInfo.delete(dashboard_id);
    }

    ////////////////////////////////////////////
    // tag_description
    ////////////////////////////////////////////
    @Override
    public int addTagDescription(TagDescriptionDTO dto) {
        return tagDescription.insert(dto);
    }

    @Override
    public List<TagDescriptionDTO> getAllTagDescription() {
        return tagDescription.select();
    }

    @Override
    public int modTagDescription(TagDescriptionDTO dto) {
        return tagDescription.update(dto);
    }

    @Override
    public int delTagDescription(int tagIndex) {
        return tagDescription.delete(tagIndex);
    }

    ////////////////////////////////////////////
    // tag_manage
    ////////////////////////////////////////////
    @Override
    public int addTagManage(TagManageDTO dto) {
        return tagManage.insert(dto);
    }

    @Override
    public List<TagManageDTO> getAllTagManage() {
        return tagManage.select();
    }

    @Override
    public List<TagManageDTO> getTagManageFromType(int type) {
        return tagManage.select(type);
    }

    @Override
    public List<TagManageDTO> getTagManageFromCode(String process, int processStep) {
        return tagManage.select(process, processStep);
    }

    @Override
    public TagManageRangeDTO getTagManageRange(String process, int processStep) {
        return tagManage.selectRange(process, processStep);
    }

    @Override
    public int modTagManage(TagManageDTO dto) {
        return tagManage.update(dto);
    }

    @Override
    public int delTagManage(TagManageDTO dto) {
        return tagManage.delete(dto);
    }

    @Override
    public List<String> selectProcCd() {
        return tagManage.selectProcCd();
    }

    ////////////////////////////////////////////
    // process_code
    ////////////////////////////////////////////
    @Override
    public List<ProcessCodeDTO> getAllProcessCode() {
        return processCode.select();
    }

    ////////////////////////////////////////////
    // load_process_realtime
    ////////////////////////////////////////////
    @Override
    public int addProcessRealtimeValue(String procCd, List<ProcessRealtimeDTO> dtos) {
        return loadProcessRealtime.insert(procCd, dtos);
    }

    @Override
    public void addProcessRealtimePartition(String procCd, List<String> partitionNameList) {
        loadProcessRealtime.addPartition(procCd, partitionNameList);
    }

    @Override
    public List<String> getAddPartitionList(String procCd) {
        return loadProcessRealtime.getAddPartitionList(procCd);
    }

    @Override
    public void delProcessRealtimePartition(String procCd, List<String> partitionNameList) {
        loadProcessRealtime.dropPartition(procCd, partitionNameList);
    }

    @Override
    public List<String> getDropPartitionList(String procCd, String partitionNm) {
        return loadProcessRealtime.getDropPartitionList(procCd, partitionNm);
    }

    ////////////////////////////////////////////
    // receiving_realtime
    ////////////////////////////////////////////
    @Override
    public int addReceivingRealtimeValue(List<ProcessRealtimeDTO> dtos) {
        return receivingRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestReceivingRealtimeValue(String partitionName, int processStep) {
        return receivingRealtime.select(partitionName, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllReceivingRealtimeValueFromTime(Date startTime, int processStep) {
        return receivingRealtime.select(startTime, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getReceivingRealtimeValueFromTag(String name, Date startTime, Date endTime,
            int processStep) {
        return receivingRealtime.select(name, startTime, endTime, processStep);
    }

    @Override
    public ProcessRealtimeDTO getLatestReceivingRealtimeValueFromTag(String name, int processStep) {
        return receivingRealtime.selectLatest(name, processStep);
    }

    @Override
    public void addReceivingRealtimePartition(String partitionName, String endTime) {
        receivingRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delReceivingRealtimePartition(String partitionName) {
        receivingRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_receiving_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiReceivingRealtimeDTO> getAiReceivingRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto,
            int processStep) {
        return aiReceivingRealtime.select(dto.getStart_time(), dto.getEnd_time(), processStep);
    }

    @Override
    public AiReceivingRealtimeDTO getLatestAiReceivingRealtimeValue(int processStep) {
        return aiReceivingRealtime.select(processStep);
    }

    @Override
    public int delAiReceivingRealtimeValue(Date updateTime, int processStep) {
        return aiReceivingRealtime.delete(updateTime, processStep);
    }

    @Override
    public List<AiClearOperationBandDTO> getAiClearOperationBandFromTimeIndex(InterfaceDateSearchDTO dto,
            int processStep) {
        return aiClearOperationBand.select(dto.getStart_time(), dto.getEnd_time(), processStep);
    }

    @Override
    public List<AiClearOperationBandDTO> getAiClearEmsOperationBandFromTimeIndex(InterfaceDateSearchDTO dto,
            int processStep) {
        return aiClearEmsOperationBand.select(dto.getStart_time(), dto.getEnd_time(), processStep);
    }

    @Override
    public List<AiClearOperationBandDTO> getAiClearWideOperationBandFromTimeIndex(InterfaceDateSearchDTO dto,
            int processStep) {
        return aiClearWideOperationBand.select(dto.getStart_time(), dto.getEnd_time(), processStep);
    }

    ////////////////////////////////////////////
    // ai_receiving_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiReceivingControl(AiProcessControlDTO dto) {
        return aiReceivingControl.select(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public List<AiProcessControlDTO> getListAiReceivingControl(AiProcessControlDTO dto) {
        return aiReceivingControl.select(dto.getRnti(), dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public int modAiReceivingControlKafkaFlag(AiProcessControlDTO dto) {
        return aiReceivingControl.updateKafkaFlag(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public int delAiReceivingControl(Date date, int processStep) {
        return aiReceivingControl.delete(date, processStep);
    }

    ////////////////////////////////////////////
    // ai_receiving_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiReceivingInit(int processStep) {
        return aiReceivingInit.select(processStep);
    }

    @Override
    public AiProcessInitDTO getAiReceivingInit(String item, int processStep) {
        return aiReceivingInit.select(item, processStep);
    }

    @Override
    public int modAiReceivingOperationMode(int operationMode, int processStep) {
        return aiReceivingInit.updateOperationMode(operationMode, processStep);
    }

    @Override
    public int modAiReceivingInit(String item, float value, int processStep) {
        return aiReceivingInit.update(item, value, processStep);
    }

    ////////////////////////////////////////////
    // ai_receiving_data
    ////////////////////////////////////////////
    @Override
    public int addAiReceivingDataValue(List<ProcessRealtimeDTO> dtos) {
        return aiReceivingData.insert(dtos);
    }

    @Override
    public int delAiReceivingDataValue(Date updateTime) {
        return aiReceivingData.delete(updateTime);
    }

    ////////////////////////////////////////////
    // ai_receiving_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiReceivingAlarm(AiProcessAlarmDTO dto) {
        return aiReceivingAlarm.insert(dto.getAlm_id(), dto.getAlm_ti(), dto.getKfk_flg());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiReceivingAlarm(Date alarm_time, int kafka_flag, int processStep) {
        return aiReceivingAlarm.select(alarm_time, kafka_flag, processStep);
    }

    @Override
    public int modAiReceivingAlarmKafkaFlag(AiProcessAlarmDTO dto) {
        return aiReceivingAlarm.updateKafkaFlag(dto);
    }

    @Override
    public int delAiReceivingAlarm(Date date) {
        return aiReceivingAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // coagulant_realtime
    ////////////////////////////////////////////
    @Override
    public int addCoagulantRealtimeValue(List<ProcessRealtimeDTO> dtos) {
        return coagulantRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestCoagulantRealtimeValue(String partitionName, int processStep) {
        return coagulantRealtime.select(partitionName, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllCoagulantRealtimeValueFromTime(Date startTime, int processStep) {
        return coagulantRealtime.select(startTime, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getCoagulantRealtimeValueFromTag(String name, Date startTime, Date endTime,
            int processStep) {
        return coagulantRealtime.select(name, startTime, endTime, processStep);
    }

    @Override
    public List<FrequencyDTO> getCoagulantDistribution(Date startTime, String name, int processStep) {
        return coagulantRealtime.selectDistribution(startTime, name, processStep);
    }

    @Override
    public void addCoagulantRealtimePartition(String partitionName, String endTime) {
        coagulantRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delCoagulantRealtimePartition(String partitionName) {
        coagulantRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_coagulant_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiCoagulantRealtimeDTO> getAiCoagulantRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto,
            int processStep) {
        return aiCoagulantRealtime.select(dto.getStart_time(), dto.getEnd_time(), processStep);
    }

    @Override
    public AiCoagulantRealtimeDTO getLatestAiCoagulantRealtimeValue(int processStep) {
        return aiCoagulantRealtime.select(processStep);
    }

    @Override
    public int delAiCoagulantRealtimeValue(Date updateTime, int processStep) {
        return aiCoagulantRealtime.delete(updateTime, processStep);
    }

    ////////////////////////////////////////////
    // ai_coagulant_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiCoagulantControl(AiProcessControlDTO dto) {
        return aiCoagulantControl.select(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public List<AiProcessControlDTO> getListAiCoagulantControl(AiProcessControlDTO dto) {
        return aiCoagulantControl.select(dto.getRnti(), dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public int modAiCoagulantControlKafkaFlag(AiProcessControlDTO dto) {
        return aiCoagulantControl.updateKafkaFlag(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public int delAiCoagulantControl(Date date, int processStep) {
        return aiCoagulantControl.delete(date, processStep);
    }

    ////////////////////////////////////////////
    // ai_coagulant_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiCoagulantInit(int processStep) {
        return aiCoagulantInit.select(processStep);
    }

    @Override
    public AiProcessInitDTO getAiCoagulantInit(String item, int processStep) {
        return aiCoagulantInit.select(item, processStep);
    }

    @Override
    public int modAiCoagulantOperationMode(int operationMode, int processStep) {
        return aiCoagulantInit.updateOperationMode(operationMode, processStep);
    }

    @Override
    public int modAiCoagulantInit(String item, float value, int processStep) {
        return aiCoagulantInit.update(item, value, processStep);
    }

    ////////////////////////////////////////////
    // ai_coagulant_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiCoagulantAlarm(AiProcessAlarmDTO dto) {
        return aiCoagulantAlarm.insert(dto.getAlm_id(), dto.getAlm_ti(), dto.getKfk_flg());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiCoagulantAlarm(Date alarm_time, int kafka_flag, int processStep) {
        return aiCoagulantAlarm.select(alarm_time, kafka_flag, processStep);
    }

    @Override
    public int modAiCoagulantAlarmKafkaFlag(AiProcessAlarmDTO dto) {
        return aiCoagulantAlarm.updateKafkaFlag(dto);
    }

    @Override
    public int delAiCoagulantAlarm(Date date) {
        return aiCoagulantAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // ai_coagulant_simulation
    ////////////////////////////////////////////
    @Override
    public int addAiCoagulantSimulation(AiCoagulantSimulationDTO dto) {
        return aiCoagulantSimulation.insert(dto);
    }

    @Override
    public List<AiCoagulantSimulationDTO> getAiCoagulantSimulation(InterfaceDateSearchDTO dto) {
        return aiCoagulantSimulation.select(dto.getStart_time(), dto.getEnd_time());
    }

    ////////////////////////////////////////////
    // mixing_realtime
    ////////////////////////////////////////////
    @Override
    public int addMixingRealtimeValue(List<ProcessRealtimeDTO> dtos) {
        return mixingRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestMixingRealtimeValue(String partitionName, int processStep) {
        return mixingRealtime.select(partitionName, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllMixingRealtimeValueFromTime(Date startTime, int processStep) {
        return mixingRealtime.select(startTime, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getMixingRealtimeValueFromTag(String name, Date startTime, Date endTime,
            int processStep) {
        return mixingRealtime.select(name, startTime, endTime, processStep);
    }

    @Override
    public ProcessRealtimeDTO getLatestMixingRealtimeValueFromTag(String name, int processStep) {
        return mixingRealtime.selectLatest(name, processStep);
    }

    @Override
    public void addMixingRealtimePartition(String partitionName, String endTime) {
        mixingRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delMixingRealtimePartition(String partitionName) {
        mixingRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_mixing_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiMixingRealtimeDTO> getAiMixingRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto,
            int processStep) {
        return aiMixingRealtime.select(dto.getStart_time(), dto.getEnd_time(), processStep);
    }

    @Override
    public AiMixingRealtimeDTO getLatestAiMixingRealtimeValue(int processStep) {
        return aiMixingRealtime.select(processStep);
    }

    @Override
    public int delAiMixingRealtimeValue(Date updateTime, int processStep) {
        return aiMixingRealtime.delete(updateTime, processStep);
    }

    ////////////////////////////////////////////
    // ai_mixing_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiMixingControl(AiProcessControlDTO dto, int processStep) {
        return aiMixingControl.select(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(), processStep);
    }

    @Override
    public List<AiProcessControlDTO> getListAiMixingControl(AiProcessControlDTO dto) {
        return aiMixingControl.select(dto.getRnti(), dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public int modAiMixingControlKafkaFlag(AiProcessControlDTO dto) {
        return aiMixingControl.updateKafkaFlag(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public int delAiMixingControl(Date date, int processStep) {
        return aiMixingControl.delete(date, processStep);
    }

    ////////////////////////////////////////////
    // ai_mixing_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiMixingInit(int processStep) {
        return aiMixingInit.select(processStep);
    }

    @Override
    public AiProcessInitDTO getAiMixingInit(String item, int processStep) {
        return aiMixingInit.select(item, processStep);
    }

    @Override
    public int modAiMixingOperationMode(int operationMode, int processStep) {
        return aiMixingInit.updateOperationMode(operationMode, processStep);
    }

    @Override
    public int modAiMixingInit(String item, float value, int processStep) {
        return aiMixingInit.update(item, value, processStep);
    }

    ////////////////////////////////////////////
    // ai_mixing_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiMixingAlarm(AiProcessAlarmDTO dto) {
        return aiMixingAlarm.insert(dto.getAlm_id(), dto.getAlm_ti(), dto.getKfk_flg());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiMixingAlarm(Date alarm_time, int kafka_flag, int processStep) {
        return aiMixingAlarm.select(alarm_time, kafka_flag, processStep);
    }

    @Override
    public int modAiMixingAlarmKafkaFlag(AiProcessAlarmDTO dto) {
        return aiMixingAlarm.updateKafkaFlag(dto);
    }

    @Override
    public int delAiMixingAlarm(Date date) {
        return aiMixingAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // sedimentation_realtime
    ////////////////////////////////////////////
    @Override
    public int addSedimentationRealtimeValue(List<ProcessRealtimeDTO> dtos) {
        return sedimentationRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestSedimentationRealtimeValue(String partitionName, int processStep) {
        return sedimentationRealtime.select(partitionName, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllSedimentationRealtimeValueFromTime(Date startTime, int processStep) {
        return sedimentationRealtime.select(startTime, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getSedimentationRealtimeValueFromTag(String name, Date startTime, Date endTime,
            int processStep) {
        return sedimentationRealtime.select(name, startTime, endTime, processStep);
    }

    @Override
    public ProcessRealtimeDTO getLatestSedimentationRealtimeValueFromTag(String name, int processStep) {
        return sedimentationRealtime.selectLatest(name, processStep);
    }

    @Override
    public void addSedimentationRealtimePartition(String partitionName, String endTime) {
        sedimentationRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delSedimentationRealtimePartition(String partitionName) {
        sedimentationRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_sedimentation_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiSedimentationRealtimeDTO> getAiSedimentationRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto,
            int processStep) {
        return aiSedimentationRealtime.select(dto.getStart_time(), dto.getEnd_time(), processStep);
    }

    @Override
    public List<FrequencyDTO> getDistributionE1Tb(Date startTime, int processStep) {
        return aiSedimentationRealtime.selectE1Tb(startTime, processStep);
    }

    @Override
    public List<FrequencyDTO> getDistributionE2Tb(Date startTime, int processStep) {
        return aiSedimentationRealtime.selectE2Tb(startTime, processStep);
    }

    @Override
    public List<FrequencyDTO> getDistribution(Date startTime, String name, int processStep) {
        return aiSedimentationRealtime.selectDistribution(startTime, name, processStep);
    }

    @Override
    public List<AiSedimentationInterfaceRealtimeDTO> getAiSedimentationInterfaceRealtimeValueFromUpdateTime(
            InterfaceDateSearchDTO dto, int processStep) {
        return aiSedimentationRealtime.selectInterface(dto.getStart_time(), dto.getEnd_time(), processStep);
    }

    @Override
    public AiSedimentationRealtimeDTO getLatestAiSedimentationRealtimeValue(int processStep) {
        return aiSedimentationRealtime.select(processStep);
    }

    @Override
    public int delAiSedimentationRealtimeValue(Date updateTime, int processStep) {
        return aiSedimentationRealtime.delete(updateTime, processStep);
    }

    ////////////////////////////////////////////
    // ai_sedimentation_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiSedimentationControl(AiProcessControlDTO dto) {
        return aiSedimentationControl.select(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public List<AiProcessControlDTO> getListAiSedimentationControl(AiProcessControlDTO dto) {
        return aiSedimentationControl.select(dto.getRnti(), dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public int modAiSedimentationControlKafkaFlag(AiProcessControlDTO dto) {
        return aiSedimentationControl.updateKafkaFlag(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public int delAiSedimentationControl(Date date, int processStep) {
        return aiSedimentationControl.delete(date, processStep);
    }

    ////////////////////////////////////////////
    // ai_sedimentation_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiSedimentationInit(int processStep) {
        return aiSedimentationInit.select(processStep);
    }

    @Override
    public AiProcessInitDTO getAiSedimentationInit(String item, int processStep) {
        return aiSedimentationInit.select(item, processStep);
    }

    @Override
    public int modAiSedimentationOperationMode(int operationMode, int processStep) {
        return aiSedimentationInit.updateOperationMode(operationMode, processStep);
    }

    @Override
    public int modAiSedimentationInit(String item, float value, int processStep) {
        return aiSedimentationInit.update(item, value, processStep);
    }

    ////////////////////////////////////////////
    // ai_sedimentation_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiSedimentationAlarm(AiProcessAlarmDTO dto) {
        return aiSedimentationAlarm.insert(dto.getAlm_id(), dto.getAlm_ti(), dto.getKfk_flg());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiSedimentationAlarm(Date alarm_time, int kafka_flag, int processStep) {
        return aiSedimentationAlarm.select(alarm_time, kafka_flag, processStep);
    }

    @Override
    public int modAiSedimentationAlarmKafkaFlag(AiProcessAlarmDTO dto) {
        return aiSedimentationAlarm.updateKafkaFlag(dto);
    }

    @Override
    public int delAiSedimentationAlarm(Date date) {
        return aiSedimentationAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // filter_realtime
    ////////////////////////////////////////////
    @Override
    public int addFilterRealtimeValue(List<ProcessRealtimeDTO> dtos) {
        return filterRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestFilterRealtimeValue(String partitionName, int processStep) {
        return filterRealtime.select(partitionName, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllFilterRealtimeValueFromTime(Date startTime, int processStep) {
        return filterRealtime.select(startTime, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getFilterRealtimeValueFromTag(String name, Date startTime, Date endTime,
            int processStep) {
        return filterRealtime.select(name, startTime, endTime, processStep);
    }

    @Override
    public ProcessRealtimeDTO getLatestFilterRealtimeValueFromTag(String name, int processStep) {
        return filterRealtime.selectLatest(name, processStep);
    }

    @Override
    public void addFilterRealtimePartition(String partitionName, String endTime) {
        filterRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delFilterRealtimePartition(String partitionName) {
        filterRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_filter_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiFilterRealtimeDTO> getAiFilterRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto) {
        return aiFilterRealtime.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AiFilterRealtimeDTO getLatestAiFilterRealtimeValue() {
        return aiFilterRealtime.select();
    }

    @Override
    public int delAiFilterRealtimeValue(Date updateTime) {
        return aiFilterRealtime.delete(updateTime);
    }

    ////////////////////////////////////////////
    // ai_filter_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiFilterControl(AiProcessControlDTO dto) {
        return aiFilterControl.select(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public List<AiProcessControlDTO> getListAiFilterControl(AiProcessControlDTO dto) {
        return aiFilterControl.select(dto.getRnti(), dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public int modAiFilterControlKafkaFlag(AiProcessControlDTO dto) {
        return aiFilterControl.updateKafkaFlag(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public int delAiFilterControl(Date date, int processStep) {
        return aiFilterControl.delete(date, processStep);
    }

    ////////////////////////////////////////////
    // ai_filter_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiFilterInit(int processStep) {
        return aiFilterInit.select(processStep);
    }

    @Override
    public AiProcessInitDTO getAiFilterInit(String item, int processStep) {
        return aiFilterInit.select(item, processStep);
    }

    @Override
    public int modAiFilterOperationMode(int operationMode, int processStep) {
        return aiFilterInit.updateOperationMode(operationMode, processStep);
    }

    @Override
    public int modAiFilterInit(String item, float value, int processStep) {
        return aiFilterInit.update(item, value, processStep);
    }

    @Override
    public int modAiFilterInitTi(float value, String itm) {
        return aiFilterInit.updateTi(value, itm);
    }

    ////////////////////////////////////////////
    // ai_filter_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiFilterAlarm(AiProcessAlarmDTO dto) {
        return aiFilterAlarm.insert(dto.getAlm_id(), dto.getAlm_ti(), dto.getKfk_flg());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiFilterAlarm(Date alarm_time, int kafka_flag, int processStep) {
        return aiFilterAlarm.select(alarm_time, kafka_flag, processStep);
    }

    @Override
    public int modAiFilterAlarmKafkaFlag(AiProcessAlarmDTO dto) {
        return aiFilterAlarm.updateKafkaFlag(dto);
    }

    @Override
    public int delAiFilterAlarm(Date date) {
        return aiFilterAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // gac_realtime
    ////////////////////////////////////////////
    @Override
    public int addGacRealtimeValue(List<ProcessRealtimeDTO> dtos) {
        return gacRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestGacRealtimeValue(String partitionName, int processStep) {
        return gacRealtime.select(partitionName, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllGacRealtimeValueFromTime(Date startTime, int processStep) {
        return gacRealtime.select(startTime, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getGacRealtimeValueFromTag(String name, Date startTime, Date endTime,
            int processStep) {
        return gacRealtime.select(name, startTime, endTime, processStep);
    }

    @Override
    public ProcessRealtimeDTO getLatestGacRealtimeValueFromTag(String name, int processStep) {
        return gacRealtime.selectLatest(name, processStep);
    }

    @Override
    public void addGacRealtimePartition(String partitionName, String endTime) {
        gacRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delGacRealtimePartition(String partitionName) {
        gacRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_gac_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiGacRealtimeDTO> getAiGacRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto) {
        return aiGacRealtime.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AiGacRealtimeDTO getLatestAiGacRealtimeValue() {
        return aiGacRealtime.select();
    }

    @Override
    public int delAiGacRealtimeValue(Date updateTime, int processStep) {
        return aiGacRealtime.delete(updateTime);
    }

    ////////////////////////////////////////////
    // ai_gac_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiGacControl(AiProcessControlDTO dto) {
        return aiGacControl.select(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public List<AiProcessControlDTO> getListAiGacControl(AiProcessControlDTO dto) {
        return aiGacControl.select(dto.getRnti(), dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public int modAiGacControlKafkaFlag(AiProcessControlDTO dto) {
        return aiGacControl.updateKafkaFlag(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public int delAiGacControl(Date date, int processStep) {
        return aiGacControl.delete(date, processStep);
    }

    ////////////////////////////////////////////
    // ai_gac_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiGacInit(int processStep) {
        return aiGacInit.select(processStep);
    }

    @Override
    public AiProcessInitDTO getAiGacInit(String item, int processStep) {
        return aiGacInit.select(item, processStep);
    }

    @Override
    public int modAiGacOperationMode(int operationMode, int processStep) {
        return aiGacInit.updateOperationMode(operationMode, processStep);
    }

    @Override
    public int modAiGacInit(String item, float value, int processStep) {
        return aiGacInit.update(item, value, processStep);
    }

    @Override
    public int modAiGacInitTi(float value) {
        return aiGacInit.updateTi(value);
    }

    ////////////////////////////////////////////
    // disinfection_realtime
    ////////////////////////////////////////////
    @Override
    public int addDisinfectionRealtimeValue(List<ProcessRealtimeDTO> dtos) {
        return disinfectionRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestDisinfectionRealtimeValue(String partitionName, int processStep) {
        return disinfectionRealtime.select(partitionName, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllDisinfectionRealtimeValueFromTime(Date startTime, int processStep) {
        return disinfectionRealtime.select(startTime, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getDisinfectionRealtimeValueFromTag(String name, Date startTime, Date endTime,
            int processStep) {
        return disinfectionRealtime.select(name, startTime, endTime, processStep);
    }

    @Override
    public ProcessRealtimeDTO getLatestDisinfectionRealtimeValueFromTag(String name, int processStep) {
        return disinfectionRealtime.selectLatest(name, processStep);
    }

    @Override
    public void addDisinfectionRealtimePartition(String partitionName, String endTime) {
        disinfectionRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delDisinfectionRealtimePartition(String partitionName) {
        disinfectionRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_disinfection_realtime
    ////////////////////////////////////////////

    @Override
    public List<AiDisinfectionRealtimeDTO> getAiDisinfectionRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto,
            int processStep, int disinfectionIndex) {
        return aiDisinfectionRealtime.select(dto.getStart_time(), dto.getEnd_time(), processStep, disinfectionIndex);
    }

    @Override
    public AiDisinfectionRealtimeDTO getLatestAiDisinfectionRealtimeValue(int processStep, int disinfectionIndex) {
        return aiDisinfectionRealtime.select(processStep, disinfectionIndex);
    }

    @Override
    public int delAiDisinfectionRealtimeValue(Date updateTime, int processStep, int disinfectionIndex) {
        return aiDisinfectionRealtime.delete(updateTime, processStep, disinfectionIndex);
    }

    ////////////////////////////////////////////
    // ai_disinfection_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiPreDisinfectionControl(AiProcessControlDTO dto) {
        return aiPreDisinfectionControl.select(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public AiProcessControlDTO getOneAiPeriDisinfectionControl(AiProcessControlDTO dto) {
        return aiPeriDisinfectionControl.select(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public AiProcessControlDTO getOneAiPostDisinfectionControl(AiProcessControlDTO dto) {
        return aiPostDisinfectionControl.select(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public List<AiProcessControlDTO> getListAiPreDisinfectionControl(AiProcessControlDTO dto) {
        return aiPreDisinfectionControl.select(dto.getRnti(), dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public List<AiProcessControlDTO> getListAiPeriDisinfectionControl(AiProcessControlDTO dto) {
        return aiPeriDisinfectionControl.select(dto.getRnti(), dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public List<AiProcessControlDTO> getListAiPostDisinfectionControl(AiProcessControlDTO dto) {
        return aiPostDisinfectionControl.select(dto.getRnti(), dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public int modAiPreDisinfectionControlKafkaFlag(AiProcessControlDTO dto) {
        return aiPreDisinfectionControl.updateKafkaFlag(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(),
                dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public int modAiPeriDisinfectionControlKafkaFlag(AiProcessControlDTO dto) {
        return aiPeriDisinfectionControl.updateKafkaFlag(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(),
                dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public int modAiPostDisinfectionControlKafkaFlag(AiProcessControlDTO dto) {
        return aiPostDisinfectionControl.updateKafkaFlag(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(),
                dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public int delAiPreDisinfectionControl(Date date, int processStep) {
        return aiPreDisinfectionControl.delete(date, processStep);
    }

    @Override
    public int delAiPeriDisinfectionControl(Date date, int processStep) {
        return aiPeriDisinfectionControl.delete(date, processStep);
    }

    @Override
    public int delAiPostDisinfectionControl(Date date, int processStep) {
        return aiPostDisinfectionControl.delete(date, processStep);
    }

    ////////////////////////////////////////////
    // ai_disinfection_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiDisinfectionInit(int processStep, int disinfectionIndex) {
        return aiDisinfectionInit.select(processStep, disinfectionIndex);
    }

    @Override
    public AiProcessInitDTO getAiDisinfectionInit(String item, int processStep, int disinfectionIndex) {
        return aiDisinfectionInit.select(item, processStep, disinfectionIndex);
    }

    @Override
    public int modAiDisinfectionInit(String item, float value, int processStep, int disinfectionIndex) {
        return aiDisinfectionInit.update(item, value, processStep, disinfectionIndex);
    }
    
    @Override
    public int modAiDisinfectionOperationMode(int operationMode, int processStep, int disinfectionStep) {
        return aiDisinfectionInit.updateOperationMode(operationMode, processStep, disinfectionStep);
    }

    ////////////////////////////////////////////
    // ai_disinfection_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiDisinfectionAlarm(AiProcessAlarmDTO dto) {
        return aiDisinfectionAlarm.insert(dto.getAlm_id(), dto.getAlm_ti(), dto.getKfk_flg());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiDisinfectionAlarm(Date alarm_time, int kafka_flag, int processStep,
            int disinfectionIndex) {
        return aiDisinfectionAlarm.select(alarm_time, kafka_flag, processStep, disinfectionIndex);
    }

    @Override
    public int modAiDisinfectionAlarmKafkaFlag(AiProcessAlarmDTO dto) {
        return aiDisinfectionAlarm.updateKafkaFlag(dto);
    }

    @Override
    public int delAiDisinfectionAlarm(Date date) {
        return aiDisinfectionAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // ozone_realtime
    ////////////////////////////////////////////
    @Override
    public int addOzoneRealtimeValue(List<ProcessRealtimeDTO> dtos) {
        return ozoneRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestOzoneRealtimeValue(String partitionName, int processStep) {
        return ozoneRealtime.select(partitionName, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllOzoneRealtimeValueFromTime(Date startTime, int processStep) {
        return ozoneRealtime.select(startTime, processStep);
    }

    @Override
    public List<ProcessRealtimeDTO> getOzoneRealtimeValueFromTag(String name, Date startTime, Date endTime,
            int processStep) {
        return ozoneRealtime.select(name, startTime, endTime, processStep);
    }

    @Override
    public ProcessRealtimeDTO getLatestOzoneRealtimeValueFromTag(String name, int processStep) {
        return ozoneRealtime.selectLatest(name, processStep);
    }

    @Override
    public void addOzoneRealtimePartition(String partitionName, String endTime) {
        ozoneRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delOzoneRealtimePartition(String partitionName) {
        ozoneRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_ozone_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiOzoneRealtimeDTO> getAiOzoneRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto) {
        return aiOzoneRealtime.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AiOzoneRealtimeDTO getLatestAiOzoneRealtimeValue() {
        return aiOzoneRealtime.select();
    }

    @Override
    public int delAiOzoneRealtimeValue(Date updateTime) {
        return aiOzoneRealtime.delete(updateTime);
    }

    ////////////////////////////////////////////
    // ai_ozone_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiOzoneControl(AiProcessControlDTO dto) {
        return aiOzoneControl.select(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public List<AiProcessControlDTO> getListAiOzoneControl(AiProcessControlDTO dto) {
        return aiOzoneControl.select(dto.getRnti(), dto.getKfk_flg(), dto.getProcessStep());
    }

    @Override
    public int modAiOzoneControlKafkaFlag(AiProcessControlDTO dto) {
        return aiOzoneControl.updateKafkaFlag(dto.getUpd_ti(), dto.getRnti(), dto.getTag_sn(), dto.getKfk_flg(),
                dto.getProcessStep());
    }

    @Override
    public int delAiOzoneControl(Date date, int processStep) {
        return aiOzoneControl.delete(date, processStep);
    }

    ////////////////////////////////////////////
    // ai_ozone_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiOzoneInit(int processStep) {
        return aiOzoneInit.select(processStep);
    }

    @Override
    public AiProcessInitDTO getAiOzoneInit(String item, int processStep) {
        return aiOzoneInit.select(item, processStep);
    }

    @Override
    public int modAiOzoneOperationMode(int operationMode, int processStep) {
        return aiOzoneInit.updateOperationMode(operationMode, processStep);
    }

    @Override
    public int modAiOzoneInit(String item, float value, int processStep) {
        return aiOzoneInit.update(item, value, processStep);
    }

    ////////////////////////////////////////////
    // ems_realtime
    ////////////////////////////////////////////
    @Override
    public int addEmsRealtimeValue(List<ProcessRealtimeDTO> dtos) {
        return emsRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestEmsRealtimeValue() {
        return emsRealtime.select("", 0);
    }
    
    @Override
    public List<AiEmsRealtimeDTO> getLatestAiEmsRealtimeValue() {
        return aiEmsRealtime.select();
    }

    ////////////////////////////////////////////
    // pms_realtime
    ////////////////////////////////////////////
    @Override
    public List<PmsAiDTO> getLatestPmsAiValue() {
        return pmsRealtime.selectAi();
    }

    @Override
    public List<PmsScadaDTO> getLatestPmsScadaValue() {
        return pmsRealtime.selectScada();
    }

    @Override
    public ResponseEntity<String> getMixingLatest(HashMap<String, Object> paramMap) {
        AiProcessInitDTO aiMixingInitDto = new AiProcessInitDTO();
        List<AiProcessInitDTO> aiMixingInitList = new ArrayList<AiProcessInitDTO>();
        AiMixingRealtimeDTO aiMixingRealtimeDto = new AiMixingRealtimeDTO();
        List<ProcessRealtimeDTO> mixingRealtimeList = new ArrayList<ProcessRealtimeDTO>();
        List<TagManageDTO> tagManageList = new ArrayList<TagManageDTO>();
        TagManageRangeDTO mixingRangeDto = new TagManageRangeDTO();
        
        if(paramMap.get("processStep") == null) {
        	String strErrorBody = "{\"reason\":\"missing processStep\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // get ai_mixing_init(d_operation_mode)
        aiMixingInitDto = getAiMixingInit(CommonValue.D_OPERATION_MODE,
                Integer.parseInt(paramMap.get("processStep").toString()));
        log.debug("getAiMixingInit, result:[{}]", aiMixingInit != null ? 1 : 0);

        // get ai_mixing_init
        aiMixingInitList = getAllAiMixingInit(Integer.parseInt(paramMap.get("processStep").toString()));
        log.debug("getAllAiMixingInit, result:[{}]", aiMixingInitList.size());

        // get ai_mixing_realtime
        aiMixingRealtimeDto = getLatestAiMixingRealtimeValue(Integer.parseInt(paramMap.get("processStep").toString()));
        log.debug("getLatestAiMixingRealtimeValue, result:[{}]", aiMixingRealtime != null ? 1 : 0);

        // get mixing_realtime
        mixingRealtimeList = getLatestMixingRealtimeValue(String.valueOf(paramMap.get("strPartitionName")),
                Integer.parseInt(paramMap.get("processStep").toString()));
        log.debug("getLatestMixingRealtimeValue, result:[{}]", mixingRealtimeList.size());

        // get tag_manage
        tagManageList = getTagManageFromCode(CommonValue.PROCESS_MIXING,
                Integer.parseInt(paramMap.get("processStep").toString()));
        log.debug("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_MIXING, tagManageList.size());

        // get location number(지 번호)
        mixingRangeDto = getTagManageRange(CommonValue.PROCESS_MIXING,
                Integer.parseInt(paramMap.get("processStep").toString()));
        log.debug("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_MIXING, mixingRangeDto != null ? 1 : 0);

        int nLocationMin = 0, nLocationMax = 0;
        if (mixingRangeDto != null) {
            nLocationMin = mixingRangeDto.getMin();
            nLocationMax = mixingRangeDto.getMax();
        }

        if (aiMixingRealtimeDto != null) {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiMixingInfo = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp;
            List<String> keyList;

            aiMixingInfo.put("upd_ti", aiMixingRealtimeDto.getUpd_ti()); // update_time
            aiMixingInfo.put("locationMin", nLocationMin);
            aiMixingInfo.put("locationMax", nLocationMax);
            aiMixingInfo.put("d_ki_dv", String.format("%.8f", aiMixingRealtimeDto.getD_ki_dv())); // 동점성 계수
            aiMixingInfo.put("d_anr", aiMixingRealtimeDto.getD_anr()); // 패들면적
            aiMixingInfo.put("d_v", aiMixingRealtimeDto.getD_v()); // 조 체적
            // operation_mode
            aiMixingInfo.put("ai_opr", aiMixingInitDto != null ? aiMixingInitDto.getInit_val().intValue()
                    : aiMixingRealtimeDto.getAi_opr());

            // Realtime data from SCADA
            int bFind = 0;
            for (TagManageDTO tagManage : tagManageList) {
                for (ProcessRealtimeDTO dto : mixingRealtimeList) {
                    if (tagManage.getItm().equalsIgnoreCase("b_te") == true
                            && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 원수 수온
                        aiMixingInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        bFind++;
                        break;
                    } else if (tagManage.getItm().equalsIgnoreCase("d_tb_e") == true
                            && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                        // 침전지 탁도
                        aiMixingInfo.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        bFind++;
                        break;
                    }
                }
                if (bFind == 2) {
                    break;
                }
            }
            Object objectTemp = new Object();
            LinkedHashMap<String, Object> dvalueInfo = new LinkedHashMap<>();

            try {

                // aiMixingInitDto g값 제어여부
                aiMixingInfo.put("d_g_value_ctr_flag", aiMixingInitList.stream()
                        .filter(target -> "d_g_value_ctr_flag".equals(target.getItm())).findAny().get().getInit_val());

                // G 값
                for (AiProcessInitDTO dto : aiMixingInitList) {
                    if (dto.getItm().equalsIgnoreCase("d_g_value_step1") == true) { // 응집기 1단
                        dvalueInfo.put("d_g_value1", dto.getInit_val());
                    } else if (dto.getItm().equalsIgnoreCase("d_g_value_step2") == true) { // 응집기 2단
                        dvalueInfo.put("d_g_value2", dto.getInit_val());
                    } else if (dto.getItm().equalsIgnoreCase("d_g_value_step3") == true) { // 응집기 3단
                        dvalueInfo.put("d_g_value3", dto.getInit_val());
                    } else if (dto.getItm().equalsIgnoreCase("d_g_step1_min")) { // 1열 하한
                        aiMixingInfo.put(dto.getItm(), dto.getInit_val());
                    } else if (dto.getItm().equalsIgnoreCase("d_g_step1_max")) { // 1열 상한
                        aiMixingInfo.put(dto.getItm(), dto.getInit_val());
                    } else if (dto.getItm().equalsIgnoreCase("d_g_step2_min")) { // 2열 하한
                        aiMixingInfo.put(dto.getItm(), dto.getInit_val());
                    } else if (dto.getItm().equalsIgnoreCase("d_g_step2_max")) { // 2열 상한
                        aiMixingInfo.put(dto.getItm(), dto.getInit_val());
                    } else if (dto.getItm().equalsIgnoreCase("d_g_step3_min")) { // 3열 하한
                        aiMixingInfo.put(dto.getItm(), dto.getInit_val());
                    } else if (dto.getItm().equalsIgnoreCase("d_g_step3_max")) { // 3열 상한
                        aiMixingInfo.put(dto.getItm(), dto.getInit_val());
                    } else if (dto.getItm().equalsIgnoreCase("d_g_step1_crt")) { // 1열 보정값
                        aiMixingInfo.put(dto.getItm(), dto.getInit_val());
                    } else if (dto.getItm().equalsIgnoreCase("d_g_step2_crt")) { // 2열 보정값
                        aiMixingInfo.put(dto.getItm(), dto.getInit_val());
                    } else if (dto.getItm().equalsIgnoreCase("d_g_step3_crt")) { // 3열 보정값
                        aiMixingInfo.put(dto.getItm(), dto.getInit_val());
                    }
                }

                if (Float.valueOf(aiMixingInfo.get("d_g_value_ctr_flag").toString()) == 0) { // 수동
                    for (String key : dvalueInfo.keySet()) {
                        aiMixingInfo.put(key, dvalueInfo.get(key));
                    }
                } else {
                    // G 값
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiMixingRealtimeDto.getD_g(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    for (String key : keyList) {
                        aiMixingInfo.put("d_g_".concat("value").concat(String.valueOf(key).replaceAll("[^0-9]", "")),
                                objectMapper.convertValue(mapTemp.get(key), Float.class));
                    }
                }
                // 물의 밀도
                aiMixingInfo.put("d_de", aiMixingRealtimeDto.getD_de());
                // 물의 점성계수
                aiMixingInfo.put("d_dv", aiMixingRealtimeDto.getD_dv());

                // 임펠러 직경
                // 1 ~ 4지의 데이터를 모두 담아서 내려주기
                if (aiMixingRealtimeDto.getD_im_d() != null) {
                    mapTemp = objectMapper.readValue(aiMixingRealtimeDto.getD_im_d(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    for (String key : keyList) {
                        aiMixingInfo.put("d_im_d" + key, objectMapper.convertValue(mapTemp.get(key), Float.class));
                    }
                }

                // Power Number
                // 1 ~ 4지의 데이터를 모두 담아서 내려주기
                if (aiMixingRealtimeDto.getD_pw() != null) {
                    mapTemp = objectMapper.readValue(aiMixingRealtimeDto.getD_pw(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    for (String key : keyList) {
                        aiMixingInfo.put("d_pw" + key, objectMapper.convertValue(mapTemp.get(key), Float.class));
                    }
                }

                // 지별 응집기 상태, 응집기 속도
                for (int i = nLocationMin; i <= nLocationMax; i++) {
                    LinkedHashMap<String, Object> locationStateMap = new LinkedHashMap<>();
                    LinkedHashMap<String, Object> locationSpMap = new LinkedHashMap<>();

                    for (int j = 1; j <= 3; j++) {
                        HashMap<String, Object> stepStateMap = new HashMap<>();
                        HashMap<String, Object> stepSpMap = new HashMap<>();

                        for (int k = 1; k <= 3; k++) {
                            String strStateName = "d_fc_on" + i + "_" + j + "_" + k;
                            String strSpName = "d_fc_sp" + i + "_" + j + "_" + k;
                            for (TagManageDTO tagManage : tagManageList) {
                                for (ProcessRealtimeDTO dto : mixingRealtimeList) {
                                    if (tagManage.getItm().equalsIgnoreCase(strStateName) == true
                                            && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                                        stepStateMap.put(String.format("%d", k), Float.parseFloat(dto.getTag_val()));
                                    } else if (tagManage.getItm().equalsIgnoreCase(strSpName) == true
                                            && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                                        stepSpMap.put(String.format("%d", k), Float.parseFloat(dto.getTag_val()));
                                    }
                                }
                            }
                        }

                        locationStateMap.put("step" + j, stepStateMap);
                        locationSpMap.put("step" + j, stepSpMap);
                    }
                    aiMixingInfo.put("d_loc_fc_stt" + i, locationStateMap);
                    aiMixingInfo.put("d_loc_fc_sp" + i, locationSpMap);
                }

                // AI 지별 응집기 속도 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiMixingRealtimeDto.getAi_d_loc_fc_sp(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                for (String key : keyList) {
                    LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(key),
                            LinkedHashMap.class);
                    aiMixingInfo.put(key.replaceAll("location", "ai_d_loc_fc_sp"), locationMapTemp);
                }

            } catch (JsonProcessingException e) {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiMixingInfo);
            responseBody.put("d_value", dvalueInfo);
            String strBody = "";
            try {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            } catch (JsonProcessingException e) {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        } else {
            String strErrorBody = "{\"reason\":\"Empty ai_mixing\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    public LinkedHashMap<String, Object> getAiopr() {
        LinkedHashMap<String, Object> aiopr = new LinkedHashMap<String, Object>();

        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            HashMap<String, Object> processMap = new HashMap<String, Object>();

            processMap.put("receiving", getAiReceivingInit(CommonValue.B_OPERATION_MODE, processStep).getInit_val()); // 착수
            processMap.put("coagulants", getAiCoagulantInit(CommonValue.C_OPERATION_MODE, processStep).getInit_val()); // 약품
            processMap.put("mixing", getAiMixingInit(CommonValue.D_OPERATION_MODE, processStep).getInit_val()); // 혼화응집
            processMap.put("sedimentation",
                    getAiSedimentationInit(CommonValue.E_OPERATION_MODE, processStep).getInit_val()); // 침전
            processMap.put("filter", getAiFilterInit(CommonValue.F_OPERATION_MODE, processStep).getInit_val()); // 여과
            processMap.put("disinfectionPre", getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE, processStep,
                    CommonValue.DISINFECTION_PRE_STEP).getInit_val()); // 소독 전차염
            if (processStep == 2) {
                processMap.put("disinfectionPost", getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE, processStep,
                        CommonValue.DISINFECTION_POST_STEP).getInit_val()); // 소독 후차염
            }
            getLatest(processMap, processStep);
            aiopr.put("processStep" + processStep, processMap);
        }
        return aiopr;
    }

    public void getLatest(HashMap<String, Object> processMap, int processStep) {
        LinkedHashMap<String, Object> mapTemp;
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedHashMap<String, Object> aiInfoMap = new LinkedHashMap<>();
        ArrayList<String> keyList = new ArrayList<String>();
        Object objectTemp = new Object();

        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");

        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());

        try {
            /* 착수 */
            List<ProcessRealtimeDTO> receivingRealtime = getLatestReceivingRealtimeValue(strPartitionName, processStep);
            List<TagManageDTO> tagManageList = getTagManageFromCode(CommonValue.PROCESS_RECEIVING, processStep);
            AiReceivingRealtimeDTO aiReceivingRealtime = getLatestAiReceivingRealtimeValue(processStep);

            if (aiReceivingRealtime != null) {
                for (TagManageDTO tagManage : tagManageList) {
                    for (ProcessRealtimeDTO dto : receivingRealtime) {
                        if (tagManage.getItm().equalsIgnoreCase("b_in_fr") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 원수 유입 유량 순시
                            aiInfoMap.put("b_in_fr_i", Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("b_in_pr") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 원수 유입 압력 1
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("h_location_le1") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 정수지 #1 수위
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("h_location_le2") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 정수지 #2 수위
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("h_out_fr") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 정수지 유출 유량
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        }
                    }
                }

                if (processStep == 2) {
                    mapTemp = objectMapper.readValue(aiReceivingRealtime.getAi_b_vv_po(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    JsonBSeriesInt ai_b_vv_po = objectMapper.convertValue(objectTemp, JsonBSeriesInt.class);
                    aiInfoMap.put("ai_b1_vv_po", ai_b_vv_po.getSeries1());
                    aiInfoMap.put("ai_b2_vv_po", ai_b_vv_po.getSeries2());
                } else {
                    aiInfoMap.put("ai_b1_vv_po", aiReceivingRealtime.getAi_b_vv_po());
                }
            }
            processMap.put("receivingLatest", aiInfoMap);

            /* 약품 */
            List<ProcessRealtimeDTO> coagulantRealtime = getLatestCoagulantRealtimeValue(strPartitionName, processStep);
            AiCoagulantRealtimeDTO aiCoagulantRealtime = getLatestAiCoagulantRealtimeValue(processStep);
            Map<String, Object> c_cf_coagulant = aiCoagulantControl.selectUsrMng("c" + processStep + "_cf_coagulant");

            tagManageList = getTagManageFromCode(CommonValue.PROCESS_COAGULANT, processStep);
            aiInfoMap = new LinkedHashMap<>();

            if (aiCoagulantRealtime != null) {
                for (TagManageDTO tagManage : tagManageList) {
                    for (ProcessRealtimeDTO dto : coagulantRealtime) {
                        if (tagManage.getItm().equalsIgnoreCase("b_tb") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 원수 탁도
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("b_ph") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 원수 ph
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("b_te") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 원수 수온
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("b_cu") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 원수 전기전도도
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("b_in_fr") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 원수 유입 유량
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        }
                    }
                }
                aiInfoMap.put("ai_c_cf", aiCoagulantRealtime.getAi_c_cf()); // 주입률
                aiInfoMap.put("c_cf_coagulant", c_cf_coagulant.get("init_val")); // 약품종류
            }
            processMap.put("coagulantLatest", aiInfoMap);

            /* 혼화응집 */
            AiMixingRealtimeDTO aiMixingRealtimeDto = getLatestAiMixingRealtimeValue(processStep);

            if (aiMixingRealtimeDto != null) {
                List<ProcessRealtimeDTO> mixingRealtimeList = getLatestMixingRealtimeValue(strPartitionName,
                        processStep);
                aiInfoMap = new LinkedHashMap<>();

                mapTemp = objectMapper.readValue(aiMixingRealtimeDto.getAi_d_loc_fc_sp(), LinkedHashMap.class);

                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                for (String key : keyList) {
                    LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(key),
                            LinkedHashMap.class);
                    aiInfoMap.put(key.replaceAll("location", "ai_d_loc_fc_sp"), locationMapTemp);
                }

                List<AiProcessInitDTO> aiMixingInitList = getAllAiMixingInit(processStep);
                Float d_g_value_ctr_flag = aiMixingInitList.stream()
                        .filter(target -> "d_g_value_ctr_flag".equals(target.getItm())).findAny().get().getInit_val();

                if (d_g_value_ctr_flag == 0) { // 수동모드
                    for (AiProcessInitDTO dto : aiMixingInitList) {
                        if (dto.getItm().equalsIgnoreCase("d_g_value_step1") == true) {
                            // 응집기 1단
                            aiInfoMap.put("d_g_value1", dto.getInit_val());
                        } else if (dto.getItm().equalsIgnoreCase("d_g_value_step2") == true) {
                            // 응집기 2단
                            aiInfoMap.put("d_g_value2", dto.getInit_val());
                        } else if (dto.getItm().equalsIgnoreCase("d_g_value_step3") == true) {
                            // 응집기 3단
                            aiInfoMap.put("d_g_value3", dto.getInit_val());
                        }
                    }
                } else {
                    // G 값
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiMixingRealtimeDto.getD_g(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    for (String key : keyList) {
                        aiInfoMap.put("d_g_".concat("value").concat(String.valueOf(key).replaceAll("[^0-9]", "")),
                                objectMapper.convertValue(mapTemp.get(key), Float.class));
                    }
                }

                // 물의 밀도
                aiInfoMap.put("d_de", aiMixingRealtimeDto.getD_de());
                // 물의 점성계수
                aiInfoMap.put("d_dv", aiMixingRealtimeDto.getD_dv());
                aiInfoMap.put("d_ki_dv", String.format("%.8f", aiMixingRealtimeDto.getD_ki_dv())); // 동점성 계수
                aiInfoMap.put("d_anr", aiMixingRealtimeDto.getD_anr()); // 패들면적
                aiInfoMap.put("d_v", aiMixingRealtimeDto.getD_v()); // 조 체적

                // 임펠러 직경
                // 1 ~ 4지의 데이터를 모두 담아서 내려주기
                if (aiMixingRealtimeDto.getD_im_d() != null) {
                    mapTemp = objectMapper.readValue(aiMixingRealtimeDto.getD_im_d(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    for (String key : keyList) {
                        aiInfoMap.put("d_im_d" + key, objectMapper.convertValue(mapTemp.get(key), Float.class));
                    }
                }

                // Power Number
                // 1 ~ 4지의 데이터를 모두 담아서 내려주기
                if (aiMixingRealtimeDto.getD_pw() != null) {
                    mapTemp = objectMapper.readValue(aiMixingRealtimeDto.getD_pw(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    for (String key : keyList) {
                        aiInfoMap.put("d_pw" + key, objectMapper.convertValue(mapTemp.get(key), Float.class));
                    }
                }

                int bFind = 0;
                for (TagManageDTO tagManage : tagManageList) {
                    for (ProcessRealtimeDTO dto : mixingRealtimeList) {
                        if (tagManage.getItm().equalsIgnoreCase("b_te") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 원수 수온
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                            bFind++;
                            break;
                        } else if (tagManage.getItm().equalsIgnoreCase("d_tb_e") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 침전지 탁도
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                            bFind++;
                            break;
                        }
                    }
                    if (bFind == 2) {
                        break;
                    }
                }
                processMap.put("mixingLatest", aiInfoMap);
            }

            // 침전
            aiInfoMap = new LinkedHashMap<>();
            AiSedimentationRealtimeDTO aiSedimentationRealtime = getLatestAiSedimentationRealtimeValue(processStep);
            if (aiSedimentationRealtime != null) {
                aiInfoMap.put("ai_e1_sludge", aiSedimentationRealtime.getAIE_5300());
                tagManageList = getTagManageFromCode(CommonValue.PROCESS_SEDIMENTATION, processStep);
                List<ProcessRealtimeDTO> sedimentationRealtime = getLatestSedimentationRealtimeValue(strPartitionName,
                        processStep);
                Float mm_fr = 0.0f;
                // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiSedimenationInfo에 등록
                for (TagManageDTO tagManage : tagManageList) {
                    for (ProcessRealtimeDTO dto : sedimentationRealtime) {
                        if (tagManage.getItm().equalsIgnoreCase("b_in_fr") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 원수 유입 유량
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("b_tb") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 원수 탁도
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("mm_fr") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // PACS 주입량
                            mm_fr = Float.parseFloat(dto.getTag_val());
                        } else if (tagManage.getItm().equalsIgnoreCase("c_mm_fr_etc") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 폴리아민 주입량
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        } else if (tagManage.getItm().equalsIgnoreCase("c_mm_fr_etc1") == true
                                && tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                            // 활성탄 주입률
                            aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                        }
                    }
                }
                aiInfoMap.put("c_cf_coagulant", selectUsrMng("c" + processStep + "_cf_coagulant").get("init_val"));// 약품종류
                // 약품 사용량 유량
                aiInfoMap.put("mm_fr", mm_fr);
            }
            processMap.put("sedimentationLatest", aiInfoMap);
            /* 소독 */
            AiDisinfectionRealtimeDTO aiDisinfectionRealtime = getLatestAiDisinfectionRealtimeValue(processStep,
                    CommonValue.DISINFECTION_PRE_STEP); // 전차염
            if (aiDisinfectionRealtime != null) {
                aiInfoMap = new LinkedHashMap<>();
                aiInfoMap.put("ai_g_chol_rate", aiDisinfectionRealtime.getAi_g_chol_rate());
                aiInfoMap.put("g_pre_chol_rate", aiDisinfectionRealtime.getG_pre_chol_rate()); // 현재 주입률
                aiInfoMap.put("g_tei", aiDisinfectionRealtime.getG_tei()); // 기온

                if (processStep == 1) {
                    tagManageList = getTagManageFromCode(CommonValue.PROCESS_DISINFECTION, processStep);
                    List<ProcessRealtimeDTO> disinfectionRealtime = getLatestDisinfectionRealtimeValue(strPartitionName,
                            processStep);
                    for (TagManageDTO tagManage : tagManageList) {
                        for (ProcessRealtimeDTO dto : disinfectionRealtime) {
                            if (tagManage.getItm().equalsIgnoreCase("b_te") == true &&
                                    tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                                // 원수 수온
                                aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                                break;
                            } else if (tagManage.getItm().equalsIgnoreCase("b_in_fr") == true &&
                                    tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                                // 원수 유입 유량
                                aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                                break;
                            } else if (tagManage.getItm().equalsIgnoreCase("h_ph") == true &&
                                    tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                                // 정수 탁도
                                aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                                break;
                            } else if (tagManage.getItm().equalsIgnoreCase("h_tb") == true &&
                                    tagManage.getTag_sn().equalsIgnoreCase(dto.getTag_sn()) == true) {
                                // 정수 pH
                                aiInfoMap.put(tagManage.getItm(), Float.parseFloat(dto.getTag_val()));
                                break;
                            }
                        }
                    }
                }
                processMap.put("disinfectionPreLatest", aiInfoMap);
            }

            if (processStep == 2) { // 후차염
                aiDisinfectionRealtime = getLatestAiDisinfectionRealtimeValue(processStep,
                        CommonValue.DISINFECTION_POST_STEP);
                aiInfoMap = new LinkedHashMap<>();
                aiInfoMap.put("ai_g_chol_rate", aiDisinfectionRealtime.getAi_g_chol_rate());
                processMap.put("disinfectionPostLatest", aiInfoMap);
            }

        } catch (JsonMappingException e) {
            log.error("JsonMappingException Occurred in Alarm Receiving Control Process");
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException Occurred in Alarm Receiving Control Process");
        }
    }

    @Override
    public int modUsrMng(String item, String value) {
        return aiCoagulantControl.update(item, value);
    }

    @Override
    public Map<String, Object> selectUsrMng(String item) {
        return aiCoagulantControl.selectUsrMng(item);
    }
    
    @Override
    public List<InterfaceAlarmControlHistoryDTO> getCtrHisBySeqAndTag(int almSeq, String tagSn) {
    	return alarmNotify.selectBySeqAndTag(almSeq, tagSn);
    }

    @Override
    public void insertAlarmControlHistory(InterfaceAlarmControlHistoryDTO alarmControl) {
        alarmNotify.insertAlarmControlHistory(alarmControl);
    }

    @Override
    public List<InterfaceAlarmControlHistoryDTO> getAlarmControlHistory(InterfaceAlarmControlHistoryDTO dto) {
        return alarmNotify.selectAlarmControlHistory(dto);
    }
    
    @Override
    public List<InterfaceAlarmControlHistoryDTO> getTagInfoList(InterfaceAlarmControlHistoryDTO dto) {
        return alarmNotify.selectTagInfoList(dto);
    }

    @Override
    public List<InterfaceAlarmControlHistoryDTO> getAlarmControlHistoryDetail(InterfaceAlarmControlHistoryDTO dto) {
        return alarmNotify.selectAlarmControlHistoryDetail(dto);
    }

    @Override
    public InterfaceAlarmControlHistoryDTO getAlarmExceeded(InterfaceAlarmControlHistoryDTO queryDto) {
        return alarmNotify.getAlarmExceeded(queryDto);
    }

    public String selectTagDp(String tagSn) {
        return tagManage.select(tagSn);
    }

    public List<InterfaceAlarmControlHistoryDTO> getAllRealTime(LocalDateTime date) {
        return mixingRealtime.selectAllRealTime(date);
    }

    @Override
    public int modUsrMngFromItm(String item, String value) {
        return aiCoagulantControl.update(item, value);
    }

    public List<InterfaceAlarmControlHistoryDTO> getBeforeAlarmNotify(InterfaceAlarmControlHistoryDTO dto) {
        return alarmNotify.getBeforeAlarmNotify(dto);
    }
    
    @Override
    public AiFactorDTO getAiFactorData(AiFactorDTO dto) {
    	return aiFactor.select(dto);
    }
    
    @Override
    public List<AiOprRealTimeDTO> getAllAiOprRealTime() {
        return aiOprRealTime.select();
    }
    
    @Override
    public int modAiOprRealTime(String process, String disinfectionStep, int value, int processStep) {
        return aiOprRealTime.update(process, disinfectionStep, value, processStep);
    }
    
    @Override
    public int modAllAiOprRealTime() {
        return aiOprRealTime.updateAll();
    }
    
    @Override
    public int addAiOprHistoryList(List<AiOprHistoryDTO> aiOprHistoryList) {
    	return aiOprHistory.insert(aiOprHistoryList);
    }
    
    @Override
    public List<AiOprHistoryDTO> getAllAiOprHistory() {
        return aiOprHistory.select();
    }
    
    @Override
    public List<AiOprHistoryDTO> getAiOprHistoryBySearchDate(InterfaceDateSearchDTO dto) {
        return aiOprHistory.select(dto);
    }
}
