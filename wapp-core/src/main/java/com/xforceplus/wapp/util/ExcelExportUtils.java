package com.xforceplus.wapp.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.entryaccount.dto.TDxSummonsRMSDto;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * @Author: ChenHang
 * @Date: 2023/8/10 9:56
 */
@Component
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ExcelExportUtils {

    @Autowired
    private ExportCommonService exportCommonService;

    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Autowired
    private FtpUtilService ftpUtilService;

    /**
     * 多页导出
     * 多页及exportDtoLists存储多个list, 数据不能为空, 为空会失败
     * @param exportDtoLists 需要导出的数据
     * @param fileName 文件名
     * @param requestJsonStr 请求查询参数
     * @param sheetName sheet页名称
     */
    public void messageExportMoreSheet(List<List> exportDtoLists, List<Class> clazzs, String fileName, String requestJsonStr, String... sheetName) {
        List<String> sheetNames = Arrays.asList(sheetName);
        if (exportDtoLists.size() != sheetNames.size()) {
            throw new RRException("页数不一致");
        }
        final String excelFileName = ExcelExportUtil.getExcelFileName(UserUtil.getUserId(), fileName);
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            String title = fileName + "成功";
            ExcelWriter excelWriter = EasyExcel.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
            for (int index = 0; index < exportDtoLists.size(); index++) {
                WriteSheet sheet = EasyExcel.writerSheet(index, sheetNames.get(index)).head(clazzs.get(index)).build();
                excelWriter.write(exportDtoLists.get(index), sheet);
            }
            excelWriter.finish();

//            // 本地测试代码
//            // 将 ByteArrayOutputStream 转换为字节数组
//            byte[] byteArray = outputStream.toByteArray();
//            // 创建一个新的 FileOutputStream 对象
//            FileOutputStream fileOutputStream = new FileOutputStream("F:\\测试more.xlsx");
//            fileOutputStream.write(byteArray);
//            // 关闭 FileOutputStream
//            fileOutputStream.close();

            this.pushSftp(ftpPath, excelFileName, outputStream, title, requestJsonStr);
        } catch (Exception e) {
            log.error("excel导出失败:", e);
        }

    }

    /**
     * excel导出 消息方式
     * @param exportDtos 导出实体类与excel对应
     * @param fileName 导出文件名 RMS非商入账传票清单 这种
     * @param requestJsonStr 导出数据查询参数
     */
    public void messageExportOneSheet(List exportDtos, Class clazz, String fileName, String requestJsonStr, String sheetName) {
        final String excelFileName = ExcelExportUtil.getExcelFileName(UserUtil.getUserId(), fileName + "数据导出");
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        try {

            String title = fileName + "导出成功";
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ExcelWriter excelWriter = EasyExcel.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
            WriteSheet sheet1 = EasyExcel.writerSheet(0, sheetName).head(clazz).build();
            excelWriter.write(exportDtos, sheet1);
            excelWriter.finish();

//            // 本地测试代码
//            // 将 ByteArrayOutputStream 转换为字节数组
//            byte[] byteArray = outputStream.toByteArray();
//            // 创建一个新的 FileOutputStream 对象
//            FileOutputStream fileOutputStream = new FileOutputStream("F:\\测试one.xlsx");
//            fileOutputStream.write(byteArray);
//            // 关闭 FileOutputStream
//            fileOutputStream.close();

            this.pushSftp(ftpPath, excelFileName, outputStream, title, requestJsonStr);
        } catch (Exception e) {
            log.error("excel单页导出失败:", e);
        }

    }

    private void pushSftp(String ftpPath, String excelFileName, ByteArrayOutputStream outputStream, String title, String requestJsonStr) {
        ByteArrayInputStream inputStream = null;
        try {
            //推送sftp
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ftpUtilService.uploadFile(ftpPath, excelFileName, inputStream);
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
            exportDto.setUserId(userId);
            exportDto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserName());
            excelExportlogEntity.setUserName(UserUtil.getLoginName());
            excelExportlogEntity.setConditions(requestJsonStr);
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + excelFileName);
            excelExportLogService.save(excelExportlogEntity);
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), title, exportCommonService.getSuccContent());
        }catch (Exception e) {
            log.error("导出异常:{}", e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }


}
