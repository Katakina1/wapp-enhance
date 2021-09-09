/**  
 * @Title:  SamlService.java   
 * @Package com.xforceplus.wapp.modules.base.service
 * @Description:    TODO
 * @author: jiaohongyang     
 * @date:   2019年1月15日 下午7:33:42   
 */  
package com.xforceplus.wapp.modules.base.service;

/**   
 * @ClassName:  SamlService   
 * @Description:TODO
 * @author: jiaohongyang
 * @date:   2019年1月15日 下午7:33:42   
 *   
 */
public interface SamlService {
	String getUserID(String responseMessage);
	String generateRequestURL();
}
