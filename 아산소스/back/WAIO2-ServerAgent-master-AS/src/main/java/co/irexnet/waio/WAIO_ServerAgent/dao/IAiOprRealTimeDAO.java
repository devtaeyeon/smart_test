package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.util.List;

import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprRealTimeDTO;

public interface IAiOprRealTimeDAO {
	List<AiOprRealTimeDTO> select();
    int update(String process, String disinfectionStep, int value, int processStep);
    int updateAll();
}
