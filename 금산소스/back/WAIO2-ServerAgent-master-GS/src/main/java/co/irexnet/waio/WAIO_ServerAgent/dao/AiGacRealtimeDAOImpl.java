package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiGacRealtimeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiGacRealtimeDAOImpl implements IAiGacRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiGacRealtimeDTO> select(Date start_time, Date end_time)
    {
        String strQuery = "SELECT * FROM TB_AI_I_RT " +
                "WHERE upd_ti > ? AND upd_ti <= ? ORDER BY upd_ti";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiGacRealtimeDTO.class)
        );
    }

    @Override
    public AiGacRealtimeDTO select()
    {
        String strQuery = "SELECT * FROM TB_AI_I_RT ORDER BY upd_ti DESC LIMIT 1";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(AiGacRealtimeDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int delete(Date upd_ti)
    {
        String strQuery = "DELETE FROM TB_AI_I_RT WHERE upd_ti < ?";
        return jdbcTemplate.update(strQuery, upd_ti);
    }
}
