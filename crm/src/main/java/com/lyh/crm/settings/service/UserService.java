package com.lyh.crm.settings.service;

import com.lyh.crm.exception.LoginException;
import com.lyh.crm.settings.domain.User;

import java.util.List;

/**
 * Author 北京动力节点
 */
public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();
}
