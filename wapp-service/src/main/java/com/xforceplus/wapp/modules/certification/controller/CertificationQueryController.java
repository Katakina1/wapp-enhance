package com.xforceplus.wapp.modules.certification.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.certification.export.CertificationQueryExport;
import com.xforceplus.wapp.modules.certification.service.CertificationQueryService;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionExcelInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionResultExcelInfo;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.certification.WebUriMappingConstant.URI_INVOICE_CERTIFICATION_EXPORT;
import static com.xforceplus.wapp.modules.certification.WebUriMappingConstant.URI_INVOICE_CERTIFICATION_LIST;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 认证查询控制层
 * @author Colin.hu
 * @date 4/13/2018
 */
@RestController
public class CertificationQueryController extends AbstractController {

    private static final Logger LOGGER = getLogger(CertificationQueryController.class);

    private final CertificationQueryService certificationQueryService;

    @Autowired
    public CertificationQueryController(CertificationQueryService certificationQueryService) {
        this.certificationQueryService = certificationQueryService;
    }

    @PostMapping(value = URI_INVOICE_CERTIFICATION_LIST)
    public R queryCertification(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<InvoiceCollectionInfo> infoPagedQueryResult = certificationQueryService.queryCertification(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage());
        //返回结果
        return R.ok().put("page", pageUtil).put("totalAmount", infoPagedQueryResult.getTotalAmount()).put("totalTax", infoPagedQueryResult.getTotalTax());
    }

    /**
     * 导出列表
     * @param params 查询参数
     */
    @SysLog("认证查询导出")
    @AuthIgnore
    @GetMapping(value = URI_INVOICE_CERTIFICATION_EXPORT)
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());
        final Query query = new Query(params);
        final Map<String, List<InvoiceCollectionInfo>> map = newHashMapWithExpectedSize(1);
//        map.put("certificationQueryExport", certificationQueryService.queryCertificationExport(query));
        List<InvoiceCollectionExcelInfo> list =certificationQueryService.queryCertificationExport(query);
        try {
            ExcelUtil.writeExcel(response,list,"认证查询导出","sheet1", ExcelTypeEnum.XLSX,InvoiceCollectionExcelInfo.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
        //生成excel
//        final CertificationQueryExport excelView = new CertificationQueryExport(map, "export/certification/certificationQuery.xlsx", "certificationQueryExport");
//        final String excelName = now().toString("yyyyMMdd");
//        excelView.write(response, "certificationQuery" + excelName);
    }
}
