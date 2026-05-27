package co.irexnet.waio.WAIO_ServerAgent.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.dto.AiFactorDTO;
import java.util.Date;

@Repository
public class AiFactorDAOImpl implements IAiFactorDAO{
	
    @Autowired
    JdbcTemplate jdbcTemplate;
    
	@Override
	public AiFactorDTO select(Date rnti, String procCd, String disinfectionIndex) {

		String strQuery = "SELECT PROC_CD, DISINFECTION_INDEX, RNTI, FACTOR FROM TB_AI_FACTOR WHERE PROC_CD = ? AND DISINFECTION_INDEX = ? AND RNTI = ? LIMIT 1";
		try {
			return jdbcTemplate.queryForObject(strQuery, new Object[]{procCd, disinfectionIndex, rnti}, new BeanPropertyRowMapper<>(AiFactorDTO.class));			
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	
}
