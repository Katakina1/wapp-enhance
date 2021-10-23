package com.xforceplus.wapp.modules.exceptionreport.service.impl;

import cn.hutool.poi.excel.BigExcelWriter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.exceptionreport.ExceptionReportTypeEnum;
import com.xforceplus.wapp.export.ExportHandlerEnum;
import com.xforceplus.wapp.export.IExportHandler;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.backFill.service.FileService;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import com.xforceplus.wapp.modules.deduct.service.ClaimBillService;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exceptionreport.dto.ReMatchRequest;
import com.xforceplus.wapp.modules.exceptionreport.mapstruct.ExceptionReportMapper;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import com.xforceplus.wapp.repository.dao.TXfExceptionReportDao;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.CommonMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

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

    private static final SimpleDateFormat SDF = new SimpleDateFormat(YYYY_MM_DD);

    private final String downLoadurl = "api/core/ftp/download";

    @Autowired
    private IDSequence idSequence;

    @Autowired
    private ExceptionReportMapper exceptionReportMapper;

    @Autowired
    private FileService fileService;

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

    private final Map<String, String> headClaim;
    private final Map<String, String> headEPD;

    public ExceptionReportServiceImpl() {
        headClaim = excelHeadClaim();
        headEPD = excelHeadEPD();
    }

    @Override
    public void add4Claim(TXfExceptionReportEntity entity) {
        entity.setType(ExceptionReportTypeEnum.CLAIM.getType());
        saveExceptionReport(entity);
    }

    @Override
    public void add4Agreement(TXfExceptionReportEntity entity) {
        entity.setType(ExceptionReportTypeEnum.AGREEMENT.getType());
        saveExceptionReport(entity);
    }

    @Override
    public void add4EPD(TXfExceptionReportEntity entity) {
        entity.setType(ExceptionReportTypeEnum.EPD.getType());
        saveExceptionReport(entity);
    }

    private void saveExceptionReport(TXfExceptionReportEntity entity) {

        entity.setId(idSequence.nextId());
        entity.setStatus(1);
        this.save(entity);

    }

    @Override
    public Page<TXfExceptionReportEntity> getPage(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum) {
        Page<TXfExceptionReportEntity> page = new Page<>();
        page.setSize(request.getSize());
        page.setCurrent(request.getPage());
        TXfExceptionReportEntity entity = exceptionReportMapper.toEntity(request);
        entity.setType(typeEnum.getType());
        final String startDeductDate = request.getStartDeductDate();
        final LambdaQueryWrapper<TXfExceptionReportEntity> wrapper = Wrappers.lambdaQuery(entity);

        if (StringUtils.isNotBlank(startDeductDate)) {
            wrapper.gt(TXfExceptionReportEntity::getCreateTime, startDeductDate);
        }

        if (StringUtils.isNotBlank(request.getEndDeductDate())) {

            try {
                final String format = DateUtils.addDayToYYYYMMDD(request.getEndDeductDate(), 1);
                wrapper.lt(TXfExceptionReportEntity::getCreateTime, format);
            } catch (Exception e) {
                log.error("时间转换失败" + e.getMessage(), e);
            }
        }

        return this.page(page, wrapper);
    }

    @Override
    public void export(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum) {
        final Long userId = UserUtil.getUserId();
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

        List<TXfExceptionReportEntity> list = getExportData(request, typeEnum, 0);
        try (BigExcelWriter bigExcelWriter = new BigExcelWriter()) {
            Map<String, String> head = typeEnum == ExceptionReportTypeEnum.CLAIM ? headClaim : headEPD;
            bigExcelWriter.setHeaderAlias(head);
            while (CollectionUtils.isNotEmpty(list)) {
                bigExcelWriter.write(list);
                long lastId = list.get(list.size() - 1).getId();
                list = getExportData(request, typeEnum, lastId);
            }
            // TODO

            try {
                final String excelFileName = ExcelExportUtil.getExcelFileName(exportDto.getUserId(), filePrefix);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bigExcelWriter.flush(out);


                ByteArrayInputStream is = new ByteArrayInputStream(out.toByteArray());

                //推送sftp
                String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                String ftpFilePath = ftpPath + "/" + excelFileName;
                ftpUtilService.uploadFile(ftpPath, excelFileName, is);
                messagecontrolEntity.setUrl(getUrl(excelExportlogEntity.getId()));
                excelExportlogEntity.setFilepath(ftpFilePath);
                messagecontrolEntity.setTitle(this.filePrefix + "导出成功");

            } catch (Exception e) {
                e.printStackTrace();
                log.error("例外报告导出失败:" + e.getMessage(), e);
                excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
                excelExportlogEntity.setErrmsg(e.getMessage());
                messagecontrolEntity.setTitle(this.filePrefix + "导出失败");
                messagecontrolEntity.setContent(getFailContent(e.getMessage()));

            } finally {
                excelExportLogService.updateById(excelExportlogEntity);
                commonMessageService.sendMessage(messagecontrolEntity);
            }
        }
    }

    private List<TXfExceptionReportEntity> getExportData(ExceptionReportRequest request, ExceptionReportTypeEnum typeEnum, long lastId) {
        TXfExceptionReportEntity entity = exceptionReportMapper.toEntity(request);
        entity.setType(typeEnum.getType());
        final String startDeductDate = request.getStartDeductDate();
        final LambdaQueryWrapper<TXfExceptionReportEntity> wrapper = Wrappers.lambdaQuery(entity);

        wrapper.gt(TXfExceptionReportEntity::getId, lastId);
        if (StringUtils.isNotBlank(startDeductDate)) {
            wrapper.gt(TXfExceptionReportEntity::getCreateTime, startDeductDate);
        }

        if (StringUtils.isNotBlank(request.getEndDeductDate())) {

            try {
                final String format = DateUtils.addDayToYYYYMMDD(request.getEndDeductDate(), 1);
                wrapper.lt(TXfExceptionReportEntity::getCreateTime, format);
            } catch (Exception e) {
                log.error("时间转换失败" + e.getMessage(), e);
            }
        }
        wrapper.orderByAsc(TXfExceptionReportEntity::getId);

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
        Map<String, String> head = new HashMap<>();
        head.put("code", "例外CODE");
        head.put("description", "例外说明");
        head.put("sellerNo", "供应商编号");
        head.put("sellerName", "供应商名称");
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
        Map<String, String> head = new HashMap<>();
        head.put("code", "例外CODE");
        head.put("description", "例外说明");
        head.put("sellerNo", "供应商编号");
        head.put("sellerName", "供应商名称");
        head.put("purchaserNo", "扣款公司编号");
        head.put("amountWithoutTax", "成本金额(不含税)");
        head.put("agreementTypeCode", "协议类型编码");
        head.put("billNo", "索赔号/换货号");
        head.put("taxCode", "税码");
        head.put("verdictDate", "定案日期");
        head.put("taxRate", "税率");
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
            claimService.reMatchClaimTaxCode(x.getBillId());
        });

        final LambdaUpdateWrapper<TXfExceptionReportEntity> updateWrapper = Wrappers.lambdaUpdate(TXfExceptionReportEntity.class).set(TXfExceptionReportEntity::getStatus, 2).in(TXfExceptionReportEntity::getId, request.getIds());
        this.update(updateWrapper);
    }
}
