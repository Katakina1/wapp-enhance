package com.xforceplus.wapp.modules.posuopei.entity;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;

/**
 * @author raymond.yan
 */
public class DetailClaimExcelEntity extends BaseRowModel implements Serializable {




    //序号
    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    //购方税号
    @ExcelProperty(value={"索赔号"},index = 1)
    private  String claimCode;
    @ExcelProperty(value={"日期"},index = 2)
    private  String claimDate;
    //供应商名称
    @ExcelProperty(value={"索赔金额"},index = 3)
    private  String claimAmount;
    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getClaimCode() {
        return claimCode;
    }

    public void setClaimCode(String claimCode) {
        this.claimCode = claimCode;
    }

    public String getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(String claimDate) {
        this.claimDate = claimDate;
    }

    public String getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(String claimAmount) {
        this.claimAmount = claimAmount;
    }


}
