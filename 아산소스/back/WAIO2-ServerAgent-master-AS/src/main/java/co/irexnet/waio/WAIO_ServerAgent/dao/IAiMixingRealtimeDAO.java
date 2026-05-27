package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiMixingRealtimeDTO;

import java.util.Date;
import java.util.List;

public interface IAiMixingRealtimeDAO
{
    List<AiMixingRealtimeDTO> select(Date start_time, Date end_time, int processStep);
    AiMixingRealtimeDTO select(int processStep);
    int delete(Date update_time, int processStep);
}
