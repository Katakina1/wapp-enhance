package com.xforceplus.wapp.modules.einvoice.service.impl;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.xforceplus.wapp.config.SystemConfig;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.einvoice.constant.Constants;
import com.xforceplus.wapp.modules.einvoice.service.EinvoiceUploadService;
import org.apache.commons.codec.binary.Base64;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xforceplus.wapp.modules.collect.pojo.InvoiceDetail;
import com.xforceplus.wapp.modules.collect.pojo.RequestData;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.einvoice.dao.ElectronInvoiceUploadDao;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceEntity;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceImage;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceLog;
import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoice;
import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoiceDetail;
import com.xforceplus.wapp.modules.einvoice.util.DateTimeHelper;
import com.xforceplus.wapp.modules.einvoice.util.ParsePdfUtil;
import com.xforceplus.wapp.modules.einvoice.util.PdfToImgUtil;
import com.xforceplus.wapp.modules.einvoice.util.RarUtil;
import com.xforceplus.wapp.modules.einvoice.util.SFTPHandler;
import com.xforceplus.wapp.modules.einvoice.util.ZipUtil;
import com.xforceplus.wapp.modules.einvoice.util.ZipUtilRead;
import com.ele.parse.entity.FPEntity;
import com.google.common.collect.Lists;

/**
 * @author marvin
 * 电票上传业务层接口实现
 */
@Service("einvoiceUploadService")
@Transactional
public class EinvoiceUploadServiceImpl implements EinvoiceUploadService {
    private final static Logger LOGGER = getLogger(EinvoiceUploadServiceImpl.class);

    /**
     * 上传时本地文件暂存路径
     */
    @Value("${filePathConstan.tempPath}")
    private String tempPath;
    /**
     * 上传时本地文件存放路径
     */
    @Value("${filePathConstan.depositPath}")
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

    private final ElectronInvoiceUploadDao einvoiceUplordDao;

    private final SystemConfig systemConfig;


    @Autowired
    public EinvoiceUploadServiceImpl(ElectronInvoiceUploadDao einvoiceUplordDao, SystemConfig systemConfig) {
        this.einvoiceUplordDao = einvoiceUplordDao;
        this.systemConfig = systemConfig;
    }

    @Override
    public List<ElectronInvoiceEntity> uploadElectronInvoice(String schemaLabel, MultipartFile file, UserEntity user) {
        LOGGER.debug("----------------电票上传开始--------------------");
        List<ElectronInvoiceEntity> invoices = Lists.newArrayList();
        //文件名称
        String fileName = this.getOriginalFilename(file.getOriginalFilename());
        //上传文件重新命名
        String pdfFileName = DateTimeHelper.formatNowDate(Constants.FILE_DATE_FORMAT) + fileName.substring(fileName.indexOf("."));
        //创建临时存储文件夹
        this.createFileDir();
        File newFile = new File(tempPath + pdfFileName);
        try {
            file.transferTo(newFile);
            if (fileName.contains(Constants.ZIP_FILE)) {
                //若用户上传的是zip压缩文件,对文件进行解压
                ZipUtil.unzip(tempPath + pdfFileName);
                // 对上传的文件进行移动保存
                PdfToImgUtil.move(newFile, depositPath);
            } else if (fileName.contains(Constants.RAR_FILE)) {
                //若用户上传的是rar压缩文件,对文件进行解压
                RarUtil.unRarFile(tempPath + pdfFileName, tempPath);
                // 对上传的文件进行移动保存
                PdfToImgUtil.move(newFile, depositPath);
            } else {
                //对用pdf上传，压缩pdf文件，然后保存
                ZipUtil.zip(newFile, Boolean.FALSE);
                List<File> zipFiles = PdfToImgUtil.findFile(tempPath, Constants.ZIP_FILE_POINT);
                for (File zipFile : zipFiles) {
                    // 对上传的文件进行移动保存
                    PdfToImgUtil.move(zipFile, depositPath);
                }
            }
            // 获取临时文件夹里的所有pdf文件
            List<File> files = PdfToImgUtil.findFile(tempPath, Constants.PDF_FILE);
            //解析并保存电票信息
            invoices = this.analysisElectronFile(schemaLabel, files, user, fileName);
        } catch (Exception e) {
            final ElectronInvoiceEntity entity = new ElectronInvoiceEntity();
            entity.setReadPdfSuccess(Boolean.FALSE);
            entity.setPdfName(fileName);
            invoices.add(entity);
            LOGGER.debug("----------------电票上传异常--------------------:{}" , e);
        } finally {
            //清空临时文件里面的文件
            ParsePdfUtil.deleteFile(tempPath);
        }
        LOGGER.debug("----------------电票上传完成--------------------");
        return invoices;
    }

    @Override
    public Long saveElectronInvoice(String schemaLabel, ElectronInvoiceEntity invoiceEntity) {
        einvoiceUplordDao.saveElectronInvoice(schemaLabel, invoiceEntity);
        return invoiceEntity.getId();
    }

    @Override
    public void saveElectronInvoiceLog(String schemaLabel, ElectronInvoiceLog invoiceLog) {
        einvoiceUplordDao.saveElectronLog(schemaLabel, invoiceLog);
    }

    @Override
    public void saveOrUpdateInvoiceImage(String schemaLabel, ElectronInvoiceImage invoiceImage, Boolean isSave) {
        if(isSave){
            einvoiceUplordDao.saveElectronImg(schemaLabel, invoiceImage);
        }else {
            einvoiceUplordDao.updateElectronInvoiceImg(schemaLabel, invoiceImage);
        }

    }

    @Override
    public Boolean deleteElectronInvoice(String schemaLabel, Long id) {
        //根据要删除发票的id查询此发票的所有信息
        ElectronInvoiceEntity invoiceEntity = this.selectElectronInvoiceAll(schemaLabel, id);
        //保存要删除发票的信息到删除表
        this.saveOrUpdateDelInvoice(schemaLabel, invoiceEntity);
        //判断此发票是否签收成功
        if (Constants.INVOICE_QS_STATUS_SUCCESS_ONE.equals(invoiceEntity.getQsStatus())) {
            //签收成功，删除底账表数据
            einvoiceUplordDao.deleteRecordInvoice(schemaLabel, invoiceEntity.getUuid());
            //删除明细表数据
            einvoiceUplordDao.deleteRecordInvoiceDetail(schemaLabel, invoiceEntity.getUuid());
        }
        //删除图片
        this.delInvoiceImgIncludeSFTP(schemaLabel, invoiceEntity);
        return einvoiceUplordDao.deleteElectronInvoice(schemaLabel, id) > 0;
    }

    @Override
    public int saveRecordInvoice(String schemaLabel, RecordInvoice recordInvoice) {
        return einvoiceUplordDao.saveRecordInvoice(schemaLabel, recordInvoice);
    }

    @Override
    public int saveRecordInvoiceDetail(String schemaLabel, List<RecordInvoiceDetail> details) {
        return einvoiceUplordDao.saveRecordInvoiceDetail(schemaLabel, details);
    }

