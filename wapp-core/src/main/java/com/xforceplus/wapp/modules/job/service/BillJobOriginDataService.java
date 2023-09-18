package com.xforceplus.wapp.modules.job.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.exceptionreport.BillJobOriginDataTypeEnum;
import com.xforceplus.wapp.export.dto.BillJobOriginDataExportDto;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.job.dto.*;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.daoExt.OriginBillDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.repository.vo.OriginClaimBillVo;
import com.xforceplus.wapp.service.CommonMessageService;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Xforce
 */
@Service
@Slf4j
public class BillJobOriginDataService {

    @Autowired
    private TXfOriginClaimBillDao tXfOriginClaimBillDao;
    @Autowired
    private TXfOriginClaimItemHyperDao tXfOriginClaimItemHyperDao;
    @Autowired
    private TXfOriginClaimItemSamsDao tXfOriginClaimItemSamsDao;
    @Autowired
    private TXfOriginSapFbl5nDao tXfOriginSapFbl5nDao;
    @Autowired
    private TXfOriginSapZarrDao tXfOriginSapZarrDao;
    @Autowired
    private TXfOriginEpdBillDao tXfOriginEpdBillDao;
    @Autowired
    private TXfOriginEpdLogItemDao tXfOriginEpdLogItemDao;
    @Autowired
    private ExcelExportLogService excelExportLogService;
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    private CommonMessageService commonMessageService;
    @Autowired
    private OriginBillDao originBillDao;

    private final String downLoadurl = "api/core/ftp/download";

