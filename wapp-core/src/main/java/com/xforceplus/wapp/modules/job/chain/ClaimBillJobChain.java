package com.xforceplus.wapp.modules.job.chain;

import com.xforceplus.wapp.modules.job.command.ClaimBillDownloadCommand;
import com.xforceplus.wapp.modules.job.command.ClaimBillFilterCommand;
import com.xforceplus.wapp.modules.job.command.ClaimBillSaveCommand;
import org.apache.commons.chain.impl.ChainBase;

/**
 * @program: wapp-generator
 * @description: 索赔单任务责任链
 * @author: Kenny Wong
 * @create: 2021-10-14 15:09
 **/
public class ClaimBillJobChain extends ChainBase {

    public ClaimBillJobChain() {
        super();
        addCommand(new ClaimBillDownloadCommand());
        addCommand(new ClaimBillSaveCommand());
        addCommand(new ClaimBillFilterCommand());
    }
}
