package com.xforceplus.wapp.export.handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xforceplus.wapp.export.ExportHandlerEnum;
import com.xforceplus.wapp.export.IExportHandler;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-19 10:59
 **/
@Component
@Slf4j
public class ExceptionReportExportHandler implements IExportHandler {
    @Autowired
    private ExceptionReportService exceptionReportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void doExport(Message<String> message,String messageId) {
        final String payload = message.getPayload();
        try {
            final ExceptionReportExportDto exportDto = objectMapper.readValue(payload, ExceptionReportExportDto.class);
            exceptionReportService.doExport(exportDto);
        } catch (Exception e) {
            log.error("例外报告导出参数反序列化出错:"+e.getMessage(),e);
        }

    }

    @Override
    public String handlerName() {
        return ExportHandlerEnum.EXCEPTION_REPORT.name();
    }
}
