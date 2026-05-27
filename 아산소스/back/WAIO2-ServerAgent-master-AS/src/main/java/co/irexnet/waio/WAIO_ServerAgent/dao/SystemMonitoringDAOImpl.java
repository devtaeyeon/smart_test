package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.dto.SystemMonitoringDTO;

@Repository
public class SystemMonitoringDAOImpl implements ISystemMonitoringDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(SystemMonitoringDTO dto)
    {
        String strQuery = "insert into TB_SYS_MNTR (host, mntr_ty, mntr_itm, mntr_val, mntr_upd_ti)" +
                "values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getHost(), dto.getMntr_ty(), dto.getMntr_itm(), dto.getMntr_val(), dto.getMntr_upd_ti()
        );
    }

    @Override
    public List<SystemMonitoringDTO> select()
    {
        String strQuery = "select * from TB_SYS_MNTR order by seq desc limit 1000";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(SystemMonitoringDTO.class));
    }

    @Override
    public List<SystemMonitoringDTO> select(String host)
    {
        String strQuery = "select * from TB_SYS_MNTR where host=? order by seq desc limit 1000";
        return jdbcTemplate.query(strQuery, new Object[]{host}, new BeanPropertyRowMapper<>(SystemMonitoringDTO.class));
    }

    @Override
    public List<SystemMonitoringDTO> selectLatest(Date startDate)
    {
        String strQuery = "select * " +
                "from " +
                "(select * from TB_SYS_MNTR where mntr_upd_ti > ? order by seq desc limit 1000) " +
                "TB_SYS_MNTR " +
                "group by host, mntr_ty, mntr_itm";
        return jdbcTemplate.query(strQuery, new Object[]{startDate}, new BeanPropertyRowMapper<>(SystemMonitoringDTO.class));
    }

    @Override
    public int delete(Date date)
    {
        String strQuery = "delete from TB_SYS_MNTR where mntr_upd_ti < ?";
        return jdbcTemplate.update(strQuery, date);
    }
}
