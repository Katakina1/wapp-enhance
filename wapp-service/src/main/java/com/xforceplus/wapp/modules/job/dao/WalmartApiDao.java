package com.xforceplus.wapp.modules.job.dao;

import com.xforceplus.wapp.modules.job.entity.WalmartApiEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WalmartApiDao {
    /**
     * 查询购方名称
     */
    List<WalmartApiEntity> searchGf();
}