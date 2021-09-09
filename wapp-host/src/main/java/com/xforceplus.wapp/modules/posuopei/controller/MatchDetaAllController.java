package com.xforceplus.wapp.modules.posuopei.controller;

/**
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/13
 * Time:13:48
*/

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.xforceplus.wapp.modules.posuopei.service.MatchDetaAllService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.xforceplus.wapp.modules.posuopei.constant.Constants.*;

@RestController
public class MatchDetaAllController extends AbstractController {
    private static final  Logger LOGGER = LoggerFactory.getLogger(MatchDetaAllController.class);
    @Autowired
    private MatchDetaAllService matchDetaAllService;



    /**
     * 点击匹配明细的查询
     * @param params
     * @return
     */
    @SysLog("匹配明细")
    @RequestMapping("modules/posuopei/matchQuerysAll/detail")
    public R getMatchDetails(@RequestBody Map<String,Object> params){
        String matchno=(String)params.get("matchno");

        LOGGER.info("票单关联号:{}",matchno);
        MatchEntity matchEntity=matchDetaAllService.getMatchDetail(matchno);

        R r= R.ok().put("invoiceList",matchEntity.getInvoiceEntityList()).put("poList",matchEntity.getPoEntityList()).put("claimList",matchEntity.getClaimEntityList());
        return r;
    }


}
