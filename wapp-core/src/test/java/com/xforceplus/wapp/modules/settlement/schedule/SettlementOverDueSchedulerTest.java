package com.xforceplus.wapp.modules.settlement.schedule;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.deduct.service.SettlmentItemBatchService;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemExtDao;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SettlementOverDueSchedulerTest extends BaseUnitTest {
    @Autowired
    TXfSettlementItemDao tXfSettlementItemDao;
    @Autowired
    TXfSettlementItemExtDao tXfSettlementItemExtDao;
    @Autowired
    SettlmentItemBatchService settlmentItemBatchService;

    @Autowired
    private SettlementOverDueScheduler settlementOverDueScheduler;

    @Test
    public void testSettlementAutoConfirm() {
        settlementOverDueScheduler.settlementAutoConfirm();
    }

    @Test
    public void testSettlementItem(){
      TXfSettlementItemEntity itemEntity = tXfSettlementItemDao.selectById(124238327502131200L);
      System.out.println("itemEntity"+JSON.toJSONString(itemEntity));

        itemEntity.setId(System.currentTimeMillis());
        itemEntity.setSettlementNo("");
      settlmentItemBatchService.saveBatch(Lists.newArrayList(itemEntity));
    }
}