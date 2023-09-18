package com.xforceplus.wapp.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-09-19 13:13
 **/
@Slf4j
public class TransactionUtils {

    /**
     * 如果有事务就在事务提交后执行，没有事务就立即执行
     *
     * @param afterCommit 具体执行的逻辑
     */
    public static void invokeAfterCommitIfExistOrImmediately(Runnable afterCommit){
        //如果有事务就在事务提交后执行，没有事务就立即执行
        if (TransactionSynchronizationManager.isSynchronizationActive() && TransactionSynchronizationManager.isActualTransactionActive()) {
            log.info("当前操作在事务中，注册事务成功回调事件,{}",TransactionSynchronizationManager.getCurrentTransactionName());
            TransactionSynchronization transactionSynchronization = new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    afterCommit.run();
                }
            };
            TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);
        } else {
            log.info("当前无事务，直接执行逻辑");
            afterCommit.run();
        }
    }
}
