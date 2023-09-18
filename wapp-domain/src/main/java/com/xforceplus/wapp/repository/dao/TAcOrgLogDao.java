package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.OrgEntity;
import com.xforceplus.wapp.repository.entity.OrgLogEntity;
import com.xforceplus.wapp.repository.entity.OrgQuotaLogEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TAcOrgLogDao extends BaseMapper<OrgLogEntity> {
    void addDiscountRateLog(@Param("entity") OrgLogEntity orgLogEntity);
}
