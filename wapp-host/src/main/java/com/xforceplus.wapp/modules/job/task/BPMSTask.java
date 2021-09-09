package com.xforceplus.wapp.modules.job.task;

import com.xforceplus.wapp.interfaceBPMS.CostAppliction;
import com.xforceplus.wapp.modules.job.service.AdvanceService;
import com.xforceplus.wapp.modules.job.service.BPMSInterfaceService;
import com.xforceplus.wapp.modules.job.service.BPMSJdbcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 *  BPMS接口，venderMaster
 * @author fth
 * @date 2018年11月02日 下午15:57:22
 */
@PropertySource(value = {"classpath:config.properties"})
@Component("BPMSTask")
public class BPMSTask {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private BPMSInterfaceService bpmsInterfaceService;

    @Autowired
    private CostAppliction costAppliction;

    @Autowired
    private AdvanceService advanceService;

    @Autowired
    private BPMSJdbcService bpmsJdbcService;


    /**
     *  venderMaster 接口定时任务
     */
    @Async
    @Scheduled(cron = "${task.venderMaster.time}")
    public void  getVenderMasters(){
            bpmsInterfaceService.executeVender();
    }

    /**
     *  BPMS状态 接口定时任务
     */
    @Async
    @Scheduled(cron = "${task.bpmsStatus.time}")
    public void  getBPMSStatus(){
        costAppliction.getWalmartStatus();
    }

    /**
     *  BPMS状态 获取预付款数据
     */
    @Async
    @Scheduled(cron = "${task.advance.time}")
    public void  getAdvance(){
        advanceService.getDataFromBPMS();
    }


    /**
     *  BPMS状态 获取带票付款数据
     */
    @Async
    @Scheduled(cron = "${task.dpfk.time}")
    public void  getDpfk(){
        advanceService.getDPFKFromBPMS();
    }

    /**
     * venderMaster全量更新
     * */
    @Async
    @Scheduled(cron = "${task.venderMasterJDBC.time}")
    public void getVenderMasterJDBC(){
        bpmsJdbcService.executeVenderJDBC();
    }
}
