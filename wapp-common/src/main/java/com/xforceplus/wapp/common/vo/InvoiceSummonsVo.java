package com.xforceplus.wapp.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: ChenHang
 * @Date: 2023/8/9 17:25
 */
@Data
public class InvoiceSummonsVo implements Serializable {

    /**
     * jv
     */
    private String jvCode;
    /**
     * 发票代码
     */
    private String invoiceCode;
    /**
     * 发票号码
     */
    private String invoiceNo;
    /**
     * 供应商id
     */
    private String venderid;
    /**
     * 供应商名称
     */
    private String vendername;
    /**
     * 凭证号
     */
    private String certificateNo;
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
    /**
     * 页数
     */
    @ApiModelProperty("页数")
    private Integer pageSize = 20;
    /**
     * 页码
     */
    @ApiModelProperty("页码")
    private Integer pageNo = 1;
    /**
     * 认证状态
     */
    private String authStatus;

}
