package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AiFilterInitDAOImpl implements IAiProcessInitDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiProcessInitDTO> select(int processStep)
    {
        String strQuery = "SELECT * FROM " + getTableByProcessStep(processStep);
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
        String strQuery = "UPDATE " + getTableByProcessStep(processStep) + " SET init_val=? WHERE itm='f_operation_mode'";
//    	String strQuery = "UPDATE " + getTableByProcessStep(processStep) + " SET init_val=? WHERE itm='ai_opr'";
        return jdbcTemplate.update(strQuery, operation_mode);
    }

    @Override
    public int update(String itm, float init_val, int processStep)
    {
        String strQuery = "UPDATE " + getTableByProcessStep(processStep) + " SET init_val=? WHERE itm=?";
        return jdbcTemplate.update(strQuery, init_val, itm);
    }

    public int updateTi(float init_val, int processStep)
    {
//        String strQuery = "UPDATE " + getTableByProcessStep(processStep) + " SET init_val = ? WHERE itm REGEXP 'AIF-20.'";
    	String strQuery = "UPDATE " + getTableByProcessStep(processStep) + " SET init_val = ? WHERE itm ='f_location_ti_set_max'";
        return jdbcTemplate.update(strQuery, init_val);
    }
    
    public String getTableByProcessStep(int processStep) {
        String tableNm = "";
        if(processStep == 1) {
            tableNm = "TB_AI_F_LIV_INIT";
        } 
        return tableNm;
    }
}
