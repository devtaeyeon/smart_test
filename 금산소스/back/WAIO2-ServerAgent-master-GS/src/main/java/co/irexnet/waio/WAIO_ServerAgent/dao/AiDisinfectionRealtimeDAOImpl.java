package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiDisinfectionRealtimeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiDisinfectionRealtimeDAOImpl implements IAiDisinfectionRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiDisinfectionRealtimeDTO> select(Date start_time, Date end_time, int processStep, int disinfectionStep)
    {
        String strQuery = "SELECT UPD_TI, AI_OPR, IN_VAL, OUT_VAL"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_D_RESIDUAL_CL')) AS g_d_residual_cl"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_E_RESIDUAL_CL')) AS g_e_residual_cl"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_F_OUT_RESIDUAL_CL')) AS g_f_out_residual_cl"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_PRE_CHOL_RATE')) AS g_pre_chol_rate"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_PERI_CHOL_RATE')) AS g_peri_chol_rate"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_POST_CHOL_RATE')) AS g_post_chol_rate"
                + ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_H_OUT_RESIDUAL_CL')) AS g_h_out_residual_cl"
                + ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_H_IN_RESIDUAL_CL')) AS g_h_in_residual_cl"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_H_IN_RESIDUAL_CL_PREP')) AS g_h_in_residual_cl_prep"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_H_RESIDUAL_CL')) AS g_h_residual_cl"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(OUT_VAL, '$.AI_G_EVAP')) AS ai_g_evap"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(OUT_VAL, '$.AI_G_CHOL_RATE')) AS ai_g_chol_rate"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(OUT_VAL, '$.AI_G_CONSUMPTION')) AS ai_g_consumption"
        		+ " FROM " + getTableByProcessStep(processStep, disinfectionStep) + " " +
                "WHERE upd_ti > ? AND upd_ti <= ? ORDER BY upd_ti";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiDisinfectionRealtimeDTO.class)
        );
    }

    @Override
    public AiDisinfectionRealtimeDTO select(int processStep, int disinfectionStep)
    {
        String strQuery = "SELECT UPD_TI, AI_OPR, IN_VAL, OUT_VAL"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_D_RESIDUAL_CL')) AS g_d_residual_cl"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_E_RESIDUAL_CL')) AS g_e_residual_cl"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_F_OUT_RESIDUAL_CL')) AS g_f_out_residual_cl"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_PRE_CHOL_RATE')) AS g_pre_chol_rate"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_PERI_CHOL_RATE')) AS g_peri_chol_rate"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_POST_CHOL_RATE')) AS g_post_chol_rate"
                + ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_H_OUT_RESIDUAL_CL')) AS g_h_out_residual_cl"
                + ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_H_IN_RESIDUAL_CL')) AS g_h_in_residual_cl"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_H_IN_RESIDUAL_CL_PREP')) AS g_h_in_residual_cl_prep"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(IN_VAL, '$.G_H_RESIDUAL_CL')) AS g_h_residual_cl"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(OUT_VAL, '$.AI_G_EVAP')) AS ai_g_evap"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(OUT_VAL, '$.AI_G_CHOL_RATE')) AS ai_g_chol_rate"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(OUT_VAL, '$.G_PUMP_1_RUN')) AS g_pump_1_run"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(OUT_VAL, '$.G_PUMP_2_RUN')) AS g_pump_2_run"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(OUT_VAL, '$.AI_G_CONSUMPTION')) AS ai_g_consumption"
        		+ ", JSON_UNQUOTE(JSON_EXTRACT(OUT_VAL, '$.G_ELAPSED_TIME')) AS g_elapsed_time"
        		+ " FROM " + getTableByProcessStep(processStep, disinfectionStep) + " ORDER BY upd_ti DESC LIMIT 1";
//        		+ " FROM " + getTableByProcessStep(processStep, disinfectionStep) + " ORDER BY upd_ti ASC LIMIT 1"; // TODO DESC변경 필요
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(AiDisinfectionRealtimeDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int delete(Date upd_ti, int processStep, int disinfectionStep)
    {
        String strQuery = "DELETE FROM " + getTableByProcessStep(processStep, disinfectionStep) + " WHERE upd_ti < ?";
        return jdbcTemplate.update(strQuery, upd_ti);
    }
        
    public String getTableByProcessStep(int processStep, int disinfectionStep) {
        String tableNm = "";
        if(processStep == 1 && disinfectionStep == 1) {
            tableNm = "TB_AI_PRE_G_RT";
        } else if(processStep == 1 && disinfectionStep == 3) {
            tableNm = "TB_AI_POST_G_RT";
        }
        return tableNm;
    }
}
