package com.xforceplus.wapp.threadpool.callable;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.export.dto.BillExportDto;
import com.xforceplus.wapp.modules.deduct.service.ExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * Describe: 业务单导出
 *
 * @Author xiezhongyong
 * @Date 2022/10/11
 */
public class BillExportCallable implements Callable<Boolean> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExportService exportService;

    private BillExportDto dto;


    public BillExportCallable(ExportService billExportService, BillExportDto dto) {
        this.exportService = billExportService;
        this.dto = dto;
    }

    @Override
    public Boolean call() {
        Boolean isSuccess;
        long startTime = System.currentTimeMillis();
        try {
            logger.info("***********BillExportCallable通过线程池发起业务单导出执行开始,request:{}", JSONObject.toJSONString(dto));
            isSuccess = exportService.doExport(dto);
        } catch (Exception e) {
            logger.error("导出失败", e);
            isSuccess = false;
        }
        logger.info("***********BillExportCallable通过线程池发起业务单导出执行完毕，执行结果：isSuccess[{}],costTime[{}]"
                , isSuccess, (System.currentTimeMillis() - startTime));
        return isSuccess;
    }
}
