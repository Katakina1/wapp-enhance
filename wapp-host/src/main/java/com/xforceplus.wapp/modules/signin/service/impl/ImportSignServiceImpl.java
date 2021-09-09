package com.xforceplus.wapp.modules.signin.service.impl;

import static com.xforceplus.wapp.modules.Constant.*;
import static com.xforceplus.wapp.modules.signin.enumflord.InvoiceTypeEnum.invoiceTypeMap;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.service.OrganizationService;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckVehicleDetailModel;
import com.xforceplus.wapp.modules.collect.dao.NoDetailedInvoiceDao;
import com.xforceplus.wapp.modules.collect.entity.InvoiceDetailInfo;
import com.xforceplus.wapp.modules.collect.entity.RecordInvoiceStatistics;
import com.xforceplus.wapp.modules.collect.pojo.InvoiceDetail;
import com.xforceplus.wapp.modules.signin.entity.*;
import com.xforceplus.wapp.modules.signin.enumflord.InvoiceTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.IterableUtil;
import com.xforceplus.wapp.common.utils.ReturnInfoEnum;
import com.xforceplus.wapp.config.SystemConfig;
import com.xforceplus.wapp.modules.certification.dao.ImportCertificationDao;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.pojo.RequestData;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.einvoice.util.ImgCompressUtil;
import com.xforceplus.wapp.modules.einvoice.util.SFTPHandler;
import com.xforceplus.wapp.modules.einvoice.util.ZipUtil;
import com.xforceplus.wapp.modules.einvoice.util.ZipUtilRead;
import com.xforceplus.wapp.modules.signin.dao.SignImportDao;
import com.xforceplus.wapp.modules.signin.enumflord.SignInEnum;
import com.xforceplus.wapp.modules.signin.service.ImportSignService;

/**
 * 导入签收业务层实现
 *
 * @author Colin.hu
 * @date 4/23/2018
 */
@Transactional
@Service
public class ImportSignServiceImpl implements ImportSignService {

    private final static Logger LOGGER = getLogger(ImportSignServiceImpl.class);

    private final SignImportDao signImportDao;
    private final SystemConfig systemConfig;

    private static int sequence = 0;
    private static int length = 6;
    private static SimpleDateFormat simpleDateFormat=new SimpleDateFormat(SHORT_DATE_FORMAT);
   // private final OcrSerivce ocrSerivce;
    
    private final ImportCertificationDao importCertificationDao;

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private NoDetailedInvoiceDao noDetailedInvoiceDao;
    @Autowired
    public ImportSignServiceImpl(SignImportDao signImportDao,  SystemConfig systemConfig,
                                 ImportCertificationDao importCertificationDao) {
        this.signImportDao = signImportDao;
        this.systemConfig = systemConfig;

        this.importCertificationDao = importCertificationDao;
    }

    /**
     * A、导入excel文件，解析出文件中信息，根据解析出的发票代码判断发票类型
     * a、识别出专票、通行费发票、机动车发票时：根据识别出的发票代码和发票号码查询抵账表中的数据是否存在，
     * 如果存在则对比开票日期，未税金额、税额是否一致，一致则更新底账表中的签收状态（已签收）签收方式（导入签收）签收时间（当前时间），并将发票数据保存到扫描表中。
     * 如果不存在，将解析出来的发票信息存到扫描表中（签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
     * b、识别出普票时：根据识别出的发票代码、发票号码、开票日期、校验码来触发查验操作
     * 如果查验成功，则将获取到的发票全票面信息保存到底账表（签收状态（已签收）签收方式（导入签收）签收时间（当前时间））中，并保存发票信息到扫描表中（签收状态（签收成功）签收方式（导入签收）签收时间（当前时间））
     * 如果查验失败，则保存导入的发票信息到扫描表中以便使用签收处理菜单进行签收操作。
     * @throws Exception 
     */

    @Override
	public String onlyUploadImg(ExportEntity exportEntity,MultipartFile imgFile,Map<String, String> ocrMap) throws Exception {
        LOGGER.debug("----------------上传图片开始--------------------");
        //文件名  ---客戶端已经对文件名称做了处理
         final String newFileName = imgFile.getOriginalFilename();
        //上传文件重新命名
        //String newFileName = new SimpleDateFormat(LONG_DATE_FORMAT).format(new Date()) + imgFile.getOriginalFilename().substring(imgFile.getOriginalFilename().indexOf("."));
        //创建临时存储文件夹+buyerTaxNo+"/"
        this.createFileDir();
        File newFile = new File(systemConfig.getTempPath() + newFileName);
        
        String [] returnVale = null ;
        String errMsg = null;
        
        try {
            imgFile.transferTo(newFile);
           
            List<File> files = newArrayList();

            files.add(newFile);
            
            // 保存图片
            returnVale =  this.onlyUploadFile(exportEntity,files,ocrMap);
            LOGGER.debug("----------------图片导入完成--------------------");
            return returnVale[0];
           
        }catch (Exception e) {
            LOGGER.error("图片处理失败:{}", e);
            errMsg = e.getMessage();
        } finally {
        	try{
	            //清空临时文件里面的当前文件
	             File file = new File(systemConfig.getTempPath());
	             File[] list = file.listFiles();
	             for (int i = 0; i < list.length; i++) {
	                 if (list[i].isFile() && list[i].getName().endsWith(newFileName)) {
	                     list[i].delete();
	                 }
	                 if (list[i].isFile() && list[i].getName().endsWith(returnVale[1]+".zip")) {
	                     list[i].delete();
	                 }
	             }
        	}catch(Exception e){
        		LOGGER.error("清空临时文件里面的当前文件时发生异常:", e);
        	}
        }
        
        throw new Exception(errMsg);
    }

    @Override
    public String getInvoiceImage(Map<String, String> params) {
        final String invoiceImage = signImportDao.getImgPath(params);
        SFTPHandler imageHandler = SFTPHandler.getHandler(systemConfig.getRemoteImageRootPath(), systemConfig.getLocalImageRootPath());
      //默认文件名
        final String userAccount = UUID.randomUUID().toString().replace("-", "");
        try {
            if (!StringUtils.isEmpty(invoiceImage)) {
                imageHandler.openChannel(systemConfig.getHost(), systemConfig.getUserName(), systemConfig.getPassword(), Integer.parseInt(systemConfig.getDefaultPort()), Integer.parseInt(systemConfig.getDefaultTimeout()));
                
                
                imageHandler.download(invoiceImage, userAccount + FILE_TYPE_ZIP);

                final byte[] zipFile = ZipUtilRead.readZipFile(imageHandler.getLocalImageRootPath() + userAccount + ".zip");

                return org.apache.commons.codec.binary.Base64.encodeBase64String(zipFile);
            }
        } catch (Exception e) {
            LOGGER.info("获取图片失败:{}", e);
        } finally {
        	
            //清空临时文件里面的当前文件
             File file = new File(imageHandler.getLocalImageRootPath());
             File[] list = file.listFiles();
             for (int i = 0; i < list.length; i++) {
                 
                 if (list[i].isFile() && list[i].getName().endsWith(userAccount+".zip")) {
                     list[i].delete();
                 }
             }
                 
            imageHandler.closeChannel();
        }
        return "";
    }
    
    @Override
    @Transactional
    public String deleteInvoiceImage(Map<String, String> params) {
        final String invoiceImage = signImportDao.getImgPath(params);
        SFTPHandler imageHandler = SFTPHandler.getHandler(systemConfig.getRemoteImageRootPath(), systemConfig.getLocalImageRootPath());
        try {
            if (!StringUtils.isEmpty(invoiceImage)) {
                imageHandler.openChannel(systemConfig.getHost(), systemConfig.getUserName(), systemConfig.getPassword(), Integer.parseInt(systemConfig.getDefaultPort()), Integer.parseInt(systemConfig.getDefaultTimeout()));
                
                imageHandler.deleteRemote(invoiceImage);
                //还需要把对应的数据库记录给删掉，等会写
                signImportDao.deleteInvoiceImg(params);
                
               
                return invoiceImage;
            }
        } catch (Exception e) {
            LOGGER.info("删除图片失败:{}", e);
        } finally {
            imageHandler.closeChannel();
        }
        return "";
    }









    /**
     * 构建扫描表实体
     *
     * @param exportEntity    包含人员信息实体
     * @param responseInvoice 查验响应实体
     * @param entity          导入实体
     * @return 扫描实体
     */
    private InvoiceScan buildInvoiceScan(ExportEntity exportEntity, ResponseInvoice responseInvoice, RecordInvoiceEntity entity) {
        //定义返回值
        final InvoiceScan invoiceScan = new InvoiceScan();
        //人员名
        invoiceScan.setUserName(exportEntity.getUserName());
        //人员帐号
        invoiceScan.setUserAccount(exportEntity.getUserAccount());
        //校验码
        invoiceScan.setCheckCode(responseInvoice.getCheckCode());
        //发票类型
        invoiceScan.setInvoiceType(responseInvoice.getInvoiceType());
        //发票代码
        invoiceScan.setInvoiceCode(responseInvoice.getInvoiceCode());
        //发票号码
        invoiceScan.setInvoiceNo(responseInvoice.getInvoiceNo());
        //金额
        invoiceScan.setInvoiceAmount(bigToStr(responseInvoice.getInvoiceAmount()));
        //开票时间
        invoiceScan.setInvoiceDate(responseInvoice.getInvoiceDate());
        //签收方式--导入签收
        invoiceScan.setQsType(SignInEnum.QS_LEADING_IN.getValue());
        //'签收结果(0-签收失败 1-签收成功）'
        invoiceScan.setInvoiceStatus(SignInEnum.QS_SUCCESS.getValue());
        //签收描述
        invoiceScan.setNotes(entity.getNotes());
        //是否有效（1-有效 0-无效）默认1
        invoiceScan.setValid("1");
        if ("N".equals(responseInvoice.getIsCancelled())) {
            invoiceScan.setInvoiceStatus("0");
        } else if("Y".equals(responseInvoice.getIsCancelled())){
            invoiceScan.setInvoiceStatus("2");
        }else if("0".equals(responseInvoice.getIsCancelled())){
            invoiceScan.setInvoiceStatus("0");
        }else if("3".equals(responseInvoice.getIsCancelled())){
            invoiceScan.setInvoiceStatus("3");
        }else if("2".equals(responseInvoice.getIsCancelled())){
            invoiceScan.setInvoiceStatus("2");
        }
        //uuid
        invoiceScan.setUuId(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());

        //购方税号
        invoiceScan.setGfTaxNo(responseInvoice.getBuyerTaxNo());
        //购方名称
        invoiceScan.setGfName(responseInvoice.getBuyerName());
        //销方税号
        invoiceScan.setXfTaxNo(responseInvoice.getSalerTaxNo());
        //销方名称
        invoiceScan.setXfName(responseInvoice.getSalerName());
        //税额
        invoiceScan.setTaxAmount(bigToStr(responseInvoice.getTaxAmount()));
        //价税合计
        invoiceScan.setTotalAmount(bigToStr(responseInvoice.getTotalAmount()));
        invoiceScan.setQsDate(new Date().toString());
        return invoiceScan;
    }



