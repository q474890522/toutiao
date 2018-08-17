package com.nowcoder.dao;

import com.nowcoder.model.News;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NewsDAO {
    String TABLE_NAME = " news ";
    String INSERT_FIELD = " title, link, image, like_count, comment_count, created_date, user_id ";
    String SELECT_FIELD = "id, title, link, image, like_count, comment_count, created_date, user_id";
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELD, ") values(#{title}, #{link}, #{image}, #{likeCount}, #{commentCount}," +
            " #{createdDate}, #{userId})"})
    void addNews(News news);

    List<News> selectByUserIdAndOffset(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);
    //@Select({"select ", SELECT_FIELD, " from ", TABLE_NAME, " where user_id = #{userId} and #{userId} != 0 order by id desc limit #{offset} #{limit}"})
    //List<News> selectByUserIdAndOffset(int userId, int offset, int limit);
}
