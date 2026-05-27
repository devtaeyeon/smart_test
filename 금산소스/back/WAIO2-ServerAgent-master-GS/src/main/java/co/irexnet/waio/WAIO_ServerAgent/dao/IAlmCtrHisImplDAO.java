package co.irexnet.waio.WAIO_ServerAgent.dao;


import java.util.Date;
import java.util.List;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessControlDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmNotifyDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlmCtrHisCntDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlmCtrHisDTO;

public interface IAlmCtrHisImplDAO
{
    List<AlmCtrHisDTO> selectBySeqAndTag (int almSeq, String tagSn);
//    List<AlmCtrHisDTO> select();
    List<AlmCtrHisDTO> select(Date start_time, Date end_time);
    int insert(List<AlmCtrHisDTO> almCtrHisList);
    List<AiProcessControlDTO> select(int almId, int almSeq, Date ctrTi);
    List<AlmCtrHisDTO> selectTagInfoList(AlmCtrHisDTO dto);
}
