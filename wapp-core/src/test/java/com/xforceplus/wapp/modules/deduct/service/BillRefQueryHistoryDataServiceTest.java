package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductBaseResponse;
import com.xforceplus.wapp.modules.deduct.model.DeductBillItemModel;
import com.xforceplus.wapp.modules.statement.models.PreInvoice;
import com.xforceplus.wapp.repository.dao.TXfBillDeductDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfRedNotificationDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BillRefQueryHistoryDataServiceTest {

    private BillRefQueryHistoryDataService billRefQueryHistoryDataServiceUnderTest;

    @BeforeEach
    void setUp() {
        billRefQueryHistoryDataServiceUnderTest = spy(new BillRefQueryHistoryDataService());
        billRefQueryHistoryDataServiceUnderTest.tXfBillDeductDao = mock(TXfBillDeductDao.class);
        billRefQueryHistoryDataServiceUnderTest.tXfSettlementDao = mock(TXfSettlementDao.class);
        billRefQueryHistoryDataServiceUnderTest.tXfPreInvoiceDao = mock(TXfPreInvoiceDao.class);
        billRefQueryHistoryDataServiceUnderTest.tXfRedNotificationDao = mock(TXfRedNotificationDao.class);
    }

    @Test
    void testFullBillRedNotification() {

        final QueryDeductBaseResponse queryDeductBaseResponse = new QueryDeductBaseResponse();
        queryDeductBaseResponse.setId(0L);
        queryDeductBaseResponse.setRefSettlementNo("refSettlementNo");
        queryDeductBaseResponse.setBusinessNo("businessNo");
        queryDeductBaseResponse.setBusinessType(0);
        queryDeductBaseResponse.setSellerNo("sellerNo");
        queryDeductBaseResponse.setSellerName("sellerName");
        queryDeductBaseResponse.setDeductDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        queryDeductBaseResponse.setPurchaserNo("purchaserNo");
        queryDeductBaseResponse.setPurchaserName("purchaserName");
        queryDeductBaseResponse.setAgreementMemo("agreementMemo");
        queryDeductBaseResponse.setCreateTime(new Date());
        final List<? extends QueryDeductBaseResponse> list = Arrays.asList(queryDeductBaseResponse);
        final List<String> setRedInfoTabs = Arrays.asList("value");

        final TXfBillDeductEntity tXfBillDeductEntity = new TXfBillDeductEntity();
        tXfBillDeductEntity.setBusinessNo("businessNo");
        tXfBillDeductEntity.setBusinessType(0);
        tXfBillDeductEntity.setRefSettlementNo("refSettlementNo");
        tXfBillDeductEntity.setVerdictDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        tXfBillDeductEntity.setDeductDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        tXfBillDeductEntity.setDeductInvoice("deductInvoice");
        tXfBillDeductEntity.setTaxRate(new BigDecimal("0.00"));
        tXfBillDeductEntity.setAgreementReasonCode("agreementReasonCode");
        tXfBillDeductEntity.setAgreementReference("agreementReference");
        tXfBillDeductEntity.setAgreementTaxCode("agreementTaxCode");
        when(billRefQueryHistoryDataServiceUnderTest.tXfBillDeductDao.selectById(0L)).thenReturn(tXfBillDeductEntity);
        final TXfSettlementEntity tXfSettlementEntity = new TXfSettlementEntity();
        tXfSettlementEntity.setSellerNo("sellerNo");
        tXfSettlementEntity.setSellerName("sellerName");
        tXfSettlementEntity.setSellerTaxNo("sellerTaxNo");
        tXfSettlementEntity.setSellerTel("sellerTel");
        tXfSettlementEntity.setSellerAddress("sellerAddress");
        tXfSettlementEntity.setSellerBankName("sellerBankName");
        tXfSettlementEntity.setSellerBankAccount("sellerBankAccount");
        tXfSettlementEntity.setPurchaserNo("purchaserNo");
        tXfSettlementEntity.setPurchaserName("purchaserName");
        tXfSettlementEntity.setPurchaserTaxNo("purchaserTaxNo");
        tXfSettlementEntity.setCreateTime(new Date());
        final List<TXfSettlementEntity> settlementEntityList = Arrays.asList(tXfSettlementEntity);
        when(billRefQueryHistoryDataServiceUnderTest.tXfSettlementDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(settlementEntityList);

        final TXfPreInvoiceEntity preInvoiceEntity = new TXfPreInvoiceEntity();
        preInvoiceEntity.setSettlementNo("settlementNo");
        preInvoiceEntity.setPurchaserNo("purchaserNo");
        preInvoiceEntity.setPurchaserName("purchaserName");
        preInvoiceEntity.setPurchaserTaxNo("purchaserTaxNo");
        preInvoiceEntity.setPurchaserTel("purchaserTel");
        preInvoiceEntity.setPurchaserAddress("purchaserAddress");
        preInvoiceEntity.setPurchaserBankName("purchaserBankName");
        preInvoiceEntity.setPurchaserBankAccount("purchaserBankAccount");
        preInvoiceEntity.setSellerNo("sellerNo");
        preInvoiceEntity.setSellerTaxNo("sellerTaxNo");
        final List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = Arrays.asList(preInvoiceEntity);
        when(billRefQueryHistoryDataServiceUnderTest.tXfPreInvoiceDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(tXfPreInvoiceEntities);

        final TXfRedNotificationEntity notificationEntity = new TXfRedNotificationEntity();
        notificationEntity.setId(0L);
        notificationEntity.setInvoiceDate("invoiceDate");
        notificationEntity.setPid("pid");
        notificationEntity.setSerialNo("serialNo");
        notificationEntity.setUserRole(0);
        notificationEntity.setApplyType(0);
        notificationEntity.setApplyRemark("applyRemark");
        notificationEntity.setApplyingStatus(0);
        notificationEntity.setApproveStatus(0);
        notificationEntity.setRedNotificationNo("redNotificationNo");
        final List<TXfRedNotificationEntity> tXfRedNotificationEntities = Arrays.asList(notificationEntity);
        when(billRefQueryHistoryDataServiceUnderTest.tXfRedNotificationDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(tXfRedNotificationEntities);
        doReturn(999L).when(billRefQueryHistoryDataServiceUnderTest).getHistoryTime();
        billRefQueryHistoryDataServiceUnderTest.fullBillRedNotification(list, setRedInfoTabs);

    }

    @Test
    void testFullBillItemRedNotification() {

        final TXfBillDeductEntity bill = new TXfBillDeductEntity();
        bill.setBusinessNo("businessNo");
        bill.setBusinessType(0);
        bill.setRefSettlementNo("refSettlementNo");
        bill.setVerdictDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        bill.setDeductDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        bill.setDeductInvoice("deductInvoice");
        bill.setTaxRate(new BigDecimal("0.00"));
        bill.setAgreementReasonCode("agreementReasonCode");
        bill.setAgreementReference("agreementReference");
        bill.setAgreementTaxCode("agreementTaxCode");
        bill.setCreateTime(new Date());
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

        final TXfBillDeductEntity tXfBillDeductEntity = new TXfBillDeductEntity();
        tXfBillDeductEntity.setBusinessNo("businessNo");
        tXfBillDeductEntity.setBusinessType(0);
        tXfBillDeductEntity.setRefSettlementNo("refSettlementNo");
        tXfBillDeductEntity.setVerdictDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        tXfBillDeductEntity.setDeductDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        tXfBillDeductEntity.setDeductInvoice("deductInvoice");
        tXfBillDeductEntity.setTaxRate(new BigDecimal("0.00"));
        tXfBillDeductEntity.setAgreementReasonCode("agreementReasonCode");
        tXfBillDeductEntity.setAgreementReference("agreementReference");
        tXfBillDeductEntity.setAgreementTaxCode("agreementTaxCode");
        when(billRefQueryHistoryDataServiceUnderTest.tXfBillDeductDao.selectById(0L)).thenReturn(tXfBillDeductEntity);

        final TXfSettlementEntity tXfSettlementEntity = new TXfSettlementEntity();
        tXfSettlementEntity.setSellerNo("sellerNo");
        tXfSettlementEntity.setSellerName("sellerName");
        tXfSettlementEntity.setSellerTaxNo("sellerTaxNo");
        tXfSettlementEntity.setSellerTel("sellerTel");
        tXfSettlementEntity.setSellerAddress("sellerAddress");
        tXfSettlementEntity.setSellerBankName("sellerBankName");
        tXfSettlementEntity.setSellerBankAccount("sellerBankAccount");
        tXfSettlementEntity.setPurchaserNo("purchaserNo");
        tXfSettlementEntity.setPurchaserName("purchaserName");
        tXfSettlementEntity.setPurchaserTaxNo("purchaserTaxNo");
        final List<TXfSettlementEntity> settlementEntityList = Arrays.asList(tXfSettlementEntity);
        when(billRefQueryHistoryDataServiceUnderTest.tXfSettlementDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(settlementEntityList);

        final TXfPreInvoiceEntity preInvoiceEntity = new TXfPreInvoiceEntity();
        preInvoiceEntity.setSettlementNo("settlementNo");
        preInvoiceEntity.setPurchaserNo("purchaserNo");
        preInvoiceEntity.setPurchaserName("purchaserName");
        preInvoiceEntity.setPurchaserTaxNo("purchaserTaxNo");
        preInvoiceEntity.setPurchaserTel("purchaserTel");
        preInvoiceEntity.setPurchaserAddress("purchaserAddress");
        preInvoiceEntity.setPurchaserBankName("purchaserBankName");
        preInvoiceEntity.setPurchaserBankAccount("purchaserBankAccount");
        preInvoiceEntity.setSellerNo("sellerNo");
        preInvoiceEntity.setSellerTaxNo("sellerTaxNo");
        final List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = Arrays.asList(preInvoiceEntity);
        when(billRefQueryHistoryDataServiceUnderTest.tXfPreInvoiceDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(tXfPreInvoiceEntities);

        final TXfRedNotificationEntity notificationEntity = new TXfRedNotificationEntity();
        notificationEntity.setId(0L);
        notificationEntity.setInvoiceDate("invoiceDate");
        notificationEntity.setPid("pid");
        notificationEntity.setSerialNo("serialNo");
        notificationEntity.setUserRole(0);
        notificationEntity.setApplyType(0);
        notificationEntity.setApplyRemark("applyRemark");
        notificationEntity.setApplyingStatus(0);
        notificationEntity.setApproveStatus(0);
        notificationEntity.setRedNotificationNo("redNotificationNo");
        final List<TXfRedNotificationEntity> tXfRedNotificationEntities = Arrays.asList(notificationEntity);
        when(billRefQueryHistoryDataServiceUnderTest.tXfRedNotificationDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(tXfRedNotificationEntities);
        doReturn(999L).when(billRefQueryHistoryDataServiceUnderTest).getHistoryTime();
        billRefQueryHistoryDataServiceUnderTest.fullBillItemRedNotification(bill, deductBillItemList);

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
        preInvoice.setCreateTime(9999L);
        final List<PreInvoice> preInvoices = Arrays.asList(preInvoice);

        final TXfRedNotificationEntity notificationEntity = new TXfRedNotificationEntity();
        notificationEntity.setId(0L);
        notificationEntity.setInvoiceDate("invoiceDate");
        notificationEntity.setPid("pid");
        notificationEntity.setSerialNo("serialNo");
        notificationEntity.setUserRole(0);
        notificationEntity.setApplyType(0);
        notificationEntity.setApplyRemark("applyRemark");
        notificationEntity.setApplyingStatus(0);
        notificationEntity.setApproveStatus(0);
        notificationEntity.setRedNotificationNo("redNotificationNo");
        final List<TXfRedNotificationEntity> tXfRedNotificationEntities = Arrays.asList(notificationEntity);
        when(billRefQueryHistoryDataServiceUnderTest.tXfRedNotificationDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(tXfRedNotificationEntities);
        doReturn(999L).when(billRefQueryHistoryDataServiceUnderTest).getHistoryTime();
        billRefQueryHistoryDataServiceUnderTest.fullPreInvoiceRedNotification(preInvoices);

    }
}
