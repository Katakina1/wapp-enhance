package com.xforceplus.wapp.modules.job.utils;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.MessageProducer;

@Component
public class JMSProducer {
	private static final Logger LOGGER = getLogger(JMSProducer.class);

	//@Value("${activemq.user}")
	private String username;
	//@Value("${activemq.password}")
	private String pwd;
	//@Value("${activemq.url_one}")
	private String url_one;
	//@Value("${activemq.url_two}")
	private String url_two;
	//@Value("${activemq.queue}")
	private String queue;
	/*
    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(Destination destination, String message) {
    	LOGGER.info("-------sendMessageBegin-------",message);
    	LOGGER.info("-------message-------",message);
    	LOGGER.info("-------destination-------",destination);
        this.jmsTemplate.convertAndSend(destination,message);
		 LOGGER.info("-------sendMessagesuccess-------");
    }
    */

	public void sendMessage(Destination destination, String message)
	{
//		String username = "system";
//		String pwd = "manager";
//		String url = "tcp://tstr501218.cn.wal-mart.com:61616";
//		String queue = "cn.wm.host.invoice_test.request";

		ConnectionFactory cfOne = new ActiveMQConnectionFactory(username, pwd, url_one);
		ConnectionFactory cfTwo = new ActiveMQConnectionFactory(username, pwd, url_two);
		Connection c = null;
		try {
			c = cfOne.createConnection();
			c.start();
			Session s = c.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			destination = s.createQueue(queue);
			MessageProducer mp = s.createProducer(destination);
			TextMessage tm = s.createTextMessage(message);
			mp.send(tm);
			s.commit();
		} catch (Exception e) {
			try {
				c = cfTwo.createConnection();
				c.start();
				Session s = c.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
				destination = s.createQueue(queue);
				MessageProducer mp = s.createProducer(destination);
				TextMessage tm = s.createTextMessage(message);
				mp.send(tm);
				s.commit();
			}catch(Exception e1) {
				e1.printStackTrace();
			}finally {
				if(c != null) {
					try {
						c.close();
					} catch (JMSException ee) {
						ee.printStackTrace();
					}
				}
			}
			e.printStackTrace();
		}finally {
			if(c != null) {
				try {
					c.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
