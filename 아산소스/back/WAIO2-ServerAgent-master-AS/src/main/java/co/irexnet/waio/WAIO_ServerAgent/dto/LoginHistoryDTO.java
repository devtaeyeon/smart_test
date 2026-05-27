package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// login_history
public class LoginHistoryDTO
{
    private int seq;
    private String usr_id;      // 사용자 ID
    private String usr_nm;        // 사용자 이름
    private Integer lgn_ty;       // 이력 종류 (-1:비정상 로그아웃, 0:로그아웃, 1:로그인)
    private Date lgn_ti;     // 이력 발생 시간
    private String lgn_addr;     // 이력 발생 주소
}
