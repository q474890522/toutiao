package com.nowcoder;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@WebAppConfiguration
public class MessageTests {

    @Autowired
    MessageDAO messageDAO;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;
    @Test
    public void messageTest() {
        List<Message> messages = messageService.getConversationDetails("2_10", 0, 10);
        for(Message message : messages) {
            System.out.println(message.getFromId());
            //Assert.assertNotNull(userService.getUser(message.getFromId()));
        }
        Assert.assertNotNull(messages.get(1));
    }

}