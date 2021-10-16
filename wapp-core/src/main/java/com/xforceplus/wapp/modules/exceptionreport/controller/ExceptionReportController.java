package com.xforceplus.wapp.modules.exceptionreport.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import io.swagger.annotations.Api;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 16:19
 **/
@Api(tags = "例外报告接口")
@RestController
@RequestMapping(EnhanceApi.BASE_PATH+"/exception-report")
public class ExceptionReportController {

    @Autowired
    private ExceptionReportService exceptionReportService;

    @GetMapping("claim")
    public R getClaim(ExceptionReportRequest request){
        final Page<TXfExceptionReportEntity> page = exceptionReportService.getPage(request, ExceptionReportTypeEnum.CLAIM);
        return R.ok(toPageResult(page));
    }

    @GetMapping("epd")
    public R getEPD(ExceptionReportRequest request){
        final Page<TXfExceptionReportEntity> page = exceptionReportService.getPage(request, ExceptionReportTypeEnum.EPD);
        return R.ok(toPageResult(page));
    }

    @GetMapping("agreement")
    public R getAgreement(ExceptionReportRequest request){
        final Page<TXfExceptionReportEntity> page = exceptionReportService.getPage(request, ExceptionReportTypeEnum.AGREEMENT);
        return R.ok(toPageResult(page));
    }

   private static <T> PageResult<T> toPageResult(Page<T> page){
        return PageResult.of(page.getRecords(), page.getTotal(), page.getPages(), page.getSize());
    }


}
