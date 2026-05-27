package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_filter_realtime
// 여과 공정 AI 결과 테이블
public class AiFilterRealtimeDTO
{
    private Date upd_ti;
    private Integer ai_opr;           // 운전 모드 0:수동, 1:반자동, 2:완전자동
    private Integer f_peak_mode;                // 피크 관리 모드 0:OFF, 1:ON
    private Integer f_bw_mode;                  // 역세 모드 0:OFF, 1:ON
    private String d_ser_in_fr;                     // 혼화지 유입 유량
    private Float f_out_fr;                     // 여과지 유출 유량
    private Float f_speed;                         // 여과 속도
    private Integer f_opr_cnt;          // 운영지 수
    private String f_loc_stt;            // 지별 여과 상태
    private String f_loc_le;               // 지별 수위
    private String f_loc_tb;               // 지별 탁도
    private String f_loc_ti;               // 지별 여과 지속시간
    private String ai_f_wl;            // AI 지별 여과 수위 예측
    private String ai_f_time;            // AI 지별 여과 지속시간 예측
    private String ai_f_bw_start_time;   // AI 지별 역세 시작 시간 예측
    private String f_loc_bw_wt_ti;       // 역세 후 대기 시간
    private String e_ser_tb_b;                      // 침전지 후 탁도
    private String ai_f_num_fil;        // AI 운영지 수 예측
    private String ai_f_schedule;     // AI 지별 여과 스케쥴
    private String ai_f_loc_fnl_opr_sch;         // AI 여과 운영 스케쥴 표
//    private String f_fil_ready;			// 여과준비중
    private String f_fil_ing;			// 여과중
//    private String f_fil_end;			// 여과종료
//    private String f_wait;				// 대기중
    private String f_bw_wait;			// 역세대기중
    private String f_bw_ing;			// 역세중
    private String f_fil_wait;			// 여과대기중 --23/11/09추가
    private String f_rest;				//운휴중(지별) --23/11/09추가
    private String f_dr_ing;			// 시동방수중
//    private String f_etc;				// 예외상황
    private String ai_f_location_operation; //여과스케줄
    private String ai_f_bw_wait_time; //역세 대기시간
}
