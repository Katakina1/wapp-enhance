package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BlueInvoiceServiceTest extends BaseUnitTest {

    @Autowired
    private BlueInvoiceService blueInvoiceService;

/*    @Test
    public void testMatchInvoiceInfo() {
        final BigDecimal bigDecimal = BigDecimal.valueOf(188.6800);
        final List<BlueInvoiceService.MatchRes> matchRes = blueInvoiceService.matchInvoiceInfo(bigDecimal, TXfDeductionBusinessTypeEnum.EPD_BILL, "",
                "914403006189074000",
                "91310114MA1GTUH95J"
                , BigDecimal.valueOf(6));
        final BigDecimal reduce = matchRes.stream().map(BlueInvoiceService.MatchRes::getInvoiceItems)
                .flatMap(Collection::stream).peek(x->{
                    assertEquals(x.getTaxAmount(),x.getDetailAmount().multiply(x.getTaxRate().movePointLeft(2)).setScale(2, RoundingMode.HALF_UP));
                }).map(BlueInvoiceService.InvoiceItem::getDetailAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, reduce.compareTo(bigDecimal));
    }*/
    
/*    @Test
    public void test_MatchInvoiceInfo_AGREEMENT() {
        final BigDecimal bigDecimal = BigDecimal.valueOf(39515.2800);
        final List<BlueInvoiceService.MatchRes> matchRes = blueInvoiceService.matchInvoiceInfo(bigDecimal, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL, "",
                "91420900730880978G",
                "914403007109368585"
                , BigDecimal.valueOf(13));
        final BigDecimal reduce = matchRes.stream().map(BlueInvoiceService.MatchRes::getInvoiceItems)
                .flatMap(Collection::stream).peek(x->{
                    assertEquals(x.getTaxAmount(),x.getDetailAmount().multiply(x.getTaxRate().movePointLeft(2)).setScale(2, RoundingMode.HALF_UP));
                }).map(BlueInvoiceService.InvoiceItem::getDetailAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, reduce.compareTo(bigDecimal));
    }*/
}