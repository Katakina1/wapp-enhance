package com.xforceplus.wapp.modules.certification.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.service.ManualCheckService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
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
 * 手工勾选
 * @author kevin.wang
 * @date 4/14/2018
 */
@RestController
@RequestMapping("certification/manualCheck")
public class ManualCheckController extends AbstractController {

    private ManualCheckService manualCheckService;
    
    private static final Logger LOGGER = getLogger(ManualCheckController.class);

    @Autowired
    public ManualCheckController(ManualCheckService manualCheckService) {

        this.manualCheckService = manualCheckService;
    }
    
    /**
     * 手工勾选
     * 分页数据
     */
    @RequestMapping("/list")
    @SysLog("发票认证-手动勾选")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        Query query = new Query(params);
        List<InvoiceCertificationEntity> list = manualCheckService.queryList(schemaLabel,query);

        ReportStatisticsEntity result = manualCheckService.queryTotal(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }

    /**
     * 手工勾选处理
     */
    @RequestMapping("/submit")
    @SysLog("发票认证-手动勾选提交")
    public Boolean manualCheck(@RequestParam(value = "ids") String ids) {

        final String schemaLabel = getCurrentUserSchemaLabel();
        return manualCheckService.manualCheck(schemaLabel,ids,getLoginName(),getUserName());
    }

}
