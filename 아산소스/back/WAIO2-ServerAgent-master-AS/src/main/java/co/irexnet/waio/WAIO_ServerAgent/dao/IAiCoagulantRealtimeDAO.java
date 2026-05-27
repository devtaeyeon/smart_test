package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiCoagulantRealtimeDTO;

import java.util.Date;
import java.util.List;

public interface IAiCoagulantRealtimeDAO
{
    List<AiCoagulantRealtimeDTO> select(Date start_time, Date end_time, int processStep);
    AiCoagulantRealtimeDTO select(int processStep);
    int delete(Date update_time, int processStep);
}
