package com.xforceplus.wapp.converters;

import com.google.common.collect.Lists;
import com.xforceplus.wapp.handle.vo.InvoiceVo;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
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
    TDxRecordInvoiceDetailEntity map(InvoiceVo.InvoiceItemVO items);

    List<TDxRecordInvoiceDetailEntity> map(List<InvoiceVo.InvoiceItemVO> items);
}
