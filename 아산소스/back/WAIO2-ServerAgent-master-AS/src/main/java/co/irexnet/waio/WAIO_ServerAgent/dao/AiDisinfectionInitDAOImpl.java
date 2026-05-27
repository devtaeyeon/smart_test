package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessInitDTO;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;

@Repository
public class AiDisinfectionInitDAOImpl implements IAiDisinfectionProcessInitDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiProcessInitDTO> select(int processStep, int disinfectionIndex)
    {
        String strQuery = "SELECT * FROM " + getTableByProcessStep(processStep, disinfectionIndex);
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AiProcessInitDTO.class));
    }

    @Override
    public AiProcessInitDTO select(String itm, int processStep, int disinfectionIndex)
    {
        String strQuery = "SELECT * FROM "+ getTableByProcessStep(processStep, disinfectionIndex) + " WHERE itm=?";
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
    public int updateOperationMode(int operation_mode, int processStep, int disinfectionIndex)
    {
        String strQuery = "UPDATE "+ getTableByProcessStep(processStep, disinfectionIndex) + " SET init_val=? WHERE itm='" + getOperationModeName(disinfectionIndex)+"'";
        return jdbcTemplate.update(strQuery, operation_mode);
    }

    @Override
    public int update(String itm, float init_val, int processStep, int disinfectionIndex)
    {
        String strQuery = "UPDATE "+ getTableByProcessStep(processStep, disinfectionIndex) + " SET init_val=? WHERE itm=?";
        return jdbcTemplate.update(strQuery, init_val, itm);
    }

    public String getTableByProcessStep(int processStep, int disinfectionIndex) {
        String tableNm = "";
        if(processStep == 1 && disinfectionIndex == CommonValue.DISINFECTION_PRE_STEP) { // 1단계공업 전차염
            tableNm = "TB_AI_PRE_G1_INIT";
        } else if(processStep == 2 && disinfectionIndex == CommonValue.DISINFECTION_PRE_STEP) { // 2단계생활 전차염
            tableNm = "TB_AI_PRE_G2_INIT";
        } else if(processStep == 2 && disinfectionIndex == CommonValue.DISINFECTION_POST_STEP) { // 2단계생활 후차염
            tableNm = "TB_AI_POST_G2_INIT";
        } else if(processStep == 3 && disinfectionIndex == CommonValue.DISINFECTION_PRE_STEP) { // 3단계공업 전차염
            tableNm = "TB_AI_PRE_G3_INIT";
        }
        return tableNm;
    }
    
    public String getOperationModeName(int disinfectionIndex) {
        StringBuilder sb = new StringBuilder("g_");
        if(disinfectionIndex == 1) {
            sb.append("pre");
        } else if(disinfectionIndex == 3){
            sb.append("post");
        }
        sb.append("_operation_mode");
        return sb.toString();
    }

}
