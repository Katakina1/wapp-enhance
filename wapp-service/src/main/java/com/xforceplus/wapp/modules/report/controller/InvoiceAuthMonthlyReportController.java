package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import com.xforceplus.wapp.modules.report.service.InvoiceAuthMonthlyReportService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 发票认证月报
 */
@RestController
@RequestMapping("/report/invoiceAuthMonthlyReport")
public class InvoiceAuthMonthlyReportController extends AbstractController {



    @Autowired
    private InvoiceAuthMonthlyReportService invoiceAuthMonthlyReportService;

    @Autowired
    private ComprehensiveInvoiceQueryService comprehensiveInvoiceQueryService;

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("发票认证月报")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID,用于查询对应税号
        params.put("userID", getUserId());
        //获取相应税号
        List<OptionEntity> optionList = comprehensiveInvoiceQueryService.searchGf(schemaLabel,getUserId());

        //页面初始化的时候
        if(StringUtils.isEmpty(params.get("gfName").toString())) {

            //默认税号,名称
            String defaultTaxNo = null;
            if (!optionList.isEmpty()) {
                defaultTaxNo = optionList.get(0).getValue();
            }

            //设置条件
            params.put("gfName", defaultTaxNo);
        }

        //月报数据列表
        List<DailyReportEntity> reportList = invoiceAuthMonthlyReportService.getList(schemaLabel,params);

        return R.ok().put("reportList", invoiceAuthMonthlyReportService.fixList(reportList, params.get("rzhBelongDate").toString().substring(0,4)))
                .put("optionList", optionList)
                .put("gfName", params.get("gfName"));
    }
}
