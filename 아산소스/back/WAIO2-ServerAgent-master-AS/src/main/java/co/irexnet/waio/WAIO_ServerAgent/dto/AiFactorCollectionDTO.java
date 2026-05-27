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
	//Receiving
	private float b_in_fr; 				// 원수 유입유량
	private float b_in_pr;				// 원수 유입 압력
	private float h_location_le1; 		// 정수지 #1 수위
	private float h_location_le2; 		// 정수지 #2 수위
	private float h_out_fr;				// 정수지 총 유출 유량
	
	//Coagulant
	private float b_tb; 				// 원수 탁도
	private float b_ph; 				// 원수 pH
	private float b_te; 				// 원수 수온
	private float b_cu; 				// 원수 전기전도도

	//Mixing
//	private float b_te;					// 원수 수온
	private float d_tb_e;				// 침전지 탁도

	//Sedimentation
//	private float b_in_fr;				// 원수 유입 유량
//	private float b_tb;					// 원수 탁도
	private float c_cf_coagulant;		// 약품
	private float mm_fr;				// 약품 주입량
	private float c_mm_fr_etc;			// 폴리아민 주입률
	private float c_mm_fr_etc1;			// 활성탄 주입률
	
	//Filter
//	private float b_in_fr;				// 원수 유입 유량
	private float e2_tb_b; 				// 침전수 탁도
	private float f_speed; 				// 여과속도
	private float f_out_fr;				// 여과 유출 유량
	
	//Disinfection
	private float g_tei;				// 기온
//	private float b_te;					// 수온
//	private float h_tb;					// 정수 탁도
//	private float h_ph;					// 정수 pH
	private float g_pre_chol_rate;		// 현재 주입률
	private float g_d_residual_cl;		// 혼화지 잔류 염소
	private float g_e_residual_cl;		// 침전지 잔류 염소
	
	private float g_h_in_residual_cl;	// 정수지 유입 잔류 염소
//	private float g_h_out_residual_cl;	// 정수지 유출 잔류 염소
	private float g_post_chol_rate;		// 현재 주입률
}
