package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 11지 세부현황
public class AiSedimentationLocation11RealtimeDTO
{
    // 대차 위치
    @JsonProperty("e_loc_sc_11")
	private AiSedimentationSludgeCollectorPosition e_loc_sc_11;

    // 슬러지 양
    @JsonProperty("AIE-5011")
    private Float AIE_5011;

    // 대차 스케쥴
    @JsonProperty("AIE-6011")
    private AiSedimentationSludgeCollectorSchedule AIE_6011;

    // 인발밸브 상태
	private AiSedimentation11drnvv e_drn_vv11;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5111")
    private Object AIE_5111;

    // 대차 시작 제어
    @JsonProperty("AIE-7011")
    private Integer AIE_7011;

    // 시작 시 제어
    @JsonProperty("AIE-8011")
    private Integer AIE_8011;

    // 시작 분 제어
    @JsonProperty("AIE-8111")
    private Integer AIE_8111;

    // 운영 상태
    @JsonProperty("e_operation_mode")
    private Integer e_operation_mode;
    
    // 슬러지 경과시간
    @JsonProperty("AIE-50111")
    private Float AIE_50111;
}
