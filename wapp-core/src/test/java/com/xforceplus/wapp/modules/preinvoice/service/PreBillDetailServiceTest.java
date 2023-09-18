package com.xforceplus.wapp.modules.preinvoice.service;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.repository.entity.TXfPreBillDetailEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 测试
 * @date : 2022/09/05 19:57
 **/
public class PreBillDetailServiceTest extends BaseUnitTest {

    @Autowired
    PreBillDetailService preBillDetailService;

    @Test
    public void test() {
        // 插入数据
        TXfPreBillDetailEntity tXfPreBillDetailEntity = new TXfPreBillDetailEntity();
        tXfPreBillDetailEntity.setId(110L);
        tXfPreBillDetailEntity.setSettlementId(10L);
        tXfPreBillDetailEntity.setSettlementItemId(120L);
        tXfPreBillDetailEntity.setSettlementNo("billNo");
        tXfPreBillDetailEntity.setSettlementItemNo("billItemNo");
        tXfPreBillDetailEntity.setPreInvoiceId(20L);
        tXfPreBillDetailEntity.setPreInvoiceItemId(230L);
        tXfPreBillDetailEntity.setAmountWithTax(new BigDecimal("100"));
        tXfPreBillDetailEntity.setAmountWithoutTax(new BigDecimal("100"));
        tXfPreBillDetailEntity.setTaxAmount(new BigDecimal("0"));
        boolean save = preBillDetailService.save(tXfPreBillDetailEntity);
        assertTrue(save);

        // 查询
        List<TXfPreBillDetailEntity> details1 = preBillDetailService.getDetails(tXfPreBillDetailEntity.getPreInvoiceId());
        assertEquals(details1.size(), 1);
        assertEquals(details1.get(0).getId(), 110L);
        assertEquals(details1.get(0).getSettlementItemNo(), "billItemNo");
        // 更新
        TXfPreBillDetailEntity preBillDetailEntityU = new TXfPreBillDetailEntity();
        preBillDetailEntityU.setSettlementItemNo("billItemNo1");
        preBillDetailEntityU.setId(details1.get(0).getId());
        preBillDetailService.updateById(preBillDetailEntityU);

        List<TXfPreBillDetailEntity> details2 = preBillDetailService.getDetails(Lists.newArrayList(tXfPreBillDetailEntity.getPreInvoiceId()));
        assertEquals(details2.size(), 1);
        assertEquals(details2.get(0).getId(), 110L);
        assertEquals(details2.get(0).getSettlementItemNo(), "billItemNo1");

        // 删除
        boolean remove = preBillDetailService.removeById(tXfPreBillDetailEntity.getId());
        assertTrue(remove);
    }
}
