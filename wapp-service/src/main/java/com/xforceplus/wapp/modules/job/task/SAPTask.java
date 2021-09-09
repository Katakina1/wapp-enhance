package com.xforceplus.wapp.modules.job.task;

import com.xforceplus.wapp.interfaceSAP.SAP;

/**
 *
 *  BPMS接口，venderMaster
 * @author fth
 * @date 2018年11月02日 下午15:57:22
 */

public class SAPTask {
    /**
     *  SAP 接口定时任务
     */

    public void sap(){
        SAP sap = SAP.getInstance();
        //先导入供应商
        sap.runVendor();
        //再导入付款信息
        sap.run();
    }
}