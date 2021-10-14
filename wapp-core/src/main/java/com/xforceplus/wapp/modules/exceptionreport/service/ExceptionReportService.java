package com.xforceplus.wapp.modules.exceptionreport.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;

public interface ExceptionReportService {

    /**
     * 索赔单添加例外报告
     *
     * @param entity
     */
    void add4Claim(TXfExceptionReportEntity entity);

    /**
     * 协议单添加例外报告
     *
     * @param entity
     */
    void add4Agreement(TXfExceptionReportEntity entity);

    /**
     * EPD添加例外报告
     *
     * @param entity
     */
    void add4EPD(TXfExceptionReportEntity entity);

    Page<TXfExceptionReportEntity> getPage(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum);
}
