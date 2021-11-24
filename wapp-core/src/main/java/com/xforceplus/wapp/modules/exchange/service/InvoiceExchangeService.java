package com.xforceplus.wapp.modules.exchange.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.enums.InvoiceExchangeStatusEnum;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.modules.backFill.model.*;
import com.xforceplus.wapp.modules.backFill.service.FileService;
import com.xforceplus.wapp.modules.backFill.service.InvoiceFileService;
import com.xforceplus.wapp.modules.backFill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.exchange.model.*;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.noneBusiness.util.ZipUtil;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfInvoiceExchangeDao;
import com.xforceplus.wapp.repository.daoExt.InvoiceFileDao;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfInvoiceExchangeEntity;
import com.xforceplus.wapp.repository.entity.TXfInvoiceFileEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;
import static com.xforceplus.wapp.modules.sys.util.UserUtil.getUserId;

/**
 * Created by SunShiyong on 2021/11/18.
 * 换票服务
 */
@Service
@Slf4j
public class InvoiceExchangeService {


    @Autowired
    private TXfInvoiceExchangeDao tXfInvoiceExchangeDao;

    @Autowired
    private RecordInvoiceService recordInvoiceService;

    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;

    @Autowired
    private InvoiceFileService invoiceFileService;

    @Autowired
    private ExportCommonService exportCommonService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FtpUtilService ftpUtilService;

    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Autowired
    private InvoiceFileDao invoiceFileDao;
    /**
     * 换票列表
     * @param request
     * @return PageResult
     */
    public PageResult<InvoiceExchangeResponse> queryPageList(QueryInvoiceExchangeRequest request){
        Page<TXfInvoiceExchangeEntity> page=new Page<>(request.getPageNo(),request.getPageSize());
        QueryWrapper<TXfInvoiceExchangeEntity> wrapper = this.getQueryWrapper(request);
        Page<TXfInvoiceExchangeEntity> pageResult = tXfInvoiceExchangeDao.selectPage(page,wrapper);
        List<InvoiceExchangeResponse> response = new ArrayList<>();
        BeanUtil.copyList(pageResult.getRecords(),response,InvoiceExchangeResponse.class);
        return PageResult.of(response,pageResult.getTotal(), pageResult.getPages(), pageResult.getSize());
    }

    /**
     * 换票（新票）详情
     * @param id
     * @return PageResult
     */
    public List<InvoiceDetailResponse> getNewInvoiceById(Long id){
        List<InvoiceDetailResponse> response = new ArrayList<>();
        TXfInvoiceExchangeEntity tXfInvoiceExchangeEntity = tXfInvoiceExchangeDao.selectById(id);
        if(StringUtils.isNotEmpty(tXfInvoiceExchangeEntity.getNewInvoiceId())){
            String[] split = tXfInvoiceExchangeEntity.getNewInvoiceId().split(",");
            for (String newInvoiceId : split) {
                InvoiceDetailResponse invoiceById = recordInvoiceService.getInvoiceById(Long.valueOf(newInvoiceId));
                response.add(invoiceById);
            }
        }
        return response;
    }

    /**
     * 换票匹配关系
     * @param request
     * @return R
     */
    public R match(BackFillExchangeRequest request){
        if(CollectionUtils.isEmpty(request.getVerifyBeanList())){
            return R.fail("回填发票列表不能为空");
        }
        if(request.getVerifyBeanList().stream().filter(t -> new BigDecimal(t.getAmount()).compareTo(BigDecimal.ZERO) < 0).count() > 1){
            return R.fail("最多允许上传一张红票");
        }
        boolean isElec = request.getVerifyBeanList().stream().allMatch(t -> InvoiceTypeEnum.isElectronic(t.getInvoiceType()));
        boolean isNotElec = request.getVerifyBeanList().stream().noneMatch(t -> InvoiceTypeEnum.isElectronic(t.getInvoiceType()));
        if(!(isElec || isNotElec)) {
            return R.fail("蓝票不允许纸电混合");
        }

        //TODO 匹配状态校验
        TXfInvoiceExchangeEntity entity = new TXfInvoiceExchangeEntity();
        entity.setId(request.getInvoiceId());
        StringBuilder sb = new StringBuilder();
        for (BackFillVerifyBean backFillVerifyBean : request.getVerifyBeanList()) {
            sb.append(backFillVerifyBean.getId()).append(",");
        }
        String newInvoiceId = StringUtils.removeEnd(sb.toString(),",");
        entity.setNewInvoiceId(newInvoiceId);
        entity.setStatus(InvoiceExchangeStatusEnum.UPLOADED.getCode());
        tXfInvoiceExchangeDao.updateById(entity);
        return R.ok();
    }

