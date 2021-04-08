package com.lyh.crm.workbench.dao;

import com.lyh.crm.workbench.domain.Contacts;

import java.util.List;
import java.util.Map;

public interface ContactsDao {

    int save(Contacts con);

    List<Contacts> getContactsListByName(String aname2);

    int getTotalByCondition(Map<String, Object> map);

    List<Contacts> getContactsListByCondition(Map<String, Object> map);

    int deleteByIds(String[] ids);

    Contacts getById(String id);

    int update(Contacts con);

    int getCustomerAndContactsTotalId(String contactsName, String customerName);
    int getCustomerAndContactsTotalId(Map<String,String> map);

    String getContactsIdByName(String contactsName, String customerName);
    String getContactsIdByName(Map<String,String> map);

    List<Contacts> getContactsListByCusId(String customerId);

    int deleteById(String id);
}
