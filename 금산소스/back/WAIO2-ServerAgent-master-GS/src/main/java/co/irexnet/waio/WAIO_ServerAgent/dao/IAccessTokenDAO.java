package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;

import java.util.Date;
import java.util.List;

public interface IAccessTokenDAO
{
    int insert(AccessTokenDTO dto);
    List<AccessTokenDTO> select();
    AccessTokenDTO select(String token);
    int update(String token, Date expiration);
    int delete(String token);
    int delete(Date date);
}
