package com.xforceplus.wapp.modules.deduct.service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.deduct.dto.MatchedInvoiceListResponse;
import com.xforceplus.wapp.modules.epd.dto.SummaryResponse;
import com.xforceplus.wapp.modules.settlement.dto.PreMakeSettlementRequest;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

public class DeductViewServiceTest extends BaseUnitTest {

    @Autowired
    private DeductViewService deductViewService;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Test
    public void testSummary() {
        final DeductListRequest deductListRequest = new DeductListRequest();

        final List<SummaryResponse> summary = deductViewService.summary(deductListRequest, TXfDeductionBusinessTypeEnum.EPD_BILL);
        System.out.println("summary:"+ JSON.toJSONString(summary));
    }

    @Test
    public void testSumDueAndNegative() {
        final BigDecimal bigDecimal = deductViewService.sumDueAndNegative("PT", "172164", TXfDeductionBusinessTypeEnum.EPD_BILL, new BigDecimal("0.09"),null);
        System.out.println("sumDueAndNegative---->"+bigDecimal.toPlainString());
    }

/*    @Test
    public void testGetMatchedInvoice() {
        PreMakeSettlementRequest request=JSON.parseObject("{\"taxRate\":\"0.09\",\"purchaserNo\":\"D073\",\"billIds\":[\"252992\"]}",PreMakeSettlementRequest.class);
        request.setSellerNo("172164");
        final List<MatchedInvoiceListResponse> matchedInvoice = deductViewService.getMatchedInvoice(request, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL);
        System.out.println("matchedInvoice:-->"+JSON.toJSONString(matchedInvoice));
    }*/

//    @Test
    public void testRowLockInterceptor(){
        TXfSettlementEntity entity=JSON.parseObject(
                "  {\n" +
                "    \"id\": 131231311111,\n" +
                "    \"seller_no\": \"172164\",\n" +
                "    \"seller_name\": \"深圳沃尔玛百货零售有限公司\",\n" +
                "    \"seller_tax_no\": \"914403006189074000\",\n" +
                "    \"seller_tel\": \"021-62887959\",\n" +
                "    \"seller_address\": \"上海市宝山区1122号\",\n" +
                "    \"seller_bank_name\": \"三菱东京日联银行（中国）有限公司上海分行2\",\n" +
                "    \"seller_bank_account\": \"404029000003327045\",\n" +
                "    \"purchaser_no\": \"PT\",\n" +
                "    \"purchaser_name\": \"沃尔玛（四川）百货有限公司\",\n" +
                "    \"purchaser_tax_no\": \"9113030074540443\",\n" +
                "    \"purchaser_tel\": \"028-86892288\",\n" +
                "    \"purchaser_address\": \"四川省成都市锦江区走马街68号锦城大厦5楼\",\n" +
                "    \"purchaser_bank_name\": \"中国工商银行股份有限公司成都金牛支行\",\n" +
                "    \"purchaser_bank_account\": \"4402243009004997977\",\n" +
                "    \"invoice_type\": \"04\",\n" +
                "    \"price_method\": 0,\n" +
                "    \"amount_with_tax\": -3600.230000,\n" +
                "    \"amount_without_tax\": -3302.960000,\n" +
                "    \"tax_amount\": -297.270000,\n" +
                "    \"create_time\": \"2021-11-02 15:50:47.573\",\n" +
                "    \"create_user\": 0,\n" +
                "    \"update_time\": \"2021-11-02 15:50:47.573\",\n" +
                "    \"update_user\": 0,\n" +
                "    \"remark\": \"\",\n" +
                "    \"available_amount\": -3302.960000,\n" +
                "    \"batch_no\": \"\",\n" +
                "    \"settlement_no\": \"settlementNo6918666239803393\",\n" +
                "    \"settlement_type\": 3,\n" +
                "    \"settlement_status\": 10,\n" +
                "    \"tax_rate\": 0.09,\n" +
                "    \"business_type\": 1\n" +
                "  }\n"
                ,TXfSettlementEntity.class);
        entity.setId(10010101L);
        entity.setSettlementStatus(TXfSettlementStatusEnum.DESTROY.getCode());
        entity.setInvoiceType("01");
        entity.setSettlementNo("010101010101");
        entity.setSellerNo("test01");
        entity.setPurchaserNo("test10");
        tXfSettlementDao.insert(entity);

        tXfSettlementDao.updateById(entity);
    }
}