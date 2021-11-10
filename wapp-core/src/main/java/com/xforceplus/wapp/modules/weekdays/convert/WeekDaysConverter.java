package com.xforceplus.wapp.modules.weekdays.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.enums.DefaultSettingEnum;
import com.xforceplus.wapp.enums.ServiceTypeEnum;
import com.xforceplus.wapp.modules.overdue.dto.OverdueDto;
import com.xforceplus.wapp.modules.overdue.models.Overdue;
import com.xforceplus.wapp.modules.weekdays.dto.TXfMatchWeekdaysDto;
import com.xforceplus.wapp.modules.weekdays.dto.WeekDaysImportDto;
import com.xforceplus.wapp.repository.entity.OverdueEntity;
import com.xforceplus.wapp.repository.entity.TXfMatchWeekdaysEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface WeekDaysConverter {
    List<TXfMatchWeekdaysDto> map(List<TXfMatchWeekdaysEntity> entity);

    List<TXfMatchWeekdaysEntity> importMap(List<WeekDaysImportDto> entity);

}
