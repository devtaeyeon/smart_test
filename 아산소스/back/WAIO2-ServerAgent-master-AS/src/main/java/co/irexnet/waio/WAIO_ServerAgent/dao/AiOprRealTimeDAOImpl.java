package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprRealTimeDTO;

@Repository
public class AiOprRealTimeDAOImpl implements IAiOprRealTimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public List<AiOprRealTimeDTO> select()
    {
        String strQuery = "SELECT * FROM TB_AI_OPR_RT"; 
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AiOprRealTimeDTO.class));
    }

    @Override
    public int update(String process, String disinfectionStep, int init_val, int processStep)
    {
        String strQuery = "UPDATE TB_AI_OPR_RT"
        				+ " SET OPR_MINUTES = OPR_MINUTES + 1"
        				+ " WHERE PROC_CD='" + process + processStep + "'"
        				+ " AND DISINFECTION_INDEX='" + disinfectionStep + "'"
        				+ " AND AI_OPR=?";
        return jdbcTemplate.update(strQuery, init_val);
    }
    
    @Override
    public int updateAll()
    {
        String strQuery = "UPDATE TB_AI_OPR_RT SET OPR_MINUTES = 0";
        return jdbcTemplate.update(strQuery);
    }

}
