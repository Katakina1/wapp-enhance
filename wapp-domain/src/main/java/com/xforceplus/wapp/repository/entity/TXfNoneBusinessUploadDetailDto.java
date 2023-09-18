package com.xforceplus.wapp.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * <p>
    * 非商电票上传记录明细表
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
public class TXfNoneBusinessUploadDetailDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，雪花算法
     */
    private Long id;

    /**
     * 上传ofd/pdf批次号
     */
    private String batchNo;

    /**
     * pdf识别任务号
     */
    private String xfDiscernTaskId;

    /**
     * 验真任务号
     */
    private String xfVerifyTaskId;

    /**
     * 发票号
     */
    private String invoiceNo;

    /**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 失败原因
     */
    private String reason;

    /**
     * ofd验签状态 0 验真中 1验真成功 2 验真失败
     */
    private String verifyStatus;

    /**
     * ofd验签状态 0 验签中 1验签成功 2 验签失败
     */
    private String ofdStatus;

    /**
     * 发票上传门店
     */
    private String invoiceStoreNo;

    /**
     * 门店号
     */
    private String storeNo;

    /**
     * 货物/服务发生期间 开始时间
     */
    private String storeStart;

    /**
     * 货物/服务发生期间 结束时间
     */
    private String storeEnd;

    /**
     * 业务类型  0 水电费 1 leasing in  2 固定资产转移
     */
    private String bussinessType;

    /**
     * 业务单号
     */
    private String bussinessNo;

    /**
     * 发票类型 1 SG 2 A 3 RE 4 FA
     */
    private String invoiceType;

    /**
     * 发票类型 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
     */
    private String fpInvoiceType;

    /**
     * 源文件服务返回-文件ID
     */
    private String sourceUploadId;

    /**
     * 源文件服务返回-文件路径
     */
    private String sourceUploadPath;

    /**
     * 文件服务返回-文件ID
     */
    private String uploadId;

    /**
     * 文件服务返回-文件路径
     */
    private String uploadPath;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 文件类型: 0-ofd文件，1-pdf文件
     */
    private String fileType;

    /**
     * 是否提交 0未提交 1 已提交
     */
    private String submitFlag;
    /**
     * 开票日期
     */
    private String invoiceDate;
    /**
     * 公司代码
     */
    private String companyCode;
    /**
     * 金额
     */
    private String invoiceAmount;
    /**
     * 税额
     */
    private String taxAmount;
    /**
     * 价税合计
     */
    private String totalAmount;
    /**
     * 0查询当前用户数据 1 查询所有数据
     */
    private String queryType;
    /**
     * 购方税号
     */
    private String purTaxNo;
    /**
     * 购方名称
     */
    private String purTaxName;
    /**
     * 销方税号
     */
    private String sellerTaxNo;
    /**
     * 销方名称
     */
    private String sellerTaxName;
    /**
     * 税率
     */
    private String taxRate;
    /**
     * 税率字符串， 前端展示用
     */
    private String taxRateStr;

    public String getTaxRateStr() {
        String taxRateStr = "";
        if (StringUtils.isNotEmpty(taxRate)) {
            String split = "、";
            String[] taxRates = taxRate.split(split);
            for (String taxRate : taxRates) {
                taxRateStr = taxRateStr +  taxRate + "%" + split;
            }
            taxRateStr = taxRateStr.replaceAll( split + "$", "");
        }
        return taxRateStr;
    }

    /**
     * 发票状态
     */
    private String invoiceStatus;
    /**
     * 认证状态
     */
    private String authStatus;
    /**
     * 认证日期
     */
    private String authDate;
    /**
     * sap编号
     */
    private String sap;

    /**
     * supplieId (沃尔玛结算平台-发票管理-非商电票上传 展示供应商id)
     */
    private String supplierId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 发票备注
     */
    private String invoiceRemark;
  
   /**
     * 公司代码
     */
    private String companyNo;

    /**
     * 税码
     */
    private String taxCode;

    private String entryDate;

    private String voucherNo;

    /**
     * 货物或应税劳务名称
     * chc发票到web, t_xf_none_business_upload_detail表新增了 goods_name 可直接查询
     */
    private String goodsName;

    /**
     * 发票代码 + 发票号码
     */
    private String uuid;
}
