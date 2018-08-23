package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.QiniuService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newDetail(@PathVariable("newsId") int newsId, Model model) {
        News news = newsService.getNewsById(newsId);

        if(news != null) {
            //评论
            List<Comment> comments = commentService.getCommentByEntity(newsId, EntityType.ENTITY_NEWS);
            List<ViewObject> commentVOs = new ArrayList<>();
            for(Comment comment : comments) {
                ViewObject vo = new ViewObject();
                vo.set("comment", comment);
                vo.set("user", userService.getUser(comment.getUserId()));
                commentVOs.add(vo);
            }
            model.addAttribute("comments", commentVOs);
            model.addAttribute("news", news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));
        }
        return "detail";
    }
    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId, @RequestParam("content") String content) {
        try {
            //过滤
            content = HtmlUtils.htmlEscape(content);
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setCreatedDate(new Date());
            comment.setUserId(hostHolder.getUser().getId());
            comment.setStatus(0);
            commentService.addComment(comment);
            //更新news里评论数
            int count = commentService.getCommentCount(newsId, EntityType.ENTITY_NEWS);
            newsService.updateCommentCount(count, newsId);
            //异步化更新
        } catch (Exception e) {
            logger.error("评论失败" + e.getMessage());
        }
        return "redirect:/news/" + newsId;
    }
    @RequestMapping(path = {"/uploadImage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            //本地
            String fileUrl = newsService.saveImage(file);
            //String fileUrl = qiniuService.saveImage(file);
            if(fileUrl == null) {
                return ToutiaoUtil.getJSONString(1, "上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        } catch(Exception e) {
            logger.error("上传图片异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传失败");
        }
    }

    @RequestMapping(path = "/image", method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam(value = "name") String imageName,
                         HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            //本地
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR + imageName)), response.getOutputStream());
            //StreamUtils.copy((new URL(ToutiaoUtil.QINIU_DOMIN_PREFIX + imageName + "?imageView2/1/w/50/h/50/").openStream()), response.getOutputStream());
        } catch (IOException e) {
            logger.error("图片读取异常" + e.getMessage());
        }
    }

    @RequestMapping(path = "/user/addNews", method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link) {
        try {
            News news = new News();
            if(hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                news.setUserId(3); // 写到前端里，3表示未登陆的匿名用户
            }
            news.setImage(image);
            news.setTitle(title);
            news.setLink(link);
            news.setCreatedDate(new Date());
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        } catch(Exception e) {
            logger.error("资讯发布失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "发布失败");
        }

    }

}
