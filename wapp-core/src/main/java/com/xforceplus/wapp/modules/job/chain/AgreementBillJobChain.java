package com.xforceplus.wapp.modules.job.chain;

import com.xforceplus.wapp.modules.job.command.*;
import org.apache.commons.chain.impl.ChainBase;
import org.springframework.context.ApplicationContext;

/**
 * @program: wapp-generator
 * @description: 原始协议单处理责任链
 * @author: Kenny Wong
 * @create: 2021-10-14 14:05
 **/
public class AgreementBillJobChain extends ChainBase {

    public AgreementBillJobChain(ApplicationContext applicationContext) {
        super();

        addCommand(applicationContext.getBean(AgreementBillDownloadCommand.class));
        addCommand(applicationContext.getBean(AgreementBillFbl5nSaveCommand.class));
        addCommand(applicationContext.getBean(AgreementBillZarrSaveCommand.class));
        addCommand(applicationContext.getBean(AgreementFbl5nMergeCommand.class));
        addCommand(applicationContext.getBean(AgreementZarrMergeCommand.class));
        addCommand(applicationContext.getBean(AgreementBillFilterCommand.class));
    }

}
