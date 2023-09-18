package com.xforceplus.wapp.modules.taxcode.service.impl;


import cn.hutool.poi.excel.BigExcelWriter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.client.CacheClient;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.export.ExportHandlerEnum;
import com.xforceplus.wapp.export.IExportHandler;
import com.xforceplus.wapp.export.dto.TaxCodeReportExportDto;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.taxcode.dto.TaxCodeReportDto;
import com.xforceplus.wapp.modules.taxcode.dto.TaxCodeReportRequest;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeReportService;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import com.xforceplus.wapp.repository.dao.TXfTaxCodeReportDao;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
import com.xforceplus.wapp.repository.entity.TXfTaxCodeReportEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.CommonMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

@Slf4j
@Service
public class TaxCodeReportServiceImpl extends ServiceImpl<TXfTaxCodeReportDao, TXfTaxCodeReportEntity> implements TaxCodeReportService {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    private final String downLoadurl = "api/core/ftp/download";
    private static final String EXPORT_LOCK_KEY = "exception-report:%s:%s";

    @Autowired
    private IDSequence idSequence;

    private final String filePrefix = "例外报告";
    @Autowired
    private ActiveMqProducer activeMqProducer;

    @Autowired
    private CommonMessageService commonMessageService;

    @Value("${activemq.queue-name.export-request}")
    private String exportQueue;

    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Autowired
    private FtpUtilService ftpUtilService;

    @Autowired
    private CacheClient cacheClient;


    @Autowired
    ThreadPoolExecutor threadPool;
    @Autowired
    ExportCommonService exportCommonService;

    private final Map<String, String> headTaxCodeReport;

    public TaxCodeReportServiceImpl() {
        this.headTaxCodeReport = excelHeadTaxCodeReport();
    }


    @Override
    public PageResult<TXfTaxCodeReportEntity> getPage(TaxCodeReportRequest request) {

        Page<TXfTaxCodeReportEntity> page = new Page<>();
        page.setSize(request.getSize());
        page.setCurrent(request.getPage());
        page.addOrder(OrderItem.desc("id"));

        TXfTaxCodeReportEntity entity = new TXfTaxCodeReportEntity();
        entity.setItemName(request.getItemName());
        entity.setItemNo(request.getItemNo());
        final LambdaQueryWrapper<TXfTaxCodeReportEntity> wrapper = Wrappers.lambdaQuery(entity);
        wrapper.eq(StringUtils.isNotBlank(request.getDisposeStatus()), TXfTaxCodeReportEntity::getDisposeStatus, request.getDisposeStatus());
        if (StringUtils.isNotBlank(request.getStartCreateTime())) {
            wrapper.gt(TXfTaxCodeReportEntity::getCreateTime, request.getStartCreateTime());
        }

        if (StringUtils.isNotBlank(request.getEndCreateTime())) {
            try {
                final String format = DateUtils.addDayToYYYYMMDD(request.getEndCreateTime(), 1);
                wrapper.lt(TXfTaxCodeReportEntity::getCreateTime, format);
            } catch (Exception e) {
                log.error("报告时间转换失败" + e.getMessage(), e);
            }
        }
        wrapper.ge(StringUtils.isNotBlank(request.getStartUpdateTime()), TXfTaxCodeReportEntity::getUpdateTime, request.getStartUpdateTime());
        if (StringUtils.isNotBlank(request.getEndUpdateTime())) {
            try {
                final String format = DateUtils.addDayToYYYYMMDD(request.getEndUpdateTime(), 1);
                wrapper.lt(TXfTaxCodeReportEntity::getUpdateTime, format);
            } catch (Exception e) {
                log.error("报告时间转换失败" + e.getMessage(), e);
            }
        }
        Page<TXfTaxCodeReportEntity> re = this.page(page, wrapper);
        List<TXfTaxCodeReportEntity> response = new ArrayList<>();
        for (TXfTaxCodeReportEntity taxCodeReportEntity : re.getRecords()) {
            response.add(taxCodeReportEntity);
        }
        return PageResult.of(response, re.getTotal(), re.getPages(), re.getSize());
    }

