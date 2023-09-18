package com.xforceplus.wapp.modules.discountRateLog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.modules.discountRateLog.dto.DiscountRateLogDto;
import com.xforceplus.wapp.modules.invoicetaxmapping.dto.InvoiceTaxMappingQuery;
import com.xforceplus.wapp.repository.entity.OrgEntity;
import com.xforceplus.wapp.repository.entity.OrgLogEntity;

public interface DiscountRateLogService extends IService<OrgLogEntity> {

    /**
     * 插入
     * @param orgLogEntity
     */
    void addDiscountRateLog(OrgLogEntity orgLogEntity);
    Page<OrgLogEntity> getDiscountRateLog(DiscountRateLogDto vo);
}
