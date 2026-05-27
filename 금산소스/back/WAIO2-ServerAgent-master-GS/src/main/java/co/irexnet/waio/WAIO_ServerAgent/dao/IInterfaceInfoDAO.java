package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceInfoDTO;

import java.util.List;

public interface IInterfaceInfoDAO
{
    int insert(InterfaceInfoDTO dto);
    List<InterfaceInfoDTO> select(String hostname);
    List<InterfaceInfoDTO> selectWhereAddress(String address);
    int update(InterfaceInfoDTO dto);
}
