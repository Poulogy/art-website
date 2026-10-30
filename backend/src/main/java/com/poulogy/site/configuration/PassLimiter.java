package com.poulogy.site.configuration;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

@Component
public class PassLimiter {
    private final Map<String, Deque<Long>> hits = new HashMap<>();

    public synchronized int count(String key, long windowMs) {
        Deque<Long> list = hits.get(key);
        if (list == null) {
            return 0;
        }
        long now = System.currentTimeMillis() - windowMs;
        while (!list.isEmpty() && list.peekFirst() < now) {
            list.pollFirst();
        }
        if (list.isEmpty()) {
            hits.remove(key);
            return 0;
        }
        return list.size();
    }
    public synchronized void hit(String key) {
        hits.computeIfAbsent(key, k -> new ArrayDeque<>()).addLast(System.currentTimeMillis());
    }

    public synchronized boolean allow(String key, int max, long windowMs) {
        if (count(key, windowMs) >= max) {
            return false;
        }
        hit(key);
        return true;
    }

}
