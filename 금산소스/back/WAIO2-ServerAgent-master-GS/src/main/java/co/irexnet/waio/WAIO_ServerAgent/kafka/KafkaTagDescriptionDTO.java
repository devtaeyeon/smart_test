package co.irexnet.waio.WAIO_ServerAgent.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KafkaTagDescriptionDTO
{
    @JsonProperty("tagname")
    private String tagname;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created")
    private String created;
}
