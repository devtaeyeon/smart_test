package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiDisinfectionRealtimeDTO;

import java.util.Date;
import java.util.List;

public interface IAiDisinfectionRealtimeDAO
{
    List<AiDisinfectionRealtimeDTO> select(Date start_time, Date end_time, int processStep, int disinfectionStep);
    AiDisinfectionRealtimeDTO select(int processStep, int disinfectionStep);
    int delete(Date update_time, int processStep, int disinfectionStep);
}