    /**
     * 创建文件夹
     */
    private void createFileDir() {
        //创建临时存储文件夹
        File tempFileDir = new File(systemConfig.getTempPath());
        if (!tempFileDir.exists()) {
            tempFileDir.mkdir();
        }
        //创建压缩包存储文件夹
        File zipFileDir = new File(systemConfig.getDepositPath());
        if (!zipFileDir.exists()) {
            zipFileDir.mkdir();
        }
    }
    


    

    
   /**
    * 
    * @param files
    * @return
 * @throws Exception 
    */
    @Transactional
    public String[] onlyUploadFile(ExportEntity exportEntity,List<File> files,Map<String, String> ocrMap) throws Exception {
       
    	 //图片map集
        final List<Map<String, String>> imgMapList = newArrayList();
    	
        
        int x=1+(int)(Math.random()*10) ;        
        
        try {
        	//随机休息100-2000毫秒
			Thread.sleep(x*200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        //连接远程sftp服务器 
        SFTPHandler handler = SFTPHandler.getHandler(systemConfig.getRemoteImageRootPath(), systemConfig.getTempPath());

        //连接远程服务器
        handler.openChannel(systemConfig.getHost(), systemConfig.getUserName(), systemConfig.getPassword(), Integer.parseInt(systemConfig.getDefaultPort()), Integer.parseInt(systemConfig.getDefaultTimeout()));
       
        String uploadImg = null;
        
        String [] returnVale = new String [2];
        
        //默认ocr识别成功
        if (true) {
        
	        for (File file : files) {
	        	
	           final String stringUuid = UUID.randomUUID().toString().replace("-", "");
	        	
	           // final String scanId = UUID.randomUUID().toString().replace("-", "");
	           
	          
	            final String scanId =ocrMap.get("scanId");
	        	
	            File zipImgFile = zipImg(systemConfig.getTempPath() + file.getName());
	            
//	            ocrMap.get("gfTaxNo")!=null &&!"".equals(ocrMap.get("gfTaxNo"))
//	            		&& ocrMap.get("invoiceDate")!=null &&!"".equals(ocrMap.get("invoiceDate"))
//	            		&& ocrMap.get("invoiceCode")!=null &&!"".equals(ocrMap.get("invoiceCode"))
//	            		&& ocrMap.get("invoiceNo")!=null &&!"".equals(ocrMap.get("invoiceNo"))
	            
	            if(ocrMap.get("isImage")!=null && ocrMap.get("isImage").equals("1") ){//"1"说明是发票
	                //将图片上传ftp
	            	uploadImg = handler.onlyUploadImg(ocrMap.get("gfTaxNo"), ocrMap.get("invoiceDate"), zipImgFile.getName());
	                returnVale[0] = ocrMap.get("invoiceCode")+ocrMap.get("invoiceNo");
	            }else
	            {
	            	String  str1 = stringUuid.substring(0,15);
	            	String  str2 = stringUuid.substring(15,30);
	            	
	            	uploadImg =  handler.onlyUploadImg(str1, str2, zipImgFile.getName());
	            	returnVale[0] = stringUuid.substring(0, 30);
	            }
	            
	            
	           returnVale[1] = zipImgFile.getName().substring(zipImgFile.getName().indexOf("_")+1, zipImgFile.getName().indexOf("."));
	           //final Map<String, String> imgMap = buildInvoiceImgMap(uploadImg + zipImgFile.getName(), record.getInvoiceCode() + record.getInvoiceNo(), scanId);
	           //buyerTaxNo+invoiceDate  组成了t_dx_invoice_img表的uuid，这样可以覆盖掉之前的,scanId一样会更新之前的记录
	            Map<String, String> imgMap ;
	            if(ocrMap.get("isImage")!=null && ocrMap.get("isImage").equals("1") )//"1"说明是发票
	            {
	            	imgMap = buildInvoiceImgMap(uploadImg + zipImgFile.getName(), ocrMap.get("invoiceCode")+ocrMap.get("invoiceNo"), scanId);
	            	  
	            }else{
	            	
	                 imgMap = buildInvoiceImgMap(uploadImg + zipImgFile.getName(), stringUuid.substring(0, 30), scanId);
	            }
                
	          imgMap.put("seqNo", ocrMap.get("seqNo"));
          	  imgMap.put("arrayName", ocrMap.get("arrayName"));
          	  imgMap.put("isImage", ocrMap.get("isImage"));
          	  
          	  //设置扫描点和票据类型
          	  imgMap.put("scanPoint", ocrMap.get("scanPoint"));
          	  imgMap.put("billtypeCode", ocrMap.get("billtypeCode"));
          	  imgMap.put("userId", exportEntity.getUserId().toString());
          	  
//          	  Long oldUserId = signImportDao.queryImgUserid(exportEntity.getSchemaLabel(), imgMap.get("uuid"));
//	          if(oldUserId != null && oldUserId.toString().equals(imgMap.get("userId")) == false){
//	        	  throw new Exception("此张发票已由其他用户上传！");
//	          }
          	  
          	  imgMapList.add(imgMap);	          
	        }
        }
        
         Boolean flag=true;
        for ( Map<String, String> imgMap:imgMapList) {
//            flag = signImportDao.insertInvoiceImgforCustomer(exportEntity.getSchemaLabel(), imgMapList) > 0;
            flag = signImportDao.insertInvoiceImgforCustomerOne(exportEntity.getSchemaLabel(), imgMap) > 0;
        }
        if (!flag) {
      	  throw new Exception("图片数据保存失败!");
        }
        
        //保存发票图片
        if (imgMapList.isEmpty()) {
            throw new Exception("ocr获取数据失败");
        }
        LOGGER.info("插入图片数据:{}", imgMapList.toString());
        
        return returnVale;
    }
    

    /**
     * 将ocr返回信息构建签收实体
     *
     * @param map ocr返回的 map
     * @return 发票签收信息
     */
    private RecordInvoiceEntity buildRecordByMap(Map<String, String> map, String scanId) {
        //定义返回值
        final RecordInvoiceEntity entity = new RecordInvoiceEntity();
        entity.setImportType("1");
        //发票代码
        entity.setInvoiceCode(map.get("invoiceCode"));
        //发票号码
        entity.setInvoiceNo(map.get("invoiceNo"));
        //金额
        if (StringUtils.isNotEmpty(map.get("invoiceAmount"))) {
            entity.setInvoiceAmount(new BigDecimal(map.get("invoiceAmount")));
        } else {
            entity.setInvoiceAmount(null);
        }
        //开票日期
        entity.setInvoiceDate(formatterDate(map.get("invoiceDate")));
        //校验码
        entity.setCheckCode(map.get("verifyCode"));
        //购方税号
        entity.setGfTaxNo(map.get("buyerTaxNo"));
        //图片唯一识别号
        entity.setScanId(scanId);
        //销方税号
        entity.setXfTaxNo(map.get("salerTaxNo"));
        //税额
        if (StringUtils.isNotEmpty(map.get("taxAmount"))) {
            entity.setTaxAmount(new BigDecimal(map.get("taxAmount")));
        } else {
            entity.setTaxAmount(null);
        }
        //价税合计
        if (StringUtils.isNotEmpty(map.get("totalAmount"))) {
            entity.setTotalAmount(/*Double.valueOf*/ new BigDecimal(map.get("totalAmount")));// double - bigdecimal  1/7
        } else {
            entity.setTotalAmount(null);
        }
        //返回
        return entity;
    }

    /**
     * 构建发票图片map
     *
     * @param imagePath 图片路径
     * @param uuid      发票代码 + 发票号码
     * @param scanId    扫描id 自动生成
     * @return 发票图片map
     */
    private Map<String, String> buildInvoiceImgMap(String imagePath, String uuid, String scanId) {
        //定义返回值
        final Map<String, String> map = newHashMap();
        //图片路径
        map.put("imagePath", imagePath);
        //uuid
        map.put("uuid", uuid);
        //扫描id
        map.put("scanId", scanId);
        //返回map
        return map;
    }

    private Date formatterDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(SHORT_DATE_FORMAT);
        try {
            return sdf.parse(date.replaceAll("-", ""));
        } catch (Exception e) {
            LOGGER.error("数据转换失败：{}", e);
            return null;
        }
    }

    /**
     * 图片压缩
     *
     * @param imgPath 图片全路径包括后缀
     * @return 返回 zip压缩文件
     * @throws IOException 异常
     */
    private File zipImg(String imgPath) throws IOException {
    	LOGGER.info("zipImg:" + imgPath);
        final ImgCompressUtil img = new ImgCompressUtil(imgPath);
        if (img.aspectRatio()) {
            img.resize(2840, 1656, imgPath);
        } else {
            img.resize(1656, 2840, imgPath);
        }
        final File imgFile = new File(imgPath);
        final String path = new SimpleDateFormat(SHORT_DATE_FORMAT).format(new Date()) + "_" + UUID.randomUUID().toString().replace("-", "") + ".zip";
        return ZipUtil.zip(imgFile, path);
    }



    /**
     * 判断uuid是否重复
     *
     * @param map  保存uuid的map key：uuid value:index
     * @param uuid 发票代码 + 发票号码
     * @return false 没有重复 true 重复
     */
    private Boolean checkRepeat(Map<String, Integer> map, String uuid) {
        if (map.get(uuid) == null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 遍历对重复发票单独赋值
     *
     * @param recordInvoiceEntityList 集合
     * @return 返回集合
     */
    private List<RecordInvoiceEntity> repeatMap(List<RecordInvoiceEntity> recordInvoiceEntityList) {
        //只要uuid不重复 每执行一次都将以uuid为key 以元素位置index为value 放入map
        final Map<String, Integer> integerMap = newHashMap();
        IterableUtil.forEach(recordInvoiceEntityList, (index, recordInvoiceEntity) -> {
            //uuid
            final String uuid = recordInvoiceEntity.getInvoiceCode() + recordInvoiceEntity.getInvoiceNo();
            final Boolean repeat = checkRepeat(integerMap, uuid);
            if (repeat) {
                //存在 放入对应集合
                recordInvoiceEntity.setNotes("发票已存在");
                recordInvoiceEntity.setRepeatFlag("1");
                //并更改元有的为发票已存在
                final RecordInvoiceEntity entity = recordInvoiceEntityList.get(integerMap.get(uuid));
                if (!"1".equals(entity.getRepeatFlag())) {
                    entity.setNotes("发票已存在");
                    entity.setRepeatFlag("1");
                }
            } else {
                //不存在则放入integerMap
                integerMap.put(uuid, index);
            }
        });
        return recordInvoiceEntityList;
    }

    private String bigToStr(Object value) {
        if(value != null && StringUtils.isNotEmpty(String.valueOf(value))) {
            return String.valueOf(value);
        }
        return null;
    }

	@Override
	public InvoiceCollectionInfo queryInvoiceInfo(String schemaLabel, String invoiceNo, String invoiceCode) {
		// TODO Auto-generated method stub
		return importCertificationDao.queryInvoiceInfo(schemaLabel, invoiceNo, invoiceCode);
	}

	



	@Override
	public Integer checkScanPoint(String schemaLabel, String userId, String scanPoint) {
		// TODO Auto-generated method stub
		return importCertificationDao.checkScanPoint(schemaLabel, userId, scanPoint);
	}

	@Override
	public Integer checkbilltypeCode(String schemaLabel, String userId, String billtypeCode) {
		// TODO Auto-generated method stub
		return importCertificationDao.checkBilltypeCode(schemaLabel, userId, billtypeCode);
	}

    /**
     * 转换
     * @param ocrMap
     * @return
     */
    @Override
    public List<RecordInvoiceEntity> toSignInvoiceVo(Map<String, String> ocrMap,String scanPathId){
        List<RecordInvoiceEntity> invoiceVos = new ArrayList<RecordInvoiceEntity>();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        try{
                RecordInvoiceEntity invoiceVo = new RecordInvoiceEntity();
                //文件类型
                String isImage=StringUtils.isBlank(ocrMap.get("isImage"))?"":ocrMap.get("isImage");
                invoiceVo.setFileType(isImage);
                invoiceVo.setBilltypeCode(ocrMap.get("billtypeCode"));
                try{
                    invoiceVo.setXfName(ocrMap.get("xfName"));
                }  catch (Exception e) {
                    invoiceVo.setXfName(null);
                }
                invoiceVo.setXfTaxNo(ocrMap.get("xfTaxNo"));
                //如果是发票，并且有销方税号，设置供应商代码
                if(isImage.equals("1")&&StringUtils.isNotBlank(invoiceVo.getXfTaxNo())){
                    //根据销方税号获取机构表orgType=8的orgcode
                    OrganizationEntity organizationEntity=new OrganizationEntity();
                    organizationEntity.setOrgtype("8");
                    organizationEntity.setTaxno(invoiceVo.getXfTaxNo());
                    List<OrganizationEntity> organizationEntitys=organizationService.queryList(null,organizationEntity);
                    if(organizationEntitys!=null&&organizationEntitys.size()==1){
                        invoiceVo.setVenderid(organizationEntitys.get(0).getOrgcode());
                    }



                }
                //生成序列号
                invoiceVo.setLocalTrmSeqNum(getLocalTrmSeqNum());
                invoiceVo.setInvoiceCode(ocrMap.get("invoiceCode"));
                invoiceVo.setInvoiceNo(ocrMap.get("invoiceNo"));
                invoiceVo.setDyInvoiceCode(ocrMap.get("dyInvoiceCode"));
                invoiceVo.setDyInvoiceNo(ocrMap.get("dyInvoiceNo"));
                invoiceVo.setCheckCode(ocrMap.get("checkCode"));
                try {
                    invoiceVo.setInvoiceDate(simpleDateFormat.parse(ocrMap.get("invoiceDate")));
                }catch (Exception e){
                    invoiceVo.setInvoiceDate(null);
                }

                if(ocrMap.get("fplx").contains("专票")){
                    invoiceVo.setInvoiceType("01");
                }else if(ocrMap.get("fplx").contains("普票")){
                    invoiceVo.setInvoiceType("04");
                }else if(ocrMap.get("fplx").contains("电票")){
                    invoiceVo.setInvoiceType("10");
                }else if(StringUtils.isBlank(ocrMap.get("fpxl"))){
                    invoiceVo.setInvoiceType("10");
                }else{
                    invoiceVo.setInvoiceType("10");
                }
                invoiceVo.setCheckCode(ocrMap.get("checkCode"));

                try {
                    invoiceVo.setInvoiceAmount(new BigDecimal(ocrMap.get("invoiceAmount")));
                } catch (Exception e) {
                    invoiceVo.setInvoiceAmount(null);
                }
                try {
                    invoiceVo.setTaxAmount(new BigDecimal(ocrMap.get("taxAmount")));
                } catch (Exception e) {
                    invoiceVo.setTaxAmount(null);
                }
                try {
                    invoiceVo.setTotalAmount(new BigDecimal(ocrMap.get("totalAmount"))/*.doubleValue()*/); // double - bigdecimal  2/7
                } catch (Exception e) {
                    invoiceVo.setTotalAmount(null);
                }
                try {
                    invoiceVo.setGfName(ocrMap.get("gfName"));
                }  catch (Exception e) {
                    invoiceVo.setGfName(null);
                }

                invoiceVo.setScanPathId(scanPathId);
                invoiceVo.setGfTaxNo(ocrMap.get("gfTaxNo"));

                invoiceVo.setScanId(ocrMap.get("scanId"));
                invoiceVo.setUuid(ocrMap.get("dyInvoiceCode")+ocrMap.get("dyInvoiceNo"));
                invoiceVos.add(invoiceVo);

        }catch(Exception e){
            e.printStackTrace();
        }
        return invoiceVos;
    }

    /**
     * YYYYMMDDHHMMSS+6位自增长码(20位)
     * @author shijing
     * 2015年6月29日下午1:25:23
     * @return
     */
    public static synchronized String getLocalTrmSeqNum() {
        sequence = sequence >= 999999 ? 1 : sequence + 1;
        String datetime =System.currentTimeMillis()+"";
        String s = Integer.toString(sequence);
        return datetime +addLeftZero(s, length);
    }

    /**
     * 左填0
     * @author shijing
     * 2015年6月29日下午1:24:32
     * @param s
     * @param length
     * @return
     */
    public static String addLeftZero(String s, int length) {
        // StringBuilder sb=new StringBuilder();
        int old = s.length();
        if (length > old) {
            char[] c = new char[length];
            char[] x = s.toCharArray();
            if (x.length > length) {
                throw new IllegalArgumentException(
                        "Numeric value is larger than intended length: " + s
                                + " LEN " + length);
            }
            int lim = c.length - x.length;
            for (int i = 0; i < lim; i++) {
                c[i] = '0';
            }
            System.arraycopy(x, 0, c, lim, x.length);
            return new String(c);
        }
        return s.substring(0, length);

    }

    @Override
    public Map<String,Object> getUpdateRecordInvoiceEntity(ExportEntity exportEntity, Map<String, Object> invoicess){
        List<RecordInvoiceEntity> recordInvoiceEntities=newArrayList();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("yyyyMMdd");
        RecordInvoiceEntity recordInvoiceEntity= signImportDao.getRecordInvoiceEntityById(exportEntity.getSchemaLabel(),Long.valueOf((Integer) invoicess.get("id")));
        if(recordInvoiceEntity!=null){
            recordInvoiceEntity.setDyInvoiceNo((String)invoicess.get("dyInvoiceNo"));
            recordInvoiceEntity.setBilltypeCode((String)invoicess.get("flowType"));
            recordInvoiceEntity.setDyInvoiceCode((String)invoicess.get("dyInvoiceCode"));
            recordInvoiceEntity.setInvoiceCode((String)invoicess.get("invoiceCode"));
            recordInvoiceEntity.setInvoiceNo((String)invoicess.get("invoiceNo"));
            if(null!=invoicess.get("venderid")&&!invoicess.get("venderid").equals("null")){
                recordInvoiceEntity.setVenderid((String)invoicess.get("venderid"));
            }
            try {
                recordInvoiceEntity.setInvoiceDate(simpleDateFormat.parse((String) invoicess.get("invoiceDate")));
            }catch (Exception e){
                try {
                    recordInvoiceEntity.setInvoiceDate(simpleDateFormat2.parse((String) invoicess.get("invoiceDate")));
                }catch (Exception e2){
                    recordInvoiceEntity.setInvoiceDate(new Date());
                }
            }
            recordInvoiceEntity.setGfTaxNo((String)invoicess.get("gfTaxNo"));
            recordInvoiceEntity.setGfName((String)invoicess.get("gfName"));
            recordInvoiceEntity.setXfTaxNo((String)invoicess.get("xfTaxNo"));
            try {
                recordInvoiceEntity.setInvoiceAmount(new BigDecimal((String)invoicess.get("invoiceAmount")));
            }catch (Exception e){
                recordInvoiceEntity.setInvoiceAmount(null);
            }
            try{
                recordInvoiceEntity.setTaxAmount(new BigDecimal((String)invoicess.get("taxAmount")));
            }catch (Exception e){
                recordInvoiceEntity.setTaxAmount(null);
            }
            try{
                BigDecimal bigDecimal=new BigDecimal((String)invoicess.get("totalAmount"));
                recordInvoiceEntity.setTotalAmount(bigDecimal/*.doubleValue()*/);// double - bigdecimal  3/7
            }catch (Exception e){
                try{
                    recordInvoiceEntity.setTotalAmount(/*(Double)*/new BigDecimal((String)invoicess.get("totalAmount")));
                    																				// double - bigdecimal  4/7
                }catch (Exception e2){
                    e2.printStackTrace();
                    recordInvoiceEntity.setTotalAmount(null);
                }
            }
            recordInvoiceEntity.setCheckCode((String)invoicess.get("checkCode"));
            recordInvoiceEntities.add(recordInvoiceEntity);
        }
        return signHandleService(exportEntity, repeatMap(recordInvoiceEntities), "0", Boolean.TRUE);
    }





    @Override
    public Map<String,Object> importSignExcel(ExportEntity exportEntity, List<RecordInvoiceEntity> recordInvoiceEntityList) throws RRException {



        //读取到的为空 则抛异常
        if (recordInvoiceEntityList.isEmpty()) {
            throw new RRException("读取无数据");
        }

        //返回 0 excel导入
        return signHandleService(exportEntity, repeatMap(recordInvoiceEntityList), "0", Boolean.FALSE);
    }

    /**
     * 将原有的签收集进行区分处理后并返回
     *
     * @param recordInvoiceEntityList 原有的签收集
     * @return 新的发票签收集
     */
    private Map<String,Object> signHandleService(ExportEntity exportEntity, List<RecordInvoiceEntity> recordInvoiceEntityList, String importType, Boolean modifyFlag) {

        //获取区分后的发票签收map
        final Map<String, List<RecordInvoiceEntity>> listMap = distinguishByInvoiceType(exportEntity, recordInvoiceEntityList, modifyFlag);


        //商品发票集
        final List<RecordInvoiceEntity> tomeInvoiceList = listMap.get("tomeInvoiceList");
//        //商品发票集
//        final List<RecordInvoiceEntity> costInvoiceList = listMap.get("costInvoiceList");

        //所有发票处理
        final Map<String,Object> tomeRecordInvoice = tomeInvoiceHandle(exportEntity, tomeInvoiceList, importType,modifyFlag);
//        //费用业务处理
//        final Map<String,Object> costRecordInvoice = costInvoiceHandle(exportEntity, costInvoiceList, importType);
        Map<String,Object> returnMap=new HashMap<>();


        returnMap.put("list",recordInvoiceEntityList);
        //返回
        return returnMap;
    }

    /**
     * 根据发票代码，发票号码 识别发票类型 并将识别出的专票、通行费发票、机动车发票单独放入一个数组
     *
     * @param exportEntity            导入实体
     * @param recordInvoiceEntityList 读取到的发票签收实体集
     * @param modifyFlag              是否为修改操作（true 是 false 不是）
     * @return 识别出专票、通行费发票、机动车发票集(01,03,14)和普票集(04,10,11)
     */
    private Map<String, List<RecordInvoiceEntity>> distinguishByInvoiceType(ExportEntity exportEntity, List<RecordInvoiceEntity> recordInvoiceEntityList, Boolean modifyFlag) {
        //定义返回值
        final Map<String, List<RecordInvoiceEntity>> map = newHashMapWithExpectedSize(5);

        //所有发票集
        final List<RecordInvoiceEntity> tomeInvoiceList = newArrayList();

//        //非商品发票集合
//        final List<RecordInvoiceEntity> costInvoiceList = newArrayList();

        //其他发票集
        final List<RecordInvoiceEntity> otherInvoiceList = newArrayList();
        //其他发票集入库集合
        final List<InvoiceScan> otherInvoiceScaList = newArrayList();

        //不符合处理条件的发票（数据不对）
        final List<RecordInvoiceEntity> errorDataList = newArrayList();
        //不符合处理条件的发票（数据不对）入库集合
        final List<InvoiceScan> errorInvoiceScanList = newArrayList();









        //遍历
        recordInvoiceEntityList.forEach((RecordInvoiceEntity recordInvoiceEntity) -> {
            //获取发票类型
            final String invoiceType = CommonUtil.getFplx(recordInvoiceEntity.getDyInvoiceCode());
            //赋值
            recordInvoiceEntity.setInvoiceType(invoiceType);
            //名
            recordInvoiceEntity.setInvoiceTypeName(invoiceTypeMap().get(invoiceType));


                final Boolean flag =recordInvoiceEntity.getFileType().equals("1");
                //判断是否为发票
                if (flag) {
                    if (checkListRecordInvoiceEntity(recordInvoiceEntity)) {
//                        if(recordInvoiceEntity.getBilltypeCode().equals("1")){
                            //放入发票集合
                            tomeInvoiceList.add(recordInvoiceEntity);
//                        }else{
//                            //放入非商品发票集合
//                            costInvoiceList.add(recordInvoiceEntity);
//                        }
                    } else {
                        errorDataList.add(recordInvoiceEntity);
                        errorInvoiceScanList.add(errorDataListToInvoiceScan(exportEntity,recordInvoiceEntity));
                        LOGGER.info("描述信息为:{}", recordInvoiceEntity.getNotes());
                    }
                } else {
                    //其他 放入其他集合
                    recordInvoiceEntity.setNotes("签收成功");
                    recordInvoiceEntity.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                    recordInvoiceEntity.setTypeErrorFlag("1");
                    otherInvoiceList.add(recordInvoiceEntity);
                    otherInvoiceScaList.add(otherInvoiceListToInvoiceScan(exportEntity,recordInvoiceEntity));

                }

        });
        if (!otherInvoiceScaList.isEmpty()) {
            getId(otherInvoiceScaList);
        }
        setId(otherInvoiceList,otherInvoiceScaList);


        if (!errorInvoiceScanList.isEmpty()) {
            getId(errorInvoiceScanList);
        }
        setId(errorDataList,errorInvoiceScanList);
        map.put("tomeInvoiceList", tomeInvoiceList);
//        map.put("costInvoiceList", costInvoiceList);
        return map;
    }


/*
    *//**
     * 签收处理
     *
     * @param recordInvoiceEntityList 发票集
     * @return 处理后的普票集
     *//*
    private Map<String,Object> costInvoiceHandle(ExportEntity exportEntity, List<RecordInvoiceEntity> recordInvoiceEntityList, String importType) {
        final RequestData requestData = new RequestData();

        //抵账表不存在，查验失败的发票信息
        final List<InvoiceScan> invoiceScanErrorList = newArrayList();

        //抵账表存在 但信息不一致(签收失败)
        final List<InvoiceScan> invoiceScanCheckErrorList = newArrayList();
        //抵账表存在 但信息一致(签收成功)
        final List<InvoiceScan> invoiceScanCheckSuccessList = newArrayList();

        //遍历
        recordInvoiceEntityList.forEach(entity -> {

            entity.setImportType(importType);
            //购方税号
            requestData.setBuyerTaxNo(systemConfig.getBuyerTaxNo());
            //发票类型
            requestData.setInvoiceType(entity.getInvoiceType());
            //发票代码
            requestData.setInvoiceCode(entity.getInvoiceCode());
            //发票号码
            requestData.setInvoiceNo(entity.getInvoiceNo());


            //开票时间
            final String invoiceDate = simpleDateFormat.format(entity.getInvoiceDate());
            requestData.setInvoiceDate(invoiceDate);
            //校验码
            requestData.setCheckCode(entity.getCheckCode());
            //金额
            if (entity.getInvoiceAmount() != null) {
                requestData.setInvoiceAmount(String.valueOf(entity.getInvoiceAmount()));
            } else {
                requestData.setInvoiceAmount(StringUtils.EMPTY);
            }

            //校验打印的代码号码
            if (StringUtils.isNotBlank(entity.getDyInvoiceNo()) && StringUtils.isNotBlank(entity.getInvoiceNo()) && StringUtils.isNotBlank(entity.getDyInvoiceCode()) && StringUtils.isNotBlank(entity.getInvoiceCode()) && (entity.getDyInvoiceCode() + "" + entity.getDyInvoiceNo()).equals(entity.getInvoiceCode() + "" + entity.getInvoiceNo())) {
                //获取准确底账
                RecordInvoiceDataEntity dataEntity = signImportDao.getRecordData(exportEntity.getSchemaLabel(), entity.getInvoiceCode(), entity.getInvoiceNo());
                if (dataEntity != null) {
                        String comCode = signImportDao.getComCode(dataEntity.getGfTaxNo());
                        if(StringUtils.isNotBlank(comCode)){
                            dataEntity.setCompanyCode(comCode);
                        }else{
                            dataEntity.setCompanyCode("");
                        }
                    //比对客户端跟底账的发票流程类型    不一致则签收失败
                    if (StringUtils.isNotBlank(entity.getBilltypeCode()) && StringUtils.isNotBlank(dataEntity.getFlowType())) {
                        if (entity.getBilltypeCode().equals(dataEntity.getFlowType())) {
//                    //不等于空时校验码为数据库中的校验码
//                    entity.setCheckCode(dataEntity.getCheckCode());
                            if (dataEntity.getSourceSystem().equals("2") || (dataEntity.getSourceSystem().equals("0") && dataEntity.getDetailYesorno().equals("0"))) {
                                //查验
                                final ResponseInvoice responseInvoice = invoiceCheckService.sendRequest(requestData);
                                //查验一致
                                if (responseInvoice != null && ReturnInfoEnum.CHECK_SUCCESS.getResultCode().equals(responseInvoice.getResultCode())) {
                                    //更改底账信息并更改来源
                                    //查验成功的底账集
                                    final List<ResponseInvoice> responseInvoiceList = newArrayList();
                                    responseInvoiceList.add(responseInvoice);
                                    //保存抵账表 抵账明细表， 抵账统计表
                                    if (dataEntity.getSourceSystem().equals("2")) {
                                        inspectionProcess(exportEntity.getSchemaLabel(), responseInvoiceList, true);
                                        //重新获取底账
                                        dataEntity = signImportDao.getRecordData(exportEntity.getSchemaLabel(), entity.getInvoiceCode(), entity.getInvoiceNo());
                                    } else {
                                        inspectionProcess(exportEntity.getSchemaLabel(), responseInvoiceList, false);
                                        dataEntity.setDetailYesorno("1");
                                    }

                                } else {
                                    //查验不一致 赋值 签收失败
                                    entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                    //失败描述
                                    entity.setNotes("查验失败");
                                    if (responseInvoice != null && StringUtils.isNotEmpty(responseInvoice.getResultTip())) {
                                        entity.setNotes(responseInvoice.getResultTip());
                                    }
                                    //将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                    final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                    //放入集合
                                    invoiceScanErrorList.add(invoiceScan);
                                }
                            }

                            //抵账表存在 则比对信息
                            final Boolean consistentFlag = checkInfoConsistent(entity, dataEntity);
                            if (consistentFlag) {
                                //对比一致 则签收成功 赋值 签收成功
                                entity.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                                entity.setNotes("签收成功");
                                //将解析出来的发票信息存到扫描表中 （签收状态（签收成功）签收方式（导入签收）签收时间当前时间）
                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.TRUE);
                                //放入集合
                                invoiceScanCheckSuccessList.add(invoiceScan);
                            } else {
                                //对比不一致 则签收失败 赋值 签收失败
                                entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                //将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.TRUE);
                                //放入集合
                                invoiceScanCheckErrorList.add(invoiceScan);
                            }
                        } else {
                            //对比不一致，签收失败 赋值     流程类型不匹配
                            entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                            entity.setNotes("发票流程类型不匹配");
                            final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                            //放入集合
                            invoiceScanErrorList.add(invoiceScan);
                        }
                    }else {
                        //对比不一致，签收失败 赋值     流程类型不匹配
                        entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                        entity.setNotes("发票流程类型不匹配");
                        final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                        //放入集合
                        invoiceScanErrorList.add(invoiceScan);
                    }
                } else {

                    //赋值 签收失败
                    entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                    //失败描述
                    entity.setNotes("签收失败，无抵账信息");
                    //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                    final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, new RecordInvoiceDataEntity(), entity, Boolean.FALSE);
                    //放入集合
                    invoiceScanErrorList.add(invoiceScan);
                }

            } else {
                if (!entity.getDyInvoiceCode().equals(entity.getInvoiceCode())) {
                    //打印的代码错误
                    //赋值 签收失败
                    entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                    //失败描述
                    entity.setNotes("打印代码与印刷代码不一致!");
                    //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                    final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, new RecordInvoiceDataEntity(), entity, Boolean.FALSE);
                    //放入集合
                    invoiceScanErrorList.add(invoiceScan);
                } else {
                    //打印的号码错误
                    //赋值 签收失败
                    entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                    //失败描述
                    entity.setNotes("打印号码与印刷号码不一致!");
                    //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                    final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, new RecordInvoiceDataEntity(), entity, Boolean.FALSE);
                    //放入集合
                    invoiceScanErrorList.add(invoiceScan);
                }

            }

        });

        //将抵账表存在 但信息不一致保存进扫描表
        if (!invoiceScanCheckErrorList.isEmpty()) {
//			signImportDao.insertScanInvoice( invoiceScanCheckErrorList);
            getId(invoiceScanCheckErrorList);
        }
        setId(recordInvoiceEntityList,invoiceScanCheckErrorList);
        //将抵账表存在 信息一致保存进扫描表 并更新抵账表，签收状态（已签收）签收方式（导入签收）签收时间（当前时间）
        if (!invoiceScanCheckSuccessList.isEmpty()) {
            //保存扫描表
//			signImportDao.insertScanInvoice( invoiceScanCheckSuccessList);
            getId(invoiceScanCheckSuccessList);
            //更新抵账表
            signImportDao.updateRecordQsStatus(exportEntity.getSchemaLabel(), invoiceScanCheckSuccessList);
        }
        setId(recordInvoiceEntityList,invoiceScanCheckSuccessList);


        //将校验失败的发票保存进扫描表
        if (!invoiceScanErrorList.isEmpty()) {
//			signImportDao.insertScanInvoice( invoiceScanErrorList);
            getId(invoiceScanErrorList);
        }
        setId(recordInvoiceEntityList,invoiceScanErrorList);

        Map<String,Object> returnMap=new HashMap();
        returnMap.put("list",recordInvoiceEntityList);
        returnMap.put("err",invoiceScanErrorList.size());
        return returnMap;
    }*/

    /**
     * 签收处理
     *
     * @param recordInvoiceEntityList 发票集
     * @param modifyFlag
     * @return 处理后的普票集
     */
    private Map<String,Object> tomeInvoiceHandle(ExportEntity exportEntity, List<RecordInvoiceEntity> recordInvoiceEntityList, String importType, Boolean modifyFlag) {
        final RequestData requestData = new RequestData();

        //抵账表不存在，查验失败的发票信息
        final List<InvoiceScan> invoiceScanErrorList = newArrayList();

        //抵账表存在 但信息不一致(签收失败)
        final List<InvoiceScan> invoiceScanCheckErrorList = newArrayList();
        //抵账表存在 但信息一致(签收成功)
        final List<InvoiceScan> invoiceScanCheckSuccessList = newArrayList();


        //人员下的所有税号、、更改机构表内所有购方税号
        List<String> taxNoList = signImportDao.getGfTaxNoList(exportEntity.getSchemaLabel());
        if (taxNoList == null) {
            taxNoList = newArrayList();
        }
        final List<String> finalTaxNoList = taxNoList;





        //遍历
        for (RecordInvoiceEntity entity:recordInvoiceEntityList){

            //取机构表中的 companyCode
            List<String> comCode = signImportDao.getComCode(entity.getGfTaxNo());
            if (comCode!=null&&comCode.size()>0) {
                entity.setCompanyCode(comCode.get(0));
            } else {
                entity.setCompanyCode("");
            }
            //取机构表的jvcode
            List<String> orgcode = signImportDao.getOrgCode(entity.getGfTaxNo());
            if (orgcode!=null&&orgcode.size()>0) {
                entity.setJvCode(orgcode.get(0));
            } else {
                entity.setJvCode("");
            }



            if(!modifyFlag||entity.getBilltypeCode().equals("4")) {
                //取机构表的venderid
                List<String> venderId = signImportDao.getVenderId(entity.getXfTaxNo(), entity.getBilltypeCode());
                if (venderId != null && venderId.size() > 0) {
                    entity.setVenderid(venderId.get(0));
                } else {
                    entity.setVenderid("");
                }
            }
            entity.setImportType(importType);
            //购方税号
            requestData.setBuyerTaxNo(systemConfig.getBuyerTaxNo());
            //发票类型
            requestData.setInvoiceType(entity.getInvoiceType());
            if(StringUtils.isNotBlank(entity.getDyInvoiceCode())){
             String fplx=   CommonUtil.getFplx(entity.getDyInvoiceCode());
             if(StringUtils.isNotBlank(fplx)){
                 entity.setInvoiceType(fplx);
             }
            }
            //发票代码
            requestData.setInvoiceCode(entity.getInvoiceCode());
            //发票号码
            requestData.setInvoiceNo(entity.getInvoiceNo());


            //开票时间
            final String invoiceDate = simpleDateFormat.format(entity.getInvoiceDate());
            requestData.setInvoiceDate(invoiceDate);
            //校验码
            requestData.setCheckCode(entity.getCheckCode());
            //金额
            if (entity.getInvoiceAmount() != null) {
                requestData.setInvoiceAmount(String.valueOf(entity.getInvoiceAmount()));
            } else {
                requestData.setInvoiceAmount(StringUtils.EMPTY);
            }

            //校验打印的代码号码
            if (StringUtils.isNotBlank(entity.getDyInvoiceNo()) && StringUtils.isNotBlank(entity.getInvoiceNo()) &&
                    StringUtils.isNotBlank(entity.getDyInvoiceCode()) &&
                    StringUtils.isNotBlank(entity.getInvoiceCode()) &&
                    (entity.getDyInvoiceCode() + "" + entity.getDyInvoiceNo()).equals(entity.getInvoiceCode() + "" + entity.getInvoiceNo())) {
                //获取准确底账
                RecordInvoiceDataEntity dataEntity = signImportDao.getRecordData(exportEntity.getSchemaLabel(), entity.getInvoiceCode()+ entity.getInvoiceNo());
                if (dataEntity != null||entity.getInvoiceType().equals("04")) {



                    //取机构表中的 companyCode
//                    List<String> comCode = signImportDao.getComCode(dataEntity.getGfTaxNo());
//                    if (comCode!=null&&comCode.size()>0) {
//                        dataEntity.setCompanyCode(comCode.get(0));
//                    } else {
//                        dataEntity.setCompanyCode("");
//                    }
                    if ((dataEntity==null&&"04".equals(entity.getInvoiceType()))||"2".equals(dataEntity.getSourceSystem()) || ("0".equals(dataEntity.getSourceSystem()) && "0".equals(dataEntity.getDetailYesorno()))) {
                        //查验
                        ResponseInvoice responseInvoice=null;
                        try{
                            responseInvoice = null;
                            //重新获取底账
                            //dataEntity = signImportDao.getRecordData(exportEntity.getSchemaLabel(), entity.getInvoiceCode()+entity.getInvoiceNo());
                        }catch (Exception e){
                            //赋值 签收失败
                            entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                            //失败描述
                            entity.setNotes("签收失败，查验失败");
                            //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                            final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, new RecordInvoiceDataEntity(), entity, Boolean.FALSE);
                            //放入集合
                            invoiceScanErrorList.add(invoiceScan);
                            break;
                        }
                        //查验一致
                        if (responseInvoice != null && ReturnInfoEnum.CHECK_SUCCESS.getResultCode().equals(responseInvoice.getResultCode())) {
                            //更改底账信息并更改来源
                            //查验成功的底账集
                            final List<ResponseInvoice> responseInvoiceList = newArrayList();
                            responseInvoiceList.add(responseInvoice);
                            //保存抵账表 抵账明细表， 抵账统计表
                            if(dataEntity==null){
                                //到这一步的都是普票无底账的，直接将查验结果入库
                                inspectionProcess(exportEntity.getSchemaLabel(), responseInvoiceList, true);
                                //重新获取底账
                                dataEntity = signImportDao.getRecordData(exportEntity.getSchemaLabel(), entity.getInvoiceCode()+ entity.getInvoiceNo());
                            }else if (dataEntity.getSourceSystem().equals("2")) {
                                //来源为录入的发票底账税额与查验税额做对比，（专票、非0税率普票）税额对比成功才覆盖
                                //根据查验回来数据判断是否为0税率普票
                                boolean isol=inspectionProcess2(responseInvoiceList);
                                if("01".equals(dataEntity.getInvoiceType())||("04".equals(dataEntity.getInvoiceType())&&!isol)){
                                    //对比税额
                                    if(dataEntity.getTaxAmount().compareTo(new BigDecimal(responseInvoice.getTaxAmount()))==0){
                                        dataEntity.setDetailYesorno("1");
                                        inspectionProcess(exportEntity.getSchemaLabel(), responseInvoiceList, true);
                                        //重新获取底账
                                        dataEntity = signImportDao.getRecordData(exportEntity.getSchemaLabel(), entity.getInvoiceCode()+ entity.getInvoiceNo());
                                    }else{
                                        //赋值 签收失败
                                        entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                        //失败描述
                                        entity.setNotes("签收失败，录入税额与查验税额不一致");
                                        //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                        final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, new RecordInvoiceDataEntity(), entity, Boolean.FALSE);
                                        //放入集合
                                        invoiceScanErrorList.add(invoiceScan);
                                        break;
                                    }

                                }else{
                                    dataEntity.setDetailYesorno("1");
                                    inspectionProcess(exportEntity.getSchemaLabel(), responseInvoiceList, true);
                                    //重新获取底账
                                    dataEntity = signImportDao.getRecordData(exportEntity.getSchemaLabel(), entity.getInvoiceCode()+ entity.getInvoiceNo());
                                }

                            }
                            else
                            {
                                dataEntity.setDetailYesorno("1");
                                inspectionProcess(exportEntity.getSchemaLabel(), responseInvoiceList, false);
                                //重新获取底账
                                dataEntity = signImportDao.getRecordData(exportEntity.getSchemaLabel(), entity.getInvoiceCode()+ entity.getInvoiceNo());

                            }

                        } else {
                            //查验不一致 赋值 签收失败
                            entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                            //失败描述
                            entity.setNotes("查验失败");
                            if (responseInvoice != null && StringUtils.isNotEmpty(responseInvoice.getResultTip())) {
                                entity.setNotes(responseInvoice.getResultTip());
                            }
                            //将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                            final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                            //放入集合
                            invoiceScanErrorList.add(invoiceScan);
                            break;
                        }
                    }
                    //如果不是修改，用底賬覆蓋掃描在做對比
                    if(!modifyFlag){
                        entity.setInvoiceAmount(dataEntity.getInvoiceAmount());
                        entity.setGfTaxNo(dataEntity.getGfTaxNo());
                        entity.setGfName(dataEntity.getGfName());
                        entity.setXfTaxNo(dataEntity.getXfTaxNo());
                        entity.setXfName(dataEntity.getXfName());
                        entity.setTaxAmount(dataEntity.getTaxAmount());
                        entity.setTotalAmount(dataEntity.getTotalAmount()/*.doubleValue()*/);// double - bigdecimal  5/7
                    }

                    //保存scanId到底账表
                    dataEntity.setScanningSeriano(entity.getScanId());
                    signImportDao.updateScanningSeriano(exportEntity.getSchemaLabel(),dataEntity);

                    //有底账时用底账的获取
                    //取机构表中的 companyCode
                    comCode = signImportDao.getComCode(entity.getGfTaxNo());
                    if (comCode!=null&&comCode.size()>0) {
                        entity.setCompanyCode(comCode.get(0));
                    } else {
                        entity.setCompanyCode("");
                    }
                    //取机构表的jvcode
                    orgcode = signImportDao.getOrgCode(entity.getGfTaxNo());
                    if (orgcode!=null&&orgcode.size()>0) {
                        entity.setJvCode(orgcode.get(0));
                    } else {
                        entity.setJvCode("");
                    }



                    if(!modifyFlag||entity.getBilltypeCode().equals("4")) {
                        //取机构表的venderid
                        List<String> venderId = signImportDao.getVenderId(entity.getXfTaxNo(), entity.getBilltypeCode());
                        if (venderId != null && venderId.size() > 0) {
                            entity.setVenderid(venderId.get(0));
                        } else {
                            entity.setVenderid("");
                        }
                    }


                    boolean isrukCJV=false;
                    if(StringUtils.isBlank(dataEntity.getCompanyCode())) {
                        dataEntity.setCompanyCode(entity.getCompanyCode());
                        isrukCJV=true;
                    }else{
                        entity.setCompanyCode(dataEntity.getCompanyCode());
                    }
                    //
                    if(StringUtils.isBlank(dataEntity.getJvcode())){
                        dataEntity.setJvcode(entity.getJvCode());
                        isrukCJV=true;
                    }else{
                        entity.setJvCode(dataEntity.getJvcode());
                    }
                    if(modifyFlag&&!entity.getBilltypeCode().equals("4")){
                        dataEntity.setVenderid(entity.getVenderid());
                        isrukCJV=true;
                    }else {
                        if (StringUtils.isBlank(dataEntity.getVenderid())) {
                            dataEntity.setVenderid(entity.getVenderid());
                            isrukCJV=true;
                        }else{
                            //在扫描表加标记，在修改处不让修改
                            entity.setVenderidEdit("1");
                            entity.setVenderid(dataEntity.getVenderid());
                        }
                    }
                    //入库
                    if(isrukCJV){
                        signImportDao.updateCJV(exportEntity.getSchemaLabel(),dataEntity);
                    }

                    //抵账表存在 则比对信息
                    final Boolean consistentFlag = checkInfoConsistent(entity, dataEntity,finalTaxNoList);


                    if (consistentFlag) {

                        //对比一致
                        //判断业务底账类型是否为空
                        if(StringUtils.isBlank(dataEntity.getFlowType())){
                            //如果选择业务类型是固定资产（5）、租赁（6）覆盖底账业务类型
                            if(entity.getBilltypeCode().equals("5")||entity.getBilltypeCode().equals("6")){
                                dataEntity.setFlowType(entity.getBilltypeCode());
                                //更新底账业务业务类型
                                signImportDao.updateFlowType(exportEntity.getSchemaLabel(),dataEntity);
                            }
                        }
                        //若此时底账业务类型还是空的，获取业务类型不一致，则签收失败，
                        if(StringUtils.isBlank(entity.getBilltypeCode())||!entity.getBilltypeCode().equals(dataEntity.getFlowType())){
                            //业务类型是否为空、判断所选类型与发票业务类型是否一致

                                //不一致签收失败
                                //赋值 签收失败
                                entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                //失败描述
                                entity.setNotes("签收失败，所选类型与发票业务类型不一致");
                                //构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                //放入集合
                                invoiceScanErrorList.add(invoiceScan);
                                break;

                        }





                        if(!dataEntity.getFlowType().equals("1")){
                            //非商品发票流程
                            //税额
                            boolean getTaxAmountFlag=true;
                            if (dataEntity.getTaxAmount() != null && entity.getTaxAmount() != null) {
                                if (dataEntity.getTaxAmount().compareTo(entity.getTaxAmount()) != 0) {
                                    entity.setNotes("税额不一致");
                                    getTaxAmountFlag = Boolean.FALSE;
                                }
                            }
                            if(getTaxAmountFlag) {
                                //对比一致 则签收成功 赋值 签收成功
                                entity.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                                entity.setNotes("签收成功");
                                //将解析出来的发票信息存到扫描表中 （签收状态（签收成功）签收方式（导入签收）签收时间当前时间）
                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.TRUE);
                                //放入集合
                                invoiceScanCheckSuccessList.add(invoiceScan);
                            }else{
                                entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                //将解析出来的发票信息存到扫描表中 （签收状态（签收成功）签收方式（导入签收）签收时间当前时间）
                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                //放入集合
                                invoiceScanErrorList.add(invoiceScan);
                            }
                        }else{
                            //商品发票流程
                            //对比一致 判断非多税率
                            if(StringUtils.isNotBlank(dataEntity.getTaxRate())){
                                //0.05计算
                                //税率
                                //底账税率
                                String dzTaxTate=dataEntity.getTaxRate();
                                if(dataEntity.getTaxRate().equals("免税")||dataEntity.getTaxRate().equals("不征收")){
                                    dzTaxTate="0";
                                }
                                BigDecimal sl=new BigDecimal(dzTaxTate).divide(new BigDecimal("100"));
                                BigDecimal chazhi=entity.getInvoiceAmount().multiply(sl).subtract(dataEntity.getTaxAmount());
                                //最大值0.05  最小值-0.05
                                BigDecimal max=new BigDecimal("0.05");
                                BigDecimal min=new BigDecimal("0.05").divide(new BigDecimal(-1));
                                System.out.println("=========="+chazhi.compareTo(max));
                                System.out.println("=========="+chazhi.compareTo(min));
                                if(chazhi.compareTo(max)==1||chazhi.compareTo(min)==-1){
                                    //赋值 签收失败
                                    entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                    //失败描述
                                    entity.setNotes("签收失败，税额误差超出±0.05");
                                    //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                    final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                    //放入集合
                                    invoiceScanErrorList.add(invoiceScan);
                                }else{
                                    //0稅率普票計算抵扣稅額
                                    if(StringUtils.isNotBlank(dataEntity.getLslbz())){
                                        //根据供应商号判断明细商品名称是否都存在，有一个不存在就失败,如果多税率也签收失败
                                         Boolean dkmx=true;
                                         String shuiLv=null; //明细税率对比值
                                         int errType=0;//错误类型  1无该明细   2明细多税率
                                         //获取底账明细
                                        List<String> getDZMXName= noDetailedInvoiceDao.getDZMXName(exportEntity.getSchemaLabel(),dataEntity.getInvoiceCode()+dataEntity.getInvoiceNo());
                                        for (String dzmxName:getDZMXName){

                                            // 邮箱验证规则
                                            String regEx = "^\\*[\\u4e00-\\u9fa5a-zA-Z0-9]{1,99}\\*[\\u4e00-\\u9fa5a-zA-Z0-9]{1,99}$";
                                            // 编译正则表达式
                                            Pattern pattern = Pattern.compile(regEx);
                                            // 忽略大小写的写法
                                            // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
                                            Matcher matcher = pattern.matcher(dzmxName);
                                            // 字符串是否与正则表达式相匹配
                                            boolean rs = matcher.matches();
                                            if(rs){
                                                dzmxName= dzmxName.substring( dzmxName.indexOf("*",dzmxName.indexOf("*") +1 )+1,dzmxName.length());
                                            }

                                            List<String> notesList=signImportDao.getNotesByVendorNbr(exportEntity.getSchemaLabel(),dzmxName,dataEntity.getVenderid());
                                            if(notesList==null||notesList.size()==0){//说明没有该明细
                                                errType=1;
                                                dkmx=false;
                                                break;
                                            }else{//有该明细，校验所有明细税率是否正确
                                                if(shuiLv==null){
                                                    if(notesList.get(0)!=null) {
                                                        shuiLv = notesList.get(0);
                                                    }
                                                }else{
                                                    if(!shuiLv.equals(notesList.get(0))){
                                                        errType=2;
                                                        dkmx=false;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if(dkmx){
                                            //计算抵扣税额   金额*excel税率=抵扣税额

                                             BigDecimal dkTaxAmount=  entity.getInvoiceAmount().multiply(new BigDecimal(shuiLv).divide(new BigDecimal("100")));

                                            dataEntity.setDeductibleTax(shuiLv);
                                            dataEntity.setDeductibleTaxRate(dkTaxAmount.toPlainString());


                                            //抵扣金额与发票金额的对比 若抵扣税率存在抵扣金额要小于发票金额否不存在则抵扣金额等于发票金额，在这里 抵扣税率一定存在
                                            if(StringUtils.isNotBlank(dataEntity.getDkInvoiceAmount())){
                                                if(new BigDecimal(dataEntity.getDkInvoiceAmount()).compareTo(entity.getInvoiceAmount())==-1){
                                                    //保存抵扣
                                                    signImportDao.setDeductible(exportEntity.getSchemaLabel(),dataEntity);
                                                    //对比一致 则签收成功 赋值 签收成功
                                                    entity.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                                                    entity.setNotes("签收成功");
                                                    //将解析出来的发票信息存到扫描表中 （签收状态（签收成功）签收方式（导入签收）签收时间当前时间）
                                                    final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.TRUE);
                                                    //放入集合
                                                    invoiceScanCheckSuccessList.add(invoiceScan);
                                                }else{
                                                    //赋值 签收失败
                                                    entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                                    //失败描述
                                                    entity.setNotes("签收失败，抵扣金额应小于发票金额");
                                                    //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                                    final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                                    //放入集合
                                                    invoiceScanErrorList.add(invoiceScan);
                                                }
                                            }else{
                                                //赋值 签收失败
                                                entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                                //失败描述
                                                entity.setNotes("签收失败，抵扣金额为空");
                                                //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                                //放入集合
                                                invoiceScanErrorList.add(invoiceScan);
                                            }

                                        }else{
                                            //赋值 签收失败
                                            entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                            //失败描述
                                            if(errType==1) {
//                                                entity.setNotes("签收失败，该供应商号下商品与明细不匹配，无法计算抵扣税额");
                                                //普票计算抵扣金额与发票金额的对比 若抵扣税率存在抵扣金额要小于发票金额否不存在则抵扣金额等于发票金额
                                                if(dataEntity.getInvoiceType().equals("04")) {
                                                    if (StringUtils.isNotBlank(dataEntity.getDkInvoiceAmount())) {
                                                        if (StringUtils.isNotBlank(dataEntity.getDeductibleTax())) {


                                                            if (new BigDecimal(dataEntity.getDkInvoiceAmount()).compareTo(entity.getInvoiceAmount()) == -1) {
                                                                //对比一致 则签收成功 赋值 签收成功
                                                                entity.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                                                                entity.setNotes("签收成功");
                                                                //将解析出来的发票信息存到扫描表中 （签收状态（签收成功）签收方式（导入签收）签收时间当前时间）
                                                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.TRUE);
                                                                //放入集合
                                                                invoiceScanCheckSuccessList.add(invoiceScan);
                                                            } else {
                                                                //赋值 签收失败
                                                                entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                                                //失败描述
                                                                entity.setNotes("签收失败，抵扣金额应小于发票金额");
                                                                //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                                                //放入集合
                                                                invoiceScanErrorList.add(invoiceScan);
                                                            }
                                                        } else {
                                                            if (new BigDecimal(dataEntity.getDkInvoiceAmount()).compareTo(entity.getInvoiceAmount()) == 0) {
                                                                //对比一致 则签收成功 赋值 签收成功
                                                                entity.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                                                                entity.setNotes("签收成功");
                                                                //将解析出来的发票信息存到扫描表中 （签收状态（签收成功）签收方式（导入签收）签收时间当前时间）
                                                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.TRUE);
                                                                //放入集合
                                                                invoiceScanCheckSuccessList.add(invoiceScan);
                                                            } else {
                                                                //赋值 签收失败
                                                                entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                                                //失败描述
                                                                entity.setNotes("签收失败，抵扣金额应等于发票金额");
                                                                //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                                                //放入集合
                                                                invoiceScanErrorList.add(invoiceScan);
                                                            }
                                                        }
                                                    } else {
                                                        //赋值 签收失败
                                                        entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                                        //失败描述
                                                        entity.setNotes("签收失败，抵扣金额为空");
                                                        //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                                        final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                                        //放入集合
                                                        invoiceScanErrorList.add(invoiceScan);
                                                    }
                                                }else{
                                                    //对比一致 则签收成功 赋值 签收成功
                                                    entity.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                                                    entity.setNotes("签收成功");
                                                    //将解析出来的发票信息存到扫描表中 （签收状态（签收成功）签收方式（导入签收）签收时间当前时间）
                                                    final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.TRUE);
                                                    //放入集合
                                                    invoiceScanCheckSuccessList.add(invoiceScan);
                                                }

                                            }else if(errType==2){
                                                entity.setNotes("签收失败，明细内商品多税率，无法计算抵扣税额");
                                                //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                                //放入集合
                                                invoiceScanErrorList.add(invoiceScan);
                                            }

                                        }

                                    }else{

                                        //普票计算抵扣金额与发票金额的对比 若抵扣税率存在抵扣金额要小于发票金额否不存在则抵扣金额等于发票金额
                                        if(dataEntity.getInvoiceType().equals("04")) {
                                            if (StringUtils.isNotBlank(dataEntity.getDkInvoiceAmount())) {
                                                if (StringUtils.isNotBlank(dataEntity.getDeductibleTax())) {


                                                    if (new BigDecimal(dataEntity.getDkInvoiceAmount()).compareTo(entity.getInvoiceAmount()) == -1) {
                                                        //对比一致 则签收成功 赋值 签收成功
                                                        entity.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                                                        entity.setNotes("签收成功");
                                                        //将解析出来的发票信息存到扫描表中 （签收状态（签收成功）签收方式（导入签收）签收时间当前时间）
                                                        final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.TRUE);
                                                        //放入集合
                                                        invoiceScanCheckSuccessList.add(invoiceScan);
                                                    } else {
                                                        //赋值 签收失败
                                                        entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                                        //失败描述
                                                        entity.setNotes("签收失败，抵扣金额应小于发票金额");
                                                        //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                                        final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                                        //放入集合
                                                        invoiceScanErrorList.add(invoiceScan);
                                                    }
                                                } else {
                                                    if (new BigDecimal(dataEntity.getDkInvoiceAmount()).compareTo(entity.getInvoiceAmount()) == 0) {
                                                        //对比一致 则签收成功 赋值 签收成功
                                                        entity.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                                                        entity.setNotes("签收成功");
                                                        //将解析出来的发票信息存到扫描表中 （签收状态（签收成功）签收方式（导入签收）签收时间当前时间）
                                                        final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.TRUE);
                                                        //放入集合
                                                        invoiceScanCheckSuccessList.add(invoiceScan);
                                                    } else {
                                                        //赋值 签收失败
                                                        entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                                        //失败描述
                                                        entity.setNotes("签收失败，抵扣金额应等于发票金额");
                                                        //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                                        final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                                        //放入集合
                                                        invoiceScanErrorList.add(invoiceScan);
                                                    }
                                                }
                                            } else {
                                                //赋值 签收失败
                                                entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                                //失败描述
                                                entity.setNotes("签收失败，抵扣金额为空");
                                                //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                                //放入集合
                                                invoiceScanErrorList.add(invoiceScan);
                                            }
                                        }else{
                                            //对比一致 则签收成功 赋值 签收成功
                                            entity.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                                            entity.setNotes("签收成功");
                                            //将解析出来的发票信息存到扫描表中 （签收状态（签收成功）签收方式（导入签收）签收时间当前时间）
                                            final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.TRUE);
                                            //放入集合
                                            invoiceScanCheckSuccessList.add(invoiceScan);
                                        }
                                    }
                                }
                            }else{
                                //赋值 签收失败
                                entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                                //失败描述
                                entity.setNotes("签收失败，多税率无法签收");
                                //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.FALSE);
                                //放入集合
                                invoiceScanErrorList.add(invoiceScan);
                            }

                        }
                    } else {
                        //对比不一致 则签收失败 赋值 签收失败
                        entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                        //将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                        final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, dataEntity, entity, Boolean.TRUE);
                        //放入集合
                        invoiceScanCheckErrorList.add(invoiceScan);
                    }
                } else {

                    //赋值 签收失败
                    entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                    //失败描述
                    entity.setNotes("签收失败，无抵账信息");
                    //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                    final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, new RecordInvoiceDataEntity(), entity, Boolean.FALSE);
                    //放入集合
                    invoiceScanErrorList.add(invoiceScan);
                }

            } else {
                if (!entity.getDyInvoiceCode().equals(entity.getInvoiceCode())) {
                    //打印的代码错误
                    //赋值 签收失败
                    entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                    //失败描述
                    entity.setNotes("打印代码与印刷代码不一致!");
                    //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                    final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, new RecordInvoiceDataEntity(), entity, Boolean.FALSE);
                    //放入集合
                    invoiceScanErrorList.add(invoiceScan);
                } else {
                    //打印的号码错误
                    //赋值 签收失败
                    entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                    //失败描述
                    entity.setNotes("打印号码与印刷号码不一致!");
                    //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                    final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, new RecordInvoiceDataEntity(), entity, Boolean.FALSE);
                    //放入集合
                    invoiceScanErrorList.add(invoiceScan);
                }

            }

        };

