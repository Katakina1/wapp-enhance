package com.xforceplus.wapp.modules.certification.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.service.SubmitCheckService;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
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
 * 勾选发票确认
 * @author kevin.wang
 * @date 4/14/2018
 */
@RestController
@RequestMapping("certification/submitCheck")
public class SubmitCheckController extends AbstractController {

    private SubmitCheckService submitCheckService;

    private static final Logger LOGGER = getLogger(SubmitCheckController.class);

    @Autowired
    private ComprehensiveInvoiceQueryService comprehensiveInvoiceQueryService;
    
    @Autowired
    public SubmitCheckController(SubmitCheckService submitCheckService) {

        this.submitCheckService = submitCheckService;
    }
    
    @RequestMapping("/list")
    @SysLog("发票认证-勾选发票确认")
    public R list(@RequestParam Map<String, Object> params) {

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        Query query = new Query(params);
        List<InvoiceCertificationEntity> list = submitCheckService.queryList(schemaLabel,query);
        List<OptionEntity> optionList = comprehensiveInvoiceQueryService.searchGf(schemaLabel,getUserId());
        ReportStatisticsEntity result = submitCheckService.queryTotal(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax()).put("optionList", optionList);
    }

    @RequestMapping("/cancel")
    @SysLog("发票认证-勾选发票确认-撤销")
    public Boolean cancelCheck(@RequestParam(value = "ids") String ids) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        LOGGER.info("撤销数据id为:{}", ids);
        return submitCheckService.cancelCheck(schemaLabel,ids);
    }

    @RequestMapping("/submit")
    @SysLog("发票认证-勾选发票确认-提交")
    public Boolean submitCheck(@RequestParam(value = "ids") String ids) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        LOGGER.info("提交数据id为:{}", ids);
        return submitCheckService.submitCheck(schemaLabel,ids,getLoginName(),getUserName());
    }

}
