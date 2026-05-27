package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.CoagulantsSimulationDTO;

import java.util.List;

public interface ICoagulantsSimulationDAO
{
    int insert(CoagulantsSimulationDTO dto);
    List<CoagulantsSimulationDTO> select();
    List<CoagulantsSimulationDTO> select(boolean upper, int state);
}
