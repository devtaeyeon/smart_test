package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessControlDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiOzoneControlDAOImpl implements IAiProcessControlDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public AiProcessControlDTO select(Date upd_ti, Date rnti, String tag_sn, int kfk_flg, int processStep)
    {
        String strQuery = "SELECT * FROM TB_AI_IO_CTR WHERE upd_ti=? AND rnti=? AND tag_sn=? AND kfk_flg=?";

        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery,
                    new Object[]{upd_ti, rnti, tag_sn, kfk_flg},
                    new BeanPropertyRowMapper<>(AiProcessControlDTO.class)
            );
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public List<AiProcessControlDTO> select(Date rnti, int kfk_flg, int processStep)
    {
        String strQuery = "SELECT * FROM " +
                "(SELECT * FROM TB_AI_IO_CTR WHERE rnti>=? AND kfk_flg=? ORDER BY rnti DESC LIMIT 100) " +
                "TB_AI_IO_CTR GROUP BY tag_sn";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{rnti, kfk_flg},
                new BeanPropertyRowMapper<>(AiProcessControlDTO.class)
        );
    }

    @Override
    public int updateKafkaFlag(Date upd_ti, Date rnti, String tag_sn, int kfk_flg, int processStep)
    {
        String strQuery = "UPDATE TB_AI_IO_CTR SET kfk_flg=? WHERE upd_ti=? AND rnti=? AND tag_sn=?";
        return jdbcTemplate.update(
                strQuery,
                kfk_flg, upd_ti, rnti, tag_sn
        );
    }

    @Override
    public int delete(Date date, int processStep)
    {
        String strQuery = "DELETE FROM TB_AI_IO_CTR WHERE upd_ti < ?";
        return jdbcTemplate.update(strQuery, date);
    }
}
