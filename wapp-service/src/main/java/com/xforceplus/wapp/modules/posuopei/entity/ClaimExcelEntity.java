package com.xforceplus.wapp.modules.posuopei.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.io.Serializable;

/**
 * 索赔表
 */
public class ClaimExcelEntity extends BaseRowModel implements Serializable {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"供应商号"},index = 1)
    private String venderid;//供应商号
    @ExcelProperty(value={"门店号"},index = 2)
    private String storeNbr;//门店号
    @ExcelProperty(value={"部门号"},index = 3)
    private String dept;//部门号
    @ExcelProperty(value={"JVCODE"},index = 4)
    private String jvcode;//jvcode
    @ExcelProperty(value={"索赔号码"},index = 5)
    private String claimno;//索赔号
    @ExcelProperty(value={"对应货款发票号"},index = 6)
    private String invoiceno;//对应货款发票号
    @ExcelProperty(value={"定案日期"},index = 7)
    private String postdate;//定案日期
    @ExcelProperty(value={"索赔金额"},index = 8)
    private String claimAmount;//索赔金额
    @ExcelProperty(value={"匹配状态"},index = 9)
    private String matchstatus;//匹配状态
    @ExcelProperty(value={"沃尔玛状态"},index = 10)
    private String hostStatus;//host状态








    public String getClaimno() {
        return claimno;
    }

    public void setClaimno(String claimno) {
        this.claimno = claimno;
    }

    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        this.venderid = venderid;
    }

    public String getStoreNbr() {
        return storeNbr;
    }

    public void setStoreNbr(String storeNbr) {
        this.storeNbr = storeNbr;
    }

    public String getHostStatus() {
        return hostStatus;
    }

    public void setHostStatus(String hostStatus) {
        this.hostStatus = hostStatus;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getMatchstatus() {
        return matchstatus;
    }

    public void setMatchstatus(String matchstatus) {
        this.matchstatus = matchstatus;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getInvoiceno() {
        return invoiceno;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    public String getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(String claimAmount) {
        this.claimAmount = claimAmount;
    }
}
