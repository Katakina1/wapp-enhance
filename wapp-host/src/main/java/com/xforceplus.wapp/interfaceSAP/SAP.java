package com.xforceplus.wapp.interfaceSAP;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.utils.ConfigConstant;
import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.base.entity.DictdetaEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.redTicket.dao.SapInvoiceDao;
import com.xforceplus.wapp.modules.redTicket.entity.SapInvoiceEntity;
import com.google.common.base.Strings;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.springframework.util.StringUtils.endsWithIgnoreCase;
import static org.springframework.util.StringUtils.trimAllWhitespace;

@Component
public class SAP {
    //sftp IP地址
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

    private static final Logger logger = Logger.getLogger(SAP.class);

    @Autowired
    private SapInvoiceDao sapInvoiceDao;

    /**
     * sap源文件目录
     */
    @Value("${sap.sapRemoteRootPath}")
    private String sapRemoteRootPath;
    /**
     * sap备份目录
     */
    @Value("${filePathConstan.sapRemoteBakPath}")
    private String sapRemoteBakPath;
    /**
     * sap本地临时目录
     */
    @Value("${filePathConstan.sapLocalTempPath}")
    private String sapLocalTempPath;

    /**
     * vendor源文件目录
     */
    @Value("${filePathConstan.vendorRemoteRootPath}")
    private String vendorRemoteRootPath;
    /**
     * vendor备份目录
     */
    @Value("${filePathConstan.vendorRemoteBakPath}")
    private String vendorRemoteBakPath;
    /**
     * vendor本地临时目录
     */
    @Value("${filePathConstan.vendorLocalTempPath}")
    private String vendorLocalTempPath;

    private SFTPHandler handler;
    @Value("${sap.remoteUsername}")
    private String remoteUsername;
    @Value("${sap.remotePassword}")
    private String remotePassword;
    private static  SAP sap;

    //CSV模板列数
    private static final int LEN = 18;

    //VENDOR CSV模板列数
    private static final int VENDOR_LEN = 2;

    private static final String STATUS = "status";
    //status 为1 cell为空，0为正常
    private static final String STATUS_NORMAL = "0";
    private static final String STATUS_NULL = "1";

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
        for(File excel : fileList) {
            logger.info("开始解析:" + excel.getName());
            Workbook wb;
            try {
                wb = getWorkBook(excel);
                final int sheetStart = 0;
                final Sheet sheet = wb.getSheetAt(sheetStart);//得到Excel工作表对象
                final int rowCount = sheet.getLastRowNum();//得到Excel工作表最大行数
                final int len = sheet.getRow(0).getPhysicalNumberOfCells();//数据列数
                if (VENDOR_LEN != len) {
                    logger.error("文件数据列与模板存在差异,期望" + VENDOR_LEN + "列,实际" + len + "列!");
                    break;
                }
                Row row = null;
                for (int i = 1; i <= rowCount; i++) {
                    try {
                        row = sheet.getRow(i);
                        final Map<String, Object> wrapResult = constructUserEntity(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）
                        String status = (String) wrapResult.get(STATUS);
                        //excel行状态正常，获取数据
                        if (STATUS_NORMAL.equals(status)) {
                            count++;
                            UserEntity userEntity = (UserEntity) wrapResult.get("vendor");
                            //供应商号不超过6位，10位供应商号都是数字的才添加10位供应商号
                            if (userEntity.getUsercode().trim().length() <= 6
                                    && userEntity.getTenUserCode().trim().length() == 10
                                    && StringUtils.isNumeric(userEntity.getTenUserCode())) {
                                //供应商号如果不足6位，前面补0
                                DecimalFormat g1 = new DecimalFormat("000000");
                                userEntity.setUsercode(g1.format(new BigDecimal(userEntity.getUsercode())));
                                sap.sapInvoiceDao.updateTenUserCode(userEntity);
                            } else {
                                //处理单条不符合条件数据
                                String[] error = new String[VENDOR_LEN];
                                //转换成csv文件需要string[],row转换为string[]
                                for (int j = 0; j < VENDOR_LEN; j++) {
                                    error[j] = getCellData(row, j);
                                }
                                errorList.add(error);
                                }

                        }
                    } catch (Exception e) {
                        //处理单条数据异常
                        String[] error = new String[VENDOR_LEN];
                        //转换成csv文件需要string[],row转换为string[]
                        for (int j = 0; j < VENDOR_LEN; j++) {
                            error[j] = getCellData(row, j);
                        }
                        errorList.add(error);
                    }
                }
            } catch (ExcelException excelE) {
                logger.error("读取excel异常,ExcelException:", excelE);
            } catch (Exception e) {
                logger.error("SAP导入供应商解析数据异常:" + e);
            }
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
            writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(errorFile),"GBK"));
            writer.writeAll(errorList);

