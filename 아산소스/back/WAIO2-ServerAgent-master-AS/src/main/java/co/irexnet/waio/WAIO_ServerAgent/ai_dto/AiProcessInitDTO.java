package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// ai_receiving_init, ai_coagulant_init, ai_mixing_init, ai_sedimentation_init, ai_filter_init, ai_gac_init
// ai_disinfection_init, ai_ozone_init
// 공정 별 알고리즘 내부 저장 값
public class AiProcessInitDTO
{
    private String itm;    // 항목명
    private String tag_sn;    // 태그명
    private Float init_val;    // 값
    public AiProcessInitDTO select(String d_OPERATION_MODE) {
        return null;
    }
}
