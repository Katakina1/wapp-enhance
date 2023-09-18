package com.xforceplus.wapp.modules.exceptionreport.service.impl;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportStatusEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.client.CacheClient;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportCodeEnum;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.export.ExportHandlerEnum;
import com.xforceplus.wapp.export.IExportHandler;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.deduct.service.BillExceptionReportService;
import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportDto;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exceptionreport.dto.ReMatchRequest;
import com.xforceplus.wapp.modules.exceptionreport.listener.ExceptionReportImportListener;
import com.xforceplus.wapp.modules.exceptionreport.mapstruct.ExceptionReportMapper;
import com.xforceplus.wapp.modules.exceptionreport.model.excel.ExceptionReportImport;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import com.xforceplus.wapp.repository.dao.TXfExceptionReportDao;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.CommonMessageService;

import cn.hutool.poi.excel.BigExcelWriter;
import io.vavr.Tuple3;
import lombok.extern.slf4j.Slf4j;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 20:42
 **/
@Service
@Slf4j
public class ExceptionReportServiceImpl extends ServiceImpl<TXfExceptionReportDao, TXfExceptionReportEntity> implements ExceptionReportService {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    private final String downLoadurl = "api/core/ftp/download";
    private static final String EXPORT_LOCK_KEY="exception-report:%s:%s";

    @Autowired
    private IDSequence idSequence;
    @Autowired
    private ExceptionReportMapper exceptionReportMapper;
    private final String filePrefix = "例外报告";
    @Autowired
    private ActiveMqProducer activeMqProducer;

    @Autowired
    private CommonMessageService commonMessageService;

    @Value("${activemq.queue-name.export-request}")
    private String exportQueue;

    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Autowired
    private FtpUtilService ftpUtilService;

    @Autowired
    private ClaimBillService claimService;
    @Autowired
    private CacheClient cacheClient;
    @Autowired
    private OperateLogService operateLogService;

    private final Map<String, String> headClaim;
    private final Map<String, String> headEPD;
    private final Map<String, String> headAgreement;
    @Autowired
    ThreadPoolExecutor threadPool;
    @Autowired
    ExportCommonService exportCommonService;


    @Autowired
    private BillExceptionReportService billExceptionReportService;

    public ExceptionReportServiceImpl() {
        headClaim = excelHeadClaim();
        headEPD = excelHeadEPD();
        headAgreement = excelHeadAgreement();
    }

    @Override
    public void add4Claim(TXfExceptionReportEntity entity) {
    	log.info("add4Claim:{}", JSON.toJSONString(entity));
        entity.setType(ExceptionReportTypeEnum.CLAIM.getType());
        //2022-08-08 重复的例外报告原因只update
        final LambdaQueryWrapper<TXfExceptionReportEntity> wrapper = Wrappers.lambdaQuery(TXfExceptionReportEntity.class);
        wrapper.eq(TXfExceptionReportEntity::getType, ExceptionReportTypeEnum.CLAIM.getType());
        wrapper.eq(TXfExceptionReportEntity::getBillNo, entity.getBillNo());
        wrapper.eq(TXfExceptionReportEntity::getSellerNo, entity.getSellerNo());
        wrapper.eq(TXfExceptionReportEntity::getBatchNo, entity.getBatchNo());
        wrapper.eq(TXfExceptionReportEntity::getCode, entity.getCode());
        List<TXfExceptionReportEntity> list = this.list(wrapper);
		if (list != null && list.size() > 0) {
			entity.setId(list.get(0).getId());
			entity.setCreateTime(list.get(0).getCreateTime());
			entity.setUpdateTime(new Date());
			entity.setStatus(1);
			this.updateById(entity);

            // ***** 同步列外报表信息到 业务单 *****
            billExceptionReportService.syncExceptionReport(entity);
		} else {
			saveExceptionReport(entity);
		}
        // 添加日志履历
        ExceptionReportCodeEnum exceptionReportCodeEnum = ExceptionReportCodeEnum.fromCode(entity.getCode());
        Optional.ofNullable(exceptionReportCodeEnum)
                .filter(codeEnum -> StringUtils.isNotBlank(codeEnum.getOperateKind()))
                .ifPresent(codeEnum -> {
                    OperateLogEnum operateLogEnum = OperateLogEnum.fromCode(codeEnum.getOperateKind());
                    Optional.ofNullable(operateLogEnum)
                            .ifPresent(logEnum -> operateLogService.addDeductLog(entity.getBillId(), TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM, null, logEnum, codeEnum.getDescription(), 0L, "系统"));
                });

    }

