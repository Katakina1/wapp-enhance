package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.dto.PreInvoiceDTO;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceItemDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemDao;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class CommRedNotificationServiceTest extends BaseUnitTest {

    @Mock
    private CommRedNotificationService commRedNotificationService;
    @Autowired
    private TXfPreInvoiceDao xfPreInvoiceDao;
    @Autowired
    private TXfPreInvoiceItemDao xfPreInvoiceItemDao;

    @Test
    public void testApplyAddRedNotification() {
        TXfPreInvoiceEntity tXfPreInvoiceEntity = xfPreInvoiceDao.selectById(43182376406794240L);
        Assert.assertNotNull(tXfPreInvoiceEntity);
        LambdaQueryWrapper<TXfPreInvoiceItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TXfPreInvoiceItemEntity::getPreInvoiceId,43182376406794240L);
        List<TXfPreInvoiceItemEntity> preInvoiceItemEntityList = xfPreInvoiceItemDao.selectList(queryWrapper);
        Assert.assertNotNull(preInvoiceItemEntityList);
        PreInvoiceDTO preInvoiceDTO = new PreInvoiceDTO();
        preInvoiceDTO.setTXfPreInvoiceEntity(tXfPreInvoiceEntity);
        preInvoiceDTO.setTXfPreInvoiceItemEntityList(preInvoiceItemEntityList);
        commRedNotificationService.applyAddRedNotification(preInvoiceDTO);
        Mockito.verify(commRedNotificationService, Mockito.times(1)).applyAddRedNotification(preInvoiceDTO);
    }

    @Test
    public void testApplyDestroyRedNotification() {
        commRedNotificationService.applyDestroyRedNotification(1L, "备注");
        Mockito.verify(commRedNotificationService, Mockito.times(1)).applyDestroyRedNotification(1L, "备注");
    }

    @Test
    public void testConfirmDestroyRedNotification() {
        commRedNotificationService.confirmDestroyRedNotification(1L, null);
        Mockito.verify(commRedNotificationService, Mockito.times(1)).confirmDestroyRedNotification(1L, null);
    }
}
