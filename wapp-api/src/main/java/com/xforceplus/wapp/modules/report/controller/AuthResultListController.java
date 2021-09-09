package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.AuthResultListService;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import com.xforceplus.wapp.modules.report.service.InvoiceAuthDailyReportService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 认证结果清单
 */
@RestController
@RequestMapping("/report/authResultList")
public class AuthResultListController extends AbstractController {

    @Autowired
    private AuthResultListService authResultListService;

    @Autowired
    private InvoiceAuthDailyReportService invoiceAuthDailyReportService;

    @Autowired
    private ComprehensiveInvoiceQueryService comprehensiveInvoiceQueryService;

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("认证结果清单列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();

        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        query.put("userID", getUserId());

        //数据列表
        List<ComprehensiveInvoiceQueryEntity> list = authResultListService.queryList(schemaLabel,query);
        ReportStatisticsEntity result = authResultListService.queryTotalResult(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil)
                .put("totalAmount", result.getTotalAmount())
                .put("totalTax", result.getTotalTax());
    }
}