    @Override
    public void add4Agreement(TXfExceptionReportEntity entity) {
        entity.setType(ExceptionReportTypeEnum.AGREEMENT.getType());
        saveExceptionReport(entity);

        // 添加日志履历
        ExceptionReportCodeEnum exceptionReportCodeEnum = ExceptionReportCodeEnum.fromCode(entity.getCode());
        Optional.ofNullable(exceptionReportCodeEnum)
                .filter(codeEnum -> StringUtils.isNotBlank(codeEnum.getOperateKind()))
                .ifPresent(codeEnum -> {
                    OperateLogEnum operateLogEnum = OperateLogEnum.fromCode(codeEnum.getOperateKind());
                    Optional.ofNullable(operateLogEnum)
                            .ifPresent(logEnum -> operateLogService.addDeductLog(entity.getBillId(), TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue(), TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, null, logEnum, codeEnum.getDescription(), 0L, "系统"));
                });
    }

    @Override
    public void add4EPD(TXfExceptionReportEntity entity) {
        entity.setType(ExceptionReportTypeEnum.EPD.getType());
        saveExceptionReport(entity);
    }

    private void saveExceptionReport(TXfExceptionReportEntity entity) {

        entity.setId(idSequence.nextId());
        entity.setStatus(1);
        entity.setCreateTime(new Date());
        this.save(entity);

        // ***** 同步列外报表信息到 业务单 *****
        billExceptionReportService.syncExceptionReport(entity);

    }

    @Override
    public Page<TXfExceptionReportEntity> getPage(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum) {
        Page<TXfExceptionReportEntity> page = new Page<>();
        page.setSize(request.getSize());
        page.setCurrent(request.getPage());
        page.addOrder(OrderItem.desc("id"));
//        if (StringUtils.isBlank(request.getBillNo())){
//            request.setBillNo(null);
//        }
//        if(StringUtils.isBlank(request.getSellerNo())){
//            request.setSellerNo(null);
//        }
//
//        if (StringUtils.isBlank(request.getPurchaserNo())){
//            request.setPurchaserNo(null);
//        }

        TXfExceptionReportEntity entity = exceptionReportMapper.toEntity(request);
        entity.setType(typeEnum.getType());
        final LambdaQueryWrapper<TXfExceptionReportEntity> wrapper = filterQuery(request, entity).lambda();

        if (StringUtils.isNotBlank(request.getStartDeductDate())) {
            try {
            final String format = DateUtils.addDayToYYYYMMDD(request.getStartDeductDate(), -1);
            wrapper.gt(TXfExceptionReportEntity::getDeductDate, format);
            } catch (Exception e) {
                log.error("时间转换失败" + e.getMessage(), e);
            }
        }

        if (StringUtils.isNotBlank(request.getEndDeductDate())) {

            try {
                final String format = DateUtils.addDayToYYYYMMDD(request.getEndDeductDate(), 1);
                wrapper.lt(TXfExceptionReportEntity::getDeductDate, format);
            } catch (Exception e) {
                log.error("时间转换失败" + e.getMessage(), e);
            }
        }

        if (StringUtils.isNotBlank(request.getStartCreateTime())) {
            wrapper.gt(TXfExceptionReportEntity::getCreateTime, request.getStartCreateTime());
        }

        if (StringUtils.isNotBlank(request.getEndCreateTime())) {

            try {
                final String format = DateUtils.addDayToYYYYMMDD(request.getEndCreateTime(), 1);
                wrapper.lt(TXfExceptionReportEntity::getCreateTime, format);
            } catch (Exception e) {
                log.error("报告时间转换失败" + e.getMessage(), e);
            }
        }

        return this.page(page, wrapper);
    }

    /**
     * 查询条件过滤
     * 需求链接： https://xforceplus.yuque.com/dew9bm/qa8c30/ryzm6z#XHUAA
     * @param request
     * @param entity
     */
    private QueryWrapper<TXfExceptionReportEntity> filterQuery(ExceptionReportRequest request, TXfExceptionReportEntity entity) {
        entity.setStatus(null);
        entity.setCode(null);
        QueryWrapper<TXfExceptionReportEntity> wrapper = Wrappers.query(entity);
        Integer status = request.getStatus();
        // 查询无需处理（S001，S005）
        if (null != status && ExceptionReportStatusEnum.IGNORE.getType().equals(status)) {
            // 无需处理 使用code 代替status
            wrapper.in(TXfExceptionReportEntity.CODE, Arrays.asList(ExceptionReportCodeEnum.WITH_DIFF_TAX.getCode(), ExceptionReportCodeEnum.CLAIM_DETAIL_ZERO_TAX_RATE.getCode()));
        } else if (null != status) {
            // 正常状态查询
            wrapper.eq(TXfExceptionReportEntity.STATUS, status);
            wrapper.notIn(TXfExceptionReportEntity.CODE, Arrays.asList(ExceptionReportCodeEnum.WITH_DIFF_TAX.getCode(), ExceptionReportCodeEnum.CLAIM_DETAIL_ZERO_TAX_RATE.getCode()));
        }
        if (StringUtils.isNotBlank(request.getCode())) {
            wrapper.eq(TXfExceptionReportEntity.CODE, request.getCode());
        }
        return wrapper;
    }

