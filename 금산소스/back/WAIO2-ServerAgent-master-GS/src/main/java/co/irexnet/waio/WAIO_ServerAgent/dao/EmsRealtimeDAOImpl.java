package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessRealtimeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class EmsRealtimeDAOImpl implements IProcessRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(List<ProcessRealtimeDTO> list)
    {
        int[] result = jdbcTemplate.batchUpdate(
                "INSERT INTO ems_realtime VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                        "update_time = VALUES(update_time), value = VALUES(value);",
                new BatchPreparedStatementSetter()
                {
                    public void setValues(PreparedStatement ps, int i) throws SQLException
                    {
                        ps.setTimestamp(1, new java.sql.Timestamp(list.get(i).getUpd_ti().getTime()));
                        ps.setString(2, list.get(i).getTag_sn());
                        ps.setString(3, list.get(i).getTag_val());
                        ps.setInt(4, list.get(i).getQlt().intValue());
                    }
                    public int getBatchSize() { return list.size(); }
                }
        );
        return result.length;
    }

    @Override
    public List<ProcessRealtimeDTO> select(String partitionName, int processStep)
    {
    	String strQuery = "SELECT TAGNAME AS tag_sn, VALUE AS tag_val, TS AS upd_ti, QUALITY AS qlt"
                + " FROM SMART_EMS.TB_DATA_RAW_TAG"
                + " WHERE 1=1"
                + " AND TS = (SELECT DATE_FORMAT(MAX(ts), '%Y-%m-%d %H:%i:00')"
                + "           FROM SMART_EMS.TB_DATA_RAW_TAG)"
                + " AND TAGNAME IN ("
                + "   '771-445-XXB-8112',"  // 도수(가) 펌프 1 ON/OFF 상태
                + "   '771-445-XXB-8212',"  // 도수(가) 펌프 2 ON/OFF 상태
                + "   '771-445-XXB-8312',"  // 도수(가) 펌프 3 ON/OFF 상태
                + "   '771-446-XXB-8112',"  // 송수(가) 펌프 1 ON/OFF 상태
                + "   '882-602-PMB-8002',"  // 용담(가) 펌프 1 ON/OFF 상태
                + "   '882-602-PMB-8004',"  // 용담(가) 펌프 2 ON/OFF 상태
                + "   '882-602-PMB-8006',"  // 용담(가) 펌프 3 ON/OFF 상태
                + "   '882-603-PMB-8002',"  // 운봉(가) 펌프 1 ON/OFF 상태
                + "   '882-603-PMB-8004',"  // 운봉(가) 펌프 2 ON/OFF 상태
                + "   '882-603-PMB-8006' "  // 운봉(가) 펌프 3 ON/OFF 상태
                + ")";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(ProcessRealtimeDTO.class));
    }

    @Override
    public List<ProcessRealtimeDTO> select(Date start_time, int processStep)
    {
        // Do anything
        return null;
    }

    @Override
    public List<ProcessRealtimeDTO> select(String name, Date start_time, Date end_time, int processStep)
    {
        // Do anything
        return null;
    }

    @Override
    public ProcessRealtimeDTO selectLatest(String name, int processStep)
    {
        // Do anything
        return null;
    }

    @Override
    public void addPartition(String partitionName, String end_time)
    {
        // Do anything
    }

    @Override
    public void dropPartition(String partitionName)
    {
        // Do anything
    }
}
