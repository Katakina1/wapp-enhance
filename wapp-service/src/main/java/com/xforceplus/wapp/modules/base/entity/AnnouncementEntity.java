package com.xforceplus.wapp.modules.base.entity;

import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 公告表
 */
public class AnnouncementEntity implements Serializable {

    private static final long serialVersionUID = 2809669407591888363L;
    private Long id;
    private String announcementTitle;//公告标题
    private String announcementType;//公告类型（0-普通 1-带附件 2-带选择）
    private String announcementInfo;//公告内容
    private Date releasetime;//发布时间
    private String announcementStatus;//公告状态(0-正常 1-作废)
    private String announcementAnnex;//公告附件路径
    private Integer supplierUnreadNum;//供应商未读数量
    private Integer supplierReadNum;//供应商已读数量
    private Integer supplierDisagreeNum;//供应商不同意数量
    private Integer supplierAgreeNum;//供应商同意数量
    private String header;//页眉
    private String footer;//页脚
    private MultipartFile attchment;//公告附件
    private MultipartFile venderFile;//供应商导入文件
    private String orgLevel;//供应商类型
    private String userType;//供应商选择类型
    private String venderId;//供应商号
    private Date trainDate;
    //库存商品补偿金
    private BigDecimal compensationAmount;
    //价格调整商品应付差额
    private BigDecimal priceDifference;
    //商品价格下调日期
    private Date goodsReduceDate;
    //自定义公告发布日期
    private Date customReleasetime;
    //债务类别
    private String debtType;
    //供应商公司
    private String orgName;

    //pc加md的合计金额
    private long totalAmount;

    //是否已同意
    private String isAgree;
    private String rownum;

    public String getIsAgree() {
        return isAgree;
    }

    public void setIsAgree(String isAgree) {
        this.isAgree = isAgree;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getCompensationAmount() {
        return compensationAmount;
    }

    public void setCompensationAmount(BigDecimal compensationAmount) {
        this.compensationAmount = compensationAmount;
    }

    public BigDecimal getPriceDifference() {
        return priceDifference;
    }

    public void setPriceDifference(BigDecimal priceDifference) {
        this.priceDifference = priceDifference;
    }

    public Date getGoodsReduceDate() {
        return goodsReduceDate;
    }

    public void setGoodsReduceDate(Date goodsReduceDate) {
        this.goodsReduceDate = goodsReduceDate;
    }

    public Date getCustomReleasetime() {
        return customReleasetime;
    }

    public void setCustomReleasetime(Date customReleasetime) {
        this.customReleasetime = customReleasetime;
    }

    public String getDebtType() {
        return debtType;
    }

    public void setDebtType(String debtType) {
        this.debtType = debtType;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Date getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(Date trainDate) {
        this.trainDate = trainDate;
    }

    public MultipartFile getVenderFile() {
        return venderFile;
    }

    public void setVenderFile(MultipartFile venderFile) {
        this.venderFile = venderFile;
    }

    public String getOrgLevel() {
        return orgLevel;
    }

    public void setOrgLevel(String orgLevel) {
        this.orgLevel = orgLevel;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public MultipartFile getAttchment() {
        return attchment;
    }

    public void setAttchment(MultipartFile attchment) {
        this.attchment = attchment;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    private List<UserEntity> userinfo;//供应商信息

    private List<SettlementFileEntity> fileList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnnouncementTitle() {
        return announcementTitle;
    }

    public void setAnnouncementTitle(String announcementTitle) {
        this.announcementTitle = announcementTitle;
    }

    public String getAnnouncementType() {
        return announcementType;
    }

    public void setAnnouncementType(String announcementType) {
        this.announcementType = announcementType;
    }

    public String getAnnouncementInfo() {
        return announcementInfo;
    }

    public void setAnnouncementInfo(String announcementInfo) {
        this.announcementInfo = announcementInfo;
    }

    public Date getReleasetime() {
        return releasetime;
    }

    public void setReleasetime(Date releasetime) {
        this.releasetime = releasetime;
    }

    public String getAnnouncementStatus() {
        return announcementStatus;
    }

    public void setAnnouncementStatus(String announcementStatus) {
        this.announcementStatus = announcementStatus;
    }

    public String getAnnouncementAnnex() {
        return announcementAnnex;
    }

    public void setAnnouncementAnnex(String announcementAnnex) {
        this.announcementAnnex = announcementAnnex;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<UserEntity> getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(List<UserEntity> userinfo) {
        this.userinfo = userinfo;
    }

    public Integer getSupplierUnreadNum() {
        return supplierUnreadNum;
    }

    public void setSupplierUnreadNum(Integer supplierUnreadNum) {
        this.supplierUnreadNum = supplierUnreadNum;
    }

    public Integer getSupplierReadNum() {
        return supplierReadNum;
    }

    public void setSupplierReadNum(Integer supplierReadNum) {
        this.supplierReadNum = supplierReadNum;
    }

    public Integer getSupplierDisagreeNum() {
        return supplierDisagreeNum;
    }

    public void setSupplierDisagreeNum(Integer supplierDisagreeNum) {
        this.supplierDisagreeNum = supplierDisagreeNum;
    }

    public Integer getSupplierAgreeNum() {
        return supplierAgreeNum;
    }

    public void setSupplierAgreeNum(Integer supplierAgreeNum) {
        this.supplierAgreeNum = supplierAgreeNum;
    }

    public List<SettlementFileEntity> getFileList() {
        return fileList;
    }

    public void setFileList(List<SettlementFileEntity> fileList) {
        this.fileList = fileList;
    }
}
