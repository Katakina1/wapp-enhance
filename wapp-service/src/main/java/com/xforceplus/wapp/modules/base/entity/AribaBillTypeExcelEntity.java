package com.xforceplus.wapp.modules.base.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

public class AribaBillTypeExcelEntity extends BaseRowModel{

    //文件id
    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;

    //文件名
    @ExcelProperty(value={"MCC编码"},index = 1)
    private String mccCode;

    //文件扩展名
    @ExcelProperty(value={"GLAccount"},index = 2)
    private String glAccount;

    //文件大小
    @ExcelProperty(value={"业务类型"},index = 3)
    private String serviceName;

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getMccCode() {
        return mccCode;
    }

    public void setMccCode(String mccCode) {
        this.mccCode = mccCode;
    }

    public String getGlAccount() {
        return glAccount;
    }

    public void setGlAccount(String glAccount) {
        this.glAccount = glAccount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    //上传时间
    @ExcelProperty(value={"所属大类"},index = 4)
    private String serviceType;

}
