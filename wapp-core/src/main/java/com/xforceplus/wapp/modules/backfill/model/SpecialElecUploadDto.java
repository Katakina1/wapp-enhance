package com.xforceplus.wapp.modules.backfill.model;

import lombok.Data;

import java.util.List;
import java.util.Set;

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
    private List<byte[]> xmls;

    private Long userId;
    private String gfName;
    private String jvCode;
    private String vendorId;
    private String xfName;
    private String xfTaxNo;
    private String gfTaxNo;
    private String settlementNo;
    private Integer businessType;

}
