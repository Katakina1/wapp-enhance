package com.xforceplus.wapp.modules.InformationInquiry.entity;

import java.io.Serializable;
import java.util.List;
import com.xforceplus.wapp.modules.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

public class SupplierInformationSearchEntity extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;

    //用户所在分库名
    private String schemaLabel;

    //所属中心企业
    private String company;

    //用户表id
    private Integer userid;

    //人员姓名
    private String userName;

    //人员编码
    private String userCode;

    //电子邮箱
    private String email;

    //电话号码
    private String phone;

    //手机号码
    private String cellPhone;

    //联系地址
    private String address;   
    //状态
    private String status;
   
    //开户行
    private String bank;

    //银行帐号
    private String account;

    //传真
    private String fax;

    //银行代码
    private String bankCode;

    //机构名称
    private String orgName;
    //JV号
    private String orgcode;
    //门店号
    private String storeNumber;

    private String finusername;

    private List<Long> ids;

    private String orgId;
    private String orgCode;//机构编码
    private String taxNo;//纳税人识别号
    private String orgType;//机构类型机构类型(0-大象慧云;1-中心企业;2-购方虚机构;3-销方虚机构;4-管理机构;5-购方企业;6-购销双方;7-门店;8-销方企业)
   
    private String remark;//备注
    private String extf0;//JV码
    private String extf1;//店号
    
    private String postAddress;//邮寄地址
    private String postType;//邮寄方式
    private String orgLevel;//供应商类型


    private String taxno;
    private String usertype;
    private String city;


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getOrgLevel() {
        return orgLevel;
    }

    public void setOrgLevel(String orgLevel) {
        this.orgLevel = orgLevel;
    }

    public String getSchemaLabel() {
        return schemaLabel;
    }

    public void setSchemaLabel(String schemaLabel) {
        this.schemaLabel = schemaLabel;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

    public String getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getFinusername() {
        return finusername;
    }

    public void setFinusername(String finusername) {
        this.finusername = finusername;
    }


    public String getTaxno() {
        return taxno;
    }

    public void setTaxno(String taxno) {
        this.taxno = taxno;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }
}
