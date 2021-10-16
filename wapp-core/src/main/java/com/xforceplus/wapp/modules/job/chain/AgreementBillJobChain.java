package com.xforceplus.wapp.modules.job.chain;

import com.xforceplus.wapp.modules.job.command.AgreementBillDownloadCommand;
import com.xforceplus.wapp.modules.job.command.AgreementBillFilterCommand;
import com.xforceplus.wapp.modules.job.command.AgreementBillSaveCommand;
import com.xforceplus.wapp.modules.job.command.AgreementItemSaveCommand;
import org.apache.commons.chain.impl.ChainBase;

/**
 * @program: wapp-generator
 * @description: 原始协议单处理责任链
 * @author: Kenny Wong
 * @create: 2021-10-14 14:05
 **/
public class AgreementBillJobChain extends ChainBase {

    public AgreementBillJobChain() {
        super();
        addCommand(new AgreementBillDownloadCommand());
        addCommand(new AgreementBillSaveCommand());
        addCommand(new AgreementItemSaveCommand());
        addCommand(new AgreementBillFilterCommand());
    }

}
