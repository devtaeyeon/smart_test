package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.ClassInfoDTO;

import java.util.List;

public interface IClassInfoDAO
{
    int insert(ClassInfoDTO dto);
    List<ClassInfoDTO> select();
    int update(ClassInfoDTO dto);
    int delete(int class_index);
}
