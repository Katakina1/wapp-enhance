package com.xforceplus.wapp.modules.redTicket.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsEntity;
import com.xforceplus.wapp.modules.redTicket.entity.GenerateRedRush;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceDetail;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatchDetail;
import com.xforceplus.wapp.modules.redTicket.service.GenerateRedTicketInformationService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.URI_GENERATE_REDTICKET_DATA;
import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.URI_INVOICE_LIST;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class GenerateRedTicketInformationController extends AbstractController {
    private GenerateRedTicketInformationService generateRedTicketInformationService;
    private static final Logger LOGGER = getLogger(GenerateRedTicketInformationController.class);
    @Autowired
    public GenerateRedTicketInformationController(GenerateRedTicketInformationService generateRedTicketInformationService){ this.generateRedTicketInformationService=generateRedTicketInformationService; }

    @RequestMapping("gfOrg/list/query")
    @SysLog("查询机构代码")
    public R generateRedTicketData(@RequestBody OrganizationEntity organizationEntity) {
        LOGGER.info("查询机构代码,param{}",organizationEntity);
        OrganizationEntity list=generateRedTicketInformationService.queryGfCode(organizationEntity.getTaxno());
        return R.ok().put("list", list);
    }


    @PostMapping(value = URI_INVOICE_LIST)
    @SysLog("可红冲发票查询列表")
    public R list(@RequestParam Map<String, Object> params) {
        LOGGER.info("可红冲发票信息查询,param{}",params);
        Query query =new Query(params);
        query.put("userID",getUser().getUserid());
        query.remove("offset");
        List<InvoiceEntity> list=generateRedTicketInformationService.getInvoicelist(query);
        PageUtils pageUtil = new PageUtils(list, list.size(), query.getLimit(), query.getPage());
        return R.ok().put("page3", pageUtil);
    }

    @RequestMapping("generateRedTicketData/list/insert")
    @SysLog("生成红票数据")
    public R generateRedTicketData(@RequestBody GenerateRedRush generateRedRush) {
        LOGGER.info("生成红票数据,param{}",generateRedRush);
        String message="红冲成功";
        try {
            generateRedTicketInformationService.generateRedTicketData(generateRedRush,getUser().getUserid(),getUser().getLoginname(),getUser().getUsercode());
        }catch (Exception e){
            return R.ok().put("message", e.getMessage());
        }
        return R.ok().put("message", message);
    }

    @RequestMapping("redTicket/GenerateRedTicketInformation/returnquery")
    @SysLog("查询退货信息")
    public R returngoodsData(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询退货信息,param{}",params);
        Query query =new Query(params);
        query.put("userCode",getUser().getUsercode());
        query.remove("offset");
        List<ReturngoodsEntity> list =generateRedTicketInformationService.getReturnGoodsList(query);
        PageUtils pageUtil = new PageUtils(list, list.size(), query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }


}
