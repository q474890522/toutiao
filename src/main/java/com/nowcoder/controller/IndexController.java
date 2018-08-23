package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.User;
import com.nowcoder.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    ToutiaoService toutiaoService;

    @RequestMapping(path = {"/","/index"})
    @ResponseBody
    public String index(HttpSession session) {
        logger.info("Visit Index.");
        return "Hello world, " + session.getAttribute("msg") + "<br>service: " + toutiaoService.say();
    }

    @RequestMapping(value = {"/profile/{groupID}/{userID}"})
    @ResponseBody
    public String profile(@PathVariable("groupID") String groupID,
                          @PathVariable("userID") int userID,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", defaultValue = "1") int key) {
        return String.format("groupID:{%s}, userID:{%d}, type:{%d}, key:{%d}", groupID, userID, type, key);
    }

    @RequestMapping(value = "/vm")
    public String news(Model model) {
        model.addAttribute("value1", 1);

        List<String> colors = Arrays.asList(new String[] {"Green", "Black", "White"});
        Map<String, String> map = new HashMap<>();
        for(int i = 0; i < 4; i++) {
            map.put(String.valueOf(i), String.valueOf(i * i));
        }

        model.addAttribute("colors", colors);
        model.addAttribute("maps", map);
        model.addAttribute("user1", new User());
        return "news";
    }

    @RequestMapping(path = {"/request"})
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }

        for(Cookie cookie : request.getCookies()) {
            sb.append("Cookie:");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br>");
        }

        sb.append("Method:" + request.getMethod() + "<br>");
        sb.append("PathInfo:" + request.getPathInfo() + "<br>");
        sb.append("QueryString:" + request.getQueryString() + "<br>");
        sb.append("URI:" + request.getRequestURI() + "<br>");
        return sb.toString();
    }

    @RequestMapping(value = "/response")
    @ResponseBody
    public String response(@CookieValue(value = "nowcoderid", defaultValue = "a") String nowcoderId,
                           @RequestParam(value = "key", defaultValue = "key") String key,
                           @RequestParam(value = "value", defaultValue = "value") String value,
                           HttpServletResponse response) {
        response.addCookie(new Cookie(key, value));
        response.addHeader(key, value);
        return "NowCoderId from Cookie:" + nowcoderId;
    }

    @RequestMapping(path = "/redirect/{code}")
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession session) {
        RedirectView red = new RedirectView("/");
        if(code == 301)
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        session.setAttribute("msg", "Jump index!");
        return red;
    }

    @RequestMapping(path = "/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "password", required = false) String password) {
        if("admin".equals(password)) {
            return "hello admin!";
        }
        throw new IllegalArgumentException("Password 错误");
    }
    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e) {
        return "error:" + e.getMessage();
    }
}
