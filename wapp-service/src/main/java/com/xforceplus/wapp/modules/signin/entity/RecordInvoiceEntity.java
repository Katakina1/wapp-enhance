package com.xforceplus.wapp.modules.signin.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票签收实体
 * CreateBy leal.liang on 2018/4/12.
 **/
@Getter
@Setter
@ToString
public class RecordInvoiceEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = 3784001228206872166L;

    private String uuid;

    //自定义标志
    private String qs;

    //用户账号
    private String userNum;

    //用户名称
    private String userName;

    //备注
    private String remark;

    //购方税号
    private String gfTaxNo;

    //销方税号
    private String xfTaxNo;

    //合计
    private Double totalAmount;

    //发票类型
    private String invoiceType;

    //发票代码
    private String invoiceCode;

    //发票号码
    private String invoiceNo;

    //开票日期
    private Date invoiceDate;

    //签收日期
    private Date signInDate;

    //签收日期
    private Date createDate;

    //购方名称
    private String gfName;

    //销方名称
    private String xfName;

    //金额
    private BigDecimal invoiceAmount;

    //税额
    private BigDecimal taxAmount;

    //签收方式
    private String qsType;

    //签收描述
    private String notes;

    //扫描id
    private String scanId;

    //签收结果
    private String qsStatus;

    //校验码
    private String checkCode;

    //自定义标识 导入类型（0:excel 1:图片）
    private String importType;
    //重复标识(0：未重复， 1重复)
    private String repeatFlag;
    //自定义签收处理标识，只用于导入时发票已在扫描表存在（1:扫描表存在 2:没有税号处理权限）
    private String handleFlag;

    /**
     * 发票类型错误标识 1 类型错误
     */
    private String typeErrorFlag;

    //发票类型名
    private String invoiceTypeName;


    private String userAccount;

    //序列号
    private String localTrmSeqNum;

    private String vendername;

    private String rebateyesorno;


    private String venderid;
    private String jvCode;
    private String companyCode;
    private String fileType;
    private Date rzhDate;
    private String rzhYesorno;

    private String dyInvoiceCode;
    private String dyInvoiceNo;

    private String flowType;

    private String isdel;
    
    private String scanMatchStatus;

    private String scanFailReason;

    private String venderidEdit;
    private String isExistStamper;
    private String noExistStamperNotes;
    
    private String costNo;
    private String rownumber;
    private Integer jpl;
    private String gl;
    private String supplierNumber;

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
	
    
}
