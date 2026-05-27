package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiMixingRealtimeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiMixingRealtimeDAOImpl implements IAiMixingRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiMixingRealtimeDTO> select(Date start_time, Date end_time, int processStep)
    {
        String strQuery = "SELECT UPD_TI, AI_OPR, IN_VAL, OUT_VAL "
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_TE.d_te'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_TE.d_te'))-2) AS d_te"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_TE.d_loc_te.location1'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_TE.d_loc_te.location1'))-2) AS d_te_loc1"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_TE.d_loc_te.location2'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_TE.d_loc_te.location2'))-2) AS d_te_loc2"
//        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_DE'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_DE'))-2) AS d_de"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_DV.d_loc_dv.location1'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_DV.d_loc_dv.location1'))-2) AS d_dv_loc1"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_DV.d_loc_dv.location2'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_DV.d_loc_dv.location2'))-2) AS d_dv_loc2"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_PW'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_PW'))-2) AS d_pw"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_IM_D'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_IM_D'))-2) AS d_im_d"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_G'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_G'))-2) AS d_g"
        + ", SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_D_LOC_FC_G'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_D_LOC_FC_G'))-2) AS ai_d_loc_fc_g" 
        + ", SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_D_LOC_FC_SP'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_D_LOC_FC_SP'))-2) AS ai_d_loc_fc_sp FROM " + getTableByProcessStep(processStep) + " WHERE upd_ti > ? AND upd_ti <= ? ORDER BY upd_ti";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiMixingRealtimeDTO.class)
        );
    }

    @Override
    public AiMixingRealtimeDTO select(int processStep)
    {
        String strQuery = "SELECT UPD_TI, AI_OPR, IN_VAL, OUT_VAL "
//        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_DE'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_DE'))-2) AS d_de"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_DV.d_loc_dv.location1'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_DV.d_loc_dv.location1'))-2) AS d_dv_loc1"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_DV.d_loc_dv.location2'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_DV.d_loc_dv.location2'))-2) AS d_dv_loc2"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_PW'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_PW'))-2) AS d_pw"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_IM_D'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_IM_D'))-2) AS d_im_d"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_G'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_G'))-2) AS d_g"
//        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_KI_DV'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_KI_DV'))-2) AS d_ki_dv"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_ANR'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_ANR'))-2) AS d_anr"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].D_V'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].D_V'))-2) AS d_v"  
        + ", SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_D_LOC_FC_G'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_D_LOC_FC_G'))-2) AS ai_d_loc_fc_g" 
        + ", SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AI_D_LOC_FC_SP'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AI_D_LOC_FC_SP'))-2) AS ai_d_loc_fc_sp FROM " 
        + getTableByProcessStep(processStep) + " ORDER BY upd_ti DESC LIMIT 1";//TODO 개발만 ASC
        
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(AiMixingRealtimeDTO.class));
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
            tableNm = "TB_AI_D_RT";
        }
        return tableNm;
    }
}
