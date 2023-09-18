package com.xforceplus.wapp.modules.noneBusiness.dto;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 非商电票上传导入
 */
@Slf4j
@Getter
public class NonbusinessUploadImportListener extends AnalysisEventListener<TXfNoneBusinessUploadImportDto> {
    private Integer rows;
    private Integer validRows;
    private Integer invalidRows;
    private final List<TXfNoneBusinessUploadImportDto> validInvoices = Lists.newArrayList();
    private final List<TXfNoneBusinessUploadImportDto> invalidInvoices = Lists.newArrayList();

    public NonbusinessUploadImportListener() {
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
        return builder.toString();
    }

}
