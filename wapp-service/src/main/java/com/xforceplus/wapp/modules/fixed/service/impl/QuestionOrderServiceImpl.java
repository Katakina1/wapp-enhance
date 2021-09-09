package com.xforceplus.wapp.modules.fixed.service.impl;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xforceplus.wapp.common.utils.RMBUtils;
import com.xforceplus.wapp.common.utils.SFTPHandler;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.fixed.dao.QuestionOrderCheckDao;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.fixed.entity.QuestionOrderEntity;
import com.xforceplus.wapp.modules.fixed.service.QuestionOrderService;
import com.xforceplus.wapp.modules.job.service.PurchaseOrderService;
import com.xforceplus.wapp.modules.posuopei.entity.DetailEntity;
import com.xforceplus.wapp.modules.posuopei.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.posuopei.entity.InvoicesEntity;
import com.xforceplus.wapp.modules.posuopei.entity.QuestionPaperEntity;
import com.xforceplus.wapp.modules.posuopei.service.impl.MatchServiceImpl;

@Service("questionOrderService")
@Transactional
public class QuestionOrderServiceImpl implements QuestionOrderService{

	  private static final Logger LOGGER= getLogger(QuestionOrderServiceImpl.class);
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
    @Value("${filePathConstan.remoteFixedRootPath}")
    private String remoteRootPath;
    /**
     * 远程文件临时存放路径
     */
    @Value("${filePathConstan.remoteFixedTempRootPath}")
    private String remoteTempRootPath;
    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.localFixedRootPath}")
    private String localRootPath;
    @Autowired
    PurchaseOrderService purchaseOrderService;
	
	@Autowired
	private   QuestionOrderCheckDao questionOrderDao;

	@Override
	public List<QuestionOrderEntity> queryCheckOrderList(String schemaLabel,Map<String, Object> map) {
		// TODO Auto-generated method stub
		
		return questionOrderDao.queryCheckOrderList( schemaLabel,map);
	}

	@Override
	public Integer countOrders(String schemaLabel,Map<String, Object> map) {
		// TODO Auto-generated method stub
		return questionOrderDao.countOrders(schemaLabel,map);
	}

	@Override
	public List<InvoiceEntity> queryInvoice(String schemaLabel,
			Map<String, Object> map) {
		// TODO Auto-generated method stub
		return questionOrderDao.queryInvoice(schemaLabel, map);
	}

	@Override
	public List<OrderEntity> queryOrder(String schemaLabel,
			Map<String, Object> map) {
		// TODO Auto-generated method stub
		return questionOrderDao.queryOrder(schemaLabel, map);
	}
	public List<FileEntity> queryFileName(@Param("schemaLabel") String schemaLabel,@Param("map") Map<String, Object> map){
    	return questionOrderDao.queryFileName(schemaLabel, map);
    }

    /**
     * 获取机动车销售发票明细
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public DetailVehicleEntity getVehicleDetail(String schemaLabel, Long id)throws Exception{
        return questionOrderDao.getVehicleDetail(schemaLabel,id);
    }
    
    
    
    public void getInvoiceImageForAll(Long id, UserEntity user, HttpServletResponse response) {
        //获取图片实体
          FileEntity fileEntity = this.getFileImage(id);
        SFTPHandler handler = SFTPHandler.getHandler(remoteRootPath,localRootPath);
//        com.xforceplus.wapp.common.utils.SFTPHandler imageHandler = com.xforceplus.wapp.common.utils.SFTPHandler.getHandler(depositPath,remoteQuestionPaperFileRootPath);
        //SFTPHandler imageHandler = SFTPHandler.getHandler(depositPath);
        try {
            if (null != fileEntity) {
                handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                String userAccount = user.getLoginname();
                //response.setContentType("image/png");
               // String name = invoiceEntity.getScanId();
                String filePath = fileEntity.getFilePath();
                String fileName =  fileEntity.getFileName();
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
                handler.download(filePath, fileName);
                   File file = new File(handler.getLocalImageRootPath()+fileName);
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
            if (handler != null) {
                handler.closeChannel();
            }
        }
    }

    

    /**
     * 通过id 找文件实体
     * @param id
     * @return
     */
    private FileEntity getFileImage(Long id) {
         return  questionOrderDao.getFileImage(id);
    }
    /**
     * 获取转出信息
     * @param schemaLabel
     * @param uuid
     * @return
     */
    @Override
    public List<InvoicesEntity> getOutInfo(String schemaLabel, String uuid) {
        return questionOrderDao.getOutInfo(schemaLabel, uuid);
    }

    /**
     * 获取明细中抵账表销方购方明细信息
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public InvoicesEntity getDetailInfo(String schemaLabel, Long id) {
        InvoicesEntity invoiceEntity=questionOrderDao.getDetailInfo(schemaLabel,id);
        invoiceEntity.setStringTotalAmount(RMBUtils.getRMBCapitals(Math.round(invoiceEntity.getTotalAmount()*100)));
        return invoiceEntity;
    }
    /**
     * 获取明细表信息
     * @param schemaLabel
     * @param id
     * @return
     */
    @Override
    public List<DetailEntity> getInvoiceDetail(String schemaLabel, Long  id) {
        List<DetailEntity> result  = questionOrderDao.getInvoiceDetail(schemaLabel,id);
        return result;
    }

	@Override
	public FileEntity getFileInfo(Long id) {
		// TODO Auto-generated method stub
		return questionOrderDao.getFileInfo(id);
	}
	
	
	  @Override
	    public void downloadFile(String filePath, String fileName, HttpServletResponse response) {
	        SFTPHandler handler = SFTPHandler.getHandler(remoteRootPath,localRootPath);
	        try {
	            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
	            response.reset();
	            //设置响应头
                fileName = new String(fileName.getBytes("utf-8"),"iso-8859-1");
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

	    @Override
	    public Boolean check(Map<String,Object> param) {
	        Boolean yesornor = questionOrderDao.check(param)>0;
	       /* if(yesornor){
	            //QuestionPaperEntity 
	            
	            QuestionOrderEntity entity = questionOrderDao.getPaperById(param);
	            purchaseOrderService.sendSingle02(0,entity);
	        }*/
	        return yesornor;
	    }
    
}
