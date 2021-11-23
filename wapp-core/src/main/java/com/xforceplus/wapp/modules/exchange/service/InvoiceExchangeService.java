package com.xforceplus.wapp.modules.exchange.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.enums.InvoiceExchangeStatusEnum;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.modules.backFill.model.BackFillVerifyBean;
import com.xforceplus.wapp.modules.backFill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.backFill.service.FileService;
import com.xforceplus.wapp.modules.backFill.service.InvoiceFileService;
import com.xforceplus.wapp.modules.backFill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.exchange.model.*;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.noneBusiness.util.ZipUtil;
import com.xforceplus.wapp.modules.rednotification.mapstruct.IdGenerator;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfInvoiceExchangeDao;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

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
    public R finish(ExchangeFinishRequest request){
        if(CollectionUtils.isEmpty(request.getIdList())){
            return R.fail("id不能为空");
        }
        UpdateWrapper<TXfInvoiceExchangeEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(TXfInvoiceExchangeEntity.ID,request.getIdList());
        TXfInvoiceExchangeEntity entity = new TXfInvoiceExchangeEntity();
        entity.setVoucherNo(request.getVoucherNo());
        entity.setStatus(InvoiceExchangeStatusEnum.FINISHED.getCode());
        if(tXfInvoiceExchangeDao.update(entity,updateWrapper) > 0){
            return R.ok("换票成功");
        }else{
            return R.fail("换票失败");
        }
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

    public  R download(String invoiceCode,String invoiceNo) {
        TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
        String title = "";
        String content = "";
        try {
            List<TXfInvoiceFileEntity> byInvoice = invoiceFileService.getByInvoice(invoiceNo, invoiceCode);
            String path = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String ftpPath = ftpUtilService.pathprefix + path;
            final File tempDirectory = FileUtils.getTempDirectory();
            File file = new File(tempDirectory, path);
            file.mkdir();
            String downLoadFileName = "电票源文件" + ".zip";
            for (TXfInvoiceFileEntity tXfInvoiceFileEntity : byInvoice) {
                byte[] bytes = fileService.downLoadFile4ByteArray(tXfInvoiceFileEntity.getPath());
                String suffix;
                if (tXfInvoiceFileEntity.getType().equals(String.valueOf(Constants.FILE_TYPE_OFD))) {
                    suffix = "." + Constants.SUFFIX_OF_OFD;
                } else {
                    suffix = "." + Constants.SUFFIX_OF_PDF;
                }
                FileUtils.writeByteArrayToFile(new File(file, tXfInvoiceFileEntity.getInvoiceNo() + "-" + tXfInvoiceFileEntity.getInvoiceCode() + suffix), bytes);
            }
            ZipUtil.zip(file.getPath() + ".zip", file);
            exportCommonService.putFile(ftpPath, tempDirectory.getPath() + "/" + downLoadFileName, downLoadFileName);
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserId().toString());
            excelExportlogEntity.setUserName(UserUtil.getUserName());
            excelExportlogEntity.setConditions(invoiceCode+invoiceNo);
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
        return wrapper;

    }

}
