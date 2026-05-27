package co.irexnet.waio.WAIO_ServerAgent.util;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CheckPartition {

    @Autowired
    DatabaseServiceImpl databaseService;
    
    @PostConstruct
    public void init()
    {
        List<String> procCdList = databaseService.selectProcCd();
        for(String procCd : procCdList)
        {
        	if(!"H".equals(procCd)) {
        		try {
        			List<String> addPartitionList = databaseService.getAddPartitionList(procCd);
        			if (addPartitionList.size() > 0) {
        				databaseService.addProcessRealtimePartition(procCd, addPartitionList);
        				log.info("[Collector] Success Add Table[TB_{}_RT]... Partition Name:[{}]", procCd, addPartitionList.toString());
        			}
        		} catch(DataAccessException e) {
        			log.error(e.toString());
        		}
        	}
        }
    }
}
