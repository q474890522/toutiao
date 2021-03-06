package com.nowcoder.service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.News;
import com.nowcoder.utils.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class NewsService {
    @Autowired
    NewsDAO newsDAO;

    public List<News> getNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }

    public void addNews(News news) {
        newsDAO.addNews(news);
    }

    public News getNewsById(int id) {
        return newsDAO.selectById(id);
    }

    public int updateCommentCount(int commentCount, int newsId) {
        return newsDAO.updateCommentCount(commentCount, newsId);
    }

    public int updateLikeCount(int likeCount, int newsId) {
        return newsDAO.updateLikeCount(likeCount, newsId);
    }

    public String saveImage(MultipartFile file) throws IOException {
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if(dotPos < 0)
            return null;
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        Boolean isImage = ToutiaoUtil.isImage(fileExt);
        if(!isImage)
            return null;
        String filename = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + filename).toPath(), StandardCopyOption.REPLACE_EXISTING);
        return ToutiaoUtil.IMAGE_DOMIN + "image?name=" + filename;
    }
}
