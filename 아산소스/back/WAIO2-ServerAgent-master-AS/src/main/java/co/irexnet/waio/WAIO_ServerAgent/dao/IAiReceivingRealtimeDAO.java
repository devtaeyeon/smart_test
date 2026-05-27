package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiReceivingRealtimeDTO;

import java.util.Date;
import java.util.List;


public interface IAiReceivingRealtimeDAO
{
    List<AiReceivingRealtimeDTO> select(Date start_time, Date end_time, int processStep);
    AiReceivingRealtimeDTO select(int processStep);
    int delete(Date update_time, int processStep);
}
