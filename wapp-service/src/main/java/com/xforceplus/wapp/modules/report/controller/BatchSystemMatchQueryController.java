package com.xforceplus.wapp.modules.report.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.BatchSystemMatchQueryEntity;
import com.xforceplus.wapp.modules.report.entity.GfOptionEntity;
import com.xforceplus.wapp.modules.report.entity.MatchEntity;
import com.xforceplus.wapp.modules.report.service.BatchSystemMatchQueryService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票处理状态报告
 */
@RestController
public class BatchSystemMatchQueryController extends AbstractController {

    private static final Logger LOGGER = getLogger(BatchSystemMatchQueryController.class);
    @Autowired
    private BatchSystemMatchQueryService batchSystemMatchQuery;

    /**
     * 发票匹配处理状态报告查询
     */
    @SysLog("批量系统匹配报告查询")
    @RequestMapping("modules/report/batchSystemMatchQuery/list")
    public R getReturnGoodsList(@RequestParam Map<String,Object> params ){
        LOGGER.info("批量系统匹配报告查询,param{}",params);
        Query query =new Query(params);
        Integer result = batchSystemMatchQuery.matchlistCounts(query);
        List<BatchSystemMatchQueryEntity> list=batchSystemMatchQuery.matchlists(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("modules/report/batchSystemMatchQuery/searchGf")
    public R searchGf() {
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<GfOptionEntity> optionList = batchSystemMatchQuery.searchGf();
        return R.ok().put("optionList", optionList);
    }
}
