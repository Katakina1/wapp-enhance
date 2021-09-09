package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/report/comprehensiveInvoiceQuery")
public class ComprehensiveInvoiceQueryController extends AbstractController {



    @Autowired
    private ComprehensiveInvoiceQueryService comprehensiveInvoiceQueryService;

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("发票综合查询列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        query.put("userID", getUserId());
        List<ComprehensiveInvoiceQueryEntity> list = comprehensiveInvoiceQueryService.queryList(schemaLabel,query);
        ReportStatisticsEntity result = comprehensiveInvoiceQueryService.queryTotalResult(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }

    @RequestMapping("/searchGf")
    public R searchGf() {
        final String schemaLabel = getCurrentUserSchemaLabel();

        List<OptionEntity> optionList = comprehensiveInvoiceQueryService.searchGf(schemaLabel,getUserId());

        return R.ok().put("optionList", optionList);
    }

    @RequestMapping("/searchXf")
    public R searchXf(@RequestParam String queryString) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        Map<String, Object> params = new HashMap<>();
        params.put("queryString", queryString);
        //当前登录人ID,用于查询对应税号
        params.put("userID", getUserId());
        List<String> list = comprehensiveInvoiceQueryService.searchXf(schemaLabel,params);

        return R.ok().put("list", list);
    }
}
