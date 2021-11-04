package com.xforceplus.wapp.modules.noneBusiness.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.backFill.model.*;
import com.xforceplus.wapp.modules.backFill.service.BackFillService;
import com.xforceplus.wapp.modules.backFill.service.DiscernService;
import com.xforceplus.wapp.modules.backFill.service.FileService;
import com.xforceplus.wapp.modules.backFill.service.VerificationService;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.noneBusiness.convert.NoneBusinessConverter;
import com.xforceplus.wapp.modules.noneBusiness.dto.FileDownRequest;
import com.xforceplus.wapp.modules.noneBusiness.dto.TXfNoneBusinessUploadExportDto;
import com.xforceplus.wapp.modules.noneBusiness.util.ZipUtil;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import com.xforceplus.wapp.repository.dao.TXfNoneBusinessUploadDetailDao;
import com.xforceplus.wapp.repository.daoExt.ElectronicInvoiceDao;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailDto;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadQueryDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * 非商业务逻辑
 */
@Service
@Slf4j
public class NoneBusinessService extends ServiceImpl<TXfNoneBusinessUploadDetailDao, TXfNoneBusinessUploadDetailEntity> {
    @Autowired
    private BackFillService backFillService;

    @Autowired
    private FileService fileService;
    @Autowired
    private DiscernService discernService;
    @Value("${wapp.integration.customer-no}")
    private String customerNo;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FtpUtilService ftpUtilService;

    @Autowired
    ExportCommonService exportCommonService;

    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Autowired
    private ActiveMqProducer activeMqProducer;

    @Value("${activemq.queue-name.export-request}")
    private String exportQueue;
    @Autowired
    private TXfNoneBusinessUploadDetailDao tXfNoneBusinessUploadDetailDao;

    @Value("${wapp.export.tmp}")
    private String tmp;

    @Autowired
    private ElectronicInvoiceDao electronicInvoiceDao;
    @Autowired
    private NoneBusinessConverter noneBusinessConverter;

    @Autowired
    private RedisTemplate redisTemplate;
    private static String KEY = "NOBUSINESS_SIGN_";

