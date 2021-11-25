package com.xforceplus.wapp.modules.job.executor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.InvoiceStatusEnum;
import com.xforceplus.wapp.modules.exchange.service.InvoiceExchangeService;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

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

    @Async
    //@Scheduled(cron = "0 */1 * * * ?")
    public void execute(){
        log.info("换票定时任务--开始");
        try {
            QueryWrapper<TDxRecordInvoiceEntity> recordInvoiceWrapper = new QueryWrapper<>();
            recordInvoiceWrapper.eq(TDxRecordInvoiceEntity.IS_DEL, IsDealEnum.NO);
            recordInvoiceWrapper.eq(TDxRecordInvoiceEntity.RZH_YESORNO,"1");
            recordInvoiceWrapper.eq(TDxRecordInvoiceEntity.INVOICE_STATUS, InvoiceStatusEnum.INVOICE_STATUS_LOSE.getCode());
            recordInvoiceWrapper.ge(TDxRecordInvoiceEntity.CREATE_DATE, DateUtils.addDate(new Date(), -30));
            List<TDxRecordInvoiceEntity> tDxRecordInvoiceEntities =  tDxRecordInvoiceDao.selectList(recordInvoiceWrapper);
            if(CollectionUtils.isNotEmpty(tDxRecordInvoiceEntities)){
                invoiceExchangeService.saveBatch(tDxRecordInvoiceEntities, "底账同步该发票已失控");
            }
        } catch (Exception e) {
            log.error("换票定时任务执行失败",e);
        }

    }
}
