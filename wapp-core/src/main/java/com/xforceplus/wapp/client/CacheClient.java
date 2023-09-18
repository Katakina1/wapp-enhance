package com.xforceplus.wapp.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import jodd.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Slf4j
@Service
public class CacheClient {

    @Autowired
    private RedissonClient redissonClient;

    private Cache<String, Serializable> lockCache =
            CacheBuilder.newBuilder().maximumSize(500)
                    .expireAfterWrite(1, TimeUnit.SECONDS).build();


    /**
     * 从底层数据源回源,放入缓存
     *
     * @param key
     * @param callable
     * @param timeToLive
     * @param <T>
     * @return
     */
    public <T extends Serializable> T get(String key, Callable<T> callable, int timeToLive) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("key not empty");
        }
        if (callable == null) {
            throw new IllegalArgumentException("callable not empty");
        }
        if (timeToLive < 1) {
            throw new IllegalArgumentException("过期时间必须>=1秒");
        }
        return innerGet(key, callable, timeToLive);
    }


    /**
     * 从缓存获取数据
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(String key) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("key not empty");
        }
        try {
            return (T) redissonClient.getBucket(key).get();
        } catch (Exception ex) {
            String msg = MessageFormat.format("get error {0}", key);
            log.error(msg, ex);
        }
        return null;
    }

    /**
     * 数据放入缓存
     *
     * @param key        缓存key
     * @param value      缓存value
     * @param timeToLive 过期时间 秒为单位
     */
    public void set(String key, Serializable value, int timeToLive) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("key not empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("value not empty");
        }
        if (timeToLive < 1) {
            throw new IllegalArgumentException("过期时间必须>=1秒");
        }
        try {
            redissonClient.getBucket(key).set(value, timeToLive, TimeUnit.SECONDS);
        } catch (Exception ex) {
            String msg = MessageFormat.format("get error {0}", key);
            log.error(msg, ex);
        }
    }

    /**
     * 从底层数据源回源,放入缓存
     *
     * @param key
     * @param callable
     * @param timeToLive
     * @param <T>
     * @return
     */
    private <T extends Serializable> T innerGet(String key, Callable<T> callable, int timeToLive) {
        Serializable value = get(key);
        if (value != null) {
            return (T) value;
        }
        value = fetchSource(key, callable);
        set(key, value, timeToLive);
        return (T) value;
    }

    /**
     * 回源<br>
     * 并发情况下只有单线程回源
     *
     * @param key
     * @param callable
     * @param <T>
     * @return
     */
    private <T extends Serializable> T fetchSource(String key, Callable<T> callable) {
        try {
            T value = (T) lockCache.get(key, callable);
            return value;
        } catch (Throwable ex) {
            Throwable rootCause = ExceptionUtil.getRootCause(ex);
            if (CacheLoader.InvalidCacheLoadException.class.isInstance(rootCause)) {
                return null;
            }
            String msg = MessageFormat.format("fetch source error {0}", key);
            log.error(msg, ex);
        }
        return null;
    }

    public void clean(String key){
        redissonClient.getBucket(key).delete();
    }


}
