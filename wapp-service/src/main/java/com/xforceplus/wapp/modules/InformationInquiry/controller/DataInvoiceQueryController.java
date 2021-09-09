package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.MatchEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.DataInvoiceQueryService;

import com.xforceplus.wapp.modules.report.entity.GfOptionEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.InformationInquiry.constant.DataInvoiceQuery.INFORMATIONINQUIRY_MATCH_QUERY_LIST;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * 匹配查询
 */
@RestController
public class DataInvoiceQueryController extends AbstractController {
    private static final Logger LOGGER = getLogger(DataInvoiceQueryController.class);
    @Autowired
    private DataInvoiceQueryService dataInvoiceQueryService;

    /**
     * 点击结果明细的查询
     * @param param
     * @return
     */
    @SysLog("数据发票匹配查询")
    @RequestMapping("modules/InformationInquiry/matchQuerys/list")
    public R matchQuery(@RequestParam Map<String,Object> param){
        LOGGER.info("数据发票匹配查询,param {}",param);

        Query query=new Query(param);

        Integer result = dataInvoiceQueryService.matchlistCount(query);
        List<MatchEntity> list=dataInvoiceQueryService.matchlist(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
    @RequestMapping("modules/InformationInquiry/matchQuerys/searchGf")
    public R searchGf() {
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<GfOptionEntity> optionList = dataInvoiceQueryService.searchGf();
        return R.ok().put("optionList", optionList);
    }

}
