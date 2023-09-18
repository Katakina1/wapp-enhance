package com.xforceplus.wapp.converters;

import com.google.common.collect.ImmutableMap;
import com.xforceplus.wapp.handle.vo.InvoiceVo;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface InvoiceConverter {
    @Mapping(source = "data.paperDrewDate", target = "invoiceDate")
    @Mapping(source = "data.invoiceType", target = "invoiceType", qualifiedByName = "mapInvoiceType")
    @Mapping(source = "data.purchaserName", target = "gfName")
    @Mapping(source = "data.purchaserTaxNo", target = "gfTaxNo")
    @Mapping(source = "data.purchaserAddrTel", target = "gfAddressAndPhone")
    @Mapping(source = "data.purchaserBankNameAccount", target = "gfBankAndNo")
    @Mapping(source = "data.machineCode", target = "machinecode")
    @Mapping(source = "data.amountWithTax", target = "totalAmount")
    @Mapping(source = "data.amountWithoutTax", target = "invoiceAmount", defaultValue = "0")
    @Mapping(source = "data.sellerName", target = "xfName")
    @Mapping(source = "data.sellerTaxNo", target = "xfTaxNo")
    @Mapping(source = "data.sellerAddrTel", target = "xfAddressAndPhone")
    @Mapping(source = "data.sellerBankNameAccount", target = "xfBankAndNo")
    @Mapping(source = "data.status", target = "invoiceStatus", qualifiedByName = "mapInvoiceStatus")
    @Mapping(source = "data.authStatus", target = "rzhYesorno")
    @Mapping(target = "data.rzhType", expression = "java(1)")
    @Mapping(source = "data.authSyncStatus", target = "authStatus", qualifiedByName = "mapAuthSyncStatus")
    @Mapping(source = "data.checkTime", target = "gxDate", qualifiedByName = "formatYMDHMS")
    @Mapping(target = "uuid", expression = "java(data.getInvoiceCode() + data.getInvoiceNo())")
    @Mapping(source = "data.invoiceExtend.newPurchaserTaxNo", target = "newGfTaxno")
    @Mapping(source = "data.effectiveTaxAmount", target = "yxse")
    @Mapping(source = "data.authTime", target = "rzhDate", qualifiedByName = "formatYMDHMS")
    @Mapping(source = "detailYesorno", target = "detailYesorno")
    // 成品油转换
    @Mapping(source = "data.invoiceType", target = "isOil", qualifiedByName = "isOil")
    @Mapping(source = "data.taxRate", target = "taxRate", qualifiedByName = "mapTaxRate")
    TDxRecordInvoiceEntity map(InvoiceVo.Invoice data, int detailYesorno);
    // 01-增值税专用发票 03-机动车销售统一发票 04-增值税普通发票 10-电子发票 11-卷票 14-通行费发票
    Map<String, String> INVOICE_TYPE_MAP = ImmutableMap.<String, String>builder()
            .put("01-10-01", "01").put("02-60-01", "03").put("02-10-01", "04").put("01-10-06", "08")
            .put("02-10-06", "10").put("02-10-02", "11").put("02-20-06", "14")
            .put("01-30-01", "01").put("01-30-06", "10").put("02-30-01", "04").put("02-30-02", "11").build();
    @Named("mapInvoiceType")
    default String mapInvoiceType(String status) {
        return INVOICE_TYPE_MAP.get(status);
    }

    String OIL_FLAG = "-30-";
    @Named("isOil")
    default Integer isOil(@NonNull String invoiceType) {
         return invoiceType.contains(OIL_FLAG) ? 1 : 0;
    }

    Map<String, String> INVOICE_STATUS_MAP = ImmutableMap.<String, String>builder()
            .put("0", "2").put("1", "0").put("2", "3").put("3", "1").put("4", "4").put("9", "4").build();

    @Named("mapInvoiceStatus")
    default String mapInvoiceStatus(String status) {
        return INVOICE_STATUS_MAP.get(status);
    }

    Map<String, String> AUTH_SYNC_STATUS_MAP = ImmutableMap.<String, String>builder()
            .put("1", "0").put("2", "1").put("3", "4").put("4", "0").build();

    @Named("mapAuthSyncStatus")
    default String mapAuthSyncStatus(String status) {
        return AUTH_SYNC_STATUS_MAP.get(status);
    }

    @Named("mapTaxRate")
    default BigDecimal mapTaxRate(String taxRate) {
        if (StringUtils.isBlank(taxRate)) {
            return null;
        }
        if (taxRate.contains("%")) {
            taxRate = taxRate.replaceAll("%", "");
        }
        return new BigDecimal(taxRate);
    }
}
