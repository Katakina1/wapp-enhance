package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.deduct.model.DeductBillItemModel;
import com.xforceplus.wapp.modules.settlement.dto.SettlementItemResponse;
import com.xforceplus.wapp.modules.statement.models.PreInvoice;
import com.xforceplus.wapp.repository.dao.TXfDeductPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfPreBillDetailDao;
import com.xforceplus.wapp.repository.daoExt.BillDeductQueryExtDao;
import com.xforceplus.wapp.repository.entity.TXfBillItemRefDetailExtEntity;
import com.xforceplus.wapp.repository.entity.TXfDeductPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreBillDetailEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BillRefQueryServiceTest {

    @Mock
    private BillDeductQueryExtDao mockBillDeductQueryExtDao;
    @Mock
    private TXfDeductPreInvoiceDao mockTXfDeductPreInvoiceDao;
    @Mock
    private TXfPreBillDetailDao mockTXfPreBillDetailDao;

    @InjectMocks
    private BillRefQueryService billRefQueryServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testFullBillItemRedNotification() {

        final DeductBillItemModel deductBillItemModel = new DeductBillItemModel();
        deductBillItemModel.setId(0L);
        deductBillItemModel.setBusinessNo("businessNo");
        deductBillItemModel.setItemNo("itemNo");
        deductBillItemModel.setCnDesc("cnDesc");
        deductBillItemModel.setGoodsTaxNo("goodsTaxNo");
        deductBillItemModel.setQuantity(new BigDecimal("0.00"));
        deductBillItemModel.setPrice(new BigDecimal("0.00"));
        deductBillItemModel.setAmountWithoutTax(new BigDecimal("0.00"));
        deductBillItemModel.setAmountWithTax(new BigDecimal("0.00"));
        deductBillItemModel.setTaxRate(new BigDecimal("0.00"));
        final List<DeductBillItemModel> deductBillItemList = Arrays.asList(deductBillItemModel);

        final TXfBillItemRefDetailExtEntity tXfBillItemRefDetailExtEntity = new TXfBillItemRefDetailExtEntity();
        tXfBillItemRefDetailExtEntity.setPreInvoiceId(0L);
        tXfBillItemRefDetailExtEntity.setDeductItemId(0L);
        tXfBillItemRefDetailExtEntity.setSettlementItemId(0L);
        tXfBillItemRefDetailExtEntity.setPreInvoiceItemId(0L);
        final List<TXfBillItemRefDetailExtEntity> tXfBillItemRefDetailExtEntities = Arrays.asList(tXfBillItemRefDetailExtEntity);
        when(mockBillDeductQueryExtDao.getBillItemRefDetail(Arrays.asList(0L))).thenReturn(tXfBillItemRefDetailExtEntities);

        final TXfDeductPreInvoiceEntity tXfDeductPreInvoiceEntity = new TXfDeductPreInvoiceEntity();
        tXfDeductPreInvoiceEntity.setId(0L);
        tXfDeductPreInvoiceEntity.setDeductId(0L);
        tXfDeductPreInvoiceEntity.setRedNotificationId(0L);
        tXfDeductPreInvoiceEntity.setApplyStatus(0);
        tXfDeductPreInvoiceEntity.setAmountWithoutTax(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setDeleted(0);
        tXfDeductPreInvoiceEntity.setAmountWithTax(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setTaxAmount(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setTaxDiff(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setRedNotificationNo("redNotificationNo");
        final List<TXfDeductPreInvoiceEntity> tXfDeductPreInvoiceEntities = Arrays.asList(tXfDeductPreInvoiceEntity);
        when(mockBillDeductQueryExtDao.getBillRefByPreInvoiceIds(Arrays.asList(0L))).thenReturn(tXfDeductPreInvoiceEntities);


    }

    @Test
    void testFullPreInvoiceRedNotification() {

        final PreInvoice preInvoice = new PreInvoice();
        preInvoice.setId(0L);
        preInvoice.setSettlementNo("settlementNo");
        preInvoice.setPurchaserNo("purchaserNo");
        preInvoice.setPurchaserName("purchaserName");
        preInvoice.setPurchaserTaxNo("purchaserTaxNo");
        preInvoice.setPurchaserTel("purchaserTel");
        preInvoice.setPurchaserAddress("purchaserAddress");
        preInvoice.setPurchaserBankName("purchaserBankName");
        preInvoice.setPurchaserBankAccount("purchaserBankAccount");
        preInvoice.setSellerNo("sellerNo");
        final List<PreInvoice> invoices = Arrays.asList(preInvoice);

        final TXfDeductPreInvoiceEntity tXfDeductPreInvoiceEntity = new TXfDeductPreInvoiceEntity();
        tXfDeductPreInvoiceEntity.setId(0L);
        tXfDeductPreInvoiceEntity.setDeductId(0L);
        tXfDeductPreInvoiceEntity.setRedNotificationId(0L);
        tXfDeductPreInvoiceEntity.setApplyStatus(0);
        tXfDeductPreInvoiceEntity.setAmountWithoutTax(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setDeleted(0);
        tXfDeductPreInvoiceEntity.setAmountWithTax(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setTaxAmount(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setTaxDiff(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setRedNotificationNo("redNotificationNo");
        final List<TXfDeductPreInvoiceEntity> tXfDeductPreInvoiceEntities = Arrays.asList(tXfDeductPreInvoiceEntity);
        when(mockBillDeductQueryExtDao.getBillRefByPreInvoiceIds(Arrays.asList(0L))).thenReturn(tXfDeductPreInvoiceEntities);


    }

    @Test
    void testFullSettlementItem() {

        final SettlementItemResponse settlementItemResponse = new SettlementItemResponse();
        settlementItemResponse.setSettlementNo("settlementNo");
        settlementItemResponse.setSalesbillItemNo("salesbillItemNo");
        settlementItemResponse.setItemCode("itemCode");
        settlementItemResponse.setItemName("itemName");
        settlementItemResponse.setItemShortName("itemShortName");
        settlementItemResponse.setTaxName("taxName");
        settlementItemResponse.setItemSpec("itemSpec");
        settlementItemResponse.setUnitPriceWithTax(new BigDecimal("0.00"));
        settlementItemResponse.setUnitPrice(new BigDecimal("0.00"));
        settlementItemResponse.setQuantity(new BigDecimal("0.00"));
        final List<SettlementItemResponse> itemEntities = Arrays.asList(settlementItemResponse);

        final TXfPreBillDetailEntity tXfPreBillDetailEntity = new TXfPreBillDetailEntity();
        tXfPreBillDetailEntity.setId(0L);
        tXfPreBillDetailEntity.setSettlementId(0L);
        tXfPreBillDetailEntity.setSettlementItemId(0L);
        tXfPreBillDetailEntity.setSettlementNo("settlementNo");
        tXfPreBillDetailEntity.setSettlementItemNo("settlementItemNo");
        tXfPreBillDetailEntity.setPreInvoiceId(0L);
        tXfPreBillDetailEntity.setPreInvoiceItemId(0L);
        tXfPreBillDetailEntity.setAmountWithTax(new BigDecimal("0.00"));
        tXfPreBillDetailEntity.setAmountWithoutTax(new BigDecimal("0.00"));
        tXfPreBillDetailEntity.setTaxAmount(new BigDecimal("0.00"));
        final List<TXfPreBillDetailEntity> preBillDetailEntities = Arrays.asList(tXfPreBillDetailEntity);
        when(mockTXfPreBillDetailDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(preBillDetailEntities);

        final TXfDeductPreInvoiceEntity tXfDeductPreInvoiceEntity = new TXfDeductPreInvoiceEntity();
        tXfDeductPreInvoiceEntity.setId(0L);
        tXfDeductPreInvoiceEntity.setDeductId(0L);
        tXfDeductPreInvoiceEntity.setRedNotificationId(0L);
        tXfDeductPreInvoiceEntity.setApplyStatus(0);
        tXfDeductPreInvoiceEntity.setAmountWithoutTax(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setDeleted(0);
        tXfDeductPreInvoiceEntity.setAmountWithTax(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setTaxAmount(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setTaxDiff(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setRedNotificationNo("redNotificationNo");
        final List<TXfDeductPreInvoiceEntity> tXfDeductPreInvoiceEntities = Arrays.asList(tXfDeductPreInvoiceEntity);
        when(mockBillDeductQueryExtDao.getBillRefByPreInvoiceIds(Arrays.asList(0L))).thenReturn(tXfDeductPreInvoiceEntities);

    }

    @Test
    void testFullAgreementBillRedNotification() {

        final DeductListResponse deductListResponse = new DeductListResponse();
        deductListResponse.setInvoiceCount(0);
        deductListResponse.setBillNo("billNo");
        deductListResponse.setBusinessType(0);
        deductListResponse.setRefSettlementNo("refSettlementNo");
        deductListResponse.setVerdictDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        deductListResponse.setDeductDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        deductListResponse.setDeductInvoice("deductInvoice");
        deductListResponse.setTaxRate(new BigDecimal("0.00"));
        deductListResponse.setAgreementReasonCode("agreementReasonCode");
        deductListResponse.setAgreementReference("agreementReference");
        final List<DeductListResponse> list = Arrays.asList(deductListResponse);

        final TXfDeductPreInvoiceEntity tXfDeductPreInvoiceEntity = new TXfDeductPreInvoiceEntity();
        tXfDeductPreInvoiceEntity.setId(0L);
        tXfDeductPreInvoiceEntity.setDeductId(0L);
        tXfDeductPreInvoiceEntity.setRedNotificationId(0L);
        tXfDeductPreInvoiceEntity.setApplyStatus(0);
        tXfDeductPreInvoiceEntity.setAmountWithoutTax(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setDeleted(0);
        tXfDeductPreInvoiceEntity.setAmountWithTax(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setTaxAmount(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setTaxDiff(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setRedNotificationNo("redNotificationNo");
        final List<TXfDeductPreInvoiceEntity> tXfDeductPreInvoiceEntities = Arrays.asList(tXfDeductPreInvoiceEntity);
        when(mockBillDeductQueryExtDao.getBillRefByBillIds(Arrays.asList(0L))).thenReturn(tXfDeductPreInvoiceEntities);

        billRefQueryServiceUnderTest.fullAgreementBillRedNotification(list);

    }

    @Test
    void testForListBillRefByPreInvoiceIds() {

        final List<Long> preInvoiceIds = Arrays.asList(0L);
        final TXfDeductPreInvoiceEntity tXfDeductPreInvoiceEntity = new TXfDeductPreInvoiceEntity();
        tXfDeductPreInvoiceEntity.setId(0L);
        tXfDeductPreInvoiceEntity.setDeductId(0L);
        tXfDeductPreInvoiceEntity.setRedNotificationId(0L);
        tXfDeductPreInvoiceEntity.setApplyStatus(0);
        tXfDeductPreInvoiceEntity.setAmountWithoutTax(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setDeleted(0);
        tXfDeductPreInvoiceEntity.setAmountWithTax(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setTaxAmount(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setTaxDiff(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity.setRedNotificationNo("redNotificationNo");
        final List<TXfDeductPreInvoiceEntity> expectedResult = Arrays.asList(tXfDeductPreInvoiceEntity);

        final TXfDeductPreInvoiceEntity tXfDeductPreInvoiceEntity1 = new TXfDeductPreInvoiceEntity();
        tXfDeductPreInvoiceEntity1.setId(0L);
        tXfDeductPreInvoiceEntity1.setDeductId(0L);
        tXfDeductPreInvoiceEntity1.setRedNotificationId(0L);
        tXfDeductPreInvoiceEntity1.setApplyStatus(0);
        tXfDeductPreInvoiceEntity1.setAmountWithoutTax(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity1.setDeleted(0);
        tXfDeductPreInvoiceEntity1.setAmountWithTax(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity1.setTaxAmount(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity1.setTaxDiff(new BigDecimal("0.00"));
        tXfDeductPreInvoiceEntity1.setRedNotificationNo("redNotificationNo");
        final List<TXfDeductPreInvoiceEntity> tXfDeductPreInvoiceEntities = Arrays.asList(tXfDeductPreInvoiceEntity1);
        when(mockBillDeductQueryExtDao.getBillRefByPreInvoiceIds(Arrays.asList(0L))).thenReturn(tXfDeductPreInvoiceEntities);

        final List<TXfDeductPreInvoiceEntity> result = billRefQueryServiceUnderTest.forListBillRefByPreInvoiceIds(preInvoiceIds);

        assertThat(result.size()).isEqualTo(expectedResult.size());
    }
}
