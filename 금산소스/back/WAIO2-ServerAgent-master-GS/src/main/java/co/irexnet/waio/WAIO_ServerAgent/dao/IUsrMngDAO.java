package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.UsrMngDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageRangeDTO;

import java.util.List;

public interface IUsrMngDAO
{
	UsrMngDTO select(int processStep);
    int update(UsrMngDTO dto, int processStep);
}
