/**  
 * @Title:  ReceiveListener.java   
 * @Package com.xforceplus.wapp.modules.job.utils
 * @Description:    TODO
 * @author: jiaohongyang     
 * @date:   2018年12月20日 下午9:54:49   
 */  
package com.xforceplus.wapp.modules.job.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.modules.export.service.IExcelExportService;
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
public class ExprotOkListener implements MessageListener{
	private static Logger logger = Logger.getLogger(ExprotOkListener.class);
	private static IExcelExportService iExcelExportService;

	@Resource
    public void setDao(IExcelExportService iExcelExportService) {
		ExprotOkListener.iExcelExportService = iExcelExportService;
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
					JSONObject jsonObject = JSON.parseObject(text);
					jsonObject=jsonObject.getJSONObject("message");
					iExcelExportService.sendWebsocketMessage(jsonObject.getString("userAccount"));
            	}
            }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
	
}
