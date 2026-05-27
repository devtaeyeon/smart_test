package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//침전지 4지 세부현황
public class AiSedimentationLocation4RealtimeDTO {
	// 대차 위치
	@JsonProperty("e_loc_sc_4")
	private AiSedimentationSludgeCollectorPosition e_loc_sc_4;

	// 슬러지 양
	@JsonProperty("AIE-5004")
	private Float AIE_5004;

	// 대차 스케쥴
	@JsonProperty("AIE-6004")
	private AiSedimentationSludgeCollectorSchedule AIE_6004;

	// 인발밸브 상태
	private AiSedimentation4drnvv e_drn_vv4;

	// 슬러지 양 트렌드
	@JsonProperty("AIE-5104")
	private Object AIE_5104;

	// 대차 시작 제어
	@JsonProperty("AIE-7004")
	private Integer AIE_7004;

	// 시작 시 제어
	@JsonProperty("AIE-8004")
	private Integer AIE_8004;

	// 시작 분 제어
	@JsonProperty("AIE-8104")
	private Integer AIE_8104;

	// 운영 상태
	@JsonProperty("e_operation_mode")
	private Integer e_operation_mode;
	
	// 슬러지 경과시간
    @JsonProperty("AIE-5014")
    private Float AIE_5014;
}
