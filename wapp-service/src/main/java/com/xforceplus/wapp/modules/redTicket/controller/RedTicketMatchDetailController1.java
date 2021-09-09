package com.xforceplus.wapp.modules.redTicket.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatchDetail;
import com.xforceplus.wapp.modules.redTicket.service.RedTicketMatchDetailService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/invoiceOut/details")
public class RedTicketMatchDetailController1 extends AbstractController {
    private RedTicketMatchDetailService redTicketMatchDetailService;
    private static final Logger LOGGER = getLogger(RedTicketMatchDetailController1.class);
    @Autowired
    public RedTicketMatchDetailController1(RedTicketMatchDetailService redTicketMatchDetailService){ this.redTicketMatchDetailService=redTicketMatchDetailService; }

    @RequestMapping("/redRushInvoiceDetails")
    @SysLog("红冲发票明细")
    public R listUpdateDetails(@RequestBody RedTicketMatchDetail params) {

        LOGGER.info("红冲发票明细,param{}",params);
        Integer userId= getUser().getUserid();
        Map<String, Object> list1=redTicketMatchDetailService.updateInvoiceDetaillist(params,getUser().getUserid());
        List<RedTicketMatchDetail> list2=(List<RedTicketMatchDetail>)list1.get("list");
        PageUtils pageUtil = new PageUtils(list2, list2.size(), 12, 1);
        R r= R.ok();
        r.put("page6", pageUtil);
        return r;
    }
}
