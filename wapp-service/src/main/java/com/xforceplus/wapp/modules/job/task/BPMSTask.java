package com.xforceplus.wapp.modules.job.task;

import com.xforceplus.wapp.interfaceBPMS.CostAppliction;
import com.xforceplus.wapp.modules.job.service.AdvanceService;
import com.xforceplus.wapp.modules.job.service.BPMSInterfaceService;
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


    /**
     *  venderMaster 接口定时任务
     */
    @Async
    public void  getVenderMasters(){
            bpmsInterfaceService.executeVender();
    }

    /**
     *  BPMS状态 接口定时任务
     */
    @Async
    public void  getBPMSStatus(){
        costAppliction.getWalmartStatus();
    }

    /**
     *  BPMS状态 获取预付款数据
     */
//    @Async
//    public void  getAdvance(){
//        advanceService.getDataFromBPMS();
//    }


    /**
     *  BPMS状态 获取带票付款数据
     */
    @Async
    public void  getDpfk(){
        advanceService.getDPFKFromBPMS();
    }



}
