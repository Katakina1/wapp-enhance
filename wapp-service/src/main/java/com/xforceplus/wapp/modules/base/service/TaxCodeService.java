package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.TaxCodeEntity;

import java.util.List;

public interface TaxCodeService {
    /**
     * 插入税码表
     * */
    int insert(List<TaxCodeEntity> list);
    /**
     * 分页查询税码表
     * */
    List<TaxCodeEntity> queryList(TaxCodeEntity entity);
    /**
     * 查询税码表总数
     * */
    int queryTotal();
}
