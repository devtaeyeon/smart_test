package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AiSedimentationInitDAOImpl implements IAiProcessInitDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiProcessInitDTO> select(int processStep)
    {
        String strQuery ="SELECT * FROM " + getTableByProcessStep(processStep);

        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AiProcessInitDTO.class));
    }

    @Override
    public AiProcessInitDTO select(String itm, int processStep)
    {
        String strQuery = "SELECT * FROM " + getTableByProcessStep(processStep) + " WHERE itm=?";
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
        String strQuery = "UPDATE " + getTableByProcessStep(processStep) + " SET init_val=? WHERE itm='e_operation_mode'";
        return jdbcTemplate.update(strQuery, operation_mode);
    }

    @Override
    public int update(String itm, float value, int processStep)
    {
        String strQuery = "UPDATE " + getTableByProcessStep(processStep) + " SET init_val=? WHERE itm=?";
        return jdbcTemplate.update(strQuery, value, itm);
    }

    public String getTableByProcessStep(int processStep) {
        String tableNm = "";
        if(processStep == 1) {
            tableNm = "TB_AI_E_LIV_INIT";
        } else if(processStep == 2) {
            tableNm = "TB_AI_E_IND_INIT";
        }
        return tableNm;
    }
}
