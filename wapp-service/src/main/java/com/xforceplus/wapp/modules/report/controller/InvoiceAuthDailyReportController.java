package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import com.xforceplus.wapp.modules.report.service.InvoiceAuthDailyReportService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.joda.time.DateTime.now;

/**
 * 发票认证日报
 */
@RestController
@RequestMapping("/report/invoiceAuthDailyReport")
public class InvoiceAuthDailyReportController  extends AbstractController {



    @Autowired
    private InvoiceAuthDailyReportService invoiceAuthDailyReportService;

    @Autowired
    private ComprehensiveInvoiceQueryService comprehensiveInvoiceQueryService;

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("发票认证日报")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID,用于查询对应税号
        params.put("userID", getUserId());
        //获取相应税号
        List<OptionEntity> optionList = comprehensiveInvoiceQueryService.searchGf(schemaLabel,getUserId());

        //定义默认当前税款所属期
        String currentTaxPeriod = null;
        //页面初始化的时候
        if(StringUtils.isEmpty(params.get("gfName").toString())) {

            //默认税号,名称
            String defaultTaxNo = null;
            if (!optionList.isEmpty()) {
                defaultTaxNo = optionList.get(0).getValue();
            }

            //获取默认税号的当前税款所属期
            currentTaxPeriod = invoiceAuthDailyReportService.getCurrentTaxPeriod(schemaLabel,defaultTaxNo);
            if(currentTaxPeriod==null || currentTaxPeriod==""){
                currentTaxPeriod = now().toString("yyyyMM");
            }

            //设置条件
            params.put("gfName", defaultTaxNo);
            params.put("rzhBelongDate", currentTaxPeriod);
        }

        //日报数据列表
        List<DailyReportEntity> reportList = invoiceAuthDailyReportService.getList(schemaLabel,params);

        return R.ok().put("reportList", reportList)
                .put("optionList", optionList)
                .put("gfName", params.get("gfName"))
                .put("rzhBelongDate", params.get("rzhBelongDate"))
                .put("defaultRzhBelongDate", currentTaxPeriod);
    }

    /**
     * 获取纳税人的当前税款所属期
     * @param taxNo
     * @return
     */
    @SysLog("查询纳税人当前税款所属期")
    @RequestMapping("/currentTaxPeriod")
    public String getCurrentTaxPeriod(@RequestParam String taxNo) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        String currentTaxPeriod =  invoiceAuthDailyReportService.getCurrentTaxPeriod(schemaLabel,taxNo);
        if(currentTaxPeriod==null || currentTaxPeriod==""){
            currentTaxPeriod = now().toString("yyyyMM");
        }
        return currentTaxPeriod;
    }
}
