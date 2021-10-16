package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xforceplus.wapp.repository.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
/**
 * <p>
    * 匹配表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_dx_match")
public class TDxMatchEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 匹配状态：0未匹配、1预匹配（没有发票）、2部分匹配、3完全匹配、4差异匹配、5匹配失败、6取消匹配
     */
    @TableField("match_status")
    private String matchStatus;

    /**
     * 匹配表主键
     */
    @TableField("id")
    private Integer id;

    /**
     * 匹配关联号
     */
    @TableField("matchno")
    private String matchno;

    /**
     * 供应商号
     */
    @TableField("venderid")
    private String venderid;

    /**
     * 取消匹配原因
     */
    @TableField("reason_for_cancel")
    private String reasonForCancel;

    /**
     * 结算金额
     */
    @TableField("settlementamount")
    private Double settlementamount;

    /**
     * 匹配备注
     */
    @TableField("match_remarks")
    private String matchRemarks;

    /**
     * po总金额
     */
    @TableField("po_amount")
    private Double poAmount;

    /**
     * 索赔总金额
     */
    @TableField("claim_amount")
    private Double claimAmount;

    /**
     * 匹配日期
     */
    @TableField("match_date")
    private Date matchDate;

    /**
     * 购方名称
     */
    @TableField("gf_name")
    private String gfName;

    /**
     * host状态
     */
    @TableField("host_status")
    private String hostStatus;

    /**
     * po单数量
     */
    @TableField("po_num")
    private Integer poNum;

    /**
     * 索赔单数量
     */
    @TableField("claim_num")
    private Integer claimNum;

    /**
     * 发票数量
     */
    @TableField("invoice_num")
    private Integer invoiceNum;

    /**
     * 发票金额
     */
    @TableField("invoice_amount")
    private Double invoiceAmount;

    /**
     * 购方税号；
     */
    @TableField("gf_taxno")
    private String gfTaxno;

    /**
     * 打印号
     */
    @TableField("printcode")
    private String printcode;

    /**
     * 扫描匹配状态  0--未扫描匹配，1--扫描匹配成功，2--扫描匹配失败 
     */
    @TableField("scan_match_status")
    private String scanMatchStatus;

    /**
     * 匹配差额
     */
    @TableField("match_cover")
    private Double matchCover;

    /**
     * 扫描匹配失败原因
     */
    @TableField("scan_fail_reason")
    private String scanFailReason;

    /**
     * 0:未在失败报告退票1:已经退票
     */
    @TableField("isdel")
    private String isdel;

    /**
     * 匹配类型 1 电票 为空 纸票
     */
    @TableField("match_type")
    private String matchType;


    public static final String MATCH_STATUS = "match_status";

    public static final String ID = "id";

    public static final String MATCHNO = "matchno";

    public static final String VENDERID = "venderid";

    public static final String REASON_FOR_CANCEL = "reason_for_cancel";

    public static final String SETTLEMENTAMOUNT = "settlementamount";

    public static final String MATCH_REMARKS = "match_remarks";

    public static final String PO_AMOUNT = "po_amount";

    public static final String CLAIM_AMOUNT = "claim_amount";

    public static final String MATCH_DATE = "match_date";

    public static final String GF_NAME = "gf_name";

    public static final String HOST_STATUS = "host_status";

    public static final String PO_NUM = "po_num";

    public static final String CLAIM_NUM = "claim_num";

    public static final String INVOICE_NUM = "invoice_num";

    public static final String INVOICE_AMOUNT = "invoice_amount";

    public static final String GF_TAXNO = "gf_taxno";

    public static final String PRINTCODE = "printcode";

    public static final String SCAN_MATCH_STATUS = "scan_match_status";

    public static final String MATCH_COVER = "match_cover";

    public static final String SCAN_FAIL_REASON = "scan_fail_reason";

    public static final String ISDEL = "isdel";

    public static final String MATCH_TYPE = "match_type";

}
