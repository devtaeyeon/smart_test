package co.irexnet.waio.WAIO_ServerAgent.vo;

import co.irexnet.waio.WAIO_ServerAgent.vo.NameNodeJMX;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class HadoopJmxBeans
{
    List<NameNodeJMX> beans;
}
