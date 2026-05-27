package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// tag_description
public class TagDescriptionDTO
{
    private int seq;
    private String tag_sn;        // 태그 명
    private String desc; // 설명
    private String crtd;     // 생성일
}
