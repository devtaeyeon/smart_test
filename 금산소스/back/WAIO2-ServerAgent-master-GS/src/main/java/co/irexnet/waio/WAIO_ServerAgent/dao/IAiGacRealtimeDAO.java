package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiGacRealtimeDTO;

import java.util.Date;
import java.util.List;

public interface IAiGacRealtimeDAO
{
    List<AiGacRealtimeDTO> select(Date start_time, Date end_time);
    AiGacRealtimeDTO select();
    int delete(Date update_time);
}
