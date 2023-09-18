package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.repository.entity.OrgEntity;
import com.xforceplus.wapp.repository.entity.OrgQuotaLogEntity;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TDxCustomsEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TDiscountRateSettingDao extends BaseMapper<TAcOrgEntity> {

    void editDiscountRate(@Param("entity") TAcOrgEntity orgEntity);
    void editQuota(@Param("entity") TAcOrgEntity orgEntity);
    int findOrgByUserCode(@Param("userCode") String userCode);
    TAcOrgEntity selectOrg(@Param("orgid") int orgid);

    Long selectNowDiscountRate(@Param("orgid") Long orgid);
    Long selectNowQuota(@Param("orgid") Long orgid);
    List<OrgQuotaLogEntity> selectQuotaLog(@Param("orgid") Long orgid);

    List<TAcOrgEntity> queryPage(@Param("offset")Integer offset, @Param("next")Integer next,
                                            @Param("orgCode")String orgCode, @Param("orgName") String orgName);

}
