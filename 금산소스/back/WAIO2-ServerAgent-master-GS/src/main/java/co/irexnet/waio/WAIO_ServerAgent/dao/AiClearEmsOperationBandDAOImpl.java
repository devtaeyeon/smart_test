package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiClearOperationBandDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiClearEmsOperationBandDAOImpl implements IAiClearOperationBandDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiClearOperationBandDTO> select(Date start_time, Date end_time, int processStep)
    {
        String strQuery = "SELECT * FROM TB_AI_H_EMS_OPR_BND " +
                "WHERE `ti_seq` > ? and `ti_seq` <= ? ORDER BY `ti_seq`";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiClearOperationBandDTO.class)
        );
    }
}
