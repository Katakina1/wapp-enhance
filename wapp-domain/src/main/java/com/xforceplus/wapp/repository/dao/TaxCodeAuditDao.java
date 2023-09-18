package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TaxCodeAuditEntity;
import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaxCodeAuditDao extends BaseMapper<TaxCodeAuditEntity> {
}