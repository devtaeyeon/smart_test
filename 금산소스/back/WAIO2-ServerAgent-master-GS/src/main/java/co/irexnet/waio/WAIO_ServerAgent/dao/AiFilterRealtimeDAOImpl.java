package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiFilterRealtimeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiFilterRealtimeDAOImpl implements IAiFilterRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiFilterRealtimeDTO> select(Date start_time, Date end_time, int processStep)
    {
        String strQuery = "SELECT * "
        + " FROM " 
        + getTableByProcessStep(processStep) + " WHERE upd_ti > ? AND upd_ti <= ? ORDER BY upd_ti";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiFilterRealtimeDTO.class)
        );
    }

    @Override
    public AiFilterRealtimeDTO select(int processStep)
    {
        String strQuery = "SELECT UPD_TI, AI_OPR, IN_VAL, OUT_VAL "
        + ", JSON_EXTRACT(IN_VAL, '$[0].F_SPEED') AS f_speed "
        + ", CONCAT('{\"f_fil_ing\":', SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].F_FIL_ING'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].F_FIL_ING'))-2),'}') AS f_fil_ing "
        + ", CONCAT('{\"f_bw_wait\":', SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].F_BW_WAIT'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].F_BW_WAIT'))-2),'}') AS f_bw_wait "
        + ", CONCAT('{\"f_bw_ing\":', SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].F_BW_ING'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].F_BW_ING'))-2),'}') AS f_bw_ing "
        + ", CONCAT('{\"f_fil_wait\":', SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].F_FIL_WAIT'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].F_FIL_WAIT'))-2),'}') AS f_fil_wait "
        + ", CONCAT('{\"f_dr_ing\":', SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].F_DR_ING'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].F_DR_ING'))-2),'}') AS f_dr_ing "
        + ", CONCAT('{\"f_rest\":', SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].F_REST'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].F_REST'))-2),'}') AS f_rest "
        + ", CONCAT('{\"ai_f_num_fil\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_F_NUM_FIL'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_F_NUM_FIL'))-2),'}') AS ai_f_num_fil "
        + ", CONCAT('{\"ai_f_wl\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_F_WL'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_F_WL'))-2),'}') AS ai_f_wl "
        + ", CONCAT('{\"ai_f_time\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_F_TIME'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_F_TIME'))-2),'}') AS ai_f_time " //예측여과지속시간(지별)
        + ", CONCAT('{\"ai_f_bw_wait_time\":', SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].F_TIME_BW_PER'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].F_TIME_BW_PER'))-2),'}') AS ai_f_bw_wait_time " //역세대기시간
        + ", CONCAT('{\"ai_f_bw_start_time\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_F_BW_START_TIME'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_F_BW_START_TIME'))-2),'}') AS ai_f_bw_start_time "
        + ", CONCAT('{\"ai_f_schedule\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_F_SCHEDULE_FINAL'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_F_SCHEDULE_FINAL'))-2),'}') AS ai_f_schedule "
        + ", CONCAT('{\"ai_f_location_operation\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_F_LOCATION_OPERATION'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_F_LOCATION_OPERATION'))-2),'}') AS ai_f_location_operation FROM "
        + getTableByProcessStep(processStep) + " ORDER BY upd_ti DESC LIMIT 1"; //TODO
//        + getTableByProcessStep(processStep) + " ORDER BY upd_ti ASC LIMIT 1";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(AiFilterRealtimeDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int delete(Date upd_ti, int processStep)
    {
        String strQuery = "DELETE FROM " + getTableByProcessStep(processStep) + " WHERE upd_ti < ?";
        return jdbcTemplate.update(strQuery, upd_ti);
    }
    
    public String getTableByProcessStep(int processStep) {
        String tableNm = "";
        if(processStep == 1) {
            tableNm = "TB_AI_F_LIV_RT";
        } 
        return tableNm;
    }
}
