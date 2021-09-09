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
