package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.config.TaxRateConfig;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListRequest;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListResponse;
import com.xforceplus.wapp.modules.deduct.model.*;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeServiceImpl;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.sequence.IDSequence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;

/**
 * 类描述：扣除单通用方法
 *
 * @ClassName DeductionService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 11:38
 */
@Service
@Slf4j
public class DeductService   {
    @Autowired
    protected TXfBillDeductExtDao  tXfBillDeductExtDao;
    @Autowired
    protected TXfBillDeductItemExtDao tXfBillDeductItemExtDao;

    @Autowired
    private TXfBillDeductItemRefExtDao tXfBillDeductItemRefDao;
    @Autowired
    protected TXfSettlementDao tXfSettlementDao;
    @Autowired
    protected TXfSettlementItemDao tXfSettlementItemDao;
    @Autowired
    protected   IDSequence idSequence;
    @Autowired
    private TaxRateConfig taxRateConfig;
    @Autowired
    protected TaxCodeServiceImpl taxCodeService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    protected BlueInvoiceService blueInvoiceService;
    @Autowired
    protected TXfBillDeductInvoiceDao tXfBillDeductInvoiceDao;
    @PostConstruct
    public void initData() {
        int no = 1001121107;
        /**
         * 索赔单 主信息
         */
        int amount = 10;
        List<DeductBillBaseData> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ClaimBillData deductBillBaseData = new ClaimBillData();
            deductBillBaseData.setAmountWithoutTax(new BigDecimal(amount*2*(i+1)+i));
            deductBillBaseData.setBusinessNo(idSequence.nextId().toString());
            deductBillBaseData.setAmountWithTax(new BigDecimal(10));
            deductBillBaseData.setBusinessType(XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue());
            deductBillBaseData.setBatchNo("BT112312312312");
            deductBillBaseData.setDeductDate(new Date());
            deductBillBaseData.setPurchaserNo("PT");
            deductBillBaseData.setSellerNo("172164");
            deductBillBaseData.setRemark("索赔");
            deductBillBaseData.setTaxAmount(new BigDecimal("0.13").multiply(deductBillBaseData.getAmountWithoutTax()).setScale(2, RoundingMode.HALF_UP));
            no = no + 1;
            deductBillBaseData.setBusinessNo(no + StringUtils.EMPTY);
            deductBillBaseData.setTaxRate(new BigDecimal("0.13"));
            deductBillBaseData.setStoreType("smas");
            deductBillBaseData.setVerdictDate(new Date());
            deductBillBaseData.setInvoiceReference("invoice00022222");
            dataList.add(deductBillBaseData);
        }
        List<ClaimBillItemData> res = new ArrayList<>();
        amount = 6;
        for (int i = 0; i < 10; i++) {
            int tmp = amount*i + i;
            ClaimBillItemData claimBillItemData = new ClaimBillItemData();
            claimBillItemData.setAmountWithoutTax(new BigDecimal(tmp));
            claimBillItemData.setSellerNo("337852");
            claimBillItemData.setDeptNbr("114");
            claimBillItemData.setPrice(new BigDecimal(1));
            claimBillItemData.setQuantity(new BigDecimal(tmp));
            claimBillItemData.setItemNo("20280238");
            claimBillItemData.setTaxRate(new BigDecimal("0.13"));
            claimBillItemData.setCategoryNbr("30");
            claimBillItemData.setCnDesc("小林刻立眼镜清洁纸");
            claimBillItemData.setStoreNbr("WI");
            claimBillItemData.setUnit("包");
            claimBillItemData.setUpc("111");
            claimBillItemData.setVerdictDate(new Date());
            claimBillItemData.setVnpkQuantity(new BigDecimal(6));
            claimBillItemData.setVnpkCost(new BigDecimal(10));
            res.add(claimBillItemData);
        }
        amount = 5;
        for (int i = 0; i < 10; i++) {
            int tmp = amount*i + i;
            ClaimBillItemData claimBillItemData = new ClaimBillItemData();
            claimBillItemData.setAmountWithoutTax(new BigDecimal(tmp));
            claimBillItemData.setSellerNo("337852");
            claimBillItemData.setDeptNbr("114");
            claimBillItemData.setPrice(new BigDecimal(1));
            claimBillItemData.setQuantity(new BigDecimal(tmp));
            claimBillItemData.setItemNo("20280238");
            claimBillItemData.setTaxRate(new BigDecimal("0.09"));
            claimBillItemData.setCategoryNbr("30");
            claimBillItemData.setCnDesc("小林刻立眼镜清洁纸");
            claimBillItemData.setStoreNbr("WI");
            claimBillItemData.setUnit("包");
            claimBillItemData.setUpc("111");
            claimBillItemData.setVerdictDate(new Date());
            claimBillItemData.setVnpkQuantity(new BigDecimal(6));
            claimBillItemData.setVnpkCost(new BigDecimal(10));
            res.add(claimBillItemData);
        }
        amount = 4;
        for (int i = 0; i < 10; i++) {
            int tmp = amount*i + i;
            ClaimBillItemData claimBillItemData = new ClaimBillItemData();
            claimBillItemData.setAmountWithoutTax(new BigDecimal(tmp));
            claimBillItemData.setSellerNo("337852");
            claimBillItemData.setDeptNbr("WI2");
            claimBillItemData.setPrice(new BigDecimal(1));
            claimBillItemData.setQuantity(new BigDecimal(tmp));
            claimBillItemData.setItemNo("20280238");
            claimBillItemData.setTaxRate(new BigDecimal("0.09"));
            claimBillItemData.setCategoryNbr("30");
            claimBillItemData.setCnDesc("小林刻立眼镜清洁纸");
            claimBillItemData.setStoreNbr("114");
            claimBillItemData.setUnit("包");
            claimBillItemData.setUpc("111");
            claimBillItemData.setVerdictDate(new Date());
            claimBillItemData.setVnpkQuantity(new BigDecimal(6));
            claimBillItemData.setVnpkCost(new BigDecimal(10));
            res.add(claimBillItemData);
        }
        amount = 3;
        for (int i = 0; i < 10; i++) {
            int tmp = amount*i + i;
            ClaimBillItemData claimBillItemData = new ClaimBillItemData();
            claimBillItemData.setAmountWithoutTax(new BigDecimal(tmp));
            claimBillItemData.setSellerNo("279771");
            claimBillItemData.setDeptNbr("WI");
            claimBillItemData.setPrice(new BigDecimal(1));
            claimBillItemData.setQuantity(new BigDecimal(tmp));
            claimBillItemData.setItemNo("20280238");
            claimBillItemData.setTaxRate(new BigDecimal("0.09"));
            claimBillItemData.setCategoryNbr("30");
            claimBillItemData.setCnDesc("小林刻立眼镜清洁纸");
            claimBillItemData.setStoreNbr("114");
            claimBillItemData.setUnit("包");
            claimBillItemData.setUpc("111");
            claimBillItemData.setVerdictDate(new Date());
            claimBillItemData.setVnpkQuantity(new BigDecimal(6));
            claimBillItemData.setVnpkCost(new BigDecimal(10));
            res.add(claimBillItemData);
        }
         //receiveItemData(res, "");
         // receiveData(dataList, XFDeductionBusinessTypeEnum.CLAIM_BILL);
          // receiveDone(XFDeductionBusinessTypeEnum.CLAIM_BILL);
//        int amount = 10;
//        List<DeductBillBaseData> dataList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            AgreementBillData deductBillBaseData = new AgreementBillData();
//            deductBillBaseData.setAmountWithoutTax(new BigDecimal(amount*2*(i+1)-i).negate());
//            deductBillBaseData.setBusinessNo(idSequence.nextId().toString());
//            deductBillBaseData.setBusinessType(XFDeductionBusinessTypeEnum.AGREEMENT_BILL.getType());
//            deductBillBaseData.setBatchNo("BT112312312312");
//            deductBillBaseData.setDeductDate(new Date());
//            deductBillBaseData.setPurchaserNo("PT");
//            deductBillBaseData.setSellerNo("172164");
//            deductBillBaseData.setRemark("索赔");
//            deductBillBaseData.setTaxAmount(new BigDecimal("0.13").multiply(deductBillBaseData.getAmountWithoutTax()).setScale(2, RoundingMode.HALF_UP));
//             deductBillBaseData.setTaxRate(new BigDecimal("0.13"));
//            deductBillBaseData.setAmountWithTax(deductBillBaseData.getAmountWithoutTax().add(deductBillBaseData.getTaxAmount()));
//
//            deductBillBaseData.setMemo("172164");
//            deductBillBaseData.setReasonCode("reasonCode" + i);
//            deductBillBaseData.setReferenceType("ko");
//            deductBillBaseData.setDocumentNo("DocumentNo" + i);
//            deductBillBaseData.setDocumentType("LK" );
//            deductBillBaseData.setTaxCode("tx");
//            dataList.add(deductBillBaseData);
//        }
      // receiveData(dataList, XFDeductionBusinessTypeEnum.AGREEMENT_BILL);
      //receiveDone(XFDeductionBusinessTypeEnum.AGREEMENT_BILL);

    }
    /**
     * 接收索赔明细
     * 会由不同线程调用，每次调用，数据不会重复，由上游保证
     * @param
     * @return
     */
    public boolean receiveItemData(List<ClaimBillItemData> claimBillItemDataList ,String batchNo ) {
        List<TXfBillDeductItemEntity> list =  transferBillItemData(claimBillItemDataList,batchNo);
        for (TXfBillDeductItemEntity tXfBillDeductItemEntity : list) {
            tXfBillDeductItemExtDao.insert(tXfBillDeductItemEntity);
        }
        return true;
    }

    public List<TXfBillDeductItemEntity> transferBillItemData(List<ClaimBillItemData> claimBillItemDataList ,String batchNo) {
        Date date = new Date();
        List<TXfBillDeductItemEntity> list = new ArrayList<>();
        for (ClaimBillItemData claimBillItemData : claimBillItemDataList) {
            TXfBillDeductItemEntity tmp = new TXfBillDeductItemEntity();
            if (Objects.isNull(claimBillItemData)) {
                continue;
            }
            BeanUtils.copyProperties(claimBillItemData, tmp);
            tmp.setGategoryNbr(claimBillItemData.getCategoryNbr());
            tmp.setVnpkQuantity(claimBillItemData.getVnpkQuantity().intValue());
            tmp.setPurchaserNo(claimBillItemData.getStoreNbr());
            tmp.setCreateDate(date);
            tmp.setId(idSequence.nextId());
            tmp.setRemainingAmount(claimBillItemData.getAmountWithoutTax());
            tmp.setGoodsNoVer("33.0");
            tmp.setUpdateDate(tmp.getCreateDate());
            tmp = fixTaxCode(tmp);
            list.add(tmp);
        }
        return list;
    }

    /**
     * 补充商品
     * @param entity
     * @return
     */
    private TXfBillDeductItemEntity fixTaxCode(  TXfBillDeductItemEntity entity) {
        Optional<TaxCode> taxCodeOptional = taxCodeService.getTaxCodeByItemNo(entity.getItemNo());
        if (taxCodeOptional.isPresent()) {
                TaxCode taxCode = taxCodeOptional.get();
                entity.setGoodsTaxNo(taxCode.getGoodsTaxNo());
                entity.setTaxPre(taxCode.getTaxPre());
                entity.setTaxPreCon(taxCode.getTaxPreCon());
                entity.setZeroTax(taxCode.getZeroTax());
                entity.setItemShortName(taxCode.getSmallCategoryName());
        }
//        entity.setGoodsTaxNo("123123");
//        entity.setTaxPre(StringUtils.EMPTY);
//        entity.setTaxPreCon(StringUtils.EMPTY);
//        entity.setZeroTax(StringUtils.EMPTY);
//        entity.setItemShortName(StringUtils.EMPTY);
        return entity;
    }
    /**
     * 接收索赔 协议 EPD主信息数据
     * @param deductBillBaseDataList
     * @param deductionEnum
     * @return
     */
    public boolean receiveData(List<DeductBillBaseData> deductBillBaseDataList, XFDeductionBusinessTypeEnum deductionEnum) {
        List<TXfBillDeductEntity> list = transferBillData(deductBillBaseDataList, deductionEnum);
        for (TXfBillDeductEntity tXfBillDeductEntity : list) {
            unlockAndCancel(deductionEnum, tXfBillDeductEntity );
            tXfBillDeductExtDao.insert(tXfBillDeductEntity);
        }

        return true;
    }

    public List<TXfBillDeductEntity> transferBillData(List<DeductBillBaseData> deductBillDataList ,  XFDeductionBusinessTypeEnum deductionEnum) {
        Date date = new Date();
        List<TXfBillDeductEntity> list = new ArrayList<>();
        Optional<DeductionHandleEnum> optionalDedcutionHandleEnum = DeductionHandleEnum.getHandleEnum( deductionEnum);
        if (!optionalDedcutionHandleEnum.isPresent()) {
            throw new EnhanceRuntimeException("","无效的单价类型");
        }
        DeductionHandleEnum dedcutionHandleEnum = optionalDedcutionHandleEnum.get();
        for (DeductBillBaseData deductBillBaseData : deductBillDataList) {
            TXfBillDeductEntity tmp = dedcutionHandleEnum.function.apply(deductBillBaseData);
            tmp.setCreateDate(date);
            tmp.setUpdateDate(tmp.getCreateDate());
            tmp.setId(idSequence.nextId());
            list.add(tmp);
         }
        return list;
    }

    /**
     * 自动取消和解锁 自动解锁当天新增的EPD 协议单
     * @param deductionEnum
     * @return true - 操作成功，不存在失败的场景
     */
    public boolean unlockAndCancel(XFDeductionBusinessTypeEnum deductionEnum, TXfBillDeductEntity tXfBillDeductEntity) {
        if (Objects.equals(deductionEnum, XFDeductionBusinessTypeEnum.AGREEMENT_BILL)
                || Objects.equals(deductionEnum,XFDeductionBusinessTypeEnum.EPD_BILL)) {
            // 获取协议单号
            String reference = tXfBillDeductEntity.getAgreementReference();
            if (Objects.nonNull(reference)) {
                // 查找相同协议号的数据，取第一页数据，分页大小没有特殊要求，默认设置成10
                Page<TXfBillDeductEntity> pages = tXfBillDeductExtDao
                        .selectPage(new Page<>(1, 10),
                                new QueryWrapper<TXfBillDeductEntity>()
                                        .lambda()
                                        .eq(TXfBillDeductEntity::getBusinessType, deductionEnum.getValue())
                                        .eq(TXfBillDeductEntity::getAgreementReference, reference)
                        );
                log.info("根据单据id={}的匹配到的拥有相同的reference={}的单据数量为{}", tXfBillDeductEntity.getId(), reference, pages.getTotal());
                if (pages.getTotal() > 0) {
                    //  如果返回个数大于1，则取第一条记录
                    TXfBillDeductEntity target = pages.getRecords().get(0);
                    if (sameParties(tXfBillDeductEntity, target)) {
                        if (Objects.equals(TXfBillDeductStatusEnum.LOCK.getCode(), target.getLockFlag())){
                            boolean result = updateBillStatus(deductionEnum, target, TXfBillDeductStatusEnum.UNLOCK);
                            log.info("解锁source单据id={}的target单据的id={}, result={}", tXfBillDeductEntity.getId(), target.getId(), result);
                        }
                        tXfBillDeductEntity.setStatus(target.getStatus());
                    } else {
                        log.info("source单据id={}与target单据的id={}购销对不一致，跳过解锁取消逻辑", tXfBillDeductEntity.getId(), target.getId());
                    }
                }
            } else {
                log.warn("非法的单据id={}，协议号agreement reference为空，跳过解锁取消逻辑", tXfBillDeductEntity.getId());
            }
        }
        return true;
    }

    /**
     * 判断两个单据的交易双方是否一致
     *
     * @param source
     * @param target
     * @return
     */
    private boolean sameParties(TXfBillDeductEntity source, TXfBillDeductEntity target){
        if (Objects.isNull(source) || Objects.isNull(target)) {
            return false;
        }
        return Objects.equals(source.getPurchaserNo(), target.getPurchaserNo())
                && Objects.equals(source.getSellerNo(), target.getSellerNo());
    }

    /**
     * 更新协议单或EPD单
     *
     * @param deductionEnum {@link XFDeductionBusinessTypeEnum} 单据类型
     * @param tXfBillDeductEntity {@link TXfBillDeductEntity} 单据实体
     * @param status {@link TXfBillDeductStatusEnum} 业务单状态
     * @return {boolean} true-更新成功, false-更新失败
     */
    public boolean updateBillStatus(XFDeductionBusinessTypeEnum deductionEnum, TXfBillDeductEntity tXfBillDeductEntity, TXfBillDeductStatusEnum status) {
        if(tXfBillDeductEntity.getId() == null){
            log.error("Id不能为空");
            return false;
        }
        if(tXfBillDeductEntity.getStatus() == null){
            log.error("结算单状态不能为空");
            return false;
        }
        if(XFDeductionBusinessTypeEnum.AGREEMENT_BILL.equals(deductionEnum)){
            if(!TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode().equals(tXfBillDeductEntity.getStatus())){
                if(TXfBillDeductStatusEnum.AGREEMENT_CANCEL.equals(status)){
                    log.info("只有待匹配结算单的协议单才能撤销");
                    return false;
                }
                if(TXfBillDeductStatusEnum.LOCK.equals(status)){
                    log.info("只有待匹配结算单的协议单才能锁定");
                    return false;
                }
            }
        }else if(XFDeductionBusinessTypeEnum.EPD_BILL.equals(deductionEnum)){
            if(!TXfBillDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT.getCode().equals(tXfBillDeductEntity.getStatus())){
                if(TXfBillDeductStatusEnum.AGREEMENT_CANCEL.equals(status)){
                    log.info("只有待匹配结算单的EPD才能撤销");
                    return false;
                }
                if(TXfBillDeductStatusEnum.LOCK.equals(status)){
                    log.info("只有待匹配结算单的EPD才能锁定");
                    return false;
                }
            }
        }else {
            log.info("非法结算单类型");
            return false;
        }
        if(TXfBillDeductStatusEnum.LOCK.equals(status) || TXfBillDeductStatusEnum.UNLOCK.equals(status) ){
            tXfBillDeductEntity.setLockFlag(status.getCode());
        }else{
           tXfBillDeductEntity.setStatus(status.getCode());
        }
        return tXfBillDeductExtDao.updateById(tXfBillDeductEntity) >0;
    }




    /**
     * 结算单转换操作
     * @param tXfBillDeductEntities
     * @return
     */
     public TXfSettlementEntity trans2Settlement(List<TXfBillDeductEntity> tXfBillDeductEntities,XFDeductionBusinessTypeEnum deductionBusinessTypeEnum) {
        if (CollectionUtils.isEmpty(tXfBillDeductEntities)) {
            log.warn("业务单合并结算单失败：{} 业务单集合为空",deductionBusinessTypeEnum.getDes());
            return null;
        }
        String purchaserNo = tXfBillDeductEntities.get(0).getPurchaserNo();
        String sellerNo = tXfBillDeductEntities.get(0).getSellerNo();
        Integer type = deductionBusinessTypeEnum.getValue();
        Integer status = TXfBillDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode();
        TXfSettlementEntity tXfSettlementEntity = new TXfSettlementEntity();
        TAcOrgEntity purchaserOrgEntity = queryOrgInfo("PT",false);
        TAcOrgEntity sellerOrgEntity = queryOrgInfo("172164", true);
        BigDecimal amountWithoutTax = BigDecimal.ZERO;
        BigDecimal amountWithTax = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        for (TXfBillDeductEntity tmp : tXfBillDeductEntities) {
            amountWithoutTax = amountWithoutTax.add(tmp.getAmountWithoutTax());
            amountWithTax = amountWithTax.add(tmp.getAmountWithTax());
            taxAmount = taxAmount.add(tmp.getTaxAmount());
        }
        tXfSettlementEntity.setAmountWithoutTax(amountWithoutTax);
        tXfSettlementEntity.setAmountWithTax(amountWithTax);
        tXfSettlementEntity.setTaxAmount(taxAmount);
        tXfSettlementEntity.setSellerNo(sellerNo);
        tXfSettlementEntity.setSellerTaxNo(sellerOrgEntity.getTaxNo());
        tXfSettlementEntity.setSellerAddress(defaultValue(sellerOrgEntity.getAddress()));
        tXfSettlementEntity.setSellerBankAccount(sellerOrgEntity.getAccount());
        tXfSettlementEntity.setSellerBankName(sellerOrgEntity.getBank());
        tXfSettlementEntity.setSellerName(defaultValue(sellerOrgEntity.getOrgName()));
        tXfSettlementEntity.setSellerTel(defaultValue(sellerOrgEntity.getPhone()));
        tXfSettlementEntity.setPurchaserNo(purchaserNo);
        tXfSettlementEntity.setRemark(StringUtils.EMPTY);
        tXfSettlementEntity.setPurchaserTaxNo(purchaserOrgEntity.getTaxNo());
        tXfSettlementEntity.setPurchaserAddress(purchaserOrgEntity.getAddress());
        tXfSettlementEntity.setPurchaserBankAccount(purchaserOrgEntity.getAccount());
        tXfSettlementEntity.setPurchaserBankName(purchaserOrgEntity.getBank());
        tXfSettlementEntity.setPurchaserName(defaultValue(purchaserOrgEntity.getOrgName()));
        tXfSettlementEntity.setPurchaserTel(defaultValue(purchaserOrgEntity.getPhone()) );
        tXfSettlementEntity.setPurchaserTaxNo(purchaserOrgEntity.getTaxNo());
        tXfSettlementEntity.setAvailableAmount(tXfSettlementEntity.getAmountWithoutTax());
        tXfSettlementEntity.setTaxRate(BigDecimal.valueOf(0.00));
        tXfSettlementEntity.setId(idSequence.nextId());
        tXfSettlementEntity.setBatchNo(StringUtils.EMPTY);
        tXfSettlementEntity.setInvoiceType(StringUtils.EMPTY);
        tXfSettlementEntity.setSettlementNo("settlementNo"+idSequence.nextId());
        tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getCode());
        tXfSettlementEntity.setCreateTime(DateUtils.getNow());
        tXfSettlementEntity.setUpdateTime(tXfSettlementEntity.getCreateTime());
        tXfSettlementEntity.setUpdateUser(0L);
        tXfSettlementEntity.setCreateUser(0L);
        tXfSettlementEntity.setSettlementType(deductionBusinessTypeEnum.getValue());
        tXfSettlementEntity.setPriceMethod(0);
        /**
         * 索赔单 直接生成 结算单
         */
         boolean partMatch = false;
        if (deductionBusinessTypeEnum == XFDeductionBusinessTypeEnum.CLAIM_BILL) {
            List<TXfBillDeductItemEntity> tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryItemsByBill(purchaserNo,sellerNo,type,status);
            for (TXfBillDeductItemEntity tXfBillDeductItemEntity : tXfBillDeductItemEntities) {
                TXfSettlementItemEntity tXfSettlementItemEntity = new TXfSettlementItemEntity();
                BeanUtils.copyProperties(tXfBillDeductItemEntity,tXfSettlementItemEntity);
                tXfSettlementItemEntity.setItemName(tXfBillDeductItemEntity.getCnDesc());
                tXfSettlementItemEntity.setTaxRate(tXfBillDeductItemEntity.getTaxRate());
                tXfSettlementItemEntity.setItemCode(tXfBillDeductItemEntity.getItemNo());
                tXfSettlementItemEntity.setTaxAmount(tXfBillDeductItemEntity.getAmountWithoutTax().multiply(tXfBillDeductItemEntity.getTaxRate()).setScale(2, RoundingMode.HALF_UP));
                tXfSettlementItemEntity.setAmountWithTax(tXfSettlementItemEntity.getTaxAmount().add(tXfSettlementItemEntity.getAmountWithoutTax()));
                tXfSettlementItemEntity.setQuantityUnit(tXfBillDeductItemEntity.getUnit());
                tXfSettlementItemEntity.setItemSpec(tXfBillDeductItemEntity.getCnDesc());
                tXfSettlementItemEntity.setCreateTime(DateUtils.getNow());
                tXfSettlementItemEntity.setUpdateTime(tXfSettlementItemEntity.getCreateTime());
                tXfSettlementItemEntity.setItemStatus(0);
                tXfSettlementItemEntity.setUnitPrice(tXfBillDeductItemEntity.getPrice());
                tXfSettlementItemEntity.setId(idSequence.nextId());
                tXfSettlementItemEntity.setSettlementNo(tXfSettlementEntity.getSettlementNo());
                tXfSettlementItemEntity.setRemark(StringUtils.EMPTY);
                tXfSettlementItemEntity.setCreateUser(0L);
                tXfSettlementItemEntity.setUnitPriceWithTax(tXfSettlementItemEntity.getAmountWithTax().divide(tXfSettlementItemEntity.getQuantity(), 6, RoundingMode.HALF_UP));
                tXfSettlementItemEntity.setUpdateUser(tXfSettlementItemEntity.getCreateUser());
                if(!partMatch){
                    if (tXfBillDeductItemEntity.getRemainingAmount().compareTo(BigDecimal.ZERO) < 0) {
                        partMatch = true;
                    }
                }
                tXfSettlementItemDao.insert(tXfSettlementItemEntity);
            }
        }
         /**
          * 部分匹配 索赔单明细 需要确认数据单据，如果不需要确认，进入拆票流程，状态是 待拆票
          */
         status = partMatch ? TXfSettlementStatusEnum.WAIT_CONFIRM.getCode() : TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode();
         tXfSettlementEntity.setSettlementStatus(status);
         tXfSettlementDao.insert(tXfSettlementEntity);
         return tXfSettlementEntity;
    }

    public TAcOrgEntity queryOrgInfo(String no, boolean iseller) {
        TAcOrgEntity res ;
        if (iseller) {
            res =  companyService.getOrgInfoByOrgCode(no, "8");
        }else{
            res = companyService.getOrgInfoByOrgCode(no, "5");
        }
        return res;
    }

    static String defaultValue(String value) {
        return StringUtils.isEmpty(value) ? StringUtils.EMPTY : value;
    }
    private BigDecimal defaultValue(BigDecimal value) {
        return Objects.isNull(value) ? BigDecimal.ZERO : value;
    }
    private Integer defaultValue(Integer value) { return Objects.isNull(value) ? 0 : value; }
    static Long defaultValue(Long value) {  return Objects.isNull(value) ? 0L : value; }


    enum DeductionHandleEnum {
        CLAIM_BILL(XFDeductionBusinessTypeEnum.CLAIM_BILL, x -> {
            ClaimBillData tmp = (ClaimBillData) x;
            TXfBillDeductEntity tXfBillDeductEntity = dataTrans(x);
            tXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
            tXfBillDeductEntity.setDeductInvoice(tmp.getInvoiceReference());
             return tXfBillDeductEntity;
        }) ,
        AGREEMENT_BILL(XFDeductionBusinessTypeEnum.AGREEMENT_BILL,x -> {
            AgreementBillData tmp = (AgreementBillData) x;
            TXfBillDeductEntity tXfBillDeductEntity = dataTrans(x);
            tXfBillDeductEntity.setAgreementDocumentNumber(tmp.getDocumentNo());
            tXfBillDeductEntity.setAgreementDocumentType(tmp.getDocumentType());
            tXfBillDeductEntity.setAgreementMemo(tmp.getMemo());
            tXfBillDeductEntity.setAgreementReasonCode(tmp.getReasonCode());
            tXfBillDeductEntity.setAgreementReference(tmp.getReference());
            tXfBillDeductEntity.setAgreementTaxCode(tmp.getTaxCode());
            tXfBillDeductEntity.setDeductInvoice(StringUtils.EMPTY);
            tXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode());
            return tXfBillDeductEntity;
        }),
        EPD_BILL(XFDeductionBusinessTypeEnum.EPD_BILL,x -> {
            EPDBillData tmp = (EPDBillData) x;
            TXfBillDeductEntity tXfBillDeductEntity = dataTrans(x);
            tXfBillDeductEntity.setAgreementMemo(tmp.getMemo());
            tXfBillDeductEntity.setAgreementReasonCode(tmp.getReasonCode());
            tXfBillDeductEntity.setAgreementReference(tmp.getReference());
            tXfBillDeductEntity.setAgreementTaxCode(tmp.getTaxCode());
            tXfBillDeductEntity.setAgreementDocumentType(tmp.getDocumentType());
            tXfBillDeductEntity.setAgreementMemo(tmp.getDocumentNo());
            tXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT.getCode());
            return tXfBillDeductEntity;
        });

        private XFDeductionBusinessTypeEnum deductionEnum;
        private Function<DeductBillBaseData,   TXfBillDeductEntity> function;

        DeductionHandleEnum(XFDeductionBusinessTypeEnum deductionEnum, Function<DeductBillBaseData,   TXfBillDeductEntity> function) {
            this.deductionEnum = deductionEnum;
            this.function = function;
        }

        public static Optional <DeductionHandleEnum> getHandleEnum(XFDeductionBusinessTypeEnum xfDeductionEnum) {
            DeductionHandleEnum[] dedcutionHandleEnums = DeductionHandleEnum.values();
            for (DeductionHandleEnum tmp : dedcutionHandleEnums) {
                if (tmp.deductionEnum == xfDeductionEnum) {
                    return Optional.of(tmp);
                }
            }
            return Optional.empty();
        }
    }

    private static TXfBillDeductEntity dataTrans(DeductBillBaseData deductBillBaseData) {
        TXfBillDeductEntity tXfBillDeductEntity = new TXfBillDeductEntity();
        BeanUtils.copyProperties(deductBillBaseData, tXfBillDeductEntity);
        tXfBillDeductEntity.setAgreementDocumentNumber(StringUtils.EMPTY);
        tXfBillDeductEntity.setAgreementDocumentType(StringUtils.EMPTY);
        tXfBillDeductEntity.setAgreementMemo(StringUtils.EMPTY);
        tXfBillDeductEntity.setAgreementReasonCode(StringUtils.EMPTY);
        tXfBillDeductEntity.setAgreementReference(StringUtils.EMPTY);
        tXfBillDeductEntity.setRefSettlementNo(StringUtils.EMPTY);
        tXfBillDeductEntity.setAgreementTaxCode(StringUtils.EMPTY);
        tXfBillDeductEntity.setDeductInvoice(StringUtils.EMPTY);
        tXfBillDeductEntity.setLockFlag(TXfBillDeductStatusEnum.UNLOCK.getCode());
        tXfBillDeductEntity.setSourceId(defaultValue(deductBillBaseData.getId()));
        tXfBillDeductEntity.setSellerName(defaultValue(deductBillBaseData.getSellerName()));
        return tXfBillDeductEntity;
    }

    public TXfBillDeductEntity getDeductById(Long id){
        return tXfBillDeductExtDao .selectById(id);
    }

    public PageResult<QueryDeductListResponse> queryPageList(QueryDeductListRequest request){
        int offset = (request.getPageNo() -1) * request.getPageSize();
        int next = offset+request.getPageSize();
        int count = tXfBillDeductExtDao.countBillPage(request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductDate(), request.getPurchaserNo(), request.getKey());
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryBillPage(offset,next,request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductDate(), request.getPurchaserNo(), request.getKey());
        List<QueryDeductListResponse> response = new ArrayList<>();
        BeanUtil.copyList(tXfBillDeductEntities,response,QueryDeductListResponse.class);
        return PageResult.of(response,count, request.getPageNo(), request.getPageSize());

    }

    private QueryWrapper<TXfBillDeductEntity>  getQueryWrapper(QueryDeductListRequest   request){
        QueryWrapper<TXfBillDeductEntity> wrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(request.getBusinessNo())){
            wrapper.eq(TXfBillDeductEntity.BUSINESS_NO,request.getBusinessNo());
        }
        if(StringUtils.isNotEmpty(request.getPurchaserNo())){
            wrapper.eq(TXfBillDeductEntity.PURCHASER_NO,request.getPurchaserNo());
        }
        if(StringUtils.isNotEmpty(request.getSellerName())){
            wrapper.eq(TXfBillDeductEntity.SELLER_NAME,request.getSellerName());
        }
        if(StringUtils.isNotEmpty(request.getSellerNo())){
            wrapper.eq(TXfBillDeductEntity.SELLER_NO,request.getSellerNo());
        }
        if(request.getBusinessType() != null){
            wrapper.eq(TXfBillDeductEntity.BUSINESS_TYPE,request.getBusinessType());
        }
        if(request.getDeductDate() != null){
            wrapper.apply("format(deduct_date,'yyyy-MM-dd') = {0}",request.getDeductDate());
        }
        return wrapper;
    }

}
