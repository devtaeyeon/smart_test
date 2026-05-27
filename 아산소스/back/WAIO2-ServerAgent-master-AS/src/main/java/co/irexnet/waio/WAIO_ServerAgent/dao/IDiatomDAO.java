package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.DiatomDTO;

import java.util.Date;
import java.util.List;

public interface IDiatomDAO
{
    int insert(DiatomDTO dto);
    List<DiatomDTO> select();
    DiatomDTO selectLatest();
    int update(DiatomDTO dto);
    int delete(int diatom_index);
}