    @Override
    public ElectronInvoiceEntity saveInputElectronInvoice(String schemaLabel, ElectronInvoiceEntity invoiceEntity, UserEntity user) {
        //根据用户输入的发票号码和发票代码组成的UUID查询是否此发票已存入
        ElectronInvoiceEntity isAlreadySaveInvoice = this.selectElectronInvoice(schemaLabel, null,
                invoiceEntity.getInvoiceCode() + invoiceEntity.getInvoiceNo());

        if (isAlreadySaveInvoice != null) {
            //若此发票已经录入
            invoiceEntity = isAlreadySaveInvoice;
            invoiceEntity.setNotes(Constants.UPLOAD_REPEAT_INVOICE);
            invoiceEntity.setSaveRepeat(Boolean.TRUE);
            //设置手工录入的发票类型
            String invoiceType = this.getInvoiceCodeType(invoiceEntity.getInvoiceCode());
            invoiceEntity.setInvoiceType(invoiceType);
            invoiceEntity.setId(null);
        } else {
            //设置UUid
            invoiceEntity.setUuid(invoiceEntity.getInvoiceCode() + invoiceEntity.getInvoiceNo());
            //设置手工录入的发票类型
            String invoiceType = this.getInvoiceCodeType(invoiceEntity.getInvoiceCode());
            invoiceEntity.setInvoiceType(invoiceType);
            //设置当前登录人信息
            invoiceEntity.setUserAccount(user.getLoginname());
            invoiceEntity.setUserName(user.getUsername());
            //获取用户关联的税号
            List<String> userTaxNos = this.getUserTaxNos(schemaLabel, user.getUserid().longValue());
            //判断发票类型是电子发票还是通行费发票
            if (Constants.COMMON_ELECTRON_INVOICE_TYPE.equals(invoiceType)) {
                //手工录入电子票查验签收并保存
                this.checkInputElectronInvoice(schemaLabel, invoiceEntity, userTaxNos);
            } else if (Constants.TOLL_ELECTRON_INVOICE_TYPE.equals(invoiceType)) {
                //签收通行费发票
                this.signTollElectronInvoice(schemaLabel, invoiceEntity, userTaxNos);
            }
        }
        return invoiceEntity;
    }

    @Override
    public ElectronInvoiceEntity selectElectronInvoice(String schemaLabel, Long id, String uuid) {
        return einvoiceUplordDao.selectElectronInvoiceById(schemaLabel, id, uuid);
    }

    @Override
    public ElectronInvoiceEntity saveUpdateElectronInvoice(String schemaLabel, ElectronInvoiceEntity invoiceEntity, Long userId) {

        //获取用户关联的税号
        List<String> userTaxNos = this.getUserTaxNos(schemaLabel, userId);
        if (Constants.COMMON_ELECTRON_INVOICE_TYPE.equals(this.getInvoiceCodeType(invoiceEntity.getInvoiceCode()))) {
            //查验普通的电子发票
            this.checkUpdateInvoiceAndSave(schemaLabel, invoiceEntity, userTaxNos);
        } else if (Constants.TOLL_ELECTRON_INVOICE_TYPE.equals(this.getInvoiceCodeType(invoiceEntity.getInvoiceCode()))) {
            //签收修改的通行费发票
            this.tollSignForUpdate(schemaLabel, invoiceEntity, userTaxNos);
        }
        return invoiceEntity;
    }

    @Override
    public Boolean updateElectronInvoice(String schemaLabel, ElectronInvoiceEntity invoiceEntity) {
        return einvoiceUplordDao.updateElectronInvoice(schemaLabel, invoiceEntity) > 0;
    }

    @Override
    public String getInvoiceImage(String schemaLabel, Long id, UserEntity user) {
        //获取scanId
        ElectronInvoiceEntity invoiceEntity = this.selectElectronInvoice(schemaLabel, id, null);
        //获取图片实体
        ElectronInvoiceImage invoiceImage = this.getElectronInvoiceImage(schemaLabel, null, invoiceEntity.getScanId());
        SFTPHandler imageHandler = SFTPHandler.getHandler(remoteImageRootPath, localImageRootPath);
        try {
            if (null != invoiceImage) {
                imageHandler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                String userAccount = user.getLoginname();
                imageHandler.download(invoiceImage.getImagePath(), userAccount + Constants.ZIP_FILE);
                byte[] zipFile = ZipUtilRead.readZipFile(imageHandler.getLocalImageRootPath() + userAccount + Constants.ZIP_FILE);
                final Base64 base64 = new Base64();
                return new String(base64.encode(zipFile), "UTF-8");
            }
        } catch (Exception e) {
            LOGGER.debug("----获取图片异常---" + e);
        } finally {
            if (imageHandler != null) {
                imageHandler.closeChannel();
            }
        }
        return "";
    }

