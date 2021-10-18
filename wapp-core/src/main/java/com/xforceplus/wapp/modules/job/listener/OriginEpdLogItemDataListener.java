package com.xforceplus.wapp.modules.job.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xforceplus.wapp.modules.job.dto.OriginEpdLogItemDto;
import com.xforceplus.wapp.modules.job.service.OriginEpdLogItemService;
import com.xforceplus.wapp.repository.entity.TXfOriginEpdLogItemEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

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
    /**
     * 缓存的数据
     */
    private List<OriginEpdLogItemDto> list = new ArrayList<>();

    private final int jobId;

    @Getter
    private long cursor;

    private final OriginEpdLogItemService service;

    public OriginEpdLogItemDataListener(int jobId, long cursor, OriginEpdLogItemService service) {
        this.jobId = jobId;
        this.cursor = cursor;
        this.service = service;
    }

    @Override
    public void invoke(OriginEpdLogItemDto data, AnalysisContext context) {
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
        List<TXfOriginEpdLogItemEntity> entities = new ArrayList<>(list.size());
        list.forEach(
                v1 -> {
                    TXfOriginEpdLogItemEntity v2 = new TXfOriginEpdLogItemEntity();
                    BeanUtils.copyProperties(v1, v2);
                    v2.setJobId(jobId);
                    entities.add(v2);
                }
        );
        service.saveBatch(entities);
        cursor += list.size();
        log.info("jobId={}, 已入库{}条数据！", jobId, cursor);
    }
}