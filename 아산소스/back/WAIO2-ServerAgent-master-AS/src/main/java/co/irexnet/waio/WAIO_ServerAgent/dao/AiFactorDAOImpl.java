package co.irexnet.waio.WAIO_ServerAgent.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.dto.AiFactorDTO;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;

import java.util.Date;

@Repository
public class AiFactorDAOImpl implements IAiFactorDAO{
	
    @Autowired
    JdbcTemplate jdbcTemplate;
    
	@Override
	public AiFactorDTO select(AiFactorDTO dto) {

		String strQuery = "SELECT PROC_CD, DISINFECTION_INDEX, RNTI, FACTOR"
						+ " FROM TB_AI_FACTOR"
						+ " WHERE PROC_CD = ?"
						+ " AND DISINFECTION_INDEX = ?"
						+ " AND RNTI = ?";
		try {
			return jdbcTemplate.queryForObject(strQuery, new Object[]{
					dto.getProc_cd()
					, convertDisinfectionIndex(Integer.parseInt(dto.getDisinfection_index()))
					, dto.getRnti()}
			, new BeanPropertyRowMapper<>(AiFactorDTO.class));			
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private String convertDisinfectionIndex(int disinfectionIndex) {
		String disinfectionIndexString = ""; 
		if(disinfectionIndex == CommonValue.DISINFECTION_PRE_STEP) {
			disinfectionIndexString = "PRE";
		} else if (disinfectionIndex == CommonValue.DISINFECTION_POST_STEP) {
			disinfectionIndexString = "POST";
		} else {
			disinfectionIndexString = "NONE";
		}
		return disinfectionIndexString;
	}
	
}