    @Override
    public boolean update(String status, TaxCodeReportRequest request) {
        return new LambdaUpdateChainWrapper<>(getBaseMapper())
                .in(CollectionUtils.isNotEmpty(request.getIds()), TXfTaxCodeReportEntity::getId, request.getIds())
                .eq(StringUtils.isNotBlank(request.getItemNo()), TXfTaxCodeReportEntity::getItemNo, request.getItemNo())
                .eq(StringUtils.isNotBlank(request.getItemName()), TXfTaxCodeReportEntity::getItemName, request.getItemName())
                .eq(StringUtils.isNotBlank(request.getDisposeStatus()), TXfTaxCodeReportEntity::getDisposeStatus, request.getDisposeStatus())
                .gt(StringUtils.isNotBlank(request.getStartCreateTime()), TXfTaxCodeReportEntity::getCreateTime, request.getStartCreateTime())
                .lt(StringUtils.isNotBlank(request.getEndCreateTime()), TXfTaxCodeReportEntity::getCreateTime, DateUtils.addDayToYYYYMMDD(request.getEndCreateTime(), 1))
                .set(TXfTaxCodeReportEntity::getDisposeStatus, status)
                .set(TXfTaxCodeReportEntity::getDisposeTime, "1".equals(status) ? new Date() : null)
                .update();
    }

    @Override
    public void export(TaxCodeReportRequest request, ExceptionReportTypeEnum typeEnum) {
        final Long userId = UserUtil.getUserId();
        String key = String.format(EXPORT_LOCK_KEY, userId, typeEnum.name());
        boolean lock = Objects.nonNull(cacheClient.get(key));
        if (lock) {
            throw new EnhanceRuntimeException("文件正在导出中，请稍后在通知中心查看导出结果。若长时间没结果请在1分钟后重试");
        }
        TaxCodeReportExportDto dto = new TaxCodeReportExportDto();
        dto.setType(typeEnum);
        dto.setRequest(request);
        dto.setUserId(userId);
        dto.setLoginName(UserUtil.getLoginName());

        TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
        excelExportlogEntity.setCreateDate(new Date());
        //这里的userAccount是userid
        excelExportlogEntity.setUserAccount(dto.getUserId().toString());
        excelExportlogEntity.setUserName(dto.getLoginName());
        excelExportlogEntity.setConditions(JSON.toJSONString(request));
        excelExportlogEntity.setStartDate(new Date());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
        excelExportlogEntity.setServiceType(SERVICE_TYPE);

        this.excelExportLogService.save(excelExportlogEntity);
        dto.setLogId(excelExportlogEntity.getId());

        activeMqProducer.send(exportQueue, JSON.toJSONString(dto),
                Collections.singletonMap(IExportHandler.KEY_OF_HANDLER_NAME, ExportHandlerEnum.TAX_CODE_REPORT.name())
        );
        cacheClient.set(key, Boolean.TRUE, 60);
    }

    @Override
    public void doExport(TaxCodeReportExportDto exportDto) {
        TaxCodeReportRequest request = exportDto.getRequest();
        ExceptionReportTypeEnum typeEnum = exportDto.getType();
        //这里的userAccount是userid
        final TDxExcelExportlogEntity excelExportlogEntity = excelExportLogService.getById(exportDto.getLogId());
        excelExportlogEntity.setEndDate(new Date());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);

        TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
        //这里的userAccount是userName
        messagecontrolEntity.setUserAccount(exportDto.getLoginName());
        messagecontrolEntity.setContent(getSuccContent());
        String filePrefix = this.filePrefix;

        String key = String.format(EXPORT_LOCK_KEY, exportDto.getUserId(), typeEnum.name());
        filePrefix = "税编" + filePrefix;

