package com.xforceplus.wapp.modules.monitearly.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.modules.monitearly.educe.DefinitiveInfoExcel;
import com.xforceplus.wapp.modules.monitearly.educe.ExpireInvoiceInfoExcel;
import com.xforceplus.wapp.modules.monitearly.educe.RecordInvoiceInfoExcel;
import com.xforceplus.wapp.modules.monitearly.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.monitearly.service.AbnormalInvoiceCopService;
import com.xforceplus.wapp.modules.monitearly.service.DefinitiveStrideYearCopService;
import com.xforceplus.wapp.modules.monitearly.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 用于数据导出控制层
 * Created by alfred.zong on 2018/04/18.
 */
@RestController
@RequestMapping("/monit")
public class InvoiceInfoExcle extends AbstractController {

    private final static Logger LOGGER = getLogger(InvoiceInfoExcle.class);

    private final static String LONG_DATE_FORMAT = "yyyyMMddHHmmss";

    @Autowired
    private AbnormalInvoiceCopService abnormalInvoiceCopService;

    @Autowired
    private DefinitiveStrideYearCopService definitiveStrideYearCopService;

    @Autowired
    private RecordInvoiceService recordInvoiceService;

    @SysLog("异常发票-导出")
    @RequestMapping("/toexcel")
    public void abnormalInvoiceInfoExcle(@RequestParam Map<String, Object> params, HttpServletResponse response){
        //获取当前时间来对文件进行取名
        final String dateNowstr = DateTime.now().toString(LONG_DATE_FORMAT);

        //分库标识
        final String schemaLabel=getCurrentUserSchemaLabel();
        //schemaLabel：分库分表所需要的参数
        params.put("schemaLabel",schemaLabel);
        //getUserId()是为了获取当前用户的ID
        params.put("userId",getUserId());

        //根据条件查询发票列表
        final List<RecordInvoiceEntity> recordlist = abnormalInvoiceCopService.queryAbnormalInvoicelist(params);
        final Map<String, List<RecordInvoiceEntity>> viewMap = newHashMap();
        viewMap.put("abnormInvoiceEntityList", recordlist);

        final RecordInvoiceInfoExcel excel = new RecordInvoiceInfoExcel(viewMap);
        excel.write(response, "abnormal" + dateNowstr);
    }

    @SysLog("逾期预警-导出")
    @RequestMapping("/exportExcel")
    public void expireExcle(@RequestParam Map<String, Object> params, HttpServletResponse response){
        //获取当前时间来对文件进行取名
        final String dateNowstr = DateTime.now().toString(LONG_DATE_FORMAT);

        //分库标识
        final String schemaLabel=getCurrentUserSchemaLabel();
        //schemaLabel：分库分表所需要的参数
        params.put("schemaLabel",schemaLabel);
        //getUserId()是为了获取当前用户的ID
        params.put("userId",getUserId());

        //根据条件查询发票列表
        final List<RecordInvoiceEntity> recordlist = recordInvoiceService.queryInvoiceToExcel(params);
        final Map<String, List<RecordInvoiceEntity>> viewMap = newHashMap();
        viewMap.put("expireInvoiceEntityList", recordlist);

        final ExpireInvoiceInfoExcel excel = new ExpireInvoiceInfoExcel(viewMap);
        excel.write(response, "expire" + dateNowstr);
    }

    @SysLog("普票跨年度预警-导出")
    @RequestMapping("/excelOut")
    public void definitiveStrideYearCopExcle(@RequestParam Map<String, Object> params,HttpServletResponse response){
        //获取当前时间来对文件进行取名
        final String dateNowstr = DateTime.now().toString(LONG_DATE_FORMAT);

        //分库
        final String schemaLabel=getCurrentUserSchemaLabel();
        //schemaLabel：分库分表所需要的参数
        params.put("schemaLabel",schemaLabel);
        // 添加当前用户的ID
        params.put("userId",getUserId());

        //根据条件查询
        final List<RecordInvoiceEntity> definitiveInfoList = definitiveStrideYearCopService.queryDefinitiveInfoList(params);
        final Map<String, List<RecordInvoiceEntity>> viewMap = newHashMap();

        viewMap.put("definitInvoiceEntityList",definitiveInfoList);
        final DefinitiveInfoExcel excel = new DefinitiveInfoExcel(viewMap);
        excel.write(response, "definitive" + dateNowstr);
    }
}
