package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.utils.PdfUtils;
import com.xforceplus.wapp.modules.InformationInquiry.dao.RedNoticeInvoiceDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.RedNoticeBathEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.RedNoticeInvoiceImport;
import com.xforceplus.wapp.modules.InformationInquiry.service.RedNoticeInvoiceService;
import com.xforceplus.wapp.modules.einvoice.util.ZipUtil;
import com.xforceplus.wapp.modules.job.utils.FileZip;
import com.xforceplus.wapp.modules.redInvoiceManager.dao.UploadScarletLetterDao;
import com.xforceplus.wapp.modules.redTicket.dao.ExamineAndUploadRedNoticeDao;
import com.xforceplus.wapp.modules.redTicket.entity.FileEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.einvoice.constant.Constants.SUBSTR_REGEX_FOR_FILE;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.apache.log4j.Logger.getLogger;

/**
 * Created by 1 on 2018/11/21 9:25
 */
@Service
public class RedNoticeInvoiceServiceImpl implements RedNoticeInvoiceService {

    private static final Logger LOGGER = getLogger(RedNoticeInvoiceServiceImpl.class);

    @Autowired
    private RedNoticeInvoiceDao redNoticeInvoiceDao;
    @Autowired
    private ExamineAndUploadRedNoticeDao examineAndUploadRedNoticeDao;

    private RedTicketMatch selectMatchTableByRedTicketDataSerialNumber(String redTicketDataSerialNumber) {
        return examineAndUploadRedNoticeDao.selectMatchTableByRedTicketDataSerialNumber(redTicketDataSerialNumber);
    }
    @Autowired
    private UploadScarletLetterDao uploadScarletLetterDao;
    private int errorCount2 = 0;

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


    String tempDir = "redpdf";

