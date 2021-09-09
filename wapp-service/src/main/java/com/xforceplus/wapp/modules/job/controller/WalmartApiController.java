package com.xforceplus.wapp.modules.job.controller;

import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.job.entity.ScheduleJobLogEntity;
import com.xforceplus.wapp.modules.job.entity.WalmartApiEntity;
import com.xforceplus.wapp.modules.job.service.ScheduleJobLogService;
import com.xforceplus.wapp.modules.job.service.WalmartApiService;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/walmartapi")
public class WalmartApiController {
    @Autowired
    private WalmartApiService walmartApiService;


    @ResponseBody
    @RequestMapping(value="/diis",method=RequestMethod.GET)
    public String searchGf() {
        List<WalmartApiEntity> returnResult = walmartApiService.searchGf();
        JSONArray arr = JSONArray.fromObject(returnResult);
        return arr.toString();
    }


}
