package com.xforceplus.wapp.modules.overdue.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.overdue.dto.OverdueDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Getter
public class OverdueImportListener extends AnalysisEventListener<OverdueDto> {
    private Integer rows;
    private Integer validRows;
    private Integer invalidRows;
    private final List<OverdueDto> validInvoices = Lists.newArrayList();
    private final List<OverdueDto> invalidInvoices = Lists.newArrayList();

    public OverdueImportListener() {
        this.rows = 0;
        this.validRows = 0;
        this.invalidRows = 0;
    }

    @Override
    public void invoke(OverdueDto data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        rows++;
        if (checkImportInvoice(data)) {
            validRows++;
            validInvoices.add(data);
        } else {
            invalidRows++;
            invalidInvoices.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }

    private boolean checkImportInvoice(OverdueDto data) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<OverdueDto>> validate = validator.validate(data);
        return validate.isEmpty();
    }
}
