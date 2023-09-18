package com.xforceplus.wapp.modules.discountRateLog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xforceplus.wapp.modules.discountRateLog.dto.DiscountRateLogDto;
import com.xforceplus.wapp.repository.entity.OrgLogEntity;
import com.xforceplus.wapp.repository.entity.OrgQuotaLogEntity;

public interface SupplierInvoiceQuotaLogService extends IService<OrgQuotaLogEntity> {

    /**
     * 插入
     * @param orgQuotaLogEntity
     */
    void addQuotaLog(OrgQuotaLogEntity orgQuotaLogEntity);
}
