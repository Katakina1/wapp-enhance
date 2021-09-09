package com.xforceplus.wapp.modules.redTicket.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceDetail;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.service.PrintingOpenRedTicketDataService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.URI_OPEN_RED_TICKET_LIST_PRINTING;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by 1 on 2018/11/1 18:33
 */
@RestController
public class PrintingOpenRedTicketDataController extends AbstractController {
    private PrintingOpenRedTicketDataService printingOpenRedTicketDataService;
    private static final Logger LOGGER = getLogger(PrintingOpenRedTicketDataController.class);
    @Autowired
    public PrintingOpenRedTicketDataController(PrintingOpenRedTicketDataService printingOpenRedTicketDataService) {
        this.printingOpenRedTicketDataService = printingOpenRedTicketDataService;
    }

    @RequestMapping(URI_OPEN_RED_TICKET_LIST_PRINTING)
    @SysLog("打印开红票查询列表")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("userCode",getUser().getUsercode());
        LOGGER.info("查询条件为:{}", params);

        //查询列表数据
        Query query = new Query(params);
        Integer result = printingOpenRedTicketDataService.getRedTicketMatchListCount(query);
        List<RedTicketMatch> list = printingOpenRedTicketDataService.queryOpenRedTicket(query);

        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);

    }

    /**
     * 导出数据-
     * @param
     * @return
     */
    @RequestMapping(value = "/export/openRedTicket/openRedDateResultListExport" )
    public void resultListExport(@RequestParam("id")String id ,@RequestParam("businessType")String businessType ,@RequestParam("redTicketDataSerialNumber")String redTicketDataSerialNumber , HttpServletResponse response) {
        LOGGER.info("查询条件为:{}", id);
        Map <String,Object> params =new HashMap<>();
        params.put("businessType",businessType);
        params.put("id",id);
        params.put("redTicketDataSerialNumber",redTicketDataSerialNumber);

        List<InvoiceDetail> resultList = printingOpenRedTicketDataService.getList((String)redTicketDataSerialNumber);
        ReportStatisticsEntity totalData = printingOpenRedTicketDataService.queryTotalResult((String)redTicketDataSerialNumber);
        //查询pdf开始结束时间
       InvoiceEntity invoiceEntity = printingOpenRedTicketDataService.getPdfDate((String)redTicketDataSerialNumber);
       BigDecimal total=new BigDecimal(0);
       for (InvoiceDetail inv:resultList){
            inv.setRedRushTaxAmount(inv.getRedRushAmount().multiply(new BigDecimal(inv.getTaxRate()).divide(new BigDecimal(100))));
        total=total.add(inv.getRedRushTaxAmount());
       }
       totalData.setRedPushTotalTaxAmount(total.doubleValue());
        params.put("invoiceEntity",invoiceEntity);
        params.put("resultList", resultList);
        params.put("totalData", totalData);
        params.put("currentDate", now().toString("yyyy-MM-dd"));
        params.put("user",getUser());
        params.put("userCode",getUser().getUsercode());
        try {
            printingOpenRedTicketDataService.exportPdf(params, response);
        } catch (Exception e){
                LOGGER.error("导出PDF出错:"+e);
            }

    }

}
