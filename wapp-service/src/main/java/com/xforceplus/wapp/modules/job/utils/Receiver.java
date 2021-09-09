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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
public class Receiver implements ApplicationRunner{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ConsumerServerImpl.class);
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

    @Value("${activemq.producer_status_url}")
    private String activemqProducerstatusUrl;

    @Value("${activemq.producer_status}")
    private String activemqProducerStatus;
    @Value("${activemq.producer_status_code}")
    private String activemqProducerStatusCode;

    @Value("${activemq.exprot_rquest_queue_gfone}")
    private String exprotRquestQueuegfone;
    @Value("${activemq.exprot_rquest_queue_gftwo}")
    private String exprotRquestQueuegftwo;
    @Value("${activemq.exprot_rquest_queue_xfone}")
    private String exprotRquestQueuexfone;
    @Value("${activemq.exprot_rquest_queue_xftwo}")
    private String exprotRquestQueuexftwo;
    @Value("${activemq.export_success_queue_gfone}")
    private String exportSuccessQueuegfone;
    @Value("${activemq.export_success_queue_gftwo}")
    private String exportSuccessQueuegftwo;
    @Value("${activemq.export_success_queue_xfone}")
    private String exportSuccessQueuexfone;
    @Value("${activemq.export_success_queue_xftwo}")
    private String exportSuccessQueuexftwo;





    @Value("${activemq.exprot_equest_queue_buyer}")
    private String activemqExprotEquestEueueBuyer;
    @Value("${activemq.exprot_equest_queue_sellers}")
    private String activemqExprotEquestEueueSellers;

    @Value("${activemq.export_success_queue_one}")
    private String activemqExportSuccessQueueOne;
    @Value("${activemq.export_success_queue_two}")
    private String activemqExportSuccessQueueTwo;

    @Value("${activemq.switch_on_off}")
    private Boolean activemqSwitchOnOff;

    private ConnectionFactory connectionFactory;
    private ActiveMQConnection connection;
    private Session session;
    private Queue queue;
    private MessageConsumer consumer;


    private ActiveMQConnection connection2;
    private Session session2;
    private Queue queue2;
    private MessageConsumer consumer2;


    private ActiveMQConnection connection3;
    private Session session3;
    private Queue queue3;
    private MessageConsumer consumer3;


    private ActiveMQConnection connection4;
    private Session session4;
    private Queue queue4;
    private MessageConsumer consumer4;


    private ActiveMQConnection connection5;
    private Session session5;
    private Queue queue5;
    private MessageConsumer consumer5;



    private ActiveMQConnection connection6;
    private Session session6;
    private Queue queue6;
    private MessageConsumer consumer6;



    private ActiveMQConnection connection7;
    private Session session7;
    private Queue queue7;
    private MessageConsumer consumer7;


    private ActiveMQConnection connection8;
    private Session session8;
    private Queue queue8;
    private MessageConsumer consumer8;




    private ActiveMQConnection connection9;
    private Session session9;
    private Queue queue9;
    private MessageConsumer consumer9;



    private ActiveMQConnection connection10;
    private Session session10;
    private Queue queue10;
    private MessageConsumer consumer10;


    private ActiveMQConnection connection11;
    private Session session11;
    private Queue queue11;
    private MessageConsumer consumer11;


    private ActiveMQConnection connection12;
    private Session session12;
    private Queue queue12;
    private MessageConsumer consumer12;


    private ActiveMQConnection connection13;
    private Session session13;
    private Queue queue13;
    private MessageConsumer consumer13;


    private ActiveMQConnection connection14;
    private Session session14;
    private Queue queue14;
    private MessageConsumer consumer14;


    private ActiveMQConnection connection15;
    private Session session15;
    private Queue queue15;
    private MessageConsumer consumer15;


    private ActiveMQConnection connection16;
    private Session session16;
    private Queue queue16;
    private MessageConsumer consumer16;

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
            if(activemqSwitchOnOff) {
                if (activemqProducerStatus.equals("exprotSeccussRequestProducer")) {
                    if (activemqProducerStatusCode.equals("gfone")) {
                        queue = session.createQueue(exprotRquestQueuegfone);
                        consumer = session.createConsumer(queue);
                        //设置消息监听器
                        consumer.setMessageListener(new ExprotEquestListener());

                        connection2 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection2.start();
                        session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue2 = session2.createQueue(exprotRquestQueuegfone);
                        consumer2 = session2.createConsumer(queue2);
                        //设置消息监听器
                        consumer2.setMessageListener(new ExprotEquestListener());


                        connection3 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection3.start();
                        session3 = connection3.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue3 = session3.createQueue(exprotRquestQueuegfone);
                        consumer3 = session3.createConsumer(queue3);
                        //设置消息监听器
                        consumer3.setMessageListener(new ExprotEquestListener());


                        connection4 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection4.start();
                        session4 = connection4.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue4 = session4.createQueue(exprotRquestQueuegfone);
                        consumer4 = session4.createConsumer(queue4);
                        //设置消息监听器
                        consumer4.setMessageListener(new ExprotEquestListener());


                        connection5 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection5.start();
                        session5 = connection5.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue5 = session5.createQueue(exprotRquestQueuegfone);
                        consumer5 = session5.createConsumer(queue5);
                        //设置消息监听器
                        consumer5.setMessageListener(new ExprotEquestListener());


                        connection6 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection6.start();
                        session6 = connection6.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue6 = session6.createQueue(exprotRquestQueuegfone);
                        consumer6 = session6.createConsumer(queue6);
                        //设置消息监听器
                        consumer6.setMessageListener(new ExprotEquestListener());

                        connection7 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection7.start();
                        session7 = connection7.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue7 = session7.createQueue(exprotRquestQueuegfone);
                        consumer7 = session7.createConsumer(queue7);
                        //设置消息监听器
                        consumer7.setMessageListener(new ExprotEquestListener());


                        connection8 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection8.start();
                        session8 = connection8.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue8 = session8.createQueue(exprotRquestQueuegfone);
                        consumer8 = session8.createConsumer(queue8);
                        //设置消息监听器
                        consumer8.setMessageListener(new ExprotEquestListener());


                        connection9 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection9.start();
                        session9 = connection9.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue9 = session9.createQueue(exprotRquestQueuegfone);
                        consumer9 = session9.createConsumer(queue9);
                        //设置消息监听器
                        consumer9.setMessageListener(new ExprotEquestListener());


                        connection10 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection10.start();
                        session10 = connection10.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue10 = session10.createQueue(exprotRquestQueuegfone);
                        consumer10 = session10.createConsumer(queue10);
                        //设置消息监听器
                        consumer10.setMessageListener(new ExprotEquestListener());
                    } else if (activemqProducerStatusCode.equals("gftwo")) {
                        queue = session.createQueue(exprotRquestQueuegftwo);
                        consumer = session.createConsumer(queue);
                        //设置消息监听器
                        consumer.setMessageListener(new ExprotEquestListener());

                        connection2 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection2.start();
                        session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue2 = session2.createQueue(exprotRquestQueuegftwo);
                        consumer2 = session2.createConsumer(queue2);
                        //设置消息监听器
                        consumer2.setMessageListener(new ExprotEquestListener());


                        connection3 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection3.start();
                        session3 = connection3.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue3 = session3.createQueue(exprotRquestQueuegftwo);
                        consumer3 = session3.createConsumer(queue3);
                        //设置消息监听器
                        consumer3.setMessageListener(new ExprotEquestListener());


                        connection4 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection4.start();
                        session4 = connection4.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue4 = session4.createQueue(exprotRquestQueuegftwo);
                        consumer4 = session4.createConsumer(queue4);
                        //设置消息监听器
                        consumer4.setMessageListener(new ExprotEquestListener());


                        connection5 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection5.start();
                        session5 = connection5.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue5 = session5.createQueue(exprotRquestQueuegftwo);
                        consumer5 = session5.createConsumer(queue5);
                        //设置消息监听器
                        consumer5.setMessageListener(new ExprotEquestListener());


                        connection6 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection6.start();
                        session6 = connection6.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue6 = session6.createQueue(exprotRquestQueuegftwo);
                        consumer6 = session6.createConsumer(queue6);
                        //设置消息监听器
                        consumer6.setMessageListener(new ExprotEquestListener());

                        connection7 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection7.start();
                        session7 = connection7.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue7 = session7.createQueue(exprotRquestQueuegftwo);
                        consumer7 = session7.createConsumer(queue7);
                        //设置消息监听器
                        consumer7.setMessageListener(new ExprotEquestListener());


                        connection8 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection8.start();
                        session8 = connection8.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue8 = session8.createQueue(exprotRquestQueuegftwo);
                        consumer8 = session8.createConsumer(queue8);
                        //设置消息监听器
                        consumer8.setMessageListener(new ExprotEquestListener());


                        connection9 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection9.start();
                        session9 = connection9.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue9 = session9.createQueue(exprotRquestQueuegftwo);
                        consumer9 = session9.createConsumer(queue9);
                        //设置消息监听器
                        consumer9.setMessageListener(new ExprotEquestListener());


                        connection10 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection10.start();
                        session10 = connection10.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue10 = session10.createQueue(exprotRquestQueuegftwo);
                        consumer10 = session10.createConsumer(queue10);
                        //设置消息监听器
                        consumer10.setMessageListener(new ExprotEquestListener());
                    }else if(activemqProducerStatusCode.equals("xfone")){
                        queue = session.createQueue(exprotRquestQueuexfone);
                        consumer = session.createConsumer(queue);
                        //设置消息监听器
                        consumer.setMessageListener(new ExprotEquestListener());

                        connection2 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection2.start();
                        session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue2 = session2.createQueue(exprotRquestQueuexfone);
                        consumer2 = session2.createConsumer(queue2);
                        //设置消息监听器
                        consumer2.setMessageListener(new ExprotEquestListener());


                        connection3 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection3.start();
                        session3 = connection3.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue3 = session3.createQueue(exprotRquestQueuexfone);
                        consumer3 = session3.createConsumer(queue3);
                        //设置消息监听器
                        consumer3.setMessageListener(new ExprotEquestListener());


                        connection4 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection4.start();
                        session4 = connection4.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue4 = session4.createQueue(exprotRquestQueuexfone);
                        consumer4 = session4.createConsumer(queue4);
                        //设置消息监听器
                        consumer4.setMessageListener(new ExprotEquestListener());


                        connection5 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection5.start();
                        session5 = connection5.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue5 = session5.createQueue(exprotRquestQueuexfone);
                        consumer5 = session5.createConsumer(queue5);
                        //设置消息监听器
                        consumer5.setMessageListener(new ExprotEquestListener());


                        connection6 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection6.start();
                        session6 = connection6.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue6 = session6.createQueue(exprotRquestQueuexfone);
                        consumer6 = session6.createConsumer(queue6);
                        //设置消息监听器
                        consumer6.setMessageListener(new ExprotEquestListener());

                        connection7 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection7.start();
                        session7 = connection7.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue7 = session7.createQueue(exprotRquestQueuexfone);
                        consumer7 = session7.createConsumer(queue7);
                        //设置消息监听器
                        consumer7.setMessageListener(new ExprotEquestListener());


                        connection8 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection8.start();
                        session8 = connection8.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue8 = session8.createQueue(exprotRquestQueuexfone);
                        consumer8 = session8.createConsumer(queue8);
                        //设置消息监听器
                        consumer8.setMessageListener(new ExprotEquestListener());


                        connection9 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection9.start();
                        session9 = connection9.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue9 = session9.createQueue(exprotRquestQueuexfone);
                        consumer9 = session9.createConsumer(queue9);
                        //设置消息监听器
                        consumer9.setMessageListener(new ExprotEquestListener());


                        connection10 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection10.start();
                        session10 = connection10.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue10 = session10.createQueue(exprotRquestQueuexfone);
                        consumer10 = session10.createConsumer(queue10);
                        //设置消息监听器
                        consumer10.setMessageListener(new ExprotEquestListener());
                    }else if(activemqProducerStatusCode.equals("xftwo")){
                        queue = session.createQueue(exprotRquestQueuexftwo);
                        consumer = session.createConsumer(queue);
                        //设置消息监听器
                        consumer.setMessageListener(new ExprotEquestListener());

                        connection2 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection2.start();
                        session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue2 = session2.createQueue(exprotRquestQueuexftwo);
                        consumer2 = session2.createConsumer(queue2);
                        //设置消息监听器
                        consumer2.setMessageListener(new ExprotEquestListener());


                        connection3 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection3.start();
                        session3 = connection3.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue3 = session3.createQueue(exprotRquestQueuexftwo);
                        consumer3 = session3.createConsumer(queue3);
                        //设置消息监听器
                        consumer3.setMessageListener(new ExprotEquestListener());


                        connection4 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection4.start();
                        session4 = connection4.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue4 = session4.createQueue(exprotRquestQueuexftwo);
                        consumer4 = session4.createConsumer(queue4);
                        //设置消息监听器
                        consumer4.setMessageListener(new ExprotEquestListener());


                        connection5 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection5.start();
                        session5 = connection5.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue5 = session5.createQueue(exprotRquestQueuexftwo);
                        consumer5 = session5.createConsumer(queue5);
                        //设置消息监听器
                        consumer5.setMessageListener(new ExprotEquestListener());


                        connection6 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection6.start();
                        session6 = connection6.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue6 = session6.createQueue(exprotRquestQueuexftwo);
                        consumer6 = session6.createConsumer(queue6);
                        //设置消息监听器
                        consumer6.setMessageListener(new ExprotEquestListener());

                        connection7 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection7.start();
                        session7 = connection7.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue7 = session7.createQueue(exprotRquestQueuexftwo);
                        consumer7 = session7.createConsumer(queue7);
                        //设置消息监听器
                        consumer7.setMessageListener(new ExprotEquestListener());


                        connection8 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection8.start();
                        session8 = connection8.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue8 = session8.createQueue(exprotRquestQueuexftwo);
                        consumer8 = session8.createConsumer(queue8);
                        //设置消息监听器
                        consumer8.setMessageListener(new ExprotEquestListener());


                        connection9 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection9.start();
                        session9 = connection9.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue9 = session9.createQueue(exprotRquestQueuexftwo);
                        consumer9 = session9.createConsumer(queue9);
                        //设置消息监听器
                        consumer9.setMessageListener(new ExprotEquestListener());


                        connection10 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection10.start();
                        session10 = connection10.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue10 = session10.createQueue(exprotRquestQueuexftwo);
                        consumer10 = session10.createConsumer(queue10);
                        //设置消息监听器
                        consumer10.setMessageListener(new ExprotEquestListener());
                    }
                }else if (activemqProducerStatus.equals("exprotRequestProducer")){
                    if (activemqProducerStatusCode.equals("gfone")) {
                        //如果是one导出申请生产者，那么他就是导出成功one队列的消费者，
                        queue = session.createQueue(exportSuccessQueuegfone);
                        consumer = session.createConsumer(queue);
                        //设置消息监听器
                        consumer.setMessageListener(new ExprotOkListener());


                        connection2 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection2.start();

                        session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue2 = session2.createQueue(exportSuccessQueuegfone);
                        consumer2 = session2.createConsumer(queue2);
                        //设置消息监听器
                        consumer2.setMessageListener(new ExprotOkListener());
                    } else if (activemqProducerStatusCode.equals("gftwo")) {
                        //如果是one导出申请生产者，那么他就是导出成功two队列的消费者，
                        queue = session.createQueue(exportSuccessQueuegftwo);
                        consumer = session.createConsumer(queue);
                        //设置消息监听器
                        consumer.setMessageListener(new ExprotOkListener());

                        connection2 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection2.start();

                        session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue2 = session2.createQueue(exportSuccessQueuegftwo);
                        consumer2 = session2.createConsumer(queue2);
                        //设置消息监听器
                        consumer2.setMessageListener(new ExprotOkListener());
                    }else if (activemqProducerStatusCode.equals("xfone")) {
                        //如果是one导出申请生产者，那么他就是导出成功one队列的消费者，
                        queue = session.createQueue(exportSuccessQueuexfone);
                        consumer = session.createConsumer(queue);
                        //设置消息监听器
                        consumer.setMessageListener(new ExprotOkListener());


                        connection2 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection2.start();

                        session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue2 = session2.createQueue(exportSuccessQueuexfone);
                        consumer2 = session2.createConsumer(queue2);
                        //设置消息监听器
                        consumer2.setMessageListener(new ExprotOkListener());
                    } else if (activemqProducerStatusCode.equals("xftwo")) {
                        //如果是one导出申请生产者，那么他就是导出成功two队列的消费者，
                        queue = session.createQueue(exportSuccessQueuexftwo);
                        consumer = session.createConsumer(queue);
                        //设置消息监听器
                        consumer.setMessageListener(new ExprotOkListener());

                        connection2 = (ActiveMQConnection) connectionFactory.createConnection();
                        connection2.start();

                        session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        queue2 = session2.createQueue(exportSuccessQueuexftwo);
                        consumer2 = session2.createConsumer(queue2);
                        //设置消息监听器
                        consumer2.setMessageListener(new ExprotOkListener());
                    }
                }
//                } else if (activemqProducerStatus.equals("exprotRequestProducer")) {
//                    if (activemqProducerStatusCode.equals("one")) {
//                        //如果是one导出申请生产者，那么他就是导出成功one队列的消费者，
//                        queue = session.createQueue(activemqExportSuccessQueueOne);
//                        consumer = session.createConsumer(queue);
//                        //设置消息监听器
//                        consumer.setMessageListener(new ExprotOkListener());
//
//
//                        connection2 = (ActiveMQConnection) connectionFactory.createConnection();
//                        connection2.start();
//
//                        session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
//                        queue2 = session2.createQueue(activemqExportSuccessQueueOne);
//                        consumer2 = session2.createConsumer(queue2);
//                        //设置消息监听器
//                        consumer2.setMessageListener(new ExprotOkListener());
//                    } else if (activemqProducerStatusCode.equals("two")) {
//                        //如果是one导出申请生产者，那么他就是导出成功two队列的消费者，
//                        queue = session.createQueue(activemqExportSuccessQueueTwo);
//                        consumer = session.createConsumer(queue);
//                        //设置消息监听器
//                        consumer.setMessageListener(new ExprotOkListener());
//
//                        connection2 = (ActiveMQConnection) connectionFactory.createConnection();
//                        connection2.start();
//
//                        session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
//                        queue2 = session2.createQueue(activemqExportSuccessQueueTwo);
//                        consumer2 = session2.createConsumer(queue2);
//                        //设置消息监听器
//                        consumer2.setMessageListener(new ExprotOkListener());
//                    }
//                }
            }else{

            }
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
        logger.info("Successful service startup!");
        init();

    }

}
