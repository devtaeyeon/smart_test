package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;

@Repository
public class DisinfectionRealtimeDAOImpl implements IProcessRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(List<ProcessRealtimeDTO> list)
    {
        int[] result = jdbcTemplate.batchUpdate(
                "INSERT IGNORE INTO TB_G_RT VALUES(?, ?, ?, ?);",
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
    public List<ProcessRealtimeDTO> select(String partitiontag_sn, int processStep)
    {
        String strQuery = String.format("SELECT * FROM " + getTableByProcessStep(processStep) + " PARTITION(p_%s) WHERE upd_ti IN " +
                "(SELECT MAX(upd_ti) FROM " + getTableByProcessStep(processStep) + " PARTITION(p_%s) GROUP BY tag_sn)", partitiontag_sn, partitiontag_sn);

        try
        {
            return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(ProcessRealtimeDTO.class));
        }
        catch(DataAccessException e)
        {
            strQuery = String.format("SELECT * FROM " + getTableByProcessStep(processStep) + " PARTITION(p_max) WHERE upd_ti IN " +
                    "(SELECT MAX(upd_ti) FROM " + getTableByProcessStep(processStep) + " PARTITION(p_max) GROUP BY tag_sn)");

            return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(ProcessRealtimeDTO.class));
        }
    }

    @Override
    public List<ProcessRealtimeDTO> select(Date start_time, int processStep)
    {
        String strQuery = "SELECT * FROM " + getTableByProcessStep(processStep) + " WHERE upd_ti > ? ORDER BY upd_ti DESC";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time},
                new BeanPropertyRowMapper<>(ProcessRealtimeDTO.class)
        );
    }

    @Override
    public List<ProcessRealtimeDTO> select(String tag_sn, Date start_time, Date end_time, int processStep)
    {
        String strQuery = "SELECT * FROM " + getTableByProcessStep(processStep) + " WHERE tag_sn=? AND upd_ti > ? AND upd_ti < ?";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{tag_sn, start_time, end_time},
                new BeanPropertyRowMapper<>(ProcessRealtimeDTO.class)
        );
    }

    @Override
    public ProcessRealtimeDTO selectLatest(String tag_sn, int processStep)
    {
        String strQuery =  "SELECT * FROM " + getTableByProcessStep(processStep) + " WHERE tag_sn=? ORDER BY upd_ti DESC limit 1";
        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery,
                    new Object[]{tag_sn},
                    new BeanPropertyRowMapper<>(ProcessRealtimeDTO.class)
            );
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public void addPartition(String partitiontag_sn, String end_time)
    {
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            String strQuery = String.format("ALTER TABLE TB_G" + processStep + "_RT REORGANIZE PARTITION p_max INTO (" +
                    "PARTITION p_%s VALUES LESS THAN('%s') ENGINE = INNODB, " +
                    "PARTITION p_max VALUES LESS THAN MAXVALUE ENGINE = INNODB);", partitiontag_sn, end_time);
            jdbcTemplate.execute(strQuery);
        }
    }

    @Override
    public void dropPartition(String partitiontag_sn)
    {
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            String strQuery = String.format("ALTER TABLE TB_G" + processStep + "_RT DROP PARTITION p_%s", partitiontag_sn);
            jdbcTemplate.execute(strQuery);
        }
    }

    public String getTableByProcessStep(int processStep) {
        String tableNm = "";
        if(processStep == 1) { // 1단계공업
            tableNm = "TB_G1_RT";
        } else if(processStep == 2) { // 2단계생활
            tableNm = "TB_G2_RT";
        } else if(processStep == 3) { // 3단계공업
            tableNm = "TB_G3_RT";
        }
        return tableNm;
    }
}
