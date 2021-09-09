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
public class ElectronInvoiceLog extends AbstractBaseDomain {


    private static final long serialVersionUID = 2197029813050879183L;

    /**
     * 发票号码
     */
    private String invoiceNo;
    /**
     * 发票代码
     */
    private String invoiceCode;
    /**
     * 上传文件路径
     */
    private String filePath;
    /**
     * 创建日期
     */
    private Date createDate;
    /**
     * 文件名称
     */
    private String fileName;

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
