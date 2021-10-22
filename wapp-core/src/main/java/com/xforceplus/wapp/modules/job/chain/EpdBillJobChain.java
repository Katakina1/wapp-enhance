package com.xforceplus.wapp.modules.job.chain;

import com.xforceplus.wapp.modules.job.command.EpdBillDownloadCommand;
import com.xforceplus.wapp.modules.job.command.EpdBillFilterCommand;
import com.xforceplus.wapp.modules.job.command.EpdBillSaveCommand;
import com.xforceplus.wapp.modules.job.command.EpdLogItemSaveCommand;
import org.apache.commons.chain.impl.ChainBase;
import org.springframework.context.ApplicationContext;

/**
 * @program: wapp-generator
 * @description: EPD单任务责任链
 * @author: Kenny Wong
 * @create: 2021-10-14 15:28
 **/
public class EpdBillJobChain extends ChainBase {

    public EpdBillJobChain(ApplicationContext applicationContext) {
        super();
        addCommand(applicationContext.getBean(EpdBillDownloadCommand.class));
        addCommand(applicationContext.getBean(EpdLogItemSaveCommand.class));
        addCommand(applicationContext.getBean(EpdBillSaveCommand.class));
        addCommand(applicationContext.getBean(EpdBillFilterCommand.class));
    }
}
