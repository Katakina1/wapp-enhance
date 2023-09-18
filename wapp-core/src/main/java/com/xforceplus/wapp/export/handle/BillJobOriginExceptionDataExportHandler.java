package com.xforceplus.wapp.export.handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xforceplus.wapp.export.ExportHandlerEnum;
import com.xforceplus.wapp.export.IExportHandler;
import com.xforceplus.wapp.export.dto.BillJobOriginDataExportDto;
import com.xforceplus.wapp.modules.job.service.BillJobOriginDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-19 10:59
 **/
@Component
@Slf4j
public class BillJobOriginExceptionDataExportHandler implements IExportHandler {
    @Autowired
    private BillJobOriginDataService billJobOriginDataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void doExport(Message<String> message,String messageId) {
        final String payload = message.getPayload();
        try {
            final BillJobOriginDataExportDto exportDto = objectMapper.readValue(payload, BillJobOriginDataExportDto.class);
            billJobOriginDataService.exportBillJobOriginExceptionData(exportDto);
        } catch (Exception e) {
            log.error("索赔主信息异常数据导出参数反序列化出错:"+e.getMessage(),e);
        }
    }

    @Override
    public String handlerName() {
        return ExportHandlerEnum.BILL_JOB_REPORT.name();
    }
}
