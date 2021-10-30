package com.xforceplus.wapp.modules.backFill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.TXfInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.backFill.model.InvoiceDetail;
import com.xforceplus.wapp.modules.backFill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.backFill.model.RecordInvoiceResponse;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;

    @Autowired
    private TXfSettlementDao tXfSettlementDao;

    /**
     * 正式发票列表
     * @param
     * @return PageResult
     */
    public PageResult<RecordInvoiceResponse> queryPageList(long pageNo,long pageSize,String settlementNo,String invoiceStatus,String venderid){
        Page<TDxRecordInvoiceEntity> page=new Page<>(pageNo,pageSize);
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = this.getQueryWrapper(settlementNo, invoiceStatus,venderid,null,true);
        Page<TDxRecordInvoiceEntity> pageResult = tDxRecordInvoiceDao.selectPage(page,wrapper);
        List<RecordInvoiceResponse> response = new ArrayList<>();
        RecordInvoiceResponse recordInvoiceResponse = null;
        for (RecordInvoiceResponse recordInvoice : response) {
            recordInvoiceResponse = new RecordInvoiceResponse();
            BeanUtil.copyProperties(recordInvoice,recordInvoiceResponse);
            BigDecimal taxRate = new BigDecimal(recordInvoice.getTaxRate()).divide(BigDecimal.valueOf(100L), 3, RoundingMode.HALF_UP);
            recordInvoiceResponse.setTaxRate(taxRate.toPlainString());
            response.add(recordInvoiceResponse);
        }
        return PageResult.of(response,pageResult.getTotal(), pageResult.getPages(), pageResult.getSize());
    }

    /**
     * 正式发票详情
     * @param
     * @return InvoiceDetailResponse
     */
    public InvoiceDetailResponse getInvoiceById(Long id){
        TDxRecordInvoiceEntity invoiceEntity = tDxRecordInvoiceDao.selectById(id);
        InvoiceDetailResponse response = new InvoiceDetailResponse();
        if(invoiceEntity != null){
            List<InvoiceDetail> invoiceDetails = queryInvoiceDetailByUuid(invoiceEntity.getUuid());
            response.setItems(invoiceDetails);
            BeanUtil.copyProperties(invoiceEntity,response);
            this.convertMain(invoiceEntity,response);
        }
        return response;
    }

    public List<InvoiceDetail> queryInvoiceDetailByUuid(String uuid){
        QueryWrapper<TDxRecordInvoiceDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceDetailEntity.UUID,uuid);
        List<TDxRecordInvoiceDetailEntity> tDxRecordInvoiceDetailEntities = recordInvoiceDetailsDao.selectList(wrapper);
        List<InvoiceDetail> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(tDxRecordInvoiceDetailEntities)){
            InvoiceDetail invoiceDetail;
            for (TDxRecordInvoiceDetailEntity tDxRecordInvoiceDetailEntity : tDxRecordInvoiceDetailEntities) {
                invoiceDetail = new InvoiceDetail();
                this.convertItem(tDxRecordInvoiceDetailEntity,invoiceDetail);
                list.add(invoiceDetail);
            }
        }
        return list;
    }

    private static final String NEGATIVE_SYMBOL = "-";

    /**
     * 根据uuid获取该发票的所有正数明细
     *
     * by Kenny Wong
     *
     * @param uuid
     * @return
     */
    public List<TDxRecordInvoiceDetailEntity> getInvoiceDetailByUuid(String uuid){
        QueryWrapper<TDxRecordInvoiceDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceDetailEntity.UUID, uuid);
        wrapper.ne(TDxRecordInvoiceDetailEntity.DETAIL_AMOUNT, "0");
        // by Kenny Wong 按照明细序号排序，保证每次返回的结果顺序一致
        wrapper.orderByAsc(TDxRecordInvoiceDetailEntity.DETAIL_NO);
        return Optional.ofNullable(recordInvoiceDetailsDao.selectList(wrapper))
                .orElse(Collections.emptyList())
                .stream()
                .filter(v -> !v.getDetailAmount().startsWith(NEGATIVE_SYMBOL))
                .collect(Collectors.toList());
    }


    public Integer getCountBySettlementNo(String settlementNo,String invoiceStatus,String venderid){
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = this.getQueryWrapper(settlementNo, invoiceStatus,venderid,null,false);
        return tDxRecordInvoiceDao.selectCount(wrapper);
    }




    /**
     * 删除红票
     * @param id
     * @return R
     */
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
            return R.fail("当前发票类型或状态无法作废，重新开票前，请开同税率蓝票进行冲抵");
        }
        entity.setIsDel(IsDealEnum.YES.getValue());
        entity.setInvoiceStatus(TXfInvoiceStatusEnum.CANCEL.getCode());
        int count = tDxRecordInvoiceDao.updateById(entity);
        TDxInvoiceEntity tDxInvoiceEntity = new TDxInvoiceEntity();
        tDxInvoiceEntity.setIsdel(IsDealEnum.YES.getValue());
        UpdateWrapper<TDxInvoiceEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq(TDxInvoiceEntity.UUID,entity.getUuid());
        int count1 = tDxInvoiceDao.update(tDxInvoiceEntity,wrapper);
        if(count1 < 1){
            throw  new EnhanceRuntimeException("删除失败");
        }
        //修改预制发票状态为待上传
        QueryWrapper<TXfPreInvoiceEntity> preWrapper = new QueryWrapper<>();
        preWrapper.eq(TXfPreInvoiceEntity.INVOICE_CODE,entity.getInvoiceCode());
        preWrapper.eq(TXfPreInvoiceEntity.INVOICE_NO,entity.getInvoiceNo());
        int count2 = tXfPreInvoiceDao.delete(preWrapper);
        if(count2 < 1){
            throw  new EnhanceRuntimeException("删除失败");
        }
        //修改结算单状态
        QueryWrapper<TDxRecordInvoiceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TDxRecordInvoiceEntity.SETTLEMENTNO,entity.getSettlementNo());
        TXfSettlementEntity tXfSettlementEntity = new TXfSettlementEntity();
        List<TDxRecordInvoiceEntity> tDxRecordInvoiceEntities = tDxRecordInvoiceDao.selectList(queryWrapper);
        if(tDxRecordInvoiceEntities.stream().allMatch(t ->t.getIsDel().equals(TXfInvoiceStatusEnum.CANCEL.getCode()))){
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getCode());
        }else{
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode());
        }
        UpdateWrapper<TXfSettlementEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO,entity.getSettlementNo());
        int count3 = tXfSettlementDao.update(tXfSettlementEntity, updateWrapper);
        if(count3 < 1){
            throw  new EnhanceRuntimeException("删除失败");
        }
        return R.ok("删除成功");
    }

    /**
     * 发票列表
     * @param settlementNo,invoiceStatus,venderid
     * @param invoiceStatus
     * @param venderid
     * @return R
     */
    public List<InvoiceDetailResponse> queryInvoiceList(String settlementNo,String invoiceStatus,String invoiceColor,String venderid){
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = this.getQueryWrapper(settlementNo, invoiceStatus,venderid,invoiceColor,true);
        List<TDxRecordInvoiceEntity> tDxRecordInvoiceEntities = tDxRecordInvoiceDao.selectList(wrapper);
        List<InvoiceDetailResponse> response = new ArrayList<>();
        InvoiceDetailResponse invoice;
        for (TDxRecordInvoiceEntity invoiceEntity : tDxRecordInvoiceEntities) {
            invoice = new InvoiceDetailResponse();
            List<InvoiceDetail> list = queryInvoiceDetailByUuid(invoiceEntity.getUuid());
            invoice.setItems(list);
            BeanUtil.copyProperties(invoiceEntity,invoice);
            this.convertMain(invoiceEntity,invoice);
            response.add(invoice);
        }
        return response;
    }


    private QueryWrapper<TDxRecordInvoiceEntity> getQueryWrapper(String settlementNo,String invoiceStatus,String venderid,String invoiceColor,boolean isOrder){
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
        if(isOrder){
            wrapper.orderByDesc(TDxRecordInvoiceEntity.ID);
        }
        wrapper.eq(TDxRecordInvoiceEntity.IS_DEL, IsDealEnum.NO.getValue());
        if(StringUtils.isNotEmpty(venderid)){
            wrapper.eq(TDxRecordInvoiceEntity.VENDERID,venderid);
        }
        if(StringUtils.isNotEmpty(settlementNo)){
            wrapper.eq(TDxRecordInvoiceEntity.SETTLEMENTNO,settlementNo);
        }
        if(StringUtils.isNotEmpty(invoiceStatus)){
            wrapper.eq(TDxRecordInvoiceEntity.INVOICE_STATUS,invoiceStatus);
        }
        if(StringUtils.isNotEmpty(invoiceColor)){
            if(invoiceColor.equals("0")){
                wrapper.lt(TDxRecordInvoiceEntity.INVOICE_AMOUNT,0);
            }else{
                wrapper.gt(TDxRecordInvoiceEntity.INVOICE_AMOUNT,0);
            }
        }
        return wrapper;

    }


    public boolean blue4RedInvoice(String redInvoiceNo,String redInvoiceCode){
        TDxRecordInvoiceEntity entity=new TDxRecordInvoiceEntity();
        entity.setInvoiceStatus("5");
        LambdaUpdateWrapper<TDxRecordInvoiceEntity> wrapper=new LambdaUpdateWrapper<>();
        wrapper.eq(TDxRecordInvoiceEntity::getUuid,redInvoiceCode+redInvoiceNo);
        tDxRecordInvoiceDao.update(entity,wrapper);
        return true;

    }




    public void convertMain(TDxRecordInvoiceEntity entity,InvoiceDetailResponse invoice){
        invoice.setPurchaserAddressAndPhone(entity.getGfAddressAndPhone());
        invoice.setPurchaserBankAndNo(entity.getGfBankAndNo());
        invoice.setPurchaserName(entity.getGfName());
        invoice.setPurchaserTaxNo(entity.getGfTaxNo());
        invoice.setSellerAddressAndPhone(entity.getXfAddressAndPhone());
        invoice.setSellerBankAndNo(entity.getXfBankAndNo());
        invoice.setSellerName(entity.getXfName());
        invoice.setSellerTaxNo(entity.getXfTaxNo());
        invoice.setPaperDrewDate(entity.getInvoiceDate());
    }

    public void convertItem(TDxRecordInvoiceDetailEntity entity,InvoiceDetail invoiceDetail){
        invoiceDetail.setAmountWithTax(entity.getDetailAmount());
        BigDecimal amountWithTax = new BigDecimal(entity.getDetailAmount()).add(new BigDecimal(entity.getTaxAmount()));
        invoiceDetail.setAmountWithTax(amountWithTax.toPlainString());
        invoiceDetail.setTaxAmount(entity.getTaxAmount());
        invoiceDetail.setCargoName(entity.getGoodsName());
        invoiceDetail.setItemSpec(entity.getModel());
        invoiceDetail.setQuantity(entity.getNum());
        invoiceDetail.setQuantityUnit(entity.getUnit());
        invoiceDetail.setUnitPrice(entity.getUnitPrice());
        invoiceDetail.setTaxRate(entity.getTaxRate());
    }

}
