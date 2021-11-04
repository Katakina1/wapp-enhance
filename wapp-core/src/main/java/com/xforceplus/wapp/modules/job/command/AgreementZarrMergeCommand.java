package com.xforceplus.wapp.modules.job.command;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.converters.TXfOriginAgreementBillEntityConvertor;
import com.xforceplus.wapp.enums.BillJobEntryObjectEnum;
import com.xforceplus.wapp.enums.BillJobStatusEnum;
import com.xforceplus.wapp.modules.blackwhitename.service.SpeacialCompanyService;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.job.service.OriginAgreementMergeService;
import com.xforceplus.wapp.modules.job.service.OriginSapFbl5nService;
import com.xforceplus.wapp.modules.job.service.OriginSapZarrService;
import com.xforceplus.wapp.repository.dao.TXfBillJobDao;
import com.xforceplus.wapp.repository.dao.TXfOriginAgreementMergeDao;
import com.xforceplus.wapp.repository.dao.TXfOriginSapFbl5nDao;
import com.xforceplus.wapp.repository.dao.TXfOriginSapZarrDao;
import com.xforceplus.wapp.repository.entity.TXfBillJobEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginAgreementMergeEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginSapFbl5nEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginSapZarrEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private SpeacialCompanyService speacialCompanyService;
    @Autowired
    private DeductService deductService;
    @Autowired
    private OriginSapFbl5nService originSapFbl5nService;
    @Autowired
    private OriginSapZarrService originSapZarrService;
    @Autowired
    private TXfOriginSapFbl5nDao tXfOriginSapFbl5nDao;
    @Autowired
    private TXfOriginSapZarrDao tXfOriginSapZarrDao;
    @Autowired
    private TXfOriginAgreementMergeDao tXfOriginAgreementMergeDao;
    @Autowired
    private OriginAgreementMergeService originAgreementMergeService;
    @Autowired
    private IDSequence idSequence;
    @Autowired
    private TXfBillJobDao tXfBillJobDao;
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
            log.info("开始过滤原始协议单ZARR数据入业务表={}", fileName);
            int jobId = Integer.parseInt(String.valueOf(context.get(TXfBillJobEntity.ID)));
            try {
                process(jobId, context);
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
        Object jobEntryProgress = context.get(TXfBillJobEntity.JOB_ENTRY_ZARR0355_PROGRESS);
        // 上次完成页
        long last = 0;
        // 获取当前进度
        if (Objects.isNull(jobEntryProgress)) {
            context.put(TXfBillJobEntity.JOB_ENTRY_ZARR0355_PROGRESS, 0);
        } else {
            // 记录上次完成的页数，这次从last+1开始
            last = Long.parseLong(String.valueOf(jobEntryProgress));
        }
//        if (last == 0) {
//            //如果第一页需要特殊处理把数据此次job数据清理一下（可能之前执行到一半重启，数据已经完成一半但是last没有更新）
//            deleteOriginAgreementMerge(jobId);
//        }
        // 先获取分页总数
        long pages;
        do {
            Page<TXfOriginSapZarrEntity> page = originSapZarrService.page(
                    new Page<>(++last, BATCH_COUNT),
                    new QueryWrapper<TXfOriginSapZarrEntity>()
                            .lambda()
                            .eq(TXfOriginSapZarrEntity::getJobId, jobId)
                            .orderByAsc(TXfOriginSapZarrEntity::getId)
            );
            // 总页数
            pages = page.getPages();
            filter(page.getRecords());
            context.put(TXfBillJobEntity.JOB_ENTRY_ZARR0355_PROGRESS, last);
            //更新页码
            TXfBillJobEntity updateTXfBillJobEntity = new TXfBillJobEntity();
            updateTXfBillJobEntity.setId(jobId);
            updateTXfBillJobEntity.setJobEntryZarr0355Progress(last);
            tXfBillJobDao.updateById(updateTXfBillJobEntity);
        } while (last < pages);
    }

    private void filter(List<TXfOriginSapZarrEntity> list) {
        List<TXfOriginAgreementMergeEntity> newList = list.parallelStream()
                .map(zarr -> {
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
                            String postDate = zarr.getMemo().substring(zarr.getMemo().length() - 10, zarr.getMemo().length());
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
                            String reference = zarr.getContents().substring(zarr.getContents().length() - 10, zarr.getContents().length());
                            tXfOriginAgreementMergeTmpEntity.setReference(reference);
                            tXfOriginAgreementMergeTmpEntity.setTaxCode(getTaxCode(zarr.getJobId(), reference));
                            SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
                            String deductDate = getDeductDate(zarr.getJobId(), reference);
                            if (StringUtils.isNotBlank(deductDate)) {
                                tXfOriginAgreementMergeTmpEntity.setDeductDate(fmt.parse(deductDate));
                            }
                            tXfOriginAgreementMergeTmpEntity.setDocumentType(getDocumentType(zarr.getJobId(), reference));
                        }
                        BigDecimal taxRate = TXfOriginAgreementBillEntityConvertor.TAX_CODE_TRANSLATOR.get(tXfOriginAgreementMergeTmpEntity.getTaxCode());
                        if (taxRate != null) {
                            tXfOriginAgreementMergeTmpEntity.setTaxRate(taxRate);
                        }
                        tXfOriginAgreementMergeTmpEntity.setDocumentNumber(zarr.getSapAccountingDocument());
                        if (tXfOriginAgreementMergeTmpEntity.getWithAmount() != null &&
                                tXfOriginAgreementMergeTmpEntity.getTaxRate() != null) {
                            BigDecimal taxAmount = tXfOriginAgreementMergeTmpEntity.getWithAmount()
                                    .divide(tXfOriginAgreementMergeTmpEntity.getTaxRate().add(BigDecimal.ONE), 2, BigDecimal.ROUND_HALF_UP)
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
                })
                .filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newList)) {
            originAgreementMergeService.saveBatch(newList);
        }
    }

    private String getTaxCode(Integer jobId, String reference) {
        QueryWrapper<TXfOriginSapFbl5nEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapFbl5nEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginSapFbl5nEntity.REFERENCE, reference);
        queryWrapper.in(TXfOriginSapFbl5nEntity.COMPANY_CODE, companyCodeList);
        queryWrapper.in(TXfOriginSapFbl5nEntity.DOCUMENT_TYPE, docTypeList);
        List<TXfOriginSapFbl5nEntity> list = tXfOriginSapFbl5nDao.selectList(queryWrapper);
        TXfOriginSapFbl5nEntity tXfOriginSapFbl5nEntity = list.stream().filter(fbl5n -> StringUtils.isNotBlank(fbl5n.getReasonCode())).findAny().orElse(null);
        if (tXfOriginSapFbl5nEntity != null) {
            return tXfOriginSapFbl5nEntity.getTaxCode();
        }
        return null;
    }

    private String getDeductDate(Integer jobId, String reference) {
        QueryWrapper<TXfOriginSapFbl5nEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapFbl5nEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginSapFbl5nEntity.REFERENCE, reference);
        queryWrapper.in(TXfOriginSapFbl5nEntity.COMPANY_CODE, companyCodeList);
        queryWrapper.in(TXfOriginSapFbl5nEntity.DOCUMENT_TYPE, docTypeList);
        List<TXfOriginSapFbl5nEntity> list = tXfOriginSapFbl5nDao.selectList(queryWrapper);
        TXfOriginSapFbl5nEntity tXfOriginSapFbl5nEntity = list.stream().filter(fbl5n -> StringUtils.isNotBlank(fbl5n.getClearingDate())).findAny().orElse(null);
        if (tXfOriginSapFbl5nEntity != null) {
            return tXfOriginSapFbl5nEntity.getClearingDate();
        }
        return null;
    }

    private String getDocumentType(Integer jobId, String reference) {
        QueryWrapper<TXfOriginSapFbl5nEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginSapFbl5nEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginSapFbl5nEntity.REFERENCE, reference);
        queryWrapper.in(TXfOriginSapFbl5nEntity.COMPANY_CODE, companyCodeList);
        queryWrapper.in(TXfOriginSapFbl5nEntity.DOCUMENT_TYPE, docTypeList);
        List<TXfOriginSapFbl5nEntity> list = tXfOriginSapFbl5nDao.selectList(queryWrapper);
        TXfOriginSapFbl5nEntity tXfOriginSapFbl5nEntity = list.stream().filter(fbl5n -> StringUtils.isNotBlank(fbl5n.getDocumentType())).findAny().orElse(null);
        if (tXfOriginSapFbl5nEntity != null) {
            return tXfOriginSapFbl5nEntity.getDocumentType();
        }
        return null;
    }

    private void deleteOriginAgreementMerge(Integer jobId) {
        QueryWrapper<TXfOriginAgreementMergeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfOriginAgreementMergeEntity.JOB_ID, jobId);
        queryWrapper.eq(TXfOriginAgreementMergeEntity.SOURCE, 2);
        tXfOriginAgreementMergeDao.delete(queryWrapper);
    }

}
