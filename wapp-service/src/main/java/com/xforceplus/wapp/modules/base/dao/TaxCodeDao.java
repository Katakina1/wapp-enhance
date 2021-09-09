package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.TaxCodeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
public interface TaxCodeDao {
    /**
     * 插入税码表
     * */
    @Transactional
    int insert(@Param("list") List<TaxCodeEntity> list);
    /**
     * 清空税码表
     * */
    void emptyTable();
    /**
     * 分页查询税码表
     * */
    List<TaxCodeEntity> queryList(@Param("entity") TaxCodeEntity entity);
    /**
     * 查询税码表总数
     * */
    int queryTotal();
}
