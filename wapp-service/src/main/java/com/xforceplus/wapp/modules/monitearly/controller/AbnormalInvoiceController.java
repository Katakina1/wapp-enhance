package com.xforceplus.wapp.modules.monitearly.controller;


import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.monitearly.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.monitearly.service.AbnormalInvoiceCopService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 异常发票预警控制器
 * Created by alfred.zong on 2018/04/13.
 */
@RestController
@RequestMapping("monit/abnormalinvoice")
public class AbnormalInvoiceController extends AbstractController {

    private static final  Logger LOGGER = getLogger(AbnormalInvoiceController.class);

    private final AbnormalInvoiceCopService abnormalInvoiceCopService;

    @Autowired
    public AbnormalInvoiceController(AbnormalInvoiceCopService abnormalInvoiceCopService){
        this.abnormalInvoiceCopService=abnormalInvoiceCopService;
    }

    @SysLog("异常发票预警")
    @RequestMapping("/searchlist")
    public R queryAbnormalInvoice(@RequestParam Map<String,Object> params){
        //分库标识
        final String schemaLabel=getCurrentUserSchemaLabel();
        params.put("schemaLabel",schemaLabel);
        //获取当前用户的ID
        params.put("userId", getUserId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("查询条件为:{}", params);
        }

        // 查询列表数据
        final Query query = new Query(params);
        final PagedQueryResult<RecordInvoiceEntity> abnormalResultlist=abnormalInvoiceCopService.queryAbnormalInvoice(query);

        // 分页
        final PageUtils pageUtil=new PageUtils(abnormalResultlist.getResults(),abnormalResultlist.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("page",pageUtil).put("totalAmount", abnormalResultlist.getTotalAmount()).put("totalTax", abnormalResultlist.getTotalTax());
    }
}