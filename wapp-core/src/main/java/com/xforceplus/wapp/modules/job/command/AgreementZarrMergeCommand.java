package com.xforceplus.wapp.modules.job.command;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.converters.TXfOriginAgreementBillEntityConvertor;
import com.xforceplus.wapp.enums.BillJobEntryObjectEnum;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.job.service.OriginAgreementMergeService;
import com.xforceplus.wapp.modules.job.service.OriginSapZarrService;
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.dao.TXfOriginSapFbl5nDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginAgreementMergeEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginSapFbl5nEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginSapZarrEntity;
import com.xforceplus.wapp.sequence.IDSequence;

import lombok.extern.slf4j.Slf4j;

/**
 * @program: wapp-generator
 * @description: 原始协议单过滤步骤
 * @author: Kenny Wong
 * @create: 2021-10-14 14:01
 **/
@Slf4j
@Component
public class AgreementZarrMergeCommand implements Command {

    /**
     * 一次从数据库中拉取的最大行数
     */
    private static final int BATCH_COUNT = 1000;
    @Autowired
    private OriginSapZarrService originSapZarrService;
    @Autowired
    private TXfOriginSapFbl5nDao tXfOriginSapFbl5nDao;
    @Autowired
    private OriginAgreementMergeService originAgreementMergeService;
    @Autowired
    private IDSequence idSequence;
    @Autowired
    private TXfBillJobDao tXfBillJobDao;

    private ExecutorService executorService = Executors.newFixedThreadPool(20);
    /**
     * 过滤fbl5n数据
     */
    private List<String> companyCodeList = Stream.of("D073").collect(Collectors.toList());
    private List<String> docTypeList = Stream.of("YC", "YD", "YR", "1C", "1D", "1R", "RV", "DA").collect(Collectors.toList());

