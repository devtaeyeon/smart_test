package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiFilterRealtimeDTO;

import java.util.Date;
import java.util.List;

public interface IAiFilterRealtimeDAO
{
    List<AiFilterRealtimeDTO> select(Date start_time, Date end_time, int processStep);
    AiFilterRealtimeDTO select(int processStep);
    int delete(Date update_time, int processStep);
}
