package com.xforceplus.wapp.modules.redTicket.service.impl;

import com.xforceplus.wapp.common.utils.MailUtils;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.posuopei.entity.ExamineExcelEntity;
import com.xforceplus.wapp.modules.redTicket.dao.ExamineAndUploadRedNoticeDao;
import com.xforceplus.wapp.modules.redTicket.dao.QueryOpenRedTicketDataDao;
import com.xforceplus.wapp.modules.redTicket.entity.FileEntity;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatchDetail;
import com.xforceplus.wapp.modules.redTicket.export.RedTicketMatchExamineQueryExcel;
import com.xforceplus.wapp.modules.redTicket.service.ExamineAndUploadRedNoticeService;
import com.xforceplus.wapp.modules.redTicket.service.QueryOpenRedTicketDataService;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.SUBSTR_REGEX_FOR_FILE;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by 1 on 2018/10/22 13:56
 */
@Service
public class ExamineAndUploadRedNoticeServiceImpl  implements ExamineAndUploadRedNoticeService {

    private final static Logger LOGGER = getLogger(QueryOpenRedTicketDataServiceImpl.class);
    @Autowired
    QueryOpenRedTicketDataService queryOpenRedTicketDataService;
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
    //路径

    private String dirpath ="/home/vn088jh/jxfp/emailFile/";

    private  ExamineAndUploadRedNoticeDao examineAndUploadRedNoticeDao;
    @Autowired
    private QueryOpenRedTicketDataDao queryOpenRedTicketDataDao;


    @Autowired
    public ExamineAndUploadRedNoticeServiceImpl(ExamineAndUploadRedNoticeDao examineAndUploadRedNoticeDao) {
        this.examineAndUploadRedNoticeDao = examineAndUploadRedNoticeDao;
    }

    @Override
    public List<RedTicketMatch> queryOpenRedTicket(Map<String, Object> map) {
        List<RedTicketMatch> redTicketMatches = examineAndUploadRedNoticeDao.queryOpenRedTicket(map);
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
    public Integer getRedTicketMatchListCount(Map<String, Object> params) {
        return examineAndUploadRedNoticeDao.getRedTicketMatchListCount(params);
    }

    @Override
    @Transactional
    public String saveExamineRemarks(Map<String, Object> map) {
        Boolean flag=false;
        try {
            JSONArray arr = JSONArray.fromObject( map.get("ids"));
            for (int i = 0; i < arr.size(); i++) {
                long id = Long.valueOf(String.valueOf(arr.get(i))).longValue();
                //examineAndUploadRedNoticeService.updateMatchStatus(id);
                RedTicketMatch redTicketMatch=  examineAndUploadRedNoticeDao.getRedTicketMatch(id);
                map.put("businessType", redTicketMatch.getBusinessType());
                map.put("redTicketDataSerialNumber", redTicketMatch.getRedTicketDataSerialNumber());
                map.put("id", redTicketMatch.getId());
                //红票审核状态修改
                examineAndUploadRedNoticeDao.saveExamineRemarks(map);
               /* //取消退货状态
                if (map.get("businessType").equals("1")){
                    examineAndUploadRedNoticeDao.cancelReturnGoodsStatus(map);
                }
                //取消协议状态
                if (map.get("businessType").equals("2")){
                    examineAndUploadRedNoticeDao.cancelAgreementStatus(map);
                }
                //红票审核状态修改
                examineAndUploadRedNoticeDao.saveExamineRemarks(map);
                //清空发票明细红冲数据
                examineAndUploadRedNoticeDao.clearTicketInformationData(map);
                //发票中间表查询
                List<RedTicketMatchMiddle> redTicketMatchMiddles=examineAndUploadRedNoticeDao.queryRedTicketMatchMiddle(map);
                //发票可红冲金额回冲
                for (int j=0;j<redTicketMatchMiddles.size();j++){
                    examineAndUploadRedNoticeDao.invoiceRedRushAmountBackflush(redTicketMatchMiddles.get(j));
                }*/
                flag=true;
            }
        }catch(Exception e){
            LOGGER.info("取消失败 {}",e);
            throw new RuntimeException();
        }
        if(flag){
            return "审核不通过成功";
        }else{
            return "审核不通过失败";
        }

    }

    @Override @Transactional
    public String uploadRedTicketRedBatch(MultipartFile file) {
        LOGGER.debug("----------------红字通知单批量上传开始--------------------");

        //文件名称

        String fileName = this.getOriginalFilename(file.getOriginalFilename());
        String[] split = fileName.split("_");
        //通过序列号查询匹配表，并插入红字通知单号
        RedTicketMatch redTicketMatch =  this.selectMatchTableByRedTicketDataSerialNumber(split[0]);
        if(redTicketMatch!=null ){

            if(redTicketMatch.getWhetherOpenRedticket().equals("1")){
                LOGGER.info("红通通知单已开红票！");
                return "红通通知单已开红票！";
            }
            if("".equals(redTicketMatch.getRedNoticeNumber()) ||redTicketMatch.getRedNoticeNumber()==null ){
                //获取文件类型
                String fileType = fileName.substring(fileName.indexOf(".")+1);
                //获取红字通知单号
                String redNoticeNumber =split[1].substring(0,split[1].lastIndexOf('.'));
                String reg="[0-9]{16}";
                if(!redNoticeNumber.matches(reg)){
                    return "红字通知单的是16位数字格式！"+fileName;
                }

                int res = examineAndUploadRedNoticeDao.getRedMatchByNo(redNoticeNumber);
                if(res>0){
                    LOGGER.info("红字通知单已存在！");
                    return "红字通知单已存在！";
                }else {

                    //上传文件重新命名
                    com.xforceplus.wapp.common.utils.SFTPHandler handler = com.xforceplus.wapp.common.utils.SFTPHandler.getHandler(depositPath);
                    String path = "";
                    try {
                        if (null != fileName) {
                            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));

                            path = handler.upload(file);
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
                        LOGGER.debug("----上传文件异常---" + e);
                        return "上传失败，文件异常！";
                    } finally {
                        if (handler != null) {
                            handler.closeChannel();
                        }
                    }
                }
            }else {
                return "红字通知单号已经被关联！";
            }

        }else {
            LOGGER.debug("----------------序列号有误！--------------------");
            return "序列号有误！";
        }
        LOGGER.debug("----------------红字通知单批量上传完成--------------------");
        return "success";
    }

