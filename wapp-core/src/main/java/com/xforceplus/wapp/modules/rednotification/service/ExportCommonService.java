package com.xforceplus.wapp.modules.rednotification.service;


import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
import com.xforceplus.wapp.service.CommonMessageService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * 发送消息中心工具类
 */
@Service
@Slf4j
public class ExportCommonService {
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    private CommonMessageService commonMessageService;
    @Autowired
    private ExcelExportLogService excelExportLogService;

    private final String downLoadurl = "api/core/ftp/download";


    /**
     * 导出处理中
     * @param request
     * @return <日志id，用户id,用户名>
     */
    public Tuple3<Long,Long,String> insertRequest(Object request) {
        final Long userId = UserUtil.getUserId();

        TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
        excelExportlogEntity.setCreateDate(new Date());
        //这里的userAccount是userid
        excelExportlogEntity.setUserAccount(userId.toString());
        excelExportlogEntity.setUserName(UserUtil.getLoginName());
        excelExportlogEntity.setConditions(JSON.toJSONString(request));
        excelExportlogEntity.setStartDate(new Date());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
        excelExportlogEntity.setServiceType(SERVICE_TYPE);

        this.excelExportLogService.save(excelExportlogEntity);
        return Tuple.of(excelExportlogEntity.getId(),userId,UserUtil.getLoginName());
    }

    /**
     * 更新日志状态
     * @param logId
     * @param status
     * @param ftpPath
     */
   public   String updatelogStatus(Long logId, String status,String ftpPath){
        final TDxExcelExportlogEntity excelExportlogEntity = excelExportLogService.getById(logId);
        if (ftpPath !=null){
            excelExportlogEntity.setFilepath(ftpPath);
        }
        excelExportlogEntity.setExportStatus(status);
        excelExportlogEntity.setEndDate(new Date());
        excelExportLogService.updateById(excelExportlogEntity);
        return excelExportlogEntity.getUserName();
    }

    /**
     * 上传文件ftp服务器
     * @param ftpPath  目录
     * @param localFilePath 本地文件
     * @param fileName 文件名称
     * @return
     */
    public  String putFile(String ftpPath ,String localFilePath,String  fileName) {
        File localFile = null;
        try {
            localFile = new File(localFilePath);
            if (!localFile.getParentFile().exists()) {
                localFile.getParentFile().mkdirs();
            }

            FileInputStream inputStream = FileUtils.openInputStream(localFile);

            ftpUtilService.uploadFile(ftpPath, fileName, inputStream);

            FileUtils.deleteQuietly(localFile);
            return null ;
        } catch (Exception e) {
            log.error("上传文件到ftp发生异常", e);
            return "上传文件到ftp发生异常";
        } finally {
            if (localFile != null) {
                localFile.delete();
            }
        }
    }

    /**
     * 发送消息
     * @return
     */
    public  void sendMessage(Long logId,String userName , String title,String content){
        TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
        //这里的userAccount是userName
        messagecontrolEntity.setUserAccount(userName);
        messagecontrolEntity.setContent(content);
        messagecontrolEntity.setUrl(getUrl(logId));
        messagecontrolEntity.setTitle(title);
        commonMessageService.sendMessage(messagecontrolEntity);
    }

    /**
     * 发送消息
     * 指定Url
     * @return
     */
    public  void sendMessageWithUrl(Long logId,String userName , String title,String content,String url){
        TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
        //这里的userAccount是userName
        messagecontrolEntity.setUserAccount(userName);
        messagecontrolEntity.setContent(content);
        messagecontrolEntity.setUrl(url);
        messagecontrolEntity.setTitle(title);
        commonMessageService.sendMessage(messagecontrolEntity);
    }



    /**
     * 获取导出成功内容
     *
     * @return
     * @since 1.0
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

    /**
     * @param res
     * @param req
     * @param fileName 例如 红字信息表导入模板.xlsx
     */
    public void template(HttpServletResponse res, HttpServletRequest req, String fileName) {
        try {
            //获取
            res.setContentType("multipart/form-data");
            res.setCharacterEncoding("UTF-8");
            res.setContentType("text/html");
            String filePath = "/excl/" + fileName;
            String userAgent = req.getHeader("User-Agent");
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
                // 非IE浏览器的处理：
                fileName = new String((fileName).getBytes("UTF-8"), "ISO-8859-1");
            }
            res.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
            InputStream inputStream = this.getClass().getResourceAsStream(filePath);
            ServletOutputStream   out = res.getOutputStream();
            int b = 0;
            byte[] buffer = new byte[1024];
            while ((b = inputStream.read(buffer)) != -1) {
                // 4.写到输出流(out)中
                out.write(buffer, 0, b);
            }
            inputStream.close();

            if (out != null) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
