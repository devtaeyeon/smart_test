package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.ai_dto.AiProcessControlDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmNotifyDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlmCtrHisCntDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.AlmCtrHisDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceAlarmControlHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.util.AlarmInfoList;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;

import java.util.Calendar;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class AlmCtrHisImpl implements IAlmCtrHisImplDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    AlarmInfoList alarmInfoList;

    @Override
    public List<AlmCtrHisDTO> selectBySeqAndTag (int almSeq, String tagSn) {
    	String strQuery = "SELECT * FROM TB_ALM_CTR_HIS WHERE ALM_SEQ = ? AND TAG_SN = ? ";
    	return jdbcTemplate.query(strQuery, new Object[] {almSeq, tagSn}, new BeanPropertyRowMapper<>(AlmCtrHisDTO.class));
    }

    @Override
    public List<AlmCtrHisDTO> selectTagInfoList(AlmCtrHisDTO dto) { 
        String strQuery = "SELECT CTR.UPD_TI, CTR.TAG_SN, MNG.DP, CTR.TAG_VAL, CTR.TAG_CMP_VAL"
                + " FROM " + getCtrTblNm(dto.getAlm_id()) + " CTR"
                + " INNER JOIN TB_TAG_MNG MNG"
                + " ON CTR.TAG_SN = MNG.TAG_SN"
                + " AND MNG.TAG_TY = 4"
                + " WHERE CTR.UPD_TI = ?";
        return jdbcTemplate.query(strQuery, new Object[]{dto.getUpd_ti()}, new BeanPropertyRowMapper<>(AlmCtrHisDTO.class));
    }
    
    //select all group by ctr_ti, alm_seq for list
