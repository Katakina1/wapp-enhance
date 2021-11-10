package com.xforceplus.wapp.modules.rednotification.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
public class QueryModel {
    @JsonProperty("isAllSelected")
    @ApiModelProperty("是否全选")
    private Boolean isAllSelected = null;

    @JsonProperty("includes")
    @ApiModelProperty("选中的id")
    private List<Long> includes = new ArrayList<Long>();

    @ApiModelProperty("排除id")
    @JsonProperty("excludes")
    private List<Long> excludes = new ArrayList<Long>();

    @ApiModelProperty("申请类型 购方发起:0-已抵扣1-未抵扣 销方发起:2-开票有误")
    private Integer applyType;

    @ApiModelProperty("购方名称")
    private String purchaserName;

    @ApiModelProperty("销方名称")
    private String sellerName;

    @ApiModelProperty("供应商公司编号")
    private String companyCode;

    @ApiModelProperty("扣款时间毫秒值")
    private List<Long> paymentTime;

    @ApiModelProperty("单号")
    private String billNo;

    @ApiModelProperty("红字信息来源1.索赔单，2协议单，3.EPD")
    private Integer invoiceOrigin;


    @ApiModelProperty("红字信息表编号")
    private String redNotificationNo;

    @ApiModelProperty("预制发票id")
    private List<Long> pidList;


    @ApiModelProperty("审批状态 1. 审核通过,2. 审核不通过,3. 已核销,4. 已撤销,5.撤销待审批")
    private Integer approveStatus;

    @ApiModelProperty(" 1.未申请 2.申请中 3.已申请 4.撤销待审核 ")
    private Integer applyingStatus;

    @ApiModelProperty("1正常，2申请锁定中，3撤销锁定中")
    private Integer lockFlag;

    @ApiModelProperty("1正常，0删除")
    private Integer status;


    @ApiModelProperty("分页码 最小1")
    Integer pageNo ;

    @ApiModelProperty("分页大小")
    Integer pageSize ;

}
