package com.xforceplus.wapp.modules.certification.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发票认证实体类
 * @author kevin.wang
 * @date 4/13/2018
 */
@Getter
@Setter
@ToString
public class InvoiceCertificationEntity {

    //税号权限-当前税号下的发票是否有底账信息
    private Boolean taxAccess;

    private List<Long> ids;

    private Integer count;
    //智能勾选操作状态
    private String flag;
    //发票流程类型（1：商品，2：费用）
    private String flowType;
    //host状态
    private String hostStatus;
    //companyCode
    private String companyCode;
    //凭证号
    private String certificateNo;
    //操作状态码
    private Integer code;

    //底账表Id
    private Long id;

    //发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
    private String invoiceType;

    //发票代码
    private String invoiceCode;

    //发票号码
    private String invoiceNo;

    //开票时间
    private String invoiceDate;

    //购方名称
    private String gfName;

    //购方代码
    private String jvcode;

    //销方名称
    private String xfName;

    //不含税金额
    private BigDecimal invoiceAmount;

    //发票税额
    private BigDecimal taxAmount;

    //价格合计
    private BigDecimal totalAmount;

    //发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常
    private String invoiceStatus;

    //发票状态修改时间
    private String  statusUpdateDate;

    //发票状态最后修改时间
    private String lastUpdateDate;

    //认证时间
    private String rzhDate;

    //签收时间
    private String qsDate;

    //认证确认时间
    private String confirmDate;

    //认证方式 1-勾选认证 2-扫描认证
    private String rzhType;

    //是否认证 0-未认证 1-已认证
    private String rzhYesOrNo;

    //提交认证操作人账号
    private String gxUserAccount;

    //提交认证操作人
    private String gxUserName;

    //是否有效 0-有效 1-无效
    private String valid;

    //勾选时间
    private String gxDate;

    //是否已勾选
    private String sfygx;

    //是否存入明细 0 无明细 1 有明细
    private String detailYesOrNo;

    //勾选方式(0-手工勾选 1-扫码勾选 2-导入勾选 3- 智能勾选 4-手工认证 5-扫码认证 6-导入认证)
    private String gxType;

    //认证处理状态  0-未认证 1-已勾选未确认，2已确认 3 已发送认证 4 认证成功 5 认证失败
    private String authStatus;

    //发送认证时间
    private String sendDate;

    //认证类型(1-抵扣 2-退税 3-代理退税   发票已认证、已勾选有该标签）
    private String rzlx;

    //签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收
    private String qsType;

    //签收结果（0-未签收 1-已签收）
    private String  qsStatus;

    //校验码
    private String checkCode;

    //认证结果回传信息
    private String rzhBackMsg;

    //当前税款所属期--取自t_dx_tax_current表
    private String currentTaxPeriod;

    //当前税款所属期勾选截止日
    private String gxjzr;

    //当前税款所属期可勾选发票开票日期范围起
    private String gxfwq;

    //当前税款所属期可勾选发票开票日期范围止
    private String gxfwz;

    //最晚认证归属期 yyyyMM
    private String rzhBelongDateLate;

    //实际认证归属期 yyyyMM
    private String rzhBelongDate;

    private String scanCode;
    private String venderid;
    private String rownumber;

}
