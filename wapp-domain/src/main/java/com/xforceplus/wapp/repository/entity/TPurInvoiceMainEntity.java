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
    * 
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2022-10-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_pur_invoice_main")
public class TPurInvoiceMainEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("tax_amount")
    private String taxAmount;

    @TableField("invoice_type")
    private String invoiceType;

    @TableField("invoice_no")
    private String invoiceNo;

    @TableField("last_update_date")
    private Date lastUpdateDate;

    @TableField("invoice_with_tax")
    private String invoiceWithTax;

    @TableField("invoice_with_out_amount")
    private String invoiceWithOutAmount;

    @TableField("supplier_name")
    private String supplierName;

    @TableField("create_date")
    private Date createDate;

    @TableField("purchaser_no")
    private String purchaserNo;

    @TableField("id")
    private Integer id;

    @TableField("invoice_status")
    private String invoiceStatus;

    @TableField("purchaser_name")
    private String purchaserName;

    @TableField("company_code")
    private String companyCode;

    @TableField("invoice_date")
    private String invoiceDate;

    @TableField("invoice_code")
    private String invoiceCode;

    @TableField("period")
    private String period;

    @TableField("company_name")
    private String companyName;

    @TableField("invoice_amount")
    private String invoiceAmount;

    @TableField("supplier_code")
    private String supplierCode;


    public static final String TAX_AMOUNT = "tax_amount";

    public static final String INVOICE_TYPE = "invoice_type";

    public static final String INVOICE_NO = "invoice_no";

    public static final String LAST_UPDATE_DATE = "last_update_date";

    public static final String INVOICE_WITH_TAX = "invoice_with_tax";

    public static final String INVOICE_WITH_OUT_AMOUNT = "invoice_with_out_amount";

    public static final String SUPPLIER_NAME = "supplier_name";

    public static final String CREATE_DATE = "create_date";

    public static final String PURCHASER_NO = "purchaser_no";

    public static final String ID = "id";

    public static final String INVOICE_STATUS = "invoice_status";

    public static final String PURCHASER_NAME = "purchaser_name";

    public static final String COMPANY_CODE = "company_code";

    public static final String INVOICE_DATE = "invoice_date";

    public static final String INVOICE_CODE = "invoice_code";

    public static final String PERIOD = "period";

    public static final String COMPANY_NAME = "company_name";

    public static final String INVOICE_AMOUNT = "invoice_amount";

    public static final String SUPPLIER_CODE = "supplier_code";

}
