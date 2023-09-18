package com.xforceplus.wapp.repository.entity;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/18
 * Time:10:03
*/

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class OrgEntity {
    private Long id;
    private String schemaLabel;
    private Long orgid;// 机构id
    private String orgcode;//机构编码
    private String orgname;//机构名称
    private String taxno;//纳税人识别号
    private String taxname;//纳税人名称
    private Long parentid;//上级机构id
    private String orgtype;//机构类型机构类型(0-大象慧云;1-中心企业;2-购方虚机构;3-销方虚机构;4-管理机构;5-购方企业;6-购销双方;7-门店;8-销方企业)
    private String linkman;//联系人
    private String phone;//联系电话
    private String address;//联系地址
    private String email;//电子邮箱
    private String postcode;//邮政编码
    private String bank;//开户行
    private String account;//银行帐号
    private String isbottom;//是否有下级[０－无；１－有]
    private Long orglevel;//机构级别
    private String orglayer;//机构层级代码
    private String company;//所属中心企业
    private String remark;//备注
    private String sortno;//排序字段
    private Date createTime;//创建时间
    private String createBy;//创建人
    private String usercode;
    private String username;

    private String dictname;
    private String dictcode;

    public String getDictcode() {
        return dictcode;
    }

    public void setDictcode(String dictcode) {
        this.dictcode = dictcode;
    }

    public String getDictname() {
        return dictname;
    }

    public void setDictname(String dictname) {
        this.dictname = dictname;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    private Date lastModifyTime;//修改时间
    private String lastModifyBy;//修改人
    private String comType;//公司类型 0国家，1企业
    private String isBlack;//是否加入黑名单  0未加入 1 已加入

    private Long discountRate; //协议折让率

    public Long getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Long discountRate) {
        this.discountRate = discountRate;
    }

    public Long getOrgid() {
        return orgid;
    }

    public void setOrgid(Long orgid) {
        this.orgid = orgid;
    }

    public String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getTaxno() {
        return taxno;
    }

    public void setTaxno(String taxno) {
        this.taxno = taxno;
    }

    public String getTaxname() {
        return taxname;
    }

    public void setTaxname(String taxname) {
        this.taxname = taxname;
    }

    public Long getParentid() {
        return parentid;
    }

    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    public String getOrgtype() {
        return orgtype;
    }

    public void setOrgtype(String orgtype) {
        this.orgtype = orgtype;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
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

    public String getIsbottom() {
        return isbottom;
    }

    public void setIsbottom(String isbottom) {
        this.isbottom = isbottom;
    }

    public Long getOrglevel() {
        return orglevel;
    }

    public void setOrglevel(Long orglevel) {
        this.orglevel = orglevel;
    }

    public String getOrglayer() {
        return orglayer;
    }

    public void setOrglayer(String orglayer) {
        this.orglayer = orglayer;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSortno() {
        return sortno;
    }

    public void setSortno(String sortno) {
        this.sortno = sortno;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getLastModifyBy() {
        return lastModifyBy;
    }

    public void setLastModifyBy(String lastModifyBy) {
        this.lastModifyBy = lastModifyBy;
    }

    public String getComType() {
        return comType;
    }

    public void setComType(String comType) {
        this.comType = comType;
    }

    public String getIsBlack() {
        return isBlack;
    }

    public void setIsBlack(String isBlack) {
        this.isBlack = isBlack;
    }

    public Date getCreateTime() {
        return obtainValidDate(this.createTime);
    }

    public void setCreateTime(Date createTime) {
        this.createTime =  obtainValidDate(createTime);
    }

    public Date getLastModifyTime() {
        return obtainValidDate(this.lastModifyTime);
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = obtainValidDate(lastModifyTime);
    }

    /**
     * 创建一个日期对象副本
     * @param srcDate
     * @return
     */
    public  Date obtainValidDate(Date srcDate) {
        return srcDate == null ? null : new Date(srcDate.getTime());
    }
    /**
     * 获取现在时间,这个好用
     *
     * @return返回长时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static Date getSqlDate() {
        Date sqlDate = new java.sql.Date(new Date().getTime());
        return sqlDate;
    }
}
