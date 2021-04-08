package com.lyh.crm.workbench.dao;

import com.lyh.crm.workbench.domain.CustomerRemark;
import com.lyh.crm.workbench.domain.Tran;

import java.util.List;

public interface CustomerRemarkDao {

    int save(CustomerRemark customerRemark);

    List<CustomerRemark> getRemarkListByAid(String customerId);

    int deleteById(String id);

    int saveRemark(CustomerRemark cr);

    int updateRemark(CustomerRemark cr);

    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    int getContactsCountByAids(String[] ids);

    int updateContactsByAids(String[] ids);

}
