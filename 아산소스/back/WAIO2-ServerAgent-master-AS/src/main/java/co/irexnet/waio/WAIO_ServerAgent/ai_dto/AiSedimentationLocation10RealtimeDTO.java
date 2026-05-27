package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 10지 세부현황
public class AiSedimentationLocation10RealtimeDTO
{
    // 대차 위치
    @JsonProperty("SCI-2910")
    private AiSedimentationSludgeCollectorPosition SCI_2910;

    // 슬러지 양
    @JsonProperty("AIE-5010")
    private Float AIE_10;

    // 대차 스케쥴
    @JsonProperty("AIE-6010")
    private AiSedimentationSludgeCollectorSchedule AIE_6010;

    // 인발밸브 1 열림
    @JsonProperty("VVB-2913")
    private Integer VVB_2913;

    // 인발밸브 2 열림
    @JsonProperty("VVB-2916")
    private Integer VVB_2916;

    // 인발밸브 3 열림
    @JsonProperty("VVB-2910")
    private Integer VVB_2919;

    // 인발밸브 4 열림
    @JsonProperty("VVB-2922")
    private Integer VVB_2922;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5110")
    private Object AIE_5110;

    // 대차 시작 제어
    @JsonProperty("AIE-7010")
    private Integer AIE_7010;

    // 시작 시 제어
    @JsonProperty("AIE-8010")
    private Integer AIE_8010;

    // 시작 분 제어
    @JsonProperty("AIE-8110")
    private Integer AIE_8110;

    // 운영 상태
    @JsonProperty("operation")
    private Integer operation;
}
