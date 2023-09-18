package com.xforceplus.wapp.modules.customs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class CustomsQueryDto {

    @ApiModelProperty("海关缴款书号")
    private String customsNo;

    @ApiModelProperty("管理状态1-正常、0-非正常")
    private String manageStatus;

    @ApiModelProperty("数据来源")
    private Integer sourceType;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("公司税号")
    private String companyTaxNo;

    @ApiModelProperty("勾选状态")
    private String isCheck;

    @ApiModelProperty("起始填开日期yyyy-mm-dd")
    private String paperDrewDateStart;

    @ApiModelProperty("结束填开日期yyyy-mm-dd")
    private String paperDrewDateEnd;

    @ApiModelProperty("税款所属期")
    private String taxPeriod;

    @ApiModelProperty("起始勾选日期yyyy-mm-dd")
    private String checkTimeStart;

    @ApiModelProperty("结束勾选日期yyyy-mm-dd")
    private String checkTimeEnd;

    @ApiModelProperty("凭证号")
    private String voucherNo;

    @ApiModelProperty("入账状态02-入账（企业所得税税前扣除）03-入账（企业所得税不扣除）06-入账撤销；00-未入账、01-入账中、04入账失败、05入账撤销中")
    private String accountStatus;

    @ApiModelProperty("手工认证状态 1-手工认证")
    private String confirmStatus;

    @ApiModelProperty("比对状态 -1-比对失败 0-未比对 1-比对成功")
    private String billStatus;

    @ApiModelProperty("起始撤销勾选日期yyyy-mm-dd")
    private String unCheckTimeStart;

    @ApiModelProperty("结束撤销勾选日期yyyy-mm-dd")
    private String unCheckTimeEnd;

    @ApiModelProperty("PO单号")
    private String contractNo;

    @ApiModelProperty("报关单编号")
    private String customsDocNo;

    @ApiModelProperty("页数")
    private Integer pageSize=20;

    @ApiModelProperty("页码")
    private Integer pageNo=1;

    @ApiModelProperty("是否全选 0 未全选 1全选")
    private String isAllSelected;

    /**
     * 入账日期 起
     */
    @ApiModelProperty("凭证入账时间起")
    private String voucherAccountTimeStart;
    /**
     * 入账日期 止
     */
    @ApiModelProperty("凭证入账时间止")
    private String voucherAccountTimeEnd;
}
