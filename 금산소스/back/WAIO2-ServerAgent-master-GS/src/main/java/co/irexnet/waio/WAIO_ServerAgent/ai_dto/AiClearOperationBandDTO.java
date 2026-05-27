package co.irexnet.waio.WAIO_ServerAgent.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_clear_operation_band, ai_clear_ems_operation_band, ai_clear_wide_operation_band
// 정수지 수위 밴드
public class AiClearOperationBandDTO
{
    private Date ti_seq;     // 시간
    private Double h_bnd_uplmt;      // up band
    private Double h_bnd_lolmt;    // down band
}
