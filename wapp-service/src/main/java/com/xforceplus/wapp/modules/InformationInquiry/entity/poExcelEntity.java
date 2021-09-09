package com.xforceplus.wapp.modules.InformationInquiry.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表
 */
public class poExcelEntity extends BaseRowModel implements Serializable {
    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"JV"},index = 1)
    private String jvcode;//机构编码
    @ExcelProperty(value={"供应商号"},index = 2)
    private String venderId;//供应商号
    @ExcelProperty(value={"订单号"},index = 3)
    private String poCode;//订单号
    @ExcelProperty(value={"订单类型"},index = 4)
    private String poType;//po类型 1收货，2调整，3索赔退货，4冲账
    @ExcelProperty(value={"收货日期"},index = 5)
    private String receiptDate;//收货日期
    @ExcelProperty(value={"收货号"},index = 6)
    private String receiptId;//收货号
    @ExcelProperty(value={"交易号"},index = 7)
    private String tractionNbr;//交易号
    @ExcelProperty(value={"收货金额"},index = 8)
    private String receiptAmount;//收货金额
    @ExcelProperty(value={"发票号码"},index = 9)
    private String invoiceNo;//发票号
    @ExcelProperty(value={"匹配状态"},index = 10)
    private String dxhyMatchStatus;//匹配状态
    @ExcelProperty(value={"沃尔玛状态"},index = 11)
    private String hostStatus;//订单状态





    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(String receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public String getHostStatus() {
        return hostStatus;
    }

    public void setHostStatus(String hostStatus) {
        this.hostStatus = hostStatus;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }


    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }



    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getDxhyMatchStatus() {
        return dxhyMatchStatus;
    }

    public void setDxhyMatchStatus(String dxhyMatchStatus) {
        this.dxhyMatchStatus = dxhyMatchStatus;
    }









    public String getPoType() {
        return poType;
    }

    public void setPoType(String poType) {
        this.poType = poType;
    }

    public String getTractionNbr() {
        return tractionNbr;
    }

    public void setTractionNbr(String tractionNbr) {
        this.tractionNbr = tractionNbr;
    }

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }
}
