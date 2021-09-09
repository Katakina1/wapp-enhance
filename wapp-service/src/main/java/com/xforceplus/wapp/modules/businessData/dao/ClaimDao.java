package com.xforceplus.wapp.modules.businessData.dao;

import com.xforceplus.wapp.modules.posuopei.entity.ClaimEntity;
import com.xforceplus.wapp.modules.posuopei.entity.PoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClaimDao {
    List<ClaimEntity> claimQueryList(Map<String, Object> map);
    Integer claimQueryCount(Map<String, Object> map);
    
}
