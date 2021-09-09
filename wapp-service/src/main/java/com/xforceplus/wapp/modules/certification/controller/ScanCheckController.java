package com.xforceplus.wapp.modules.certification.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.service.ScanCheckService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;


/**
 * 扫码勾选
 * @author kevin.wang
 * @date 4/16/2018
 */
@RestController
@RequestMapping("certification/scanCheck")
public class ScanCheckController extends AbstractController {

    private ScanCheckService scanCheckService;

    private static final Logger LOGGER = getLogger(ScanCheckController.class);

    @Autowired
    public ScanCheckController(ScanCheckService scanCheckService) {

        this.scanCheckService = scanCheckService;
    }

    @SysLog("发票认证-扫码勾选-扫码操作")
    @RequestMapping(value="/selectCheck")
    public R selectCheck(@RequestParam Map<String, Object> params) {

        final String schemaLabel = getCurrentUserSchemaLabel();

        params.put("userId",getUserId());

        LOGGER.info("查询条件为:{}", params);

        InvoiceCertificationEntity entity=scanCheckService.selectCheck(schemaLabel,params);

        if(null==entity){

            return R.error(1,"没有数据！");

        }

        return R.ok().put("entity",entity);
    }
    
    @SysLog("发票认证-扫码勾选确认")
    @RequestMapping("/submit")
    public Boolean scanCheck(@RequestParam(value = "ids") String ids) {

        final String schemaLabel = getCurrentUserSchemaLabel();

        LOGGER.info("勾选处理的id:{}", ids);
        
        return scanCheckService.scanCheck(schemaLabel,ids,getLoginName(),getUserName());
    }
}
