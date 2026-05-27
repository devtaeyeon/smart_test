package co.irexnet.waio.WAIO_ServerAgent.util;

import co.irexnet.waio.WAIO_ServerAgent.dto.AlarmInfoDTO;
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
public class AlarmInfoList
{
    private List<AlarmInfoDTO> alarmInfoList;

    @PostConstruct
    public void init()
    {
        alarmInfoList = new ArrayList<>();
    }

    public int getSize()
    {
        int nSize = alarmInfoList.size();
        return nSize;
    }

    public AlarmInfoDTO getAlarmInfoFromAlarmCode(String codeName, int processStep)
    {
        for(AlarmInfoDTO dto : alarmInfoList)
        {
            if(dto.getCd_nm().equals(codeName) == true && processStep == 0 || dto.getCd_nm().equals(codeName) == true && dto.getProcessStep() == processStep)
            {
                return dto;
            }
        }

        return null;
    }

    public AlarmInfoDTO getAlarmInfoFromAlarmInfoIndex(int alarmInfoIndex)
    {
        for(AlarmInfoDTO dto : alarmInfoList)
        {
            if(dto.getSeq() == alarmInfoIndex)
            {
                return dto;
            }
        }

        return null;
    }

    public AlarmInfoDTO getAlarmInfoFromAlarmId(int alarmId)
    {
        for(AlarmInfoDTO dto : alarmInfoList)
        {
            if(dto.getAlm_id() == alarmId)
            {
                return dto;
            }
        }

        return null;
    }

    public void addAlarmInfo(AlarmInfoDTO dto)
    {
        alarmInfoList.add(dto);
    }

    public void removeAlarmInfo(AlarmInfoDTO dto)
    {
        alarmInfoList.remove(dto);
    }

    public List<AlarmInfoDTO> getAlarmCategory(int alarm_category)
    {
        List<AlarmInfoDTO> alarmCategory = new ArrayList<>();

        for(AlarmInfoDTO dto : alarmInfoList)
        {
            if(dto.getAlm_id() / 100000 == alarm_category)
            {
                alarmCategory.add(dto);
            }
        }

        return alarmCategory;
    }

    public boolean isAlarm(String codeName, float value) 
    {
        AlarmInfoDTO alarmInfo = getAlarmInfoFromAlarmCode(codeName, 0);
        if(alarmInfo == null)
        {
            return false;
        }

        float fCompare = Float.parseFloat(alarmInfo.getCmp_val());

        switch(alarmInfo.getCmp())
        {
            case 1:     // <
                return value < fCompare ? true : false;

            case 2:     // <=
                return value <= fCompare ? true : false;

            case 3:     // >
                return value > fCompare ? true : false;

            case 4:     // >=
                return value >= fCompare ? true : false;

            default:
                return false;
        }
    }
}
