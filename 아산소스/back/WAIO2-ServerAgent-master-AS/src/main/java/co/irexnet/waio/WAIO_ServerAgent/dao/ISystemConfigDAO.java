package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.SystemConfigDTO;

public interface ISystemConfigDAO
{
    SystemConfigDTO select();
    int update(SystemConfigDTO dto);
}
