package com.xforceplus.wapp.modules.discountRateLog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.discountRateLog.dto.DiscountRateLogDto;
import com.xforceplus.wapp.modules.discountRateLog.service.DiscountRateLogService;
import com.xforceplus.wapp.modules.discountRateLog.service.SupplierInvoiceQuotaLogService;
import com.xforceplus.wapp.repository.dao.TAcOrgLogDao;
import com.xforceplus.wapp.repository.dao.TAcOrgQuotaLogDao;
import com.xforceplus.wapp.repository.entity.OrgLogEntity;
import com.xforceplus.wapp.repository.entity.OrgQuotaLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class SupplierInvoiceQuotaLogServiceImpl extends ServiceImpl<TAcOrgQuotaLogDao, OrgQuotaLogEntity> implements SupplierInvoiceQuotaLogService {

    @Autowired
    private TAcOrgQuotaLogDao tAcOrgLogDao;

    @Override
    public void addQuotaLog(OrgQuotaLogEntity orgQuotaLogEntity) {
        tAcOrgLogDao.addQuotaLog(orgQuotaLogEntity);
    }
}
