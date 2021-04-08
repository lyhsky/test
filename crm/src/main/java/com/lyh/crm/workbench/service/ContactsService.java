package com.lyh.crm.workbench.service;

import com.lyh.crm.vo.PaginationVO;
import com.lyh.crm.workbench.domain.Contacts;

import java.util.List;
import java.util.Map;

public interface ContactsService {
    List<Contacts> getContactsListByName(String aname2);

    boolean save(Contacts con);

    PaginationVO<Contacts> pageList(Map<String, Object> map);

    boolean deleteByIds(String[] ids);

    Map<String, Object> getUserListAndContacts(String id);

    boolean update(Contacts con);

    boolean deleteById(String id);
}