    public void parseOfdFile(List<byte[]> ofd, TXfNoneBusinessUploadDetailEntity entity) {

        List<TXfNoneBusinessUploadDetailEntity> list = new ArrayList<>();
        ofd.stream().forEach(ofdEntity -> {
            TXfNoneBusinessUploadDetailEntity addEntity = new TXfNoneBusinessUploadDetailEntity();
            BeanUtil.copyProperties(entity, addEntity);
            StringBuffer fileName = new StringBuffer();
            fileName.append(UUID.randomUUID().toString());
            fileName.append(".");
            fileName.append(Constants.SUFFIX_OF_OFD);
            String uploadResult = null;
            try {
                //上传源文件到ftp服务器
                uploadResult = fileService.uploadFile(ofdEntity, fileName.toString());
            } catch (IOException ex) {
                log.error("非商上传电票到文件服务器失败:{}", ex);
            }
            UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
            UploadFileResultData data = uploadFileResult.getData();
            addEntity.setFileType(String.valueOf(Constants.FILE_TYPE_OFD));
            addEntity.setSourceUploadId(data.getUploadId());
            addEntity.setCreateUser(UserUtil.getLoginName());
            addEntity.setSourceUploadPath(data.getUploadPath());
            addEntity.setCreateTime(DateUtils.getNowDate());
            //发送验签
            OfdResponse response = backFillService.signOfd(ofdEntity, entity.getBussinessNo());
            //验签成功
            if (response.isOk()) {
                addEntity.setOfdStatus(Constants.SIGN_NONE_BUSINESS_SUCCESS);
                final InvoiceMain invoiceMain = response.getResult().getInvoiceMain();
                if (StringUtils.isNotEmpty(response.getResult().getImageUrl())) {
                    String base64 = Base64.getEncoder().encodeToString(response.getResult().getImageUrl().getBytes());
                    redisTemplate.opsForValue().set(KEY + invoiceMain.getInvoiceCode() + invoiceMain.getInvoiceNo(), base64, 10, TimeUnit.MINUTES);
                }
                VerificationRequest verificationRequest = new VerificationRequest();
                verificationRequest.setAmount(invoiceMain.getAmountWithoutTax());
                verificationRequest.setCheckCode(invoiceMain.getCheckCode());
                verificationRequest.setCustomerNo(customerNo);
                verificationRequest.setInvoiceCode(invoiceMain.getInvoiceCode());
                verificationRequest.setInvoiceNo(invoiceMain.getInvoiceNo());
                verificationRequest.setPaperDrewDate(invoiceMain.getPaperDrewDate());
                VerificationResponse verificationResponse = verificationService.verify(verificationRequest);
                if (verificationResponse.isOK()) {
                    addEntity.setXfVerifyTaskId(verificationResponse.getResult());
                    addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_DOING);
                } else {
                    addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
                }
                this.save(addEntity);


            } else {
                addEntity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);
                addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
            }


        });

    }

    public void parsePdfFile(List<byte[]> pdf, TXfNoneBusinessUploadDetailEntity entity) {
        List<TXfNoneBusinessUploadDetailEntity> list = new ArrayList<>();
        pdf.stream().forEach(pdfEntity -> {
            TXfNoneBusinessUploadDetailEntity addEntity = new TXfNoneBusinessUploadDetailEntity();
            BeanUtil.copyProperties(entity, addEntity);
            StringBuffer fileName = new StringBuffer();
            fileName.append(UUID.randomUUID().toString());
            fileName.append(".");
            fileName.append(Constants.SUFFIX_OF_PDF);
            String uploadResult = null;
            try {
                //上传源文件到ftp服务器
                uploadResult = fileService.uploadFile(pdfEntity, fileName.toString());
            } catch (IOException ex) {
                log.error("非商上传电票到文件服务器失败:{}", ex);
            }
            UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
            UploadFileResultData data = uploadFileResult.getData();
            addEntity.setFileType(String.valueOf(Constants.FILE_TYPE_PDF));
            addEntity.setSourceUploadId(data.getUploadId());
            addEntity.setCreateUser(UserUtil.getLoginName());
            addEntity.setSourceUploadPath(data.getUploadPath());
            addEntity.setUploadId(data.getUploadId());
            addEntity.setUploadPath(data.getUploadPath());
            addEntity.setCreateTime(DateUtils.getNowDate());
            //发送识别
            String taskId = discernService.discern(pdfEntity);
            //发送识别成功
            if (StringUtils.isNotEmpty(taskId)) {
                addEntity.setOfdStatus(Constants.SIGN_NONE_BUSINESS_DOING);
                addEntity.setXfDiscernTaskId(taskId);
                addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_DOING);
            } else {
                addEntity.setOfdStatus(Constants.SIGIN_NONE_BUSINESS_FAIL);
                addEntity.setVerifyStatus(Constants.VERIFY_NONE_BUSINESS_FAIL);
            }
            list.add(addEntity);
            this.save(addEntity);

        });


    }

    public TXfNoneBusinessUploadDetailEntity getObjByVerifyTaskId(String verifyTaskId) {
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.eq(TXfNoneBusinessUploadDetailEntity.XF_VERIFY_TASK_ID, verifyTaskId);
        return getOne(wrapper);
    }

    public TXfNoneBusinessUploadDetailEntity getObjByDiscernTaskId(String taskId) {
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.eq(TXfNoneBusinessUploadDetailEntity.XF_DISCERN_TASK_ID, taskId);
        return getOne(wrapper);
    }

    public boolean updateById(TXfNoneBusinessUploadDetailEntity entity) {
        if (null == entity.getId()) {
            return false;
        }
        LambdaUpdateChainWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new LambdaUpdateChainWrapper<>(getBaseMapper());

        if (null != entity.getId()) {
            wrapper.eq(TXfNoneBusinessUploadDetailEntity::getId, entity.getId());
        }
        if (StringUtils.isNotBlank(entity.getInvoiceCode())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getInvoiceCode, entity.getInvoiceCode());
        }
        if (StringUtils.isNotBlank(entity.getInvoiceNo())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getInvoiceNo, entity.getInvoiceNo());
        }
        if (StringUtils.isNotBlank(entity.getInvoiceDate())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getInvoiceDate, entity.getInvoiceDate());
        }
        if (StringUtils.isNotBlank(entity.getVerifyStatus())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getVerifyStatus, entity.getVerifyStatus());
        }
        if (StringUtils.isNotBlank(entity.getXfVerifyTaskId())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getXfVerifyTaskId, entity.getXfVerifyTaskId());
        }
        if (StringUtils.isNotBlank(entity.getReason())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getReason, entity.getReason());
        }
        if (StringUtils.isNotBlank(entity.getOfdStatus())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getOfdStatus, entity.getOfdStatus());
        }
        if (StringUtils.isNotBlank(entity.getUploadId())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getUploadId, entity.getUploadId());
        }
        if (StringUtils.isNotBlank(entity.getUploadPath())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getUploadPath, entity.getUploadPath());
        }
        if (StringUtils.isNotBlank(entity.getSourceUploadId())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getSourceUploadId, entity.getSourceUploadId());
        }
        if (StringUtils.isNotBlank(entity.getSourceUploadPath())) {
            wrapper.set(TXfNoneBusinessUploadDetailEntity::getSourceUploadPath, entity.getSourceUploadPath());
        }
        return wrapper.update();
    }

    public Page<TXfNoneBusinessUploadDetailDto> page(Long current, Long size, TXfNoneBusinessUploadQueryDto dto) {
        if ("0".equals(dto.getQueryType())) {
            dto.setCreateUser(UserUtil.getLoginName());
        }
        LambdaQueryChainWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new LambdaQueryChainWrapper<TXfNoneBusinessUploadDetailEntity>(baseMapper);
        Page<TXfNoneBusinessUploadDetailDto> page = new Page<>(current, size);
        if ("0".equals(dto.getQueryType())) {
            dto.setCreateUser(UserUtil.getLoginName());
        }
        log.debug("抬头信息分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());

        return tXfNoneBusinessUploadDetailDao.list(page, dto);
    }

    public List<TXfNoneBusinessUploadDetailDto> noPaged(TXfNoneBusinessUploadQueryDto dto) {
        LambdaQueryChainWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new LambdaQueryChainWrapper<TXfNoneBusinessUploadDetailEntity>(baseMapper);
        if ("0".equals(dto.getQueryType())) {
            dto.setCreateUser(UserUtil.getLoginName());
        }
        return tXfNoneBusinessUploadDetailDao.list(dto);
    }

    public List<TXfNoneBusinessUploadDetailDto> getByIds(List<Long> ids) {
        LambdaQueryChainWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new LambdaQueryChainWrapper<TXfNoneBusinessUploadDetailEntity>(baseMapper);
        return tXfNoneBusinessUploadDetailDao.getByIds(ids);
    }

    public void down(List<TXfNoneBusinessUploadDetailEntity> list, FileDownRequest request) {
        String path = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String ftpPath = ftpUtilService.pathprefix + path;
        log.info("文件ftp路径{}", ftpPath);
        final File tempDirectory = FileUtils.getTempDirectory();
        File file = new File(tempDirectory, path);
        file.mkdir();
        String downLoadFileName = path + ".zip";
        for (TXfNoneBusinessUploadDetailEntity fileEntity : list) {
            if (StringUtils.isNotEmpty(request.getSingle())) {
                if (1 == request.getOfd() && fileEntity.getFileType().equals(String.valueOf(Constants.FILE_TYPE_PDF)) && !"1".equals(request.getSingle())) {
                    continue;
                }
                if (1 == request.getPdf() && fileEntity.getFileType().equals(String.valueOf(Constants.FILE_TYPE_OFD)) && !"1".equals(request.getSingle())) {
                    continue;
                }
            }


            final byte[] bytes = fileService.downLoadFile4ByteArray(fileEntity.getSourceUploadId());
            try {
                String suffix = null;
                if (fileEntity.getFileType().equals(String.valueOf(Constants.FILE_TYPE_OFD))) {
                    suffix = "." + Constants.SUFFIX_OF_OFD;
                } else {
                    suffix = "." + Constants.SUFFIX_OF_PDF;
                }
                FileUtils.writeByteArrayToFile(new File(file, fileEntity.getInvoiceNo() + "-" + fileEntity.getInvoiceCode() + suffix), bytes);
            } catch (IOException e) {
                log.error("临时文件存储失败:" + e.getMessage(), e);
            }
        }
        try {
            ZipUtil.zip(file.getPath() + ".zip", file);
            String s = exportCommonService.putFile(ftpPath, tempDirectory.getPath() + "/" + downLoadFileName, downLoadFileName);
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto dto = new ExceptionReportExportDto();
            dto.setUserId(userId);
            dto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(dto.getUserId().toString());
            excelExportlogEntity.setUserName(dto.getLoginName());
            excelExportlogEntity.setConditions(JSON.toJSONString(request));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + downLoadFileName);
            this.excelExportLogService.save(excelExportlogEntity);
            dto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "非商电票源文件下载成功", exportCommonService.getSuccContent());
        } catch (Exception e) {
            log.error("下载文件打包失败:" + e.getMessage(), e);
            throw new RRException("下载文件打包失败，请重试");
        }
    }

    public R export(List<TXfNoneBusinessUploadDetailDto> resultList, List<Long> id) {

        final String excelFileName = ExcelExportUtil.getExcelFileName(UserUtil.getUserId(), "非商数据导出");
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        ExcelWriter excelWriter;
        FileInputStream inputStream = null;
        try {
            //创建一个sheet
            File file = new File(tmp + ftpPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File excl = new File(file, excelFileName);
            EasyExcel.write(tmp + ftpPath + "/" + excelFileName, TXfNoneBusinessUploadExportDto.class).sheet("sheet1").doWrite(noneBusinessConverter.exportMap(resultList));
            //推送sftp
            String ftpFilePath = ftpPath + "/" + excelFileName;
            inputStream = FileUtils.openInputStream(excl);
            ftpUtilService.uploadFile(ftpPath, excelFileName, inputStream);
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
            exportDto.setUserId(userId);
            exportDto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserName());
            excelExportlogEntity.setUserName(UserUtil.getLoginName());
            excelExportlogEntity.setConditions(JSON.toJSONString(id));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + excelFileName);
            this.excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "非商电票结果导出成功", exportCommonService.getSuccContent());
        } catch (Exception e) {
            log.error("导出异常:{}", e);
            return R.fail("导出异常");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return R.ok();
    }

    /**
     * 根据发表代码号码删除已经提交的发票信息
     *
     * @param invoiceNo
     * @param invoiceCode
     */
    public boolean deleteSubmitInvoice(String invoiceNo, String invoiceCode) {
        LambdaUpdateChainWrapper<TXfNoneBusinessUploadDetailEntity> wrapper = new LambdaUpdateChainWrapper<>(getBaseMapper());
        if (StringUtils.isEmpty(invoiceNo) || StringUtils.isEmpty(invoiceCode)) {
            return false;
        }
        wrapper.eq(TXfNoneBusinessUploadDetailEntity::getInvoiceNo, invoiceNo);
        wrapper.eq(TXfNoneBusinessUploadDetailEntity::getInvoiceCode, invoiceCode);
        wrapper.eq(TXfNoneBusinessUploadDetailEntity::getSubmitFlag, Constants.SUBMIT_NONE_BUSINESS_DONE_FLAG);
        wrapper.set(TXfNoneBusinessUploadDetailEntity::getSubmitFlag, Constants.SUBMIT_NONE_BUSINESS_UNDO_FLAG);
        return wrapper.update();
    }

    /**
     * 将发票信息改为可认证
     *
     * @param uuid
     */
    public void updateInvoiceInfo(List<String> uuid) {
        if (uuid != null && uuid.size() > 0) {
            electronicInvoiceDao.updateNoDeduction(uuid);
        }


    }
}
