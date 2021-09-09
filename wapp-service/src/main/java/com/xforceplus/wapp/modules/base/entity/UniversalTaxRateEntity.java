package com.xforceplus.wapp.modules.base.entity;


public class UniversalTaxRateEntity{

    //id
    private Integer id;

    private  int index;
    //供应商号
    private String vendorNbr;

    //供应商名称
    private String vendorName;

    //部门
    private String deptId;

    //商品说明
    private String notes;

    //税率
    private String inputTax;
    //商品号
    private String itemNbr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(Integer id) {
        this.index= index;
    }

    public String getVendorNbr() {
        return vendorNbr;
    }

    public void setVendorNbr(String vendorNbr) {
        this.vendorNbr = vendorNbr;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getInputTax() {
        return inputTax;
    }

    public void setInputTaxe(String inputTax) {
        this.inputTax = inputTax;
    }

    public String getItemNbr() {
        return itemNbr;
    }

    public void setItemNbr(String itemNbr) {
        this.itemNbr = itemNbr;
    }

}
