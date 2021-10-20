package com.xforceplus.wapp.modules.exceptionreport.service.impl;

import cn.hutool.poi.excel.BigExcelWriter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.export.ExportHandlerEnum;
import com.xforceplus.wapp.export.IExportHandler;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.backFill.model.FileUploadResult;
import com.xforceplus.wapp.modules.backFill.service.FileService;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exceptionreport.mapstruct.ExceptionReportMapper;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.messagecontrol.service.MessageControlService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import com.xforceplus.wapp.repository.dao.TXfExceptionReportDao;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.CommonMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 20:42
 **/
@Service
@Slf4j
public class ExceptionReportServiceImpl extends ServiceImpl<TXfExceptionReportDao, TXfExceptionReportEntity> implements ExceptionReportService {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    private static final SimpleDateFormat SDF = new SimpleDateFormat(YYYY_MM_DD);

    @Autowired
    private IDSequence idSequence;

    @Autowired
    private ExceptionReportMapper exceptionReportMapper;

    @Autowired
    private FileService fileService;

    private final String filePrefix="例外报告";

    @Autowired
    private ActiveMqProducer activeMqProducer;

    @Autowired
    private CommonMessageService commonMessageService;

    @Value("${activemq.queue-name.export-request}")
    private String exportQueue;

    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Autowired
    private MessageControlService messageControlService;

    @Value("${wapp.export_success_queue_gfone}")
    private String gfoneQueue;

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
        final Long userId = UserUtil.getUserId();
        request.setUserId(userId);
        request.setUserName(UserUtil.getLoginName());
        ExceptionReportExportDto dto=new ExceptionReportExportDto();
        dto.setType(typeEnum);
        dto.setRequest(request);
        activeMqProducer.send(exportQueue, JSON.toJSONString(dto),
                Collections.singletonMap(IExportHandler.KEY_OF_HANDLER_NAME, ExportHandlerEnum.EXCEPTION_REPORT.name())
        );
    }


    public void doExport(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum){
        List<TXfExceptionReportEntity> list = getExportData(request,typeEnum,0);
        TDxExcelExportlogEntity excelExportlogEntity=new TDxExcelExportlogEntity();
        excelExportlogEntity.setCreateDate(new Date());
        //这里的userAccount是userid
        excelExportlogEntity.setUserAccount(request.getUserId().toString());
        excelExportlogEntity.setUserName(request.getUserName());
        excelExportlogEntity.setConditions(JSON.toJSONString(request));
        excelExportlogEntity.setStartDate(new Date());
        excelExportlogEntity.setEndDate(new Date());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
        excelExportlogEntity.setServiceType(SERVICE_TYPE);

        TDxMessagecontrolEntity messagecontrolEntity=new TDxMessagecontrolEntity();
        //这里的userAccount是userName
        messagecontrolEntity.setUserAccount(request.getUserName());
        messagecontrolEntity.setContent(getSuccContent());

        try (BigExcelWriter bigExcelWriter = new BigExcelWriter()) {
            while (CollectionUtils.isNotEmpty(list)) {
                bigExcelWriter.write(list);
                long lastId = list.get(list.size() - 1).getId();
                list = getExportData(request, typeEnum, lastId);
            }
            // TODO

            try {
                final String excelFileName = ExcelExportUtil.getExcelFileName(request.getUserId(), filePrefix);
                final File x = File.createTempFile(excelFileName, "x");
                log.info("file:{}",x.getAbsolutePath());
                FileOutputStream fileOutputStream=new FileOutputStream(x);
                bigExcelWriter.flush(fileOutputStream);
//                FileInputStream fileInputStream=new FileInputStream(x);
//                IOUtils.read(fileInputStream,)
                final byte[] bytes = FileUtils.readFileToByteArray(x);


//                ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

                final FileUploadResult file = fileService.upload(bytes, excelFileName);
                messagecontrolEntity.setUrl(file.getData().getUploadId());
                excelExportlogEntity.setFilepath(file.getData().getUploadPath());
                messagecontrolEntity.setTitle(this.filePrefix+"导出成功");

            } catch (Exception e) {
                e.printStackTrace();
                log.error("例外报告导出失败:"+e.getMessage(),e);
                excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
                excelExportlogEntity.setErrmsg(e.getMessage());
                messagecontrolEntity.setTitle(this.filePrefix+"导出失败");
                messagecontrolEntity.setContent(getFailContent(e.getMessage()));

            }finally {
                excelExportLogService.save(excelExportlogEntity);
                commonMessageService.sendMessage(messagecontrolEntity);
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


    /**
     * 获取导出成功内容
     *
     * @return
     * @since           1.0
     */
    public String getSuccContent() {
        String createDate = DateUtils.format(new Date());
        String content = "申请时间：" + createDate + "。申请导出成功，可以下载！";
        return content;
    }


    /**
     * 获取导出失败的内容
     *
     * @param errmsg 错误信息
     *
     * @return
     * @since           1.0
     */
    public String getFailContent(String errmsg) {
        StringBuilder content = new StringBuilder();
        content.append("申请时间：");
        String createDate = DateUtils.format(new Date());
        content.append(createDate);
        content.append("。申请导出失败，请重新申请！");
        return content.toString();
    }
}
