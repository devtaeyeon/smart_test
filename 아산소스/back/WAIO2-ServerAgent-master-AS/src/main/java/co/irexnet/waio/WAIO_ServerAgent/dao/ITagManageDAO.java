package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageRangeDTO;

import java.util.List;

public interface ITagManageDAO
{
    int insert(TagManageDTO dto);
    List<TagManageDTO> select();
    List<TagManageDTO> select(int type);
    List<TagManageDTO> select(String process, int processStep);
    TagManageRangeDTO selectRange(String process, int processStep);
    int update(TagManageDTO dto);
    int delete(TagManageDTO dto);
     String select(String tagSn);
}
