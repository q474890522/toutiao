package com.nowcoder.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    private ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public User getUser() {
        return threadLocal.get();
    }

    public void setUser(User user) {
        threadLocal.set(user);
    }

    public void clear() {
        threadLocal.remove();
    }
}
