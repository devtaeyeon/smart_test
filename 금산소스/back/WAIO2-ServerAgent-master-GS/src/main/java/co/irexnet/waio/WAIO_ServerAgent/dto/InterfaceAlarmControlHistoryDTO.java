package co.irexnet.waio.WAIO_ServerAgent.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 팝업을 통한 제어 명령을 저장하기 위한 class
public class InterfaceAlarmControlHistoryDTO extends InterfaceDateSearchDTO {
    @JsonProperty("alm_id")
    private int alm_id;

    @JsonProperty("alm_ntf_ti")
    private Date alm_ntf_ti;
    
    @JsonProperty("ctrYn")
    private String ctrYn;

    @JsonProperty("seq")
    private int seq;

    @JsonProperty("almSeq")
    private int almSeq;

    @JsonProperty("usrId")
    private String usrId;

    @JsonProperty("usrNm")
    private String usrNm;

    @JsonProperty("updTi")
    private Date updTi;

    @JsonProperty("ctrTi")
    private Date ctrTi;

    @JsonProperty("almTy")
    private int almTy;

    @JsonProperty("dp_nm")
    private String dp_nm;

    @JsonProperty("tagSn")
    private String tagSn;
    
    @JsonProperty("process")
    private String process;

    @JsonProperty("processStep")
    private String processStep;
    
    @JsonProperty("disinfectionIndex")
    private int disinfectionIndex;
    
    @JsonProperty("historySeq")
    private int historySeq;

    @JsonProperty("tagCmpVal")
    private String tagCmpVal;
    
    @JsonProperty("tagVal")
    private String tagVal;

    @JsonProperty("url")
    private String url;

}
