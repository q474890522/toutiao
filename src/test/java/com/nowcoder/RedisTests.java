package com.nowcoder;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.JedisAdapter;
import org.apache.commons.lang.builder.ToStringBuilder;
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
public class RedisTests {

    @Autowired
    JedisAdapter jedisAdapter;

    @Test
    public void testObject() {
        User user = new User();
        user.setHeadUrl("http://image.nowcoder.com/head/100t.png");
        user.setPassword("aaaaaa");
        user.setName("Linda");
        user.setSalt("salt");

        jedisAdapter.setObject("user1xx", user);

        User u = jedisAdapter.getObject("user1xx", User.class);
        System.out.println(ToStringBuilder.reflectionToString(u));
    }

}