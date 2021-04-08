package com.lyh.crm.workbench.service.impl;

import com.lyh.crm.settings.dao.UserDao;
import com.lyh.crm.settings.domain.User;
import com.lyh.crm.utils.DateTimeUtil;
import com.lyh.crm.utils.SqlSessionUtil;
import com.lyh.crm.utils.UUIDUtil;
import com.lyh.crm.vo.PaginationVO;
import com.lyh.crm.workbench.dao.ContactsDao;
import com.lyh.crm.workbench.dao.CustomerDao;
import com.lyh.crm.workbench.domain.Activity;
import com.lyh.crm.workbench.domain.Contacts;
import com.lyh.crm.workbench.domain.Customer;
import com.lyh.crm.workbench.service.ContactsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsServiceImpl implements ContactsService {
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    public List<Contacts> getContactsListByName(String aname2) {
        List<Contacts> aList = contactsDao.getContactsListByName(aname2);

        return aList;
    }

    public boolean save(Contacts con) {

        boolean flag = true;

        String customerName = con.getCustomerId();
        Customer cus = customerDao.getCustomerByName(customerName);

        System.out.println("customer = " + cus.getId());
        //如果cus为null，需要创建客户
        if (cus == null) {

            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setCreateBy(con.getCreateBy());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setContactSummary(con.getContactSummary());
            cus.setNextContactTime(con.getNextContactTime());
            cus.setOwner(con.getOwner());
            //添加客户
            int count1 = customerDao.save(cus);
            if (count1 != 1) {
                flag = false;
            }

        }

        //通过以上对于客户的处理，不论是查询出来已有的客户，还是以前没有我们新增的客户，总之客户已经有了，客户的id就有了
        //将客户id封装到con对象中
        con.setCustomerId(cus.getId());

        int count = contactsDao.save(con);
        System.out.println("count = " + count);
        if (count != 1) {

            flag = false;

        }

        return flag;
    }

    public PaginationVO<Contacts> pageList(Map<String, Object> map) {
        //取得total
        int total = contactsDao.getTotalByCondition(map);
        //取得dataList
        List<Contacts> dataList = contactsDao.getContactsListByCondition(map);

        //创建一个vo对象，将total和dataList封装到vo中
        PaginationVO<Contacts> vo = new PaginationVO<Contacts>();
        vo.setTotal(total);
        vo.setDataList(dataList);

        //将vo返回
        return vo;
    }

    public boolean deleteByIds(String[] ids) {
        boolean flag = true;

        //删除市场活动
        int count = contactsDao.deleteByIds(ids);
        if (count != ids.length) {

            flag = false;

        }

        return flag;
    }

    public Map<String, Object> getUserListAndContacts(String id) {
        //取uList
        List<User> uList = userDao.getUserList();

        //取cus
        Contacts con = contactsDao.getById(id);
        System.out.println(con);
        //将uList和cus打包到map中
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uList", uList);
        map.put("con", con);

        //返回map就可以了
        return map;
    }

    public boolean update(Contacts con) {
        boolean flag = true;

        String customerName = con.getCustomerId();
        System.out.println("customerName = " + customerName);

        int count2 = customerDao.getCustomerTotalId(customerName);
        System.out.println("count2 = " + count2);

        if (count2 != 1) {

            flag = false;

        } else {

            String customerId = customerDao.getCustomerId(customerName);
            System.out.println("customerId = " + customerId);
            con.setCustomerId(customerId);

            int count = contactsDao.update(con);
            if (count != 1) {

                flag = false;

            }
        }
        return flag;
    }

    public boolean deleteById(String id) {

        boolean flag = true;

        //删除交易
        int count = contactsDao.deleteById(id);
        if (count != 1) {

            flag = false;

        }

        return flag;
    }

}
