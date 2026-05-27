package co.irexnet.waio.WAIO_ServerAgent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// Front-end 팝업을 통한 제어 명령을 저장하기 위한 class
public class InterfaceAlarmControlDTO
{
    @JsonProperty("alm_id")
    private int alm_id;

    @JsonProperty("alm_ntf_ti")
    private Date alm_ntf_ti;

    @JsonProperty("kfk_flg")
    private Integer kfk_flg;     // Kafka Flag 0:Default, 1:ai_popup
    
    @JsonProperty("no_action_flg")
    private String no_action_flg;
}
