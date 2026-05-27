package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessRealtimeDTO;

@Repository
public class LoadProcessRealtimeDAOImpl implements ILoadProcessRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    public int insert(String procCd, List<ProcessRealtimeDTO> list)
    {
        if (list == null) {
            throw new IllegalArgumentException("list parameter is null");
        }
        int[] result = jdbcTemplate.batchUpdate(
                "INSERT IGNORE INTO TB_"+ procCd +"_RT VALUES(?, ?, ?, ?);",
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
    public void addPartition(String procCd, List<String> partitionNameList)
    {
        String strQuery = String.format("ALTER TABLE TB_%s_RT REORGANIZE PARTITION p_max INTO (", procCd);
        String pmaxStrQuery = "PARTITION p_max VALUES LESS THAN MAXVALUE ENGINE = INNODB);";

        for (String partitionName : partitionNameList) {
            LocalDate date = LocalDate.parse(partitionName.replace("p_", ""), DateTimeFormatter.BASIC_ISO_DATE);
            LocalDate nextDay = date.plusDays(1);

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
            String outputDateStr = nextDay.format(outputFormatter);

            String midstrQuery = String.format("PARTITION %s VALUES LESS THAN('%s') ENGINE = INNODB, ", partitionName, outputDateStr);
            strQuery = strQuery + midstrQuery;
        }
        strQuery = strQuery + pmaxStrQuery;
        jdbcTemplate.execute(strQuery);
    }

    public List<String> getAddPartitionList(String procCd)
    {
        String database = datasourceUrl.split("\\?")[0].split("/")[datasourceUrl.split("/").length-1];

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // String endPartitionNm = currentDate.format(formatter);

        LocalDate startDate = currentDate.minusDays(7);
        // String startPartitionNm = startDate.format(formatter);

        String strQuery = String.format("select max(PARTITION_NAME) from information_schema.PARTITIONS " +
                "where TABLE_SCHEMA = '%s' " +
                "and TABLE_NAME = 'TB_%s_RT' " +
                "and PARTITION_NAME <> 'p_max'", database, procCd);
        String maxPartitionNm = jdbcTemplate.queryForObject(strQuery, String.class);
        LocalDate maxPartitionDate = LocalDate.parse(maxPartitionNm.replace("p_", ""), DateTimeFormatter.BASIC_ISO_DATE);

        List<String> addPartitionList = new ArrayList<>();
        LocalDate tempDate = startDate;
        if(maxPartitionDate.compareTo(startDate) >= 0 && maxPartitionDate.compareTo(currentDate) < 0) {
            tempDate = maxPartitionDate.plusDays(1);
        } else if (maxPartitionDate.compareTo(currentDate) == 0) {
            return addPartitionList;
        }
        while (!tempDate.isAfter(currentDate)) {
            String partitionNm = "p_" + tempDate.format(formatter);
            addPartitionList.add(partitionNm);
            tempDate = tempDate.plusDays(1);
        }
        return addPartitionList;
    }

    public void dropPartition(String procCd, List<String> partitionNameList)
    {
        String partitionNameStr = String.join(", ", partitionNameList);
        String strQuery = String.format("ALTER TABLE TB_" + procCd + "_RT DROP PARTITION %s", partitionNameStr);
        jdbcTemplate.execute(strQuery);
    }

    public List<String> getDropPartitionList(String procCd, String partitionNm)
    {
        String database = datasourceUrl.split("\\?")[0].split("/")[datasourceUrl.split("/").length-1];

        String strQuery = String.format("select PARTITION_NAME from information_schema.PARTITIONS " +
                "where TABLE_SCHEMA = '%s' " +
                "and TABLE_NAME = 'TB_%s_RT' " +
                "and PARTITION_NAME < '%s' " +
                "and PARTITION_NAME <> 'p_max'", database, procCd, partitionNm);
        return jdbcTemplate.queryForList(strQuery, String.class);
    }
}
