package co.irexnet.waio.WAIO_ServerAgent.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 로그인 정보를 저장하기 위한 class
public class InterfaceLoginDTO
{
    @JsonProperty("usr_id")
    private String usr_id;

    @JsonProperty("usr_pw")
    private String usr_pw;
    
    @JsonProperty("localtime")
    private Date localtime;
}
