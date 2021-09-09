package com.xforceplus.wapp.modules.redTicket.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.certification.export.CertificationTemplateExport;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.service.EntryRedTicketService;
import com.xforceplus.wapp.modules.redTicket.service.PrintCoverService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.collect.Maps;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.*;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by 1 on 2018/10/26 17:13
 */
@RestController
public class PrintCoverController extends AbstractController {
    private final PrintCoverService printCoverService;
    private static final Logger LOGGER = getLogger(PrintCoverController.class);


    @Autowired
    public PrintCoverController(PrintCoverService printCoverService) {
        this.printCoverService = printCoverService;
    }

    @RequestMapping(URI_INVOICE_OPEN_RED_QUERY_PRINT_COVER)
    @SysLog("红票查询列表")
    public R list(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        if(params.get("userCode").equals("")){
            params.put("userCode",getUser().getUsercode());
        }

        Query query = new Query(params);
        Integer result = printCoverService.selectRedTicketListCount(query);
        List<RedTicketMatch> list = printCoverService.selectRedTicketList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);

    }




    /**
     * 导出数据-
     * @param
     * @return
     */
    @RequestMapping(value = "/export/redTicket/printCover" )
    public void resultListExport(@RequestParam("ids")String ids ,@RequestParam("userCode")String userCode , HttpServletResponse response) {
        LOGGER.info("查询条件为:{}", ids);
        JSONArray arr = JSONArray.fromObject(ids);
        Map<String, Object> params = new HashMap<>();
        List<RedTicketMatch> list = new ArrayList<RedTicketMatch>();
        for (int i = 0; i < arr.size(); i++) {

            long id = Long.valueOf(String.valueOf(arr.get(i))).longValue();
            RedTicketMatch redTicketMatch = printCoverService.getRedTicketMatch(id);
            if(redTicketMatch.getTaxRate()!=null){
                String s=redTicketMatch.getTaxRate().toString();
                if(StringUtils.isNotEmpty(s)){
                    s= s.substring(0,s.indexOf('.'))+"%";
                    redTicketMatch.setTaxRateOne(s);
                }
            }


            redTicketMatch.setIndexNo(i+1);
            if(userCode.equals("null") ){
                params.put("user", getUser());
            }else {
                //通过供应商号 查供应商名称
                UserEntity u1 = printCoverService.getUserName(redTicketMatch.getVenderid());
                getUser().setUsername(u1.getUsername());
                params.put("user", getUser());

            }
            list.add(redTicketMatch);
        }
        params.put("list", list);
        params.put("currentDate", now().toString("yyyy-MM-dd"));

        try {
            printCoverService.exportRedTicketPdf(params, response);
        } catch (Exception e) {
            LOGGER.error("导出PDF出错:" + e);
        }

    }
}
