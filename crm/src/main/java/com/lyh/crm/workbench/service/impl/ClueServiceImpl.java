package com.lyh.crm.workbench.service.impl;

import com.lyh.crm.settings.dao.UserDao;
import com.lyh.crm.settings.domain.User;
import com.lyh.crm.utils.DateTimeUtil;
import com.lyh.crm.utils.SqlSessionUtil;
import com.lyh.crm.utils.UUIDUtil;
import com.lyh.crm.vo.PaginationVO;
import com.lyh.crm.workbench.dao.*;
import com.lyh.crm.workbench.domain.*;
import com.lyh.crm.workbench.service.ClueService;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author 北京动力节点
 */
public class ClueServiceImpl implements ClueService {

    //线索相关表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    //客户相关表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    //联系人相关表
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    //交易相关表
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    public boolean save(Clue c) {

        boolean flag = true;

        int count = clueDao.save(c);

        if (count != 1) {

            flag = false;

        }

        return flag;
    }

    public PaginationVO<Clue> pageList(Map<String, Object> map) {
        int total = clueDao.getTotalByCondition(map);

        //取得dataList
        List<Clue> dataList = clueDao.getClueListByCondition(map);

        //创建一个vo对象，将total和dataList封装到vo中
        PaginationVO<Clue> vo = new PaginationVO<Clue>();
        vo.setTotal(total);
        vo.setDataList(dataList);

        //将vo返回
        return vo;
    }

    public Clue detail(String id) {
        Clue c = clueDao.detail(id);

        return c;
    }

    public boolean unbund(String id) {

        boolean flag = true;

        int count = clueActivityRelationDao.unbund(id);

        if (count != 1) {

            flag = false;

        }

        return flag;
    }

    public boolean bund(String cid, String[] aids) {

        boolean flag = true;

        for (String aid : aids) {

            //取得每一个aid和cid做关联
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setActivityId(aid);
            car.setClueId(cid);

            //添加关联关系表中的记录
            int count = clueActivityRelationDao.bund(car);
            if (count != 1) {
                flag = false;
            }

        }

        return flag;
    }

