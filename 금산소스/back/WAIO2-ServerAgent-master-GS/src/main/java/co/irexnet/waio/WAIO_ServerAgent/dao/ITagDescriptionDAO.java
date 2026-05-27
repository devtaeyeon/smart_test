package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.TagDescriptionDTO;

import java.util.List;

public interface ITagDescriptionDAO
{
    int insert(TagDescriptionDTO dto);
    List<TagDescriptionDTO> select();
    int update(TagDescriptionDTO dto);
    int delete(int tagIndex);
}
