/**  
 * @Title:  Receiver.java   
 * @Package com.xforceplus.wapp.modules.job.utils
 * @Description:    TODO
 * @author: jiaohongyang     
 * @date:   2018年12月20日 下午10:03:51   
 */  
package com.xforceplus.wapp.modules.job.utils;

import com.xforceplus.wapp.modules.job.service.impl.ConsumerServerImpl;
import com.xforceplus.wapp.modules.posuopei.dao.SubmitOutstandingReportDao;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**   
 * @ClassName:  Receiver   
 * @Description:TODO
 * @author: jiaohongyang
 * @date:   2018年12月20日 下午10:03:51   
 *   
 */
@Component
public class Receiver implements ApplicationRunner {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Receiver.class);
    @Autowired
    SubmitOutstandingReportDao submitOutstandingReportDao;
    
    @Value("${activemq.user}")
	private String username;
	@Value("${activemq.password}")
	private String pwd;
	@Value("${activemq.url_one}")
	private String url_one;
	@Value("${activemq.url_two}")
	private String url_two;
	@Value("${activemq.queueResponse}")
	private String queueResponse;
    @Value("${activemq.queueResponse_two}")
    private String queueResponse2;
    @Value("${activemq.queueResponse_three}")
    private String queueResponse3;
    @Value("${activemq.queueResponse_four}")
    private String queueResponse4;
    @Value("${activemq.queueResponse_five}")
    private String queueResponse5;
    @Value("${activemq.queueResponse_six}")
    private String queueResponse6;
    @Value("${activemq.queueResponse_seven}")
    private String queueResponse7;
    @Value("${activemq.queueResponse_eight}")
    private String queueResponse8;
	private ActiveMQConnectionFactory connectionFactory;
    private ActiveMQConnection connection;
    private ActiveMQConnection connection2;
    private ActiveMQConnection connection3;
    private ActiveMQConnection connection4;
    private ActiveMQConnection connection5;
    private ActiveMQConnection connection6;
    private ActiveMQConnection connection7;
    private ActiveMQConnection connection8;
    private Session session;
    private Session session2;
    private Session session3;
    private Session session4;
    private Session session5;
    private Session session6;
    private Session session7;
    private Session session8;
    private Queue queue;
    private Queue queue2;
    private Queue queue3;
    private Queue queue4;
    private Queue queue5;
    private Queue queue6;
    private Queue queue7;
    private Queue queue8;
    private MessageConsumer consumer;
    private MessageConsumer consumer2;
    private MessageConsumer consumer3;
    private MessageConsumer consumer4;
    private MessageConsumer consumer5;
    private MessageConsumer consumer6;
    private MessageConsumer consumer7;
    private MessageConsumer consumer8;
	private  static Integer count=0;
	public void init() {
        try {
            //设置预读取为1
            ActiveMQPrefetchPolicy p = new ActiveMQPrefetchPolicy();
            p.setQueuePrefetch(1);
            //创建一个链接工厂
            connectionFactory = new ActiveMQConnectionFactory(username, pwd, url_one);
            //设置预读取为1
            connectionFactory.setPrefetchPolicy(p);
             //从工厂中创建一个链接
            logger.info("create first consumer start");
            connection = (ActiveMQConnection) connectionFactory.createConnection();


            //启动链接
            connection.start();

            //创建一个事物session
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            queue = session.createQueue(queueResponse);
            consumer = session.createConsumer(queue);
            //设置消息监听器
            consumer.setMessageListener(new ReceiveListener());
            logger.info("create first consumer success");

            //create second consumer
            logger.info("create second consumer start");
            connection2 = (ActiveMQConnection) connectionFactory.createConnection();
            connection2.start();

            session2 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            queue2 = session2.createQueue(queueResponse2);
            consumer2 = session2.createConsumer(queue2);
            //设置消息监听器
            consumer2.setMessageListener(new ReceiveListener());
            logger.info("create second consumer success");


            //create third consumer
            logger.info("create third consumer start");
            connection3 = (ActiveMQConnection) connectionFactory.createConnection();
            connection3.start();

            session3 = connection3.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            queue3 = session3.createQueue(queueResponse3);
            consumer3 = session3.createConsumer(queue3);
            //设置消息监听器
            consumer3.setMessageListener(new ReceiveListener());
            logger.info("create third consumer success");


            //create forth consumer
            logger.info("create forth consumer start");
            connection4 = (ActiveMQConnection) connectionFactory.createConnection();
            connection4.start();

            session4 = connection4.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            queue4 = session4.createQueue(queueResponse4);
            consumer4 = session4.createConsumer(queue4);
            //设置消息监听器
            consumer4.setMessageListener(new ReceiveListener());
            logger.info("create forth consumer success");


            //create five consumer
            logger.info("create five consumer start");
            connection5 = (ActiveMQConnection) connectionFactory.createConnection();
            connection5.start();

            session5 = connection5.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            queue5 = session5.createQueue(queueResponse5);
            consumer5 = session5.createConsumer(queue5);
            //设置消息监听器
            consumer5.setMessageListener(new ReceiveListener());
            logger.info("create five consumer success");



            //create six consumer
            logger.info("create six consumer start");
            connection6 = (ActiveMQConnection) connectionFactory.createConnection();
            connection6.start();

            session6 = connection6.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            queue6 = session6.createQueue(queueResponse6);
            consumer6 = session6.createConsumer(queue6);
            //设置消息监听器
            consumer6.setMessageListener(new ReceiveListener());
            logger.info("create six consumer success");



            //create seven consumer
            logger.info("create seven consumer start");
            connection7 = (ActiveMQConnection) connectionFactory.createConnection();
            connection7.start();

            session7 = connection7.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            queue7 = session7.createQueue(queueResponse7);
            consumer7 = session7.createConsumer(queue7);
            //设置消息监听器
            consumer7.setMessageListener(new ReceiveListener());
            logger.info("create seven consumer success");

            //create eight consumer
            logger.info("create eight consumer start");
            connection8 = (ActiveMQConnection) connectionFactory.createConnection();
            connection8.start();

            session8 = connection8.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            queue8 = session8.createQueue(queueResponse8);
            consumer8 = session8.createConsumer(queue8);
            //设置消息监听器
            consumer8.setMessageListener(new ReceiveListener());
            logger.info("create eight consumer success");


        } catch (Exception e) {
            count++;
            e.printStackTrace();
            try {
                //设置预读取为1
                ActiveMQPrefetchPolicy p = new ActiveMQPrefetchPolicy();
                p.setQueuePrefetch(1);
                //创建一个链接工厂
                connectionFactory = new ActiveMQConnectionFactory(username, pwd, url_one);
                //设置预读取为1
                connectionFactory.setPrefetchPolicy(p);
                //从工厂中创建一个链接
                logger.info("create first consumer start");
                connection = (ActiveMQConnection) connectionFactory.createConnection();


                //启动链接
                connection.start();

                //创建一个事物session
                session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                queue = session.createQueue(queueResponse);
                consumer = session.createConsumer(queue);
                //设置消息监听器
                consumer.setMessageListener(new ReceiveListener());
                logger.info("create first consumer success");

                //create second consumer
                logger.info("create second consumer start");
                connection2 = (ActiveMQConnection) connectionFactory.createConnection();
                connection2.start();

                session2 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                queue2 = session2.createQueue(queueResponse2);
                consumer2 = session2.createConsumer(queue2);
                //设置消息监听器
                consumer2.setMessageListener(new ReceiveListener());
                logger.info("create second consumer success");


                //create third consumer
                logger.info("create third consumer start");
                connection3 = (ActiveMQConnection) connectionFactory.createConnection();
                connection3.start();

                session3 = connection3.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                queue3 = session3.createQueue(queueResponse3);
                consumer3 = session3.createConsumer(queue3);
                //设置消息监听器
                consumer3.setMessageListener(new ReceiveListener());
                logger.info("create third consumer success");


                //create forth consumer
                logger.info("create forth consumer start");
                connection4 = (ActiveMQConnection) connectionFactory.createConnection();
                connection4.start();

                session4 = connection4.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                queue4 = session4.createQueue(queueResponse4);
                consumer4 = session4.createConsumer(queue4);
                //设置消息监听器
                consumer4.setMessageListener(new ReceiveListener());
                logger.info("create forth consumer success");


                //create five consumer
                logger.info("create five consumer start");
                connection5 = (ActiveMQConnection) connectionFactory.createConnection();
                connection5.start();

                session5 = connection5.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                queue5 = session5.createQueue(queueResponse5);
                consumer5 = session5.createConsumer(queue5);
                //设置消息监听器
                consumer5.setMessageListener(new ReceiveListener());
                logger.info("create five consumer success");



                //create six consumer
                logger.info("create six consumer start");
                connection6 = (ActiveMQConnection) connectionFactory.createConnection();
                connection6.start();

                session6 = connection6.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                queue6 = session6.createQueue(queueResponse6);
                consumer6 = session6.createConsumer(queue6);
                //设置消息监听器
                consumer6.setMessageListener(new ReceiveListener());
                logger.info("create six consumer success");



                //create seven consumer
                logger.info("create seven consumer start");
                connection7 = (ActiveMQConnection) connectionFactory.createConnection();
                connection7.start();

                session7 = connection7.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                queue7 = session7.createQueue(queueResponse7);
                consumer7 = session7.createConsumer(queue7);
                //设置消息监听器
                consumer7.setMessageListener(new ReceiveListener());
                logger.info("create seven consumer success");

                //create eight consumer
                logger.info("create eight consumer start");
                connection8 = (ActiveMQConnection) connectionFactory.createConnection();
                connection8.start();

                session8 = connection8.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                queue8 = session8.createQueue(queueResponse8);
                consumer8 = session8.createConsumer(queue8);
                //设置消息监听器
                consumer8.setMessageListener(new ReceiveListener());
                logger.info("create eight consumer success");

            } catch (Exception e1) {
                if(count<=10){
                    init();
                }
            }
        }
    }

	/**   
	 * <p>Title: run</p>   
	 * <p>Description: </p>   
	 * @param args
	 * @throws Exception   
	 * @see org.springframework.boot.ApplicationRunner#run(org.springframework.boot.ApplicationArguments)
	 */  
	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.info("Successful service startup!");
		init();
		
	}

}
