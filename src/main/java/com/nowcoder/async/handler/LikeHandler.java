package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.User;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {
    @Override
    public void doHandler(EventModel model) {
        System.out.println("Liked");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
