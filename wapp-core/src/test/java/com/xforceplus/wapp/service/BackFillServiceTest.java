package com.xforceplus.wapp.service;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.backFill.model.VerificationBack;
import com.xforceplus.wapp.modules.backFill.service.EInvoiceMatchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by SunShiyong on 2021/10/18.
 */
@Slf4j
public class BackFillServiceTest extends BaseUnitTest {

    @Autowired
    EInvoiceMatchService eInvoiceMatchService;
    @Test
    public void testAfterVerify() {
        String back = "{\"result\":{\"invoiceType\":\"ce\",\"invoiceDetails\":[{\"cargoName\":\"*肉及肉制品*MM美国谷饲牛霖.\",\"itemSpec\":\"1X1kg.\",\"quantityUnit\":\"件\",\"quantity\":\"1.84389000\",\"taxRate\":\"0.00\",\"zeroTax\":\"1\",\"unitPrice\":\"83.60043170\",\"amountWithoutTax\":\"154.15\",\"taxAmount\":\"0.00\",\"amountWithTax\":\"154.15\"},{\"cargoName\":\"*纸制品*MM斑布竹纤维本色面巾纸.\",\"itemSpec\":\"24X130'S\",\"quantityUnit\":\"袋\",\"quantity\":\"1.00000000\",\"taxRate\":\"13\",\"zeroTax\":\" \",\"unitPrice\":\"57.43000000\",\"amountWithoutTax\":\"57.43\",\"taxAmount\":\"7.47\",\"amountWithTax\":\"64.90\"},{\"cargoName\":\"*水果*都乐超甜蕉一把.\",\"itemSpec\":\"1X1.4kg\",\"quantityUnit\":\"把\",\"quantity\":\"1.00000000\",\"taxRate\":\"9\",\"zeroTax\":\" \",\"unitPrice\":\"18.26000000\",\"amountWithoutTax\":\"18.26\",\"taxAmount\":\"1.64\",\"amountWithTax\":\"19.90\"},{\"cargoName\":\"*糖果类食品*I.MM棒棒糖480G.\",\"itemSpec\":\"1X480g.\",\"quantityUnit\":\"盒\",\"quantity\":\"1.00000000\",\"taxRate\":\"13\",\"zeroTax\":\" \",\"unitPrice\":\"60.18000000\",\"amountWithoutTax\":\"60.18\",\"taxAmount\":\"7.82\",\"amountWithTax\":\"68.00\"},{\"cargoName\":\"*乳制品*蒙牛消健益生菌酸牛奶原味.\",\"itemSpec\":\"24X100g.\",\"quantityUnit\":\"盒\",\"quantity\":\"1.00000000\",\"taxRate\":\"13\",\"zeroTax\":\" \",\"unitPrice\":\"26.46000000\",\"amountWithoutTax\":\"26.46\",\"taxAmount\":\"3.44\",\"amountWithTax\":\"29.90\"},{\"cargoName\":\"*焙烤食品*曼可顿超醇面包.\",\"itemSpec\":\"2X400g\",\"quantityUnit\":\"组\",\"quantity\":\"1.00000000\",\"taxRate\":\"13\",\"zeroTax\":\" \",\"unitPrice\":\"17.52000000\",\"amountWithoutTax\":\"17.52\",\"taxAmount\":\"2.28\",\"amountWithTax\":\"19.80\"}],\"invoiceMain\":{\"taskId\":\"41d45b13-df09-4211-b024-cf252425b8a0\",\"invoiceCode\":\"044002100411\",\"invoiceNo\":\"09238197\",\"invoiceType\":\"ce\",\"purchaserTaxNo\":\"31460000MD0234170W\",\"purchaserName\":\"广东盈隆(海口)律师事务所\",\"purchaserAddrTel\":\"13600450599\",\"purchaserBankInfo\":\"\",\"sellerTaxNo\":\"914401016852269688\",\"sellerName\":\"沃尔玛（广东）百货有限公司\",\"sellerAddrTel\":\"广州市天河区黄埔大道东657、659、661号 020-31388712\",\"sellerBankInfo\":\"中国银行广州五羊新城支行 639274237796\",\"paperDrewDate\":\"20210928\",\"amountWithoutTax\":\"334\",\"taxAmount\":\"22.65\",\"amountWithTax\":\"356.65\",\"checkCode\":\"52112764761122566512\",\"machineCode\":\"661022829977\",\"remark\":\"订单号[CPCSM651809163272676487649]\",\"status\":\"1\",\"redFlag\":\"0\",\"ctStatus\":\"\",\"cpyStatus\":\"0\",\"checkNumber\":\"4\",\"checkTime\":\"2021-10-19 17:33:58\",\"goodsListFlag\":\"0\",\"dqCode\":\"4400\",\"dqName\":\"广东\",\"ofdDownloadUrl\":\"\",\"pdfDownloadUrl\":\"\",\"ofdPreviewUrl\":\"\",\"ofdImageUrl\":\"\"}},\"code\":\"TXWRVC0001\",\"message\":\"查验成功\",\"taskId\":\"41d45b13-df09-4211-b024-cf252425b8a0\"}";
        VerificationBack verificationBack = JSONObject.parseObject(back, VerificationBack.class);
        eInvoiceMatchService.matchResultAfterVerify(verificationBack,null);
    }



}
