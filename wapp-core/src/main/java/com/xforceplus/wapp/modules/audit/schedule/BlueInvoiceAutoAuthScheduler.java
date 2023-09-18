package com.xforceplus.wapp.modules.audit.schedule;

import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.modules.audit.service.InvoiceAuditService;
import com.xforceplus.wapp.modules.backfill.service.BackFillService;
import com.xforceplus.wapp.repository.entity.InvoiceAudit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 蓝票自动审核（查询认证状态）
 * @date : 2022/09/21 13:41
 **/
@Component
@Slf4j
public class BlueInvoiceAutoAuthScheduler {

    @Autowired
    InvoiceAuditService invoiceAuditService;
    @Autowired
    BackFillService backFillService;


    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${task.blueInvoiceAutoAuth-cron:0 0 1 * * ?}")
    public void execute() {
        log.info("查询近五周待审核的发票");
        // 查询近五周待审核的发票
        List<InvoiceAudit> invoiceAuditList = Lists.newArrayList();
        int times = 5;
        for (int i = 0; i < times; i++) {
            Date end = DateUtils.addDate(new Date(), (-i) * 7);
            Date begin = DateUtils.addDate(new Date(), (-i - 1) * 7);
            invoiceAuditList.addAll(invoiceAuditService.search(begin, end));
        }

        invoiceAuditList.forEach(invoiceAudit -> {
            try {
                backFillService.autoAuthBlueFlush(invoiceAudit);
            } catch (EnhanceRuntimeException ee) {
                log.warn("蓝票自动审核异常:{},{}", invoiceAudit.getInvoiceUuid(), ee.getMessage());
            } catch (Exception e) {
                log.error("蓝票自动审核异常:{}", invoiceAudit.getInvoiceUuid(), e);
            }
        });
    }

}
