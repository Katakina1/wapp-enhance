package com.xforceplus.wapp.modules.noneBusiness.dto;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.enums.BusinessTypeExportEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author aiwentao@xforceplus.com
 */
@Slf4j
@Getter
public class NonbusinessImportCompanyImportListener extends AnalysisEventListener<TXfNoneBusinessUploadImportDto> {
    private Integer rows;
    private Integer validRows;
    private Integer invalidRows;
    private final List<TXfNoneBusinessUploadImportDto> validInvoices = Lists.newArrayList();
    private final List<TXfNoneBusinessUploadImportDto> invalidInvoices = Lists.newArrayList();

    public NonbusinessImportCompanyImportListener() {
        this.rows = 0;
        this.validRows = 0;
        this.invalidRows = 0;
    }


    @Override
    public void invoke(TXfNoneBusinessUploadImportDto tXfNoneBusinessUploadImportDto, AnalysisContext analysisContext) {
        rows++;
        if (StringUtils.isEmpty(checkData(tXfNoneBusinessUploadImportDto))) {
            validInvoices.add(tXfNoneBusinessUploadImportDto);
        } else {
            tXfNoneBusinessUploadImportDto.setErrorMessage(checkData(tXfNoneBusinessUploadImportDto));
            invalidInvoices.add(tXfNoneBusinessUploadImportDto);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public String checkData(TXfNoneBusinessUploadImportDto tXfNoneBusinessUploadImportDto) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isEmpty(tXfNoneBusinessUploadImportDto.getInvoiceNo())) {
            builder.append("发票号码不能为空; ");
        }
        if (StringUtils.isNotEmpty(tXfNoneBusinessUploadImportDto.getInvoiceNo())
                && tXfNoneBusinessUploadImportDto.getInvoiceNo().length() != 20
                && StringUtils.isEmpty(tXfNoneBusinessUploadImportDto.getInvoiceCode())) {
            builder.append("发票代码不能为空; ");
        }
        if (StringUtils.isEmpty(tXfNoneBusinessUploadImportDto.getVoucherNo())) {
            builder.append("凭证号不能为空; ");
        }
        if (StringUtils.isEmpty(tXfNoneBusinessUploadImportDto.getEntryDate())) {
            builder.append("入账日期不能为空; ");
        }
        if (BusinessTypeExportEnum.BUSINESS_TYPE_GNFR.getCode().equals(tXfNoneBusinessUploadImportDto.getBussinessType())) {
            if (StringUtils.isEmpty(tXfNoneBusinessUploadImportDto.getTaxCode())) {
                builder.append("税码不能为空; ");
            }
        }
        return builder.toString();
    }

}