    @Override
    public void updateMatchStatus(long id) {
        examineAndUploadRedNoticeDao.updateMatchStatus(id);
    }

    @Override  @Transactional
    public Map<String, Object> sendMessageToTax(String ids) {
        String[] split = ids.split(",");
        List<RedTicketMatch> redTicketMatches = examineAndUploadRedNoticeDao.selectOpenRedTicketById(split);
        //生成Excel  输出流
        final Map<String, List<Object>> map = newHashMapWithExpectedSize(1);
        List<Object> mergeInvoiceDetailList = new ArrayList<>();
        for (RedTicketMatch entity : redTicketMatches) {
            Map<String, Object> map1 = new HashMap();
            map1.put("redTicketDataSerialNumber",entity.getRedTicketDataSerialNumber());
            map1.put("id",entity.getId());
            List<RedTicketMatchDetail> tempList = queryOpenRedTicketDataService.getMergeInvoiceDetailList(map1);
            if(tempList.size()>0){
                RedTicketMatchDetail redTicketMatchDetail = tempList.get(0);
                String businessType= queryOpenRedTicketDataService.selectBusinessType(redTicketMatchDetail.getRedTicketDataSerialNumber());
                redTicketMatchDetail.setBusinessType(businessType);
                List<InvoiceEntity> recordInvoiceList = queryOpenRedTicketDataService.getRecordInvoiceList(map1);
                String uuid = recordInvoiceList.get(0).getInvoiceCode() + recordInvoiceList.get(0).getInvoiceNo();
                InvoiceEntity invoiceEntity = examineAndUploadRedNoticeDao.getRedInfo(uuid);
                String name = redTicketMatchDetail.getGoodsName();
                String tax_sortcode = "";
                if(name.indexOf("*") == 0 && name.lastIndexOf("*") != -1 && name.lastIndexOf("*") > name.indexOf("*") +1){
                    tax_sortcode = examineAndUploadRedNoticeDao.selectTaxCode(name.substring(name.indexOf("*")+1,name.lastIndexOf("*")));
                }
                if(null==tax_sortcode){
                    tax_sortcode = "";
                }
                mergeInvoiceDetailList.add(redTicketMatchDetail);
                mergeInvoiceDetailList.add(invoiceEntity);
                mergeInvoiceDetailList.add(tax_sortcode);
            }
        }
        map.put("RedTicketInfoList",mergeInvoiceDetailList);
        //根据Excel模板生成 excel文件
        final RedTicketMatchExamineQueryExcel excelView = new RedTicketMatchExamineQueryExcel(map, "export/redTicket/RedTicketInfoList.xlsx", "RedTicketInfoList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        final String excelName = "extRedTickeList"+excelNameSuffix+".xlsx";
        excelView.writeBD(dirpath, excelName);
        return this.sendMessageAA(dirpath+excelName);
    }

    private Map<String, Object> sendMessageAA(String path) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Map<String, Object> map = newHashMap();
        File file=new File(path);
        String toEmail=examineAndUploadRedNoticeDao.getCopyPerson("RECIPIENTS");
        String titel="外部供应商办理红票信息表实物清单-退货"+formatter.format(new Date());
        String message="尊敬的税务组你好：供应商申请的红票资料已审核通过,请登陆〈Q:\\AP-Superctacct\\AP\\外部红票清单〉路径查看，并及时开具红字通知单。GBS- AP GFR Payment联系电话： 0755-21511395邮箱地址 ：CNGFRS <CNGFRS@email.wal-mart.com>";
        try{
            MailUtils.sendEmailWithAttachment("Aileen.Li@walmart.com","","",titel,message,file);
            map.put("success", "yes");
            file.delete();
        }catch (Exception e){
            map.put("success", "no");
            map.put("reason", "发送失败！");
            e.printStackTrace();
        }
        return map;

    }

