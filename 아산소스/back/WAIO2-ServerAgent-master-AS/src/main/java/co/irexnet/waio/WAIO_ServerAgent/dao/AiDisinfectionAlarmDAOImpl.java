package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessAlarmDTO;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;

@Repository
public class AiDisinfectionAlarmDAOImpl implements IAiDisinfectionProcessAlarmDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(int alm_id, Date alm_ti, int kfk_flg)
    {
        String strQuery = "insert into TB_AI_G_ALM values (?, ?, ?)";
        return jdbcTemplate.update(strQuery, alm_id, alm_ti, kfk_flg);
    }

    @Override
    public List<AiProcessAlarmDTO> select(Date alm_ti, int kfk_flg, int processStep, int disinfectionIndex) {
        String strQuery = "SELECT ALM_ID, ALM_TI, KFK_FLG, SUBSTRING(ALM_ID, 4, 1) AS PROCESS_STEP, '" + disinfectionIndex + "' AS DISINFECTION_INDEX FROM " 
                        + getTableByProcessStep(processStep, disinfectionIndex) + " WHERE alm_ti>=? AND kfk_flg=? ORDER BY alm_ti ASC";
        return jdbcTemplate.query(
            strQuery,
            new Object[]{alm_ti, kfk_flg},
            new BeanPropertyRowMapper<>(AiProcessAlarmDTO.class)
            );
    }

    @Override
    public int updateKafkaFlag(AiProcessAlarmDTO dto) {
        String strQuery = "UPDATE " + getTableByProcessStep(dto.getProcessStep(), dto.getDisinfectionIndex()) + " SET kfk_flg=? WHERE alm_id=? AND alm_ti=?";
        return jdbcTemplate.update(strQuery, dto.getKfk_flg(), dto.getAlm_id(), dto.getAlm_ti());
    }

    @Override
    public int delete(Date date)
    {
        String strQuery = "DELETE FROM TB_AI_G_ALM WHERE alm_ti<?";
        return jdbcTemplate.update(strQuery, date);
    }

    public String getTableByProcessStep(int processStep, int disinfectionIndex) {
        String tableNm = "";
        if(processStep == 1 && disinfectionIndex == CommonValue.DISINFECTION_PRE_STEP) { // 1단계공업 전차염
            tableNm = "TB_AI_PRE_G1_ALM";
        } else if(processStep == 2 && disinfectionIndex == CommonValue.DISINFECTION_PRE_STEP) { // 2단계생활 전차염
            tableNm = "TB_AI_PRE_G2_ALM";
        } else if(processStep == 2 && disinfectionIndex == CommonValue.DISINFECTION_POST_STEP) { // 2단계생활 후차염
            tableNm = "TB_AI_POST_G2_ALM";
        } else if(processStep == 3 && disinfectionIndex == CommonValue.DISINFECTION_PRE_STEP) { // 3단계공업 전차염
            tableNm = "TB_AI_PRE_G3_ALM";
        }
        return tableNm;
    }
}
