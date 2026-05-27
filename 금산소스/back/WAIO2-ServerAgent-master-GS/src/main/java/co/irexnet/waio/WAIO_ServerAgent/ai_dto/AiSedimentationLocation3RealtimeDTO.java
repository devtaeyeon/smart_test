package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 3지 세부현황
public class AiSedimentationLocation3RealtimeDTO
{
    // 대차 위치
    @JsonProperty("e_loc_sc_3")
    private AiSedimentationSludgeCollectorPosition e_loc_sc_3;

    // 슬러지 양
    @JsonProperty("AIE-5003")
    private Float AIE_5003;

    // 대차 스케쥴
    @JsonProperty("AIE-6003")
    private AiSedimentationSludgeCollectorSchedule AIE_6003;
    
    // 인발밸브 상태
    private AiSedimentation3drnvv e_drn_vv3;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5103")
    private Object AIE_5103;

    // 대차 시작 제어
    @JsonProperty("AIE-7003")
    private Integer AIE_7003;

    // 시작 시 제어
    @JsonProperty("AIE-8003")
    private Integer AIE_8003;

    // 시작 분 제어
    @JsonProperty("AIE-8103")
    private Integer AIE_8103;

    // 운영 상태
    @JsonProperty("e_operation_mode")
    private Integer e_operation_mode;
}
