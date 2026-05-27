package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationInterfaceRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiSedimentationRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.ai_dto.FrequencyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiSedimentationRealtimeDAOImpl implements IAiSedimentationRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public List<AiSedimentationRealtimeDTO> select(Date start_time, Date end_time, int processStep)
    {
        String strQuery = "SELECT * FROM " + getTableByProcessStep(processStep) + " WHERE upd_ti > ? AND upd_ti <= ? ORDER BY upd_ti";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiSedimentationRealtimeDTO.class)
        );
    }

    @Override
    public List<FrequencyDTO> selectE1Tb(Date start_time, int processStep)
    {
        String strQuery = "select truncate(TBI_2001, 2) as value, count(truncate(TBI_2001, 2)) as count " +
                "from " + getTableByProcessStep(processStep) + " where upd_ti > ? group by truncate(TBI_2001, 2)";
        return jdbcTemplate.query(strQuery, new Object[]{start_time}, new BeanPropertyRowMapper<>(FrequencyDTO.class));
    }

    @Override
    public List<FrequencyDTO> selectE2Tb(Date start_time, int processStep)
    {
        String strQuery = "select truncate(TBI_2002, 2) as value, count(truncate(TBI_2002, 2)) as count " +
                "from " + getTableByProcessStep(processStep) + " where upd_ti > ? group by truncate(TBI_2002, 2)";
        return jdbcTemplate.query(strQuery, new Object[]{start_time}, new BeanPropertyRowMapper<>(FrequencyDTO.class));
    }

    @Override
    public List<FrequencyDTO> selectDistribution(Date start_time, String tag_sn, int processStep)
    {
        String strQuery = "select truncate(value, 2) as value, count(truncate(value, 2)) as count " +
                "FROM " + getTableByProcessStep(processStep) + " where upd_ti > ? and tag_sn = ? group by truncate(value, 2)";
        return jdbcTemplate.query(strQuery, new Object[]{start_time, tag_sn}, new BeanPropertyRowMapper<>(FrequencyDTO.class));
    }

    @Override
    public List<AiSedimentationInterfaceRealtimeDTO> selectInterface(Date start_time, Date end_time, int processStep)
    {
        String strQuery = "SELECT upd_ti, AIE_9901, AIE_9902 FROM " + getTableByProcessStep(processStep) + 
                " WHERE upd_ti > ? AND upd_ti <= ? ORDER BY upd_ti";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiSedimentationInterfaceRealtimeDTO.class)
        );
    }

    @Override
    public AiSedimentationRealtimeDTO select(int processStep)
    {
    	String strQuery = "SELECT UPD_TI, AI_OPR, IN_VAL, OUT_VAL"
        + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].b_in_fr'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].b_in_fr'))-2) AS b_in_fr"
    	+ ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].b_tb'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].b_tb'))-2) AS b_tb"
    	+ ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr'))-2) AS c_mm_fr"
    	+ ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr1'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr1'))-2) AS c_mm_fr1"
    	+ ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr2'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr2'))-2) AS c_mm_fr2"
    	+ ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr3'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr3'))-2) AS c_mm_fr3"
    	+ ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr_etc'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr_etc'))-2) AS c_mm_fr_etc"
    	+ ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr_etc1'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr_etc1'))-2) AS c_mm_fr_etc1"
    	+ ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr_etc2'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr_etc2'))-2) AS c_mm_fr_etc2"
    	// + ", SUBSTR(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr_etc3'), 2, LENGTH(JSON_EXTRACT(IN_VAL, '$[*].c_mm_fr_etc3'))-2) AS c_mm_fr_etc3"
        + ", CONCAT('{\"AIE-9001\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-5200'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-5200'))-2),'}') AS AIE_5200"
        + ", SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-5300'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-5300'))-2) AS AIE_5300"
    	+ ", CONCAT('{\"AIE-9001\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9001'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9001'))-2),'}') AS AIE_9001"
        + ", CONCAT('{\"AIE-9002\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9002'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9002'))-2),'}') AS AIE_9002"
        + ", CONCAT('{\"AIE-9003\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9003'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9003'))-2),'}') AS AIE_9003"
        + ", CONCAT('{\"AIE-9004\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9004'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9004'))-2),'}') AS AIE_9004"
        + ", CONCAT('{\"AIE-9005\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9005'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9005'))-2),'}') AS AIE_9005"
        + ", CONCAT('{\"AIE-9006\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9006'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9006'))-2),'}') AS AIE_9006"
        + ", CONCAT('{\"AIE-9007\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9007'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9007'))-2),'}') AS AIE_9007"
        + ", CONCAT('{\"AIE-9008\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9008'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9008'))-2),'}') AS AIE_9008"
        + ", CONCAT('{\"AIE-9009\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9009'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9009'))-2),'}') AS AIE_9009"
        + ", CONCAT('{\"AIE-9010\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9010'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9010'))-2),'}') AS AIE_9010"
        + ", CONCAT('{\"AIE-9011\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9011'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9011'))-2),'}') AS AIE_9011"
        + ", CONCAT('{\"AIE-9012\":', SUBSTR(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9012'), 2, LENGTH(JSON_EXTRACT(OUT_VAL, '$[*].AIE-9012'))-2),'}') AS AIE_9012 FROM " 
        + getTableByProcessStep(processStep) + " ORDER BY upd_ti DESC LIMIT 1";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(AiSedimentationRealtimeDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }

    }

    @Override
    public int delete(Date upd_ti, int processStep)
    {
        String strQuery = "DELETE FROM " + getTableByProcessStep(processStep) + " WHERE upd_ti < ?";
        return jdbcTemplate.update(strQuery, upd_ti);
    }

    public String getTableByProcessStep(int processStep) {
        String tableNm = "";
        if(processStep == 1) {
            tableNm = "TB_AI_E1_RT";
        } else if(processStep == 2) {
            tableNm = "TB_AI_E2_RT";
        } else if(processStep == 3) {
            tableNm = "TB_AI_E3_RT";
        }
        return tableNm;
    }
}
