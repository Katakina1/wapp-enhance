package com.xforceplus.wapp.modules.job.executor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.InvoiceExchangeStatusEnum;
import com.xforceplus.wapp.enums.InvoiceStatusEnum;
import com.xforceplus.wapp.modules.exchange.service.InvoiceExchangeService;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by SunShiyong on 2021/11/19.
 */

@Slf4j
@Component
public class InvoiceExchangeJobExecutor {

    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;
    @Autowired
    private InvoiceExchangeService invoiceExchangeService;
    @Autowired
    private LockClient lockClient;
    public static String KEY = "invoice-exchange";
    //扫描的天数
    private static final int days = 7;

    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "0 0 * * * ?")
    public void execute(){
        lockClient.tryLock(KEY, () -> {
            log.info("换票定时任务--开始");
            QueryWrapper<TDxRecordInvoiceEntity> recordInvoiceWrapper = new QueryWrapper<>();
            recordInvoiceWrapper.ne(TDxRecordInvoiceEntity.IS_DEL, IsDealEnum.YES.getValue());
            recordInvoiceWrapper.eq(TDxRecordInvoiceEntity.RZH_YESORNO,"1");
            recordInvoiceWrapper.eq(TDxRecordInvoiceEntity.INVOICE_STATUS, InvoiceStatusEnum.INVOICE_STATUS_LOSE.getCode());
            recordInvoiceWrapper.ge(TDxRecordInvoiceEntity.CREATE_DATE, DateUtils.addDate(new Date(), -days));
            recordInvoiceWrapper.and(w  -> w.eq(TDxRecordInvoiceEntity.EXCHANGE_STATUS,InvoiceExchangeStatusEnum.DEFAULT.getCode()).or().isNull(TDxRecordInvoiceEntity.EXCHANGE_STATUS ));
            List<TDxRecordInvoiceEntity> tDxRecordInvoiceEntities =  tDxRecordInvoiceDao.selectList(recordInvoiceWrapper);
            if(CollectionUtils.isNotEmpty(tDxRecordInvoiceEntities)) {
                List<Long> idList = tDxRecordInvoiceEntities.stream().map(TDxRecordInvoiceEntity::getId).collect(Collectors.toList());
                invoiceExchangeService.updateExchangeStatus(idList, InvoiceExchangeStatusEnum.TO_BE_EXCHANGE, "底账同步该发票已失控");
            }
            log.info("换票定时任务--结束");
        }, -1, 1);
    }
}

