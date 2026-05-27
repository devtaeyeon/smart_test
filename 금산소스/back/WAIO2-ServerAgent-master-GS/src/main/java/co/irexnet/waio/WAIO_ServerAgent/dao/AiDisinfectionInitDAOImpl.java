package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AiDisinfectionInitDAOImpl implements IAiDisinfectionProcessInitDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiProcessInitDTO> select(int processStep, int disinfectionStep)
    {
        String strQuery = "SELECT * FROM " + getTableByProcessStep(processStep, disinfectionStep);
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AiProcessInitDTO.class));
    }

    @Override
    public AiProcessInitDTO select(String itm, int processStep, int disinfectionStep)
    {
        String strQuery = "SELECT * FROM " + getTableByProcessStep(processStep, disinfectionStep) + " WHERE itm=?";
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
    public int updateOperationMode(int operation_mode, int processStep, int disinfectionStep)
    {
        String strQuery = "UPDATE " + getTableByProcessStep(processStep, disinfectionStep) +" SET init_val=? WHERE itm='"+getOperationColumn(disinfectionStep)+"'";
        return jdbcTemplate.update(strQuery, operation_mode);
    }

    @Override
    public int update(String itm, float init_val, int processStep, int disinfectionStep)
    {
        String strQuery = "UPDATE " + getTableByProcessStep(processStep, disinfectionStep) +" SET init_val=? WHERE itm=?";
        return jdbcTemplate.update(strQuery, init_val, itm);
    }
    
    public String getTableByProcessStep(int processStep, int disinfectionStep) {
        String tableNm = "";
        if(processStep == 1 && disinfectionStep == 1) {
            tableNm = "TB_AI_PRE_G_INIT";
        } else if(processStep == 1 && disinfectionStep == 3) {
            tableNm = "TB_AI_POST_G_INIT";
        }
        return tableNm;
    }
    
    public String getOperationColumn(int disinfectionStep) {
    	String operationNm = "";
        if(disinfectionStep == 1) {
        	operationNm = CommonValue.G_PRE_OPERATION_MODE;
        } else if(disinfectionStep == 2) {
        	operationNm = CommonValue.G_PERI_OPERATION_MODE;
        } else if(disinfectionStep == 3) {
        	operationNm = CommonValue.G_POST_OPERATION_MODE;
        }
        return operationNm;
    }
    
}
