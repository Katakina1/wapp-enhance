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
@Service("DeductClaimService")
@Slf4j
public class ClaimBillService extends DeductService{

    @Autowired
    private TXfBillDeductItemRefExtDao tXfBillDeductItemRefDao;
    @Autowired
    private TaxRateConfig taxRateConfig;
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
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(deductId,startDate, limit, XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
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
            tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(deductId,startDate,  limit, XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
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
        TXfSettlementEntity tXfSettlementEntity =  trans2Settlement(Arrays.asList(tXfBillDeductEntity), XFDeductionBusinessTypeEnum.CLAIM_BILL);
        tXfBillDeductExtDao.updateSuitableClaimBill(XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode(), TXfBillDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode(), tXfSettlementEntity.getSettlementNo(), tXfBillDeductEntity.getPurchaserNo(), tXfBillDeductEntity.getSellerNo());
    }
}
