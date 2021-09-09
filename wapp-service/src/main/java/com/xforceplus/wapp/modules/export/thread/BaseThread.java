package com.xforceplus.wapp.modules.export.thread;

public class BaseThread extends Thread {
	
	private final String downLoadurl = "api/core/ftp/download";
	
	/**
	 * 获取导出成功标题
	 *	
	 * @param title 公共标题
	 * @return
	 * @since           1.0
	 */
	public String getSuccTitle(String title) {
		title += "导出成功，可以下载";
		return title;
	}
	
	/**
	 * 获取导出成功内容
	 *	
	 * @param createDate 申请时间
	 * @return
	 * @since           1.0
	 */
	public String getSuccContent(String createDate) {
		String content = "申请时间：" + createDate + "。申请导出成功，可以下载！";
		return content;
	}
	
	/**
	 * 获取导出失败标题
	 *	
	 * @param title 公共标题
	 * @return
	 * @since           1.0
	 */
	public String getFailTitle(String title) {
		title += "导出失败";
		return title;
	}
	
	/**
	 * 获取导出失败的内容
	 *	
	 * @param createDate 申请时间
	 * @param errmsg 错误信息
	 * 
	 * @return
	 * @since           1.0
	 */
	public String getFailContent(String createDate,String errmsg) {
		StringBuilder content = new StringBuilder();
		content.append("申请时间：");
		content.append(createDate);
		content.append("。申请导出失败，请重新申请！");
		return content.toString();
	}
	
	/**
	 * 获取excel下载连接
	 *	
	 * @param id
	 * @return
	 * @since           1.0
	 */
	public String getUrl(long id) {
		String url = downLoadurl + "?serviceType=2&downloadId=" + id;
		return url;
	}
}
