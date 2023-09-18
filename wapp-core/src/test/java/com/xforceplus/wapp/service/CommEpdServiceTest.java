package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceItemDao;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class CommEpdServiceTest extends BaseUnitTest {

    @Mock
    private CommEpdService commEpdService;
    @Autowired
    private TXfPreInvoiceItemDao xfPreInvoiceItemDao;

    @Test
    public void testDestroyEpdSettlement() {
        commEpdService.destroyEpdSettlement(43087460385210368L);
        Mockito.verify(commEpdService, Mockito.times(1)).destroyEpdSettlement(43087460385210368L);
    }

    @Test
    public void testAgainSplitPreInvoice() {
        LambdaQueryWrapper<TXfPreInvoiceItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TXfPreInvoiceItemEntity::getPreInvoiceId, 43087460385210368L);
        List<TXfPreInvoiceItemEntity> preInvoiceItemEntityList = xfPreInvoiceItemDao.selectList(queryWrapper);
        Assert.assertNotNull(preInvoiceItemEntityList);
        commEpdService.againSplitPreInvoice(43087460385210368L, preInvoiceItemEntityList);
        Mockito.verify(commEpdService, Mockito.times(1)).againSplitPreInvoice(43087460385210368L, preInvoiceItemEntityList);

    }

}