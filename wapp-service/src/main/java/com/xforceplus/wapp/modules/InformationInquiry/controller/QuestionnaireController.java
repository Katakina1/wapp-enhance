package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.QuestionnaireExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.PoInquiryService;
import com.xforceplus.wapp.modules.InformationInquiry.service.QuestionnaireService;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.certification.export.CertificationTemplateExport;
import com.xforceplus.wapp.modules.posuopei.entity.MatchExcelEntity;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GroupRefundEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import static com.xforceplus.wapp.modules.InformationInquiry.constant.InformationInquiry.INFORMATIONINQUIRY_PODETAILS_QUERY;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * 订单查询
 */
@RestController
public class QuestionnaireController extends AbstractController {
    private static final Logger LOGGER = getLogger(QuestionnaireController.class);
    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private DetailsService detailsService;

    /**
     * 订单查询
     */
    @SysLog("问题单信息查询")
    @RequestMapping("modules/InformationInquiry/questionnaire/getQuestionnaireList/list")
    public R getQuestionnaireList(@RequestParam Map<String,Object> params ){
        LOGGER.info("订单信息查询,param{}",params);
        Query query =new Query(params);
        Integer result = questionnaireService.questionnairelistCount(query);
        List<QuestionnaireEntity> list=questionnaireService.questionnairelist(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
    /**
     * 订单查询
     */
    @SysLog("问题单信息查询")
    @RequestMapping("modules/InformationInquiry/questionnaire/getQuestionnaireList/cplist")
    public R getCpQuestionnaireList(@RequestParam Map<String,Object> params ){
        LOGGER.info("订单信息查询,param{}",params);
        Query query =new Query(params);
        query.put("vendorNo",getUser().getUsercode());
        Integer result = questionnaireService.questionnairelistCount(query);
        List<QuestionnaireEntity> list=questionnaireService.questionnairelist(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("导出问题单模板")
    @AuthIgnore
    @GetMapping("export/InformationInquiry/questionnaireExport")
    public void exportTemplate(HttpServletResponse response) {
        LOGGER.info("导出问题单模板");

        //生成excel
        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/InformationInquiry/questionnaire.xlsx");
        excelView.write(response, "questionnaire");
    }


    @SysLog("导入问题信息")
    @PostMapping("modules/InformationInquiry/invoiceImport")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入问题单，文件开始导入");
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(5);
        return questionnaireService.importInvoice(params,multipartFile);
    }

    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("export/InformationInquiry/questionnaire/questionnaireExport")
    public void signForQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        List<QuestionnaireEntity> list = questionnaireService.questionnairelistAll(params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("questionnaire", list);
        //生成excel
        List<QuestionnaireExcelEntity> list2=questionnaireService.transformExcle(list);
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



//    @SysLog("确定是否退票")
//    @RequestMapping("modules/InformationInquiry/questionnaire/refundyesnos")
//    public R refundyesnobyId(@RequestBody QuestionnaireEntity questionnaireEntity) {
//        int a = questionnaireEntity.getIds().length;
//        for (int i = 0; i < a; i++) {
//            long ii = questionnaireEntity.getIds()[i];
//            questionnaireService.queryuuid(questionnaireEntity.getIds()[i]);
//            questionnaireService.queryuuids(questionnaireEntity.getIds()[i]);
//
//
//            int count = queryuuid.size();
//            for(int j = 0; j<count;j++){
//                questionnaireService.inputrefundyesno(queryuuid.get(j).getUuid());
//            }
//
//        }
//        return R.ok();
//    }

    @SysLog("确定是否退票")
    @RequestMapping("modules/InformationInquiry/questionnaire/refundyesnos")
    public R refundyesnobyId(@RequestBody QuestionnaireEntity questionnaireEntity) {
        int a = questionnaireEntity.getIds().length;
        for (int i = 0; i < a; i++) {
            List<QuestionnaireEntity> queryuuid = questionnaireService.queryuuid(questionnaireEntity.getIds()[i]);
            int count = queryuuid.size();
            try {
                for(int j = 0; j<count;j++){
                    questionnaireService.inputrefundyesno(queryuuid.get(j).getInvNo(),queryuuid.get(j).getVendorNo(),queryuuid.get(j).getInvoiceDate(),queryuuid.get(j).getErrStatus(),queryuuid.get(j).getInvoiceCost());
//                    String uuid=questionnaireService.getUuId(queryuuid.get(j).getInvNo(),queryuuid.get(j).getVendorNo(),queryuuid.get(j).getInvoiceDate(),queryuuid.get(j).getInvoiceCost());
//                    String matchno=questionnaireService.queryMatchno(uuid);
//                    if(StringUtils.isNotEmpty(matchno)){
//                        questionnaireService.updateIsDel("1",matchno);
//                    }
                }
            }catch (RuntimeException e){
                LOGGER.info("匹配条件为:{}",e);
            }
            //questionnaireService.queryuuids(questionnaireEntity.getIds()[i]);

        }
        return R.ok();
    }

    @SysLog("确定是否处理")
    @RequestMapping("modules/InformationInquiry/questionnaire/xrefundyesnos")
    public R refundyesnobyIds(@RequestBody QuestionnaireEntity questionnaireEntity) {
        int a = questionnaireEntity.getIds().length;
        for (int i = 0; i < a; i++) {
            long ii = questionnaireEntity.getIds()[i];
            questionnaireService.xqueryuuids(questionnaireEntity.getIds()[i]);
            List<QuestionnaireEntity> queryuuid = questionnaireService.queryuuid(questionnaireEntity.getIds()[i]);
            questionnaireService.invoiceCl(queryuuid.get(i).getInvNo(),queryuuid.get(i).getVendorNo(),queryuuid.get(i).getInvoiceDate(),queryuuid.get(i).getErrStatus(),queryuuid.get(i).getInvoiceCost());
        }
        return R.ok();
    }

    @SysLog("撤销处理")
    @RequestMapping("modules/InformationInquiry/questionnaire/revocationids")
    public R revocationIds(@RequestBody Map<String,Object> params) {
        String ids=params.get("ids").toString();
        questionnaireService.cancelTheProcess(ids);
        return R.ok();
    }


    @SysLog("撤销退票")
    @RequestMapping("modules/InformationInquiry/questionnaire/revocationid")
    public R revocationId(@RequestBody Map<String,Object> params) {
        long ids= Long.valueOf(params.get("ids").toString());
        List<QuestionnaireEntity> queryuuid = questionnaireService.queryuuid(ids);
        int count = queryuuid.size();
        try {
            questionnaireService.cancelTheRefund(queryuuid.get(0).getInvNo(),queryuuid.get(0).getVendorNo(),queryuuid.get(0).getInvoiceDate(),queryuuid.get(0).getErrStatus(),queryuuid.get(0).getInvoiceCost());
//            String uuid=questionnaireService.getUuId(queryuuid.get(0).getInvNo(),queryuuid.get(0).getVendorNo(),queryuuid.get(0).getInvoiceDate(),queryuuid.get(0).getInvoiceCost());
//            String matchno=questionnaireService.queryMatchno(uuid);
//            if(StringUtils.isNotEmpty(matchno)){
//                questionnaireService.updateIsDel("0",matchno);
//            }
        }catch (RuntimeException e){
            LOGGER.info("匹配条件为:{}",e);
        }
        return R.ok();
    }

    @SysLog("确定是否需重新匹配")
    @RequestMapping("modules/InformationInquiry/questionnaire/xrefundyesnoss")
    public R refundyesnobyIdss(@RequestBody QuestionnaireEntity questionnaireEntity) {
        int a = questionnaireEntity.getIds().length;
        for (int i = 0; i < a; i++) {
            long ii = questionnaireEntity.getIds()[i];
            questionnaireService.xqueryuuidss(questionnaireEntity.getIds()[i]);
        }
        String batchId= questionnaireService.getBatchId(questionnaireEntity.getIds()[0]);
        detailsService.submitMatchCancel(batchId);
        return R.ok();
    }

    @SysLog("撤销需重新匹配")
    @RequestMapping("modules/InformationInquiry/questionnaire/revocationidss")
    public R revocationIdss(@RequestBody Map<String,Object> params) {
        String ids=params.get("ids").toString();
        questionnaireService.cancelTheProcesss(ids);
        return R.ok();
    }

}
