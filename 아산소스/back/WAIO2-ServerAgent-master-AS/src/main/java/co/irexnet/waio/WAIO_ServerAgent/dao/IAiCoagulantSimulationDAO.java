package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiCoagulantSimulationDTO;

import java.util.Date;
import java.util.List;

public interface IAiCoagulantSimulationDAO
{
    int insert(AiCoagulantSimulationDTO dto);
    List<AiCoagulantSimulationDTO> select(Date start_time, Date end_time);
}
