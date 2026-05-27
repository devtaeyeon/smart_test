package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.WaterPurificationInfoDTO;

import java.util.List;

public interface IWaterPurificationInfoDAO
{
    int insert(WaterPurificationInfoDTO dto);
    List<WaterPurificationInfoDTO> select();
    WaterPurificationInfoDTO select(String code);
    int update(WaterPurificationInfoDTO dto);
    int delete(String code);
}
