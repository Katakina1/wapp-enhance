package com.xforceplus.wapp.service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ActiveMqTest extends BaseUnitTest {

    @Value("${activemq.queue-name.enhance_claim_verdict_queue}")
    private String activemqEnhanceClaimVerdictQueue;

    @Autowired
    private ActiveMqProducer activeMqProducer;

    @Test
    public void testJms() {
        Map<String, String> enhanceClaimVerdictMap = new HashMap<>();
        enhanceClaimVerdictMap.put("operationType", "1");//1通过 2不通过
        enhanceClaimVerdictMap.put("businessNo", "10000");
        activeMqProducer.send(activemqEnhanceClaimVerdictQueue, JSON.toJSONString(enhanceClaimVerdictMap));
        System.out.println("--");
    }
}
