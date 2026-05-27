package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AiGacInitDAOImpl implements IAiProcessInitDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiProcessInitDTO> select(int processStep)
    {
        String strQuery = "SELECT * FROM TB_AI_I_INIT";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AiProcessInitDTO.class));
    }

    @Override
    public AiProcessInitDTO select(String itm, int processStep)
    {
        String strQuery = "SELECT * FROM TB_AI_I_INIT WHERE itm=?";
        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery, new Object[]{itm}, new BeanPropertyRowMapper<>(AiProcessInitDTO.class)
            );
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int updateOperationMode(int operation_mode, int processStep)
    {
        String strQuery = "UPDATE TB_AI_I_INIT SET init_val=? WHERE itm='i_operation_mode'";
        return jdbcTemplate.update(strQuery, operation_mode);
    }

    @Override
    public int update(String itm, float init_val, int processStep)
    {
        String strQuery = "UPDATE TB_AI_I_INIT SET init_val=? WHERE itm=?";
        return jdbcTemplate.update(strQuery, init_val, itm);
    }

    public int updateTi(float init_val)
    {
        String strQuery = "UPDATE TB_AI_I_INIT SET VALUE = ? WHERE itm REGEXP 'AII-20.'";
        return jdbcTemplate.update(strQuery, init_val);
    }
}
