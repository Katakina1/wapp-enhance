package com.xforceplus.wapp.modules.exceptionreport.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
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
@RestController
@RequestMapping(EnhanceApi.BASE_PATH+"/exception-report")
public class ExceptionReportController {

    @Autowired
    private ExceptionReportService exceptionReportService;

    @GetMapping("claim")
    public R getClaim(ExceptionReportRequest request){
        exceptionReportService.getPage(request);
        return R.ok();
    }

    @GetMapping("epd")
    public R getEPD(ExceptionReportRequest request){
        exceptionReportService.getPage(request);
        return R.ok();
    }

    @GetMapping("agreement")
    public R getAgreement(ExceptionReportRequest request){
        exceptionReportService.getPage(request);
        return R.ok();
    }
}
