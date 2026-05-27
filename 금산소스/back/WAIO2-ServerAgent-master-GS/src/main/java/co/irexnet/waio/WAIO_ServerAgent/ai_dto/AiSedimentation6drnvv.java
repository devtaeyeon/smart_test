package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// 침전지 6지 인발밸브 상태
public class AiSedimentation6drnvv
{
    private Integer e_drn_vv6_1;
    private Integer e_drn_vv6_2;
    private Integer e_drn_vv6_3;
    private Integer e_drn_vv6_4;
    private Integer e_drn_vv6_5;
    private Integer e_drn_vv6_6;
}
