package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.export.AgreeInvoiceQueryExcel;
import com.xforceplus.wapp.modules.InformationInquiry.export.AuthenticationQueryExcel;
import com.xforceplus.wapp.modules.InformationInquiry.export.AuthenticationResultQueryExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.AgreeInvoiceQueryService;
import com.xforceplus.wapp.modules.InformationInquiry.service.AuthenticationResultQueryService;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationExcelEntity;
import com.xforceplus.wapp.modules.certification.export.CertificationQueryExport;
import com.xforceplus.wapp.modules.certification.service.CertificationQueryService;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionExcelInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionResultExcelInfo;
import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.InformationInquiry.constant.AgreeInvoiceQuery.INFORMATIONINQUIRY_MATCH_QUERY_LIST;
import static com.xforceplus.wapp.modules.certification.WebUriMappingConstant.URI_INVOICE_CERTIFICATION_EXPORT;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * 索赔查询
 */
@RestController
public class AuthenticationResultQueryController extends AbstractController {
    private static final Logger LOGGER = getLogger(AuthenticationResultQueryController.class);
    private final AuthenticationResultQueryService authenticationResultQueryService;
    @Autowired
    public AuthenticationResultQueryController(AuthenticationResultQueryService authenticationResultQueryService) {
        this.authenticationResultQueryService = authenticationResultQueryService;
    }

    @PostMapping(value = "modules/authenticationResultQuery/list")
    public R queryAuthenticationResult(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        //params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<InvoiceCollectionInfo> infoPagedQueryResult = authenticationResultQueryService.queryCertification(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage());
        //返回结果
        return R.ok().put("page", pageUtil).put("totalAmount", infoPagedQueryResult.getTotalAmount()).put("totalTax", infoPagedQueryResult.getTotalTax());
    }


    /**
     * 导出列表
     * @param params 查询参数
     */
    @SysLog("发票认证报告导出")
    @AuthIgnore
    @GetMapping(value = "export/authenticationResultQuery")
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);
        params.remove("limit");

        //获取当前用户的userId
        params.put("userId", getUserId());
        //final Query query = new Query(params);
//        final Map<String, List<InvoiceCollectionInfo>> map = newHashMapWithExpectedSize(1);
//        map.put("authenticationResultQuery", authenticationResultQueryService.queryCertification(params).getResults());
        List<InvoiceCollectionResultExcelInfo> list = authenticationResultQueryService.queryCertificationForExport(params);
        try {
            ExcelUtil.writeExcel(response,list,"发票认证报告导出","sheet1", ExcelTypeEnum.XLSX,InvoiceCollectionResultExcelInfo.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
        //生成excel
//        final AuthenticationResultQueryExcel excelView = new AuthenticationResultQueryExcel(map, "export/InformationInquiry/authenticationResultQuery.xlsx", "authenticationResultQuery");
//        final String excelName = now().toString("yyyyMMdd");
//        excelView.write(response, "authenticationResultQuery" + excelName);
    }




}
