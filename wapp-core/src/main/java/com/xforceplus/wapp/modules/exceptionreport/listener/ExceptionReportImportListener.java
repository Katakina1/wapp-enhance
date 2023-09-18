package com.xforceplus.wapp.modules.exceptionreport.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.modules.exceptionreport.model.excel.ExceptionReportImportError;
import com.xforceplus.wapp.modules.exceptionreport.model.excel.ExceptionReportImport;
import com.xforceplus.wapp.modules.exceptionreport.service.impl.ExceptionReportServiceImpl;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;

import io.vavr.Tuple3;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionReportImportListener extends AnalysisEventListener<ExceptionReportImport> {
    private List<ExceptionReportImportError> importErrorNoList = new ArrayList<>();
    private ExceptionReportServiceImpl exceptionReportService;
    private Tuple3<Long,Long,String> tuple3;
    private ExportCommonService exportCommonService;
    private FtpUtilService ftpUtilService;
    public ExceptionReportImportListener(ExceptionReportServiceImpl exceptionReportService,FtpUtilService ftpUtilService,ExportCommonService exportCommonService,Tuple3<Long,Long,String> tuple3 ){
        this.exceptionReportService = exceptionReportService;
        this.exportCommonService = exportCommonService;
        this.ftpUtilService = ftpUtilService;
        this.tuple3 = tuple3;
    }

    @Override
    public void invoke(ExceptionReportImport record, AnalysisContext analysisContext) {
    	List<String> fieldList = Lists.newArrayList();
    	if(StringUtils.isEmpty(record.getId())) {
        	fieldList.add("流水号");
    	}
    	if(record.getId()!= null && StringUtils.isEmpty( record.getRemark())) {
    		fieldList.add("备注");
    	}
    	if(CollectionUtils.isEmpty(fieldList)) {
        	String remark = record.getRemark();
    		TXfExceptionReportEntity exceptionReportEntity = exceptionReportService.getById(record.getId());
    		if(exceptionReportEntity!=null) {
    			exceptionReportEntity.setRemark(record.getRemark());
    			exceptionReportService.updateById(exceptionReportEntity);
    		}else {
    			ExceptionReportImportError errorInfo = new ExceptionReportImportError();
    			errorInfo.setId(record.getId());
    			errorInfo.setErrorMsg("流水号不存在");
    			importErrorNoList.add(errorInfo);
    		}
    	}else {
			ExceptionReportImportError errorInfo = new ExceptionReportImportError();
			errorInfo.setId(record.getId());
			errorInfo.setErrorMsg(fieldList.stream().collect(Collectors.joining())+"不能为空");
			importErrorNoList.add(errorInfo);
    	}
    }




    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    	log.info("例外报告excel解析完处理");
        if (!CollectionUtils.isEmpty(importErrorNoList)){
            //如果存在失败生成错误文件，发送小叮当
            //推送sftp
            final String excelFileName = ExcelExportUtil.getExcelFileName(tuple3._2, "索赔单例外报告导入结果");
            String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String ftpFilePath = ftpPath + "/" + excelFileName;
            log.info("文件ftp路径{}",ftpFilePath);

            String localFilePath = ftpFilePath.substring(1);
            File localFile = FileUtils.getFile(localFilePath);
            if (!localFile.getParentFile().exists()) {
                localFile.getParentFile().mkdirs();
            }

            OutputStream out = null;
            try {
                out = new FileOutputStream(localFile);
            } catch (FileNotFoundException e) {
                log.error("new FileOutputStream(localFile) err!",e);
            }
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);

            Sheet sheet = new Sheet(1, 0, ExceptionReportImportError.class);

            sheet.setSheetName("索赔单例外报告导入错误信息");

            writer.write(importErrorNoList, sheet);

            writer.finish();
            String s = exportCommonService.putFile(ftpPath,localFilePath, excelFileName);

            try {
                out.close();
            } catch (IOException e) {
                log.info(" out.close() err!",e);
            }

            exportCommonService.updatelogStatus(tuple3._1, ExcelExportLogService.FAIL, ftpFilePath);
            exportCommonService.sendMessage(tuple3._1,tuple3._3,"索赔单例外报告导入存在失败数据",exportCommonService.getFailContent("导入存在失败数据,失败详情见附件"));

        }else {
            String userName = exportCommonService.updatelogStatus(tuple3._1, ExcelExportLogService.OK,null);
            exportCommonService.sendMessageWithUrl(tuple3._1,userName,"索赔单例外报告导入成功", "",null);
        }
    }
}
