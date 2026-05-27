package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// 침전지 5지 인발밸브 상태
public class AiSedimentation5drnvv
{
    private Integer e_drn_vv5_1;
    private Integer e_drn_vv5_2;
    private Integer e_drn_vv5_3;
    private Integer e_drn_vv5_4;
    private Integer e_drn_vv5_5;
    private Integer e_drn_vv5_6;
}
