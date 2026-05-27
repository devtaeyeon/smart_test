package co.irexnet.waio.WAIO_ServerAgent.service;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.ResponseEntity;

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
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.UsrMngDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiFactorDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmNotifyDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlmCtrHisCntDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlmCtrHisDTO;
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

public interface IDatabaseService
{
    ////////////////////////////////////////////
    // access_token
    ////////////////////////////////////////////
    int addToken(AccessTokenDTO dto);
    List<AccessTokenDTO> getAllTokens();
    AccessTokenDTO getToken(String token);
    int modToken(String token, Date expiration);
    int delToken(String token);
    int delToken(Date expiration);

    ////////////////////////////////////////////
    // user
    ////////////////////////////////////////////
    int addUser(UserDTO dto) throws NoSuchAlgorithmException;
    UserDTO getUser(String userid, String usr_pw) throws NoSuchAlgorithmException;
    UserDTO getUserFromUserid(String userid);
    List<UserDTO> getAllUser();
    int modUser(int usr_auth, UserDTO dto);
    int modUserMyInfo(int usr_auth, UserDTO dto);
    int modUserPw(String userid, String usr_pw) throws NoSuchAlgorithmException;
    int delUser(String userid);

    ////////////////////////////////////////////
    // login_history
    ////////////////////////////////////////////
    int addLoginHistory(LoginHistoryDTO dto);
    List<LoginHistoryDTO> getAllLoginHistory();
    int delLoginHistory(Date date);

    ////////////////////////////////////////////
    // disk_info
    ////////////////////////////////////////////
    int addDiskInfo(DiskInfoDTO dto);
    List<DiskInfoDTO> getDiskInfoFromHostname(String hostname);
    int modDiskInfo(DiskInfoDTO dto);

    ////////////////////////////////////////////
    // partition_info
    ////////////////////////////////////////////
    int addPartitionInfo(PartitionInfoDTO dto);
    List<PartitionInfoDTO> getPartitionInfoFromHostname(String hostname);
    int modPartitionInfo(PartitionInfoDTO dto);

    ////////////////////////////////////////////
    // interface_info
    ////////////////////////////////////////////
    int addInterfaceInfo(InterfaceInfoDTO dto);
    List<InterfaceInfoDTO> getInterfaceInfoFromHostname(String hostname);
    List<InterfaceInfoDTO> getInterfaceInfoFromAddress(String address);
    int modInterfaceInfo(InterfaceInfoDTO dto);

    ////////////////////////////////////////////
    // system_config
    ////////////////////////////////////////////
    SystemConfigDTO getSystemConfig();
    int modSystemConfig(SystemConfigDTO dto);

    ////////////////////////////////////////////
    // system_info
    ////////////////////////////////////////////
    int addSystemInfo(SystemInfoDTO dto);
    SystemInfoDTO getSystemInfoFromHostname(String hostname);
    List<SystemInfoDTO> getAllSystemInfo();
    int modSystemInfo(SystemInfoDTO dto);
    int modSystemInfoName(String hostname, String name);

    ////////////////////////////////////////////
    // system_monitoring
    ////////////////////////////////////////////
    int addSystemMonitoring(SystemMonitoringDTO dto);
    List<SystemMonitoringDTO> getSystemMonitoringFromHostname(String hostname);
    List<SystemMonitoringDTO> getAllSystemMonitoring();
    List<SystemMonitoringDTO> getLatestSystemMonitoring(Date startDate);
    int delSystemMonitoring(Date date);

    ////////////////////////////////////////////
    // sensor
    ////////////////////////////////////////////
    int addSensor(SensorDTO dto);
    List<SensorDTO> getSensor(Date startDate);
    SensorDTO getLatestSensor();
    List<CoagulantsDTO> getHistorySensor();
    CoagulantsDTO getLatestCoagulants();
    List<TrendTbDTO> getTrendTb(InterfaceDateSearchDTO dto);
    List<TrendCodeDTO> getTrendCode(String code, InterfaceDateSearchDTO dto);
    List<FrequencyDTO> getDistinctCountHsE1Tb(Date startDate);
    List<FrequencyDTO> getDistinctCountHsE2Tb(Date startDate);
    List<FrequencyDTO> getDistinctCountHsFTb(Date startDate);
    int delSensor(Date updateTime);

