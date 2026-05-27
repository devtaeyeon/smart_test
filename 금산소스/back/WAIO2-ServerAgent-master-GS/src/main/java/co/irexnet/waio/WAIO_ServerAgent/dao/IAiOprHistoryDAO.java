package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.AiFactorDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;

import java.util.Date;
import java.util.List;


public interface IAiOprHistoryDAO
{
	int insert(AiOprHistoryDTO dto);
	List<AiOprHistoryDTO> select (InterfaceDateSearchDTO dto);
}
