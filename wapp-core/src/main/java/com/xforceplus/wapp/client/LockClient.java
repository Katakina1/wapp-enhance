package com.xforceplus.wapp.client;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class LockClient {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * @param key
     * @param runnable
     * @param leaseTime 如果为-1表示不超时，后台启动看门狗自动续约30s
     * @param waitTime  表示获取锁等待时间
     * @param <T>
     * @return
     */
    @SneakyThrows
    public <T extends Serializable> boolean tryLock(String key, Runnable runnable, int leaseTime, int waitTime) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("key not empty");
        }
        if (runnable == null) {
            throw new IllegalArgumentException("runnable not null");
        }
        if (leaseTime < -1) {
            throw new IllegalArgumentException("leaseTime>=-1");
        }
        if (waitTime < 1) {
            throw new IllegalArgumentException("waitTime >= 1");
        }
        String lockKey = "lockKey:" + key;
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
            try {
                log.info("获取分布式锁成功 lockKey={}", lockKey);
                runnable.run();
                return true;
            } finally {
                try {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        log.info("释放分布式锁 lockKey={}", lockKey);
                    } else {
                        log.info("释放分布式锁 当前线程不持有锁 lockKey={}", lockKey);
                    }
                } catch (Exception e) {
                    log.error("释放分布式锁失败 lockKey={} {}", lockKey, e);
                }
            }
        }
        log.warn("获取分布式锁失败 lockKey={}", lockKey);
        return false;
    }

    /**
     * @param key
     * @param runnable
     * @param leaseTime 如果为-1表示不超时，后台启动看门狗自动续约30s
     * @param waitTime  表示获取锁等待时间
     * @param <T>
     * @return
     */
    @SneakyThrows
    public <T> T tryLock(String key, Callable<T> callable, int leaseTime, int waitTime) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("key not empty");
        }
        if (callable == null) {
            throw new IllegalArgumentException("runnable not null");
        }
        if (leaseTime < -1) {
            throw new IllegalArgumentException("leaseTime>=-1");
        }
        if (waitTime < 1) {
            throw new IllegalArgumentException("waitTime >= 1");
        }
        String lockKey = "lockKey:" + key;
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
            try {
                log.info("获取分布式锁成功 lockKey={}", lockKey);
                return callable.call();
            } finally {
                try {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        log.info("释放分布式锁 lockKey={}", lockKey);
                    } else {
                        log.info("释放分布式锁 当前线程不持有锁 lockKey={}", lockKey);
                    }
                } catch (Exception e) {
                    log.error("释放分布式锁失败 lockKey={} {}", lockKey, e);
                }
            }
        }
        log.warn("获取分布式锁失败 lockKey={}", lockKey);
        return null;
    }

    /**
     * @param key
     * @return
     */
    @SneakyThrows
    public boolean tryLock(String key) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("key not empty");
        }
        String lockKey = "lockKey:" + key;
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.tryLock(0, -1, TimeUnit.SECONDS)) {
            try {
                log.info("获取分布式锁成功 lockKey={}", lockKey);
                return true;
            } finally {
                try {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        log.info("释放分布式锁 lockKey={}", lockKey);
                    } else {
                        log.info("释放分布式锁 当前线程不持有锁 lockKey={}", lockKey);
                    }
                } catch (Exception e) {
                    log.error("释放分布式锁失败 lockKey={} {}", lockKey, e);
                }
            }
        }
        log.warn("获取分布式锁失败 lockKey={}", lockKey);
        return false;
    }

}
