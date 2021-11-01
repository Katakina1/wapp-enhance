package com.xforceplus.wapp.modules.rednotification.service;

import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.rednotification.model.AddRedNotificationRequest;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class RedNotificationOuterServiceTest extends BaseUnitTest {
    @Autowired
    RedNotificationOuterService redNotificationOuterService;

    @Test
    public void add() {
        String Json="{\"autoApplyFlag\":1,\"redNotificationInfoList\":[{\"redNotificationItemList\":[{\"amountWithTax\":-106.000000,\"amountWithoutTax\":-100.000000,\"goodsName\":\"*小类大米 *天维美牌叶酸片.\",\"goodsTaxNo\":\"1030102010100000000\",\"model\":\"天维美牌叶酸片.\",\"num\":-4.000000000000000,\"taxAmount\":-6.000000,\"taxPre\":1,\"taxPreCon\":\"免税\",\"taxRate\":0.06,\"unit\":\"瓶.\",\"unitPrice\":25.0000,\"zeroTax\":1},{\"amountWithTax\":-63.600000,\"amountWithoutTax\":-60.000000,\"goodsName\":\"*小类大米 *小林刻立眼镜清洁纸\",\"goodsTaxNo\":\"1030102010100000000\",\"model\":\"小林刻立眼镜清洁纸\",\"num\":-10.000000000000000,\"taxAmount\":-3.600000,\"taxPre\":1,\"taxPreCon\":\"免税\",\"taxRate\":0.06,\"unit\":\"\",\"unitPrice\":6.0000,\"zeroTax\":1}],\"rednotificationMain\":{\"amountWithTax\":-169.60,\"amountWithoutTax\":-160.00,\"applyType\":0,\"billNo\":\"test123\",\"companyCode\":\"UA\",\"id\":5499454996217856,\"invoiceOrigin\":1,\"invoiceType\":\"s\",\"originInvoiceType\":\"\",\"originalInvoiceCode\":\"\",\"originalInvoiceDate\":\"\",\"originalInvoiceNo\":\"\",\"pid\":\"123\",\"purchaserName\":\"沃尔玛（浙江）百货有限公司宁波清河路山姆会员商店\",\"purchaserTaxNo\":\"91330205MA2CLGHY6M\",\"remark\":\"\",\"sellerName\":\"深圳沃尔玛百货零售有限公司\",\"sellerTaxNo\":\"914403006189074000\",\"specialInvoiceFlag\":0,\"taxAmount\":-9.60,\"userRole\":2}}]}";
        AddRedNotificationRequest addRedNotificationRequest = JsonUtil.fromJson(Json, AddRedNotificationRequest.class);
        Response<String> add = redNotificationOuterService.add(addRedNotificationRequest);
        assertTrue(add.getCode() == 1);
    }

    @Test
    public void rollback() {
    }

    @Test
    public void isWaitingApplyBySettlementNo() {
    }

    @Test
    public void updateAppliedToWaitAppproveByPid() {
    }

    @Test
    public void getWaitApplyPreIds() {
    }

    @Test
    public void deleteRednotification() {
    }

    @Test
    public void update() {
    }
}