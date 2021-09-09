package com.xforceplus.wapp.modules.monitearly.entity;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;
import static com.google.common.base.MoreObjects.toStringHelper;



/**
 * 抵账表 实体类
 * Created by alfred.zong on 2018/04/12.
 */
@Setter
@Getter
public final class RecordInvoiceEntity implements Serializable {

    private static final long serialVersionUID = -8722120875736521594L;

    // 主键ID
    private Integer id;

    // 发票类型
    private String invoiceType;

    // 发票代码
    private String invoiceCode;

    // 发票号码
    private String invoiceNo;

    // 开票日期
    private Date invoiceDate;

    // 购方税号
    private String gfTaxNo;

    // 购方名称
    private String gfName;

    // 购房地址电话号码
    private String gfAddressAndPhone;

    // 购方开户行及账号
    private String gfBankAndNo;

    // 销方税号
    private String xfTaxNo;

    // 销方名称
    private String xfName;

    // 销方地址及电话
    private String xfAddressAndPhone;

    // 销方开户行及账号
    private String xfBankAndNo;

    // 金额
    private String invoiceAmount;

    // 税额
    private String taxAmount;

    // 价格合计
    private String totalAmount;

    // 备注
    private String remark;

    // 发票状态 0-正常  1-失控 2-作废  3-红冲 4-异常
    private String invoiceStatus;

    // 发票状态修改时间
    private Date statusUpdateDate;

    // 发票状态最后修改时间
    private String lastUpdateDate;

    // 认证时间
    private String rzhDate;

    // 签收时间
    private Date qsDate;

    // 最晚认证归属期 yyyyMM
    private String rzhBelongDateLate;

    // 实际认证归属期 yyyyMM
    private String rzhBelongDate;

    // 认证确认时间
    private Date confirmDate;

    // 认证方式 1-勾选认证 2-扫描认证
    private String rzhType;

    // 是否认证 0-未认证 1-已认证
    private String rzhYesorno;

    // 提交认证操作人账号
    private String gxUserAccount;

    // 提交认证操作人
    private String gxUserName;

    // 底账来源  0-采集 1-查验
    private String sourceSystem;

    // 是否有效 0-有效 1-无效
    private String valid;

    // 唯一索引 防重复
    private String uuid;

    // 采集时间
    private Date createDate;

    // 勾选时间
    private Date gxDate;

    // 认证结果回传信息
    private String rzhBackMsg;

    // 当前税款所属期
    private String dqskssq;

    // 当前税款所属期勾选截止日
    private String gxjzr;

    // 当前税款所属期可勾选发票开票日期范围起
    private String gxfwq;

    // 当前税款所属期可勾选发票开票日期范围止
    private String gxfwz;

    // 是否勾选
    private String sfygx;

    // 是否存入明细 0 无明细 1 有明细
    private String detailYesorno;

    // 勾选方式(0-手工勾选 1-扫码勾选 2-导入勾选 3- 智能勾选 4-手工认证 5-扫码认证 6-导入认证)
    private String gxType;

    // 认证处理状态  0-未认证 1-已勾选未确认，2已确认 3 已发送认证 4 认证成功 5 认证失败
    private String authStatus;

    // 当发送认证时间
    private String sendDate;

    // 认证类型(1-抵扣 2-退税 3-代理退税   发票已认证、已勾选有该标签）
    private String rzlx;

    // 是否代办退税(0：否 1：是)
    private String sfdbts;

    // 签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收5-pdf上传签收
    private String qsType;

    // 签收结果（0-未签收 1-已签收）
    private String qsStatus;

    // 校验码
    private String checkCode;

    // 通行费标志(Y-可抵扣通行费，N-不可抵扣通行费)
    private String txfbz;

    // 零税率标志 (空:非零税率，1:税率栏位显示“免税”，2:税率栏位显示“不征收”，3:零税率
    private String lslbz;

    // 转出状态 0-未转出   1-已转出
    private String outStatus;

    // 转出金额
    private Double outInvoiceAmout;

    // 转出税额
    private String outTaxAmount;

    /**
     * 转出原因1-免税项目用 ；2-集体福利,个人消费；3-非正常损失；4-简易计税方法征税项目用；
     * 5-免抵退税办法不得抵扣的进项税额；6-纳税检查调减进项税额；7-红字专用发票通知单注明的进项税额；
     * 8-上期留抵税额抵减欠税
     */
    private String outReason;

    // 转出备注
    private String outRemark;

    // 转出日期
    private Date outDate;

    //最后认证截止日
    private Date cutApproveDate;

    //价格合计中文大写字段
    private String chainTotalAmount;

    @Override
    public String toString() {
        return toStringHelper(this).
                add("id", id).
                add("invoiceType", invoiceType).
                add("invoiceCode", invoiceCode).
                add("invoiceNo", invoiceNo).
                add("invoiceDate", invoiceDate).
                add("gfTaxNo", gfTaxNo).
                add("gfName", gfName).
                add("gfAddressAndPhone", gfAddressAndPhone).
                add("gfBankAndNo", gfBankAndNo).
                add("xfTaxNo", xfTaxNo).
                add("xfName", xfName).
                add("xfAddressAndPhone", xfAddressAndPhone).
                add("xfBankAndNo", xfBankAndNo).
                add("invoiceAmount", invoiceAmount).
                add("taxAmount", taxAmount).
                add("totalAmount", totalAmount).
                add("remark", remark).
                add("invoiceStatus", invoiceStatus).
                add("statusUpdateDate", statusUpdateDate).
                add("lastUpdateDate", lastUpdateDate).
                add("rzhDate", rzhDate).
                add("qsDate", qsDate).
                add("rzhBelongDateLate", rzhBelongDateLate).
                add("rzhBelongDate", rzhBelongDate).
                add("confirmDate", confirmDate).
                add("rzhType", rzhType).
                add("rzhYesorno", rzhYesorno).
                add("gxUserAccount", gxUserAccount).
                add("gxUserName", gxUserName).
                add("sourceSystem", sourceSystem).
                add("valid", valid).
                add("uuid", uuid).
                add("createDate", createDate).
                add("gxDate", gxDate).
                add("rzhBackMsg", rzhBackMsg).
                add("dqskssq", dqskssq).
                add("gxjzr", gxjzr).
                add("gxfwq", gxfwq).
                add("gxfwz", gxfwz).
                add("sfygx", sfygx).
                add("detailYesorno", detailYesorno).
                add("gxType", gxType).
                add("authStatus", authStatus).
                add("sendDate", sendDate).
                add("rzlx", rzlx).
                add("sfdbts", sfdbts).
                add("qsType", qsType).
                add("qsStatus", qsStatus).
                add("checkCode", checkCode).
                add("txfbz", txfbz).
                add("lslbz", lslbz).
                add("outStatus", outStatus).
                add("outInvoiceAmout", outInvoiceAmout).
                add("outTaxAmount", outTaxAmount).
                add("outReason", outReason).
                add("outRemark", outRemark).
                add("outDate", outDate).
                add("cutApproveDate", cutApproveDate).
                add("chainTotalAmount", chainTotalAmount).
                toString();
    }
}
