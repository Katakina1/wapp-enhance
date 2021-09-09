package com.xforceplus.wapp.modules.job.task;

import com.xforceplus.wapp.interfaceSAP.SAP;
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
@Component("SAPTask")
public class SAPTask {
    /**
     *  SAP 接口定时任务
     */
    @Async
    @Scheduled(cron = "${task.sap.time}")
    public void sap(){
        SAP sap = SAP.getInstance();
        //先导入供应商
        sap.runVendor();
        //再导入付款信息
        sap.run();
    }
}