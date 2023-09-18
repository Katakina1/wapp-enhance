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
    * 企业税务信息表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2022-11-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@TableName(value="t_dx_tax_current")
public class TDxTaxCurrentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 税号
     */
    @TableField("taxno")
    private String taxno;

    /**
     * 企业名称
     */
    @TableField("taxname")
    private String taxname;

    /**
     * 当前税款所属期
     */
    @TableField("current_tax_period")
    private String currentTaxPeriod;

    /**
     * 当前税款所属期可勾选发票的起始开票日期
     */
    @TableField("select_start_date")
    private String selectStartDate;

    /**
     * 当前税款所属期可勾选发票的截止开票日期
     */
    @TableField("select_end_date")
    private String selectEndDate;

    /**
     * 当前税款所属期可勾选发票操作截止日期
     */
    @TableField("operation_end_date")
    private String operationEndDate;

    /**
     * 企业旧税号
     */
    @TableField("old_tax_no")
    private String oldTaxNo;

    /**
     * 申报周期，值为3-季度/1-月
     */
    @TableField("declare_period")
    private String declarePeriod;

    /**
     * 信用等级，值为A/B/C/D或者空
     */
    @TableField("credit_rating")
    private String creditRating;

    @TableField(value="update_time", update="getdate()" )
    private Date updateTime;

    @TableField("create_time")
    private Date createTime;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    public static final String TAXNO = "taxno";

    public static final String TAXNAME = "taxname";

    public static final String CURRENT_TAX_PERIOD = "current_tax_period";

    public static final String SELECT_START_DATE = "select_start_date";

    public static final String SELECT_END_DATE = "select_end_date";

    public static final String OPERATION_END_DATE = "operation_end_date";

    public static final String OLD_TAX_NO = "old_tax_no";

    public static final String DECLARE_PERIOD = "declare_period";

    public static final String CREDIT_RATING = "credit_rating";

    public static final String UPDATE_TIME = "update_time";

    public static final String CREATE_TIME = "create_time";

    public static final String ID = "id";

}
