package com.xforceplus.wapp.modules.backFill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.apollo.utils.business.InvoiceType;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.TXfInvoiceStatusEnum;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by SunShiyong on 2021/10/16.
 * 底账数据服务
 */
@Service
@Slf4j
public class RecordInvoiceService {
    @Autowired
    TDxRecordInvoiceDao tDxRecordInvoiceDao;

    @Autowired
    TXfPreInvoiceDao tXfPreInvoiceDao;

    public List<TDxRecordInvoiceEntity> getInvocieBySettlementNo(String settlementNo,String invoiceStatus){
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper();
        wrapper.eq(TDxRecordInvoiceEntity.SETTLEMENTNO,settlementNo)
        .eq(TDxRecordInvoiceEntity.INVOICE_STATUS,invoiceStatus)
        .eq(TDxRecordInvoiceEntity.IS_DEL,"0");
        List<TDxRecordInvoiceEntity> tDxRecordInvoiceEntities = tDxRecordInvoiceDao.selectList(wrapper);
        return tDxRecordInvoiceEntities;
    }

    @Transactional
    public R deleteInvoice(Long id){
        TDxRecordInvoiceEntity entity = tDxRecordInvoiceDao.selectById(id);
        if(entity == null){
            return R.fail("根据id未找到发票");
        }
        if(entity.getInvoiceAmount().compareTo(BigDecimal.ZERO) > 0){
            return R.fail("蓝票不允许删除");
        }
        if(InvoiceTypeEnum.isElectronic(entity.getInvoiceType())) {
            return R.fail("电票不允许删除");
        }
        if(!DateUtils.isCurrentMonth(entity.getInvoiceDate())){
            return R.fail("不是当月开的发票不允许删除");
        }
        entity.setIsDel("1");
        entity.setInvoiceStatus(TXfInvoiceStatusEnum.CANCEL.getCode());
        tDxRecordInvoiceDao.updateById(entity);
        return R.ok("删除成功");
    }

}
