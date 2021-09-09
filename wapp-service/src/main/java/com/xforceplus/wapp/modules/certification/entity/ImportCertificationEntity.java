package com.xforceplus.wapp.modules.certification.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 导入页面实体
 * @author Colin.hu
 * @date 4/19/2018
 */
@Getter @Setter @ToString
public class ImportCertificationEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = -2308842182785334065L;

    /**
     * 不可认证提示
     */
    private String noAuthTip;

    /**
     * 查验结果描述
     */
    private String checkMassege;

    /**
     * 发票代码
     */
    @NotBlank
    @Length(max = 12)
    private String invoiceCode;

    /**
     * 发票号码
     */
    @NotBlank
    @Length(max = 8)
    private String invoiceNo;

    /**
     * 开票日期
     */
    @NotBlank
    private String invoiceDate;

    /**
     * 金额
     */
    @NotBlank
    private String amount;

    /**
     * 金额
     */
    private String invoiceAmount;

    /**
     * 校验码
     */
    private String checkCode;

    /**
     * 发票类型
     */
    private String invoiceType;

    /**
     * 发票状态
     */
    private String invoiceStatus;

    /**
     * 税额
     */
    private String taxAmount;

    /**
     * 认证处理状态 ( 4-认证成功 5-认证失败)
     */
    private String authStatus;

    /**
     * 是否认证  0-未认证 1-已认证
     */
    private String rzhYesorno;

    /**
     * 开票日期与认证归属期的比较结果
     */
    private Boolean compareToResult;

    /**
     * 是否有效
     */
    private String valid;

    /**
     * 发票类型名称
     */
    private String invoiceTypeName;

    /**
     * 是否存在抵账（0：不存在 1存在 2 存在但无税号权限）
     */
    private String recordFlag;

    /**
     * excel导入的序号
     */
    private int indexNo;

    /**
     * 后续添加的税款所属期
     * 2018-07-09
     */
    private String currentTaxPeriod;
    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
