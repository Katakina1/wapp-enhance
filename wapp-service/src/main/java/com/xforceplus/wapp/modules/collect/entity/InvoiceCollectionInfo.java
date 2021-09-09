package com.xforceplus.wapp.modules.collect.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票采集实体（抵账表）
 * @author Colin.hu
 * @date 4/11/2018
 */
@Getter @Setter @ToString
public class InvoiceCollectionInfo extends AbstractBaseDomain {

    private static final long serialVersionUID = -1356483923293195675L;

    /**
     * 税额
     */
    private String taxAmount;

    /**
     * 购方税号
     */
    private String gfTaxNo;
    private String dqskssq;//当前税款所属期
    private String outReason;//转出原因1-免税项目用 ；2-集体福利,个人消费；3-非正常损失；4-简易计税方法征税项目用；5-免抵退税办法不得抵扣的进项税额；6-纳税检查调减进项税额；7-红字专用发票通知单注明的进项税额；8-上期留抵税额抵减欠税
    private String xfBankAndNo;//销方开户行及账号
    private String uuid;//唯一索引 防重复
    private String gxType;//勾选方式(0-手工勾选 1-扫码勾选 2-导入勾选 3- 智能勾选 4-手工认证 5-扫码认证 6-导入认证)
    private Date sendDate;//发送认证时间
    private String gxUserName;//提交认证操作人
    private String authStatus;//认证处理状态  0-未认证 1-已勾选未确认，2已确认 3 已发送认证 4 认证成功 5 认证失败
    private Date createDate;//采集时间
    private String gfAddressAndPhone;//购方地址电话
    private String rzhType;//认证方式 1-勾选认证 2-扫描认证
    private String txfbz;//通行费标志(Y-可抵扣通行费，N-不可抵扣通行费)
    private String sourceSystem;//底账来源  0-采集 1-查验
    private String outTaxAmount;//转出税额
    private String rzhBackMsg;//认证结果回传信息
    private String xfAddressAndPhone;//销方地址及电话
    private Date rzhDate;//认证时间
    private String totalAmount;//价格合计
    private Date statusUpdateDate;//发票状态修改时间
    private String qsStatus;//签收结果（0-未签收 1-已签收）
    private Date confirmDate;//认证确认时间
    private String invoiceStatus;//发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常
    private String rzhBelongDate;//实际认证归属期 yyyyMM
    private String rzhYesorno;//是否认证 0-未认证 1-已认证
    private Date outDate;//转出日期
    private String remark;//备注
    private String rzhBelongDateLate;//最晚认证归属期 yyyyMM
    private String gxfwq;//当前税款所属期可勾选发票开票日期范围起
    private String invoiceAmount;//金额
    private Date invoiceDate;//开票日期
    private String valid;//是否有效 0-有效 1-无效
    private String xfName;//销方名称
    private String checkCode;//校验码
    private String gfBankAndNo;//购方开户行及账号
    private String sfdbts;//是否代办退税(0：否 1：是)
    private String invoiceType;//发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
    private String gxfwz;//当前税款所属期可勾选发票开票日期范围止
    private String outInvoiceAmout;//转出金额
    private Date lastUpdateDate;//发票状态最后修改时间
    private String detailYesorno;//是否存入明细 0 无明细 1 有明细
    private String rzlx;//认证类型(1-抵扣 2-退税 3-代理退税   发票已认证、已勾选有该标签）
    private String invoiceNo;//发票号码
    private String gxUserAccount;//提交认证操作人账号
    private String outStatus;//转出状态 0-未转出   1-已转出
    private Date qsDate;//签收时间
    private Date gxDate;//勾选时间
    private String invoiceCode;//发票代码
    private String outRemark;//转出备注
    private String gxjzr;//当前税款所属期勾选截止日
    private String sfygx;//是否已勾选
    private String gfName;//购方名称
    private String qsType;//签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收5-pdf上传签收
    private String xfTaxNo;//销方税号
    private String lslbz;//零税率标志 (空:非零税率，1:税率栏位显示“免税”，2:税率栏位显示“不征收”，3:零税率
    private String machinecode; //机器编号
    private String confirmUser;//认证人
    private String jvCode;
    private  String companyCode;
    private  String scanName;//扫描人
    private  String flowType;//大类类型 （商品 ，费用，资产）
    private  String vendername;//大类类型 （商品 ，费用，资产）
    private  String supplierNumber;//供应商号
    private  String newTaxno;


    //税率
    private  String taxRate;
    /**
     * 抵账表原有购方税号，防止查验后得到的购方税号和原有抵账表购方税号不同而更新不到数据
     */
    private String buyerTaxNo;
    /**
     * 以字符串接收开票日期（只适用于getInvoiceInfo，其他请用invoiceDate字段）
     */
    private String openInvoiceDate;
    //扫描流水号
    private String scanningSeriaNo;

    /**
     * 签收状态名称
     */
    private String qsStatusName;

    /**
     * 开票状态名称
     */
    private String invoiceStatusName;

    /**
     * 是否签收名称
     */
    private String rzhYesornoName;

    /**
     * 发票类型名称
     */
    private String invoiceTypeName;
    //凭证号
    private String certificateNo;
    //供应商号
    private String venderid;

   //税码
   private String taxCode;
    /**
     * 签收方式名称
     */
    private String qsTypeName;
    /**
     * host税率
     */
    private String hostTaxRate;
    /**
     * 购入不动产或入不动产的建筑安装将增加不动产原值是否超过50%-
     */
    private String smoking;
    /**
     * 业务类型
     */
    private String serviceType;
    //退货状态
    private String tpStatus;
    //退票时间
    private String tpDate;
    private String store;
    private BigDecimal dkInvoiceAmount;
    private BigDecimal deductibleTax;
    private BigDecimal deductibleTaxRate;
    private String costDeptId;
    private String epsNo;
    private String rownumber;
    private String mccCode;
    private String glAccount;
    private String cpUserId;
    private String dkTaxAmount;
    private String gl;

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
