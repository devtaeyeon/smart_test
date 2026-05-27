package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.util.List;

import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;

public interface IAiOprHistoryDAO {
	List<AiOprHistoryDTO> select();
	List<AiOprHistoryDTO> select(InterfaceDateSearchDTO dto);
    int insert(List<AiOprHistoryDTO> list);
}
