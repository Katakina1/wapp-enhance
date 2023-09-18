package com.xforceplus.wapp.service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.common.enums.DeductRedNotificationEventEnum;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ActiveMqTest extends BaseUnitTest {

    @Value("${activemq.queue-name.enhance-claim-verdict-queue}")
    private String activemqEnhanceClaimVerdictQueue;

    @Autowired
    private ActiveMqProducer activeMqProducer;
    @Autowired
    private CommonMessageService commonMessageService;

    @Test
    public void testJms() {
        Map<String, String> enhanceClaimVerdictMap = new HashMap<>();
        enhanceClaimVerdictMap.put("operationType", "1");//1通过 2不通过
        enhanceClaimVerdictMap.put("businessNo", "10000");
        activeMqProducer.send(activemqEnhanceClaimVerdictQueue, JSON.toJSONString(enhanceClaimVerdictMap));
        System.out.println("--");
    }

    @Test
    public void test01() {
        TXfRedNotificationEntity tXfRedNotificationEntity = new TXfRedNotificationEntity();
        tXfRedNotificationEntity.setId(1231L);
        tXfRedNotificationEntity.setPid("1222");
        commonMessageService.sendMessage(DeductRedNotificationEventEnum.APPLY_FAILED, tXfRedNotificationEntity);
    }

}
