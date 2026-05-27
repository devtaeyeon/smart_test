package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// EMS 예측 결과 테이블
public class AiEmsRealtimeDTO
{
	private String tag_sn;        // 태그명
    private String tag_val;       // 값
}
