package com.xforceplus.wapp.interfaceSAP;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.redTicket.dao.SapInvoiceDao;
import com.xforceplus.wapp.modules.redTicket.entity.SapInvoiceEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class SAP {
    //sftp IP地址
    //@Value("${pro.sftp.host}")
    private String host;
    //sftp 用户名
   // @Value("${pro.sftp.username}")
    private String userName;
    //sftp 密码
   // @Value("${pro.sftp.password}")
    private String password;
    //sftp 默认端口号
   // @Value("${pro.sftp.default.port}")
    private String defaultPort;
    //sftp 默认超时时间
   // @Value("${pro.sftp.default.timeout}")
    private String defaultTimeout;

    private static final Logger logger = Logger.getLogger(SAP.class);

    @Autowired
    private SapInvoiceDao sapInvoiceDao;

    /**
     * sap源文件目录
     */
   // @Value("${filePathConstan.sapRemoteRootPath}")
    private String sapRemoteRootPath;
    /**
     * sap备份目录
     */
  //  @Value("${filePathConstan.sapRemoteBakPath}")
    private String sapRemoteBakPath;
    /**
     * sap本地临时目录
     */
   // @Value("${filePathConstan.sapLocalTempPath}")
    private String sapLocalTempPath;

    /**
     * vendor源文件目录
     */
   // @Value("${filePathConstan.vendorRemoteRootPath}")
    private String vendorRemoteRootPath;
    /**
     * vendor备份目录
     */
  // @Value("${filePathConstan.vendorRemoteBakPath}")
    private String vendorRemoteBakPath;
    /**
     * vendor本地临时目录
     */
  //  @Value("${filePathConstan.vendorLocalTempPath}")
    private String vendorLocalTempPath;

    private SFTPHandler handler;

    private static  SAP sap;

    //CSV模板列数
    private static final int LEN = 19;

    //VENDOR CSV模板列数
    private static final int VENDOR_LEN = 2;

    @PostConstruct
    public void init(){
        sap = this;
        sap.sapInvoiceDao = this.sapInvoiceDao;
        sap.host = this.host;
        sap.userName = this.userName;
        sap.password = this.password;
        sap.defaultPort = this.defaultPort;
        sap.defaultTimeout = this.defaultTimeout;
        sap.sapRemoteRootPath = this.sapRemoteRootPath;
        sap.sapRemoteBakPath = this.sapRemoteBakPath+new SimpleDateFormat("yyyyMMdd").format(new Date())+"/";
        sap.sapLocalTempPath = this.sapLocalTempPath;
        sap.vendorRemoteRootPath = this.vendorRemoteRootPath;
        sap.vendorRemoteBakPath = this.vendorRemoteBakPath+new SimpleDateFormat("yyyyMMdd").format(new Date())+"/";
        sap.vendorLocalTempPath = this.vendorLocalTempPath;
    }


    private static class SAPInstance {
        private static final SAP INSTANCE = sap;
    }

    public static SAP getInstance() {
        return SAP.SAPInstance.INSTANCE;
    }

    public void runVendor(){
        logger.info("----------------SAP导入供应商启动------------------");
        //本次需要解析的文件名列表
        List<String> fileNameList = newArrayList();

        //异常列表
        List errorList = newArrayList();

        int count = 0;

        handler = SFTPHandler.getHandler(sap.vendorRemoteRootPath, sap.vendorLocalTempPath);
        try {
            handler.openChannel(sap.host, sap.userName, sap.password, Integer.parseInt(sap.defaultPort), Integer.parseInt(sap.defaultTimeout));
            //从SFTP服务器获取指定文件夹下所有文件名
            fileNameList = handler.getFileNameList();
            if(fileNameList.size()<=0){
                return;
            }
            for (String fileName : fileNameList) {
                //下载到本地
                handler.download(sap.vendorRemoteRootPath+File.separator+fileName, fileName);
            }
        } catch (Exception e){
            logger.error("获取供应商文件时异常:"+e);
        } finally {
            if (handler != null) {
                handler.closeChannel();
                handler = null;
            }
        }

        File[] fileList = new File(sap.vendorLocalTempPath).listFiles();
        CSVReader reader = null;
        for(File csv : fileList){
            logger.info("开始解析:"+csv.getName());
            try {
                reader = new CSVReader(new InputStreamReader(new FileInputStream(csv), "GBK"));
                String[] header = reader.readNext();//跳过表头
                final int len = header.length;//数据列数
                if(VENDOR_LEN!=len){
                    logger.error("文件数据列与模板存在差异,期望"+VENDOR_LEN+"列,实际"+len+"列!");
                    break;
                }
            } catch (FileNotFoundException ffe){
                logger.error("找不到文件:"+ffe);
            } catch (IOException ioe){
                logger.error("读取CSV文件异常:"+ioe);
            } catch (Exception e){
                logger.error("解析数据异常:"+e);
            }

            try{
                String[] row;
                while ((row = reader.readNext()) != null){
                    try {
                        count++;
                        UserEntity userEntity = constructUserEntity(row);
                        //供应商号不超过6位，10位供应商号都是数字的才添加10位供应商号
                        if(StringUtils.isNotBlank(userEntity.getUsercode())&&StringUtils.isNotBlank(userEntity.getTenUserCode())){
                            if(userEntity.getUsercode().trim().length()<=6
                                    &&userEntity.getTenUserCode().trim().length()==10
                                    &&StringUtils.isNumeric(userEntity.getTenUserCode())){
                                //供应商号如果不足6位，前面补0
                                DecimalFormat g1 = new DecimalFormat("000000");
                                userEntity.setUsercode(g1.format(new BigDecimal(userEntity.getUsercode())));
                                sap.sapInvoiceDao.updateTenUserCode(userEntity);
                            }
                        }
                    } catch (Exception e){
                        //处理单条数据异常
                        errorList.add(row);
                    }
                }
            } catch (IOException ioe){
                logger.error("读取CSV文件异常:"+ioe);
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //添加总体日志
        errorList.add(new String[]{"今日共计处理10位供应商号数据"+count+"条,其中失败"+errorList.size()+"条"});

        handler = SFTPHandler.getHandler(sap.vendorRemoteBakPath, sap.vendorRemoteRootPath);
        CSVWriter writer = null;
        try {
            handler.openChannel(sap.host, sap.userName, sap.password, Integer.parseInt(sap.defaultPort), Integer.parseInt(sap.defaultTimeout));
            for (String fileName : fileNameList) {
                //完成后备份(将文件移动到备份文件夹,并删除原文件)
                handler.move(sap.vendorRemoteRootPath+fileName);
            }

            //错误数据写入文件
            File errorFile = new File(sap.vendorLocalTempPath+"errorRecord.csv");
            writer = new CSVWriter(new FileWriter(errorFile));
            writer.writeAll(errorList);

            writer.flush();

            //错误文件上传
            handler.upload(errorFile, "errorRecord"+new Date().getTime()+".csv");

        } catch (Exception e){
            logger.error("备份文件时异常:"+e);
        } finally {
            if (handler != null) {
                handler.closeChannel();
                handler = null;
            }
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //删除本地临时文件
        File dir = new File(sap.vendorLocalTempPath);
        for(File f : dir.listFiles()){
            f.delete();
        }
        logger.info("----------------SAP导入供应商结束------------------");
    }

    public void run(){
        logger.info("----------------SAP启动------------------");
        //本次需要解析的文件名列表
        List<String> fileNameList = newArrayList();

        //异常列表
        List errorList = newArrayList();

        int count = 0;

        handler = SFTPHandler.getHandler(sap.sapRemoteRootPath, sap.sapLocalTempPath);
        try {
            handler.openChannel(sap.host, sap.userName, sap.password, Integer.parseInt(sap.defaultPort), Integer.parseInt(sap.defaultTimeout));
            //从SFTP服务器获取指定文件夹下所有文件名
            fileNameList = handler.getFileNameList();
            if(fileNameList.size()<=0){
                return;
            }
            for (String fileName : fileNameList) {
                //下载到本地
                handler.download(sap.sapRemoteRootPath+File.separator+fileName, fileName);
            }
        } catch (Exception e){
            logger.error("获取文件时异常:"+e);
        } finally {
            if (handler != null) {
                handler.closeChannel();
                handler = null;
            }
        }

        File[] fileList = new File(sap.sapLocalTempPath).listFiles();
        CSVReader reader = null;
        for(File csv : fileList){
            logger.info("开始解析:"+csv.getName());
            //在本地解析文件,根据(参照后8位=发票号码, 本币金额=发票金额)查询底账表,有则更新null的字段,没有跳过
            try {
                reader = new CSVReader(new InputStreamReader(new FileInputStream(csv), "GBK"));
                String[] header = reader.readNext();//跳过表头
                final int len = header.length;//数据列数
                if(LEN!=len){
                    logger.error("文件数据列与模板存在差异,期望"+LEN+"列,实际"+len+"列!");
                    break;
                }
            } catch (FileNotFoundException ffe){
                logger.error("找不到文件:"+ffe);
            } catch (IOException ioe){
                logger.error("读取CSV文件异常:"+ioe);
            } catch (Exception e){
                logger.error("解析数据异常:"+e);
            }
            List<SapInvoiceEntity> sapList = newArrayList();
            try{
                String[] row;
                while ((row = reader.readNext()) != null){
                    try {
                        count++;
                        SapInvoiceEntity invoice = constructEntity(row);
                        //通过10位供应商号获取6号供应商号
                        String usercode = sap.sapInvoiceDao.getUserCode(invoice.getSubject());
                        invoice.setUsercode(usercode);
                        sapList.add(invoice);
                        sap.sapInvoiceDao.saveSapInvoice(invoice);
                        sap.sapInvoiceDao.updateSapInvoiceToRecord(invoice);
                    } catch (Exception e){
                        //处理单条数据异常
                        errorList.add(row);
                    }
                }
            } catch (IOException ioe){
                logger.error("读取CSV文件异常:"+ioe);
            }
            List<SapInvoiceEntity> invoiceList = getInvoiceOrProtocolList(sapList,"invoice");
            List<SapInvoiceEntity> protocolList = getInvoiceOrProtocolList(sapList,"protocol");
            //付款信息关联发票
            associateInvoice(invoiceList);
            //付款信息关联协议
            associateProtocol(protocolList);
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //添加总体日志
        errorList.add(new String[]{"今日共计处理数据"+count+"条,其中失败"+errorList.size()+"条"});

        handler = SFTPHandler.getHandler(sap.sapRemoteBakPath, sap.sapRemoteRootPath);
        CSVWriter writer = null;
        try {
            handler.openChannel(sap.host, sap.userName, sap.password, Integer.parseInt(sap.defaultPort), Integer.parseInt(sap.defaultTimeout));
            for (String fileName : fileNameList) {
                //完成后备份(将文件移动到备份文件夹,并删除原文件)
                handler.move(sap.sapRemoteRootPath+fileName);
            }

            //错误数据写入文件
            File errorFile = new File(sap.sapLocalTempPath+"errorRecord.csv");
            writer = new CSVWriter(new FileWriter(errorFile));
            writer.writeAll(errorList);

            writer.flush();

            //错误文件上传
            handler.upload(errorFile, "errorRecord"+new Date().getTime()+".csv");

        } catch (Exception e){
            logger.error("备份文件时异常:"+e);
        } finally {
            if (handler != null) {
                handler.closeChannel();
                handler = null;
            }
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //删除本地临时文件
        File dir = new File(sap.sapLocalTempPath);
        for(File f : dir.listFiles()){
            f.delete();
        }
        logger.info("----------------SAP结束------------------");
    }


    /**
     * 将一行数据构造成实体
     * @param row
     * @return
     */
    private SapInvoiceEntity constructEntity(String[] row){
        SapInvoiceEntity invoice = new SapInvoiceEntity();
        invoice.setDocumentHeaderText(row[0]);
        invoice.setSubject(row[1]);
        invoice.setCompanyCode(row[2]);
        invoice.setDocumentType(row[3]);
        invoice.setCertificateNo(row[4]);
        invoice.setWriteOff(row[5]);
        invoice.setClearanceVoucher(row[6]);
        invoice.setClearingDate(row[7]);
        invoice.setReference(row[8]);
        invoice.setShowCurrencyAmount(new BigDecimal(row[9].replace(",","")));
        invoice.setVoucherCurrency(row[10]);
        invoice.setCurrency(row[11]);
        invoice.setCurrencyAmount(new BigDecimal(row[12].replace(",","")));
        invoice.setPostingDate(row[13]);
        invoice.setPaymentDate(row[14]);
        invoice.setTaxCode(row[15]);
        invoice.setFiscalYear(row[16]);
        invoice.setInvoiceText(row[17]);
        invoice.setInvoiceDate(row[18]);
        return invoice;
    }

    private List<SapInvoiceEntity> getInvoiceOrProtocolList(List<SapInvoiceEntity> list,String type){
        List<SapInvoiceEntity> sapList = newArrayList();
        if("protocol".equals(type)){
            for (SapInvoiceEntity sap : list) {
                if("YS IS YC YD IC ID YO IO YR IR".contains(sap.getDocumentType().toUpperCase())){
                    sapList.add(sap);
                }
            }
        } else if("invoice".equals(type)){
            for (SapInvoiceEntity sap : list) {
                if("Z1 Z2 K4 KR KZ".contains(sap.getDocumentType().toUpperCase())){
                    sapList.add(sap);
                }
            }
        }
        return sapList;
    }

    private void associateInvoice(List<SapInvoiceEntity> list){
        //根据参照(发票号码)和科目(供应商号)分组
        Map<String, List<SapInvoiceEntity>> sapMap = list.stream()
                .collect(Collectors.groupingBy(d -> fetchGroupKey(d)));
        for(Map.Entry<String, List<SapInvoiceEntity>> entry : sapMap.entrySet()) {
            List<SapInvoiceEntity> sapList =entry.getValue();
            StringBuilder stringBuilder = new StringBuilder();
            for (SapInvoiceEntity entity : sapList) {
                //发票号如果不足8位，前面补0
                DecimalFormat g1 = new DecimalFormat("00000000");
                String invoiceNo = entity.getReference();
                if(invoiceNo.length()>8) {
                    entity.setReference(invoiceNo.substring(invoiceNo.length() - 8, invoiceNo.length()));
                }
                entity.setReference(g1.format(new BigDecimal(entity.getReference())));
                stringBuilder.append(entity.getDocumentType().trim().substring(0,1));
            }

            //如果凭证类型只包含Z开头的，按Z1的来关联，没有Z1就按其他Z开头的,否则代表含有K开头的
            boolean haveZ1 = false;
            SapInvoiceEntity z1 = null;
            if(StringUtils.containsOnly(stringBuilder.toString(),"Z")){
                for (SapInvoiceEntity entity : sapList) {
                    if("Z1".equals(entity.getDocumentType())){
                        haveZ1 = true;
                        z1 = entity;
                    }
                }
                if(haveZ1){
                    //如果有Z1的 就按Z1的关联
                    sap.sapInvoiceDao.associateInvoice(z1);
                } else{
                    //没有Z1的，任取一个Z开头的
                    sap.sapInvoiceDao.associateInvoice(sapList.get(0));
                }
            } else{
                for (SapInvoiceEntity entity : sapList) {
                    if(entity.getDocumentType().contains("K")){
                        sap.sapInvoiceDao.associateInvoice(entity);
                        break;
                    }
                }
            }
        }
    }

    private void associateProtocol(List<SapInvoiceEntity> list){
        for (SapInvoiceEntity entity : list) {
            sap.sapInvoiceDao.associateProtocol(entity);
        }
    }

    /**
     * 将一行数据构造成实体
     * @param row
     * @return
     */
    private UserEntity constructUserEntity(String[] row){
        UserEntity userEntity = new UserEntity();
        userEntity.setTenUserCode(row[0]);
        userEntity.setUsercode(row[1]);
        return userEntity;
    }

    private String fetchGroupKey(SapInvoiceEntity entity){
        return entity.getReference()
                + entity.getSubject();
    }
}