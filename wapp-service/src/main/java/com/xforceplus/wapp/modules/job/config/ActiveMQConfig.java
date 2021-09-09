package com.xforceplus.wapp.modules.job.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import com.xforceplus.wapp.modules.job.utils.JMSProducer;

import static org.slf4j.LoggerFactory.getLogger;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;

//@Configuration
//@EnableJms
public class ActiveMQConfig {
	private static final Logger LOGGER= getLogger(ActiveMQConfig.class);
    // topic模式的ListenerContainer
    //@Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ConnectionFactory activeMQConnectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setPubSubDomain(true);
        bean.setConnectionFactory(activeMQConnectionFactory);
        LOGGER.info("---------------------createTOPICBeenSuccesss---------------------------------");

        return bean;
    }
    // queue模式的ListenerContainer
   // @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerQueue(ConnectionFactory activeMQConnectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(activeMQConnectionFactory);
        LOGGER.info("---------------------createQUEUEBeenSuccesss---------------------------------");
        return bean;
    }


}
