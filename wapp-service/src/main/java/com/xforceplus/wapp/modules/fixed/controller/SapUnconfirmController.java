package com.xforceplus.wapp.modules.fixed.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.fixed.service.SapUnconfirmService;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;


@RestController
public class SapUnconfirmController extends AbstractController {
    private static final Logger LOGGER = getLogger(SapUnconfirmController.class);
    @Autowired
    private SapUnconfirmService sapUnconfirmService;


    @SysLog("sap待处理查询")
    @RequestMapping("modules/fixed/sapUnconfirm/list")
    public R getQuestionnaireList(@RequestParam Map<String,Object> params ){
        Query query =new Query(params);
        Integer result = sapUnconfirmService.sapCount(query);
        List<InvoiceImportAndExportEntity> list=sapUnconfirmService.sapList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("SAP匹配成功")
    @RequestMapping("modules/fixed/sapUnconfirm/sapSuccess")
    public R sapSuccess(@RequestParam("invoiceId") Long invoiceId) {
        boolean result = sapUnconfirmService.sapSuccess(invoiceId);
        if(result) {
            return R.ok();
        } else {
            return R.error("SAP匹配成功修改失败!");
        }
    }

    @SysLog("退票")
    @RequestMapping("modules/fixed/sapUnconfirm/refund")
    public R check( @RequestBody Map<String,Object> param) {
        sapUnconfirmService.refund(param);
        return R.ok().put("msg","0");
    }
}
