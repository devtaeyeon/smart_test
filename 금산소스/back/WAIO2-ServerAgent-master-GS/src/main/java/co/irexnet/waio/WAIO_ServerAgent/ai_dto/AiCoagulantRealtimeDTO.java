package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_coagulant_realtime
// 약품 공정 AI 결과 테이블
public class AiCoagulantRealtimeDTO
{
    private Date upd_ti;
    private Integer ai_opr;   // 운전 모드 0:수동, 1:반자동, 2:완전자동
    private Float b_tb;                 // 원수 탁도
    private Float b_ph;                 // 원수 pH
    private Float b_te;                 // 원수 수온
    private Float b_cu;                 // 원수 전기전도도
    private Float b_in_fr;              // 원수 유입 유량
    private String c_cf_coagulant;      // 현재 약품 종류
    private String c_mm_fr;             // 현재 약품 주입량
    private String c_cf;                // 현재 약품 주입률
    private String d_ser_in_fr;             // 혼화지 유입 유량
    private Float c_tb;              // 침전지 후탁도
    private Integer ai_clst_id;      // 클러스터 ID
    private String ai_c_cglnt;      // AI 약품 종류 예측
    private String ai_c_fnl;          // AI 약품 주입률 최종값
    private String ai_c_cf;         // AI 약품 주입률 예측
    private String ai_c_crt;      // AI 약품 주입률 보정값
    private String in_val;
    private String out_val;
    private String c_injector1; //1번 주입기 run
    private String c_injector2; //2번 주입기 run
    
    private float ai_c_cf_norm_co;        // 사용자보정 처리 이전 주입률
    
}
