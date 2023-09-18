package com.xforceplus.wapp.modules.customs.service;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.customs.dto.CustomsImportDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author aiwentao@xforceplus.com
 */
@Slf4j
@Getter
public class CustomsImportListener extends AnalysisEventListener<CustomsImportDto> {
    private Integer rows;
    private Integer validRows;
    private Integer invalidRows;
    private final List<CustomsImportDto> validInvoices = Lists.newArrayList();
    private final List<CustomsImportDto> invalidInvoices = Lists.newArrayList();

    public CustomsImportListener() {
        this.rows = 0;
        this.validRows = 0;
        this.invalidRows = 0;
    }


    @Override
    public void invoke(CustomsImportDto importDto, AnalysisContext analysisContext) {
        rows++;
        String message = checkData(importDto);
        if (StringUtils.isEmpty(message)) {
            validInvoices.add(importDto);
        } else {
            importDto.setErrorMsg(message);
            invalidInvoices.add(importDto);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public String checkData(CustomsImportDto importDto) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isEmpty(importDto.getCustomsNo())) {
            builder.append("海关缴款书号不能为空");
        }

        if (StringUtils.isEmpty(importDto.getVoucherNo())) {
            builder.append("凭证号不能为空");
        }

        if (StringUtils.isEmpty(importDto.getVoucherAccountTime())) {
            builder.append("凭证入账日期不能为空");
        }

        return builder.toString();
    }

}
