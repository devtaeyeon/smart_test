package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;
@Getter
@Setter
@ToString
//운전모드 이력
public class AiOprHistoryDTO
{	
	private int his_seq; //이력 순번
	private String proc_cd; //공정 코드
	private String disinfection_index; // 소독 공정 인덱스
	private int ai_opr;// AI운전모드
	private LocalDate his_date; //이력 날짜
	private int opr_minutes; // 운영 시간(분)
}
