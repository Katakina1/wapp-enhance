package com.xforceplus.wapp.modules.weekdays.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.modules.weekdays.dto.WeekDaysImportDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author aiwentao@xforceplus.com
 */
@Slf4j
@Getter
public class WeekDaysImportListener extends AnalysisEventListener<WeekDaysImportDto> {
    private Integer rows;
    private Integer validRows;
    private Integer invalidRows;
    private final List<WeekDaysImportDto> validInvoices = Lists.newArrayList();
    private final List<WeekDaysImportDto> invalidInvoices = Lists.newArrayList();

    public WeekDaysImportListener() {
        this.rows = 0;
        this.validRows = 0;
        this.invalidRows = 0;
    }


    @Override
    public void invoke(WeekDaysImportDto WeekDaysImportDto, AnalysisContext analysisContext) {
        if (StringUtils.isEmpty(checkData(WeekDaysImportDto))) {
            validInvoices.add(WeekDaysImportDto);
        } else {
            WeekDaysImportDto.setErrorMessage(checkData(WeekDaysImportDto));
            invalidInvoices.add(WeekDaysImportDto);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public String checkData(WeekDaysImportDto WeekDaysImportDto) {
        StringBuilder builder = new StringBuilder();
        if (WeekDaysImportDto.getWeekdays()==null) {
            builder.append("工作日不能为空");
        }
        return builder.toString();
    }

    public static boolean isDate(String weekDays) {
        int length = 10;
        if (length != weekDays.length()) {
            return false;
        }
        try {
            DateUtils.strToDate(weekDays);
        } catch (Exception e) {
            log.error("导入转换日期异常:{}", e);
            return false;
        }
        return true;

    }

}
