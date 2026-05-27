package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.UserDTO;

import java.util.List;

public interface IUserDAO
{
    int insert(UserDTO dto);
    UserDTO selectUser(String usr_id, String usr_pw);
    UserDTO selectUserFromUserid(String usr_id);
    List<UserDTO> selectAll();
    int update(int usr_auth, UserDTO dto);
    int updateMyInfo(int usr_auth, UserDTO dto);
    int updatePw(String usr_id, String usr_pw);
    int delete(String usr_id);
}
