package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Resource Network Interface를 저장하기 위한 class
public class InterfaceInfoDTO
{
    private String host;        // 호스트 명
    private String nm;            // 인터페이스 명
    private String dp_nm;    // 표시 명
    private String ipv4;            // IP 주소
    private String mac;             // MAC 주소
}