    @Override
    @Transactional
    public Map<String, Object> parseExcel(MultipartFile multipartFile, String logingName, String redTicketType) {
        //进入解析excel方法
        final RedNoticeInvoiceImport redInvoiceImport = new RedNoticeInvoiceImport(multipartFile);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<RedNoticeBathEntity> redInvoiceList = redInvoiceImport.analysisExcel();
            if (!redInvoiceList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<RedNoticeBathEntity>> entityMap = RedInvoiceImportData(redInvoiceList, logingName, redTicketType);
                map.put("errorCount2", errorCount2);
                map.put("reason", entityMap.get("successEntityList"));
                map.put("errorEntityList1", entityMap.get("errorEntityList1"));
                map.put("errorEntityList2", entityMap.get("errorEntityList2"));
                map.put("errorCount1", entityMap.get("errorEntityList1").size());
                map.put("errorCount", entityMap.get("errorEntityList").size());
                map.put("errorEntityList3", entityMap.get("errorEntityList3"));
                map.put("errorEntityList4", entityMap.get("errorEntityList4"));
            } else {
                // LOGGER.info("读取到excel无数据");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            //LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }

    @Override
    public List<RedNoticeBathEntity> queryList(Map<String, Object> map) {
        List<RedNoticeBathEntity> list= redNoticeInvoiceDao.queryList(map);
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                String str = "";
                String str2 = "";
                str = list.get(i).getRedNoticeNumber();
                str2 = list.get(j).getRedNoticeNumber();
                if (str.equals(str2)) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

    @Override
    public int queryTotalResult(Map<String, Object> map) {


        return redNoticeInvoiceDao.queryTotalResult(map);
    }

    @Override
    public String createZip(List<RedNoticeBathEntity> list) {
        deleteDir(tempPath + tempDir);
        for (RedNoticeBathEntity entity : list) {
            createPDF(entity);
        }
        String zipName = "redPdf" + new Date().getTime() + ".zip";
        File file = new File(tempPath + tempDir);
        File[] files = file.listFiles();
        FileZip.zipFiles(files, new File(tempPath + tempDir + File.separator + zipName));
        return tempPath + tempDir + File.separator + zipName;
    }

    @Override
    public void downloadPDF(String path, HttpServletResponse response) {
        try {
            OutputStream output = response.getOutputStream();
            File file = new File(path);
            FileInputStream in = new FileInputStream(file);
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                output.write(buf, 0, len);
            }
            output.flush();
            in.close();
            output.close();
            //删除文件
            File temp = new File(tempPath + tempDir);
            for (File f : temp.listFiles()) {
                f.delete();
            }
            temp.delete();
        } catch (Exception e) {
            LOGGER.error("下载文件异常:" + e);
        }
    }
    public static boolean deleteDir(String path){
        File file = new File(path);
        if(!file.exists()){//判断是否待删除目录是否存在
            System.err.println("The dir are not exists!");
            return false;
        }

        String[] content = file.list();//取得当前目录下所有文件和文件夹
        for(String name : content){
            File temp = new File(path, name);
            if(temp.isDirectory()){//判断是否是目录
                deleteDir(temp.getAbsolutePath());//递归调用，删除目录里的内容
                temp.delete();//删除空目录
            }else{
                if(!temp.delete()){//直接删除文件
                    System.err.println("Failed to delete " + name);
                }
            }
        }
        return true;
    }


        private void createPDF(RedNoticeBathEntity entity) {
        try {
            Map<Object, Object> o = newHashMap();
            o.put("tkDate", entity.getTkDate());
            o.put("xfName", entity.getXfName());
            o.put("xfTaxno", entity.getXfTaxno());
            o.put("gfName", entity.getGfName());
            o.put("gfTaxno", entity.getGfTaxno());
            o.put("amount", entity.getAmount());
            o.put("taxRate", entity.getTaxRate());
            o.put("taxAmount", entity.getTaxAmount());
            o.put("redNoticeNumber", entity.getRedNoticeNumber());

            //String fileName = entity.getXfTaxno()+'_'+new Date().getTime()+".pdf";
            String fileName = entity.getRedTicketDataSerialNumber()+'_'+entity.getRedNoticeNumber()+".pdf";
            File file = new File(tempPath + tempDir);
            if (!file.exists()) {
                file.mkdir();
            }
            PdfUtils.generateToFile("redTicketNotice.ftl", null, o, tempPath + tempDir + File.separator + fileName);

        } catch (Exception e) {
            LOGGER.error("生成PDF异常:" + e);
        }
    }

    private Map<String, List<RedNoticeBathEntity>> RedInvoiceImportData(List<RedNoticeBathEntity> redInvoiceList, String loginName, String redTicketType) {
        //返回值
        final Map<String, List<RedNoticeBathEntity>> map = newHashMap();
        //导入成功的数据集
        final List<RedNoticeBathEntity> successEntityList = newArrayList();
        //导入失败的数据集
        final List<RedNoticeBathEntity> errorEntityList = newArrayList();
        //储存和库里有相同的红字通知单号
        final List<RedNoticeBathEntity> errorEntityList1 = newArrayList();
        //Excel重复的集合
        final List<RedNoticeBathEntity> errorEntityList2 = newArrayList();
        final List<RedNoticeBathEntity> errorEntityList3 = newArrayList();
        //未审核或审核不同意
        final List<RedNoticeBathEntity> errorEntityList4 = newArrayList();
        deleteDir(tempPath + tempDir);

        redInvoiceList.forEach(redInvoiceData -> {
            String redTicketDataSerialNumber = redInvoiceData.getRedTicketDataSerialNumber();
            String xfName = redInvoiceData.getXfName();
            String xfTaxno = redInvoiceData.getXfTaxno();
            List<RedNoticeBathEntity> uuid=redNoticeInvoiceDao.getGfuuid(redTicketDataSerialNumber);
            if(uuid.size()>0){
                String name=redNoticeInvoiceDao.gfDxName(uuid.get(0).getUuid());
                String tax=redNoticeInvoiceDao.gfDxTaxno(uuid.get(0).getUuid());
                redInvoiceData.setGfName(name);
                redInvoiceData.setGfTaxno(tax);
            }

          /*  String gfName = redNoticeInvoiceDao.gfNames(redTicketDataSerialNumber);
            redInvoiceData.setGfName(gfName);
            String gfTaxno = redNoticeInvoiceDao.gfTaxnos(redTicketDataSerialNumber);
            redInvoiceData.setGfTaxno(gfTaxno);*/
            String amount = redInvoiceData.getAmount();
            String taxRate = redInvoiceData.getTaxRate();
            String taxAmount = redInvoiceData.getTaxAmount();
            String tkDate = redInvoiceData.getTkDate();
            String redNoticeNumber = redInvoiceData.getRedNoticeNumber();
            //验证是否审核已经同意
            RedTicketMatch rm= redNoticeInvoiceDao.selectRedNoticeNumbers(redTicketDataSerialNumber);

            if (!redTicketDataSerialNumber.isEmpty() && !xfName.isEmpty() && !xfTaxno.isEmpty() && !taxAmount.isEmpty() && !amount.isEmpty() && !taxRate.isEmpty()
                    && !tkDate.isEmpty() && !redNoticeNumber.isEmpty()) {
                //外部红票
                if (redTicketType.equals("1")) {
                    //检验匹配表里面是否已经有红字通知单
                    RedTicketMatch redNoticeNumbers = redNoticeInvoiceDao.selectRedNoticeNumbers(redTicketDataSerialNumber);
                    if (redNoticeNumbers != null) {
                        String number=redNoticeNumbers.getRedNoticeNumber();
                        if (number!=null&&!number.equals("")) {
                                errorEntityList1.add(redInvoiceData);
                        } else if(!rm.getExamineResult().equals("2")){
                            errorEntityList4.add(redInvoiceData);
                        }
                        else {
                            successEntityList.add(redInvoiceData);
                        }

                    } else {
                        errorEntityList3.add(redInvoiceData);

                    }
                } else if (redTicketType.equals("0")) {
                    //内部红票
                    //1判断红字通知单有没有
                    int i = redNoticeInvoiceDao.selectRedNotice(redNoticeNumber);
                    if (i > 0) {
                        errorEntityList1.add(redInvoiceData);
                    } else if(!rm.getExamineResult().equals("2")){
                        errorEntityList4.add(redInvoiceData);
                    }
                    else {
                        successEntityList.add(redInvoiceData);
                    }

                }
            } else {
                errorEntityList.add(redInvoiceData);
            }
        });
        //去重

        if (redTicketType.equals("0")) {
            for (int i = 0; i < successEntityList.size() - 1; i++) {
                for (int j = successEntityList.size() - 1; j > i; j--) {
                    String str = "";
                    String str2 = "";
                    str = successEntityList.get(i).getRedNoticeNumber();
                    str2 = successEntityList.get(j).getRedNoticeNumber();
                    if (str.equals(str2)) {
                        errorCount2 = errorCount2 + 1;
                        errorEntityList2.add(successEntityList.get(j));
                        successEntityList.remove(j);
                    }
                }
            }
        }

        if (redTicketType.equals("1")) {
            for (int i = 0; i < successEntityList.size() - 1; i++) {
                for (int j = successEntityList.size() - 1; j > i; j--) {
                    String str = "";
                    String str2 = "";
                    String str3 = "";
                    String str4 = "";
                    str = successEntityList.get(j).getRedTicketDataSerialNumber();
                    str2 = successEntityList.get(i).getRedTicketDataSerialNumber();
                    str3 = successEntityList.get(i).getRedNoticeNumber();
                    str4 = successEntityList.get(j).getRedNoticeNumber();
                    if (str.equals(str2) || str3.equals(str4)) {
                        errorCount2 = errorCount2 + 1;
                        errorEntityList2.add(successEntityList.get(j));
                        successEntityList.remove(j);
                    }
                }
            }
        }

        if (errorEntityList.size() == 0) {
            //如果都校验通过，保存入库
            for (RedNoticeBathEntity red : successEntityList) {
                red.setUpdatePersion(loginName);
                //实体添加红票类型 0 内部 1 外部
                red.setRedTicketType(redTicketType);
                //1.将信息插入红字通知单信息表
                redNoticeInvoiceDao.saveRedNoticeInvoiceData(red);


                //生成PDF文件
                createPDFs(red);
                //外部红票
                if (redTicketType.equals("1")) {
                    RedTicketMatch redTicketMatch = examineAndUploadRedNoticeDao.selectMatchTableByRedTicketDataSerialNumber(red.getRedTicketDataSerialNumber());
                    if (redTicketMatch != null) {
                        //修改匹配标的信息
                       // examineAndUploadRedNoticeDao.updateStatus(new Integer(redTicketMatch.getId().intValue()), red.getRedNoticeNumber(), null);
                        //改变退货状态
                      /*  if (redTicketMatch.getBusinessType().equals("1")) {
                            examineAndUploadRedNoticeDao.updateRuturnStatus(red.getRedTicketDataSerialNumber());
                        }*/
                        //改变协议状态
                       /* if (redTicketMatch.getBusinessType().equals("2")) {
                            examineAndUploadRedNoticeDao.updateAgreementStatus(red.getRedTicketDataSerialNumber());
                        }*/
                    } else {
                        errorEntityList1.add(red);
                    }

                }
                //内部红票
                if (redTicketType.equals("0")) {
                    uploadScarletLetterDao.saveRedDetail(red.getRedNoticeNumber(), red.getRedTicketDataSerialNumber());
                    uploadScarletLetterDao.updateStatus(red.getRedTicketDataSerialNumber());
                }

            }
        }

        map.put("successEntityList", successEntityList);
        map.put("errorEntityList1", errorEntityList1);
        map.put("errorEntityList2", errorEntityList2);
        map.put("errorEntityList", errorEntityList);
        map.put("errorEntityList3", errorEntityList3);
        map.put("errorEntityList4", errorEntityList4);
        return map;
    }




    private void createPDFs(RedNoticeBathEntity entity) {
        try {
            Map<Object, Object> o = newHashMap();
            o.put("tkDate", entity.getTkDate());
            o.put("xfName", entity.getXfName());
            o.put("xfTaxno", entity.getXfTaxno());
            o.put("gfName", entity.getGfName());
            o.put("gfTaxno", entity.getGfTaxno());
            o.put("amount", entity.getAmount());
            o.put("taxRate", entity.getTaxRate());
            o.put("taxAmount", entity.getTaxAmount());
            o.put("redNoticeNumber", entity.getRedNoticeNumber());

            //String fileName = entity.getXfTaxno()+'_'+new Date().getTime()+".pdf";
            String fileName = entity.getRedTicketDataSerialNumber() + '_' + entity.getRedNoticeNumber() + ".pdf";
            File file = new File(tempPath + tempDir);
            if (!file.exists()) {
                file.mkdir();
            }
            PdfUtils.generateToFile("redTicketNotice.ftl", null, o, tempPath + tempDir + File.separator + fileName);



            LOGGER.debug("----------------红字通知单批量上传开始--------------------");

            File files = new File(tempPath + tempDir + File.separator + fileName);
            String[] split = fileName.split("_");
            //通过序列号查询匹配表，并插入红字通知单号
            RedTicketMatch redTicketMatch = this.selectMatchTableByRedTicketDataSerialNumber(entity.getRedTicketDataSerialNumber());
            if (redTicketMatch != null) {

                if (redTicketMatch.getWhetherOpenRedticket().equals("1")) {
                    LOGGER.info("红通通知单已开红票！");
                }
                if ("".equals(redTicketMatch.getRedNoticeNumber()) || redTicketMatch.getRedNoticeNumber() == null) {
                    //获取文件类型
                    String fileType = fileName.substring(fileName.indexOf(".") + 1);
                    //获取红字通知单号
                    String redNoticeNumber = split[1].substring(0, split[1].lastIndexOf('.'));
                    String reg = "[0-9]{16}";
                    if (!redNoticeNumber.matches(reg)) {
                    }

                    int res = examineAndUploadRedNoticeDao.getRedMatchByNo(redNoticeNumber);
                    if (res > 0) {
                        LOGGER.info("红字通知单已存在！");
                    } else {

                        //上传文件重新命名
                        com.xforceplus.wapp.common.utils.SFTPHandler handler = com.xforceplus.wapp.common.utils.SFTPHandler.getHandler(depositPath);
                        try {
                            if (null != fileName) {
                                String path = depositPath+fileName;
                                handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                                handler.uploadTzd(files,fileName,depositPath);
                                FileEntity fileEntity = new FileEntity();
                                fileEntity.setFilePath(path);
                                fileEntity.setFileName(fileName);
                                fileEntity.setFileType(fileType);
                                this.saveFilePath(fileEntity);
                                this.updateSatus(new Integer(redTicketMatch.getId().intValue()), redNoticeNumber, fileEntity.getId().intValue());

                                //改变退货状态
                                if (redTicketMatch.getBusinessType().equals("1")) {
                                    examineAndUploadRedNoticeDao.updateRuturnStatus(redTicketMatch.getRedTicketDataSerialNumber());
                                }
                                //改变协议状态
                       /* if (redTicketMatch.getBusinessType().equals("2")) {
                            examineAndUploadRedNoticeDao.updateAgreementStatus(redTicketMatch.getRedTicketDataSerialNumber());
                        }*/


                            }
                        } catch (Exception e) {
                            LOGGER.debug("----上传文件异常---");
                            e.printStackTrace();
                        } finally {
                            if (handler != null) {
                                handler.closeChannel();
                            }
                        }
                    }
                } else {
                }

            } else {
                LOGGER.debug("----------------序列号有误！--------------------");
            }
            LOGGER.debug("----------------红字通知单批量上传完成--------------------");


        } catch (Exception e) {
            LOGGER.error("生成PDF异常:" + e);
        }
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
    private int saveFilePath(FileEntity fileEntity) {
        return examineAndUploadRedNoticeDao.saveFilePathRed(fileEntity);
    }

    /**
     * 修改红字通知单的状态
     * @param redNoticeAssociation
     * @param redNoticeNumber
     * @param id
     */
    private void updateSatus(Integer id, String redNoticeNumber, Integer redNoticeAssociation) {
        examineAndUploadRedNoticeDao.updateStatus(id, redNoticeNumber, redNoticeAssociation);
    }
}
