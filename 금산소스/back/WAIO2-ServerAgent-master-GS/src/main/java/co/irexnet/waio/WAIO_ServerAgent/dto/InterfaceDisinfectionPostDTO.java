package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 소독 후염소 알고리즘 설정값을 저장하기 위한 class
public class InterfaceDisinfectionPostDTO
{
    private float g_post_set_max;              		  	 // 후염소 주입률 최대값
    private float g_post_set_min;             		  	 // 후염소 주입률 최소값
    private float g_post_chg_limit_for_onetime; 	  	 // 후염소 1회 변경 한계치
    private float g_post_calib_cycle;                    // 보정주기
    private float g_h_in_obj_residual_cl;               // 정수지 유입 목표 잔류염소
    private float g_post_chol_rate_holding_time;		// 주입률 변경 후 잔류염소 미변동 시 대기시간
    private float g_h_in_residual_cl_holding;			// 정수지 유입 잔류염소 홀딩 범위
}
