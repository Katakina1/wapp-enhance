package com.xforceplus.wapp.modules.collect.pojo;

import com.xforceplus.wapp.modules.job.pojo.BasePojo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 *
 * 查验发票响应
 * @author Colin.hu
 * @date 4/17/2018
 */
@Getter @Setter @ToString
public class ResponseInvoice extends BasePojo {

    private static final long serialVersionUID = 4839290190539522047L;

    /**
     * 查验结果
     */
    private String resultCode;

    private String resultTip;

    private String checkCount;

    private String invoiceType;

    private String buyerTaxNo;

    private String buyerName;

    private String invoiceCode;

    private String invoiceNo;

    private String totalAmount;

    private String invoiceAmount;

    private String taxAmount;

    private String machineNo;

    private String salerTaxNo;

    private String salerName;

    private String salerAddressPhone;

    private String salerAccount;

    private String buyerAddressPhone;

    private String buyerAccount;

    private String invoiceDate;

    private String isCancelled;

    private String remark;

    private String checkCode;

    private String carrierTaxName;

    private String carrierTaxNum;

    private String acceptTaxName;

    private String acceptTaxNum;

    private String receiverName;

    private String receiverTaxNum;

    private String shipperName;

    private String shipperTaxNum;

    private String wayInfo;

    private String transportInfo;

    private String vehicleNum;

    private String vehicleTonnage;

    private String buyerIDNum;

    private String vehicleType;

    private String factoryModel;

    private String productPlace;

    private String certificate;

    private String certificateImport;

    private String inspectionNum;

    private String engineNo;

    private String vehicleNo;

    private String taxBureauCode;

    private String taxBureauName;

    private String taxRecords;

    private String tonnage;

    private String limitPeople;

    private String taxRate;

    private String txfbz;

    private List<InvoiceDetail> detailList;
}
