package co.irexnet.waio.WAIO_ServerAgent.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// TB_AI_OPR_HIS
// 운전모드 이력 테이블
public class AiOprHistoryDTO
{
	private int his_seq; 				// 이력 순번
	private String proc_cd; 			// 공정 코드
	private String disinfection_index; 	// 소독 공정 인덱스 (PRE, PERI, POST, NONE)
	private Integer ai_opr;				// AI운전모드
	private Date his_date; 				// 이력 날짜
	private Integer opr_minutes; 		// 운영 시간(분)
}
