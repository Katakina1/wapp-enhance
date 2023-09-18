package com.xforceplus.wapp.modules.job.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.job.convert.HostUnMatchInvoiceConverter;
import com.xforceplus.wapp.modules.job.dto.HostUnMatchInvoiceExportDto;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.repository.dao.TAcUserDao;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.entity.TAcUserEntity;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * @program: wapp-generator
 * @description: bill job service
 * @author: Kenny Wong
 * @create: 2021-10-14 16:01
 **/
@Service
@Slf4j
public class HostUnMatchInvoiceService {

    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;
    @Autowired
    private TAcUserDao TAcUserDao;

    @Autowired
    private HostUnMatchInvoiceConverter hostUnMatchInvoiceConverter;

    @Autowired
    private FtpUtilService ftpUtilService;

    @Value("${wapp.export.tmp}")
    private String tmp;

    @Autowired
    ExportCommonService exportCommonService;

    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Autowired
    private CompanyService companyService;
    @Value("${host.login-name}")
    private String loginName;

    public List<TDxRecordInvoiceEntity> selectList(String venderId) {
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isEmpty(venderId)) {
            wrapper.select(TDxRecordInvoiceEntity.VENDERID);
            wrapper.groupBy(TDxRecordInvoiceEntity.VENDERID);
        } else {
            wrapper.eq(TDxRecordInvoiceEntity.VENDERID, venderId);
        }

        wrapper.eq(TDxRecordInvoiceEntity.QS_STATUS, "1");
        wrapper.eq(TDxRecordInvoiceEntity.HOST_STATUS, "0");
        wrapper.isNull(TDxRecordInvoiceEntity.MATCHNO);
        wrapper.eq(TDxRecordInvoiceEntity.INVOICE_STATUS, "0");
        wrapper.eq(TDxRecordInvoiceEntity.DXHY_MATCH_STATUS, "0");
        wrapper.and(wapper1->wapper1.eq(TDxRecordInvoiceEntity.RZH_YESORNO, "0").or().isNull(TDxRecordInvoiceEntity.RZH_YESORNO));
        wrapper.eq(TDxRecordInvoiceEntity.FLOW_TYPE, "1");
        Calendar ca1 = Calendar.getInstance();
        ca1.add(Calendar.YEAR, -1);
        wrapper.gt(TDxRecordInvoiceEntity.INVOICE_AMOUNT,"0");
        Calendar ca = Calendar.getInstance();
        wrapper.between(TDxRecordInvoiceEntity.INVOICE_DATE, ca1.getTime(), ca.getTime());
        Calendar ca2 = Calendar.getInstance();
        ca2.add(Calendar.MONTH, -1);
        wrapper.lt(TDxRecordInvoiceEntity.QS_DATE, ca2.getTime());

