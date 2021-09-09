package com.xforceplus.wapp.modules.posuopei.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;

/**
 * 索赔表
 */
public class ExamineExcelEntity extends BaseRowModel implements Serializable {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"供应商号"},index = 1)
    private String venderid;//供应商号
    @ExcelProperty(value={"纳税识别号"},index = 2)
    private String taxNbr;//门店号
    @ExcelProperty(value={"供应商名称"},index = 3)
    private String venderNmae;//部门号
    @ExcelProperty(value={"货物(劳务服务)名称"},index = 4)
    private String cargoName;//jvcode
    @ExcelProperty(value={"单位"},index = 5)
    private String unit;//索赔号
    @ExcelProperty(value={"数量"},index = 6)
    private String num;//对应货款发票号
    @ExcelProperty(value={"单价"},index = 7)
    private String price;//定案日期
    @ExcelProperty(value={"金额"},index = 8)
    private String amount;//索赔金额
    @ExcelProperty(value={"税率"},index = 9)
    private String tax;//匹配状态
    @ExcelProperty(value={"税额"},index = 10)
    private String taxAmount;//host状态
    @ExcelProperty(value={"开红票通知单理由"},index = 10)
    private String redReason;//host状态
    @ExcelProperty(value={"税收分类编码"},index = 10)
    private String taxCode;//host状态
    @ExcelProperty(value={"办理类型"},index = 10)
    private String type;//host状态
    @ExcelProperty(value={"蓝票是否已抵扣"},index = 10)
    private String isDk;//host状态

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getTaxNbr() {
        return taxNbr;
    }

    public void setTaxNbr(String taxNbr) {
        this.taxNbr = taxNbr;
    }

    public String getVenderNmae() {
        return venderNmae;
    }

    public void setVenderNmae(String venderNmae) {
        this.venderNmae = venderNmae;
    }

    public String getCargoName() {
        return cargoName;
    }

    public void setCargoName(String cargoName) {
        this.cargoName = cargoName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getRedReason() {
        return redReason;
    }

    public void setRedReason(String redReason) {
        this.redReason = redReason;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsDk() {
        return isDk;
    }

    public void setIsDk(String isDk) {
        this.isDk = isDk;
    }
}
