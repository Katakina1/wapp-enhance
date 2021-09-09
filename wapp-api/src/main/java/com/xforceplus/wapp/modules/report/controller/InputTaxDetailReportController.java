package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.RateAmountEntity;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import com.xforceplus.wapp.modules.report.service.InputTaxDetailReportService;
import com.xforceplus.wapp.modules.report.service.InvoiceAuthDailyReportService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;



/**
 * 进项税额明细报表
 */
@RestController
@RequestMapping("/report/inputTaxDetailReport")
public class InputTaxDetailReportController extends AbstractController {



    @Autowired
    private InputTaxDetailReportService inputTaxDetailReportService;

    @Autowired
    private ComprehensiveInvoiceQueryService comprehensiveInvoiceQueryService;

    @Autowired
    private InvoiceAuthDailyReportService invoiceAuthDailyReportService;

    /**
     * 查询页面所需的数据
     * @param params
     * @return
     */
    @SysLog("进项发票明细数据")
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
                currentTaxPeriod = new SimpleDateFormat("yyyyMM").format(new Date());
            }

            //设置条件
            params.put("gfName", defaultTaxNo);
            params.put("rzhBelongDate", currentTaxPeriod);
        }

        Integer noneDetailCount = inputTaxDetailReportService.getNoneDetailCount(schemaLabel,params);
        List<RateAmountEntity> rateList = inputTaxDetailReportService.getRateData(schemaLabel,params);

        return R.ok().put("noneDetailCount", noneDetailCount)
                .put("rateList", rateList)
                .put("gfName", params.get("gfName"))
                .put("rzhBelongDate",params.get("rzhBelongDate"))
                .put("defaultRzhBelongDate", currentTaxPeriod)
                .put("optionList", optionList);
    }
}
