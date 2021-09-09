package com.xforceplus.wapp.modules.enterprise.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 税收分类编码实体类 (税收分类编码表)
 * Created by vito.xing on 2018/4/12
 */
@Getter @Setter @ToString
public class TaxCodeEntity extends AbstractBaseDomain {

    private static final long serialVersionUID = 2877752114340611620L;

    //税收分类编码
    private String ssbmCode;

    //税收分类名称
    private String ssbmName;

    //商品和服务分类简称
    private String ssbmAbbreviation;

    //说明
    private String explain;

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
