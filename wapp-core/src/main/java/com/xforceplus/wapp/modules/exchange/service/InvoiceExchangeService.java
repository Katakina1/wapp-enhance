package com.xforceplus.wapp.modules.exchange.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.IsDealEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.enums.ExchangeTickeyStatusEnum;
import com.xforceplus.wapp.enums.InvoiceExchangeStatusEnum;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.backfill.model.*;
import com.xforceplus.wapp.modules.backfill.service.FileService;
import com.xforceplus.wapp.modules.backfill.service.InvoiceFileService;
import com.xforceplus.wapp.modules.backfill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.exchange.model.*;
import com.xforceplus.wapp.modules.exchangeTicket.service.ExchangeTicketService;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.noneBusiness.util.ZipUtil;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfBlueRelationDao;
import com.xforceplus.wapp.repository.dao.TXfExchangeTicketDao;
import com.xforceplus.wapp.repository.dao.TXfInvoiceExchangeDao;
import com.xforceplus.wapp.repository.daoExt.InvoiceFileDao;
import com.xforceplus.wapp.repository.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.enums.ExchangeTickeyStatusEnum.EXCHANGE_TICKEY_STATUS_FAIL;
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

    @Value("${wapp.export.tmp}")
    private String tmp;
    @Autowired
    private ExchangeExportConverter exchangeExportConverter;

    @Autowired
    private ExchangeTicketService exchangeTicketService;

    @Autowired
    private TXfExchangeTicketDao tXfExchangeTicketDao;
    @Autowired
    private TXfBlueRelationDao tXfBlueRelationDao;

    /**
     * 换票列表
     * @param request
     * @return PageResult
     */
    public PageResult<InvoiceExchangeResponse> queryPageList(QueryInvoiceExchangeRequest request){
        Page<TDxRecordInvoiceEntity> page=new Page<>(request.getPageNo(),request.getPageSize());
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = this.getQueryWrapper(request);
        Page<TDxRecordInvoiceEntity> pageResult = tDxRecordInvoiceDao.selectPage(page,wrapper);
        List<InvoiceExchangeResponse> response = new ArrayList<>();
        InvoiceExchangeResponse invoice;
        if(CollectionUtils.isNotEmpty(pageResult.getRecords())){
            for (TDxRecordInvoiceEntity entity : pageResult.getRecords()) {
                invoice = new InvoiceExchangeResponse();
                invoice.setInvoiceId(entity.getId());
                invoice.setFlowType(entity.getFlowType());
                invoice.setInvoiceCode(entity.getInvoiceCode());
                invoice.setInvoiceNo(entity.getInvoiceNo());
                invoice.setInvoiceType(entity.getInvoiceType());
                invoice.setPaperDrewDate(DateUtils.dateToStr(entity.getInvoiceDate()));
                invoice.setJvcode(entity.getJvcode());
                invoice.setRebateDate(entity.getRebateDate());
                invoice.setExchangeReason(entity.getExchangeReason());
                invoice.setSellerNo(entity.getVenderid());
                invoice.setTaxAmount(entity.getTaxAmount());
                invoice.setAmountWithoutTax(entity.getInvoiceAmount());
                invoice.setAmountWithTax(entity.getTotalAmount());
                invoice.setExchangeStatus(entity.getExchangeStatus());
                invoice.setGfName(entity.getGfName());
                invoice.setXfName(entity.getXfName());
                invoice.setTaxRate(entity.getTaxRate());
                invoice.setGfTaxNo(entity.getGfTaxNo());
                invoice.setXfTaxNo(entity.getXfTaxNo());
                response.add(invoice);
            }
        }

        return PageResult.of(response,pageResult.getTotal(), pageResult.getPages(), pageResult.getSize());
    }

    public List<InvoiceExchangeResponse> getByIds(List<Long> idList){
        List<TDxRecordInvoiceEntity> result= tDxRecordInvoiceDao.selectBatchIds(idList);
        List<InvoiceExchangeResponse> response = new ArrayList<>();
        InvoiceExchangeResponse invoice;
        if(CollectionUtils.isNotEmpty(result)){
            for (TDxRecordInvoiceEntity entity : result) {
                invoice = new InvoiceExchangeResponse();
                invoice.setInvoiceId(entity.getId());
                invoice.setFlowType(entity.getFlowType());
                invoice.setInvoiceCode(entity.getInvoiceCode());
                invoice.setInvoiceNo(entity.getInvoiceNo());
                invoice.setInvoiceType(entity.getInvoiceType());
                invoice.setPaperDrewDate(DateUtils.dateToStr(entity.getInvoiceDate()));
                invoice.setJvcode(entity.getJvcode());
                invoice.setRebateDate(entity.getRebateDate());
                invoice.setExchangeReason(entity.getExchangeReason());
                invoice.setSellerNo(entity.getVenderid());
                invoice.setTaxAmount(entity.getTaxAmount());
                invoice.setAmountWithoutTax(entity.getInvoiceAmount());
                invoice.setAmountWithTax(entity.getTotalAmount());
                invoice.setExchangeStatus(entity.getExchangeStatus());
                invoice.setGfName(entity.getGfName());
                invoice.setXfName(entity.getXfName());
                invoice.setTaxRate(entity.getTaxRate());
                invoice.setGfTaxNo(entity.getGfTaxNo());
                invoice.setXfTaxNo(entity.getXfTaxNo());
                response.add(invoice);
            }
        }
        return response;
    }
    public List<InvoiceExchangeResponse> noPaged(QueryInvoiceExchangeRequest request){
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = this.getQueryWrapper(request);
        List<TDxRecordInvoiceEntity> result= tDxRecordInvoiceDao.selectList(wrapper);
        List<InvoiceExchangeResponse> response = new ArrayList<>();
        InvoiceExchangeResponse invoice;
        if(CollectionUtils.isNotEmpty(result)){
            for (TDxRecordInvoiceEntity entity : result) {
                invoice = new InvoiceExchangeResponse();
                invoice.setInvoiceId(entity.getId());
                invoice.setFlowType(entity.getFlowType());
                invoice.setInvoiceCode(entity.getInvoiceCode());
                invoice.setInvoiceNo(entity.getInvoiceNo());
                invoice.setInvoiceType(entity.getInvoiceType());
                invoice.setPaperDrewDate(DateUtils.dateToStr(entity.getInvoiceDate()));
                invoice.setJvcode(entity.getJvcode());
                invoice.setRebateDate(entity.getRebateDate());
                invoice.setExchangeReason(entity.getExchangeReason());
                invoice.setSellerNo(entity.getVenderid());
                invoice.setTaxAmount(entity.getTaxAmount());
                invoice.setAmountWithoutTax(entity.getInvoiceAmount());
                invoice.setAmountWithTax(entity.getTotalAmount());
                invoice.setExchangeStatus(entity.getExchangeStatus());
                invoice.setGfName(entity.getGfName());
                invoice.setXfName(entity.getXfName());
                invoice.setTaxRate(entity.getTaxRate());
                invoice.setGfTaxNo(entity.getGfTaxNo());
                invoice.setXfTaxNo(entity.getXfTaxNo());
                response.add(invoice);
            }
        }
        return response;
    }
    /**
     * 换票（新票）详情
     * @param id
     * @return PageResult
     */
    public List<InvoiceDetailResponse> getNewInvoiceById(Long id){
        List<InvoiceDetailResponse> response = new ArrayList<>();
        QueryWrapper<TXfInvoiceExchangeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfInvoiceExchangeEntity.INVOICE_ID, id);
        List<TXfInvoiceExchangeEntity> tXfInvoiceExchangeEntities = tXfInvoiceExchangeDao.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(tXfInvoiceExchangeEntities)){
            for (TXfInvoiceExchangeEntity entity : tXfInvoiceExchangeEntities) {
                InvoiceDetailResponse invoiceById = recordInvoiceService.getInvoiceById(entity.getNewInvoiceId());
                response.add(invoiceById);
            }
        }
        return response;
    }

    /**
     * 保存换票关系
     * @param request
     * @return R
     */
    @Transactional
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
            return R.fail("发票不允许纸电混合");
        }
        TDxRecordInvoiceEntity recordInvoiceEntity = tDxRecordInvoiceDao.selectById(request.getInvoiceId());
        if(recordInvoiceEntity == null){
            return R.fail("根据id没有找到发票");
        }
        BigDecimal amount = request.getVerifyBeanList().stream().filter(t -> new BigDecimal(t.getAmount()).compareTo(BigDecimal.ZERO) > 0).map(t -> new BigDecimal(t.getAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
        if(amount.compareTo(recordInvoiceEntity.getInvoiceAmount()) != 0){
            return R.fail("上传的发票与需要换票的金额不一致");
        }
        List<Long> idList = request.getVerifyBeanList().stream().filter(t-> new BigDecimal(t.getAmount()).compareTo(BigDecimal.ZERO) >0).map(BackFillVerifyBean::getId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(idList)){
            QueryWrapper<TDxRecordInvoiceEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(TDxRecordInvoiceEntity.ID,idList);
            queryWrapper.ne(TDxRecordInvoiceEntity.DXHY_MATCH_STATUS, 0);
            queryWrapper.eq(TDxRecordInvoiceEntity.IS_DEL, IsDealEnum.NO.getValue());
            if(tDxRecordInvoiceDao.selectCount(queryWrapper) > 0){
                return R.fail("蓝票必须是未匹配状态");
            }

            QueryWrapper<TXfInvoiceExchangeEntity> exchangeQueryWrapper = new QueryWrapper<>();
            exchangeQueryWrapper.in(TXfInvoiceExchangeEntity.NEW_INVOICE_ID, idList);
            if(tXfInvoiceExchangeDao.selectCount(exchangeQueryWrapper) >0){
                return R.fail("上传的发票已换过票");
            }
        }
        //修改发票状态
        if(!this.updateExchangeStatus(Lists.newArrayList(request.getInvoiceId()), InvoiceExchangeStatusEnum.UPLOADED, null)){
            return R.fail("修改换票异常");
        }
        QueryWrapper<TXfExchangeTicketEntity> queryExchangeTicketWrapper = new QueryWrapper<>();
        queryExchangeTicketWrapper.eq(StringUtils.isNotBlank(recordInvoiceEntity.getInvoiceCode()), TXfExchangeTicketEntity.INVOICE_CODE, recordInvoiceEntity.getInvoiceCode());
        queryExchangeTicketWrapper.eq(TXfExchangeTicketEntity.INVOICE_NO, recordInvoiceEntity.getInvoiceNo());
        queryExchangeTicketWrapper.ne(TXfExchangeTicketEntity.EXCHANGE_STATUS, EXCHANGE_TICKEY_STATUS_FAIL.getCode());
        TXfExchangeTicketEntity exchangeTicketEntity = tXfExchangeTicketDao.selectOne(queryExchangeTicketWrapper);
        if (null == exchangeTicketEntity){
            return  R.fail("未查询到换票数据");
        }
        for (BackFillVerifyBean fillVerifyBean : request.getVerifyBeanList()) {
            if (!recordInvoiceEntity.getGfTaxNo().equals(fillVerifyBean.getGfTaxNo())) {
                return R.fail("上传的发票与需要换票的购方税号不一致");
            }
            if (!recordInvoiceEntity.getGfName().equals(fillVerifyBean.getGfName())) {
                return R.fail("上传的发票与需要换票的购方名称不一致");
            }
            if (!recordInvoiceEntity.getXfTaxNo().equals(fillVerifyBean.getXfTaxNo())) {
                return R.fail("上传的发票与需要换票的销方税号不一致");
            }
            if (!recordInvoiceEntity.getXfName().equals(fillVerifyBean.getXfName())) {
                return R.fail("上传的发票与需要换票的销方名称不一致");
            }
        }
        String reason =  request.getExchangeReason();
        BigDecimal totalAmount = request.getVerifyBeanList().stream().filter(t -> new BigDecimal(t.getTotalAmount()).compareTo(BigDecimal.ZERO) > 0).map(t -> new BigDecimal(t.getTotalAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (StringUtils.isNotEmpty(reason) && "税率变更".equals(reason) && totalAmount.compareTo(recordInvoiceEntity.getTotalAmount()) != 0) {
            return R.fail("上传的发票与需要换票的价税合计不一致");
        }
        if (StringUtils.isNotEmpty(reason) && "作废或红冲".equals(reason)) {
            for (BackFillVerifyBean backFillVerifyBean : request.getVerifyBeanList()) {
                if (!new BigDecimal(backFillVerifyBean.getAmount()).equals(recordInvoiceEntity.getInvoiceAmount())) {
                    return R.fail("上传的发票与需要换票的金额不一致");
                }
                if (!backFillVerifyBean.getTaxRate().equals(recordInvoiceEntity.getTaxRate() + "")) {
                    return R.fail("上传的发票与需要换票的税率不一致");
                }
                if (new BigDecimal(backFillVerifyBean.getTaxAmount()).compareTo(recordInvoiceEntity.getTaxAmount()) != 0) {
                    return R.fail("上传的发票与需要换票的税额不一致");
                }
                if (new BigDecimal(backFillVerifyBean.getTotalAmount()).compareTo(recordInvoiceEntity.getTotalAmount()) != 0) {
                    return R.fail("上传的发票与需要换票的价税合计不一致");
                }
            }
        }

        List<Long> verifyIds = request.getVerifyBeanList().stream().map(BackFillVerifyBean::getId).collect(Collectors.toList());
        List<TDxRecordInvoiceEntity> verifyEntities = tDxRecordInvoiceDao.selectBatchIds(verifyIds);
        if (verifyEntities.size() != verifyIds.size()) {
            return R.fail("上传的发票未查到数据");
        }
        //大象平台匹配状态  0未匹配、1预匹配（没有发票）、2部分匹配、3完全匹配、4差异匹配、5匹配失败、6取消匹配
        List<String> unMatch = Arrays.asList("0", "5", "6");
        // 未签收、未匹配、未付款、未认证、未换票、未蓝冲的发票才允许上传
        for (TDxRecordInvoiceEntity it: verifyEntities) {
//            电票上传就会被签收，这个状态控制去掉
//            if ("1".equals(it.getQsStatus())) {
//                return R.fail("上传的发票签收状态不满足条件");
//            }
            if (!unMatch.contains(it.getDxhyMatchStatus())) {
                return R.fail("发票号码：" + it.getInvoiceNo() + "，状态已有匹配关系，请解除后重新上传");
            }
            if (!"0".equals(it.getHostStatus())) {
                return R.fail("发票号码：" + it.getInvoiceNo() + "，已付款不允许换票");
            }
            if ("1".equals(it.getRzhYesorno())) {
                return R.fail("发票号码：" + it.getInvoiceNo() + "，已认证不允许换票");
            }
        }

        LambdaQueryChainWrapper<TXfExchangeTicketEntity> exchangeQ = new LambdaQueryChainWrapper<>(tXfExchangeTicketDao);
        LambdaQueryChainWrapper<TXfBlueRelationEntity> blueQ = new LambdaQueryChainWrapper<>(tXfBlueRelationDao);
        verifyEntities.forEach(it -> {
            exchangeQ.or(or -> or.eq(TXfExchangeTicketEntity::getInvoiceNo, it.getInvoiceNo()).eq(StringUtils.isNotBlank(it.getInvoiceCode()), TXfExchangeTicketEntity::getInvoiceCode, it.getInvoiceCode()));
            blueQ.or(or -> or.eq(TXfBlueRelationEntity::getBlueInvoiceNo, it.getInvoiceNo()).eq(StringUtils.isNotBlank(it.getInvoiceCode()), TXfBlueRelationEntity::getBlueInvoiceCode, it.getInvoiceCode()));
        });
        List<TXfExchangeTicketEntity> exchangeOpt = exchangeQ.list();
        if (CollectionUtils.isNotEmpty(exchangeOpt)) {
            return R.fail("发票号码：" + exchangeOpt.get(0).getInvoiceNo() + "，已发生换票业务，不允许换票");
        }
        List<TXfBlueRelationEntity> blueOpt = blueQ.list();
        if (CollectionUtils.isNotEmpty(blueOpt)) {
            return R.fail("发票号码：" + blueOpt.get(0).getBlueInvoiceNo() + "，为蓝冲票，不允许换票");
        }
        //修改换票成功的发票为FlowType = 7
    	UpdateWrapper<TDxRecordInvoiceEntity> entityUpdateWrapper = new UpdateWrapper<>();
        entityUpdateWrapper.in(TDxRecordInvoiceEntity.ID, verifyIds);
        TDxRecordInvoiceEntity updateEntity = new TDxRecordInvoiceEntity();
        updateEntity.setFlowType("7"); //WALMART-3411
    	tDxRecordInvoiceDao.update(updateEntity, entityUpdateWrapper);
        //保存新上传发票id到换票
        TXfInvoiceExchangeEntity entity;
        for (BackFillVerifyBean backFillVerifyBean : request.getVerifyBeanList()) {
            entity = new TXfInvoiceExchangeEntity();
            entity.setInvoiceId(request.getInvoiceId());
            entity.setCreateTime(new Date());
            entity.setNewInvoiceId(backFillVerifyBean.getId());
            tXfInvoiceExchangeDao.insert(entity);


            TXfExchangeTicketEntity tXfExchangeTicketEntity = new TXfExchangeTicketEntity();
            tXfExchangeTicketEntity.setExchangeStatus(ExchangeTickeyStatusEnum.EXCHANGE_TICKEY_STATUS_UPLOAD.getCode());
            tXfExchangeTicketEntity.setExchangeAmountWithTax(new BigDecimal(backFillVerifyBean.getTotalAmount()));
            tXfExchangeTicketEntity.setExchangeAmountWithoutTax(new BigDecimal(backFillVerifyBean.getAmount()));
            tXfExchangeTicketEntity.setExchangeTaxAmount(new BigDecimal(backFillVerifyBean.getTaxAmount()));
            tXfExchangeTicketEntity.setExchangeInvoiceCode(backFillVerifyBean.getInvoiceCode());
            tXfExchangeTicketEntity.setExchangeInvoiceNo(backFillVerifyBean.getInvoiceNo());
            tXfExchangeTicketEntity.setInvoiceCode(recordInvoiceEntity.getInvoiceCode());
            tXfExchangeTicketEntity.setInvoiceNo(recordInvoiceEntity.getInvoiceNo());
            String invoiceDate =backFillVerifyBean.getInvoiceDate();
            if(StringUtils.isNotEmpty(invoiceDate)){
                invoiceDate=invoiceDate.replaceAll("-","").substring(0,8);
            }
            tXfExchangeTicketEntity.setExchangePaperDate(invoiceDate);
            tXfExchangeTicketEntity.setExchangeTaxRate(new BigDecimal(backFillVerifyBean.getTaxRate()));
            tXfExchangeTicketEntity.setExchangeReason(request.getExchangeReason());
            tXfExchangeTicketEntity.setInvoiceId(String.valueOf(request.getInvoiceId()));
            exchangeTicketService.update(tXfExchangeTicketEntity);
        }
        return R.ok(null,"保存成功");
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
        queryWrapper.in(TXfInvoiceExchangeEntity.INVOICE_ID,request.getIdList());
        List<TXfInvoiceExchangeEntity> tXfInvoiceExchangeEntities = tXfInvoiceExchangeDao.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(tXfInvoiceExchangeEntities)){
            return R.fail("根据id没有查询到换票");
        }
        UpdateWrapper<TXfInvoiceExchangeEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(TXfInvoiceExchangeEntity.INVOICE_ID,request.getIdList());
        TXfInvoiceExchangeEntity invoiceExchangeEntity = new TXfInvoiceExchangeEntity();
        invoiceExchangeEntity.setVoucherNo(request.getVoucherNo());
        tXfInvoiceExchangeDao.update(invoiceExchangeEntity,updateWrapper);
        //修改发票状态可以去认证
        List<Long> idList = tXfInvoiceExchangeEntities.stream().map(TXfInvoiceExchangeEntity :: getInvoiceId).collect(Collectors.toList());
        UpdateWrapper<TDxRecordInvoiceEntity> invoiceUpdateWrapper = new UpdateWrapper<>();
        invoiceUpdateWrapper.in(TDxRecordInvoiceEntity.ID,idList);
        TDxRecordInvoiceEntity entity = new TDxRecordInvoiceEntity();
        entity.setExchangeStatus(InvoiceExchangeStatusEnum.FINISHED.getCode());
        entity.setConfirmStatus("1");
        entity.setConfirmTime(new Date());
        tDxRecordInvoiceDao.update(entity,invoiceUpdateWrapper);
        return R.ok(null,"换票完成");

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
        if(!tDxRecordInvoiceEntities.stream().allMatch(a ->a.getExchangeStatus() == null || InvoiceExchangeStatusEnum.DEFAULT.getCode().equals(a.getExchangeStatus()))){
            return R.fail("发票状态为已换票");
        }
        if(!this.updateExchangeStatus(request.getIdList(), InvoiceExchangeStatusEnum.TO_BE_EXCHANGE,request.getReason())){
            return R.fail("确认换票失败");
        }
        return R.ok(null,"确认成功");
    }

    @Transactional
    public boolean updateExchangeStatus(List<Long> idList,InvoiceExchangeStatusEnum statusEnum,String reason){
        log.info("换票批量修改状态--需要换票的发票id:{}", idList);
        int successs = 0;
        try {
            UpdateWrapper<TDxRecordInvoiceEntity> entityUpdateWrapper = new UpdateWrapper<>();
            entityUpdateWrapper.in(TDxRecordInvoiceEntity.ID, idList);
            TDxRecordInvoiceEntity entity = new TDxRecordInvoiceEntity();
            entity.setExchangeStatus(statusEnum.getCode());
            entity.setExchangeReason(reason);
            //entity.setFlowType("7"); WALMART-3411
            successs= tDxRecordInvoiceDao.update(entity, entityUpdateWrapper);
        } catch (Exception e) {
            log.error("换票批量修改状态--异常",e);
            return false;
        }
        log.info("换票批量修改状态--成功数量：{}",successs);
        return true;
    }


    @Transactional
    public R upload(MultipartFile[] files,Long invoiceId) {
        try {
            for (MultipartFile file : files) {
                String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                StringBuffer fileName = new StringBuffer();
                fileName.append(UUID.randomUUID().toString());
                int type;
                if (suffix.toLowerCase().equals(Constants.SUFFIX_OF_OFD)) {
                    fileName.append(InvoiceFileEntity.SUFFIX_OF_OFD);
                    type = InvoiceFileEntity.TYPE_OF_OFD;
                } else if(suffix.toLowerCase().equals(Constants.SUFFIX_OF_PDF)){
                    fileName.append(InvoiceFileEntity.SUFFIX_OF_PDF);
                    type = InvoiceFileEntity.TYPE_OF_PDF;
                }else{
                    throw new EnhanceRuntimeException("文件:[" + fileName + "]类型不正确,应为:[ofd/pdf]");
                }
                QueryWrapper<TXfInvoiceExchangeEntity> wrapper = new QueryWrapper<>();
                wrapper.in(TXfInvoiceExchangeEntity.INVOICE_ID,invoiceId);
                List<TXfInvoiceExchangeEntity> tXfInvoiceExchangeEntities = tXfInvoiceExchangeDao.selectList(wrapper);
                if(CollectionUtils.isEmpty(tXfInvoiceExchangeEntities)){
                    return R.fail("根据id没有找到换票");
                }
                TDxRecordInvoiceEntity entity = tDxRecordInvoiceDao.selectById(tXfInvoiceExchangeEntities.get(0).getNewInvoiceId());
                if(entity == null){
                    return R.fail("根据id没有找到发票");
                }
                String uploadResult = fileService.uploadFile(file.getBytes(), fileName.toString(), UserUtil.getUser().getUsercode());
                UploadFileResult uploadFileResult = JsonUtil.fromJson(uploadResult, UploadFileResult.class);
                UploadFileResultData data = uploadFileResult.getData();
                invoiceFileService.save(entity.getInvoiceCode(),entity.getInvoiceNo(),data.getUploadPath(),type,getUserId());
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new EnhanceRuntimeException("上传文件异常");
        }
        return R.ok(null,"上传成功");
    }

    public  R download(Long invoiceId) {
        TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
        excelExportlogEntity.setUserAccount(UserUtil.getUserId().toString());
        excelExportlogEntity.setUserName(UserUtil.getUserName());
        excelExportlogEntity.setConditions(invoiceId.toString());
        excelExportlogEntity.setStartDate(new Date());
        excelExportlogEntity.setServiceType(SERVICE_TYPE);
        String title;
        String content;
        try {
            QueryWrapper<TXfInvoiceExchangeEntity> wrapper = new QueryWrapper<>();
            wrapper.in(TXfInvoiceExchangeEntity.INVOICE_ID,invoiceId);
            List<TXfInvoiceExchangeEntity> tXfInvoiceExchangeEntities = tXfInvoiceExchangeDao.selectList(wrapper);
            if(CollectionUtils.isEmpty(tXfInvoiceExchangeEntities)){
                return R.fail("根据id没有找到换票");
            }
            String path = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String ftpPath = ftpUtilService.pathprefix + path;
            final File tempDirectory = FileUtils.getTempDirectory();
            File file = FileUtils.getFile(tempDirectory, path);
            file.mkdir();
            String downLoadFileName = path+ ".zip";
            for (TXfInvoiceExchangeEntity exchangeEntity : tXfInvoiceExchangeEntities) {
                TDxRecordInvoiceEntity entity = tDxRecordInvoiceDao.selectById(exchangeEntity.getNewInvoiceId());
                if(entity != null){
                    List<Integer> types = new ArrayList<>();
                    types.add(InvoiceFileEntity.TYPE_OF_OFD);
                    types.add(InvoiceFileEntity.TYPE_OF_PDF);
                    List<TXfInvoiceFileEntity> invoiceAndTypes = invoiceFileDao.getByInvoiceAndTypes(entity.getInvoiceNo(), entity.getInvoiceCode(), types);
                    for (int i = 0; i < invoiceAndTypes.size(); i++) {
                        TXfInvoiceFileEntity tXfInvoiceFileEntity= invoiceAndTypes.get(i);
                        byte[] bytes = fileService.downLoadFile4ByteArray(tXfInvoiceFileEntity.getPath());
                        if(bytes == null){
                            continue;
                        }
                        String suffix;
                        if (tXfInvoiceFileEntity.getType().equals(InvoiceFileEntity.TYPE_OF_OFD)) {
                            suffix = InvoiceFileEntity.SUFFIX_OF_OFD;
                        } else {
                            suffix = InvoiceFileEntity.SUFFIX_OF_PDF;
                        }
                        String name = tXfInvoiceFileEntity.getInvoiceNo() + "-" + Objects.toString(tXfInvoiceFileEntity.getInvoiceCode(), "");
                        FileUtils.writeByteArrayToFile(FileUtils.getFile(file, name +"-"+(i+1)+suffix), bytes);
                    }
                }
            }
            if(file.listFiles() == null){
                throw new EnhanceRuntimeException("下载的发票版式文件不存在");
            }
            ZipUtil.zip(file.getPath() + ".zip", file);
            exportCommonService.putFile(ftpPath, tempDirectory.getPath() + "/" + downLoadFileName, downLoadFileName);
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setFilepath(ftpPath + "/" + downLoadFileName);
            title = "电票源文件下载成功";
            content = exportCommonService.getSuccContent();
        } catch (Exception e) {
            log.info(e.getMessage());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
            excelExportlogEntity.setErrmsg(e.getMessage());
            excelExportLogService.save(excelExportlogEntity);
            return R.fail("下载异常");
        }
        excelExportLogService.save(excelExportlogEntity);
        exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), title,content);
        return R.ok(null,"请求成功，请往消息中心查看下载结果");
    }


    private QueryWrapper<TDxRecordInvoiceEntity> getQueryWrapper(QueryInvoiceExchangeRequest request){
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc(TXfInvoiceExchangeEntity.ID);
        if(StringUtils.isNotEmpty(request.getJvcode())){
            wrapper.eq(TDxRecordInvoiceEntity.JVCODE,request.getJvcode());
        }
        if(StringUtils.isNotEmpty(request.getFlowType())){
            wrapper.eq(TDxRecordInvoiceEntity.FLOW_TYPE,request.getFlowType());
        }
        if(StringUtils.isNotEmpty(request.getInvoiceType())){
            wrapper.eq(TDxRecordInvoiceEntity.INVOICE_TYPE,request.getInvoiceType());
        }
        if(StringUtils.isNotEmpty(request.getPaperDrewStartDate())){
            wrapper.ge(TDxRecordInvoiceEntity.INVOICE_DATE,request.getPaperDrewStartDate());
        }
        if(StringUtils.isNotEmpty(request.getPaperDrewEndDate())){
            wrapper.le(TDxRecordInvoiceEntity.INVOICE_DATE,request.getPaperDrewEndDate());
        }
        if(StringUtils.isNotEmpty(request.getVenderid())){
            wrapper.eq(TDxRecordInvoiceEntity.VENDERID,request.getVenderid());
        }
        if(request.getExchangeStatus() != null){
            if(request.getExchangeStatus() == -1){
                wrapper.in(TDxRecordInvoiceEntity.EXCHANGE_STATUS, Lists.newArrayList(1,2,3));
            }else{
                wrapper.eq(TDxRecordInvoiceEntity.EXCHANGE_STATUS, request.getExchangeStatus());
            }
        }
        if(request.getIsExchange() != null){
            if(request.getIsExchange() == 0){
                wrapper.in(TDxRecordInvoiceEntity.EXCHANGE_STATUS, Lists.newArrayList(1,2));
            }else{
                wrapper.eq(TDxRecordInvoiceEntity.EXCHANGE_STATUS, 3);
            }
        }
        if(StringUtils.isNotEmpty(request.getInvoiceCode())){
            wrapper.eq(TDxRecordInvoiceEntity.INVOICE_CODE, request.getInvoiceCode());
        }
        if(StringUtils.isNotEmpty(request.getInvoiceNo())){
            wrapper.eq(TDxRecordInvoiceEntity.INVOICE_NO, request.getInvoiceNo());
        }
        if(request.getTaxRate() != null){
            wrapper.eq(TDxRecordInvoiceEntity.TAX_RATE, request.getTaxRate());
        }
        return wrapper;

    }


    public R export(List<InvoiceExchangeResponse> resultList, ExchangeExportRequest request) {

        final String excelFileName = ExcelExportUtil.getExcelFileName(UserUtil.getUserId(), "供应商换票导出");
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        ExcelWriter excelWriter;
        FileInputStream inputStream = null;
        try {
            //创建一个sheet
            File file = FileUtils.getFile(tmp + ftpPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File excl = FileUtils.getFile(file, excelFileName);
            EasyExcel.write(tmp + ftpPath + "/" + excelFileName, ExSupplierchangeExportDto.class).sheet("sheet1").doWrite(exchangeExportConverter.exportMap(resultList));
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
            excelExportlogEntity.setConditions(JSON.toJSONString(request));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + excelFileName);
            this.excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "供应商换票导出导出成功", exportCommonService.getSuccContent());
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




}
