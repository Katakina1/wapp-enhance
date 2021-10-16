package com.xforceplus.wapp.modules.job.chain;

import com.xforceplus.wapp.modules.job.command.EpdBillDownloadCommand;
import com.xforceplus.wapp.modules.job.command.EpdBillFilterCommand;
import com.xforceplus.wapp.modules.job.command.EpdBillSaveCommand;
import com.xforceplus.wapp.modules.job.command.EpdLogItemSaveCommand;
import org.apache.commons.chain.impl.ChainBase;

/**
 * @program: wapp-generator
 * @description: EPD单任务责任链
 * @author: Kenny Wong
 * @create: 2021-10-14 15:28
 **/
public class EpdBillJobChain extends ChainBase {

    public EpdBillJobChain() {
        super();
        addCommand(new EpdBillDownloadCommand());
        addCommand(new EpdBillSaveCommand());
        addCommand(new EpdLogItemSaveCommand());
        addCommand(new EpdBillFilterCommand());
    }
}
