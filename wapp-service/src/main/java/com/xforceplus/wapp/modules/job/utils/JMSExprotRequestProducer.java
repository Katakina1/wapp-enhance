package com.xforceplus.wapp.modules.job.utils;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.jms.*;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class JMSExprotRequestProducer {
	private static final Logger LOGGER = getLogger(JMSExprotRequestProducer.class);

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


	public void sendMessage(Destination destination, String message)  throws Exception
	{

		LOGGER.info("导出生产者队列："+destination);
		LOGGER.info("导出生产者消息："+message);
		ConnectionFactory cfOne=null;
		if(activemqProducerstatusUrl.equals("one")) {
			cfOne = new ActiveMQConnectionFactory(username, pwd, url_one);
		}else{
			cfOne = new ActiveMQConnectionFactory(username, pwd, url_two);
		}
		Connection c = null;
		try {
			c = cfOne.createConnection();
			c.start();
			Session s = c.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
//			destination = s.createQueue(queue);

			MessageProducer mp = s.createProducer(destination);
			TextMessage tm = s.createTextMessage(message);
			mp.send(tm);
			s.commit();
		} catch (Exception e) {
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