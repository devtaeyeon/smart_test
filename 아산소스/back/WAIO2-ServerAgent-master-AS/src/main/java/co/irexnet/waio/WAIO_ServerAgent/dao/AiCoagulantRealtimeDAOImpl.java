package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiCoagulantRealtimeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiCoagulantRealtimeDAOImpl implements IAiCoagulantRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiCoagulantRealtimeDTO> select(Date start_time, Date end_time, int processStep)
    {
        String strQuery = "SELECT UPD_TI, AI_OPR, IN_VAL, OUT_VAL"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].C_TB_E'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].C_TB_E'))-2) AS C_TB_E"
		+ ", SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_C_CF'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_C_CF'))-2) AS ai_c_cf"
        + ", SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_C_CF'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_C_CF'))-2) AS c_tb FROM "
        + getTableByProcessStep(processStep) + " WHERE upd_ti > ? AND upd_ti <= ? ORDER BY upd_ti";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiCoagulantRealtimeDTO.class)
        );
    }

    @Override
    public AiCoagulantRealtimeDTO select(int processStep)
    {
        String strQuery = "SELECT UPD_TI, AI_OPR, IN_VAL, OUT_VAL"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].C_TB_E'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].C_TB_E'))-2) AS C_TB_E"
		+ ", SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_C_CF'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_C_CF'))-2) AS ai_c_cf"
        + ", SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].C_INJECTOR1'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].C_INJECTOR1'))-2) AS c_injector1"
        + ", SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].C_INJECTOR2'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].C_INJECTOR2'))-2) AS c_injector2"
        + ", IFNULL(SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].C_INJECTOR3'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].C_INJECTOR3'))-2), 0) AS c_injector3"
        + ", IFNULL(SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].C_INJECTOR4'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].C_INJECTOR4'))-2), 0) AS c_injector4"
        + ", SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_C_CF_NORM_CO'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_C_CF_NORM_CO'))-2) AS ai_c_cf_norm_co"
        + " FROM " + getTableByProcessStep(processStep) + " ORDER BY upd_ti DESC LIMIT 1"; // FIXME DESC 수정
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(AiCoagulantRealtimeDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int delete(Date update_time, int processStep)
    {
        String strQuery = "DELETE FROM " + getTableByProcessStep(processStep) + " WHERE upd_ti < ?";
        return jdbcTemplate.update(strQuery, update_time);
    }
    
    public String getTableByProcessStep(int processStep) {
        String tableNm = "";
        if(processStep == 1) {
            tableNm = "TB_AI_C1_RT";
        } else if(processStep == 2) {
            tableNm = "TB_AI_C2_RT";
        } else if(processStep == 3) {
            tableNm = "TB_AI_C3_RT";
        }
        return tableNm;
    }
}
