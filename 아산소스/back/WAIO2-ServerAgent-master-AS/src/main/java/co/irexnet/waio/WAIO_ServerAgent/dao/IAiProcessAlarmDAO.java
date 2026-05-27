package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessAlarmDTO;

import java.util.Date;
import java.util.List;

public interface IAiProcessAlarmDAO
{
    int insert(int alarm_id, Date alarm_time, int kafka_flag);
    List<AiProcessAlarmDTO> select(Date alarm_time, int kafka_flag, int processStep);
    int updateKafkaFlag(AiProcessAlarmDTO dto);
    int delete(Date date);
}
