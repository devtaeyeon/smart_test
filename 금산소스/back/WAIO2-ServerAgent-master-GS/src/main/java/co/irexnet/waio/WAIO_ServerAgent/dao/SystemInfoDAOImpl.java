package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.SystemInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SystemInfoDAOImpl implements ISystemInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(SystemInfoDTO dto)
    {
        String strQuery = "insert into TB_SYS_INFO values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getHost(), dto.getHost(), dto.getOs(), dto.getMd(),
                dto.getPrc_nm(), dto.getPrc_cnt(), dto.getCore_cnt(),
                dto.getLgc_core_cnt(), dto.getMax_freq(),
                dto.getTot_mem(), dto.getAvl_mem(),
                dto.getDb_used(), dto.getDb_free(),
                dto.getSys_upd_ti()
        );
    }

    @Override
    public SystemInfoDTO select(String host)
    {
        String strQuery = "select * from TB_SYS_INFO where host=?";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new Object[]{host}, new BeanPropertyRowMapper<>(SystemInfoDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public List<SystemInfoDTO> select()
    {
        String strQuery = "select * from TB_SYS_INFO";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(SystemInfoDTO.class));
    }

    @Override
    public int update(SystemInfoDTO dto)
    {
        String strQuery = "update TB_SYS_INFO " +
                "set os=?, md=?, prc_nm=?, prc_cnt=?, core_cnt=?, lgc_core_cnt=?, max_freq=?, " +
                "tot_mem=?, avl_mem=?, db_used=?, db_free=?, sys_upd_ti=? " +
                "where host=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getOs(), dto.getMd(), dto.getPrc_nm(), dto.getPrc_cnt(),
                dto.getCore_cnt(), dto.getLgc_core_cnt(), dto.getMax_freq(),
                dto.getTot_mem(), dto.getAvl_mem(), dto.getDb_used(), dto.getDb_free(), dto.getSys_upd_ti(),
                dto.getHost()
        );
    }

    @Override
    public int updateName(String host, String sys_nm)
    {
        String strQuery = "update TB_SYS_INFO set sys_nm=? where host=?";
        return jdbcTemplate.update(strQuery, sys_nm, host);
    }
}
