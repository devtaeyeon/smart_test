package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 6지 세부현황
public class AiSedimentationLocation6RealtimeDTO
{
    // 대차 위치
    @JsonProperty("e_loc_sc_6")
    private AiSedimentationSludgeCollectorPosition e_loc_sc_6;

    // 슬러지 양
    @JsonProperty("AIE-5006")
    private Float AIE_5006;

    // 대차 스케쥴
    @JsonProperty("AIE-6006")
    private AiSedimentationSludgeCollectorSchedule AIE_6006;
    
    // 인발밸브 상태
    private AiSedimentation6drnvv e_drn_vv6;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5106")
    private Object AIE_5106;

    // 대차 시작 제어
    @JsonProperty("AIE-7006")
    private Integer AIE_7006;

    // 시작 시 제어
    @JsonProperty("AIE-8006")
    private Integer AIE_8006;

    // 시작 분 제어
    @JsonProperty("AIE-8106")
    private Integer AIE_8106;

    // 운영 상태
    @JsonProperty("e_operation_mode")
    private Integer e_operation_mode;
}
