package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAlarmControlHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class MixingRealtimeDAOImpl implements IProcessRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(List<ProcessRealtimeDTO> list)
    {
        int[] result = jdbcTemplate.batchUpdate(
                "INSERT IGNORE INTO TB_D2_RT VALUES(?, ?, ?, ?);",
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
        // String strQuery = String.format("SELECT * FROM TB_D2_RT PARTITION(p_%s) WHERE upd_ti IN " +
        //         "(SELECT MAX(upd_ti) FROM TB_D2_RT PARTITION(p_%s) GROUP BY tag_sn)", partitionName, partitionName);
        
        String strQuery = String.format("SELECT * FROM " + getTableByProcessStep(processStep) + 
        " PARTITION(p_%s) WHERE upd_ti IN (SELECT MAX(upd_ti) FROM " + getTableByProcessStep(processStep) + " PARTITION(p_%s) GROUP BY tag_sn)", partitionName, partitionName);

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
    public void addPartition(String partitionName, String end_time)
    {
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            String strQuery = String.format("ALTER TABLE TB_D" + processStep + "_RT REORGANIZE PARTITION p_max INTO (" +
                "PARTITION p_%s VALUES LESS THAN('%s') ENGINE = INNODB, " +
                "PARTITION p_max VALUES LESS THAN MAXVALUE ENGINE = INNODB);", partitionName, end_time);
            jdbcTemplate.execute(strQuery);
        }
        
    }

    @Override
    public void dropPartition(String partitionName)
    {
        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            String strQuery = String.format("ALTER TABLE TB_D" + processStep + "_RT DROP PARTITION p_%s", partitionName);
            jdbcTemplate.execute(strQuery);
        }
    }

    public List<InterfaceAlarmControlHistoryDTO> selectAllRealTime(LocalDateTime today) {
        Date date = Date.from(today.atZone(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterdayDate = calendar.getTime();
        
        String todayPartition = partitionNameFormat.format(date);
        String yesterdayPartition = partitionNameFormat.format(yesterdayDate);
        
        
        StringBuffer strQuery = new StringBuffer();
        String[] processArr = { 
             CommonValue.PROCESS_COAGULANT
            , CommonValue.PROCESS_MIXING
            , CommonValue.PROCESS_DISINFECTION
        };
        
        strQuery.append("WITH TB_TMP AS ( SELECT T.PROCESS, T.UPD_TI FROM ( ");

        for (int processStep : CommonValue.PROCESS_STEP_ARRAY) {
            for (String process : processArr) {
                strQuery.append("(SELECT '").append(process)
                        .append("' AS PROCESS, MAX(UPD_TI) AS UPD_TI FROM TB_")
                        .append(process).append("_RT PARTITION(p_")
                        .append(todayPartition).append(", p_").append(yesterdayPartition)
                        .append(") GROUP BY TAG_SN LIMIT 1)")
                        .append(" UNION ALL \n");
            }
        }
        strQuery.setLength(strQuery.lastIndexOf("UNION ALL"));
        strQuery.append(" ) T ) SELECT * FROM TB_TMP WHERE TIMESTAMPDIFF(MINUTE, UPD_TI, ?) >= 5 ");
        strQuery.append("UNION ALL SELECT NULL PROCESS, ? AS UPD_TI FROM DUAL ");
        strQuery.append("WHERE NOT EXISTS ( SELECT * FROM TB_TMP )");
        
//        System.out.println(date);
        try {
            return jdbcTemplate.query(strQuery.toString(), new Object[]{date, date}, new BeanPropertyRowMapper<>(InterfaceAlarmControlHistoryDTO.class));
        } catch (DataAccessException e) {
            strQuery.append("SELECT NULL PROCESS, ? AS UPD_TI FROM DUAL");
            return jdbcTemplate.query(strQuery.toString(), new Object[]{date}, new BeanPropertyRowMapper<>(InterfaceAlarmControlHistoryDTO.class));
        }
    }


    public String getTableByProcessStep(int processStep) {
        String tableNm = "";
        if(processStep == 1) {
            tableNm = "TB_D_RT";
        }
        return tableNm;
    }
}
