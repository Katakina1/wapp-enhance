package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BlueInvoiceServiceTest extends BaseUnitTest {

    @Autowired
    private BlueInvoiceService blueInvoiceService;

    @Test
    public void testMatchInvoiceInfo() {
        final BigDecimal bigDecimal = BigDecimal.valueOf(188.6800);
        final List<BlueInvoiceService.MatchRes> matchRes = blueInvoiceService.matchInvoiceInfo(bigDecimal, TXfDeductionBusinessTypeEnum.EPD_BILL, "",
                "914403006189074000",
                "91310114MA1GTUH95J"
                , BigDecimal.valueOf(6));
        final BigDecimal reduce = matchRes.stream().map(BlueInvoiceService.MatchRes::getInvoiceItems).flatMap(Collection::stream).map(BlueInvoiceService.InvoiceItem::getDetailAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, reduce.compareTo(bigDecimal));
    }
}