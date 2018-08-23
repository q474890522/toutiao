package com.nowcoder.dao;

import com.nowcoder.model.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketDAO {
    String TABLE_NAME = "login_ticket";
    String SELECT_FIELD = " id, ticket, user_id, expired, status ";
    String INSERT_FIELD = " ticket, user_id, expired, status ";

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELD, ") values(#{ticket}, #{userId}, #{expired}, #{status})"})
    int addLoginTicket(LoginTicket loginTicket);

    @Select({"select ", SELECT_FIELD, " from ", TABLE_NAME, "where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update ", TABLE_NAME, " set status = #{status} where ticket = #{ticket}"})
    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}
