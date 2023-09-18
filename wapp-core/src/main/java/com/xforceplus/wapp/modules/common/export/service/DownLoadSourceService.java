package com.xforceplus.wapp.modules.common.export.service;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.backfill.model.InvoiceFileEntity;
import com.xforceplus.wapp.modules.backfill.service.FileService;
import com.xforceplus.wapp.modules.backfill.service.InvoiceFileService;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.noneBusiness.service.NoneBusinessService;
import com.xforceplus.wapp.modules.noneBusiness.util.ZipUtil;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

@Service
@Slf4j
public class DownLoadSourceService {
    @Autowired
    private FtpUtilService ftpUtilService;

    @Autowired
    ExportCommonService exportCommonService;

    @Autowired
    private FileService fileService;

    @Autowired
    private InvoiceFileService invoiceFileService;
    @Autowired

    private NoneBusinessService noneBusinessService;
    @Autowired
    private ExcelExportLogService excelExportLogService;

    public void down(List<TDxRecordInvoiceEntity> list, Long[] ids) {
        String path = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String ftpPath = ftpUtilService.pathprefix + path;
        log.info("文件ftp路径{}", ftpPath);
        final File tempDirectory = FileUtils.getTempDirectory();
        File file = FileUtils.getFile(tempDirectory, path);
        file.mkdir();
        String downLoadFileName = path + ".zip";
        for (TDxRecordInvoiceEntity entity : list) {
            TXfInvoiceFileEntity tXfInvoiceFileEntity = invoiceFileService.getSourceInvoiceFileUrl(entity.getInvoiceNo(), entity.getInvoiceCode(), entity.getInvoiceType());


            try {
                if (tXfInvoiceFileEntity != null) {
                    String path1 = tXfInvoiceFileEntity.getPath();
                    StringBuilder builder = new StringBuilder();
                    // 兼容费用上传的电子发票 如果为-1则表示从提供给费用, 开发的通用功能
                    if (!StringUtils.equals(tXfInvoiceFileEntity.getFileType(), "-1")) {
                        if(path1.startsWith("/evtaSystemIntegration")){
                            builder.append("/u/app");
                            builder.append(path1);
                            tXfInvoiceFileEntity.setPath(builder.toString());
                        }
                        if(path1.startsWith("/u/evtaSystemIntegration")){
                            builder.append(path1, 0, 2);
                            builder.append("/app");
                            builder.append(path1.substring(2));
                            tXfInvoiceFileEntity.setPath(builder.toString());
                        }
                    }

                    final byte[] bytes = fileService.downLoadFile4ByteArray(tXfInvoiceFileEntity.getPath());
                    String suffix = null;
                    if (tXfInvoiceFileEntity.getType().equals(InvoiceFileEntity.TYPE_OF_OFD)
                            || StringUtils.equals(tXfInvoiceFileEntity.getFileSuffix(), InvoiceFileEntity.SUFFIX_OF_OFD.replace(".", ""))) {
                        suffix = "." + Constants.SUFFIX_OF_OFD;
                    } else if (tXfInvoiceFileEntity.getType().equals(InvoiceFileEntity.TYPE_OF_PDF)
                            || StringUtils.equals(tXfInvoiceFileEntity.getFileSuffix(), InvoiceFileEntity.SUFFIX_OF_PDF.replace(".", ""))) {
                        suffix = "." + Constants.SUFFIX_OF_PDF;
                    }else if (tXfInvoiceFileEntity.getType().equals(InvoiceFileEntity.TYPE_OF_XML)
                            || StringUtils.equals(tXfInvoiceFileEntity.getFileSuffix(), InvoiceFileEntity.SUFFIX_OF_XML.replace(".", ""))) {
                        suffix = "." + Constants.SUFFIX_OF_XML;
                    }
                    if (StringUtils.isEmpty(tXfInvoiceFileEntity.getInvoiceNo()) || (tXfInvoiceFileEntity.getInvoiceNo().length() != 20 && StringUtils.isEmpty(tXfInvoiceFileEntity.getInvoiceCode()))) {
                        FileUtils.writeByteArrayToFile(FileUtils.getFile(file, "附件_" + path + suffix), bytes);
                    } else {
                        String name = tXfInvoiceFileEntity.getInvoiceNo() + "-" + Objects.toString(tXfInvoiceFileEntity.getInvoiceCode(), "");
                        FileUtils.writeByteArrayToFile(FileUtils.getFile(file, name + suffix), bytes);
                    }
                } else {
                    TXfNoneBusinessUploadQueryDto dto = new TXfNoneBusinessUploadQueryDto();
                    dto.setInvoiceNo(entity.getInvoiceNo());
                    dto.setInvoiceCode(entity.getInvoiceCode());
                    dto.setSubmitFlag(Constants.SUBMIT_NONE_BUSINESS_DONE_FLAG);
                    List<TXfNoneBusinessUploadDetailDto> resultList = noneBusinessService.noPaged(dto);
                    if (CollectionUtils.isEmpty(resultList)) {
                        continue;
                    }
                    TXfNoneBusinessUploadDetailDto detailDto = resultList.get(0);
                    final byte[] bytes = fileService.downLoadFile4ByteArray(detailDto.getSourceUploadPath());
                    String suffix = null;
                    if (detailDto.getFileType().equals(String.valueOf(Constants.FILE_TYPE_OFD))) {
                        suffix = "." + Constants.SUFFIX_OF_OFD;
                    } else if (detailDto.getFileType().equals(String.valueOf(Constants.FILE_TYPE_PDF))){
                        suffix = "." + Constants.SUFFIX_OF_PDF;
                    }else if (detailDto.getFileType().equals(String.valueOf(Constants.FILE_TYPE_XML))){
                        suffix = "." + Constants.SUFFIX_OF_XML;
                    }
                    if (StringUtils.isEmpty(detailDto.getInvoiceNo()) || (detailDto.getInvoiceNo().length() != 20 && StringUtils.isEmpty(detailDto.getInvoiceCode()))) {
                        FileUtils.writeByteArrayToFile(FileUtils.getFile(file, "附件_" + path + suffix), bytes);
                    } else {
                        String name = detailDto.getInvoiceNo() + "-" + Objects.toString(detailDto.getInvoiceCode(), "");
                        FileUtils.writeByteArrayToFile(FileUtils.getFile(file, name + suffix), bytes);
                    }
                }

            } catch (IOException e) {
                log.error("临时文件存储失败:" + e.getMessage(), e);
            }
        }
        try {
            ZipUtil.zip(file.getPath() + ".zip", file);
            String s = exportCommonService.putFile(ftpPath, tempDirectory.getPath() + "/" + downLoadFileName, downLoadFileName);
            log.info("down s:{}" , s);
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto dto = new ExceptionReportExportDto();
            dto.setUserId(userId);
            dto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccournt是userid
            excelExportlogEntity.setUserAccount(dto.getUserId().toString());
            excelExportlogEntity.setUserName(dto.getLoginName());
            excelExportlogEntity.setConditions(JSON.toJSONString(ids));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + downLoadFileName);
            this.excelExportLogService.save(excelExportlogEntity);
            dto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "发票综合查询源文件下载成功", exportCommonService.getSuccContent());
        } catch (Exception e) {
            log.error("下载文件打包失败:" + e.getMessage(), e);
            throw new RRException("下载文件打包失败，请重试");
        }
    }

    /**
     * 解决Absolute_Path_Traversal 临时建立的一个无效的变量
     */
    @Value("${xxxxx:/}")
    private String nasRootpath;
    
	public void downPdf(List<TDxRecordInvoiceEntity> list, List<TXfNoneBusinessUploadDetailEntity> entities, String[] ids) {
        String path = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		String ftpPath = ftpUtilService.pathprefix + path;
		log.info("文件ftp路径{}", ftpPath);
        final File tempDirectory = FileUtils.getTempDirectory();
		File file = FileUtils.getFile(tempDirectory, path);
		file.mkdir();
		String downLoadFileName = path + ".zip";
        for (TDxRecordInvoiceEntity entity : list) {
            if (StringUtils.isBlank(entity.getInvoiceNo())) {
                continue;
            }
            TXfInvoiceFileEntity tXfInvoiceFileEntity = invoiceFileService.getSourceInvoiceFileUrl(entity.getInvoiceNo(), entity.getInvoiceCode());
            try {
                if (tXfInvoiceFileEntity != null) {
                    String path1 = tXfInvoiceFileEntity.getPath();
                    StringBuilder builder = new StringBuilder();
                    if(path1.startsWith("/evtaSystemIntegration")){
                        builder.append("/u/app");
                        builder.append(path1);
                        tXfInvoiceFileEntity.setPath(builder.toString());
                    }
                    if(path1.startsWith("/u/evtaSystemIntegration")){
                        builder.append(path1, 0, 2);
                        builder.append("/app");
                        builder.append(path1.substring(2));
                        tXfInvoiceFileEntity.setPath(builder.toString());
                    }
                    final byte[] bytes = fileService.downLoadFile4ByteArray(nasRootpath + tXfInvoiceFileEntity.getPath());
                    if (Objects.isNull(bytes)) {
                        continue;
                    }
                    String name = tXfInvoiceFileEntity.getInvoiceNo() + "-" + Objects.toString(tXfInvoiceFileEntity.getInvoiceCode(), "");
                    FileUtils.writeByteArrayToFile(FileUtils.getFile(file,  name+ "." + Constants.SUFFIX_OF_PDF), bytes);
                } else {
                    TXfNoneBusinessUploadQueryDto dto = new TXfNoneBusinessUploadQueryDto();
                    dto.setInvoiceNo(entity.getInvoiceNo());
                    dto.setInvoiceCode(entity.getInvoiceCode());
                    dto.setSubmitFlag(Constants.SUBMIT_NONE_BUSINESS_DONE_FLAG);
                    List<TXfNoneBusinessUploadDetailDto> resultList = noneBusinessService.noPaged(dto);
                    if (CollectionUtils.isEmpty(resultList)) {
                        continue;
                    }
                    TXfNoneBusinessUploadDetailDto detailDto = resultList.get(0);
                    if (StringUtils.isBlank(detailDto.getUploadPath()) || detailDto.getUploadPath().lastIndexOf(".pdf") == -1) {
                          continue;
                    }
					final byte[] bytes = fileService.downLoadFile4ByteArray(nasRootpath + detailDto.getUploadPath());
                    String name = detailDto.getInvoiceNo() + "-" + Objects.toString(detailDto.getInvoiceCode(), "");
                    FileUtils.writeByteArrayToFile(FileUtils.getFile(file, name + "." + Constants.SUFFIX_OF_PDF), bytes);
                }

            } catch (IOException e) {
                log.error("临时文件存储失败:" + e.getMessage(), e);
            }
        }
        for (TXfNoneBusinessUploadDetailEntity entity : entities) {
            if (StringUtils.isBlank(entity.getUploadPath()) || entity.getUploadPath().lastIndexOf(".pdf") == -1) {
                continue;
            }
            try {
                final byte[] bytes = fileService.downLoadFile4ByteArray(entity.getUploadPath());
                FileUtils.writeByteArrayToFile(FileUtils.getFile(file, entity.getInvoiceNo() + "-" + Objects.toString(entity.getInvoiceCode(), "")+ "." + Constants.SUFFIX_OF_PDF), bytes);
            } catch (IOException e) {
                log.error("临时文件存储失败:" + e.getMessage(), e);
            }
        }
        try {
            ZipUtil.zip(file.getPath() + ".zip", file);
            exportCommonService.putFile(ftpPath, tempDirectory.getPath() + "/" + downLoadFileName, downLoadFileName);
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto dto = new ExceptionReportExportDto();
            dto.setUserId(userId);
            dto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccournt是userid
            excelExportlogEntity.setUserAccount(dto.getUserId().toString());
            excelExportlogEntity.setUserName(dto.getLoginName());
            excelExportlogEntity.setConditions(JSON.toJSONString(ids));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + downLoadFileName);
            this.excelExportLogService.save(excelExportlogEntity);
            dto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "电子发票PDF文件批量下载成功", exportCommonService.getSuccContent());
        } catch (Exception e) {
            log.error("下载文件打包失败:" + e.getMessage(), e);
            throw new RRException("下载文件打包失败，请重试");
        }
    }
}