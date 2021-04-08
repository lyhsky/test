package com.lyh.crm.workbench.dao;

import com.lyh.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkDao {

    List<ClueRemark> getListByClueId(String clueId);

    int delete(ClueRemark clueRemark);

    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    List<ClueRemark> getRemarkListByAid(String clueId);

    int deleteById(String id);

    int saveRemark(ClueRemark ar);

    int updateRemark(ClueRemark ar);
}
