package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.config.TaxRateConfig;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.model.AgreementBillData;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.modules.deduct.model.EPDBillData;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.sequence.IDSequence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
public class DeductService  {
    @Autowired
    private TXfBillDeductExtDao  tXfBillDeductExtDao;
    @Autowired
    private TXfBillDeductItemExtDao tXfBillDeductItemExtDao;

    @Autowired
    private TXfBillDeductItemRefDao tXfBillDeductItemRefDao;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfSettlementItemDao tXfSettlementItemDao;
    @Autowired
    private  IDSequence idSequence;
    @Autowired
    private TaxRateConfig taxRateConfig;
    @Autowired
    private TAcOrgDao tAcOrgDao;
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
            tmp.setCreateDate(date);
            tmp.setId(idSequence.nextId());
            tmp.setRemainingAmount(claimBillItemData.getAmountWithoutTax());
            //todo 调用匹配方法 补充明细信息,如果匹配失败，发生例外报告
            list.add(tmp);
        }
        return list;
    }

    /**
     *
     * @param deductBillBaseDataList
     * @param batchNo
     * @param deductionEnum
     * @return
     */
    public boolean receiveData(List<DeductBillBaseData> deductBillBaseDataList, String batchNo, XFDeductionBusinessTypeEnum deductionEnum) {
        List<TXfBillDeductEntity> list = transferBillData(deductBillBaseDataList, deductionEnum);
        for (TXfBillDeductEntity tXfBillDeductEntity : list) {
            try {
                tXfBillDeductExtDao.insert(tXfBillDeductEntity);
            } catch (DuplicateKeyException d) {
                //TODO 协议单 EPD 单，如果完成了  结算单合并不做更新
                // 索赔单 完成 主信息匹配 不能更新
                //update where businessno = ? and businesstype = ? and
            }

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
            tmp.setId(idSequence.nextId());
            list.add(tmp);
         }
        return list;
    }

    /**
     * 某批次完成通知，索赔单保证，明细信息完成后，再进行主信息保存
     * @param batchNo
     * @return
     */
    public boolean receiveDone(String batchNo,XFDeductionBusinessTypeEnum deductionEnum) {
        return true;
    }

    /**
     * 自动取消和解锁
     * @param businessNo
     * @param deductionEnum
     * @return
     */
    public boolean unlockAndCancel(String businessNo,XFDeductionBusinessTypeEnum deductionEnum) {
        return false;
    }



    /**
     * 匹配索赔单 索赔单明细
     * 单线程执行，每次导入 只会执行一次，针对当月的索赔明细有效
     * @return
     */
    public boolean matchClaimBill() {
        Date startDate = DateUtils.getFristDate();
        Date endDate = DateUtils.getLastDate();
        int billStartIndex = 0;
        int itemStartIndex = 0;
        int limit = 100;
        int batchAcount = 100;
        /**
         * 查询未匹配明细的索赔单
         */
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(startDate,endDate, billStartIndex, limit, XFDeductionBusinessTypeEnum.CLAIM_BILL.getType(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
        while (CollectionUtils.isNotEmpty(tXfBillDeductEntities)) {
            for (TXfBillDeductEntity tXfBillDeductEntity : tXfBillDeductEntities) {
                String sellerNo = tXfBillDeductEntity.getSellerNo();
                String purcharseNo = tXfBillDeductEntity.getPurchaserNo();
                BigDecimal taxRate = tXfBillDeductEntity.getTaxRate();
                if (StringUtils.isEmpty(sellerNo) || StringUtils.isEmpty(purcharseNo) || Objects.isNull(taxRate)) {
                    log.warn("索赔单{}主信息 不符合要求，sellerNo:{},purcharseNo:{},taxRate:{}",sellerNo,purcharseNo,taxRate);
                    continue;
                }
                BigDecimal billAmount = tXfBillDeductEntity.getAmountWithoutTax();
                /**
                 *
                 */
                List<TXfBillDeductItemEntity> matchItem = new ArrayList<>();
                /**
                 * 查询符合条件的明细
                 */
                List<TXfBillDeductItemEntity> tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(startDate,endDate, purcharseNo, sellerNo, taxRate, itemStartIndex, limit);
                while (billAmount.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal total = tXfBillDeductItemEntities.stream().map(TXfBillDeductItemEntity::getAmountWithoutTax).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (billAmount.compareTo(total) > 0) {
                        billAmount = billAmount.subtract(total);
                     }else{
                        billAmount = BigDecimal.ZERO;
                    }
                    if (billAmount.compareTo(BigDecimal.ZERO) > 0) {
                        itemStartIndex = (itemStartIndex + 1) * batchAcount;
                        tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(startDate,endDate, purcharseNo, sellerNo, taxRate, itemStartIndex, limit);
                        /**
                         * 如果当前税率被使用完，使用下一级税率
                         */
                        if (CollectionUtils.isEmpty(tXfBillDeductItemEntities)) {
                            taxRate = taxRateConfig.getNextTaxRate(taxRate);
                            if (Objects.isNull(taxRate)) {
                                log.warn("{} 索赔的，未找到足够的索赔单明细，结束匹配",tXfBillDeductEntity.getId());
                                break;
                            }
                            itemStartIndex = 0;
                            tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(startDate,endDate, purcharseNo, sellerNo, taxRate, itemStartIndex, limit);
                        }
                    }
                }
                /**
                 * 匹配成功 进行绑定操作
                 */
                if (billAmount.compareTo(BigDecimal.ZERO) == 0) {
                    doItemMatch(tXfBillDeductEntity.getId(), matchItem, tXfBillDeductEntity.getAmountWithoutTax(),tXfBillDeductEntity.getTaxRate());
                }
            }
            /**
             * 执行下一批匹配
             */
            billStartIndex = (billStartIndex + 1) * batchAcount;
            tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(startDate,endDate, billStartIndex, limit, XFDeductionBusinessTypeEnum.CLAIM_BILL.getType(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
        }
        return true;
    }

    /**
     * 执行扣除明细，匹配主信息
     * @param billId
     * @param tXfBillDeductItemEntitys
     * @param billAmount
     * @return
     */
    @Transactional
    public BigDecimal doItemMatch(Long billId, List<TXfBillDeductItemEntity> tXfBillDeductItemEntitys, BigDecimal billAmount,BigDecimal taxRate) {
        /**
         * false 表示 存在未匹配税编的明细
         *
         */
        Boolean matchTaxNoFlag = true;
        /**
         * 如果存在不同税率，需要确认税差
         */
        Boolean checkTaxRateDifference = false;
        for (TXfBillDeductItemEntity tXfBillDeductItemEntity : tXfBillDeductItemEntitys) {
            if (billAmount.compareTo(BigDecimal.ZERO) == 0) {
                return billAmount;
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
            tXfBillDeductItemRefDao.insert(tXfBillDeductItemRefEntity);
        }
        TXfBillDeductEntity tmp = new TXfBillDeductEntity();
        tmp.setId(billId);
        /**
         * 如果存在未匹配的税编，状态未待匹配税编，如果已经完成匹配税编，简称是否存在不同税率，如果存在状态未待确认税差，如果不存在，状态为待匹配蓝票，
         */
        Integer status = matchTaxNoFlag ? (checkTaxRateDifference ? TXfBillDeductStatusEnum.CLAIM_NO_MATCH_TAX_DIFF.getCode() : TXfBillDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode()) : TXfBillDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode();
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
            mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.AGREEMENT_BILL, TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT,manualChoice);
        } else if (deductionEnum == XFDeductionBusinessTypeEnum.EPD_BILL) {
            mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum.EPD_BILL, TXfBillDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT, TXfBillDeductStatusEnum.EPD_MATCH_SETTLEMENT,manualChoice);
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
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.querySuitablePositiveBill(referenceDate, deductionEnum.getType(), tXfBillDeductStatusEnum.getCode());
        if (CollectionUtils.isNotEmpty(manualChoice)) {
            tXfBillDeductEntities.addAll(manualChoice);
        }
        for (TXfBillDeductEntity tmp : tXfBillDeductEntities) {
            /**
             * 查询 同一购销对，同一税率 下所有的负数单据
             */
            TXfBillDeductEntity negativeBill = tXfBillDeductExtDao.querySpecialNegativeBill(tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate(), deductionEnum.getType(), tXfBillDeductStatusEnum.getCode());
            if (negativeBill.getAmountWithoutTax().add(tmp.getAmountWithoutTax()).compareTo(BigDecimal.ZERO) > 0) {
                try {
                    batchUpdateMergeBill(tmp, negativeBill, tXfBillDeductStatusEnum, referenceDate, targetStatus);
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
     * 批量进行更新操作
     * @param tmp
     * @param tXfBillDeductStatusEnum
     * @param referenceDate
     * @param targetSatus
     * @return
     */
    @Transactional
    public boolean batchUpdateMergeBill(TXfBillDeductEntity tmp,TXfBillDeductEntity negativeBill, TXfBillDeductStatusEnum tXfBillDeductStatusEnum, Date referenceDate, TXfBillDeductStatusEnum targetSatus) {
        String purchaserNo = tmp.getPurchaserNo();
        String sellerNo = tmp.getSellerNo();
        BigDecimal taxRate = tmp.getTaxRate();
        Integer type = tmp.getBusinessType();
        Integer status = tXfBillDeductStatusEnum.getCode();
        Integer targetStatus = TXfBillDeductStatusEnum.EPD_MATCH_SETTLEMENT.getCode();
        List<TXfBillDeductEntity> tXfBillDeductEntities = new ArrayList<>();
        tXfBillDeductEntities.add(tmp);
        tXfBillDeductEntities.add(negativeBill);
        TXfSettlementEntity tXfSettlementEntity = trans2Settlement(tXfBillDeductEntities);
        tXfBillDeductExtDao.updateMergeNegativeBill(tXfSettlementEntity.getSettlementNo(),purchaserNo, sellerNo, taxRate, type, status, targetStatus);
        tXfBillDeductExtDao.updateMergePositiveBill(tXfSettlementEntity.getSettlementNo(),purchaserNo, sellerNo, taxRate, referenceDate, type, status, targetStatus);
        tXfSettlementDao.insert(tXfSettlementEntity);
        /**
         * 更新完成后，进行此结算下的数据校验，校验通过，提交，失败，回滚：表示有的新的单子进来，不满足条件了
         */
        tmp = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo());
        if (tmp.getAmountWithoutTax().compareTo(BigDecimal.ZERO) < 0) {
             throw new RuntimeException("");
        }
        return true;
    }


    /**
     * 结算单转换操作
     * @param tXfBillDeductEntities
     * @return
     */
    public TXfSettlementEntity trans2Settlement(List<TXfBillDeductEntity> tXfBillDeductEntities) {
        if (CollectionUtils.isEmpty(tXfBillDeductEntities)) {
            return null;
        }
        String purchaserNo = tXfBillDeductEntities.get(0).getPurchaserNo();
        String sellerNo = tXfBillDeductEntities.get(0).getSellerNo();
        BigDecimal taxRate = tXfBillDeductEntities.get(0).getTaxRate();

        TXfSettlementEntity tXfSettlementEntity = new TXfSettlementEntity();
        TAcOrgEntity purchaserOrgEntity = queryOrgInfo(purchaserNo);
        TAcOrgEntity sellerOrgEntity = queryOrgInfo(sellerNo);
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
        tXfSettlementEntity.setSellerAddress(sellerOrgEntity.getAddress());
        tXfSettlementEntity.setSellerBankAccount(sellerOrgEntity.getAccount());
        tXfSettlementEntity.setSellerBankName(sellerOrgEntity.getBank());
        tXfSettlementEntity.setSellerName(sellerOrgEntity.getCompany());
        tXfSettlementEntity.setSellerTel(sellerOrgEntity.getPhone());

        tXfSettlementEntity.setPurchaserNo(purchaserNo);
        tXfSettlementEntity.setPurchaserTaxNo(purchaserOrgEntity.getTaxNo());
        tXfSettlementEntity.setPurchaserAddress(purchaserOrgEntity.getAddress());
        tXfSettlementEntity.setPurchaserBankAccount(purchaserOrgEntity.getAccount());
        tXfSettlementEntity.setPurchaserBankName(purchaserOrgEntity.getBank());
        tXfSettlementEntity.setPurchaserName(purchaserOrgEntity.getCompany());
        tXfSettlementEntity.setPurchaserTel(purchaserOrgEntity.getPhone());
        tXfSettlementEntity.setPurchaserTaxNo(purchaserOrgEntity.getTaxNo());
        tXfSettlementEntity.setAvailableAmount(tXfSettlementEntity.getAmountWithoutTax());
        tXfSettlementEntity.setTaxRate(taxRate);
        tXfSettlementEntity.setId(idSequence.nextId());
        tXfSettlementEntity.setSettlementNo("");
        tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getCode());
        tXfSettlementEntity.setCreateTime(new Date());
        tXfSettlementEntity.setUpdateTime(tXfSettlementEntity.getCreateTime());
        tXfSettlementEntity.setUpdateUser(0L);
        tXfSettlementEntity.setCreateUser(0L);
        return tXfSettlementEntity;
    }

    public TAcOrgEntity queryOrgInfo(String no) {

        return new TAcOrgEntity();
    }

    /**
     * 合并 索赔单为结算单
     * @return
     */
    public boolean mergeClaimSettlement() {
        /**
         * 查询符合条件的索赔单，购销一致维度，状态为待生成结算单
         */
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.querySuitableClaimBill(XFDeductionBusinessTypeEnum.CLAIM_BILL.getType(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode());
        TXfSettlementEntity tXfSettlementEntity = trans2Settlement(tXfBillDeductEntities);
        /**
         * 查询索赔单明细，组装结算单明细信息
         */
        return false;
    }

    enum DeductionHandleEnum {
        CLAIM_BILL(XFDeductionBusinessTypeEnum.CLAIM_BILL, x -> {
            TXfBillDeductEntity tXfBillDeductEntity = dataTrans(x);
            tXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
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
            tXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode());
            return tXfBillDeductEntity;
        }),
        EPD_BILL(XFDeductionBusinessTypeEnum.EPD_BILL,x -> {
            EPDBillData tmp = (EPDBillData) x;
            TXfBillDeductEntity tXfBillDeductEntity = dataTrans(  tmp);
            tXfBillDeductEntity.setAgreementMemo(tmp.getMemo());
            tXfBillDeductEntity.setAgreementReasonCode(tmp.getReasonCode());
            tXfBillDeductEntity.setAgreementReference(tmp.getReference());
            tXfBillDeductEntity.setAgreementTaxCode(tmp.getTaxCode());
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
        return tXfBillDeductEntity;
    }

}
