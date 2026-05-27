package co.irexnet.waio.WAIO_ServerAgent.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.UsrMngDTO;


@Repository
public class UsrMngDAOImpl implements IUsrMngDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

	@Override
	public UsrMngDTO select(int processStep) {
		String strQuery = "SELECT ITM, TAG_SN, INIT_VAL FROM TB_USR_MNG WHERE ITM = ?";
		try {
			return jdbcTemplate.queryForObject(strQuery, new Object[]{getItmByProcess(processStep)}, new BeanPropertyRowMapper<>(UsrMngDTO.class));			
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public int update(UsrMngDTO dto, int processStep) {
		String strQuery = "UPDATE TB_USR_MNG SET INIT_VAL = ? WHERE ITM = ? ";
		return jdbcTemplate.update(strQuery, dto.getInit_val(), getItmByProcess(processStep));
	}

	public String getItmByProcess(int processStep) {
		String itm = "";
		if(processStep == 1) {
			itm = "ai_c1_cf_coagulant";
		}else if(processStep == 2) {
			itm = "ai_c2_cf_coagulant";
		}
		return itm;
	}
	
}