    private String getEmailPassword() {
        String copyPerson = examineAndUploadRedNoticeDao.getCopyPerson("EMAIL_PASSWORD");
        return copyPerson;
    }

    private String getHostName() {
        String copyPerson = examineAndUploadRedNoticeDao.getCopyPerson("HOST_NAME");
        return copyPerson;
    }


    private Map<String, Object> sendMessage( String path,String toEmailAddress, String sendingMailbox, String emailTitle, String emailContent, String copyPerson,String hostName,String myEmailPassword) {
        Properties props = new Properties();
        // 开启debug调试
        props.setProperty("mail.debug", "true");

        // 发送服务器需要身份验证
        props.setProperty("mail.smtp.auth", "true");

        // 端口号
        props.put("mail.smtp.port", 465);

        // 设置邮件服务器主机名
        props.setProperty("mail.smtp.host", hostName);

        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        final Map<String, Object> map = newHashMap();
        /**SSL认证，注意腾讯邮箱是基于SSL加密的，所以需要开启才可以使用**/
        //MailSSLSocketFactory sf = null;
        try {
                   /* sf = new MailSSLSocketFactory();
                    sf.setTrustAllHosts(true);

                 //设置是否使用ssl安全连接（一般都使用）
                props.put("mail.smtp.ssl.enable", "true");
                props.put("mail.smtp.ssl.socketFactory", sf);*/

            //创建会话
            Session session = Session.getInstance(props);

            //获取邮件对象
            //发送的消息，基于观察者模式进行设计的
            Message msg = new MimeMessage(session);

            //设置邮件标题

            msg.setSubject(emailTitle);
            //设置邮件内容
            //使用StringBuilder，因为StringBuilder加载速度会比String快，而且线程安全性也不错
            StringBuilder builder = new StringBuilder();
            //写入内容
            builder.append("\n" + emailContent);
            //设置显示的发件时间
            msg.setSentDate(new Date());
            //设置邮件内容
            msg.setText(builder.toString());
            final Multipart multipart = new MimeMultipart();
            MimeBodyPart mbpFile = new MimeBodyPart();
            //设置附件内容暂时注释
            /*final File source = new File(path);
            if (!source.exists()) {
                System.out.println(path + " not exists");
                return;
            }
            final String filePath =source.getPath();
            //根据附件文件创建文件数据源
            final DataSource ds = new FileDataSource(filePath);
            mbpFile.setDataHandler(new DataHandler(ds));
            //为附件设置文件名
            mbpFile.setFileName(ds.getName());
            multipart.addBodyPart(mbpFile);*/
            //设置发件人邮箱
            // InternetAddress 的三个参数分别为: 发件人邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
            msg.setFrom(new InternetAddress(sendingMailbox,"沃尔玛", "UTF-8"));
            //得到邮差对象
            Transport transport = session.getTransport();

            //连接自己的邮箱账户
            //密码不是自己QQ邮箱的密码，而是在开启SMTP服务时所获取到的授权码
            //connect(host, user, password)
            transport.connect( hostName, sendingMailbox, myEmailPassword);
            //发送邮件
            transport.sendMessage(msg, new Address[] { new InternetAddress(toEmailAddress) });
            transport.close();
            map.put("success", "yes");
        } catch (MessagingException e) {
            e.printStackTrace();
            map.put("success", "no");
            map.put("reason", "发送失败！");
        } catch (UnsupportedEncodingException e) {
            LOGGER.debug("连接失败！");
            e.printStackTrace();
            map.put("success", "no");
            map.put("reason", "发送失败！");
        }catch (IOException e) {
            e.printStackTrace();
            map.put("success","no");
            map.put("reason", "发送失败！");
        }
        return map;

    }

