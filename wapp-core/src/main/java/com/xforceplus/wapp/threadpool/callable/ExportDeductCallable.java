package com.xforceplus.wapp.threadpool.callable;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.export.dto.DeductBillExportDto;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * Created by SunShiyong on 2021/10/22.
 * 导出业务单
 */
public class ExportDeductCallable implements Callable<Boolean> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DeductService deductService;

    private DeductBillExportDto dto;


    public ExportDeductCallable(DeductService deductService, DeductBillExportDto dto) {
        this.deductService = deductService;
        this.dto = dto;
    }

    @Override
    public Boolean call(){
        Boolean isSuccess;
        long startTime = System.currentTimeMillis();
        try {
            logger.info("***********通过线程池发起业务单导出执行开始,request:{}", JSONObject.toJSONString(dto));
            isSuccess = deductService.doExport(dto);
        } catch (Exception e) {
            logger.error("导出失败", e);
            isSuccess = false;
        }
        logger.info("***********通过线程池发起业务单导出执行完毕，执行结果：isSuccess[{}],costTime[{}]"
                , isSuccess, (System.currentTimeMillis() - startTime));
        return isSuccess;
    }
}
