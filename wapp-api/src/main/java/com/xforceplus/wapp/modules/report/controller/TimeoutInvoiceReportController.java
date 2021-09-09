package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import com.xforceplus.wapp.modules.report.service.TimeoutInvoiceReportService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 逾期发票报表
 */
@RestController
@RequestMapping("/report/timeoutInvoiceReport")
public class TimeoutInvoiceReportController extends AbstractController {



    @Autowired
    private TimeoutInvoiceReportService timeoutInvoiceReportService;

    @Autowired
    private ComprehensiveInvoiceQueryService comprehensiveInvoiceQueryService;

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("逾期发票列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        query.put("userID", getUserId());
        List<ComprehensiveInvoiceQueryEntity> list = timeoutInvoiceReportService.queryList(schemaLabel,query);
        ReportStatisticsEntity result = timeoutInvoiceReportService.queryTotalResult(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }

}

