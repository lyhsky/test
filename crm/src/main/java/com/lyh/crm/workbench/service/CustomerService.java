package com.lyh.crm.workbench.service;

import com.lyh.crm.vo.PaginationVO;
import com.lyh.crm.workbench.domain.*;

import java.util.List;
import java.util.Map;

/**
 * Author 北京动力节点
 */
public interface CustomerService {
    List<String> getCustomerName(String name);

    boolean save(Customer cus);

    PaginationVO<Customer> pageList(Map<String, Object> map);

    boolean deleteByIds(String[] ids);

    Map<String, Object> getUserListAndCustomer(String id);

    boolean update(Customer cus);

    Customer detail(String id);

    List<CustomerRemark> getRemarkListByAid(String customerId);

    boolean deleteRemark(String id);

    boolean saveRemark(CustomerRemark cr);

    boolean updateRemark(CustomerRemark cr);

    List<Tran> getTranListByAid(String customerId);

    List<Contacts> getContactsListByCusId(String customerId);
}