    @Deprecated
    public PageResult<TXfOriginClaimBillEntity> claimInfo(String exchangeNo, Date startDate, Date endDate,
                                                          Integer page, Integer size) {
        QueryWrapper<TXfOriginClaimBillEntity> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(exchangeNo)){
        	queryWrapper.eq(TXfOriginClaimBillEntity.EXCHANGE_NO, exchangeNo);
        }
        queryWrapper.ge(TXfOriginClaimBillEntity.CREATE_TIME, startDate);
        queryWrapper.le(TXfOriginClaimBillEntity.CREATE_TIME, endDate);
        queryWrapper.eq(TXfOriginClaimBillEntity.CHECK_STATUS, 1);
        queryWrapper.orderByDesc(TXfOriginClaimBillEntity.CREATE_TIME);
        Page<TXfOriginClaimBillEntity> pageResult = tXfOriginClaimBillDao.selectPage(new Page<>(page, size), queryWrapper);
        PageResult<TXfOriginClaimBillEntity> result = new PageResult<>();
        result.setRows(pageResult.getRecords());
        PageResult.Summary summary = new PageResult.Summary();
        summary.setPages(pageResult.getPages());
        summary.setSize(pageResult.getSize());
        summary.setTotal(pageResult.getTotal());
        result.setSummary(summary);
        return result;
    }

    public PageResult<OriginClaimBillVo> claimInfo(String exchangeNo, Date startDate, Date endDate, String jobName,
                                                      Integer page, Integer size) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("checkStatus", 1);
        params.put("offsetStart", (page - 1) * size + 1);
        params.put("offsetEnd", page * size);
        if (StringUtils.isNotBlank(exchangeNo)) {
            params.put("exchangeNo", exchangeNo);
        }
        if (StringUtils.isNotBlank(jobName)) {
            params.put("jobName", "%" + jobName + "%");
        }
        long count = originBillDao.countOriginClaim(params);

        PageResult<OriginClaimBillVo> result = new PageResult<>();
        PageResult.Summary summary = new PageResult.Summary();
        summary.setPages((count / size) + 1);
        summary.setSize(size);
        summary.setTotal(count);
        result.setSummary(summary);
        if (count == 0) {
            result.setRows(Lists.newArrayList());
        } else {
            List<OriginClaimBillVo> originClaimBillVos = originBillDao.selectOriginClaimPage(params);
            result.setRows(originClaimBillVos);
        }
        return result;
    }

    public PageResult<TXfOriginClaimItemHyperEntity> claimHyper(Date startDate, Date endDate,
                                                                Integer page, Integer size) {
        QueryWrapper<TXfOriginClaimItemHyperEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(TXfOriginClaimItemHyperEntity.CREATE_TIME, startDate);
        queryWrapper.le(TXfOriginClaimItemHyperEntity.CREATE_TIME, endDate);
        queryWrapper.eq(TXfOriginClaimItemHyperEntity.CHECK_STATUS, 1);
        Page<TXfOriginClaimItemHyperEntity> pageResult = tXfOriginClaimItemHyperDao.selectPage(new Page<>(page, size), queryWrapper);
        PageResult<TXfOriginClaimItemHyperEntity> result = new PageResult<>();
        result.setRows(pageResult.getRecords());
        PageResult.Summary summary = new PageResult.Summary();
        summary.setPages(pageResult.getPages());
        summary.setSize(pageResult.getSize());
        summary.setTotal(pageResult.getTotal());
        result.setSummary(summary);
        return result;
    }

    public PageResult<TXfOriginClaimItemSamsEntity> claimSams(Date startDate, Date endDate,
                                                              Integer page, Integer size) {
        QueryWrapper<TXfOriginClaimItemSamsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(TXfOriginClaimItemSamsEntity.CREATE_TIME, startDate);
        queryWrapper.le(TXfOriginClaimItemSamsEntity.CREATE_TIME, endDate);
        queryWrapper.eq(TXfOriginClaimItemSamsEntity.CHECK_STATUS, 1);
        Page<TXfOriginClaimItemSamsEntity> pageResult = tXfOriginClaimItemSamsDao.selectPage(new Page<>(page, size), queryWrapper);
        PageResult<TXfOriginClaimItemSamsEntity> result = new PageResult<>();
        result.setRows(pageResult.getRecords());
        PageResult.Summary summary = new PageResult.Summary();
        summary.setPages(pageResult.getPages());
        summary.setSize(pageResult.getSize());
        summary.setTotal(pageResult.getTotal());
        result.setSummary(summary);
        return result;
    }

    public PageResult<TXfOriginSapZarrEntity> agreementZarr(Date startDate, Date endDate,
                                                            Integer page, Integer size) {
        QueryWrapper<TXfOriginSapZarrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(TXfOriginSapZarrEntity.CREATE_TIME, startDate);
        queryWrapper.le(TXfOriginSapZarrEntity.CREATE_TIME, endDate);
        queryWrapper.eq(TXfOriginSapZarrEntity.CHECK_STATUS, 1);
        Page<TXfOriginSapZarrEntity> pageResult = tXfOriginSapZarrDao.selectPage(new Page<>(page, size), queryWrapper);
        PageResult<TXfOriginSapZarrEntity> result = new PageResult<>();
        result.setRows(pageResult.getRecords());
        PageResult.Summary summary = new PageResult.Summary();
        summary.setPages(pageResult.getPages());
        summary.setSize(pageResult.getSize());
        summary.setTotal(pageResult.getTotal());
        result.setSummary(summary);
        return result;
    }

    public PageResult<TXfOriginSapFbl5nEntity> agreementFbl5n(Date startDate, Date endDate,
                                                              Integer page, Integer size) {
        QueryWrapper<TXfOriginSapFbl5nEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(TXfOriginSapFbl5nEntity.CREATE_TIME, startDate);
        queryWrapper.le(TXfOriginSapFbl5nEntity.CREATE_TIME, endDate);
        queryWrapper.eq(TXfOriginSapFbl5nEntity.CHECK_STATUS, 1);
        Page<TXfOriginSapFbl5nEntity> pageResult = tXfOriginSapFbl5nDao.selectPage(new Page<>(page, size), queryWrapper);
        PageResult<TXfOriginSapFbl5nEntity> result = new PageResult<>();
        result.setRows(pageResult.getRecords());
        PageResult.Summary summary = new PageResult.Summary();
        summary.setPages(pageResult.getPages());
        summary.setSize(pageResult.getSize());
        summary.setTotal(pageResult.getTotal());
        result.setSummary(summary);
        return result;
    }

    public PageResult<TXfOriginEpdBillEntity> epdInfo(Date startDate, Date endDate,
                                                      Integer page, Integer size) {
        QueryWrapper<TXfOriginEpdBillEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(TXfOriginEpdBillEntity.CREATE_TIME, startDate);
        queryWrapper.le(TXfOriginEpdBillEntity.CREATE_TIME, endDate);
        queryWrapper.eq(TXfOriginEpdBillEntity.CHECK_STATUS, 1);
        Page<TXfOriginEpdBillEntity> pageResult = tXfOriginEpdBillDao.selectPage(new Page<>(page, size), queryWrapper);
        PageResult<TXfOriginEpdBillEntity> result = new PageResult<>();
        result.setRows(pageResult.getRecords());
        PageResult.Summary summary = new PageResult.Summary();
        summary.setPages(pageResult.getPages());
        summary.setSize(pageResult.getSize());
        summary.setTotal(pageResult.getTotal());
        result.setSummary(summary);
        return result;
    }

    public PageResult<TXfOriginEpdLogItemEntity> epdLog(Date startDate, Date endDate,
                                                        Integer page, Integer size) {
        QueryWrapper<TXfOriginEpdLogItemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(TXfOriginEpdLogItemEntity.CREATE_TIME, startDate);
        queryWrapper.le(TXfOriginEpdLogItemEntity.CREATE_TIME, endDate);
        queryWrapper.eq(TXfOriginEpdLogItemEntity.CHECK_STATUS, 1);
        Page<TXfOriginEpdLogItemEntity> pageResult = tXfOriginEpdLogItemDao.selectPage(new Page<>(page, size), queryWrapper);
        PageResult<TXfOriginEpdLogItemEntity> result = new PageResult<>();
        result.setRows(pageResult.getRecords());
        PageResult.Summary summary = new PageResult.Summary();
        summary.setPages(pageResult.getPages());
        summary.setSize(pageResult.getSize());
        summary.setTotal(pageResult.getTotal());
        result.setSummary(summary);
        return result;
    }

    public void exportBillJobOriginExceptionData(BillJobOriginDataExportDto exportDto) {
        log.info("导出异常业务数据Excel：{}", JSON.toJSONString(exportDto));
        final TDxExcelExportlogEntity excelExportlogEntity = excelExportLogService.getById(exportDto.getLogId());
        excelExportlogEntity.setEndDate(new Date());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);

        TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
        //这里的userAccount是userName
        messagecontrolEntity.setUserAccount(exportDto.getLoginName());
        messagecontrolEntity.setContent(getSuccContent());
        messagecontrolEntity.setTitle(exportDto.getType().getDesc() + "异常原始数据导出成功");

        try {
            File tempFile = null;
            if (exportDto.getType() == BillJobOriginDataTypeEnum.CLAIM_INFO) {
                tempFile = exportClaimInfo(exportDto);
            } else if (exportDto.getType() == BillJobOriginDataTypeEnum.CLAIM_HYPER) {
                tempFile = exportClaimHyper(exportDto);
            } else if (exportDto.getType() == BillJobOriginDataTypeEnum.CLAIM_SAMS) {
                tempFile = exportClaimSams(exportDto);
            } else if (exportDto.getType() == BillJobOriginDataTypeEnum.AGREEMENT_FBL5N) {
                tempFile = exportAgreementFbl5n(exportDto);
            } else if (exportDto.getType() == BillJobOriginDataTypeEnum.AGREEMENT_ZARR) {
                tempFile = exportAgreementZarr(exportDto);
            } else if (exportDto.getType() == BillJobOriginDataTypeEnum.EPD_INFO) {
                tempFile = exportEpdInfo(exportDto);
            } else if (exportDto.getType() == BillJobOriginDataTypeEnum.EPD_LOG) {
                tempFile = exportEpdLog(exportDto);
            }
            final String excelFileName = ExcelExportUtil.getExcelFileName(exportDto.getUserId(), exportDto.getType().getDesc());
            //推送sftp
            String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String ftpFilePath = ftpPath + "/" + excelFileName;
            ftpUtilService.uploadFile(ftpPath, excelFileName, new FileInputStream(tempFile));
            messagecontrolEntity.setUrl(getUrl(excelExportlogEntity.getId()));
            excelExportlogEntity.setFilepath(ftpFilePath);
        } catch (Exception e) {
            log.error("数据导出失败:" + e.getMessage(), e);
            excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
            excelExportlogEntity.setErrmsg(e.getMessage());
            messagecontrolEntity.setTitle(exportDto.getType().getDesc() + "异常原始数据导出失败");
            messagecontrolEntity.setContent(getFailContent(e.getMessage()));
        } finally {
            excelExportLogService.updateById(excelExportlogEntity);
            commonMessageService.sendMessage(messagecontrolEntity);
        }
    }

    private File exportClaimInfo(BillJobOriginDataExportDto exportDto) throws IOException {
   //     String filePrefix = exportDto.getUserId() + "-" + System.currentTimeMillis() + "-" + exportDto.getType().getDesc();
        String filePrefix =  System.currentTimeMillis() + "-" + "索赔单主信息";
        String fileSuffix = "xlsx";
        int page = 1;
        int size = 500;
        File tempFile = File.createTempFile(filePrefix, fileSuffix);
        ExcelWriter excelWriter = EasyExcel.write(tempFile, OriginClaimBillDto.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(exportDto.getType().getSheet()).build();
        while (true) {
            PageResult<OriginClaimBillVo> pageResult = claimInfo(exportDto.getExchangeNo(), exportDto.getStartDate(), exportDto.getEndDate(), exportDto.getJobName(), page, size);
            List<OriginClaimBillDto> list = pageResult.getRows().stream().map(entity -> {
                OriginClaimBillDto originClaimBillDto = new OriginClaimBillDto();
                BeanUtils.copyProperties(entity, originClaimBillDto);
                if(null != entity.getCreateTime()) {
                    originClaimBillDto.setCreateTime(DateUtils.dateToStr(entity.getCreateTime()));
                }
                return originClaimBillDto;
            }).collect(Collectors.toList());
            excelWriter.write(list, writeSheet);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            page++;
        }
        excelWriter.finish();
        return tempFile;
    }

    private File exportClaimHyper(BillJobOriginDataExportDto exportDto) throws IOException {
       // String filePrefix = exportDto.getUserId() + "-" + System.currentTimeMillis() + "-" + exportDto.getType().getDesc();
        String filePrefix =  System.currentTimeMillis() + "-" + "索赔单hyper明细";
        String fileSuffix = "xlsx";
        int page = 1;
        int size = 500;
        File tempFile = File.createTempFile(filePrefix, fileSuffix);
        ExcelWriter excelWriter = EasyExcel.write(tempFile, OriginClaimItemHyperDto.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(exportDto.getType().getSheet()).build();
        while (true) {
            PageResult<TXfOriginClaimItemHyperEntity> pageResult = claimHyper(exportDto.getStartDate(), exportDto.getEndDate(), page, size);
            List<OriginClaimItemHyperDto> list = pageResult.getRows().stream().map(entity -> {
                OriginClaimItemHyperDto dto = new OriginClaimItemHyperDto();
                BeanUtils.copyProperties(entity, dto);
                return dto;
            }).collect(Collectors.toList());
            excelWriter.write(list, writeSheet);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            page++;
        }
        excelWriter.finish();
        return tempFile;
    }

    private File exportClaimSams(BillJobOriginDataExportDto exportDto) throws IOException {
      //  String filePrefix = exportDto.getUserId() + "-" + System.currentTimeMillis() + "-" + exportDto.getType().getDesc();
        String filePrefix =  System.currentTimeMillis() + "-" + "索赔单sams明细";
        String fileSuffix = "xlsx";
        int page = 1;
        int size = 500;
        File tempFile = File.createTempFile(filePrefix, fileSuffix);
        ExcelWriter excelWriter = EasyExcel.write(tempFile, OriginClaimItemSamsDto.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(exportDto.getType().getSheet()).build();
        while (true) {
            PageResult<TXfOriginClaimItemSamsEntity> pageResult = claimSams(exportDto.getStartDate(), exportDto.getEndDate(), page, size);
            List<OriginClaimItemSamsDto> list = pageResult.getRows().stream().map(entity -> {
                OriginClaimItemSamsDto dto = new OriginClaimItemSamsDto();
                BeanUtils.copyProperties(entity, dto);
                return dto;
            }).collect(Collectors.toList());
            excelWriter.write(list, writeSheet);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            page++;
        }
        excelWriter.finish();
        return tempFile;
    }

    private File exportAgreementFbl5n(BillJobOriginDataExportDto exportDto) throws IOException {
        //String filePrefix = exportDto.getUserId() + "-" + System.currentTimeMillis() + "-" + exportDto.getType().getDesc();
        String filePrefix =  System.currentTimeMillis() + "-" + "协议单fbl5n";
        String fileSuffix = "xlsx";
        int page = 1;
        int size = 500;
        File tempFile = File.createTempFile(filePrefix, fileSuffix);
        ExcelWriter excelWriter = EasyExcel.write(tempFile, OriginAgreementBillFbl5nDto.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(exportDto.getType().getSheet()).build();
        while (true) {
            PageResult<TXfOriginSapFbl5nEntity> pageResult = agreementFbl5n(exportDto.getStartDate(), exportDto.getEndDate(), page, size);
            List<OriginAgreementBillFbl5nDto> list = pageResult.getRows().stream().map(entity -> {
                OriginAgreementBillFbl5nDto dto = new OriginAgreementBillFbl5nDto();
                BeanUtils.copyProperties(entity, dto);
                return dto;
            }).collect(Collectors.toList());
            excelWriter.write(list, writeSheet);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            page++;
        }
        excelWriter.finish();
        return tempFile;
    }

    private File exportAgreementZarr(BillJobOriginDataExportDto exportDto) throws IOException {
//        String filePrefix = exportDto.getUserId() + "-" + System.currentTimeMillis() + "-" + exportDto.getType().getDesc();
        String filePrefix =  System.currentTimeMillis() + "-" + "协议单zarr";
        String fileSuffix = "xlsx";
        int page = 1;
        int size = 500;
        File tempFile = File.createTempFile(filePrefix, fileSuffix);
        ExcelWriter excelWriter = EasyExcel.write(tempFile, OriginAgreementBillZarrDto.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(exportDto.getType().getSheet()).build();
        while (true) {
            PageResult<TXfOriginSapZarrEntity> pageResult = agreementZarr(exportDto.getStartDate(), exportDto.getEndDate(), page, size);
            List<OriginAgreementBillZarrDto> list = pageResult.getRows().stream().map(entity -> {
                OriginAgreementBillZarrDto dto = new OriginAgreementBillZarrDto();
                BeanUtils.copyProperties(entity, dto);
                return dto;
            }).collect(Collectors.toList());
            excelWriter.write(list, writeSheet);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            page++;
        }
        excelWriter.finish();
        return tempFile;
    }

    private File exportEpdInfo(BillJobOriginDataExportDto exportDto) throws IOException {
//        String filePrefix = exportDto.getUserId() + "-" + System.currentTimeMillis() + "-" + exportDto.getType().getDesc();
        String filePrefix =  System.currentTimeMillis() + "-" + "epd单主信息";
        String fileSuffix = "xlsx";
        int page = 1;
        int size = 500;
        File tempFile = File.createTempFile(filePrefix, fileSuffix);
        ExcelWriter excelWriter = EasyExcel.write(tempFile, OriginEpdBillDto.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(exportDto.getType().getSheet()).build();
        while (true) {
            PageResult<TXfOriginEpdBillEntity> pageResult = epdInfo(exportDto.getStartDate(), exportDto.getEndDate(), page, size);
            List<OriginEpdBillDto> list = pageResult.getRows().stream().map(entity -> {
                OriginEpdBillDto dto = new OriginEpdBillDto();
                BeanUtils.copyProperties(entity, dto);
                return dto;
            }).collect(Collectors.toList());
            excelWriter.write(list, writeSheet);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            page++;
        }
        excelWriter.finish();
        return tempFile;
    }

    private File exportEpdLog(BillJobOriginDataExportDto exportDto) throws IOException {
       // String filePrefix = exportDto.getUserId() + "-" + System.currentTimeMillis() + "-" + exportDto.getType().getDesc();
        String filePrefix =  System.currentTimeMillis() + "-" + "epd单log";
        String fileSuffix = "xlsx";
        int page = 1;
        int size = 500;
        File tempFile = File.createTempFile(filePrefix, fileSuffix);
        ExcelWriter excelWriter = EasyExcel.write(tempFile, OriginEpdLogItemDto.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(exportDto.getType().getSheet()).build();
        while (true) {
            PageResult<TXfOriginEpdLogItemEntity> pageResult = epdLog(exportDto.getStartDate(), exportDto.getEndDate(), page, size);
            List<OriginEpdLogItemDto> list = pageResult.getRows().stream().map(entity -> {
                OriginEpdLogItemDto dto = new OriginEpdLogItemDto();
                BeanUtils.copyProperties(entity, dto);
                return dto;
            }).collect(Collectors.toList());
            excelWriter.write(list, writeSheet);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            page++;
        }
        excelWriter.finish();
        return tempFile;
    }


    /**
     * 获取导出成功内容
     *
     * @return
     * @since 1.0
     */
    private String getSuccContent() {
        String createDate = DateUtils.format(new Date());
        String content = "申请时间：" + createDate + "。申请导出成功，可以下载！";
        return content;
    }

    /**
     * 获取excel下载连接
     *
     * @param id
     * @return
     * @since 1.0
     */
    private String getUrl(long id) {
        String url = downLoadurl + "?serviceType=2&downloadId=" + id;
        return url;
    }

    /**
     * 获取导出失败的内容
     *
     * @param errmsg 错误信息
     * @return
     * @since 1.0
     */
    private String getFailContent(String errmsg) {
        StringBuilder content = new StringBuilder();
        content.append("申请时间：");
        String createDate = DateUtils.format(new Date());
        content.append(createDate);
        content.append("。申请导出失败，请重新申请！");
        return content.toString();
    }
}
