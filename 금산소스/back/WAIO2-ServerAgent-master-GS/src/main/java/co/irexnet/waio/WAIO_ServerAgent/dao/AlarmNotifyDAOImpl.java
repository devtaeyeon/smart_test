package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmNotifyDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAlarmControlHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
public class AlarmNotifyDAOImpl implements IAlarmNotifyDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(AlarmNotifyDTO dto)
    {
    	String strQuery = "INSERT INTO TB_ALM_NTF (ALM_ID, ALM_NTF_TI, HOST, VAL, ACK_STT) VALUES (?, ?, ?, ?, 0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(strQuery, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, dto.getAlm_id());
                ps.setTimestamp(2, new Timestamp(dto.getAlm_ntf_ti().getTime()));
                ps.setString(3, dto.getHost());
                ps.setString(4, dto.getVal());
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<AlarmNotifyDTO> select()
    {
        // 알람 네비게이터 삭제
//        String strQuery = "select " + 
//                "seq, alm_id, alm_ntf_ti, host, val, IF(ack_stt, 'true', 'false') as ack_stt " +
//                "from TB_ALM_NTF order by seq desc limit 1000";
        String strQuery = "select " +
                "seq, alm_id, alm_ntf_ti, host, val " +
                "from TB_ALM_NTF order by seq desc limit 1000";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AlarmNotifyDTO.class));
    }
    
    @Override
    public List<AlarmNotifyDTO> select(Date start_time)
    {
        String strQuery = "select seq, alm_id, alm_ntf_ti, host, val " +
                "from TB_ALM_NTF where alm_ntf_ti >= ? and ack_stt = 0 " +
                "order by seq asc limit 10";
        return jdbcTemplate.query(strQuery, new Object[]{start_time}, new BeanPropertyRowMapper<>(AlarmNotifyDTO.class));
    }

    @Override
    public List<AlarmNotifyDTO> select(boolean ackState)
    {
        // 알람 네비게이터 삭제
//        String strQuery = "select " +
//                "seq, alm_id, alm_ntf_ti, host, val, IF(ack_stt, 'true', 'false') as ack_stt " +
//                "from TB_ALM_NTF where ack_stt=? " +
//                "order by seq desc limit 1000";
        String strQuery = "select " +
                "seq, alm_id, alm_ntf_ti, host, val " +
                "from TB_ALM_NTF where ack_stt=? " +
                "order by seq desc limit 1000";
        return jdbcTemplate.query(strQuery, new Object[]{ackState}, new BeanPropertyRowMapper<>(AlarmNotifyDTO.class));
    }

    @Override
    public AlarmNotifyDTO select(int alm_id, Date alm_ntf_ti, String host)
    {
        // 알람 네비게이터 삭제
//        String strQuery = "select " +
//                "seq, alm_id, alm_ntf_ti, host, val, IF(ack_stt, 'true', 'false') as ack_stt " +
//                "from TB_ALM_NTF where alm_id=? and host=? and alm_ntf_ti>?" +
//                "order by seq desc limit 1";
        String strQuery = "select " +
                "seq, alm_id, alm_ntf_ti, host, val " +
                "from TB_ALM_NTF where alm_id=? and host=? and alm_ntf_ti>?" +
                "order by seq desc limit 1";
        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery,
                    new Object[]{alm_id, host, alm_ntf_ti},
                    new BeanPropertyRowMapper<>(AlarmNotifyDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public List<AlarmNotifyDTO> select(Date start_time, Date end_time)
    {
// 알람 네비게이터 삭제
//        String strQuery = "select " +
//                "seq, alm_id, alm_ntf_ti, host, val, IF(ack_stt, 'true', 'false') as ack_stt " +
//                "from TB_ALM_NTF where alm_ntf_ti >= ? and alm_ntf_ti < ?" +
//                "order by seq desc";
        String strQuery = "select " +
                "seq, alm_id, alm_ntf_ti, host, val " +
                "from TB_ALM_NTF where alm_ntf_ti >= ? and alm_ntf_ti < ?" +
                "order by seq asc";
        return jdbcTemplate.query(strQuery, new Object[]{start_time, end_time}, new BeanPropertyRowMapper<>(AlarmNotifyDTO.class));
    }

    @Override
    public int updateAckState(int alarmNotifyIndex, boolean ackState)
    {
        String strQuery = "update alarm_notify set ack_stt=? where seq=?";
        return jdbcTemplate.update(strQuery, ackState, alarmNotifyIndex);
    }
    
    @Override
    public InterfaceAlarmControlHistoryDTO getAlarmExceeded(InterfaceAlarmControlHistoryDTO dto) {
        String strQuery = "SELECT C.ALM_ID, C.DP_NM, C.URL, A.RNTI, C.ALM_TY "
        + " FROM " + getTableByProcessStep(dto) + "_CTR A " 
        + " INNER JOIN " + getTableByProcessStep(dto) +  "_ALM B " 
        + " ON A.RNTI = B.ALM_TI " 
        + " INNER JOIN TB_ALM_INFO C "
        + " ON B.ALM_ID = C.ALM_ID "
        + " WHERE ALM_TY = ? AND A.RNTI = ? AND A.UPD_TI = ? AND A.TAG_SN = ? AND A.KFK_FLG = 0";
        try {
            return jdbcTemplate.queryForObject(strQuery, new Object[]{dto.getAlmTy(), dto.getAlm_ntf_ti(), dto.getUpdTi(), dto.getTagSn()}, new BeanPropertyRowMapper<>(InterfaceAlarmControlHistoryDTO.class));
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    // 5분전 데이터 조회
    public List<InterfaceAlarmControlHistoryDTO> getBeforeAlarmNotify(InterfaceAlarmControlHistoryDTO dto) {
        StringBuffer strQuery = new StringBuffer();
        strQuery.append("SELECT SEQ, ALM_ID, ALM_NTF_TI, HOST, VAL FROM TB_ALM_NTF ")
                .append("WHERE ALM_ID = ? AND TIMESTAMPDIFF(MINUTE, ALM_NTF_TI, ?) <= 5 ");
        try {
            return jdbcTemplate.query(strQuery.toString(), new Object[]{dto.getAlm_id(), dto.getStart_time()}, new BeanPropertyRowMapper<>(InterfaceAlarmControlHistoryDTO.class));
        } catch (DataAccessException e) {
            strQuery.append("SELECT NULL PROCESS, ? AS UPD_TI FROM DUAL");
            return jdbcTemplate.query(strQuery.toString(), new Object[]{dto.getStart_time()}, new BeanPropertyRowMapper<>(InterfaceAlarmControlHistoryDTO.class));
        }

        
    }
    
    public String getTableByProcessStep(InterfaceAlarmControlHistoryDTO dto) {
        StringBuffer tableNm = new StringBuffer();
        tableNm.append("TB_AI_");
        if(CommonValue.PROCESS_DISINFECTION.equals(dto.getProcess())) { // 소독
        	if(dto.getDisinfectionIndex() == CommonValue.DISINFECTION_PRE_STEP) {
        		tableNm.append("PRE_");
        	}else if(dto.getDisinfectionIndex() == CommonValue.DISINFECTION_POST_STEP) {
        		tableNm.append("POST_");
        	}
        }
        tableNm.append(dto.getProcess());

        return tableNm.toString();
    }
    
    
}
