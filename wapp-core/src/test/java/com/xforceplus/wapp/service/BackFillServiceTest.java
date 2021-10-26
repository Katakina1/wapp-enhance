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
        String back = "{\n" +
                "\t\"result\": {\n" +
                "\t\t\"invoiceType\": \"s\",\n" +
                "\t\t\"invoiceDetails\": [{\n" +
                "\t\t\t\"cargoName\": \"*油料*花生碎\",\n" +
                "\t\t\t\"itemSpec\": \"\",\n" +
                "\t\t\t\"quantityUnit\": \"件\",\n" +
                "\t\t\t\"quantity\": \"-1\",\n" +
                "\t\t\t\"taxRate\": \"13\",\n" +
                "\t\t\t\"zeroTax\": \" \",\n" +
                "\t\t\t\"unitPrice\": \"74.00\",\n" +
                "\t\t\t\"amountWithoutTax\": \"-74\",\n" +
                "\t\t\t\"taxAmount\": \"-9.62\",\n" +
                "\t\t\t\"amountWithTax\": \"-83.62\"\n" +
                "\t\t}, {\n" +
                "\t\t\t\"cargoName\": \"*植物油*V金味高级夹心油\",\n" +
                "\t\t\t\"itemSpec\": \"\",\n" +
                "\t\t\t\"quantityUnit\": \"件\",\n" +
                "\t\t\t\"quantity\": \"-2\",\n" +
                "\t\t\t\"taxRate\": \"13\",\n" +
                "\t\t\t\"zeroTax\": \" \",\n" +
                "\t\t\t\"unitPrice\": \"168.00\",\n" +
                "\t\t\t\"amountWithoutTax\": \"-336\",\n" +
                "\t\t\t\"taxAmount\": \"-43.68\",\n" +
                "\t\t\t\"amountWithTax\": \"-379.68\"\n" +
                "\t\t}],\n" +
                "\t\t\"invoiceMain\": {\n" +
                "\t\t\t\"taskId\": \"d63a7b2b-f15d-4bef-8940-d41179c4e2cb\",\n" +
                "\t\t\t\"invoiceCode\": \"5300194130\",\n" +
                "\t\t\t\"invoiceNo\": \"00957689\",\n" +
                "\t\t\t\"invoiceType\": \"s\",\n" +
                "\t\t\t\"purchaserTaxNo\": \"915300005501314867\",\n" +
                "\t\t\t\"purchaserName\": \"沃尔玛（云南）商业零售有限公司\",\n" +
                "\t\t\t\"purchaserAddrTel\": \"云南省昆明市五华区红云街道北市区银河片区尚家营村旁银河星辰花园2幢1层商铺2号 0871-64626580\",\n" +
                "\t\t\t\"purchaserBankInfo\": \"中国工商银行股份有限公司昆明银通支行 2502010509200118958\",\n" +
                "\t\t\t\"sellerTaxNo\": \"91530300323114440U\",\n" +
                "\t\t\t\"sellerName\": \"沃尔玛（云南）商业零售有限公司曲靖子午路分店\",\n" +
                "\t\t\t\"sellerAddrTel\": \"云南省曲靖市麒麟区南片区子午路与文笔路交叉口东北角 0874-3192618\",\n" +
                "\t\t\t\"sellerBankInfo\": \"中国工商银行股份有限公司曲靖南市支行 2505001009000018549\",\n" +
                "\t\t\t\"paperDrewDate\": \"20201231\",\n" +
                "\t\t\t\"amountWithoutTax\": \"-410\",\n" +
                "\t\t\t\"taxAmount\": \"-53.3\",\n" +
                "\t\t\t\"amountWithTax\": \"-463.3\",\n" +
                "\t\t\t\"checkCode\": \"56924369321133034746\",\n" +
                "\t\t\t\"machineCode\": \"661506428408\",\n" +
                "\t\t\t\"remark\": \"开具红字增值税专用发票信息表编号5303022012013293&lt;br/&gt; MTR#272464006190001/发出方：2464；ZW/购入方：2393；ZW 订单号[]\",\n" +
                "\t\t\t\"status\": \"1\",\n" +
                "\t\t\t\"redFlag\": \"0\",\n" +
                "\t\t\t\"ctStatus\": \"\",\n" +
                "\t\t\t\"cpyStatus\": \"0\",\n" +
                "\t\t\t\"checkNumber\": \"4\",\n" +
                "\t\t\t\"checkTime\": \"2021-10-26 10:59:37\",\n" +
                "\t\t\t\"goodsListFlag\": \"0\",\n" +
                "\t\t\t\"dqCode\": \"5300\",\n" +
                "\t\t\t\"dqName\": \"云南\",\n" +
                "\t\t\t\"ofdDownloadUrl\": \"\",\n" +
                "\t\t\t\"pdfDownloadUrl\": \"\",\n" +
                "\t\t\t\"ofdPreviewUrl\": \"\",\n" +
                "\t\t\t\"ofdImageUrl\": \"\"\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"code\": \"TXWRVC0001\",\n" +
                "\t\"message\": \"查验成功\",\n" +
                "\t\"taskId\": \"d63a7b2b-f15d-4bef-8940-d41179c4e2cb\"\n" +
                "}";
        VerificationBack verificationBack = JSONObject.parseObject(back, VerificationBack.class);
        eInvoiceMatchService.matchResultAfterVerify(verificationBack,null);
    }



}
