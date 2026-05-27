package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// user
public class UserDTO
{
    private String usr_id;      // 사용자 ID
    private String usr_pw;    // 사용자 비밀번호
    private String usr_nm;        // 사용자 이름
    private String usr_pn;    // 부서명
    private Integer usr_auth;  // 권한
    private Integer usr_ti;  // 로그인 유지시간
}
