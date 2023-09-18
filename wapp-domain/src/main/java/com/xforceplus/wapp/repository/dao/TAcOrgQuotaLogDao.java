package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.OrgLogEntity;
import com.xforceplus.wapp.repository.entity.OrgQuotaLogEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TAcOrgQuotaLogDao extends BaseMapper<OrgQuotaLogEntity> {
    void addQuotaLog(@Param("entity") OrgQuotaLogEntity orgQuotaLogEntity);
}
