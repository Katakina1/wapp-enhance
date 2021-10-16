package com.xforceplus.wapp.modules.backFill.model;

import lombok.Data;

import java.util.Date;

/**
 * 发票附件表
 * tableName: t_xf_invoice_file
 *
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-23 19:41
 **/
@Data
public class InvoiceFileEntity {

    public static final int TYPE_OF_JPG = 0;
    public static final int TYPE_OF_JPEG = 3;
    public static final int TYPE_OF_PDF = 1;
    public static final int TYPE_OF_OFD = 2;

    public static final String SUFFIX_OF_JPG=".jpg";
    public static final String SUFFIX_OF_JPEG=".jpeg";
    public static final String SUFFIX_OF_PDF=".pdf";
    public static final String SUFFIX_OF_OFD=".ofd";
    public static final String SUFFIX_OF_DEFAULT="";
    /**
     * ID  雪花算法
     */
    private Long id;
    /**
     * 发票号码
     */
    private String invoiceNo;
    /**
     * 发票代码
     */
    private String invoiceCode;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 创建时间
     */
    private Date createTime;
    private String createUser;
    private String updateUser;
    private Date updateTime;
    /**
     * 存储介质  0 沃尔玛文件服务
     */
    private Integer storage;

    /**
     * 文件来源 0 供应商上传
     */
    private Integer origin;
    /**
     * 发票类型，
     * 0 jpg;
     * 1 pdf
     * 2 ofd
     */
    private Integer type;

    /**
     * 状态：1 正常，0删除
     */
    private Integer status;

    public String getSuffix(){
        switch (this.type){
            case 0:
                return SUFFIX_OF_JPG;
            case 1:
                return SUFFIX_OF_PDF;
            case 2:
                return SUFFIX_OF_OFD;
            case 3:
                return SUFFIX_OF_JPEG;
        }
        return SUFFIX_OF_DEFAULT;
    }
}
