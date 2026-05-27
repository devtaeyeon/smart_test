package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;

import java.util.List;

public interface IAiProcessInitDAO
{
    List<AiProcessInitDTO> select(int processStep);
    AiProcessInitDTO select(String item, int processStep);
    int updateOperationMode(int operation_mode, int processStep);
    int update(String item, float value, int processStep);
}