    @Override
    public void getInvoiceImageForAll(String schemaLabel, Long id, UserEntity user, HttpServletResponse response) {
        //获取scanId
        ElectronInvoiceEntity invoiceEntity = this.selectElectronInvoice(schemaLabel, id, null);
        //获取图片实体
        ElectronInvoiceImage invoiceImage = this.getElectronInvoiceImage(schemaLabel, null, invoiceEntity.getScanId());
        SFTPHandler imageHandler = SFTPHandler.getHandler(remoteImageRootPath, localImageRootPath);
        try {
            if (null != invoiceImage) {
                imageHandler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                String userAccount = user.getLoginname();
                response.setContentType("image/png");
                String name = invoiceEntity.getScanId();
                response.reset();
                response.addHeader("Content-Disposition", "attachment;filename=" + name);
                OutputStream output = response.getOutputStream();
                imageHandler.download(invoiceImage.getImagePath(), userAccount + Constants.ZIP_FILE);
                byte[] zipFile = ZipUtilRead.readZipFile(imageHandler.getLocalImageRootPath() + userAccount + Constants.ZIP_FILE);

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
        } catch (Exception e) {
            LOGGER.debug("----获取图片异常---" + e);
        } finally {
            if (imageHandler != null) {
                imageHandler.closeChannel();
            }
        }
    }

    @Override
    public ElectronInvoiceImage getElectronInvoiceImage(String schemaLabel, String uuid, String scanId) {
        return einvoiceUplordDao.getImg(schemaLabel, uuid, scanId);
    }

    @Override
    public ElectronInvoiceEntity selectElectronInvoiceAll(String schemaLabel, Long id) {
        return einvoiceUplordDao.selectElectronInvoiceAll(schemaLabel, id);
    }

    @Override
    public int saveOrUpdateDelInvoice(String schemaLabel, ElectronInvoiceEntity invoiceEntity) {
        Integer count = einvoiceUplordDao.selectDelInvoiceCount(schemaLabel, invoiceEntity.getUuid());
        if (count != null && count > 0) {
            return einvoiceUplordDao.updateDelRecordInvoice(schemaLabel, invoiceEntity);
        }
        return einvoiceUplordDao.saveDelElectronInvoice(schemaLabel, invoiceEntity);
    }

    @Override
    public RecordInvoice selectRecordInvoice(String schemaLabel, String uuid) {
        return einvoiceUplordDao.selectRecordInvoiceAll(schemaLabel, uuid);
    }

    @Override
    public Boolean checkUserTaxNoPower(String schemaLabel, Long userId, String gfTaxNo) {
        if (null == schemaLabel || schemaLabel.isEmpty() || null == userId) {
            return Boolean.FALSE;
        }
        //根据用户id查询用户关联的购方税号
        List<String> userTaxNos = einvoiceUplordDao.selectGfTaxNo(schemaLabel, userId);

        return this.checkUserTaxNoLimit(gfTaxNo, userTaxNos);
    }

    @Override
    public void delInvoiceImgIncludeSFTP(String schemaLabel, Long id, String uuid) {
        //验证数据schemaLabel不为空，id和uuid不能同时为空
        Boolean validateParam = null == schemaLabel || schemaLabel.isEmpty() || (null == id && (null == uuid || uuid.isEmpty()));
        if (validateParam) {
            return;
        }
        //根据id或者uuid查询发票信息
        ElectronInvoiceEntity invoice = this.selectElectronInvoice(schemaLabel, id, uuid);

        if (null == invoice) {
            return;
        }
        //删除图片
        this.delInvoiceImgIncludeSFTP(schemaLabel, invoice);
    }

    /**
     * 删除发票上传是的图片，包括同时删除sftp上面的图片
     *
     * @param schemaLabel   当前用户所在的分库名
     * @param invoiceEntity 扫描表实体信息
     */
    private void delInvoiceImgIncludeSFTP(String schemaLabel, ElectronInvoiceEntity invoiceEntity) {
        //获取图片实体
        if (null != invoiceEntity.getScanId()) {
            ElectronInvoiceImage invoiceImage = this.getElectronInvoiceImage(schemaLabel, null, invoiceEntity.getScanId());
            if (null != invoiceImage) {
                LOGGER.debug("--------------删除sftp图片------------");
                // 删除sftp文件服务器上的zip压缩图片
                SFTPHandler imageHandler = SFTPHandler.getHandler(remoteImageRootPath, localImageRootPath);
                try {
                    imageHandler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
                    imageHandler.deleteRemote(invoiceImage.getImagePath());
                    LOGGER.debug("--------------删除sftp图片成功------------");
                } catch (Exception e) {
                    LOGGER.debug("----删除sftp图片时异常---" + e);
                } finally {
                    //关闭远程sftp连接
                    imageHandler.closeChannel();
                    LOGGER.debug("--------------关闭远程sftp连接成功------------");
                }
            }
        }

        //删除本地保存的图片路径信息
        einvoiceUplordDao.deleteInvoiceImg(schemaLabel, invoiceEntity.getUuid());
    }

    /**
     * 修改时，签收通行费发票
     *
     * @param schemaLabel 当前用户所在的分库名 不可以为空
     * @param invoice     修改的信息
     * @param userTaxNos  当前登录用户关联的税号
     */
    private void tollSignForUpdate(String schemaLabel, ElectronInvoiceEntity invoice, List<String> userTaxNos) {
        //查询底账表信息
        RecordInvoice recordInvoice = this.selectRecordInvoice(schemaLabel,
                invoice.getInvoiceCode() + invoice.getInvoiceNo());
        if (null != recordInvoice) {
            //判读用户是否由此发票的操作权限
            if (this.checkUserTaxNoLimit(recordInvoice.getGfTaxNo(), userTaxNos)) {
                //查询到底账表数据，判读签收
                //如果发票代码、发票号码、开票日期和校验码都一致，则签收成功，并更新扫描表中的数据
                if (recordInvoice.getInvoiceCode().equals(invoice.getInvoiceCode())
                        && recordInvoice.getInvoiceNo().equals(invoice.getInvoiceNo())
                        && DateTimeHelper.formatDate(recordInvoice.getInvoiceDate()).equals(DateTimeHelper.formatDate(invoice.getInvoiceDate()))
                        && recordInvoice.getCheckCode() != null && recordInvoice.getCheckCode().endsWith(invoice.getCheckCode())) {
                    //更新底账表数据，并设置设置扫描表数据
                    this.setTollToSign(schemaLabel, invoice, recordInvoice);
                    //更新扫描表数据
                    this.updateElectronInvoice(schemaLabel, invoice);
                } else {
                    //签收失败
                    invoice.setCheckSuccess(Boolean.FALSE);
                    invoice.setResultTip(Constants.TOLL_INVOICE_QS_FAIL);
                }
            } else {
                //若没有税号权限
                invoice.setCheckSuccess(Boolean.FALSE);
                invoice.setResultTip(Constants.INVOICE_QS_FAIL_NO_TAX);
            }
        } else {
            //如果返回的底账信息为空，则表示查验失败
            invoice.setCheckSuccess(Boolean.FALSE);
            invoice.setResultTip(Constants.TOLL_INVOICE_QS_FAIL_NO_RECORD);
        }
    }

    /**
     * 签收通行费发票
     *
     * @param schemaLabel 当前用户所在的分库名 不可以为空
     * @param invoice     要签收的发票信息
     * @param userTaxNos  当前登录用户关联的税号
     */
    private void signTollElectronInvoice(String schemaLabel, ElectronInvoiceEntity invoice, List<String> userTaxNos) {
        //查询底账表数据，准备签收
        RecordInvoice recordInvoice = this.selectRecordInvoice(schemaLabel,
                invoice.getInvoiceCode() + invoice.getInvoiceNo());
        //设置发票类型为通行费发票
        invoice.setInvoiceType(Constants.TOLL_ELECTRON_INVOICE_TYPE);
        if (null != recordInvoice) {
            //判读用户是否由此发票的操作权限
            if (this.checkUserTaxNoLimit(recordInvoice.getGfTaxNo(), userTaxNos)) {
                //查询到底账表数据，判读签收
                //如果发票代码、发票号码、开票日期和校验码都一致，则签收成功，并保存数据到扫描表中
                if (recordInvoice.getInvoiceCode().equals(invoice.getInvoiceCode())
                        && recordInvoice.getInvoiceNo().equals(invoice.getInvoiceNo())
                        && DateTimeHelper.formatDate(recordInvoice.getInvoiceDate()).equals(DateTimeHelper.formatDate(invoice.getInvoiceDate()))
                        && recordInvoice.getCheckCode() != null && recordInvoice.getCheckCode().endsWith(invoice.getCheckCode())) {
                    //更新底账表数据，并设置设置扫描表数据
                    this.setTollToSign(schemaLabel, invoice, recordInvoice);
                } else {
                    invoice.setNotes(Constants.INVOICE_QS_FAIL_ZH);
                    invoice.setQsStatus(Constants.INVOICE_QS_STATUS_FAIL_ZERO);
                    invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
                }
                //保存信息
                invoice.setId(this.saveElectronInvoice(schemaLabel, invoice));
            } else {
                //若没有税号权限
                invoice.setNotes(Constants.INVOICE_QS_FAIL_NO_TAX);
                invoice.setQsStatus(Constants.INVOICE_QS_STATUS_FAIL_ZERO);
                invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
                invoice.setScanId(null);
            }
        } else {
            //若没有查询到底账表数据
            invoice.setNotes(Constants.TOLL_INVOICE_QS_FAIL_NO_RECORD);
            invoice.setQsStatus(Constants.INVOICE_QS_STATUS_FAIL_ZERO);
            invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
            //保存信息
            invoice.setId(this.saveElectronInvoice(schemaLabel, invoice));
        }

    }

    /**
     * 更新底账表的数据，并设置扫描表的数据
     *
     * @param schemaLabel   当前用户所在的分库名 不可以为空
     * @param invoice       扫描表数据
     * @param recordInvoice 底账表数据
     */
    private void setTollToSign(String schemaLabel, ElectronInvoiceEntity invoice, RecordInvoice recordInvoice) {
        invoice.setGfTaxNo(recordInvoice.getGfTaxNo());
        invoice.setGfName(recordInvoice.getGfName());
        invoice.setInvoiceType(recordInvoice.getInvoiceType());
        invoice.setXfTaxNo(recordInvoice.getXfTaxNo());
        invoice.setXfName(recordInvoice.getXfName());
        invoice.setInvoiceDate(recordInvoice.getInvoiceDate());
        invoice.setUserAccount(recordInvoice.getGxUserAccount());
        invoice.setUserName(recordInvoice.getGxUserName());
        invoice.setInvoiceAmount(recordInvoice.getInvoiceAmount());
        invoice.setTaxAmount(recordInvoice.getTaxAmount());
        invoice.setTotalAmount(recordInvoice.getTotalAmount());
        invoice.setNotes(Constants.INVOICE_QS_SUCCESS_ZH);
        invoice.setQsStatus(Constants.INVOICE_QS_STATUS_SUCCESS_ONE);
        invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
        //更新底账表签收日期、签收时间、签收方式
        einvoiceUplordDao.updateRecordInvoice(schemaLabel, recordInvoice.getUuid());
    }

    /**
     * 手工录入的电子发票查验签收保存
     *
     * @param schemaLabel 当前用户所在的分库名
     * @param invoice     录入的电票信息
     * @param userTaxNos  当前登录人关联的税号
     */
    private void checkInputElectronInvoice(String schemaLabel, ElectronInvoiceEntity invoice, List<String> userTaxNos) {

        //发送数据并且得到查验回来的数据
        RecordInvoice recordInvoice = this.getCheckBackData(invoice);
        if (Constants.CHECK_INVOICE_SUCCESS_CODE.equals(recordInvoice.getResultCode())) {
            //判断是否有操作此发票的权限
            if (this.checkUserTaxNoLimit(recordInvoice.getGfTaxNo(), userTaxNos)) {
                //如果购方税号为空，这只默认发票号码
                if ("".equals(recordInvoice.getGfTaxNo()) || recordInvoice.getGfTaxNo() == null || (" ").equals(recordInvoice.getGfTaxNo())) {
                    recordInvoice.setGfTaxNo(Constants.DEFAULT_ELECTRON_INVOICE_GF_TAX_NO);
                }
                if (Constants.CHECK_INVOICE_FAIL_CODE.equals(recordInvoice.getInvoiceStatus())) {
                    //若发票作废在更新保存的信息
                    invoice.setNotes(Constants.INVOICE_QS_FAIL_Z_ZH);
                    invoice.setQsStatus(Constants.INVOICE_QS_STATUS_FAIL_ZERO);
                    invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
                } else {
                    invoice.setNotes(Constants.INVOICE_QS_SUCCESS_ZH);
                    invoice.setQsStatus(Constants.INVOICE_QS_STATUS_SUCCESS_ONE);
                    invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
                    invoice.setGfTaxNo(recordInvoice.getGfTaxNo());
                    invoice.setGfName(recordInvoice.getGfName());
                    invoice.setXfTaxNo(recordInvoice.getXfTaxNo());
                    invoice.setXfName(recordInvoice.getXfName());
                    invoice.setTotalAmount(recordInvoice.getTotalAmount());
                    invoice.setTaxAmount(recordInvoice.getTaxAmount());
                    invoice.setInvoiceAmount(recordInvoice.getInvoiceAmount());
                    invoice.setInvoiceDate(recordInvoice.getInvoiceDate());
                    invoice.setUuid(recordInvoice.getInvoiceCode() + recordInvoice.getInvoiceNo());
                    invoice.setCreateDate(new Date());
                    invoice.setUpdateDate(new Date());
                    invoice.setInvoiceType(Constants.COMMON_ELECTRON_INVOICE_TYPE);
                    recordInvoice.setQsStatus(Constants.INVOICE_QS_STATUS_SUCCESS_ONE);

                    //保存底账信息
                    this.saveRecordInvoice(schemaLabel, recordInvoice);
                    //保存商品明细信息
                    if (recordInvoice.getDetailList() != null && !recordInvoice.getDetailList().isEmpty()) {
                        this.saveRecordInvoiceDetail(schemaLabel, recordInvoice.getDetailList());
                    }
                    //保存电票扫描信息到扫描表
                    invoice.setId(this.saveElectronInvoice(schemaLabel, invoice));
                }
            } else {
                //若没有税号权限
                invoice.setNotes(Constants.INVOICE_QS_FAIL_NO_TAX);
                invoice.setQsStatus(Constants.INVOICE_QS_STATUS_FAIL_ZERO);
                invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
            }
        } else {
            //如果返回的底账信息为空，则表示查验失败
            invoice.setCheckSuccess(Boolean.FALSE);
            invoice.setResultTip(recordInvoice.getResultTip());
            invoice.setNotes(recordInvoice.getResultTip());
            invoice.setQsStatus(Constants.INVOICE_QS_STATUS_FAIL_ZERO);
            invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
            //保存电票扫描信息到扫描表
            invoice.setId(this.saveElectronInvoice(schemaLabel, invoice));
        }
    }

    /**
     * 修改保存的方法入口，查验验收修改的发票，并且同事更新这个发票新，同时把查验回来的数据更新到底账表中
     *
     * @param schemaLabel 当前用户所在的分库名
     * @param invoice     修改的电票信息
     * @param userTaxNos  当前用户关联的税号集合
     */
    private void checkUpdateInvoiceAndSave(String schemaLabel, ElectronInvoiceEntity invoice, List<String> userTaxNos) {

        //发送数据并且得到查验回来的数据
        RecordInvoice recordInvoice = this.getCheckBackData(invoice);
        if (Constants.CHECK_INVOICE_SUCCESS_CODE.equals(recordInvoice.getResultCode())) {
            //判断是否有操作此发票的权限
            if (this.checkUserTaxNoLimit(recordInvoice.getGfTaxNo(), userTaxNos)) {
                //如果购方税号为空，这只默认发票号码
                if ("".equals(recordInvoice.getGfTaxNo()) || recordInvoice.getGfTaxNo() == null || (" ").equals(recordInvoice.getGfTaxNo())) {
                    recordInvoice.setGfTaxNo(Constants.DEFAULT_ELECTRON_INVOICE_GF_TAX_NO);
                }
                if (Constants.CHECK_INVOICE_FAIL_CODE.equals(recordInvoice.getInvoiceStatus())) {
                    //若发票作废在更新保存的信息
                    invoice.setNotes(Constants.INVOICE_QS_FAIL_ZH);
                    invoice.setQsStatus(Constants.INVOICE_QS_STATUS_FAIL_ZERO);
                    //修改发票信息
                    this.updateElectronInvoice(schemaLabel, invoice);
                } else {
                    invoice.setNotes(Constants.INVOICE_QS_SUCCESS_ZH);
                    invoice.setQsStatus(Constants.INVOICE_QS_STATUS_SUCCESS_ONE);
                    recordInvoice.setQsStatus(Constants.INVOICE_QS_STATUS_SUCCESS_ONE);
                    invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
                    invoice.setGfTaxNo(recordInvoice.getGfTaxNo());
                    invoice.setGfName(recordInvoice.getGfName());
                    invoice.setXfTaxNo(recordInvoice.getXfTaxNo());
                    invoice.setXfName(recordInvoice.getXfName());
                    invoice.setTotalAmount(recordInvoice.getTotalAmount());
                    invoice.setTaxAmount(recordInvoice.getTaxAmount());
                    invoice.setInvoiceAmount(recordInvoice.getInvoiceAmount());
                    invoice.setInvoiceDate(recordInvoice.getInvoiceDate());
                    invoice.setUuid(recordInvoice.getInvoiceCode() + recordInvoice.getInvoiceNo());
                    invoice.setUpdateDate(new Date());
                    invoice.setInvoiceType(Constants.COMMON_ELECTRON_INVOICE_TYPE);

                    //查验成功修改电票信息
                    this.updateElectronInvoice(schemaLabel, invoice);

                    //保存底账信息
                    this.saveRecordInvoice(schemaLabel, recordInvoice);
                    //保存商品明细信息
                    if (recordInvoice.getDetailList() != null && !recordInvoice.getDetailList().isEmpty()) {
                        this.saveRecordInvoiceDetail(schemaLabel, recordInvoice.getDetailList());
                    }
                }
            } else {
                //若没有税号权限
                invoice.setCheckSuccess(Boolean.FALSE);
                invoice.setResultTip(Constants.INVOICE_QS_FAIL_NO_TAX);
            }
        } else {
            //如果返回的底账信息为空，则表示查验失败
            invoice.setCheckSuccess(Boolean.FALSE);
            invoice.setResultTip(recordInvoice.getResultTip());
        }
    }

    /**
     * 解析上传的电票文件
     *
     * @param schemaLabel 当前用户所在的分库名
     * @param files       电票文件
     * @param user        当前登录人信息
     * @param pdfFileName 当用户上传pdf文件的名称
     */
    private List<ElectronInvoiceEntity> analysisElectronFile(String schemaLabel, List<File> files, UserEntity user, String pdfFileName) {
        List<ElectronInvoiceEntity> invoices = Lists.newArrayList();
        //存放文件解析的数据
        Map<String, Object> map = null;
        //图片存储路径
        String uploadImg = "";
        //连接远程ftp服务器，上传电票图片
        SFTPHandler handler = SFTPHandler.getHandler(remoteImageRootPath, tempPath);
        File exceptionFile = null;
        try {
            //连接远程服务器
            handler.openChannel(host, userName, password, Integer.parseInt(defaultPort), Integer.parseInt(defaultTimeout));
            for (File file : files) {
                exceptionFile = file;
                //解析pdf文件
                map = PdfToImgUtil.getImg(file, tempPath);
                if (map.containsKey(Constants.MAP_KEY_FILE_NAME)) {
                    //处理发票解析失败
                    ElectronInvoiceEntity expectionPdf = new ElectronInvoiceEntity();
                    if (pdfFileName.endsWith(Constants.PDF_FILE)) {
                        expectionPdf.setPdfName(pdfFileName);
                    } else {
                        expectionPdf.setPdfName((String) map.get(Constants.MAP_KEY_FILE_NAME));
                    }
                    expectionPdf.setReadPdfSuccess(false);
                    invoices.add(expectionPdf);
                    continue;
                }
                uploadImg = handler.uploadImg((String) map.get("gfTaxNo"), DateTimeHelper.formatDat(new Date()), ((File) map.get("zip")).getName());
                //扫描图片唯一识别id
                String scanId = UUID.randomUUID().toString().replace("-", "");
                //获取解析的发票信息
                ElectronInvoiceEntity invoiceEntity = this.getElectronInvoiceToSave((FPEntity) map.get("fp"), scanId, user);
                //设置图片上传路径
                invoiceEntity.setImgPath(uploadImg + ((File) map.get(Constants.ZIP_FILE_POINT)).getName());
                if (pdfFileName.endsWith(Constants.PDF_FILE)) {
                    invoiceEntity.setPdfName(pdfFileName);
                } else {
                    invoiceEntity.setPdfName(file.getName());
                }
                invoiceEntity.setReadPdfSuccess(Boolean.TRUE);
                invoices.add(invoiceEntity);
            }
            //查验发票并保存
            this.checkElectronInvoice(schemaLabel, invoices, user.getUserid().longValue());
        } catch (Exception e) {
            LOGGER.debug("-----------------上传图片异常------------------" + e);
            final ElectronInvoiceEntity entity = new ElectronInvoiceEntity();
            entity.setReadPdfSuccess(Boolean.FALSE);
            if (pdfFileName.endsWith(Constants.PDF_FILE)) {
                entity.setPdfName(pdfFileName);
            } else {
                if(exceptionFile!=null) {
                    entity.setPdfName(exceptionFile.getName());
                }
            }
            invoices.add(entity);
        } finally {
            //关闭远程sftp连接
            handler.closeChannel();
            LOGGER.debug("--------------关闭远程sftp连接成功------------");
        }
        return invoices;
    }

    /**
     * 查验签收发票
     *
     * @param schemaLabel 当前用户所在的分库名
     * @param invoices    需要查验签收的发票
     * @param userId      当前登录用户的id
     */
    private void checkElectronInvoice(String schemaLabel, List<ElectronInvoiceEntity> invoices, Long userId) {
        //获取用户关联的税号
        List<String> userTaxNos = this.getUserTaxNos(schemaLabel, userId);
        for (ElectronInvoiceEntity invoice : invoices) {
            //判断此pdf是否解析成功，若没有解析成功，则不走查验
            if (!invoice.getReadPdfSuccess()) {
                continue;
            }
            //判断此pdf是否上传过，若上传过则不走查验
            ElectronInvoiceEntity selectInvoiceEntity = this.selectElectronInvoice(schemaLabel, null,
                    invoice.getInvoiceCode() + invoice.getInvoiceNo());
            if (selectInvoiceEntity != null) {
                invoice.setNotes(Constants.UPLOAD_REPEAT_INVOICE);
                invoice.setQsStatus(selectInvoiceEntity.getQsStatus());
                invoice.setSaveRepeat(Boolean.TRUE);
                continue;
            }

            //判断发票类型
            if (Constants.COMMON_ELECTRON_INVOICE_TYPE.equals(this.getInvoiceCodeType(invoice.getInvoiceCode()))) {
                //普通电子发票查验
                this.upLoadCheckCallBack(schemaLabel, invoice, userTaxNos);
            } else if (Constants.TOLL_ELECTRON_INVOICE_TYPE.equals(this.getInvoiceCodeType(invoice.getInvoiceCode()))) {
                //通行费电子发票查询底账表签收
                this.signTollElectronInvoice(schemaLabel, invoice, userTaxNos);
            }

            //如果登录人没有权限，则没有scanId和id，则不用保存记录和图片路径
            if (null != invoice.getScanId() && null != invoice.getId()) {
                //保存电票上传记录
                this.saveElectronLog(schemaLabel, invoice.getImgPath(),
                        invoice.getInvoiceCode(), invoice.getInvoiceNo(), invoice.getPdfName());
                //电票图片路径保存
                ElectronInvoiceImage image = this.getElectronInvoiceImage(schemaLabel, invoice.getInvoiceCode() + invoice.getInvoiceNo(), null);

                this.saveOrUpdateInvoiceImage(schemaLabel, invoice.getImgPath(),
                        invoice.getInvoiceCode(), invoice.getInvoiceNo(), invoice.getScanId(), null == image);
            }
        }
    }

    /**
     * 上传电票保存查验回来的数据
     *
     * @param schemaLabel 当前用户所在的分库名
     * @param invoice     电票信息
     * @param userTaxNos  当前登录用户的关系税号集合
     */
    private void upLoadCheckCallBack(String schemaLabel, ElectronInvoiceEntity invoice, List<String> userTaxNos) {
        //发送数据并且得到查验回来的数据
        RecordInvoice recordInvoice = this.getCheckBackData(invoice);
        if (Constants.CHECK_INVOICE_SUCCESS_CODE.equals(recordInvoice.getResultCode())) {
            //判断该用户是否有此发票的数据权限
            if (this.checkUserTaxNoLimit(recordInvoice.getGfTaxNo(), userTaxNos)) {
                //如果购方税号为空，这只默认发票号码
                if ("".equals(recordInvoice.getGfTaxNo()) || recordInvoice.getGfTaxNo() == null || (" ").equals(recordInvoice.getGfTaxNo())) {
                    recordInvoice.setGfTaxNo(Constants.DEFAULT_ELECTRON_INVOICE_GF_TAX_NO);
                }
                //判断发票状态
                if (Constants.CHECK_INVOICE_FAIL_CODE.equals(recordInvoice.getInvoiceStatus())) {
                    //若发票作废在更新保存的信息
                    invoice.setNotes(Constants.INVOICE_QS_FAIL_Z_ZH);
                    invoice.setQsStatus(Constants.INVOICE_QS_STATUS_FAIL_ZERO);
                    invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
                    recordInvoice.setQsStatus(Constants.INVOICE_QS_STATUS_FAIL_ZERO);
                } else {
                    invoice.setNotes(Constants.INVOICE_QS_SUCCESS_ZH);
                    invoice.setQsStatus(Constants.INVOICE_QS_STATUS_SUCCESS_ONE);
                    invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
                    invoice.setGfTaxNo(recordInvoice.getGfTaxNo());
                    invoice.setXfTaxNo(recordInvoice.getXfTaxNo());
                    invoice.setTotalAmount(recordInvoice.getTotalAmount());
                    invoice.setTaxAmount(recordInvoice.getTaxAmount());
                    invoice.setInvoiceAmount(recordInvoice.getInvoiceAmount());
                    invoice.setInvoiceDate(recordInvoice.getInvoiceDate());
                    recordInvoice.setQsStatus(Constants.INVOICE_QS_STATUS_SUCCESS_ONE);
                    //保存底账信息
                    this.saveRecordInvoice(schemaLabel, recordInvoice);
                    //保存商品明细信息
                    if (recordInvoice.getDetailList() != null && !recordInvoice.getDetailList().isEmpty()) {
                        this.saveRecordInvoiceDetail(schemaLabel, recordInvoice.getDetailList());
                    }
                }
                //保存电票扫描信息到扫描表
                invoice.setId(this.saveElectronInvoice(schemaLabel, invoice));
            } else {
                //若没有税号权限
                invoice.setNotes(Constants.INVOICE_QS_FAIL_NO_TAX);
                invoice.setQsStatus(Constants.INVOICE_QS_STATUS_FAIL_ZERO);
                invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
                invoice.setScanId(null);
            }
        } else {
            //如果返回的底账信息为空，则表示查验失败
            invoice.setCheckSuccess(Boolean.FALSE);
            invoice.setNotes(recordInvoice.getResultTip());
            invoice.setQsStatus(Constants.INVOICE_QS_STATUS_FAIL_ZERO);
            invoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
            invoice.setResultTip(recordInvoice.getResultTip());
            //保存电票扫描信息到扫描表
            invoice.setId(this.saveElectronInvoice(schemaLabel, invoice));
        }
    }

    /**
     * 获取查验签收回来的数据
     *
     * @param invoice 需要查验的发票信息
     * @return 查验返回的结果
     */
    private RecordInvoice getCheckBackData(ElectronInvoiceEntity invoice) {

        //整理带查验签收发票的请求数据
        RequestData requestData = new RequestData();
        requestData.setCheckCode(invoice.getCheckCode());
        requestData.setInvoiceAmount(invoice.getInvoiceAmount() != null ?
                invoice.getInvoiceAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() : "");
        requestData.setInvoiceNo(invoice.getInvoiceNo());
        requestData.setInvoiceCode(invoice.getInvoiceCode());
        requestData.setInvoiceDate(DateTimeHelper.formatDat(invoice.getInvoiceDate()));
        requestData.setInvoiceType(invoice.getInvoiceType() != null ? invoice.getInvoiceType() : Constants.COMMON_ELECTRON_INVOICE_TYPE);
        requestData.setBuyerTaxNo(systemConfig.getBuyerTaxNo());
        //发送数据差异签收
        ResponseInvoice responseInvoice = null;//invoiceCheckService.sendRequest(requestData);
        //返回整理好的查验数据
        return this.getRRecordInvoiceFromResponse(responseInvoice);
    }

    /**
     * 根据用户id查询用户关联的购方税号
     *
     * @param schemaLabel 当前用户所在的分库名
     * @param userId      当前登录用户的id
     * @return
     */
    private List<String> getUserTaxNos(String schemaLabel, Long userId) {
        List<String> taxNos = Lists.newArrayList();
        //根据用户id查询用户关联的购方税号
        List<String> tempTaxNos = einvoiceUplordDao.selectGfTaxNo(schemaLabel, userId);
        if (!tempTaxNos.isEmpty()) {
            taxNos.addAll(tempTaxNos);
        }
        return taxNos;
    }

    /**
     * 判断用户是否拥有此税号的权限
     *
     * @param taxNo      发票的纳税人识别号
     * @param userTaxNos 用户关联的税号
     * @return true ：拥有   false ：不拥有
     */
    private Boolean checkUserTaxNoLimit(String taxNo, List<String> userTaxNos) {
        return null != taxNo && !" ".equals(taxNo) && !taxNo.isEmpty() && !Constants.DEFAULT_ELECTRON_INVOICE_GF_TAX_NO.equals(taxNo)
                && !userTaxNos.isEmpty() && userTaxNos.contains(taxNo);
    }

    /**
     * 根据发票代码获取发票类型
     *
     * @param invoiceCode 发票代码
     * @return 10：电子发票   14：电子通行费发票
     */
    private String getInvoiceCodeType(String invoiceCode) {
        String invoiceType = Constants.INVOICE_CODE_INIT_TYPE;
        //判断是否是特殊的发票
        if (Constants.SPECIAL_ELECTRON_INVOICE.contains(invoiceCode)) {
            invoiceType = Constants.COMMON_ELECTRON_INVOICE_TYPE;
        }
        //判断是否是普通电子发票或者通行费发票 电子发票和通行费发票长度都是12，且以0开头
        //电子发票已11结尾；通行费发票以12结尾
        if (invoiceCode.length() == Constants.INVOICE_CODE_LENGTH && invoiceCode.startsWith(Constants.INVOICE_CODE_START_WITH)) {
            if (invoiceCode.endsWith(Constants.INVOICE_CODE_END_WITH_ELEVEN)) {
                invoiceType = Constants.COMMON_ELECTRON_INVOICE_TYPE;
            }
            if (invoiceCode.endsWith(Constants.INVOICE_CODE_END_WITH_TWELVE)) {
                invoiceType = Constants.TOLL_ELECTRON_INVOICE_TYPE;
            }
        }

        return invoiceType;
    }

    /**
     * 获取电票的实体
     *
     * @param fpEntity 电票文件解析后的数据
     * @param scanId   扫描图片唯一识别id
     * @param user     当前登录人信息
     * @return 电票的实体
     */
    private ElectronInvoiceEntity getElectronInvoiceToSave(FPEntity fpEntity, String scanId, UserEntity user) {
        ElectronInvoiceEntity invoiceEntity = new ElectronInvoiceEntity();
        String userAccount = user.getLoginname();
        String userName = user.getUsername();
        //购方信息
        invoiceEntity.setGfName(fpEntity.getBuyer_name());
        invoiceEntity.setGfTaxNo(fpEntity.getBuyer_nsrsbh());
        invoiceEntity.setXfTaxNo(fpEntity.getSeller_nsrsbh());
        invoiceEntity.setXfName(fpEntity.getSeller_name());
        invoiceEntity.setInvoiceType(Constants.COMMON_ELECTRON_INVOICE_TYPE);
        Date kprq = null;
        try {
            kprq = DateTimeHelper.parseDateTime(fpEntity.getKprq(), Constants.INVOICE_KPRQ_FORMAT_ZH);
        } catch (Exception e) {
            kprq = DateTimeHelper.parseDateTime(fpEntity.getKprq(), Constants.INVOICE_KPRQ_FORMAT);
        }
        invoiceEntity.setInvoiceDate(kprq);
        Boolean flag = Boolean.FALSE;
        try {
            invoiceEntity.setInvoiceAmount(new BigDecimal(fpEntity.getHjje()));
        } catch (Exception e) {
            invoiceEntity.setInvoiceAmount(BigDecimal.ZERO);
            flag = true;
        }

        try {
            invoiceEntity.setTaxAmount(new BigDecimal(fpEntity.getHjse()));
        } catch (Exception e) {
            invoiceEntity.setTaxAmount(BigDecimal.ZERO);
            flag = true;
        }
        try {
            invoiceEntity.setTotalAmount(new BigDecimal(fpEntity.getJshj()));
        } catch (Exception e) {
            invoiceEntity.setTotalAmount(BigDecimal.ZERO);
            flag = true;
        }

        if (flag) {
            invoiceEntity.setNotes("发票扫描异常");
        } else {
            invoiceEntity.setNotes("未签收");
        }
        invoiceEntity.setInvoiceCode(fpEntity.getFpdm());
        invoiceEntity.setInvoiceNo(fpEntity.getFphm());
        invoiceEntity.setUuid(fpEntity.getFpdm() + fpEntity.getFphm());

        invoiceEntity.setScanId(scanId);
        invoiceEntity.setUserAccount(userAccount);
        invoiceEntity.setUserName(userName);
        invoiceEntity.setCreateDate(new Date());
        invoiceEntity.setUpdateDate(new Date());
        invoiceEntity.setCheckCode(fpEntity.getJym().substring(fpEntity.getJym().length() - 6));
        return invoiceEntity;
    }

    /**
     * 保存电票上传记录
     *
     * @param schemaLabel 当前用户所在的分库名
     * @param imgPath     保存的图片路径
     * @param invoiceCode 发票代码
     * @param invoiceNo   发票号码
     * @param fileName    pdf文件名
     */
    private void saveElectronLog(String schemaLabel, String imgPath, String invoiceCode, String invoiceNo, String fileName) {
        ElectronInvoiceLog invoiceLog = new ElectronInvoiceLog();
        invoiceLog.setFilePath(imgPath);
        invoiceLog.setInvoiceCode(invoiceCode);
        invoiceLog.setInvoiceNo(invoiceNo);
        invoiceLog.setFileName(fileName);
        this.saveElectronInvoiceLog(schemaLabel, invoiceLog);
    }

    /**
     * 保存电票上传的图片路径
     *
     * @param schemaLabel 当前用户所在的分库名
     * @param imgPath     图片路径
     * @param invoiceCode 发票代码
     * @param invoiceNo   发票号码
     * @param scanId      扫描图片唯一识别id
     * @param isSave      是否是保存true：保存    false：更新
     */
    private void saveOrUpdateInvoiceImage(String schemaLabel, String imgPath, String invoiceCode, String invoiceNo, String scanId, Boolean isSave) {
        ElectronInvoiceImage invoiceImage = this.getImageEntityForSaveOrUpdate(imgPath, invoiceCode, invoiceNo, scanId);

        this.saveOrUpdateInvoiceImage(schemaLabel, invoiceImage, isSave);

    }

    /**
     * 获取需要的图片实体信息
     *
     * @param imgPath     图片路径
     * @param invoiceCode 发票代码
     * @param invoiceNo   发票号码
     * @param scanId      扫描图片唯一识别id
     * @return
     */
    private ElectronInvoiceImage getImageEntityForSaveOrUpdate(String imgPath, String invoiceCode, String invoiceNo, String scanId) {
        ElectronInvoiceImage invoiceImage = new ElectronInvoiceImage();
        invoiceImage.setImagePath(imgPath);
        invoiceImage.setScanId(scanId);
        invoiceImage.setUuid(invoiceCode + invoiceNo);
        return invoiceImage;
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

    /**
     * 通过查验签收回来的数据获取底账表实体信息
     *
     * @param responseInvoice 查验签收回来的数据
     * @return 底账表数据信息
     */
    private RecordInvoice getRRecordInvoiceFromResponse(ResponseInvoice responseInvoice) {
        RecordInvoice recordInvoice = null;
        if (responseInvoice != null && Constants.CHECK_INVOICE_SUCCESS_CODE.equals(responseInvoice.getResultCode())) {
            //如果查验成功
            recordInvoice = new RecordInvoice();
            //查验返回结果信息
            recordInvoice.setResultCode(responseInvoice.getResultCode());
            recordInvoice.setResultTip(responseInvoice.getResultTip());

            recordInvoice.setInvoiceCode(responseInvoice.getInvoiceCode());
            recordInvoice.setInvoiceNo(responseInvoice.getInvoiceNo());
            recordInvoice.setInvoiceType(responseInvoice.getInvoiceType());
            recordInvoice.setInvoiceDate(DateTimeHelper.parseDate(responseInvoice.getInvoiceDate()));
            recordInvoice.setGfTaxNo(responseInvoice.getBuyerTaxNo());
            recordInvoice.setGfName(responseInvoice.getBuyerName());
            recordInvoice.setGfAddressAndPhone(responseInvoice.getBuyerAddressPhone());
            recordInvoice.setGfBankAndNo(responseInvoice.getBuyerAccount());
            recordInvoice.setXfTaxNo(responseInvoice.getSalerTaxNo());
            recordInvoice.setXfName(responseInvoice.getSalerName());
            recordInvoice.setXfAddressAndPhone(responseInvoice.getSalerAddressPhone());
            recordInvoice.setXfBankAndNo(responseInvoice.getSalerAccount());
            recordInvoice.setInvoiceStatus(Constants.CHECK_BACK_INVOICE_Y.equals(responseInvoice.getIsCancelled()) ? Constants.INVOICE_STATUS_TWO : Constants.INVOICE_STATUS_ZERO);
            if (!"".equals(responseInvoice.getInvoiceAmount()) && responseInvoice.getInvoiceAmount() != null && !(" ").equals(responseInvoice.getInvoiceAmount())) {
                recordInvoice.setInvoiceAmount(new BigDecimal(responseInvoice.getInvoiceAmount()));
            }
            if (!"".equals(responseInvoice.getTaxAmount()) && responseInvoice.getTaxAmount() != null && !(" ").equals(responseInvoice.getTaxAmount())) {
                recordInvoice.setTaxAmount(new BigDecimal(responseInvoice.getTaxAmount()));
            }
            if (!"".equals(responseInvoice.getTotalAmount()) && responseInvoice.getTotalAmount() != null && !(" ").equals(responseInvoice.getTotalAmount())) {
                recordInvoice.setTotalAmount(new BigDecimal(responseInvoice.getTotalAmount()));
            }
            recordInvoice.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
            recordInvoice.setRemark(responseInvoice.getRemark());
            recordInvoice.setCheckCode(responseInvoice.getCheckCode());
            recordInvoice.setDetailYesorno(responseInvoice.getDetailList().isEmpty() ? Constants.INVOICE_DETAIL_NO : Constants.INVOICE_DETAIL_YES);
            recordInvoice.setDetailList(this.getInvoiceDetails(responseInvoice.getDetailList(),
                    responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo(), responseInvoice.getInvoiceCode(),
                    responseInvoice.getInvoiceNo()));
            recordInvoice.setValid(Constants.INVOICE_VALID_ONE);
            recordInvoice.setSourceSystem(Constants.INVOICE_SOURCE_SYSTEM);
            recordInvoice.setQsType(Constants.ELECTRON_INVOICE_QS_TYPE_FIVE);
            recordInvoice.setRemark(responseInvoice.getRemark());
            recordInvoice.setMachinecode(responseInvoice.getMachineNo());
        } else {
            //如果查验失败
            recordInvoice = new RecordInvoice();
            //查验返回结果信息
            recordInvoice.setResultCode(responseInvoice != null ? responseInvoice.getResultCode() : Constants.CHECK_INVOICE_BACK_FAIL_CODE);
            recordInvoice.setResultTip(responseInvoice != null ? responseInvoice.getResultTip() : Constants.CHECK_RESULT_TIP_ERROR);
        }

        return recordInvoice;
    }

    /**
     * 获取查验签收回来的商品明细
     *
     * @param list 查验回来的数据
     * @return 整理好的商品明细信息
     */
    private List<RecordInvoiceDetail> getInvoiceDetails(List<InvoiceDetail> list, String uuid, String invoiceCode, String invoiceNo) {
        List<RecordInvoiceDetail> details = Lists.newArrayList();
        RecordInvoiceDetail invoiceDetail;
        for (InvoiceDetail detail : list) {
            invoiceDetail = new RecordInvoiceDetail();
            invoiceDetail.setUuid(uuid);
            invoiceDetail.setInvoiceCode(invoiceCode);
            invoiceDetail.setInvoiceNo(invoiceNo);
            invoiceDetail.setDetailNo(detail.getDetailNo());
            invoiceDetail.setGoodsName(detail.getGoodsName());
            invoiceDetail.setModel(detail.getSpecificationModel());
            invoiceDetail.setNum(detail.getNum());
            invoiceDetail.setUnit(detail.getUnit());
            invoiceDetail.setUnitPrice(detail.getUnitPrice());
            invoiceDetail.setDetailAmount(detail.getDetailAmount());
            invoiceDetail.setTaxRate(detail.getTaxRate());
            invoiceDetail.setTaxAmount(detail.getTaxAmount());
            invoiceDetail.setCph(detail.getCph());
            invoiceDetail.setLx(detail.getLx());
            invoiceDetail.setTxrqq(detail.getTxrqq());
            invoiceDetail.setTxrqz(detail.getTxrqz());
            details.add(invoiceDetail);
        }
        return details;
    }

    /**
     * 获取上传文件的名称
     *
     * @param filename 文件的原始名称，有可能包含路径
     * @return
     */
    private String getOriginalFilename(String filename) {

        int unixSep = filename.lastIndexOf("/");
        int winSep = filename.lastIndexOf(Constants.SUBSTR_REGEX_FOR_FILE);
        int pos = winSep > unixSep ? winSep : unixSep;
        return pos != -1 ? filename.substring(pos + 1) : filename;

    }
}
