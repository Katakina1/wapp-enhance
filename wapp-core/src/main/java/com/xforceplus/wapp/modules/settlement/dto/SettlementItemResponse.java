package com.xforceplus.wapp.modules.settlement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Describe: 结算单明细 响应
 *
 * @Author xiezhongyong
 * @Date 2022/9/16
 */
@Data
public class SettlementItemResponse{


    @ApiModelProperty("结算单编码")
    private String settlementNo;

    @ApiModelProperty("明细编码")
    private String salesbillItemNo;

    @ApiModelProperty("明细代码")
    private String itemCode;

    @ApiModelProperty("明细名称")
    private String itemName;

    @ApiModelProperty("商品简称-税编简称")
    private String itemShortName;

    @ApiModelProperty("税编名称")
    private String taxName;

    @ApiModelProperty("规格型号")
    private String itemSpec;

    @ApiModelProperty("含税单价")
    private BigDecimal unitPriceWithTax;

    @ApiModelProperty("单价")
    private BigDecimal unitPrice;

    @ApiModelProperty("数量")
    private BigDecimal quantity;

    @ApiModelProperty("单位")
    private String quantityUnit;

    @ApiModelProperty("含税金额")
    private BigDecimal amountWithTax;

    @ApiModelProperty("不含税金额")
    private BigDecimal amountWithoutTax;

    @ApiModelProperty("税额")
    private BigDecimal taxAmount;

    @ApiModelProperty("税率 目前整数存储，需要程序单独处理\n" +
            "1---1%\n" +
            "9---9%")
    private BigDecimal taxRate;

    @ApiModelProperty("是否享受税收优惠政策 0 - 不 1- 是")
    private String taxPre;

    @ApiModelProperty("优惠政策内容")
    private String taxPreCon;

    @ApiModelProperty("零税率标志 空 - 非0税率，0-出口退税，1-免税，2-不征税，3-普通0税率")
    private String zeroTax;

    @ApiModelProperty("税收分类编码")
    private String goodsTaxNo;

    @ApiModelProperty("编码版本号")
    private String goodsNoVer;

    @ApiModelProperty("已开票 未开票 ")
    private Integer itemStatus;

    @ApiModelProperty("0 正常 1 待匹配税编 2 待确认金额")
    private Integer itemFlag;

    @ApiModelProperty("第三方ID(索赔明细Id、蓝票明细ID)")
    private Long thridId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("数据ID")
    private Long id;

    @ApiModelProperty("更新用户")
    private Long updateUser;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建用户")
    private Long createUser;

    @ApiModelProperty("是否成品油")
    private Integer isOil;


    @ApiModelProperty("红字信息表状态（0：无需申请；1：待申请；2：已申请；3：申请中；4：申请失败；5：撤销中；6：撤销失败；7：已撤销）")
    private List<Integer> redNotificationStatus;

    @ApiModelProperty("红字信息表编号列表")
    private List<String> redNotificationNos;

}
