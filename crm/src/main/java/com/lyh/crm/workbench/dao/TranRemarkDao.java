package com.lyh.crm.workbench.dao;

import com.lyh.crm.workbench.domain.ActivityRemark;
import com.lyh.crm.workbench.domain.TranRemark;

import java.util.List;

/**
 * Author 北京动力节点
 */
public interface TranRemarkDao {
    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    List<TranRemark> getRemarkListByAid(String tranId);

    int deleteById(String id);

    int saveRemark(TranRemark ar);

    int updateRemark(TranRemark ar);
}
