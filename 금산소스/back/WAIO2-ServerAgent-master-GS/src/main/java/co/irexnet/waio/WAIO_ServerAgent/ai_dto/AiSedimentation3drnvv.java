package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// 침전지 3지 인발밸브 상태
public class AiSedimentation3drnvv
{
    private Integer e_drn_vv3_1;
    private Integer e_drn_vv3_2;
    private Integer e_drn_vv3_3;
    private Integer e_drn_vv3_4;
    private Integer e_drn_vv3_5;
    private Integer e_drn_vv3_6;
}
