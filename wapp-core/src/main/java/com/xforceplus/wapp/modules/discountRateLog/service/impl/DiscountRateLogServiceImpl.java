package com.xforceplus.wapp.modules.discountRateLog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.modules.discountRateLog.dto.DiscountRateLogDto;
import com.xforceplus.wapp.modules.discountRateLog.service.DiscountRateLogService;
import com.xforceplus.wapp.modules.discountRateSetting.service.DiscountRateSettingService;
import com.xforceplus.wapp.modules.invoicetaxmapping.dto.InvoiceTaxMappingQuery;
import com.xforceplus.wapp.repository.dao.TAcOrgLogDao;
import com.xforceplus.wapp.repository.dao.TDiscountRateSettingDao;
import com.xforceplus.wapp.repository.entity.OrgEntity;
import com.xforceplus.wapp.repository.entity.OrgLogEntity;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class DiscountRateLogServiceImpl extends ServiceImpl<TAcOrgLogDao, OrgLogEntity> implements DiscountRateLogService {

    @Autowired
    private TAcOrgLogDao tAcOrgLogDao;

    @Override
    public void addDiscountRateLog(OrgLogEntity orgLogEntity) {
        tAcOrgLogDao.addDiscountRateLog(orgLogEntity);
    }

    @Override
    public Page<OrgLogEntity> getDiscountRateLog(DiscountRateLogDto vo) {
        LambdaQueryWrapper<OrgLogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!ObjectUtils.isEmpty(vo.getOrgid()), OrgLogEntity::getOrgid, vo.getOrgid());
        queryWrapper.orderByDesc(OrgLogEntity::getUpdateTime);
        Page<OrgLogEntity> pageRsult = this.page(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
        return pageRsult;
    }

}
