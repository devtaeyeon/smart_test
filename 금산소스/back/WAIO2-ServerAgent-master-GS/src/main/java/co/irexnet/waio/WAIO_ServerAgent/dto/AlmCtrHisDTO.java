package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// Alarm Control History DTO
public class AlmCtrHisDTO
{
    private int seq; //순번
    private String usr_id; //사용자 아이디
    private String usr_nm; //사용자 이름
    private Date ctr_ti; // 제어 시간
    private String ctr_yn; //제어 여부
    private int alm_seq; // TB_ALM_NTF seq외래키
    private Date upd_ti; // 각 공정 ctr의 upd_ti
    private String tag_sn;
    private String process_name; // 공정 명칭
    private int alm_id; //알람 ID
    private int alm_ty; //알람 타입

    //태그 관련 추가 정보
    private String dp;
    private String tag_val;
    private String tag_cmp_val;
    private String process_step;
    private String process;
    private int disinfection_index;

}
