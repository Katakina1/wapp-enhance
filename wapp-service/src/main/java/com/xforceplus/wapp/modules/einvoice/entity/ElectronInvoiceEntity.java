package com.xforceplus.wapp.modules.einvoice.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created on 2018/04/12.
 * @author marvin
 * 电票上传实体类
 */
@Getter
@Setter
@ToString
public class ElectronInvoiceEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = -8006603698970334917L;

    /**
     * 发票类型
     */
    private String invoiceType;
    /**
     * 发票代码
     */
    private String invoiceCode;
    /**
     * 发票号码
     */
    private String invoiceNo;
    /**
     * 扫描流水号
     */
    private String invoiceSerialNo;
    /**
     * 购方税号
     */
    private String gfTaxNo;
    /**
     * 购方名称
     */
    private String gfName;
    /**
     * 销方税号
     */
    private String xfTaxNo;
    /**
     * 销方名称
     */
    private String xfName;
    /**
     * 金额
     */
    private BigDecimal invoiceAmount;
    /**
     * 税额
     */
    private BigDecimal taxAmount;
    /**
     * 价税合计
     */
    private BigDecimal totalAmount;
    /**
     * 开票日期
     */
    private Date invoiceDate;
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收，5-pdf上传）
     */
    private String qsType;
    /**
     * 签收结果(0-签收失败 1-签收成功）
     */
    private String qsStatus;
    /**
     * 有效状态 1-有效 0-无效
     */
    private String valid;
    /**
     * 唯一索引 组成：发票代码+发票号码
     */
    private String uuid;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新日期
     */
    private Date updateDate;
    /**
     * 签收时间
     */
    private Date qsDate;
    /**
     * 扫描唯一识别码
     */
    private String  scanId;
    /**
     * 扫描备注
     */
    private String notes;
    /**
     * 校验码
     */
    private String checkCode;

    /**
     * 上传的pdf文件名
     */
    private String pdfName;

    /**
     * 解析的pdf是否正常 true：正常  false：解析失败
     */
    private Boolean readPdfSuccess;

    /**
     * 上传的pdf是否重复true:重复   false: 不重复
     */
    private Boolean saveRepeat = Boolean.FALSE;
    /**
     * 查验是否成功
     */
    private Boolean checkSuccess = Boolean.TRUE;
    /**
     * 查验返回信息
     */
    private String resultTip;

    /**
     * 上传图片路径
     */
    private String imgPath;

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
