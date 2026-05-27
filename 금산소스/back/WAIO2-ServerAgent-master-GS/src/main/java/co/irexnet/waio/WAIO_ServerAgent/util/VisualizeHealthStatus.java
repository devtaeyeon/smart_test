package co.irexnet.waio.WAIO_ServerAgent.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Getter
@Setter
@ToString
public class VisualizeHealthStatus
{
    private int status;

    @PostConstruct
    public void init()
    {
        status = CommonValue.HEALTH_CHECK_NORMAL;
    }

    public void setFailStatus()
    {
        if(status == CommonValue.HEALTH_CHECK_NORMAL)
        {
            status = CommonValue.HEALTH_CHECK_FAIL;
        }
        else if(status == CommonValue.HEALTH_CHECK_FAIL)
        {
            status = CommonValue.HEALTH_CHECK_ALARM;
        }
    }
}
