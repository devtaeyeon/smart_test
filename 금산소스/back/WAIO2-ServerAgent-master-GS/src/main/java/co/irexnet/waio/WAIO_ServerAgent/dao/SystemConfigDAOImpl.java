package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.SystemConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SystemConfigDAOImpl implements ISystemConfigDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public SystemConfigDTO select()
    {
        String strQuery = "select * from TB_SYS_CNF limit 1";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(SystemConfigDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int update(SystemConfigDTO dto)
    {
        String strQuery = "update TB_SYS_CNF set " +
                "Sscd_addr1=?, scd_port1=?, daq_port1=?, scd_addr2=?, scd_port2=?, daq_port2=?, " +
                "anl_addr1=?, anl_rm_port1=?, anl_nm_port1=?, anl_nn_port1=?, " +
                "anl_addr2=?, anl_rm_port2=?, anl_nm_port2=?, anl_nn_port2=?";

        return jdbcTemplate.update(
                strQuery,
                dto.getScd_addr1(), dto.getScd_port1(), dto.getDaq_port1(),
                dto.getScd_addr2(), dto.getScd_port2(), dto.getDaq_port2(),
                dto.getAnl_addr1(), dto.getAnl_rm_port1(), dto.getAnl_nm_port1(), dto.getAnl_nn_port1(),
                dto.getAnl_addr2(), dto.getAnl_rm_port2(), dto.getAnl_nm_port2(), dto.getAnl_nn_port2()
        );
    }
}
