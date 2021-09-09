package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by 1 on 2018/10/20 20:05
 */
public class FileEntity extends AbstractBaseDomain implements Serializable {
    private static final long serialVersionUID = 4954216018177391110L;

    //文件路径
    private String filePath;

    //文件类型
    private String fileType;

    //文件描述
    private String fileDescription;

    //文件号
    private String fileNumber;

    //文件名称
    private String fileName;




    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }



    @Override
    public Boolean isNullObject() {
        return null;
    }
}
