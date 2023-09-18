package com.xforceplus.wapp.modules.job.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xforceplus.wapp.modules.job.dto.OriginClaimBillDto;
import com.xforceplus.wapp.modules.job.service.OriginClaimBillService;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimBillEntity;
import jodd.util.StringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
public class OriginClaimBillDataListener extends AnalysisEventListener<OriginClaimBillDto> {
    /**
     * 每隔1000条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;
    private final int jobId;
    private final OriginClaimBillService service;
    private final Validator validator;
    /**
     * 缓存的数据
     */
    private List<OriginClaimBillDto> list = new ArrayList<>();
    @Getter
    private long cursor;

    public OriginClaimBillDataListener(int jobId, long cursor, OriginClaimBillService service, Validator validator) {
        this.jobId = jobId;
        this.cursor = cursor;
        this.service = service;
        this.validator = validator;
    }

    @Override
    public void invoke(OriginClaimBillDto data, AnalysisContext context) {
        Set<ConstraintViolation<OriginClaimBillDto>> violations = validator.validate(data);
        if (CollectionUtils.isEmpty(violations)) {
            list.add(data);
            if (list.size() >= BATCH_COUNT) {
                saveData();
                // 存储完成清理 list
                list = new ArrayList<>();
            }
        } else {
            log.warn("索赔单原始数据校验失败 jobId={} 错误原因={}", jobId, violations.stream().findAny().orElse(null));
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
        long start = System.currentTimeMillis();
        List<TXfOriginClaimBillEntity> entities = new ArrayList<>(list.size());
        Date now = new Date();
        list.forEach(
                v1 -> {
                    TXfOriginClaimBillEntity v2 = new TXfOriginClaimBillEntity();
                    BeanUtils.copyProperties(v1, v2);
                    v2.setJobId(jobId);
                    v2.setCreateTime(now);
                    v2.setUpdateTime(now);
                    v2.setCostAmount(v2.getCostAmount().replace(",",""));
                    v2.setTaxRate(v2.getTaxRate().replace(",",""));
                    v2.setAmountWithTax(v2.getAmountWithTax().replace(",",""));
                    check(v2);
                    entities.add(v2);
                }
        );
        service.saveBatch(entities);
        cursor += list.size();
        // cursor - 1 排除表头行
        log.info("jobId={}, 已入库{}条原始索赔单数据！本次{}条花费{}ms", jobId, cursor - 1, list.size(), System.currentTimeMillis() - start);
    }

    private void check(TXfOriginClaimBillEntity entity) {
        String checkRemark = "";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        if (StringUtil.isBlank(entity.getCostAmount())) {
            checkRemark += "成本金额为空;";
        }else{
            try {
                BigDecimal costAmount = new BigDecimal(entity.getCostAmount().replace(",",""));
//                if(costAmount.compareTo(BigDecimal.ZERO) < 0){
//                    checkRemark += "成本金额必须是正数;";
//                }
            } catch (Exception e) {
                checkRemark += "成本金额格式错误;";
            }
        }
        if (StringUtil.isBlank(entity.getClaimNo())) {
            checkRemark += "索赔号为空;";
        }
        if (StringUtil.isBlank(entity.getDecisionDate())) {
            checkRemark += "定案日期为空;";
        } else {
            try {
                fmt.parse(entity.getDecisionDate());
            } catch (Exception e) {
                checkRemark += "定案日期格式错误;";
            }
        }
        if (StringUtil.isBlank(entity.getDeductionDate())) {
            checkRemark += "扣款日期为空;";
        } else {
            try {
                fmt.parse(entity.getDeductionDate());
            } catch (Exception e) {
                checkRemark += "扣款日期格式错误;";
            }
        }
        if (StringUtil.isBlank(entity.getTaxRate())) {
            checkRemark += "税率为空;";
        } else {
            try {
                BigDecimal taxRate = new BigDecimal(entity.getTaxRate());
                if (taxRate.compareTo(BigDecimal.ZERO) == -1
                        || taxRate.compareTo(BigDecimal.ONE) > 0) {
                    checkRemark += "税率格式错误;";
                }
            } catch (Exception e) {
                checkRemark += "税率格式错误;";
            }
        }
        if (StringUtil.isBlank(entity.getAmountWithTax())) {
            checkRemark += "含税金额为空;";
        }else{
            try {
                new BigDecimal(entity.getAmountWithTax().replace(",",""));
            } catch (Exception e) {
                checkRemark += "含税金额格式错误;";
            }
        }
        if (StringUtil.isNotBlank(checkRemark)) {
            entity.setCheckRemark(checkRemark);
            entity.setCheckStatus(1);
        }
    }
}
