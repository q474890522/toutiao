package com.nowcoder.controller;

import com.nowcoder.service.UserService;
import com.nowcoder.utils.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@Controller
public class loginController {

    private static final Logger logger = LoggerFactory.getLogger(loginController.class);

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/reg"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String register(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password,
                           @RequestParam(value = "rember", defaultValue = "0") int rememberme,
                           HttpServletResponse response) {
        try{
            Map<String, Object> map = userService.register(username, password);
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme > 0) {
                    cookie.setMaxAge(3600*24 * 5);
                }
                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0, "注册成功");
            } else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "注册异常");
        }

    }

    @RequestMapping(path = {"/login"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password,
                        @RequestParam(value = "rember", defaultValue = "0") int rememberme,
                        HttpServletResponse response) {
        try{
            Map<String, Object> map = userService.login(username, password);
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme > 0){
                    cookie.setMaxAge(3600*24 * 5);
                }
                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0, "登录成功");
            } else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        } catch (Exception e) {
            logger.error("登录异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "登录异常");
        }
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }
}
