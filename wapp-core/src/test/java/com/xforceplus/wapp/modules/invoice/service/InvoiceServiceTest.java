package com.xforceplus.wapp.modules.invoice.service;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

import static org.junit.Assert.*;

public class InvoiceServiceTest extends BaseUnitTest {
    @Autowired
    InvoiceService invoiceService;

    @Test
    public void detail() {
        Response<InvoiceDto> detail = invoiceService.detail(1450387015679946754L);
        assertTrue("查询数据为空", detail.getResult()==null);
    }
}