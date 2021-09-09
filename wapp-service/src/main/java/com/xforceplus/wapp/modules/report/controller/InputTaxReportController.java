package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import com.xforceplus.wapp.modules.report.service.InputTaxReportService;
import com.xforceplus.wapp.modules.report.service.InvoiceAuthDailyReportService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.joda.time.DateTime.*;

/**
 * 进项税额报表
 */
@RestController
@RequestMapping("/report/inputTaxReport")
public class InputTaxReportController extends AbstractController{



    @Autowired
    private InputTaxReportService inputTaxReportService;

    @Autowired
    private ComprehensiveInvoiceQueryService comprehensiveInvoiceQueryService;

    @Autowired
    private InvoiceAuthDailyReportService invoiceAuthDailyReportService;

    /**
     * 查询页面所需的数据
     * @param params
     * @return
     */
    @SysLog("进项发票数据")
    @RequestMapping("/index")
    public R index(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID,用于查询对应税号
        params.put("userID", getUserId());
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

        Double totalAmount = inputTaxReportService.getTotalAmount(schemaLabel,params);
        Double totalTax = inputTaxReportService.getTotalTax(schemaLabel,params);
        Double totalOutTax = inputTaxReportService.getTotalOutTax(schemaLabel,params);
        List<DailyReportEntity> outTaxDetailList = inputTaxReportService.getOutTaxDetail(schemaLabel,params);

        return R.ok().put("totalAmount", totalAmount)
                .put("totalTax", totalTax)
                .put("totalOutTax", totalOutTax)
                .put("outTaxDetailList", outTaxDetailList)
                .put("gfName", params.get("gfName"))
                .put("rzhBelongDate",params.get("rzhBelongDate"))
                .put("defaultRzhBelongDate", currentTaxPeriod)
                .put("optionList", optionList);
    }
}