    private String getCopyPerson() {
        String copyPerson = examineAndUploadRedNoticeDao.getCopyPerson("COPY_PERSION");
        return copyPerson;
    }
    private String getToEmailAddress() {
        String toEmailAddress = examineAndUploadRedNoticeDao.getCopyPerson("RECIPIENTS");
        return toEmailAddress;

    }
    private String getEmailTitle() {
        String emailTitle = examineAndUploadRedNoticeDao.getCopyPerson("MAIL_TITLE");
        return emailTitle;

    }
    private String getEmailContent() {
        String emailContent = examineAndUploadRedNoticeDao.getCopyPerson("SEND_CONTENT");
        return emailContent;

    }
    private String getSendingMailbox() {
        String sendingMailbox = examineAndUploadRedNoticeDao.getCopyPerson("SENDER");
        return sendingMailbox;
    }


 @Override
    public int revoke(Long id) {
        return examineAndUploadRedNoticeDao.revoke(id);
    }
 @Override
    public InvoiceEntity getRedInfo(String uuid) {
        return examineAndUploadRedNoticeDao.getRedInfo(uuid);
    }

    @Override
    public String seletcTaxCode(String taxname) {
        return examineAndUploadRedNoticeDao.selectTaxCode(taxname);
    }
    private RedTicketMatch selectMatchTableByRedTicketDataSerialNumber(String redTicketDataSerialNumber) {
        return examineAndUploadRedNoticeDao.selectMatchTableByRedTicketDataSerialNumber(redTicketDataSerialNumber);
    }