        //将抵账表存在 但信息不一致保存进扫描表
        if (!invoiceScanCheckErrorList.isEmpty()) {
//			signImportDao.insertScanInvoice( invoiceScanCheckErrorList);
            getId(invoiceScanCheckErrorList);
        }
        setId(recordInvoiceEntityList,invoiceScanCheckErrorList);
        //将抵账表存在 信息一致保存进扫描表 并更新抵账表，签收状态（已签收）签收方式（导入签收）签收时间（当前时间）
        if (!invoiceScanCheckSuccessList.isEmpty()) {
            //保存扫描表
//			signImportDao.insertScanInvoice( invoiceScanCheckSuccessList);
            getId(invoiceScanCheckSuccessList);
            //更新抵账表
            signImportDao.updateRecordQsStatus(exportEntity.getSchemaLabel(), invoiceScanCheckSuccessList.get(0));
        }
        setId(recordInvoiceEntityList,invoiceScanCheckSuccessList);


        //将校验失败的发票保存进扫描表
        if (!invoiceScanErrorList.isEmpty()) {
//			signImportDao.insertScanInvoice( invoiceScanErrorList);
            getId(invoiceScanErrorList);
        }
        setId(recordInvoiceEntityList,invoiceScanErrorList);

        Map<String,Object> returnMap=new HashMap();
        returnMap.put("list",recordInvoiceEntityList);
        returnMap.put("err",invoiceScanErrorList.size());
        return returnMap;
    }

    private Boolean checkListRecordInvoiceEntity(RecordInvoiceEntity recordInvoiceEntity) {

        //发票代码格式校验 10或12位数字
        final Boolean codeFlag = CommonUtil.isValidNum(recordInvoiceEntity.getInvoiceCode(), "^(\\d{10}|\\d{12})$");
        if (!codeFlag) {
            recordInvoiceEntity.setQsStatus(SignInEnum.QS_FAIL.getValue());
            recordInvoiceEntity.setTypeErrorFlag("1");
            recordInvoiceEntity.setNotes("发票代码格式错误!");
        }

        //发票号码格式校验 8位数字
        final Boolean numFlag = CommonUtil.isValidNum(recordInvoiceEntity.getInvoiceNo(), "^[\\d]{8}$");
        if (!numFlag) {
            recordInvoiceEntity.setQsStatus(SignInEnum.QS_FAIL.getValue());
            recordInvoiceEntity.setTypeErrorFlag("1");
            recordInvoiceEntity.setNotes("发票号码格式错误!");
        }

        //开票日期格式校验
        Boolean dateFlag = Boolean.FALSE;
        if (recordInvoiceEntity.getInvoiceDate() != null) {
            SimpleDateFormat simpleDateFormat2=new SimpleDateFormat(DEFAULT_SHORT_DATE_FORMAT);
            dateFlag = CommonUtil.isValidDate(simpleDateFormat2.format(recordInvoiceEntity.getInvoiceDate()), DEFAULT_SHORT_DATE_FORMAT, "[0-9]{4}-[0-9]{2}-[0-9]{2}");
            if (!dateFlag) {
                recordInvoiceEntity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                recordInvoiceEntity.setInvoiceDate(null);
                recordInvoiceEntity.setTypeErrorFlag("1");
                recordInvoiceEntity.setNotes("开票日期格式错误!");
            }
        } else {
            recordInvoiceEntity.setQsStatus(SignInEnum.QS_FAIL.getValue());
            recordInvoiceEntity.setInvoiceDate(null);
            recordInvoiceEntity.setTypeErrorFlag("1");
            recordInvoiceEntity.setNotes("开票日期格式错误!");
            dateFlag = Boolean.FALSE;
        }

        Boolean jymOrJe = Boolean.TRUE;
        //获取发票类型
        final String invoiceType = CommonUtil.getFplx(recordInvoiceEntity.getDyInvoiceCode());
        //专票、通行费发票、机动车发票数组
        final String[] speciallyInvoiceTypeArray = systemConfig.getInvoiceTypeParam().split(",");
        final Boolean flag = Arrays.asList(speciallyInvoiceTypeArray).contains(invoiceType);
        if (flag) {
            //专票
            if(recordInvoiceEntity.getInvoiceAmount()==null){
                recordInvoiceEntity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                recordInvoiceEntity.setTypeErrorFlag("1");
                recordInvoiceEntity.setInvoiceAmount(new BigDecimal("0.00"));
                recordInvoiceEntity.setNotes("金额格式错误!");
                jymOrJe = Boolean.FALSE;
            }
        } else if (Arrays.asList(TOME_INVOICE_TYPE).contains(invoiceType)) {
            //普票
            if(!CommonUtil.isValidNum(recordInvoiceEntity.getCheckCode(), "^[\\d]{20}$")&&!CommonUtil.isValidNum(recordInvoiceEntity.getCheckCode(), "^[\\d]{6}$")){
                recordInvoiceEntity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                recordInvoiceEntity.setTypeErrorFlag("1");
                recordInvoiceEntity.setNotes("校验码格式错误!");
                jymOrJe = Boolean.FALSE;
            }
        }
        return codeFlag && numFlag && dateFlag&&jymOrJe;
    }


    private InvoiceScan otherInvoiceListToInvoiceScan(ExportEntity exportEntity,RecordInvoiceEntity entity){
        //定义返回值
        final InvoiceScan invoiceScan = new InvoiceScan();
        invoiceScan.setId(entity.getId());
        invoiceScan.setVenderid(entity.getVenderid());
        invoiceScan.setVenderidEdit(entity.getVenderidEdit());
        invoiceScan.setLocalTrmSeqNum(entity.getLocalTrmSeqNum());
        invoiceScan.setFileType(entity.getFileType());
        invoiceScan.setFlowType(entity.getBilltypeCode());
        //人员名
        invoiceScan.setUserName(exportEntity.getUserName());
        //人员帐号
        invoiceScan.setUserAccount(exportEntity.getUserAccount());
        //签收方式--
        invoiceScan.setQsType(SignInEnum.QS_SCANNER.getValue());
        //签收描述
        invoiceScan.setNotes(entity.getNotes());
        invoiceScan.setQsStatus(entity.getQsStatus());
        invoiceScan.setValid("1");
        invoiceScan.setUuId(entity.getDyInvoiceCode()+entity.getDyInvoiceNo());
        //scan_id
        invoiceScan.setScanId(entity.getScanId());



        return invoiceScan;
    }
    private InvoiceScan errorDataListToInvoiceScan(ExportEntity exportEntity,RecordInvoiceEntity entity){
//定义返回值
        final InvoiceScan invoiceScan = new InvoiceScan();
        invoiceScan.setId(entity.getId());
        invoiceScan.setVenderid(entity.getVenderid());
        invoiceScan.setVenderidEdit(entity.getVenderidEdit());
        invoiceScan.setLocalTrmSeqNum(entity.getLocalTrmSeqNum());
        invoiceScan.setFileType(entity.getFileType());
        invoiceScan.setFlowType(entity.getBilltypeCode());
        //人员名
        invoiceScan.setUserName(exportEntity.getUserName());
        //人员帐号
        invoiceScan.setUserAccount(exportEntity.getUserAccount());
        //校验码
        invoiceScan.setCheckCode(entity.getCheckCode());
        //发票类型
        invoiceScan.setInvoiceType(entity.getInvoiceType());
        //打印代码
        invoiceScan.setDyInvoiceCode(entity.getDyInvoiceCode());
        //打印号码
        invoiceScan.setDyInvoiceNo(entity.getDyInvoiceNo());
        //发票代码
        invoiceScan.setInvoiceCode(entity.getInvoiceCode());
        //发票号码
        invoiceScan.setInvoiceNo(entity.getInvoiceNo());
        //金额
        invoiceScan.setInvoiceAmount(bigToStr(entity.getInvoiceAmount()));
        invoiceScan.setVenderid(entity.getVenderid());
        //开票时间
        try {
            invoiceScan.setInvoiceDate(simpleDateFormat.format(entity.getInvoiceDate()));
        }catch (Exception e){
            invoiceScan.setInvoiceDate(null);
        }
        //签收方式--
        invoiceScan.setQsType(SignInEnum.QS_SCANNER.getValue());
        //'签收结果(0-签收失败 1-签收成功）'
        invoiceScan.setQsStatus(entity.getQsStatus());
        //签收描述
        invoiceScan.setNotes(entity.getNotes());
        //是否有效（1-有效 0-无效）默认1
        invoiceScan.setValid("1");
        //uuid
        invoiceScan.setUuId(entity.getDyInvoiceCode() + entity.getDyInvoiceNo());
        //购方税号
        invoiceScan.setGfTaxNo(entity.getGfTaxNo());
        //销方税号
        invoiceScan.setXfTaxNo(entity.getXfTaxNo());
        //税额
        invoiceScan.setTaxAmount(bigToStr(entity.getTaxAmount()));
        //价税合计
        invoiceScan.setTotalAmount(bigToStr(entity.getTotalAmount()));
        //scanId
        invoiceScan.setScanId(entity.getScanId());
        //购方名称
        invoiceScan.setGfName(entity.getGfName());
        //x方名称
        invoiceScan.setXfName(entity.getXfName());

        return invoiceScan;
    }

    //插入获取id
    private  void getId(List<InvoiceScan> invoiceScans){
        for (InvoiceScan invoiceScan:invoiceScans){
            signImportDao.insertScanInvoice(null,invoiceScan);
        }
    }
    //插入成功修改id
    private  void setId(List<RecordInvoiceEntity> recordInvoiceEntityList,List<InvoiceScan> InvoiceScanList){
        for (InvoiceScan invoiceScan:InvoiceScanList){
            for (RecordInvoiceEntity recordInvoiceEntity:recordInvoiceEntityList){
                if ((recordInvoiceEntity.getInvoiceCode()+""+recordInvoiceEntity.getInvoiceNo()).equals(invoiceScan.getInvoiceCode()+""+invoiceScan.getInvoiceNo())){
                    recordInvoiceEntity.setId(invoiceScan.getId());
                }
            }
        }
    }
    /**
     * 比对 开票日期，未税金额、发票类型、打印代码、打印号码、购方税号、购方名称、税额、销方名称、销方税号是否一致
     *
     * @param entity     导入实体
     * @param dataEntity 数据库查询出的实体
     * @return 比对结果
     */
    private Boolean checkInfoConsistent(RecordInvoiceEntity entity, RecordInvoiceDataEntity dataEntity,List<String> finalTaxNoList) {
        Boolean consistentFlag = Boolean.TRUE;

        if (StringUtils.isNotBlank(entity.getGfTaxNo())&&StringUtils.isNotBlank(dataEntity.getGfTaxNo())) {
            if (!entity.getGfTaxNo().equals(dataEntity.getGfTaxNo())) {
                entity.setNotes("购方税号不一致");
                consistentFlag = Boolean.FALSE;
            }
        }
        //销方税号

        if (StringUtils.isNotBlank(entity.getXfTaxNo())&&StringUtils.isNotBlank(dataEntity.getXfTaxNo())) {
            if (!entity.getXfTaxNo().equals(dataEntity.getXfTaxNo())) {
                entity.setNotes("销方税号不一致");
                consistentFlag = Boolean.FALSE;
            }
        }

        //未税金额
        if (dataEntity.getInvoiceAmount() != null && entity.getInvoiceAmount() != null) {
            if (dataEntity.getInvoiceAmount().compareTo(entity.getInvoiceAmount()) != 0) {
                entity.setNotes("金额不一致");
                consistentFlag = Boolean.FALSE;
            }
        }

        if ((dataEntity.getInvoiceAmount() == null && entity.getInvoiceAmount() != null) ||
                dataEntity.getInvoiceAmount() != null && entity.getInvoiceAmount() == null) {
            consistentFlag = Boolean.FALSE;
        }

        //开票日期
        final String dataInvoiceTime = simpleDateFormat.format(dataEntity.getInvoiceDate());
        final String entityInvoiceTime = simpleDateFormat.format(entity.getInvoiceDate());
        if (!dataInvoiceTime.equals(entityInvoiceTime)) {
            entity.setNotes("开票日期不一致");
            consistentFlag = Boolean.FALSE;
        }


        String gf=dataEntity.getGfTaxNo();
        if (!finalTaxNoList.contains(gf)) {
            //人员未绑定该发票的税号，故改人员不能处理 返回签收失败
            entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
            entity.setHandleFlag("2");
            entity.setNotes("没有找到该税号");
            consistentFlag = Boolean.FALSE;
        }
        return consistentFlag;
    }
    /**
     * 抵账表存在时 构建发票扫描表实体
     *
     * @param exportEntity 包含人员信息实体
     * @param dataEntity   抵账实体
     * @param entity       导入实体
     * @param flag         抵账表是否存在 true 存在 false 不存在
     * @return 扫描实体
     */
    private InvoiceScan buildInvoiceScan(ExportEntity exportEntity, RecordInvoiceDataEntity dataEntity, RecordInvoiceEntity entity, Boolean flag) {
        //定义返回值
        final InvoiceScan invoiceScan = new InvoiceScan();
        invoiceScan.setJvCode(entity.getJvCode());
        invoiceScan.setCompanyCode(entity.getCompanyCode());

        invoiceScan.setFlowType(entity.getBilltypeCode());
//        String venderId=dataEntity.getVenderid();
        invoiceScan.setVenderid(entity.getVenderid());
        invoiceScan.setVenderidEdit(entity.getVenderidEdit());
        invoiceScan.setId(entity.getId());
        invoiceScan.setLocalTrmSeqNum(entity.getLocalTrmSeqNum());
        invoiceScan.setFileType(entity.getFileType());
        //人员名
        invoiceScan.setUserName(exportEntity.getUserName());
        //人员帐号
        invoiceScan.setUserAccount(exportEntity.getUserAccount());
        //校验码
        invoiceScan.setCheckCode(entity.getCheckCode());
        //发票类型
        invoiceScan.setInvoiceType(entity.getInvoiceType());
        //打印代码
        invoiceScan.setDyInvoiceCode(entity.getDyInvoiceCode());
        //打印号码
        invoiceScan.setDyInvoiceNo(entity.getDyInvoiceNo());
        //发票代码
        invoiceScan.setInvoiceCode(entity.getInvoiceCode());
        //发票号码
        invoiceScan.setInvoiceNo(entity.getInvoiceNo());
        //金额
        invoiceScan.setInvoiceAmount(bigToStr(entity.getInvoiceAmount()));
        //开票时间
        invoiceScan.setInvoiceDate(simpleDateFormat.format(entity.getInvoiceDate()));
        //签收方式--
        invoiceScan.setQsType(SignInEnum.QS_SCANNER.getValue());
        //'签收结果(0-签收失败 1-签收成功）'
        invoiceScan.setQsStatus(entity.getQsStatus());
        //签收描述
        invoiceScan.setNotes(entity.getNotes());
        //是否有效（1-有效 0-无效）默认1
        invoiceScan.setValid("1");
        //uuid
        invoiceScan.setUuId(entity.getDyInvoiceCode() + entity.getDyInvoiceNo());
        //购方税号
        invoiceScan.setGfTaxNo(entity.getGfTaxNo());
        //销方税号
        invoiceScan.setXfTaxNo(entity.getXfTaxNo());
        //税额
        invoiceScan.setTaxAmount(bigToStr(entity.getTaxAmount()));
        //价税合计
        invoiceScan.setTotalAmount(bigToStr(entity.getTotalAmount()));
        //scanId
        invoiceScan.setScanId(entity.getScanId());
        //购方名称
        invoiceScan.setGfName(entity.getGfName());
        //x方名称
        invoiceScan.setXfName(entity.getXfName());


        if (flag) {
            //销方名称
            invoiceScan.setXfName(dataEntity.getXfName());
            invoiceScan.setDeductibleTaxRate(dataEntity.getDeductibleTaxRate());
            invoiceScan.setDeductibleTax(dataEntity.getDeductibleTax());
            entity.setXfName(dataEntity.getXfName());
            //购方名称
            invoiceScan.setGfName(dataEntity.getGfName());
            //购方名称
            entity.setGfName(dataEntity.getGfName());
            //签收时间
            if ("1".equals(entity.getQsStatus())) {
                invoiceScan.setQsDate(simpleDateFormat.format(new Date()));
            }

        }
        return invoiceScan;
    }

    /**
     * 比对 开票日期，未税金额、发票类型、打印代码、打印号码、购方税号、购方名称、税额、销方名称、销方税号是否一致
     *
     * @param entity     导入实体
     * @param dataEntity 数据库查询出的实体
     * @return 比对结果
     */
    private Boolean checkInfoConsistent(RecordInvoiceEntity entity, ResponseInvoice dataEntity) {
        Boolean consistentFlag = Boolean.TRUE;
        //购方名称、购方税号
        if (StringUtils.isNotBlank(dataEntity.getBuyerName())) {
//			if (!entity.getGfName().equals(dataEntity.getBuyerName())) {
//				entity.setNotes("购方名称不一致");
//				consistentFlag = Boolean.FALSE;
//			}else{
            //底账表购方名称
            //获取机构表数据
            OrganizationEntity organizationEntity=new OrganizationEntity();
            organizationEntity.setTaxno(dataEntity.getBuyerTaxNo());
            List<OrganizationEntity> organizationEntityList=organizationService.queryList(null,organizationEntity);
            if (organizationEntityList!=null&&organizationEntityList.size()>0){
                if(!StringUtils.isNotBlank(organizationEntityList.get(0).getTaxname())||!(organizationEntityList.get(0).getTaxname().equals(dataEntity.getBuyerName()))){
                    entity.setNotes("购方名称与机构信息不一致");
                    consistentFlag = Boolean.FALSE;
                }
            }else{
                entity.setNotes("购方名称与机构信息不一致");
                consistentFlag = Boolean.FALSE;
            }
//			}
        }
        return consistentFlag;
    }
    public boolean inspectionProcess2( List<ResponseInvoice> responseInvoiceList) {

        //遍历
        for (ResponseInvoice responseInvoice :responseInvoiceList){


            //所有需要保存的明细数据
            final List<InvoiceDetailInfo> inspectionDetailInfoList = CollectionHelper.newArrayList();
            //构建抵账表发票明细
            final List<InvoiceDetailInfo> invoiceDetailInfoList = buildDetailList(responseInvoice);
            //集合合并
            inspectionDetailInfoList.addAll(invoiceDetailInfoList);

            if(inspectionDetailInfoList.size() > 0) {
                //保存明细表
                //构建明细表数据
                //保存明细数据
                try {
                    String taxRate=inspectionDetailInfoList.get(0).getTaxRate();
                    for(InvoiceDetailInfo invoiceDetailInfo:inspectionDetailInfoList){
                        //獲取所有明細的稅率是否相等
                        if(taxRate!=null){
                            if(!taxRate.equals(invoiceDetailInfo.getTaxRate())){
                                taxRate=null;
                            }
                        }
                    }
                    if(taxRate.equals("0")){
                        return true;
                    }else{
                        return false;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }else{
                return false;
            }
        };


        return false;
    }



    @Transactional
    public Boolean inspectionProcess(String schemaLabel, List<ResponseInvoice> responseInvoiceList,boolean cyYoN) {

        //遍历
        responseInvoiceList.forEach(responseInvoice -> {
            //导入excel时所有需要保存主表数据
            final List<InvoiceCollectionInfo> infoList = CollectionHelper.newArrayList();
            //所有需要保存的明细数据
            final List<InvoiceDetailInfo> inspectionDetailInfoList = CollectionHelper.newArrayList();
            //构建抵账表发票明细
            final List<InvoiceDetailInfo> invoiceDetailInfoList = buildDetailList(responseInvoice);
            //集合合并
            inspectionDetailInfoList.addAll(invoiceDetailInfoList);

            //构建主表实体
            InvoiceCollectionInfo invoiceCollectionInfo = buildInvoiceCollectionInfo(responseInvoice, "");

            if(inspectionDetailInfoList.size() > 0) {
                //保存明细表
                //构建明细表数据
                //保存明细数据
                try {
                    String taxRate=inspectionDetailInfoList.get(0).getTaxRate();
                    for(InvoiceDetailInfo invoiceDetailInfo:inspectionDetailInfoList){
                        //獲取所有明細的稅率是否相等
                        if(taxRate!=null){
                            if(!taxRate.equals(invoiceDetailInfo.getTaxRate())){
                                taxRate=null;
                            }
                        }
                    }

                    noDetailedInvoiceDao.deleteDetail(schemaLabel,inspectionDetailInfoList.get(0));
                    noDetailedInvoiceDao.insertNoDetailedInvoice(schemaLabel, inspectionDetailInfoList);
                    invoiceCollectionInfo.setTaxRate(taxRate);
                    if(invoiceCollectionInfo.getTaxRate().equals("0")){
                        invoiceCollectionInfo.setLslbz("3");
                        invoiceCollectionInfo.setTaxRate("0");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //添加集合
            infoList.add(invoiceCollectionInfo);



                //保存主表
                for (InvoiceCollectionInfo invoiceCollectionInfo1 : infoList) {

                    //不知道啥情况selectKey获取到的值应该是0结果却是1，没办法，先把问题解决了
                    //根据uuid是否存在决定是插入还是修改


                       Integer count= noDetailedInvoiceDao.selectRecordInvoiceCount(schemaLabel,invoiceCollectionInfo1.getUuid());
                       if(null!=count&&count>0){
                           noDetailedInvoiceDao.updateRecordInvoiceScan(schemaLabel, invoiceCollectionInfo1,cyYoN?1:0);
                       }else{
                           noDetailedInvoiceDao.insertRecordInvoiceScan(schemaLabel, invoiceCollectionInfo1,cyYoN?1:0);
                       }

            }
        });


        return Boolean.TRUE;
    }
    /**
     * 构建抵账表发票明细
     *
     * @param invoiceDetailList 响应实体
     * @return 抵账表发票明细
     */
    private List<RecordInvoiceDetail> buildRecordDetailList(List<InvoiceDetailInfo> invoiceDetailList) {
        //构建返回值
        final List<RecordInvoiceDetail> invoiceDetailInfoList = CollectionHelper.newArrayList();



        invoiceDetailList.forEach(invoiceDetail -> {
            final RecordInvoiceDetail invoiceDetailInfo = new RecordInvoiceDetail();

            //税额
            try {
                invoiceDetailInfo.setTaxAmount(new BigDecimal(invoiceDetail.getTaxAmount()));
            }catch (Exception e){
                invoiceDetailInfo.setTaxAmount(null);
                e.printStackTrace();
            }
            //货物或应税劳务名称
            invoiceDetailInfo.setGoodName(invoiceDetail.getGoodsName());
            //发票号码
            invoiceDetailInfo.setInvoiceNo(invoiceDetail.getInvoiceNo());
            //发票代码
            invoiceDetailInfo.setInvoiceCode(invoiceDetail.getInvoiceCode());
            //数量
            try {
                invoiceDetailInfo.setNum(new BigDecimal(invoiceDetail.getNum()));
            }catch (Exception e){
                invoiceDetailInfo.setNum(null);
            }
            //明细序号
            invoiceDetailInfo.setDetailNo(Integer.parseInt(invoiceDetail.getDetailNo()));
            //单价
            try {
                invoiceDetailInfo.setUnitPrice(new BigDecimal(invoiceDetail.getUnitPrice()));
            }catch (Exception e){
                invoiceDetailInfo.setUnitPrice(null);
            }

            //税率
            try {
                invoiceDetailInfo.setTaxRate(new BigDecimal(invoiceDetail.getTaxRate()));
            }catch (Exception e){
                invoiceDetailInfo.setTaxRate(null);
            }
            //单位
            invoiceDetailInfo.setUnit(invoiceDetail.getUnit());
            //金额
            try {
                invoiceDetailInfo.setDetailAmount(new BigDecimal(invoiceDetail.getDetailAmount()));
            }catch (Exception e){
                invoiceDetailInfo.setDetailAmount(null);
            }
            //规格型号
            invoiceDetailInfo.setModel(invoiceDetail.getModel());

            //放入集合
            invoiceDetailInfoList.add(invoiceDetailInfo);
        });
        return invoiceDetailInfoList;
    }
    /**
     * 构建抵账主体数据
     *
     * @param responseInvoice 抵账主体数据
     * @return 主体数据
     */
    private InvoiceCollectionInfo buildInvoiceCollectionInfo(ResponseInvoice responseInvoice, String buyerTaxNo) {
        //定义返回值
        final InvoiceCollectionInfo invoiceCollectionInfo = new InvoiceCollectionInfo();

        //购方税号
        invoiceCollectionInfo.setGfTaxNo(responseInvoice.getBuyerTaxNo());
        //购方名称
        invoiceCollectionInfo.setGfName(responseInvoice.getBuyerName());
        //发票类型
        invoiceCollectionInfo.setInvoiceType(responseInvoice.getInvoiceType());
        //发票代码
        invoiceCollectionInfo.setInvoiceCode(responseInvoice.getInvoiceCode());
        //发票号码
        invoiceCollectionInfo.setInvoiceNo(responseInvoice.getInvoiceNo());
        //价税合计
        invoiceCollectionInfo.setTotalAmount(responseInvoice.getTotalAmount());
        //金额
        invoiceCollectionInfo.setInvoiceAmount(responseInvoice.getInvoiceAmount());
        //税额
        invoiceCollectionInfo.setTaxAmount(responseInvoice.getTaxAmount());
        //销方税号
        invoiceCollectionInfo.setXfTaxNo(responseInvoice.getSalerTaxNo());
        //销方名称
        invoiceCollectionInfo.setXfName(responseInvoice.getSalerName());
        //销方地址
        invoiceCollectionInfo.setXfAddressAndPhone(responseInvoice.getSalerAddressPhone());
        //销方银行帐号
        invoiceCollectionInfo.setXfBankAndNo(responseInvoice.getSalerAccount());
        //购方银行帐号
        invoiceCollectionInfo.setGfBankAndNo(responseInvoice.getBuyerAccount());
        //购方地址
        invoiceCollectionInfo.setGfAddressAndPhone(responseInvoice.getBuyerAddressPhone());
        SimpleDateFormat simpleDateFormatyyyyMMdd=new SimpleDateFormat(SHORT_DATE_FORMAT);
        //开票日期
        Date invoiceDateTime=null;
        try {
            invoiceDateTime = simpleDateFormatyyyyMMdd.parse(responseInvoice.getInvoiceDate());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        invoiceCollectionInfo.setInvoiceDate(invoiceDateTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(invoiceCollectionInfo.getInvoiceDate());
        invoiceCollectionInfo.setInvoiceDateString(dateString);
        //备注
        invoiceCollectionInfo.setRemark(responseInvoice.getRemark());
        //校验码
        invoiceCollectionInfo.setCheckCode(responseInvoice.getCheckCode());
        //有明细
        invoiceCollectionInfo.setDetailYesorno(DETAIL_YES_OR_NO);

        //uuid
        invoiceCollectionInfo.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
        //更新前的购方税号，放置不一致
        invoiceCollectionInfo.setBuyerTaxNo(buyerTaxNo);
        //发票状态
        if ("N".equals(responseInvoice.getIsCancelled())) {
            invoiceCollectionInfo.setInvoiceStatus("0");
        } else if("Y".equals(responseInvoice.getIsCancelled())){
            invoiceCollectionInfo.setInvoiceStatus("2");
        }else if("0".equals(responseInvoice.getIsCancelled())){
            invoiceCollectionInfo.setInvoiceStatus("0");
        }else if("3".equals(responseInvoice.getIsCancelled())){
            invoiceCollectionInfo.setInvoiceStatus("3");
        }else if("2".equals(responseInvoice.getIsCancelled())){
            invoiceCollectionInfo.setInvoiceStatus("2");
        }
        //机器编号
        invoiceCollectionInfo.setMachinecode(responseInvoice.getMachineNo());
        List<InvoiceDetail> detailList = responseInvoice.getDetailList();
        if(detailList!=null){ // 超过八条数据取第二条税率
            Set set = new HashSet<String>();
            for(InvoiceDetail detail :detailList){
                if(detail.getTaxRate()!=null){
                    set.add(detail.getTaxRate());
                }
            }
            if(detailList.size()>=8 && set.size()==1){
                if(StringUtils.isNotBlank(detailList.get(1).getTaxRate())) {
                	invoiceCollectionInfo.setTaxRate(detailList.get(1).getTaxRate());
                }
            }else{
                if(set.size()==1) {
                    if(StringUtils.isNotBlank(detailList.get(0).getTaxRate())) {
                    	invoiceCollectionInfo.setTaxRate(detailList.get(0).getTaxRate());
                    }
                }
            }
        }
        //返回
        return invoiceCollectionInfo;
    }
    /**
     * 构建统计实体集
     *
     * @param responseInvoice 响应实体
     * @return 抵账统计实体
     */
    private List<RecordInvoiceStatistics> buildRecordInvoiceStatisticsList(ResponseInvoice responseInvoice) {
        //定义返回值
        final List<RecordInvoiceStatistics> recordInvoiceStatisticsList = CollectionHelper.newArrayList();

        //明细
        List<InvoiceDetail> invoiceDetailList;

        //如果为机动车统计销售发票
        if (InvoiceTypeEnum.MOTOR_INVOICE.getResultCode().equals(responseInvoice.getInvoiceType())) {
            invoiceDetailList = motorVehicleDetail(responseInvoice);
        } else {
            //不为机动车统计销售发票
            invoiceDetailList = responseInvoice.getDetailList();
        }

        // 最终要的结果
        final Map<String, List<InvoiceDetail>> resultMap = newHashMap();
        //按税率分组
        invoiceDetailList.forEach(dataItem -> {
            if (!dataItem.getGoodsName().equals("原价合计") && !dataItem.getGoodsName().equals("折扣额合计")&& !dataItem.getGoodsName().equals("(详见销货清单)")&&StringUtils.isNotEmpty(dataItem.getTaxRate())) {
                if (resultMap.containsKey(dataItem.getTaxRate())) {
                    resultMap.get(dataItem.getTaxRate()).add(dataItem);
                } else {
                    final List<InvoiceDetail> list = CollectionHelper.newArrayList();
                    list.add(dataItem);
                    resultMap.put(dataItem.getTaxRate(), list);
                }
            }
        });

        //循环遍历map获取每组明细，并循环遍历每组里的明细组装抵账统计数据
        for (Map.Entry<String, List<InvoiceDetail>> entry : resultMap.entrySet()) {
            //获取每组明细
            final List<InvoiceDetail> detailList = entry.getValue();
            //定义抵账统计实体
            final RecordInvoiceStatistics recordInvoiceStatistics = new RecordInvoiceStatistics();
            //税额
            Double taxAmount = 0.0;
            //金额
            Double detailAmount = 0.0;
            //循环赋值
            for (InvoiceDetail invoiceDetail : detailList) {
                taxAmount = taxAmount + Double.valueOf(invoiceDetail.getTaxAmount());
                detailAmount = detailAmount + Double.valueOf(invoiceDetail.getDetailAmount());

            }
            //价税合计
            final Double totalAmount = taxAmount + detailAmount;
            //税率
            recordInvoiceStatistics.setTaxRate(Double.valueOf(entry.getKey()));
            //发票号码
            recordInvoiceStatistics.setInvoiceNo(responseInvoice.getInvoiceNo());
            //发票代码
            recordInvoiceStatistics.setInvoiceCode(responseInvoice.getInvoiceCode());
            //税额
            recordInvoiceStatistics.setTaxRate(taxAmount);
            //金额
            recordInvoiceStatistics.setDetailAmount(detailAmount);
            //价税合计
            recordInvoiceStatistics.setTotalAmount(totalAmount);
            //放入集合
            recordInvoiceStatisticsList.add(recordInvoiceStatistics);
        }
        //返回
        return recordInvoiceStatisticsList;
    }
    /**
     * 构建抵账表发票明细
     *
     * @param responseInvoice 响应实体
     * @return 抵账表发票明细
     */
    private List<InvoiceDetailInfo> buildDetailList(ResponseInvoice responseInvoice) {
        //构建返回值
        final List<InvoiceDetailInfo> invoiceDetailInfoList = CollectionHelper.newArrayList();

        //明细
        final List<InvoiceDetail> invoiceDetailList = responseInvoice.getDetailList();

        invoiceDetailList.forEach(invoiceDetail -> {
            final InvoiceDetailInfo invoiceDetailInfo = new InvoiceDetailInfo();
            //税额
            invoiceDetailInfo.setTaxAmount(invoiceDetail.getTaxAmount());
            //货物或应税劳务名称
            invoiceDetailInfo.setGoodsName(invoiceDetail.getGoodsName());
            //发票号码
            invoiceDetailInfo.setInvoiceNo(responseInvoice.getInvoiceNo());
            //发票代码
            invoiceDetailInfo.setInvoiceCode(responseInvoice.getInvoiceCode());
            //数量
            invoiceDetailInfo.setNum(invoiceDetail.getNum());
            //明细序号
            invoiceDetailInfo.setDetailNo(invoiceDetail.getDetailNo());
            //单价
            invoiceDetailInfo.setUnitPrice(invoiceDetail.getUnitPrice());
            //类型
            invoiceDetailInfo.setLx(invoiceDetail.getLx());
            //uuid唯一标识(发票代码+发票号码)
            invoiceDetailInfo.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
            //通行日期起
            invoiceDetailInfo.setTxrqq(invoiceDetail.getTxrqq());
            //通行日期止
            invoiceDetailInfo.setTxrqz(invoiceDetail.getTxrqz());
            //税率
            invoiceDetailInfo.setTaxRate(invoiceDetail.getTaxRate());
            //单位
            invoiceDetailInfo.setUnit(invoiceDetail.getUnit());
            //金额
            invoiceDetailInfo.setDetailAmount(invoiceDetail.getDetailAmount());
            //规格型号
            invoiceDetailInfo.setModel(invoiceDetail.getSpecificationModel());
            //车牌号
            invoiceDetailInfo.setCph(invoiceDetail.getCph());
            //放入集合
            invoiceDetailInfoList.add(invoiceDetailInfo);
        });
        return invoiceDetailInfoList;
    }
    private List<InvoiceCheckVehicleDetailModel> buildVehicleDetailList(ResponseInvoice responseInvoice) {
        final List<InvoiceCheckVehicleDetailModel> vehicleDetailModelList = CollectionHelper.newArrayList();
        final InvoiceCheckVehicleDetailModel invoiceCheckVehicleDetailModel = new InvoiceCheckVehicleDetailModel();
        invoiceCheckVehicleDetailModel.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
        invoiceCheckVehicleDetailModel.setBuyerIdNum(responseInvoice.getBuyerIDNum());
        invoiceCheckVehicleDetailModel.setVehicleType(responseInvoice.getVehicleType());
        invoiceCheckVehicleDetailModel.setFactoryModel(responseInvoice.getFactoryModel());
        invoiceCheckVehicleDetailModel.setProductPlace(responseInvoice.getProductPlace());
        invoiceCheckVehicleDetailModel.setCertificate(responseInvoice.getCertificate());
        invoiceCheckVehicleDetailModel.setCertificateImport(responseInvoice.getCertificateImport());
        invoiceCheckVehicleDetailModel.setInspectionNum(responseInvoice.getInspectionNum());
        invoiceCheckVehicleDetailModel.setEngineNo(responseInvoice.getEngineNo());
        invoiceCheckVehicleDetailModel.setVehicleNo(responseInvoice.getVehicleNo());
        invoiceCheckVehicleDetailModel.setTaxRate(responseInvoice.getTaxRate());
        invoiceCheckVehicleDetailModel.setTaxBureauName(responseInvoice.getTaxBureauName());
        invoiceCheckVehicleDetailModel.setTaxBureauCode(responseInvoice.getTaxBureauCode());
        invoiceCheckVehicleDetailModel.setTaxRecords(responseInvoice.getTaxRecords());
        invoiceCheckVehicleDetailModel.setLimitPeople(responseInvoice.getLimitPeople());
        invoiceCheckVehicleDetailModel.setTonnage(responseInvoice.getTonnage());

        vehicleDetailModelList.add(invoiceCheckVehicleDetailModel);
        return vehicleDetailModelList;
    }

    /**
     * 机动车统一销售发票明细
     * 这种发票没有detailList 故明细信息需从主体信息中获取
     *
     * @param responseInvoice 查验响应实体
     * @return 机动车统一销售发票明细
     */
    private List<InvoiceDetail> motorVehicleDetail(ResponseInvoice responseInvoice) {
        final List<InvoiceDetail> invoiceDetailList = CollectionHelper.newArrayList();
        final InvoiceDetail invoiceDetail = new InvoiceDetail();
        //税率
        invoiceDetail.setTaxRate(responseInvoice.getTaxRate());
        //税额
        invoiceDetail.setTaxAmount(responseInvoice.getTaxAmount());
        //金额
        invoiceDetail.setDetailAmount(responseInvoice.getInvoiceAmount());

        invoiceDetailList.add(invoiceDetail);
        return invoiceDetailList;
    }
    
    @Transactional
    public String excuteUpload(ExportEntity entity, MultipartFile file, Map<String, String> ocrMap) throws Exception{
        // 发票签收
    	// 转换实体
        List<RecordInvoiceEntity> recordInvoiceEntityList2 = this.toSignInvoiceVo(ocrMap, null);
        // 
        final Map<String,Object> recordInvoiceEntityMap = this.importSignExcel(entity, recordInvoiceEntityList2);
        String uuid = this.onlyUploadImg(entity, file, ocrMap);
    	return uuid;
    }
}