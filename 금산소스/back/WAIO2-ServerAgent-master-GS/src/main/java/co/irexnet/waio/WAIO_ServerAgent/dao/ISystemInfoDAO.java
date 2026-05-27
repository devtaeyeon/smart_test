package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.SystemInfoDTO;

import java.util.List;

public interface ISystemInfoDAO
{
    int insert(SystemInfoDTO dto);
    SystemInfoDTO select(String hostname);
    List<SystemInfoDTO> select();
    int update(SystemInfoDTO dto);
    int updateName(String hostname, String name);
}
