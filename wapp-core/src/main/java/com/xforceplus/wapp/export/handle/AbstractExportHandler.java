package com.xforceplus.wapp.export.handle;//package com.xforceplus.wapp.export.handle;
//
//import com.xforceplus.wapp.common.utils.DateUtils;
//import com.xforceplus.wapp.common.utils.ExcelExportUtil;
//import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
//import com.xforceplus.wapp.export.IExportHandler;
//import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
//import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
//import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
//import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
//import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
//import com.xforceplus.wapp.service.CommonMessageService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.Message;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * @author malong@xforceplus.com
// * @program wapp-enhance
// * @description
// * @create 2021-10-23 13:43
// **/
//@Slf4j
//public abstract class AbstractExportHandler implements IExportHandler {
//
//    @Autowired
//    private CommonMessageService commonMessageService;
//
//    @Autowired
//    private FtpUtilService ftpUtilService;
//
//    @Autowired
//    private ExcelExportLogService excelExportLogService;
//
//    private final String downLoadurl = "api/core/ftp/download";
//
//    @Override
//    public void doExport(Message<String> message, String messageId) {
//        ExceptionReportRequest request = exportDto.getRequest();
//        ExceptionReportTypeEnum typeEnum = exportDto.getType();
//        //这里的userAccount是userid
//        final TDxExcelExportlogEntity excelExportlogEntity = excelExportLogService.getById(exportDto.getLogId());
//        excelExportlogEntity.setEndDate(new Date());
//        excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
//
//        TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
//        //这里的userAccount是userName
//        messagecontrolEntity.setUserAccount(exportDto.getLoginName());
//        messagecontrolEntity.setContent(getSuccContent());
//        try {
//            byte[] bytes=export(message,messageId);
//            final String excelFileName = ExcelExportUtil.getExcelFileName(exportDto.getUserId(), filePrefix);
//
//            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
//
//            //推送sftp
//            String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
//            String ftpFilePath = ftpPath + "/" + excelFileName;
//            ftpUtilService.uploadFile(ftpPath, excelFileName, is);
//            messagecontrolEntity.setUrl(getUrl(excelExportlogEntity.getId()));
//            excelExportlogEntity.setFilepath(ftpFilePath);
//            messagecontrolEntity.setTitle(title() + "导出成功");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("例外报告导出失败:" + e.getMessage(), e);
//            excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
//            excelExportlogEntity.setErrmsg(e.getMessage());
//            messagecontrolEntity.setTitle(title() + "导出失败");
//            messagecontrolEntity.setContent(getFailContent(e.getMessage()));
//
//        } finally {
//            excelExportLogService.updateById(excelExportlogEntity);
//            commonMessageService.sendMessage(messagecontrolEntity);
//        }
//
//    }
//
//    abstract protected byte[] export(Message<String> message, String messageId);
//
//
//    private void sendMessage(boolean success,String loginName){
//        TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
//        messagecontrolEntity.setTitle(title()+(success?"成功":"失败"));
//        messagecontrolEntity.setUserAccount(loginName);
//        commonMessageService.sendMessage(messagecontrolEntity);
//    }
//
//    abstract protected String title();
//
//    protected String getPatch(){
//        return ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
//    }
//
//
//    /**
//     * 获取导出成功内容
//     *
//     * @return
//     * @since 1.0
//     */
//    public String getSuccContent() {
//        String createDate = DateUtils.format(new Date());
//        String content = "申请时间：" + createDate + "。申请导出成功，可以下载！";
//        return content;
//    }
//
//
//    /**
//     * 获取导出失败的内容
//     *
//     * @param errmsg 错误信息
//     * @return
//     * @since 1.0
//     */
//    public String getFailContent(String errmsg) {
//        StringBuilder content = new StringBuilder();
//        content.append("申请时间：");
//        String createDate = DateUtils.format(new Date());
//        content.append(createDate);
//        content.append("。申请导出失败，请重新申请！");
//        return content.toString();
//    }
//
//
//    /**
//     * 获取excel下载连接
//     *
//     * @param id
//     * @return
//     * @since 1.0
//     */
//    public String getUrl(long id) {
//        String url = downLoadurl + "?serviceType=2&downloadId=" + id;
//        return url;
//    }
//}