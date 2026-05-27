package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.LoginHistoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class LoginHistoryDAOImpl implements ILoginHistoryDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<LoginHistoryDTO> select()
    {
        String strQuery = "select * from TB_LGN_HIS order by seq desc limit 100";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(LoginHistoryDTO.class));
    }

    @Override
    public int insert(LoginHistoryDTO dto)
    {
        String strQuery = "insert into TB_LGN_HIS (usr_id, usr_nm, lgn_ty, lgn_ti, lgn_addr) values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getUsr_id(), dto.getUsr_nm(), dto.getLgn_ty(), dto.getLgn_ti(), dto.getLgn_addr()
        );
    }

    @Override
    public int delete(Date date)
    {
        String strQuery = "delete from TB_LGN_HIS where lgn_ti < ?";
        return jdbcTemplate.update(strQuery, date);
    }
}
