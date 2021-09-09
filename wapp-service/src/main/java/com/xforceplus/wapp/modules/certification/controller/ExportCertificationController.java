package com.xforceplus.wapp.modules.certification.controller;

import com.xforceplus.wapp.modules.certification.entity.EnterpriseTaxInformationEntity;
import com.xforceplus.wapp.modules.certification.export.EnterpriseTaxInformationExcel;
import com.xforceplus.wapp.modules.certification.service.EnterpriseTaxInformationService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 企业税务信息导出专用controller
 */
@RestController
public class ExportCertificationController extends AbstractController {

    private static final Logger LOGGER = getLogger(ExportCertificationController.class);

    @Autowired
    EnterpriseTaxInformationService enterpriseTaxInformationService;

    /**
     * 导出数据
     * @param params
     * @return
     */
    @RequestMapping("/export/enterpriseTaxInformationExport")
    public void enterpriseTaxInformationExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID,用于查询对应税号
        params.put("userId", getUserId());

        LOGGER.info("查询条件为:{}", params);

        //查询列表数据
        List<EnterpriseTaxInformationEntity> list = enterpriseTaxInformationService.queryListAll(schemaLabel,params);

        final Map<String, List<EnterpriseTaxInformationEntity>> map = newHashMapWithExpectedSize(1);
        map.put("enterpriseTaxInformationQueryList", list);
        //生成excel
        final EnterpriseTaxInformationExcel excelView = new EnterpriseTaxInformationExcel(map, "export/certification/enterpriseTaxInformationQueryList.xlsx", "enterpriseTaxInformationQueryList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "enterpriseTaxInformationQueryList" + excelNameSuffix);
    }




}