    @Override
    public String uploadRedTicketRed(MultipartFile file, UserEntity user, String redTicketDataSerialNumber,Integer id,String businessType) {
        LOGGER.debug("----------------红字通知单上传开始--------------------");

        //文件名称
        String fileName = this.getOriginalFilename(file.getOriginalFilename());
        String[] split = fileName.split("_");
        if(split[0].equals(redTicketDataSerialNumber)){
            //获取文件类型
            String fileType = fileName.substring(fileName.indexOf(".")+1);
            //获取红字通知单号
            String redNoticeNumber =split[1].substring(0,split[1].lastIndexOf('.'));
            String reg="[0-9]{16}";
            if(!redNoticeNumber.matches(reg)){
                return "红字通知单的是16位数字格式！";
            }
            int res = examineAndUploadRedNoticeDao.getRedMatchByNo(redNoticeNumber);
            if(res>0){
                LOGGER.info("红字通知单已存在！");
                return "红字通知单已存在！";
            }
            com.xforceplus.wapp.common.utils.SFTPHandler handler = com.xforceplus.wapp.common.utils.SFTPHandler.getHandler(depositPath);
            String path ="";
            try {
                if (null != fileName) {
                    handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                    path= handler.upload(file);

                    if(StringUtils.isNotBlank(path)) {
                        FileEntity fileEntity = new FileEntity();
                        fileEntity.setFilePath(path);
                        fileEntity.setFileName(fileName);
                        fileEntity.setFileType(fileType);
                        this.saveFilePath(fileEntity);
                        this.updateSatus(id, redNoticeNumber, (fileEntity.getId()).intValue());
                        //改变退货状态
                        if (businessType.equals("1")) {
                            examineAndUploadRedNoticeDao.updateRuturnStatus(redTicketDataSerialNumber);
                        }
                        //改变协议状态
                       /* if (businessType.equals("2")) {
                            examineAndUploadRedNoticeDao.updateAgreementStatus(redTicketDataSerialNumber);
                        }*/
                    }
                }
            } catch (Exception e) {
                LOGGER.debug("----上传文件异常---" + e);
                return "上传失败，文件异常！";
            } finally {
                if (handler != null) {
                    handler.closeChannel();
                }
            }

        }else {
            LOGGER.debug("----------------序列号不相等--------------------");
            return "序列号不相等";
        }

        LOGGER.debug("----------------红字通知单上传完成--------------------");

        return "上传成功！";
    }

    @Override
    public List<RedTicketMatchDetail> getRedTicketDetailsById(Map<String, Object> para) {
        return examineAndUploadRedNoticeDao.getRedTicketDetailsById(para);
    }

    @Override
    public List<InvoiceEntity> getRedTicketInvoice(Map<String, Object> para) {


        return examineAndUploadRedNoticeDao.getRedTicketInvoice( para);
    }

    @Override
    public void updateTotalAmount(BigDecimal totalAmount, String invoiceCode, String invoiceNo) {
        examineAndUploadRedNoticeDao.updateTotalAmount(totalAmount,invoiceCode,invoiceNo);
    }

    @Override
    public void updateRuturnNumber(Map<String, Object> para) {
        examineAndUploadRedNoticeDao.updateRuturnNumber(para);
    }

