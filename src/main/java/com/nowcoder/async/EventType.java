package com.nowcoder.async;

/**
 * 枚举类，事件类型
 */
public enum EventType {
    LIKE(0),
    COMMETN(1),
    LOGIN(2),
    MAIL(3);

    private int value;

    EventType (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
