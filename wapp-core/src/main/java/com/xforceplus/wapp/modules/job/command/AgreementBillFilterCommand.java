package com.xforceplus.wapp.modules.job.command;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.converters.TXfOriginAgreementBillEntityConvertor;
import com.xforceplus.wapp.enums.BillJobEntryObjectEnum;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.blackwhitename.service.SpeacialCompanyService;
import com.xforceplus.wapp.modules.deduct.model.AgreementBillData;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.job.service.OriginAgreementMergeService;
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.dao.TXfOriginAgreementMergeDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.repository.entity.TXfBlackWhiteCompanyEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginAgreementMergeEntity;

import lombok.extern.slf4j.Slf4j;

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
    private TXfOriginAgreementMergeDao tXfOriginAgreementMergeDao;
    @Autowired
    private TXfBillJobDao tXfBillJobDao;


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
        long start = System.currentTimeMillis();
        do {
            Page<TXfOriginAgreementMergeEntity> page = new QueryChainWrapper<>(tXfOriginAgreementMergeDao)
                    //.select("reference", "sum(with_amount) as with_amount")
                    .eq(TXfOriginAgreementMergeEntity.JOB_ID, jobId)
                    //.groupBy(TXfOriginAgreementMergeEntity.REFERENCE)
                    .page(new Page<>(++last, BATCH_COUNT));
            pages = page.getPages();
            filter(page.getRecords(), context, jobId);
            context.put(TXfBillJobEntity.JOB_ENTRY_PROGRESS, last);
            //更新页码
            TXfBillJobEntity updateTXfBillJobEntity = new TXfBillJobEntity();
            updateTXfBillJobEntity.setId(jobId);
            updateTXfBillJobEntity.setJobEntryProgress(last);
            tXfBillJobDao.updateById(updateTXfBillJobEntity);
        } while (last < pages);
        log.info("协议单:{} 数据入业务表花费{}ms", jobId, System.currentTimeMillis() - start);

    }

    /**
     * 过滤转换入库
     *
     * @param list
     */
    private void filter(List<TXfOriginAgreementMergeEntity> list, Context context, Integer jobId) {
        if(CollectionUtils.isEmpty(list)){
            log.info("协议单:{} 没有数据入业务表", jobId);
            return;
        }
        List<String> memoList = list.stream().map(TXfOriginAgreementMergeEntity::getMemo).collect(Collectors.toList());
        Map<String, TXfBlackWhiteCompanyEntity> hitBlackOrWhiteBy6DMap = speacialCompanyService.hitBlackOrWhiteBy6D("0", memoList);
        List<DeductBillBaseData> newList = list.stream()
                .filter(mergeTmpEntity -> {
                    if (StringUtils.isBlank(mergeTmpEntity.getMemo())) {
                        return true;
                    }
                    //非黑名单供应商
                    TXfBlackWhiteCompanyEntity flag = hitBlackOrWhiteBy6DMap.get(mergeTmpEntity.getMemo());
                    if (Objects.nonNull(flag)) {
                        log.warn("memo:{}已配置黑名单不能入库", mergeTmpEntity.getMemo());
                        return false;
                    }
                    return true;
                })
                .map(mergeTmpEntity -> {
                    try {
                        return convertDeductBillBaseData(mergeTmpEntity, context);
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newList)) {
            long start = System.currentTimeMillis();
            deductService.receiveData(newList, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
            log.info("协议单:{} 调用创建业务单接口：{}条数据花费时间{}ms", jobId, newList.size(), System.currentTimeMillis() - start);
        }
    }

    private AgreementBillData convertDeductBillBaseData(TXfOriginAgreementMergeEntity mergeTmpEntity, Context context) {
        if (mergeTmpEntity == null) {
            return null;
        }
        AgreementBillData deductBillBaseData = new AgreementBillData();
        deductBillBaseData.setBusinessNo(mergeTmpEntity.getReference());
        deductBillBaseData.setBusinessType(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue());
        deductBillBaseData.setSellerNo(CommonUtil.fillZero(mergeTmpEntity.getMemo()));
        deductBillBaseData.setSellerName(mergeTmpEntity.getCustomerName());
        deductBillBaseData.setDeductDate(mergeTmpEntity.getDeductDate());
        //mergeTmpEntity.getCompanyCode()) 所有的都是D073  改为WI
        deductBillBaseData.setPurchaserNo("WI");
        deductBillBaseData.setAmountWithTax(mergeTmpEntity.getWithAmount());
        deductBillBaseData.setAmountWithoutTax(mergeTmpEntity.getWithAmount().subtract(mergeTmpEntity.getTaxAmount()));
        deductBillBaseData.setTaxRate(mergeTmpEntity.getTaxRate());
        deductBillBaseData.setTaxAmount(mergeTmpEntity.getTaxAmount());
        deductBillBaseData.setBatchNo(String.valueOf(context.get(TXfBillJobEntity.JOB_NAME)));
        deductBillBaseData.setDocumentType(mergeTmpEntity.getDocumentType());
        deductBillBaseData.setDocumentNo(mergeTmpEntity.getDocumentNumber());
        //sap 供应商编号
        deductBillBaseData.setMemo(mergeTmpEntity.getCustomerNo());
        deductBillBaseData.setTaxCode(mergeTmpEntity.getTaxCode());
        deductBillBaseData.setReference(mergeTmpEntity.getReference());
        if (StringUtils.isNotBlank(mergeTmpEntity.getReasonCode())) {
            String desc = TXfOriginAgreementBillEntityConvertor.REASON_CODE_MAP.get(mergeTmpEntity.getReasonCode());
            deductBillBaseData.setReferenceType(desc);
        }
        deductBillBaseData.setReasonCode(mergeTmpEntity.getReasonCode());
        deductBillBaseData.setPostingDate(mergeTmpEntity.getPostDate());
        return deductBillBaseData;
    }

}
