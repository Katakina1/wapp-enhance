package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.common.exception.NoSuchInvoiceException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.config.TaxRateConfig;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfInvoiceDeductTypeEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.exceptionreport.event.NewExceptionReportEvent;
import com.xforceplus.wapp.repository.dao.TXfBillDeductItemRefExtDao;
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
 * 类描述：扣除单通用方法
 *
 * @ClassName DeductionService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 11:38
 */
@Service
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
        int limit = 50;
        /**
         * 查询未匹配明细的索赔单
         */
        Long deductId = 1L;
        Map<String, BigDecimal> nosuchInvoiceSeller = new HashMap<>();
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(deductId,null, limit, XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
        while (CollectionUtils.isNotEmpty(tXfBillDeductEntities)) {
            for (TXfBillDeductEntity tXfBillDeductEntity : tXfBillDeductEntities) {
                String sellerNo = tXfBillDeductEntity.getSellerNo();
                String purcharseNo = tXfBillDeductEntity.getPurchaserNo();
                if (StringUtils.isEmpty(sellerNo) || StringUtils.isEmpty(purcharseNo)) {
                    log.info("发现购销对信息不合法 跳过明细匹配：sellerNo : {} purcharseNo : {}",sellerNo,purcharseNo);
                    continue;
                }
                BigDecimal taxRate = tXfBillDeductEntity.getTaxRate();
                if (StringUtils.isEmpty(sellerNo) || StringUtils.isEmpty(purcharseNo) || Objects.isNull(taxRate)) {
                    log.warn("索赔单{} 主信息 不符合要求，sellerNo:{},purcharseNo:{},taxRate:{}",sellerNo,purcharseNo,taxRate);
                    continue;
                }
                if (StringUtils.isEmpty(tXfBillDeductEntity.getBusinessNo())  ) {
                    log.warn("索赔单 主信息单号为空 跳过匹配 sellerNo:{},purcharseNo:{},taxRate:{} ",sellerNo,purcharseNo,taxRate);
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
                List<TXfBillDeductItemEntity> tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(startDate,endDate, purcharseNo, sellerNo, taxRate,  itemId,limit, tXfBillDeductEntity.getBusinessNo());
                while (billAmount.compareTo(BigDecimal.ZERO) > 0) {
                    if (CollectionUtils.isEmpty(tXfBillDeductItemEntities)) {
                        taxRate = taxRateConfig.getNextTaxRate(taxRate);
                        if (Objects.isNull(taxRate)) {
                            log.warn("{} 索赔的，未找到足够的索赔单明细，结束匹配",tXfBillDeductEntity.getId());
                            break;
                        }
                        itemId = 0L;
                        tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(startDate,endDate, purcharseNo, sellerNo, taxRate, itemId, limit,tXfBillDeductEntity.getBusinessNo());
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
                    tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryMatchBillItem(startDate,endDate, purcharseNo, sellerNo, taxRate, itemId, limit,tXfBillDeductEntity.getBusinessNo());
                }
                /**
                 * 匹配失败，明细金额不足
                 */
                if (billAmount.compareTo(BigDecimal.ZERO) > 0) {
                    NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
                    newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
                    newExceptionReportEvent.setReportCode( ExceptionReportCodeEnum.NOT_MATCH_CLAIM_DETAIL );
                    newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
                    applicationContext.publishEvent(newExceptionReportEvent);
                    log.error("索赔单匹配明细失败 {}  发送明细金额不足例外报告 {} ", tXfBillDeductEntity.getBusinessNo(), newExceptionReportEvent);
                    continue;
                }
                /**
                 * 匹配完成 进行绑定操作
                 */
                if (CollectionUtils.isNotEmpty(matchItem)) {
                    try {
                        tXfBillDeductEntity =  doItemMatch(tXfBillDeductEntity, matchItem);
                        claimMatchBlueInvoice(tXfBillDeductEntity, nosuchInvoiceSeller);
                    } catch (Exception e) {
                        log.error("索赔单 明细匹配 蓝票匹配异常：{}", e);
                    }
                }
            }
            deductId =  tXfBillDeductEntities.stream().mapToLong(TXfBillDeductEntity::getId).max().getAsLong();
            /**
             * 执行下一批匹配
             */
            tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(deductId,null,  limit, XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
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
    public TXfBillDeductEntity doItemMatch(TXfBillDeductEntity tXfBillDeductEntity, List<TXfBillDeductItemEntity> tXfBillDeductItemEntitys ) {
        Long billId = tXfBillDeductEntity.getId();
        BigDecimal billAmount = tXfBillDeductEntity.getAmountWithoutTax();
        BigDecimal taxAmount = tXfBillDeductEntity.getTaxAmount();
        /**
         * false 表示 存在未匹配税编的明细
         *
         */
        Boolean matchTaxNoFlag = true;
        Boolean taxAmountDiff = true;
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

            TXfBillDeductItemRefEntity tXfBillDeductItemRefEntity = new TXfBillDeductItemRefEntity();
            tXfBillDeductItemRefEntity.setId(idSequence.nextId());
            tXfBillDeductItemRefEntity.setCreateTime(DateUtils.getNowDate());
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
        taxAmountDiff = taxAmountOther.compareTo(taxAmount) == 0;
        if (!matchTaxNoFlag  ) {
            NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
            newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
            newExceptionReportEvent.setReportCode( ExceptionReportCodeEnum.NOT_MATCH_GOODS_TAX );
            newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
            applicationContext.publishEvent(newExceptionReportEvent);
            log.error("索赔单 {}  发送税编匹配例外报告 {} ", tXfBillDeductEntity.getBusinessNo(), newExceptionReportEvent);
        }
        if ( !taxAmountDiff) {
            NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
            newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
            newExceptionReportEvent.setReportCode( ExceptionReportCodeEnum.WITH_DIFF_TAX);
            newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
            applicationContext.publishEvent(newExceptionReportEvent);
            log.error("索赔单 {}  发送税差例外报告 {} ", tXfBillDeductEntity.getBusinessNo(), newExceptionReportEvent);
        }
        /**
         * 如果存在未匹配的税编，状态未待匹配税编，
         */
        Integer status = matchTaxNoFlag ?  TXfBillDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode()  : TXfBillDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode() ;
        tmp.setStatus(status);
        tXfBillDeductExtDao.updateById(tmp);
        tXfBillDeductEntity.setStatus(status);
        return tXfBillDeductEntity;
    }

    /**
     * 重新补充税编
     * @param deductId
     */
    public void reMatchClaimTaxCode(Long deductId) {
        List<TXfBillDeductItemEntity> tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryItemsByBillId(deductId, TXfInvoiceDeductTypeEnum.CLAIM.getCode(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_TAX_NO.getCode());
        tXfBillDeductItemEntities =   tXfBillDeductItemEntities.stream().filter(x -> StringUtils.isEmpty(x.getGoodsTaxNo())).map(x-> fixTaxCode(x)).collect(Collectors.toList());
        tXfBillDeductItemEntities =   tXfBillDeductItemEntities.stream().filter(x -> StringUtils.isEmpty(x.getGoodsTaxNo())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tXfBillDeductItemEntities)) {
            TXfBillDeductEntity tXfBillDeductEntity = new TXfBillDeductEntity();
            tXfBillDeductEntity.setId(deductId);
            tXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode());
            tXfBillDeductExtDao.updateById(tXfBillDeductEntity);
        }
    }

    /**
     * 索赔单 匹配蓝票
     *
     * @return
     */
    public boolean claimMatchBlueInvoice() {
        Long deductId = 1L;
        Map<String, BigDecimal> nosuchInvoiceSeller = new HashMap<>();
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(deductId,null, 2, XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode());
        while (CollectionUtils.isNotEmpty(tXfBillDeductEntities)) {
            for (TXfBillDeductEntity tXfBillDeductEntity : tXfBillDeductEntities) {
                 try {
                    claimMatchBlueInvoice(tXfBillDeductEntity, nosuchInvoiceSeller);
                } catch (Exception e) {
                        log.error("蓝票匹配索赔异常：{} 单据：{}",e,tXfBillDeductEntity.getBusinessNo());
                }
            }
            deductId = tXfBillDeductEntities.stream().mapToLong(TXfBillDeductEntity::getId).max().getAsLong();
            tXfBillDeductEntities = tXfBillDeductExtDao.queryUnMatchBill(deductId, null, 2, XFDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode());
        }
        return false;
    }
            /**
             * 索赔单 匹配蓝票
             *
             * @return
             */
    @Transactional
    public boolean claimMatchBlueInvoice(TXfBillDeductEntity tXfBillDeductEntity,Map<String, BigDecimal> nosuchInvoiceSeller) {
        List<BlueInvoiceService.MatchRes> matchResList = null;
        try {
            if (tXfBillDeductEntity.getStatus().compareTo(TXfBillDeductStatusEnum.CLAIM_NO_MATCH_BLUE_INVOICE.getCode()) != 0) {
                log.info("{} 类型单据{} 状态为{} 跳过匹配蓝票 ", "索赔单", tXfBillDeductEntity.getBusinessNo(), tXfBillDeductEntity.getStatus());
                return false;
            }
            //索赔单 金额 大于 剩余发票金额
            if (nosuchInvoiceSeller.containsKey(tXfBillDeductEntity.getSellerNo()) && nosuchInvoiceSeller.get(tXfBillDeductEntity.getSellerNo()).compareTo(tXfBillDeductEntity.getAmountWithoutTax()) < 0) {
                log.error("{} 类型单据 销方:{}  蓝票不足，匹配失败 单号 {}", "索赔单", tXfBillDeductEntity.getSellerNo(), tXfBillDeductEntity.getBusinessNo());
                return false;
            }
            TAcOrgEntity tAcSellerOrgEntity = queryOrgInfo(tXfBillDeductEntity.getSellerNo(), true);
            TAcOrgEntity tAcPurcharserOrgEntity = queryOrgInfo(tXfBillDeductEntity.getPurchaserNo(), false);
            if (Objects.isNull(tAcPurcharserOrgEntity) || Objects.isNull(tAcSellerOrgEntity)) {
                log.info(" 购销方信息不完整 sellerNo : {} sellerOrgEntity{}  purcharseNo : {} purchaserOrgEntity：{}", tXfBillDeductEntity.getSellerNo(),tAcSellerOrgEntity,tXfBillDeductEntity.getPurchaserNo(),tAcPurcharserOrgEntity);
                return false;
            }
            //按照索赔单金额（负数），转正后，匹配
            matchResList = blueInvoiceService.matchInvoiceInfo(tXfBillDeductEntity.getAmountWithoutTax() , XFDeductionBusinessTypeEnum.CLAIM_BILL, tXfBillDeductEntity.getBusinessNo(), tAcSellerOrgEntity.getTaxNo(), tAcPurcharserOrgEntity.getTaxNo(),tXfBillDeductEntity.getTaxRate().multiply(BigDecimal.valueOf(100)));
            if (CollectionUtils.isEmpty(matchResList)) {
                log.error("{} 类型单据 销方:{}  蓝票不足，匹配失败 单号 {}", "索赔单", tXfBillDeductEntity.getSellerNo(), tXfBillDeductEntity.getBusinessNo());
                nosuchInvoiceSeller.put(tXfBillDeductEntity.getSellerNo(), tXfBillDeductEntity.getAmountWithoutTax());
                return false;
            }
            matchInfoTransfer(matchResList, tXfBillDeductEntity.getBusinessNo(), tXfBillDeductEntity.getId(), XFDeductionBusinessTypeEnum.CLAIM_BILL);
            TXfBillDeductEntity tmp = new TXfBillDeductEntity();
            tmp.setStatus(TXfBillDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode());
            tmp.setId(tXfBillDeductEntity.getId());
            tXfBillDeductExtDao.updateById(tmp);
        }
        catch (NoSuchInvoiceException n ) {
            NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
            newExceptionReportEvent.setDeduct(tXfBillDeductEntity);
            newExceptionReportEvent.setReportCode( ExceptionReportCodeEnum.NOT_FOUND_BLUE_TAX_RATE );
            newExceptionReportEvent.setType( ExceptionReportTypeEnum.CLAIM );
            applicationContext.publishEvent(newExceptionReportEvent);
            log.info(" 索赔单 单据匹配合并失败销方蓝票不足->sellerNo : {} purcharseNo : {} businessNo",tXfBillDeductEntity.getSellerNo(),tXfBillDeductEntity.getPurchaserNo(),tXfBillDeductEntity.getBusinessNo());
        }
        catch (Exception e) {
            if (CollectionUtils.isNotEmpty(matchResList)) {
                List<String> invoiceList = matchResList.stream().map(x -> x.getInvoiceCode() + "=---" + x.getInvoiceNo()).collect(Collectors.toList());
                log.error(" 索赔单 匹配蓝票 回撤匹配信息 单据id {} 回撤匹配信息:{}", e,tXfBillDeductEntity.getId(),invoiceList );
                blueInvoiceService.withdrawInvoices(matchResList);
            }
            log.error(" 索赔单 匹配蓝票 异常：{}  单据id {}", e,tXfBillDeductEntity.getId());
            throw e;
        }
        return false;
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

                log.error("索赔单组合结算失败: purchase_no :{} ,seller_no:{} status: {} Exception:{}",tXfBillDeductEntity.getPurchaserNo(),tXfBillDeductEntity.getSellerNo(),TXfBillDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getDesc(),e);
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
