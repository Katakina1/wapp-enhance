package com.xforceplus.wapp.modules.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 匹配表
 */
public class BatchSystemMatchQueryExcelEntity  extends BaseRowModel implements Serializable {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"发票组号"},index = 1)
    private String  matchNo;
    @ExcelProperty(value={"JV"},index = 2)
    private String jv;//购方名称
    @ExcelProperty(value={"供应商编码"},index = 3)
    private String vender;//供应商编码
    @ExcelProperty(value={"价税合计"},index = 4)
    private String invTotal;//发票金额
    @ExcelProperty(value={"税额"},index = 5)
    private  String taxAmount;
    @ExcelProperty(value={"税率"},index = 6)
    private String taxRate;//发票税率
    @ExcelProperty(value={"发票号码"},index = 7)
    private String inv;//发票号
    @ExcelProperty(value={"发票日期"},index = 8)
    private String yymmdd;//发票日期
    @ExcelProperty(value={"付款日期"},index = 9)
    private String yy1mm1dd1;//
    @ExcelProperty(value={"发票税率"},index = 10)
    private String taxRate2;//发票税率
    @ExcelProperty(value={"导入时间"},index = 11)
    private String createDate;//导入时间

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getMatchNo() {
        return matchNo;
    }

    public void setMatchNo(String matchNo) {
        this.matchNo = matchNo;
    }

    public String getJv() {
        return jv;
    }

    public void setJv(String jv) {
        this.jv = jv;
    }

    public String getVender() {
        return vender;
    }

    public void setVender(String vender) {
        this.vender = vender;
    }

    public String getInvTotal() {
        return invTotal;
    }

    public void setInvTotal(String invTotal) {
        this.invTotal = invTotal;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getInv() {
        return inv;
    }

    public void setInv(String inv) {
        this.inv = inv;
    }

    public String getYymmdd() {
        return yymmdd;
    }

    public void setYymmdd(String yymmdd) {
        this.yymmdd = yymmdd;
    }

    public String getYy1mm1dd1() {
        return yy1mm1dd1;
    }

    public void setYy1mm1dd1(String yy1mm1dd1) {
        this.yy1mm1dd1 = yy1mm1dd1;
    }

    public String getTaxRate2() {
        return taxRate2;
    }

    public void setTaxRate2(String taxRate2) {
        this.taxRate2 = taxRate2;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
