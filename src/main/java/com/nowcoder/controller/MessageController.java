package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = "/msg/addMessage", method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId, @RequestParam("toId") int toId, @RequestParam("content") String content) {
        try{
            Message message = new Message();
            message.setFromId(fromId);
            message.setToId(toId);
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
            messageService.addMessage(message);
            return ToutiaoUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("添加聊天信息异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "添加聊天信息异常");
        }
    }

    @RequestMapping(path = "/msg/list", method = {RequestMethod.GET})
    public String getConversationList(Model model) {
        try{
            int localUserId = hostHolder.getUser().getId();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            List<ViewObject> conversationVOs = new ArrayList<>();
            for(Message conversation : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", conversation);
                int targetId = conversation.getFromId() == localUserId ? conversation.getToId() : conversation.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                int unreadCount = messageService.getConversationUnReadCount(conversation.getConversationId(), localUserId);
                vo.set("unreadCount", unreadCount);
                conversationVOs.add(vo);
            }
            model.addAttribute("conversations", conversationVOs);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = "/msg/detail", method = {RequestMethod.GET})
    public String getConversationDetails(@RequestParam("conversationId") String conversationId, Model model) {
        try{
            List<Message> messages = messageService.getConversationDetails(conversationId, 0, 10);
            List<ViewObject> messageVOs = new ArrayList<>();
            for(Message message : messages) {
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                User user = userService.getUser(message.getFromId());
                if(user == null)
                    continue;
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                messageVOs.add(vo);
            }
            model.addAttribute("messages", messageVOs);
        } catch (Exception e) {
            logger.error("获取详细信息失败。");
        }
        return "letterDetail";
    }
}
