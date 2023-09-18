package com.xforceplus.wapp.modules.job.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xforceplus.wapp.modules.job.dto.OriginClaimItemSamsDto;
import com.xforceplus.wapp.modules.job.service.OriginClaimItemSamsService;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimItemHyperEntity;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimItemSamsEntity;
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
public class OriginClaimItemSamsDataListener extends AnalysisEventListener<OriginClaimItemSamsDto> {
    /**
     * 每隔1000条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;
    private final int jobId;
    private final OriginClaimItemSamsService service;
    private final Validator validator;
    /**
     * 缓存的数据
     */
    private List<OriginClaimItemSamsDto> list = new ArrayList<>();
    @Getter
    private long cursor;

    public OriginClaimItemSamsDataListener(int jobId, long cursor, OriginClaimItemSamsService service, Validator validator) {
        this.jobId = jobId;
        this.cursor = cursor;
        this.service = service;
        this.validator = validator;
    }

    @Override
    public void invoke(OriginClaimItemSamsDto data, AnalysisContext context) {
        Set<ConstraintViolation<OriginClaimItemSamsDto>> violations = validator.validate(data);
        if (CollectionUtils.isEmpty(violations)) {
            list.add(data);
            if (list.size() >= BATCH_COUNT) {
                saveData();
                // 存储完成清理 list
                list = new ArrayList<>();
            }
        } else {
            log.warn("索赔单Sams明细原始数据校验失败 jobId={} 错误原因={}", jobId, violations.stream().findAny().orElse(null));
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
        List<TXfOriginClaimItemSamsEntity> entities = new ArrayList<>(list.size());
        Date now = new Date();
        list.forEach(
                v1 -> {
                    TXfOriginClaimItemSamsEntity v2 = new TXfOriginClaimItemSamsEntity();
                    BeanUtils.copyProperties(v1, v2);
                    v2.setJobId(jobId);
                    v2.setCreateTime(now);
                    v2.setUpdateTime(now);
                    v2.setShipQty(v2.getShipQty().replace(",",""));
                    if(StringUtils.isNotBlank(v2.getItemTaxPct())) {
                    	v2.setItemTaxPct(v2.getItemTaxPct().replace(",",""));
                    }
                    check(v2);
                    entities.add(v2);
                }
        );
        service.saveBatch(entities);
        cursor += list.size();
        // cursor - 1 排除表头行
        log.info("jobId={}, 已入库{}条原始索赔单Sams明细数据！本次{}条花费{}ms", jobId, cursor - 1, list.size(), System.currentTimeMillis() - start);
    }

    private void check(TXfOriginClaimItemSamsEntity entity) {
        String checkRemark = "";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        if (StringUtil.isBlank(entity.getItemNbr())) {
            checkRemark += "item_nbr为空;";
        }
        if(StringUtil.isBlank(entity.getShipQty())){
            checkRemark += "ship_qty为空;";
        }else{
            try {
                new BigDecimal(entity.getShipQty().replace(",",""));
            } catch (Exception e) {
                checkRemark += "ship_qty格式错误;";
            }
        }
        if (StringUtil.isBlank(entity.getShipCost())) {
            checkRemark += "ship_cost为空;";
        }else{
            try {
                new BigDecimal(entity.getShipCost().replace(",",""));
            } catch (Exception e) {
                checkRemark += "ship_cost格式错误;";
            }
        }
        if (StringUtil.isBlank(entity.getItemTaxPct())) {
            checkRemark += "ITEM_TAX_PCT为空;";
        }else {
            try {
                BigDecimal taxRate = new BigDecimal(entity.getItemTaxPct().replace(",",""));
                if (taxRate.compareTo(BigDecimal.ZERO) == -1
                        || taxRate.compareTo(BigDecimal.ONE) > 0) {
                    checkRemark += "ITEM_TAX_PCT格式错误;";
                }
            } catch (Exception e) {
                checkRemark += "ITEM_TAX_PCT格式错误;";
            }
        }
        if (StringUtil.isBlank(entity.getRtnDate())) {
            checkRemark += "rtn_date为空;";
        }else{
            try{
                fmt.parse(entity.getRtnDate());
            }catch (Exception e){
                checkRemark += "RTN_DATE格式错误;";
            }
        }
        if (StringUtil.isBlank(entity.getClaimNumber())) {
            checkRemark += "claim_number为空;";
        }
        if (StringUtil.isNotBlank(checkRemark)) {
            entity.setCheckRemark(checkRemark);
            entity.setCheckStatus(1);
        }
    }
}
