package com.xforceplus.wapp.modules.job.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xforceplus.wapp.modules.job.dto.OriginEpdBillDto;
import com.xforceplus.wapp.modules.job.service.OriginEpdBillService;
import com.xforceplus.wapp.repository.entity.TXfOriginEpdBillEntity;
import jodd.util.StringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @program: wapp-generator
 * @description: 使用map接收excel数据
 * @author: Kenny Wong
 * @create: 2021-10-15 14:28
 **/
@Slf4j
public class OriginEpdBillDataListener extends AnalysisEventListener<OriginEpdBillDto> {
    /**
     * 每隔1000条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;
    private final int jobId;
    private final OriginEpdBillService service;
    private final Validator validator;
    /**
     * 缓存的数据
     */
    private List<OriginEpdBillDto> list = new ArrayList<>();
    @Getter
    private long cursor;

    public OriginEpdBillDataListener(int jobId, long cursor, OriginEpdBillService service, Validator validator) {
        this.jobId = jobId;
        this.cursor = cursor;
        this.service = service;
        this.validator = validator;
    }

    @Override
    public void invoke(OriginEpdBillDto data, AnalysisContext context) {
        Set<ConstraintViolation<OriginEpdBillDto>> violations = validator.validate(data);
        if (CollectionUtils.isEmpty(violations)) {
            list.add(data);
            if (list.size() >= BATCH_COUNT) {
                saveData();
                // 存储完成清理 list
                list = new ArrayList<>();
            }
        } else {
            log.warn("EPD单原始数据校验失败 jobId={} 错误原因={}", jobId, violations.stream().findAny().orElse(null));
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        // 出错仍然继续
        log.warn(exception.getMessage(), exception);
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        long start  = System.currentTimeMillis();
        List<TXfOriginEpdBillEntity> entities = new ArrayList<>(list.size());
        Date now = new Date();
        list.forEach(
                v1 -> {
                    TXfOriginEpdBillEntity v2 = new TXfOriginEpdBillEntity();
                    BeanUtils.copyProperties(v1, v2);
                    v2.setJobId(jobId);
                    v2.setCreateTime(now);
                    v2.setUpdateTime(now);
                    v2.setAmountInLocalCurrency(v2.getAmountInLocalCurrency().replace(",",""));
                    check(v2);
                    entities.add(v2);

                }
        );
        service.saveBatch(entities);
        cursor += list.size();
        // cursor - 1 排除表头行
        log.info("jobId={}, 已入库{}条原始Epd单数据！本次{}条花费{}ms", jobId, cursor - 1,list.size(),System.currentTimeMillis()-start);
    }

    private void check(TXfOriginEpdBillEntity entity){
        String checkRemark = "";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        if(StringUtil.isBlank(entity.getAccount())){
            checkRemark += "Account为空;";
        }
        if(StringUtil.isBlank(entity.getReference())){
            checkRemark += "Reference为空;";
        }
        if(StringUtil.isBlank(entity.getAmountInLocalCurrency())){
            checkRemark += "Amount in local currency为空;";
        }else{
            try {
                new BigDecimal(entity.getAmountInLocalCurrency().replace(",",""));
            } catch (Exception e) {
                checkRemark += "Amount in local currency格式错误;";
            }
        }
        if(StringUtil.isBlank(entity.getDocumentType())){
            checkRemark += "Document Type为空;";
        }
        if (StringUtil.isBlank(entity.getClearingDate())) {
            checkRemark += "Clearing date为空;";
        } else {
            try {
                fmt.parse(entity.getClearingDate());
            } catch (Exception e) {
                checkRemark += "Clearing date格式错误;";
            }
        }

        if (StringUtil.isBlank(entity.getPostingDate())) {
            checkRemark += "Posting Date为空;";
        } else {
            try {
                fmt.parse(entity.getPostingDate());
            } catch (Exception e) {
                checkRemark += "Posting Date格式错误;";
            }
        }
        if(StringUtil.isNotBlank(checkRemark)){
            entity.setCheckRemark(checkRemark);
            entity.setCheckStatus(1);
        }
    }
}
