package com.xforceplus.wapp.modules.job;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.deduct.service.BlueInvoiceService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: wapp-generator
 * @description: test
 * @author: Kenny Wong
 * @create: 2021-10-28 20:05
 **/
public class BlueInvoiceServiceTest extends BaseUnitTest {

    @Autowired
    private BlueInvoiceService blueInvoiceService;

    // @Test
    public void test1(){
        List<BlueInvoiceService.MatchRes> list = new ArrayList<>();
        BlueInvoiceService.MatchRes item = BlueInvoiceService.MatchRes
                .builder()
                .invoiceId(27L)
                .deductedAmount(new BigDecimal(1000))
                .build();
        list.add(item);
        blueInvoiceService.withdrawInvoices(list);
    }
}
