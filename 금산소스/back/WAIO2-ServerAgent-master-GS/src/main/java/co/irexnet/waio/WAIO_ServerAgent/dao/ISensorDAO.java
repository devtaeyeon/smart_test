package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.FrequencyDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.SensorDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TrendCodeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TrendTbDTO;
import co.irexnet.waio.WAIO_ServerAgent.resource_dto.CoagulantsDTO;

import java.util.Date;
import java.util.List;

public interface ISensorDAO
{
    int insert(SensorDTO dto);
    List<SensorDTO> select(Date start_time);
    SensorDTO selectLatest();
    List<CoagulantsDTO> selectHistory();
    CoagulantsDTO selectCoagulants();
    List<TrendTbDTO> selectTb(Date start_time, Date end_time);
    List<TrendCodeDTO> selectCode(String code, Date start_time, Date end_time);
    List<FrequencyDTO> selectDistinctCountHsE1Tb(Date start_time);
    List<FrequencyDTO> selectDistinctCountHsE2Tb(Date start_time);
    List<FrequencyDTO> selectDistinctCountHsFTb(Date start_time);
    int delete(Date update_time);
}
