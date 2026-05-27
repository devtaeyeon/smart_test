package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;

import java.util.List;

// 소독공정용
public interface IAiDisinfectionProcessInitDAO
{
    List<AiProcessInitDTO> select(int processStep, int disinfectionStep);
    AiProcessInitDTO select(String item, int processStep, int disinfectionStep);
    int updateOperationMode(int operation_mode, int processStep, int disinfectionStep);
    int update(String item, float value, int processStep, int disinfectionStep);
}
