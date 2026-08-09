package com.example.enterprise_api.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CacheUtil {

    @Value("${app.cache.ttl-seconds:300}")
    private int ttlSeconds;

    @Value("${app.cache.max-size:10000}")
    private int maxSize;

    private Cache<String, Object> cache;

    @PostConstruct
    public void init() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(ttlSeconds, TimeUnit.SECONDS)
                .maximumSize(maxSize)
                .recordStats()
                .build();
        log.info("✅ Cache initialized with TTL={}s, MaxSize={}", ttlSeconds, maxSize);
    }

    public <T> void set(String key, T value, int secondsToLive) {
        cache.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        return (T) cache.getIfPresent(key);
    }

    public void evict(String key) {
        cache.invalidate(key);
    }

    public void clear() {
        cache.invalidateAll();
    }
}