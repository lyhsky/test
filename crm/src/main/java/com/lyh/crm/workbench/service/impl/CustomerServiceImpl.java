package com.lyh.crm.workbench.service.impl;

import com.lyh.crm.settings.dao.UserDao;
import com.lyh.crm.settings.domain.User;
import com.lyh.crm.utils.SqlSessionUtil;
import com.lyh.crm.vo.PaginationVO;
import com.lyh.crm.workbench.dao.ContactsDao;
import com.lyh.crm.workbench.dao.CustomerDao;
import com.lyh.crm.workbench.dao.CustomerRemarkDao;
import com.lyh.crm.workbench.dao.TranDao;
import com.lyh.crm.workbench.domain.*;
import com.lyh.crm.workbench.service.CustomerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author 北京动力节点
 */
public class CustomerServiceImpl implements CustomerService {

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);

    public List<String> getCustomerName(String name) {

        List<String> sList = customerDao.getCustomerName(name);

        return sList;
    }

    public boolean save(Customer cus) {

        boolean flag = true;

        int count = customerDao.save(cus);
        System.out.println("count = " + count);
        if(count!=1){

            flag = false;

        }

        return flag;
    }

    public PaginationVO<Customer> pageList(Map<String, Object> map) {
        //取得total
        int total = customerDao.getTotalByCondition(map);
        //取得dataList
        List<Customer> dataList = customerDao.getCustomerListByCondition(map);

        //创建一个vo对象，将total和dataList封装到vo中
        PaginationVO<Customer> vo = new PaginationVO<Customer>();
        vo.setTotal(total);
        vo.setDataList(dataList);

        //将vo返回
        return vo;
    }

    public boolean deleteByIds(String[] ids) {

        boolean flag = true;

        //查询出需要删除的备注的数量
        int count1 = customerRemarkDao.getCountByAids(ids);

        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2 = customerRemarkDao.deleteByAids(ids);

        if(count1!=count2){

            flag = false;

        }

        //查询出需要修改的联系人customerId的数量
        int count3 = customerRemarkDao.getContactsCountByAids(ids);

        //修改联系人customerId，返回受到影响的条数（实际删除的数量）
        int count4 = customerRemarkDao.updateContactsByAids(ids);

        if(count3!=count4){

            flag = false;

        }

        //删除客户
        int count = customerDao.deleteByIds(ids);
        if(count!=ids.length){

            flag = false;

        }

        return flag;
    }

    public Map<String, Object> getUserListAndCustomer(String id) {

        //取uList
        List<User> uList = userDao.getUserList();

        //取cus
        Customer cus = customerDao.getById(id);
        System.out.println(cus);
        //将uList和cus打包到map中
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("uList", uList);
        map.put("cus", cus);

        //返回map就可以了
        return map;
    }

    public boolean update(Customer cus) {
        boolean flag = true;

        int count = customerDao.update(cus);
        if(count!=1){

            flag = false;

        }

        return flag;
    }

    public Customer detail(String id) {
        Customer cus = customerDao.detail(id);

        return cus;
    }

    public List<CustomerRemark> getRemarkListByAid(String customerId) {

        List<CustomerRemark> crList = customerRemarkDao.getRemarkListByAid(customerId);

        return crList;
    }

    public boolean deleteRemark(String id) {

        boolean flag = true;

        int count = customerRemarkDao.deleteById(id);

        if(count!=1){

            flag = false;

        }

        return flag;
    }

    public boolean saveRemark(CustomerRemark cr) {

        boolean flag = true;

        int count = customerRemarkDao.saveRemark(cr);

        if(count!=1){

            flag = false;

        }

        return flag;
    }

    public boolean updateRemark(CustomerRemark cr) {

        boolean flag = true;

        int count = customerRemarkDao.updateRemark(cr);

        if(count!=1){

            flag = false;

        }

        return flag;
    }

    public List<Tran> getTranListByAid(String customerId) {
        List<Tran> tranList = tranDao.getTranListByAid(customerId);

        return tranList;
    }

    public List<Contacts> getContactsListByCusId(String customerId) {
        List<Contacts> contactsList = contactsDao.getContactsListByCusId(customerId);

        return contactsList;
    }


}
















