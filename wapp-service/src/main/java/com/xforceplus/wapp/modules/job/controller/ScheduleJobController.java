package com.xforceplus.wapp.modules.job.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.common.validator.ValidatorUtils;
import com.xforceplus.wapp.modules.job.entity.ScheduleJobEntity;
import com.xforceplus.wapp.modules.job.service.ScheduleJobService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 定时任务
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月28日 下午2:16:40
 */
@RestController
@RequestMapping("/sys/schedule")
public class ScheduleJobController {
    @Autowired
    private ScheduleJobService scheduleJobService;

    /**
     * 定时任务列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        List<ScheduleJobEntity> jobList = scheduleJobService.queryList(query);
        int total = scheduleJobService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(jobList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 定时任务信息
     */
    @RequestMapping("/info/{jobId}")
    public R info(@PathVariable("jobId") Long jobId) {
        ScheduleJobEntity schedule = scheduleJobService.queryObject(jobId);

        return R.ok().put("schedule", schedule);
    }

    /**
     * 保存定时任务
     */
    @SysLog("保存定时任务")
    @RequestMapping("/save")
    public R save(@RequestBody ScheduleJobEntity scheduleJob) {
        ValidatorUtils.validateEntity(scheduleJob);

        scheduleJobService.save(scheduleJob);

        return R.ok();
    }

    /**
     * 修改定时任务
     */
    @SysLog("修改定时任务")
    @RequestMapping("/update")
    public R update(@RequestBody ScheduleJobEntity scheduleJob) {
        ValidatorUtils.validateEntity(scheduleJob);

        scheduleJobService.update(scheduleJob);

        return R.ok();
    }

    /**
     * 删除定时任务
     */
    @SysLog("删除定时任务")
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] jobIds) {
        scheduleJobService.deleteBatch(jobIds);

        return R.ok();
    }

    /**
     * 立即执行任务
     */
    @SysLog("立即执行任务")
    @RequestMapping("/run")
    public R run(@RequestBody Long[] jobIds) {
        scheduleJobService.run(jobIds);

        return R.ok();
    }

    /**
     * 暂停定时任务
     */
    @SysLog("暂停定时任务")
    @RequestMapping("/pause")
    public R pause(@RequestBody Long[] jobIds) {
        scheduleJobService.pause(jobIds);

        return R.ok();
    }

    /**
     * 恢复定时任务
     */
    @SysLog("恢复定时任务")
    @RequestMapping("/resume")
    public R resume(@RequestBody Long[] jobIds) {
        scheduleJobService.resume(jobIds);

        return R.ok();
    }

}
