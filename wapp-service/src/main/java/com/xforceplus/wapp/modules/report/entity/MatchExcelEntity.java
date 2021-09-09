package com.xforceplus.wapp.modules.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;
import java.util.Date;

/**
 * 匹配表
 */
public class MatchExcelEntity   extends BaseRowModel implements Serializable {

    //序号
    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"购方名称"},index = 1)
    private String gfName;//购方名称
    @ExcelProperty(value={"公司代码"},index = 2)
    private String companyCode;//company_code
    @ExcelProperty(value={"供应商名称"},index = 3)
    private String vendername;//供应商名称
    @ExcelProperty(value={"供应商号"},index = 4)
    private String venderId;//供应商编码
    @ExcelProperty(value={"发票金额"},index = 5)
    private String invoiceAmount;//发票金额
    @ExcelProperty(value={"发票数量"},index = 6)
    private String invoiceNum;//发票数量
    @ExcelProperty(value={"PO金额"},index = 7)
    private String poAmount;//PO金额
    @ExcelProperty(value={"PO数量"},index = 8)
    private String poNum;//PO数量
    @ExcelProperty(value={"索赔金额"},index = 9)
    private String claimAmount;//索赔金额
    @ExcelProperty(value={"索赔单数量"},index = 10)
    private String claimNum;//索赔单数量
    @ExcelProperty(value={"匹配日期"},index = 11)
    private String matchDate;//匹配日期
    @ExcelProperty(value={"结算金额"},index = 12)
    private String settlementAmount;//结算金额
    @ExcelProperty(value={"Host状态"},index = 13)
    private String hostStatus;//host_status


    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getVendername() {
        return vendername;
    }

    public void setVendername(String vendername) {
        this.vendername = vendername;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public String getPoAmount() {
        return poAmount;
    }

    public void setPoAmount(String poAmount) {
        this.poAmount = poAmount;
    }

    public String getPoNum() {
        return poNum;
    }

    public void setPoNum(String poNum) {
        this.poNum = poNum;
    }

    public String getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(String claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getClaimNum() {
        return claimNum;
    }

    public void setClaimNum(String claimNum) {
        this.claimNum = claimNum;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(String settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public String getHostStatus() {
        return hostStatus;
    }

    public void setHostStatus(String hostStatus) {
        this.hostStatus = hostStatus;
    }
}
