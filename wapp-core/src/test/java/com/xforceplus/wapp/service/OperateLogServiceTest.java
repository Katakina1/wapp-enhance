package com.xforceplus.wapp.service;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.modules.backFill.model.VerificationBack;
import com.xforceplus.wapp.modules.backFill.service.EInvoiceMatchService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by SunShiyong on 2021/10/18.
 */
@Slf4j
public class OperateLogServiceTest extends BaseUnitTest {

    @Autowired
    OperateLogService operateLogService;
    @Test
    public void testAdd() {
        System.out.println("添加开始");
        operateLogService.add(12L, OperateLogEnum.APPLY_RED_NOTIFICATION,"待开票",null,null);
        System.out.println("添加成功");
    }



}
