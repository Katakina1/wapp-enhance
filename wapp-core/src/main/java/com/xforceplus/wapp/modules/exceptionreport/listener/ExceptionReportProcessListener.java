package com.xforceplus.wapp.modules.exceptionreport.listener;

import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.exceptionreport.event.ExceptionReportProcessEvent;
import com.xforceplus.wapp.modules.exceptionreport.event.NewExceptionReportEvent;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-03-31 11:26
 **/
@Component
@Slf4j
public class ExceptionReportProcessListener {
    private static ApplicationContext applicationContext;

    private final ExceptionReportService exceptionReportService;

    public ExceptionReportProcessListener(ApplicationContext applicationContext, ExceptionReportService exceptionReportService) {
        ExceptionReportProcessListener.applicationContext = applicationContext;
        this.exceptionReportService = exceptionReportService;
    }

    @EventListener
    @Async
    public void handle(ExceptionReportProcessEvent exceptionReportEvent) {

        final String billNo = exceptionReportEvent.getBillNo();
        try {
            final String code = exceptionReportEvent.getReportCode().getCode();

            final int type = exceptionReportEvent.getType().getType();
            boolean updateResult = exceptionReportService.updateStatus(billNo, code, type,exceptionReportEvent.getBillId());
			if (!updateResult && exceptionReportEvent.getDeduct() != null) {//2022-10-08 如果没修改，新增一条例外报告 有问题，先注释
//            	NewExceptionReportEvent newExceptionReportEvent = new NewExceptionReportEvent();
//    			newExceptionReportEvent.setDeduct(exceptionReportEvent.getDeduct());
//    			newExceptionReportEvent.setReportCode(exceptionReportEvent.getReportCode());
//    			newExceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
//    			applicationContext.publishEvent(newExceptionReportEvent);
            }
        } catch (Exception e) {
            log.error("单据:[" + billNo + "," + exceptionReportEvent.getType() + "][" + exceptionReportEvent.getReportCode() + "]异常报告处理异常:" + e.getMessage(), e);
        }
    }

    public static void publishProcessEvent(TXfBillDeductEntity tXfBillDeductEntity, ExceptionReportTypeEnum typeEnum, ExceptionReportCodeEnum codeEnum) {
		publishProcessEvent(tXfBillDeductEntity, tXfBillDeductEntity.getId(), typeEnum, codeEnum);
    }
    private static void publishProcessEvent(TXfBillDeductEntity tXfBillDeductEntity,Long billId, ExceptionReportTypeEnum typeEnum, ExceptionReportCodeEnum codeEnum) {
		log.info("触发例外报告处理完成事件:billNo:{}-->{}-->billId:{}, codeEnum:{}", tXfBillDeductEntity.getBusinessNo(), typeEnum, billId, codeEnum);
        ExceptionReportProcessEvent event = new ExceptionReportProcessEvent();
        event.setReportCode(codeEnum);
        event.setDeduct(tXfBillDeductEntity);
        event.setBillNo(tXfBillDeductEntity.getBusinessNo());
        event.setBillId(billId);
        event.setType(typeEnum);
        applicationContext.publishEvent(event);
    }
    
    private static void publishProcessEvent(String businessNo,Long billId, ExceptionReportTypeEnum typeEnum, ExceptionReportCodeEnum codeEnum) {
		log.info("触发例外报告处理完成事件:billNo:{}-->{}-->billId:{}, codeEnum:{}", businessNo, typeEnum, billId, codeEnum);
        ExceptionReportProcessEvent event = new ExceptionReportProcessEvent();
        event.setReportCode(codeEnum);
        event.setBillNo(businessNo);
        event.setBillId(billId);
        event.setType(typeEnum);
        applicationContext.publishEvent(event);
    }

    public static void publishClaimProcessEvent(TXfBillDeductEntity tXfBillDeductEntity, ExceptionReportCodeEnum codeEnum) {
        publishProcessEvent(tXfBillDeductEntity, ExceptionReportTypeEnum.CLAIM, codeEnum);
    }

    public static void publishClaimProcessEvent(String businessNo,Long billId, ExceptionReportCodeEnum codeEnum) {
		publishProcessEvent(businessNo, billId, ExceptionReportTypeEnum.CLAIM, codeEnum);
    }

}
