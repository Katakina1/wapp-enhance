/**
 * @Title:  Receiver.java
 * @Package com.dxhy.modules.job.utils
 * @Description:    TODO
 * @author: jiaohongyang
 * @date:   2018年12月20日 下午10:03:51
 */
package com.xforceplus.wapp.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * @ClassName:  Receiver
 * @Description:TODO
 * @author: jiaohongyang
 * @date:   2018年12月20日 下午10:03:51
 *
 */
@Component
@Slf4j
public class Receiver implements ApplicationRunner{

    @Value("${activemq.user}")
    private String username;
    @Value("${activemq.password}")
    private String pwd;
    @Value("${activemq.url_one}")
    private String url_one;


    @Value("${activemq.enhance_claim_verdict_queue}")
    private String activemqEnhanceClaimVerdictQueue;

    private ConnectionFactory connectionFactory;
    private ActiveMQConnection connection;
    private Session session;
    private Queue queue;
    private MessageConsumer consumer;

    public void init() {
        try {
            //创建一个链接工厂
            connectionFactory = new ActiveMQConnectionFactory(username, pwd, url_one);
            //从工厂中创建一个链接
            connection = (ActiveMQConnection) connectionFactory.createConnection();
            //启动链接
            connection.start();
            //创建一个事物session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            queue = session.createQueue(activemqEnhanceClaimVerdictQueue);
            consumer = session.createConsumer(queue);
            //设置消息监听器
            consumer.setMessageListener(new ClaimVerdictListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>Title: run</p>
     * <p>Description: </p>
     * @param args
     * @throws Exception
     * @see ApplicationRunner#run(ApplicationArguments)
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Successful service startup!");
        init();

    }

}
