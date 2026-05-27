package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.TagDescriptionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagDescriptionDAOImpl implements ITagDescriptionDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(TagDescriptionDTO dto)
    {
        String strQuery = "insert into TB_TAG_DESC (TAG_SN, `DESC`, CRTD) values (?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getTag_sn(), dto.getDesc(), dto.getCrtd()
        );
    }

    @Override
    public List<TagDescriptionDTO> select()
    {
        String strQuery = "select * from TB_TAG_DESC";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(TagDescriptionDTO.class));
    }

    @Override
    public int update(TagDescriptionDTO dto)
    {
        String strQuery = "update TB_TAG_DESC set `desc`=?, crtd=? where seq=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getDesc(), dto.getCrtd(), dto.getSeq()
        );
    }

    @Override
    public int delete(int tagIndex)
    {
        String strQuery = "delete from TB_TAG_DESC where tag_index=?";
        return jdbcTemplate.update(strQuery, tagIndex);
    }
}
