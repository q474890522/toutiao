package com.nowcoder.dao;

import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELD = " from_id, to_id, content, created_date, has_read, conversation_id ";
    String SELECT_FIELD = "id, " + INSERT_FIELD;

    @Insert({"insert into ", TABLE_NAME, " (", INSERT_FIELD, ") " +
            "values (#{fromId}, #{toId}, #{content}, #{createdDate}, #{hasRead}, #{conversationId})"})
    int addMessage(Message message);


    @Select({"select ", SELECT_FIELD, " from ", TABLE_NAME,
            " where conversation_id = #{conversationId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetails(@Param("conversationId") String conversationId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Select({"select ", INSERT_FIELD, ", count(id) as id " +
            "from ( " +
            "select * from ", TABLE_NAME,
            " where from_id = #{userId} or to_id = #{userId} " +
            "order by id desc) as tt " +
            "group by conversation_id " +
            "order by id desc " +
            "limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    @Select({"select count(id) from ", TABLE_NAME,
            " where has_read = 0 and conversation_id = #{conversationId} and to_id = #{userId}"})
    int getConversationUnReadCount(@Param("conversationId") String conversationId, @Param("userId") int userId);

    @Select({"select count(id) from ", TABLE_NAME,
            " where conversation_id = #{conversationId}"})
    int getConversationTotalCount(@Param("conversationId") String conversationId);

    @Update({"update ", TABLE_NAME, "set has_read = #{hasRead} where conversation_id = #{conversationId}"})
    int updateHasRead(@Param("hasRead") int hasRead, @Param("conversationId") int conversationId);
}
