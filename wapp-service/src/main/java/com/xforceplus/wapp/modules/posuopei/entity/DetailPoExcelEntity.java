package com.xforceplus.wapp.modules.posuopei.entity;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;

/**
 * @author raymond.yan
 */
public class DetailPoExcelEntity extends BaseRowModel implements Serializable {




    //序号
    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    //购方税号
    @ExcelProperty(value={"订单号"},index = 1)
    private  String poCode;
    @ExcelProperty(value={"订单金额"},index = 2)
    private  String poAmount;
    //供应商名称
    @ExcelProperty(value={"收货号"},index = 3)
    private  String receipti;
    //发票金额合计(结算金额)
    @ExcelProperty(value={"收货日期"},index = 4)
    private String receiptiDate;
    //发票数量
    @ExcelProperty(value={"收货金额"},index = 5)
    private String receiptiAmount;
    //po 金额合计
    @ExcelProperty(value={"已结金额"},index = 6)
    private String yiJieAmount;
    @ExcelProperty(value={"未结金额"},index = 7)
    private String weiJieAmount;

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public String getPoAmount() {
        return poAmount;
    }

    public void setPoAmount(String poAmount) {
        this.poAmount = poAmount;
    }

    public String getReceipti() {
        return receipti;
    }

    public void setReceipti(String receipti) {
        this.receipti = receipti;
    }

    public String getReceiptiDate() {
        return receiptiDate;
    }

    public void setReceiptiDate(String receiptiDate) {
        this.receiptiDate = receiptiDate;
    }

    public String getReceiptiAmount() {
        return receiptiAmount;
    }

    public void setReceiptiAmount(String receiptiAmount) {
        this.receiptiAmount = receiptiAmount;
    }

    public String getYiJieAmount() {
        return yiJieAmount;
    }

    public void setYiJieAmount(String yiJieAmount) {
        this.yiJieAmount = yiJieAmount;
    }

    public String getWeiJieAmount() {
        return weiJieAmount;
    }

    public void setWeiJieAmount(String weiJieAmount) {
        this.weiJieAmount = weiJieAmount;
    }
}
