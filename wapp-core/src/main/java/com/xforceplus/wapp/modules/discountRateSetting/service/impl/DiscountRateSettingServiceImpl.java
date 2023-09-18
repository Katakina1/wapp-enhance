package com.xforceplus.wapp.modules.discountRateSetting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.evat.common.entity.TInvoiceTaxMappingEntity;
import com.xforceplus.wapp.modules.discountRateSetting.service.DiscountRateSettingService;
import com.xforceplus.wapp.modules.invoicetaxmapping.dto.InvoiceTaxMappingQuery;
import com.xforceplus.wapp.repository.dao.TDiscountRateSettingDao;
import com.xforceplus.wapp.repository.dao.TInvoiceTaxMappingDao;
import com.xforceplus.wapp.repository.entity.OrgEntity;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Slf4j
@Service
public class DiscountRateSettingServiceImpl extends ServiceImpl<TDiscountRateSettingDao, TAcOrgEntity> implements DiscountRateSettingService {

    @Autowired
    private TDiscountRateSettingDao tDiscountRateSettingDao;

    /**
     * 通过userCode获取ORGid
     * @param userCode
     * @return
     */
    @Override
    public int findOrgByUserCode(String userCode) {
        return tDiscountRateSettingDao.findOrgByUserCode(userCode);
    }

    @Override
    public TAcOrgEntity selectOrg(Integer orgid) {
        return tDiscountRateSettingDao.selectOrg(orgid);
    }

    @Override
    public void editDiscountRate(TAcOrgEntity orgEntity) {
        tDiscountRateSettingDao.editDiscountRate(orgEntity);
    }



    @Override
    public Long selectNowDiscountRate(Long orgid) {
        return tDiscountRateSettingDao.selectNowDiscountRate(orgid);
    }
}
