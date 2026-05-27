package co.irexnet.waio.WAIO_ServerAgent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 시스템명을 저장하기 위한 class
public class SystemNameDTO
{
    @JsonProperty("name")
    private String name;
}
