package com.xforceplus.wapp.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT;

/**
 * 海关缴款书明细, 从BMS获取保存
 * @Author: ChenHang
 * @Date: 2023/7/4 15:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "t_dx_customs_detail")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TDxCustomsDetailEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 海关缴款书号码(税单号)
     */
    @ApiModelProperty("海关缴款书号码(税单号)")
    @TableField("customs_no")
    private String customsNo;

    @ApiModelProperty("科目")
    @TableField("payee_subject")
    private String payeeSubject;

    @ApiModelProperty("PO号")
    @TableField("contract_no")
    private String contractNo;

    @ApiModelProperty("报关单编号(从BMS获取返回)")
    @TableField("customs_doc_no")
    private String customsDocNo;

    @ApiModelProperty("完税价格(不含税金额)")
    @TableField("dutiable_price")
    private BigDecimal dutiablePrice;

    @ApiModelProperty("账单id")
    @TableField("bill_id")
    private String billId;

    @ApiModelProperty("货物名称")
    @TableField("material_desc")
    private String materialDesc;

    @ApiModelProperty("物料号")
    @TableField("material_id")
    private String materialId;

    @ApiModelProperty("税款金额(税额)")
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    @ApiModelProperty("填发日期")
    @TableField("paper_drew_date")
    private String paperDrewDate;

    @ApiModelProperty("税号")
    @TableField("company_tax_no")
    private String companyTaxNo;

    @ApiModelProperty("税率")
    @TableField("tax_rate")
    private BigDecimal taxRate;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time")
    private Date updateTime;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time")
    private Date createTime;

}
