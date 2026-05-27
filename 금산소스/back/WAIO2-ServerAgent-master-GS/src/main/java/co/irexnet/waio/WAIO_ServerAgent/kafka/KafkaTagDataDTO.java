package co.irexnet.waio.WAIO_ServerAgent.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KafkaTagDataDTO
{
    @JsonProperty("tagname")
    private String tagname;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("value")
    private String value;

    @JsonProperty("quality")
    private String quality;

}
