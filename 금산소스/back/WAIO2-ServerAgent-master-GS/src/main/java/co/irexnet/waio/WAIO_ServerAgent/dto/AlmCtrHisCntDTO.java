package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// Alarm Control History DTO
public class AlmCtrHisCntDTO
{
    private Integer totalCnt; // 전체 갯수
    private Integer controlCnt; // 제어 갯수
    private Integer cancelCnt; // 취소 갯수
    private Integer noActionCnt; // 무응답 갯수
}
