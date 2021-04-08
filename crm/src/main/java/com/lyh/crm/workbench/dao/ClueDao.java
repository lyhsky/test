package com.lyh.crm.workbench.dao;

import com.lyh.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueDao {


    int save(Clue c);

    Clue detail(String id);

    Clue getById(String clueId);

    int delete(String clueId);

    int deleteByIds(String clueIds[]);

    int getTotalByCondition(Map<String, Object> map);

    int update(Clue a);

    List<Clue> getClueListByCondition(Map<String, Object> map);
}
