package com.xforceplus.wapp.modules.certification.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.service.IntelligentizeCheckService;
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
 * 智能勾选
 * @author kevin.wang
 * @date 4/14/2018
 */
@RestController
@RequestMapping("certification/intelligentizeCheck")
public class IntelligentizeCheckController extends AbstractController {

    private IntelligentizeCheckService intelligentizeCheckService;
    
    private static final Logger LOGGER = getLogger(IntelligentizeCheckController.class);

    @Autowired
    public IntelligentizeCheckController(IntelligentizeCheckService intelligentizeCheckService) {

        this.intelligentizeCheckService = intelligentizeCheckService;
    }
    
    @RequestMapping("/list")
    @SysLog("发票认证-智能勾选")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        LOGGER.info("查询处理条件ids为:{}", params.get("ids"));
        //查询列表数据
        if(params.get("ids").equals("") || null==params.get("ids")){
            return R.ok();
        }
        List<InvoiceCertificationEntity> list = intelligentizeCheckService.queryList(schemaLabel,params,getLoginName(),getUserName());

        return R.ok().put("list", list);
    }

    @SysLog("发票认证-智能勾选-选择数据确认或取消")
    @RequestMapping(value="/select")
    public R cancelCheck(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //获取当前用户的userId
        params.put("userId", getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        InvoiceCertificationEntity  entity=intelligentizeCheckService.selectQueryList(schemaLabel,query);
        if(null==entity){
            return R.error(1,"没有可操作的数据");
        }
        
        return R.ok().put("entity",entity);
    }
    @SysLog("获取开关状态")
    @RequestMapping(value="/selectSwitchStatus")
    public R getSwitchStatus() {
        String   switchStatus=intelligentizeCheckService.selectSwitchStatus();

        return R.ok().put("switchStatus",switchStatus);
    }
    
}