    @Override
    public boolean execute(Context context) throws Exception {
        String fileName = String.valueOf(context.get(TXfBillJobEntity.JOB_NAME));
        int jobStatus = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.JOB_STATUS)));
        if (isValidJobStatus(jobStatus)) {
            log.info("开始过滤原始协议单zarr数据入merge表={}", fileName);
            int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
            try {
                process(jobId, context);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                context.put(TXfBillJobEntity.REMARK, e.getMessage());
            }
        } else {
            log.info("协议单zarr跳过数据梳理步骤, 当前任务={}, 状态={}", fileName, jobStatus);
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
        Object jobEntryProgress = context.get(TXfBillJobEntity.JOB_ENTRY_AGREEMENT_ZARR_PROGRESS);
        // 上次完成页
        long last = 0;
        // 获取当前进度
        if (Objects.isNull(jobEntryProgress)) {
            context.put(TXfBillJobEntity.JOB_ENTRY_AGREEMENT_ZARR_PROGRESS, 0);
        } else {
            // 记录上次完成的页数，这次从last+1开始
            last = Long.parseLong(String.valueOf(jobEntryProgress));
        }
        // 先获取分页总数
        long pages;
        long start = System.currentTimeMillis();
        do {
            Page<TXfOriginSapZarrEntity> page = originSapZarrService.page(
                    new Page<>(++last, BATCH_COUNT),
                    new QueryWrapper<TXfOriginSapZarrEntity>()
                            .lambda()
                            .eq(TXfOriginSapZarrEntity::getCheckStatus, 0)
                            .eq(TXfOriginSapZarrEntity::getJobId, jobId)
                            .orderByAsc(TXfOriginSapZarrEntity::getId)
            );
            // 总页数
            pages = page.getPages();
            filter(page.getRecords(), jobId);
            //更新页码
            TXfBillJobEntity updateTXfBillJobEntity = new TXfBillJobEntity();
            updateTXfBillJobEntity.setId(jobId);
            updateTXfBillJobEntity.setJobEntryAgreementZarrProgress(last);
            tXfBillJobDao.updateById(updateTXfBillJobEntity);
        } while (last < pages);
        log.info("协议单:{} zarr数据入merge表花费{}ms", jobId, System.currentTimeMillis() - start);

    }

    private void filter(List<TXfOriginSapZarrEntity> list, Integer jobId) {
        long start = System.currentTimeMillis();
        List<Future<TXfOriginAgreementMergeEntity>> featureList = new ArrayList<>();
        list.forEach(zarr -> {
            Future<TXfOriginAgreementMergeEntity> future = executorService.submit(() -> convertTXfOriginAgreementMergeEntity(zarr));
            featureList.add(future);
        });
        List<TXfOriginAgreementMergeEntity> newList = featureList.stream().map(f -> {
            try {
                return f.get();
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
            return null;
        }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newList)) {
            originAgreementMergeService.saveBatch(newList);
            log.info("协议单:{} zarr原始数据{}条转换后{}条花费{}ms", jobId, list.size(), newList.size(), System.currentTimeMillis() - start);
        }
    }

    private TXfOriginAgreementMergeEntity convertTXfOriginAgreementMergeEntity(TXfOriginSapZarrEntity zarr) {
        try {
            TXfOriginAgreementMergeEntity tXfOriginAgreementMergeTmpEntity = new TXfOriginAgreementMergeEntity();
            tXfOriginAgreementMergeTmpEntity.setId(idSequence.nextId());
            tXfOriginAgreementMergeTmpEntity.setJobId(zarr.getJobId());
            tXfOriginAgreementMergeTmpEntity.setCustomerNo(zarr.getCustomerNumber());
            tXfOriginAgreementMergeTmpEntity.setCustomerName(zarr.getCustomer());
            if (StringUtils.isNotBlank(zarr.getMemo())) {
                String memo = zarr.getMemo().replace("V#", "").substring(0, 6);
                tXfOriginAgreementMergeTmpEntity.setMemo(memo);
                SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd");
                String postDate = zarr.getMemo().substring(zarr.getMemo().length() - 10);
                if (StringUtils.isNotBlank(postDate)) {
                    tXfOriginAgreementMergeTmpEntity.setPostDate(fmt2.parse(postDate));
                }
            }
            if (StringUtils.isNotBlank(zarr.getInternalInvoiceNo())) {
                String companyCode = zarr.getInternalInvoiceNo().substring(0, 4);
                tXfOriginAgreementMergeTmpEntity.setCompanyCode(companyCode);
            }
            if (StringUtils.isNotBlank(zarr.getAmountWithTax())) {
                String amountWithTax = zarr.getAmountWithTax().replace(",", "");
                tXfOriginAgreementMergeTmpEntity.setWithAmount(new BigDecimal(amountWithTax));
            }
            if (StringUtils.isNotBlank(zarr.getReasonCode())) {
                tXfOriginAgreementMergeTmpEntity.setReasonCode(zarr.getReasonCode().replace(" ", ""));
            }
            if (StringUtils.isNotBlank(zarr.getContents())) {
                String reference = zarr.getContents().substring(zarr.getContents().length() - 10);
                tXfOriginAgreementMergeTmpEntity.setReference(reference);
                tXfOriginAgreementMergeTmpEntity.setTaxCode(getTaxCode(zarr.getJobId(), reference, zarr.getSapAccountingDocument()));
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
                String deductDate = getDeductDate(zarr.getJobId(), reference, zarr.getSapAccountingDocument());
                if (StringUtils.isNotBlank(deductDate)) {
                    tXfOriginAgreementMergeTmpEntity.setDeductDate(fmt.parse(deductDate));
                }
                tXfOriginAgreementMergeTmpEntity.setDocumentType(getDocumentType(zarr.getJobId(), reference, zarr.getSapAccountingDocument()));
            }
            BigDecimal taxRate = Optional.ofNullable(TXfOriginAgreementBillEntityConvertor.TAX_CODE_TRANSLATOR.get(tXfOriginAgreementMergeTmpEntity.getTaxCode())).orElse(BigDecimal.ZERO);
            if (taxRate != null) {
                tXfOriginAgreementMergeTmpEntity.setTaxRate(taxRate);
            }
            tXfOriginAgreementMergeTmpEntity.setDocumentNumber(zarr.getSapAccountingDocument());
            if (tXfOriginAgreementMergeTmpEntity.getWithAmount() != null &&
                    tXfOriginAgreementMergeTmpEntity.getTaxRate() != null) {
                BigDecimal taxAmount = tXfOriginAgreementMergeTmpEntity.getWithAmount()
                        .divide(tXfOriginAgreementMergeTmpEntity.getTaxRate().add(BigDecimal.ONE), 6, BigDecimal.ROUND_HALF_UP)
                        .multiply(tXfOriginAgreementMergeTmpEntity.getTaxRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
                tXfOriginAgreementMergeTmpEntity.setTaxAmount(taxAmount);
            }
            tXfOriginAgreementMergeTmpEntity.setSource(2);
            tXfOriginAgreementMergeTmpEntity.setCreateTime(new Date());
            tXfOriginAgreementMergeTmpEntity.setUpdateTime(new Date());
            return tXfOriginAgreementMergeTmpEntity;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    private String getTaxCode(Integer jobId, String reference,String documentNumber) {
        QueryWrapper<TXfOriginSapFbl5nEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapFbl5nEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginSapFbl5nEntity.REFERENCE, reference);
        queryWrapper.in(TXfOriginSapFbl5nEntity.COMPANY_CODE, companyCodeList);
        queryWrapper.in(TXfOriginSapFbl5nEntity.DOCUMENT_TYPE, docTypeList);
        queryWrapper.eq(TXfOriginSapFbl5nEntity.DOCUMENT_NUMBER, documentNumber);
        queryWrapper.ne(TXfOriginSapFbl5nEntity.TAX_CODE, "");
        queryWrapper.isNotNull(TXfOriginSapFbl5nEntity.TAX_CODE);
        Page<TXfOriginSapFbl5nEntity> page = tXfOriginSapFbl5nDao.selectPage(new Page<>(1, 1), queryWrapper);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            TXfOriginSapFbl5nEntity fbl5n = page.getRecords().get(0);
            return fbl5n.getTaxCode();
        }
        return null;
    }

    private String getDeductDate(Integer jobId, String reference,String documentNumber) {
        QueryWrapper<TXfOriginSapFbl5nEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapFbl5nEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginSapFbl5nEntity.REFERENCE, reference);
        queryWrapper.in(TXfOriginSapFbl5nEntity.COMPANY_CODE, companyCodeList);
        queryWrapper.in(TXfOriginSapFbl5nEntity.DOCUMENT_TYPE, docTypeList);
        queryWrapper.eq(TXfOriginSapFbl5nEntity.DOCUMENT_NUMBER, documentNumber);
        queryWrapper.ne(TXfOriginSapFbl5nEntity.CLEARING_DATE, "");
        queryWrapper.isNotNull(TXfOriginSapFbl5nEntity.CLEARING_DATE);
        Page<TXfOriginSapFbl5nEntity> page = tXfOriginSapFbl5nDao.selectPage(new Page<>(1, 1), queryWrapper);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            TXfOriginSapFbl5nEntity fbl5n = page.getRecords().get(0);
            return fbl5n.getClearingDate();
        }
        return null;
    }

    private String getDocumentType(Integer jobId, String reference,String documentNumber) {
        QueryWrapper<TXfOriginSapFbl5nEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapFbl5nEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginSapFbl5nEntity.REFERENCE, reference);
        queryWrapper.in(TXfOriginSapFbl5nEntity.COMPANY_CODE, companyCodeList);
        queryWrapper.in(TXfOriginSapFbl5nEntity.DOCUMENT_TYPE, docTypeList);
        queryWrapper.eq(TXfOriginSapFbl5nEntity.DOCUMENT_NUMBER, documentNumber);
        queryWrapper.ne(TXfOriginSapFbl5nEntity.DOCUMENT_TYPE, "");
        queryWrapper.isNotNull(TXfOriginSapFbl5nEntity.DOCUMENT_TYPE);
        Page<TXfOriginSapFbl5nEntity> page = tXfOriginSapFbl5nDao.selectPage(new Page<>(1, 1), queryWrapper);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            TXfOriginSapFbl5nEntity fbl5n = page.getRecords().get(0);
            return fbl5n.getDocumentType();
        }
        return null;
    }

}
