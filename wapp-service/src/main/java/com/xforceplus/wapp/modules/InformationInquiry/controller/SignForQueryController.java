package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;

import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.PaymentInvoiceUploadExcel;
import com.xforceplus.wapp.modules.InformationInquiry.export.SignForQueryInquiryExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.SignForQueryService;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceExcelEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
public class SignForQueryController extends AbstractController {
    private static final Logger LOGGER = getLogger(SignForQueryController.class);
    @Autowired
    private SignForQueryService signForQueryService;

    /**
     * 发票签收查询
     * @param param
     * @return
     */
    @SysLog("发票签收查询")
    @RequestMapping("modules/InformationInquiry/signForQuery/matchQuerys/list")
    public R matchQuerys(@RequestParam Map<String,Object> param){
        LOGGER.info("发票签收查询,param {}",param);

        Query query=new Query(param);

        Integer result = signForQueryService.invoiceMatchCount(query);
        List<SignForQueryEntity> list=signForQueryService.queryList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("export/InformationInquiry/signForQuery/signForQueryExport")
    public void signForQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        List<SignForQueryEntity> list = signForQueryService.queryListAll(params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("signForQuery", list);
        List<SignForQueryExcelEntity> list2=signForQueryService.transformExcle(list);
        try {

            ExcelUtil.writeExcel(response,list2,"扫描处理导出","sheet1", ExcelTypeEnum.XLSX,SignForQueryExcelEntity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }




//        //生成excel
//        final SignForQueryInquiryExcel excelView = new SignForQueryInquiryExcel(map, "export/InformationInquiry/signForQuery.xlsx", "signForQuery");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "signForQuery" + excelNameSuffix);
    }

}
