package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.PartitionInfoDTO;

import java.util.List;

public interface IPartitionInfoDAO
{
    int insert(PartitionInfoDTO dto);
    List<PartitionInfoDTO> select(String hostname);
    int update(PartitionInfoDTO dto);
}
