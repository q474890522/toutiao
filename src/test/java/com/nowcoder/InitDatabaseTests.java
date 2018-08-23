package com.nowcoder;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.NewsDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.security.acl.LastOwnerException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")
@WebAppConfiguration
public class InitDatabaseTests {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private NewsDAO newsDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private CommentDAO commentDAO;

    @Test
    public void contextLoads() {
        Random random = new Random();
        for (int i = 0; i < 11; i++) {
            User user = new User();
            user.setName(String.format("User%d", i+1));
            user.setPassword("");
            user.setSalt("");
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            userDAO.addUser(user);

            user.setPassword("aaaaaa");
            userDAO.updatePassword(user);

            News news = new News();
            Date date = new Date();
            date.setTime(date.getTime() + 10*3600*5*i);
            news.setCreatedDate(date);
            news.setUserId(i);
            news.setLikeCount(i+1);
            news.setCommentCount(i+1);
            news.setTitle(String.format("Title:%d", i));
            news.setImage(String.format("http://images.nowcoder.com/head/%dm.png", random.nextInt(1000)));
            news.setLink(String.format("http://www.nowcoder.com/%d.html", i));
            newsDAO.addNews(news);

            LoginTicket loginTicket = new LoginTicket();
            loginTicket.setUserId(user.getId());
            loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
            Date date1 = new Date();
            date1.setTime(date1.getTime() + 3600*24*5*1000);
            loginTicket.setExpired(date1);
            loginTicket.setStatus(0);
            loginTicketDAO.addLoginTicket(loginTicket);

            for(int j = 0; j < 3; j++) {
                Comment comment = new Comment();
                comment.setContent("lalalaalalalalalaalalla" + String.valueOf(j));
                comment.setCreatedDate(new Date());
                comment.setUserId(user.getId());
                comment.setEntityType(EntityType.ENTITY_NEWS);
                comment.setEntityId(news.getId());
                comment.setStatus(0);
                commentDAO.addComment(comment);
            }

        }
        Assert.assertEquals("aaaaaa", userDAO.selectById(1).getPassword());
        //userDAO.deleteById(1);
        //Assert.assertNull(userDAO.selectById(1));
        Assert.assertNotNull(commentDAO.selectByEntity(1, EntityType.ENTITY_NEWS).get(0));
        Assert.assertEquals(3, commentDAO.getCommentCount(1, EntityType.ENTITY_NEWS));
    }
}