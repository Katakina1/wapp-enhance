package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryExcel1Entity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.SignForQueryInquiryExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.SignForQueryChargeService;
import com.xforceplus.wapp.modules.InformationInquiry.service.SignForQueryService;
import com.xforceplus.wapp.modules.cost.entity.SettlementExcelEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
public class SignForQueryChargeController extends AbstractController {
    private static final Logger LOGGER = getLogger(SignForQueryChargeController.class);
    @Autowired
    private SignForQueryChargeService signForQueryChargeService;

    /**
     * 发票签收查询
     * @param param
     * @return
     */
    @SysLog("发票签收查询")
    @RequestMapping("modules/InformationInquiry/signForQuery/matchQuerys/lists")
    public R matchQuerys(@RequestParam Map<String,Object> param){
        LOGGER.info("发票签收查询,param {}",param);

        Query query=new Query(param);

        Integer result = signForQueryChargeService.invoiceMatchCount(query);
        List<SignForQueryEntity> list=signForQueryChargeService.queryList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("export/InformationInquiry/signForQuery/signForQueryExports")
    public void signForQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        List<SignForQueryExcel1Entity> list = signForQueryChargeService.queryListAll(params);
        try {
            ExcelUtil.writeExcel(response,list,"发票扫描报告","sheet1", ExcelTypeEnum.XLSX,SignForQueryExcel1Entity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("signForQuery", list);
//        //生成excel
//        final SignForQueryInquiryExcel excelView = new SignForQueryInquiryExcel(map, "export/InformationInquiry/signForQuery.xlsx", "signForQuery");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "signForQuery" + excelNameSuffix);
    }

}
