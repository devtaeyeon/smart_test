package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 5지 세부현황
public class AiSedimentationLocation5RealtimeDTO
{
    // 대차 위치
    @JsonProperty("e_loc_sc_5")
    private AiSedimentationSludgeCollectorPosition e_loc_sc_5;

    // 슬러지 양
    @JsonProperty("AIE-5005")
    private Float AIE_5005;

    // 대차 스케쥴
    @JsonProperty("AIE-6005")
    private AiSedimentationSludgeCollectorSchedule AIE_6005;
    
    // 인발밸브 상태
    private AiSedimentation5drnvv e_drn_vv5;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5105")
    private Object AIE_5105;

    // 대차 시작 제어
    @JsonProperty("AIE-7005")
    private Integer AIE_7005;

    // 시작 시 제어
    @JsonProperty("AIE-8005")
    private Integer AIE_8005;

    // 시작 분 제어
    @JsonProperty("AIE-8105")
    private Integer AIE_8105;

    // 운영 상태
    @JsonProperty("e_operation_mode")
    private Integer e_operation_mode;
}
