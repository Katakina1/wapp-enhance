package com.xforceplus.wapp.modules.discountRateSetting.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.modules.invoicetaxmapping.dto.InvoiceTaxMappingQuery;
import com.xforceplus.wapp.repository.entity.OrgEntity;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;

import java.util.Map;

public interface DiscountRateSettingService extends IService<TAcOrgEntity> {

    int findOrgByUserCode(String userCode);
    TAcOrgEntity selectOrg(Integer orgid);
    /**
     * 修改
     * @param orgEntity
     */
    void editDiscountRate(TAcOrgEntity orgEntity);
    Long selectNowDiscountRate(Long orgid);
}
