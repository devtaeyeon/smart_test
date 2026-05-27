package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.CoagulantsAnalysisDTO;

import java.util.Date;
import java.util.List;

public interface ICoagulantsAnalysisDAO
{
    List<CoagulantsAnalysisDTO> select();
    CoagulantsAnalysisDTO select(Date log_time);
    CoagulantsAnalysisDTO selectLatest();
    List<CoagulantsAnalysisDTO> select2Latest();
    List<CoagulantsAnalysisDTO> selectMinute(Date start_time);
}
