package com.lyh.crm.workbench.dao;

import com.lyh.crm.workbench.domain.Customer;
import com.lyh.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface CustomerDao {

    Customer getCustomerByName(String company);

    int save(Customer cus);

    List<String> getCustomerName(String name);

    int getTotalByCondition(Map<String, Object> map);

    List<Customer> getCustomerListByCondition(Map<String, Object> map);

    int deleteByIds(String[] ids);

    Customer getById(String id);

    int update(Customer cus);

    int getCustomerTotalId(String customerName);

    String getCustomerId(String customerName);

    Customer detail(String id);


}
