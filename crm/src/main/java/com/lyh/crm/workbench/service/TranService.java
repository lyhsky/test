package com.lyh.crm.workbench.service;

import com.lyh.crm.vo.PaginationVO;
import com.lyh.crm.workbench.domain.Activity;
import com.lyh.crm.workbench.domain.Tran;
import com.lyh.crm.workbench.domain.TranHistory;
import com.lyh.crm.workbench.domain.TranRemark;

import java.util.List;
import java.util.Map;

/**
 * Author 北京动力节点
 */
public interface TranService {
    boolean save(Tran t, String customerName);

    Tran detail(String id);

    List<TranHistory> getHistoryListByTranId(String tranId);

    boolean changeStage(Tran t);

    Map<String, Object> getCharts();

    PaginationVO<Tran> pageList(Map<String, Object> map);

    boolean delete(String[] ids);

    List<TranRemark> getRemarkListByAid(String tranId);

    boolean deleteRemark(String id);

    boolean saveRemark(TranRemark ar);

    boolean updateRemark(TranRemark ar);

    Map<String, Object> getUserListAndTran(String id);

    boolean update(Tran t);

    boolean deleteById(String id);
}
