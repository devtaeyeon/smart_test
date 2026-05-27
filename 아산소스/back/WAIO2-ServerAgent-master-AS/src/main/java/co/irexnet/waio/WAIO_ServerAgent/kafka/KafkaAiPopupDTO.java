package co.irexnet.waio.WAIO_ServerAgent.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KafkaAiPopupDTO
{
    @JsonProperty("alarm_id")
    private int alarmId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("url")
    private String url;

    @JsonProperty("time")
    private String time;
}
