package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.DiskInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DiskInfoDAOImpl implements IDiskInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(DiskInfoDTO dto)
    {
        String strQuery = "insert into TB_DISK_INFO values (?, ?, ?)";
        return jdbcTemplate.update(strQuery, dto.getHost(), dto.getMd(), dto.getSize());
    }

    @Override
    public List<DiskInfoDTO> select(String host) {
        String strQuery = "select * from TB_DISK_INFO where host=?";
        return jdbcTemplate.query(strQuery, new Object[]{host}, new BeanPropertyRowMapper<>(DiskInfoDTO.class));
    }

    @Override
    public int update(DiskInfoDTO dto) {
        String strQuery = "update TB_DISK_INFO set size=? where host=? and md=?";
        return jdbcTemplate.update(strQuery, dto.getSize(), dto.getHost(), dto.getMd());
    }
}
