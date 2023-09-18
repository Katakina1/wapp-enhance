package com.xforceplus.wapp.modules.customs.dto;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT;

/**
 * 海关缴款书明细, 从BMS获取保存
 * @Author: ChenHang
 * @Date: 2023/7/4 15:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomsDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 海关缴款书号码(税单号)
     */
    @ApiModelProperty("海关缴款书号码(税单号)")
    private String customsNo;

    @ApiModelProperty("科目")
    private String payeeSubject;

    @ApiModelProperty("PO号")
    private String contractNo;

    @ApiModelProperty("报关单编号(从BMS获取返回)")
    private String customsDocNo;

    @ApiModelProperty("完税价格")
    private BigDecimal dutiablePrice;

    @ApiModelProperty("账单id")
    private String billId;

    @ApiModelProperty("货物名称")
    private String materialDesc;

    @ApiModelProperty("物料号")
    private String materialId;

    @ApiModelProperty("税款金额")
    private BigDecimal taxAmount;

    @ApiModelProperty("填发日期")
    private String paperDrewDate;

    @ApiModelProperty("税号")
    private String companyTaxNo;

    @ApiModelProperty("税率")
    private BigDecimal taxRate;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

}
