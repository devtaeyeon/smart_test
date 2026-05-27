package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.ProcessCodeDTO;

import java.util.List;

public interface IProcessCodeDAO
{
    List<ProcessCodeDTO> select();
}