    ////////////////////////////////////////////
    // diatom
    ////////////////////////////////////////////
    int addDiatom(DiatomDTO dto);
    List<DiatomDTO> getDiatom();
    DiatomDTO getLatestDiatom();
    int modDiatom(DiatomDTO dto);
    int delDiatom(int diatomIndex);

    ////////////////////////////////////////////
    // alarm_info
    ////////////////////////////////////////////
    int addAlarmInfo(AlarmInfoDTO dto);
    List<AlarmInfoDTO> getAlarmInfo();
    int modAlarmInfo(AlarmInfoDTO dto);
    int delAlarmInfo(int alarmId);

    ////////////////////////////////////////////
    // alarm_notify
    ////////////////////////////////////////////
    int addAlarmNotify(AlarmNotifyDTO dto);
    List<AlarmNotifyDTO> getAllAlarmNotify();
    List<AlarmNotifyDTO> getAlarmNotify(Date startTime);
//    List<AlarmNotifyDTO> getAlarmNotifyFromAckState(boolean ackState);    // 알람 네비게이터 삭제
    AlarmNotifyDTO getLatestAlarmNotify(int alarmId, Date alarmDate, String hostname);
    List<AlarmNotifyDTO> getSearchAlarmNotify(InterfaceDateSearchDTO dto);
    int modAlarmNotifyAckState(int alarmNotifyIndex, boolean ackState);

    ////////////////////////////////////////////
    // chemical_info
    ////////////////////////////////////////////
    int addChemicalInfo(ChemicalInfoDTO dto);
    List<ChemicalInfoDTO> getAllChemicalInfo();
    ChemicalInfoDTO getChemicalInfoFromCode(String code);
    int modChemicalInfo(ChemicalInfoDTO dto);
    int delChemicalInfo(String code);

    ////////////////////////////////////////////
    // water_purification_info
    ////////////////////////////////////////////
    int addWaterPurificationInfo(WaterPurificationInfoDTO dto);
    List<WaterPurificationInfoDTO> getAllWaterPurificationInfo();
    WaterPurificationInfoDTO getWaterPurificationInfoFromCode(String code);
    int modWaterPurificationInfo(WaterPurificationInfoDTO dto);
    int delWaterPurificationInfo(String code);

    ////////////////////////////////////////////
    // coagulants_analysis
    ////////////////////////////////////////////
    List<CoagulantsAnalysisDTO> getAllCoagulantsAnalysis();
    CoagulantsAnalysisDTO getCoagulantsAnalysisFromLogTime(Date logTime);
    CoagulantsAnalysisDTO getLatestCoagulantsAnalysis();
    List<CoagulantsAnalysisDTO> get2LatestCoagulantsAnalysis();
    List<CoagulantsAnalysisDTO> getMinuteCoagulantsAnalysis(Date startTime);
    int addRawWaterClassInfo(ClassInfoDTO dto);
    List<ClassInfoDTO> getAllRawWaterClassInfo();
    int modRawWaterClassInfo(ClassInfoDTO dto);
    int delRawWaterClassInfo(int classIndex);
    List<ClusterInfoDTO> getAllCoagulantsClusterInfo();

    ////////////////////////////////////////////
    // coagulants_simulation
    ////////////////////////////////////////////
    int addCoagulantsSimulation(CoagulantsSimulationDTO dto);
    List<CoagulantsSimulationDTO> getAllCoagulantsSimulation();
    List<CoagulantsSimulationDTO> getCoagulantsSimulationUpperState(int state);
    List<CoagulantsSimulationDTO> getCoagulantsSimulationLowerState(int state);

