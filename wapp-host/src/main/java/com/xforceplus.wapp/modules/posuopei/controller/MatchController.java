package com.xforceplus.wapp.modules.posuopei.controller;


import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;


import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;


import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import static com.xforceplus.wapp.modules.posuopei.constant.Constants.*;
import static java.lang.Thread.sleep;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author raymond.yan
 */
@RestController
public class MatchController extends AbstractController {
    private final static Logger LOGGER = getLogger(MatchController.class);
    private MatchService matchService;
    @Autowired
    public MatchController(MatchService matchService){
        this.matchService=matchService;
    }

    @SysLog("forceWrite")
    @RequestMapping(POSUOPEI_PO_MATCH_WRITE)
    public R write(@RequestBody Long[] ids){
        String msg="开始写屏！";
        if( ids==null||ids.length==0){
            return  R.ok("请勾选要写屏的数据！");
        }else{
            try{
                matchService.runWritrScreen1(ids);
            }catch (Exception e){
                msg=e.getMessage();
            }

        }

        return  R.ok(msg);

    }



}
