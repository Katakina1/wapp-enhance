package com.xforceplus.wapp.modules.collect.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.collect.entity.CollectListStatistic;
import com.xforceplus.wapp.modules.collect.export.InvoiceCollectionLisExcel;
import com.xforceplus.wapp.modules.collect.service.AbnormalInvoiceCollectionService;
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

import static com.xforceplus.wapp.modules.Constant.EXPORT_ABNORMAL_INVOICE_COLLECTION;
import static com.xforceplus.wapp.modules.Constant.SHORT_DATE_FORMAT;
import static com.xforceplus.wapp.modules.collect.WebUriMappingConstant.URI_ABNORMAL_INVOICE_COLLECTION;
import static com.xforceplus.wapp.modules.collect.WebUriMappingConstant.URI_ABNORMAL_INVOICE_COLLECTION_EXPORT;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 异常发票采集控制层
 * @author Colin.hu
 * @date 4/11/2018
 */
@RestController
public class AbnormalInvoiceCollectionController extends AbstractController {

    private final static Logger LOGGER = getLogger(AbnormalInvoiceCollectionController.class);

    private final AbnormalInvoiceCollectionService abnormalInvoiceCollectionService;

    @Autowired
    public AbnormalInvoiceCollectionController(AbnormalInvoiceCollectionService abnormalInvoiceCollectionService) {
        this.abnormalInvoiceCollectionService = abnormalInvoiceCollectionService;
    }

    /**
     * 获取异常发票采集页面数据
     * @param params 查询条件
     * @return 异常发票采集页面数据集
     */
    @PostMapping(value = URI_ABNORMAL_INVOICE_COLLECTION)
    public R selectAbnormalInvoiceCollection(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<CollectListStatistic> infoPagedQueryResult = abnormalInvoiceCollectionService.selectAbnormalInvoiceCollection(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage(),infoPagedQueryResult.getSummationTotalAmount(), infoPagedQueryResult.getSummationTaxAmount());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 导出列表
     * @param params 查询参数
     */
    @SysLog("导出异常发票采集列表")
    @AuthIgnore
    @GetMapping(value = URI_ABNORMAL_INVOICE_COLLECTION_EXPORT)
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());

        final Map<String, List<CollectListStatistic>> map = newHashMapWithExpectedSize(1);
        map.put("abnormalInvoiceCollectionList", abnormalInvoiceCollectionService.selectAbnormalInvoiceCollection(params).getResults());
        //生成excel
        final InvoiceCollectionLisExcel excelView = new InvoiceCollectionLisExcel(map, EXPORT_ABNORMAL_INVOICE_COLLECTION, "abnormalInvoiceCollectionList");
        final String excelName = now().toString(SHORT_DATE_FORMAT);
        excelView.write(response, "abnormalInvoiceCollectionList" + excelName);
    }
}
