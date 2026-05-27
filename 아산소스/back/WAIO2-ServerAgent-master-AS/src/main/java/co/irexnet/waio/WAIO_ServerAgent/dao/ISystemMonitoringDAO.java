package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.SystemMonitoringDTO;

import java.util.Date;
import java.util.List;

public interface ISystemMonitoringDAO
{
    int insert(SystemMonitoringDTO dto);
    List<SystemMonitoringDTO> select();
    List<SystemMonitoringDTO> select(String hostname);
    List<SystemMonitoringDTO> selectLatest(Date startDate);
    int delete(Date date);
}
