package com.lyh.crm.workbench.web.controller;

import com.lyh.crm.settings.domain.User;
import com.lyh.crm.settings.service.UserService;
import com.lyh.crm.settings.service.impl.UserServiceImpl;
import com.lyh.crm.utils.DateTimeUtil;
import com.lyh.crm.utils.PrintJson;
import com.lyh.crm.utils.ServiceFactory;
import com.lyh.crm.utils.UUIDUtil;
import com.lyh.crm.vo.PaginationVO;
import com.lyh.crm.workbench.domain.Activity;
import com.lyh.crm.workbench.domain.Contacts;
import com.lyh.crm.workbench.domain.Customer;
import com.lyh.crm.workbench.service.ActivityService;
import com.lyh.crm.workbench.service.ContactsService;
import com.lyh.crm.workbench.service.CustomerService;
import com.lyh.crm.workbench.service.TranService;
import com.lyh.crm.workbench.service.impl.ActivityServiceImpl;
import com.lyh.crm.workbench.service.impl.ContactsServiceImpl;
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

public class ContactsContronller extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到联系人控制器");

        String path = request.getServletPath();

        if ("/workbench/contacts/getContactsListByName.do".equals(path)) {

            getContactsListByName(request, response);

        } else if ("/workbench/contacts/getUserList.do".equals(path)) {

            getUserList(request, response);

        } else if ("/workbench/contacts/save.do".equals(path)) {

            save(request, response);

        } else if ("/workbench/contacts/pageList.do".equals(path)) {

            pageList(request, response);

        } else if ("/workbench/contacts/deleteByIds.do".equals(path)) {

            deleteByIds(request, response);

        } else if ("/workbench/contacts/getUserListAndContacts.do".equals(path)) {

            getUserListAndContacts(request, response);

        } else if ("/workbench/contacts/update.do".equals(path)) {

            update(request, response);

        } else if ("/workbench/contacts/deleteById.do".equals(path)) {

            deleteById(request, response);

        }


    }

    private void deleteById(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行交易的单个删除操作");

        String id = request.getParameter("id");

        ContactsService as = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());

        boolean flag = as.deleteById(id);

        PrintJson.printJsonFlag(response, flag);
    }

    private void getContactsListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查询联系人列表（根据名称模糊查）");

        String aname2 = request.getParameter("aname2");

        ContactsService as = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());

        List<Contacts> aList = as.getContactsListByName(aname2);

        PrintJson.printJsonObj(response, aList);
    }

    private void update(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行联系人修改操作");

        String id = request.getParameter("id");
        String fullname = request.getParameter("fullname");
        String owner = request.getParameter("owner");
        String customerName = request.getParameter("customerName");
        String birth = request.getParameter("birth");
        String source = request.getParameter("source");
        String appellation = request.getParameter("appellation");
        String email = request.getParameter("email");
        String mphone = request.getParameter("mphone");
        String job = request.getParameter("job");
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        //修改时间：当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        //修改人：当前登录用户
        String editBy = ((User) request.getSession().getAttribute("user")).getName();

        Contacts con = new Contacts();
        con.setId(id);
        con.setOwner(owner);
        con.setFullname(fullname);
        con.setCustomerId(customerName);
        con.setBirth(birth);
        con.setSource(source);
        con.setAppellation(appellation);
        con.setEmail(email);
        con.setMphone(mphone);
        con.setJob(job);
        con.setContactSummary(contactSummary);
        con.setNextContactTime(nextContactTime);
        con.setDescription(description);
        con.setAddress(address);
        con.setEditBy(editBy);
        con.setEditTime(editTime);

        ContactsService cons = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());
        System.out.println("con = " + con);
        boolean flag = cons.update(con);

        PrintJson.printJsonFlag(response, flag);

    }

    private void getUserListAndContacts(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询用户信息列表和根据联系人id查询单条记录的操作");

        String id = request.getParameter("id");

        ContactsService cons = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());

        Map<String, Object> map = cons.getUserListAndContacts(id);

        PrintJson.printJsonObj(response, map);


    }

    private void deleteByIds(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行联系人的删除操作");

        String ids[] = request.getParameterValues("id");

        ContactsService cons = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());

        boolean flag = cons.deleteByIds(ids);

        PrintJson.printJsonFlag(response, flag);


    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询联系人信息列表的操作（结合条件查询+分页查询）");

        String fullname = request.getParameter("fullname");
        String owner = request.getParameter("owner");
        String birth = request.getParameter("birth");
        String source = request.getParameter("source");
        String customerName = request.getParameter("customerName");

        String pageNoStr = request.getParameter("pageNo");
        int pageNo = Integer.valueOf(pageNoStr);
        //每页展现的记录数
        String pageSizeStr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo - 1) * pageSize;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("fullname", fullname);
        map.put("owner", owner);
        map.put("birth", birth);
        map.put("source", source);
        map.put("customerName", customerName);
        map.put("skipCount", skipCount);
        map.put("pageSize", pageSize);


        ContactsService cons = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());

        PaginationVO<Contacts> vo = cons.pageList(map);

        PrintJson.printJsonObj(response, vo);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行联系人添加操作");

        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String owner = request.getParameter("owner");
        String customerName = request.getParameter("customerName");
        String birth = request.getParameter("birth");
        String source = request.getParameter("source");
        String appellation = request.getParameter("appellation");
        String email = request.getParameter("email");
        String mphone = request.getParameter("mphone");
        String job = request.getParameter("job");
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        //创建时间：当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人：当前登录用户
        String createBy = ((User) request.getSession().getAttribute("user")).getName();

        Contacts con = new Contacts();
        con.setId(id);
        con.setOwner(owner);
        con.setFullname(fullname);
        con.setCustomerId(customerName);
        con.setBirth(birth);
        con.setSource(source);
        con.setAppellation(appellation);
        con.setEmail(email);
        con.setMphone(mphone);
        con.setJob(job);
        con.setContactSummary(contactSummary);
        con.setNextContactTime(nextContactTime);
        con.setDescription(description);
        con.setAddress(address);
        con.setCreateTime(createTime);
        con.setCreateBy(createBy);

        System.out.println("con1 = " + con);

        ContactsService cons = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());

        boolean flag = cons.save(con);

        PrintJson.printJsonFlag(response, flag);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得用户信息列表");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> uList = us.getUserList();

        PrintJson.printJsonObj(response, uList);

    }

}