    ////////////////////////////////////////////
    // dashboard_info
    ////////////////////////////////////////////
    int addDashboardInfo(String data);
    DashboardIdDTO getLatestDashboardInfo();
    DashboardDataDTO getDashboardInfo(int dashboard_id);
    int modDashboardInfo(int dashboard_id, String data);
    int delDashboardInfo(int dashboard_id);

    ////////////////////////////////////////////
    // tag_description
    ////////////////////////////////////////////
    int addTagDescription(TagDescriptionDTO dto);
    List<TagDescriptionDTO> getAllTagDescription();
    int modTagDescription(TagDescriptionDTO dto);
    int delTagDescription(int tagIndex);

    ////////////////////////////////////////////
    // tag_manage
    ////////////////////////////////////////////
    int addTagManage(TagManageDTO dto);
    List<TagManageDTO> getAllTagManage();
    List<TagManageDTO> getTagManageFromType(int type, String process, int processStep);
    List<TagManageDTO> getTagManageFromCode(String process, int processStep);
    TagManageRangeDTO getTagManageRange(String process, int processStep);
    int modTagManage(TagManageDTO dto);
    int delTagManage(TagManageDTO dto);
    List<String> selectProcCd();

    ////////////////////////////////////////////
    // process_code
    ////////////////////////////////////////////
    List<ProcessCodeDTO> getAllProcessCode();

    ////////////////////////////////////////////
    // load_process_realtime
    ////////////////////////////////////////////
    int addProcessRealtimeValue(String procCd, List<ProcessRealtimeDTO>  dtos);
    void addProcessRealtimePartition(String procCd, List<String> partitionNameList);
    List<String> getAddPartitionList(String procCd);
    void delProcessRealtimePartition(String procCd, List<String> partitionNameList);
    List<String> getDropPartitionList(String procCd, String partitionNm);

    ////////////////////////////////////////////
    // receiving_realtime
    ////////////////////////////////////////////
    int addReceivingRealtimeValue(List<ProcessRealtimeDTO>  dtos);
    List<ProcessRealtimeDTO> getLatestReceivingRealtimeValue(String partitionName, int processStep);
    List<ProcessRealtimeDTO> getAllReceivingRealtimeValueFromTime(Date startTime, int processStep);
    List<ProcessRealtimeDTO> getReceivingRealtimeValueFromTag(String name, Date startTime, Date endTime, int processStep);
    ProcessRealtimeDTO getLatestReceivingRealtimeValueFromTag(String name, int processStep);
    void addReceivingRealtimePartition(String partitionName, String endTime);
    void delReceivingRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_receiving_realtime
    ////////////////////////////////////////////
    List<AiReceivingRealtimeDTO> getAiReceivingRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto, int processStep);
    AiReceivingRealtimeDTO getLatestAiReceivingRealtimeValue(int processStep);
    int delAiReceivingRealtimeValue(Date updateTime, int processStep);
    List<AiClearOperationBandDTO> getAiClearOperationBandFromTimeIndex(InterfaceDateSearchDTO dto, int processStep);
