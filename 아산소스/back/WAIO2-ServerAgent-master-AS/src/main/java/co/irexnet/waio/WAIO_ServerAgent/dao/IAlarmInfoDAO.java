package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;

import java.util.List;

public interface IAlarmInfoDAO
{
    int insert(AlarmInfoDTO dto);
    List<AlarmInfoDTO> select();
    int update(AlarmInfoDTO dto);
    int delete(int alarm_id);
}
