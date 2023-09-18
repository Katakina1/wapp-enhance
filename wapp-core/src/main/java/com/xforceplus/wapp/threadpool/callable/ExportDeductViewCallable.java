package com.xforceplus.wapp.threadpool.callable;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.export.dto.DeductViewBillExportDto;
import com.xforceplus.wapp.modules.deduct.service.DeductViewService;

/**
 * Created by le
 * 导出业务单
 */
public class ExportDeductViewCallable implements Callable<Boolean> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DeductViewService deductService;

    private DeductViewBillExportDto request;
    
    public ExportDeductViewCallable(DeductViewService deductService, DeductViewBillExportDto dto, TXfDeductionBusinessTypeEnum businessTypeEnum) {
        this.deductService = deductService;
        this.request = dto;
    }

    @Override
    public Boolean call(){
        Boolean isSuccess = false;
        long startTime = System.currentTimeMillis();
        try {
            logger.info("***********通过线程池发起业务单导出执行开始,request:{}", JSONObject.toJSONString(request));
			isSuccess = deductService.doExport(request);
        } catch (Exception e) {
            logger.error("导出失败", e);
            isSuccess = false;
        }
        logger.info("***********通过线程池发起业务单导出执行完毕，执行结果：isSuccess[{}],costTime[{}]", isSuccess, (System.currentTimeMillis() - startTime));
        return isSuccess;
    }
}
