package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// 침전지 4지 인발밸브 상태
public class AiSedimentation4drnvv
{
    private Integer e_drn_vv4_1;
    private Integer e_drn_vv4_2;
    private Integer e_drn_vv4_3;
    private Integer e_drn_vv4_4;
    private Integer e_drn_vv4_5;
    private Integer e_drn_vv4_6;
}
