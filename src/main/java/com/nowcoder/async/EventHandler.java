package com.nowcoder.async;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.User;

import java.util.List;

/**
 * 事件处理的统一接口
 */
public interface EventHandler {
    //处理事件的方法
    void doHandler(EventModel model);
    //这个Handler需要关注的一些EventType，并且当这些事件到来的时候要进行处理
    List<EventType> getSupportEventTypes();
}
