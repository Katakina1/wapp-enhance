package com.xforceplus.wapp.modules.job.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xforceplus.wapp.modules.job.dto.OriginAgreementBillFbl5nDto;
import com.xforceplus.wapp.modules.job.service.OriginSapFbl5nService;
import com.xforceplus.wapp.repository.entity.TXfOriginSapFbl5nEntity;
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
public class OriginAgreementBillFbl5nDataListener extends AnalysisEventListener<OriginAgreementBillFbl5nDto> {
    /**
     * 每隔1000条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;
    private final int jobId;
    private final OriginSapFbl5nService service;
    private final Validator validator;
    /**
     * 缓存的数据
     */
    private List<OriginAgreementBillFbl5nDto> list = new ArrayList<>();
    @Getter
    private long cursor;

    public OriginAgreementBillFbl5nDataListener(int jobId, long cursor, OriginSapFbl5nService service, Validator validator) {
        this.jobId = jobId;
        this.cursor = cursor;
        this.service = service;
        this.validator = validator;
    }

    @Override
    public void invoke(OriginAgreementBillFbl5nDto data, AnalysisContext context) {
        Set<ConstraintViolation<OriginAgreementBillFbl5nDto>> violations = validator.validate(data);
        if (CollectionUtils.isEmpty(violations)) {
            list.add(data);
            if (list.size() >= BATCH_COUNT) {
                saveData();
                // 存储完成清理 list
                list = new ArrayList<>();
            }
        } else {
            log.warn("协议单原始fbl5n数据校验失败 jobId={} 错误原因={}", jobId, violations.stream().findAny().orElse(null));
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
        List<TXfOriginSapFbl5nEntity> entities = new ArrayList<>(list.size());
        Date now = new Date();
        list.forEach(
                v1 -> {
                    TXfOriginSapFbl5nEntity v2 = new TXfOriginSapFbl5nEntity();
                    BeanUtils.copyProperties(v1, v2);
                    v2.setJobId(jobId);
                    v2.setCreateTime(now);
                    v2.setUpdateTime(now);
                    v2.setAmountInDocCurr(v2.getAmountInDocCurr().replace(",",""));
                    check(v2);
                    entities.add(v2);
                }
        );
        service.saveBatch(entities);
        cursor += list.size();
        // cursor - 1 排除表头行
        log.info("jobId={}, 已入库{}条原始协议单fbl5n据！本次{}条花费{}ms", jobId, cursor - 1,list.size(),System.currentTimeMillis()-start);
    }

    private void check(TXfOriginSapFbl5nEntity entity){
        String checkRemark = "";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        if(StringUtil.isBlank(entity.getCompanyCode())){
            checkRemark += "Company Code为空;";
        }
        if(StringUtil.isBlank(entity.getAccount())){
            checkRemark += "Account为空;";
        }
        if(StringUtil.isBlank(entity.getReference())){
            checkRemark += "Reference为空;";
        }
        if(StringUtil.isBlank(entity.getDocumentType())){
            checkRemark += "Document Type为空;";
        }
        if(StringUtil.isBlank(entity.getPostingDate())){
            checkRemark += "Posting Date为空;";
        }else{
            try {
                fmt.parse(entity.getPostingDate());
            }catch (Exception e){
                checkRemark += "Posting Date格式错误;";
            }
        }
        if(StringUtil.isBlank(entity.getClearingDate())){
            checkRemark += "Clearing date为空;";
        }else{
            try {
                fmt.parse(entity.getClearingDate());
            }catch (Exception e){
                checkRemark += "Clearing date格式错误;";
            }
        }
        if(StringUtil.isBlank(entity.getAmountInDocCurr())){
            checkRemark += "Amount in doc.curr.为空;";
        }else{
            try {
                new BigDecimal(entity.getAmountInDocCurr().replace(",",""));
            } catch (Exception e) {
                checkRemark += "Amount in doc.curr.格式错误;";
            }
        }
        if(StringUtil.isNotBlank(checkRemark)){
            entity.setCheckRemark(checkRemark);
            entity.setCheckStatus(1);
        }
    }
}
