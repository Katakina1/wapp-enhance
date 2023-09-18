package com.xforceplus.wapp.modules.exceptionreport.listener;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.modules.exceptionreport.event.NewExceptionReportEvent;
import com.xforceplus.wapp.modules.exceptionreport.mapstruct.ExceptionReportMapper;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-20 15:38
 **/
@Component
@Slf4j
public class NewExceptionReportListener {
    @Autowired
    private ExceptionReportService exceptionReportService;

    @Autowired
    private ExceptionReportMapper exceptionReportMapper;

    @EventListener
    @Async
    public void handle(NewExceptionReportEvent exceptionReportEvent) {
		log.info("NewExceptionReportListener:{}", JSON.toJSONString(exceptionReportEvent));
        final TXfExceptionReportEntity entity = exceptionReportMapper.deductToReport(exceptionReportEvent.getDeduct());
        entity.setType(exceptionReportEvent.getType().getType());
        entity.setDescription(exceptionReportEvent.getReportCode().getDescription());
        entity.setId(null);
        entity.setCode(exceptionReportEvent.getReportCode().getCode());
        entity.setBillId(exceptionReportEvent.getDeduct().getId());
        entity.setBillNo(exceptionReportEvent.getDeduct().getBusinessNo());
        entity.setPurchaserName(exceptionReportEvent.getDeduct().getPurchaserName());
        entity.setTaxBalance(exceptionReportEvent.getTaxBalance());
        //2022-08-08新增，例外报告新增具体原因
        if(exceptionReportEvent.getMessage() != null) {
        	entity.setRemark(entity.getRemark() == null ? exceptionReportEvent.getMessage() : entity.getRemark() + exceptionReportEvent.getMessage());
        }
        switch (exceptionReportEvent.getType()) {
            case CLAIM:
                exceptionReportService.add4Claim(entity);
                break;
            case EPD:
                exceptionReportService.add4EPD(entity);
                break;
            case AGREEMENT:
                exceptionReportService.add4Agreement(entity);
                break;
            default:
        }

    }
}
