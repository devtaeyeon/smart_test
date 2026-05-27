package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_receiving_alarm, ai_coagulant_alarm, ai_mixing_alarm, ai_sedimentation_alarm, ai_filter_alarm, ai_gac_alarm
// ai_disinfection_alarm, ai_ozone_alarm
// 공정 별 알람 테이블
public class AiProcessAlarmDTO
{
    private int alm_id;           // 알람 ID
    private Date alm_ti;        // 알람 시간
    private Integer kfk_flg;     // Kafka Flag 0:Default, 1:ai_popup
    private int processStep;     // 공정단계
    private int disinfectionIndex; // 소독 전/후차염 단계
}
