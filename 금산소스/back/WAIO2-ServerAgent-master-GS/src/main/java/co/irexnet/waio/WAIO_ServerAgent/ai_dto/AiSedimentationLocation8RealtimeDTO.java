package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 8지 세부현황
public class AiSedimentationLocation8RealtimeDTO
{
    // 대차 위치
    @JsonProperty("e_loc_sc_8")
    private AiSedimentationSludgeCollectorPosition e_loc_sc_8;

    // 슬러지 양
    @JsonProperty("AIE-5008")
    private Float AIE_5008;

    // 대차 스케쥴
    @JsonProperty("AIE-6008")
    private AiSedimentationSludgeCollectorSchedule AIE_6008;
    
    // 인발밸브 상태
    private AiSedimentation8drnvv e_drn_vv8;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5108")
    private Object AIE_5108;

    // 대차 시작 제어
    @JsonProperty("AIE-7008")
    private Integer AIE_7008;

    // 시작 시 제어
    @JsonProperty("AIE-8008")
    private Integer AIE_8008;

    // 시작 분 제어
    @JsonProperty("AIE-8108")
    private Integer AIE_8108;

    // 운영 상태
    @JsonProperty("e_operation_mode")
    private Integer e_operation_mode;
}
