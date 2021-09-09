package com.xforceplus.wapp.modules.einvoice.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.einvoice.entity.EinvoiceQueryEntity;
import com.xforceplus.wapp.modules.einvoice.service.EinvoiceQueryService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.xforceplus.wapp.modules.einvoice.WebUriMappingConstant.ELECTRON_INVOICE_EXPORT;
import static com.xforceplus.wapp.modules.einvoice.WebUriMappingConstant.ELECTRON_INVOICE_QUERY_LIST;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Marvin
 * 电票查询控制层
 */
@RestController
public class EinvoiceQueryController extends AbstractController {

    private static final Logger LOGGER = getLogger(EinvoiceQueryController.class);

    @Autowired
    private EinvoiceQueryService einvoiceQueryService;

    /**
     * 查询列表
     *
     * @return 电票查询信息列表
     */
    @SysLog("电票查询")
    @RequestMapping(value = ELECTRON_INVOICE_QUERY_LIST, method = {POST})
    public R list(EinvoiceQueryEntity queryEntity) {
        LOGGER.debug("--------------------电票查询------------------------");
        final String schemaLabel = getCurrentUserSchemaLabel();
        queryEntity.setUserId(getUserId());
        return R.ok().put("page", einvoiceQueryService.queryInvoiceMsg(schemaLabel, queryEntity, getUserId()));
    }

    @SysLog("电票导出")
    @RequestMapping(value = ELECTRON_INVOICE_EXPORT, method = {GET})
    public void exportElectronInvoice(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.debug("--------------------电票导出------------------------");
        final String schemaLabel = getCurrentUserSchemaLabel();
        einvoiceQueryService.exportElectronInvoice(schemaLabel, params, response, getUserId());
    }
}
