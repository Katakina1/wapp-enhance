package com.xforceplus.wapp.modules.job.chain;

import com.xforceplus.wapp.modules.job.command.*;
import org.apache.commons.chain.impl.ChainBase;
import org.springframework.context.ApplicationContext;

/**
 * @program: wapp-generator
 * @description: 索赔单任务责任链
 * @author: Kenny Wong
 * @create: 2021-10-14 15:09
 **/
public class ClaimBillJobChain extends ChainBase {

    public ClaimBillJobChain(ApplicationContext applicationContext) {
        super();
        addCommand(applicationContext.getBean(ClaimBillDownloadCommand.class));
        addCommand(applicationContext.getBean(ClaimItemHyperSaveCommand.class));
        addCommand(applicationContext.getBean(ClaimItemSamsSaveCommand.class));
        addCommand(applicationContext.getBean(ClaimBillSaveCommand.class));
        addCommand(applicationContext.getBean(ClaimBillFilterCommand.class));
    }
}
