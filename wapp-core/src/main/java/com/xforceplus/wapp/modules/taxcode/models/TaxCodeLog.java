package com.xforceplus.wapp.modules.taxcode.models;

import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class TaxCodeLog {
    private Long id;

    private String sellerNo;

    private String sellerName;

    /**
     * 税编转换代码
     */
    private String itemNo;

    /**
     * 商品和服务名称
     */
    private String itemName;

    private TaxCodeEntity before;

    private TaxCodeEntity after;

    private Integer auditStatus;

    private Date auditTime;

    private String auditOpinion;

    private Integer sendStatus;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新用户
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;
}
