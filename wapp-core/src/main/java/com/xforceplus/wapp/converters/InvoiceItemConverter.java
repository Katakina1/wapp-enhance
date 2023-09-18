package com.xforceplus.wapp.converters;

import com.xforceplus.wapp.handle.vo.InvoiceVo;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = {BaseConverter.class})
public interface InvoiceItemConverter {
    @Mapping(target = "uuid", expression = "java(items.getInvoiceCode() + items.getInvoiceNo())")
    @Mapping(target = "goodsName", source = "cargoName")
    @Mapping(target = "model", source = "itemSpec")
    @Mapping(target = "unit", source = "quantityUnit")
    @Mapping(target = "num", source = "quantity")
    @Mapping(target = "detailAmount", source = "amountWithoutTax")
    @Mapping(target = "cph", source = "plateNumber")
    @Mapping(target = "lx", source = "vehicleType")
    @Mapping(target = "txrqq", source = "tollStartDate")
    @Mapping(target = "txrqz", source = "tollEndDate")
    @Mapping(target = "goodsNum", source = "goodsTaxNo")
    @Mapping(target = "detailNo",source = "sequence")
    @Mapping(target = "taxRate", source = "taxRate", qualifiedByName = "mapTaxRate")
    TDxRecordInvoiceDetailEntity map(InvoiceVo.InvoiceItemVO items);

    List<TDxRecordInvoiceDetailEntity> map(List<InvoiceVo.InvoiceItemVO> items);

    @Named("mapTaxRate")
    default String mapTaxRate(String taxRate) {
        if (StringUtils.isBlank(taxRate)) {
            return "";
        }
        if (taxRate.contains("%")) {
            taxRate = taxRate.replaceAll("%", "");
        }
        return taxRate;
    }
}
