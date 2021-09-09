package com.xforceplus.wapp.modules.transferOut.controller;

import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.modules.transferOut.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.transferOut.export.transferOutExport;
import com.xforceplus.wapp.modules.transferOut.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/23
 * Time:11:37
*/
@RestController
@RequestMapping("/export")
public class TransferOutExportController  extends AbstractController {

    @Autowired
    private InvoiceService invoiceService;
    /**
     * 导出数据-进项税转出查询
     * @param params
     * @return
     */
    @RequestMapping("/transferOutInvoiceQueryExport")
    public void transferOutInvoiceQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID,用于查询对应税号
        if (params.get("gfTaxNo")==null||params.get("gfTaxNo").equals("")){
            params.put("userId",getUserId());
        }
        //查询列表数据
        List<InvoiceEntity> list = invoiceService.transferOutedQuery(schemaLabel,params);

        final Map<String, List<InvoiceEntity>> map = newHashMapWithExpectedSize(1);
        map.put("transferOutInvoiceQueryList", list);
        //生成excel
        final transferOutExport excelView = new transferOutExport(map, "export/transferOut/TransferOutExport.xlsx", "transferOutInvoiceQueryList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "transferOutInvoiceQueryList" + excelNameSuffix);
    }

}
