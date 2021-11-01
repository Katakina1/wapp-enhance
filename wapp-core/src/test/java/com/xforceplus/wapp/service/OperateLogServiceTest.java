package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.repository.dao.TXfBillDeductDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
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
    @Autowired
    private TXfBillDeductDao tXfBillDeductDao;

    @Test
    public void testAdd() {
        System.out.println("添加开始");
        operateLogService.add(12L, OperateLogEnum.APPLY_RED_NOTIFICATION,"待开票",null,null);
        System.out.println("添加成功");
    }


    @Test
    public void a(){
        Page<TXfBillDeductEntity> page = new QueryChainWrapper<>(tXfBillDeductDao)
                .select("STATUS", "sum(TAX_AMOUNT) as TAX_AMOUNT")
                .eq(TXfBillDeductEntity.BUSINESS_TYPE, "1")
                .groupBy(TXfBillDeductEntity.STATUS).page(new Page<>(1, 10));

        System.out.println(page.getRecords());
    }



}
