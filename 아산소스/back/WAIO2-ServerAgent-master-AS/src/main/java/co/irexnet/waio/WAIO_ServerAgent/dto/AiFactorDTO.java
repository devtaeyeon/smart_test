package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;
@Getter
@Setter
@ToString
// TB_AI_FACTOR DTO
public class AiFactorDTO
{
    private String proc_cd; //공정코드
    private String disinfection_index; //소독 공정 인덱스
    private Date rnti; //실행시간
    private String factor;//주요인자
}
