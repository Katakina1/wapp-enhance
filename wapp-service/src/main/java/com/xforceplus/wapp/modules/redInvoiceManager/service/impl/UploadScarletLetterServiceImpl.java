package com.xforceplus.wapp.modules.redInvoiceManager.service.impl;



import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.einvoice.util.*;
import com.xforceplus.wapp.modules.redInvoiceManager.dao.UploadScarletLetterDao;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceListExcelEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarleQueryExcelEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.service.UploadScarletLetterService;
import com.xforceplus.wapp.modules.redTicket.entity.FileEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.einvoice.constant.Constants.FILE_DATE_FORMAT;
import static com.xforceplus.wapp.modules.einvoice.constant.Constants.SUBSTR_REGEX_FOR_FILE;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class UploadScarletLetterServiceImpl implements UploadScarletLetterService {
    private final static Logger LOGGER = getLogger(UploadScarletLetterServiceImpl.class);
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
    @Autowired
    private UploadScarletLetterDao uploadScarletLetterDao;

    @Override
    public List<UploadScarletLetterEntity> queryList(String schemaLabel, Map<String, Object> map) {
        return uploadScarletLetterDao.queryList(schemaLabel,map);
    }
    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel,Map<String, Object> map) {

        return uploadScarletLetterDao.queryTotalResult(schemaLabel,map);
    }

    @Override
    public List<UploadScarletLetterEntity> queryListByStore(String schemaLabel, Map<String, Object> map) {
        return uploadScarletLetterDao.queryListByStore(schemaLabel,map);
    }

    @Override
    public List<UploadScarletLetterEntity> queryListByStoreAll(Map<String, Object> map) {
        return uploadScarletLetterDao.queryListByStoreAll(map);
    }

    @Override
    public UploadScarletLetterEntity getTypeById(String schemaLabel, Map<String, Object> map) {
        return uploadScarletLetterDao.getTypeById(schemaLabel,map);
    }
    @Override
    public ReportStatisticsEntity queryTotalResultByStore(String schemaLabel,Map<String, Object> map) {

        return uploadScarletLetterDao.queryTotalResultByStore(schemaLabel,map);
    }
    @Override
    public String uploadRedTicketRed(MultipartFile file, UserEntity user, String serialNumber, Integer id) {

        String msg="";
        LOGGER.debug("----------------开红票资料上传开始--------------------");
        //文件名称
        String fileName = this.getOriginalFilename(file.getOriginalFilename());
        int w = fileName.length();
        if(w != 33 && w != 34){
            msg="文件名不正确，文件名格式为（12位序列号_16位红字通知单号）";
            return msg;
        }
        //获取序列号
        String str1 = fileName.substring(0,12);
        //获取红字通知单号
        String str2 = fileName.substring(13,29);
        String str3 = fileName.substring(0,29);
        String[] split = fileName.split("_");

        if(split[0].equals(serialNumber)) {
            String redNoticeNumber =split[1].substring(0,split[1].lastIndexOf('.'));
            String reg="[0-9]{16}";
            if(!redNoticeNumber.matches(reg)){
                return "红字通知单的是16位数字格式！";
            }
            int filecount = getfileCount(str3);
            if (filecount == 0) {
                //获取文件类型
                String fileType = fileName.substring(fileName.indexOf(".") + 1);
                com.xforceplus.wapp.common.utils.SFTPHandler handler = com.xforceplus.wapp.common.utils.SFTPHandler.getHandler(depositPath);
                try {
                    if (null != fileName) {
                        handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                        String path = handler.upload(file);
                        String str= path.substring(path.lastIndexOf("/") + 1);
                        String fileNa=str.substring(0,str.indexOf("."));
                        this.saveFilePath(path, fileType, str1, fileName,fileNa);
                        this.saveRedDetail(str2,str1);
                        this.updateSatus(str1);
                        this.updateSatus1(str2,id);
                        msg = "文件上传成功";
                    }
                } catch (Exception e) {
                    LOGGER.debug("----上传文件异常---" + e);
                    msg = "上传文件异常";
                } finally {
                    if (handler != null) {
                        handler.closeChannel();
                    }
                }
            } else {
                msg =  "文件已经存在";
            }
        } else {
            msg = "序列号不相等";
        }
        LOGGER.debug("----------------开红票资料上传完成--------------------");
        //return invoices;

        return msg;
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

//    private int saveFilePath(FileEntity fileEntity) {
//        return uploadScarletLetterDao.saveFilePathRed(fileEntity);
//    }

    private void saveFilePath(String s, String fileType,String fileNumber,String localFileName,String fileName) {
        uploadScarletLetterDao.saveFilePath(s,fileType,fileNumber,localFileName,fileName);

    }

    private int saveRedDetail(String redLetterNotice,String serialNumber) {
        return uploadScarletLetterDao.saveRedDetail(redLetterNotice,serialNumber);
    }


    /**
     * 修改红字通知单的状态
     * @param serialNumber
     */
    private int updateSatus(String serialNumber) {
        return uploadScarletLetterDao.updateStatus(serialNumber);
    }

    private int updateSatus1(String redLetterNotice,Integer  redNoticeAssociation) {
        return uploadScarletLetterDao.updateStatus1(redLetterNotice,redNoticeAssociation);
    }
    @Override
    public Integer getfileCount(String filename) {

        return uploadScarletLetterDao.getfileCount(filename);
    }
    @Override
    public List<UploadScarletLetterEntity> getfileName(Map<String, Object> map) {
        return uploadScarletLetterDao.getfileName(map);
    }
    @Override
    public ReportStatisticsEntity getfileNameCount(Map<String, Object> map) {

        return uploadScarletLetterDao.getfileNameCount(map);
    }



//    @Override
//    public void delete(String fileName) {
//        uploadScarletLetterDao.delete( fileName);
//    }

    @Override
    public void delete1(String redLetterNotice) {
        uploadScarletLetterDao.delete1( redLetterNotice);
    }

    @Override
    public int deleteRedData(Map<String, Object> para)  {
        com.xforceplus.wapp.common.utils.SFTPHandler handler = com.xforceplus.wapp.common.utils.SFTPHandler.getHandler(depositPath);

        String filename = para.get("localFileName").toString();
        try {
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            handler.deleteRemote(para.get("filePath").toString());
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            LOGGER.debug("远程删除失败！");
        }

        return uploadScarletLetterDao.delete(filename);

    }
    @Override
    public Integer getfileCount1(String serialNumber) {

        return uploadScarletLetterDao.getfileCount1(serialNumber);
    }

    public int updateStatus2(String serialNumber) {
        return uploadScarletLetterDao.updateStatus2(serialNumber);
    }


    @Override
    public List<UploadScarletLetterEntity> queryListAll(Map<String, Object> map) {

        return uploadScarletLetterDao.queryListAll(map);
    }
    @Override
    public List<UploadScarletLetterEntity> queryListAllExport(Map<String, Object> map) {
        return uploadScarletLetterDao.queryListAllExport(map);
    }
    @Override
     public String getRedNoticeNumber(String serialNumber){
        return uploadScarletLetterDao.getRedNoticeNumber(serialNumber);
    }
    @Override
    public List<UploadScarleQueryExcelEntity> toExcel(List<UploadScarletLetterEntity> list){
        List<UploadScarleQueryExcelEntity> list2=new ArrayList<>();
        for (UploadScarletLetterEntity ue:list) {
            UploadScarleQueryExcelEntity uq=new UploadScarleQueryExcelEntity();
            uq.setRownumber(ue.getRownumber());
            uq.setStore(ue.getStore());
            uq.setBuyerName(ue.getBuyerName());
            uq.setInvoiceType(ue.getInvoiceType());
            uq.setInvoiceAmount(ue.getInvoiceAmount().setScale(2, BigDecimal.ROUND_UP).toString());
            uq.setTaxRate(ue.getTaxRate().setScale(2, BigDecimal.ROUND_UP).toString()+"%");
            uq.setTaxAmount(ue.getTaxAmount().setScale(2, BigDecimal.ROUND_UP).toString());
            uq.setJvcode(ue.getJvCode());
            uq.setMakeoutDate(ue.getMakeoutDate());
            uq.setSerialNumber(ue.getSerialNumber());
            uq.setRedLetterNotice(ue.getRedLetterNotice());
            list2.add(uq);
        }
        return list2;
    }
    @Override
    public List<RedInvoiceListExcelEntity> toExcel2(List<UploadScarletLetterEntity> list){
        List<RedInvoiceListExcelEntity> list2=new ArrayList<>();
        for (UploadScarletLetterEntity ue:list) {
            RedInvoiceListExcelEntity uq=new RedInvoiceListExcelEntity();
            uq.setRownumber(ue.getRownumber());
            uq.setStore(ue.getStore());
            uq.setBuyerName(ue.getBuyerName());
            uq.setInvoiceType(ue.getInvoiceType());
            uq.setInvoiceAmount(ue.getInvoiceAmount().setScale(2, BigDecimal.ROUND_UP).toString());
            uq.setTaxRate(ue.getTaxRate().setScale(2, BigDecimal.ROUND_UP).toString()+"%");
            uq.setTaxAmount(ue.getTaxAmount().setScale(2, BigDecimal.ROUND_UP).toString());
            uq.setJvcode(ue.getJvCode());
            uq.setMakeoutDate(ue.getMakeoutDate());
            uq.setSpName("日用商品一批");
            uq.setRedLetterNotice(ue.getRedLetterNotice());
            list2.add(uq);
        }
        return list2;
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }
}
