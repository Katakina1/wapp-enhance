/**  
 * @Title:  ReceiveListener.java   
 * @Package com.xforceplus.wapp.modules.job.utils
 * @Description:    TODO
 * @author: jiaohongyang     
 * @date:   2018年12月20日 下午9:54:49   
 */  
package com.xforceplus.wapp.modules.job.utils;

import com.xforceplus.wapp.modules.export.service.IExcelExportService;
import com.xforceplus.wapp.modules.job.service.ConsumerServer;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**   
 * @ClassName:  ReceiveListener   
 * @Description:TODO
 * @author: jiaohongyang
 * @date:   2018年12月20日 下午9:54:49   
 *   
 */
@Component
public class ExprotEquestListener implements MessageListener{
	private static Logger logger = Logger.getLogger(ExprotEquestListener.class);
	private static IExcelExportService iExcelExportService;

	@Resource
    public void setDao(IExcelExportService iExcelExportService) {
		ExprotEquestListener.iExcelExportService = iExcelExportService;
    }

	/**
	 * <p>Title: onMessage</p>receiveQueue
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
            	logger.info("消费者收到的报文为:"+msg);
            	String text = msg.getText();
            	if(StringUtils.isNotBlank(text)) {
					iExcelExportService.exportExcel(text);
            	}
            }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
	
}
