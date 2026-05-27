package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 소독 전염소 알고리즘 설정값을 저장하기 위한 class
public class InterfaceDisinfectionPreDTO
{
    private float g_pre_set_max;               // 전염소 1계열 주입률 최대값
    private float g_pre_set_min;               // 전염소 1계열 주입률 최소값
    private float g_pre_chg_limit_for_onetime; // 전염소 1계열 1회 변경 한계치
    private float d1_target_cl;                 // 1계열 혼화지 목표 잔류염소
    private float g_e_obj_residual_cl;                 // 1계열 침전지 목표 잔류염소
    private float g_pre2_set_max;               // 전염소 2계열 주입률 최대값
    private float g_pre2_set_min;               // 전염소 2계열 주입률 최소값
    private float g_pre2_chg_limit_for_onetime; // 전염소 2계열 1회 변경 한계치
    private float d2_target_cl;                 // 2계열 혼화지 목표 잔류염소
    private float e2_target_cl;                 // 2계열 침전지 목표 잔류염소
    private float g_pre_calib_cycle;            // 전차염 보정주기
    private float g_pre_max_limit_0;			// 전차염 주입률 상한 0시
    private float g_pre_max_limit_1;			// 전차염 주입률 상한 1시
    private float g_pre_max_limit_2;			// 전차염 주입률 상한 2시
    private float g_pre_max_limit_3;			// 전차염 주입률 상한 3시
    private float g_pre_max_limit_4;			// 전차염 주입률 상한 4시
    private float g_pre_max_limit_5;			// 전차염 주입률 상한 5시
    private float g_pre_max_limit_6;			// 전차염 주입률 상한 6시
    private float g_pre_max_limit_7;			// 전차염 주입률 상한 7시
    private float g_pre_max_limit_8;			// 전차염 주입률 상한 8시
    private float g_pre_max_limit_9;			// 전차염 주입률 상한 9시
    private float g_pre_max_limit_10;			// 전차염 주입률 상한 10시
    private float g_pre_max_limit_11;			// 전차염 주입률 상한 11시
    private float g_pre_max_limit_12;			// 전차염 주입률 상한 12시
    private float g_pre_max_limit_13;			// 전차염 주입률 상한 13시
    private float g_pre_max_limit_14;			// 전차염 주입률 상한 14시
    private float g_pre_max_limit_15;			// 전차염 주입률 상한 15시
    private float g_pre_max_limit_16;			// 전차염 주입률 상한 16시
    private float g_pre_max_limit_17;			// 전차염 주입률 상한 17시
    private float g_pre_max_limit_18;			// 전차염 주입률 상한 18시
    private float g_pre_max_limit_19;			// 전차염 주입률 상한 19시
    private float g_pre_max_limit_20;			// 전차염 주입률 상한 20시
    private float g_pre_max_limit_21;			// 전차염 주입률 상한 21시
    private float g_pre_max_limit_22;			// 전차염 주입률 상한 22시
    private float g_pre_max_limit_23;			// 전차염 주입률 상한 23시
    private float g_pre_min_limit_0;			// 전차염 주입률 하한 0시
    private float g_pre_min_limit_1;			// 전차염 주입률 하한 1시
    private float g_pre_min_limit_2;			// 전차염 주입률 하한 2시
    private float g_pre_min_limit_3;			// 전차염 주입률 하한 3시
    private float g_pre_min_limit_4;			// 전차염 주입률 하한 4시
    private float g_pre_min_limit_5;			// 전차염 주입률 하한 5시
    private float g_pre_min_limit_6;			// 전차염 주입률 하한 6시
    private float g_pre_min_limit_7;			// 전차염 주입률 하한 7시
    private float g_pre_min_limit_8;			// 전차염 주입률 하한 8시
    private float g_pre_min_limit_9;			// 전차염 주입률 하한 9시
    private float g_pre_min_limit_10;			// 전차염 주입률 하한 10시
    private float g_pre_min_limit_11;			// 전차염 주입률 하한 11시
    private float g_pre_min_limit_12;			// 전차염 주입률 하한 12시
    private float g_pre_min_limit_13;			// 전차염 주입률 하한 13시
    private float g_pre_min_limit_14;			// 전차염 주입률 하한 14시
    private float g_pre_min_limit_15;			// 전차염 주입률 하한 15시
    private float g_pre_min_limit_16;			// 전차염 주입률 하한 16시
    private float g_pre_min_limit_17;			// 전차염 주입률 하한 17시
    private float g_pre_min_limit_18;			// 전차염 주입률 하한 18시
    private float g_pre_min_limit_19;			// 전차염 주입률 하한 19시
    private float g_pre_min_limit_20;			// 전차염 주입률 하한 20시
    private float g_pre_min_limit_21;			// 전차염 주입률 하한 21시
    private float g_pre_min_limit_22;			// 전차염 주입률 하한 22시
    private float g_pre_min_limit_23;			// 전차염 주입률 하한 23시
    private float g_e_residual_cl_holding;		// 목표 침전지 잔류염소 대비 잔류염소 허용 편차
}
