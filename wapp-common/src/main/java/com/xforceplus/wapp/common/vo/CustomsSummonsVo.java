package com.xforceplus.wapp.common.vo;

import com.xforceplus.wapp.common.utils.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Author: ChenHang
 * @Date: 2023/7/18 14:16
 */
@Data
public class CustomsSummonsVo implements Serializable {

    /**
     * 海关缴款书号
     */
    @ApiModelProperty("海关缴款书号")
    private String invoiceNo;
    /**
     * 供应商税号
     */
    @ApiModelProperty("供应商税号")
    private String venderid;
    /**
     * 税款所属期
     */
    @ApiModelProperty("税款所属期")
    private String taxPeriod;

    public String getTaxPeriod() {
        if (StringUtils.isNotEmpty(taxPeriod) && taxPeriod.length() == 7){
            return DateUtils.toFormatDateMM2(taxPeriod);
        }
        return taxPeriod;
    }

    /**
     * 凭证号 传票清单表
     */
    @ApiModelProperty("凭证号码")
    private String certificateNo;
    /**
     * 订单号(缴款书合同号) PO号 海关票主表
     */
    @ApiModelProperty("订单号(缴款书合同号)")
    private String contractNo;
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
     * 填发日期, 开票日期
     */
    @ApiModelProperty("填发日期开始yyyymmdd")
    private String paperDrewDateStart;
    /**
     * 填发日期, 开票日期
     */
    @ApiModelProperty("填发日期结束yyyymmdd")
    private String paperDrewDateEnd;
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

}
