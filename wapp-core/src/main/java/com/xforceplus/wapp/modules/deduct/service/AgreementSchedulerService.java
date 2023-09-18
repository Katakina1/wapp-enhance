package com.xforceplus.wapp.modules.deduct.service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.common.exception.NoSuchInvoiceException;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.deduct.model.AgreementMergeData;
import com.xforceplus.wapp.modules.exceptionreport.event.NewExceptionReportEvent;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.syslog.util.SysLogUtil;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.util.CodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
public class AgreementSchedulerService extends DeductService{
    @Autowired
    private DeductBlueInvoiceService deductBlueInvoiceService;
    @Autowired
    private AgreementBillService agreementBillService;
    @Autowired
    private PreinvoiceService preinvoiceService;


    /**
     * 超期协议单合并结算单(仅供定时任务调用)
     * 主流程步骤：
     * 1.获取正数协议单列表
     * 2.对正数协议单按 购销对+税率 合并汇总
     * 3.根据合并信息生成结算单
     * @return
     */
    public boolean makeSettlementByScheduler() {
        // 1.获取正数协议单列表
        //1.1 获取超期天数
        Integer overdueDayNum =  defaultSettingService.getOverdueDay(DefaultSettingEnum.AGREEMENT_OVERDUE_DEFAULT_DAY);
        //记录系统日志
		SysLogUtil.sendLog(TXfSysLogModuleEnum.AGREEMENT_TO_SETTLEMENT, SysLogUtil.getTraceInfo(), "", "Y", "overdueDayNum:" + overdueDayNum);
        //1.2 获取超期业务单列表
        List<TXfBillDeductEntity> plusDeductList = tXfBillDeductExtDao.querySuitablePositiveBillList(overdueDayNum
                ,TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue()
                , TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode()
                , TXfDeductStatusEnum.UNLOCK.getCode());
        List<String> businessNos = plusDeductList.stream().map(TXfBillDeductEntity::getBusinessNo).collect(Collectors.toList());
        log.info("超期协议单合并结算单Scheduler本次执行数量:{},业务单号:{}",plusDeductList.size(),businessNos);
        if (CollectionUtils.isEmpty(plusDeductList)) {
            //记录系统日志
            SysLogUtil.sendLog(TXfSysLogModuleEnum.AGREEMENT_TO_SETTLEMENT,SysLogUtil.getTraceInfo()
                    ,"","N","未找到符合条件的超期协议单据，跳过合并单据");
            log.info("未找到符合条件的超期协议单据，跳过合并单据");
            return false;
        }
        // 2.对正数协议单按 购销对+税率 合并汇总
        Map<String,AgreementMergeData> mergeDataMap = new HashMap<>();//key-购销对+税率   value-合并对象（含明细）
        //megerKey为销方税号+购方税号+税率
        String megerKey;
        //协议单合并对象
        AgreementMergeData mergeData;
        // 把购销方信息和税率一致的业务单金额进行累加，如果是13、9 税率的业务单也要做一个区分
        for (TXfBillDeductEntity plusDeduct : plusDeductList){
            megerKey = new StringBuilder(plusDeduct.getSellerNo())
                    .append(plusDeduct.getPurchaserNo())
                    .append(plusDeduct.getTaxRate().toPlainString()).toString();
            if ((new BigDecimal("0.13").equals(plusDeduct.getTaxRate().setScale(2, RoundingMode.DOWN)) || new BigDecimal("0.09").equals(plusDeduct.getTaxRate().setScale(2, RoundingMode.DOWN)))){
				megerKey += "_" + plusDeduct.getAgreementTaxCode();
            }
            mergeData = mergeDataMap.get(megerKey);
            if (mergeData == null){
                mergeData = new AgreementMergeData(plusDeduct.getPurchaserNo(), plusDeduct.getSellerNo(),plusDeduct.getTaxRate());
                //生成结算单号
				mergeData.setSettlementNo(CodeGenerator.generateCode(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL));
				mergeData.setTargetTaxRate(AgreementBillService.switchToTargetTaxRate(plusDeduct.getTaxRate(), true,
						plusDeduct.getAgreementTaxCode()));
				mergeData.setTaxCode(plusDeduct.getAgreementTaxCode());
				mergeDataMap.put(megerKey, mergeData);
            }
            //执行合并
            mergeData.addAmountWithoutTax(plusDeduct.getAmountWithoutTax());
            mergeData.addAmountWithTax(plusDeduct.getAmountWithTax());
            mergeData.addTaxAmount(plusDeduct.getTaxAmount());
            mergeData.addPlusDeduct(plusDeduct);
        }
        //记录系统日志
        SysLogUtil.sendLog(TXfSysLogModuleEnum.AGREEMENT_TO_SETTLEMENT,SysLogUtil.getTraceInfo(),"","Y","对正数协议单按 购销对+税率 合并汇总完成");
        // 3.根据合并信息生成结算单
        Iterator<Map.Entry<String,AgreementMergeData>> entryIterator = mergeDataMap.entrySet().iterator();
        while (entryIterator.hasNext()){
            Map.Entry<String,AgreementMergeData> mergeDataEntry = entryIterator.next();
            log.info("mergeDataEntry:{}",JSON.toJSON(mergeDataEntry));
            mergeData = mergeDataEntry.getValue();
            log.info("协议单合并mergeKey:{}",mergeDataEntry.getKey());
            try {
                //获取对应负数协议业务单列表,如存在则合并，并验证合并后金额必须为正数
                agreementBillService.makeMergeDataOrgAndNegative(mergeData);
                mergeData.exchangeAmount();
                //如果税额差异高于5分钱，则需进行组合拆分
                if (AgreementBillService.MAX_DIFF_TAX_AMOUNT.compareTo(mergeData.getDiffTaxAmount().abs())<0) {
                    List<AgreementMergeData> splitMergeDataList = agreementBillService.splitMergeForTaxAmountDiff(mergeData);
                    for (AgreementMergeData splitMergeData : splitMergeDataList){
                        //合并结算单
                        TXfSettlementEntity settlementEntity = agreementBillService.makeSettlement(splitMergeData);
                        log.info("协议单组合生成结算单结果1：{}", JSON.toJSONString(settlementEntity));
                        //记录履历
                        if(CollectionUtils.isNotEmpty(splitMergeData.getMergeDeductList())){
                            splitMergeData.getMergeDeductList().forEach(entity -> operateLogService.addDeductLog(entity.getId(),
                                    entity.getBusinessType(), TXfDeductStatusEnum.getEnumByCode(entity.getStatus()),
                                    entity.getRefSettlementNo(), OperateLogEnum.AGREEMENT_MATCH_BLUE_INVOICE_FAILED,
                                    "组合生成结算单", 0L, "系统"));
                        }
                        settlementConfirmAndSplit(settlementEntity);
                    }
                }else {
                    //合并结算单
                    TXfSettlementEntity settlementEntity = agreementBillService.makeSettlement(mergeData);
                    log.info("协议单组合生成结算单结果2：{}", JSON.toJSONString(settlementEntity));
                    //记录履历
                    if(CollectionUtils.isNotEmpty(mergeData.getMergeDeductList())){
                        mergeData.getMergeDeductList().forEach(entity -> operateLogService.addDeductLog(entity.getId(),
                                entity.getBusinessType(), TXfDeductStatusEnum.getEnumByCode(entity.getStatus()),
                                entity.getRefSettlementNo(), OperateLogEnum.AGREEMENT_MATCH_BLUE_INVOICE_FAILED,
                                "组合生成结算单", 0L, "系统"));
                    }
                    settlementConfirmAndSplit(settlementEntity);
                }
            } catch (NoSuchInvoiceException e) {
                log.info("协议单据匹配合并失败销方蓝票不足->sellerNo:{} purcharseNo:{}"
                        ,mergeData.getSellerNo(),mergeData.getPurchaserNo(),e);
                //记录系统日志
                SysLogUtil.sendLog(TXfSysLogModuleEnum.AGREEMENT_TO_SETTLEMENT,SysLogUtil.getTraceInfo()
                        ,"","N","协议单据匹配合并失败销方蓝票不足->sellerNo:{"
                                +mergeData.getSellerNo()+"} purcharseNo:{"+mergeData.getPurchaserNo()+"}");
                //记录履历
                if(CollectionUtils.isNotEmpty(mergeData.getMergeDeductList())){
                    mergeData.getMergeDeductList().forEach(entity -> operateLogService.addDeductLog(entity.getId(),
                            entity.getBusinessType(), TXfDeductStatusEnum.getEnumByCode(entity.getStatus()),
                            entity.getRefSettlementNo(), OperateLogEnum.AGREEMENT_MATCH_BLUE_INVOICE_FAILED,
                            "匹配合并失败销方蓝票不足", 0L, "系统"));
                }
                NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
                TXfBillDeductEntity deductEntity =  mergeData.getPlusDeductList().get(0);
                newExceptionReportEvent.setDeduct(deductEntity);
                newExceptionReportEvent.setReportCode( ExceptionReportCodeEnum.NOT_MATCH_BLUE_INVOICE );
                newExceptionReportEvent.setType(ExceptionReportTypeEnum.AGREEMENT);
                applicationContext.publishEvent(newExceptionReportEvent);
                //回滚发票占用
                if (CollectionUtils.isNotEmpty(mergeData.getAllMatchResList())) {
                    List<Long> deductIdList = mergeData.getMergeDeductList().stream()
                            .map(TXfBillDeductEntity::getId).collect(Collectors.toList());
                    deductBlueInvoiceService.withdrawBlueInvoiceByDeduct(deductIdList);
                }
            } catch (Exception e) {
                log.error("协议单合并异常 sellerNo:{} purcharseNo:{} taxRate:{} "
                        , mergeData.getSellerNo(),mergeData.getPurchaserNo(), mergeData.getTaxRate(), e);
                //记录履历
                if(CollectionUtils.isNotEmpty(mergeData.getMergeDeductList())){
                    mergeData.getMergeDeductList().forEach(entity -> operateLogService.addDeductLog(entity.getId(),
                            entity.getBusinessType(), TXfDeductStatusEnum.getEnumByCode(entity.getStatus()),
                            entity.getRefSettlementNo(), OperateLogEnum.AGREEMENT_MATCH_BLUE_INVOICE_FAILED,
                            "合并异常", 0L, "系统"));
                }
                //回滚发票占用
                if (CollectionUtils.isNotEmpty(mergeData.getAllMatchResList())) {
                    List<Long> deductIdList = mergeData.getMergeDeductList().stream()
                            .map(TXfBillDeductEntity::getId).collect(Collectors.toList());
                    deductBlueInvoiceService.withdrawBlueInvoiceByDeduct(deductIdList);
                }
            }
        }

        return true;
    }

    /**
     * 自动确认和拆票
     * @param settlementEntity
     */
    private void settlementConfirmAndSplit(TXfSettlementEntity settlementEntity){
        CompletableFuture.runAsync(() -> {
            try {
                log.info("协议结算单调用拆票方法参数,settlementNo:{},sellerNo:{}"
                        , settlementEntity.getSettlementNo(), settlementEntity.getSellerNo());
                long pStart = System.currentTimeMillis();
                preinvoiceService.splitPreInvoice(settlementEntity, true);
                log.info("协议结算单调用拆票方法耗时:{}", System.currentTimeMillis() - pStart);
            } catch (Exception e) {
                log.error("协议结算单拆票方法异常,", e);
            }
        });
    }

}
