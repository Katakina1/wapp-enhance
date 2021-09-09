package com.xforceplus.wapp.modules.businessData.dao;

import com.xforceplus.wapp.modules.posuopei.entity.PoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface PoDao {
    List<PoEntity> poQueryList(Map<String, Object> map);
    Integer poQueryCount(Map<String, Object> map);
}
