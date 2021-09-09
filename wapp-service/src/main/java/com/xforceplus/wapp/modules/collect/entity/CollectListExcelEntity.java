package com.xforceplus.wapp.modules.collect.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;

/**
 * 扫描表
 */
public class CollectListExcelEntity extends BaseRowModel implements Serializable {


    @ExcelProperty(value={"采集时间"},index = 0)
    private String createDate;
    @ExcelProperty(value={"购方税号"},index = 1)
    private String gfTaxNo;//对应orgcode
    @ExcelProperty(value={"购方名称"},index = 2)
    private String gfName;//对应companyCode
    @ExcelProperty(value={"采集数量合计"},index = 3)
    private String collectCount;//供应商号
    @ExcelProperty(value={"金额合计"},index = 4)
    private String sumTotalAmount;//购方名称
    @ExcelProperty(value={"税额合计"},index = 5)
    private String sumTaxAmount;//销方名称


    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getGfTaxNo() {
        return gfTaxNo;
    }

    public void setGfTaxNo(String gfTaxNo) {
        this.gfTaxNo = gfTaxNo;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public String getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(String collectCount) {
        this.collectCount = collectCount;
    }

    public String getSumTotalAmount() {
        return sumTotalAmount;
    }

    public void setSumTotalAmount(String sumTotalAmount) {
        this.sumTotalAmount = sumTotalAmount;
    }

    public String getSumTaxAmount() {
        return sumTaxAmount;
    }

    public void setSumTaxAmount(String sumTaxAmount) {
        this.sumTaxAmount = sumTaxAmount;
    }




}
