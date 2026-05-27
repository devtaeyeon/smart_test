package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiEmsRealtimeDTO;

@Repository
public class AiEmsRealtimeDAOImpl implements IAiEmsRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiEmsRealtimeDTO> select()
    {
        String strQuery = "SELECT tag AS tag_sn, value AS tag_val"
        		+ " FROM SMART_EMS.TB_PTR_CTR_ANLY_RST"
        		+ " WHERE 1=1"
        		+ " AND TIME = (SELECT DATE_FORMAT(MAX(time),'%Y-%m-%d %H:%i:00')"
        		+ "            FROM SMART_EMS.TB_PTR_CTR_ANLY_RST"
        		+ "			)"
        		+ " AND tag IN ("
        		+ "	'606-354-EMS-9101'," //당진공업1"
        		+ "	'606-354-EMS-9102'," //당진공업2"
        		+ "	'606-354-EMS-9103'," //당진공업3"
        		+ "	'606-354-EMS-9104'," //당진공업4"
        		+ "	'606-354-EMS-9105'," //당진공업5"
        		+ "	'606-354-EMS-9301'," //당진생활1"
        		+ "	'606-354-EMS-9302'," //당진생활2"
        		+ "	'606-354-EMS-9303'," //당진생활3"
        		+ "	'606-354-EMS-9304'," //당진생활4"
        		+ "	'606-354-EMS-9201'," //당진공업1주파수"
        		+ "	'606-354-EMS-9202'," //당진공업2주파수"
        		+ "	'606-354-EMS-9401'," //당진생활1주파수"
        		+ "	'606-354-EMS-9402'," //당진생활2주파수"
        		+ "	'606-354-EMS-9403'," //당진생활3주파수"
        		+ "	'606-354-EMS-9404' " //당진생활4주파수"
        		+ ")"
        		+ " UNION ALL"
        		+ " SELECT tag AS tag_sn, value AS tag_val"
        		+ " FROM SMART_EMS.TB_PTR_CTR_INF "
        		+ " WHERE 1=1"
        		+ " AND tag IN ("
        		+ "	'606-485-EMS-1001'," //당진공업운전모드
        		+ "	'606-485-EMS-2001'"  //당진생활운전모드
        		+ ")";
        try
        {
            return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AiEmsRealtimeDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }
}