    /**
     * 完成换票操作
     * @param request
     * @return R
     */
    @Transactional
    public R finish(ExchangeFinishRequest request){
        if(CollectionUtils.isEmpty(request.getIdList())){
            return R.fail("id不能为空");
        }
        QueryWrapper<TXfInvoiceExchangeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(TXfInvoiceExchangeEntity.ID,request.getIdList());
        List<TXfInvoiceExchangeEntity> tXfInvoiceExchangeEntities = tXfInvoiceExchangeDao.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(tXfInvoiceExchangeEntities)){
            return R.fail("根据id没有查询到换票");
        }
        UpdateWrapper<TXfInvoiceExchangeEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(TXfInvoiceExchangeEntity.ID,request.getIdList());
        TXfInvoiceExchangeEntity invoiceExchangeEntity = new TXfInvoiceExchangeEntity();
        invoiceExchangeEntity.setVoucherNo(request.getVoucherNo());
        invoiceExchangeEntity.setStatus(InvoiceExchangeStatusEnum.FINISHED.getCode());
        tXfInvoiceExchangeDao.update(invoiceExchangeEntity,updateWrapper);
        //修改发票状态可以去认证
        for (TXfInvoiceExchangeEntity tXfInvoiceExchangeEntity : tXfInvoiceExchangeEntities) {
            String[] split = tXfInvoiceExchangeEntity.getNewInvoiceId().split(",");
            List<Long> idList = Arrays.stream(split).map(Long::parseLong).collect(Collectors.toList());
            UpdateWrapper<TDxRecordInvoiceEntity> invoiceUpdateWrapper = new UpdateWrapper<>();
            invoiceUpdateWrapper.in(TDxRecordInvoiceEntity.ID,idList);
            TDxRecordInvoiceEntity entity = new TDxRecordInvoiceEntity();
            entity.setConfirmStatus("1");
            entity.setConfirmTime(new Date());
            tDxRecordInvoiceDao.update(entity,invoiceUpdateWrapper);
        }
        return R.ok("换票完成");

    }

    /**
     * 手工确认换票
     * @param request
     * @return R
     */
    public R confirm(ExchangeSaveRequest request){
        if(CollectionUtils.isEmpty(request.getIdList())){
            return R.fail("id不能为空");
        }
        QueryWrapper<TDxRecordInvoiceEntity>  queryWrapper = new QueryWrapper<>();
        queryWrapper.in(TDxRecordInvoiceEntity.ID,request.getIdList());
        List<TDxRecordInvoiceEntity> tDxRecordInvoiceEntities = tDxRecordInvoiceDao.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(tDxRecordInvoiceEntities)){
            return R.fail("根据id未找到对应的发票");
        }
        this.saveBatch(tDxRecordInvoiceEntities, request.getReason());
        return R.ok();
    }

    @Transactional
    public int saveBatch(List<TDxRecordInvoiceEntity> tDxRecordInvoiceEntities,String remark){
        List<Long> idList = tDxRecordInvoiceEntities.stream().map(TDxRecordInvoiceEntity :: getId).collect(Collectors.toList());
        log.info("换票批量保存--需要换票的发票id:{}", JSONArray.toJSONString(idList));
        QueryWrapper<TXfInvoiceExchangeEntity> invoiceExchangeWrapper = new QueryWrapper<>();
        invoiceExchangeWrapper.in(TXfInvoiceExchangeEntity.INVOICE_ID,idList);
        List<TXfInvoiceExchangeEntity> tXfInvoiceExchangeEntities = tXfInvoiceExchangeDao.selectList(invoiceExchangeWrapper);
        List<Long> filterList = tXfInvoiceExchangeEntities.stream().map(TXfInvoiceExchangeEntity :: getInvoiceId).collect(Collectors.toList());
        log.info("换票批量保存--已提交的发票id:{}", JSONArray.toJSONString(filterList));
        List<TDxRecordInvoiceEntity> newInvoice = tDxRecordInvoiceEntities.stream().filter(t -> !filterList.contains(t.getId())).collect(Collectors.toList());
        TXfInvoiceExchangeEntity exchangeEntity;
        int successs = 0;
        try {
            for (TDxRecordInvoiceEntity entity : newInvoice) {
                exchangeEntity = new TXfInvoiceExchangeEntity();
                exchangeEntity.setInvoiceId(entity.getId());
                exchangeEntity.setAmountWithoutTax(entity.getInvoiceAmount());
                exchangeEntity.setTaxAmount(entity.getTaxAmount());
                //exchangeEntity.setBusinessType("");
                exchangeEntity.setCreateTime(new Date());
                exchangeEntity.setInvoiceCode(entity.getInvoiceCode());
                exchangeEntity.setInvoiceNo(entity.getInvoiceNo());
                exchangeEntity.setInvoiceType(entity.getInvoiceType());
                exchangeEntity.setJvcode(entity.getJvcode());
                exchangeEntity.setVenderid(entity.getVenderid());
                exchangeEntity.setRemark(remark);
                //exchangeEntity.setReturnNo("");
                if(tXfInvoiceExchangeDao.insert(exchangeEntity) >0){
                    successs++;
                }
            }
        } catch (Exception e) {
            log.error("换票批量保存--异常",e);
        }
        log.info("换票批量保存--成功数量：{}",successs);
        return successs;
    }


    public R upload(MultipartFile file,String newInvoiceId, String vendorid) {
        try {
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            StringBuffer fileName = new StringBuffer();
            fileName.append(UUID.randomUUID().toString());
            fileName.append(".");
            int type;
            if (suffix.toLowerCase().equals(Constants.SUFFIX_OF_OFD)) {
                fileName.append(Constants.SUFFIX_OF_OFD);
                type = Constants.FILE_TYPE_OFD;
            } else if(suffix.toLowerCase().equals(Constants.SUFFIX_OF_PDF)){
                fileName.append(Constants.SUFFIX_OF_PDF);
                type = Constants.FILE_TYPE_PDF;
            }else{
                throw new EnhanceRuntimeException("文件:[" + fileName + "]类型不正确,应为:[ofd/pdf]");
            }
            String[] split = newInvoiceId.split(",");
            TDxRecordInvoiceEntity entity = tDxRecordInvoiceDao.selectById(Long.valueOf(split[0]));
            if(entity != null){
                String uploadResult = fileService.uploadFile(file.getBytes(), fileName.toString(), vendorid);
                UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
                UploadFileResultData data = uploadFileResult.getData();
                invoiceFileService.save(entity.getInvoiceCode(),entity.getInvoiceNo(),data.getUploadPath(),type,getUserId());
            }else{
                return R.fail("根据id未找到发票");
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new EnhanceRuntimeException("上传文件异常");
        }
        return R.ok("上传成功");
    }

    public  R download(String newInvoiceId) {
        TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
        String title;
        String content;
        try {
            String[] split = newInvoiceId.split(",");
            List<Long> idList = Arrays.stream(split).map(Long::parseLong).collect(Collectors.toList());
            QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
            wrapper.in(TDxRecordInvoiceEntity.ID,idList);
            List<TDxRecordInvoiceEntity> tDxRecordInvoiceEntities = tDxRecordInvoiceDao.selectList(wrapper);
            if(CollectionUtils.isEmpty(tDxRecordInvoiceEntities)){
                return R.fail("成请失败，根据id未找到发票");
            }
            String path = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String ftpPath = ftpUtilService.pathprefix + path;
            final File tempDirectory = FileUtils.getTempDirectory();
            File file = new File(tempDirectory, path);
            file.mkdir();
            String downLoadFileName = "电票源文件" + ".zip";
            List<Integer> types = new ArrayList<>();
            types.add(InvoiceFileEntity.TYPE_OF_OFD);
            types.add(InvoiceFileEntity.TYPE_OF_PDF);
            for (TDxRecordInvoiceEntity invoice : tDxRecordInvoiceEntities) {
                List<TXfInvoiceFileEntity> invoiceAndTypes = invoiceFileDao.getByInvoiceAndTypes(invoice.getInvoiceNo(), invoice.getInvoiceCode(), types);
                for (TXfInvoiceFileEntity tXfInvoiceFileEntity : invoiceAndTypes) {
                    byte[] bytes = fileService.downLoadFile4ByteArray(tXfInvoiceFileEntity.getPath());
                    String suffix;
                    if (tXfInvoiceFileEntity.getType().equals(Constants.FILE_TYPE_OFD)) {
                        suffix = "." + Constants.SUFFIX_OF_OFD;
                    } else {
                        suffix = "." + Constants.SUFFIX_OF_PDF;
                    }
                    FileUtils.writeByteArrayToFile(new File(file, tXfInvoiceFileEntity.getInvoiceNo() + "-" + tXfInvoiceFileEntity.getInvoiceCode() + suffix), bytes);
                }
            }
            ZipUtil.zip(file.getPath() + ".zip", file);
            exportCommonService.putFile(ftpPath, tempDirectory.getPath() + "/" + downLoadFileName, downLoadFileName);
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(getUserId().toString());
            excelExportlogEntity.setUserName(UserUtil.getUserName());
            excelExportlogEntity.setConditions(newInvoiceId);
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + downLoadFileName);
            title = "电票源文件下载成功";
            content = exportCommonService.getSuccContent();
        } catch (Exception e) {
            log.info(e.getMessage());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
            excelExportlogEntity.setErrmsg(e.getMessage());
            title = "电票源文件下载失败";
            content = exportCommonService.getFailContent(e.getMessage());
        }
        excelExportLogService.save(excelExportlogEntity);
        exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), title,content);
        return R.ok("请求成功，请往消息中心查看下载结果");
    }


    private QueryWrapper<TXfInvoiceExchangeEntity> getQueryWrapper(QueryInvoiceExchangeRequest request){
        QueryWrapper<TXfInvoiceExchangeEntity> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc(TXfInvoiceExchangeEntity.ID);
        if(StringUtils.isNotEmpty(request.getJvcode())){
            wrapper.eq(TXfInvoiceExchangeEntity.JVCODE,request.getJvcode());
        }
        if(StringUtils.isNotEmpty(request.getBusinessType())){
            wrapper.eq(TXfInvoiceExchangeEntity.BUSINESS_TYPE,request.getBusinessType());
        }
        if(StringUtils.isNotEmpty(request.getInvoiceType())){
            wrapper.eq(TXfInvoiceExchangeEntity.INVOICE_TYPE,request.getInvoiceType());
        }
        if(StringUtils.isNotEmpty(request.getPaperDrewStartDate())){
            wrapper.ge(TXfInvoiceExchangeEntity.PAPER_DREW_DATE,request.getPaperDrewStartDate());
        }
        if(StringUtils.isNotEmpty(request.getPaperDrewEndDate())){
            wrapper.le(TXfInvoiceExchangeEntity.PAPER_DREW_DATE,request.getPaperDrewEndDate());
        }
        if(StringUtils.isNotEmpty(request.getReturnNo())){
            wrapper.eq(TXfInvoiceExchangeEntity.RETURN_NO,request.getReturnNo());
        }
        if(StringUtils.isNotEmpty(request.getReturnStartDate())){
            wrapper.apply("{0} <= CONVERT(varchar(10), create_time, 23)",request.getReturnStartDate());
        }
        if(StringUtils.isNotEmpty(request.getReturnEndDate())){
            wrapper.apply("{0} >= CONVERT(varchar(10), create_time, 23)",request.getReturnEndDate());
        }
        if(StringUtils.isNotEmpty(request.getVenderid())){
            wrapper.eq(TXfInvoiceExchangeEntity.VENDERID,request.getVenderid());
        }
        if(request.getStatus() != null){
            wrapper.eq(TXfInvoiceExchangeEntity.STATUS, request.getStatus());
        }
        return wrapper;

    }

}
