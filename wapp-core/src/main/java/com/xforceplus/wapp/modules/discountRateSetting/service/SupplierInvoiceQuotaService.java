package com.xforceplus.wapp.modules.discountRateSetting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xforceplus.wapp.repository.entity.OrgQuotaLogEntity;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;

import java.util.List;

public interface SupplierInvoiceQuotaService extends IService<TAcOrgEntity> {


    void editQuota(TAcOrgEntity orgEntity);
    Long selectNowQuota(Long orgid);

    List<OrgQuotaLogEntity> selectQuotaLog(Long orgid);
}
