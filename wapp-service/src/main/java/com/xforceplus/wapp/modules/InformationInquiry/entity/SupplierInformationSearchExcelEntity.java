package com.xforceplus.wapp.modules.InformationInquiry.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;

public class SupplierInformationSearchExcelEntity extends BaseRowModel implements Serializable {



    //序号
    @ExcelProperty(value={"序号"},index = 0)
    private String indexNo;
    @ExcelProperty(value={"供应商业务类型"},index = 1)
    private String usertype;
    //人员编码
    @ExcelProperty(value={"供应商号"},index = 2)
    private String userCode;
    //人员姓名
    @ExcelProperty(value={"供应商名称"},index = 3)
    private String userName;
    @ExcelProperty(value={"供应商税号"},index = 4)
    private String taxno;
    @ExcelProperty(value={"联系人"},index = 5)
    private String finusername;
    //电话号码
    @ExcelProperty(value={"电话"},index = 6)
    private String phone;
    //传真
    @ExcelProperty(value={"传真"},index = 7)
    private String fax;
    //电子邮箱
    @ExcelProperty(value={"邮箱地址"},index = 8)
    private String email;
    @ExcelProperty(value={"邮寄方式"},index = 9)
    private String postType;//邮寄方式
    @ExcelProperty(value={"城市"},index = 10)
    private String city;//城市
    @ExcelProperty(value={"邮寄地址"},index = 11)
    private String postAddress;//邮寄地址
    @ExcelProperty(value={"供应商级别"},index = 12)
    private String orgLevel;//供应商类型
    @ExcelProperty(value={"供应商类型"},index = 13)
    private String type;//供应商类型
    @ExcelProperty(value={"冻结状态"},index = 14)
    private String extf0;//供应商类型
    @ExcelProperty(value={"删除状态"},index = 14)
    private String extf1;//供应商类型

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtf0() {
        return extf0;
    }

    public void setExtf0(String extf0) {
        this.extf0 = extf0;
    }

    public String getExtf1() {
        return extf1;
    }

    public void setExtf1(String extf1) {
        this.extf1 = extf1;
    }

    public String getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(String indexNo) {
        this.indexNo = indexNo;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTaxno() {
        return taxno;
    }

    public void setTaxno(String taxno) {
        this.taxno = taxno;
    }

    public String getFinusername() {
        return finusername;
    }

    public void setFinusername(String finusername) {
        this.finusername = finusername;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getOrgLevel() {
        return orgLevel;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setOrgLevel(String orgLevel) {
        this.orgLevel = orgLevel;
    }
}
