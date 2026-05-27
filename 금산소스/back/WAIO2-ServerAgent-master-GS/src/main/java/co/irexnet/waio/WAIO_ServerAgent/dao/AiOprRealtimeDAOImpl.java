package co.irexnet.waio.WAIO_ServerAgent.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.dto.AiOprRealtimeDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlmCtrHisDTO;

import java.util.Date;
import java.util.List;

@Repository
public class AiOprRealtimeDAOImpl implements IAiOprRealtimeDAO{
	
    @Autowired
    JdbcTemplate jdbcTemplate;
    
	@Override
	public int update(AiOprRealtimeDTO dto) {
		String strQuery = "UPDATE TB_AI_OPR_RT SET OPR_MINUTES = (OPR_MINUTES + 1) WHERE PROC_CD = ? AND DISINFECTION_INDEX = ? AND AI_OPR = ?";
		return jdbcTemplate.update(strQuery, dto.getProc_cd(), dto.getDisinfection_index(), dto.getAi_opr());
	}
	
	@Override
	public int initalizeValues() {
		String strQuery = "UPDATE TB_AI_OPR_RT SET OPR_MINUTES = 0";
		return jdbcTemplate.update(strQuery);
	}
	
	@Override
	public List<AiOprRealtimeDTO> select() {
		String strQuery = "SELECT * FROM TB_AI_OPR_RT";
    	return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AiOprRealtimeDTO.class));
	}
	
}
