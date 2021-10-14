package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.model.AgreementBillData;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.modules.deduct.model.EPDBillData;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemRefEntity;
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
public class DeductService extends ServiceImpl {
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
    /**
     * 接收索赔明细
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
            //todo 调用匹配方法 补充明细信息
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
     * @return
     */
    public boolean matchClaimBill() {
        Date date = DateUtils.getFristDate();
        int billStartIndex = 0;
        int itemStartIndex = 0;
        int limit = 100;
        int batchAcount = 100;

        /**
         * 查询未匹配明细的索赔单
         */
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(date, billStartIndex, limit, XFDeductionBusinessTypeEnum.CLAIM_BILL.getType(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
        while (CollectionUtils.isNotEmpty(tXfBillDeductEntities)) {
            for (TXfBillDeductEntity tXfBillDeductEntity : tXfBillDeductEntities) {
                String sellerNo = tXfBillDeductEntity.getSellerNo();
                String purcharseNo = tXfBillDeductEntity.getPurchaserNo();
                BigDecimal taxRate = tXfBillDeductEntity.getTaxRate();
                if (StringUtils.isEmpty(sellerNo) || StringUtils.isEmpty(purcharseNo) || Objects.isNull(taxRate)) {
                    log.warn("索赔单{}主信息 不符合要求，sellerNo:{},purcharseNo:{},taxRate:{}",sellerNo,purcharseNo,taxRate);
                    continue;
                }
                TXfBillDeductEntity tmp = new TXfBillDeductEntity();
                BigDecimal billAmount = tXfBillDeductEntity.getAmountWithoutTax();
                tmp.setId(tXfBillDeductEntity.getId());
                tmp.setStatus( TXfBillDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode());
                /**
                 * 更新索赔单状态，无事务包含，后续已更改状态，单匹配未完成的，通过定时器，进行补偿
                 */
                tXfBillDeductExtDao.updateById(tmp);
                List<TXfBillDeductItemEntity> tXfBillDeductItemEntities = tXfBillDeductItemExtDao  .queryMatchBillItem(date, purcharseNo, sellerNo, taxRate, itemStartIndex, limit);
                while (billAmount.compareTo(BigDecimal.ZERO) == 0) {
                    for (TXfBillDeductItemEntity tXfBillDeductItemEntity : tXfBillDeductItemEntities) {
                        try {
                            billAmount = doItemMatch(tXfBillDeductEntity.getId(), tXfBillDeductItemEntity, billAmount);
                        } catch (Exception e) {
                            log.error("匹配索赔的明细 异常：{},{}",tXfBillDeductEntity.getId(),tXfBillDeductItemEntity.getId());
                        }
                    }
                    itemStartIndex = (itemStartIndex + 1) * batchAcount;
                    tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(date, purcharseNo, sellerNo, taxRate, itemStartIndex, limit);
                }
            }
            billStartIndex = (billStartIndex + 1) * batchAcount;
            tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(date, billStartIndex, limit, XFDeductionBusinessTypeEnum.CLAIM_BILL.getType(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
        }
        return true;
    }

    /**
     * 执行扣除明细，匹配主信息
     * @param billId
     * @param tXfBillDeductItemEntity
     * @param billAmount
     */
    @Transactional
    public BigDecimal doItemMatch(Long billId, TXfBillDeductItemEntity tXfBillDeductItemEntity, BigDecimal billAmount) {
        BigDecimal amount = tXfBillDeductItemEntity.getRemainingAmount();
        amount = billAmount .compareTo(amount) > 0 ? amount : billAmount;
        billAmount = billAmount.subtract(amount);
        int res = tXfBillDeductItemExtDao.updateBillItem(tXfBillDeductItemEntity.getId(), amount);
        if (res == 0) {
            return billAmount;
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
        return billAmount;
    }

    /**
     * 商品税编匹配
     * @param businessNo
     * @param deductionEnum
     * @param itemNo 商品编码
     * @return
     */
    public boolean matchTaxCode(String businessNo, XFDeductionBusinessTypeEnum deductionEnum, String itemNo) {
        return false;
    }

    /***
     * 结算单匹配蓝票
     * @param businessNo
     * @param amout
     * @param sellerNo
     * @return
     */
    public boolean settlementMatchInvoice(String businessNo, BigDecimal amout,String sellerNo) {
        return false;
    }

    /**
     *  合并协议单、EPD
     * @return
     */
    public boolean mergeBill(XFDeductionBusinessTypeEnum deductionEnum) {
        return false;
    }

    /**
     *  协议单、EPD 索赔单 合并结算单, 合并2年内的未匹配的单子
     * @return
     */
    public boolean mergeSettlement(XFDeductionBusinessTypeEnum deductionEnum) {
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
