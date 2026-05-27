package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessRealtimeDTO;

import java.util.List;

public interface ILoadProcessRealtimeDAO
{
    int insert(String procCd, List<ProcessRealtimeDTO> list);
    void addPartition(String procCd, List<String> partitionNameList);
    List<String> getAddPartitionList(String procCd);
    void dropPartition(String procCd, List<String> partitionNameList);
    List<String> getDropPartitionList(String procCd, String partitionNm);
}
