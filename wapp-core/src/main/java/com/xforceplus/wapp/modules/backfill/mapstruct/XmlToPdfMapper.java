package com.xforceplus.wapp.modules.backfill.mapstruct;


import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.htmladapter.dto.XmlToPdf;
import com.xforceplus.wapp.modules.backfill.dto.AnalysisXmlResult;
import com.xforceplus.wapp.util.RMBUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class)
public interface XmlToPdfMapper {

    @Mapping(target = "taxRate", source = "taxRate", qualifiedByName = "mapTaxRate")
    @Mapping(target = "quantity", source = "quantity", qualifiedByName = "amountFormat")
    @Mapping(target = "unitPrice", source = "unitPrice", qualifiedByName = "amountFormat")
    @Mapping(target = "amountWithoutTax", source = "amountWithoutTax", qualifiedByName = "amountFormat")
    @Mapping(target = "taxAmount", source = "taxAmount", qualifiedByName = "amountFormat")
    XmlToPdf.PdfInvoiceDetail map(AnalysisXmlResult.InvoiceDetailsDTO detail);

    @Named("mapDetails")
    List<XmlToPdf.PdfInvoiceDetail> map(List<AnalysisXmlResult.InvoiceDetailsDTO> details);

    @Named("mapTaxRate")
    default String mapTaxRate(String taxRate) {
        if (!NumberUtils.isNumber(taxRate)) {
            return taxRate;
        }
        return taxRate.split("\\.")[0] + "%";
    }

    @Named("decimalToCn")
    default String decimalToCn(String amount) {
        if (!NumberUtils.isNumber(amount)) {
            return amount;
        }
        return RMBUtils.getRMBCapitals(new BigDecimal(amount).multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.DOWN).longValue());
    }


    @Named("amountFormat")
    default String amountFormat(String amount) {
        if (StringUtils.isBlank(amount)) {
            return "";
        }
        try {
            return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).toPlainString();
        } catch (Exception e) {
            return amount;
        }
    }
    @Named("generateInvoiceType")
    default String generateInvoiceType(String invoiceType) {
        if (StringUtils.isBlank(invoiceType)) {
            return "";
        }
        try {
            if ("qc".equals(invoiceType)){
                return "16";
            }
            if ("qc".equals(invoiceType)){
                return "18";
            }

        } catch (Exception e) {
            return invoiceType;
        }
        return invoiceType;
    }

    @Mapping(target = "invDetails", source = "details", qualifiedByName = "mapDetails")
    @Mapping(target = "amountWithoutTax", source = "main.amountWithoutTax", qualifiedByName = "amountFormat")
    @Mapping(target = "taxAmount", source = "main.taxAmount", qualifiedByName = "amountFormat")
    @Mapping(target = "amountWithTax", source = "main.amountWithTax", qualifiedByName = "amountFormat")
    @Mapping(target = "amountWithTaxCn", source = "main.amountWithTax", qualifiedByName = "decimalToCn")
    @Mapping(target = "invoiceType", source = "main.invoiceType", qualifiedByName = "generateInvoiceType")
    XmlToPdf.PdfInvoice map(AnalysisXmlResult.InvoiceMainDTO main, List<AnalysisXmlResult.InvoiceDetailsDTO> details);

    default XmlToPdf map(AnalysisXmlResult result) {
        XmlToPdf pdf = new XmlToPdf();

        AnalysisXmlResult.InvoiceMainDTO main = result.getInvoiceMain();
        List<AnalysisXmlResult.InvoiceDetailsDTO> details = result.getInvoiceDetails();

        List<XmlToPdf.PdfInvoice> list = new ArrayList<>();
        List<AnalysisXmlResult.InvoiceDetailsDTO> detailsDTOList = new ArrayList<>();

        for (int i = 0; i < details.size(); i++) {
            detailsDTOList.add(details.get(i));
            if ((i + 1) % 8 == 0) {
                list.add(map(main, detailsDTOList));
                detailsDTOList = new ArrayList<>();
            }
        }
        if (detailsDTOList.size() != 0) {
            list.add(map(main, detailsDTOList));
        }
        pdf.setInvoiceMain(list);
        return pdf;
    }
}
