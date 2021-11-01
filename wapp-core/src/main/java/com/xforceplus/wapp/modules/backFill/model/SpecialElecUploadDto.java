package com.xforceplus.wapp.modules.backFill.model;

import lombok.Data;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-16 14:43
 **/
@Data
public class SpecialElecUploadDto {
    private List<byte[]> pdfs;
    private List<byte[]> ofds;

    private Long userId;
    private String gfName;
    private String jvCode;
    private String vendorId;
    private String xfName;
    private String xfTaxNo;
    private String gfTaxNo;
    private String settlementNo;

}
