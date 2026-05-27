package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.AiFactorDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprRealtimeDTO;

import java.util.Date;
import java.util.List;


public interface IAiOprRealtimeDAO
{
	int update(AiOprRealtimeDTO dto);
	int initalizeValues();
	List<AiOprRealtimeDTO> select();
}
