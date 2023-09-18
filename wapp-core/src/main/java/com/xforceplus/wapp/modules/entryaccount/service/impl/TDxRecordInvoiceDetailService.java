package com.xforceplus.wapp.modules.entryaccount.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDetailDao;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: ChenHang
 * @Date: 2023/7/17 17:08
 */
@Service
public class TDxRecordInvoiceDetailService extends ServiceImpl<TDxRecordInvoiceDetailDao, TDxRecordInvoiceDetailEntity> {

    /**
     * 根据uuid查询发票明细信息
     * @param uuid
     * @param taxRate
     * @return
     */
    public List<TDxRecordInvoiceDetailEntity> queryByUuidTaxRate(String uuid, BigDecimal taxRate) {
        LambdaQueryWrapper<TDxRecordInvoiceDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(uuid), TDxRecordInvoiceDetailEntity::getUuid, uuid);
        queryWrapper.eq(!ObjectUtils.isEmpty(taxRate), TDxRecordInvoiceDetailEntity::getTaxRate, taxRate);
        return this.list(queryWrapper);
    }
}
