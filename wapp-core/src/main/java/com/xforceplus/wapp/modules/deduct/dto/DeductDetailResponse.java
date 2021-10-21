package com.xforceplus.wapp.modules.deduct.dto;

import com.xforceplus.wapp.modules.deduct.model.DeductBillItemModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by SunShiyong on 2021/10/21.
 */
@ApiModel("业务单详情对象")
@Data
public class DeductDetailResponse {

    @ApiModelProperty("业务单明细列表")
    private List<DeductBillItemModel> deductBillItemList;

    /**
     * 业务单据编号
     */
    @ApiModelProperty("业务单据编号")
    private String businessNo;
    /**
     * 业务单据类型;1:索赔;2:协议;3:EPD
     */
    @ApiModelProperty("业务单据类型;1:索赔;2:协议;3:EPD")
    private Integer businessType;
    /**
     * 供应商编号
     */
    @ApiModelProperty("供应商编号")
    private String sellerNo;
    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String sellerName;
    /**
     * 扣款日期
     */
    @ApiModelProperty("扣款日期")
    private Date deductDate;
    /**
     * 门店编码
     */
    @ApiModelProperty("门店编码")
    private String purchaserNo;

    @ApiModelProperty("协议供应商6D")
    private String agreementMemo;

    @ApiModelProperty("协议类型")
    private String agreementDocumentType;

    @ApiModelProperty("协议类型编码")
    private String agreementReasonCode;

    /**
     * 含税金额
     */
    @ApiModelProperty("含税金额")
    private BigDecimal amountWithTax;
    /**
     * 税率
     */
    @ApiModelProperty("税率")
    private BigDecimal taxRate;

    @ApiModelProperty("定案日期")
    private Date verdictDate;

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("发票类型")
    private String invoiceType;




}
