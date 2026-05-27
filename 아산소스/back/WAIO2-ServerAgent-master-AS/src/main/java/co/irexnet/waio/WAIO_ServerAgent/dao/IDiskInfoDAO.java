package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.DiskInfoDTO;

import java.util.List;

public interface IDiskInfoDAO
{
    int insert(DiskInfoDTO dto);
    List<DiskInfoDTO> select(String hostname);
    int update(DiskInfoDTO dto);
}
