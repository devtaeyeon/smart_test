package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessAlarmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiCoagulantAlarmDAOImpl implements IAiProcessAlarmDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(int alm_id, Date alm_ti, int kfk_flg, int processStep)
    {
        String strQuery = "insert into "+getTableByProcessStep(processStep)+" values (?, ?, ?)";
        return jdbcTemplate.update(strQuery, alm_id, alm_ti, kfk_flg);
    }

    @Override
    public List<AiProcessAlarmDTO> select(Date alm_ti, int kfk_flg, int processStep)
    {
        String strQuery = "SELECT * FROM "+getTableByProcessStep(processStep)+" WHERE alm_ti>=? AND kfk_flg=? ORDER BY alm_ti ASC";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{alm_ti, kfk_flg},
                new BeanPropertyRowMapper<>(AiProcessAlarmDTO.class)
        );
    }

    @Override
    public int updateKafkaFlag(int alm_id, Date alm_ti, int kfk_flg, int processStep)
    {
        String strQuery = "UPDATE "+getTableByProcessStep(processStep)+" SET kfk_flg=? WHERE alm_id=? AND alm_ti=?";
        return jdbcTemplate.update(strQuery, kfk_flg, alm_id, alm_ti);
    }

    @Override
    public int delete(Date date, int processStep)
    {
        String strQuery = "DELETE FROM "+getTableByProcessStep(processStep)+" WHERE alm_ti<?";
        return jdbcTemplate.update(strQuery, date);
    }
    
    public String getTableByProcessStep(int processStep) {
    	String tableNm = "";
    	if(processStep == 1) {
    		tableNm = "TB_AI_C_ALM";
    	}
    	return tableNm;
    }
}
