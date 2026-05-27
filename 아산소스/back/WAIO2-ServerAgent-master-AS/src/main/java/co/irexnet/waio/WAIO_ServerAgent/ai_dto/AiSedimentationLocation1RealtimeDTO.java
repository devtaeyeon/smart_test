package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//침전지 1지 세부현황
public class AiSedimentationLocation1RealtimeDTO {
	// 대차 위치
	@JsonProperty("e_loc_sc_1")
	private AiSedimentationSludgeCollectorPosition e_loc_sc_1;

	// 슬러지 양
	@JsonProperty("AIE-5001")
	private Float AIE_5001;

	// 대차 스케줄
	@JsonProperty("AIE-6001")
	private AiSedimentationSludgeCollectorSchedule AIE_6001;

	// 인발밸브 상태
	private AiSedimentation1drnvv e_drn_vv1;

	// 슬러지 양 트렌드
	@JsonProperty("AIE-5101")
	private Object AIE_5101;

	// 대차 시작 제어
	@JsonProperty("AIE-7001")
	private Integer AIE_7001;

	// 시작 시 제어
	@JsonProperty("AIE-8001")
	private Integer AIE_8001;

	// 시작 분 제어
	@JsonProperty("AIE-8101")
	private Integer AIE_8101;

	// 운영 상태
	@JsonProperty("e_operation_mode")
	private Integer e_operation_mode;
	
	// 슬러지 경과시간
    @JsonProperty("AIE-5011")
    private Float AIE_5011;
}