    @Override
    public void export(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum) {
        final Long userId = UserUtil.getUserId();
        String key= String.format(EXPORT_LOCK_KEY, userId,typeEnum.name());

        boolean lock = Objects.nonNull(cacheClient.get(key));

        if (lock){
            throw new EnhanceRuntimeException("文件正在导出中，请稍后在通知中心查看导出结果。若长时间没结果请在1分钟后重试");
        }

        ExceptionReportExportDto dto = new ExceptionReportExportDto();
        dto.setType(typeEnum);
        dto.setRequest(request);
        dto.setUserId(userId);
        dto.setLoginName(UserUtil.getLoginName());

        TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
        excelExportlogEntity.setCreateDate(new Date());
        //这里的userAccount是userid
        excelExportlogEntity.setUserAccount(dto.getUserId().toString());
        excelExportlogEntity.setUserName(dto.getLoginName());
        excelExportlogEntity.setConditions(JSON.toJSONString(request));
        excelExportlogEntity.setStartDate(new Date());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
        excelExportlogEntity.setServiceType(SERVICE_TYPE);

        this.excelExportLogService.save(excelExportlogEntity);
        dto.setLogId(excelExportlogEntity.getId());

        activeMqProducer.send(exportQueue, JSON.toJSONString(dto),
                Collections.singletonMap(IExportHandler.KEY_OF_HANDLER_NAME, ExportHandlerEnum.EXCEPTION_REPORT.name())
        );

        cacheClient.set(key,Boolean.TRUE,60);
    }


    public void doExport(ExceptionReportExportDto exportDto) {
        ExceptionReportRequest request = exportDto.getRequest();
        ExceptionReportTypeEnum typeEnum = exportDto.getType();
        //这里的userAccount是userid
        final TDxExcelExportlogEntity excelExportlogEntity = excelExportLogService.getById(exportDto.getLogId());
        excelExportlogEntity.setEndDate(new Date());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);

        TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
        //这里的userAccount是userName
        messagecontrolEntity.setUserAccount(exportDto.getLoginName());
        messagecontrolEntity.setContent(getSuccContent());
        String filePrefix=this.filePrefix;

        String key= String.format(EXPORT_LOCK_KEY, exportDto.getUserId(),typeEnum.name());
        switch (typeEnum){
            case CLAIM:
                filePrefix="索赔单"+filePrefix;
                break;
            case AGREEMENT:
                filePrefix="协议单"+filePrefix;
                break;
            case EPD:
                filePrefix="EPD"+filePrefix;
                break;
            default:
        }

