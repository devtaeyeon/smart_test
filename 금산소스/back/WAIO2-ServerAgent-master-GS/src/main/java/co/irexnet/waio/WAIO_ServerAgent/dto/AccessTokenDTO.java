package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// access_token
public class AccessTokenDTO
{
    private int seq;
    private String tkn;    // access token
    private String usr_id;          // 사용자 아이디
    private String usr_nm;            // 사용자 이름
    private Integer usr_auth;      // 사용자 권한
    private Date expr_ti;        // 토큰 만료 시간
}
