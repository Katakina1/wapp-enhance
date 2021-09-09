package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimEntity;
import com.xforceplus.wapp.modules.report.entity.GfOptionEntity;
import com.xforceplus.wapp.modules.report.entity.MatchEntity;
import com.xforceplus.wapp.modules.report.service.InvoiceProcessingStatusReportService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.InformationInquiry.constant.InformationInquiry.INFORMATIONINQUIRY_CLAIMLIST_QUERY;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票处理状态报告
 */
@RestController
@RequestMapping("/report/invoiceProcessingStatusReport")
public class InvoiceProcessingStatusReportController extends AbstractController {

    private static final Logger LOGGER = getLogger(InvoiceProcessingStatusReportController.class);
    @Autowired
    private InvoiceProcessingStatusReportService invoiceProcessingStatusReportService;

    /**
     * 发票匹配处理状态报告查询
     */
    @SysLog("发票匹配处理状态报告查询")
    @RequestMapping("/list")
    public R getReturnGoodsList(@RequestParam Map<String,Object> params ){
        LOGGER.info("发票匹配处理状态报告查询,param{}",params);
        Query query =new Query(params);

        Integer result = invoiceProcessingStatusReportService.matchlistCount(query);
        List<MatchEntity> list=invoiceProcessingStatusReportService.matchlist(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/searchGf")
    public R searchGf() {
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<GfOptionEntity> optionList = invoiceProcessingStatusReportService.searchGf();
        return R.ok().put("optionList", optionList);
    }
}
