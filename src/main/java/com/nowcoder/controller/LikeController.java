package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.utils.ToutiaoUtil;
import org.apache.catalina.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    private static final Logger logger  = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NewsService newsService;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("newsId") int newsId) {
        try {
            int userId = hostHolder.getUser().getId();
            long likeCount = likeService.like(userId, EntityType.ENTITY_NEWS, newsId);
            //?不也是访问数据库耗时？
            newsService.updateLikeCount((int)likeCount, newsId);
            return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
        } catch (Exception e) {
            logger.error("添加喜欢异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "添加喜欢异常");
        }

    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("newsId") int newsId) {
        try {
            int userId = hostHolder.getUser().getId();
            long likeCount = likeService.dislike(userId, EntityType.ENTITY_NEWS, newsId);
            //?不也是访问数据库耗时？
            newsService.updateLikeCount((int)likeCount, newsId);
            return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
        } catch (Exception e) {
            logger.error("添加不喜欢异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "添加不喜欢异常");
        }

    }
}
