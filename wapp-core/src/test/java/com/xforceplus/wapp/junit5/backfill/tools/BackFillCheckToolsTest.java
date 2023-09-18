package com.xforceplus.wapp.junit5.backfill.tools;

import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.modules.backfill.tools.BackFillCheckTools;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 回填校验工具
 * @date : 2022/12/01 16:29
 **/
public class BackFillCheckToolsTest {

    /**
     * @see BackFillCheckTools#checkPurchaserAndSeller(com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity, com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity)
     *
     */
    @ParameterizedTest
    @CsvSource({
            "购方（测试）公司,123444DFG,销方（测试）公司,223444DFG,true,",
            "购方（测试)公司,123444DFG,销方(测试）公司,223444DFG,true,",
            "购方（测试)公司1,123444DFG,销方(测试）公司,223444DFG,false,发票的购方名称与结算单的购方名称不一致，请确认无误后重试",
            "购方（测试)公司1,123444DFG1,销方(测试）公司,223444DFG,false,发票的购方名称、购方税号与结算单的购方名称、购方税号不一致，请确认无误后重试",
            "购方（测试)公司1,123444DFG1,销方(测试）公司1,223444DFG,false,发票的购方名称、销方名称、购方税号与结算单的购方名称、销方名称、购方税号不一致，请确认无误后重试",
            "购方（测试)公司1,123444DFG1,销方(测试）公司1,223444DFG1,false,发票的购方名称、销方名称、购方税号、销方税号与结算单的购方名称、销方名称、购方税号、销方税号不一致，请确认无误后重试",
    })
    void testCheckPurchaserAndSeller(String gfName, String gfTaxNo, String xfName, String xfTaxNo, boolean expected, String message) {
        TXfPreInvoiceEntity preInvoiceEntity = new TXfPreInvoiceEntity();
        preInvoiceEntity.setPurchaserName("购方（测试）公司");
        preInvoiceEntity.setPurchaserTaxNo("123444DFG");
        preInvoiceEntity.setSellerName("销方（测试）公司");
        preInvoiceEntity.setSellerTaxNo("223444DFG");
        TDxRecordInvoiceEntity invoiceEntity = new TDxRecordInvoiceEntity();
        invoiceEntity.setGfName(gfName);
        invoiceEntity.setGfTaxNo(gfTaxNo);
        invoiceEntity.setXfName(xfName);
        invoiceEntity.setXfTaxNo(xfTaxNo);
        if (expected) {
            BackFillCheckTools.checkPurchaserAndSeller(preInvoiceEntity, invoiceEntity);
        } else {
            assertThatThrownBy(() -> BackFillCheckTools.checkPurchaserAndSeller(preInvoiceEntity, invoiceEntity)).isInstanceOf(EnhanceRuntimeException.class).hasMessage(message);
        }
    }
}
