package com.xforceplus.wapp.modules.check.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckModel;
import com.xforceplus.wapp.modules.check.export.InvoiceCheckHistoryExport;
import com.xforceplus.wapp.modules.check.service.InvoiceCheckModulesService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.check.WebUriMappingConstant.*;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
//import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Bobby
 * @date 2018/4/19
 * 发票查验控制层
 */
@RestController
public class InvoiceCheckController extends AbstractController {

    private final static Logger LOGGER = getLogger(InvoiceCheckModel.class);

    private InvoiceCheckModulesService invoiceCheckModulesService;

    public InvoiceCheckController(InvoiceCheckModulesService invoiceCheckModulesService) {
        this.invoiceCheckModulesService = invoiceCheckModulesService;
    }

    /**
     * 发票查验
     */
    @SysLog("发票查验")
    @PostMapping(URI_INVOICE_CHECK_MODULES_INVOICE_HAND_CHECK)
    public R getInvoiceCheck(@RequestParam Map<String, Object> params) {
        LOGGER.info("发票查验,params {}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        return R.ok().put("result", invoiceCheckModulesService.doInvoiceCheck(schemaLabel, params, getUser().getLoginname()));
    }

    /**
     * 查验历史列表
     */
    @SysLog("查验历史列表")
    @PostMapping(URI_INVOICE_CHECK_MODULES_HISTORY_LIST)
    public R getInvoiceCheckHistoryList(@RequestParam Map<String, Object> params) {
        LOGGER.info("发票查验列表,params {}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        //获取当前用户的userAccount
        params.put("userAccount", getLoginName());
        final Query query = new Query(params);
        PagedQueryResult<InvoiceCheckModel> resultList = invoiceCheckModulesService.getInvoiceCheckHistoryList(schemaLabel, query);
        final PageUtils pageUtil = new PageUtils(resultList.getResults(), resultList.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("result", pageUtil);
    }

    /**
     * 查验历史-查验
     */
    @SysLog("查验历史-查验")
    @PostMapping(URI_INVOICE_CHECK_MODULES_INVOICE_CHECK)
    public R doInvoiceCheckHistoryCheck(@RequestParam Map<String, Object> params) {
        LOGGER.info("发票查验,params {}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        return R.ok().put("result", invoiceCheckModulesService.doInvoiceCheck(schemaLabel, params, getUser().getLoginname()));
    }

    /**
     * 查验历史详情
     */
    @SysLog("查验历史详情")
    @PostMapping(URI_INVOICE_CHECK_MODULES_HISTORY_DETAIL)
    public R getInvoiceCheckHistoryDetail(@RequestParam Map<String, Object> params) {
        LOGGER.info("发票查验详情,params {}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        final Query query = new Query(params);
        PagedQueryResult<InvoiceCheckModel> resultList = invoiceCheckModulesService.getInvoiceCheckHistoryDetail(schemaLabel, query);
        final PageUtils pageUtil = new PageUtils(resultList.getResults(), resultList.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("result", pageUtil);
    }

    /**
     * 查验历史删除
     */
    @PostMapping(URI_INVOICE_CHECK_MODULES_HISTORY_DELETE)
    public R getInvoiceCheckHistoryDelete(@RequestParam Map<String, Object> params) {
        LOGGER.info("查验历史删除,params {}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        return R.ok().put("result", invoiceCheckModulesService.getInvoiceCheckHistoryDelete(schemaLabel, params));
    }

    /**
     * 查验历史导出
     */
    @SysLog("查验历史导出")
    @GetMapping(URI_INVOICE_CHECK_MODULES_HISTORY_EXPORT)
    public void getInvoiceCheckHistoryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("发票查验历史导出,params {}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        //获取当前用户的userAccount
        params.put("userAccount", getLoginName());
        final Map<String, List<InvoiceCheckModel>> map = newHashMapWithExpectedSize(1);
        map.put("invoiceCheckHistory", invoiceCheckModulesService.getInvoiceCheckHistoryList(schemaLabel, params).getResults());
        //生成excel
        final InvoiceCheckHistoryExport excelView = new InvoiceCheckHistoryExport(map, "export/check/invoiceCheckHistory.xlsx", "invoiceCheckHistory");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        final String excelName = formatter.format(new Date());
        excelView.write(response, "invoiceCheckHistory" + excelName);
    }

    /**
     * 查验统计
     */
    @SysLog("查验统计")
    @PostMapping(URI_INVOICE_CHECK_MODULES_STATISTICS)
    public R getInvoiceStatistics(@RequestParam Map<String, Object> params) {
        LOGGER.info("发票查验,params {}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("userAccount", getLoginName());
        final Query query = new Query(params);
        final PagedQueryResult<Map<String, Object>> resultList = invoiceCheckModulesService.getInvoiceStatistics(schemaLabel, query);
        //分页
        final PageUtils pageUtil = new PageUtils(resultList.getResults(), resultList.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("result", pageUtil);
    }

    /**
     * 发票查验删除
     * @param params
     * @return
     */
    @SysLog("发票查验删除")
    @PostMapping(URI_INVOICE_CHECK_DELETE)
    public R deleteCheckInvoice(@RequestParam Map<String, Object> params) {
        final String uuid = String.valueOf(params.get("invoiceCode")) + String.valueOf(params.get("invoiceNo"));
        final String schemaLabel = getCurrentUserSchemaLabel();
        final Boolean flag = invoiceCheckModulesService.deleteCheckInvoice(schemaLabel, uuid);
        return R.ok().put("result", flag);
    }
}
