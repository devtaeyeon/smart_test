package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 2지 세부현황
public class AiSedimentationLocation2RealtimeDTO
{
    // 대차 위치
    @JsonProperty("e_loc_sc_2")
    private AiSedimentationSludgeCollectorPosition e_loc_sc_2;

    // 슬러지 양
    @JsonProperty("AIE-5002")
    private Float AIE_5002;

    // 대차 스케줄
    @JsonProperty("AIE-6002")
    private AiSedimentationSludgeCollectorSchedule AIE_6002;

    // 인발밸브 상태
    private AiSedimentation2drnvv e_drn_vv2;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5102")
    private Object AIE_5102;

    // 대차 시작 제어
    @JsonProperty("AIE-7002")
    private Integer AIE_7002;

    // 시작 시 제어
    @JsonProperty("AIE-8002")
    private Integer AIE_8002;

    // 시작 분 제어
    @JsonProperty("AIE-8102")
    private Integer AIE_8102;

    // 운영 상태
    @JsonProperty("e_operation_mode")
    private Integer e_operation_mode;
}
