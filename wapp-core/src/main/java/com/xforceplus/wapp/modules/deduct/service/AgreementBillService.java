package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.common.exception.NoSuchInvoiceException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementItemFlagEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

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
        int expireScale = -5;
        /**
         * 获取超期时间 判断超过此日期的正数单据
         */
        Date referenceDate = DateUtils.addDate(DateUtils.getNow(), expireScale);
        //查询大于expireDate， 同一购销对，同一税率 总不含税金额为正的单据
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.querySuitablePositiveBill(referenceDate, deductionEnum.getValue(), tXfBillDeductStatusEnum.getCode(),TXfBillDeductStatusEnum.UNLOCK.getCode());
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
        executeMatch(deductionEnum, tXfSettlementEntity);
    }

    /**
     * 执行结算单匹配蓝票
     * @param deductionEnum
     * @param tXfSettlementEntity
     */
    public void executeMatch(XFDeductionBusinessTypeEnum deductionEnum, TXfSettlementEntity tXfSettlementEntity) {
        //匹配蓝票
        String sellerTaxNo = tXfSettlementEntity.getSellerTaxNo();
        List<BlueInvoiceService.MatchRes> matchResList = blueInvoiceService.matchInvoiceInfo(tXfSettlementEntity.getAmountWithoutTax(), XFDeductionBusinessTypeEnum.AGREEMENT_BILL, tXfSettlementEntity.getSettlementNo(),sellerTaxNo,tXfSettlementEntity.getPurchaserTaxNo());
        if (CollectionUtils.isEmpty(matchResList)) {
            log.error("{} 类型单据 销方:{}  蓝票不足，匹配失败 ", deductionEnum.getDes(), sellerTaxNo);
            throw new NoSuchInvoiceException();
        }
        //匹配税编
        Integer status = matchInfoTransfer(matchResList, tXfSettlementEntity.getSettlementNo(),tXfSettlementEntity.getId(),XFDeductionBusinessTypeEnum.AGREEMENT_BILL);
        if(status == TXfSettlementItemFlagEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode()){
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode());
        }
        if(status == TXfSettlementItemFlagEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode()){
            tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode());
        }
        //更新结算状态为- 1.存在锁定、取消的协议单、EPD进行->撤销-- 2.税编匹配失败 ->待确认税编 3,存在反算明细->待确认明细 4->结算单进入待拆票状态
        TXfBillDeductEntity checkEntity = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo(), TXfBillDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode(), TXfBillDeductStatusEnum.UNLOCK.getCode());
        if ( checkEntity.getAmountWithoutTax().compareTo(tXfSettlementEntity.getAmountWithoutTax()) < 0 ) {
            log.error("{}单 存在锁定、取消的 sellerNo: {} purchaserNo: {} taxRate:{}", deductionEnum.getDes(),tXfSettlementEntity.getSellerNo(), tXfSettlementEntity.getPurchaserNo() );
            throw new RuntimeException();
        }
        TXfSettlementEntity updadte = new TXfSettlementEntity();
        updadte.setId(tXfSettlementEntity.getId());
        updadte.setSettlementStatus(tXfSettlementEntity.getSettlementStatus());
        tXfSettlementDao.updateById(updadte);
    }

    /**
     * 手动合并结算单
     * @param ids
     * @param xfDeductionBusinessTypeEnum
     * @return
     */
    public TXfSettlementEntity mergeSettlementByManual(List<Long> ids, XFDeductionBusinessTypeEnum xfDeductionBusinessTypeEnum) {
        List<TXfBillDeductEntity> tXfBillDeductEntities = tXfBillDeductExtDao.querySuitableBillById(ids, xfDeductionBusinessTypeEnum.getValue(), TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode(), TXfBillDeductStatusEnum.UNLOCK.getCode());
        if (CollectionUtils.isEmpty(tXfBillDeductEntities)  ) {
            log.error("选择的{} 单据列表{}，查询符合条件结果为空",xfDeductionBusinessTypeEnum.getDes(),ids);
        }
        if (tXfBillDeductEntities.size() != 1) {

            log.error("选择的{} 单据列表{}，查询符合条件结果分组为{}",xfDeductionBusinessTypeEnum.getDes(),ids,tXfBillDeductEntities.size());
        }

        if (tXfBillDeductEntities.get(0).getAmountWithoutTax().compareTo(BigDecimal.ZERO) <= 0) {

            log.error("选择的{} 单据列表{}，查询结果总金额为{}",xfDeductionBusinessTypeEnum.getDes(),ids,tXfBillDeductEntities.get(0).getAmountWithoutTax());
        }
        TXfSettlementEntity tXfSettlementEntity = trans2Settlement(tXfBillDeductEntities, xfDeductionBusinessTypeEnum);
        tXfBillDeductExtDao.updateBillById(ids, tXfSettlementEntity.getSettlementNo(), xfDeductionBusinessTypeEnum.getValue(), TXfBillDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode(), TXfBillDeductStatusEnum.UNLOCK.getCode(), TXfBillDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode());
        TXfBillDeductEntity  tmp = tXfBillDeductExtDao.queryBillBySettlementNo(tXfSettlementEntity.getSettlementNo(),TXfBillDeductStatusEnum.AGREEMENT_MATCH_SETTLEMENT.getCode(), TXfBillDeductStatusEnum.UNLOCK.getCode());
        checkDeduct(tmp, tXfSettlementEntity, xfDeductionBusinessTypeEnum);
        executeMatch(xfDeductionBusinessTypeEnum, tXfSettlementEntity);
        return null;
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
        tXfBillDeductEntities.add(negativeBill);
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
