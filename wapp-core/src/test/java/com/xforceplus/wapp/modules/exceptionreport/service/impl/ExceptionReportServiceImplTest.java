package com.xforceplus.wapp.modules.exceptionreport.service.impl;

import com.xforceplus.wapp.client.CacheClient;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.export.ExportHandlerEnum;
import com.xforceplus.wapp.export.IExportHandler;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import com.xforceplus.wapp.repository.dao.TXfExceptionReportDao;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import org.apache.commons.lang3.RandomUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.quality.Strictness.LENIENT;

@MockitoSettings(strictness = LENIENT)
@ExtendWith(MockitoExtension.class)
class ExceptionReportServiceImplTest {

    @Mock
    private ActiveMqProducer activeMqProducer;

    @Mock
    private ExcelExportLogService excelExportLogService;

    @Mock
    private CacheClient cacheClient;
    @InjectMocks
    private ExceptionReportServiceImpl exceptionReportService;

    @Mock
    private IDSequence idSequence;
    @Mock
    private Subject subject;
    @Mock
    private TXfExceptionReportDao dao;

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
    void exportClaimSuccess() {
        final ExceptionReportTypeEnum claim = ExceptionReportTypeEnum.CLAIM;
        String key = String.format("exception-report:%s:%s", UserUtil.getUserId(), claim.name());
        when(subject.getPrincipal()).thenReturn(null);
        ExceptionReportRequest request = new ExceptionReportRequest();
        when(excelExportLogService.save(Mockito.any())).thenReturn(true);
        doNothing().when(activeMqProducer).send(any(), anyString(), anyMap());
        exceptionReportService.export(request, ExceptionReportTypeEnum.CLAIM);
        ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> map = ArgumentCaptor.forClass(Map.class);
        verify(activeMqProducer, times(1)).send(any(), json.capture(), map.capture());
        final Map value = map.getValue();
        assertTrue(value.containsKey(IExportHandler.KEY_OF_HANDLER_NAME));
        assertEquals(ExportHandlerEnum.EXCEPTION_REPORT.name(), value.get(IExportHandler.KEY_OF_HANDLER_NAME));

        verify(cacheClient, times(1)).set(eq(key), eq(Boolean.TRUE), eq(60));

    }

    @Test
    void exportClaimLocked() {
        final ExceptionReportTypeEnum claim = ExceptionReportTypeEnum.CLAIM;
        String key = String.format("exception-report:%s:%s", UserUtil.getUserId(), claim.name());
        when(subject.getPrincipal()).thenReturn(null);
        ExceptionReportRequest request = new ExceptionReportRequest();
        when(cacheClient.get(eq(key))).thenReturn(true);
        doNothing().when(activeMqProducer).send(any(), anyString(), anyMap());
        assertThrows(EnhanceRuntimeException.class, () -> exceptionReportService.export(request, claim));
        verify(cacheClient, times(0)).set(eq(key), eq(Boolean.TRUE), eq(60));
    }

    @Test
    void addClaim() {
        add(ExceptionReportTypeEnum.CLAIM,exceptionReportService::add4Claim);
    }

    @Test
    void addEPD() {
        add(ExceptionReportTypeEnum.EPD,exceptionReportService::add4EPD);
    }

    @Test
    void addAGREEMENT() {
        add(ExceptionReportTypeEnum.AGREEMENT,exceptionReportService::add4Agreement);
    }

    private void add(ExceptionReportTypeEnum typeEnum, Consumer<TXfExceptionReportEntity> consumer) {
        long id = RandomUtils.nextLong(1, 100000000000L);
        TXfExceptionReportEntity entity = new TXfExceptionReportEntity();
        when(this.idSequence.nextId()).thenReturn(id);
        when(dao.insert(any())).thenReturn(1);
        consumer.accept(entity);
        assertEquals(typeEnum.getType(), entity.getType());
        assertEquals(id, entity.getId());
        assertEquals(1, entity.getStatus());
    }

}