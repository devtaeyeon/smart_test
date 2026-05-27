package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessAlarmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiOzoneAlarmDAOImpl implements IAiProcessAlarmDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(int alm_id, Date alm_ti, int kfk_flg, int processStep)
    {
        String strQuery = "insert into TB_AI_IO_ALM values (?, ?, ?)";
        return jdbcTemplate.update(strQuery, alm_id, alm_ti, kfk_flg);
    }

    @Override
    public List<AiProcessAlarmDTO> select(Date alm_ti, int kfk_flg, int processStep)
    {
        String strQuery = "SELECT * FROM TB_AI_IO_ALM WHERE alm_ti>=? AND kfk_flg=? ORDER BY alm_ti DESC";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{alm_ti, kfk_flg},
                new BeanPropertyRowMapper<>(AiProcessAlarmDTO.class)
        );
    }

    @Override
    public int updateKafkaFlag(int alm_id, Date alm_ti, int kfk_flg, int processStep)
    {
        String strQuery = "UPDATE TB_AI_IO_ALM SET kfk_flg=? WHERE alm_id=? AND alm_ti=?";
        return jdbcTemplate.update(strQuery, kfk_flg, alm_id, alm_ti);
    }

    @Override
    public int delete(Date date, int processStep)
    {
        String strQuery = "DELETE FROM TB_AI_IO_ALM WHERE alm_ti<?";
        return jdbcTemplate.update(strQuery, date);
    }
}
