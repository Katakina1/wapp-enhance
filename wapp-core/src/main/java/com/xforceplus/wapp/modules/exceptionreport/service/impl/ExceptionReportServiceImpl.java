package com.xforceplus.wapp.modules.exceptionreport.service.impl;

import cn.hutool.poi.excel.BigExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.modules.backFill.service.FileService;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exceptionreport.mapstruct.ExceptionReportMapper;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.repository.dao.TXfExceptionReportDao;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 20:42
 **/
@Service
public class ExceptionReportServiceImpl extends ServiceImpl<TXfExceptionReportDao, TXfExceptionReportEntity> implements ExceptionReportService {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    private static final SimpleDateFormat SDF = new SimpleDateFormat(YYYY_MM_DD);

    @Autowired
    private IDSequence idSequence;

    @Autowired
    private ExceptionReportMapper exceptionReportMapper;

    @Autowired
    private FileService fileService;


    @Override
    public void add4Claim(TXfExceptionReportEntity entity) {
        entity.setType(ExceptionReportTypeEnum.CLAIM.getType());
        saveExceptionReport(entity);
    }

    @Override
    public void add4Agreement(TXfExceptionReportEntity entity) {
        entity.setType(ExceptionReportTypeEnum.AGREEMENT.getType());
        saveExceptionReport(entity);
    }

    @Override
    public void add4EPD(TXfExceptionReportEntity entity) {
        entity.setType(ExceptionReportTypeEnum.EPD.getType());
        saveExceptionReport(entity);
    }

    private void saveExceptionReport(TXfExceptionReportEntity entity) {

        entity.setId(idSequence.nextId());
        this.save(entity);

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
                final String format = DateUtils.addDayToYYYYMMDD(request.getEndDeductDate(), 1);
                wrapper.lt(TXfExceptionReportEntity::getCreateTime, format);
            } catch (Exception e) {
                log.error("时间转换失败" + e.getMessage(), e);
            }
        }

        return this.page(page, wrapper);
    }
    @Override
    public void export(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum) {
        List<TXfExceptionReportEntity> list = getExportData(request,typeEnum,0);
        try (BigExcelWriter bigExcelWriter = new BigExcelWriter()) {
            while (CollectionUtils.isNotEmpty(list)) {
                bigExcelWriter.write(list);
                long lastId = list.get(list.size() - 1).getId();
                list = getExportData(request, typeEnum, lastId);
            }
            // TODO
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            bigExcelWriter.flush(outputStream);
            try {
                final String file = fileService.uploadFile(outputStream.toByteArray(), "");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private List<TXfExceptionReportEntity> getExportData(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum,long lastId){
        TXfExceptionReportEntity entity = exceptionReportMapper.toEntity(request);
        entity.setType(typeEnum.getType());
        final String startDeductDate = request.getStartDeductDate();
        final LambdaQueryWrapper<TXfExceptionReportEntity> wrapper = Wrappers.lambdaQuery(entity);

        wrapper.gt(TXfExceptionReportEntity::getId,lastId);
        if (StringUtils.isNotBlank(startDeductDate)) {
            wrapper.gt(TXfExceptionReportEntity::getCreateTime, startDeductDate);
        }

        if (StringUtils.isNotBlank(request.getEndDeductDate())) {

            try {
                final String format = DateUtils.addDayToYYYYMMDD(request.getEndDeductDate(), 1);
                wrapper.lt(TXfExceptionReportEntity::getCreateTime, format);
            } catch (Exception e) {
                log.error("时间转换失败" + e.getMessage(), e);
            }
        }
        wrapper.orderByAsc(TXfExceptionReportEntity::getId);

        return this.list(wrapper);

    }
}
