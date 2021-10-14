package com.xforceplus.wapp.modules.exceptionreport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exceptionreport.mapstruct.ExceptionReportMapper;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.repository.dao.TXfExceptionReportDao;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 20:42
 **/
@Service
public class ExceptionReportServiceImpl extends ServiceImpl<TXfExceptionReportDao, TXfExceptionReportEntity> implements ExceptionReportService {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    private static final SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);

    @Autowired
    private ExceptionReportMapper exceptionReportMapper;


    @Override
    public void add4Claim(TXfExceptionReportEntity entity) {

    }

    @Override
    public void add4Agreement(TXfExceptionReportEntity entity) {

    }

    @Override
    public void add4EPD(TXfExceptionReportEntity entity) {

    }

    @Override
    public Page<TXfExceptionReportEntity> getPage(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum) {
        Page<TXfExceptionReportEntity> page = new Page<>();
        page.setSize(request.getSize());
        page.setCurrent(request.getPage());
        TXfExceptionReportEntity entity = exceptionReportMapper.toEntity(request);
        entity.setType(typeEnum.getType());
        final String startDeductDate = request.getStartDeductDate();
        final LambdaQueryWrapper<TXfExceptionReportEntity> wrapper = Wrappers.lambdaQuery(entity);

        if (StringUtils.isNotBlank(startDeductDate)) {
            wrapper.gt(TXfExceptionReportEntity::getCreateTime, startDeductDate);
        }

        if (StringUtils.isNotBlank(request.getEndDeductDate())) {

            try {
                final Date parse = sdf.parse(request.getEndDeductDate());
                final Instant plus = parse.toInstant().plus(1, ChronoUnit.DAYS);
                final LocalDateTime from = LocalDateTime.ofInstant(plus, ZoneId.systemDefault());
                final String format = from.format(DateTimeFormatter.ofPattern(YYYY_MM_DD));
                wrapper.lt(TXfExceptionReportEntity::getCreateTime, format);
            } catch (ParseException e) {
                log.error("时间转换失败" + e.getMessage(), e);
            }
        }

        return this.page(page, wrapper);
    }
}
