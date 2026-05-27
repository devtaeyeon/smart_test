package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.util.List;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiEmsRealtimeDTO;

public interface IAiEmsRealtimeDAO
{
    List<AiEmsRealtimeDTO> select();
}
