package com.xforceplus.wapp.modules.base.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

public class AnnouncementExcelEntity extends BaseRowModel{

    //文件id
    @ExcelProperty(value={"序号"},index = 0)
    private String rownum;
    @ExcelProperty(value={"公告标题"},index = 1)
    private String announcementTitle;

    @ExcelProperty(value={"公告类型"},index = 2)
    private String announcementType;

    //文件名
    @ExcelProperty(value={"发布时间"},index = 3)
    private String releasetime;

    //文件扩展名
    @ExcelProperty(value={"内容"},index = 4)
    private String announcementInfo;

    //文件大小
    @ExcelProperty(value={"已读数量"},index = 5)
    private String supplierReadNum;

    //上传时间
    @ExcelProperty(value={"未读数量"},index = 6)
    private String supplierUnreadNum;

    //供应商类型
    @ExcelProperty(value={"同意数量"},index = 7)
    private String supplierAgreeNum;

    @ExcelProperty(value={"不同意数量"},index = 8)
    private String supplierDisagreeNum;

    public String getRownum() {
        return rownum;
    }

    public void setRownum(String rownum) {
        this.rownum = rownum;
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

    public String getReleasetime() {
        return releasetime;
    }

    public void setReleasetime(String releasetime) {
        this.releasetime = releasetime;
    }

    public String getAnnouncementInfo() {
        return announcementInfo;
    }

    public void setAnnouncementInfo(String announcementInfo) {
        this.announcementInfo = announcementInfo;
    }

    public String getSupplierReadNum() {
        return supplierReadNum;
    }

    public void setSupplierReadNum(String supplierReadNum) {
        this.supplierReadNum = supplierReadNum;
    }

    public String getSupplierUnreadNum() {
        return supplierUnreadNum;
    }

    public void setSupplierUnreadNum(String supplierUnreadNum) {
        this.supplierUnreadNum = supplierUnreadNum;
    }

    public String getSupplierAgreeNum() {
        return supplierAgreeNum;
    }

    public void setSupplierAgreeNum(String supplierAgreeNum) {
        this.supplierAgreeNum = supplierAgreeNum;
    }

    public String getSupplierDisagreeNum() {
        return supplierDisagreeNum;
    }

    public void setSupplierDisagreeNum(String supplierDisagreeNum) {
        this.supplierDisagreeNum = supplierDisagreeNum;
    }
}
