/**  
 * @Title:  ReceiveListener.java   
 * @Package com.xforceplus.wapp.modules.job.utils
 * @Description:    TODO
 * @author: jiaohongyang     
 * @date:   2018年12月20日 下午9:54:49   
 */  
package com.xforceplus.wapp.modules.job.utils;

import com.xforceplus.wapp.modules.job.service.ConsumerServer;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import static org.slf4j.LoggerFactory.getLogger;

/**   
 * @ClassName:  ReceiveListener   
 * @Description:TODO
 * @author: jiaohongyang
 * @date:   2018年12月20日 下午9:54:49   
 *   
 */
@Component
public class ReceiveListener implements MessageListener{
	private static Logger logger =getLogger (ReceiveListener.class);
	private static ConsumerServer consumerServer;
	
	@Resource
    public void setDao(ConsumerServer consumerServer) {
		ReceiveListener.consumerServer = consumerServer;
    }

	/**   
	 * <p>Title: onMessage</p>   
	 * <p>Description: </p>   
	 * @param message   
	 * @see MessageListener#onMessage(Message)
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
            		try{
						consumerServer.receiveQueue(text);
						msg.acknowledge();
					}catch (Exception e){
            			logger.info("Exception",e);
					}


            	}
            }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
	
}
