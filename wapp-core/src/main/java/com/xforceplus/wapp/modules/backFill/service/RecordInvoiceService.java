package com.xforceplus.wapp.modules.backFill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xforceplus.apollo.utils.business.InvoiceType;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.TXfInvoiceStatusEnum;
import com.xforceplus.wapp.repository.dao.TDxInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.entity.TDxInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
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
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;

    @Autowired
    private TDxInvoiceDao tDxInvoiceDao;

    public List<TDxRecordInvoiceEntity> getListBySettlementNo(String settlementNo,String invoiceStatus){
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceEntity.SETTLEMENTNO,settlementNo)
        .eq(TDxRecordInvoiceEntity.INVOICE_STATUS,invoiceStatus)
        .eq(TDxRecordInvoiceEntity.IS_DEL, IsDealEnum.NO.getValue());
        return tDxRecordInvoiceDao.selectList(wrapper);
    }

    public Integer getCountBySettlementNo(String settlementNo,String invoiceStatus){
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceEntity.SETTLEMENTNO,settlementNo)
        .eq(TDxRecordInvoiceEntity.INVOICE_STATUS,invoiceStatus)
        .eq(TDxRecordInvoiceEntity.IS_DEL,IsDealEnum.NO.getValue());
        return tDxRecordInvoiceDao.selectCount(wrapper);
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
        entity.setIsDel(IsDealEnum.YES.getValue());
        entity.setInvoiceStatus(TXfInvoiceStatusEnum.CANCEL.getCode());
        int count = tDxRecordInvoiceDao.updateById(entity);
        TDxInvoiceEntity tDxInvoiceEntity = new TDxInvoiceEntity();
        tDxInvoiceEntity.setIsdel(IsDealEnum.YES.getValue());
        UpdateWrapper<TDxInvoiceEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq(TDxInvoiceEntity.UUID,entity.getUuid());
        int count1 = tDxInvoiceDao.update(tDxInvoiceEntity,wrapper);
        if(count > 0 && count1 >0){
            return R.ok("删除成功");
        }else {
            return R.fail("删除失败");
        }
    }

}
