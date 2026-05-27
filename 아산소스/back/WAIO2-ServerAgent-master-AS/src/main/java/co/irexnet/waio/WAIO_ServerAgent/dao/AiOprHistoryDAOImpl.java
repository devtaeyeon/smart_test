package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;

@Repository
public class AiOprHistoryDAOImpl implements IAiOprHistoryDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public List<AiOprHistoryDTO> select(InterfaceDateSearchDTO dto)
    {
        String strQuery = "SELECT PROC_CD, DISINFECTION_INDEX, AI_OPR, SUM(OPR_MINUTES) AS OPR_MINUTES"
        		+ " FROM TB_AI_OPR_HIS"
        		+ " WHERE HIS_DATE >= ? AND HIS_DATE <= ?"
        		+ " GROUP BY PROC_CD, DISINFECTION_INDEX, AI_OPR"; 
        return jdbcTemplate.query(
        		strQuery
        		, new Object[] {dto.getStart_time(), dto.getEnd_time()}
        		, new BeanPropertyRowMapper<>(AiOprHistoryDTO.class)
        );
    }
    
    @Override
    public List<AiOprHistoryDTO> select()
    {
        String strQuery = "SELECT PROC_CD, DISINFECTION_INDEX, AI_OPR, SUM(OPR_MINUTES) AS OPR_MINUTES"
        		+ " FROM TB_AI_OPR_HIS"
        		+ " GROUP BY PROC_CD, DISINFECTION_INDEX, AI_OPR"; 
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AiOprHistoryDTO.class));
    }

    @Override
    public int insert(List<AiOprHistoryDTO> list)
    {
        int[] result = jdbcTemplate.batchUpdate(
                "INSERT INTO TB_AI_OPR_HIS (PROC_CD, DISINFECTION_INDEX, AI_OPR, HIS_DATE, OPR_MINUTES)"
                + " VALUES(?, ?, ?, ?, ?);",
                new BatchPreparedStatementSetter()
                {
                    public void setValues(PreparedStatement ps, int i) throws SQLException
                    {
                        ps.setString(1, list.get(i).getProc_cd());
                        ps.setString(2, list.get(i).getDisinfection_index());
                        ps.setInt(3, list.get(i).getAi_opr());
                        ps.setDate(4, new java.sql.Date(list.get(i).getHis_date().getTime()));
                        ps.setInt(5, list.get(i).getOpr_minutes());
                    }

                    public int getBatchSize() { return list.size(); }
                }
        );
        return result.length;
    }
}
