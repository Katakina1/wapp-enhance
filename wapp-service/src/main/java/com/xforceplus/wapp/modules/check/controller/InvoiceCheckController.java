package com.xforceplus.wapp.modules.check.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.certification.entity.ImportCertificationEntity;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckModel;
import com.xforceplus.wapp.modules.check.export.CheckTemplate;
import com.xforceplus.wapp.modules.check.export.InvoiceCheckHistoryExport;
import com.xforceplus.wapp.modules.check.service.InvoiceCheckModulesService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.INVOICE_TYPE;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.RESPONSE_CODE_AUTH_ERROR;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.RESPONSE_CODE_EXIST;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.RESPONSE_CODE_INNER_ERROR;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.RESPONSE_CODE_REMOTE_SERVER_ERROR;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.RESPONSE_CODE_SUCCESS;
import static com.xforceplus.wapp.modules.check.WebUriMappingConstant.URI_INVOICE_CHECK_DELETE;
import static com.xforceplus.wapp.modules.check.WebUriMappingConstant.URI_INVOICE_CHECK_MODULES_HISTORY_DELETE;
import static com.xforceplus.wapp.modules.check.WebUriMappingConstant.URI_INVOICE_CHECK_MODULES_HISTORY_DETAIL;
import static com.xforceplus.wapp.modules.check.WebUriMappingConstant.URI_INVOICE_CHECK_MODULES_HISTORY_EXPORT;
import static com.xforceplus.wapp.modules.check.WebUriMappingConstant.URI_INVOICE_CHECK_MODULES_HISTORY_LIST;
import static com.xforceplus.wapp.modules.check.WebUriMappingConstant.URI_INVOICE_CHECK_MODULES_INVOICE_CHECK;
import static com.xforceplus.wapp.modules.check.WebUriMappingConstant.URI_INVOICE_CHECK_MODULES_INVOICE_HAND_CHECK;
import static com.xforceplus.wapp.modules.check.WebUriMappingConstant.URI_INVOICE_CHECK_MODULES_STATISTICS;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static java.lang.String.valueOf;
import static org.joda.time.DateTime.now;
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
        return R.ok().put("result", pageUtil).put("totalAmount", resultList.getTotalAmount()).put("totalTax", resultList.getTotalTax());
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
        final String excelName = now().toString("yyyyMMdd");
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


    @SysLog("导出查验模板")
    @AuthIgnore
    @GetMapping("export/checkTemplate")
    public void exportTemplate(HttpServletResponse response) {
        LOGGER.info("导出查验模板");

        //生成excel
        final CheckTemplate excelView = new CheckTemplate();
        excelView.write(response, "checkTemplate");
    }

    @SysLog("导入发票信息")
    @PostMapping("export/checkImport")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入查验，文件开始导入");

        final String schemaLabel = getCurrentUserSchemaLabel();

        return invoiceCheckModulesService.importEnjoySubsided(schemaLabel,getUserId(),multipartFile);
    }

    /**
     * 发票查验
     */
    @SysLog("导入查验")
    @PostMapping("check/doImportCheck")
    public R getImportCheck(@RequestParam Map<String, Object> params) {
        LOGGER.info("导入查验,params {}", params);
        final List<Object> resultList = Lists.newArrayList();

        final String schemaLabel = getCurrentUserSchemaLabel();
        final String jsonParam = (String)params.get("jsonParam");
        final List<ImportCertificationEntity> entityList = new Gson().fromJson(jsonParam, new TypeToken<List<ImportCertificationEntity>>(){}.getType());
        for(ImportCertificationEntity entity : entityList){
            Map<String, Object> invoiceParam = Maps.newHashMap();
            invoiceParam.put("invoiceCode",entity.getInvoiceCode());
            invoiceParam.put("invoiceNo",entity.getInvoiceNo());
            invoiceParam.put("invoiceDate",entity.getInvoiceDate());
            if (INVOICE_TYPE.contains(CommonUtil.getFplx(entity.getInvoiceCode()))) {
                invoiceParam.put("invoiceAmount",entity.getCheckCode());
            } else {
                invoiceParam.put("invoiceAmount",entity.getInvoiceAmount());
            }
            Map<String, Object> result = invoiceCheckModulesService.doInvoiceCheck(schemaLabel, invoiceParam, getUser().getLoginname());
            if(RESPONSE_CODE_SUCCESS.equals(result.get("RCode"))){
                entity.setCheckMassege((String)result.get("msg"));
            }else if(RESPONSE_CODE_EXIST.equals(result.get("RCode"))){
                entity.setCheckMassege("该发票已查验成功，请勿重复查验");
            }else if(RESPONSE_CODE_INNER_ERROR.equals(result.get("RCode"))){
                entity.setCheckMassege("服务器内部错误，请稍后重试");
            }else if(RESPONSE_CODE_REMOTE_SERVER_ERROR.equals(result.get("RCode"))){
                entity.setCheckMassege("无效的发票信息，请核对后重新发起查验");
            }else if(RESPONSE_CODE_AUTH_ERROR.equals(result.get("RCode"))){
                entity.setCheckMassege("该发票已被他人查验，请勿重复查验");
            }

            if(null==result.get("data")){
                resultList.add(entity);
            }else{
                resultList.add(result.get("data"));
            }
        }
        return R.ok().put("success",Boolean.TRUE).put("result",resultList);
    }
}
