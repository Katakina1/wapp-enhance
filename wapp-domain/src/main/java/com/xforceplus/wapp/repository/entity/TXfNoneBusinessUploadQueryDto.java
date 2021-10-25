package com.xforceplus.wapp.repository.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TXfNoneBusinessUploadQueryDto {


    @ApiModelProperty("发票号码")
    /**
     * 发票号
     */
    private String invoiceNo;
    @ApiModelProperty("验真状态 0 验真中 1验真成功 2 验真失败")
    /**
     * 验真状态态 0 验真中 1验真成功 2 验真失败
     */
    private String verifyStatus;
    @ApiModelProperty("ofd验签状态 0 验签中 1验签成功 2 验签失败")
    /**
     * ofd验签状态 0 验签中 1验签成功 2 验签失败
     */
    private String ofdStatus;

    @ApiModelProperty("门店号")
    /**
     * 门店号
     */
    private String storeNo;

    @ApiModelProperty("业务类型  0 水电费 1 leasing in  2 固定资产转移")
    /**
     * 业务类型  0 水电费 1 leasing in  2 固定资产转移
     */
    private String bussinessType;

    @ApiModelProperty("发票类型 1 否 2 SGA 3 IC 4 EC 5 RE 6 SR")
    /**
     * 发票类型 1 否 2 SGA 3 IC 4 EC 5 RE 6 SR
     */
    private String invoiceType;
    @ApiModelProperty("查询类型 0查询当前 1 查询所有")

    /**
     * 创建人
     */
    private String queryType;
    /**
     * 创建人
     */
    private String createUser;
    @ApiModelProperty("订单开始时间")
    private String createDateStart;
    @ApiModelProperty("订单结束时间")
    private String createDateEnd;
    @ApiModelProperty("沃尔玛公司代码")
    private String companyCode;
    @ApiModelProperty("供应商编号")
    private String venderId;

}
