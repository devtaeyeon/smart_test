package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessControlDTO;

@Repository
public class AiCoagulantControlDAOImpl implements IAiProcessControlDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public AiProcessControlDTO select(Date upd_ti, Date rnti, String tag_sn, int kfk_flg, int processStep)
    {
        String strQuery = "SELECT * FROM " + getTableByProcessStep(processStep) + " WHERE upd_ti=? and rnti=? and tag_sn=? and kfk_flg=?";
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
                "(SELECT * FROM " + getTableByProcessStep(processStep) + " WHERE rnti>=? and kfk_flg=? ORDER BY rnti DESC LIMIT 100) " 
                + getTableByProcessStep(processStep) + " GROUP BY tag_sn ORDER BY rnti";
                
        return jdbcTemplate.query (
                strQuery,
                new Object[]{rnti, kfk_flg},
                new BeanPropertyRowMapper<>(AiProcessControlDTO.class)
        );
    }

    @Override
    public int updateKafkaFlag(Date upd_ti, Date rnti, String tag_sn, int kfk_flg, int processStep)
    {
        String strQuery = "UPDATE " + getTableByProcessStep(processStep) + " SET kfk_flg=? WHERE upd_ti=? and rnti=? and tag_sn=?";
        return jdbcTemplate.update(
                strQuery,
                kfk_flg, upd_ti, rnti, tag_sn
        );
    }

    @Override
    public int delete(Date date, int processStep)
    {
        String strQuery = "DELETE FROM " + getTableByProcessStep(processStep) + " WHERE upd_ti < ?";
        return jdbcTemplate.update(strQuery, date);
    }


    public int update(String item, String value) {
        String strQuery = "UPDATE TB_USR_MNG SET init_val=? WHERE itm=? ";
        return jdbcTemplate.update(
                strQuery,
                value,
                item 
        );
    }

    public Map<String, Object> selectUsrMng(String item) {
        String strQuery = "SELECT REPLACE(ITM, ITM, 'c_cf_coagulant') AS ITM, TAG_SN, INIT_VAL FROM TB_USR_MNG WHERE itm = ?";
        try {
            return jdbcTemplate.queryForMap(
                    strQuery,
                    new Object[]{item}
            );
        } catch(EmptyResultDataAccessException e) {
            return new HashMap<String, Object>();
        }
    }

    public String getTableByProcessStep(int processStep) {
        String tableNm = "";
        if(processStep == 1) {
            tableNm = "TB_AI_C1_CTR";
        } else if(processStep == 2) {
            tableNm = "TB_AI_C2_CTR";
        } else if(processStep == 3) {
            tableNm = "TB_AI_C3_CTR";
        }
        return tableNm;
    }
}
