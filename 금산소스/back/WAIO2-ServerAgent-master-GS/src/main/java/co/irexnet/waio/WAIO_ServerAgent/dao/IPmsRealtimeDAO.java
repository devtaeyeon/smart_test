package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.PmsAiDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.PmsScadaDTO;

import java.util.List;

public interface IPmsRealtimeDAO
{
    List<PmsScadaDTO> selectScada();
    List<PmsAiDTO> selectAi();
}