        List<TXfTaxCodeReportEntity> list = getExportData(request, typeEnum, 0);
        try {
            Map<String, String> head = headTaxCodeReport;
            List<TaxCodeReportDto> list1 = toExportDto(list);

            final File file = File.createTempFile("exception-report-export", "xls");
            try (BigExcelWriter bigExcelWriter = new BigExcelWriter();
                 FileOutputStream fileOutputStream = new FileOutputStream(file);
                 FileInputStream fileInputStream = new FileInputStream(file);
            ) {
                bigExcelWriter.setHeaderAlias(head);
                bigExcelWriter.autoSizeColumnAll();
                bigExcelWriter.write(list1);
                bigExcelWriter.flush(fileOutputStream);

                //推送sftp
                String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                final String excelFileName = ExcelExportUtil.getExcelFileName(exportDto.getUserId(), filePrefix);
                String ftpFilePath = ftpPath + "/" + excelFileName;
                ftpUtilService.uploadFile(ftpPath, excelFileName, fileInputStream);
                messagecontrolEntity.setUrl(getUrl(excelExportlogEntity.getId()));
                excelExportlogEntity.setFilepath(ftpFilePath);
                messagecontrolEntity.setTitle(filePrefix + "导出成功");

            } finally {
                file.deleteOnExit();
            }

        } catch (Exception e) {
            log.error("例外报告导出失败:" + e.getMessage(), e);
            excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
            excelExportlogEntity.setErrmsg(e.getMessage());
            messagecontrolEntity.setTitle(filePrefix + "导出失败");
            messagecontrolEntity.setContent(getFailContent(e.getMessage()));
        } finally {
            excelExportLogService.updateById(excelExportlogEntity);
            commonMessageService.sendMessage(messagecontrolEntity);
            cacheClient.clean(key);
        }
    }

    @Override
    public List<TaxCodeReportDto> toExportDto(List<TXfTaxCodeReportEntity> entity) {
        if (entity == null) {
            return null;
        }
        List<TaxCodeReportDto> list = new ArrayList<TaxCodeReportDto>(entity.size());
        for (TXfTaxCodeReportEntity tXfTaxCodeReportEntity : entity) {
            TaxCodeReportDto taxCodeReportDto = new TaxCodeReportDto();
            BeanUtil.copyProperties(tXfTaxCodeReportEntity, taxCodeReportDto);
            taxCodeReportDto.setUpdateTime(DateUtils.format(tXfTaxCodeReportEntity.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
            list.add(taxCodeReportDto);
        }
        return list;
    }

    public String getSuccContent() {
        String createDate = DateUtils.format(new Date());
        String content = "申请时间：" + createDate + "。申请导出成功，可以下载！";
        return content;
    }

    /**
     * 获取导出失败的内容
     *
     * @param errmsg 错误信息
     * @return
     * @since 1.0
     */
    public String getFailContent(String errmsg) {
        StringBuilder content = new StringBuilder();
        content.append("申请时间：");
        String createDate = DateUtils.format(new Date());
        content.append(createDate);
        content.append("。申请导出失败，请重新申请！");
        return content.toString();
    }


    /**
     * 获取excel下载连接
     *
     * @param id
     * @return
     * @since 1.0
     */
    public String getUrl(long id) {
        String url = downLoadurl + "?serviceType=2&downloadId=" + id;
        return url;
    }

    private List<TXfTaxCodeReportEntity> getExportData(TaxCodeReportRequest request, ExceptionReportTypeEnum typeEnum, long lastId) {
        Page<TXfTaxCodeReportEntity> page = new Page<>();
        page.setSize(request.getSize());
        page.setCurrent(request.getPage());
        page.addOrder(OrderItem.desc("id"));

        TXfTaxCodeReportEntity entity = new TXfTaxCodeReportEntity();
        entity.setItemName(request.getItemName());
        entity.setItemNo(request.getItemNo());
        final QueryWrapper<TXfTaxCodeReportEntity> wrapper = Wrappers.query(entity);
        if (StringUtils.isNotBlank(request.getStartUpdateTime())) {
            wrapper.gt(TXfTaxCodeReportEntity.UPDATE_TIME, request.getStartUpdateTime());
        }

        if (StringUtils.isNotBlank(request.getEndUpdateTime())) {
            try {
                final String format = DateUtils.addDayToYYYYMMDD(request.getEndUpdateTime(), 1);
                wrapper.lt(TXfTaxCodeReportEntity.UPDATE_TIME, format);
            } catch (Exception e) {
                log.error("报告时间转换失败" + e.getMessage(), e);
            }
        }
        wrapper.orderByDesc(TXfTaxCodeReportEntity.ID);
        wrapper.in(CollectionUtils.isNotEmpty(request.getIds()), TXfTaxCodeReportEntity.ID, request.getIds());
        wrapper.eq(StringUtils.isNotBlank(request.getDisposeStatus()), TXfTaxCodeReportEntity.DISPOSE_STATUS, request.getDisposeStatus());
        return this.list(wrapper);

    }


    private Map<String, String> excelHeadTaxCodeReport() {
        Map<String, String> head = new LinkedHashMap<>();
        head.put("reportDesc", "例外说明");
        head.put("updateTime", "报告时间");
        head.put("itemNo", "商品号/Item Number");
        head.put("itemName", "商品描述一/Description 1");
        head.put("goodsTaxNo", "商品税收编码");
        head.put("taxRate", "销项税");
        head.put("zeroTax", "零税率标志");
        head.put("taxPre", "优惠政策标识");
        head.put("taxPreCon", "优惠政策内容");
        return head;
    }

}
