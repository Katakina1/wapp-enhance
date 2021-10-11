package com.xforceplus.wapp.modules.exceptionreport.controller;

import com.xforceplus.wapp.annotation.EnhanceApiV1;
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
@RequestMapping(EnhanceApiV1.BASE_PATH+"/exception-report")
public class ExceptionReportController {

    @GetMapping
    public String get(){
        return "hello";
    }
}
