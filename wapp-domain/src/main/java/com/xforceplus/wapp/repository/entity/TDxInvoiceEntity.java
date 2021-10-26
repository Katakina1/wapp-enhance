package com.xforceplus.wapp.repository.entity;

import java.math.BigDecimal;
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
    * 发票扫描表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_dx_invoice")
public class TDxInvoiceEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 发票类型（01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票）
     */
    @TableField("invoice_type")
    private String invoiceType;

    /**
     * 发票代码
     */
    @TableField("invoice_code")
    private String invoiceCode;

    /**
     * 发票号码
     */
    @TableField("invoice_no")
    private String invoiceNo;

    /**
     * 扫描流水号
     */
    @TableField("invoice_serial_no")
    private String invoiceSerialNo;

    /**
     * 购方税号
     */
    @TableField("gf_tax_no")
    private String gfTaxNo;

    /**
     * 购方名称
     */
    @TableField("gf_name")
    private String gfName;

    /**
     * 销方税号
     */
    @TableField("xf_tax_no")
    private String xfTaxNo;

    /**
     * 销方名称
     */
    @TableField("xf_name")
    private String xfName;

    /**
     * 金额
     */
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    /**
     * 价税合计
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 开票时间
     */
    @TableField("invoice_date")
    private Date invoiceDate;

    /**
     * 扫描账号
     */
    @TableField("user_account")
    private String userAccount;

    /**
     * 扫描人
     */
    @TableField("user_name")
    private String userName;

    /**
     * 签收方式（0-扫码签收 1-扫描仪签收 2-app签收 3-导入签收 4-手工签收，5-pdf上传）
     */
    @TableField("qs_type")
    private String qsType;

    /**
     * 签收结果(0-签收失败 1-签收成功）
     */
    @TableField("qs_status")
    private String qsStatus;

    /**
     * 是否有效（1-有效 0-无效）
     */
    @TableField("valid")
    private String valid;

    /**
     * 发票唯一标识 组成：发票代码+发票号码
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 创建时间
     */
    @TableField("create_date")
    private Date createDate;

    /**
     * 修改时间
     */
    @TableField("update_date")
    private Date updateDate;

    /**
     * 签收时间
     */
    @TableField("qs_date")
    private Date qsDate;

    /**
     * 扫描图片唯一识别码
     */
    @TableField("scan_id")
    private String scanId;

    /**
     * 扫描备注
     */
    @TableField("notes")
    private String notes;

    /**
     * 校验码
     */
    @TableField("check_code")
    private String checkCode;

    /**
     * 供应商名称
     */
    @TableField("vendername")
    private String vendername;

    /**
     * 装订号
     */
    @TableField("bbindingno")
    private String bbindingno;

    /**
     * 装箱号
     */
    @TableField("packingno")
    private String packingno;

    /**
     * 装订日期
     */
    @TableField("bbinding_date")
    private Date bbindingDate;

    /**
     * 是否录入装订号（0-未装订，1-已装订）
     */
    @TableField("bindyesorno")
    private String bindyesorno;

    /**
     * 是否录入装箱号（0-未录入，1-已录入）
     */
    @TableField("packyesorno")
    private String packyesorno;

    /**
     * 生成退单号日期
     */
    @TableField("rebate_date")
    private Date rebateDate;

    /**
     * 是否生成退单号（0-未生成，1-已生成）
     */
    @TableField("rebateyesorno")
    private String rebateyesorno;

    /**
     * 是否录入邮包号（0-未录入，1-已录入）
     */
    @TableField("expressnoyesorno")
    private String expressnoyesorno;

    /**
     * 供应商号
     */
    @TableField("venderid")
    private String venderid;

    /**
     * 退单号
     */
    @TableField("rebateno")
    private String rebateno;

    /**
     * 邮包号
     */
    @TableField("rebate_expressno")
    private String rebateExpressno;

    /**
     * 退票理由
     */
    @TableField("refund_notes")
    private String refundNotes;

    /**
     * 是否整组退票（0-未整组退，1-已整组退）
     */
    @TableField("refundyesorno")
    private String refundyesorno;

    /**
     * 打印发票代码
     */
    @TableField("dy_invoice_code")
    private String dyInvoiceCode;

    /**
     * 打印发票号码
     */
    @TableField("dy_invoice_no")
    private String dyInvoiceNo;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 删除时间
     */
    @TableField("del_date")
    private Date delDate;

    /**
     * 退票原因
     */
    @TableField("refund_reason")
    private String refundReason;

    /**
     * 开票时间
     */
    @TableField("make_date")
    private Date makeDate;

    /**
     * 是否从页面导入  0--是  1 --否
     */
    @TableField("is_import")
    private String isImport;

    /**
     * 是否有印章
     */
    @TableField("is_exist_stamper")
    private String isExistStamper;

    /**
     * 无印章描述
     */
    @TableField("no_exist_stamper_notes")
    private String noExistStamperNotes;

    /**
     * 费用封面组号
     */
    @TableField("cost_no")
    private String costNo;

    /**
     * 申请单号
     */
    @TableField("eps_no")
    private String epsNo;

    /**
     * 属于
     */
    @TableField("belongs_to")
    private String belongsTo;

    @TableField("ariba_confirm_date")
    private Date aribaConfirmDate;

    @TableField("flow_type")
    private String flowType;

    @TableField("company_code")
    private String companyCode;

    @TableField("venderid_edit")
    private String venderidEdit;

    @TableField("isdel")
    private String isdel;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("mail_date")
    private Date mailDate;

    @TableField("jv_code")
    private String jvCode;

    @TableField("mail_company")
    private String mailCompany;

    @TableField("ariba_confirm_status")
    private String aribaConfirmStatus;

    @TableField("refund_code")
    private String refundCode;


    public static final String INVOICE_TYPE = "invoice_type";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String INVOICE_NO = "invoice_no";

    public static final String INVOICE_SERIAL_NO = "invoice_serial_no";

    public static final String GF_TAX_NO = "gf_tax_no";

    public static final String GF_NAME = "gf_name";

    public static final String XF_TAX_NO = "xf_tax_no";

    public static final String XF_NAME = "xf_name";

    public static final String INVOICE_AMOUNT = "invoice_amount";

    public static final String TAX_AMOUNT = "tax_amount";

    public static final String TOTAL_AMOUNT = "total_amount";

    public static final String INVOICE_DATE = "invoice_date";

    public static final String USER_ACCOUNT = "user_account";

    public static final String USER_NAME = "user_name";

    public static final String QS_TYPE = "qs_type";

    public static final String QS_STATUS = "qs_status";

    public static final String VALID = "valid";

    public static final String UUID = "uuid";

    public static final String CREATE_DATE = "create_date";

    public static final String UPDATE_DATE = "update_date";

    public static final String QS_DATE = "qs_date";

    public static final String SCAN_ID = "scan_id";

    public static final String NOTES = "notes";

    public static final String CHECK_CODE = "check_code";

    public static final String VENDERNAME = "vendername";

    public static final String BBINDINGNO = "bbindingno";

    public static final String PACKINGNO = "packingno";

    public static final String BBINDING_DATE = "bbinding_date";

    public static final String BINDYESORNO = "bindyesorno";

    public static final String PACKYESORNO = "packyesorno";

    public static final String REBATE_DATE = "rebate_date";

    public static final String REBATEYESORNO = "rebateyesorno";

    public static final String EXPRESSNOYESORNO = "expressnoyesorno";

    public static final String VENDERID = "venderid";

    public static final String REBATENO = "rebateno";

    public static final String REBATE_EXPRESSNO = "rebate_expressno";

    public static final String REFUND_NOTES = "refund_notes";

    public static final String REFUNDYESORNO = "refundyesorno";

    public static final String DY_INVOICE_CODE = "dy_invoice_code";

    public static final String DY_INVOICE_NO = "dy_invoice_no";

    public static final String FILE_TYPE = "file_type";

    public static final String DEL_DATE = "del_date";

    public static final String REFUND_REASON = "refund_reason";

    public static final String MAKE_DATE = "make_date";

    public static final String IS_IMPORT = "is_import";

    public static final String IS_EXIST_STAMPER = "is_exist_stamper";

    public static final String NO_EXIST_STAMPER_NOTES = "no_exist_stamper_notes";

    public static final String COST_NO = "cost_no";

    public static final String EPS_NO = "eps_no";

    public static final String BELONGS_TO = "belongs_to";

    public static final String ARIBA_CONFIRM_DATE = "ariba_confirm_date";

    public static final String FLOW_TYPE = "flow_type";

    public static final String COMPANY_CODE = "company_code";

    public static final String VENDERID_EDIT = "venderid_edit";

    public static final String ISDEL = "isdel";

    public static final String ID = "id";

    public static final String MAIL_DATE = "mail_date";

    public static final String JV_CODE = "jv_code";

    public static final String MAIL_COMPANY = "mail_company";

    public static final String ARIBA_CONFIRM_STATUS = "ariba_confirm_status";

    public static final String REFUND_CODE = "refund_code";

}
