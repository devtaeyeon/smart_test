package co.irexnet.waio.WAIO_ServerAgent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DashboardPointDTO
{
    @JsonProperty("x")
    private double x;

    @JsonProperty("y")
    private double y;
}
