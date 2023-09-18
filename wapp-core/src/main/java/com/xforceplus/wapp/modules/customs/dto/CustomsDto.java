package com.xforceplus.wapp.modules.customs.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
public class CustomsDto {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("缴款书号码")
    private String customsNo;

    @ApiModelProperty("公司税号")
    private String companyTaxNo;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("填发日期")
    private String paperDrewDate;

    @ApiModelProperty("税款金额")
    private String taxAmount;

    @ApiModelProperty("有效抵扣税款金额")
    private String effectiveTaxAmount;

    @ApiModelProperty("勾选状态 -1 - 撤销勾选失败 0-撤销勾选中 1-不可勾选  2-未勾选 3-勾选中4-已勾选 5-勾选失败  6-抵扣异常 8-已确认抵扣 9-撤销勾选成功(属地使用)")
    private String isCheck;

    @ApiModelProperty("勾选时间")
    private Date checkTime;

    @ApiModelProperty("用途")
    private String checkPurpose;

    @ApiModelProperty("认证备注")
    private String authRemark;

    @ApiModelProperty("勾选人Id")
    private String checkUserId;

    @ApiModelProperty("勾选人")
    private String checkUserName;

    @ApiModelProperty("管理状态1-正常、0-非正常")
    private String manageStatus;

    @ApiModelProperty("统计状态 待申请统计、统计中、统计完成、撤销统计中、撤销统计失败")
    private String countStatus;

    @ApiModelProperty("统计时间")
    private Date countTime;

    @ApiModelProperty("签名确认状态 确认中、已确认、撤销确认中、撤销确认失败")
    private String signStatus;

    @ApiModelProperty("签名确认时间")
    private Date signTime;

    @ApiModelProperty("02-已入账企业所得税前扣除、03-已入账所得税不扣除、06入账撤销失败,00-未入账、01-入账中、04入账失败、05入账撤销中")
    private String accountStatus;

    @ApiModelProperty("入账时间")
    private Date accountTime;

    @ApiModelProperty("凭证入账时间")
    private Date voucherAccountTime;

    @ApiModelProperty("科目")
    private String payeeSubject;

    @ApiModelProperty("所属期")
    private String taxPeriod;

    @ApiModelProperty("凭证号")
    private String voucherNo;

    @ApiModelProperty("税额差")
    private String taxAmountDifference;

    @ApiModelProperty("比对状态 -1-比对失败 0-未比对 1-比对成功")
    private String billStatus;

    @ApiModelProperty("海关票异常信息")
    private String abnormalInfo;

    @ApiModelProperty("撤销勾选日期")
    private Date unCheckTime;

    @ApiModelProperty("PO号")
    private String contractNo;

    @ApiModelProperty("报关单编号(从BMS获取返回)")
    private String customsDocNo;

    private Date updateTime;

    private Date createTime;
}
