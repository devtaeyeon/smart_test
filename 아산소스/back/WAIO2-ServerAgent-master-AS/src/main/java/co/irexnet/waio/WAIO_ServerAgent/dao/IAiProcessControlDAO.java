package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.util.Date;
import java.util.List;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessControlDTO;

public interface IAiProcessControlDAO
{
    AiProcessControlDTO select(Date update_time, Date run_time, String name, int kafka_flag, int processStep);
    List<AiProcessControlDTO> select(Date run_time, int kafka_flag, int processStep);
    int updateKafkaFlag(Date update_time, Date run_time, String name, int kafka_flag, int processStep);
    int delete(Date date, int processStep);
}
