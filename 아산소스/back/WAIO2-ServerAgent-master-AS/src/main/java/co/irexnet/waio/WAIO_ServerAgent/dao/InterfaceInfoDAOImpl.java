package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.DiatomDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InterfaceInfoDAOImpl implements IInterfaceInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(InterfaceInfoDTO dto)
    {
        String strQuery = "insert into TB_ITF_INFO values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getHost(), dto.getNm(), dto.getDp_nm(), dto.getIpv4(), dto.getMac()
        );
    }

    @Override
    public List<InterfaceInfoDTO> select(String host)
    {
        String strQuery = "select * from TB_ITF_INFO where host=?";
        return jdbcTemplate.query(strQuery, new Object[]{host}, new BeanPropertyRowMapper<>(InterfaceInfoDTO.class));
    }

    @Override
    public List<InterfaceInfoDTO> selectWhereAddress(String address)
    {
        String strQuery = "select * from TB_ITF_INFO where ipv4=?";

        return jdbcTemplate.query(strQuery, new Object[]{address}, new BeanPropertyRowMapper<>(InterfaceInfoDTO.class));
    }

    @Override
    public int update(InterfaceInfoDTO dto)
    {
        String strQuery = "update TB_ITF_INFO set dp_nm=?, ipv4=?, mac=? where host=? and nm=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getDp_nm(), dto.getIpv4(), dto.getMac(), dto.getHost(), dto.getNm()
                );
    }
}