        List<TXfExceptionReportEntity> list = getExportData(request, typeEnum, 0);
        try {
            Map<String, String> head = null;
            Function<List<TXfExceptionReportEntity>,List> toExport ;
            switch (typeEnum){
                case EPD:
                    head=headEPD;
                    toExport=this.exceptionReportMapper::toExport;
                    break;
                case AGREEMENT:
                    head=headAgreement;
                    toExport=this.exceptionReportMapper::toAgreementExport;
                    break;
                case CLAIM:
                    head=headClaim;
                    toExport=this.exceptionReportMapper::toClaimExport;
                    break;
                default:
                    throw new EnhanceRuntimeException("不支持的例外报告类型:"+typeEnum);
            }



            final File file = File.createTempFile("exception-report-export", "xls");
            try (BigExcelWriter bigExcelWriter = new BigExcelWriter();
                    FileOutputStream fileOutputStream=new FileOutputStream(file);
                 FileInputStream fileInputStream=new FileInputStream(file);
            ){
                bigExcelWriter.setHeaderAlias(head);
                bigExcelWriter.autoSizeColumnAll();
                while (CollectionUtils.isNotEmpty(list)) {
                    final List reportExportDtos = toExport.apply(list);
                    bigExcelWriter.write(reportExportDtos);
                    long lastId = list.get(list.size() - 1).getId();
                    list = getExportData(request, typeEnum, lastId);
                }
                bigExcelWriter.flush(fileOutputStream);

                //推送sftp
                String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                final String excelFileName = ExcelExportUtil.getExcelFileName(exportDto.getUserId(), filePrefix);
                String ftpFilePath = ftpPath + "/" + excelFileName;
                ftpUtilService.uploadFile(ftpPath, excelFileName, fileInputStream);
                messagecontrolEntity.setUrl(getUrl(excelExportlogEntity.getId()));
                excelExportlogEntity.setFilepath(ftpFilePath);
                messagecontrolEntity.setTitle(filePrefix + "导出成功");

            }  finally {
                file.deleteOnExit();
            }

        } catch (Exception e) {
            log.error("例外报告导出失败:" + e.getMessage(), e);
            excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
            excelExportlogEntity.setErrmsg(e.getMessage());
            messagecontrolEntity.setTitle(filePrefix + "导出失败");
            messagecontrolEntity.setContent(getFailContent(e.getMessage()));
        } finally {
            excelExportLogService.updateById(excelExportlogEntity);
            commonMessageService.sendMessage(messagecontrolEntity);
            cacheClient.clean(key);
        }
    }

    private List<TXfExceptionReportEntity> getExportData(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum, long lastId) {
        final String startDeductDate = request.getStartDeductDate();
        TXfExceptionReportEntity entity = exceptionReportMapper.toEntity(request);
        entity.setType(typeEnum.getType());
        final QueryWrapper<TXfExceptionReportEntity> wrapper = filterQuery(request, entity);

        if (lastId > 0) {
            wrapper.lt(TXfExceptionReportEntity.ID, lastId);
        }

        if (StringUtils.isNotBlank(startDeductDate)) {
            wrapper.gt(TXfExceptionReportEntity.DEDUCT_DATE, startDeductDate);
        }

        if (StringUtils.isNotBlank(request.getEndDeductDate())) {

            try {
                final String format = DateUtils.addDayToYYYYMMDD(request.getEndDeductDate(), 1);
                wrapper.lt(TXfExceptionReportEntity.DEDUCT_DATE, format);
            } catch (Exception e) {
                log.error("时间转换失败" + e.getMessage(), e);
            }
        }

        if (StringUtils.isNotBlank(request.getStartCreateTime())) {
            wrapper.gt(TXfExceptionReportEntity.CREATE_TIME, request.getStartCreateTime());
        }

        if (StringUtils.isNotBlank(request.getEndCreateTime())) {

            try {
                final String format = DateUtils.addDayToYYYYMMDD(request.getEndCreateTime(), 1);
                wrapper.lt(TXfExceptionReportEntity.CREATE_TIME, format);
            } catch (Exception e) {
                log.error("报告时间转换失败" + e.getMessage(), e);
            }
        }

        wrapper.select("top 100 1,*");
        wrapper.orderByDesc(TXfExceptionReportEntity.ID);

        return this.list(wrapper);

    }


    /**
     * 获取导出成功内容
     *
     * @return
     * @since 1.0
     */
    public String getSuccContent() {
        String createDate = DateUtils.format(new Date());
        String content = "申请时间：" + createDate + "。申请导出成功，可以下载！";
        return content;
    }


    /**
     * 获取导出失败的内容
     *
     * @param errmsg 错误信息
     * @return
     * @since 1.0
     */
    public String getFailContent(String errmsg) {
        StringBuilder content = new StringBuilder();
        content.append("申请时间：");
        String createDate = DateUtils.format(new Date());
        content.append(createDate);
        content.append("。申请导出失败，请重新申请！");
        return content.toString();
    }


    /**
     * 获取excel下载连接
     *
     * @param id
     * @return
     * @since 1.0
     */
    public String getUrl(long id) {
        String url = downLoadurl + "?serviceType=2&downloadId=" + id;
        return url;
    }


    private Map<String, String> excelHeadEPD() {
        return excelHead();
    }

    private Map<String, String> excelHeadAgreement() {
        Map<String, String> head = excelHead();
        head.put("agreementTypeCode", "协议类型编码");
        return head;
    }
    private Map<String, String> excelHead() {
        Map<String, String> head = new LinkedHashMap<>();
        head.put("code", "例外CODE");
        head.put("description", "例外说明");
        head.put("sellerNo", "供应商编号");
        head.put("sellerName", "供应商名称");
        head.put("purchaserName", "扣款公司");
        head.put("purchaserNo", "扣款公司编号");
        head.put("amountWithTax", "含税金额");
        head.put("agreementTypeCode", "协议类型编码");
        head.put("billNo", "协议号");
        head.put("taxCode", "税码");
        head.put("deductDate", "扣款日期");
        head.put("taxRate", "税率");
        return head;
    }



    private Map<String, String> excelHeadClaim() {
        Map<String, String> head = new LinkedHashMap<>();
        head.put("id", "流水号");
        head.put("code", "例外CODE");
        head.put("description", "例外说明");
        head.put("createTime", "报告时间");
        head.put("deductDate", "扣款日期");
        head.put("purchaserName", "扣款公司");
        head.put("sellerNo", "供应商编号");
        head.put("sellerName", "供应商名称");
        head.put("billNo", "索赔号/换货号");
        head.put("verdictDate", "定案日期");
        head.put("amountWithoutTax", "成本金额(不含税)");
        head.put("taxBalance", "税差");
        head.put("status", "处理状态");
        head.put("remark", "备注");
        return head;
    }


    @Transactional
    @Override
    public void reMatchTaxCode(ReMatchRequest request){
        if(CollectionUtils.isEmpty(request.getIds())){
            throw new EnhanceRuntimeException("请选择一项需要重新匹配的单据");
        }
        final List<TXfExceptionReportEntity> list = this.list(Wrappers.lambdaQuery(TXfExceptionReportEntity.class).in(TXfExceptionReportEntity::getId, request.getIds()));

        list.forEach(x->{
            claimService.reMatchClaimTaxCode(x.getBillId(), x.getBillNo());
        });

//        final LambdaUpdateWrapper<TXfExceptionReportEntity> updateWrapper = Wrappers.lambdaUpdate(TXfExceptionReportEntity.class).set(TXfExceptionReportEntity::getStatus, 2).in(TXfExceptionReportEntity::getId, request.getIds());
//        this.update(updateWrapper);
    }

    @Override
    public boolean updateStatus(String billNo, String code, int type, Long billId) {

        final List<TXfExceptionReportEntity> reports = this.list(Wrappers.lambdaQuery(TXfExceptionReportEntity.class).select(TXfExceptionReportEntity::getId)
                .eq(TXfExceptionReportEntity::getBillNo, billNo)
                .eq(TXfExceptionReportEntity::getBillId,billId)
                .eq(TXfExceptionReportEntity::getCode, code)
                .eq(TXfExceptionReportEntity::getType, type)
                .eq(TXfExceptionReportEntity::getStatus, 1));
        log.info("billNo:{},code:{},type:{},billId:{} need process reports:{}", billNo, code, type, billId, JSON.toJSONString(reports));
        if (CollectionUtils.isNotEmpty(reports)) { //当找到原来的例外报告，把例外报告修改为已处理
            final List<Long> reportIds = reports.stream().map(TXfExceptionReportEntity::getId).collect(Collectors.toList());
            final LambdaUpdateWrapper<TXfExceptionReportEntity> update = Wrappers.lambdaUpdate(TXfExceptionReportEntity.class)
                    .set(TXfExceptionReportEntity::getStatus, 2)
                    .set(TXfExceptionReportEntity::getUpdateTime,new Date())
                        .in(TXfExceptionReportEntity::getId,reportIds)
                        .eq(TXfExceptionReportEntity::getBillNo, billNo)
                        .eq(TXfExceptionReportEntity::getCode, code)
                        .eq(TXfExceptionReportEntity::getType, type)
                        .eq(TXfExceptionReportEntity::getStatus, 1);

            // ***** 同步列外报表处理状态 到 业务单 *****
            billExceptionReportService.syncExceptionStatus(billId, 2);
            return this.update(update);
        }
        return false;
    }

	@Override
	public R exceptionReportImport(MultipartFile file) {
        Tuple3<Long, Long, String> longLongStringTuple3 = exportCommonService.insertRequest(file.getOriginalFilename());
        threadPool.execute(
                () -> {
                    InputStream inputStream = null;
                    try {
                        inputStream = new BufferedInputStream(file.getInputStream());
                    } catch (IOException e) {
                        log.error("获取导入文件失败", e);
                    }
                    //实例化实现了AnalysisEventListener接口的类
                    ExceptionReportImportListener excelListener = new ExceptionReportImportListener(this,ftpUtilService,exportCommonService , longLongStringTuple3);
                    ExcelReader reader = new ExcelReader(inputStream, null, excelListener);
                    //读取信息
                    reader.read(new Sheet(1, 1, ExceptionReportImport.class));
                }
        );

        return R.ok("处理成功，请到右上角小铃铛查看导入结果");
	}

	@Override
	public R update(ExceptionReportDto exceptionReportDto) {
		TXfExceptionReportEntity entity = this.exceptionReportMapper.toEntity(exceptionReportDto);
		boolean bool = this.updateById(entity);
		return bool?R.ok():R.fail("修改失败");
	}

}
