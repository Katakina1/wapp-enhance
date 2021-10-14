package com.xforceplus.wapp.modules.job.chain;

import com.xforceplus.wapp.modules.job.command.EPDBillDownloadCommand;
import com.xforceplus.wapp.modules.job.command.EPDBillFilterCommand;
import com.xforceplus.wapp.modules.job.command.EPDBillSaveCommand;
import org.apache.commons.chain.impl.ChainBase;

/**
 * @program: wapp-generator
 * @description: EPD单任务责任链
 * @author: Kenny Wong
 * @create: 2021-10-14 15:28
 **/
public class EPDBillJobChain extends ChainBase {

    public EPDBillJobChain() {
        super();
        addCommand(new EPDBillDownloadCommand());
        addCommand(new EPDBillSaveCommand());
        addCommand(new EPDBillFilterCommand());
    }
}
