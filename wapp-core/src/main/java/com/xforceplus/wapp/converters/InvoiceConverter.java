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
    @Mapping(source = "data.authBussiDate", target = "gxDate", qualifiedByName = "formatYMDHMS")
    @Mapping(target = "uuid", expression = "java(data.getInvoiceCode() + data.getInvoiceNo())")
    @Mapping(source = "detailYesorno", target = "detailYesorno")
    TDxRecordInvoiceEntity map(InvoiceVo.Invoice data, int detailYesorno);

    Map<String, String> INVOICE_TYPE_MAP = ImmutableMap.<String, String>builder()
            .put("01-10-01", "01").put("02-60-01", "03").put("02-10-01", "04").put("01-10-06", "08")
            .put("02-10-06", "10").put("02-10-02", "11").put("02-20-06", "14").build();

    @Named("mapInvoiceType")
    default String mapInvoiceType(String status) {
//      01 01-10-01、03 02-60-01、04 02-10-01、08 01-10-06、10 02-10-06、11 02-10-02、14 02-20-06
        return INVOICE_TYPE_MAP.get(status);
    }

    Map<String, String> INVOICE_STATUS_MAP = ImmutableMap.<String, String>builder()
            .put("0", "2").put("1", "0").put("2", "3").put("3", "1").put("4", "4").put("9", "4").build();

    @Named("mapInvoiceStatus")
    default String mapInvoiceStatus(String status) {
        // 0 2、1 0、2 3、3 1、4 4、9 4
        return INVOICE_STATUS_MAP.get(status);
    }

    Map<String, String> AUTH_SYNC_STATUS_MAP = ImmutableMap.<String, String>builder()
            .put("1", "0").put("2", "1").put("3", "4").put("4", "0").build();

    @Named("mapAuthSyncStatus")
    default String mapAuthSyncStatus(String status) {
        return AUTH_SYNC_STATUS_MAP.get(status);
    }
}
