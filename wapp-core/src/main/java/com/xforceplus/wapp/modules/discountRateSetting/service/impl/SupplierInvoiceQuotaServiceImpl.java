package com.xforceplus.wapp.modules.discountRateSetting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.modules.discountRateSetting.service.DiscountRateSettingService;
import com.xforceplus.wapp.modules.discountRateSetting.service.SupplierInvoiceQuotaService;
import com.xforceplus.wapp.repository.dao.TDiscountRateSettingDao;
import com.xforceplus.wapp.repository.entity.OrgQuotaLogEntity;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SupplierInvoiceQuotaServiceImpl extends ServiceImpl<TDiscountRateSettingDao, TAcOrgEntity> implements SupplierInvoiceQuotaService {

    @Autowired
    private TDiscountRateSettingDao tDiscountRateSettingDao;


    @Override
    public void editQuota(TAcOrgEntity orgEntity) {
        tDiscountRateSettingDao.editQuota(orgEntity);
    }

    @Override
    public Long selectNowQuota(Long orgid) {
        return tDiscountRateSettingDao.selectNowQuota(orgid);
    }

    @Override
    public List<OrgQuotaLogEntity> selectQuotaLog(Long orgid){
        return tDiscountRateSettingDao.selectQuotaLog(orgid);
    }
}
