package com.xforceplus.wapp.modules.signin.entity;

import com.mysql.jdbc.Blob;

/** 
* @ClassName: InvoiceImgQueryVo 
* @Description: 查询返回发票图片数据对象
* @author yuanlz
* @date 2016年12月1日 下午12:25:54 
*  
*/
public class InvoiceImgQueryVo  {

	private static final long serialVersionUID = 1L;
	
	// 主键
	private Long id;
	// 图片数据
	private byte[] image;
	// 唯一索引
	private String uuid;
	//图片路径
	private String imagePath;
	//扫描图片唯一识别码 
	private String scanId;
	
	public String getScanId() {
		return scanId;
	}
	public void setScanId(String scanId) {
		this.scanId = scanId;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
