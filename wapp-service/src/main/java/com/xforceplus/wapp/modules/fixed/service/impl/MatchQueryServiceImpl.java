package com.xforceplus.wapp.modules.fixed.service.impl;

import com.xforceplus.wapp.common.utils.RMBUtils;
import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoiceDetail;
import com.xforceplus.wapp.modules.fixed.dao.MatchQueryDao;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.MatchQueryEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.fixed.service.MatchQueryService;
import com.xforceplus.wapp.modules.posuopei.entity.DetailEntity;
import com.xforceplus.wapp.modules.posuopei.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.posuopei.entity.InvoicesEntity;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@Transactional
public class MatchQueryServiceImpl implements MatchQueryService {
    private static final Logger LOGGER= getLogger(MatchQueryServiceImpl.class);
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
    @Value("${filePathConstan.remoteQuestionPaperFileRootPath}")
    private String remoteQuestionPaperFileRootPath;
    /**
     * 远程文件临时存放路径
     */
    @Value("${filePathConstan.remoteQuestionPaperFileTempRootPath}")
    private String remoteQuestionPaperFileTempRootPath;
    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.localImageRootPath}")
    private String localImageRootPath;

    @Autowired
    MatchQueryDao matchQueryDao;


    @Override
    public List<MatchQueryEntity> querylist(Map<String, Object> map) {

        return matchQueryDao.querylist(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return matchQueryDao.queryTotal(map);
    }

    @Override
    public List<String> queryDetail(String uuid) {
        return matchQueryDao.queryDetail(uuid);
    }

    @Override
    public void cancelMatch(MatchQueryEntity entity) {
            List<Long> orderid = matchQueryDao.queryOrderid(entity);
            List<String> uuid = matchQueryDao.queryInvoiceNo(entity);
//            matchQueryDao.cancelMatchStatus(entity);
        matchQueryDao.delMatch(entity);
            matchQueryDao.cancelOrderStatus(orderid);
            matchQueryDao.cancelInvoiceStatus(uuid);
    }

    /**
     * 获取明细中抵账表销方购方明细信息
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public InvoicesEntity getDetailInfo(String schemaLabel, Long id) {
        InvoicesEntity invoiceEntity = matchQueryDao.getDetailInfo(schemaLabel, id);
        invoiceEntity.setStringTotalAmount(RMBUtils.getRMBCapitals(Math.round(invoiceEntity.getTotalAmount() * 100)));
        return invoiceEntity;
    }

    /**
     * 获取转出信息
     * @param schemaLabel
     * @param uuid
     * @return
     */
    @Override
    public List<InvoicesEntity> getOutInfo(String schemaLabel, String uuid) {
        return matchQueryDao.getOutInfo(schemaLabel, uuid);
    }

    /**
     * 获取机动车销售发票明细
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public DetailVehicleEntity getVehicleDetail(String schemaLabel, Long id)throws Exception{
        return matchQueryDao.getVehicleDetail(schemaLabel,id);
    }

    /**
     * 获取明细表信息
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public List<DetailEntity> getInvoiceDetail(String schemaLabel, Long  id) {
        List<DetailEntity> result  = matchQueryDao.getInvoiceDetail(schemaLabel,id);
        return result;
    }

    @Override
    public List<RecordInvoiceEntity> getDetailInvoice(Long matchId) {
        return matchQueryDao.getDetailInvoice(matchId);
    }

    @Override
    public List<OrderEntity> getDetailOrder(Long matchId) {
        return matchQueryDao.getDetailOrder(matchId);
    }

    @Override
    public List<RecordInvoiceDetail> exportDetailInvoice(Map<String, Object> params) {
        return matchQueryDao.exportDetailInvoice(params);
    }

    public List<FileEntity> queryFileName(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map){
        return matchQueryDao.queryFileName(schemaLabel, map);
    }

    /**
     * 通过id 找文件实体
     * @param id
     * @return
     */
    private FileEntity getFileImage(Long id) {
        return  matchQueryDao.getFileImage(id);
    }

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
                String endname="";
                //response.setHeader("image/png", "attachment; filename=" + java.net.URLEncoder.encode(name, "UTF-8"));
                if(fileEntity.getFilePath().endsWith("png")){
                    //response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    response.setHeader("Content-Type","image/png");
                    endname="png";
                }
                if(fileEntity.getFilePath().endsWith("pdf")){
                    //response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    response.setHeader("Content-Type","application/adobe-pdf");
                    endname="pdf";
                }
                if(fileEntity.getFilePath().endsWith("jpeg")){
                    //response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    response.setHeader("Content-Type","image/jpeg");
                    endname="jpeg";
                }
                if(fileEntity.getFilePath().endsWith("jpg")){
                    //response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    response.setHeader("Content-Type","image/jpg");
                    endname="jpg";
                }
                if(fileEntity.getFilePath().endsWith("gif")){
                    //response.addHeader("Content-Disposition", "attachment;filename=" + name);
                    response.setHeader("Content-Type","image/gif");
                    endname="gif";
                }
                fileEntity.setFileType(endname);
                OutputStream output = response.getOutputStream();
                InputStream is = imageHandler.getInputStream(fileEntity.getFilePath());
                BufferedImage bi = ImageIO.read(is);
                ImageIO.write(bi, endname, output);
                output.flush();
                is.close();
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
    public FileEntity getFileInfo(Long id) {
        // TODO Auto-generated method stub
        return matchQueryDao.getFileInfo(id);
    }

    @Override
    public void downloadFile(String filePath, String fileName, HttpServletResponse response) {
        SFTPHandler handler = SFTPHandler.getHandler(localImageRootPath, localImageRootPath);
        try {
            fileName = new String(fileName.getBytes("utf-8"),"iso-8859-1");
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            response.reset();
            //设置响应头
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            OutputStream output = response.getOutputStream();
            handler.download(filePath, fileName);
            File file = new File(handler.getLocalImageRootPath()+fileName);
            FileInputStream in = new FileInputStream(file);// 获取实体类对应Byte
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                output.write(buf, 0, len);
            }
            output.flush();
            in.close();
            output.close();
        } catch (Exception e) {
            LOGGER.info("----下载文件异常--- {}",e);

        } finally {
            if (handler != null) {
                handler.closeChannel();
            }
        }
    }
}
