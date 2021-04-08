package com.lyh.crm.settings.dao;

import com.lyh.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

public interface UserDao {


    User login(Map<String, String> map);

    List<User> getUserList();

    String getUserId(String owner);
}
