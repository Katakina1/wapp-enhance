package com.xforceplus.wapp.modules.noneBusiness.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Setter
@Getter
public class MtrIcSaveDto implements Serializable {
    private final static long serialVersionUID = 1L;
    @ApiModelProperty("上传时间")
    private Date createTime;
    @ApiModelProperty("电票流水号")
    private String batchNo;
    @ApiModelProperty("业务类型 9:MTR-Hyper 5：Intercom-特殊收入分配")
    private String bussinessType;
    @ApiModelProperty("费用承担门店")
    private String storeNo;
    @ApiModelProperty("业务单号")
    private String bussinessNo;

    /**
     * 验真状态 0 验真中 1验真成功 2 验真失败
     */
    @ApiModelProperty("验真状态 0 验真中 1验真成功 2 验真失败")
    private String verifyStatus;

    /**
     * ofd验签状态 0 验签中 1验签成功 2 验签失败
     */
    @ApiModelProperty("验签状态 0 验签中 1验签成功 2 验签失败")
    private String ofdStatus;

    @ApiModelProperty("验真失败备注")
    private String reason;
    @ApiModelProperty("功能组 5 IC")
    private String invoiceType;
    @ApiModelProperty("发票上传门店")
    private String invoiceStoreNo;
    @ApiModelProperty("货物/服务发生期间开始时间")
    private String storeStart;
    @ApiModelProperty("pdf地址")
    private String pdfUrl;
    @ApiModelProperty("ofd地址")
    private String ofdUrl;
    @ApiModelProperty("图片地址")
    private String imageUrl;

    private String xmlUrl;
    /**
     * 货物/服务发生期间结束时间
     */
    @ApiModelProperty("货物/服务发生期间结束时间")
    private String storeEnd;
    @ApiModelProperty("凭证号")
    private String voucherNo;
    @ApiModelProperty("上传备注")
    private String invoiceRemark;
    @ApiModelProperty("上传人 默认为wapp3.0")
    private String createUser;
    /**
     * 税率
     */
    @ApiModelProperty("税率(只有CHC过来的提货券和山姆本地订单才有值)")
    private String taxRate;
    /**
     * 税码
     * 提货券和山姆本地单才会设置值
     */
    @ApiModelProperty("税码")
    private String taxCode;

    @ApiModelProperty("发票主表信息")
    private MtrIcInvoiceMainDto mtrIcInvoiceMainDto;

    @ApiModelProperty("发票明细信息")
    private List<MtrIcInvoiceDetailDto> mtrIcInvoiceDetailDto;
}