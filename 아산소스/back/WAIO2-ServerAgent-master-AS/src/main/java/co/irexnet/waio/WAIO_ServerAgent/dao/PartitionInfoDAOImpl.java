package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.PartitionInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PartitionInfoDAOImpl implements IPartitionInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(PartitionInfoDTO dto)
    {
        String strQuery = "insert into TB_PRT_INFO values (?, ?, ?, ?)";
        return jdbcTemplate.update(strQuery, dto.getHost(), dto.getNm(), dto.getTot_size(), dto.getUnu_size());
    }

    @Override
    public List<PartitionInfoDTO> select(String host)
    {
        String strQuery = "select * from TB_PRT_INFO where host=?";
        return jdbcTemplate.query(strQuery, new Object[]{host}, new BeanPropertyRowMapper<>(PartitionInfoDTO.class));
    }

    @Override
    public int update(PartitionInfoDTO dto)
    {
        String strQuery = "update TB_PRT_INFO set total_size=?, usable_size=? where host=? and name=?";
        return jdbcTemplate.update(strQuery, dto.getTot_size(), dto.getUnu_size(), dto.getHost(), dto.getNm());
    }
}
