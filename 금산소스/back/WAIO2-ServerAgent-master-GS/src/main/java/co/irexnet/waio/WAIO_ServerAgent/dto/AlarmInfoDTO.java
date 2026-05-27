package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// alarm_info
public class AlarmInfoDTO
{
    private int seq;
    private int alm_id;           // 알람 ID
    private String cd_nm;       // 알람 코드명
    private String dp_nm;    // 알람 표시명
    private String url;             // 알고리즘 링크
//    private Integer severity; // 알람 중요도 삭제
    private Integer alm_ty;           // 알람 종류 0:OFF 알람, 1:Threshold 알람
    private Integer cmp;        // 알람 비교 인자 0:==, 1:<(미만), 2:<=(이하), 3:>(초과), 4:>=(이상)
    private String cmp_val;           // 알람 임계 값
    private boolean scd_snd;     // SCADA 전송 여부
    private String tag_sn;             // 태그명
    private int processStep; // 공정단계
    private int disinfectionIndex;  // 소독단계
    private String process;
    private String alm_dp_yn; //알람 여부
}
