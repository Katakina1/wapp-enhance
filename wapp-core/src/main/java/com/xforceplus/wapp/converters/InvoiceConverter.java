package com.xforceplus.wapp.converters;

import com.google.common.collect.ImmutableMap;
import com.xforceplus.wapp.handle.vo.InvoiceVo;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface InvoiceConverter {
    @Mapping(source = "paperDrewDate", target = "invoiceDate")
    @Mapping(source = "invoiceType", target = "invoiceType", qualifiedByName = "mapInvoiceType")
    @Mapping(source = "purchaserName", target = "gfName")
    @Mapping(source = "purchaserTaxNo", target = "gfTaxNo")
    @Mapping(source = "purchaserAddrTel", target = "gfAddressAndPhone")
    @Mapping(source = "purchaserBankNameAccount", target = "gfBankAndNo")
    @Mapping(source = "machineCode", target = "machinecode")
    @Mapping(source = "amountWithTax", target = "totalAmount")
    @Mapping(source = "amountWithoutTax", target = "invoiceAmount")
    @Mapping(source = "sellerName", target = "xfName")
    @Mapping(source = "sellerTaxNo", target = "xfTaxNo")
    @Mapping(source = "sellerAddrTel", target = "xfAddressAndPhone")
    @Mapping(source = "sellerBankNameAccount", target = "xfBankAndNo")
    @Mapping(source = "status", target = "invoiceStatus", qualifiedByName = "mapInvoiceStatus")
    @Mapping(source = "authStatus", target = "rzhYesorno", qualifiedByName = "mapAuthStatus")
    @Mapping(target = "rzhType", expression = "java(1)")
    @Mapping(source = "authSyncStatus", target = "authStatus", qualifiedByName = "")
    @Mapping(source = "authBussiDate", target = "gxDate")
    TDxRecordInvoiceEntity map(InvoiceVo.Invoice data);

    Map<String, String> INVOICE_TYPE_MAP = ImmutableMap.<String, String>builder()
            .put("01-10-01", "01").put("02-60-01", "03").put("02-10-01", "04").put("01-10-06", "08")
            .put("02-10-06", "10").put("02-10-02", "11").put("02-20-06", "14").build();

    @Named("mapInvoiceType")
    default String mapInvoiceType(String status) {
//      01 01-10-01、03 02-60-01、04 02-10-01、08 01-10-06、10 02-10-06、11 02-10-02、14 02-20-06
        return INVOICE_TYPE_MAP.get(status);
    }
    Map<String, String> INVOICE_STATUS_MAP = ImmutableMap.<String, String>builder()
            .put("01-10-01", "01").put("02-60-01", "03").put("02-10-01", "04").put("01-10-06", "08")
            .put("02-10-06", "10").put("02-10-02", "11").put("02-20-06", "14").build();

    @Named("mapInvoiceStatus")
    default String mapInvoiceStatus(String status) {
//      01 01-10-01、03 02-60-01、04 02-10-01、08 01-10-06、10 02-10-06、11 02-10-02、14 02-20-06
        return INVOICE_STATUS_MAP.get(status);
    }
    Map<String, String> AUTH_STATUS_MAP = ImmutableMap.<String, String>builder()
            .put("01-10-01", "01").put("02-60-01", "03").put("02-10-01", "04").put("01-10-06", "08")
            .put("02-10-06", "10").put("02-10-02", "11").put("02-20-06", "14").build();

    @Named("mapAuthStatus")
    default String mapAuthStatus(String status) {
//      01 01-10-01、03 02-60-01、04 02-10-01、08 01-10-06、10 02-10-06、11 02-10-02、14 02-20-06
        return AUTH_STATUS_MAP.get(status);
    }
    Map<String, String> AUTH_SYNC_STATUS_MAP = ImmutableMap.<String, String>builder()
            .put("01-10-01", "01").put("02-60-01", "03").put("02-10-01", "04").put("01-10-06", "08")
            .put("02-10-06", "10").put("02-10-02", "11").put("02-20-06", "14").build();

    @Named("mapAuthSyncStatus")
    default String mapAuthSyncStatus(String status) {
//      01 01-10-01、03 02-60-01、04 02-10-01、08 01-10-06、10 02-10-06、11 02-10-02、14 02-20-06
        return AUTH_SYNC_STATUS_MAP.get(status);
    }
}
