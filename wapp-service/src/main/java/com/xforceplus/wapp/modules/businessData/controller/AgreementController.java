package com.xforceplus.wapp.modules.businessData.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.businessData.entity.AgreementEntity;
import com.xforceplus.wapp.modules.businessData.service.AgreementService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.businessData.constant.BusinessDataConstant.BUSINESSDATA_AGREEMENTBy_QUERY;
import static com.xforceplus.wapp.modules.businessData.constant.BusinessDataConstant.BUSINESSDATA_AGREEMENT_QUERY;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Adil.Xu
 */
@RestController
public class AgreementController  extends AbstractController {
    private static final Logger LOGGER = getLogger(AgreementController.class);
    private AgreementService agreementService;
    @Autowired
    public AgreementController(AgreementService agreementService){
        this.agreementService=agreementService;
    }

    /**
     * 查询协议信息
     */
    @SysLog("协议信息查询")
    @PostMapping(value = BUSINESSDATA_AGREEMENT_QUERY)
    public R agreementList(@RequestParam Map<String,Object> params){
        LOGGER.info("协议信息查询,param{}",params);
        Query query =new Query(params);
        query.put("userCode",getUser().getUsercode());
        Integer result = agreementService.agreementQueryCount(query);
        List<AgreementEntity> list = agreementService.getAgreementList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 查询未红冲协议信息
     */
    @SysLog("协议信息查询")
    @PostMapping(value = BUSINESSDATA_AGREEMENTBy_QUERY)
    public R agreementListBy(@RequestParam Map<String,Object> params){
        LOGGER.info("协议信息查询,param{}",params);
        Query query =new Query(params);
        query.put("userCode",getUser().getUsercode());
        Integer result = agreementService.agreementQueryRedCount(query);
        List<AgreementEntity> list = agreementService.getAgreementListBy(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page2", pageUtil);
    }
}
