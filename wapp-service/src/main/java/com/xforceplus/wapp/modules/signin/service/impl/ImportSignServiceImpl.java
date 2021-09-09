package com.xforceplus.wapp.modules.signin.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.IterableUtil;
import com.xforceplus.wapp.common.utils.ReturnInfoEnum;
import com.xforceplus.wapp.config.SystemConfig;
import com.xforceplus.wapp.modules.collect.pojo.RequestData;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.collect.service.NoDetailedInvoiceService;
import com.xforceplus.wapp.modules.einvoice.util.*;
import com.xforceplus.wapp.modules.job.service.OcrSerivce;
import com.xforceplus.wapp.modules.signin.dao.SignImportDao;
import com.xforceplus.wapp.modules.signin.entity.ExportEntity;
import com.xforceplus.wapp.modules.signin.entity.InvoiceScan;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceDataEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.enumflord.SignInEnum;
import com.xforceplus.wapp.modules.signin.service.ImportSignService;
import com.xforceplus.wapp.modules.signin.toexcel.SignImport;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.xforceplus.wapp.modules.Constant.*;
import static com.xforceplus.wapp.modules.signin.enumflord.InvoiceTypeEnum.invoiceTypeMap;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

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


    private final NoDetailedInvoiceService noDetailedInvoiceService;

    private final OcrSerivce ocrSerivce;

    @Autowired
    public ImportSignServiceImpl(SignImportDao signImportDao, SystemConfig systemConfig,  NoDetailedInvoiceService noDetailedInvoiceService, OcrSerivce ocrSerivce) {
        this.signImportDao = signImportDao;
        this.systemConfig = systemConfig;
        this.noDetailedInvoiceService = noDetailedInvoiceService;
        this.ocrSerivce = ocrSerivce;
    }

    /**
     * A、导入excel文件，解析出文件中信息，根据解析出的发票代码判断发票类型
     * a、识别出专票、通行费发票、机动车发票时：根据识别出的发票代码和发票号码查询抵账表中的数据是否存在，
     * 如果存在则对比开票日期，未税金额、税额是否一致，一致则更新底账表中的签收状态（已签收）签收方式（导入签收）签收时间（当前时间），并将发票数据保存到扫描表中。
     * 如果不存在，将解析出来的发票信息存到扫描表中（签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
     * b、识别出普票时：根据识别出的发票代码、发票号码、开票日期、校验码来触发查验操作
     * 如果查验成功，则将获取到的发票全票面信息保存到底账表（签收状态（已签收）签收方式（导入签收）签收时间（当前时间））中，并保存发票信息到扫描表中（签收状态（签收成功）签收方式（导入签收）签收时间（当前时间））
     * 如果查验失败，则保存导入的发票信息到扫描表中以便使用签收处理菜单进行签收操作。
     */
    @Override
    public List<RecordInvoiceEntity> importSignExcel(ExportEntity exportEntity, MultipartFile excelFile, Integer count) throws ExcelException, RRException {
        LOGGER.info("解析excel开始");

        final SignImport signImport = new SignImport(excelFile);

        //读取excel
        final List<RecordInvoiceEntity> recordInvoiceEntityList = signImport.analysisExcel();

        //读取到的excel为空 则抛异常
        if (recordInvoiceEntityList.isEmpty()) {
            throw new RRException("读取到excel无数据");
        }

        //读取到excel的条数大于500则抛异常
        if (recordInvoiceEntityList.size() + count > MAX_IMPORT_SIZE) {
            throw new RRException("导入数据超过500条，请修改模板!");
        }
        //返回 0 excel导入
        return signHandleService(exportEntity, repeatMap(recordInvoiceEntityList), "0", Boolean.FALSE);
    }

    @Override
    public List<RecordInvoiceEntity> importSignImg(ExportEntity exportEntity, MultipartFile imgFile, Integer count) {
        LOGGER.debug("----------------图片开始--------------------");

        List<RecordInvoiceEntity> invoices = new ArrayList<>();
        //文件名
        final String fileName = imgFile.getOriginalFilename();
        //上传文件重新命名
        String pdfFileName = new SimpleDateFormat(LONG_DATE_FORMAT).format(new Date()) + imgFile.getOriginalFilename().substring(imgFile.getOriginalFilename().indexOf("."));
        //创建临时存储文件夹
        this.createFileDir();
        File newFile = new File(systemConfig.getTempPath() + pdfFileName);
        try {
            imgFile.transferTo(newFile);
            if (fileName.contains(FILE_TYPE_ZIP) || fileName.contains(".rar")) {
                //若用户上传的是压缩文件,对文件进行解压
                ZipUtil.unzip(systemConfig.getTempPath() + pdfFileName);
                // 对上传的文件进行移动保存
                PdfToImgUtil.move(newFile, systemConfig.getDepositPath());
            }
            // 获取临时文件夹里的所有文件
            List<File> files = newArrayList();
            List<File> pngFiles = PdfToImgUtil.findFile(systemConfig.getTempPath(), IMG_TYPE_PNG);

            List<File> jpgFiles = PdfToImgUtil.findFile(systemConfig.getTempPath(), IMG_TYPE_JPG);
            files.addAll(pngFiles);
            files.addAll(jpgFiles);
            if (files.size() + count > MAX_IMPORT_SIZE) {
                throw new RRException("导入数据超过500条!");
            }
            //解析并保存电票信息
            invoices = this.analysisElectronFile(exportEntity, files);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error("图片处理失败:{}", e);
        } finally {
            //清空临时文件里面的文件
            ParsePdfUtil.deleteImgFile(systemConfig.getTempPath());
        }
        LOGGER.debug("----------------图片导入完成--------------------");
        return invoices;
    }

    @Override
    public String getInvoiceImage(Map<String, String> params) {
        final String invoiceImage = signImportDao.getImgPath(params);
        SFTPHandler imageHandler = SFTPHandler.getHandler(systemConfig.getRemoteImageRootPath(), systemConfig.getLocalImageRootPath());
        try {
            if (!StringUtils.isEmpty(invoiceImage)) {
                imageHandler.openChannel(systemConfig.getHost(), systemConfig.getUserName(), systemConfig.getPassword(), Integer.parseInt(systemConfig.getDefaultPort()), Integer.parseInt(systemConfig.getDefaultTimeout()));
                //默认文件名
                final String userAccount = "invoiceImg";
                imageHandler.download(invoiceImage, userAccount + FILE_TYPE_ZIP);

                final byte[] zipFile = ZipUtilRead.readZipFile(imageHandler.getLocalImageRootPath() + userAccount + ".zip");

                return org.apache.commons.codec.binary.Base64.encodeBase64String(zipFile);
            }
        } catch (Exception e) {
            LOGGER.info("获取图片失败:{}", e);
        } finally {
            imageHandler.closeChannel();
        }
        return "";
    }

    @Override
    public List<RecordInvoiceEntity> modifyInvoice(Map<String, String> map, ExportEntity exportEntity) {
        final RecordInvoiceEntity recordInvoiceEntity = new RecordInvoiceEntity();
        final List<RecordInvoiceEntity> entityList = newArrayList();
        //发票代码
        recordInvoiceEntity.setInvoiceCode(map.get("invoiceCode"));
        //发票号码
        recordInvoiceEntity.setInvoiceNo(map.get("invoiceNo"));
        //金额
        if (StringUtils.isNotEmpty(map.get("invoiceAmount"))) {
            recordInvoiceEntity.setInvoiceAmount(new BigDecimal(map.get("invoiceAmount")));
        } else {
            recordInvoiceEntity.setInvoiceAmount(null);
        }
        //开票日期
        recordInvoiceEntity.setInvoiceDate(formatterDate(map.get("invoiceDate")));
        //校验码
        recordInvoiceEntity.setCheckCode(map.get("checkCode"));
        //导入类型
        recordInvoiceEntity.setImportType(map.get("importType"));
        entityList.add(recordInvoiceEntity);
        return signHandleService(exportEntity, entityList, map.get("importType"), Boolean.TRUE);
    }

    /**
     * 将原有的签收集进行区分处理后并返回
     *
     * @param recordInvoiceEntityList 原有的签收集
     * @return 新的发票签收集
     */
    private List<RecordInvoiceEntity> signHandleService(ExportEntity exportEntity, List<RecordInvoiceEntity> recordInvoiceEntityList, String importType, Boolean modifyFlag) {

        //获取区分后的发票签收map
        final Map<String, List<RecordInvoiceEntity>> listMap = distinguishByInvoiceType(exportEntity, recordInvoiceEntityList, modifyFlag);

        //专票、通行费发票、机动车发票集
        final List<RecordInvoiceEntity> speciallyInvoiceList = listMap.get("speciallyInvoiceList");
        //普票集
        final List<RecordInvoiceEntity> tomeInvoiceList = listMap.get("tomeInvoiceList");
        //其他发票集 如果存在其他发票，则导入签收信息有误 直接计入导入失败数量
        final List<RecordInvoiceEntity> otherInvoiceList = listMap.get("otherInvoiceList");
        //扫描表已存在的发票 不做任何处理 和其他发票一样只在页面展示
        final List<RecordInvoiceEntity> scanExitInvoiceList = listMap.get("scanExitInvoiceList");
        //数据存在问题的发票
        final List<RecordInvoiceEntity> errorDataList = listMap.get("errorDataList");

        //专票、通行费发票、机动车发票处理
        final List<RecordInvoiceEntity> speciallyRecordInvoice = speciallyInvoiceHandle(exportEntity, speciallyInvoiceList, importType);

        //普票业务处理
        final List<RecordInvoiceEntity> tomeRecordInvoice = tomeInvoiceHandle(exportEntity, tomeInvoiceList, importType);
        //合并集合
        speciallyRecordInvoice.addAll(tomeRecordInvoice);
        speciallyRecordInvoice.addAll(otherInvoiceList);
        speciallyRecordInvoice.addAll(scanExitInvoiceList);
        speciallyRecordInvoice.addAll(errorDataList);
        //返回
        return speciallyRecordInvoice;
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
        //专票、通行费发票、机动车发票集
        final List<RecordInvoiceEntity> speciallyInvoiceList = newArrayList();
        //普票集
        final List<RecordInvoiceEntity> tomeInvoiceList = newArrayList();
        //其他发票集
        final List<RecordInvoiceEntity> otherInvoiceList = newArrayList();
        //在扫描表中存在的发票集（在扫描表存在，则说明已经处理过）
        final List<RecordInvoiceEntity> scanExitInvoiceList = newArrayList();
        //不符合处理条件的发票（数据不对）
        final List<RecordInvoiceEntity> errorDataList = newArrayList();

        //专票、通行费发票、机动车发票数组
        final String[] speciallyInvoiceTypeArray = null;
        //遍历
        recordInvoiceEntityList.forEach((RecordInvoiceEntity recordInvoiceEntity) -> {
            //获取发票类型
            final String invoiceType = CommonUtil.getFplx(recordInvoiceEntity.getInvoiceCode());
            //赋值
            recordInvoiceEntity.setInvoiceType(invoiceType);
            //名
            recordInvoiceEntity.setInvoiceTypeName(invoiceTypeMap().get(invoiceType));
            //uuid
            final String uuid = recordInvoiceEntity.getInvoiceCode() + recordInvoiceEntity.getInvoiceNo();

            if (checkListRecordInvoiceEntity(recordInvoiceEntity)) {
                //判断发票在扫描表中是否存在 存在且不为修改操作时则不能做任何处理 因为一张发票只能导入处理一次 不论成功还是失败
                final Boolean exitFlag = signImportDao.queryCountScan(exportEntity.getSchemaLabel(), uuid) > 0;
                if ((exitFlag || ("1".equals(recordInvoiceEntity.getRepeatFlag()))) && !modifyFlag) {
                    //存在 放入对应集合
                    recordInvoiceEntity.setNotes("发票已存在");
                    recordInvoiceEntity.setRepeatFlag("1");
                    recordInvoiceEntity.setHandleFlag("1");
                    scanExitInvoiceList.add(recordInvoiceEntity);
                } else {
                    //不存在， 判断是否为专票、通行费发票、机动车发票
                    final Boolean flag = Arrays.asList(speciallyInvoiceTypeArray).contains(invoiceType);
                    if (flag) {
                        //是 则将对象放入专票集合
                        speciallyInvoiceList.add(recordInvoiceEntity);
                    } else {
                        if (Arrays.asList(TOME_INVOICE_TYPE).contains(invoiceType)) {
                            //为普票 放入普票集合
                            tomeInvoiceList.add(recordInvoiceEntity);
                        } else {
                            //其他 放入其他集合
                            recordInvoiceEntity.setNotes("发票类型不对，无法签收");
                            recordInvoiceEntity.setTypeErrorFlag("1");
                            otherInvoiceList.add(recordInvoiceEntity);
                        }
                    }
                }
            } else {
                errorDataList.add(recordInvoiceEntity);
                LOGGER.info("描述信息为:{}", recordInvoiceEntity.getNotes());
            }
        });
        map.put("speciallyInvoiceList", speciallyInvoiceList);
        map.put("tomeInvoiceList", tomeInvoiceList);
        map.put("otherInvoiceList", otherInvoiceList);
        map.put("scanExitInvoiceList", scanExitInvoiceList);
        map.put("errorDataList", errorDataList);
        return map;
    }

    /**
     * 专票、通行费发票、机动车发票处理
     *
     * @param recordInvoiceEntityList 专票、通行费发票、机动车发票集
     * @return 处理后的专票、通行费发票、机动车发票集
     */
    private List<RecordInvoiceEntity> speciallyInvoiceHandle(ExportEntity exportEntity, List<RecordInvoiceEntity> recordInvoiceEntityList, String importType) {
        //抵账表不存在（签收失败）
        final List<InvoiceScan> invoiceScanErrorList = newArrayList();
        //抵账表存在 但信息不一致(签收失败)
        final List<InvoiceScan> invoiceScanCheckErrorList = newArrayList();
        //抵账表存在 但信息一致(签收成功)
        final List<InvoiceScan> invoiceScanCheckSuccessList = newArrayList();
        //人员下的所有税号
        List<String> taxNoList = signImportDao.getTaxNoList(exportEntity.getSchemaLabel(), exportEntity.getUserId());
        if (taxNoList == null) {
            taxNoList = newArrayList();
        }
        final List<String> finalTaxNoList = taxNoList;
        //遍历
        recordInvoiceEntityList.forEach(entity -> {
            entity.setImportType(importType);
            //根据识别出的发票代码和发票号码查询抵账表中的数据是否存在
            final RecordInvoiceDataEntity dataEntity = signImportDao.getRecordData(exportEntity.getSchemaLabel(), entity.getInvoiceCode(), entity.getInvoiceNo());
            if (dataEntity != null) {
                //不等于空时校验码为数据库中的校验码
                entity.setCheckCode(dataEntity.getCheckCode());
                //如过查到，且数据库的状态为已签收，则不再做重复签收操作，直接显示'已签收，请勿重复签收'
                if ("1".equals(dataEntity.getQsStatus())) {
                    entity.setQsStatus(dataEntity.getQsStatus());
                    entity.setNotes("签收成功");
                } else if (!finalTaxNoList.contains(dataEntity.getGfTaxNo())) {
                    //发票在抵账表中存在 但是人员未绑定该发票的税号，故改人员不能处理 返回签收失败
                    entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                    entity.setHandleFlag("2");
                    entity.setNotes("没有税号权限");
                } else {
                    //抵账表存在 则比对 开票日期，未税金额、发票类型是否一致
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
                }
            } else {
                //赋值 签收失败
                entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                //失败描述
                entity.setNotes("签收失败，发票不存在");
                //抵账表不存在 构建扫描实体 将解析出来的发票信息存到扫描表中 （签收状态（签收失败）签收方式（导入签收）签收时间（空））以便在签收处理菜单进行签收。
                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, new RecordInvoiceDataEntity(), entity, Boolean.FALSE);
                //放入集合
                invoiceScanErrorList.add(invoiceScan);
            }
        });
        //将无抵账信息数据保存进扫描表
        if (!invoiceScanErrorList.isEmpty()) {
            signImportDao.insertScanInvoice(exportEntity.getSchemaLabel(), invoiceScanErrorList);
        }

        //将抵账表存在 但信息不一致保存进扫描表
        if (!invoiceScanCheckErrorList.isEmpty()) {
            signImportDao.insertScanInvoice(exportEntity.getSchemaLabel(), invoiceScanCheckErrorList);
        }

        //将抵账表存在 信息一致保存进扫描表 并更新抵账表，签收状态（已签收）签收方式（导入签收）签收时间（当前时间）
        if (!invoiceScanCheckSuccessList.isEmpty()) {
            //保存扫描表
            signImportDao.insertScanInvoice(exportEntity.getSchemaLabel(), invoiceScanCheckSuccessList);
            //更新抵账表
            signImportDao.updateRecordQsStatus(exportEntity.getSchemaLabel(), invoiceScanCheckSuccessList);
        }

        //返回
        return recordInvoiceEntityList;
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
        //人员名
        invoiceScan.setUserName(exportEntity.getUserName());
        //人员帐号
        invoiceScan.setUserAccount(exportEntity.getUserAccount());
        //校验码
        invoiceScan.setCheckCode(entity.getCheckCode());
        //发票类型
        invoiceScan.setInvoiceType(entity.getInvoiceType());
        //发票代码
        invoiceScan.setInvoiceCode(entity.getInvoiceCode());
        //发票号码
        invoiceScan.setInvoiceNo(entity.getInvoiceNo());
        //金额
        invoiceScan.setInvoiceAmount(bigToStr(entity.getInvoiceAmount()));
        //开票时间
        invoiceScan.setInvoiceDate(new DateTime(entity.getInvoiceDate()).toString(DEFAULT_SHORT_DATE_FORMAT));
        //签收方式--导入签收
        invoiceScan.setQsType(SignInEnum.QS_LEADING_IN.getValue());
        //'签收结果(0-签收失败 1-签收成功）'
        invoiceScan.setInvoiceStatus(entity.getQsStatus());
        //签收描述
        invoiceScan.setNotes(entity.getNotes());
        //是否有效（1-有效 0-无效）默认1
        invoiceScan.setValid("1");
        //uuid
        invoiceScan.setUuId(entity.getInvoiceCode() + entity.getInvoiceNo());
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
        if (flag) {
            //购方税号
            invoiceScan.setGfTaxNo(dataEntity.getGfTaxNo());
            //购方名称
            invoiceScan.setGfName(dataEntity.getGfName());
            //销方税号
            invoiceScan.setXfTaxNo(dataEntity.getXfTaxNo());
            //销方名称
            invoiceScan.setXfName(dataEntity.getXfName());
            //税额
            invoiceScan.setTaxAmount(bigToStr(dataEntity.getTaxAmount()));
            //价税合计
            invoiceScan.setTotalAmount(bigToStr(dataEntity.getTotalAmount()));
            //签收时间
            if ("1".equals(entity.getQsStatus())) {
                invoiceScan.setQsDate(new DateTime(new Date()).toString(DEFAULT_SHORT_DATE_FORMAT));
            }
        }
        return invoiceScan;
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
        } else {
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
     * 比对 开票日期，未税金额、发票类型是否一致
     *
     * @param entity     导入实体
     * @param dataEntity 数据库查询出的实体
     * @return 比对结果
     */
    private Boolean checkInfoConsistent(RecordInvoiceEntity entity, RecordInvoiceDataEntity dataEntity) {
        Boolean consistentFlag = Boolean.TRUE;
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
        //发票类型
        if (!dataEntity.getInvoiceType().equals(entity.getInvoiceType())) {
            entity.setNotes("发票类型不一致");
            consistentFlag = Boolean.FALSE;
        }
        //开票日期
        final String dataInvoiceTime = new DateTime(dataEntity.getInvoiceDate()).toString(DEFAULT_SHORT_DATE_FORMAT);
        final String entityInvoiceTime = new DateTime(entity.getInvoiceDate()).toString(DEFAULT_SHORT_DATE_FORMAT);
        if (!dataInvoiceTime.equals(entityInvoiceTime)) {
            entity.setNotes("开票日期不一致");
            consistentFlag = Boolean.FALSE;
        }
        return consistentFlag;
    }

    /**
     * 普票处理
     *
     * @param recordInvoiceEntityList 普票集
     * @return 处理后的普票集
     */
    private List<RecordInvoiceEntity> tomeInvoiceHandle(ExportEntity exportEntity, List<RecordInvoiceEntity> recordInvoiceEntityList, String importType) {
        final RequestData requestData = new RequestData();

        //查验失败的发票信息
        final List<InvoiceScan> invoiceScanErrorList = newArrayList();
        //查验成功的发票信息
        final List<InvoiceScan> invoiceScanSuccessList = newArrayList();
        //响应集
        final List<ResponseInvoice> responseInvoiceList = newArrayList();
        //人员下的所有税号
        List<String> taxNoList = signImportDao.getTaxNoList(exportEntity.getSchemaLabel(), exportEntity.getUserId());
        if (taxNoList == null) {
            taxNoList = newArrayList();
        }
        final List<String> finalTaxNoList = taxNoList;
        //遍历
        recordInvoiceEntityList.forEach(entity -> {
            entity.setImportType(importType);
            //购方税号
            requestData.setBuyerTaxNo("");
            //发票类型
            requestData.setInvoiceType(entity.getInvoiceType());
            //发票代码
            requestData.setInvoiceCode(entity.getInvoiceCode());
            //发票号码
            requestData.setInvoiceNo(entity.getInvoiceNo());
            //开票时间
            final String invoiceDate = new DateTime(entity.getInvoiceDate()).toString(SHORT_DATE_FORMAT);
            requestData.setInvoiceDate(invoiceDate);
            //校验码
            requestData.setCheckCode(entity.getCheckCode());
            //金额
            if (entity.getInvoiceAmount() != null) {
                requestData.setInvoiceAmount(String.valueOf(entity.getInvoiceAmount()));
            } else {
                requestData.setInvoiceAmount(StringUtils.EMPTY);
            }
            //查验
            final ResponseInvoice responseInvoice = null;
            //查验一致
            if (responseInvoice != null && ReturnInfoEnum.CHECK_SUCCESS.getResultCode().equals(responseInvoice.getResultCode())) {
                //根据识别出的发票代码和发票号码查询抵账表中的数据是否存在
                final RecordInvoiceDataEntity dataEntity = signImportDao.getRecordData(exportEntity.getSchemaLabel(), entity.getInvoiceCode(), entity.getInvoiceNo());

                if (dataEntity != null) {
                    //抵账表存在，则重复签收 因为普票只有签收成功后才会保存抵账表，抵账表存在，则说明已经签收成功，无需重复签收
                    entity.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                    entity.setNotes("签收成功");
                } else {
                    if (!finalTaxNoList.contains(responseInvoice.getBuyerTaxNo())) {
                        //发票在抵账表中存在 但是人员未绑定该发票的税号，故改人员不能处理 返回签收失败
                        entity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                        entity.setHandleFlag("2");
                        entity.setNotes("没有税号权限");
                    } else {
                        entity.setQsStatus(SignInEnum.QS_SUCCESS.getValue());
                        entity.setNotes("查验成功");
                        //构建扫描实体
                        final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, responseInvoice, entity);
                        //放入查验成功集合
                        invoiceScanSuccessList.add(invoiceScan);
                        responseInvoiceList.add(responseInvoice);
                    }
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
                final InvoiceScan invoiceScan = buildInvoiceScan(exportEntity, new RecordInvoiceDataEntity(), entity, Boolean.FALSE);
                //放入集合
                invoiceScanErrorList.add(invoiceScan);
            }
        });
        //将校验失败的发票保存进扫描表
        if (!invoiceScanErrorList.isEmpty()) {
            signImportDao.insertScanInvoice(exportEntity.getSchemaLabel(), invoiceScanErrorList);
        }

        //查验成功
        if (!invoiceScanSuccessList.isEmpty()) {
            //保存扫描表
            signImportDao.insertScanInvoice(exportEntity.getSchemaLabel(), invoiceScanSuccessList);
            //保存抵账表 抵账明细表， 抵账统计表
            noDetailedInvoiceService.inspectionProcess(exportEntity.getSchemaLabel(), responseInvoiceList);
        }
        return recordInvoiceEntityList;
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
     * 解析上传的电票文件
     *
     * @param files 文件集
     */
    private List<RecordInvoiceEntity> analysisElectronFile(ExportEntity exportEntity, List<File> files) throws IOException, SftpException, JSchException {
        //定义发票签收集
        final List<RecordInvoiceEntity> recordInvoiceEntityList = newArrayList();

        //图片map集
        final List<Map<String, String>> imgMapList = newArrayList();

        //连接远程ftp服务器，上传电票图片
        SFTPHandler handler = SFTPHandler.getHandler(systemConfig.getRemoteImageRootPath(), systemConfig.getTempPath());

        //连接远程服务器
        handler.openChannel(systemConfig.getHost(), systemConfig.getUserName(), systemConfig.getPassword(), Integer.parseInt(systemConfig.getDefaultPort()), Integer.parseInt(systemConfig.getDefaultTimeout()));
        for (File file : files) {
            //将文件base64
            final String baseFileStr = ParsePdfUtil.getBase64ByFile(file);
            //图片唯一识别id
            final String scanId = UUID.randomUUID().toString().replace("-", "");
            //ocr返回信息
            final Map<String, String> ocrMap = ocrSerivce.discernInvoice(scanId, scanId, baseFileStr);

            //如果ocr识别成功
            if ("0000".equals(ocrMap.get("returnCode")) && StringUtils.isNotEmpty(ocrMap.get("invoiceCode")) && StringUtils.isNotEmpty(ocrMap.get("invoiceNo"))) {
                //定义签收实体
                final RecordInvoiceEntity record = buildRecordByMap(ocrMap, scanId);
                //放入集合
                recordInvoiceEntityList.add(record);

                File zipImgFile = zipImg(systemConfig.getTempPath() + file.getName());
                //将图片上传ftp
                final String uploadImg = handler.uploadImg(ocrMap.get("buyerTaxNo"), ocrMap.get("invoiceDate"), zipImgFile.getName());
                //构建图片map
                final Map<String, String> imgMap = buildInvoiceImgMap(uploadImg + zipImgFile.getName(), record.getInvoiceCode() + record.getInvoiceNo(), scanId);
                //放入集合
                imgMapList.add(imgMap);
            }
        }
        //保存发票图片
        if (imgMapList.isEmpty()) {
            throw new RRException("ocr获取数据失败");
        }
        LOGGER.info("插入图片数据:{}", imgMapList.toString());
        final Boolean flag = signImportDao.insertInvoiceImg(exportEntity.getSchemaLabel(), imgMapList) > 0;
        if (!flag) {
            throw new RRException("图片数据保存失败!");
        }
        //返回 1图片导入
        return signHandleService(exportEntity, repeatMap(recordInvoiceEntityList), "1", Boolean.FALSE);
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
            entity.setTotalAmount(Double.valueOf(map.get("totalAmount")));
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
     * 对导入的数据进行数据格式检测 校验数据是否符合格式要求
     *
     * @param recordInvoiceEntity 导入的数据实体
     * @return true校验成功 false校验失败
     */
    private Boolean checkListRecordInvoiceEntity(RecordInvoiceEntity recordInvoiceEntity) {

        //发票代码格式校验 10或12位数字
        final Boolean codeFlag = CommonUtil.isValidNum(recordInvoiceEntity.getInvoiceCode(), "^(\\d{10}|\\d{12})$");
        if (!codeFlag) {
            recordInvoiceEntity.setQsStatus(SignInEnum.QS_FAIL.getValue());
            recordInvoiceEntity.setTypeErrorFlag("1");
            recordInvoiceEntity.setNotes("发票代码:" + recordInvoiceEntity.getInvoiceCode() + "，发票号码:" + recordInvoiceEntity.getInvoiceNo() + "的发票代码格式错误!");
        }

        //发票号码格式校验 8位数字
        final Boolean numFlag = CommonUtil.isValidNum(recordInvoiceEntity.getInvoiceNo(), "^[\\d]{8}$");
        if (!numFlag) {
            recordInvoiceEntity.setQsStatus(SignInEnum.QS_FAIL.getValue());
            recordInvoiceEntity.setTypeErrorFlag("1");
            recordInvoiceEntity.setNotes("发票代码:" + recordInvoiceEntity.getInvoiceCode() + "，发票号码:" + recordInvoiceEntity.getInvoiceNo() + "的发票号码格式错误!");
        }

        //开票日期格式校验
        Boolean dateFlag = Boolean.FALSE;
        if (recordInvoiceEntity.getInvoiceDate() != null) {
            dateFlag = CommonUtil.isValidDate(new DateTime(recordInvoiceEntity.getInvoiceDate()).toString(DEFAULT_SHORT_DATE_FORMAT), DEFAULT_SHORT_DATE_FORMAT, "[0-9]{4}-[0-9]{2}-[0-9]{2}");
            if (!dateFlag) {
                recordInvoiceEntity.setQsStatus(SignInEnum.QS_FAIL.getValue());
                recordInvoiceEntity.setInvoiceDate(null);
                recordInvoiceEntity.setTypeErrorFlag("1");
                recordInvoiceEntity.setNotes("发票代码:" + recordInvoiceEntity.getInvoiceCode() + "，发票号码:" + recordInvoiceEntity.getInvoiceNo() + "的时间格式错误!");
            }
        } else {
            recordInvoiceEntity.setQsStatus(SignInEnum.QS_FAIL.getValue());
            recordInvoiceEntity.setInvoiceDate(null);
            recordInvoiceEntity.setTypeErrorFlag("1");
            recordInvoiceEntity.setNotes("发票代码:" + recordInvoiceEntity.getInvoiceCode() + "，发票号码:" + recordInvoiceEntity.getInvoiceNo() + "的时间格式错误!");
        }

        return codeFlag && numFlag && dateFlag;
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
}