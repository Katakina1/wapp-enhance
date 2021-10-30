package com.xforceplus.wapp.modules.job.command;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.enums.BillJobEntryObjectEnum;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.blackwhitename.service.SpeacialCompanyService;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.job.service.OriginAgreementMergeService;
import com.xforceplus.wapp.repository.dao.TXfOriginAgreementMergeDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginAgreementMergeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @program: wapp-generator
 * @description: 原始协议单过滤步骤
 * @author: Kenny Wong
 * @create: 2021-10-14 14:01
 **/
@Slf4j
@Component
public class AgreementBillFilterCommand implements Command {

    /**
     * 一次从数据库中拉取的最大行数
     */
    private static final int BATCH_COUNT = 1000;
    @Autowired
    private SpeacialCompanyService speacialCompanyService;
    @Autowired
    private DeductService deductService;
    @Autowired
    private OriginAgreementMergeService originAgreementMergeTmpService;
    @Autowired
    private TXfOriginAgreementMergeDao tXfOriginAgreementMergeDao;


    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus)) {
            log.info("开始过滤原始协议单Merge数据入业务表={}", fileName);
            int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
            try {
                process(jobId, context);
                context.put(TXfBillJobEntity.JOB_STATUS, BillJobStatusEnum.FILTER_COMPLETE.getJobStatus());
                return true;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                context.put(TXfBillJobEntity.REMARK, e.getMessage());
            }
        } else {
            log.info("跳过数据梳理步骤, 当前任务={}, 状态={}", fileName, jobStatus);
        }
        return false;
    }

    /**
     * 是否是当前步骤的前置状态
     *
     * @param jobStatus
     * @return
     */
    private boolean isValidJobStatus(int jobStatus) {
        return Objects.equals(BillJobStatusEnum.SAVE_COMPLETE.getJobStatus(), jobStatus);
    }

    /**
     * @param jobId
     * @param context
     */
    @SuppressWarnings("unchecked")
    private void process(int jobId, Context context) {
        if (Objects.isNull(context.get(TXfBillJobEntity.JOB_ENTRY_OBJECT))) {
            context.put(TXfBillJobEntity.JOB_ENTRY_OBJECT, BillJobEntryObjectEnum.BILL.getCode());
        }
        Object jobEntryProgress = context.get(TXfBillJobEntity.JOB_ENTRY_PROGRESS);
        // 上次完成页
        long last = 0;
        // 获取当前进度
        if (Objects.isNull(jobEntryProgress)) {
            context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, 0);
        } else {
            // 记录上次完成的页数，这次从last+1开始
            last = Long.parseLong(String.valueOf(jobEntryProgress));
        }
        // 先获取分页总数
        long pages;
        do {
            Page<TXfOriginAgreementMergeEntity> page = new QueryChainWrapper<>(tXfOriginAgreementMergeDao)
                    .select("reference", "sum(with_amount) as with_amount")
                    .eq(TXfOriginAgreementMergeEntity.JOB_ID, jobId)
                    .groupBy(TXfOriginAgreementMergeEntity.REFERENCE).page(new Page<>(++last, BATCH_COUNT));
            pages = page.getPages();
            filter(page.getRecords(), context, jobId);
            context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, last);
        } while (last < pages);
    }

    /**
     * 过滤转换入库
     *
     * @param list
     */
    private void filter(List<TXfOriginAgreementMergeEntity> list, Context context,Integer jobId) {
        List<DeductBillBaseData> newList = list.stream()
                .filter(mergeTmpEntity -> mergeTmpEntity.getWithAmount().compareTo(BigDecimal.ZERO) != 0)
                .filter(mergeTmpEntity -> {
                    if (Objects.isNull(mergeTmpEntity.getMemo())) {
                        return true;
                    } else {
                        // 非黑名单供应商
                        return !speacialCompanyService.hitBlackOrWhiteList("0", mergeTmpEntity.getMemo());
                    }
                })
                .map(mergeTmpEntity -> {
                    // 排除转换异常的数据
                    try {
                        QueryWrapper<TXfOriginAgreementMergeEntity> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq(TXfOriginAgreementMergeEntity.JOB_ID, jobId);
                        queryWrapper.eq(TXfOriginAgreementMergeEntity.REFERENCE, mergeTmpEntity.getReference());
                        List<TXfOriginAgreementMergeEntity> originAgreementMergeList = tXfOriginAgreementMergeDao.selectList(queryWrapper);
                        if(CollectionUtils.isNotEmpty(originAgreementMergeList)) {
                            TXfOriginAgreementMergeEntity originAgreementMergeTmp = originAgreementMergeList.get(0);
                            originAgreementMergeTmp.setWithAmount(originAgreementMergeTmp.getWithAmount());
                            BigDecimal taxAmount = originAgreementMergeTmp.getWithAmount()
                                    .divide(originAgreementMergeTmp.getTaxRate().add(BigDecimal.ONE),2, BigDecimal.ROUND_HALF_UP)
                                    .multiply(originAgreementMergeTmp.getTaxRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
                            originAgreementMergeTmp.setTaxAmount(taxAmount);
                            return convertDeductBillBaseData(originAgreementMergeTmp, context);
                        }
                        return null;
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newList)) {
            deductService.receiveData(newList, XFDeductionBusinessTypeEnum.AGREEMENT_BILL);
        }
    }

    private DeductBillBaseData convertDeductBillBaseData(TXfOriginAgreementMergeEntity mergeTmpEntity, Context context) {
        if(mergeTmpEntity == null){
            return null;
        }
        DeductBillBaseData deductBillBaseData = new DeductBillBaseData();
        deductBillBaseData.setBusinessNo(mergeTmpEntity.getReference());
        deductBillBaseData.setBusinessType(2);
        deductBillBaseData.setSellerNo(mergeTmpEntity.getCustomerNo());
        deductBillBaseData.setSellerName(mergeTmpEntity.getCustomerName());
        deductBillBaseData.setDeductDate(mergeTmpEntity.getDeductDate());
        deductBillBaseData.setPurchaserNo(mergeTmpEntity.getCompanyCode());
        deductBillBaseData.setAmountWithTax(mergeTmpEntity.getWithAmount());
        deductBillBaseData.setAmountWithoutTax(mergeTmpEntity.getWithAmount().subtract(mergeTmpEntity.getTaxAmount()));
        deductBillBaseData.setTaxRate(mergeTmpEntity.getTaxRate());
        deductBillBaseData.setTaxAmount(mergeTmpEntity.getTaxAmount());
        deductBillBaseData.setBatchNo(String.valueOf(context.get(TXfBillJobEntity.JOB_NAME)));
        return deductBillBaseData;
    }

}