//    List<AiClearOperationBandDTO> getAiClearEmsOperationBandFromTimeIndex(InterfaceDateSearchDTO dto);
//    List<AiClearOperationBandDTO> getAiClearWideOperationBandFromTimeIndex(InterfaceDateSearchDTO dto);

    ////////////////////////////////////////////
    // ai_receiving_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiReceivingControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiReceivingControl(AiProcessControlDTO dto);
    int modAiReceivingControlKafkaFlag(AiProcessControlDTO dto);
    int delAiReceivingControl(Date date, int processStep);

    ////////////////////////////////////////////
    // ai_receiving_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiReceivingInit(int processStep);
    AiProcessInitDTO getAiReceivingInit(String item, int processStep);
    int modAiReceivingOperationMode(int operationMode, int processStep);
    int modAiReceivingInit(String item, float value, int processStep);

    ////////////////////////////////////////////
    // ai_receiving_data
    ////////////////////////////////////////////
    int addAiReceivingDataValue(List<ProcessRealtimeDTO>  dtos);
    int delAiReceivingDataValue(Date updateTime);

    ////////////////////////////////////////////
    // ai_receiving_alarm
    ////////////////////////////////////////////
    int addAiReceivingAlarm(AiProcessAlarmDTO dto, int processStep);
    List<AiProcessAlarmDTO> getAllAiReceivingAlarm(Date alarm_time, int kafka_flag, int processStep);
    int modAiReceivingAlarmKafkaFlag(AiProcessAlarmDTO dto, int processStep);
    int delAiReceivingAlarm(Date date, int processStep);

    ////////////////////////////////////////////
    // coagulant_realtime
    ////////////////////////////////////////////
    int addCoagulantRealtimeValue(List<ProcessRealtimeDTO>  dtos);
    List<ProcessRealtimeDTO> getLatestCoagulantRealtimeValue(String partitionName, int processStep);
    List<ProcessRealtimeDTO> getAllCoagulantRealtimeValueFromTime(Date startTime, int processStep);
    List<ProcessRealtimeDTO> getCoagulantRealtimeValueFromTag(String name, Date startTime, Date endTime, int processStep);
    List<FrequencyDTO> getCoagulantDistribution(Date startTime, String name, int processStep);
    void addCoagulantRealtimePartition(String partitionName, String endTime);
    void delCoagulantRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_coagulant_realtime
    ////////////////////////////////////////////
    List<AiCoagulantRealtimeDTO> getAiCoagulantRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto, int processStep);
    AiCoagulantRealtimeDTO getLatestAiCoagulantRealtimeValue(int processStep);
    int delAiCoagulantRealtimeValue(Date updateTime, int processStep);

    ////////////////////////////////////////////
    // ai_coagulant_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiCoagulantControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiCoagulantControl(AiProcessControlDTO dto);
    int modAiCoagulantControlKafkaFlag(AiProcessControlDTO dto);
    int delAiCoagulantControl(Date date, int processStep);

    ////////////////////////////////////////////
    // ai_coagulant_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiCoagulantInit(int processStep);
    AiProcessInitDTO getAiCoagulantInit(String item, int processStep);
    int modAiCoagulantOperationMode(int operationMode, int processStep);
    int modAiCoagulantInit(String item, float value, int processStep);

    ////////////////////////////////////////////
    // ai_coagulant_alarm
    ////////////////////////////////////////////
    int addAiCoagulantAlarm(AiProcessAlarmDTO dto, int processStep);
    List<AiProcessAlarmDTO> getAllAiCoagulantAlarm(Date alarm_time, int kafka_flag, int processStep);
    int modAiCoagulantAlarmKafkaFlag(AiProcessAlarmDTO dto, int processStep);
    int delAiCoagulantAlarm(Date date, int processStep);

    ////////////////////////////////////////////
    // ai_coagulant_simulation
    ////////////////////////////////////////////
    int addAiCoagulantSimulation(AiCoagulantSimulationDTO dto);
    List<AiCoagulantSimulationDTO> getAiCoagulantSimulation(InterfaceDateSearchDTO dto);

    ////////////////////////////////////////////
    // mixing_realtime
    ////////////////////////////////////////////
    int addMixingRealtimeValue(List<ProcessRealtimeDTO>  dtos);
    List<ProcessRealtimeDTO> getLatestMixingRealtimeValue(String partitionName, int processStep);
    List<ProcessRealtimeDTO> getAllMixingRealtimeValueFromTime(Date startTime, int processStep);
    List<ProcessRealtimeDTO> getMixingRealtimeValueFromTag(String name, Date startTime, Date endTime, int processStep);
    ProcessRealtimeDTO getLatestMixingRealtimeValueFromTag(String name, int processStep);
    void addMixingRealtimePartition(String partitionName, String endTime);
    void delMixingRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_mixing_realtime
    ////////////////////////////////////////////
    List<AiMixingRealtimeDTO> getAiMixingRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto, int processStep);
    AiMixingRealtimeDTO getLatestAiMixingRealtimeValue(int processStep);
    int delAiMixingRealtimeValue(Date updateTime, int processStep);
    ResponseEntity<String> getMixingLatest(HashMap<String, Object> paramMap);

    ////////////////////////////////////////////
    // ai_mixing_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiMixingControl(AiProcessControlDTO dto, int processStep);
    List<AiProcessControlDTO> getListAiMixingControl(AiProcessControlDTO dto);
    int modAiMixingControlKafkaFlag(AiProcessControlDTO dto);
    int delAiMixingControl(Date date, int processStep);

    ////////////////////////////////////////////
    // ai_mixing_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiMixingInit(int processStep);
    AiProcessInitDTO getAiMixingInit(String item, int processStep);
    int modAiMixingOperationMode(int operationMode, int processStep);
    int modAiMixingInit(String item, float value, int processStep);

    ////////////////////////////////////////////
    // ai_mixing_alarm
    ////////////////////////////////////////////
    int addAiMixingAlarm(AiProcessAlarmDTO dto, int processStep);
    List<AiProcessAlarmDTO> getAllAiMixingAlarm(Date alarm_time, int kafka_flag, int processStep);
    int modAiMixingAlarmKafkaFlag(AiProcessAlarmDTO dto, int processStep);
    int delAiMixingAlarm(Date date, int processStep);

    ////////////////////////////////////////////
    // sedimentation_realtime
    ////////////////////////////////////////////
    int addSedimentationRealtimeValue(List<ProcessRealtimeDTO>  dtos);
    List<ProcessRealtimeDTO> getLatestSedimentationRealtimeValue(String partitionName, int processStep);
    List<ProcessRealtimeDTO> getAllSedimentationRealtimeValueFromTime(Date startTime, int processStep);
    List<ProcessRealtimeDTO> getSedimentationRealtimeValueFromTag(String name, Date startTime, Date endTime, int processStep);
    ProcessRealtimeDTO getLatestSedimentationRealtimeValueFromTag(String name, int processStep);
    void addSedimentationRealtimePartition(String partitionName, String endTime);
    void delSedimentationRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_sedimentation_realtime
    ////////////////////////////////////////////
    List<AiSedimentationRealtimeDTO> getAiSedimentationRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto, int processStep);
    List<FrequencyDTO> getDistributionE1Tb(Date startTime, int processStep);
    List<FrequencyDTO> getDistributionE2Tb(Date startTime, int processStep);
    List<FrequencyDTO> getDistribution(Date startTime, String name, int processStep);
    List<AiSedimentationInterfaceRealtimeDTO> getAiSedimentationInterfaceRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto, int processStep);
    AiSedimentationRealtimeDTO getLatestAiSedimentationRealtimeValue(int processStep);
    int delAiSedimentationRealtimeValue(Date updateTime, int processStep);

    ////////////////////////////////////////////
    // ai_sedimentation_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiSedimentationControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiSedimentationControl(AiProcessControlDTO dto);
    int modAiSedimentationControlKafkaFlag(AiProcessControlDTO dto);
    int delAiSedimentationControl(Date date, int processStep);

    ////////////////////////////////////////////
    // ai_sedimentation_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiSedimentationInit(int processStep);
    AiProcessInitDTO getAiSedimentationInit(String item, int processStep);
    int modAiSedimentationOperationMode(int operationMode, int processStep);
    int modAiSedimentationInit(String item, float value, int processStep);

    ////////////////////////////////////////////
    // ai_sedimentation_alarm
    ////////////////////////////////////////////
    int addAiSedimentationAlarm(AiProcessAlarmDTO dto, int processStep);
    List<AiProcessAlarmDTO> getAllAiSedimentationAlarm(Date alarm_time, int kafka_flag, int processStep);
    int modAiSedimentationAlarmKafkaFlag(AiProcessAlarmDTO dto, int processStep);
    int delAiSedimentationAlarm(Date date, int processStep);

    ////////////////////////////////////////////
    // filter_realtime
    ////////////////////////////////////////////
    int addFilterRealtimeValue(List<ProcessRealtimeDTO>  dtos);
    List<ProcessRealtimeDTO> getLatestFilterRealtimeValue(String partitionName, int processStep);
    List<ProcessRealtimeDTO> getAllFilterRealtimeValueFromTime(Date startTime, int processStep);
    List<ProcessRealtimeDTO> getFilterRealtimeValueFromTag(String name, Date startTime, Date endTime, int processStep);
    ProcessRealtimeDTO getLatestFilterRealtimeValueFromTag(String name, int processStep);
    void addFilterRealtimePartition(String partitionName, String endTime);
    void delFilterRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_filter_realtime
    ////////////////////////////////////////////
    List<AiFilterRealtimeDTO> getAiFilterRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto, int processStep);
    AiFilterRealtimeDTO getLatestAiFilterRealtimeValue(int processStep);
    int delAiFilterRealtimeValue(Date updateTime, int processStep);

    ////////////////////////////////////////////
    // ai_filter_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiFilterControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiFilterControl(AiProcessControlDTO dto);
    int modAiFilterControlKafkaFlag(AiProcessControlDTO dto);
    int delAiFilterControl(Date date, int processStep);


    ////////////////////////////////////////////
    // ai_filter_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiFilterInit(int processStep);
    AiProcessInitDTO getAiFilterInit(String item, int processStep);
    int modAiFilterOperationMode(int operationMode, int processStep);
    int modAiFilterInit(String item, float value, int processStep);
    int modAiFilterInitTi(float value, int processStep);

    ////////////////////////////////////////////
    // ai_filter_alarm
    ////////////////////////////////////////////
    int addAiFilterAlarm(AiProcessAlarmDTO dto, int processStep);
    List<AiProcessAlarmDTO> getAllAiFilterAlarm(Date alarm_time, int kafka_flag, int processStep);
    int modAiFilterAlarmKafkaFlag(AiProcessAlarmDTO dto, int processStep);
    int delAiFilterAlarm(Date date, int processStep);

    ////////////////////////////////////////////
    // gac_realtime
    ////////////////////////////////////////////
    int addGacRealtimeValue(List<ProcessRealtimeDTO>  dtos);
    List<ProcessRealtimeDTO> getLatestGacRealtimeValue(String partitionName, int processStep);
    List<ProcessRealtimeDTO> getAllGacRealtimeValueFromTime(Date startTime, int processStep);
    List<ProcessRealtimeDTO> getGacRealtimeValueFromTag(String name, Date startTime, Date endTime, int processStep);
    ProcessRealtimeDTO getLatestGacRealtimeValueFromTag(String name, int processStep);
    void addGacRealtimePartition(String partitionName, String endTime);
    void delGacRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_gac_realtime
    ////////////////////////////////////////////
    List<AiGacRealtimeDTO> getAiGacRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto);
    AiGacRealtimeDTO getLatestAiGacRealtimeValue();
    int delAiGacRealtimeValue(Date updateTime, int processStep);

    ////////////////////////////////////////////
    // ai_gac_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiGacControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiGacControl(AiProcessControlDTO dto);
    int modAiGacControlKafkaFlag(AiProcessControlDTO dto);
    int delAiGacControl(Date date, int processStep);


    ////////////////////////////////////////////
    // ai_gac_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiGacInit(int processStep);
    AiProcessInitDTO getAiGacInit(String item, int processStep);
    int modAiGacOperationMode(int operationMode, int processStep);
    int modAiGacInit(String item, float value, int processStep);
    int modAiGacInitTi(float value);

    ////////////////////////////////////////////
    // ai_gac_alarm
    ////////////////////////////////////////////
    int addAiGacAlarm(AiProcessAlarmDTO dto, int processStep);
    List<AiProcessAlarmDTO> getAllAiGacAlarm(Date alarm_time, int kafka_flag, int processStep);
    int modAiGacAlarmKafkaFlag(AiProcessAlarmDTO dto, int processStep);
    int delAiGacAlarm(Date date, int processStep);

    ////////////////////////////////////////////
    // disinfection_realtime
    ////////////////////////////////////////////
    int addDisinfectionRealtimeValue(List<ProcessRealtimeDTO> dtos);
    List<ProcessRealtimeDTO> getLatestDisinfectionRealtimeValue(String partitionName, int processStep);
    List<ProcessRealtimeDTO> getAllDisinfectionRealtimeValueFromTime(Date startTime, int processStep);
    List<ProcessRealtimeDTO> getDisinfectionRealtimeValueFromTag(String name, Date startTime, Date endTime, int processStep);
    ProcessRealtimeDTO getLatestDisinfectionRealtimeValueFromTag(String name, int processStep);
    void addDisinfectionRealtimePartition(String partitionName, String endTime);
    void delDisinfectionRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_disinfection_realtime
    ////////////////////////////////////////////
    List<AiDisinfectionRealtimeDTO> getAiDisinfectionRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto, int processStep, int disinfectionStep);
    AiDisinfectionRealtimeDTO getLatestAiDisinfectionRealtimeValue(int processStep, int disinfectionStep);
    int delAiDisinfectionRealtimeValue(Date updateTime, int processStep, int disinfectionStep);

    ////////////////////////////////////////////
    // ai_disinfection_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiPreDisinfectionControl(AiProcessControlDTO dto);
    AiProcessControlDTO getOneAiPeriDisinfectionControl(AiProcessControlDTO dto);
    AiProcessControlDTO getOneAiPostDisinfectionControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiPreDisinfectionControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiPeriDisinfectionControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiPostDisinfectionControl(AiProcessControlDTO dto);
    int modAiPreDisinfectionControlKafkaFlag(AiProcessControlDTO dto);
    int modAiPeriDisinfectionControlKafkaFlag(AiProcessControlDTO dto);
    int modAiPostDisinfectionControlKafkaFlag(AiProcessControlDTO dto);
    int delAiPreDisinfectionControl(Date date, int processStep);
    int delAiPeriDisinfectionControl(Date date, int processStep);
    int delAiPostDisinfectionControl(Date date, int processStep);

    ////////////////////////////////////////////
    // ai_disinfection_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiDisinfectionInit(int processStep, int disinfectionStep);
    AiProcessInitDTO getAiDisinfectionInit(String item, int processStep, int disinfectionStep);
    int modAiDisinfectionInit(String item, float value, int processStep, int disinfectionStep);
    int modAiDisinfectionOperationMode(int operationMode, int processStep, int disinfectionIndex);

    ////////////////////////////////////////////
    // ai_disinfection_alarm
    ////////////////////////////////////////////
    int addAiDisinfectionAlarm(AiProcessAlarmDTO dto, int processStep, int disinfectionStep);
    List<AiProcessAlarmDTO> getAllAiDisinfectionAlarm(Date alarm_time, int kafka_flag, int processStep, int disinfectionStep);
    int modAiDisinfectionAlarmKafkaFlag(AiProcessAlarmDTO dto, int processStep, int disinfectionStep);
    int delAiDisinfectionAlarm(Date date, int processStep, int disinfectionStep);

    ////////////////////////////////////////////
    // ozone_realtime
    ////////////////////////////////////////////
    int addOzoneRealtimeValue(List<ProcessRealtimeDTO>  dtos, int processStep);
    List<ProcessRealtimeDTO> getLatestOzoneRealtimeValue(String partitionName, int processStep);
    List<ProcessRealtimeDTO> getAllOzoneRealtimeValueFromTime(Date startTime, int processStep);
    List<ProcessRealtimeDTO> getOzoneRealtimeValueFromTag(String name, Date startTime, Date endTime, int processStep);
    ProcessRealtimeDTO getLatestOzoneRealtimeValueFromTag(String name, int processStep);
    void addOzoneRealtimePartition(String partitionName, String endTime);
    void delOzoneRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_ozone_realtime
    ////////////////////////////////////////////
    List<AiOzoneRealtimeDTO> getAiOzoneRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto);
    AiOzoneRealtimeDTO getLatestAiOzoneRealtimeValue();
    int delAiOzoneRealtimeValue(Date updateTime);

    ////////////////////////////////////////////
    // ai_ozone_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiOzoneControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiOzoneControl(AiProcessControlDTO dto);
    int modAiOzoneControlKafkaFlag(AiProcessControlDTO dto);
    int delAiOzoneControl(Date date, int processStep);

    ////////////////////////////////////////////
    // ai_ozone_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiOzoneInit(int processStep);
    AiProcessInitDTO getAiOzoneInit(String item, int processStep);
    int modAiOzoneOperationMode(int operationMode, int processStep);
    int modAiOzoneInit(String item, float value, int processStep);

    ////////////////////////////////////////////
    // ai_ozone_alarm
    ////////////////////////////////////////////
    int addAiOzoneAlarm(AiProcessAlarmDTO dto, int processStep);
    List<AiProcessAlarmDTO> getAllAiOzoneAlarm(Date alarm_time, int kafka_flag, int processStep);
    int modAiOzoneAlarmKafkaFlag(AiProcessAlarmDTO dto, int processStep);
    int delAiOzoneAlarm(Date date, int processStep);

    ////////////////////////////////////////////
    // ems_realtime
    ////////////////////////////////////////////
    int addEmsRealtimeValue(List<ProcessRealtimeDTO>  dtos);
    List<ProcessRealtimeDTO> getLatestEmsRealtimeValue();
    List<AiEmsRealtimeDTO> getLatestAiEmsRealtimeValue();

    ////////////////////////////////////////////
    // pms_realtime
    ////////////////////////////////////////////
    List<PmsAiDTO> getLatestPmsAiValue();
    List<PmsScadaDTO> getLatestPmsScadaValue();
    
    // UsrMng 테이블 조회, 업데이트 //
    UsrMngDTO getUsrMng(int processStep);
    int updateUsrMngVal(UsrMngDTO dto, int processStep);
    
    //TagMng DP조회
    String selectTagDp(String tagSn);
    
    // AlmCtrHis 
    List<AlmCtrHisDTO> getCtrHisBySeqAndTag(AlmCtrHisDTO dto);
    List<AlmCtrHisDTO> getTagInfoList(AlmCtrHisDTO dto);
//    List<AlmCtrHisDTO> getAlmCtrHisList();
    List<AlmCtrHisDTO> getSearchAlmCtrHis(InterfaceDateSearchDTO dto);
    List<AiProcessControlDTO> getCtrListByAlm(int almId, int almSeq, Date ctrTi);
    int addAlmCtrHis(List<AlmCtrHisDTO> almCtrHisList);
    
    InterfaceAlarmControlHistoryDTO getAlarmExceeded(InterfaceAlarmControlHistoryDTO queryDto);
    AiFactorDTO getAiFactorData(Date rnti, String procCd, String disinfectionIndex);

    int updateAiOprRealtime(AiOprRealtimeDTO dto);
    int initializeAiOprRealtimeValues();
    List<AiOprRealtimeDTO> getAllAiOprRt();
    
    int addAiOprHistory(AiOprHistoryDTO dto);
	List<AiOprHistoryDTO> getApiOprHistory(InterfaceDateSearchDTO dto);
}
