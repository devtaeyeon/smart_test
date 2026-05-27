package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.AiFactorDTO;

import java.util.Date;


public interface IAiFactorDAO
{
	AiFactorDTO select(AiFactorDTO dto);
}
