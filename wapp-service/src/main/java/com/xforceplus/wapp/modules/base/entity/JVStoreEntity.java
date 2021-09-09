package com.xforceplus.wapp.modules.base.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author toffler
 * jvstore
 * */

@Getter
@Setter
public class JVStoreEntity extends BaseEntity implements Serializable {
    /**
     * 用户所在分库名
     */
    private String schemaLabel;


    private String[] jvcodes;

    private String[] storeCodes;

    //公司代码
    private String jvcode;
    //商场代码
    private String storeCode;
    //商场中文名
    private String storeChinese;
    //商场税务
    private String storeTax;
    //jv对应门店名称
    private String jvcodeName;
    //纳税人识别号
    private String taxpayerCode;
    //创建人
    private String establishName;
    //修改人
    private String amendName;
    //创建时间
    private Date establishDate;
    //修改时间
    private Date amendDate;
    private Long id;

    //sheet行
    private Integer row;

    //上传日期
    private Date uploadDate;

    //导入失败原因
    private String failureReason;

}
