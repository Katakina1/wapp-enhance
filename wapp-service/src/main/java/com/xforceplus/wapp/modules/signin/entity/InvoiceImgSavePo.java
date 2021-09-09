package com.xforceplus.wapp.modules.signin.entity;

import java.util.Date;


/** 
* @ClassName: InvoiceImgSavePo 
* @Description: 插入图片传输参数po
* @author yuanlz
* @date 2016年12月1日 上午11:29:23 
*  
*/
public class InvoiceImgSavePo {

	private static final long serialVersionUID = 1L;

	// 主键
	private Long id;
	// blob 图片数据
	private byte[] image;
	// 唯一索引
	private String uuid;
	 
	private int valid;
	
	private Date create_date;
	
	private Date update_date;
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
	public int getValid() {
		return valid;
	}
	public void setValid(int valid) {
		this.valid = valid;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}
	
}