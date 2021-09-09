package com.xforceplus.wapp.modules.certification.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationExcelEntity;
import com.xforceplus.wapp.modules.certification.export.ManualCertificationQueryExport;
import com.xforceplus.wapp.modules.certification.service.ManualCertificationService;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryExcelEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.joda.time.DateTime.now;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 手工认证
 * @author kevin.wang
 * @date 4/14/2018
 */
@RestController
public class ManualCertificationController extends AbstractController {

    private ManualCertificationService manualCertificationService;

    private static final Logger LOGGER = getLogger(ManualCertificationController.class);

    @Autowired
    public ManualCertificationController(ManualCertificationService manualCertificationService) {

        this.manualCertificationService = manualCertificationService;
    }
    
    @RequestMapping("certification/manualCertification/list")
    @SysLog("发票认证-手工认证")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        
        //查询列表数据
        Query query = new Query(params);
        
        List<InvoiceCertificationEntity> userList = manualCertificationService.queryList(schemaLabel,query);

        ReportStatisticsEntity result = manualCertificationService.queryTotal(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(userList, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }

    @RequestMapping("certification/manualCertification/submit")
    @SysLog("发票认证-手工认证提交")
    public R manualCertification(@RequestParam(value="ids") String ids) {
        long userId = getUserId();
        final String schemaLabel = getCurrentUserSchemaLabel();
        LOGGER.info("提交处理的id:{}", ids);
        return R.ok(manualCertificationService.manualCertification(schemaLabel,ids,getLoginName(),getUserName(),userId));
    }

    /**
     * 导出列表
     * @param params 查询参数
     */
    @SysLog("手工认证导出")
    @AuthIgnore
    @GetMapping(value = "export/manualCertificationData")
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);
        final Query query = new Query(params);
         Map<String, List<InvoiceCertificationEntity>> map = new HashMap<>();
       List<InvoiceCertificationExcelEntity> excelList =  manualCertificationService.queryExportList(schemaLabel,query);
        try {
            ExcelUtil.writeExcel(response,excelList,"手工认证查询导出","sheet1", ExcelTypeEnum.XLSX,InvoiceCertificationExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        //生成excel
//        final ManualCertificationQueryExport excelView = new ManualCertificationQueryExport(map, "export/certification/manualCertificationData.xlsx", "manualCertificationData");
//         String excelName = now().toString("yyyyMMdd");
//        excelView.write(response, "manualCertificationData" + excelName);
    }


}
