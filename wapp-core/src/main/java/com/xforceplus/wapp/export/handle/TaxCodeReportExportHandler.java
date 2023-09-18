package com.xforceplus.wapp.export.handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xforceplus.wapp.export.ExportHandlerEnum;
import com.xforceplus.wapp.export.IExportHandler;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.export.dto.TaxCodeReportExportDto;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class TaxCodeReportExportHandler implements IExportHandler {
    @Autowired
    private TaxCodeReportService taxCodeReportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void doExport(Message<String> message,String messageId) {
        final String payload = message.getPayload();
        try {
            final TaxCodeReportExportDto exportDto = objectMapper.readValue(payload, TaxCodeReportExportDto.class);
            taxCodeReportService.doExport(exportDto);
        } catch (Exception e) {
            log.error("例外报告导出参数反序列化出错:"+e.getMessage(),e);
        }

    }

    @Override
    public String handlerName() {
        return ExportHandlerEnum.TAX_CODE_REPORT.name();
    }
}
