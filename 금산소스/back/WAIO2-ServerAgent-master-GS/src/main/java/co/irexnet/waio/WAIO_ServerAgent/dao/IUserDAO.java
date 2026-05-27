package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.UserDTO;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface IUserDAO
{
    int insert(UserDTO dto) throws NoSuchAlgorithmException;
    UserDTO selectUser(String usr_id, String usr_pw) throws NoSuchAlgorithmException;
    UserDTO selectUserFromUserid(String usr_id);
    List<UserDTO> selectAll();
    int update(int usr_auth, UserDTO dto);
    int updateMyInfo(int usr_auth, UserDTO dto);
    int updatePw(String usr_id, String usr_pw) throws NoSuchAlgorithmException;
    int delete(String usr_id);
}
