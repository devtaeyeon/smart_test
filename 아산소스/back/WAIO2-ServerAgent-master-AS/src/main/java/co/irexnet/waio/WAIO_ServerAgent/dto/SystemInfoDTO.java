package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// system_info
public class SystemInfoDTO
{
    private String host;        // 호스트 명
    private String sys_nm;            // 시스템 명
    private String os;              // 운영체제
    private String md;           // 모델명
    private String prc_nm;  // 프로세스 명
    private Integer prc_cnt;  // CPU 개수
    private Integer core_cnt;     // 코어 수
    private Integer lgc_core_cnt;  // 논리 코어 수
    private long max_freq;     // CPU 최대 주파수
    private long tot_mem;      // 총 메모리
    private long avl_mem;  // 사용가능 메모리
    private long db_used;           // DB 사용량
    private long db_free;           // DB 잔여량
    private Date sys_upd_ti;       // 업데이트 시간
}
