package com.xforceplus.wapp.modules.collect.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.export.NoDetailedInvoiceExcel;
import com.xforceplus.wapp.modules.collect.service.NoDetailedInvoiceService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.EXPORT_NO_DETAILED;
import static com.xforceplus.wapp.modules.Constant.SHORT_DATE_FORMAT;
import static com.xforceplus.wapp.modules.collect.WebUriMappingConstant.*;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 未补明细发票控制层
 * @author Colin.hu
 * @date 4/11/2018
 */
@RestController
public class NoDetailedInvoiceController extends AbstractController {

    private final static Logger LOGGER = getLogger(NoDetailedInvoiceController.class);

    private final NoDetailedInvoiceService noDetailedInvoiceService;

    @Autowired
    public NoDetailedInvoiceController(NoDetailedInvoiceService noDetailedInvoiceService) {
        this.noDetailedInvoiceService = noDetailedInvoiceService;
    }

    /**
     * 获取采集发票列表页面数据
     * @param params 查询条件
     * @return 采集发票列表页面数据集
     */
    @PostMapping(value = URI_DETAILED_INVOICE_COLLECTION)
    public R selectInvoiceCollection(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<InvoiceCollectionInfo> infoPagedQueryResult = noDetailedInvoiceService.selectNoDetailedInvoice(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage(), infoPagedQueryResult.getSummationTotalAmount(), infoPagedQueryResult.getSummationTaxAmount());
        //返回
        return R.ok().put("page", pageUtil);
    }

    /**
     * 导出列表
     * @param params 查询参数
     */
    @SysLog("导出未补明细发票列表")
    @AuthIgnore
    @GetMapping(value = URI_DETAILED_INVOICE_COLLECTION_EXPORT)
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());

        final Map<String, List<InvoiceCollectionInfo>> map = newHashMapWithExpectedSize(1);
        map.put("noDetailedInvoiceList", noDetailedInvoiceService.selectNoDetailedInvoice(params).getResults());
        //生成excel
        final NoDetailedInvoiceExcel excelView = new NoDetailedInvoiceExcel(map, EXPORT_NO_DETAILED, "noDetailedInvoiceList");
        final String excelName = now().toString(SHORT_DATE_FORMAT);
        excelView.write(response, "noDetailedInvoiceList" + excelName);
    }

    /**
     * 手动查验
     * @param params 请求参数
     * @return 验证结果
     */
    @SysLog("手动查验")
    @PostMapping(value = URI_DETAILED_INVOICE_MANUAL_INSPECTION)
    public Map<String, String> manualInspection(@RequestParam Map<String, String> params) {
        LOGGER.info("验证参数为:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //执行业务 并返回结果
       return noDetailedInvoiceService.manualInspection(params);

    }

    /**
     * 根据类型查询数据字典表获取对应明细
     */
    @PostMapping(value = URI_PARAM_MAP_BY_TYPE)
    public List<Map<String, String>> getParamMapByType(@RequestParam Map<String, String> paramMap) {
        LOGGER.info("获取数据字典参数为:{}", paramMap);

        final String schemaLabel = getCurrentUserSchemaLabel();
        paramMap.put("schemaLabel", schemaLabel);

        return noDetailedInvoiceService.getParamMapByType(paramMap);
    }
}
