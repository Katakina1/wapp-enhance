package com.xforceplus.wapp.modules.job.executor;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.modules.job.service.impl.HostUnMatchInvoiceService;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
/**
 * 每个月的一号扫描签收成功，未付款未认证 未匹配的发票，通过小铃铛提醒给供应商
 */
public class HostUnSuccInvoiceExecutor {

    @Autowired
    private HostUnMatchInvoiceService hostUnMatchInvoiceService;


    @Autowired
    private LockClient lockClient;
    public static String KEY = "host-unMatch-invoice";
    //扫描的天数
    static final int days = 7;

    @Async("taskThreadPoolExecutor")

    @Scheduled(cron = "${host.synchro-cron}")
    public void execute(){
        lockClient.tryLock(KEY, () -> {
            log.info("定时扫描每个月的一号扫描签收成功，未付款未认证 未匹配的发票--开始");
            List<TDxRecordInvoiceEntity> list=hostUnMatchInvoiceService.selectList(null);
            if(CollectionUtils.isNotEmpty(list)){
                hostUnMatchInvoiceService.sendVenderMessage(list);
                hostUnMatchInvoiceService.sendWalmartMessage();
            }
            log.info("定时扫描每个月的一号扫描签收成功，未付款未认证 未匹配的发票--结束");
        }, -1, 1);
    }
}

