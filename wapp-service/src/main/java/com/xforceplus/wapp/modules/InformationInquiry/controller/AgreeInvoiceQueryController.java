package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.AgreeInvoiceQueryExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.AgreeInvoiceQueryService;
import com.xforceplus.wapp.modules.InformationInquiry.service.ClaimInquiryService;
import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.xforceplus.wapp.modules.InformationInquiry.constant.AgreeInvoiceQuery.INFORMATIONINQUIRY_MATCH_QUERY_LIST;
import static com.xforceplus.wapp.modules.InformationInquiry.constant.InformationInquiry.INFORMATIONINQUIRY_CLAIMLIST_QUERY;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * 索赔查询
 */
@RestController
public class AgreeInvoiceQueryController extends AbstractController {
    private static final Logger LOGGER = getLogger(AgreeInvoiceQueryController.class);
    @Autowired
    private AgreeInvoiceQueryService agreeInvoiceQueryService;

    @Autowired
    private DetailsService detailsService;

    /**
     * 点击结果明细的查询
     * @param param
     * @return
     */
    @SysLog("匹配查询")
    @RequestMapping(INFORMATIONINQUIRY_MATCH_QUERY_LIST)
    public R matchQuery(@RequestParam Map<String,Object> param){
        LOGGER.info("param {}",param);

        Query query=new Query(param);

        PagedQueryResult<MatchEntity> pagedQueryResult=agreeInvoiceQueryService.getMatchList(query);

        PageUtils pageUtils=new PageUtils(pagedQueryResult.getResults(),pagedQueryResult.getTotalCount(),query.getLimit(),query.getPage());
        return R.ok().put("page",pageUtils);
    }


    /**
     * 导出数据查询
     * @param matchnoList
     * @return
     */
    @SysLog("po导出Excel")
    @RequestMapping("/export/invoicePoExcel/invoicePoListExcel")
    public void poInvoiceQueryExport(@RequestParam("matchnoList")String matchnoList , HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", matchnoList);
        JSONArray arr = JSONArray.fromObject(matchnoList);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        List<MatchEntity> list = new ArrayList<MatchEntity>();
        for(int i=0;i<arr.size();i++){
            String matchno = (String) arr.get(i);
            MatchEntity matchEntity=detailsService.getMatchDetail(matchno);
            matchEntity.setMatchno(matchno);
            //获取匹配日期
            MatchEntity matchEntity1 = detailsService.selectMatchEntity(matchno);
            matchEntity.setMatchDate(matchEntity1.getMatchDate());
            //添加差异金额
            matchEntity.setMatchCover(matchEntity1.getCover());
            list.add(matchEntity);
       }
        map.put("agreeInvoiceQuery",list);
        //生成excel
        final AgreeInvoiceQueryExcel excelView = new AgreeInvoiceQueryExcel(map, "export/InformationInquiry/agreeInvoiceQuery.xlsx", "agreeInvoiceQuery");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "agreeInvoiceQuery" + excelNameSuffix);
    }

}
