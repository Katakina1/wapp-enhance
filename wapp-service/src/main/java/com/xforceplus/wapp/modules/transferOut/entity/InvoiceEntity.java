package com.xforceplus.wapp.modules.transferOut.entity;


import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/11
 * Time:19:26
 * 抵账表
*/
@Getter @Setter @ToString
public class InvoiceEntity extends AbstractBaseDomain{
    private Long id;//
    private Long invoiceId;//进项税转出模块用于保存抵账表id
    private String invoiceType;//发票类型（01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票）
    private String invoiceCode;//发票代码
    private String invoiceNo;//发票号码
    private Date invoiceDate;//开票时间
    private String gfTaxNo;//购方税号
    private String gfName;//购方名称
    private String gfAddressAndPhone;//购方地址电话
    private String gfBankAndNo;//购方银行账号
    private String xfTaxNo;//销方税号
    private String xfName;//销方名称
    private String xfAddressAndPhone;//销方地址电话
    private String xfBankAndNo;//销方银行账号
    private Double invoiceAmount;//不含税金额（保留2位小数）
    private Double taxAmount;//发票税额（保留2位小数）
    private Double totalAmount;//价税合计（保留2位小数）
    private String remark;//发票备注
    private String invoiceStatus;//发票状态（0-正常  1-失控 2-作废  3-红冲 4-异常）
    private Date statusUpdateDate;//发票状态修改时间
    private Date lastUpdateDate;//发票状态最后修改时间
    private Date rzhDate;//认证时间
    private Date qsDate;//签收时间
    private String rzhBelongDateLate;//最晚认证归属期 yyyyMM
    private String rzhBelongDate;//认证归属期
    private String confirmUser;//确认人
    private Date confirmDate;//认证确认时间
    private String machinecode;//机器编号
    private String rzhType;//认证方式 1-勾选认证 2-扫描认证
    private String rzhYesorno;//是否认证 0-未认证 1-已认证
    private String gxUserAccount;//提交认证操作人账号
    private String gxUserName;//提交认证操作人
    private String sourceSystem;//底账来源（0-采集 1-查验）
    private String valid;//是否有效（0-有效 1-无效）
    private String uuid;//发票唯一字段 组成：发票代码+发票号码
    private Date createDate;//采集时间
    private Date gxDate;//勾选时间
    private String rzhBackMsg;//认证结果回传信息
    private String dqskssq;//当前税款所属期
    private String gxjzr;//当前税款所属期勾选截止日
    private String gxfwq;//当前税款所属期可勾选发票开票日期范围起
    private String gxfwz;//当前税款所属期可勾选发票开票日期范围止
    private String sfygx;//是否已勾选
    private String detailYesorno;//是否存入明细 0 无明细 1 有明细
    private String gxType;//勾选方式(0-手工勾选 1-扫码勾选 2-导入a选 3- 智能勾选 4-手工认证 5-扫码认证 6-导入认证)
    private String authStatus;//认证处理状态  0-未认证 1-已勾选未确认，2已确认 3 已发送认证 4 认证成功 5 认证失败
    private Date sendDate;//发送认证时间
    private String rzlx;//认证类型(1-抵扣 2-退税 3-代理退税   发票已认证、已勾选有该标签）
    private String sfdbts;//是否代办退税(0：否 1：是)
    private String qsType;//签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收5-pdf上传签收
    private String qsStatus;//签收结果（0-未签收 1-已签收）
    private String checkCode;//校验码
    private String txfbz;//通行费标志(Y-可抵扣通行费，N-不可抵扣通行费)
    private String lslbz;//零税率标志 (空:非零税率，1:税率栏位显示“免税”，2:税率栏位显示“不征收”，3:零税率
    private String outStatus;//转出状态 0-未转出   1-已转出
    private Double outInvoiceAmout;//转出金额
    private Double outTaxAmount;//转出税额
    private String outReason;//转出原因1-免税项目用 ；2-集体福利,个人消费；3-非正常损失；4-简易计税方法征税项目用；5-免抵退税办法不得抵扣的进项税额；6-纳税检查调减进项税额；7-红字专用发票通知单注明的进项税额；8-上期留抵税额抵减欠税
    private String outRemark;//转出备注
    private Date outDate;//转出日期
    private String outBy;//转出人
    private String qsBy;//签收人
    private String stringTotalAmount;//用来存储大写的价税合计

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}