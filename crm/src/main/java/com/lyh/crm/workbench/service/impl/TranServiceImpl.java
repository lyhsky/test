package com.lyh.crm.workbench.service.impl;

import com.lyh.crm.settings.dao.UserDao;
import com.lyh.crm.settings.domain.User;
import com.lyh.crm.utils.DateTimeUtil;
import com.lyh.crm.utils.SqlSessionUtil;
import com.lyh.crm.utils.UUIDUtil;
import com.lyh.crm.vo.PaginationVO;
import com.lyh.crm.workbench.dao.*;
import com.lyh.crm.workbench.domain.*;
import com.lyh.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author 北京动力节点
 */
public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    private TranRemarkDao tranRemarkDao = SqlSessionUtil.getSqlSession().getMapper(TranRemarkDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);

    public boolean save(Tran t, String customerName) {

        /*

            交易添加业务：

                在做添加之前，参数t里面就少了一项信息，就是客户的主键，customerId

                先处理客户相关的需求

                （1）判断customerName，根据客户名称在客户表进行精确查询
                       如果有这个客户，则取出这个客户的id，封装到t对象中
                       如果没有这个客户，则再客户表新建一条客户信息，然后将新建的客户的id取出，封装到t对象中

                （2）经过以上操作后，t对象中的信息就全了，需要执行添加交易的操作

                （3）添加交易完毕后，需要创建一条交易历史



         */

        boolean flag = true;

        Customer cus = customerDao.getCustomerByName(customerName);

        //如果cus为null，需要创建客户
        if (cus == null) {

            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setCreateBy(t.getCreateBy());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setContactSummary(t.getContactSummary());
            cus.setNextContactTime(t.getNextContactTime());
            cus.setOwner(t.getOwner());
            //添加客户
            int count1 = customerDao.save(cus);
            if (count1 != 1) {
                flag = false;
            }

        }

        //通过以上对于客户的处理，不论是查询出来已有的客户，还是以前没有我们新增的客户，总之客户已经有了，客户的id就有了
        //将客户id封装到t对象中
        t.setCustomerId(cus.getId());

        //添加交易
        int count2 = tranDao.save(t);
        if (count2 != 1) {
            flag = false;
        }

        //添加交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setCreateBy(t.getCreateBy());
        int count3 = tranHistoryDao.save(th);
        if (count3 != 1) {
            flag = false;
        }

        return flag;
    }

    public Tran detail(String id) {

        Tran t = tranDao.detail(id);

        return t;
    }

    public List<TranHistory> getHistoryListByTranId(String tranId) {

        List<TranHistory> thList = tranHistoryDao.getHistoryListByTranId(tranId);

        return thList;
    }

    public boolean changeStage(Tran t) {

        boolean flag = true;

        //改变交易阶段
        int count1 = tranDao.changeStage(t);
        if (count1 != 1) {

            flag = false;

        }

        //交易阶段改变后，生成一条交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setStage(t.getStage());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setTranId(t.getId());
        //添加交易历史
        int count2 = tranHistoryDao.save(th);
        if (count2 != 1) {

            flag = false;

        }

        return flag;
    }

    public Map<String, Object> getCharts() {

        //取得total
        int total = tranDao.getTotal();

        //取得dataList
        List<Map<String, Object>> dataList = tranDao.getCharts();

        //将total和dataList保存到map中
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("total", total);
        map.put("dataList", dataList);

        //返回map
        return map;
    }

    public PaginationVO<Tran> pageList(Map<String, Object> map) {
        //取得total
        int total = tranDao.getTotalByCondition(map);
        System.out.println("total = " + total);

        /*String owner = (String) map.get("owner");
        System.out.println("owner = " + owner);
        String customerId = (String) map.get("customerId");
        System.out.println("customerId = " + customerId);
        String contactsId = (String) map.get("contactsId");
        System.out.println("contactsId = " + contactsId);*/


        //取得dataList
        List<Tran> dataList = tranDao.getTranListByCondition(map);

        for (Tran tran : dataList) {
            System.out.println("tran = " + tran);
        }

        //创建一个vo对象，将total和dataList封装到vo中
        PaginationVO<Tran> vo = new PaginationVO<Tran>();
        vo.setTotal(total);
        vo.setDataList(dataList);

        //将vo返回
        return vo;
    }

    public boolean delete(String[] ids) {
        boolean flag = true;

        //查询出需要删除的备注的数量
        int count1 = tranRemarkDao.getCountByAids(ids);

        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2 = tranRemarkDao.deleteByAids(ids);

        if (count1 != count2) {

            flag = false;

        }

        //删除交易
        int count3 = tranDao.delete(ids);
        if (count3 != ids.length) {

            flag = false;

        }

        return flag;
    }

    public List<TranRemark> getRemarkListByAid(String tranId) {

        List<TranRemark> arList = tranRemarkDao.getRemarkListByAid(tranId);

        return arList;
    }

    public boolean deleteRemark(String id) {

        boolean flag = true;

        int count = tranRemarkDao.deleteById(id);

        if (count != 1) {

            flag = false;

        }

        return flag;
    }

    public boolean saveRemark(TranRemark ar) {

        boolean flag = true;

        int count = tranRemarkDao.saveRemark(ar);

        if (count != 1) {

            flag = false;

        }

        return flag;
    }

    public boolean updateRemark(TranRemark ar) {

        boolean flag = true;

        int count = tranRemarkDao.updateRemark(ar);

        if (count != 1) {

            flag = false;

        }

        return flag;
    }

    public Map<String, Object> getUserListAndTran(String id) {
        //取uList
        List<User> uList = userDao.getUserList();

        //取a
        Tran t = tranDao.getById(id);
        System.out.println(t);

        //将uList和a打包到map中
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uList", uList);
        map.put("t", t);
        Tran t2 = (Tran) map.get("t");
        System.out.println("owner = " + t2.getOwner());

        //返回map就可以了
        return map;
    }

    public boolean update(Tran t) {

        boolean flag = true;

        /*String owner = t.getOwner();
        System.out.println("owner = " + owner);*/
        String activityName = t.getActivityId();
        System.out.println("activityName = " + activityName);
        String contactsName = t.getContactsId();
        System.out.println("contactsName = " + contactsName);
        String customerName = t.getCustomerId();
        System.out.println("customerName = " + customerName);

        int count2 = activityDao.getActivityTotalId(activityName);
        Map<String,String> map = new HashMap<String, String>();
        map.put("contactsName",contactsName);
        map.put("customerName",customerName);
        //联系人可能重名，将客户名称一起查
        int count3 = contactsDao.getCustomerAndContactsTotalId(map);

        System.out.println("t = " + t);

        System.out.println("=======================完成Dao==========================");
        if (count2 != 1 || count3 != 1 ) {

            flag = false;

        } else {

            /*String ownerId = userDao.getUserId(owner);
            System.out.println("ownerId = " + ownerId);*/
            String activityId = activityDao.getActivityIdByName(activityName);
            System.out.println("activityId = " + activityId);
            String contactsId = contactsDao.getContactsIdByName(map);
            System.out.println("contactsId = " + contactsId);
            String customerId = customerDao.getCustomerId(customerName);
            System.out.println("customerId = " + customerId);

            /*t.setOwner(ownerId);*/
            t.setActivityId(activityId);
            t.setContactsId(contactsId);
            t.setCustomerId(customerId);

            int count = tranDao.update(t);

            if (count != 1) {

                flag = false;

            }
            System.out.println("flag=" + flag);

        }
        return flag;
    }

    public boolean deleteById(String id) {

        boolean flag = true;

        //删除交易
        int count = tranDao.deleteById(id);
        if (count != 1) {

            flag = false;

        }

        return flag;
    }
}


































