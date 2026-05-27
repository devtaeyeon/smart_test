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
    private float g_pre_set_max;              		   // 전염소 주입률 최대값
    private float g_pre_set_min;             		   // 전염소 주입률 최소값
    private float g_pre_chg_limit_for_onetime; 	   	   // 전염소 1회 변경 한계치
    private float g_pre_calib_cycle;                   // 보정주기
    private float g_d_obj_residual_cl;                 // 혼화지 목표 잔류염소
    private float g_d_residual_cl_holding;			   // 혼화지 잔류염소 홀딩 범위
}
