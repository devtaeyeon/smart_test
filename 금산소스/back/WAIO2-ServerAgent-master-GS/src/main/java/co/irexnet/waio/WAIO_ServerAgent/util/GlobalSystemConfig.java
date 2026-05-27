package co.irexnet.waio.WAIO_ServerAgent.util;

import co.irexnet.waio.WAIO_ServerAgent.dto.SystemConfigDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Getter
@Setter
@ToString
public class GlobalSystemConfig
{
    private SystemConfigDTO systemConfig;

    @PostConstruct
    public void init()
    {
        systemConfig = new SystemConfigDTO();
    }

    public String getScada1_information()
    {
        return systemConfig.getScd_addr1() + ":" + systemConfig.getScd_port1();
    }

    public String getScada1_daq()
    {
        return systemConfig.getScd_addr1() + ":" + systemConfig.getDaq_port1();
    }

    public String getScada2_information()
    {
        return systemConfig.getScd_addr2() + ":" + systemConfig.getScd_port2();
    }

    public String getScada2_daq()
    {
        return systemConfig.getScd_addr2() + ":" + systemConfig.getDaq_port2();
    }

    public String getAnalysis1_ResourceManager()
    {
        return systemConfig.getAnl_addr1() + ":" + systemConfig.getAnl_rm_port1();
    }

    public String getAnalysis1_NodeManager()
    {
        return systemConfig.getAnl_addr1() + ":" + systemConfig.getAnl_nm_port1();
    }

    public String getAnalysis1_NameNode()
    {
        return systemConfig.getAnl_addr1() + ":" + systemConfig.getAnl_nn_port1();
    }

    public String getAnalysis2_ResourceManager()
    {
        return systemConfig.getAnl_addr2() + ":" + systemConfig.getAnl_rm_port2();
    }

    public String getAnalysis2_NodeManager()
    {
        return systemConfig.getAnl_addr2() + ":" + systemConfig.getAnl_rm_port2();
    }

    public String getAnalysis2_NameNode()
    {
        return systemConfig.getAnl_addr2() + ":" + systemConfig.getAnl_nn_port2();
    }
}
