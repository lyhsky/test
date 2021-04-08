package com.lyh.crm.workbench.service;

import com.lyh.crm.vo.PaginationVO;
import com.lyh.crm.workbench.domain.*;

import java.util.List;
import java.util.Map;

/**
 * Author 北京动力节点
 */
public interface ClueService {
    boolean save(Clue c);

    PaginationVO<Clue> pageList(Map<String, Object> map);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String cid, String[] aids);


    boolean convert(String clueId, Tran t, String createBy);

    boolean deleteByIds(String[] ids);

    Map<String, Object> getUserListAndClue(String id);

    boolean update(Clue a);

    List<ClueRemark> getRemarkListByAid(String clueId);

    boolean deleteRemark(String id);

    boolean saveRemark(ClueRemark ar);

    boolean updateRemark(ClueRemark ar);
}
