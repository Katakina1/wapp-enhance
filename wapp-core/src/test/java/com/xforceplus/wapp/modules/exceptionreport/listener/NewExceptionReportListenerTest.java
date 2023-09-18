package com.xforceplus.wapp.modules.exceptionreport.listener;

import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.exceptionreport.event.NewExceptionReportEvent;
import com.xforceplus.wapp.modules.exceptionreport.mapstruct.ExceptionReportMapper;
//import com.xforceplus.wapp.modules.exceptionreport.mapstruct.ExceptionReportMapperImpl;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class NewExceptionReportListenerTest {
    @Mock
    private ExceptionReportService exceptionReportService;
    @Spy
//    private ExceptionReportMapper exceptionReportMapper = new ExceptionReportMapperImpl();
    @InjectMocks
    NewExceptionReportListener listener;

    @Test
    void handle() {
        NewExceptionReportEvent exceptionReportEvent = new NewExceptionReportEvent();
        exceptionReportEvent.setTaxBalance(new BigDecimal(100));
        exceptionReportEvent.setReportCode(ExceptionReportCodeEnum.WITH_DIFF_TAX);
        exceptionReportEvent.setType(ExceptionReportTypeEnum.CLAIM);
        TXfBillDeductEntity deduct=new TXfBillDeductEntity();
        deduct.setAmountWithoutTax(new BigDecimal(100));
        exceptionReportEvent.setDeduct(deduct);
        doNothing().when(exceptionReportService).add4Claim(any());
//        when(exceptionReportMapper.deductToReport(any())).thenCallRealMethod();
        listener.handle(exceptionReportEvent);
        ArgumentCaptor<TXfExceptionReportEntity> argumentCaptor=ArgumentCaptor.forClass(TXfExceptionReportEntity.class);
        verify(exceptionReportService,times(1)).add4Claim(argumentCaptor.capture());

        TXfExceptionReportEntity entity=argumentCaptor.getValue();
        assertEquals(ExceptionReportTypeEnum.CLAIM.getType(),entity.getType());
        assertEquals(ExceptionReportCodeEnum.WITH_DIFF_TAX.getDescription(),entity.getDescription());
        assertEquals(0,new BigDecimal(100).compareTo(entity.getAmountWithoutTax()));
    }

}