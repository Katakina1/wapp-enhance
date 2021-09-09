package com.xforceplus.wapp.modules.einvoice.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Date 4/18/2018.
 *
 * @author marvin.zhong
 */
@Getter
@Setter
@ToString
public class ElectronInvoiceImage extends AbstractBaseDomain {

    private static final long serialVersionUID = 694711801586873578L;
    /**
     * 图片
     */
    private byte[] image;
    /**
     * 唯一索引
     */
    private String uuid;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getScanId() {
		return scanId;
	}

	public void setScanId(String scanId) {
		this.scanId = scanId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
     * 是否有效
     */
    private int valid;
    /**
     * 创建日期
     */
    private Date createDate;
    /**
     * 更新日期
     */
    private Date updateDate;
    /**
     * 图片路径
     */
    private String imagePath;
    /**
     * 扫描图片唯一识别码
     */
    private String scanId;

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
