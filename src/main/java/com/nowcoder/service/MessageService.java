package com.nowcoder.service;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    public int addMessage(Message message) {
        return messageDAO.addMessage(message);
    }

    public List<Message> getConversationDetails(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetails(conversationId, offset, limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId, offset, limit);
    }

    public int getConversationUnReadCount(String conversationId, int userId) {
        return messageDAO.getConversationUnReadCount(conversationId, userId);
    }

    public int getConversationTotalCount(String conversationId) {
        return messageDAO.getConversationTotalCount(conversationId);
    }
}
