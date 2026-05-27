package co.irexnet.waio.WAIO_ServerAgent.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprHistoryDTO;

import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceDateSearchDTO;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@Repository
public class AiOprHistoryDAOImpl implements IAiOprHistoryDAO{
	
    @Autowired
    JdbcTemplate jdbcTemplate;
    
	@Override
	public int insert(AiOprHistoryDTO dto) {
		String strQuery = "INSERT INTO TB_AI_OPR_HIS "
					    + "(PROC_CD, DISINFECTION_INDEX, AI_OPR, HIS_DATE, OPR_MINUTES) "
						+ "VALUES(?, ?, ?, ?, ?)";
		return jdbcTemplate.update(strQuery, new Object[] {dto.getProc_cd(), dto.getDisinfection_index(), dto.getAi_opr(), dto.getHis_date(), dto.getOpr_minutes()});
	}
	
	@Override 
	public List<AiOprHistoryDTO> select(InterfaceDateSearchDTO dto) {
		String strQuery = "SELECT PROC_CD, DISINFECTION_INDEX, AI_OPR, SUM(OPR_MINUTES) AS OPR_MINUTES"
						+ " FROM TB_AI_OPR_HIS"
						+ " WHERE HIS_DATE >= ? AND HIS_DATE <= ?"
						+ " GROUP BY PROC_CD, DISINFECTION_INDEX, AI_OPR";
		return jdbcTemplate.query(strQuery, new Object[] {convertDateType(dto.getStart_time()), convertDateType(dto.getEnd_time())}, new BeanPropertyRowMapper<>(AiOprHistoryDTO.class));
	}

	private String convertDateType(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}
	
}
