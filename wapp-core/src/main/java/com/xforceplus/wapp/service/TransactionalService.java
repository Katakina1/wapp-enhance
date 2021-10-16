package com.xforceplus.wapp.service;

import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-18 13:48
 **/
@Service
public class TransactionalService {
    @Transactional
    public void execute(List<Supplier<Boolean>> dbActions) {
        for (Supplier<Boolean> dbAction : dbActions) {
            if(!dbAction.get()){
                throw new EnhanceRuntimeException("执行事务失败");
            }
        }
    }
}
