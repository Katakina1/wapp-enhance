package com.xforceplus.wapp.modules.collect.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.collect.entity.CollectListStatistic;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.export.InvoiceCollectionLisExcel;
import com.xforceplus.wapp.modules.collect.service.InvoiceCollectionService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.EXPORT_INVOICE_COLLECTION;
import static com.xforceplus.wapp.modules.Constant.SHORT_DATE_FORMAT;
import static com.xforceplus.wapp.modules.collect.WebUriMappingConstant.*;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票采集列表控制层
 *
 * @author Colin.hu
 * @date 4/11/2018
 */
@RestController
public class InvoiceCollectionListController extends AbstractController {

    private final static Logger LOGGER = getLogger(InvoiceCollectionListController.class);

    private final InvoiceCollectionService invoiceCollectionService;

    @Autowired
    public InvoiceCollectionListController(InvoiceCollectionService invoiceCollectionService) {
        this.invoiceCollectionService = invoiceCollectionService;
    }

    /**
     * 获取采集发票列表页面数据
     *
     * @param params 查询条件
     * @return 采集发票列表页面数据集
     */
    @PostMapping(value = URI_INVOICE_COLLECTION_LIST)
    public R selectInvoiceCollection(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<CollectListStatistic> infoPagedQueryResult = invoiceCollectionService.selectInvoiceCollection(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage(), infoPagedQueryResult.getSummationTotalAmount(), infoPagedQueryResult.getSummationTaxAmount());
        //返回结果
        return R.ok().put("page", pageUtil);
    }

    /**
     * 获取购方名称，购方税号集
     *
     * @return 返回给页面的json
     */
    @PostMapping(value = URI_INVOICE_COLLECTION_TAX_NAME)
    public List<Map<String, String>> getGfNameByUserId() {
        final String schemaLabel = getCurrentUserSchemaLabel();
        return invoiceCollectionService.getGfNameByUserId(schemaLabel, getUserId());
    }

    /**
     * 根据税号查询税号下的发票信息
     *
     * @param params 参数
     * @return 发票
     */
    @PostMapping(value = URI_INVOICE_COLLECTION_INVOICE_INFO)
    public R getInvoiceInfo(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //查询列表数据
        final Query query = new Query(params);

        //根据税号查询发票信息
        final PagedQueryResult<InvoiceCollectionInfo> infoPagedQueryResult = invoiceCollectionService.getInvoiceInfo(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage());
        //返回值
        return R.ok().put("page", pageUtil);
    }

    /**
     * 导出列表
     *
     * @param params 查询参数
     */
    @SysLog("导出发票采集列表")
    @AuthIgnore
    @GetMapping(value = URI_INVOICE_COLLECTION_LIST_EXPORT)
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());

        final Map<String, List<CollectListStatistic>> map = newHashMapWithExpectedSize(1);
        map.put("invoiceCollectionList", invoiceCollectionService.selectInvoiceCollection(params).getResults());
        //生成excel
        final InvoiceCollectionLisExcel excelView = new InvoiceCollectionLisExcel(map, EXPORT_INVOICE_COLLECTION, "invoiceCollectionList");
        final String excelName = now().toString(SHORT_DATE_FORMAT);
        excelView.write(response, "invoiceCollectionList" + excelName);
    }
}
