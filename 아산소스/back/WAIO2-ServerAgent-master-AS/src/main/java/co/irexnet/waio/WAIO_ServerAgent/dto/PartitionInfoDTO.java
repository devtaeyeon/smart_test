package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// partition_info
public class PartitionInfoDTO
{
    private String host;    // 대상 호스트명
    private String nm;        // 파티션 명
    private long tot_size;    // 전체 크기
    private long unu_size;   // 사용가능 크기
}