    public boolean convert(String clueId, Tran t, String createBy) {

        String createTime = DateTimeUtil.getSysTime();

        boolean flag = true;

        //(1)通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue c = clueDao.getById(clueId);

        //(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        String company = c.getCompany();
        Customer cus = customerDao.getCustomerByName(company);
        //如果cus为null，说明以前没有这个客户，需要新建一个
        if (cus == null) {

            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setAddress(c.getAddress());
            cus.setWebsite(c.getWebsite());
            cus.setPhone(c.getPhone());
            cus.setOwner(c.getOwner());
            cus.setNextContactTime(c.getNextContactTime());
            cus.setName(company);
            cus.setDescription(c.getDescription());
            cus.setCreateTime(createTime);
            cus.setCreateBy(createBy);
            cus.setContactSummary(c.getContactSummary());
            //添加客户
            int count1 = customerDao.save(cus);
            if (count1 != 1) {
                flag = false;
            }


        }
        //--------------------------------------------------------------------------
        //经过第二步处理后，客户的信息我们已经拥有了，将来在处理其他表的时候，如果要使用到客户的id
        //直接使用cus.getId();
        //--------------------------------------------------------------------------

        //(3)通过线索对象提取联系人信息，保存联系人
        Contacts con = new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setSource(c.getSource());
        con.setOwner(c.getOwner());
        con.setNextContactTime(c.getNextContactTime());
        con.setMphone(c.getMphone());
        con.setJob(c.getJob());
        con.setFullname(c.getFullname());
        con.setEmail(c.getEmail());
        con.setDescription(c.getDescription());
        con.setCustomerId(cus.getId());
        con.setCreateTime(createTime);
        con.setCreateBy(createBy);
        con.setContactSummary(c.getContactSummary());
        con.setAppellation(c.getAppellation());
        con.setAddress(c.getAddress());
        //添加联系人
        int count2 = contactsDao.save(con);
        if (count2 != 1) {
            flag = false;
        }

        //--------------------------------------------------------------------------
        //经过第三步处理后，联系人的信息我们已经拥有了，将来在处理其他表的时候，如果要使用到联系人的id
        //直接使用con.getId();
        //--------------------------------------------------------------------------

        //(4) 线索备注转换到客户备注以及联系人备注
        //查询出与该线索关联的备注信息列表
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListByClueId(clueId);
        //取出每一条线索的备注
        for (ClueRemark clueRemark : clueRemarkList) {

            //取出备注信息（主要转换到客户备注和联系人备注的就是这个备注信息）
            String noteContent = clueRemark.getNoteContent();

            //创建客户备注对象，添加客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(cus.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(noteContent);
            int count3 = customerRemarkDao.save(customerRemark);
            if (count3 != 1) {
                flag = false;
            }

            //创建联系人备注对象，添加联系人
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(con.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(noteContent);
            int count4 = contactsRemarkDao.save(contactsRemark);
            if (count4 != 1) {
                flag = false;
            }

        }

        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        //查询出与该条线索关联的市场活动，查询与市场活动的关联关系列表
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        //遍历出每一条与市场活动关联的关联关系记录
        for (ClueActivityRelation clueActivityRelation : clueActivityRelationList) {

            //从每一条遍历出来的记录中取出关联的市场活动id
            String activityId = clueActivityRelation.getActivityId();

            //创建 联系人与市场活动的关联关系对象 让第三步生成的联系人与市场活动做关联
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(con.getId());
            //添加联系人与市场活动的关联关系
            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if (count5 != 1) {
                flag = false;
            }


        }

        //(6)如果有创建交易需求，创建一条交易
        if (t != null) {

            /*

                t对象在controller里面已经封装好的信息如下：
                    id,money,name,expectedDate,stage,activityId,createBy,createTime

                接下来可以通过第一步生成的c对象，取出一些信息，继续完善对t对象的封装

             */

            t.setSource(c.getSource());
            t.setOwner(c.getOwner());
            t.setNextContactTime(c.getNextContactTime());
            t.setDescription(c.getDescription());
            t.setCustomerId(cus.getId());
            t.setContactSummary(c.getContactSummary());
            t.setContactsId(con.getId());
            //添加交易
            int count6 = tranDao.save(t);
            if (count6 != 1) {
                flag = false;
            }

            //(7)如果创建了交易，则创建一条该交易下的交易历史
            TranHistory th = new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setCreateBy(createBy);
            th.setCreateTime(createTime);
            th.setExpectedDate(t.getExpectedDate());
            th.setMoney(t.getMoney());
            th.setStage(t.getStage());
            th.setTranId(t.getId());
            //添加交易历史
            int count7 = tranHistoryDao.save(th);
            if (count7 != 1) {
                flag = false;
            }


        }

        //(8)删除线索备注
        for (ClueRemark clueRemark : clueRemarkList) {

            int count8 = clueRemarkDao.delete(clueRemark);
            if (count8 != 1) {
                flag = false;
            }

        }

        //(9) 删除线索和市场活动的关系
        for (ClueActivityRelation clueActivityRelation : clueActivityRelationList) {

            int count9 = clueActivityRelationDao.delete(clueActivityRelation);
            if (count9 != 1) {

                flag = false;

            }

        }

        //(10) 删除线索
        int count10 = clueDao.delete(clueId);
        if (count10 != 1) {
            flag = false;
        }


        return flag;
    }

    public boolean deleteByIds(String[] ids) {
        boolean flag = true;

        //查询出需要删除的备注的数量
        int count1 = clueRemarkDao.getCountByAids(ids);

        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2 = clueRemarkDao.deleteByAids(ids);

        if (count1 != count2) {

            flag = false;

        }

        //删除市场活动
        int count3 = clueDao.deleteByIds(ids);
        if (count3 != ids.length) {

            flag = false;

        }

        return flag;
    }

    public Map<String, Object> getUserListAndClue(String id) {
        //取uList
        List<User> uList = userDao.getUserList();

        //取a
        Clue c = clueDao.getById(id);

        //将uList和a打包到map中
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uList", uList);
        map.put("c", c);

        //返回map就可以了
        return map;
    }

    public boolean update(Clue a) {
        boolean flag = true;

        int count = clueDao.update(a);
        if (count != 1) {

            flag = false;

        }

        return flag;
    }

    public List<ClueRemark> getRemarkListByAid(String clueId) {

        List<ClueRemark> arList = clueRemarkDao.getRemarkListByAid(clueId);

        return arList;
    }

    public boolean deleteRemark(String id) {
        boolean flag = true;

        int count = clueRemarkDao.deleteById(id);

        if(count!=1){

            flag = false;

        }

        return flag;
    }

    public boolean saveRemark(ClueRemark ar) {
        boolean flag = true;

        int count = clueRemarkDao.saveRemark(ar);

        if(count!=1){

            flag = false;

        }

        return flag;
    }

    public boolean updateRemark(ClueRemark ar) {
        boolean flag = true;

        int count = clueRemarkDao.updateRemark(ar);

        if(count!=1){

            flag = false;

        }

        return flag;
    }


}






























