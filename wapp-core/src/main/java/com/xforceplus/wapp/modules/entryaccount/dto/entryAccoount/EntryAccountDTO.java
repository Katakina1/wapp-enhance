package com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.util.BeanUtils;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: ChenHang
 * @Date: 2023/6/29 17:01
 */
@Data
public class EntryAccountDTO implements Serializable {


    /**
     * 系统来源
     * S001:BMS非商结算
     * S002:增值税海关缴款书
     */
    private String businessSource;
    /**
     * 预制凭证息 也就是 凭证号
     * RMS系统的预制凭证号
     */
    private String accNo;
    /**
     * 过账日期 也就是 入账日期
     */
    @BeanUtils.DateTimeFormat
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    private Date postDate;
    /**
     * 发票类型
     * 1-增值税发票
     * 2-增值税海关缴款书
     */
    private String invType;
    /**
     * 发票号码
     */
    private String invoiceNo;
    /**
     * 发票代码
     */
    private String invoiceCode;
    /**
     * jv
     */
    private String jvCode;
    /**
     * 成本中心
     */
    private String costCenter;
    /**
     * 公司代码
     */
    private String companyCode;
    /**
     * 税单号 及 海关缴款书号码
     */
    private String taxDocNo;
    /**
     * 税码
     */
    private String taxCode;
    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 税额
     */
    private BigDecimal taxAmount;
    /**
     * 签名
     * 签名, 根据入参进行签名处理
     */
    private String sign;
    /**
     * 供应商id
     */
    private String venderid;
    /**
     * 税价合计(含税金额), 发票+税率维度
     */
    private BigDecimal totalAmount;
    /**
     * 大类
     */
    private String largeCategory;
    /**
     * 成本金额(不含税金额), 发票+税率维度
     */
    private BigDecimal invoiceAmount;

}
