package com.xforceplus.wapp.modules.supserviceconf.service;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.supserviceconf.dto.SuperServiceConfImportDto;
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
public class SuperSerConfImportListener extends AnalysisEventListener<SuperServiceConfImportDto> {
    private Integer rows;
    private Integer validRows;
    private Integer invalidRows;
    private final List<SuperServiceConfImportDto> validInvoices = Lists.newArrayList();
    private final List<SuperServiceConfImportDto> invalidInvoices = Lists.newArrayList();

    public SuperSerConfImportListener() {
        this.rows = 0;
        this.validRows = 0;
        this.invalidRows = 0;
    }


    @Override
    public void invoke(SuperServiceConfImportDto superSerConfImportDto, AnalysisContext analysisContext) {
        rows++;
        String message = checkData(superSerConfImportDto);
        if (StringUtils.isEmpty(message)) {
            validInvoices.add(superSerConfImportDto);
        } else {
            superSerConfImportDto.setErrorMsg(message);
            invalidInvoices.add(superSerConfImportDto);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public String checkData(SuperServiceConfImportDto superSerConfImportDto) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isEmpty(superSerConfImportDto.getUserCode())) {
            builder.append("供应商号不能为空; ");
        }
        if (Objects.isNull(superSerConfImportDto.getServiceType())) {
            builder.append("服务类型不能为空; ");
        }
        if (StringUtils.isEmpty(superSerConfImportDto.getAssertDate())) {
            builder.append("生效日期不能为空; ");
        }
        if (StringUtils.isEmpty(superSerConfImportDto.getExpireDate())) {
            builder.append("失效日期不能为空; ");
        }
        return builder.toString();
    }

}
