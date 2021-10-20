package com.xforceplus.wapp.handle;


import com.google.common.collect.Maps;
import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.BaseUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class InvoiceHandlerTest extends BaseUnitTest {
    @Autowired
    private InvoiceHandler taxWareInvoiceHandler;

    @Test
    public void testHandler() {
        String payloadString = "{\"data\":{\"deposeUserName\":\"\",\"purchaserBankAccount\":\"\",\"deposeTime\":\"\",\"sellerAddress\":\"秦皇岛市海港区海滨路35号0335-3092241\",\"createUserName\":\"\",\"cipherText\":\"\",\"sellerAddrTel\":\"秦皇岛市海港区海滨路35号0335-3092241\",\"veriResponseTime\":\"\",\"invoiceType\":\"01-10-01\",\"purchaserCompanyId\":4422612870668879849,\"id\":481791903369097216,\"invoiceNo\":\"06148276\",\"purchaserInvoiceItemVOList\":[],\"redFlag\":1,\"channelSource\":5,\"purchaserTenantCode\":\"\",\"machineCode\":\"\",\"invoiceOrigin\":2,\"updateUserName\":\"\",\"turnOutPeriod\":\"\",\"authResponseTime\":\"\",\"invoiceCode\":\"1616253671\",\"purchaserName\":\"秦皇岛金海特种食用油工业有限公司\",\"checkCode\":\"\",\"sellerTenantId\":1257915377817145344,\"taxCategory\":\"01\",\"purchaserBankName\":\"\",\"taxRate\":\"9\",\"purchaserTenantId\":4882809030005538709,\"authBussiDate\":\"\",\"sellerTaxNo\":\"91130300723397611J\",\"checkTime\":\"\",\"authRequestTime\":\"\",\"purchaserTaxNo\":\"9113030074540443XP\",\"veriRequestTime\":\"\",\"paperDrewDate\":\"20211016\",\"checkerName\":\"\",\"taxAmount\":26206.02,\"sellerBankName\":\"农行秦皇岛迎宾支行50809001040004625\",\"generateChannel\":16,\"status\":1,\"industryIssueType\":\"10\",\"purchaserOrgId\":4422612870668879852,\"invoicerName\":\"\",\"authSyncStatus\":1,\"authTaxPeriod\":\"\",\"invoiceExtend\":{\"seller_company_no\":\"GG\",\"ledger_identifier\":\"0\",\"new_purchaser_tax_no\":\"9113030074540443XP\",\"image_flag\":\"1\",\"purchaser_company_code\":\"GS\",\"match_status_desc\":\"未保存\",\"purchaser_company_no\":\"GS\",\"check_code\":\"\",\"seller_company_code\":\"GG\",\"accounting_status\":\"0\",\"accounting_period\":\"\",\"accounting_date\":\"\",\"purchaser_tenant_ids\":\"#1203260024735584256#1257915377817145344#4418178918234191798#\"},\"sellerName\":\"秦皇岛金海粮油工业有限公司\",\"authUse\":0,\"remark\":\"销售订单号1138010107提单号1135822214系统发票号1175768663客户采购订单号1240006931会计凭证号9070048795\",\"authTime\":\"\",\"sellerCompanyId\":4422612870668879823,\"purchaserAddress\":\"秦皇岛市海港区海滨路35号0335-3092241\",\"redNotificationNo\":\"\",\"elEnsureTime\":\"\",\"sellerTel\":\"\",\"authSyncTime\":\"\",\"cashierName\":\"\",\"effectiveTaxAmount\":26206.02,\"amountWithoutTax\":4498,\"sellerOrgId\":4437605845502951546,\"turnOutStatus\":0,\"sellerBankNameAccount\":\"农行秦皇岛迎宾支行50809001040004625\",\"authStatus\":0,\"updateTime\":\"2021-10-19 10:01:58.925\",\"purchaserTel\":\"\",\"hashValue\":\"\",\"businessExtend\":{\"yi_hai_kerry_purchaser\":{\"invoiceAllocationStatus\":\"0\",\"earlyWarning\":\"0\",\"scanTime\":\"1634538706000\",\"sellerNo\":\"9010000011\",\"billCode\":\"AP21101800133\",\"postingDate\":\"\",\"imageToExist\":\"1\",\"dtIspaper\":\"1\",\"purchaserNo\":\"GS\",\"salesbillNo\":\"NHLILYBILL20211018001\",\"invoiceType\":\"s\",\"id\":\"\",\"identifyFlag\":\"0\",\"accountingPeriod\":\"\",\"businessType\":\"0\",\"invoiceAllocationStatusDesc\":\"未保存\",\"_sync_timestamp\":\"1634608918809\",\"bookkeepingStatus\":\"0\"}},\"invoiceMedium\":\"01\",\"createTime\":\"2021-10-19 10:01:58.925\",\"purchaserAddrTel\":\"秦皇岛市海港区海滨路35号0335-3092241\",\"sellerBankAccount\":\"\",\"veriStatus\":0,\"purchaserBankNameAccount\":\"\",\"amountWithTax\":4902.82,\"synchronizeTime\":\"2021-10-19 10:01:58.809\",\"invoiceColor\":1},\"timestamp\":\"2021-10-19 10:01:59.401\"}";
        taxWareInvoiceHandler.handle(new SealedMessage(
                new SealedMessage.Header("111","purchaserInvoiceSync", Maps.newHashMap()),
                new SealedMessage.Payload(payloadString)));
    }

}