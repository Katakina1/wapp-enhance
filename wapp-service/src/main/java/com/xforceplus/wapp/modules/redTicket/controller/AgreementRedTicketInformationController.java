package com.xforceplus.wapp.modules.redTicket.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolInvoiceDetailEntity;
import com.xforceplus.wapp.modules.protocol.service.ProtocolService;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.ProtocolDetailEntity;
import com.xforceplus.wapp.modules.redTicket.entity.ProtocolEntity;
import com.xforceplus.wapp.modules.redTicket.service.AgreementRedTicketInformationService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.SimpleFormatter;

import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.URI_INVOICE_LIST;
import static org.slf4j.LoggerFactory.getLogger;
@RestController
@RequestMapping("/redTicket/agreementRedTicketInformation")
public class AgreementRedTicketInformationController extends AbstractController {
    private static final Logger LOGGER = getLogger(AgreementRedTicketInformationController.class);
    @Autowired
    private AgreementRedTicketInformationService agreementRedTicketInformationService;


    @SysLog("协议列表查询")
    @RequestMapping("/protocollist")
    public R protocollist(@RequestParam Map<String, Object> params) {
        LOGGER.info("协议列表查询,param{}",params);
        Query query =new Query(params);
        query.put("userCode",getUser().getUsercode());
        query.remove("offset");
        Integer result=agreementRedTicketInformationService.protocolCount(query);
        List<ProtocolEntity> list=agreementRedTicketInformationService.protocollist(query);
        List<ProtocolEntity> newList=new ArrayList<ProtocolEntity>();
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        ProtocolInvoiceDetailEntity pde=null;
        for (ProtocolEntity pe:list ) {
            String formatStr2 =formatter2.format(pe.getCaseDate());
            List<ProtocolInvoiceDetailEntity> ls=agreementRedTicketInformationService.queryInvoiceDetailList(formatStr2,pe.getProtocolNo());
            if(ls.size()>0){
                   newList.add(pe);
          }
       }
        PageUtils pageUtil = new PageUtils(newList, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("协议明细列表查询")
    @RequestMapping("/protocoldetaillist")
    public R protocoldetaillist(@RequestParam Map<String, Object> params) {
        LOGGER.info("协议明细列表查询,param{}",params);
        Query query =new Query(params);
        query.remove("offset");
        List<ProtocolDetailEntity> list=agreementRedTicketInformationService.protocoldetaillist(query);
        PageUtils pageUtil = new PageUtils(list, list.size(), query.getLimit(), query.getPage());
        return R.ok().put("page1", pageUtil);
    }

    @SysLog("可红冲发票查询列表协议版")
    @RequestMapping("/invoicelist")
    public R invoicelist(@RequestParam Map<String, Object> params) {
        LOGGER.info("可红冲发票信息查询协议版,param{}",params);
        Query query =new Query(params);
        query.put("userID",getUser().getUserid());
        Integer result=agreementRedTicketInformationService.getInvoiceCount(query);
        List<InvoiceEntity> list=agreementRedTicketInformationService.getInvoicelist(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page3", pageUtil);
    }
}
