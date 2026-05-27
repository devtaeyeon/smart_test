package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.LoginHistoryDTO;

import java.util.Date;
import java.util.List;

public interface ILoginHistoryDAO
{
    List<LoginHistoryDTO> select();
    int insert(LoginHistoryDTO dto);
    int delete(Date date);
}
