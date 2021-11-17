package com.xforceplus.wapp.modules.backFill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.modules.backFill.model.InvoiceDetail;
import com.xforceplus.wapp.modules.backFill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.backFill.model.RecordInvoiceResponse;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private TXfBlueRelationDao tXfBlueRelationDao;
    /**
     * 正式发票列表
     * @param
     * @return PageResult
     */
    public PageResult<RecordInvoiceResponse> queryPageList(long pageNo,long pageSize,String settlementNo,String invoiceColor,String invoiceStatus,String venderid){
        Page<TDxRecordInvoiceEntity> page=new Page<>(pageNo,pageSize);
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = this.getQueryWrapper(settlementNo, invoiceStatus,venderid,invoiceColor,true);
        Page<TDxRecordInvoiceEntity> pageResult = tDxRecordInvoiceDao.selectPage(page,wrapper);
        List<RecordInvoiceResponse> response = new ArrayList<>();
        RecordInvoiceResponse recordInvoiceResponse = null;
        for (TDxRecordInvoiceEntity recordInvoice : pageResult.getRecords()) {
            recordInvoiceResponse = new RecordInvoiceResponse();
            BeanUtil.copyProperties(recordInvoice,recordInvoiceResponse);
            if(recordInvoice.getTaxRate() != null){
                BigDecimal taxRate = recordInvoice.getTaxRate().divide(BigDecimal.valueOf(100L), 2, RoundingMode.HALF_UP);
                recordInvoiceResponse.setTaxRate(taxRate.toPlainString());
                recordInvoiceResponse.setRedNotificationNo(recordInvoice.getRedNoticeNumber());
            }
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
        //TODO  负数的不能参与匹配，是否可以考虑使用大于 0 ？
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
            return R.fail("当前发票类型或状态无法作废，重新开票前，请开同税率蓝票进行冲抵");
        }
        if(!DateUtils.isCurrentMonth(entity.getInvoiceDate())){
            return R.fail("当前发票类型或状态无法作废，重新开票前，请开同税率蓝票进行冲抵");
        }
        Date updateDate = new Date();
        String settlementNo = entity.getSettlementNo();
        entity.setIsDel(IsDealEnum.YES.getValue());
        entity.setInvoiceStatus(TXfInvoiceStatusEnum.CANCEL.getCode());
        entity.setSettlementNo("");
        entity.setStatusUpdateDate(updateDate);
        int count = tDxRecordInvoiceDao.updateById(entity);
        if(count < 1){
            throw  new EnhanceRuntimeException("删除失败,未找到发票");
        }
        TDxInvoiceEntity tDxInvoiceEntity = new TDxInvoiceEntity();
        tDxInvoiceEntity.setIsdel(IsDealEnum.YES.getValue());
        tDxInvoiceEntity.setUpdateDate(updateDate);
        UpdateWrapper<TDxInvoiceEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq(TDxInvoiceEntity.UUID,entity.getUuid());
        int count1 = tDxInvoiceDao.update(tDxInvoiceEntity,wrapper);
        if(count1 < 1){
            throw  new EnhanceRuntimeException("删除失败,未找到扫描发票");
        }
        //修改预制发票状态为待上传并制空字段
        UpdateWrapper<TXfPreInvoiceEntity> preWrapper = new UpdateWrapper<>();
        preWrapper.eq(TXfPreInvoiceEntity.INVOICE_CODE,entity.getInvoiceCode());
        preWrapper.eq(TXfPreInvoiceEntity.INVOICE_NO,entity.getInvoiceNo());
        TXfPreInvoiceEntity tXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        tXfPreInvoiceEntity.setInvoiceCode("");
        tXfPreInvoiceEntity.setInvoiceNo("");
        tXfPreInvoiceEntity.setMachineCode("");
        tXfPreInvoiceEntity.setPaperDrewDate("");
        tXfPreInvoiceEntity.setCheckCode("");
        tXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        tXfPreInvoiceEntity.setUpdateTime(updateDate);
        int count2 = tXfPreInvoiceDao.update(tXfPreInvoiceEntity,preWrapper);
        if(count2 < 1){
            throw  new EnhanceRuntimeException("删除失败,未找到对应预制发票");
        }
        //修改结算单状态
        if(!updateSettlement(settlementNo,entity.getInvoiceCode(),entity.getInvoiceNo())){
            throw  new EnhanceRuntimeException("删除失败，未找到对应结算单");
        }
        return R.ok("删除成功");
    }

    public boolean updateSettlement(String settlementNo,String invoiceCode,String invoiceNo){
        QueryWrapper<TXfPreInvoiceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO,settlementNo);
        queryWrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS,TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode());
        queryWrapper.ne(TXfPreInvoiceEntity.INVOICE_CODE,invoiceCode);
        queryWrapper.ne(TXfPreInvoiceEntity.INVOICE_NO,invoiceNo);
        List<TXfPreInvoiceEntity> tXfPreInvoices= tXfPreInvoiceDao.selectList(queryWrapper);
        TXfSettlementEntity tXfSettlementEntity = new TXfSettlementEntity();
        tXfSettlementEntity.setUpdateTime(new Date());
        if(CollectionUtils.isEmpty(tXfPreInvoices)){
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        }else{
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode());
        }
        UpdateWrapper<TXfSettlementEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO,settlementNo);
        return tXfSettlementDao.update(tXfSettlementEntity, updateWrapper) >0;
    }

    /**
     * 发票列表
     * @param uuid
     * @return R
     */
    public InvoiceDetailResponse queryInvoiceByUuid(String uuid){
        InvoiceDetailResponse response = new InvoiceDetailResponse();
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceEntity.UUID,uuid);
        TDxRecordInvoiceEntity entity = tDxRecordInvoiceDao.selectOne(wrapper);
        if(entity == null){
            return null;
        }
        List<InvoiceDetail> list = queryInvoiceDetailByUuid(uuid);
        response.setItems(list);
        BeanUtil.copyProperties(entity,response);
        this.convertMain(entity,response);
        return response;
    }
     /**
      * 根据红票查询蓝票发票列表
     * @param invoiceCode
      * @param invoiceNo
     * @return List
     */
    public List<InvoiceDetailResponse> queryBlueInvoice(String  invoiceCode,String invoiceNo){
        QueryWrapper<TXfBlueRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfBlueRelationEntity.RED_INVOICE_CODE,invoiceCode);
        wrapper.eq(TXfBlueRelationEntity.RED_INVOICE_NO,invoiceNo);
        List<TXfBlueRelationEntity> tXfBlueRelationEntities = tXfBlueRelationDao.selectList(wrapper);
        List<InvoiceDetailResponse> response = new ArrayList<>();
        InvoiceDetailResponse invoice;
        for (TXfBlueRelationEntity tXfBlueRelationEntity : tXfBlueRelationEntities) {
            String uuid = tXfBlueRelationEntity.getBlueInvoiceCode()+tXfBlueRelationEntity.getBlueInvoiceNo();
            QueryWrapper<TDxRecordInvoiceEntity> blueWrapper = new QueryWrapper<>();
            blueWrapper.eq(TDxRecordInvoiceEntity.UUID,uuid);
            TDxRecordInvoiceEntity invoiceEntity = tDxRecordInvoiceDao.selectOne(blueWrapper);
            if(invoiceEntity != null){
                invoice = new InvoiceDetailResponse();
                List<InvoiceDetail> list = queryInvoiceDetailByUuid(uuid);
                invoice.setItems(list);
                BeanUtil.copyProperties(invoiceEntity,invoice);
                this.convertMain(invoiceEntity,invoice);
                response.add(invoice);
            }
        }
        return response;
    }

    /**
     * 发票列表
     * @param settlementNo,
     * @param invoiceStatus
     * @param venderid
     * @param invoiceColor
     * @return List
     */
    public List<InvoiceDetailResponse> queryInvoicesBySettlementNo(String settlementNo,String invoiceStatus,String invoiceColor,String venderid) {
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = this.getQueryWrapper(settlementNo, invoiceStatus, venderid, invoiceColor, true);
        List<TDxRecordInvoiceEntity> tDxRecordInvoiceEntities = tDxRecordInvoiceDao.selectList(wrapper);
        List<InvoiceDetailResponse> response = new ArrayList<>();
        InvoiceDetailResponse invoice;
        for (TDxRecordInvoiceEntity invoiceEntity : tDxRecordInvoiceEntities) {
            invoice = new InvoiceDetailResponse();
            List<InvoiceDetail> list = queryInvoiceDetailByUuid(invoiceEntity.getUuid());
            invoice.setItems(list);
            BeanUtil.copyProperties(invoiceEntity, invoice);
            this.convertMain(invoiceEntity, invoice);
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
            wrapper.eq(TDxRecordInvoiceEntity.SETTLEMENT_NO,settlementNo);
        }
        if(StringUtils.isNotEmpty(invoiceStatus)){
            wrapper.eq(TDxRecordInvoiceEntity.INVOICE_STATUS,invoiceStatus);
        }
        if(StringUtils.isNotEmpty(invoiceColor)){
            if(invoiceColor.equals("0")){
                wrapper.le(TDxRecordInvoiceEntity.INVOICE_AMOUNT,0);
            }else{
                wrapper.ge(TDxRecordInvoiceEntity.INVOICE_AMOUNT,0);
            }
        }
        return wrapper;

    }


    public boolean blue4RedInvoice(String redInvoiceNo,String redInvoiceCode){
        TDxRecordInvoiceEntity entity=new TDxRecordInvoiceEntity();
        entity.setInvoiceStatus(InvoiceStatusEnum.INVOICE_STATUS_SEND_BLUE.getCode());
        entity.setStatusUpdateDate(new Date());
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
        invoice.setAmountWithoutTax(entity.getInvoiceAmount());
        invoice.setAmountWithTax(entity.getTotalAmount());
        invoice.setRedNotificationNo(entity.getRedNoticeNumber());
        invoice.setMachineCode(entity.getMachinecode());
        if(entity.getTaxRate() != null){
            BigDecimal taxRate = entity.getTaxRate().divide(BigDecimal.valueOf(100L), 2, RoundingMode.HALF_UP);
            invoice.setTaxRate(taxRate.toPlainString());
        }
    }

    public void convertItem(TDxRecordInvoiceDetailEntity entity,InvoiceDetail invoiceDetail){
        invoiceDetail.setAmountWithoutTax(entity.getDetailAmount());
        invoiceDetail.setId(entity.getId());
        if(StringUtils.isNotEmpty(entity.getDetailAmount())&& StringUtils.isNotEmpty(entity.getTaxAmount())){
            BigDecimal amountWithTax = new BigDecimal(entity.getDetailAmount()).add(new BigDecimal(entity.getTaxAmount()));
            invoiceDetail.setAmountWithTax(amountWithTax.toPlainString());
        }
        invoiceDetail.setTaxAmount(entity.getTaxAmount());
        invoiceDetail.setCargoName(entity.getGoodsName());
        invoiceDetail.setItemSpec(entity.getModel());
        invoiceDetail.setQuantity(entity.getNum());
        invoiceDetail.setQuantityUnit(entity.getUnit());
        invoiceDetail.setUnitPrice(entity.getUnitPrice());
        if(StringUtils.isNotEmpty(entity.getTaxRate())){
            BigDecimal taxRate = new BigDecimal(entity.getTaxRate()).divide(BigDecimal.valueOf(100L), 2, RoundingMode.HALF_UP);
            invoiceDetail.setTaxRate(taxRate.toPlainString());
        }
    }

}
