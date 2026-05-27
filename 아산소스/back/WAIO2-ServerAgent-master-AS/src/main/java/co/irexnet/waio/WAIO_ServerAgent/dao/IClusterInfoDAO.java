package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.ClusterInfoDTO;

import java.util.List;

public interface IClusterInfoDAO
{
    List<ClusterInfoDTO> select();
}