//    @Override
//    public List<AlmCtrHisDTO> select()
//    {	
//    	String strQuery = "SELECT "
//		    		    + "TACH.SEQ, TACH.USR_ID, TACH.USR_NM, TACH.CTR_TI, TACH.CTR_YN, TACH.ALM_SEQ, TACH.UPD_TI, TAI.ALM_TY, "
//		    		    + "(SELECT "
//		    		    + "    CASE "
//		    		    + "        WHEN TAI.DP_NM LIKE '%소독%' THEN SUBSTRING_INDEX(TAI.DP_NM, ' ', 2)"
//		    		    + "        ELSE SUBSTRING_INDEX(TAI.DP_NM, ' ', 1)"
//		    		    + "    END AS PROCESS_NAME "
//		    		    + ") AS PROCESS_NAME, "
//		    		    + "TAI.ALM_ID "
//		    		    + "FROM TB_ALM_CTR_HIS TACH "
//		    		    + "INNER JOIN TB_ALM_NTF TAN ON TACH.ALM_SEQ = TAN.SEQ "
//		    		    + "INNER JOIN TB_ALM_INFO TAI ON TAI.ALM_ID = TAN.ALM_ID "
//		    		    + "GROUP BY CTR_TI, ALM_SEQ "
//		    		    + "ORDER BY CTR_TI DESC";
//    	
//    	return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AlmCtrHisDTO.class));
//    }
    
  //select list by date
    @Override
    public List<AlmCtrHisDTO> select(Date start_time, Date end_time)
    {
    	Calendar updTiStartTime = Calendar.getInstance();
    	updTiStartTime.setTime(start_time);
    	updTiStartTime.add(Calendar.HOUR, -1);
    	
        String strQuery = 
//        	"WITH ctr_tables AS ( " +
//            "SELECT TAG_SN, UPD_TI, TAG_VAL, TAG_CMP_VAL " +
//            "FROM TB_AI_C_CTR " +
//    		"WHERE UPD_TI BETWEEN ? AND ? " +
//            "UNION ALL " +
//            "SELECT TAG_SN, UPD_TI, TAG_VAL, TAG_CMP_VAL " +
//            "FROM TB_AI_D_CTR " +
//    		"WHERE UPD_TI BETWEEN ? AND ? " +
//            "UNION ALL " +
//            "SELECT TAG_SN, UPD_TI, TAG_VAL, TAG_CMP_VAL " +
//            "FROM TB_AI_PRE_G_CTR " +
//    		"WHERE UPD_TI BETWEEN ? AND ? " +
//            "UNION ALL " +
//            "SELECT TAG_SN, UPD_TI, TAG_VAL, TAG_CMP_VAL " +
//            "FROM TB_AI_POST_G_CTR " +
//    		"WHERE UPD_TI BETWEEN ? AND ? " +
//            ") " +
            "SELECT " +
            "    A.SEQ, " +
            "    B.ALM_SEQ, " +
            "    B.ALM_ID, " +
            "    B.ALM_TY, " +
            "    A.USR_ID, " +
            "    A.USR_NM, " +
            "    A.CTR_TI, " +
            "    A.CTR_YN, " +
            "    A.UPD_TI, " +
            "    A.TAG_SN, " +
            "    PROCESS_STEP, " +
            "    DISINFECTION_INDEX, " +
            "    (SELECT " +
            "        CASE " +
            "            WHEN B.DP_NM LIKE '%소독%' THEN SUBSTRING_INDEX(B.DP_NM, ' ', 2) " +
            "            ELSE SUBSTRING_INDEX(B.DP_NM, ' ', 1) " +
            "        END " +
            "    ) AS PROCESS_NAME, " +
//            "    IF(COUNT(ctr_tables.TAG_SN) >= 2, '-', (SELECT DP FROM TB_TAG_MNG WHERE TAG_SN = ctr_tables.TAG_SN AND TAG_TY=4)) AS DP, " +
//            "    IF(COUNT(ctr_tables.TAG_VAL) >= 2, '-', ctr_tables.TAG_VAL) AS TAG_VAL, " +
//            "    IF(COUNT(ctr_tables.TAG_CMP_VAL) >= 2, '-', ctr_tables.TAG_CMP_VAL) AS TAG_CMP_VAL, " +
            "    ( CASE " +
            "        WHEN SUBSTRING(B.ALM_ID, 3, 1) = 2 THEN 'C' " +
            "        WHEN SUBSTRING(B.ALM_ID, 3, 1) = 3 THEN 'D' " +
            "        WHEN SUBSTRING(B.ALM_ID, 3, 1) = 7 THEN 'G' " +
            "     END ) AS PROCESS " +
            "FROM " +
            "    TB_ALM_CTR_HIS A " +
            "INNER JOIN ( " +
            "    SELECT " +
            "        A.ALM_ID, " +
            "        A.DP_NM, " +
            "        A.ALM_TY, " +
            "        B.SEQ AS ALM_SEQ, " +
            "        SUBSTRING(A.ALM_ID, 3, 1) AS PROCESS, " +
            "        SUBSTRING(A.ALM_ID, 4, 1) AS PROCESS_STEP, " +
            "        SUBSTRING(A.ALM_ID, 5, 1) AS DISINFECTION_INDEX " +
            "    FROM " +
            "        TB_ALM_INFO A " +
            "    INNER JOIN TB_ALM_NTF B ON " +
            "        A.ALM_ID = B.ALM_ID " +
            ") B ON " +
            "    A.ALM_SEQ = B.ALM_SEQ " +
//            "INNER JOIN " +
//            "    ctr_tables ON A.UPD_TI = ctr_tables.UPD_TI AND A.TAG_SN = ctr_tables.TAG_SN " +
            "WHERE " +
            "    A.CTR_TI >= ? AND A.CTR_TI < ? " +
            "GROUP BY " +
            "    A.CTR_TI, B.ALM_SEQ " +
            "ORDER BY " +
            "    A.SEQ DESC";
	    return jdbcTemplate.query(strQuery, new Object[]{
//	    		updTiStartTime, end_time,
//	    		updTiStartTime, end_time,
//	    		updTiStartTime, end_time,
//	    		updTiStartTime, end_time,
	    		start_time, end_time,
	    }, new BeanPropertyRowMapper<>(AlmCtrHisDTO.class));
    }
    
    //select by alm_seq, ctr_ti from CtrTbl
    @Override
    public List<AiProcessControlDTO> select(int almId, int almSeq, Date ctrTi)
    {	
    	String strQuery = "SELECT HIS.UPD_TI, CTR.RNTI, HIS.TAG_SN, CTR.TAG_VAL, CTR.TAG_CMP_VAL, CTR.KFK_FLG, CTR.CTR_FLG, "
				+ "(SELECT DISTINCT DP FROM TB_TAG_MNG WHERE TB_TAG_MNG.TAG_SN = CTR.TAG_SN AND TB_TAG_MNG.TAG_TY = '4') AS TAG_DP "
				+ "FROM TB_ALM_CTR_HIS HIS "
				+ "INNER JOIN " + getCtrTblNm(almId) + " CTR ON CTR.UPD_TI = HIS.UPD_TI AND CTR.TAG_SN = HIS.TAG_SN "
				+ "INNER JOIN TB_ALM_NTF NTF ON HIS.ALM_SEQ = NTF.SEQ "
				+ "WHERE CTR_TI = ? AND HIS.ALM_SEQ = ? ";
    	
    	return jdbcTemplate.query(strQuery, new Object[]{ctrTi, almSeq}, new BeanPropertyRowMapper<>(AiProcessControlDTO.class));
    }
    
    //select by 
    
    @Override
    public int insert(List<AlmCtrHisDTO> almCtrHisList) {
        int totalCount = 0;
        for (AlmCtrHisDTO almCtrHis : almCtrHisList) {
            String strQuery = "INSERT INTO TB_ALM_CTR_HIS "
                              +"(USR_ID, USR_NM, CTR_TI, CTR_YN, ALM_SEQ, TAG_SN, UPD_TI) "
                              +"VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            int result = jdbcTemplate.update(strQuery, new Object[] {
                almCtrHis.getUsr_id(),
                almCtrHis.getUsr_nm(),
                new java.sql.Timestamp(almCtrHis.getCtr_ti().getTime()),
                almCtrHis.getCtr_yn(),
                almCtrHis.getAlm_seq(),
                almCtrHis.getTag_sn(),
                new java.sql.Timestamp(almCtrHis.getUpd_ti().getTime())
            });
            
            totalCount += result;
        }
        return totalCount;
    }

    public String getCtrTblNm(int almId) {
    	String ctrTblNm = "TB_AI_";
    	String cdNm = "";
    	AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(almId);
    	if (alarmInfo != null) {
    	    cdNm = alarmInfo.getCd_nm();        
    	}
        if (CommonValue.ALARM_CODE_COAGULANT_AI_CONTROL.equals(cdNm) || CommonValue.ALARM_CODE_COAGULANT_AI_THRESHOLD.equals(cdNm)) {
        	ctrTblNm +="C_";
        } else if (CommonValue.ALARM_CODE_MIXING_AI_CONTROL.equals(cdNm)|| CommonValue.ALARM_CODE_MIXING_AI_THRESHOLD.equals(cdNm)) {
        	ctrTblNm +="D_";
        } else if (CommonValue.ALARM_CODE_DISINFECTION_AI_PRE_CONTROL.equals(cdNm)|| CommonValue.ALARM_CODE_DISINFECTION_AI_PRE_THRESHOLD.equals(cdNm)) {
        	ctrTblNm +="PRE_G_";
        } else if (CommonValue.ALARM_CODE_DISINFECTION_AI_POST_CONTROL.equals(cdNm)|| CommonValue.ALARM_CODE_DISINFECTION_AI_POST_THRESHOLD.equals(cdNm)) {
        	ctrTblNm +="POST_G_";
        }
        ctrTblNm += "CTR";
        return ctrTblNm;
    }
    

}
