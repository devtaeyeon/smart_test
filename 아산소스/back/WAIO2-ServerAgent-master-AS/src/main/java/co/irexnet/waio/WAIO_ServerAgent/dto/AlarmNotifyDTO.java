package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// alarm_notify
public class AlarmNotifyDTO
{
    private int seq;
    private int alm_id;           // 알람 ID
    private Date alm_ntf_ti;        // 알람 시간
    private String host;        // 알람 대상 호스트 명
    private String val;           // 알람 값
    private int processStep;
//    private boolean ack_state;    // 알람 네비게이터 삭제
}
