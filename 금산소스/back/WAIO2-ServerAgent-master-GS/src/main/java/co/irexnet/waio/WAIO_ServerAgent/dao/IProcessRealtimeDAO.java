package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessRealtimeDTO;

import java.util.Date;
import java.util.List;

public interface IProcessRealtimeDAO
{
    int insert(List<ProcessRealtimeDTO> list);
    List<ProcessRealtimeDTO> select(Date start_time, int processStep);
    List<ProcessRealtimeDTO> select(String name, Date start_time, Date end_time, int processStep);
    List<ProcessRealtimeDTO> select(String partitionName, int processStep);
    ProcessRealtimeDTO selectLatest(String name, int processStep);
    void addPartition(String partitionName, String end_time);
    void dropPartition(String partitionName);
}
