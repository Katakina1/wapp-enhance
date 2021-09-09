package com.xforceplus.wapp.modules.scanRefund.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.service.PrintRefundInformationService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.joda.time.DateTime.now;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/scanRefund/printRefundInformation")
public class PrintRefundInformationController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintRefundInformationController.class);

    @Autowired
    private PrintRefundInformationService printRefundInformationService;

    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    @SysLog("扫描退票查询列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        query.put("userID", getUserId());
        List<EnterPackageNumberEntity> list = printRefundInformationService.queryList(schemaLabel, query);
        ReportStatisticsEntity result = printRefundInformationService.queryTotalResult(schemaLabel, query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }

}
