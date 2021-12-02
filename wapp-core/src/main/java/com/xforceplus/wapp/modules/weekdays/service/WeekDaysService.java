package com.xforceplus.wapp.modules.weekdays.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportDto;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.weekdays.convert.WeekDaysConverter;
import com.xforceplus.wapp.modules.weekdays.dto.TXfMatchWeekdaysDto;
import com.xforceplus.wapp.modules.weekdays.dto.WeekDaysImportDto;
import com.xforceplus.wapp.modules.weekdays.listener.WeekDaysImportListener;
import com.xforceplus.wapp.repository.dao.TXfMatchWeekdaysDao;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TXfMatchWeekdaysEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * 工作日维护
 */
@Service
@Slf4j
public class WeekDaysService extends ServiceImpl<TXfMatchWeekdaysDao, TXfMatchWeekdaysEntity> {

    @Autowired
    private WeekDaysConverter weekDaysConverter;
    @Value("${wapp.export.tmp}")
    private String tmp;
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Autowired
    ExportCommonService exportCommonService;

    public Tuple2<List<TXfMatchWeekdaysDto>, Page<?>> page(Long current, Long size, String weekDayStart, String weekDayEnd) {
        LambdaQueryChainWrapper<TXfMatchWeekdaysEntity> wrapper = new LambdaQueryChainWrapper<TXfMatchWeekdaysEntity>(baseMapper);
        if (StringUtils.isNotEmpty(weekDayStart)) {
            wrapper.ge(TXfMatchWeekdaysEntity::getWeekdays, weekDayStart);
        }
        if (StringUtils.isNotEmpty(weekDayEnd)) {
            wrapper.le(TXfMatchWeekdaysEntity::getWeekdays, weekDayEnd);
        }
        Page<TXfMatchWeekdaysEntity> page = wrapper.page(new Page<>(current, size));
        return Tuple.of(weekDaysConverter.map(page.getRecords()), page);
    }

    /**
     * 导入黑白名单信息
     *
     * @param file
     * @return
     */
    public Either<String, Integer> importData(MultipartFile file) throws IOException {
        QueryWrapper wrapper = new QueryWrapper<>();
        WeekDaysImportListener listener = new WeekDaysImportListener();
        EasyExcel.read(file.getInputStream(), WeekDaysImportDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices()) && CollectionUtils.isEmpty(listener.getInvalidInvoices())) {
            return Either.left("未解析到数据");
        }
        log.info("导入数据解析条数:{}", listener.getRows());
        if (CollectionUtils.isNotEmpty(listener.getValidInvoices())) {
            List<TXfMatchWeekdaysEntity> validList = weekDaysConverter.importMap(listener.getValidInvoices());
            List<Date> weekList = listener.getValidInvoices().stream().map(WeekDaysImportDto::getWeekdays).collect(Collectors.toList());
            QueryWrapper wrapperCode = new QueryWrapper<>();
            wrapperCode.in(TXfMatchWeekdaysEntity.WEEKDAYS, weekList);
            List<TXfMatchWeekdaysEntity> resultList = this.list(wrapperCode);
            Map<Date, Long> map = new HashMap<>();
            resultList.stream().forEach(code -> {
                map.put(code.getWeekdays(), code.getId());
            });
            List<TXfMatchWeekdaysEntity> addList = validList.stream().filter(x -> Objects.isNull(map.get(x.getWeekdays()))).collect(Collectors.toList());
            addList.stream().forEach(e -> {
                e.setCreateUser(UserUtil.getLoginName());
                e.setCreateTime(new Date());
            });

            boolean save = saveOrUpdateBatch(addList);
            return save ? Either.right(addList.size()) : Either.right(0);
        }
        if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {
            File tmpFile = new File(tmp);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            File sourceFile = new File(tmp, file.getOriginalFilename());
            EasyExcel.write(tmp + "/" + file.getOriginalFilename(), SpecialCompanyImportDto.class).sheet("sheet1").doWrite(listener.getInvalidInvoices());

            String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String ftpFilePath = ftpPath + "/" + file.getOriginalFilename();
            FileInputStream inputStream = FileUtils.openInputStream(sourceFile);
            try {
                ftpUtilService.uploadFile(ftpPath, file.getOriginalFilename(), inputStream);
            } catch (Exception e) {
                log.error("上传ftp服务器异常:{}", e);
            }
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
            exportDto.setUserId(userId);
            exportDto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserName());
            excelExportlogEntity.setUserName(UserUtil.getLoginName());
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + file.getOriginalFilename());
            this.excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "工作日导入", exportCommonService.getSuccContent());

        }
        return Either.right(listener.getInvalidInvoices().size());
    }

}

