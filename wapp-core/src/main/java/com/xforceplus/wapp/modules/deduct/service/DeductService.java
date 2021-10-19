package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.config.TaxRateConfig;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.company.service.CompanyService;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
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
    private TXfBillDeductExtDao  tXfBillDeductExtDao;
    @Autowired
    private TXfBillDeductItemExtDao tXfBillDeductItemExtDao;

    @Autowired
    private TXfBillDeductItemRefExtDao tXfBillDeductItemRefDao;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfSettlementItemDao tXfSettlementItemDao;
    @Autowired
    private  IDSequence idSequence;
    @Autowired
    private TaxRateConfig taxRateConfig;
    @Autowired
    private TaxCodeServiceImpl taxCodeService;
    @Autowired
    private CompanyService companyService;
    @PostConstruct
    public void initData() {
        int no = 1001121107;
        /**
         * 索赔单 主信息
         */
//        int amount = 10;
//        List<DeductBillBaseData> dataList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            ClaimBillData deductBillBaseData = new ClaimBillData();
//            deductBillBaseData.setAmountWithoutTax(new BigDecimal(amount*2*(i+1)+i));
//            deductBillBaseData.setBusinessNo(idSequence.nextId().toString());
//            deductBillBaseData.setAmountWithTax(new BigDecimal(10));
//            deductBillBaseData.setBusinessType(XFDeductionBusinessTypeEnum.AGREEMENT_BILL.getType());
//            deductBillBaseData.setBatchNo("BT112312312312");
//            deductBillBaseData.setDeductDate(new Date());
//            deductBillBaseData.setPurchaserNo("PT");
//            deductBillBaseData.setSellerNo("172164");
//            deductBillBaseData.setRemark("索赔");
//            deductBillBaseData.setTaxAmount(new BigDecimal("0.13").multiply(deductBillBaseData.getAmountWithoutTax()).setScale(2, RoundingMode.HALF_UP));
//            no = no + 1;
//            deductBillBaseData.setBusinessNo(no + StringUtils.EMPTY);
//            deductBillBaseData.setTaxRate(new BigDecimal("0.13"));
//            deductBillBaseData.setStoreType("smas");
//            deductBillBaseData.setVerdictDate(new Date());
//            deductBillBaseData.setInvoiceReference("invoice00022222");
//            dataList.add(deductBillBaseData);
//        }
//        List<ClaimBillItemData> res = new ArrayList<>();
//        amount = 6;
//        for (int i = 0; i < 10; i++) {
//            int tmp = amount*i + i;
//            ClaimBillItemData claimBillItemData = new ClaimBillItemData();
//            claimBillItemData.setAmountWithoutTax(new BigDecimal(tmp));
//            claimBillItemData.setSellerNo("337852");
//            claimBillItemData.setDeptNbr("114");
//            claimBillItemData.setPrice(new BigDecimal(1));
//            claimBillItemData.setQuantity(new BigDecimal(tmp));
//            claimBillItemData.setItemNo("20280238");
//            claimBillItemData.setTaxRate(new BigDecimal("0.13"));
//            claimBillItemData.setCategoryNbr("30");
//            claimBillItemData.setCnDesc("小林刻立眼镜清洁纸");
//            claimBillItemData.setStoreNbr("WI");
//            claimBillItemData.setUnit("包");
//            claimBillItemData.setUpc("111");
//            claimBillItemData.setVerdictDate(new Date());
//            claimBillItemData.setVnpkQuantity(new BigDecimal(6));
//            claimBillItemData.setVnpkCost(new BigDecimal(10));
//            res.add(claimBillItemData);
//        }
//        amount = 5;
//        for (int i = 0; i < 10; i++) {
//            int tmp = amount*i + i;
//            ClaimBillItemData claimBillItemData = new ClaimBillItemData();
//            claimBillItemData.setAmountWithoutTax(new BigDecimal(tmp));
//            claimBillItemData.setSellerNo("337852");
//            claimBillItemData.setDeptNbr("114");
//            claimBillItemData.setPrice(new BigDecimal(1));
//            claimBillItemData.setQuantity(new BigDecimal(tmp));
//            claimBillItemData.setItemNo("20280238");
//            claimBillItemData.setTaxRate(new BigDecimal("0.09"));
//            claimBillItemData.setCategoryNbr("30");
//            claimBillItemData.setCnDesc("小林刻立眼镜清洁纸");
//            claimBillItemData.setStoreNbr("WI");
//            claimBillItemData.setUnit("包");
//            claimBillItemData.setUpc("111");
//            claimBillItemData.setVerdictDate(new Date());
//            claimBillItemData.setVnpkQuantity(new BigDecimal(6));
//            claimBillItemData.setVnpkCost(new BigDecimal(10));
//            res.add(claimBillItemData);
//        }
//        amount = 4;
//        for (int i = 0; i < 10; i++) {
//            int tmp = amount*i + i;
//            ClaimBillItemData claimBillItemData = new ClaimBillItemData();
//            claimBillItemData.setAmountWithoutTax(new BigDecimal(tmp));
//            claimBillItemData.setSellerNo("337852");
//            claimBillItemData.setDeptNbr("WI2");
//            claimBillItemData.setPrice(new BigDecimal(1));
//            claimBillItemData.setQuantity(new BigDecimal(tmp));
//            claimBillItemData.setItemNo("20280238");
//            claimBillItemData.setTaxRate(new BigDecimal("0.09"));
//            claimBillItemData.setCategoryNbr("30");
//            claimBillItemData.setCnDesc("小林刻立眼镜清洁纸");
//            claimBillItemData.setStoreNbr("114");
//            claimBillItemData.setUnit("包");
//            claimBillItemData.setUpc("111");
//            claimBillItemData.setVerdictDate(new Date());
//            claimBillItemData.setVnpkQuantity(new BigDecimal(6));
//            claimBillItemData.setVnpkCost(new BigDecimal(10));
//            res.add(claimBillItemData);
//        }
//        amount = 3;
//        for (int i = 0; i < 10; i++) {
//            int tmp = amount*i + i;
//            ClaimBillItemData claimBillItemData = new ClaimBillItemData();
//            claimBillItemData.setAmountWithoutTax(new BigDecimal(tmp));
//            claimBillItemData.setSellerNo("279771");
//            claimBillItemData.setDeptNbr("WI");
//            claimBillItemData.setPrice(new BigDecimal(1));
//            claimBillItemData.setQuantity(new BigDecimal(tmp));
//            claimBillItemData.setItemNo("20280238");
//            claimBillItemData.setTaxRate(new BigDecimal("0.09"));
//            claimBillItemData.setCategoryNbr("30");
//            claimBillItemData.setCnDesc("小林刻立眼镜清洁纸");
//            claimBillItemData.setStoreNbr("114");
//            claimBillItemData.setUnit("包");
//            claimBillItemData.setUpc("111");
//            claimBillItemData.setVerdictDate(new Date());
//            claimBillItemData.setVnpkQuantity(new BigDecimal(6));
//            claimBillItemData.setVnpkCost(new BigDecimal(10));
//            res.add(claimBillItemData);
//        }
        // receiveItemData(res, "");
         //receiveData(dataList, XFDeductionBusinessTypeEnum.CLAIM_BILL);
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
//        Optional<TaxCode> taxCodeOptional = taxCodeService.getTaxCodeByItemNo(entity.getItemNo());
//        if (taxCodeOptional.isPresent()) {
//                TaxCode taxCode = taxCodeOptional.get();
//                entity.setGoodsTaxNo(taxCode.getGoodsTaxNo());
//                entity.setTaxPre(taxCode.getTaxPre());
//                entity.setTaxPreCon(taxCode.getTaxPreCon());
//                entity.setZeroTax(taxCode.getZeroTax());
//                entity.setItemShortName(taxCode.getSmallCategoryName());
//        }
        entity.setGoodsTaxNo("123123");
        entity.setTaxPre(StringUtils.EMPTY);
        entity.setTaxPreCon(StringUtils.EMPTY);
        entity.setZeroTax(StringUtils.EMPTY);
        entity.setItemShortName(StringUtils.EMPTY);
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
     * 某批次完成通知，索赔单保证，明细信息完成后，再进行主信息保存
     * @param deductionEnum
     * @return
     */
    public boolean receiveDone( XFDeductionBusinessTypeEnum deductionEnum) {
        //索赔单处理 ：都在在本月内统计
        if (deductionEnum == XFDeductionBusinessTypeEnum.CLAIM_BILL) {
            /**
             * 匹配索赔单明细
             */
            matchClaimBill();
            /**
             * 索赔单匹配蓝票 TODO
             * 索赔单匹配蓝票 金额不足，
             */
            /**
             * 合并结算单
             */
            mergeSettlement(deductionEnum, null);

        }else{

            /**
             * 自动合并结算单
             */
            mergeSettlement(deductionEnum, null);
            /**
             * 匹配蓝票 TODO
             */

            /**
             * 匹配税编 根据蓝票匹配结果，组装结算单明细 标识结算单信息未 待确认
             */
            
        }
        return true;
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
                if (pages.getTotal() >0) {
                    lockBill(deductionEnum, tXfBillDeductEntity, false);
                }
                tXfBillDeductExtDao.insert(tXfBillDeductEntity);
            } else {
                log.error("非法的单据id={}，协议号agreement reference为空，跳过解锁取消逻辑", tXfBillDeductEntity.getId());
            }
        }
        return true;
    }

    /**
     * 锁定协议单或EPD单
     *
     * @param deductionEnum {@link XFDeductionBusinessTypeEnum} 单据类型
     * @param tXfBillDeductEntity {@link TXfBillDeductEntity} 单据实体
     * @param lockStatus {boolean} 锁定状态 true-锁定, false-解锁
     * @return {boolean} true-锁定或解锁成功, false-锁定或解锁失败
     */
    private boolean lockBill(XFDeductionBusinessTypeEnum deductionEnum, TXfBillDeductEntity tXfBillDeductEntity, boolean lockStatus) {
        // TODO by 孙世勇
        return true;
    }


    /**
     * 匹配索赔单 索赔单明细
     * 单线程执行，每次导入 只会执行一次，针对当月的索赔明细有效
     * @return
     */
    public boolean matchClaimBill() {
        Date startDate = DateUtils.getFristDate();
        Date endDate = DateUtils.getLastDate();
        int limit = 100;
        /**
         * 查询未匹配明细的索赔单
         */
        Long deductId = 1L;
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(deductId,startDate,endDate, limit, XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
        while (CollectionUtils.isNotEmpty(tXfBillDeductEntities)) {
            for (TXfBillDeductEntity tXfBillDeductEntity : tXfBillDeductEntities) {
                String sellerNo = tXfBillDeductEntity.getSellerNo();
                String purcharseNo = tXfBillDeductEntity.getPurchaserNo();
                BigDecimal taxRate = tXfBillDeductEntity.getTaxRate();
                if (StringUtils.isEmpty(sellerNo) || StringUtils.isEmpty(purcharseNo) || Objects.isNull(taxRate)) {
                    log.warn("索赔单{}主信息 不符合要求，sellerNo:{},purcharseNo:{},taxRate:{}",sellerNo,purcharseNo,taxRate);
                    continue;
                }
                /**
                 * 查询已匹配金额
                 */
                BigDecimal matchAmount = tXfBillDeductItemRefDao.queryRefMatchAmountByBillId(tXfBillDeductEntity.getId());
                matchAmount = Objects.isNull(matchAmount) ? BigDecimal.ZERO : matchAmount;
                BigDecimal billAmount = tXfBillDeductEntity.getAmountWithoutTax();
                billAmount = billAmount.subtract(matchAmount);

                List<TXfBillDeductItemEntity> matchItem = new ArrayList<>();
                /**
                 * 查询符合条件的明细
                 */
                Long itemId = 1L;
                List<TXfBillDeductItemEntity> tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(startDate,endDate, purcharseNo, sellerNo, taxRate,  itemId, limit);
                while (billAmount.compareTo(BigDecimal.ZERO) > 0) {
                    if (CollectionUtils.isEmpty(tXfBillDeductItemEntities)) {
                        taxRate = taxRateConfig.getNextTaxRate(taxRate);
                        if (Objects.isNull(taxRate)) {
                            log.warn("{} 索赔的，未找到足够的索赔单明细，结束匹配",tXfBillDeductEntity.getId());
                            break;
                        }
                        itemId = 0L;
                        tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(startDate,endDate, purcharseNo, sellerNo, taxRate, itemId, limit);
                        continue;
                    }
                    BigDecimal total = tXfBillDeductItemEntities.stream().map(TXfBillDeductItemEntity::getAmountWithoutTax).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (billAmount.compareTo(total) > 0) {
                        billAmount = billAmount.subtract(total);
                     }else{
                        billAmount = BigDecimal.ZERO;
                    }
                    matchItem.addAll(tXfBillDeductItemEntities);
                    if(billAmount.compareTo(BigDecimal.ZERO) == 0){
                        break;
                    }
                    itemId =   tXfBillDeductItemEntities.stream().mapToLong(TXfBillDeductItemEntity::getId).max().getAsLong();
                    tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(startDate,endDate, purcharseNo, sellerNo, taxRate, itemId, limit);
                }
                /**
                 * 匹配完成 进行绑定操作
                 */
                if (CollectionUtils.isNotEmpty(matchItem)) {
                    doItemMatch(tXfBillDeductEntity, matchItem);
                }
            }
            deductId =  tXfBillDeductEntities.stream().mapToLong(TXfBillDeductEntity::getId).max().getAsLong();
            /**
             * 执行下一批匹配
             */
            tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(deductId,startDate,endDate, limit, XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
        }
        return true;
    }

    /**
     * 执行扣除明细，匹配主信息
     * @param tXfBillDeductEntity
     * @param tXfBillDeductItemEntitys
     * @return
     */
    @Transactional
    public BigDecimal doItemMatch(TXfBillDeductEntity tXfBillDeductEntity, List<TXfBillDeductItemEntity> tXfBillDeductItemEntitys ) {
        Long billId = tXfBillDeductEntity.getId();
        BigDecimal billAmount = tXfBillDeductEntity.getAmountWithoutTax();
        BigDecimal taxRate = tXfBillDeductEntity.getTaxRate();
        BigDecimal taxAmount = tXfBillDeductEntity.getTaxAmount();
        /**
         * false 表示 存在未匹配税编的明细
         *
         */
        Boolean matchTaxNoFlag = true;
        /**
         * 如果存在不同税率，需要确认税差
         */
        Boolean checkTaxRateDifference = false;

        BigDecimal taxAmountOther = BigDecimal.ZERO;
        for (TXfBillDeductItemEntity tXfBillDeductItemEntity : tXfBillDeductItemEntitys) {
            if (billAmount.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
            BigDecimal amount = tXfBillDeductItemEntity.getRemainingAmount();
            amount = billAmount .compareTo(amount) > 0 ? amount : billAmount;
            int res = tXfBillDeductItemExtDao.updateBillItem(tXfBillDeductItemEntity.getId(), amount);
            if (res == 0) {
                continue;
            }
            billAmount = billAmount.subtract(amount);
            if (matchTaxNoFlag) {
                if (StringUtils.isEmpty(tXfBillDeductItemEntity.getGoodsTaxNo())) {
                    matchTaxNoFlag = false;
                }
            }
            if (!checkTaxRateDifference) {
                if (tXfBillDeductItemEntity.getTaxRate().compareTo(taxRate) != 0) {
                    checkTaxRateDifference = true;
                }
            }
            TXfBillDeductItemRefEntity tXfBillDeductItemRefEntity = new TXfBillDeductItemRefEntity();
            tXfBillDeductItemRefEntity.setId(idSequence.nextId());
            tXfBillDeductItemRefEntity.setCreateDate(DateUtils.getNowDate());
            tXfBillDeductItemRefEntity.setDeductId(billId);
            tXfBillDeductItemRefEntity.setUseAmount(amount);
            tXfBillDeductItemRefEntity.setDeductItemId(tXfBillDeductItemEntity.getId());
            tXfBillDeductItemRefEntity.setPrice(tXfBillDeductItemEntity.getPrice());
            tXfBillDeductItemRefEntity.setQuantity(tXfBillDeductItemEntity.getQuantity());
            tXfBillDeductItemRefEntity.setTaxAmount(amount.multiply(tXfBillDeductItemEntity.getTaxRate()).setScale(2, RoundingMode.HALF_UP));
            taxAmountOther = taxAmountOther.add(tXfBillDeductItemRefEntity.getTaxAmount());
            tXfBillDeductItemRefEntity.setAmountWithTax(tXfBillDeductItemRefEntity.getTaxAmount().add(tXfBillDeductItemRefEntity.getUseAmount()));

            tXfBillDeductItemRefDao.insert(tXfBillDeductItemRefEntity);
        }
        TXfBillDeductEntity tmp = new TXfBillDeductEntity();
        tmp.setId(billId);
        if (!matchTaxNoFlag || checkTaxRateDifference||  taxAmountOther.subtract(taxAmount).abs().compareTo(new BigDecimal("20")) > 0) {

            //TODO 发起例外报告 确认税差 不需要中止
        }
        /**
         * 如果当前金额没有匹配完 为待匹配状态，如果存在未匹配的税编，状态未待匹配税编，如果已经完成匹配税编，简称是否存在不同税率，如果存在状态未待确认税差，如果不存在，状态为待匹配蓝票，
         */
        Integer status = billAmount.compareTo(BigDecimal.ZERO)>0?TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode(): matchTaxNoFlag ? (checkTaxRateDifference ? TXfBillDeductStatusEnum.CLAIM_NO_MATCH_TAX_DIFF.getCode() : TXfBillDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode()) : TXfBillDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode();
        tmp.setStatus(status);
        tXfBillDeductExtDao.updateById(tmp);
        return billAmount;
    }

    /***
     * 结算单匹配蓝票
     * @param businessNo
     * @param amount
     * @param sellerNo
     * @return
     */
    public boolean settlementMatchInvoice(String businessNo, BigDecimal amount,String sellerNo) {
        return false;
    }


    /**
     *  协议单、EPD 索赔单 合并结算单, 合并2年内的未匹配的单子
     * @return
     */
    public boolean mergeSettlement(XFDeductionBusinessTypeEnum deductionEnum,List<TXfBillDeductEntity> manualChoice) {
        if (deductionEnum == XFDeductionBusinessTypeEnum.CLAIM_BILL) {
            mergeClaimSettlement();
        } else if (deductionEnum == XFDeductionBusinessTypeEnum.AGREEMENT_BILL) {
            mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_BLUE_INVOICE,manualChoice);
        } else if (deductionEnum == XFDeductionBusinessTypeEnum.EPD_BILL) {
            mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.EPD_BILL, TXfBillDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.EPD_NO_MATCH_BLUE_INVOICE,manualChoice);
        }
        return false;
    }

    /**
     *
     * @param deductionEnum
     * @param tXfBillDeductStatusEnum
     * @param targetStatus
     * @param manualChoice 页面手动选择的 单子明细
     * @return
     */
    public boolean mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum deductionEnum, TXfBillDeductStatusEnum tXfBillDeductStatusEnum, TXfBillDeductStatusEnum targetStatus, List<TXfBillDeductEntity> manualChoice) {

        int expireScale = -5;
        /**
         * 获取超期时间 判断超过此日期的正数单据
         */
        Date referenceDate = DateUtils.addDate(DateUtils.getNow(), expireScale);
        //查询大于expireDate， 同一购销对，同一税率 总不含税金额为正的单据
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.querySuitablePositiveBill(referenceDate, deductionEnum.getValue(), tXfBillDeductStatusEnum.getCode());
        if (CollectionUtils.isNotEmpty(manualChoice)) {
            tXfBillDeductEntities.addAll(manualChoice);
        }
        for (TXfBillDeductEntity tmp : tXfBillDeductEntities) {
            /**
             * 查询 同一购销对，同一税率 下所有的负数单据
             */
            TXfBillDeductEntity negativeBill = tXfBillDeductExtDao.querySpecialNegativeBill(tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate(), deductionEnum.getValue(), tXfBillDeductStatusEnum.getCode());
            if (negativeBill.getAmountWithoutTax().add(tmp.getAmountWithoutTax()).compareTo(BigDecimal.ZERO) > 0) {
                try {
                    batchUpdateMergeBill(deductionEnum,tmp, negativeBill, tXfBillDeductStatusEnum, referenceDate, targetStatus);
                } catch (Exception e) {
                    log.error("{}单合并异常 购方:{}，购方:{}，税率:{}", deductionEnum.getDes(), tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate());
                }
            } else {
                log.warn("{}单合并失败：合并收金额不为负数 购方:{}，购方:{}，税率:{}，手动勾选 {} ", deductionEnum.getDes(), tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate(),manualChoice);
            }
        }
        return false;
    }

    /**
     * 批量进行更新操作 保证单进程操作
     * @param tmp
     * @param tXfBillDeductStatusEnum
     * @param referenceDate
     * @param targetSatus
     * @return
     */
    @Transactional
    public boolean batchUpdateMergeBill(XFDeductionBusinessTypeEnum deductionEnum,TXfBillDeductEntity tmp,TXfBillDeductEntity negativeBill, TXfBillDeductStatusEnum tXfBillDeductStatusEnum, Date referenceDate, TXfBillDeductStatusEnum targetSatus) {
        String purchaserNo = tmp.getPurchaserNo();
        String sellerNo = tmp.getSellerNo();
        BigDecimal taxRate = tmp.getTaxRate();
        Integer type = deductionEnum.getValue();
        Integer status = tXfBillDeductStatusEnum.getCode();
        Integer targetStatus = targetSatus.getCode();
        List<TXfBillDeductEntity> tXfBillDeductEntities = new ArrayList<>();
        tXfBillDeductEntities.add(tmp);
        tXfBillDeductEntities.add(negativeBill);
        TXfSettlementEntity tXfSettlementEntity = trans2Settlement(tXfBillDeductEntities,deductionEnum);
        tXfBillDeductExtDao.updateMergeNegativeBill(tXfSettlementEntity.getSettlementNo(),purchaserNo, sellerNo, taxRate, type, status, targetStatus);
        tXfBillDeductExtDao.updateMergePositiveBill(tXfSettlementEntity.getSettlementNo(),purchaserNo, sellerNo, taxRate, referenceDate, type, status, targetStatus);
        /**
         * 更新完成后，进行此结算下的数据校验，校验通过，提交，失败，回滚：表示有的新的单子进来，不满足条件了,回滚操作
         */
        tmp = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo());
        if (tmp.getAmountWithoutTax().compareTo(BigDecimal.ZERO) <= 0) {
            /**
             * 说明在更新过程钟，新的单据被更新到,而且更新到的负数大于正数，合并失败
             */
             throw new RuntimeException("");
        }
        return true;
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

    private String defaultValue(String value) {
        return StringUtils.isEmpty(value) ? StringUtils.EMPTY : value;
    }
    private BigDecimal defaultValue(BigDecimal value) {
        return Objects.isNull(value) ? BigDecimal.ZERO : value;
    }
    private Integer defaultValue(Integer value) {
        return Objects.isNull(value) ? 0 : value;
    }
    /**
     * 合并 索赔单为结算单
     * @return
     */
    public boolean mergeClaimSettlement() {
        /**
         * 查询符合条件的索赔单，购销一致维度，状态为待生成结算单
         */
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.querySuitableClaimBill(XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode());
         /**
         * 查询索赔单明细，组装结算单明细信息
         */
        for (TXfBillDeductEntity tXfBillDeductEntity : tXfBillDeductEntities) {
            try {
                doMergeClaim(tXfBillDeductEntity);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("索赔单组合结算失败: purchase_no :{} ,seller_no:{} status: {}",tXfBillDeductEntity.getPurchaserNo(),tXfBillDeductEntity.getSellerNo(),TXfBillDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getDesc());
            }
        }
        return true;
    }

    @Transactional
    public void doMergeClaim(TXfBillDeductEntity tXfBillDeductEntity) {
        TXfSettlementEntity tXfSettlementEntity = trans2Settlement(Arrays.asList(tXfBillDeductEntity), XFDeductionBusinessTypeEnum.CLAIM_BILL);
        tXfBillDeductExtDao.updateSuitableClaimBill(XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode(), TXfBillDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode(), tXfSettlementEntity.getSettlementNo(), tXfBillDeductEntity.getPurchaserNo(), tXfBillDeductEntity.getSellerNo());
    }

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
            TXfBillDeductEntity tXfBillDeductEntity = dataTrans(tmp);
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
            TXfBillDeductEntity tXfBillDeductEntity = dataTrans(tmp);
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

        tXfBillDeductEntity.setSellerName(StringUtils.isEmpty(deductBillBaseData.getSellerName())?StringUtils.EMPTY:deductBillBaseData.getSellerName());
        return tXfBillDeductEntity;
    }
}
