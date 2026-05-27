package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiClearOperationBandDTO;

import java.util.Date;
import java.util.List;

public interface IAiClearOperationBandDAO
{
    List<AiClearOperationBandDTO> select(Date start_time, Date end_time, int processStep);
}
