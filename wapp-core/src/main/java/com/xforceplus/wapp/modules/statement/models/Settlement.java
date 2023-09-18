package com.xforceplus.wapp.modules.statement.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductBaseResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("结算单信息")
public class Settlement {
    @ApiModelProperty("唯一id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty("业务单号")
    private String businessNo;

    /**
     * 红字信息表编号
     */
    @ApiModelProperty("红字信息表编号")
    private String redNotificationNo;

    @ApiModelProperty("销方编码，供应商编码")
    private String sellerNo;

    @ApiModelProperty("供应商名称")
    private String sellerName;

    @ApiModelProperty("供应商税号")
    private String sellerTaxNo;

    @ApiModelProperty("供应商编码")
    private String sellerTel;

    @ApiModelProperty("供应商地址")
    private String sellerAddress;

    @ApiModelProperty("供应商开户行名称")
    private String sellerBankName;

    @ApiModelProperty("供应商银行账号")
    private String sellerBankAccount;

    @ApiModelProperty("购方编码")
    private String purchaserNo;

    @ApiModelProperty("购方名称")
    private String purchaserName;

    @ApiModelProperty("购方税号")
    private String purchaserTaxNo;

    @ApiModelProperty("购方电话")
    private String purchaserTel;

    @ApiModelProperty("购方地址")
    private String purchaserAddress;

    @ApiModelProperty("购方开户行")
    private String purchaserBankName;

    @ApiModelProperty("购方银行账号")
    private String purchaserBankAccount;

    @ApiModelProperty("发票类型:01.专纸、03.机动车、04.普纸、08.专电、10.普电、11.卷票、14.通行费电票")
    private String invoiceType;

    @ApiModelProperty("含税金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithTax;

    @ApiModelProperty("不含税金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amountWithoutTax;

    @ApiModelProperty("税额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal taxAmount;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("可匹配余额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal availableAmount;

    @ApiModelProperty("批次号（沃尔玛数据同步批次）")
    private String batchNo;

    @ApiModelProperty("结算单编码")
    private String settlementNo;

    @ApiModelProperty("结算单类型:1.索赔、2.协议单、3.EPD")
    private Integer settlementType;

    @ApiModelProperty("结算单状态:1.待确认、2.待开票、3.已开部票、4.已开票、5.已完成、6.待审核、7.已撤销")
    private Integer settlementStatus;

    @ApiModelProperty("税率")
    private BigDecimal taxRate;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("撤销原因")
    private String revertRemark;

    @ApiModelProperty("审核状态 1-待审核 2-审核通过 3-审核不通过")
    private Integer approveStatus;

    @ApiModelProperty("审核类型 1-开票金额修改 2-金额有误 3-蓝冲")
    private Integer approveType;

    @ApiModelProperty("审核备注")
    private String approveRemark;

    @ApiModelProperty("审核时间")
    private Long approveTime;

    @ApiModelProperty("提交审核时间")
    private Long approveRequestTime;

    @ApiModelProperty("tab页标签 00：全部；01:待确认;02:待开票;03:部分开票;04:已开票;05:已完成;06:待审核;07:已撤销")
    private QueryTabResp queryTab;

    @Data
    @AllArgsConstructor
    public static class QueryTabResp {
        private String code;
        private String message;
    }
}