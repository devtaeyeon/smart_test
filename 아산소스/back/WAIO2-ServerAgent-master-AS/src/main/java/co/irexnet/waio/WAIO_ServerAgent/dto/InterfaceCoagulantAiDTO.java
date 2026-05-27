package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InterfaceCoagulantAiDTO
{
    private float c_cf_max;
    private float c_cf_min;
    private String c_cf_coagulant;
    private float c_user_correct;
    private float c_user_tb_e;
}
