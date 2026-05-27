package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.UserDTO;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAOImpl implements IUserDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(UserDTO dto)
    {
        String strQuery = "insert into TB_USR values (?, ?, ?, ?, ?, ?)";

        try
        {
            return jdbcTemplate.update(
                    strQuery,
                    dto.getUsr_id(), dto.getUsr_pw(), dto.getUsr_nm(), dto.getUsr_pn(), dto.getUsr_auth(), dto.getUsr_ti()
            );
        }
        catch(DuplicateKeyException e)
        {
            return 0;
        }
    }

    @Override
    public UserDTO selectUser(String usr_id, String usr_pw)
    {
        String strQuery = "select * from TB_USR where usr_id=? and usr_pw=?";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new Object[]{usr_id, usr_pw}, new BeanPropertyRowMapper<>(UserDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public UserDTO selectUserFromUserid(String usr_id)
    {
        String strQuery = "select * from TB_USR where usr_id=?";

        try
        {
            return jdbcTemplate.queryForObject(strQuery, new Object[]{usr_id}, new BeanPropertyRowMapper<>(UserDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public List<UserDTO> selectAll()
    {
        String strQuery = "select * from TB_USR";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(UserDTO.class));
    }

    @Override
    public int update(int usr_auth, UserDTO dto)
    {
        String strQuery;
        strQuery = "update TB_USR set usr_pn=?, usr_auth=?, usr_ti=? where usr_id=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getUsr_pn(), dto.getUsr_auth(), dto.getUsr_ti(), dto.getUsr_id()
        );
    }
    
    @Override
    public int updateMyInfo(int usr_auth, UserDTO dto)
    {
        String strQuery;
        strQuery = "update TB_USR set usr_pn=? where usr_id=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getUsr_pn(), dto.getUsr_id()
        );
    }

    @Override
    public int updatePw(String usr_id, String usr_pw)
    {
        String strQuery = "update TB_USR set usr_pw=? where usr_id=?";
        return jdbcTemplate.update(strQuery, usr_pw, usr_id);
    }

    @Override
    public int delete(String usr_id)
    {
        String strQuery = "delete from TB_USR where usr_id=?";
        return jdbcTemplate.update(strQuery, usr_id);
    }
}
