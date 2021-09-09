package com.xforceplus.wapp.modules.base.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.util.Date;

public class KnowledgeFileEntity extends AbstractBaseDomain {

    //文件id
    private Integer fileId;

    //文件内容
    private String fileContent;

    //文件名
    private String fileName;

    //文件扩展名
    private String fileExtension;

    //文件大小
    private Long fileSize;

    //上传时间
    private Date uploadDate;

    //供应商类型
    private String venderType;

    //文件路劲
    private String filePath;

    private String rownum;

    public String getRownum() {
        return rownum;
    }

    public void setRownum(String rownum) {
        this.rownum = rownum;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getVenderType() {
        return venderType;
    }

    public void setVenderType(String venderType) {
        this.venderType = venderType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
