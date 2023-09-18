package com.xforceplus.wapp.modules.customs.dto;

import lombok.Data;

@Data
public class CustomsImportSizeDto {

    private int importCount;

    private int validCDount;

    private int unValidCount;

    private String errorMsg;

}
