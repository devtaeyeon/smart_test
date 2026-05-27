package co.irexnet.waio.WAIO_ServerAgent.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.TagManageRangeDTO;

@Repository
public class TagManageDAOImpl implements ITagManageDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(TagManageDTO dto)
    {
        String strQuery = "insert into TB_TAG_MNG values (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getAi_cd(), dto.getProc_cd(), dto.getSer(), dto.getLoc(),
                dto.getItm(), dto.getTag_sn(), dto.getDp(), dto.getTag_ty()
        );
    }

    @Override
    public List<TagManageDTO> select()
    {
        String strQuery = "select * from TB_TAG_MNG";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(TagManageDTO.class));
    }

    @Override
    public List<TagManageDTO> select(int tag_ty, String process, int processStep)
    {
        String strQuery = "select * from TB_TAG_MNG where tag_ty=? AND proc_cd=?";
        return jdbcTemplate.query(strQuery, new Object[]{tag_ty, getProcCdByProcessStep(process, processStep)}, new BeanPropertyRowMapper<>(TagManageDTO.class));
    }

    @Override
    public List<TagManageDTO> select(String process, int processStep)
    {
        String strQuery = "select * from TB_TAG_MNG where ai_cd=? AND proc_cd=?";
        return jdbcTemplate.query(strQuery, new Object[]{process, getProcCdByProcessStep(process, processStep)}, new BeanPropertyRowMapper<>(TagManageDTO.class));
    }

    @Override
    public TagManageRangeDTO selectRange(String process, int processStep)
    {
        String strQuery = "SELECT MIN(NULLIF(loc, 0)) AS min, MAX(loc) AS max FROM TB_TAG_MNG WHERE ai_cd=? AND proc_cd=? AND loc != 0";
        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery,
                    new Object[]{process, getProcCdByProcessStep(process, processStep)},
                    new BeanPropertyRowMapper<>(TagManageRangeDTO.class)
            );
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int update(TagManageDTO dto)
    {
        String strQuery = "update TB_TAG_MNG set dp=?, tag_ty=? " +
                "where ai_cd=? and proc_cd=? and ser=? and loc=? and itm=? and tag_sn=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getDp(), dto.getTag_ty(),
                dto.getAi_cd(), dto.getProc_cd(), dto.getSer(), dto.getLoc(),
                dto.getItm(), dto.getTag_sn()
        );
    }

    @Override
    public int delete(TagManageDTO dto)
    {
        String strQuery = "delete from TB_TAG_MNG " +
                "where ai_cd=? and proc_cd=? and ser=? and loc=? and itm=? and tag_sn=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getAi_cd(), dto.getProc_cd(), dto.getSer(), dto.getLoc(),
                dto.getItm(), dto.getTag_sn()
        );
    }
    
    @Override
    public String select(String tagSn){
    	String strQuery = "select DP from TB_TAG_MNG WHERE TAG_SN = ? limit 1";
		try {
			return jdbcTemplate.queryForObject(strQuery, String.class, tagSn);	
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
    }
    
    public String getProcCdByProcessStep(String process, int processStep) {
        String procCd = process;
        if(processStep == 0) {
        	// EMS data
        	return "H";
        }
        return procCd;
    }

    public List<String> selectProcCd()
    {
        String strQuery = "select distinct(PROC_CD) from TB_TAG_MNG";
        return jdbcTemplate.queryForList(strQuery, String.class);
    }
}
