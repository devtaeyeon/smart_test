package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 소독 중염소 알고리즘 설정값을 저장하기 위한 class
public class InterfaceDisinfectionPeriDTO
{
    private float g_peri_set_max;              		  	 // 중염소 주입률 최대값
    private float g_peri_set_min;             		  	 // 중염소 주입률 최소값
    private float g_peri_chg_limit_for_onetime; 	  	 // 중염소 1회 변경 한계치
    private float g_peri_calib_cycle;                    // 보정주기
    private float g_f_out_obj_residual_cl;               // 여과지 유출 목표 잔류염소
}