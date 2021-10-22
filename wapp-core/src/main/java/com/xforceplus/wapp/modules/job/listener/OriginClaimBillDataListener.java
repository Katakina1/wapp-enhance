package com.xforceplus.wapp.modules.job.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xforceplus.wapp.modules.job.dto.OriginClaimBillDto;
import com.xforceplus.wapp.modules.job.service.OriginClaimBillService;
import com.xforceplus.wapp.repository.entity.TXfOriginClaimBillEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    /**
     * 缓存的数据
     */
    private List<OriginClaimBillDto> list = new ArrayList<>();
    @Getter
    private long cursor;

    public OriginClaimBillDataListener(int jobId, long cursor, OriginClaimBillService service) {
        this.jobId = jobId;
        this.cursor = cursor;
        this.service = service;
    }

    @Override
    public void invoke(OriginClaimBillDto data, AnalysisContext context) {
        list.add(data);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            list = new ArrayList<>();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        List<TXfOriginClaimBillEntity> entities = new ArrayList<>(list.size());
        Date now = new Date();
        list.forEach(
                v1 -> {
                    TXfOriginClaimBillEntity v2 = new TXfOriginClaimBillEntity();
                    BeanUtils.copyProperties(v1, v2);
                    v2.setJobId(jobId);
                    v2.setCreateTime(now);
                    v2.setUpdateTime(now);
                    entities.add(v2);
                }
        );
        service.saveBatch(entities);
        cursor += list.size();
        log.info("jobId={}, 已入库{}条原始索赔单数据！", jobId, cursor);
    }
}
