package com.xforceplus.wapp.modules.job.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xforceplus.wapp.modules.job.dto.OriginClaimItemHyperDto;
import com.xforceplus.wapp.modules.job.service.OriginClaimItemHyperService;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimBillEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimItemHyperEntity;
import jodd.util.StringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

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
public class OriginClaimItemHyperDataListener extends AnalysisEventListener<OriginClaimItemHyperDto> {
    /**
     * 每隔1000条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;
    private final int jobId;
    private final OriginClaimItemHyperService service;
    private final Validator validator;
    /**
     * 缓存的数据
     */
    private List<OriginClaimItemHyperDto> list = new ArrayList<>();
    @Getter
    private long cursor;

    public OriginClaimItemHyperDataListener(int jobId, long cursor, OriginClaimItemHyperService service, Validator validator) {
        this.jobId = jobId;
        this.cursor = cursor;
        this.service = service;
        this.validator = validator;
    }

    @Override
    public void invoke(OriginClaimItemHyperDto data, AnalysisContext context) {
        Set<ConstraintViolation<OriginClaimItemHyperDto>> violations = validator.validate(data);
        if (CollectionUtils.isEmpty(violations)) {
            list.add(data);
            if (list.size() >= BATCH_COUNT) {
                saveData();
                // 存储完成清理 list
                list = new ArrayList<>();
            }
        } else {
            log.warn("索赔单Hyper明细原始数据校验失败 jobId={} 错误原因={}", jobId, violations.stream().findAny().orElse(null));
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
        List<TXfOriginClaimItemHyperEntity> entities = new ArrayList<>(list.size());
        Date now = new Date();
        list.forEach(
                v1 -> {
                    TXfOriginClaimItemHyperEntity v2 = new TXfOriginClaimItemHyperEntity();
                    BeanUtils.copyProperties(v1, v2);
                    v2.setJobId(jobId);
                    v2.setCreateTime(now);
                    v2.setUpdateTime(now);
                    v2.setVnpkCost(v2.getVnpkCost().replace(",",""));
                    v2.setUnitCost(v2.getUnitCost().replace(",",""));
                    v2.setItemQty(v2.getItemQty().replace(",",""));
                    v2.setLineCost(v2.getLineCost().replace(",",""));
                    v2.setTaxRate(v2.getTaxRate().replace(",",""));
                    check(v2);
                    entities.add(v2);
                }
        );
        service.saveBatch(entities);
        cursor += list.size();
        // cursor - 1 排除表头行
        log.info("jobId={}, 已入库{}条原始索赔单Hyper明细数据！本次{}条花费{}ms", jobId, cursor - 1, list.size(), System.currentTimeMillis() - start);
    }

    private void check(TXfOriginClaimItemHyperEntity entity) {
        String checkRemark = "";
        if (StringUtil.isBlank(entity.getClaimNbr())) {
            checkRemark += "CLAIM_NBR为空;";
        }
        if (StringUtil.isBlank(entity.getItemNbr())) {
            checkRemark += "ITEM_NBR为空;";
        }
        if (StringUtil.isBlank(entity.getUpcNbr())) {
            checkRemark += "UPC_NBR为空;";
        }
        if (StringUtil.isBlank(entity.getUnitCost())) {
            checkRemark += "UNIT_COST为空;";
        }else{
            try {
                new BigDecimal(entity.getUnitCost().replace(",",""));
            } catch (Exception e) {
                checkRemark += "UNIT_COST格式错误;";
            }
        }
        if (StringUtil.isBlank(entity.getItemQty())) {
            checkRemark += "ITEM_QTY为空;";
        }else{
            try {
                new BigDecimal(entity.getItemQty().replace(",",""));
            } catch (Exception e) {
                checkRemark += "ITEM_QTY格式错误;";
            }
        }
        if (StringUtil.isBlank(entity.getLineCost())) {
            checkRemark += "LINE_COST为空;";
        } else {
            try {
                new BigDecimal(entity.getLineCost().replace(",",""));
            } catch (Exception e) {
                checkRemark += "LINE_COST格式错误;";
            }
        }
        if (StringUtil.isBlank(entity.getTaxRate())) {
            checkRemark += "TAX_RATE为空;";
        } else {
            try {
                if (!entity.getTaxRate().contains("%")) {
                    checkRemark += "TAX_RATE格式错误;";
                } else {
                    BigDecimal taxRate = new BigDecimal(entity.getTaxRate().replace("%", "")).
                            divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
                    if (taxRate.compareTo(BigDecimal.ZERO) == -1
                            || taxRate.compareTo(BigDecimal.ONE) >= 0) {
                        checkRemark += "TAX_RATE格式错误;";
                    }
                }
            } catch (Exception e) {
                checkRemark += "TAX_RATE格式错误;";
            }
        }
        if (StringUtil.isNotBlank(checkRemark)) {
            entity.setCheckRemark(checkRemark);
            entity.setCheckStatus(1);
        }
    }
}
