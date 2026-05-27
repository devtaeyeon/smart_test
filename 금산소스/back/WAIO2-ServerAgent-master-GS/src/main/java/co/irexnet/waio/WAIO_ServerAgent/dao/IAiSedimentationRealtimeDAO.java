package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationInterfaceRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.FrequencyDTO;

import java.util.Date;
import java.util.List;

public interface IAiSedimentationRealtimeDAO
{
    List<AiSedimentationRealtimeDTO> select(Date start_time, Date end_time, int processStep);
    List<FrequencyDTO> selectE1Tb(Date start_time, int processStep);
    List<FrequencyDTO> selectE2Tb(Date start_time, int processStep);
    List<FrequencyDTO> selectDistribution(Date start_time, String name, int processStep);
    List<AiSedimentationInterfaceRealtimeDTO> selectInterface(Date start_time, Date end_time, int processStep);
    AiSedimentationRealtimeDTO select(int processStep);
    int delete(Date update_time, int processStep);
}
