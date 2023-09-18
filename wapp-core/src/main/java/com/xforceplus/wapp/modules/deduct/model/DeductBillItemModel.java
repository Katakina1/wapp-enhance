package com.xforceplus.wapp.modules.deduct.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by SunShiyong on 2021/10/21.
 */
@ApiModel("业务单明细对象")
@Data
public class DeductBillItemModel {
    @ApiModelProperty("业务单明细id")
    private Long id;

    @ApiModelProperty("业务单据编号")
    private String businessNo;

    @ApiModelProperty("商品编码")
    private String itemNo;

    @ApiModelProperty("商品名称")
    private String cnDesc;

    @ApiModelProperty("税收分类编码")
    private String goodsTaxNo;

    @ApiModelProperty("数量")
    private BigDecimal quantity;

    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    @ApiModelProperty("含税金额")
    private BigDecimal amountWithTax;

    @ApiModelProperty("税率")
    private BigDecimal taxRate;

    @ApiModelProperty("税额")
    private BigDecimal taxAmount;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("规格型号")
    private String itemSpec;

    @ApiModelProperty("红字信息表状态（0：无需申请；1：待申请；2：已申请；3：申请中；4：申请失败；5：撤销中；6：撤销失败；7：已撤销）")
    private List<Integer> redNotificationStatus;

    @ApiModelProperty("红字信息表编号列表")
    private List<String> redNotificationNos;

    @ApiModelProperty("历史数据红字编号填充标识")
    private Boolean fullHistoryFlag = Boolean.FALSE;


}
