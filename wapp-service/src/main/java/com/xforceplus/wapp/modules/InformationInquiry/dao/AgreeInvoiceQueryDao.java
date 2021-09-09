package com.xforceplus.wapp.modules.InformationInquiry.dao;

import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/14 20:32
 */
@Mapper
public interface AgreeInvoiceQueryDao {
    Integer getMatchCount(Map<String,Object> params);

    List<MatchEntity> getMatchList(Map<String,Object> params);
}
