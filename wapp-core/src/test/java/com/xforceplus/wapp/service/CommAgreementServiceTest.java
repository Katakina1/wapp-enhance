package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@Slf4j
class CommAgreementServiceTest {

    @Mock
    TXfSettlementDao tXfSettlementDao;

    @Mock
    TXfPreInvoiceDao tXfPreInvoiceDao;

    @Mock
    PreinvoiceService preinvoiceService;


    @InjectMocks
    CommAgreementService commAgreementService;

    @Test
    void destroyAgreementSettlement() {
        long id=1L;

        TXfSettlementEntity entity = new TXfSettlementEntity();
        entity.setSettlementStatus(TXfSettlementStatusEnum.DESTROY.getCode());
        when(tXfSettlementDao.selectById(eq(id))).thenReturn(entity);

        assertThrows(EnhanceRuntimeException.class,()->commAgreementService.destroyAgreementSettlement(id));
    }

    @Test
    void againSplitPreInvoice() {
        ArgumentCaptor<TXfPreInvoiceEntity> preInvoiceEntityArgumentCaptor = ArgumentCaptor.forClass(TXfPreInvoiceEntity.class);
        ArgumentCaptor<QueryWrapper<TXfPreInvoiceEntity>> wrapperArgumentCaptor=ArgumentCaptor.forClass(QueryWrapper.class);
        when(preinvoiceService.reSplitPreInvoice(anyString(),anyString(),anyList())).thenReturn(Collections.EMPTY_LIST);
        when(tXfPreInvoiceDao.update(preInvoiceEntityArgumentCaptor.capture(),wrapperArgumentCaptor.capture())).thenReturn(1);
        long id=1L;

        TXfSettlementEntity entity = new TXfSettlementEntity();
        when(tXfSettlementDao.selectById(eq(id))).thenReturn(entity);
        List<TXfPreInvoiceItemEntity> preInvoiceItemList= new ArrayList<>();
        TXfPreInvoiceItemEntity invoiceItemEntity=new TXfPreInvoiceItemEntity();
        invoiceItemEntity.setPreInvoiceId(id);
        preInvoiceItemList.add(invoiceItemEntity);
        commAgreementService.againSplitPreInvoice(id,preInvoiceItemList);
        assertEquals(TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode(),preInvoiceEntityArgumentCaptor.getValue().getPreInvoiceStatus());
    }

    @Test
    void splitPreInvoice() {
    }
}