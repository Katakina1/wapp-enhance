package com.xforceplus.wapp.modules.job.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xforceplus.wapp.modules.job.dto.OriginAgreementBillZarrDto;
import com.xforceplus.wapp.modules.job.service.OriginSapZarrService;
import com.xforceplus.wapp.repository.entity.TXfOriginSapZarrEntity;
import jodd.util.StringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
public class OriginAgreementBillZarrDataListener extends AnalysisEventListener<OriginAgreementBillZarrDto> {
    /**
     * 每隔1000条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;
    private final int jobId;
    private final OriginSapZarrService service;
    private final Validator validator;
    /**
     * 缓存的数据
     */
    private List<OriginAgreementBillZarrDto> list = new ArrayList<>();
    @Getter
    private long cursor;

    public OriginAgreementBillZarrDataListener(int jobId, long cursor, OriginSapZarrService service, Validator validator) {
        this.jobId = jobId;
        this.cursor = cursor;
        this.service = service;
        this.validator = validator;
    }

    @Override
    public void invoke(OriginAgreementBillZarrDto data, AnalysisContext context) {
        Set<ConstraintViolation<OriginAgreementBillZarrDto>> violations = validator.validate(data);
        if (CollectionUtils.isEmpty(violations)) {
            list.add(data);
            if (list.size() >= BATCH_COUNT) {
                saveData();
                // 存储完成清理 list
                list = new ArrayList<>();
            }
        } else {
            log.warn("协议单原始数据校验失败 jobId={} 错误原因={}", jobId, violations.stream().findAny().orElse(null));
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
        List<TXfOriginSapZarrEntity> entities = new ArrayList<>(list.size());
        Date now = new Date();
        list.forEach(
                v1 -> {
                    TXfOriginSapZarrEntity v2 = new TXfOriginSapZarrEntity();
                    BeanUtils.copyProperties(v1, v2);
                    v2.setJobId(jobId);
                    v2.setCreateTime(now);
                    v2.setUpdateTime(now);
                    if (StringUtils.isNotBlank(v2.getContents())) {
                        String reference = v2.getContents().substring(v2.getContents().length() - 10, v2.getContents().length());
                        v2.setReference(reference);
                    }
                    v2.setAmountWithTax(v2.getAmountWithTax().replace(",",""));
                    check(v2);
                    entities.add(v2);
                }
        );
        service.saveBatch(entities);
        cursor += list.size();
        // cursor - 1 排除表头行
        log.info("jobId={}, 已入库{}条原始协议单zarr数据！本次{}条花费{}ms", jobId, cursor - 1,list.size(),System.currentTimeMillis()-start);
    }

    private void check(TXfOriginSapZarrEntity entity){
        String checkRemark = "";
        if(StringUtil.isBlank(entity.getContents())){
            checkRemark += "Contents为空;";
        }
        if(StringUtil.isBlank(entity.getAmountWithTax())){
            checkRemark += "Amount With Tax为空;";
        }else{
            try{
                new BigDecimal(entity.getAmountWithTax().replace(",",""));
            }catch (Exception e){
                checkRemark += "Amount With Tax格式错误;";
            }
        }
        if(StringUtil.isBlank(entity.getMemo())){
            checkRemark += "memo为空;";
        }else{
            try {
                SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd");
                fmt2.parse(entity.getMemo().substring(entity.getMemo().length() - 10));
            }catch (Exception e){
                checkRemark += "memo中的日期格式错误;";
            }
        }
        if(StringUtil.isBlank(entity.getReference())){
            checkRemark += "Contents中没有reference相关信息;";
        }
        if(StringUtil.isNotBlank(checkRemark)){
            entity.setCheckRemark(checkRemark);
            entity.setCheckStatus(1);
        }
    }
}
