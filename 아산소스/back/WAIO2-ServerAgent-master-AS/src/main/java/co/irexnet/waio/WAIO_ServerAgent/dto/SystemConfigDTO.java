package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// system_config
public class SystemConfigDTO
{
    private String scd_addr1;      // SCADA 1번 주소
    private int scd_port1;            // SCADA 1번 port
    private int daq_port1;              // 수집기 1번 port
    private String scd_addr2;      // SCADA 2번 주소
    private int scd_port2;            // SCADA 2번 port
    private int daq_port2;              // 수집기 2번 port
    private String anl_addr1;   // 분석 서버 1번 주소
    private int anl_rm_port1;           // 분석 서버 1번 resource manager port
    private int anl_nm_port1;           // 분석 서버 1번 node manager port
    private int anl_nn_port1;           // 분석 서버 1번 name node port
    private String anl_addr2;   // 분석 서버 2번 주소
    private int anl_rm_port2;           // 분석 서버 2번 resource manager port
    private int anl_nm_port2;           // 분석 서버 2번 node manager port
    private int anl_nn_port2;           // 분석 서버 2번 name node port
    private String kfk_addr;               // Kafka 주소
    private String bd_kfk_addr;            // 유역 본부 Kafka 주소
}
