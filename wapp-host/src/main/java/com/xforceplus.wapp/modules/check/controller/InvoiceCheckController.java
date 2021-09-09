package com.xforceplus.wapp.modules.check.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckModel;
import com.xforceplus.wapp.modules.check.export.InvoiceCheckHistoryExport;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.check.WebUriMappingConstant.*;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
//import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Bobby
 * @date 2018/4/19
 * 发票查验控制层
 */
@RestController
public class InvoiceCheckController extends AbstractController {

    private final static Logger LOGGER = getLogger(InvoiceCheckModel.class);



    /**
     * 发票查验
     */
    @SysLog("发票查验")
    @PostMapping(URI_INVOICE_CHECK_MODULES_INVOICE_HAND_CHECK)
    public R getInvoiceCheck(@RequestParam Map<String, Object> params) {
        LOGGER.info("发票查验,params {}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        return R.ok().put("result", "");
    }








}
