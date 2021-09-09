package com.xforceplus.wapp.modules.job.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.job.entity.HostTaskEntity;
import com.xforceplus.wapp.modules.job.entity.ScheduleJobEntity;
import com.xforceplus.wapp.modules.job.service.HostService;
import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;
import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.posuopei.constant.Constants.POSUOPEI_PO_MATCH_QUERY;
import static com.xforceplus.wapp.modules.posuopei.constant.Constants.TASK_MATCH_QUERY;
import static com.xforceplus.wapp.modules.posuopei.constant.Constants.TASK_MATCH_WRITE;

@RestController
public class HostScheduleJobController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HostScheduleJobController.class);
    @Autowired
    private HostService hostService;
    @Autowired
    private MatchService matchService;
    /**
     * 定时任务补录
     */
    @RequestMapping("/modules/job/host/recordIn")
    public R list(@RequestBody Map<String, Object> params) {
        String taskDate=((String) params.get("taskDate")).substring(0,10);
        String vendorId=(String) params.get("vendorId");
        String type=(String) params.get("type");

        if(!StringUtils.isEmpty(vendorId)){
            vendorId="and B.vendor_nbr ='"+vendorId+"'";
        }else{
            vendorId="";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date beginDate=sdf.parse(taskDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(beginDate);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            beginDate = calendar.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String beginTime = formatter.format(beginDate);

        if("po".equals(type)){
            String poSu=matchService.checkPoSupplement();
            if("0".equals(poSu)){
            matchService.onPoSupplement();
            matchService.ReGetconnHostPo1(beginTime,taskDate,vendorId);
            matchService.ReGetconnHostPo2(beginTime,taskDate,vendorId);
            matchService.ReGetconnHostPo4(beginTime,taskDate,vendorId);
            matchService.offPoSupplement();
            //删除超两年货款转收入的信息，订单号为700888、756888以及538520
            matchService.delPo();
            }else{
                return R.error(999,"有订单正在补录！");
            }
        }else if("claim".equals(type)){
            String claim=matchService.checkClaimSupplement();
            if("0".equals(claim)){
                matchService.onClaimSupplement();
                matchService.ReconnHostClaimType2(beginTime,taskDate,vendorId);
                matchService.ReconnHostClaimType3(beginTime,taskDate,vendorId);
                matchService.offClaimSupplement();
            }else{
                return R.error(999,"有索赔正在补录！");
            }
        }else if("invoice".equals(type)){
            matchService.ReconnHostAgain(beginTime,taskDate,vendorId);
        }else if("alltask".equals(type)){
            matchService.ReGetconnHostPo1(beginTime,taskDate,vendorId);
            matchService.ReGetconnHostPo2(beginTime,taskDate,vendorId);
            matchService.ReGetconnHostPo4(beginTime,taskDate,vendorId);

        }else{
            return R.error(999,"请输入正确的任务类型！");
        }

        } catch (Exception e) {
            e.printStackTrace();
            return R.error(999,"补录任务启动失败");
        }




        return R.ok();
    }

    /**
     * 定时任务预警
     */
    @RequestMapping("/modules/job/host/error")
    public R errorList(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        PagedQueryResult<HostTaskEntity> jobList = hostService.getTaskList(query);
        int total = jobList.getTotalCount();

        PageUtils pageUtil = new PageUtils(jobList.getResults(), total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }


    /**
     * 点击结果明细的查询
     * @param param
     * @return
     */
    @SysLog("匹配查询")
    @RequestMapping(TASK_MATCH_QUERY)
    public R matchQuery(@RequestParam Map<String,Object> param){
        LOGGER.info("param {}",param);

        Query query=new Query(param);

        PagedQueryResult<MatchEntity> pagedQueryResult=hostService.getMatchEntityLists(query);

        PageUtils pageUtils=new PageUtils(pagedQueryResult.getResults(),pagedQueryResult.getTotalCount(),query.getLimit(),query.getPage());
        return R.ok().put("page",pageUtils);
    }


    /**
     * 点击结果明细的查询
     * @param ids
     * @return
     */
    @SysLog("手动写屏")
    @RequestMapping(TASK_MATCH_WRITE)
    public R matchWrite(@RequestBody Long[] ids){
        LOGGER.info("ids {}",ids);

        String message=hostService.postApi(ids);

        return R.ok(message);
    }
}