        return tDxRecordInvoiceDao.selectList(wrapper);
    }

    public List<TDxRecordInvoiceEntity> selectAllList() {
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();

        wrapper.eq(TDxRecordInvoiceEntity.QS_STATUS, "1");
        wrapper.eq(TDxRecordInvoiceEntity.HOST_STATUS, "0");
        wrapper.isNull(TDxRecordInvoiceEntity.MATCHNO);
        wrapper.eq(TDxRecordInvoiceEntity.INVOICE_STATUS, "0");
        wrapper.eq(TDxRecordInvoiceEntity.DXHY_MATCH_STATUS, "0");
        wrapper.and(wapper1->wapper1.eq(TDxRecordInvoiceEntity.RZH_YESORNO, "0").or().isNull(TDxRecordInvoiceEntity.RZH_YESORNO));
        wrapper.eq(TDxRecordInvoiceEntity.FLOW_TYPE, "1");
        wrapper.gt(TDxRecordInvoiceEntity.INVOICE_AMOUNT,"0");
        Calendar ca1 = Calendar.getInstance();
        ca1.add(Calendar.YEAR, -1);
        Calendar ca = Calendar.getInstance();
        wrapper.between(TDxRecordInvoiceEntity.INVOICE_DATE, ca1.getTime(), ca.getTime());
        Calendar ca2 = Calendar.getInstance();
        ca2.add(Calendar.MONTH, -1);
        wrapper.lt(TDxRecordInvoiceEntity.QS_DATE, ca2.getTime());

        return tDxRecordInvoiceDao.selectList(wrapper);
    }

    public void sendVenderMessage(List<TDxRecordInvoiceEntity> venderList) {
        venderList.stream().forEach(e -> {
            if (e!=null&&StringUtils.isNotEmpty(e.getVenderid())) {
                List<TDxRecordInvoiceEntity> resultList = this.selectList(e.getVenderid());
                if (CollectionUtils.isNotEmpty(resultList)) {
                    List<HostUnMatchInvoiceExportDto> exportList = hostUnMatchInvoiceConverter.exportMap(resultList);
                    if (CollectionUtils.isNotEmpty(exportList)) {
                        this.exportVender(exportList, e.getVenderid());
                    }
                }
            }
        });
    }

    public void sendWalmartMessage() {
        List<TDxRecordInvoiceEntity> resultList = selectAllList();
        if (CollectionUtils.isNotEmpty(resultList)) {
            List<HostUnMatchInvoiceExportDto> exportList = hostUnMatchInvoiceConverter.exportMap(resultList);
            if (CollectionUtils.isNotEmpty(exportList)) {
                this.exportWalmart(exportList);
            }
        }
    }

    public void exportVender(List<HostUnMatchInvoiceExportDto> resultList, String venderId) {
        FileInputStream inputStream = null;
        try {
            QueryWrapper<TAcUserEntity> wrapper = new QueryWrapper<TAcUserEntity>();
            wrapper.select("top 1 *");
            wrapper.eq(TAcUserEntity.USERCODE, venderId);
            TAcUserEntity userEntity = TAcUserDao.selectOne(wrapper);
            if (Objects.nonNull(userEntity)) {
                StringBuilder excelFileName = new StringBuilder();
                excelFileName.append("未提交匹配关系发票清单_").append(venderId).append("_").
                        append(new SimpleDateFormat("yyyyMMdd").format(new Date())).append(".xlsx");

                String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
               // ExcelWriter excelWriter;
                //创建一个sheet
                File file = FileUtils.getFile(tmp + ftpPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File excl = FileUtils.getFile(file, excelFileName.toString());
                EasyExcel.write(tmp + ftpPath + "/" + excelFileName, HostUnMatchInvoiceExportDto.class).sheet("sheet1").doWrite(resultList);
                //推送sftp
                //String ftpFilePath = ftpPath + "/" + excelFileName;
                inputStream = FileUtils.openInputStream(excl);
                ftpUtilService.uploadFile(ftpPath, excelFileName.toString(), inputStream);
                final Integer userId = userEntity.getUserid();
                ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
                exportDto.setUserId(Long.parseLong(String.valueOf(userId)));
                exportDto.setLoginName(userEntity.getLoginname());
                TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
                excelExportlogEntity.setCreateDate(new Date());
                //这里的userAccount是userid
                excelExportlogEntity.setUserAccount(userEntity.getUsername());
                excelExportlogEntity.setUserName(userEntity.getLoginname());
                excelExportlogEntity.setConditions(JSON.toJSONString(venderId));
                excelExportlogEntity.setStartDate(new Date());
                excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
                excelExportlogEntity.setServiceType(SERVICE_TYPE);
                excelExportlogEntity.setFilepath(ftpPath + "/" + excelFileName);
                this.excelExportLogService.save(excelExportlogEntity);
                exportDto.setLogId(excelExportlogEntity.getId());
                exportCommonService.sendMessage(excelExportlogEntity.getId(), userEntity.getLoginname(), "文档【" + excelFileName + "】中的发票未提交匹配关系，请尽快提交！", exportCommonService.getSuccContent());
            } else {
                log.info("HostUnMatchInvoiceService:export 未找到具体的用户信息");
            }
        } catch (Exception e) {
            log.error("HostUnMatchInvoiceService:export 导出异常:{}，venderId:{}", e, venderId);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("HostUnMatchInvoiceService:export.inputStream.close：{}", e);
                }
            }
        }

    }

    public void exportWalmart(List<HostUnMatchInvoiceExportDto> resultList) {
        FileInputStream inputStream = null;
        try {
            QueryWrapper<TAcUserEntity> wrapper = new QueryWrapper<TAcUserEntity>();
            String arr[] = loginName.split(",");
            wrapper.in(TAcUserEntity.LOGINNAME, Arrays.asList(arr));
            List<TAcUserEntity> userEntity = TAcUserDao.selectList(wrapper);
            if (CollectionUtils.isNotEmpty(userEntity)) {
                StringBuilder excelFileName = new StringBuilder();
                excelFileName.append("未提交匹配关系发票清单_").append("_").
                        append(new SimpleDateFormat("yyyyMMdd").format(new Date())).append(".xlsx");

                String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

                //创建一个sheet
                File file = FileUtils.getFile(tmp + ftpPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File excl = FileUtils.getFile(file, excelFileName.toString());
                EasyExcel.write(tmp + ftpPath + "/" + excelFileName, HostUnMatchInvoiceExportDto.class).sheet("sheet1").doWrite(resultList);
                //推送sftp
                inputStream = FileUtils.openInputStream(excl);
                ftpUtilService.uploadFile(ftpPath, excelFileName.toString(), inputStream);
                userEntity.stream().forEach(e -> {
                    final Integer userId = e.getUserid();
                    ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
                    exportDto.setUserId(Long.parseLong(String.valueOf(userId)));
                    exportDto.setLoginName(e.getLoginname());
                    TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
                    excelExportlogEntity.setCreateDate(new Date());
                    //这里的userAccount是userid
                    excelExportlogEntity.setUserAccount(e.getUsername());
                    excelExportlogEntity.setUserName(e.getLoginname());
                    excelExportlogEntity.setConditions(JSON.toJSONString(arr));
                    excelExportlogEntity.setStartDate(new Date());
                    excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
                    excelExportlogEntity.setServiceType(SERVICE_TYPE);
                    excelExportlogEntity.setFilepath(ftpPath + "/" + excelFileName);
                    this.excelExportLogService.save(excelExportlogEntity);
                    exportDto.setLogId(excelExportlogEntity.getId());
                    exportCommonService.sendMessage(excelExportlogEntity.getId(), e.getLoginname(), "文档【" + excelFileName + "】中的发票未提交匹配关系，请尽快提交！", exportCommonService.getSuccContent());
                });

            } else {
                log.info("HostUnMatchInvoiceService:export 未找到具体的用户信息");
            }
        } catch (Exception e) {
            log.error("HostUnMatchInvoiceService:export 导出异常:{}，venderId:{}", e, loginName);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("HostUnMatchInvoiceService:export.inputStream.close：{}", e);
                }
            }
        }

    }

}