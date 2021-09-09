package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.ConfigConstant;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.export.AuthenticationQueryExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.AuthenticationQueryService;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.base.entity.AribaBillTypeEntity;
import com.xforceplus.wapp.modules.base.entity.JVStoreEntity;
import com.xforceplus.wapp.modules.base.export.JVStoreTemplateExport;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationExcelEntity;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionTaxExcelInfo;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.endsWithIgnoreCase;
import static org.springframework.util.StringUtils.trimAllWhitespace;


/**
 * 索赔查询
 */
@RestController
public class AuthenticationQueryController extends AbstractController {
    private static final Logger LOGGER = getLogger(AuthenticationResultQueryController.class);
    private final AuthenticationQueryService authenticationQueryService;
    @Autowired
    public AuthenticationQueryController(AuthenticationQueryService authenticationQueryService) {
        this.authenticationQueryService = authenticationQueryService;
    }

    @PostMapping(value = "modules/authenticationQuery/list")
    public R queryAuthenticationResult(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        //params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<InvoiceCollectionInfo> infoPagedQueryResult = authenticationQueryService.queryCertification(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage());
        //返回结果
        return R.ok().put("page", pageUtil).put("totalAmount", infoPagedQueryResult.getTotalAmount()).put("totalTax", infoPagedQueryResult.getTotalTax());
    }

    @PostMapping(value = "modules/authenticationQuery/aribaList")
    public R queryAuthenticationAribaResult(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        //params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<InvoiceCollectionInfo> infoPagedQueryResult = authenticationQueryService.queryAribaCertification(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage());
        //返回结果
        return R.ok().put("page", pageUtil).put("totalAmount", infoPagedQueryResult.getTotalAmount()).put("totalTax", infoPagedQueryResult.getTotalTax());
    }



    /**
     * 导出列表
     * @param params 查询参数
     */
    @SysLog("税务传票清单查询导出")
    @AuthIgnore
    @GetMapping(value = "export/authenticationQuery")
    public void export12(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());
        final Query query = new Query(params);
        query.remove("limit");
        query.remove("offset");
        final Map<String, List<InvoiceCollectionInfo>> map = newHashMapWithExpectedSize(1);
        List<InvoiceCollectionTaxExcelInfo> list = authenticationQueryService.queryCertificationForExcel(query);

        try {
            ExcelUtil.writeExcel(response,list,"税务传票清单导出","sheet1", ExcelTypeEnum.XLSX,InvoiceCollectionTaxExcelInfo.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        map.put("authenticationQuery", authenticationQueryService.queryCertification(query).getResults());
//        //生成excel
//        final AuthenticationQueryExcel excelView = new AuthenticationQueryExcel(map, "export/InformationInquiry/authenticationQuery.xlsx", "authenticationQuery");
//        final String excelName = now().toString("yyyyMMdd");
//        excelView.write(response, "authenticationQuery" + excelName);
    }



}
