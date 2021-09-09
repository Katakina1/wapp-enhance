package com.xforceplus.wapp.modules.posuopei.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.posuopei.entity.SubmitOutstandingReportEntity;
import com.xforceplus.wapp.modules.posuopei.service.SubmitOutstandingReportService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author raymond.yan
 */
@RestController
public class SubmitOutstandingReportController extends AbstractController {
    private final static Logger LOGGER = getLogger(SubmitOutstandingReportController.class);
    private SubmitOutstandingReportService submitOutstandingReportService;
    @Autowired
    public SubmitOutstandingReportController(SubmitOutstandingReportService submitOutstandingReportService){
        this.submitOutstandingReportService=submitOutstandingReportService;
    }

    /**
     * 录入submitOutstandingReport表数据
     * @return
     */
    @SysLog("录入ReturnScreen表数据")
    @RequestMapping(value = "/modules/posuopei/submitOutstandingReport/save")
    public R insertReturnScreen(SubmitOutstandingReportEntity submitOutstandingReportEntity){
        submitOutstandingReportService.insertSubmitOutstandingReport(submitOutstandingReportEntity);
        return R.ok();
    }
    }




