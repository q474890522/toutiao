package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.utils.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    HostHolder hostHolder;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)) {
            map.put("msg", "用户名为空");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("msg", "密码为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if(user != null) {
            map.put("msg", "用户名已被注册");
            return map;
        }

        Random random = new Random();
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(ToutiaoUtil.MD5(password + user.getSalt()));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
        userDAO.addUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)) {
            map.put("msg", "用户名为空");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("msg", "密码为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if(user == null) {
            map.put("msg", "该用户名未注册");
            return map;
        }

        if(!user.getPassword().equals(ToutiaoUtil.MD5(password + user.getSalt()))) {
            map.put("msg", "密码错误");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    private String addLoginTicket(int userId) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24*5);
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        loginTicketDAO.addLoginTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
        hostHolder.clear();
    }

}