    @Override
    public void updateAgreementNumber(Map<String, Object> para) {
        examineAndUploadRedNoticeDao.updateAgreementNumber(para);
    }
    @Override
    public List<ExamineExcelEntity> toExamineExcelEntity(List<Object> mergeInvoiceDetailList,Map<String, Object> map){
        List<ExamineExcelEntity> list=new ArrayList<>();
        int page=(int)map.get("page");
        int limit=(int)map.get("limit");
        int index = (limit*(page-1))+1;
        for (Object item : mergeInvoiceDetailList) {
            ExamineExcelEntity entity=new ExamineExcelEntity();
            if (item instanceof RedTicketMatchDetail) {
                RedTicketMatchDetail redTicketMatchDetail = (RedTicketMatchDetail) item;
                //序列号
                entity.setRownumber(""+index++);
                //货物(劳务服务)名称
                entity.setCargoName("商品一批(详见清单)_" + redTicketMatchDetail.getRedTicketDataSerialNumber());
                //单位
                entity.setUnit(redTicketMatchDetail.getGoodsUnit());
                //数量
                entity.setNum(redTicketMatchDetail.getRedRushNumber()+"");
                //单价
                entity.setPrice(redTicketMatchDetail.getRedRushPrice().toString());
                //金额
                entity.setAmount(redTicketMatchDetail.getRedRushAmount().setScale(2,BigDecimal.ROUND_UP).toString());
                //税率
                entity.setTax(new BigDecimal(redTicketMatchDetail.getTaxRate()).setScale(2,BigDecimal.ROUND_UP).toString());
                //税额
                entity.setTaxAmount(redTicketMatchDetail.getRedRushAmount().multiply(new BigDecimal(redTicketMatchDetail.getTaxRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_UP).toString());
                //开红票通知单理由
                entity.setRedReason(formatBusiness(redTicketMatchDetail.getBusinessType()));
                //办理类型
                entity.setType("购买方办理");
                //蓝票是否已抵扣
                entity.setIsDk("是");
            } else if (item instanceof InvoiceEntity) {
                InvoiceEntity invoiceEntity = (InvoiceEntity) item;
                //供应商号
                entity.setVenderid(invoiceEntity.getVenderid());
                //纳税识别号
                entity.setTaxNbr(invoiceEntity.getXfTaxNo());
                //供应商名称
                entity.setVenderNmae(invoiceEntity.getXfName());
            } else if (item instanceof String) {
                String code = (String) item;
                //税收分类编码
                entity.setTaxCode(code);
            }
            list.add(entity);
        }
        return list;
}
    @Override
     public ExamineExcelEntity toEntity(RedTicketMatchDetail redTicketMatchDetail,InvoiceEntity invoiceEntity,String code,Integer index){
        ExamineExcelEntity entity=new ExamineExcelEntity();
        //序列号
        entity.setRownumber(index.toString());
        //货物(劳务服务)名称
        entity.setCargoName("商品一批(详见清单)_" + redTicketMatchDetail.getRedTicketDataSerialNumber());
        //单位
        entity.setUnit(redTicketMatchDetail.getGoodsUnit());
        //数量
        entity.setNum(redTicketMatchDetail.getRedRushNumber()+"");
        //单价
        entity.setPrice(redTicketMatchDetail.getRedRushPrice().toString());
        //金额
        entity.setAmount(redTicketMatchDetail.getRedRushAmount().setScale(2,BigDecimal.ROUND_UP).toString());
        //税率
        entity.setTax(new BigDecimal(redTicketMatchDetail.getTaxRate()).setScale(2,BigDecimal.ROUND_UP).toString());
        //税额
        entity.setTaxAmount(redTicketMatchDetail.getRedRushAmount().multiply(new BigDecimal(redTicketMatchDetail.getTaxRate()).divide(new BigDecimal(100))).setScale(2,BigDecimal.ROUND_UP).toString());
        //开红票通知单理由
        entity.setRedReason(formatBusiness(redTicketMatchDetail.getBusinessType()));
        //办理类型
        entity.setType("购买方办理");
        //蓝票是否已抵扣
        entity.setIsDk("是");
        try{
            //供应商号
            entity.setVenderid(invoiceEntity.getVenderid());
            //纳税识别号
            entity.setTaxNbr(invoiceEntity.getXfTaxNo());
            //供应商名称
            entity.setVenderNmae(invoiceEntity.getXfName());
            //税收分类编码
            entity.setTaxCode(code);
        }catch (Exception e){

        }

        return entity;
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

    private int saveFilePath(FileEntity fileEntity) {
        return examineAndUploadRedNoticeDao.saveFilePathRed(fileEntity);
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
    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DEFAULT_SHORT_DATE_FORMAT);
    }
    private String formatBusinessType(String authStatus) {
        String authStatusName = "";
        if ("1".equals(authStatus)) {
            authStatusName = "索赔类型";
        } else if ("2".equals(authStatus)) {
            authStatusName = "协议类型";
        } else if ("3".equals(authStatus)) {
            authStatusName = "折让类型";
        }
        return authStatusName;
    }
    private String formatBusiness(String authStatus) {
        String authStatusName = "";
        if ("1".equals(authStatus)) {
            authStatusName = "退货";
        } else if ("2".equals(authStatus)) {
            authStatusName = "协议";
        } else if ("3".equals(authStatus)) {
            authStatusName = "折让";
        }
        return authStatusName;
    }
    private String formatDataStatus(String authStatus) {
        String authStatusName = "";
        if ("1".equals(authStatus)) {
            authStatusName = "是";
        } else if ("2".equals(authStatus)) {
            authStatusName = "否";
        }
        return authStatusName;
    }

    private String formatExamineResult(String authStatus) {
        //1-未审核 2-同意 3-不同意
        String authStatusName = "";
        if ("1".equals(authStatus)) {
            authStatusName = "未审核";
        } else if ("2".equals(authStatus)) {
            authStatusName = "同意";
        } else if ("3".equals(authStatus)) {
            authStatusName = "不同意";
        }
        return authStatusName;
    }
}
