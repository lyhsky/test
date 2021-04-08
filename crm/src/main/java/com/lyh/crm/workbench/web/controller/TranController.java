package com.lyh.crm.workbench.web.controller;

import com.lyh.crm.settings.domain.User;
import com.lyh.crm.settings.service.UserService;
import com.lyh.crm.settings.service.impl.UserServiceImpl;
import com.lyh.crm.utils.DateTimeUtil;
import com.lyh.crm.utils.PrintJson;
import com.lyh.crm.utils.ServiceFactory;
import com.lyh.crm.utils.UUIDUtil;
import com.lyh.crm.vo.PaginationVO;
import com.lyh.crm.workbench.domain.*;
import com.lyh.crm.workbench.service.ActivityService;
import com.lyh.crm.workbench.service.CustomerService;
import com.lyh.crm.workbench.service.TranService;
import com.lyh.crm.workbench.service.impl.ActivityServiceImpl;
import com.lyh.crm.workbench.service.impl.CustomerServiceImpl;
import com.lyh.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author 北京动力节点
 */
public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到交易控制器");

        String path = request.getServletPath();

        if ("/workbench/transaction/add.do".equals(path)) {

            add(request, response);

        } else if ("/workbench/transaction/getCustomerName.do".equals(path)) {

            getCustomerName(request, response);

        } else if ("/workbench/transaction/save.do".equals(path)) {

            save(request, response);

        } else if ("/workbench/transaction/pageList.do".equals(path)) {

            pageList(request, response);

        } else if ("/workbench/transaction/delete.do".equals(path)) {

            delete(request, response);

        } else if ("/workbench/transaction/getUserListAndTran.do".equals(path)) {

            getUserListAndTran(request, response);

        } else if ("/workbench/transaction/update.do".equals(path)) {

            update(request, response);

        } else if ("/workbench/transaction/detail.do".equals(path)) {

            detail(request, response);

        } else if ("/workbench/transaction/getRemarkListByAid.do".equals(path)) {

            getRemarkListByAid(request, response);

        } else if ("/workbench/transaction/deleteRemark.do".equals(path)) {

            deleteRemark(request, response);

        } else if ("/workbench/transaction/saveRemark.do".equals(path)) {

            saveRemark(request, response);

        } else if ("/workbench/transaction/updateRemark.do".equals(path)) {

            updateRemark(request, response);

        } else if ("/workbench/transaction/getHistoryListByTranId.do".equals(path)) {

            getHistoryListByTranId(request, response);

        } else if ("/workbench/transaction/changeStage.do".equals(path)) {

            changeStage(request, response);

        } else if ("/workbench/transaction/getCharts.do".equals(path)) {

            getCharts(request, response);

        } else if ("/workbench/transaction/deleteById.do".equals(path)) {

            deleteById(request, response);

        }

    }

    private void deleteById(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行交易的单个删除操作");

        String id = request.getParameter("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.deleteById(id);

        PrintJson.printJsonFlag(response, flag);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到交易信息列表的操作（结合条件查询+分页查询）");

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String customerName = request.getParameter("customerId");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String contactsName = request.getParameter("contactsId");
        String pageNoStr = request.getParameter("pageNo");


        int pageNo = Integer.valueOf(pageNoStr);
        //每页展现的记录数
        String pageSizeStr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo - 1) * pageSize;

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("name", name);
        map.put("owner", owner);
        map.put("customerId", customerName);
        map.put("stage", stage);
        map.put("type", type);
        map.put("source", source);
        map.put("contactsId", contactsName);

        map.put("skipCount", skipCount);
        map.put("pageSize", pageSize);

        TranService as = (TranService) ServiceFactory.getService(new TranServiceImpl());

        PaginationVO<Tran> vo = as.pageList(map);
        PrintJson.printJsonObj(response, vo);

    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得交易阶段数量统计图表的数据");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        /*

            业务层为我们返回
                total
                dataList

                通过map打包以上两项进行返回


         */
        Map<String, Object> map = ts.getCharts();

        PrintJson.printJsonObj(response, map);

    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行改变阶段的操作");

        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User) request.getSession().getAttribute("user")).getName();

        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.changeStage(t);

        Map<String, String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        t.setPossibility(pMap.get(stage));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", flag);
        map.put("t", t);

        PrintJson.printJsonObj(response, map);


    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据交易id取得相应的历史列表");

        String tranId = request.getParameter("tranId");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<TranHistory> thList = ts.getHistoryListByTranId(tranId);

        //阶段和可能性之间的对应关系
        Map<String, String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");

        //将交易历史列表遍历
        for (TranHistory th : thList) {

            //根据每条交易历史，取出每一个阶段
            String stage = th.getStage();
            String possibility = pMap.get(stage);
            th.setPossibility(possibility);

        }


        PrintJson.printJsonObj(response, thList);


    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行修改备注的操作");

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User) request.getSession().getAttribute("user")).getName();
        String editFlag = "1";

        TranRemark ar = new TranRemark();

        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditFlag(editFlag);
        ar.setEditBy(editBy);
        ar.setEditTime(editTime);

        TranService as = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = as.updateRemark(ar);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", flag);
        map.put("ar", ar);

        PrintJson.printJsonObj(response, map);

    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行添加备注操作");

        String noteContent = request.getParameter("noteContent");
        String tranId = request.getParameter("tranId");
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String editFlag = "0";

        TranRemark ar = new TranRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setTranId(tranId);
        ar.setCreateBy(createBy);
        ar.setCreateTime(createTime);
        ar.setEditFlag(editFlag);

        TranService as = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = as.saveRemark(ar);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", flag);
        map.put("ar", ar);

        PrintJson.printJsonObj(response, map);


    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("删除备注操作");

        String id = request.getParameter("id");

        TranService as = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = as.deleteRemark(id);

        PrintJson.printJsonFlag(response, flag);


    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据交易id，取得备注信息列表");

        String tranId = request.getParameter("tranId");

        TranService as = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<TranRemark> arList = as.getRemarkListByAid(tranId);

        PrintJson.printJsonObj(response, arList);


    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("跳转到详细信息页");

        String id = request.getParameter("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Tran t = ts.detail(id);

        //处理可能性
        /*

            阶段 t
            阶段和可能性之间的对应关系 pMap

         */

        String stage = t.getStage();
        Map<String, String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);


        t.setPossibility(possibility);

        request.setAttribute("t", t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request, response);

    }

    private void update(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行交易修改操作");

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String customerId = request.getParameter("customerId");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");

        /*
        String activityName = request.getParameter("activityName");
        String contactsName = request.getParameter("contactsName");
        */
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        //修改时间：当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        //修改人：当前登录用户
        String editBy = ((User) request.getSession().getAttribute("user")).getName();

        Tran t = new Tran();

        System.out.println("activityId = " + activityId);
        System.out.println("contactsId = " + contactsId);

        t.setActivityId(activityId);
        t.setContactsId(contactsId);

        t.setId(id);
        t.setOwner(owner);
        t.setName(name);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setCustomerId(customerId);
        t.setStage(stage);
        t.setSource(source);
        t.setType(type);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);
        t.setDescription(description);
        t.setEditBy(editBy);
        t.setEditTime(editTime);
        System.out.println("获得的数据tran=" + t);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.update(t);


        System.out.println("=======================完成Service==========================");
        PrintJson.printJsonFlag(response, flag);

    }

    private void getUserListAndTran(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询用户信息列表和根据交易id查询单条记录的操作");

        String id = request.getParameter("id");

        TranService as = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Map<String, Object> map = as.getUserListAndTran(id);

        PrintJson.printJsonObj(response, map);


    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行交易的删除操作");

        String ids[] = request.getParameterValues("id");

        TranService as = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = as.delete(ids);

        PrintJson.printJsonFlag(response, flag);


    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("执行添加交易的操作");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");

        String tag = request.getParameter("tag");
        System.out.println("tag = " + tag);
        String customerName = request.getParameter("customerName"); //此处我们暂时只有客户名称，还没有id
        System.out.println("customerName = " + customerName);

        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");


        Tran t = new Tran();
        t.setId(id);
        t.setOwner(owner);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        t.setActivityId(activityId);
        t.setContactsId(contactsId);
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.save(t, customerName);

        if (flag) {
            if("0".equals(tag)) {
                //如果添加交易成功，跳转到列表页
                response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");
            }else if ("1".equals(tag)){
                String customerId = t.getCustomerId();

                CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

                Customer cus = cuss.detail(customerId);
                System.out.println("cus = " + cus);

                request.setAttribute("cus", cus);

                request.getRequestDispatcher("/workbench/customer/detail.jsp").forward(request, response);
            }else {
                System.out.println("程序出错!!!");
            }
        }


    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得 客户名称列表（按照客户名称进行模糊查询）");

        String name = request.getParameter("name");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<String> sList = cs.getCustomerName(name);

        PrintJson.printJsonObj(response, sList);

    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到跳转到交易添加页的操作");

        String id = request.getParameter("id");

        System.out.println("id = " + id);

        if (id != null && !"".equals(id)) {
            CustomerService cuss = new CustomerServiceImpl();

            Map<String,Object> map = cuss.getUserListAndCustomer(id);

            Customer cus = (Customer)map.get("cus");
            System.out.println("cus = " + cus);

            request.setAttribute("cus",cus);
        }

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> uList = us.getUserList();

        request.setAttribute("uList", uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request, response);

    }


}




































