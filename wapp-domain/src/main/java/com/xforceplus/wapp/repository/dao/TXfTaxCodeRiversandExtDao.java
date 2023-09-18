package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfTaxCodeRiversandEntity;

import java.util.List;


public interface TXfTaxCodeRiversandExtDao extends BaseMapper<TXfTaxCodeRiversandEntity> {

    Integer count(String itemNo, String status, String createTimeStart, String createTimeEnd);

    List<TXfTaxCodeRiversandEntity> queryByPage(Integer offset, Integer next, String itemNo, String status, String createTimeStart, String createTimeEnd);
}
