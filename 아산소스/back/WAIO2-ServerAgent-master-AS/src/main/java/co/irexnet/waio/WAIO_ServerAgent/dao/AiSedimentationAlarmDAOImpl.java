package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessAlarmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiSedimentationAlarmDAOImpl implements IAiProcessAlarmDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(int alm_id, Date alm_ti, int kfk_flg)
    {
        String strQuery = "insert into TB_AI_E_ALM values (?, ?, ?)";
        return jdbcTemplate.update(strQuery, alm_id, alm_ti, kfk_flg);
    }

    @Override
    public List<AiProcessAlarmDTO> select(Date alm_ti, int kfk_flg, int processStep)
    {
        String strQuery = "SELECT ALM_ID, ALM_TI, KFK_FLG, SUBSTRING(ALM_ID, 4, 1) AS PROCESS_STEP FROM " + getTableByProcessStep(processStep) + " WHERE alm_ti>=? AND kfk_flg=? ORDER BY alm_ti ASC";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{alm_ti, kfk_flg},
                new BeanPropertyRowMapper<>(AiProcessAlarmDTO.class)
        );
    }

    @Override
    public int updateKafkaFlag(AiProcessAlarmDTO dto) {
        String strQuery = "UPDATE " + getTableByProcessStep(dto.getProcessStep()) + " SET kfk_flg=? WHERE alm_id=? AND alm_ti=?";
        return jdbcTemplate.update(strQuery, dto.getKfk_flg(), dto.getAlm_id(), dto.getAlm_ti());
    }

    @Override
    public int delete(Date date) {
        String strQuery = "DELETE FROM TB_AI_E_ALM WHERE alm_ti<?";
        return jdbcTemplate.update(strQuery, date);
    }

    public String getTableByProcessStep(int processStep) {
        String tableNm = "";
        if(processStep == 1) {
            tableNm = "TB_AI_E1_ALM";
        } else if(processStep == 2) {
            tableNm = "TB_AI_E2_ALM";
        } else if(processStep == 3) {
            tableNm = "TB_AI_E3_ALM";
        }
        return tableNm;
    }
}
