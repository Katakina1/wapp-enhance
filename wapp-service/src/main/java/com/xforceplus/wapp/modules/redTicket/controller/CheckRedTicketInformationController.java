package com.xforceplus.wapp.modules.redTicket.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.redTicket.entity.*;
import com.xforceplus.wapp.modules.redTicket.service.CheckRedTicketInformationGService;
import com.xforceplus.wapp.modules.redTicket.service.QueryOpenRedTicketDataService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.*;
import static org.slf4j.LoggerFactory.getLogger;
@RestController
public class CheckRedTicketInformationController extends AbstractController {
    private QueryOpenRedTicketDataService queryOpenRedTicketDataService;
    private CheckRedTicketInformationGService checkRedTicketInformationGService;
    private static final Logger LOGGER = getLogger(CheckRedTicketInformationController.class);

    @Autowired
    public CheckRedTicketInformationController(QueryOpenRedTicketDataService queryOpenRedTicketDataService,CheckRedTicketInformationGService checkRedTicketInformationGService) {
        this.queryOpenRedTicketDataService = queryOpenRedTicketDataService;
        this.checkRedTicketInformationGService=checkRedTicketInformationGService;
    }

    @RequestMapping(URI_OPEN_RED_TICKET_SUPPLIER_LIST)
    @SysLog("开红票查询列表（供）")
    public R list(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        Query query = new Query(params);
        query.put("userCode",getUser().getUsercode());
        Integer result=checkRedTicketInformationGService.getRedTicketMatchListCount(query);
        List<RedTicketMatch> list = checkRedTicketInformationGService.queryOpenRedTicket(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);

    }

    @RequestMapping(URI_CANCEL_RED_RUSH_INFORMATION)
    @SysLog("取消红票资料")
    public R generateRedTicketData(@RequestParam Map<String, Object> params) {
        LOGGER.info("取消红票资料,param{}",params);
        params.put("userCode",getUser().getUsercode());
        String message=checkRedTicketInformationGService.cancelRedRushInformation(params);
        return R.ok().put("msg", message);
    }

    @RequestMapping(URI_RED_TICKET_DISCOUNT_DETAIL)
    @SysLog("折让类型红票资料查询明细列表")
    public R allListAgreement(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);

        //蓝票列表数据
        Integer resultRecord = queryOpenRedTicketDataService.getRecordInvoiceListCount(query);
        List<InvoiceEntity> recordInvoiceList = queryOpenRedTicketDataService.getRecordInvoiceList(query);
        //蓝票明细列表数据
        Integer resultRecordDetail= queryOpenRedTicketDataService.getRecordInvoiceDetailListCount(query);
        List<InvoiceDetail> recordInvoiceDetailList = queryOpenRedTicketDataService.getRecordInvoiceDetailList(query);
        for (InvoiceDetail rd:recordInvoiceDetailList){
            rd.setRedRushTaxAmount(rd.getRedRushAmount().multiply(new BigDecimal(rd.getTaxRate()).divide(new BigDecimal(100))));
        }
        //合并明细列表数据
        Integer resultMerge = queryOpenRedTicketDataService.getMergeInvoiceDetailListCount(query);
        List<RedTicketMatchDetail> mergeInvoiceDetailList = queryOpenRedTicketDataService.getMergeInvoiceDetailList(query);
        for (RedTicketMatchDetail rd:mergeInvoiceDetailList){
            rd.setRedRushTaxAmount(rd.getRedRushAmount().multiply(new BigDecimal(rd.getTaxRate()).divide(new BigDecimal(100))));
        }
        PageUtils pageUtil2 = new PageUtils(recordInvoiceList, resultRecord, query.getLimit(),query.getPage());
        PageUtils pageUtil3 = new PageUtils(recordInvoiceDetailList, resultRecordDetail, query.getLimit(),query.getPage());
        PageUtils pageUtil4 = new PageUtils(mergeInvoiceDetailList, resultMerge, query.getLimit(),query.getPage());

        return R.ok().put("page2", pageUtil2).put("page3", pageUtil3).put("page4", pageUtil4);
    }

    @SysLog("协议类型红票资料查询明细列表")
    @RequestMapping("/redTicket/checkRedTicketInformation/agreementlist")
    public R agreementList(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //协议列表数据
        Integer protocolCount=checkRedTicketInformationGService.protocolListCouont(params);
        List<ProtocolEntity> protocolEntities = checkRedTicketInformationGService.protocolList(params);
        //蓝票列表数据
        Integer recordInvoiceCount=checkRedTicketInformationGService.invoiceListCount(params);
        List<RedTicketMatchMiddle> recordInvoiceList = checkRedTicketInformationGService.invoiceList(params);
        //蓝票明细列表数据
        Integer recordInvoiceDetailCount=checkRedTicketInformationGService.invoiceDetailListCount(params);
        List<InvoiceDetail> recordInvoiceDetailList = checkRedTicketInformationGService.invoiceDetailList(params);
        for (InvoiceDetail rd:recordInvoiceDetailList){
            rd.setRedRushTaxAmount(rd.getRedRushAmount().multiply(new BigDecimal(rd.getTaxRate()).divide(new BigDecimal(100))));
        }
        //红冲明细列表数据
        Integer mergeInvoiceDetailCount=checkRedTicketInformationGService.redTicketMatchDetailListCount(params);
        List<RedTicketMatchDetail> mergeInvoiceDetailList = checkRedTicketInformationGService.redTicketMatchDetailList(params);
        //红冲税额赋值
        for (RedTicketMatchDetail rd:mergeInvoiceDetailList){
            BigDecimal amount=rd.getRedRushPrice().multiply(new BigDecimal(rd.getRedRushNumber().toString()));
            rd.setRedRushTaxAmount(amount.multiply(new BigDecimal(rd.getTaxRate()).divide(new BigDecimal(100))));
        }
        PageUtils pageUtil1 = new PageUtils(protocolEntities, protocolCount, query.getLimit(), query.getPage());
        PageUtils pageUtil2 = new PageUtils(recordInvoiceList, recordInvoiceCount, query.getLimit(),query.getPage());
        PageUtils pageUtil3 = new PageUtils(recordInvoiceDetailList, recordInvoiceDetailCount, query.getLimit(),query.getPage());
        PageUtils pageUtil4 = new PageUtils(mergeInvoiceDetailList, mergeInvoiceDetailCount, query.getLimit(),query.getPage());

        return R.ok().put("page1", pageUtil1).put("page2", pageUtil2).put("page3", pageUtil3).put("page4", pageUtil4);
    }
}
