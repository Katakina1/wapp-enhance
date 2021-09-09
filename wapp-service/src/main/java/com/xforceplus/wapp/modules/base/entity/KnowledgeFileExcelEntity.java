package com.xforceplus.wapp.modules.base.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.util.Date;

public class KnowledgeFileExcelEntity extends BaseRowModel{

    //文件id
    @ExcelProperty(value={"序号"},index = 0)
    private String rownum;

    private String fileId;

    //文件内容
    private String fileContent;

    //文件名
    @ExcelProperty(value={"文件名称"},index = 3)
    private String fileName;

    //文件扩展名
    @ExcelProperty(value={"文件类型"},index = 2)
    private String fileExtension;

    //文件大小
    @ExcelProperty(value={"文件大小"},index = 4)
    private String fileSize;

    //上传时间
    @ExcelProperty(value={"上传时间"},index = 5)
    private String uploadDate;

    //供应商类型
    @ExcelProperty(value={"供应商类型"},index = 1)
    private String venderType;

    //文件路劲
    private String filePath;

    public String getRownum() {
        return rownum;
    }

    public void setRownum(String rownum) {
        this.rownum = rownum;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
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

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
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
}
