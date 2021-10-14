package com.xforceplus.wapp.modules.rednotification.service;

import com.google.gson.Gson;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.WappApplication;
import com.xforceplus.wapp.modules.rednotification.model.taxware.ApplyRequest;
import com.xforceplus.wapp.modules.rednotification.model.taxware.GetTerminalResponse;
import com.xforceplus.wapp.modules.rednotification.model.taxware.TaxWareResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;



public class TaxWareServiceTest extends BaseUnitTest {
    @Autowired
    TaxWareService taxWareService;

    Gson gson = new Gson();

    @Test
    public void testGetTerminal() {
        GetTerminalResponse terminal = taxWareService.getTerminal("91420111271850146W");
        assert terminal!=null;
    }

    @Test
    public void testApplyRedInfo() {
        String reqJson ="{\n" +
                "\t\"deviceUn\": \"ZAFUU9ZG\",\n" +
                "\t\"redInfoList\": [{\n" +
                "\t\t\"amount\": {\n" +
                "\t\t\t\"amountWithoutTax\": -100.000000,\n" +
                "\t\t\t\"amountWithTax\": -103.000000,\n" +
                "\t\t\t\"taxAmount\": -3.000000\n" +
                "\t\t},\n" +
                "\t\t\"sellerTaxCode\": \"91420111271850146W\",\n" +
                "\t\t\"originalInvoiceType\": \"s\",\n" +
                "\t\t\"sellerName\": \"武汉市丰业电器有限公司\",\n" +
                "\t\t\"pid\": \"5854698276003504133\",\n" +
                "\t\t\"originalInvoiceNo\": \"12345678\",\n" +
                "\t\t\"purchaserTaxCode\": \"91652900761142134X\",\n" +
                "\t\t\"purchaserName\": \"中国电信股份有限公司阿克苏分公司\",\n" +
                "\t\t\"dupTaxFlag\": \"0\",\n" +
                "\t\t\"details\": [{\n" +
                "\t\t\t\"production\": {\n" +
                "\t\t\t\t\"productionCode\": \"1030204010000000000\",\n" +
                "\t\t\t\t\"unitName\": \"水泥\",\n" +
                "\t\t\t\t\"specification\": \"水泥\",\n" +
                "\t\t\t\t\"productionName\": \"*乳制品*水泥\"\n" +
                "\t\t\t},\n" +
                "\t\t\t\"detailAmount\": {\n" +
                "\t\t\t\t\"amountWithoutTax\": -100.000000,\n" +
                "\t\t\t\t\"taxAmount\": -3.000000\n" +
                "\t\t\t},\n" +
                "\t\t\t\"tax\": {\n" +
                "\t\t\t\t\"taxRate\": 0.03,\n" +
                "\t\t\t\t\"preferentialTax\": false,\n" +
                "\t\t\t\t\"zeroTax\": \"\",\n" +
                "\t\t\t\t\"taxPolicy\": \"\"\n" +
                "\t\t\t}\n" +
                "\t\t}],\n" +
                "\t\t\"oilMemo\": \"\",\n" +
                "\t\t\"originalInvoiceDate\": \"20200903\",\n" +
                "\t\t\"taxCodeVersion\": \"39.0\",\n" +
                "\t\t\"applicationReason\": \"2\",\n" +
                "\t\t\"originalInvoiceCode\": \"1236547887\"\n" +
                "\t}],\n" +
                "\t\"terminalUn\": \"AZCB8UWK\",\n" +
                "\t\"serialNo\": \"5854698276003504132\"\n" +
                "}";
        ApplyRequest applyRequest = gson.fromJson(reqJson, ApplyRequest.class);
        TaxWareResponse taxWareResponse = taxWareService.applyRedInfo(applyRequest);
    }
}