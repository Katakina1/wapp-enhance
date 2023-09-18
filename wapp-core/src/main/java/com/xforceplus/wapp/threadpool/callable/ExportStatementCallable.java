package com.xforceplus.wapp.threadpool.callable;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.modules.statement.dto.StatementExportDto;
import com.xforceplus.wapp.modules.statement.service.StatementServiceImpl;

/**
 * Created by le
 * 导出业务单
 */
public class ExportStatementCallable implements Callable<Boolean> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private StatementServiceImpl statementService;

    private StatementExportDto request;
    
    public ExportStatementCallable(StatementServiceImpl statementService, StatementExportDto exportDto) {
        this.statementService = statementService;
        this.request = exportDto;
    }

    @Override
    public Boolean call(){
        Boolean isSuccess = false;
        long startTime = System.currentTimeMillis();
        try {
            logger.info("***********通过线程池发起结算单导出执行开始,request:{}", JSONObject.toJSONString(request));
			isSuccess = statementService.doExport(request);
        } catch (Exception e) {
            logger.error("导出失败", e);
            isSuccess = false;
        }
        logger.info("***********通过线程池发起结算单导出执行完毕，执行结果：isSuccess[{}],costTime[{}]", isSuccess, (System.currentTimeMillis() - startTime));
        return isSuccess;
    }
}
