package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;
@Getter
@Setter
@ToString
// AiFactorCollectionDTO 공정별 주요인자 모음 DTO
public class AiFactorCollectionDTO
{	
	
	//Coagulant
	private float b_tb; //	원수 탁도
	private float b_ph; //	원수 pH
	private float b_te; //	원수 수온
	private float b_cu; //	원수 전기전도도

	//Mixing
	private float b_te_loc1; // 원수 수온 1지
	private float b_te_loc2; // 원수 수온 2지
	private float d_dv_loc1; // 점성 계수 1지
	private float d_dv_loc2; // 점성 계수 2지
	private float d_im_d;	// 임펠러 직경
	private float d_pw;// Power NUmber
	private float d_v;	// 조 체적
	
	//Disinfection
//	private float b_te;	// 수온
//	private float b_tb; // 원수 탁도
//	private float b_ph;	// 원수 pH
//	private float b_cu; // 원수 전기 전도도
	private float b_al; // 원수 알카리도
	private float mnr; // 취수 망간
	private float b_in_fr; // 원수 유입 유량
	private float g_d_residual_cl;	//혼화지 잔류 염소
	private float g_pre_chol_rate;	//현재 주입률
	private float h_ph;	//정수 pH
	private float f_tb; // 막여과 통합 탁도
	private float h_tb; // 정수 탁도
	private float g_h_in_residual_cl;	//정수지 유입 잔류염소 1계열
	private float g_h_out_residual_cl; //정수지 유출 잔류 염소
	private float g_post_chol_rate;	//현재 주입률 2계열
	private float g_b_residual_cl; // 원수 잔류염소
	private float g_f_out_residual_cl; //여과수 통합 잔류염소
	private float g_h_in_residual_cl_prep; // 정수지 유입 잔류염소(평활)
	private float disinfection_index; //소독 단계
}
