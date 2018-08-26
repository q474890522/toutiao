package com.nowcoder.async;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 事件类
 */
public class EventModel {
    private EventType type; //事件类型
    private int actorId; //事件发起者
    private int entityId; //事件作用者Id
    private int entityType; //事件作用者类型
    private int entityOwnerId; //事件作用者的拥有者，比如要通知的那方
    private Map<String, String> exts = new HashMap<>(); //触发事件的现场，事件携带的数据

    public EventModel(EventType type) {
        this.type = type;
    }

    public EventModel() {

    }

    public String getExt(String key) {
        return exts.get(key);
    }

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }
}
