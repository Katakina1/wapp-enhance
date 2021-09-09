package com.xforceplus.wapp.modules.posuopei.entity;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;

/**
 * @author raymond.yan
 */
public class ScreenExcelEntity extends BaseRowModel implements Serializable {




    //序号
    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    //购方税号
    @ExcelProperty(value={"购方税务识别号"},index = 1)
    private  String gfTaxNo;
    @ExcelProperty(value={"供应商编号"},index = 2)
    private  String venderid;
    //供应商名称
    @ExcelProperty(value={"供应商名称"},index = 3)
    private  String venderName;
    //发票金额合计(结算金额)
    @ExcelProperty(value={"发票金额"},index = 4)
    private String invoiceAmount;
    //发票数量
    @ExcelProperty(value={"发票数量"},index = 5)
    private String invoiceNum;
    //po 金额合计
    @ExcelProperty(value={"订单金额"},index = 6)
    private String poAmount;
    @ExcelProperty(value={"订单数量"},index = 7)
    private String poNum;
    //claim金额合计
    @ExcelProperty(value={"索赔金额"},index = 8)
    private String claimAmount;
    @ExcelProperty(value={"索赔单数量"},index = 9)
    private String claimNum;
    @ExcelProperty(value={"匹配日期"},index = 10)
    private String matchDate;
    @ExcelProperty(value={"结算金额"},index = 11)
    private String settlementamount;
    @ExcelProperty(value={"错误提示"},index = 12)
    private String errDesc;
    @ExcelProperty(value={"底账供应商号"},index = 13)
    private String dzVenderid;
    @ExcelProperty(value={"发票号"},index = 14)
    private String dzInvoiceNo;
    @ExcelProperty(value={"发票代码"},index = 15)
    private String dzInvoiceCode;
    @ExcelProperty(value={"发票金额"},index = 16)
    private String dzInvoiceAmount;

    public String getDzVenderid() {
        return dzVenderid;
    }

    public void setDzVenderid(String dzVenderid) {
        this.dzVenderid = dzVenderid;
    }

    public String getDzInvoiceCode() {
        return dzInvoiceCode;
    }

    public void setDzInvoiceCode(String dzInvoiceCode) {
        this.dzInvoiceCode = dzInvoiceCode;
    }

    public String getDzInvoiceNo() {
        return dzInvoiceNo;
    }

    public void setDzInvoiceNo(String dzInvoiceNo) {
        this.dzInvoiceNo = dzInvoiceNo;
    }

    public String getDzInvoiceAmount() {
        return dzInvoiceAmount;
    }

    public void setDzInvoiceAmount(String dzInvoiceAmount) {
        this.dzInvoiceAmount = dzInvoiceAmount;
    }

    public String getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(String errDesc) {
        this.errDesc = errDesc;
    }


    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getGfTaxNo() {
        return gfTaxNo;
    }

    public void setGfTaxNo(String gfTaxNo) {
        this.gfTaxNo = gfTaxNo;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
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

    public String getSettlementamount() {
        return settlementamount;
    }

    public void setSettlementamount(String settlementamount) {
        this.settlementamount = settlementamount;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }
}