            writer.flush();

            //错误文件上传
            handler.upload(errorFile, "errorRecord"+new Date().getTime()+".csv");

        } catch (Exception e){
            logger.error("备份文件时异常:"+e);
            e.printStackTrace();
        } finally {
            if (handler != null) {
                handler.closeChannel();
                handler = null;
            }
            try {
                if(writer!=null){
                    writer.close();
                }
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
        try {
            NtlmPasswordAuthentication auth=new NtlmPasswordAuthentication("cn",remoteUsername,remotePassword);
            SmbFile smbFile = null;
            smbFile = new SmbFile("smb://" + sap.sapRemoteRootPath,auth);
            if (smbFile != null) {
                if (smbFile.isDirectory()) {
                    for (SmbFile file : smbFile.listFiles()) {
                        String filePath="smb://" +sap.sapRemoteRootPath+file.getName();
                        if(filePath.indexOf(".xlsx")!=-1){
                            smbGet(filePath,sap.sapLocalTempPath);
                            logger.info("文件："+file.getName());
                        }
                    }
                }
            }
        } catch (Exception e){
            logger.error("获取文件时异常:"+e);
        }

        File[] fileList = new File(sap.sapLocalTempPath).listFiles();

        for(File excel : fileList){
            logger.info("开始解析:"+excel.getName());
            //从excel读取的所有数据
            List<SapInvoiceEntity> sapList = newArrayList();
            //创建工作簿对象
            Workbook wb;
            //在本地解析文件,根据(参照后8位=发票号码, 本币金额=发票金额)查询底账表,有则更新null的字段,没有跳过
            try {
                wb = getWorkBook(excel);
                final int sheetStart = 0;
                final Sheet sheet = wb.getSheetAt(sheetStart);//得到Excel工作表对象
                final int rowCount = sheet.getLastRowNum();//得到Excel工作表最大行数
                final int len = sheet.getRow(0).getPhysicalNumberOfCells();//数据列数
                if(LEN!=len){
                    logger.error("文件数据列与模板存在差异,期望"+LEN+"列,实际"+len+"列!");
                    break;
                }
                Row row=null;
                for (int i = 1; i <= rowCount; i++) {
                    try {
                         row = sheet.getRow(i);
                        final Map<String, Object> wrapResult = constructEntity(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）
                        String status = (String) wrapResult.get(STATUS);
                        //excel行状态正常，获取数据
                        if (STATUS_NORMAL.equals(status)) {
                            count++;
                            SapInvoiceEntity invoice = (SapInvoiceEntity) wrapResult.get("sap");
                            //通过10位供应商号获取6号供应商号
                            String venderId = sap.sapInvoiceDao.getUserCode(invoice.getUsercode());
                            invoice.setVenderId(venderId);
                            sapList.add(invoice);
                            Integer num=sapInvoiceDao.selectDsign(invoice.getPaymentDate(),invoice.getReference(),invoice.getCurrencyAmount(),invoice.getVenderId());
                            if(num==0){
                                sap.sapInvoiceDao.saveSapInvoice(invoice);
                            }
                            sap.sapInvoiceDao.updateSapInvoiceToRecord(invoice);
                        }
                    } catch (Exception e){
                        //处理单条数据异常
                        String[] error = new String[LEN];
                        //转换成csv文件需要string[],row转换为string[]
                        for(int j=0;j<LEN;j++){
                            Cell cell = row.getCell(j);
                            //日期单元格需要使用cell.getDateCellValue获取值,其他类型的单元格调用getCellData即可
                            if(j==1||j==3||j==8) {
                                if(cell!=null) {
                                    if (1 == cell.getCellType()) {
                                        error[j] = getCellData(row, j);
                                    } else {
                                        Date date = cell.getDateCellValue();
                                        if (date != null) {
                                            error[j] = new SimpleDateFormat("yyyy/MM/dd").format(date);
                                        }
                                    }
                                }
                            } else{
                                error[j]=getCellData(row, j);
                            }
                        }
                        errorList.add(error);
                    }
                }
            } catch (ExcelException excelE) {
                logger.error("读取excel异常,ExcelException:", excelE);
            } catch (Exception e){
                logger.error("解析数据异常:"+e);
            }
            //根据凭证类型拆分为商品发票和协议
            List<SapInvoiceEntity> invoiceList = getInvoiceOrProtocolList(sapList,"invoice");
            List<SapInvoiceEntity> protocolList = getInvoiceOrProtocolList(sapList,"protocol");
            //付款信息关联发票
            associateInvoice(invoiceList,errorList);
            //付款信息关联协议
            associateProtocol(protocolList);
        }

        //添加总体日志
        errorList.add(new String[]{"今日共计成功处理数据"+count+"条,失败"+errorList.size()+"条"});

        handler = SFTPHandler.getHandler(sap.sapRemoteBakPath, sap.sapLocalTempPath);
        CSVWriter writer = null;
        try {
            handler.openChannel(sap.host, sap.userName, sap.password, Integer.parseInt(sap.defaultPort), Integer.parseInt(sap.defaultTimeout));
            //错误数据写入文件
            File errorFile = new File(sap.sapLocalTempPath+"errorRecord.csv");
            writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(errorFile),"GBK"));
            writer.writeAll(errorList);

            writer.flush();

            //错误文件上传
            handler.upload(errorFile, "errorRecord"+new Date().getTime()+".csv");
            //文件备份
            for(File fi:fileList){
            handler.upload(fi,fi.getName());
            }
            //删除zp类型付款信息
            sap.sapInvoiceDao.delZp();
        } catch (Exception e){
            logger.error("备份文件时异常:"+e);
            e.printStackTrace();
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
    private Map<String, Object>  constructEntity(Row row){
        final Map<String, Object> result = newHashMap();
        DateFormat format1= new SimpleDateFormat("yyyy/MM/dd");
        DateFormat format2= new SimpleDateFormat("yyyy-MM-dd");
        SapInvoiceEntity invoice = new SapInvoiceEntity();

        //如果凭证号为空，标记此行状态为空,不读取
        if (StringUtils.isBlank(getCellData(row,0))) {
            result.put(STATUS, STATUS_NULL);
            return result;
        }

        invoice.setCertificateNo(getCellData(row,0));
        //获取excel日期单元格的数据(开票日期)
        Cell cell = row.getCell(1);
        if(cell!=null) {
            if (1 == cell.getCellType()) {
                String dateString = getCellData(row, 1);
                try{
                    Date de=format1.parse(dateString);
                    dateString=format2.format(de);
                }catch (Exception e){
                  e.printStackTrace();
                }
                invoice.setInvoiceDate(dateString);
            } else {
                Date date = cell.getDateCellValue();
                if (date != null) {
                    invoice.setInvoiceDate(format2.format(date));
                }
            }
        }
        invoice.setDocumentType(getCellData(row,2));
        //获取excel日期单元格的数据(过账日期)
        Cell cell3 = row.getCell(3);
        if(cell3!=null) {
            if (1 == cell3.getCellType()) {
                String dateString = getCellData(row, 3);
                try{
                    Date de=format1.parse(dateString);
                    dateString=format2.format(de);
                }catch (Exception e){
                    e.printStackTrace();
                }
                invoice.setPostingDate(dateString);
            } else {
                Date date = cell3.getDateCellValue();
                if (date != null) {
                    invoice.setPostingDate(format2.format(date));
                }
            }
        }
        invoice.setCompanyCode(getCellData(row,4));
        invoice.setCostCenter(getCellData(row,5));
        invoice.setProfitCenter(getCellData(row,6));
        invoice.setReference(getCellData(row,7));
        //获取excel日期单元格的数据(清账日期、付款日期)
        Cell cell8 = row.getCell(8);
        if(cell8!=null) {
            if (1 == cell8.getCellType()) {
                String dateString = getCellData(row, 8);
                try{
                    Date de=format1.parse(dateString);
                    dateString=format2.format(de);
                }catch (Exception e){
                    e.printStackTrace();
                }
                invoice.setClearingDate(dateString);
                invoice.setPaymentDate(dateString);
            } else {
                Date date = cell8.getDateCellValue();
                if (date != null) {
                    invoice.setClearingDate(format2.format(date));
                    invoice.setPaymentDate(format2.format(date));
                }
            }
        }
        invoice.setClearanceVoucher(getCellData(row,9));
        invoice.setSubject(getCellData(row,10));
        invoice.setUsercode(getCellData(row,11));
        invoice.setShowCurrencyAmount(new BigDecimal(getCellData(row,12)).multiply(new BigDecimal(-1)).setScale(2));
        invoice.setVoucherCurrency(getCellData(row,13));
        invoice.setCurrencyAmount(new BigDecimal(getCellData(row,14)).multiply(new BigDecimal(-1)).setScale(2));
        invoice.setCurrency(getCellData(row,15));
        invoice.setInvoiceText(getCellData(row,16));
        invoice.setDocumentHeaderText(getCellData(row,17));

        result.put(STATUS, STATUS_NORMAL);
        result.put("sap", invoice);
        return result;
    }

    /**
     * 将一行数据构造成实体
     * @param row
     * @return
     */
    private Map<String, Object> constructUserEntity(Row row){
        final Map<String, Object> result = newHashMap();
        UserEntity userEntity = new UserEntity();

        //如果供应商号为空，标记此行状态为空,不读取
        if (StringUtils.isBlank(getCellData(row,1))) {
            result.put(STATUS, STATUS_NULL);
            return result;
        }

        userEntity.setTenUserCode(getCellData(row,0));
        userEntity.setUsercode(getCellData(row,1));

        result.put(STATUS, STATUS_NORMAL);
        result.put("vendor", userEntity);
        return result;
    }

    private List<SapInvoiceEntity> getInvoiceOrProtocolList(List<SapInvoiceEntity> list,String type){
        List<SapInvoiceEntity> sapList = newArrayList();
        //从业务字典获取凭证类型-商品发票
        List<String> documentTypeInvoice = sap.sapInvoiceDao.getDictNameByType("DOCUMENT_TYPE_INVOICE");
        //从业务字典获取凭证类型-协议
        List<String> documentTypeProtocol = sap.sapInvoiceDao.getDictNameByType("DOCUMENT_TYPE_PROTOCOL");
        if("protocol".equals(type)){
            for (SapInvoiceEntity sap : list) {
                if(documentTypeProtocol.contains(sap.getDocumentType())){
                    sap.setCurrencyAmount(sap.getCurrencyAmount().multiply(new BigDecimal(-1)).setScale(2));
                    sap.setShowCurrencyAmount(sap.getShowCurrencyAmount().multiply(new BigDecimal(-1)).setScale(2));
                    sapList.add(sap);
                }
            }
        } else if("invoice".equals(type)){
            for (SapInvoiceEntity sap : list) {
                if(documentTypeInvoice.contains(sap.getDocumentType())){
                    sapList.add(sap);
                }
            }
        }
        return sapList;
    }

    private void associateInvoice(List<SapInvoiceEntity> list,List errList){
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
                try {
                    entity.setReference(g1.format(new BigDecimal(entity.getReference())));
                }catch (Exception e){
                    errList.add(entity.toString().split(","));
                    logger.error("转换数值时异常:"+e);
                    continue;
                }
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

    private String fetchGroupKey(SapInvoiceEntity entity){
        return entity.getReference()
                + entity.getUsercode();
    }

    /**
     * 获取excel单元格数据
     * @param row 行
     * @param cellNum 列号
     * @return 单元格数据
     */
    protected static String getCellData(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        if (cell == null) {
            return EMPTY;
        }
        int type = cell.getCellType();
        String returnValue = null;
        switch (type) {
            case Cell.CELL_TYPE_STRING:
                returnValue = trimAllWhitespace(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                returnValue = NumberFormat.getInstance().format(cell.getNumericCellValue()).replace(",","");
                break;
            case Cell.CELL_TYPE_FORMULA:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                if (!cell.getStringCellValue().equals("")) {
                    returnValue = cell.getStringCellValue();
                } else {
                    returnValue = cell.getNumericCellValue() + "";
                }
                if("#N/A".equals(returnValue)){
                    returnValue = EMPTY;
                }
                break;
            case Cell.CELL_TYPE_BLANK:
                returnValue = EMPTY;
                break;
            default:
                logger.error("Excel读取错误!");
                break;
        }
        return returnValue;
    }

    /**
     * 获取工作簿对象
     * @param file 导入的文件
     * @return 工作簿对象
     * @throws ExcelException 异常
     */
    protected static Workbook getWorkBook(File file) throws ExcelException {
        final Workbook workbook;
        try {
            if (endsWithIgnoreCase(file.getName(), ConfigConstant.EXCEL_XLS)) {
                workbook = new HSSFWorkbook(new FileInputStream(file));
            } else if (endsWithIgnoreCase(file.getName(), ConfigConstant.EXCEL_XLSX)) {
                workbook = new XSSFWorkbook(new FileInputStream(file));
            } else {
                throw new ExcelException(ExcelException.READ_ERROR, "Excel读取错误，请导入.xls或.xlsx文件!");
            }
            return workbook;
        } catch (IOException e) {
            logger.error("Excel读取错误:{}", e);
            throw new ExcelException(ExcelException.READ_ERROR, "Excel读取错误!");
        }
    }

    public void smbGet(String remoteUrl,String localDir) {
    InputStream in = null;
    OutputStream out = null;
        NtlmPasswordAuthentication auth=new NtlmPasswordAuthentication("cn",remoteUsername,remotePassword);
    try {
    SmbFile remoteFile = new SmbFile(remoteUrl,auth);
    if(remoteFile==null){
        logger.error("共享文件不存在");
    return;
    }
    String fileName = remoteFile.getName();
    File localFile = new File(localDir+File.separator+fileName);
    in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
    out = new BufferedOutputStream(new FileOutputStream(localFile));
    byte[] buffer = new byte[1024];
    while(in.read(buffer)!=-1){
    out.write(buffer);
    buffer = new byte[1024];
    }
    } catch (Exception e) {
    e.printStackTrace();
    } finally {
   try {
   out.close();
   in.close();
   }catch (IOException e) {
   e.printStackTrace();
   }
  }
}
}