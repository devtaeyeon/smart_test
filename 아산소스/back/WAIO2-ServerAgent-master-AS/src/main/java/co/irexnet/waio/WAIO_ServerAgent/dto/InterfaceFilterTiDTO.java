package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 여과 공정 여과 지속 시간 값을 저장하기 위한 class
public class InterfaceFilterTiDTO {
    private int f_location_ti_set_max;
    private float f_location_wl_max;
    private float f_h_le1;
    private float f_h_le2;
}
