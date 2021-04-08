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
import com.lyh.crm.workbench.service.impl.ActivityServiceImpl;
import com.lyh.crm.workbench.service.impl.CustomerServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到客户控制器");

        String path = request.getServletPath();

        if("/workbench/customer/getUserList.do".equals(path)){

            getUserList(request,response);

        }else if("/workbench/customer/save.do".equals(path)){

            save(request,response);

        }else if("/workbench/customer/pageList.do".equals(path)){

            pageList(request,response);

        }else if("/workbench/customer/deleteByIds.do".equals(path)){

            deleteByIds(request,response);

        }else if("/workbench/customer/getUserListAndCustomer.do".equals(path)){

            getUserListAndCustomer(request,response);

        }else if("/workbench/customer/update.do".equals(path)){

            update(request,response);

        }else if("/workbench/customer/detail.do".equals(path)){

            detail(request,response);

        }else if("/workbench/customer/getRemarkListByAid.do".equals(path)){

            getRemarkListByAid(request,response);

        }else if("/workbench/customer/deleteRemark.do".equals(path)){

            deleteRemark(request,response);

        }else if("/workbench/customer/saveRemark.do".equals(path)){

            saveRemark(request,response);

        }else if("/workbench/customer/updateRemark.do".equals(path)){

            updateRemark(request,response);
        }else if("/workbench/customer/getTranListByAid.do".equals(path)){

            getTranListByAid(request,response);
        }else if("/workbench/customer/getContactsListByCusId.do".equals(path)){

            getContactsListByCusId(request,response);
        }


    }

    private void getContactsListByCusId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据客户id，取得联系人信息列表");

        String customerId = request.getParameter("customerId");

        CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<Contacts> contactsList = cuss.getContactsListByCusId(customerId);

        for (Contacts con : contactsList) {
            System.out.println("con = " + con);
        }

        PrintJson.printJsonObj(response, contactsList);
    }

    private void getTranListByAid(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据客户id，取得交易信息列表");

        String customerId = request.getParameter("customerId");

        CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<Tran> tranList = cuss.getTranListByAid(customerId);

        for (Tran tran : tranList) {
            System.out.println("tran = " + tran);
        }

        PrintJson.printJsonObj(response, tranList);
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行修改备注的操作");

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "1";

        CustomerRemark cr = new CustomerRemark();

        cr.setId(id);
        cr.setNoteContent(noteContent);
        cr.setEditFlag(editFlag);
        cr.setEditBy(editBy);
        cr.setEditTime(editTime);

        CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        boolean flag = cuss.updateRemark(cr);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("success", flag);
        map.put("cr", cr);

        PrintJson.printJsonObj(response, map);

    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行添加备注操作");

        String noteContent = request.getParameter("noteContent");
        String customerId = request.getParameter("customerId");
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "0";

        CustomerRemark cr = new CustomerRemark();
        cr.setId(id);
        cr.setNoteContent(noteContent);
        cr.setCustomerId(customerId);
        cr.setCreateBy(createBy);
        cr.setCreateTime(createTime);
        cr.setEditFlag(editFlag);

        CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        boolean flag = cuss.saveRemark(cr);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("success", flag);
        map.put("cr", cr);

        PrintJson.printJsonObj(response, map);


    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("删除备注操作");

        String id = request.getParameter("id");

        CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        boolean flag = cuss.deleteRemark(id);

        PrintJson.printJsonFlag(response, flag);


    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据客户id，取得备注信息列表");

        String customerId = request.getParameter("customerId");

        CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<CustomerRemark> crList = cuss.getRemarkListByAid(customerId);

        PrintJson.printJsonObj(response, crList);



    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转到详细信息页的操作");

        String id = request.getParameter("id");

        CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        Customer cus = cuss.detail(id);

        request.setAttribute("cus", cus);

        request.getRequestDispatcher("/workbench/customer/detail.jsp").forward(request, response);

    }

    private void update(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行客户修改操作");

        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        //修改时间：当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        //修改人：当前登录用户
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        Customer cus = new Customer();

        cus.setId(id);
        cus.setOwner(owner);
        cus.setName(name);
        cus.setPhone(phone);
        cus.setWebsite(website);
        cus.setContactSummary(contactSummary);
        cus.setNextContactTime(nextContactTime);
        cus.setDescription(description);
        cus.setAddress(address);
        cus.setEditBy(editBy);
        cus.setEditTime(editTime);

        CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        boolean flag = cuss.update(cus);

        PrintJson.printJsonFlag(response, flag);

    }

    private void getUserListAndCustomer(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询用户信息列表和根据客户id查询单条记录的操作");

        String id = request.getParameter("id");

        CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        Map<String,Object> map = cuss.getUserListAndCustomer(id);

        PrintJson.printJsonObj(response, map);


    }

    private void deleteByIds(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行客户的删除操作");

        String ids[] = request.getParameterValues("id");

        CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        boolean flag = cuss.deleteByIds(ids);

        PrintJson.printJsonFlag(response, flag);


    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询客户信息列表的操作（结合条件查询+分页查询）");

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String pageNoStr = request.getParameter("pageNo");
        int pageNo = Integer.valueOf(pageNoStr);
        //每页展现的记录数
        String pageSizeStr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo-1)*pageSize;

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("phone",phone);
        map.put("website",website);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());


        PaginationVO<Customer> vo = cuss.pageList(map);

        //vo--> {"total":100,"dataList":[{市场活动1},{2},{3}]}
        PrintJson.printJsonObj(response, vo);




    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行客户添加操作");

        String id = UUIDUtil.getUUID();
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        //创建时间：当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人：当前登录用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        Customer cus = new Customer();
        cus.setId(id);
        cus.setOwner(owner);
        cus.setName(name);
        cus.setPhone(phone);
        cus.setWebsite(website);
        cus.setContactSummary(contactSummary);
        cus.setNextContactTime(nextContactTime);
        cus.setDescription(description);
        cus.setAddress(address);
        cus.setCreateTime(createTime);
        cus.setCreateBy(createBy);

        CustomerService cuss = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        boolean flag = cuss.save(cus);

        PrintJson.printJsonFlag(response, flag);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得用户信息列表");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> uList = us.getUserList();

        PrintJson.printJsonObj(response, uList);

    }
}
