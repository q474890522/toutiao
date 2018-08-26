package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 事件生产者，往Redis的Event放事件
 */
@Service
public class EventProducer {

    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    //发送事件
    public boolean fireEvent(EventModel model) {
        try {
            String json = JSON.toJSONString(model);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            logger.error("Producer放Event进队列异常" + e.getMessage());
            return false;
        }
    }
}
