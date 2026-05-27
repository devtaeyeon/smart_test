package co.irexnet.waio.WAIO_ServerAgent.ai_dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 7지 세부현황
public class AiSedimentationLocation7RealtimeDTO
{
    // 대차 위치
    @JsonProperty("e_loc_sc_7")
    private AiSedimentationSludgeCollectorPosition e_loc_sc_7;

    // 슬러지 양
    @JsonProperty("AIE-5007")
    private Float AIE_5007;

    // 대차 스케쥴
    @JsonProperty("AIE-6007")
    private AiSedimentationSludgeCollectorSchedule AIE_6007;
    
    // 인발밸브 상태
    private AiSedimentation7drnvv e_drn_vv7;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5107")
    private Object AIE_5107;

    // 대차 시작 제어
    @JsonProperty("AIE-7007")
    private Integer AIE_7007;

    // 시작 시 제어
    @JsonProperty("AIE-8007")
    private Integer AIE_8007;

    // 시작 분 제어
    @JsonProperty("AIE-8107")
    private Integer AIE_8107;

    // 운영 상태
    @JsonProperty("e_operation_mode")
    private Integer e_operation_mode;
}
