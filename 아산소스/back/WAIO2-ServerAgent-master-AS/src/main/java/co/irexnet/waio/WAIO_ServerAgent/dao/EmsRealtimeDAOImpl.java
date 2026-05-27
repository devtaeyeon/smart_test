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
        		+ " AND TS = (SELECT DATE_FORMAT(MAX(ts),'%Y-%m-%d %H:%i:00')"
        		+ "          FROM SMART_EMS.TB_DATA_RAW_TAG"
        		+ "		 )"
        		+ " AND TAGNAME IN ("
        		+ "	'606-485-PMB-8528'," //당진공업1번 펌프"
        		+ "	'606-485-PMB-8529'," //당진공업2번 펌프"
        		+ "	'606-485-PMB-8530'," //당진공업3번 펌프"
        		+ "	'606-485-PMB-8531'," //당진공업4번 펌프"
        		+ "	'606-485-PMB-8532'," //당진공업5번 펌프"
        		+ "	'606-485-PMB-8006'," //당진생활1번 펌프"
        		+ "	'606-485-PMB-8011'," //당진생활2번 펌프"
        		+ "	'606-485-PMB-8054'," //당진생활3번 펌프"
        		+ "	'606-485-PMB-8059'," //당진생활4번 펌프"
        		+ "	'606-485-SPB-9103'," //당진공업1번 주파수(Hz)"
        		+ "	'606-485-SPB-9104'," //당진공업2번 주파수(Hz)"
        		+ "	'606-485-PMI-8010'," //당진생활1번 주파수(Hz)"
        		+ "	'606-485-PMI-8026'," //당진생활2번 주파수(Hz)"
        		+ "	'606-485-PMI-8045'," //당진생활3번 주파수(Hz)"
        		+ "	'606-485-PMI-8053'"  //당진생활4번 주파수(Hz)"
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
