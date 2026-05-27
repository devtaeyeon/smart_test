package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessAlarmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiDisinfectionAlarmDAOImpl implements IAiDisinfectionAlarmDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(int alm_id, Date alm_ti, int kfk_flg, int processStep, int disinfectionStep)
    {
        String strQuery = "insert into "+getTableByProcessStep(processStep, disinfectionStep)+" values (?, ?, ?)";
        return jdbcTemplate.update(strQuery, alm_id, alm_ti, kfk_flg);
    }

    @Override
    public List<AiProcessAlarmDTO> select(Date alm_ti, int kfk_flg, int processStep, int disinfectionStep)
    {
        String strQuery = "SELECT * FROM "+getTableByProcessStep(processStep, disinfectionStep)+" WHERE alm_ti>=? AND kfk_flg=? ORDER BY alm_ti ASC";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{alm_ti, kfk_flg},
                new BeanPropertyRowMapper<>(AiProcessAlarmDTO.class)
        );
    }

    @Override
    public int updateKafkaFlag(int alm_id, Date alm_ti, int kfk_flg, int processStep, int disinfectionStep)
    {
        String strQuery = "UPDATE "+getTableByProcessStep(processStep, disinfectionStep)+" SET kfk_flg=? WHERE alm_id=? AND alm_ti=?";
        return jdbcTemplate.update(strQuery, kfk_flg, alm_id, alm_ti);
    }

    @Override
    public int delete(Date date, int processStep, int disinfectionStep)
    {
        String strQuery = "DELETE FROM "+getTableByProcessStep(processStep, disinfectionStep)+" WHERE alm_ti<?";
        return jdbcTemplate.update(strQuery, date);
    }
    
    public String getTableByProcessStep(int processStep, int disinfectionStep) {
    	String tableNm = "";
        if(processStep == 1 && disinfectionStep == 1) {
            tableNm = "TB_AI_PRE_G_ALM";
        } else if(processStep == 1 && disinfectionStep == 3) {
            tableNm = "TB_AI_POST_G_ALM";
        }
    	return tableNm;
    }
}
