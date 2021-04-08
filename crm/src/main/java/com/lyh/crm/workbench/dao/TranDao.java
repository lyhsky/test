package com.lyh.crm.workbench.dao;

import com.lyh.crm.workbench.domain.Activity;
import com.lyh.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int save(Tran t);

    Tran detail(String id);

    int changeStage(Tran t);

    int getTotal();

    List<Map<String, Object>> getCharts();

    int getTotalByCondition(Map<String, Object> map);

    List<Tran> getTranListByCondition(Map<String, Object> map);

    int delete(String[] ids);

    int update(Tran t);

    Tran getById(String id);

    int deleteById(String id);

    List<Tran> getTranListByAid(String customerId);

}
