package com.xforceplus.wapp.modules.cost.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

public class SettlementFileEntity extends AbstractBaseDomain {
    //文件名
    private String fileName;
    //文件类型 1-发票图片, 2-附件
    private String fileType;
    //文件路径
    private String filePath;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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
