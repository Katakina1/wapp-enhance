package com.xforceplus.wapp.modules.backFill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.TXfInvoiceStatusEnum;
import com.xforceplus.wapp.modules.backFill.model.InvoiceDetail;
import com.xforceplus.wapp.modules.backFill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.backFill.model.RecordInvoiceResponse;
import com.xforceplus.wapp.repository.dao.TDxInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDetailDao;
import com.xforceplus.wapp.repository.entity.TDxInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SunShiyong on 2021/10/16.
 * 底账数据服务
 */
@Service
@Slf4j
public class RecordInvoiceService extends ServiceImpl<TDxRecordInvoiceDao, TDxRecordInvoiceEntity> {
    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;

    @Autowired
    private TDxInvoiceDao tDxInvoiceDao;

    @Autowired
    private TDxRecordInvoiceDetailDao recordInvoiceDetailsDao;

    public PageResult<RecordInvoiceResponse> queryPageList(long pageNo,long pageSize,String settlementNo,String invoiceStatus,String venderid){
        Page<TDxRecordInvoiceEntity> page=new Page<>(pageNo,pageSize);
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = this.getQueryWrapper(settlementNo, invoiceStatus,venderid);
        Page<TDxRecordInvoiceEntity> pageResult = tDxRecordInvoiceDao.selectPage(page,wrapper);
        List<RecordInvoiceResponse> response = new ArrayList<>();
        BeanUtil.copyList(pageResult.getRecords(),response,RecordInvoiceResponse.class);
        return PageResult.of(response,pageResult.getTotal(), pageResult.getPages(), pageResult.getSize());
    }


    public InvoiceDetailResponse getInvoiceById(Long id){
        TDxRecordInvoiceEntity invoiceEntity = tDxRecordInvoiceDao.selectById(id);
        InvoiceDetailResponse response = new InvoiceDetailResponse();
        if(invoiceEntity != null){
            QueryWrapper<TDxRecordInvoiceDetailEntity> wrapper = new QueryWrapper<>();
            wrapper.eq(TDxRecordInvoiceDetailEntity.UUID,invoiceEntity.getUuid());
            List<TDxRecordInvoiceDetailEntity> tDxRecordInvoiceDetailEntities = recordInvoiceDetailsDao.selectList(wrapper);
            if(CollectionUtils.isNotEmpty(tDxRecordInvoiceDetailEntities)){
                List<InvoiceDetail> list = new ArrayList<>();
                BeanUtil.copyList(tDxRecordInvoiceDetailEntities,list,InvoiceDetail.class);
                response.setInvoiceDetailList(list);
            }
            BeanUtil.copyProperties(invoiceEntity,response);
        }
        return response;
    }

    public Integer getCountBySettlementNo(String settlementNo,String invoiceStatus,String venderid){
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = this.getQueryWrapper(settlementNo, invoiceStatus,venderid);
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

    private QueryWrapper<TDxRecordInvoiceEntity> getQueryWrapper(String settlementNo,String invoiceStatus,String venderid){
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceEntity.IS_DEL, IsDealEnum.NO.getValue())
        .eq(TDxRecordInvoiceEntity.VENDERID,venderid);
        if(StringUtils.isNotEmpty(settlementNo)){
            wrapper.eq(TDxRecordInvoiceEntity.SETTLEMENTNO,settlementNo);
        }
        if(StringUtils.isNotEmpty(invoiceStatus)){
            wrapper.eq(TDxRecordInvoiceEntity.INVOICE_STATUS,invoiceStatus);
        }
        return wrapper;

    }


}
