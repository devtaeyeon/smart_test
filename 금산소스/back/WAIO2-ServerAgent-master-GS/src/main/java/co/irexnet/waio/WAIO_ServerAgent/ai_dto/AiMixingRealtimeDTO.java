package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_mixing_realtime
// 혼화응집 공정 AI 결과 테이블
public class AiMixingRealtimeDTO
{
    private Date upd_ti;
    private Integer ai_opr;       // 운전 모드 0:수동, 1:반자동, 2:완전자동
    //private Float b_te;                     // 원수 수온
    private Float d_te;						// 원수 수온
    private Float d_te_loc1;						// 원수 수온 (1지)
    private Float d_te_loc2;						// 원수 수온 (2지)
    private Float d_de;                     // 물의 밀도
    private Float d_dv_loc1;              // 물의 점성계수 (1지)
    private Float d_dv_loc2;              // 물의 점성계수 (2지)
    private Float d_rq;                     // 혼화지 용량
    private Float d_im_d;					// 임펠러 직경
    //private Float d_fc_lt;                  // 임펠러 직경
    //private Float d_fc_lt2;                 // 임펠러 직경 - 2
    private Float d_pw;						  // Power Number
    //private Float d_pn;                     // Power Number
    //private Float d_pn2;                    // Power Number - 2
    private String d_g;               // G 값
    private String d_loc_fc_sp;        // 지별 응집기 속도
    private String ai_d_loc_fc_g;     // AI 응집기 교반강도 예측
    private String d_loc_fc_stt;     // 지별 응집기 상태
    private Float ai_b_te_prep;                  // AI 전처리 후 원수 수온
    private String ai_d_loc_fc_sp;     // AI 응집기 속도 예측
    private String ai_d_loc_fc_stt;    // AI 응집기 속도 예측 - 2
    private String in_val;
    private String out_val;
    private Float d_ki_dv; // 동점성 계수
    private Float d_anr; //패들 면적
    private Float d_v; //조 체적
    

}
