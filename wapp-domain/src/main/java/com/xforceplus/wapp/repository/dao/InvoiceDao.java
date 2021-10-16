package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.InvoiceEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InvoiceDao extends BaseMapper<InvoiceEntity> {
}