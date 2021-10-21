package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.exception.NoSuchInvoiceException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.config.TaxRateConfig;
import com.xforceplus.wapp.enums.*;
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
public class AgreementBillService extends DeductService{

    /**
     *
     * @param deductionEnum
     * @param tXfBillDeductStatusEnum
     * @param targetStatus
     * @param manualChoice 页面手动选择的 单子明细
     * @return
     */
    public boolean mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum deductionEnum, TXfBillDeductStatusEnum tXfBillDeductStatusEnum, TXfBillDeductStatusEnum targetStatus, List<TXfBillDeductEntity> manualChoice) {
        Map<String, BigDecimal> nosuchInvoiceSeller = new HashMap<>();
        int expireScale = -5;
        /**
         * 获取超期时间 判断超过此日期的正数单据
         */
        Date referenceDate = DateUtils.addDate(DateUtils.getNow(), expireScale);
        //查询大于expireDate， 同一购销对，同一税率 总不含税金额为正的单据
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.querySuitablePositiveBill(referenceDate, deductionEnum.getValue(), tXfBillDeductStatusEnum.getCode(),TXfBillDeductStatusEnum.UNLOCK.getCode());
        if (CollectionUtils.isNotEmpty(manualChoice)) {
            tXfBillDeductEntities.addAll(manualChoice);
        }
        for (TXfBillDeductEntity tmp : tXfBillDeductEntities) {
            /**
             * 查询 同一购销对，同一税率 下所有的负数单据
             */
            TXfBillDeductEntity negativeBill = tXfBillDeductExtDao.querySpecialNegativeBill(tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate(), deductionEnum.getValue(), tXfBillDeductStatusEnum.getCode(),TXfBillDeductStatusEnum.UNLOCK.getCode());
            BigDecimal mergeAmount = negativeBill.getAmountWithoutTax().add(tmp.getAmountWithoutTax());
            //当前结算单 金额 大于 剩余发票金额
            if (nosuchInvoiceSeller.containsKey(tmp.getSellerNo()) && nosuchInvoiceSeller.get(tmp.getSellerNo()).compareTo(mergeAmount) < 0) {
                continue;
            }
            if (mergeAmount.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    excuteMergeAndMatch(deductionEnum, tmp, negativeBill, tXfBillDeductStatusEnum, referenceDate, targetStatus);
                } catch (NoSuchInvoiceException n ) {
                    nosuchInvoiceSeller.put(tmp.getSellerNo(), negativeBill.getAmountWithoutTax().add(tmp.getAmountWithoutTax()));
                    log.error(" 销方:{} 蓝票不足，匹配失败 ", deductionEnum.getDes(), tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate());
                }
                catch (Exception e) {
                    log.error("{}单合并异常 销方:{}，购方:{}，税率:{}", deductionEnum.getDes(), tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate());
                }
            } else {
                log.warn("{}单合并失败：合并收金额不为负数 购方:{}，购方:{}，税率:{}，手动勾选 {} ", deductionEnum.getDes(), tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate(),manualChoice);
            }
        }
        return false;
    }
    @Transactional
    public void excuteMergeAndMatch(XFDeductionBusinessTypeEnum deductionEnum,TXfBillDeductEntity tmp,TXfBillDeductEntity negativeBill, TXfBillDeductStatusEnum tXfBillDeductStatusEnum, Date referenceDate, TXfBillDeductStatusEnum targetSatus) {
        TXfSettlementEntity tXfSettlementEntity = batchUpdateMergeBill(deductionEnum, tmp, negativeBill, tXfBillDeductStatusEnum, referenceDate, targetSatus);
        //匹配蓝票
        String sellerTaxNo = tXfSettlementEntity.getSellerTaxNo();
        List<BlueInvoiceService.MatchRes> matchResList = blueInvoiceService.matchInvoiceInfo(tXfSettlementEntity.getAmountWithoutTax(), XFDeductionBusinessTypeEnum.AGREEMENT_BILL, tXfSettlementEntity.getSettlementNo(),sellerTaxNo);
        if (CollectionUtils.isEmpty(matchResList)) {
            throw new NoSuchInvoiceException();
        }
        //匹配税编
        Integer status = matchInfoTransfer(matchResList, tXfSettlementEntity.getSettlementNo());
        if(status == TXfSettlementItemFlagEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode()){
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode());
        }
        if(status == TXfSettlementItemFlagEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode()){
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode());
        }
        //更新结算状态为- 1.存在锁定、取消的协议单、EPD进行->撤销-- 2.税编匹配失败 ->待确认税编 3,存在反算明细->待确认明细 4->结算单进入待拆票状态
        TXfBillDeductEntity checkEntity = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode(), TXfBillDeductStatusEnum.UNLOCK.getCode());
        if ( checkEntity.getAmountWithoutTax().compareTo(tXfSettlementEntity.getAmountWithoutTax()) < 0 ) {
            log.error("{}单 存在锁定、取消的 sellerNo: {} purchaserNo: {} taxRate:{}", deductionEnum.getDes(),tmp.getSellerNo(), tmp.getPurchaserNo(), tmp.getTaxRate());
            throw new RuntimeException();
        }
        TXfSettlementEntity updadte = new TXfSettlementEntity();
        updadte.setId(tXfSettlementEntity.getId());
        updadte.setSettlementStatus(tXfSettlementEntity.getSettlementStatus());
        tXfSettlementDao.updateById(updadte);
    }

    private  Integer  matchInfoTransfer(List<BlueInvoiceService.MatchRes> res,String settlementNo) {
        Date date = new Date();
        Integer status = TXfSettlementItemFlagEnum.NORMAL.getCode();
        for (BlueInvoiceService.MatchRes matchRes : res) {
            for(BlueInvoiceService.InvoiceItem invoiceItem:matchRes.getInvoiceItems()){
                TXfSettlementItemEntity tXfSettlementItemEntity = new TXfSettlementItemEntity();
                tXfSettlementItemEntity.setUnitPrice(invoiceItem.getUnitPrice());
                tXfSettlementItemEntity.setTaxAmount(invoiceItem.getTaxAmount());
                tXfSettlementItemEntity.setGoodsTaxNo(invoiceItem.getGoodsNum());
                tXfSettlementItemEntity.setTaxRate(invoiceItem.getTaxRate());
                tXfSettlementItemEntity.setAmountWithoutTax(invoiceItem.getDetailAmount());
                tXfSettlementItemEntity.setRemark(StringUtils.EMPTY);
                tXfSettlementItemEntity.setAmountWithTax(invoiceItem.getDetailAmount().add(invoiceItem.getTaxAmount()));
                tXfSettlementItemEntity.setUnitPriceWithTax(tXfSettlementItemEntity.getAmountWithTax().divide(tXfSettlementItemEntity.getQuantity(), 6, RoundingMode.HALF_UP));
                tXfSettlementItemEntity.setCreateUser(0l);
                tXfSettlementItemEntity.setUpdateUser(0l);
                tXfSettlementItemEntity.setId(idSequence.nextId());
                tXfSettlementItemEntity.setSettlementNo(settlementNo);
                tXfSettlementItemEntity.setCreateTime(date);
                tXfSettlementItemEntity.setUpdateTime(date);
                tXfSettlementItemEntity.setQuantity(invoiceItem.getNum());
                tXfSettlementItemEntity.setItemCode(invoiceItem.getItemNo());
                tXfSettlementItemEntity.setThridId(invoiceItem.getItemId());
                tXfSettlementItemEntity.setUnitPrice(invoiceItem.getUnitPrice());
                tXfSettlementItemEntity.setItemName(invoiceItem.getGoodsName());
                tXfSettlementItemEntity.setItemFlag(TXfSettlementItemFlagEnum.NORMAL.getCode());
                tXfSettlementItemEntity.setGoodsNoVer("33.0");
                tXfSettlementItemEntity.setZeroTax(StringUtils.EMPTY);
                tXfSettlementItemEntity.setTaxPre(StringUtils.EMPTY);
                tXfSettlementItemEntity.setTaxPreCon(StringUtils.EMPTY);
                tXfSettlementItemEntity = checkItem(  tXfSettlementItemEntity);
                if (tXfSettlementItemEntity.getItemFlag() != TXfSettlementItemFlagEnum.NORMAL.getCode()) {
                    status = tXfSettlementItemEntity.getItemFlag();
                }
                tXfSettlementItemDao.insert(tXfSettlementItemEntity);
            }
            TXfBillDeductInvoiceEntity tXfBillDeductInvoiceEntity = new TXfBillDeductInvoiceEntity();
            tXfBillDeductInvoiceEntity.setId(idSequence.nextId());
            tXfBillDeductInvoiceEntity.setBusinessNo(settlementNo);
            tXfBillDeductInvoiceEntity.setBusinessType(TXfInvoiceDeductTypeEnum.SETTLEMENT.getCode());
            tXfBillDeductInvoiceEntity.setInvoiceCode(matchRes.invoiceCode);
            tXfBillDeductInvoiceEntity.setInvoiceNo(matchRes.invoiceNo);
            tXfBillDeductInvoiceEntity.setCreateDate(date);
            tXfBillDeductInvoiceEntity.setUpdateDate(date);
            tXfBillDeductInvoiceEntity.setId(idSequence.nextId());
            tXfBillDeductInvoiceEntity.setUseAmount(matchRes.deductedAmount);
            tXfBillDeductInvoiceEntity.setStatus(TXfInvoiceDeductStatusEnum.NORMAL.getCode());
            tXfBillDeductInvoiceDao.insert(tXfBillDeductInvoiceEntity);
        }
        return status;
    }

    private TXfSettlementItemEntity checkItem(TXfSettlementItemEntity tXfSettlementItemEntity ) {
        Optional<TaxCode> taxCodeOptional = taxCodeService.getTaxCodeByItemNo(tXfSettlementItemEntity.getItemCode());

        if (!taxCodeOptional.isPresent()) {
            tXfSettlementItemEntity.setItemFlag(TXfSettlementItemFlagEnum.WAIT_MATCH_TAX_CODE.getCode());
        }
        BigDecimal ta = tXfSettlementItemEntity.getQuantity().multiply(tXfSettlementItemEntity.getUnitPrice()).setScale(2, RoundingMode.HALF_UP);
        if (ta.compareTo(tXfSettlementItemEntity.getAmountWithoutTax()) !=0) {
            tXfSettlementItemEntity.setItemFlag(TXfSettlementItemFlagEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode());
        }

        return tXfSettlementItemEntity;
    }

    /**
     * 批量进行更新操作 保证单进程操作
     * @param tmp
     * @param tXfBillDeductStatusEnum
     * @param referenceDate
     * @param targetSatus
     * @return
     */

    public TXfSettlementEntity batchUpdateMergeBill(XFDeductionBusinessTypeEnum deductionEnum,TXfBillDeductEntity tmp,TXfBillDeductEntity negativeBill, TXfBillDeductStatusEnum tXfBillDeductStatusEnum, Date referenceDate, TXfBillDeductStatusEnum targetSatus) {
        String purchaserNo = tmp.getPurchaserNo();
        String sellerNo = tmp.getSellerNo();
        BigDecimal taxRate = tmp.getTaxRate();
        Integer type = deductionEnum.getValue();
        Integer status = tXfBillDeductStatusEnum.getCode();
        Integer targetStatus = targetSatus.getCode();
        List<TXfBillDeductEntity> tXfBillDeductEntities = new ArrayList<>();
        tXfBillDeductEntities.add(tmp);
        tXfBillDeductEntities.add(negativeBill);
        TXfSettlementEntity tXfSettlementEntity =  trans2Settlement(tXfBillDeductEntities,deductionEnum);
        tXfBillDeductExtDao.updateMergeNegativeBill(tXfSettlementEntity.getSettlementNo(),purchaserNo, sellerNo, taxRate, type, status, targetStatus, TXfBillDeductStatusEnum.UNLOCK.getCode());
        tXfBillDeductExtDao.updateMergePositiveBill(tXfSettlementEntity.getSettlementNo(),purchaserNo, sellerNo, taxRate, referenceDate, type, status, targetStatus, TXfBillDeductStatusEnum.UNLOCK.getCode());
        /**
         * 更新完成后，进行此结算下的数据校验，校验通过，提交，失败，回滚：表示有的新的单子进来，不满足条件了,回滚操作
         */
        tmp = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo(),status, TXfBillDeductStatusEnum.UNLOCK.getCode());
        if (tmp.getAmountWithoutTax().compareTo(BigDecimal.ZERO) <= 0 ) {
            /**
             * tmp.getAmountWithoutTax().compareTo(BigDecimal.ZERO) <= 0 说明在更新过程钟，新的单据被更新到,而且更新到的负数大于正数，合并失败
             */

            log.error("{}单 超期的正值单据+负数单据，小于 0  sellerNo: {} purchaserNo: {} taxRate:{}",deductionEnum.getDes(), tmp.getSellerNo(), tmp.getPurchaserNo(), tmp.getTaxRate());
            throw new RuntimeException("");
        }
        if (  tmp.getAmountWithoutTax().compareTo(tXfSettlementEntity.getAmountWithoutTax()) <0) {

            /**
             * tmp.getAmountWithoutTax().compareTo(tXfSettlementEntity.getAmountWithoutTax()) <0 说明被合并的单据发生了 取消或锁定,需要回撤操作
             */
            log.error("{}单 存在锁定、取消的 sellerNo: {} purchaserNo: {} taxRate:{}", deductionEnum.getDes(),tmp.getSellerNo(), tmp.getPurchaserNo(), tmp.getTaxRate());
            throw new RuntimeException("");
        }
        return tXfSettlementEntity;
    }



}
