package com.xforceplus.wapp.modules.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.deduct.service.BlueInvoiceService;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
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

    @Autowired
    private com.xforceplus.wapp.modules.backfill.service.RecordInvoiceExtService recordInvoiceExtService;

/*    // @Test
    public void test1() {
        List<BlueInvoiceService.MatchRes> list = new ArrayList<>();
        BlueInvoiceService.MatchRes item = BlueInvoiceService.MatchRes
                .builder()
                .invoiceId(27L)
                .deductedAmount(new BigDecimal(1000))
                .build();
        list.add(item);
        blueInvoiceService.withdrawInvoices(list);
    }*/
}
