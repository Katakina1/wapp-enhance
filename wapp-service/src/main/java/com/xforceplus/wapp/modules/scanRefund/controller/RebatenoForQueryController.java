package com.xforceplus.wapp.modules.scanRefund.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.export.RebatenoForQueryInquiryExcel;
import com.xforceplus.wapp.modules.scanRefund.service.RebatenoForQueryService;
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
public class RebatenoForQueryController extends AbstractController {
    private static final Logger LOGGER = getLogger(RebatenoForQueryController.class);
    @Autowired
    private RebatenoForQueryService rebatenoForQueryService;

    /**
     * 发票签收查询
     * @param param
     * @return
     */
    @SysLog("发票退票查询")
    @RequestMapping("modules/scanRefund/groupRefund/list")
    public R matchQuerys(@RequestParam Map<String,Object> param){
        LOGGER.info("发票退票查询,param {}",param);

        Query query=new Query(param);

        Integer result = rebatenoForQueryService.invoiceMatchCount(query);
        List<RebatenoForQueryEntity> list=rebatenoForQueryService.queryList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("export/scanRefund/rebateForQueryExport")
    public void rebateForQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        List<RebatenoForQueryExcelEntity> list = rebatenoForQueryService.queryListAll(params);
        try {
            ExcelUtil.writeExcel(response,list,"发票退票查询","sheet1", ExcelTypeEnum.XLSX,RebatenoForQueryExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("rebateForQuery", list);
//        //生成excel
//        final RebatenoForQueryInquiryExcel excelView = new RebatenoForQueryInquiryExcel(map, "export/scanRefund/rebateForQuery.xlsx", "rebateForQuery");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "rebateForQuery" + excelNameSuffix);
    }

}
