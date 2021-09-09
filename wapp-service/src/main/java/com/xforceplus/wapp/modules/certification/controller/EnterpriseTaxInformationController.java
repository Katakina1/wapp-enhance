package com.xforceplus.wapp.modules.certification.controller;

import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.certification.entity.EnterpriseTaxInformationEntity;
import com.xforceplus.wapp.modules.certification.service.EnterpriseTaxInformationService;
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
 * 企业税务信息
 * kevin.wang 2018.04.25
 * 
 */
@RestController
@RequestMapping("certification/enterpriseTaxInformation")
public class EnterpriseTaxInformationController extends AbstractController {

    private EnterpriseTaxInformationService enterpriseTaxInformationService;

    private static final  Logger LOGGER = getLogger(EnterpriseTaxInformationController.class);

    @Autowired
    public EnterpriseTaxInformationController(EnterpriseTaxInformationService enterpriseTaxInformationService) {

        this.enterpriseTaxInformationService = enterpriseTaxInformationService;
    }

    /**
     * 企业税务信息
     * @param params 查询条件
     * @return 企业税务页面数据集
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {

        final String schemaLabel = getCurrentUserSchemaLabel();
        
        params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        
        //查询列表数据
        Query query = new Query(params);
        List<EnterpriseTaxInformationEntity> list = enterpriseTaxInformationService.queryList(schemaLabel,query);
        int total = enterpriseTaxInformationService.queryTotal(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }



}
