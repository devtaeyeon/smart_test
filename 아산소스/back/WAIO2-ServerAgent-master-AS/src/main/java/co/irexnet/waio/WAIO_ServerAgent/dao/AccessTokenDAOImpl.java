package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AccessTokenDAOImpl implements IAccessTokenDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(AccessTokenDTO dto)
    {
        String strQuery = "insert into TB_ACS_TKN (tkn, usr_id, usr_nm, usr_auth, expr_ti) values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getTkn(), dto.getUsr_id(), dto.getUsr_nm(), dto.getUsr_auth(), dto.getExpr_ti()
        );
    }

    @Override
    public List<AccessTokenDTO> select() {
        String strQuery = "select * from TB_ACS_TKN";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AccessTokenDTO.class));
    }

    @Override
    public AccessTokenDTO select(String token)
    {
        String strQuery = "select * from TB_ACS_TKN where tkn=?";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new Object[]{token}, new BeanPropertyRowMapper<>(AccessTokenDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int update(String token, Date expr_ti)
    {
        String strQuery = "update TB_ACS_TKN set expr_ti=? where tkn=?";
        return jdbcTemplate.update(strQuery, expr_ti, token);
    }

    @Override
    public int delete(String token)
    {
        String strQuery = "delete from TB_ACS_TKN where tkn=?";
        return jdbcTemplate.update(strQuery, token);
    }

    @Override
    public int delete(Date date)
    {
        String strQuery = "delete from TB_ACS_TKN where expr_ti<?";
        return jdbcTemplate.update(strQuery, date);
    }
}
