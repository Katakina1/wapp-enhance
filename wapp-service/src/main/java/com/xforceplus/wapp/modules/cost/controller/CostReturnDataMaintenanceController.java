package com.xforceplus.wapp.modules.cost.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.QuestionnaireService;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.certification.export.CertificationTemplateExport;
import com.xforceplus.wapp.modules.cost.entity.ApplicantEntity;
import com.xforceplus.wapp.modules.cost.service.CostReturnDataMaintenanceService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * 订单查询
 */
@RestController
public class CostReturnDataMaintenanceController extends AbstractController {
    private static final Logger LOGGER = getLogger(CostReturnDataMaintenanceController.class);
    @Autowired
    private CostReturnDataMaintenanceService costReturnDataMaintenanceService;


    /**
     * 订单查询
     */
    @SysLog("申请人信息查询")
    @RequestMapping("modules/cost/costReturnDataMaintenance/list")
    public R getQuestionnaireList(@RequestParam Map<String,Object> params ){
        LOGGER.info("订单信息查询,param{}",params);
        Query query =new Query(params);
        Integer result = costReturnDataMaintenanceService.questionnairelistCount(query);
        List<ApplicantEntity> list=costReturnDataMaintenanceService.questionnairelist(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("导出问题单模板")
    @AuthIgnore
    @GetMapping("export/cost/questionnaireExport")
    public void exportTemplate(HttpServletResponse response) {
        LOGGER.info("导出问题单模板");
        //生成excel
        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/cost/applicantImport.xlsx");
        excelView.write(response, "applicantImport");
    }


    @SysLog("导入问题信息")
    @PostMapping("modules/cost/invoiceImport")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("文件开始导入");
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(5);
        return costReturnDataMaintenanceService.importInvoice(params,multipartFile);
    }

    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("export/cost/questionnaire/questionnaireExport")
    public void signForQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        List<QuestionnaireEntity> list = costReturnDataMaintenanceService.questionnairelistAll(params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("questionnaire", list);
        //生成excel
        List<QuestionnaireExcelEntity> list2=costReturnDataMaintenanceService.transformExcle(list);
        try {

            ExcelUtil.writeExcel(response,list2,"HOST匹配失败报告导出","sheet1", ExcelTypeEnum.XLSX,QuestionnaireExcelEntity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }

//
//        final QuestionnaireExcel excelView = new QuestionnaireExcel(map, "export/InformationInquiry/questionnaire.xlsx", "questionnaire");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "questionnaire" + excelNameSuffix);
    }



    @SysLog("确定是否退票")
    @RequestMapping("modules/cost/questionnaire/refundyesnos")
    public R refundyesnobyId(@RequestBody ApplicantEntity applicantEntity) {
        int a = applicantEntity.getIds().length;
        for (int i = 0; i < a; i++) {
           costReturnDataMaintenanceService.queryuuid(applicantEntity.getIds()[i]);
        }
        return R.ok();
    }

    @SysLog("确定是否处理")
    @RequestMapping("modules/cost/questionnaire/xrefundyesnos")
    public R refundyesnobyIds(@RequestBody QuestionnaireEntity questionnaireEntity) {
        int a = questionnaireEntity.getIds().length;
        for (int i = 0; i < a; i++) {
            long ii = questionnaireEntity.getIds()[i];
            costReturnDataMaintenanceService.xqueryuuids(questionnaireEntity.getIds()[i]);
        }
        return R.ok();
    }







}
