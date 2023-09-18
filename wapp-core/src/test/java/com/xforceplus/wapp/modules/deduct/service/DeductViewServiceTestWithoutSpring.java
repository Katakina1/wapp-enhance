package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.agreement.dto.MakeSettlementRequest;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.deduct.mapstruct.MatchedInvoiceMapper;
import com.xforceplus.wapp.modules.epd.dto.SummaryResponse;
import com.xforceplus.wapp.modules.overdue.service.OverdueServiceImpl;
import com.xforceplus.wapp.modules.settlement.dto.PreMakeSettlementRequest;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-02-22 19:56
 **/
@MockitoSettings(strictness = Strictness.LENIENT)
class DeductViewServiceTestWithoutSpring {

    @Mock
    private DeductService deductService;

    @Mock
    private MatchedInvoiceMapper matchedInvoiceMapper;

    @Mock
    private BlueInvoiceService blueInvoiceService;

    @Mock
    private AgreementBillService agreementBillService;

    @Mock
    private OverdueServiceImpl overdueService;

    @Mock
    private TXfBillDeductExtDao dao;

    @InjectMocks
    private DeductViewService deductViewService = new DeductViewService("0.1,0.13,0.01,0.09");
    String sellerNo="1a";
    String purchaser="1b";

  /*  @Test
    void makeSettlement() {

        MakeSettlementRequest request = new MakeSettlementRequest();
        request.setInvoiceIds(Arrays.asList(10L));
        long billId1=20L;
        request.setBillIds(Arrays.asList(billId1));
        request.setTaxRate(new BigDecimal(0.1));
        TXfDeductionBusinessTypeEnum type = TXfDeductionBusinessTypeEnum.AGREEMENT_BILL;
        TXfBillDeductEntity entity=new TXfBillDeductEntity();
        entity.setAmountWithTax(new BigDecimal(110));
        entity.setAmountWithoutTax(new BigDecimal(100));
        entity.setTaxAmount(new BigDecimal(10));
        entity.setTaxRate(new BigDecimal(0.1));
        entity.setSellerNo(sellerNo);
        entity.setPurchaserNo(purchaser);
        when(dao.selectOne(any())).thenReturn(entity);
        TXfBillDeductEntity e1=new TXfBillDeductEntity();
        long id1=111L;
        e1.setId(id1);
        e1.setTaxRate(new BigDecimal(0.1));
        e1.setAmountWithoutTax(new  BigDecimal(100));
        when(dao.selectList(any())).thenReturn(Arrays.asList(e1));
        when(overdueService.oneOptBySellerNo(any(),any())).thenReturn(0);
        ArgumentCaptor<List> ids=ArgumentCaptor.forClass(List.class);
        when(agreementBillService.mergeSettlementByManual(ids.capture(),eq(type),any())).thenReturn(null );
        deductViewService.makeSettlement(request, type);
        final List value = ids.getValue();
        assertTrue(value.contains(id1));
        assertTrue(value.contains(billId1));

    }*/

    @Test
    void sumDueAndNegative(){
        DeductListRequest request = new DeductListRequest();
        TXfBillDeductEntity entity=new TXfBillDeductEntity();
        entity.setAmountWithTax(new BigDecimal(110));
        entity.setAmountWithoutTax(new BigDecimal(100));
        entity.setTaxAmount(new BigDecimal(10));
        entity.setTaxRate(new BigDecimal(0.1));
        entity.setSellerNo(sellerNo);
        entity.setPurchaserNo(purchaser);
        when(dao.selectOne(any())).thenReturn(entity);
        final BigDecimal bigDecimal = this.deductViewService.sumDueAndNegative(request, TXfDeductionBusinessTypeEnum.EPD_BILL);
        assertEquals(0, bigDecimal.compareTo(entity.getAmountWithoutTax()));
    }

    @Test
    void summary(){
        DeductListRequest request = new DeductListRequest();
        List<Map<String, Object>> map = new ArrayList<>();
        Map<String,Object> r=new HashMap<>();
        r.put("taxRate","0.1");
        r.put("count",10);
        map.add(r);
        when(dao.selectMaps(any())).thenReturn(map);
        final List<SummaryResponse> summary = this.deductViewService.summary(request, TXfDeductionBusinessTypeEnum.EPD_BILL);
        assertEquals(5,summary.size());
        final SummaryResponse summaryResponse = summary.get(summary.size() - 1);
        assertEquals("全部", summaryResponse.getTaxRateText());
        assertEquals(-1, summaryResponse.getTaxRate());
        assertEquals(10, summaryResponse.getCount());
        assertEquals("1%税率",summary.get(0).getTaxRateText());
        assertEquals("9%税率",summary.get(1).getTaxRateText());
        assertEquals("10%税率",summary.get(2).getTaxRateText());
        assertEquals(10,summary.get(2).getCount());
        assertEquals(0.1,summary.get(2).getTaxRate());
        assertEquals("13%税率",summary.get(3).getTaxRateText());
    }

/*    @Test
    void matchedBlueInvoice(){
        PreMakeSettlementRequest request=new PreMakeSettlementRequest();

        this.deductViewService.getMatchedInvoice(request,TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
    }*/
}
