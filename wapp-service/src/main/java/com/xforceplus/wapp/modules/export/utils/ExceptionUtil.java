package com.xforceplus.wapp.modules.export.utils;

import org.hibernate.validator.internal.util.StringHelper;

/**
 * 
 * ****************************************************************************
 * 异常明细信息处理类
 *
 * @author(作者)：xuyongyun	
 * @date(创建日期)：2019年4月2日
 ******************************************************************************
 */
public class ExceptionUtil {
	
	private static final String SIGN_1 = "com.dxhy";
	private static final String SIGN_2 = ".java:";
	
	private static final String IP_REGEX = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
	
	private static final String HOST_PREFIX_1 = "localhost";
	private static final String HOST_PREFIX_2 = "127.0.0.1";
	private static final String HOST_PREFIX_REPLACE = "address";
	
	private static final String PROTOCOL_PREFIX_1 = "http";
	private static final String PROTOCOL_PREFIX_REPLACE = "protocol";
	
	private static final String DB_PREFIX_1 = "ORA-";
	
	private static final int MAX_RANKS = 3;
	
	/**
	 * 获取异常的详细信息
	 *	
	 * @param e 异常
	 * @param subLength 需要获取的信息长度。
	 *                  指定值小于等于0时，则返回全部异常信息
	 *               	指定值大于0时，则按照指定值截取
	 * @return
	 * @since           1.0
	 */
	public static String getExceptionDeteil(Exception e,int subLength) {
		StringBuilder deteil = new StringBuilder();
		deteil.append(handleInformation(e.getMessage()));
		StackTraceElement[] messages = e.getStackTrace();
		int stackTraceLength = messages.length;
		int count = 0;
		for (int i = 0; i < stackTraceLength; i++) {
			String tmpMsg = messages[i].toString();
			if(tmpMsg.indexOf(SIGN_1)>=0&&tmpMsg.indexOf(SIGN_2)>=0) {
				if(count<MAX_RANKS) {
					deteil.append("<br>" + messages[i].toString());
					count++;
				}else {
					break;
				}
			}
		}
		String msg = deteil.toString();
		int msgLength = msg.length();
		if(subLength>0) {
			if(msgLength>subLength) {
				msg = msg.substring(0,subLength);
			}
		}
		return msg;
	}
	
	/**
	 * 获取异常的详细信息
	 *	
	 * @param e 异常
	 * @param subLength 需要获取的信息长度。
	 *                  指定值小于等于0时，则返回全部异常信息
	 *               	指定值大于0时，则按照指定值截取
	 * @return
	 * @since           1.0
	 */
	public static String getExceptionDeteil(Throwable e,int subLength) {
		StringBuilder deteil = new StringBuilder();
		deteil.append(handleInformation(e.getMessage()));
		StackTraceElement[] messages = e.getStackTrace();
		int stackTraceLength = messages.length;
		int count = 0;
		for (int i = 0; i < stackTraceLength; i++) {
			String tmpMsg = messages[i].toString();
			if(tmpMsg.indexOf(SIGN_1)>=0&&tmpMsg.indexOf(SIGN_2)>=0) {
				if(count<MAX_RANKS) {
					deteil.append("<br>" + messages[i].toString());
					count++;
				}else {
					break;
				}
			}
		}
		String msg = deteil.toString();
		int msgLength = msg.length();
		if(subLength>0) {
			if(msgLength>subLength) {
				msg = msg.substring(0,subLength);
			}
		}
		return msg;
	}
	
	/**
	 * 敏感信息过滤
	 *	
	 * @param msg
	 * @return
	 * @since           1.0
	 */
	private static String handleInformation(String msg) {
		if(!StringHelper.isNullOrEmptyString(msg)) {
			
			//过滤ip
			msg = msg.replaceAll(IP_REGEX, HOST_PREFIX_REPLACE);
			msg = msg.replaceAll(HOST_PREFIX_1, HOST_PREFIX_REPLACE);
			msg = msg.replaceAll(HOST_PREFIX_2, HOST_PREFIX_REPLACE);
			//过滤通信协议
			msg = msg.replaceAll(PROTOCOL_PREFIX_1, PROTOCOL_PREFIX_REPLACE);
			//过滤sql
			if(msg.indexOf(DB_PREFIX_1)>=0){
				int start = msg.indexOf(DB_PREFIX_1);
				msg = msg.substring(start);
			}
		}
		return msg;
	}
}
