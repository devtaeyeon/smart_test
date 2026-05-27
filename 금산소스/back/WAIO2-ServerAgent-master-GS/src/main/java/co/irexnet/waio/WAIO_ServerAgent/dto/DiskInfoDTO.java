package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// disk_info
public class DiskInfoDTO
{
    private String host;    // 대상 호스트 명
    private String md;       // 모델 명
    private long size;          // 크기
}
