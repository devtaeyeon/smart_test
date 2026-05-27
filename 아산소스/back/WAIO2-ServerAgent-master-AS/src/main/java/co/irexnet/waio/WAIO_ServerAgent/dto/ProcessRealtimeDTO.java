package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// receiving_realtime, coagulant_realtime, mixing_realtime, sedimentation_realtime, filter_realtime, gac_realtime
// disinfection_realtime, ozone_realtime
public class ProcessRealtimeDTO
{
    private String tag_sn;        // 태그명
    private String tag_val;       // 값
    private Date upd_ti;   // 업데이트 시간
    private Integer qlt;    // 퀄리티
}
