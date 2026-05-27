package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.util.Date;
import java.util.List;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessAlarmDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmNotifyDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAlarmControlHistoryDTO;

public interface IAlarmNotifyDAO {
	List<InterfaceAlarmControlHistoryDTO> selectBySeqAndTag (int almSeq, String tagSn);
    int insert(AlarmNotifyDTO dto);
    List<AlarmNotifyDTO> select();
    List<AlarmNotifyDTO> select(Date start_time);
    List<AlarmNotifyDTO> select(boolean ackState);
    AlarmNotifyDTO select(int alarm_id, Date alarm_time, String hostname);
    List<AlarmNotifyDTO> select(Date start_time, Date end_time);
    int updateAckState(int alarmNotifyIndex, boolean ackState);
    void insertAlarmControlHistory(InterfaceAlarmControlHistoryDTO dto);
    List<InterfaceAlarmControlHistoryDTO> selectAlarmControlHistory(InterfaceAlarmControlHistoryDTO dto);
    List<InterfaceAlarmControlHistoryDTO> selectTagInfoList(InterfaceAlarmControlHistoryDTO dto);
    List<InterfaceAlarmControlHistoryDTO> selectAlarmControlHistoryDetail(InterfaceAlarmControlHistoryDTO dto);

    InterfaceAlarmControlHistoryDTO getAlarmExceeded(InterfaceAlarmControlHistoryDTO dto);
}
