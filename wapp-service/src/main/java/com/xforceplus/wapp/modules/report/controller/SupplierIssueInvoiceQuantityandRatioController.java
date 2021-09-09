package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.MatchEntity;
import com.xforceplus.wapp.modules.report.entity.QuestionInvoiceQuantityAndRatioEntity;
import com.xforceplus.wapp.modules.report.service.SupplierIssueInvoiceQuantityandRatioService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 供应商问题发票数量及比率
 */
@RestController
@RequestMapping("/report/supplierIssueInvoiceQuantityandRatio")
public class SupplierIssueInvoiceQuantityandRatioController extends AbstractController {
    private static final Logger LOGGER = getLogger(SupplierIssueInvoiceQuantityandRatioController.class);
    @Autowired
    private SupplierIssueInvoiceQuantityandRatioService supplierIssueInvoiceQuantityandRatioService;

    /**
     * 发票匹配处理状态报告查询
     */
    @SysLog("发票匹配处理状态报告查询")
    @RequestMapping("/list")
    public R getReturnGoodsList(@RequestParam Map<String,Object> params ){
        LOGGER.info("发票匹配处理状态报告查询,param{}",params);
        Query query =new Query(params);
        List<QuestionInvoiceQuantityAndRatioEntity> list=supplierIssueInvoiceQuantityandRatioService.problemInvoice(query);
        int limit=query.getLimit();
        query.remove("limit");
        query.remove("offset");
        List<QuestionInvoiceQuantityAndRatioEntity> list1=supplierIssueInvoiceQuantityandRatioService.problemInvoice(query);
        PageUtils pageUtil = new PageUtils(list,list1.size(), limit, query.getPage());
        return R.ok().put("page", pageUtil);
    }
}
