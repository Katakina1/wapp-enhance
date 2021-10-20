package com.xforceplus.wapp.modules.exceptionreport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-20 11:16
 **/
@Setter
@Getter
@ApiModel
public class ExceptionReportDto {

    /**
     * 主键，雪花算法
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 例外CODE
     */
    @ApiModelProperty("例外代码")
    private String code;

    /**
     * 例外说明
     */
    @ApiModelProperty("例外说明")
    private String description;

    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String sellerName;

    /**
     * 供应商号
     */
    @ApiModelProperty("供应商号")
    private String sellerNo;

    /**
     * 机构名称，jv的名称，购方名称
     */
    @ApiModelProperty("购方名称")
    private String purchaserName;

    /**
     * jvcode,机构编码，购方编码
     */
    @ApiModelProperty("购方编码/机构编码/jvcode")
    private String purchaserNo;

    /**
     * 税率，整数
     */
    @ApiModelProperty("税率")
    private String taxRate;

    /**
     * 税码
     */
    @ApiModelProperty("税码")
    private String taxCode;

    /**
     * 单据号：索赔单号，协议单号，EPD单号
     */
    @ApiModelProperty("索赔单号，协议单号，EPD单号")
    private String billNo;

    /**
     * 协议类型编码
     */
    @ApiModelProperty("协议类型编码")
    private String agreementTypeCode;

    /**
     * 扣款日期
     */
    @ApiModelProperty("扣款日期")
    private Date deductDate;

    /**
     * 批次号
     */
    @ApiModelProperty("批次号")
    private String batchNo;


    /**
     * 文档类型
     */
    @ApiModelProperty("文档类型")
    private String documentType;

    /**
     * 协议供应商6D
     */
    @ApiModelProperty("协议供应商6D")
    private String agreementMemo;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;
}
