package co.irexnet.waio.WAIO_ServerAgent.dao;

import co.irexnet.waio.WAIO_ServerAgent.dto.DashboardDataDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.DashboardIdDTO;

public interface IDashboardInfoDAO
{
    int insert(String data);
    DashboardIdDTO selectLatest();
    DashboardDataDTO select(int dashboard_id);
    int update(int dashboard_id, String data);
    int delete(int dashboard_id);
}
