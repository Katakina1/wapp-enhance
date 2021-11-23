package com.xforceplus.wapp.service;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.client.LockClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class RedissonTest extends BaseUnitTest {

    @Autowired
    private LockClient lockClient;

    @Test
    public void testLockClient() {
        boolean lock = lockClient.tryLock("a", () -> {
            log.info("lock");
        }, -1, 1);
        log.info("lock:", lock);
    }

}
