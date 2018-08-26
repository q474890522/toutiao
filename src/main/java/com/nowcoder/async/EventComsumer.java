package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.model.EntityType;
import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

/**
 * 事件消费者，从Redis的Event Queue取出事件拿相应的Handler处理
 */
@Service
public class EventComsumer implements InitializingBean, ApplicationContextAware{
    //要找到所有实现了EventHandler的接口，用ApplicationContextAware上下文，用于记录所有事件的。

    @Autowired
    JedisAdapter jedisAdapter;

    private static final Logger logger = LoggerFactory.getLogger(EventComsumer.class);

    //对于每一个到来的事件，都有找到它对应的一堆Handler然后一个一个执行。
    private Map<EventType, List<EventHandler>> config = new HashMap<>();

    private  ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //当EventComsumer bean被初始化后获取所有实现了EventHandler接口的类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans != null) {
            for(Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                //把各个Handler中能处理的EventType取出来
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for(EventType eventType : eventTypes) {
                    //对于每个eventType把它的Handler找出来，加到config中
                    if(!config.containsKey(eventType)) {
                        config.put(eventType, new ArrayList<>());
                    }
                    config.get(eventType).add(entry.getValue());
                }
            }
        }

        //完成初始化后启动线程，从阻塞队列中取Event，找到相对应的Handler，然后处理掉
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    //从右边去阻塞pop一个事件
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> event = jedisAdapter.brpop(0, key);
                    //String eventKey = event.get(0);
                    //String eventModelJson = event.get(1);
                    //第一个为key,第二个为value
                    for(String msg : event) {
                        if(msg.equals(key))
                            continue;
                        EventModel eventModel = JSON.parseObject(msg, EventModel.class);
                        if(!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的事件");
                            continue;
                        }
                        List<EventHandler> eventHandlers = config.get(eventModel.getType());
                        for(EventHandler handler : eventHandlers) {
                            handler.doHandler(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }
}
