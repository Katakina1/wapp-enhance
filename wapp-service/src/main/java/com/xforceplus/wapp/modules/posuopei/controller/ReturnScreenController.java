package com.xforceplus.wapp.modules.posuopei.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.xforceplus.wapp.modules.posuopei.service.ReturnScreenService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.xforceplus.wapp.modules.posuopei.constant.Constants.POSUOPEI_PO_MATCH_QUERY;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author raymond.yan
 */
@RestController
public class ReturnScreenController extends AbstractController {
    private final static Logger LOGGER = getLogger(ReturnScreenController.class);
    private ReturnScreenService returnScreenService;
    @Autowired
    public ReturnScreenController(ReturnScreenService returnScreenService){
        this.returnScreenService=returnScreenService;
    }

    /**
     * 录入ReturnScreen表数据
     * @return
     */
    @SysLog("录入ReturnScreen表数据")
    @RequestMapping(value = "/modules/posuopei/returnScreen/save")
    public R insertReturnScreen(HostReturnScreenEntity hostReturnScreenEntity){
        returnScreenService.insertReturnScreen(hostReturnScreenEntity);
        return R.ok();
    }
    /**
     * 点击结果明细的查询
     * @param param
     * @return
     */
    @SysLog("匹配查询")
    @RequestMapping(value = "/modules/posuopei/returnScreen/getReturnScreenPage")
    public R returnScreenQuery(@RequestParam Map<String,Object> param){
        LOGGER.info("param {}",param);

        Query query=new Query(param);

        PagedQueryResult<HostReturnScreenEntity> pagedQueryResult=returnScreenService.getReturnScreenList(query);

        PageUtils pageUtils=new PageUtils(pagedQueryResult.getResults(),pagedQueryResult.getTotalCount(),query.getLimit(),query.getPage());
        return R.ok().put("page",pageUtils);
    }
    }




