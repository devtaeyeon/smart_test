package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 12지 세부현황
public class AiSedimentationLocation12RealtimeDTO
{
    // 대차 위치
    @JsonProperty("e_loc_sc_12")
	private AiSedimentationSludgeCollectorPosition e_loc_sc_12;

    // 슬러지 양
    @JsonProperty("AIE-5012")
    private Float AIE_5012;

    // 대차 스케쥴
    @JsonProperty("AIE-6012")
    private AiSedimentationSludgeCollectorSchedule AIE_6012;

    // 인발밸브 상태
	private AiSedimentation12drnvv e_drn_vv12;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5112")
    private Object AIE_5112;

    // 대차 시작 제어
    @JsonProperty("AIE-7012")
    private Integer AIE_7012;

    // 시작 시 제어
    @JsonProperty("AIE-8012")
    private Integer AIE_8012;

    // 시작 분 제어
    @JsonProperty("AIE-8112")
    private Integer AIE_8112;

    // 운영 상태
    @JsonProperty("e_operation_mode")
    private Integer e_operation_mode;
    
    // 슬러지 경과시간
    @JsonProperty("AIE-50112")
    private Float AIE_50112;
}
