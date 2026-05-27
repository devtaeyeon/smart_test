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
                + " AND TIME = (SELECT DATE_FORMAT(MAX(time), '%Y-%m-%d %H:%i:00')"
                + "             FROM SMART_EMS.TB_PTR_CTR_ANLY_RST)"
                + " AND tag IN ("
                + "   '771-445-EMS-9101'," // 도수(가) 펌프 1 ON/OFF 상태 예측
                + "   '771-445-EMS-9102'," // 도수(가) 펌프 2 ON/OFF 상태 예측
                + "   '771-445-EMS-9103'," // 도수(가) 펌프 3 ON/OFF 상태 예측
                + "   '771-446-EMS-9101'," // 송수(가) 펌프 1 ON/OFF 상태 예측
                + "   '882-602-EMS-9101'," // 용담(가) 펌프 1 ON/OFF 상태 예측
                + "   '882-602-EMS-9102'," // 용담(가) 펌프 2 ON/OFF 상태 예측
                + "   '882-602-EMS-9103'," // 용담(가) 펌프 3 ON/OFF 상태 예측
                + "   '882-603-EMS-9101'," // 운봉(가) 펌프 1 ON/OFF 상태 예측
                + "   '882-603-EMS-9102'," // 운봉(가) 펌프 2 ON/OFF 상태 예측
                + "   '882-603-EMS-9103' " // 운봉(가) 펌프 3 ON/OFF 상태 예측
                + ")"
                + " UNION ALL"
                + " SELECT tag AS tag_sn, value AS tag_val"
                + " FROM SMART_EMS.TB_PTR_CTR_INF"
                + " WHERE 1=1"
                + " AND tag IN ("
                + "   '771-445-EMS-1001'," // 도수(가) AI 운영 ON/OFF 구분
                + "   '771-446-EMS-1001'," // 송수(가) AI 운영 ON/OFF 구분
                + "   '882-602-EMS-1001'," // 용담(가) AI 운영 ON/OFF 구분
                + "   '882-603-EMS-1001' " // 운봉(가) AI 운영 ON/OFF 구분
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
