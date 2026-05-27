package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiClearOperationBandDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiClearOperationBandDAOImpl implements IAiClearOperationBandDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiClearOperationBandDTO> select(Date start_time, Date end_time, int processStep)
    {
        String strQuery = "SELECT * FROM " + getTableByProcessStep(processStep) +
                " WHERE `ti_seq` > ? and `ti_seq` <= ? ORDER BY `ti_seq`";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiClearOperationBandDTO.class)
        );
    }
    
    public String getTableByProcessStep(int processStep) {
        String tableNm = "";
        if(processStep == 1) {
            tableNm = "TB_AI_H_OPR_LIV_BND";
        } else if(processStep == 2) {
            tableNm = "TB_AI_H_OPR_IND_BND";
        }
        return tableNm;
    }
}
