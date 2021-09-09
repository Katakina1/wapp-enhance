package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ScanningEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.CostListService;
import com.xforceplus.wapp.modules.InformationInquiry.service.ScanningService;
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
 * 问题单查询(费用)
 */
@RestController
@RequestMapping("/InformationInquiry/costList")
public class CostListController extends AbstractController {
    private static final Logger LOGGER = getLogger(CostListController.class);
    @Autowired
    private CostListService costListService;

    @SysLog("问题单查询(费用)")
    @RequestMapping("/list")
    public R scanningList(@RequestParam Map<String,Object> params ){
        LOGGER.info("问题单查询(费用),param{}",params);
        Query query =new Query(params);
        Integer result = costListService.scanningCount(params);
        List<ScanningEntity> list=costListService.scanningList(params);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
}
