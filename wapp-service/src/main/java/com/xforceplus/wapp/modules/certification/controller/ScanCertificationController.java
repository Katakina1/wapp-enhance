package com.xforceplus.wapp.modules.certification.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.service.ScanCertificationService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 扫码认证
 * @author kevin.wang
 * @date 4/16/2018
 */
@RestController
@RequestMapping("certification/scanCertification")
public class ScanCertificationController extends AbstractController {

    private ScanCertificationService scanCertificationService;

    private static final Logger LOGGER = getLogger(ScanCertificationController.class);

    @Autowired
    public ScanCertificationController(ScanCertificationService scanCertificationService) {

        this.scanCertificationService = scanCertificationService;
    }

    @SysLog("发票认证-扫码认证-扫码操作")
    @RequestMapping(value="/selectCheck")
    public R selectCheck(@RequestParam Map<String, Object> params) {

        final String schemaLabel = getCurrentUserSchemaLabel();

        params.put("userId",getUserId());

        LOGGER.info("查询条件为:{}", params);
        
        InvoiceCertificationEntity entity=scanCertificationService.selectCheck(schemaLabel,params);

        if(null==entity){

            return R.error(1,"没有数据！");

        }
        
        return R.ok().put("entity",entity);
    }

    @SysLog("发票认证-扫码认证确认")
    @RequestMapping("/submit")
    public Boolean scanCertification(@RequestParam(value = "ids") String ids) {
        
        final String schemaLabel = getCurrentUserSchemaLabel();
        
        LOGGER.info("勾选处理的id:{}", ids);
        
        return scanCertificationService.scanCertification(schemaLabel,ids,getLoginName(),getUserName());
    }
}
