package com.xforceplus.wapp.modules.redTicket.service.impl;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.einvoice.util.*;
import com.xforceplus.wapp.modules.redTicket.dao.QueryOpenRedTicketDataDao;
import com.xforceplus.wapp.modules.redTicket.entity.*;
import com.xforceplus.wapp.modules.redTicket.service.QueryOpenRedTicketDataService;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.*;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class QueryOpenRedTicketDataServiceImpl implements QueryOpenRedTicketDataService {
    private final static Logger LOGGER = getLogger(QueryOpenRedTicketDataServiceImpl.class);
    /**
     * 上传时本地文件暂存路径
     */
    @Value("${filePathConstan.tempPath}")
    private String tempPath;
    /**
     * 上传时本地文件存放路径
     */
    @Value("${filePathConstan.remoteCostFileRootPath}")
    private String depositPath;

    /**
     * 远程文件存放路径
     */
    @Value("${filePathConstan.remoteImageRootPath}")
    private String remoteImageRootPath;

    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.localImageRootPath}")
    private String localImageRootPath;

    //sftp IP底账
    @Value("${pro.sftp.host}")
    private String host;
    //sftp 用户名
    @Value("${pro.sftp.username}")
    private String userName;
    //sftp 密码
    @Value("${pro.sftp.password}")
    private String password;
    //sftp 默认端口号
    @Value("${pro.sftp.default.port}")
    private String defaultPort;
    //sftp 默认超时时间
    @Value("${pro.sftp.default.timeout}")
    private String defaultTimeout;



    private final QueryOpenRedTicketDataDao queryOpenRedTicketDataDao;

    @Autowired
    public QueryOpenRedTicketDataServiceImpl(QueryOpenRedTicketDataDao queryOpenRedTicketDataDao) {
        this.queryOpenRedTicketDataDao = queryOpenRedTicketDataDao;
    }

    @Override
    public List<RedTicketMatch> queryOpenRedTicket(Map<String, Object> map) {
        List<RedTicketMatch> redTicketMatches=queryOpenRedTicketDataDao.getRedTicketMatchList(map);
        for (int i = 0; i < redTicketMatches.size(); i++){
            if(redTicketMatches.get(i).getBusinessType().equals("2")){
                BigDecimal taxRate =(redTicketMatches.get(i).getTaxRate()).divide(new BigDecimal(100));
                taxRate = taxRate.add(new BigDecimal(1));
                redTicketMatches.get(i).setRedTotalAmount(redTicketMatches.get(i).getRedTotalAmount().multiply(taxRate));
            }
        }
        return redTicketMatches;
    }

    @Override
    public Integer getRedTicketMatchListCount(Map<String, Object> map) {
        return queryOpenRedTicketDataDao.getRedTicketMatchListCount(map);
    }

    @Override
    public List<ReturnGoodsEntity> getReturnGoodsList(Map<String, Object> params) {
        return queryOpenRedTicketDataDao.getReturnGoodsList(params);
    }

    @Override
    public Integer getReturnGoodsListCount(Map<String, Object> params) {
        return queryOpenRedTicketDataDao.getReturnGoodsListCount(params);
    }

    @Override
    public List<InvoiceEntity> getRecordInvoiceList(Map<String, Object> params) {
        return queryOpenRedTicketDataDao.getRecordInvoiceList(params);
    }

    @Override
    public Integer getRecordInvoiceListCount(Map<String, Object> params) {
        return queryOpenRedTicketDataDao.getRecordInvoiceListCount(params);
    }

    @Override
    public List<InvoiceDetail> getRecordInvoiceDetailList(Map<String, Object> params) {
        return queryOpenRedTicketDataDao.getRecordInvoiceDetailList(params);
    }

    @Override
    public Integer getRecordInvoiceDetailListCount(Map<String, Object> params) {
        return queryOpenRedTicketDataDao.getRecordInvoiceDetailListCount(params);
    }

    @Override
    public List<RedTicketMatchDetail> getMergeInvoiceDetailList(Map<String, Object> params) {
        return queryOpenRedTicketDataDao.getMergeInvoiceDetailList(params);
    }

    @Override
    public Integer getMergeInvoiceDetailListCount(Map<String, Object> params) {
        return queryOpenRedTicketDataDao.getMergeInvoiceDetailListCount(params);
    }

    @Override
    public Integer getAgreementListCount(Query query) {
        return queryOpenRedTicketDataDao.getAgreementListCount(query);
    }

    @Override
    public List<AgreementEntity> getAgreementList(Query query) {
        return queryOpenRedTicketDataDao.getAgreementList(query);
    }

    @Override
    public List<FileEntity> getQueryImg(Map<String, Object> params) {
        List<FileEntity> queryImgs = queryOpenRedTicketDataDao.getQueryImg(params);
        for(int i = 0 ; i < queryImgs.size() ; i++){
            String fileName = queryImgs.get(i).getFileName()+'.'+queryImgs.get(i).getFileType();
            queryImgs.get(i).setFileName(fileName);
        }

        return queryImgs;
    }



    @Override
    public String uploadRedTicketData(MultipartFile file, UserEntity user,String fileNumber,Integer id ) {
        String msg="";
        LOGGER.debug("----------------开红票资料上传开始--------------------");
        //文件名称
        String fileName = this.getOriginalFilename(file.getOriginalFilename());
        //获取文件类型
        String fileType = fileName.substring(fileName.indexOf(".")+1);
        //上传文件重新命名
        String pdfFileName = DateTimeHelper.formatNowDate(FILE_DATE_FORMAT) + fileName.substring(fileName.indexOf("."));
        com.xforceplus.wapp.common.utils.SFTPHandler handler = com.xforceplus.wapp.common.utils.SFTPHandler.getHandler(depositPath);
        try {
            if (null != fileName) {
                handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));

               String path = handler.uploadRed(file);
                String str= path.substring(path.lastIndexOf("/") + 1);
                String fileNa=str.substring(0,str.indexOf("."));
                this.saveFilePath(path, fileType,id.toString(),fileNa);
                this.updateSatus(id);
                this.updateExamineStatus(id);
                msg = "文件上传成功！";
            }
        } catch (Exception e) {
            LOGGER.debug("----上传文件异常---" + e);
            msg = "上传文件异常";
        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }
        LOGGER.debug("----------------开红票资料上传完成--------------------");
        //return invoices;

        return msg;
    }
    private void updateSatus(Integer id) {
        queryOpenRedTicketDataDao.updateStatus(id);
    }
    private void updateExamineStatus(Integer id) {
        queryOpenRedTicketDataDao.updateExamineStatus(id);
    }
    private void saveFilePath(String s, String fileType,String fileNumber,String fileName) {
        queryOpenRedTicketDataDao.saveFilePath(s,fileType,fileNumber,fileName);

    }

    public void inputstreamtofile(InputStream ins,File file){

        OutputStream os = null;
        try {
            os = new FileOutputStream(file);

        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void getInvoiceImageForAll(Long id, UserEntity user, HttpServletResponse response) {
        //获取图片实体
          FileEntity fileEntity = this.getFileImage(id);
        //SFTPHandler imageHandler = SFTPHandler.getHandler(remoteImageRootPath, localImageRootPath);
        com.xforceplus.wapp.common.utils.SFTPHandler imageHandler = com.xforceplus.wapp.common.utils.SFTPHandler.getHandler(depositPath,tempPath);
        //SFTPHandler imageHandler = SFTPHandler.getHandler(depositPath);
        try {
            if (null != fileEntity) {
                imageHandler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                String userAccount = user.getLoginname();
                //response.setContentType("image/png");
               // String name = invoiceEntity.getScanId();
                String name =  fileEntity.getFileName();
                response.reset();
                //response.setHeader("image/png", "attachment; filename=" + java.net.URLEncoder.encode(name, "UTF-8"));
                if(fileEntity.getFileType().equals("png")){
                    //response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    response.setHeader("Content-Type","image/png");
                }
                if(fileEntity.getFileType().equals("pdf")){
                    //response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    response.setHeader("Content-Type","application/adobe-pdf");
                }
                if(fileEntity.getFileType().equals("jpeg")){
                    //response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    response.setHeader("Content-Type","image/jpeg");
                }
                if(fileEntity.getFileType().equals("jpg")){
                    //response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    response.setHeader("Content-Type","image/jpg");
                }
                if(fileEntity.getFileType().equals("gif")){
                    //response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    response.setHeader("Content-Type","image/gif");
                }
                OutputStream output = response.getOutputStream();
                imageHandler.download(depositPath+name+'.'+fileEntity.getFileType(),  name+'.'+fileEntity.getFileType());
                   File file = new File(tempPath+name+'.'+fileEntity.getFileType());
                    FileInputStream in =new FileInputStream(file);
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = in.read(buf)) != -1) {
                        output.write(buf, 0, len);
                    }
                    output.flush();
                    in.close();
                    output.close();
            }
        } catch (Exception e) {
            LOGGER.debug("----获取图片异常---" + e);
        } finally {
            if (imageHandler != null) {
                imageHandler.closeChannel();
            }
        }
    }

    @Override
    public void getDownLoadFile(Long id, UserEntity user, HttpServletResponse response) {
        //获取图片实体
        FileEntity fileEntity = this.getFileImage(id);
        //SFTPHandler imageHandler = SFTPHandler.getHandler(remoteImageRootPath, localImageRootPath);
        com.xforceplus.wapp.common.utils.SFTPHandler imageHandler = com.xforceplus.wapp.common.utils.SFTPHandler.getHandler(depositPath,tempPath);
        try {
            if (null != fileEntity) {
                imageHandler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                String userAccount = user.getLoginname();
                String name =  fileEntity.getFilePath().substring(fileEntity.getFilePath().lastIndexOf('/') + 1);
                response.reset();
                response.addHeader("Content-Disposition", "attachment;filename=" + name);
                //response.setHeader("application/pdf", "attachment; filename=" + java.net.URLEncoder.encode(name, "UTF-8"));
                OutputStream output = response.getOutputStream();
                imageHandler.download(depositPath+name,  name);
                    File file = new File(tempPath+name);
                    FileInputStream in = new FileInputStream(file);// 获取实体类对应Byte
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = in.read(buf)) != -1) {
                        output.write(buf, 0, len);
                    }
                    output.flush();
                    in.close();
                    output.close();


            }
        } catch (Exception e) {
            LOGGER.debug("----获取压缩文件异常---" + e);
        } finally {
            if (imageHandler != null) {
                imageHandler.closeChannel();
            }
        }
    }

    @Override
    public void getInvoiceImageForNotice(Long redNoticeAssociation, UserEntity user, HttpServletResponse response) {

        //获取图片实体
        FileEntity fileEntity = this.getFileImage(redNoticeAssociation);
        //SFTPHandler imageHandler = SFTPHandler.getHandler(remoteImageRootPath, localImageRootPath);
        com.xforceplus.wapp.common.utils.SFTPHandler imageHandler = com.xforceplus.wapp.common.utils.SFTPHandler.getHandler(depositPath,tempPath);
        try {
            if (null != fileEntity) {
                imageHandler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                String name =  fileEntity.getFileName();
                response.reset();

                if(fileEntity.getFileType().equals("pdf")){
                    //response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    response.setHeader("Content-Type","application/pdf");
                }
                OutputStream output = response.getOutputStream();
                imageHandler.download(fileEntity.getFilePath(),  name);

                if(fileEntity.getFileType().equals("png")||fileEntity.getFileType().equals("pdf")){
                        File file = new File(tempPath+name);

                        FileInputStream in =new FileInputStream(file);
                        int len;
                        byte[] buf = new byte[1024];
                        while ((len = in.read(buf)) != -1) {
                            output.write(buf, 0, len);
                        }

                    output.flush();
                    in.close();
                    output.close();
                }else if (fileEntity.getFileType().equals("zip")||fileEntity.getFileType().equals("rar")){
                    byte[] zipFile = ZipUtilRead.readZipFile(imageHandler.getLocalImageRootPath() + name);
                    ByteArrayInputStream in = new ByteArrayInputStream(zipFile);// 获取实体类对应Byte
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = in.read(buf)) != -1) {
                        output.write(buf, 0, len);
                    }
                    output.flush();
                    in.close();
                    output.close();
                }
                /*output.flush();
                in.close();
                output.close();*/
            }
        } catch (Exception e) {
            LOGGER.debug("----获取红字通知单图片异常---" + e);
        } finally {
            if (imageHandler != null) {
                imageHandler.closeChannel();
            }
        }



    }

    @Override
    public List<OptionEntity> queryXL() {
        return queryOpenRedTicketDataDao.queryXL();
    }

    @Override
    public List<OptionEntity> queryRedTicketType() {
        return queryOpenRedTicketDataDao.queryRedTicketType();
    }

    @Override
    public List<OpenRedExcelEntity> toExcel(List<RedTicketMatch> list){
        List<OpenRedExcelEntity> list2=new ArrayList<>();
        for (RedTicketMatch rm:list){
            OpenRedExcelEntity oe=new OpenRedExcelEntity();
            //发票金额
            String inAmount="";
            String taxAmount="";
            String taxRate="";
            String totalAmount="";
            if(rm.getInvoiceAmount()!=null){
                inAmount=rm.getInvoiceAmount().stripTrailingZeros().toPlainString()+".00";
            }
            if(rm.getTaxAmount()!=null){
                taxAmount= rm.getTaxAmount().stripTrailingZeros().toPlainString()+".00";
            }
            if(rm.getTaxRate()!=null){
                taxRate=rm.getTaxRate().stripTrailingZeros().toPlainString()+".00%";
            }
            if(rm.getTotalAmount()!=null){
                totalAmount=rm.getTotalAmount().stripTrailingZeros().toPlainString()+".00";
            }
            oe.setRownumber(rm.getRownumber());
            oe.setVenderId(rm.getVenderid());
            oe.setRedTicketDataSerialNumber(rm.getRedTicketDataSerialNumber());
            oe.setBusinessType(formatBusinessType(rm.getBusinessType()));
            oe.setRedTotalAmount(rm.getRedTotalAmount().toString());
            oe.setRedNoticeNumber(rm.getRedNoticeNumber());
            oe.setDataStatus(formatDataStatus(rm.getDataStatus()));
            oe.setNoticeStatus(formatDataStatus(rm.getNoticeStatus()));
            oe.setExamineResult(formatExamineResult(rm.getExamineResult()));
            oe.setExamineRemarks(rm.getExamineRemarks());
            oe.setInvoiceAmount(inAmount);
            oe.setInvoiceCode(rm.getInvoiceCode());
            oe.setInvoiceNo(rm.getInvoiceNo());
            oe.setTaxAmount(taxAmount);
            oe.setTaxReta(taxRate);
            oe.setInvoiceTotal(totalAmount);
            oe.setScanMatchStatus(formatScanMatchStatus(rm.getScanMatchStatus()));
            list2.add(oe);
        }
        return list2;
    }
    /**
     * 通过id 找文件实体
     * @param id
     * @return
     */
    private FileEntity getFileImage(Long id) {
         return  queryOpenRedTicketDataDao.getFielImage(id);
    }

    /**
     * 获取上传文件的名称
     *
     * @param filename 文件的原始名称，有可能包含路径
     * @return
     */
    private String getOriginalFilename(String filename) {

        int unixSep = filename.lastIndexOf("/");
        int winSep = filename.lastIndexOf(SUBSTR_REGEX_FOR_FILE);
        int pos = winSep > unixSep ? winSep : unixSep;
        return pos != -1 ? filename.substring(pos + 1) : filename;

    }
    /**
     * 创建文件夹
     */
    private void createFileDir() {
        //创建临时存储文件夹
        File tempFileDir = new File(tempPath);
        if (!tempFileDir.exists()) {
            tempFileDir.mkdirs();
        }
        //创建压缩包存储文件夹
        File zipFileDir = new File(depositPath);
        if (!zipFileDir.exists()) {
            zipFileDir.mkdirs();
        }
    }
   public String selectBusinessType(String redTicketDataSerialNumber){
       return queryOpenRedTicketDataDao.selectBusinessType(redTicketDataSerialNumber);
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DEFAULT_SHORT_DATE_FORMAT);
    }

    private String formatBusinessType(String authStatus) {
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "索赔类型";
        } else  if("2".equals(authStatus)) {
            authStatusName = "协议类型";
        }
        else  if("3".equals(authStatus)) {
            authStatusName = "折让类型";
        }
        return authStatusName;
    }

    private String formatScanMatchStatus(String authStatus) {
        String authStatusName = "";
        if("0".equals(authStatus)) {
            authStatusName = "未扫描匹配";
        } else  if("1".equals(authStatus)) {
            authStatusName = "扫描匹配成功";
        }
        else  if("2".equals(authStatus)) {
            authStatusName = "扫描匹配失败";
        }
        return authStatusName;
    }
    private String formatDataStatus(String authStatus) {
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "是";
        } else  if("2".equals(authStatus)) {
            authStatusName = "否";
        }
        return authStatusName;
    }
    private String formatExamineResult(String authStatus) {
        //1-未审核 2-同意 3-不同意
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "未审核";
        } else  if("2".equals(authStatus)) {
            authStatusName = "同意";
        }else  if("3".equals(authStatus)) {
            authStatusName = "不同意";
        }
        return authStatusName;
    }
}
