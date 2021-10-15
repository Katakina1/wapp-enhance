package com.xforceplus.wapp.service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ActiveMqTest extends BaseUnitTest {

    @Value("${activemq.user}")
    private String username;
    @Value("${activemq.password}")
    private String pwd;
    @Value("${activemq.url_one}")
    private String url_one;

    @Value("${activemq.enhance_claim_verdict_queue}")
    private String activemqEnhanceClaimVerdictQueue;

    @Test
    public void testSendMq()  throws Exception{
        ConnectionFactory cfOne = new ActiveMQConnectionFactory(username, pwd, url_one);
        Destination destination = new ActiveMQQueue(activemqEnhanceClaimVerdictQueue);
        Connection c = cfOne.createConnection();
        c.start();
        Session s = c.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
        MessageProducer mp = s.createProducer(destination);

        Map<String, String> enhanceClaimVerdictMap = new HashMap<>();
        enhanceClaimVerdictMap.put("operationType", "1");//1通过 2不通过
        enhanceClaimVerdictMap.put("businessNo", "10000");

        TextMessage tm = s.createTextMessage(JSON.toJSONString(enhanceClaimVerdictMap));
        mp.send(tm);
        s.commit();
    }
}
