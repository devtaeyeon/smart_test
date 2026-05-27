package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessAlarmDTO;

import java.util.Date;
import java.util.List;

public interface IAiDisinfectionAlarmDAO
{
    int insert(int alarm_id, Date alarm_time, int kafka_flag, int processStep, int disinfectionStep);
    List<AiProcessAlarmDTO> select(Date alarm_time, int kafka_flag, int processStep, int disinfectionStep);
    int updateKafkaFlag(int alarm_id, Date alarm_time, int kafka_flag, int processStep, int disinfectionStep);
    int delete(Date date, int processStep, int disinfectionStep);
}
