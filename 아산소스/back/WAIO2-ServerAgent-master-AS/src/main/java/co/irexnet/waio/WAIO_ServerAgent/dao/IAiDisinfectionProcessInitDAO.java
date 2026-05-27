package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;

import java.util.List;

public interface IAiDisinfectionProcessInitDAO
{
    List<AiProcessInitDTO> select(int processStep, int disinfectionIndex);
    AiProcessInitDTO select(String item, int processStep, int disinfectionIndex);
    int updateOperationMode(int operation_mode, int processStep, int disinfectionIndex);
    int update(String item, float value, int processStep, int disinfectionIndex);
}
