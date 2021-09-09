/**  
 * @Title:  ConsumerServer.java   
 * @Package com.xforceplus.wapp.modules.job.service
 * @Description:    TODO
 * @author: jiaohongyang     
 * @date:   2018年12月20日 下午10:08:09   
 */  
package com.xforceplus.wapp.modules.job.service;

/**   
 * @ClassName:  ConsumerServer   
 * @Description:TODO
 * @author: jiaohongyang
 * @date:   2018年12月20日 下午10:08:09   
 *   
 */
public interface ConsumerServer {
	 public void receiveQueue(String text);
}
