package co.irexnet.waio.WAIO_ServerAgent.util;

import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessCodeDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@ToString
public class ProcessCodeList
{
    private List<ProcessCodeDTO> processCodeList;

    @PostConstruct
    public void init()
    {
        processCodeList = new ArrayList<>();
    }

    public int getSize()
    {
        int nSize = processCodeList.size();
        return nSize;
    }

    public boolean isExist(String code)
    {
        boolean bResult = false;

        for(ProcessCodeDTO processCode : processCodeList)
        {
            if(processCode.getCode().equalsIgnoreCase(code) == true)
            {
                bResult = true;
                break;
            }
        }

        return bResult;
    }
}
