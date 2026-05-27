package co.irexnet.waio.WAIO_ServerAgent.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 혼화응집 알고리즘 설정값을 저장하기 위한 class
public class InterfaceMixingAiDTO {
    private float d_g_value_step1; // 응집기 1단 교반강도 G 값
    private float d_g_value_step2; // 응집기 2단 교반강도 G 값
    private float d_g_value_step3; // 응집기 3단 교반강도 G 값
    private float d_g_value_ctr_flag; // G값 제어 여부

    private boolean jiModifyMode; // 지별 상하한 제어 여부
    private boolean autoModeCrt; // 자동모드상세 제어 여부
    private float d_g_step1_min; // 1열 하한
    private float d_g_step1_max; // 1열 상한
    private float d_g_step2_min; // 2열 하한
    private float d_g_step2_max; // 2열 상한
    private float d_g_step3_min; // 3열 하한
    private float d_g_step3_max; // 3열 상한
    private float d_g_step1_crt; // 1열 보정값
    private float d_g_step2_crt; // 2열 보정값
    private float d_g_step3_crt; // 3열 보정값
}
