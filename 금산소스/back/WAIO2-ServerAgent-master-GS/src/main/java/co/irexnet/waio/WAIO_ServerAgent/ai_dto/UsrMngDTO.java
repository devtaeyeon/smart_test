package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//사용자 정의 테이블 데이터
public class UsrMngDTO
{
    private String itm;    // 항목명
    private String tag_sn;    // 태그명
    private String init_val;    // 값
}
