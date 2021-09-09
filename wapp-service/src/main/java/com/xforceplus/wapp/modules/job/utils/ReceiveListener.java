/**  
 * @Title:  ReceiveListener.java   
 * @Package com.xforceplus.wapp.modules.job.utils
 * @Description:    TODO
 * @author: jiaohongyang     
 * @date:   2018年12月20日 下午9:54:49   
 */  
package com.xforceplus.wapp.modules.job.utils;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xforceplus.wapp.modules.job.service.ConsumerServer;

/**   
 * @ClassName:  ReceiveListener   
 * @Description:TODO
 * @author: jiaohongyang
 * @date:   2018年12月20日 下午9:54:49   
 *   
 */
//@Component
public class ReceiveListener implements MessageListener{
	private static Logger logger = Logger.getLogger(ReceiveListener.class);
	private static ConsumerServer consumerServer;
	
	@Resource
    public void setDao(ConsumerServer consumerServer) {
		ReceiveListener.consumerServer = consumerServer;
    }

	/**   
	 * <p>Title: onMessage</p>   
	 * <p>Description: </p>   
	 * @param message   
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)   
	 */  
	@Override
	public void onMessage(Message message) {
		logger.info("--------接受消息-------------");
		try {
            TextMessage msg = (TextMessage) message;
            if(msg !=null ) {
            	logger.info("Consumer收到的报文为:"+msg);
            	String text = msg.getText();
            	if(StringUtils.isNotBlank(text)) {
            		consumerServer.receiveQueue(text);
            	}
            }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
	
}
