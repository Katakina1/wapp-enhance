package com.xforceplus.wapp.modules.redTicket.controller;


import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceDetail;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatchDetail;
import com.xforceplus.wapp.modules.redTicket.service.InvoiceDetailService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.URI_CANREDRUSH_INVOICE_DETAILS_LIST;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class InvoiceDetailController extends AbstractController {
    private InvoiceDetailService invoiceDetailService;
    private static final Logger LOGGER = getLogger(InvoiceDetailController.class);
    @Autowired
    public InvoiceDetailController(InvoiceDetailService invoiceDetailService){ this.invoiceDetailService=invoiceDetailService; }


    @PostMapping(value = URI_CANREDRUSH_INVOICE_DETAILS_LIST)
    @SysLog("可红冲发票明细查询列表")
    public R list(@RequestParam Map<String, Object> params) {
        LOGGER.info("可红冲发票明细信息查询,param{}",params);
        Query query =new Query(params);
        Integer result=invoiceDetailService.invoiceDetailsCount(query);
        List<InvoiceDetail> list=invoiceDetailService.getInvoiceDetaillist(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page4", pageUtil);
    }
}
