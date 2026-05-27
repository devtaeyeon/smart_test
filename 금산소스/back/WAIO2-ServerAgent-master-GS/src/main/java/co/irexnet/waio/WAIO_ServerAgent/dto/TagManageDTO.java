package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 태그명
public class TagManageDTO
{
    private String ai_cd;  // 알고리즘 공정 코드
    private String proc_cd;    // 공정 코드
    private Integer ser;         // 계열
    private Integer loc;       // 지
    private String itm;            // 항목명
    private String tag_sn;            // 태그명
    private String dp;         // 표시명
    private Integer tag_ty;           // 종류(0:실시간 태그, 1:알고리즘 태그, 2:시각화 태그, 3:내부 업데이트 태그)
}
