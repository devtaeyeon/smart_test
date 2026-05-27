package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AlarmInfoDAOImpl implements IAlarmInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(AlarmInfoDTO dto)
    {
        String strQuery = "insert into TB_ALM_INFO values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getAlm_id(), dto.getCd_nm(), dto.getDp_nm(), dto.getUrl(), /*dto.getSeverity(),*/    // 알람 중요도 삭제
                dto.getAlm_ty(), dto.getCmp(), dto.getCmp_val(), dto.isScd_snd(), dto.getTag_sn(), dto.getAlm_dp_yn()
        );
    }

    @Override
    public List<AlarmInfoDTO> select() {
    String strQuery = "SELECT A.SEQ, A.ALM_ID, A.PROCESS_STEP, A.DISINFECTION_INDEX, A.cd_nm, A.dp_nm, A.url, A.alm_ty, A.cmp, A.cmp_val, A.scd_snd, A.tag_sn, (CASE WHEN A.PROCESS = 1 THEN 'B' WHEN A.PROCESS = 2 THEN 'C' WHEN A.PROCESS = 3 THEN 'D'"
                    + " WHEN A.PROCESS = 4 THEN 'E' WHEN A.PROCESS = 5 THEN 'F' WHEN A.PROCESS = 7 THEN 'G'"
                    + " END ) AS PROCESS, A.ALM_DP_YN FROM (select seq, alm_id, SUBSTRING(ALM_ID, 3, 1) AS PROCESS, SUBSTRING(ALM_ID, 4, 1) AS PROCESS_STEP, SUBSTRING(ALM_ID, 5, 1) AS DISINFECTION_INDEX, cd_nm, dp_nm, url,"
                    + "alm_ty, cmp, cmp_val, IF(scd_snd, 'true', 'false') as scd_snd, tag_sn, alm_dp_yn "
                    + "from TB_ALM_INFO order by alm_id ) A";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AlarmInfoDTO.class));
    }

    @Override
    public int update(AlarmInfoDTO dto)
    {
// 알람 중요도 삭제
        //String strQuery = "update TB_ALM_INFO set dp_nm=?, severity=?, cmp_val=?, scd_snd=? where seq=?";
        String strQuery = "update TB_ALM_INFO set dp_nm=?, scd_snd=? where seq=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getDp_nm(), dto.isScd_snd(), dto.getSeq());
    }

    @Override
    public int delete(int alm_id)
    {
        String strQuery = "delete from TB_ALM_INFO where alm_id=?";
        return jdbcTemplate.update(strQuery, alm_id);
    }
}
