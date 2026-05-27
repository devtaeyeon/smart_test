package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.ChemicalInfoDTO;

import java.util.List;

public interface IChemicalInfoDAO
{
    int insert(ChemicalInfoDTO dto);
    List<ChemicalInfoDTO> select();
    ChemicalInfoDTO select(String code);
    int update(ChemicalInfoDTO dto);
    int delete(String code);
}
