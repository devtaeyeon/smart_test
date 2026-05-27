package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// TB_AI_OPR_RT
// 운전모드 실시간 테이블
public class AiOprRealTimeDTO
{
	private String proc_cd; 			// 공정코드
	private String disinfection_index; 	// 소독 공정 인덱스 (PRE, PERI, POST, NONE)
	private Integer ai_opr; 			// AI운전모드 (0:AI분석, 1:AI추천, 2:AI)
	private Integer opr_minutes;		// 운영시간(분)
}
