package com.xforceplus.wapp.modules.blue.service.impl;

import com.xforceplus.wapp.modules.backfill.model.BackFillVerifyBean;
import com.xforceplus.wapp.repository.dao.TXfBlueRelationDao;
import com.xforceplus.wapp.sequence.IDSequence;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class BlueInvoiceRelationServiceImplTest {

    @Mock
    private TXfBlueRelationDao dao;
    @Mock
    private IDSequence idSequence;
    @Mock
    private Subject subject;
    @InjectMocks
    private BlueInvoiceRelationServiceImpl blueInvoiceRelationService;

    @BeforeEach
    void before() {
        ThreadContext.bind(mock(SecurityManager.class));

        ThreadContext.bind(subject);
    }

    @AfterEach
    void after() {
        ThreadContext.unbindSubject();
        ThreadContext.unbindSecurityManager();
    }
    @Test
    void saveBatch(){
        String no="no111";
        String code="code111";
        List<BackFillVerifyBean> blueInvoices = new ArrayList<>();
        BackFillVerifyBean bean=new BackFillVerifyBean();
        blueInvoices.add(bean);

        long id=1111111L
                ;
        when(idSequence.nextId()).thenReturn(id);
        when(dao.insert(any())).thenReturn(1);
        when(blueInvoiceRelationService.saveBatch(any())).thenReturn(true);
        this.blueInvoiceRelationService.saveBatch(no,code,blueInvoices);

        assertEquals(id,bean.getId());
    }

}