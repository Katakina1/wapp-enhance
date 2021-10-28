package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.exception.NoSuchInvoiceException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.exceptionreport.event.NewExceptionReportEvent;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
public class AgreementBillService extends DeductService{

    /**
     *
     * @param deductionEnum
     * @param tXfBillDeductStatusEnum
     * @param targetStatus
     * @return
     */
    public boolean mergeEPDandAgreementSettlement(XFDeductionBusinessTypeEnum deductionEnum, TXfBillDeductStatusEnum tXfBillDeductStatusEnum, TXfBillDeductStatusEnum targetStatus ) {
        Map<String, BigDecimal> nosuchInvoiceSeller = new HashMap<>();
        /**
         * 获取超期时间 判断超过此日期的正数单据
         */
        Integer referenceDate =  defaultSettingService.getOverdueDay(deductionEnum == XFDeductionBusinessTypeEnum.AGREEMENT_BILL ? DefaultSettingEnum.AGREEMENT_OVERDUE_DEFAULT_DAY : DefaultSettingEnum.EPD_OVERDUE_DEFAULT_DAY);
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.querySuitablePositiveBill(referenceDate, deductionEnum.getValue(), tXfBillDeductStatusEnum.getCode(),TXfBillDeductStatusEnum.UNLOCK.getCode());
        if (CollectionUtils.isEmpty(tXfBillDeductEntities)) {
            log.info("未找到符合条件的单据，跳过合并单据");
            return false;
        }
        for (TXfBillDeductEntity tmp : tXfBillDeductEntities) {
            /**
             * 查询 同一购销对，同一税率 下所有的负数单据
             */
            String sellerNo = tmp.getSellerNo();
            String purchaserNo = tmp.getPurchaserNo();
            if (org.apache.commons.lang3.StringUtils.isEmpty(sellerNo) || org.apache.commons.lang3.StringUtils.isEmpty(purchaserNo)) {
                log.info("发现购销对信息不合法 跳过{}单据合并：sellerNo : {} purcharseNo : {}",deductionEnum.getDes(),sellerNo,purchaserNo);
                continue;
            }
            TXfBillDeductEntity negativeBill = tXfBillDeductExtDao.querySpecialNegativeBill(tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate(), deductionEnum.getValue(), tXfBillDeductStatusEnum.getCode(),TXfBillDeductStatusEnum.UNLOCK.getCode());
            BigDecimal mergeAmount =  tmp.getAmountWithoutTax();
            BigDecimal negativeBillAmount = BigDecimal.ZERO;
            if (Objects.nonNull(negativeBill)) {
                negativeBillAmount = negativeBill.getAmountWithoutTax();
                mergeAmount = negativeBillAmount.add(mergeAmount);
            }
            //当前结算单 金额 大于 剩余发票金额
            if (nosuchInvoiceSeller.containsKey(tmp.getSellerNo()) && nosuchInvoiceSeller.get(tmp.getSellerNo()).compareTo(mergeAmount) < 0) {
                log.info(" {} 单据匹配合并失败销方蓝票不足->sellerNo : {} purcharseNo : {}",deductionEnum.getDes(),sellerNo,purchaserNo);
                continue;
            }
            TAcOrgEntity purchaserOrgEntity = queryOrgInfo(purchaserNo,false);
            TAcOrgEntity sellerOrgEntity = queryOrgInfo(sellerNo, true);
            if (Objects.isNull(purchaserOrgEntity) || Objects.isNull(sellerOrgEntity)) {
                log.info(" 购销方信息不完整 sellerNo : {} sellerOrgEntity：{}  purcharseNo : {}  purchaserOrgEntity：{}", sellerNo,sellerOrgEntity,purchaserNo,purchaserOrgEntity);
                continue;
            }
            if (mergeAmount.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    Integer expireScale =  overdueService.oneOptBySellerNo(deductionEnum == XFDeductionBusinessTypeEnum.AGREEMENT_BILL ? ServiceTypeEnum.AGREEMENT : ServiceTypeEnum.EPD, sellerNo);
                    Date expireDate =    DateUtils.addDate(DateUtils.getNow(), expireScale);
                    excuteMergeAndMatch(deductionEnum, tmp, negativeBill, tXfBillDeductStatusEnum, expireDate, targetStatus);
                } catch (NoSuchInvoiceException n ) {
                    NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
                    newExceptionReportEvent.setDeduct(tmp);
                    newExceptionReportEvent.setReportCode( ExceptionReportCodeEnum.NOT_MATCH_BLUE_INVOICE );
                    newExceptionReportEvent.setType(deductionEnum == XFDeductionBusinessTypeEnum.EPD_BILL?ExceptionReportTypeEnum.EPD:ExceptionReportTypeEnum.AGREEMENT);
                    applicationContext.publishEvent(newExceptionReportEvent);
                    nosuchInvoiceSeller.put(tmp.getSellerNo(), negativeBillAmount.add(tmp.getAmountWithoutTax()));
                    log.info(" {} 单据匹配合并失败销方蓝票不足->sellerNo : {} purcharseNo : {}",deductionEnum.getDes(),sellerNo,purchaserNo);
                }
                catch (Exception e) {
                    log.error("{}单合并异常 销方:{}，购方:{}，税率:{}", deductionEnum.getDes(), tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate());
                }
            } else {
                log.warn("{}单合并失败：合并收金额不为负数 购方:{}，购方:{}，税率:{}   ", deductionEnum.getDes(), tmp.getPurchaserNo(), tmp.getSellerNo(), tmp.getTaxRate() );
            }
        }
        return false;
    }

    @Transactional
    public void excuteMergeAndMatch(XFDeductionBusinessTypeEnum deductionEnum,TXfBillDeductEntity tmp,TXfBillDeductEntity negativeBill, TXfBillDeductStatusEnum tXfBillDeductStatusEnum, Date referenceDate, TXfBillDeductStatusEnum targetStatus) {
        TXfSettlementEntity tXfSettlementEntity = executeMerge(deductionEnum, tmp, negativeBill, tXfBillDeductStatusEnum, referenceDate, targetStatus);
        executeMatch(deductionEnum, tXfSettlementEntity,targetStatus.getCode());
    }

    /**
     * 执行结算单匹配蓝票
     * @param deductionEnum
     * @param tXfSettlementEntity
     */
    public void executeMatch(XFDeductionBusinessTypeEnum deductionEnum, TXfSettlementEntity tXfSettlementEntity,Integer targetStatus) {
        //匹配蓝票
        String sellerTaxNo = tXfSettlementEntity.getSellerTaxNo();
        List<BlueInvoiceService.MatchRes> matchResList = blueInvoiceService.matchInvoiceInfo(tXfSettlementEntity.getAmountWithoutTax(), deductionEnum, tXfSettlementEntity.getSettlementNo(),sellerTaxNo,tXfSettlementEntity.getPurchaserTaxNo());
        if (CollectionUtils.isEmpty(matchResList)) {
            log.error("{} 类型单据 销方:{}  蓝票不足，匹配失败 ", deductionEnum.getDes(), sellerTaxNo);
            throw new NoSuchInvoiceException();
        }
        try {
            //匹配税编
            Integer status = matchInfoTransfer(matchResList, tXfSettlementEntity.getSettlementNo(),tXfSettlementEntity.getId(),deductionEnum);
            if(status == TXfSettlementItemFlagEnum.WAIT_MATCH_TAX_CODE.getCode()){
                tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode());
            }
            if(status == TXfSettlementItemFlagEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode()){
                tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode());
            }
            //更新结算状态为- 1.存在锁定、取消的协议单、EPD进行->撤销-- 2.税编匹配失败 ->待确认税编 3,存在反算明细->待确认明细 4->结算单进入待拆票状态
            TXfBillDeductEntity checkEntity = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo(), targetStatus, TXfBillDeductStatusEnum.UNLOCK.getCode());
            if ( checkEntity.getAmountWithoutTax().compareTo(tXfSettlementEntity.getAmountWithoutTax()) < 0 ) {
                log.error("{}单 存在锁定、取消的 sellerNo: {} purchaserNo: {} taxRate:{}", deductionEnum.getDes(),tXfSettlementEntity.getSellerNo(), tXfSettlementEntity.getPurchaserNo() );
                throw new EnhanceRuntimeException("存在已锁定的业务单");
            }
            TXfSettlementEntity updadte = new TXfSettlementEntity();
            updadte.setId(tXfSettlementEntity.getId());
            updadte.setSettlementStatus(tXfSettlementEntity.getSettlementStatus());
            tXfSettlementDao.updateById(updadte);
        } catch (Exception e) {
            if (CollectionUtils.isNotEmpty(matchResList)) {
                List<String> invoiceList = matchResList.stream().map(x -> x.getInvoiceCode() + "=---" + x.getInvoiceNo()).collect(Collectors.toList());
                log.error(" 结算匹配蓝票 回撤匹配信息 单  回撤匹配信息:{},{}", e,invoiceList );
                blueInvoiceService.withdrawInvoices(matchResList);
            }
            log.error("结算单匹配蓝票失败："+e.getMessage(), e);
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * 手动合并结算单
     * @param ids
     * @param xfDeductionBusinessTypeEnum
     * @return
     */
    @Transactional
    public TXfSettlementEntity mergeSettlementByManual(List<Long> ids, XFDeductionBusinessTypeEnum xfDeductionBusinessTypeEnum) {
        if (CollectionUtils.isEmpty(ids)) {
            log.error("选择的{} 单据列表{}，查询符合条件结果为空",xfDeductionBusinessTypeEnum.getDes(),ids);
            throw new EnhanceRuntimeException("至少选择一张单据");
        }
        String idsStr =  StringUtils.join(ids, ",");
        idsStr = "(" + idsStr + ")";
        TXfBillDeductStatusEnum statusEnum;
        TXfBillDeductStatusEnum targetStatus;
        switch (xfDeductionBusinessTypeEnum){
            case AGREEMENT_BILL:
                statusEnum=TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT;
                targetStatus=TXfBillDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT;
                break;
            case EPD_BILL:
                statusEnum=TXfBillDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT;
                targetStatus=TXfBillDeductStatusEnum.EPD_MATCH_SETTLEMENT;
                break;
            default:throw new EnhanceRuntimeException("手动合并结算单仅支持协议单和EPD");
        }
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.querySuitableBillById(idsStr, xfDeductionBusinessTypeEnum.getValue(), statusEnum.getCode(), TXfBillDeductStatusEnum.UNLOCK.getCode());
        if (CollectionUtils.isEmpty(tXfBillDeductEntities)  ) {
            log.error("选择的{} 单据列表{}，查询符合条件结果为空",xfDeductionBusinessTypeEnum.getDes(),ids);
            throw new EnhanceRuntimeException("未查询到待匹配结算单的单据");
        }

        if (tXfBillDeductEntities.size() != 1) {
            log.error("选择的{} 单据列表{}，查询符合条件结果分组为{}",xfDeductionBusinessTypeEnum.getDes(),ids,tXfBillDeductEntities.size());
            throw new EnhanceRuntimeException("您选择的单据为多税率或购销方不一致");
        }

        if (tXfBillDeductEntities.get(0).getAmountWithoutTax().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("选择的{} 单据列表{}，查询结果总金额为{}",xfDeductionBusinessTypeEnum.getDes(),ids,tXfBillDeductEntities.get(0).getAmountWithoutTax());
            throw new EnhanceRuntimeException("选择单据的总金额不能小于0");
        }
        TXfSettlementEntity tXfSettlementEntity = trans2Settlement(tXfBillDeductEntities, xfDeductionBusinessTypeEnum);
        tXfBillDeductExtDao.updateBillById(idsStr, tXfSettlementEntity.getSettlementNo(), xfDeductionBusinessTypeEnum.getValue(), statusEnum.getCode(), TXfBillDeductStatusEnum.UNLOCK.getCode(), targetStatus.getCode());
        TXfBillDeductEntity  tmp = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo(),targetStatus.getCode(), TXfBillDeductStatusEnum.UNLOCK.getCode());
        checkDeduct(tmp, tXfSettlementEntity, xfDeductionBusinessTypeEnum);
        executeMatch(xfDeductionBusinessTypeEnum, tXfSettlementEntity,targetStatus.getCode());
        return tXfSettlementEntity;
    }

        /**
         * 执行单据合并业务单
         * @param tmp
         * @param tXfBillDeductStatusEnum
         * @param referenceDate
         * @param targetSatus
         * @return
         */

    public TXfSettlementEntity executeMerge(XFDeductionBusinessTypeEnum deductionEnum,TXfBillDeductEntity tmp,TXfBillDeductEntity negativeBill, TXfBillDeductStatusEnum tXfBillDeductStatusEnum, Date referenceDate, TXfBillDeductStatusEnum targetSatus) {
        String purchaserNo = tmp.getPurchaserNo();
        String sellerNo = tmp.getSellerNo();
        BigDecimal taxRate = tmp.getTaxRate();
        Integer type = deductionEnum.getValue();
        Integer status = tXfBillDeductStatusEnum.getCode();
        Integer targetStatus = targetSatus.getCode();
        List<TXfBillDeductEntity> tXfBillDeductEntities = new ArrayList<>();
        tXfBillDeductEntities.add(tmp);
        if (Objects.nonNull(negativeBill)) {
            tXfBillDeductEntities.add(negativeBill);
        }
        TXfSettlementEntity tXfSettlementEntity =  trans2Settlement(tXfBillDeductEntities,deductionEnum);
        tXfBillDeductExtDao.updateMergeNegativeBill(tXfSettlementEntity.getSettlementNo(),purchaserNo, sellerNo, taxRate, type, status, targetStatus, TXfBillDeductStatusEnum.UNLOCK.getCode());
        tXfBillDeductExtDao.updateMergePositiveBill(tXfSettlementEntity.getSettlementNo(),purchaserNo, sellerNo, taxRate, referenceDate, type, status, targetStatus, TXfBillDeductStatusEnum.UNLOCK.getCode());
        /**
         * 更新完成后，进行此结算下的数据校验，校验通过，提交，失败，回滚：表示有的新的单子进来，不满足条件了,回滚操作
         */
        tmp = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo(),targetStatus, TXfBillDeductStatusEnum.UNLOCK.getCode());
        checkDeduct(tmp, tXfSettlementEntity, deductionEnum);
        return tXfSettlementEntity;
    }

    /**
     * 检查合并后的结果
     * @param tmp
     * @param tXfSettlementEntity
     * @param xfDeductionBusinessTypeEnum
     */
    private void checkDeduct(TXfBillDeductEntity  tmp,TXfSettlementEntity tXfSettlementEntity,XFDeductionBusinessTypeEnum xfDeductionBusinessTypeEnum) {
        if (tmp.getAmountWithoutTax().compareTo(BigDecimal.ZERO) <= 0 ) {
            /**
             * tmp.getAmountWithoutTax().compareTo(BigDecimal.ZERO) <= 0 说明在更新过程钟，新的单据被更新到,而且更新到的负数大于正数，合并失败
             */
            log.error("{}单 超期的正值单据+负数单据，小于 0  sellerNo: {} purchaserNo: {} taxRate:{}",xfDeductionBusinessTypeEnum.getDes(), tmp.getSellerNo(), tmp.getPurchaserNo(), tmp.getTaxRate());
            throw new RuntimeException("");
        }
        if ( tmp.getAmountWithoutTax().compareTo(tXfSettlementEntity.getAmountWithoutTax()) <0) {
            /**
             * tmp.getAmountWithoutTax().compareTo(tXfSettlementEntity.getAmountWithoutTax()) <0 说明被合并的单据发生了 取消或锁定,需要回撤操作
             */
            log.error("{}单 存在锁定、取消的 sellerNo: {} purchaserNo: {} taxRate:{}", xfDeductionBusinessTypeEnum.getDes(),tmp.getSellerNo(), tmp.getPurchaserNo(), tmp.getTaxRate());
            throw new RuntimeException("");
        }
    }


}
