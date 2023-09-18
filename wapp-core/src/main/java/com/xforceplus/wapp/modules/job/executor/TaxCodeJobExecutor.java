package com.xforceplus.wapp.modules.job.executor;

import com.google.gson.Gson;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.modules.taxcode.service.impl.TaxCodeReportServiceImpl;
import com.xforceplus.wapp.modules.taxcode.service.impl.TaxCodeRiversandServiceImpl;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import com.xforceplus.wapp.repository.entity.TXfTaxCodeRiversandEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class TaxCodeJobExecutor {
    @Autowired
    private TaxCodeRiversandServiceImpl taxCodeRiversandService;

    @Autowired
    private ActiveMqProducer activeMqProducer;

    @Value("${activemq.queue-name.import-riversand-taxcode-queue}")
    private String importQueue;

    @Autowired
    private LockClient lockClient;
    public static String KEY = "taxcode-exchange";
    //扫描的天数
    private static final int days = 7;

    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${riverSand.synchro-cron}")
    public void execute() {
        start(null);
    }

    @Async("taskThreadPoolExecutor")
    public void start(String time) {
        lockClient.tryLock(KEY, () -> {
            log.info("同步riversand税编定时任务--开始");
            //接口获取riversand税编信息
            //异步处理比较税编生成报告
            taxCodeRiversandService.getRiverSandTaxCode(time, it -> it.forEach(t -> activeMqProducer.send(importQueue, new Gson().toJson(t))));
            log.info("同步riversand税编定时任务--结束");
        }, -1, 1);
    }
}

