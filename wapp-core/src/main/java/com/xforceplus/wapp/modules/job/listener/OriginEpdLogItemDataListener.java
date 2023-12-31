package com.xforceplus.wapp.modules.job.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xforceplus.wapp.modules.job.dto.OriginEpdLogItemDto;
import com.xforceplus.wapp.modules.job.service.OriginEpdLogItemService;
import com.xforceplus.wapp.repository.entity.TXfOriginEpdBillEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginEpdLogItemEntity;
import jodd.util.StringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
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
public class OriginEpdLogItemDataListener extends AnalysisEventListener<OriginEpdLogItemDto> {
    /**
     * 每隔1000条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;
    private final int jobId;
    private final OriginEpdLogItemService service;
    private final Validator validator;
    /**
     * 缓存的数据
     */
    private List<OriginEpdLogItemDto> list = new ArrayList<>();
    @Getter
    private long cursor;

    public OriginEpdLogItemDataListener(int jobId, long cursor, OriginEpdLogItemService service, Validator validator) {
        this.jobId = jobId;
        this.cursor = cursor;
        this.service = service;
        this.validator = validator;
    }

    @Override
    public void invoke(OriginEpdLogItemDto data, AnalysisContext context) {
        Set<ConstraintViolation<OriginEpdLogItemDto>> violations = validator.validate(data);
        if (CollectionUtils.isEmpty(violations)) {
            list.add(data);
            if (list.size() >= BATCH_COUNT) {
                saveData();
                // 存储完成清理 list
                list = new ArrayList<>();
            }
        } else {
            log.warn("EPD单LOG明细原始数据校验失败 jobId={} 错误原因={}", jobId, violations.stream().findAny().orElse(null));
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
        List<TXfOriginEpdLogItemEntity> entities = new ArrayList<>(list.size());
        Date now = new Date();
        list.forEach(
                v1 -> {
                    TXfOriginEpdLogItemEntity v2 = new TXfOriginEpdLogItemEntity();
                    BeanUtils.copyProperties(v1, v2);
                    v2.setJobId(jobId);
                    v2.setCreateTime(now);
                    v2.setUpdateTime(now);
                    v2.setTaxRate(v2.getTaxRate().replace(",",""));
                    check(v2);
                    entities.add(v2);
                }
        );
        service.saveBatch(entities);
        cursor += list.size();
        // cursor - 1 排除表头行
        log.info("jobId={}, 已入库{}条原始EPD单LOG明细数据！本次{}条花费{}ms", jobId, cursor - 1,list.size(),System.currentTimeMillis()-start);
    }

    private void check(TXfOriginEpdLogItemEntity entity){
        String checkRemark = "";
        if(StringUtil.isBlank(entity.getDocType())){
            checkRemark += "Doc. Type为空;";
        }
        if(StringUtil.isBlank(entity.getReference())){
            checkRemark += "Reference为空;";
        }
        if(StringUtil.isBlank(entity.getVendor())){
            checkRemark += "Vendor为空;";
        }
        if(StringUtil.isBlank(entity.getStatusMessage())){
            checkRemark += "Status Message为空;";
        }
        if(StringUtil.isBlank(entity.getTaxRate())){
            checkRemark += "Tax Rate为空;";
        }else {
            try {
                new BigDecimal(entity.getTaxRate().replace(",",""));
            } catch (Exception e) {
                checkRemark += "Tax Rate格式错误;";
            }
        }
        if(StringUtil.isNotBlank(checkRemark)){
            entity.setCheckRemark(checkRemark);
            entity.setCheckStatus(1);
        }
    }
}
